package scs.core.builder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.Servant;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import scs.core.ComponentContext;
import scs.core.ComponentId;
import scs.core.builder.exception.InvalidSchemaException;
import scs.core.builder.exception.InvalidXMLException;
import scs.core.builder.exception.SchemaNotFoundException;
import scs.core.exception.InvalidComponentContextException;
import scs.core.exception.InvalidServantException;
import scs.core.exception.ReceptacleAlreadyExistsException;
import scs.core.exception.SCSException;

/**
 * The purpose of this class is to build components based entirely on an XML
 * description. The XML file must comply to the official XSD.
 * 
 */
public final class XMLComponentBuilder {
  private static final String COMPONENT_ID_ELEMENT = "id";
  private static final String COMPONENT_ID_NAME = "name";
  private static final String COMPONENT_ID_VERSION = "version";
  private static final String COMPONENT_ID_PLATFORM_SPEC = "platformSpec";

  private static final String COMPONENT_CONTEXT_ELEMENT = "context";
  private static final String COMPONENT_CONTEXT_TYPE = "type";

  private static final String FACETS_ELEMENT = "facets";
  private static final String FACET_ELEMENT = "facet";
  private static final String FACET_NAME = "name";
  private static final String FACET_INTERFACE_NAME = "interfaceName";
  private static final String FACET_IMPL = "facetImpl";

  private static final String RECEPTACLES_ELEMENT = "receptacles";
  private static final String RECEPTACLE_ELEMENT = "receptacle";
  private static final String RECEPTACLE_NAME = "name";
  private static final String RECEPTACLE_INTERFACE_NAME = "interfaceName";
  private static final String RECEPTACLE_MULTIPLEX = "isMultiplex";

  private static final String VERSION_DELIMITER = "\\.";

  private ORB orb;
  private POA poa;
  private DocumentBuilder documentBuilder;
  private Validator schemaValidator;

  /**
   * Constructs a builder that uses XML as a description format to assembly a
   * component.
   * 
   * @param orb The orb that shall be associated to this component and its CORBA
   *        objects.
   * @param poa The poa that shall be used to activate and deactivate the
   *        servants.
   * 
   * @throws SchemaNotFoundException
   * @throws InvalidSchemaException
   * @throws ParserConfigurationException
   */
  public XMLComponentBuilder(ORB orb, POA poa) throws SchemaNotFoundException,
    InvalidSchemaException, ParserConfigurationException {
    this.orb = orb;
    this.poa = poa;

    DocumentBuilderFactory documentBuilderFactory =
      DocumentBuilderFactory.newInstance();
    documentBuilderFactory.setNamespaceAware(true);
    this.documentBuilder = documentBuilderFactory.newDocumentBuilder();

    SchemaFactory factory =
      SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    String schemaResourceName = "/ComponentDescription.xsd";
    URL schemaURL = getClass().getResource(schemaResourceName);
    if (schemaURL == null) {
      throw new SchemaNotFoundException(String.format(
        "O schema %s não foi encontrado", schemaResourceName));
    }
    Schema schema;
    try {
      schema = factory.newSchema(schemaURL);
    }
    catch (SAXException e) {
      throw new InvalidSchemaException(e);
    }
    this.schemaValidator = schema.newValidator();
  }

  /**
   * Builds a component, based on an XML file. The component will be composed of
   * the basic facets, plus all facets and receptacles present on the XML file.
   * 
   * @param input The XML file.
   * 
   * @return A fully assembled component, with working facets, as described by
   *         the XML file.
   * @throws SCSException If any error occurs. The exception will contain the
   *         more specific exception.
   */
  public ComponentContext build(File file) throws IOException, SCSException {
    Document doc;
    try {
      doc = this.documentBuilder.parse(file);
    }
    catch (SAXException e) {
      throw new InvalidXMLException(e);
    }
    return this.build(doc);
  }

  /**
   * Builds a component, based on an XML file. The component will be composed of
   * the basic facets, plus all facets and receptacles present on the XML file.
   * 
   * @param inputStream .
   * 
   * @return A fully assembled component, with working facets, as described by
   *         the XML file.
   * 
   * @throws IOException
   * @throws SCSException If any error occurs. The exception will contain the
   *         more specific exception.
   */
  public ComponentContext build(InputStream input) throws IOException,
    SCSException {
    Document doc;
    try {
      doc = this.documentBuilder.parse(input);
    }
    catch (SAXException e) {
      throw new InvalidXMLException(e);
    }
    return this.build(doc);
  }

  private ComponentContext build(Document doc) throws IOException, SCSException {
    try {
      this.schemaValidator.validate(new DOMSource(doc));
    }
    catch (SAXException e) {
      throw new InvalidXMLException(e);
    }
    doc.getDocumentElement().normalize();

    Element componentElement = (Element) doc.getChildNodes().item(0);
    ComponentId id = getComponentId(componentElement);
    ComponentContext context = getComponentContext(componentElement, id);
    readAndPutFacets(componentElement, context);
    readAndPutReceptacles(componentElement, context);

    return context;
  }

  private static Element getChildByName(Element parent, String name) {
    NodeList nodes = parent.getElementsByTagName(name);
    if (nodes.getLength() == 0) {
      return null;
    }
    return (Element) nodes.item(0);
  }

  private static Element[] getChildrenByName(Element parent, String name) {
    NodeList nodes = parent.getElementsByTagName(name);
    Element[] children = new Element[nodes.getLength()];
    for (int i = 0; i < children.length; i++) {
      children[i] = (Element) nodes.item(i);
    }
    return children;
  }

  private static String getChildValueByName(Element parent, String name) {
    NodeList nodes = parent.getElementsByTagName(name);
    if (nodes.getLength() == 0) {
      return null;
    }
    Node node = nodes.item(0);
    return node.getFirstChild().getNodeValue();
  }

