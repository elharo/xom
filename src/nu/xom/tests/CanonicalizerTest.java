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

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.Serializer;
import nu.xom.XMLException;
import nu.xom.canonical.CanonicalXMLSerializer;

/**
 * <p>
 *  Tests canonicalization.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0d22
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
            Serializer serializer = new CanonicalXMLSerializer(out);
            serializer.write(doc);
            serializer.flush();
            
            byte[] actual = out.toByteArray();
            
            // for debugging
            File debug = new File(
              "data/canonical/debug/" + input.getName() + ".dbg");
            OutputStream fout = new FileOutputStream(debug);
            fout.write(actual);
            fout.flush();
            fout.close();      
            
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
            Serializer serializer 
              = new CanonicalXMLSerializer(out, false);
            serializer.write(doc);
            serializer.flush();
            
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
            Serializer serializer
              = new CanonicalXMLSerializer(out, false);
            serializer.write(doc);
            fail("Canonicalized document with relative namespace URI");
        }
        catch (XMLException ex) {
            // success   
        }    
        
    }
    
/*    public void testNodeList() 
      throws ParsingException, IOException {
        
        Element element = new Element("test");
        Nodes nodes = new Nodes();
        nodes.append(element);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        CanonicalXMLSerializer serializer 
          = new CanonicalXMLSerializer(out, false);
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
        CanonicalXMLSerializer serializer 
          = new CanonicalXMLSerializer(out, false);
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
        CanonicalXMLSerializer serializer 
          = new CanonicalXMLSerializer(out, false);
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

}
