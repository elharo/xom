/* Copyright 2011, 2018 Elliotte Rusty Harold
   
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
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.XPathContext;
import nu.xom.canonical.Canonicalizer;

/**
 * <p>
 *  Tests XML Canonicalization 1.1.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.3.0
 *
 */
public class Canonicalizer11Test extends TestCase {

    
    private File canonical;
    private ByteArrayOutputStream out = new ByteArrayOutputStream();
    private Canonicalizer canonicalizer = new Canonicalizer(out, Canonicalizer.CANONICAL_XML_11);
    private Document xmlSpaceInput;
    private Document xmlBaseInput;
    private XPathContext namespaces = new XPathContext();
    private Builder builder = new Builder(); 
    
    
    protected void setUp() throws ParsingException, IOException { 
        File data = new File("data");
        canonical = new File(data, "c14n11");
        File xmlSpaceFile = new File(canonical, "xmlspace-input.xml");
        xmlSpaceInput = builder.build(xmlSpaceFile);
        File xmlBaseFile = new File(canonical, "xmlbase-prop-input.xml");
        xmlBaseInput = builder.build(xmlBaseFile);
        namespaces.addNamespace("ietf", "http://www.ietf.org");
    }
    
    
    // 3.2.1.1 Test case c14n11/xmllang-1
    public void testXMLLang_1() throws ParsingException, IOException {
        File input = new File(canonical, "xmllang-input.xml");
        File expected = new File(canonical, "xmllang-1.output");
        
        Document doc = builder.build(input);
        String documentSubsetExpression = "(//. | //@* | //namespace::*)[ancestor-or-self::ietf:e1]";
        canonicalizer.write(doc.query(documentSubsetExpression, namespaces));  
        
        byte[] actualBytes = out.toByteArray();        
        byte[] expectedBytes = readFile(expected);
        assertEquals(expectedBytes, actualBytes);
    }
    

    // 3.2.1.2 Test case c14n11/xmllang-2
    public void testXMLLang_2() throws ParsingException, IOException {
        File input = new File(canonical, "xmllang-input.xml");
        File expected = new File(canonical, "xmllang-2.output");
        
        Document doc = builder.build(input);
        String documentSubsetExpression = "(//. | //@* | //namespace::*)[ancestor-or-self::ietf:e2]";
        canonicalizer.write(doc.query(documentSubsetExpression, namespaces));  
        
        byte[] actualBytes = out.toByteArray();        
        byte[] expectedBytes = readFile(expected);
        assertEquals(expectedBytes, actualBytes);
    }
    
    
    // 3.2.1.3 Test case c14n11/xmllang-3
    public void testXMLLang_3() throws ParsingException, IOException {
        File input = new File(canonical, "xmllang-input.xml");
        File expected = new File(canonical, "xmllang-3.output");
        
        Document doc = builder.build(input);
        String documentSubsetExpression = "(//. | //@* | //namespace::*)[ancestor-or-self::ietf:e11]";
        canonicalizer.write(doc.query(documentSubsetExpression, namespaces));  
        
        byte[] actualBytes = out.toByteArray();        
        byte[] expectedBytes = readFile(expected);
        assertEquals(expectedBytes, actualBytes);
    }
    
    
    // 3.2.1.4 Test case c14n11/xmllang-4
    public void testXMLLang_4() throws ParsingException, IOException {
        File input = new File(canonical, "xmllang-input.xml");
        File expected = new File(canonical, "xmllang-4.output");
        
        Document doc = builder.build(input);
        String documentSubsetExpression = "(//. | //@* | //namespace::*)[ancestor-or-self::ietf:e11 or ancestor-or-self::ietf:e12]";
        canonicalizer.write(doc.query(documentSubsetExpression, namespaces));  
        
        byte[] actualBytes = out.toByteArray();        
        byte[] expectedBytes = readFile(expected);
        assertEquals(expectedBytes, actualBytes);
    }
    
    
    // 3.2.2.1 Test case c14n11/xmlspace-1
    public void testXMLSpace_1() throws ParsingException, IOException {
        File expected = new File(canonical, "xmlspace-1.output");
        
        String documentSubsetExpression = "(//. | //@* | //namespace::*) [ancestor-or-self::ietf:e1]";
        canonicalizer.write(xmlSpaceInput.query(documentSubsetExpression, namespaces));  
        
        byte[] actualBytes = out.toByteArray();        
        byte[] expectedBytes = readFile(expected);
        assertEquals(expectedBytes, actualBytes);
    }


