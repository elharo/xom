/* Copyright 2005, 2006 Elliotte Rusty Harold
   
   This library is free software; you can redistribute it and/or modify
   it under the terms of version 2.1 of the GNU Lesser General Public 
   License as published by the Free Software Foundation.
   
   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
   GNU Lesser General Public License for more details.
   
   You should have received a copy of the GNU Lesser General Public
   License along with this library; if not, write to the 
   Free Software Foundation, Inc., 59 Temple Place, Suite 330, 
   Boston, MA 02111-1307  USA
   
   You can contact Elliotte Rusty Harold by sending e-mail to
   elharo@ibiblio.org. Please include the word "XOM" in the
   subject line. The XOM home page is located at https://xom.nu/
*/
package nu.xom.tests;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.IllegalDataException;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParsingException;

/**
 * <p>
 * Tests for <code>xml:id</code> attributes.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.2b2
 *
 */
public class IDTest extends XOMTestCase {

    
    public IDTest(String name) {
        super(name);
    }

    
    public void testBuilderAllowsNonNCNameXmlIdAttributes() 
      throws ParsingException, IOException {
        
        Builder builder = new Builder();
        String data = "<root xml:id='p 2'/>";
        Document doc = builder.build(data, null);
        Element root = doc.getRootElement();
        Attribute id = root.getAttribute(0);
        assertEquals("p 2", id.getValue());  
        
    }
    
    
    public void testIDCanBeNonNCName() {
        
        Attribute id = new Attribute("xml:id", 
          "http://www.w3.org/XML/1998/namespace", "name");
        assertEquals("name", id.getValue());
        id.setValue("not a name");
        assertEquals(id.getValue(), "not a name");
        
    }

    
    public void testAttributeConstructorNormalizesValue() {
        
        Attribute id = new Attribute("xml:id", 
          "http://www.w3.org/XML/1998/namespace", " name ");
        assertEquals("name", id.getValue());
        
    }

    
    public void testNameSetIDNeedNotBeNCName() {
        
        Attribute id = new Attribute("id", "not a name");
        id.setNamespace("xml", 
          "http://www.w3.org/XML/1998/namespace");
         assertEquals(Attribute.Type.ID, id.getType());
        
    }

    
    public void testBuilderNormalizesXmlIdAttributes() 
      throws ParsingException, IOException {
        
        Builder builder = new Builder();
        String data = "<root xml:id='  p2  '/>";
        Document doc = builder.build(data, null);
        Element root = doc.getRootElement();
        String value = root.getAttributeValue("id", 
          "http://www.w3.org/XML/1998/namespace");
        assertEquals("p2", value);
        
    }

    
    public void testBuilderDoesNotOverNormalizeXmlIdAttributesWithCarriageReturns() 
      throws ParsingException, IOException {
        
        Builder builder = new Builder();
        String data = "<root xml:id='&#x0D;  p2  '/>";
        Document doc = builder.build(data, null);
        Element root = doc.getRootElement();
        Attribute id = root.getAttribute(0);
        assertEquals("\r\u0020p2", id.getValue());
        
    }

    
    public void testBuilderDoesNotOverNormalizeXmlIdAttributesWithLineFeeds() 
      throws ParsingException, IOException {
        
        Builder builder = new Builder();
        String data = "<root xml:id='&#x0A;  p2  '/>";
        Document doc = builder.build(data, null);
        Element root = doc.getRootElement();
        Attribute id = root.getAttribute(0);
        assertEquals("\n\u0020p2", id.getValue());
        
    }

    
    public void testBuiltXmlIdAttributeHasTypeId() 
      throws ParsingException, IOException {
        
        Builder builder = new Builder();
        String data = "<root xml:id='  p2  '/>";
        Document doc = builder.build(data, null);
        Element root = doc.getRootElement();
        Attribute id = root.getAttribute("id", 
          "http://www.w3.org/XML/1998/namespace");
        assertEquals(Attribute.Type.ID, id.getType());
        
    }
    
    
    public void testConstructedXmlIdAttributeHasTypeId() 
      throws ParsingException, IOException {
        
        Attribute id = new Attribute("xml:id", 
          "http://www.w3.org/XML/1998/namespace", "p2");
        assertEquals(Attribute.Type.ID, id.getType());
        
    }
    
    
    public void testNamespaceSetXmlIdAttributeHasTypeId() {
        
        Attribute id = new Attribute("id", "p2");
        id.setNamespace("xml", "http://www.w3.org/XML/1998/namespace");
        assertEquals(Attribute.Type.ID, id.getType());
        
    }

    
    public void testNameSetXmlIdAttributeHasTypeId() {
        
        Attribute id = new Attribute("xml:space", 
          "http://www.w3.org/XML/1998/namespace", "preserve");
        id.setLocalName("id");
        assertEquals(Attribute.Type.ID, id.getType());
        
    }

    
    public void testNameSetXmlIdAttributeMustBeNCName() {
        
        Attribute id = new Attribute("xml:space", 
          "http://www.w3.org/XML/1998/namespace", "not a name");
        try {
            id.setLocalName("id");
            fail("Allowed non-NCNAME ID");
        }
        catch (IllegalDataException success) {
            assertNotNull(success.getMessage());
        }
        
    }

    
    public void testValueSetXmlIdAttributeIsNormalized() {
        
        Attribute id = new Attribute("xml:id", 
          "http://www.w3.org/XML/1998/namespace", "value");
        id.setValue("   a3 ");
        assertEquals("a3", id.getValue());
        
    }

    
    public void testCantChangeTypeOfXMLIDAttribute() {
        
        Attribute id = new Attribute("xml:id", 
          "http://www.w3.org/XML/1998/namespace", "p2");
        
        try {
            id.setType(Attribute.Type.CDATA);
            fail("changed xml:id attribute to CDATA");
        }
        catch (IllegalDataException success) {
            assertNotNull(success.getMessage());
        }
        assertEquals(Attribute.Type.ID, id.getType());
        
    }

    
    public void testXPathRecognizesXmlIDAttributes() 
      throws ParsingException, IOException {
        
        Element root = new Element("root");
        Document doc = new Document(root);
        Element child = new Element("child");
        root.appendChild(child);
        Attribute id = new Attribute("id", "p2");
        child.addAttribute(id);
        id.setNamespace("xml", "http://www.w3.org/XML/1998/namespace");
        Nodes result = doc.query("id('p2')");
        assertEquals(1, result.size());
        assertEquals(child, result.get(0));
        
    }
    
    
    public void testXMLIDTestSuiteFromW3CServer() 
      throws ParsingException, IOException {
        
        URL base = new URL("http://www.w3.org/XML/2005/01/xml-id/test-suite.xml");
        Builder builder = new Builder();
        Document catalog = builder.build(base.openStream());
        Element testsuite = catalog.getRootElement();
        Elements testCatalogs = testsuite.getChildElements("test-catalog");
        for (int i = 0; i < testCatalogs.size(); i++) {
            Elements testcases = testCatalogs.get(i).getChildElements("test-case");
            for (int j = 0; j < testcases.size(); j++) {
                Element testcase = testcases.get(j);
                String features = testcase.getAttributeValue("feature");
                if (features != null && features.indexOf("xml11") >= 0) {
                    continue; // skip test
                }
                URL testURL = new URL(base, testcase.getFirstChildElement("file-path").getValue() + "/");
                Element scenario = testcase.getFirstChildElement("scenario");
                boolean errorExpected = scenario.getAttribute("operation").getValue().equals("error");
                Element input = scenario.getFirstChildElement("input-file");
                URL inputFile = new URL(testURL, input.getValue());
                Elements expectedIDs = scenario.getChildElements("id");
                try {
                    Document inputDoc = builder.build(inputFile.openStream());
                    Nodes recognizedIDs = getIDs(inputDoc);
                    assertEquals(expectedIDs.size(), recognizedIDs.size());
                    for (int k = 0; k < expectedIDs.size(); k++) {
                        assertEquals(expectedIDs.get(i).getValue(), recognizedIDs.get(i).getValue());
                    }
                    if (errorExpected) fail("Did not detect xml:id error");
                }
                catch (ParsingException ex) {
                    if (!errorExpected) throw ex;
                }
            } // end for
        }
        
    }
    
    
    public void testXMLIDTestSuite() 
      throws ParsingException, IOException {
        
        Builder builder = new Builder();
        File base = new File("data");
        base = new File(base, "xmlid");
        File catalog = new File(base, "catalog.xml");
        
        // The test suite needs to be installed separately. If we can't
        // find the catalog, we just don't run these tests.
        if (catalog.exists()) {
            Document doc = builder.build(catalog);
            Element testsuite = doc.getRootElement();
            Elements testCatalogs = testsuite.getChildElements("test-catalog");
            for (int i = 0; i < testCatalogs.size(); i++) {
                Elements testcases = testCatalogs.get(i).getChildElements("test-case");
                for (int j = 0; j < testcases.size(); j++) {
                    Element testcase = testcases.get(j);
                    String features = testcase.getAttributeValue("features");
                    if (features != null && features.indexOf("xml11") >= 0) {
                        continue; // skip test
                    }
                    File root = new File(base, testcase.getFirstChildElement("file-path").getValue());
                    File inputFile = null;
                    Element scenario = testcase.getFirstChildElement("scenario");
                    Element input = scenario.getFirstChildElement("input");
                    inputFile = new File(root, input.getValue());
                    Elements expectedIDs = scenario.getChildElements("id");
                    try {
                        Document inputDoc = builder.build(inputFile);
                        Nodes recognizedIDs = getIDs(inputDoc);
                        assertEquals(expectedIDs.size(), recognizedIDs.size());
                        for (int k = 0; k < expectedIDs.size(); k++) {
                            assertEquals(expectedIDs.get(i).getValue(), recognizedIDs.get(i).getValue());
                        }
                    }
                    catch (ParsingException ex) {
                        System.err.println(inputFile);
                        ex.printStackTrace();
                    }
                } // end for
            }
            
        } // end if 
        
    }


    private Nodes getIDs(Document doc) {

        Element root = doc.getRootElement();
        Nodes list = new Nodes();
        getIDs(root, list);
        return list;
    }
    
    
    private void getIDs(Element element, Nodes list) {

        for (int i = 0; i < element.getAttributeCount(); i++) {
            Attribute a = element.getAttribute(i);
            if (a.getType() == Attribute.Type.ID) {
                // need to sort these into specific order
                list.append(a);
            }
        }
        for (int i = 0; i < element.getChildCount(); i++) {
            Node child = element.getChild(i);
            if (child instanceof Element) {
                getIDs((Element) child, list);
            }
        }
        
    }
    
    
}
