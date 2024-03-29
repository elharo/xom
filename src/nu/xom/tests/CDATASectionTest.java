/* Copyright 2002-2005 Elliotte Rusty Harold
   
   This library is free software; you can redistribute it and/or modify
   it under the terms of version 2.1 of the GNU Lesser General Public 
   License as published by the Free Software Foundation.
   
   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
   GNU Lesser General Public License for more details.
   
   You should have received a copy of the GNU Lesser General Public
   License along with this library. If not, see
   <https://www.gnu.org/licenses/>.
   
   You can contact Elliotte Rusty Harold by sending e-mail to
   elharo@ibiblio.org. Please include the word "XOM" in the
   subject line. The XOM home page is located at https://xom.nu/
*/

package nu.xom.tests;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import nu.xom.*;

/**
 * <p>
 *   Test that CDATA sections are read and where possible
 *   preserved upon serialization.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.3.0
 *
 */
public class CDATASectionTest extends XOMTestCase {

    
    public CDATASectionTest(String name) {
        super(name);
    }
    
    
    private String data = "<test><child1><![CDATA[<&>]]></child1>"
     + "<child2> <![CDATA[<&>]]> </child2> "
     + "<child3><![CDATA[<&>]]> </child3> "
     + "<child4><![CDATA[<&>]]> <![CDATA[<&>]]></child4> "
     + "<child5><![CDATA[<&>]]>]]&gt;<![CDATA[<&>]]></child5> "
     + "</test>";
    private Document doc;
    private Builder builder;
    
    
    protected void setUp() 
      throws ValidityException, ParsingException, IOException {
        builder = new Builder();
        doc = builder.build(data, "http://www.base.com");   
    }
    
    
    public void testCopy() {
        Element child1 = doc.getRootElement().getFirstChildElement("child1");
        Text cdata = (Text) child1.getChild(0);
        Text copy = cdata.copy();
        assertEquals("nu.xom.CDATASection", copy.getClass().getName());  
        assertEquals("<&>", copy.getValue());  
    }

    
    public void testToXML() {
        Element child1 = doc.getRootElement().getFirstChildElement("child1");
        Node cdata = child1.getChild(0);
        String result = cdata.toXML();
        assertEquals("<![CDATA[<&>]]>", result);  
    }

    
    public void testToXMLWhenCDATASectionContainsEndDelimiter() {
        Element child1 = doc.getRootElement().getFirstChildElement("child1");
        Text cdata = (Text) child1.getChild(0);
        cdata.setValue("A]]>A");
        assertEquals("A]]&gt;A", cdata.toXML());  
    }

    
    public void testUseCDATAWherePossible() {
        Element child1 = doc.getRootElement().getFirstChildElement("child1");
        Node cdata = child1.getChild(0);
        assertTrue(cdata instanceof Text);  
        assertEquals("nu.xom.CDATASection", cdata.getClass().getName());  
        assertEquals("<&>", cdata.getValue());  
    }

    
    public void testDontAllowCDATASectionToSplitTextNode() {
        Element child2 = doc.getRootElement().getFirstChildElement("child2");
        assertEquals(1, child2.getChildCount());
        Node data = child2.getChild(0);
        assertTrue(data instanceof Text);  
        assertEquals("nu.xom.Text", data.getClass().getName());  
        assertEquals(" <&> ", data.getValue());  
    }
    
    
    public void testAccumulateTextNodeAfterCDATASection() {
        Element child3 = doc.getRootElement().getFirstChildElement("child3");
        assertEquals(1, child3.getChildCount());
        Node data = child3.getChild(0);
        assertTrue(data instanceof Text);  
        assertEquals("nu.xom.Text", data.getClass().getName());  
        assertEquals("<&> ", data.getValue());  
    }
    
    
    public void testAccumulateTextNodeAcrossMultipleCDATASections() {
        Element child4 = doc.getRootElement().getFirstChildElement("child4");
        assertEquals(1, child4.getChildCount());
        Node data = child4.getChild(0);
        assertTrue(data instanceof Text);  
        assertEquals("nu.xom.Text", data.getClass().getName());  
        assertEquals("<&> <&>", data.getValue());  
    }
    
    
    public void testDontAllowCDATASectionToContainCDATASectionEndDelimiter() {
        Element child5 = doc.getRootElement().getFirstChildElement("child5");
        assertEquals(1, child5.getChildCount());
        Node data = child5.getChild(0);
        assertTrue(data instanceof Text);  
        assertEquals("<&>]]><&>", data.getValue());
        assertEquals("&lt;&amp;&gt;]]&gt;&lt;&amp;&gt;", data.toXML());
    }
    
    
    public void testDontAllowCDATASectionToContainCDATASectionEndDelimiter2() 
      throws IOException {
        Element child5 = doc.getRootElement().getFirstChildElement("child5");
        assertEquals(1, child5.getChildCount());
        child5.detach();
        Document doc = new Document(child5);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Serializer serializer = new Serializer(out);
        serializer.write(doc);
        serializer.flush();
        String result = new String(out.toByteArray(), "UTF-8");
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
          + "<child5>&lt;&amp;&gt;]]&gt;&lt;&amp;&gt;</child5>\r\n", result);
    }
    
    
    public void testSerializeCDATASection() throws IOException {  
        ByteArrayOutputStream out = new ByteArrayOutputStream(); 
        Serializer serializer = new Serializer(out);  
        serializer.write(doc);
        byte[] data = out.toByteArray();
        String result = new String(data, "UTF8");
        assertTrue(result.indexOf("<![CDATA[<&>]]>") > 0);
        
    }

    
    public void testSerializeCDATASectionWithOutOfRangeCharacter() 
      throws ValidityException, ParsingException, IOException {  
          
        String data = "<test><![CDATA[\u0298]]></test>";
        doc = builder.build(data, "http://www.example.com");
        ByteArrayOutputStream out = new ByteArrayOutputStream(); 
        Serializer serializer = new Serializer(out, "ISO-8859-1");  
        serializer.write(doc);
        byte[] output = out.toByteArray();
        String result = new String(output, "8859_1");
        assertEquals(-1, result.indexOf("<![CDATA[<&>]]>"));
        assertTrue(result.indexOf("&#x298;") > 1);
        
    }

    
    public void testSerializeCDATASectionWithInRangeCharactersAndANonUnicodeEncoding() 
      throws ValidityException, ParsingException, IOException {  
          
        String data = "<test><![CDATA[abcd]]></test>";
        doc = builder.build(data, "http://www.example.com");
        ByteArrayOutputStream out = new ByteArrayOutputStream(); 
        Serializer serializer = new Serializer(out, "ISO-8859-1");  
        serializer.write(doc);
        byte[] output = out.toByteArray();
        String result = new String(output, "8859_1");
        assertTrue(result.indexOf("<![CDATA[abcd]]>") > 1);
        
    }

    
    public void testSerializeCDATASectionWithCDATASectionEndDelimiter() 
      throws ValidityException, ParsingException, IOException {  
          
        String data = "<test><![CDATA[original data]]></test>";
        doc = builder.build(data, "http://www.example.com");
        Text content = (Text) (doc.getRootElement().getChild(0));
        content.setValue("]]>");
        ByteArrayOutputStream out = new ByteArrayOutputStream(); 
        Serializer serializer = new Serializer(out);  
        serializer.write(doc);
        byte[] output = out.toByteArray();
        String result = new String(output, "UTF8");
        assertEquals(-1, result.indexOf("<![CDATA[]]>]]>"));
        assertTrue(result.indexOf("]]&gt;") > 1);
    }


}