    // 3.2.2.2 Test case c14n11/xmlspace-2
    public void testXMLSpace_2() throws ParsingException, IOException {
        File expected = new File(canonical, "xmlspace-2.output");
        
        String documentSubsetExpression = "(//. | //@* | //namespace::*) [ancestor-or-self::ietf:e2]";
        canonicalizer.write(xmlSpaceInput.query(documentSubsetExpression, namespaces));  
        
        byte[] actualBytes = out.toByteArray();        
        byte[] expectedBytes = readFile(expected);
        assertEquals(expectedBytes, actualBytes);
    }


    // 3.2.2.3 Test case c14n11/xmlspace-3
    public void testXMLSpace_3() throws ParsingException, IOException {
        File expected = new File(canonical, "xmlspace-3.output");
        
        String documentSubsetExpression = "(//. | //@* | //namespace::*) [ancestor-or-self::ietf:e11]";
        canonicalizer.write(xmlSpaceInput.query(documentSubsetExpression, namespaces));  
        
        byte[] actualBytes = out.toByteArray();        
        byte[] expectedBytes = readFile(expected);
        assertEquals(expectedBytes, actualBytes);
    }


    // 3.2.2.4 Test case c14n11/xmlspace-4
    public void testXMLSpace_4() throws ParsingException, IOException {
        File expected = new File(canonical, "xmlspace-4.output");
        
        String documentSubsetExpression = "(//. | //@* | //namespace::*) [ancestor-or-self::ietf:e11 or ancestor-or-self::ietf:e12]";
        canonicalizer.write(xmlSpaceInput.query(documentSubsetExpression, namespaces));  
        
        byte[] actualBytes = out.toByteArray();        
        byte[] expectedBytes = readFile(expected);
        assertEquals(expectedBytes, actualBytes);
    }
    
       
    // 3.2.3.1 Test case c14n11/xmlid-1
    public void testXMLID_1() throws ParsingException, IOException {
        File expected = new File(canonical, "xmlid-1.output");
        File input = new File(canonical, "xmlid-input.xml");
        Document xmlIdInput = builder.build(input);
        
        String documentSubsetExpression = "(//. | //@* | //namespace::*) [ancestor-or-self::ietf:e1]";
        canonicalizer.write(xmlIdInput.query(documentSubsetExpression, namespaces));  
        
        byte[] actualBytes = out.toByteArray();        
        byte[] expectedBytes = readFile(expected);
        assertEquals(expectedBytes, actualBytes);
    }

    
    // 3.2.3.2 Test case c14n11/xmlid-2
    public void testXMLID_2() throws ParsingException, IOException {
        File expected = new File(canonical, "xmlid-2.output");
        File input = new File(canonical, "xmlid-input.xml");
        Document xmlIdInput = builder.build(input);
        
        String documentSubsetExpression = "(//. | //@* | //namespace::*) [ancestor-or-self::ietf:e11 or ancestor-or-self::ietf:e12]";
        canonicalizer.write(xmlIdInput.query(documentSubsetExpression, namespaces));  
        
        byte[] actualBytes = out.toByteArray();        
        byte[] expectedBytes = readFile(expected);
        assertEquals(expectedBytes, actualBytes);
    }
    
 
    public void testNewXMLAttributesAreNotInherited() throws ParsingException, IOException {
        String input = "<foo xml:href='http://www.w3.org/TR/2008/PR-xml-c14n11-20080129/#ProcessingModel' " +
        "xml:text='Attributes in the XML namespace other than xml:base, xml:id, xml:lang, and xml:space MUST be processed as ordinary attributes.'>" +
        "<bar/></foo>";
        Document doc = builder.build(input, "http://www.w3.org/TR/2008/PR-xml-c14n11-20080129/");
        
        String documentSubsetExpression = "(//. | //@* | //namespace::*)[ancestor-or-self::bar]";
        canonicalizer.write(doc.query(documentSubsetExpression));  
        
        String actual = new String(out.toByteArray(), "UTF-8");        
        assertEquals("<bar></bar>", actual);
    }
    
    
    // 3.2.4.1.1 Test case c14n11/xmlbase-prop-1
    public void testXMLBase_Prop_1() throws ParsingException, IOException {
        File expected = new File(canonical, "xmlbase-prop-1.output");
        
        String documentSubsetExpression 
            = "(//. | //@* | //namespace::*) [ancestor-or-self::ietf:c14n11XmlBaseDoc1 and not(ancestor-or-self::ietf:e2)]";
        canonicalizer.write(xmlBaseInput.query(documentSubsetExpression, namespaces));  
        
        byte[] actualBytes = out.toByteArray();        
        byte[] expectedBytes = readFile(expected);
        assertEquals(expectedBytes, actualBytes);
    }
    
    
    // 3.2.4.1.2 Test case c14n11/xmlbase-prop-2
    public void testXMLBase_Prop_2() throws ParsingException, IOException {
        File expected = new File(canonical, "xmlbase-prop-2.output");
        
        String documentSubsetExpression 
            = "(//. | //@* | //namespace::*) [ancestor-or-self::ietf:e1]";
        canonicalizer.write(xmlBaseInput.query(documentSubsetExpression, namespaces));  
        
        byte[] actualBytes = out.toByteArray();        
        byte[] expectedBytes = readFile(expected);
        assertEquals(expectedBytes, actualBytes);
    }  
    
    
    // 3.2.4.1.3 Test case c14n11/xmlbase-prop-3
    public void testXMLBase_Prop_3() throws ParsingException, IOException {
        File expected = new File(canonical, "xmlbase-prop-3.output");
        
        String documentSubsetExpression 
            = "(//. | //@* | //namespace::*) [ancestor-or-self::ietf:e11]";
        canonicalizer.write(xmlBaseInput.query(documentSubsetExpression, namespaces));  
        
        byte[] actualBytes = out.toByteArray();        
        byte[] expectedBytes = readFile(expected);
        assertEquals(expectedBytes, actualBytes);
    }
    
    
    // 3.2.4.1.4 Test case c14n11/xmlbase-prop-4
    public void testXMLBase_Prop_4() throws ParsingException, IOException {
        File expected = new File(canonical, "xmlbase-prop-4.output");
        
        String documentSubsetExpression 
            = "(//. | //@* | //namespace::*) [ancestor-or-self::ietf:e111]";
        canonicalizer.write(xmlBaseInput.query(documentSubsetExpression, namespaces));  
        
        byte[] actualBytes = out.toByteArray();        
        byte[] expectedBytes = readFile(expected);
        assertEquals(expectedBytes, actualBytes);
    }
    
    
    // 3.2.4.1.5 Test case c14n11/xmlbase-prop-5
    public void testXMLBase_Prop_5() throws ParsingException, IOException {
        File expected = new File(canonical, "xmlbase-prop-5.output");
        
        String documentSubsetExpression 
            = "(//. | //@* | //namespace::*) [ancestor-or-self::ietf:e21]";
        canonicalizer.write(xmlBaseInput.query(documentSubsetExpression, namespaces));  
        
        byte[] actualBytes = out.toByteArray();        
        byte[] expectedBytes = readFile(expected);
        assertEquals(expectedBytes, actualBytes);
    }
    
    
    // 3.2.4.1.6 Test case c14n11/xmlbase-prop-6
    public void testXMLBase_Prop_6() throws ParsingException, IOException {
        File expected = new File(canonical, "xmlbase-prop-6.output");
        
        String documentSubsetExpression 
            = "(//. | //@* | //namespace::*) [ancestor-or-self::ietf:e3]";
        canonicalizer.write(xmlBaseInput.query(documentSubsetExpression, namespaces));  
        
        byte[] actualBytes = out.toByteArray();        
        byte[] expectedBytes = readFile(expected);
        assertEquals(expectedBytes, actualBytes);
    }
    
    
    // 3.2.4.1.7 Test case c14n11/xmlbase-prop-7
    public void testXMLBase_Prop_7() throws ParsingException, IOException {
        File expected = new File(canonical, "xmlbase-prop-7.output");
        
        String documentSubsetExpression 
            = "(//. | //@* | //namespace::*) [ancestor-or-self::ietf:c14n11XmlBaseDoc1 and not(ancestor-or-self::ietf:e1 or ancestor-or-self::ietf:e2)]";
        canonicalizer.write(xmlBaseInput.query(documentSubsetExpression, namespaces));  
        
        byte[] actualBytes = out.toByteArray();        
        byte[] expectedBytes = readFile(expected);
        assertEquals(expectedBytes, actualBytes);
    }

