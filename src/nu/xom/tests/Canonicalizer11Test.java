/* Copyright 2011 Elliotte Rusty Harold
   
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
   subject line. The XOM home page is located at http://www.xom.nu/
*/

package nu.xom.tests;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

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
 * @version 1.2.7
 *
 */
public class Canonicalizer11Test extends TestCase {

    
    private File canonical;
    private ByteArrayOutputStream out = new ByteArrayOutputStream();
    private Canonicalizer canonicalizer = new Canonicalizer(out, Canonicalizer.CANONICAL_XML_11);
   
    public Canonicalizer11Test(String name) {
        super(name);
    }

    
    private Builder builder = new Builder(); 
    
    
    protected void setUp() { 
        File data = new File("data");
        canonical = new File(data, "c14n11");
    }
    
    // 3.2.1.1 Test case c14n11/xmllang-1
    public void testXMLLang() throws ParsingException, IOException {
        File input = new File(canonical, "xmllang-input.xml");
        File expected = new File(canonical, "xmllang-1.output");
        
        Document doc = builder.build(input);
        XPathContext namespaces = new XPathContext();
        namespaces.addNamespace("ietf", "http://www.ietf.org");
        String documentSubsetExpression = "(//. | //@* | //namespace::*)[ancestor-or-self::ietf:e1]";
        canonicalizer.write(doc.query(documentSubsetExpression, namespaces));  
        
        byte[] actualBytes = out.toByteArray();        
        byte[] expectedBytes = new byte[(int) expected.length()];
        InputStream fin = new FileInputStream(expected);
        DataInputStream in = new DataInputStream(fin);
        try {
            in.readFully(expectedBytes);
        }
        finally {
            in.close();
        }
        assertEquals(expectedBytes, actualBytes);
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
        assertEquals(expected.length, actual.length);
        for (int i = 0; i < actual.length; i++) {
            assertEquals(expected[i], actual[i]);
        }
        
    }
    
    
    
}
