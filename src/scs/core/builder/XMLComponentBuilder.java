package scs.core.builder;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
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

import scs.core.ComponentContext;
import scs.core.ComponentId;
import scs.core.builder.exception.NoComponentIdException;
import scs.core.exception.InvalidServantException;
import scs.core.exception.SCSException;

/**
 * The purpose of this class is to build components based entirely on an XML
 * description. The XML file must comply to the official XSD.
 * 
 */
public class XMLComponentBuilder {

  private final String COMPONENT_ID_ELEMENT = "id";
  private final String COMPONENT_ID_NAME = "name";
  private final String COMPONENT_ID_VERSION = "version";
  private final String COMPONENT_ID_PLATFORM_SPEC = "platformSpec";
  private final String COMPONENT_CONTEXT_ELEMENT = "context";
  private final String FACET_ELEMENT = "facet";
  private final String FACET_NAME = "name";
  private final String FACET_INTERFACE_NAME = "interfaceName";
  private final String FACET_IMPL = "facetImpl";
  private final String RECEPTACLE_ELEMENT = "receptacle";
  private final String RECEPTACLE_NAME = "name";
  private final String RECEPTACLE_INTERFACE_NAME = "interfaceName";
  private final String RECEPTACLE_MULTIPLEX = "isMultiplex";
  private final String VERSION_DELIMITER = "\\.";

  /**
   * Builds a component, based on an XML file. The component will be composed of
   * the basic facets, plus all facets and receptacles present on the XML file.
   * 
   * @param orb The orb that shall be associated to this component and its CORBA
   *        objects.
   * @param poa The poa that shall be used to activate and deactivate the
   *        servants.
   * @param f The XML file.
   * @return A fully assembled component, with working facets, as described by
   *         the XML file.
   * @throws SCSException If any error occurs. The exception will contain the
   *         more specific exception.
   */
  public ComponentContext build(ORB orb, POA poa, File f) throws SCSException {
    ComponentContext context = null;
    try {
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      dbFactory.setNamespaceAware(true);
      DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
      Document doc = dBuilder.parse(f);

      SchemaFactory factory =
        SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
      // obtains xsd file from classpath
      Schema schema =
        factory.newSchema(new File(getClass().getClassLoader().getResource(
          "ComponentDescription.xsd").toURI()));
      Validator validator = schema.newValidator();
      validator.validate(new DOMSource(doc));

      doc.getDocumentElement().normalize();

      ComponentId id = getComponentId(doc);

      Class<?> contextClass = getComponentContextClass(doc);
      if (contextClass != null) {
        context =
          (ComponentContext) contextClass.getConstructor(ORB.class, POA.class,
            ComponentId.class).newInstance(orb, poa, id);
      }
      else {
        context = new ComponentContext(orb, poa, id);
      }

      readAndPutFacets(doc, context);
      readAndPutReceptacles(doc, context);
    }
    catch (Exception e) {
      throw new SCSException(e);
    }
    return context;
  }

  /**
   * Obtains the value from a tag.
   * 
   * @param tag The tag to get the value from.
   * @param element The Element that contains the specified tag.
   */
  private String getTagValue(String tag, Element element) {
    NodeList list = element.getElementsByTagName(tag).item(0).getChildNodes();
    Node value = list.item(0);
    return value.getNodeValue();
  }

  /**
   * Obtains the componentId from the document.
   * 
   * @param doc The document with the component specification.
   */
  private ComponentId getComponentId(Document doc)
    throws NoComponentIdException {
    ComponentId id = new ComponentId();

    boolean filled = false;
    NodeList list = doc.getElementsByTagName(COMPONENT_ID_ELEMENT);
    for (int i = 0; i < list.getLength(); i++) {
      Node node = list.item(i);
      if (node.getNodeType() == Node.ELEMENT_NODE) {
        Element element = (Element) node;
        id.name = getTagValue(COMPONENT_ID_NAME, element);
        String version = getTagValue(COMPONENT_ID_VERSION, element);
        String[] versions = version.split(VERSION_DELIMITER);
        id.major_version = Byte.parseByte(versions[0]);
        id.minor_version = Byte.parseByte(versions[1]);
        id.patch_version = Byte.parseByte(versions[2]);
        id.platform_spec = getTagValue(COMPONENT_ID_PLATFORM_SPEC, element);
        filled = true;
      }
    }
    if (!filled) {
      throw new NoComponentIdException();
    }
    return id;
  }

  /**
   * Obtains the component context class from the document and loads the class.
   * 
   * @param doc The document with the component specification.
   * @throws ClassNotFoundException If the specified class cannot be found in
   *         the ContextClassLoader from the current thread.
   */
  private Class<?> getComponentContextClass(Document doc)
    throws ClassNotFoundException {
    try {
      String className = null;
      Element element = (Element) doc.getFirstChild();
      className = getTagValue(COMPONENT_CONTEXT_ELEMENT, element);
      return Class.forName(className, true, Thread.currentThread()
        .getContextClassLoader());
    }
    catch (Exception e) {
      return null;
    }
  }

  /**
   * Reads and puts all of the document's specified facets on the component.
   * 
   * @param doc The document with the component specification.
   * @param context The component.
   */
  private void readAndPutFacets(Document doc, ComponentContext context)
    throws ClassNotFoundException, IllegalArgumentException, SecurityException,
    InstantiationException, IllegalAccessException, InvocationTargetException,
    NoSuchMethodException, SCSException {
    NodeList list = doc.getElementsByTagName(FACET_ELEMENT);
    for (int i = 0; i < list.getLength(); i++) {
      Node node = list.item(i);
      if (node.getNodeType() == Node.ELEMENT_NODE) {
        Element element = (Element) node;
        String name = getTagValue(FACET_NAME, element);
        String interfaceName = getTagValue(FACET_INTERFACE_NAME, element);
        String facetImpl = getTagValue(FACET_IMPL, element);
        Class<?> c =
          Class.forName(facetImpl, true, Thread.currentThread()
            .getContextClassLoader());
        Object servant =
          c.getConstructor(ComponentContext.class).newInstance(context);
        if (servant instanceof Servant) {
          context.addFacet(name, interfaceName, (Servant) servant);
        }
        else {
          throw new InvalidServantException();
        }
      }
    }
  }

  /**
   * Reads and puts all of the document's specified receptacles on the
   * component.
   * 
   * @param doc The document with the component specification.
   * @param context The component.
   */
  private void readAndPutReceptacles(Document doc, ComponentContext context) {
    NodeList list = doc.getElementsByTagName(RECEPTACLE_ELEMENT);
    for (int i = 0; i < list.getLength(); i++) {
      Node node = list.item(i);
      if (node.getNodeType() == Node.ELEMENT_NODE) {
        Element element = (Element) node;
        String name = getTagValue(RECEPTACLE_NAME, element);
        String interfaceName = getTagValue(RECEPTACLE_INTERFACE_NAME, element);
        boolean isMultiplex =
          Boolean.parseBoolean(getTagValue(RECEPTACLE_MULTIPLEX, element));
        context.putReceptacle(name, interfaceName, isMultiplex);
      }
    }
  }
}