    // 3.2.4.2.1 Test case c14n11/xmlbase-c14n11spec-102
    public void testXMLBase_1() throws ParsingException, IOException {
        File expected = new File(canonical, "xmlbase-c14n11spec-102.output");
        File xmlBaseFile = new File(canonical, "xmlbase-c14n11spec-input.xml");
        Document doc = builder.build(xmlBaseFile);
        
        String documentSubsetExpression 
            = "(//. | //@* | //namespace::*)[self::ietf:e1 or (parent::ietf:e1 and not(self::text() or self::e2)) or count(id(\"E3\")|ancestor-or-self::node()) = count(ancestor-or-self::node())]";
        canonicalizer.write(doc.query(documentSubsetExpression, namespaces));  
        
        byte[] actualBytes = out.toByteArray();        
        byte[] expectedBytes = readFile(expected);
        assertEquals(expectedBytes, actualBytes);
    }
    
    
    // 3.2.4.2.2 Test case c14n11/xmlbase-c14n11spec2-102
    public void testXMLBase_2() throws ParsingException, IOException {
        File input = new File(canonical, "xmlbase-c14n11spec2-input.xml");
        Document doc = builder.build(input);
        File expected = new File(canonical, "xmlbase-c14n11spec2-102.output");
        
        String documentSubsetExpression 
            = "(//. | //@* | //namespace::*)[self::ietf:e1 or (parent::ietf:e1 and not(self::text() or self::e2)) or count(id(\"E3\")|ancestor-or-self::node()) = count(ancestor-or-self::node())]";
        canonicalizer.write(doc.query(documentSubsetExpression, namespaces));  
        
        byte[] actualBytes = out.toByteArray();        
        byte[] expectedBytes = readFile(expected);
        assertEquals(expectedBytes, actualBytes);
    }
    
    
    // 3.2.4.2.3 Test case c14n11/xmlbase-c14n11spec3-103
    public void testXMLBase_3() throws ParsingException, IOException {
        File input = new File(canonical, "xmlbase-c14n11spec3-input.xml");
        Document doc = builder.build(input);
        File expected = new File(canonical, "xmlbase-c14n11spec3-103.output");
        
        String documentSubsetExpression = "(//. | //@* | //namespace::*) [self::a or ancestor-or-self::d]";
        canonicalizer.write(doc.query(documentSubsetExpression, namespaces));  
        
        byte[] actualBytes = out.toByteArray();        
        byte[] expectedBytes = readFile(expected);
        assertEquals(expectedBytes, actualBytes);
    }


