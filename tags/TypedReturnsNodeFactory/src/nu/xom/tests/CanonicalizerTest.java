// Copyright 2002, 2003 Elliotte Rusty Harold
// 
// This library is free software; you can redistribute 
// it and/or modify it under the terms of version 2.1 of 
// the GNU Lesser General Public License as published by  
// the Free Software Foundation.
// 
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
// GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General 
// Public License along with this library; if not, write to the 
// Free Software Foundation, Inc., 59 Temple Place, Suite 330, 
// Boston, MA  02111-1307  USA
// 
// You can contact Elliotte Rusty Harold by sending e-mail to
// elharo@metalab.unc.edu. Please include the word "XOM" in the
// subject line. The XOM home page is temporarily located at
// http://www.cafeconleche.org/XOM/  but will eventually move
// to http://www.xom.nu/

package nu.xom.tests;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;

import com.ibm.icu.text.Normalizer;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.XMLException;
import nu.xom.canonical.Canonicalizer;

/**
 * <p>
 *  Tests canonicalization.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0d23
 *
 */
public class CanonicalizerTest extends XOMTestCase {

    public CanonicalizerTest(String name) {
        super(name);
    }

    private Builder builder;
    
    protected void setUp() {        
        builder = new Builder();       
    }

    public void testWithComments() throws ParsingException, IOException {
      
        File tests = new File("data/canonical/input/");
        String[] inputs = tests.list(new XMLFilter());
        for (int i = 0; i < inputs.length; i++) {
            File input = new File(tests, inputs[i]);   
            Document doc = builder.build(input);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Canonicalizer serializer = new Canonicalizer(out);
            serializer.write(doc);
            
            byte[] actual = out.toByteArray();
            
            // for debugging
            /* File debug = new File(
              "data/canonical/debug/" + input.getName() + ".dbg");
            OutputStream fout = new FileOutputStream(debug);
            fout.write(actual);
            fout.flush();
            fout.close();   */   
            
            File expected = new File(
              "data/canonical/output/" + input.getName() + ".out");
            assertEquals(
              input.getName(), expected.length(), actual.length);
            byte[] expectedBytes = new byte[actual.length];
            InputStream fin = new FileInputStream(expected);
            DataInputStream in = new DataInputStream(fin);
            in.readFully(expectedBytes);
            for (int j = 0; j < expectedBytes.length; j++) {
                assertEquals(expectedBytes[i], actual[i]);   
            }
            
        }
      
        
    }
    
    public void testWithoutComments() 
      throws ParsingException, IOException {
      
        File tests = new File("data/canonical/input/");
        String[] inputs = tests.list(new XMLFilter());
        for (int i = 0; i < inputs.length; i++) {
            File input = new File(tests, inputs[i]); 
            Document doc = builder.build(input);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Canonicalizer serializer 
              = new Canonicalizer(out, false);
            serializer.write(doc);
            
            byte[] actual = out.toByteArray();
            
            // for debugging
            /* File debug = new File("data/canonical/debug/" 
             + input.getName() + ".dbg");
            OutputStream fout = new FileOutputStream(debug);
            fout.write(actual);
            fout.close(); */
            
            File expected = new File("data/canonical/wocommentsoutput/" 
              + input.getName() + ".out");
            byte[] expectedBytes = new byte[actual.length];
            InputStream fin = new FileInputStream(expected);
            DataInputStream in =  new DataInputStream(fin);
            in.readFully(expectedBytes);
            for (int j = 0; j < expectedBytes.length; j++) {
                assertEquals(expectedBytes[i], actual[i]);   
            }
            
        }
        
    }    
    
    public void testRelativeNamespaceURIsForbidden() 
      throws ParsingException, IOException {
        
        try {
            String data = "<test xmlns=\"relative\">data</test>";
            Document doc = builder.build(data, "http://www.ex.org/");
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Canonicalizer serializer
              = new Canonicalizer(out, false);
            serializer.write(doc);
            fail("Canonicalized document with relative namespace URI");
        }
        catch (XMLException ex) {
            // success
            assertNotNull(ex.getMessage());
        }    
        
    }
    
/*    public void testNodeList() 
      throws ParsingException, IOException {
        
        Element element = new Element("test");
        Nodes nodes = new Nodes();
        nodes.append(element);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Canonicalizer serializer 
          = new Canonicalizer(out, false);
        serializer.write(nodes);
        serializer.flush(); 
        byte[] data = out.toByteArray();
        String result = new String(data, "UTF-8");
        assertEquals("<test></test>", result);  
        
    }
    
    public void testNodeListNamespace() 
      throws ParsingException, IOException {
        
        Element parent = new Element("parent");
        parent.addNamespaceDeclaration(
          "pre", "http://www.example.com/");
        Element element = new Element("test");
        parent.appendChild(element);
        Nodes nodes = new Nodes();
        nodes.append(element);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Canonicalizer serializer 
          = new Canonicalizer(out, false);
        serializer.write(nodes);
        serializer.flush(); 
        byte[] data = out.toByteArray();
        String result = new String(data, "UTF-8");
        assertEquals(
          "<test xmlns:pre=\"http://www.example.com/\"></test>", 
          result
        );  
        
    }
    
    public void testNodeListXMLAttributes() 
      throws ParsingException, IOException {
        
        Element grandparent = new Element("grandparent");
        grandparent.addAttribute(new Attribute("xml:base", 
          "http://www.w3.org/XML/1998/namespace", 
          "http://www.example.com/"));
        grandparent.addAttribute(new Attribute("testing", 
          "This should not appear in the output"));
        Element parent = new Element("parent");
        grandparent.appendChild(parent);
        Document doc = new Document(grandparent);
        parent.addAttribute(new Attribute("xml:space", 
          "http://www.w3.org/XML/1998/namespace", "preserve"));
        Element element = new Element("test");
        parent.appendChild(element);
        Nodes nodes = new Nodes();
        nodes.append(element);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Canonicalizer serializer 
          = new Canonicalizer(out, false);
        serializer.write(nodes);
        serializer.flush(); 
        byte[] data = out.toByteArray();
        String result = new String(data, "UTF-8");
        assertEquals(
          "<test xml:base=\"http://www.example.com/\" xml:space=\"preserve\"></test>",
          result);  
        
    } */
    
