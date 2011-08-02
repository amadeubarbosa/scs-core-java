package scs.core;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.omg.CORBA.ORB;
import org.omg.CORBA.UserException;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import scs.core.builder.XMLComponentBuilder;
import scs.core.exception.InvalidComponentContextException;
import scs.core.exception.SCSException;

public final class XMLComponentBuilderTest {
  private static ORB orb;
  private static POA poa;

  @BeforeClass
  public static void beforeClass() throws UserException, SCSException {
    Properties properties = new Properties();
    properties.put("org.omg.CORBA.ORBClass", "org.jacorb.orb.ORB");
    properties.put("org.omg.CORBA.ORBSingletonClass",
      "org.jacorb.orb.ORBSingleton");
    orb = ORB.init((String[]) null, properties);

    org.omg.CORBA.Object obj = orb.resolve_initial_references("RootPOA");
    poa = POAHelper.narrow(obj);
    poa.the_POAManager().activate();

    Thread thread = new Thread(new Runnable() {
      public void run() {
        orb.run();
      }
    });
    thread.start();
  }

  @AfterClass
  public static void afterClass() {
    orb.shutdown(true);
    orb.destroy();
  }

  @Test
  public void buildComponentComponentId() throws SCSException,
    URISyntaxException, IOException, ParserConfigurationException {
    XMLComponentBuilder builder = new XMLComponentBuilder(orb, poa);
    URL url = this.getClass().getResource("/ComponentId.xml");
    Assert.assertNotNull(url);
    File f = new File(url.toURI());
    ComponentContext context = builder.build(f);
    Assert.assertNotNull(context);
  }

  @Test
  public void buildComponentComponentContext() throws SCSException,
    URISyntaxException, IOException, ParserConfigurationException {
    XMLComponentBuilder builder = new XMLComponentBuilder(orb, poa);
    URL url = this.getClass().getResource("/ComponentContext.xml");
    Assert.assertNotNull(url);
    File f = new File(url.toURI());
    ComponentContext context = builder.build(f);
    Assert.assertNotNull(context);
  }

  @Test(expected = InvalidComponentContextException.class)
  public void buildComponentComponentContextInvalidConstructor()
    throws SCSException, URISyntaxException, IOException,
    ParserConfigurationException {
    XMLComponentBuilder builder = new XMLComponentBuilder(orb, poa);
    URL url =
      this.getClass().getResource("/ComponentContextInvalidConstructor.xml");
    Assert.assertNotNull(url);
    File f = new File(url.toURI());
    ComponentContext context = builder.build(f);
    Assert.assertNotNull(context);
  }

  @Test(expected = InvalidComponentContextException.class)
  public void buildComponentInvalidComponentContext() throws SCSException,
    URISyntaxException, IOException, ParserConfigurationException {
    XMLComponentBuilder builder = new XMLComponentBuilder(orb, poa);
    URL url = this.getClass().getResource("/InvalidComponentContext.xml");
    Assert.assertNotNull(url);
    File f = new File(url.toURI());
    ComponentContext context = builder.build(f);
    Assert.assertNotNull(context);
  }

  @Test
  public void buildComponentFacets() throws SCSException, URISyntaxException,
    IOException, ParserConfigurationException {
    XMLComponentBuilder builder = new XMLComponentBuilder(orb, poa);
    URL url = this.getClass().getResource("/Facets.xml");
    Assert.assertNotNull(url);
    File f = new File(url.toURI());
    ComponentContext context = builder.build(f);
    Assert.assertNotNull(context);
  }

  @Test(expected = SCSException.class)
  public void buildComponentInvalidFacet() throws SCSException,
    URISyntaxException, ParserConfigurationException, IOException {
    XMLComponentBuilder builder = new XMLComponentBuilder(orb, poa);
    URL url = this.getClass().getResource("/InvalidFacet.xml");
    Assert.assertNotNull(url);
    File f = new File(url.toURI());
    ComponentContext context = builder.build(f);
    Assert.assertNotNull(context);
  }

  @Test
  public void buildComponentReceptacles() throws SCSException,
    URISyntaxException, ParserConfigurationException, IOException {
    XMLComponentBuilder builder = new XMLComponentBuilder(orb, poa);
    URL url = this.getClass().getResource("/Receptacles.xml");
    Assert.assertNotNull(url);
    File f = new File(url.toURI());
    ComponentContext context = builder.build(f);
    Assert.assertNotNull(context);
  }
}