    private byte[] readFile(File expected) throws IOException {
        byte[] expectedBytes = new byte[(int) expected.length()];
        InputStream fin = new FileInputStream(expected);
        DataInputStream in = new DataInputStream(fin);
        try {
            in.readFully(expectedBytes);
        }
        finally {
            in.close();
        }
        return expectedBytes;
    }
    
    
    /**
     * <p>
     * Asserts that two byte arrays are equal. If the two arrays are  
     * not equal a <code>ComparisonFailure</code> is thrown. Two 
     * arrays are equal if and only if they have the same length, 
     * and each item in the expected array is equal to the 
     * corresponding item in the actual array. 
     * </p>
     *
     * @param expected the byte array the test should produce
     * @param actual the byte array the test does produce
     */
    private void assertEquals(byte[] expected, byte[] actual) {
        
        if (expected == null && actual == null) {
            return;
        }
        // what if one is null and the other isn't????
        try {
            assertEquals(expected.length, actual.length);
            for (int i = 0; i < actual.length; i++) {
                assertEquals(expected[i], actual[i]);
            }
        } catch (AssertionFailedError error) {
            fail(getComparisonMessage(expected, actual)); 
        }
        
        
    }

    private String getComparisonMessage(byte[] expected, byte[] actual) {
        try {
            return "Expected\n" + new String(expected, "UTF-8") + "\n\n but was \n\n" + new String(actual, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "Broken VM";
        }
    }
    
 
}