    private static class XMLFilter implements FilenameFilter {
                
        public boolean accept(File directory, String name) {
            if (name.endsWith(".xml")) return true;
            return false;           
        }
        
    }
    
    public void testNFCFromISO88591() 
      throws ParsingException, IOException {
        isoNormalizationTest("ISO-8859-1");
    }
    
    public void testNFCFromISO88592() 
      throws ParsingException, IOException {
        isoNormalizationTest("ISO-8859-2");
    }
    
    public void testNFCFromISO88593() 
      throws ParsingException, IOException {
        isoNormalizationTest("ISO-8859-3");
    }
    
    public void testNFCFromISO88594() 
      throws ParsingException, IOException {
        isoNormalizationTest("ISO-8859-4");
    }
    
    public void testNFCFromISO88595() 
      throws ParsingException, IOException {
        isoNormalizationTest("ISO-8859-5");
    }
    
    public void testNFCFromISO88596() 
      throws ParsingException, IOException {
        isoNormalizationTest("ISO-8859-6");
    }
    
    public void testNFCFromISO88597() 
      throws ParsingException, IOException {
        isoNormalizationTest("ISO-8859-7");
    }
    
    public void testNFCFromISO88598() 
      throws ParsingException, IOException {
        isoNormalizationTest("ISO-8859-8");
    }
    
    public void testNFCFromISO88599() 
      throws ParsingException, IOException {
        isoNormalizationTest("ISO-8859-9");
    }
    
    public void testNFCFromISO885913() 
      throws ParsingException, IOException {
        isoNormalizationTest("ISO-8859-13");
    }

    public void testNFCFromISO885915() 
      throws ParsingException, IOException {
        isoNormalizationTest("ISO-8859-15");
    }
    
    // 14 and 16 aren't tested because Java doesn't support them yet
    
    private void isoNormalizationTest(String encoding)
      throws ParsingException, IOException {
        String prolog = "<?xml version='1.0' encoding='" 
          + encoding + "'?>\r\n<root>";
        byte[] prologData = prolog.getBytes(encoding);
        String epilog = "</root>";
        byte[] epilogData = epilog.getBytes(encoding);      
        byte[] data = new byte[prologData.length + epilogData.length + 255 - 160 + 1];
        System.arraycopy(prologData, 0, data, 0, prologData.length);
        System.arraycopy(epilogData, 0, data, 
          data.length - epilogData.length, epilogData.length);
        for (int i = 160; i <= 255; i++) {
            data[prologData.length + (i-160)] = (byte) i;   
        }
        InputStream in = new ByteArrayInputStream(data);
        Document doc = builder.build(in);
        String rawResult = doc.getValue();
        String normalizedResult = Normalizer.normalize(rawResult, Normalizer.NFC);
        assertEquals("Parser doesn't use NFC when converting from " + encoding, 
          normalizedResult, rawResult);
    }

    public void testEBCDIC()
      throws ParsingException, IOException {
          
        String encoding = "IBM037";
        String prolog = "<?xml version='1.0' encoding='" 
          + encoding + "'?>\r\n<root>";
        byte[] prologData = prolog.getBytes(encoding);
        String epilog = "</root>";
        byte[] epilogData = epilog.getBytes(encoding);      
        byte[] data = new byte[prologData.length + epilogData.length + 255 - 160 + 1];
        System.arraycopy(prologData, 0, data, 0, prologData.length);
        System.arraycopy(epilogData, 0, data, 
          data.length - epilogData.length, epilogData.length);
        StringBuffer buffer = new StringBuffer(255 - 160 + 1);
        for (int i = 160; i <= 255; i++) {
            buffer.append((char) i);   
        }
        byte[] temp = buffer.toString().getBytes(encoding);
        System.arraycopy(temp, 0, data, prologData.length, temp.length);        
        InputStream in = new ByteArrayInputStream(data);
        Document doc = builder.build(in);
        String rawResult = doc.getValue();
        String normalizedResult = Normalizer.normalize(rawResult, Normalizer.NFC);
        assertEquals("Parser doesn't use NFC when converting from " + encoding, 
          normalizedResult, rawResult);
    }

}