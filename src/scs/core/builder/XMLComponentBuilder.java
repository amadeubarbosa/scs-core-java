package scs.core.builder;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

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

public class XMLComponentBuilder {

  private final String COMPONENT_ID_ELEMENT = "id";
  private final String COMPONENT_ID_NAME = "name";
  private final String COMPONENT_ID_VERSION = "version";
  private final String COMPONENT_ID_PLATFORM_SPEC = "platformSpec";
  private final String COMPONENT_CONTEXT_ELEMENT = "context";
  private final String FACET_ELEMENT = "facet";
  private final String FACET_NAME = "name";
  private final String FACET_REP_ID = "repId";
  private final String FACET_SERVANT = "servant";
  private final String RECEPTACLE_ELEMENT = "receptacle";
  private final String RECEPTACLE_NAME = "name";
  private final String RECEPTACLE_REP_ID = "repId";
  private final String RECEPTACLE_MULTIPLEX = "isMultiplex";
  private final String VERSION_DELIMITER = "\\.";

  public ComponentContext build(ORB orb, POA poa, File f) throws SCSException {
    ComponentContext context = null;
    try {
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
      Document doc = dBuilder.parse(f);
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

  private String getTagValue(String tag, Element element) {
    NodeList list = element.getElementsByTagName(tag).item(0).getChildNodes();
    Node value = list.item(0);
    return value.getNodeValue();
  }

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

  private Class<?> getComponentContextClass(Document doc)
    throws ClassNotFoundException {
    try {
      String className = null;
      Element element = (Element) doc.getFirstChild();
      className = getTagValue(COMPONENT_CONTEXT_ELEMENT, element);
      return Class.forName(className, true, Thread.currentThread()
        .getContextClassLoader());
    }
    catch (ClassNotFoundException e) {
      throw e;
    }
    catch (Exception e) {
      return null;
    }
  }

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
        String interfaceName = getTagValue(FACET_REP_ID, element);
        String servantName = getTagValue(FACET_SERVANT, element);
        Class<?> c =
          Class.forName(servantName, true, Thread.currentThread()
            .getContextClassLoader());
        Object servant =
          c.getConstructor(ComponentContext.class).newInstance(context);
        if (servant instanceof Servant) {
          context.putFacet(name, interfaceName, (Servant) servant);
        }
        else {
          throw new InvalidServantException();
        }
      }
    }
  }

  private void readAndPutReceptacles(Document doc, ComponentContext context) {
    NodeList list = doc.getElementsByTagName(RECEPTACLE_ELEMENT);
    for (int i = 0; i < list.getLength(); i++) {
      Node node = list.item(i);
      if (node.getNodeType() == Node.ELEMENT_NODE) {
        Element element = (Element) node;
        String name = getTagValue(RECEPTACLE_NAME, element);
        String interfaceName = getTagValue(RECEPTACLE_REP_ID, element);
        boolean isMultiplex =
          Boolean.parseBoolean(getTagValue(RECEPTACLE_MULTIPLEX, element));
        context.putReceptacle(name, interfaceName, isMultiplex);
      }
    }
  }
}