  /**
   * Obtains the componentId from the document.
   */
  private static ComponentId getComponentId(Element componentElement) {
    Element componentIdElement =
      getChildByName(componentElement, COMPONENT_ID_ELEMENT);
    String name = getChildValueByName(componentIdElement, COMPONENT_ID_NAME);
    String version =
      getChildValueByName(componentIdElement, COMPONENT_ID_VERSION);
    String[] versions = version.split(VERSION_DELIMITER);
    byte majorVersion = Byte.parseByte(versions[0]);
    byte minorVersion = Byte.parseByte(versions[1]);
    byte patchVersion = Byte.parseByte(versions[2]);
    String platformSpec =
      getChildValueByName(componentIdElement, COMPONENT_ID_PLATFORM_SPEC);
    return new ComponentId(name, majorVersion, minorVersion, patchVersion,
      platformSpec);
  }

  private ComponentContext getComponentContext(Element componentElement,
    ComponentId id) throws SCSException {
    Element componentContextElement =
      getChildByName(componentElement, COMPONENT_CONTEXT_ELEMENT);
    if (componentContextElement == null) {
      return new ComponentContext(this.orb, this.poa, id);
    }

    String type =
      getChildValueByName(componentContextElement, COMPONENT_CONTEXT_TYPE);
    Class<ComponentContext> componentContextClass;
    try {
      componentContextClass = (Class<ComponentContext>) Class.forName(type);
    }
    catch (ClassNotFoundException e) {
      throw new InvalidComponentContextException(String.format(
        "A classe %s, do contexto do componente, não foi encontrada", type));
    }
    catch (ClassCastException e) {
      throw new InvalidComponentContextException(String.format(
        "A classe informada como contexto do componente não é do tipo %s",
        ComponentContext.class.getCanonicalName()));
    }

    Constructor<ComponentContext> componentContextConstructor;
    try {
      componentContextConstructor =
        componentContextClass.getConstructor(ORB.class, POA.class,
          ComponentId.class);
    }
    catch (NoSuchMethodException e) {
      throw new InvalidComponentContextException(
        String
          .format("A classe informada como contexto do componente não possui construtor que receba um ORB, um POA e um identificador de componente"));
    }

    try {
      return componentContextConstructor.newInstance(orb, poa, id);
    }
    catch (InstantiationException e) {
      throw new SCSException(e);
    }
    catch (IllegalAccessException e) {
      throw new SCSException(e);
    }
    catch (InvocationTargetException e) {
      throw new SCSException(e);
    }
  }

  /**
   * Reads and puts all of the document's specified facets on the component.
   * 
   * @param context The component.
   */
  private static void readAndPutFacets(Element componentElement,
    ComponentContext context) throws SCSException {
    Element facetsElement = getChildByName(componentElement, FACETS_ELEMENT);
    if (facetsElement == null) {
      return;
    }
    Element[] facetElementArray =
      getChildrenByName(facetsElement, FACET_ELEMENT);

    for (int i = 0; i < facetElementArray.length; i++) {
      String name = getChildValueByName(facetElementArray[i], FACET_NAME);
      String interfaceName =
        getChildValueByName(facetElementArray[i], FACET_INTERFACE_NAME);
      String facetImpl = getChildValueByName(facetElementArray[i], FACET_IMPL);

      Class<Servant> servantClass;
      try {
        servantClass = (Class<Servant>) Class.forName(facetImpl);
      }
      catch (ClassNotFoundException e) {
        throw new InvalidServantException(String.format(
          "A classe %s, servant da faceta %s, não foi encontrada", facetImpl,
          name));
      }
      catch (ClassCastException e) {
        throw new InvalidServantException(String.format(
          "A classe informada como servant da faceta %s não é do tipo %s",
          name, Servant.class.getCanonicalName()));
      }
      Constructor<Servant> constructor;
      try {
        constructor = servantClass.getConstructor(ComponentContext.class);
      }
      catch (NoSuchMethodException e) {
        throw new InvalidServantException(
          String
            .format(
              "A classe informada como servant da faceta %s não possui um construtor que receba um contexto",
              name));
      }
      Servant servant;
      try {
        servant = constructor.newInstance(context);
      }
      catch (InstantiationException e) {
        throw new SCSException(e);
      }
      catch (IllegalAccessException e) {
        throw new SCSException(e);
      }
      catch (InvocationTargetException e) {
        throw new SCSException(e);
      }
      context.addFacet(name, interfaceName, servant);
    }
  }

  /**
   * Reads and puts all of the document's specified receptacles on the
   * component.
   * 
   * @param context The component.
   * 
   * @throws ReceptacleAlreadyExistsException
   */
  private static void readAndPutReceptacles(Element componentElement,
    ComponentContext context) throws ReceptacleAlreadyExistsException {
    Element receptaclesElement =
      getChildByName(componentElement, RECEPTACLES_ELEMENT);
    if (receptaclesElement == null) {
      return;
    }
    Element[] receptacleElementArray =
      getChildrenByName(receptaclesElement, RECEPTACLE_ELEMENT);

    for (int i = 0; i < receptacleElementArray.length; i++) {
      String name =
        getChildValueByName(receptacleElementArray[i], RECEPTACLE_NAME);
      String interfaceName =
        getChildValueByName(receptacleElementArray[i],
          RECEPTACLE_INTERFACE_NAME);
      boolean isMultiplex =
        Boolean.parseBoolean(getChildValueByName(receptacleElementArray[i],
          RECEPTACLE_MULTIPLEX));

      context.addReceptacle(name, interfaceName, isMultiplex);
    }
  }
}
