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
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.ParsingException;
import nu.xom.Serializer;

/**
 * <p>
 *   Check serialization of almost all of Unicode
 *   in a variety of encodings.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0d23
 *
 */
public class EncodingTest extends XOMTestCase {

    public EncodingTest(String name) {
        super(name);
    }

    private Document doc;

    protected void setUp() {
        Element root = new Element("root");
        doc = new Document(root);           

        for (int i = 0x20; i <= 0xD7FF; i++) {
            Element data = new Element("d");
            data.appendChild(String.valueOf(((char) i)));
            data.addAttribute(new Attribute("c", String.valueOf(i)));
            root.appendChild(data);
        }
        
        // skip surrogates between 0xD800 and 0xDFFF
        
        for (int i = 0xE000; i <= 0xFFFD; i++) {
            Element data = new Element("d");
            data.appendChild(String.valueOf(((char) i)));
            data.addAttribute(new Attribute("c", String.valueOf(i)));
            root.appendChild(data);
        }

        // Test Plane-1 characters. These are tricky because Java 
        // strings  encode them as surrogate pairs. We'll test with
        // the characters from 1D100 to 1D1FF (the musical symbols)
        /* ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(bout);
        try {
            for (int i = 0; i < 256; i++) {
                // algorithm from RFC 2781
                int u = 0x1D100 + i;
                int uprime = u - 0x10000;
                int W1 = 0xD800;
                int W2 = 0xDC00;
                W2 = W2 | (uprime & 0x7FF );
                W1 = W1 | (uprime & 0xFF800);
                out.writeShort(W1);
                out.writeShort(W2);
            }
            out.flush();
            byte[] music = bout.toByteArray();
            InputStream in = new ByteArrayInputStream(music);
            Reader r = new InputStreamReader(in, "UTF-16");
            for (int i = 0; i < 256; i++) {
                int c1 = r.read();
                int c2 = r.read();
                Element data = new Element("d");
                data.appendChild( ((char) c1) + "" + ((char) c2) );
                data.addAttribute(new Attribute("c", String.valueOf(c1)));
                root.appendChild(data);
            }
            
        }
        catch (IOException ex) {
            // shouldn't happen on a byte array 
            ex.printStackTrace();
            throw new RuntimeException("Ooops in setup"); 
        } */
        for (int i = 0; i < 256; i++) {
            // algorithm from RFC 2781
            int u = 0x1D100 + i;
            int uprime = u - 0x10000;
            int W1 = 0xD800;
            int W2 = 0xDC00;
            W2 = W2 | (uprime & 0x7FF );
            W1 = W1 | (uprime & 0xFF800);
            Element data = new Element("d");
            data.appendChild( String.valueOf(((char) W1)) + ((char) W2) );
            data.addAttribute(new Attribute("c", String.valueOf(W1)));
            root.appendChild(data);
        }        
        
    }
    
    protected void tearDown() {
      doc = null;
      System.gc();   
    } 
    
    public void testUSASCII() 
      throws ParsingException, UnsupportedEncodingException {
        checkAll("US-ASCII");
    }
    
    public void testASCII() 
      throws ParsingException, UnsupportedEncodingException {
        checkAll("ASCII");
    }

    public void testLatin1() 
      throws ParsingException, UnsupportedEncodingException {       
        checkAll("ISO-8859-1");        
    }

    public void testLatin2() 
      throws ParsingException, UnsupportedEncodingException {
        checkAll("ISO-8859-2");
    }
    
    public void testLatin3() 
      throws ParsingException, UnsupportedEncodingException {
        checkAll("ISO-8859-3");
    }
    
    public void testLatin4() 
      throws ParsingException, UnsupportedEncodingException {
        checkAll("ISO-8859-4");
    }
    
    public void testCyrillic() 
      throws ParsingException, UnsupportedEncodingException {
        checkAll("ISO-8859-5");
    }
    
    public void testArabic() 
      throws ParsingException, UnsupportedEncodingException {
        checkAll("ISO-8859-6");
    }
    
    public void testGreek() 
      throws ParsingException, UnsupportedEncodingException {
        checkAll("ISO-8859-7");
    }
    
    public void testHebrew() 
      throws ParsingException, UnsupportedEncodingException {
        checkAll("ISO-8859-8");
    }
    
    public void testLatin5()  
      throws ParsingException, UnsupportedEncodingException {
        checkAll("ISO-8859-9");
    }

    public void testUTF8() 
      throws ParsingException, UnsupportedEncodingException {
        checkAll("UTF-8");
    }
    
    public void testUTF16()  
      throws ParsingException, UnsupportedEncodingException {
        checkAll("UTF-16");
    } 

    public void testUCS2() 
      throws ParsingException, UnsupportedEncodingException {
        checkAll("ISO-10646-UCS-2");
    } 

    // This test fails to some as yet unworked around input bugs
    // in Java's handling of NEL with Cp037 encoding
    /* public void testEBCDIC037() 
      throws ParsingException, UnsupportedEncodingException {
        checkAll("CP037");
    } */

    // These encodings are only available after Java 1.3
    private static boolean java14OrLater = false;
    
    static {
        String version = System.getProperty("java.version");
        String majorVersion = version.substring(0, 3);
        double versionNumber = Double.parseDouble(majorVersion);
        if (versionNumber >= 1.4) java14OrLater = true; 
    }   
    
    public void testLatin7()  
      throws ParsingException, UnsupportedEncodingException {
        if (java14OrLater) checkAll("ISO-8859-13");
    }
    
    public void testLatin9()  
      throws ParsingException, UnsupportedEncodingException {
        if (java14OrLater) checkAll("ISO-8859-15");
    } 
        
    private static boolean extendedCharsetsAvailable = false;
    
    static {
        // hack to avoid using 1.4 classes
        try {
            "data".getBytes("ISO-8859-10");
            extendedCharsetsAvailable = true;
        }
        catch (UnsupportedEncodingException ex) {
            extendedCharsetsAvailable = false;   
        }
        
    }
       
    // These encodings are not installed in all distributions by default 
    public void testLatin6()  
      throws ParsingException, UnsupportedEncodingException {
        if (extendedCharsetsAvailable) checkAll("ISO-8859-10");
    } 

    public void testLatin8()  
      throws ParsingException, UnsupportedEncodingException {
        if (extendedCharsetsAvailable) checkAll("ISO-8859-14");
    }



// Java 1.4 does not yet support these encodings
/*  public void testUCS4() 
      throws ParsingException, UnsupportedEncodingException {
        checkAll("ISO-10646-UCS-4");
    } 

    public void testLatin10()  
      throws ParsingException, UnsupportedEncodingException {
        checkAll("ISO-8859-16");
    } */

    
    private void checkAll(String encoding) 
      throws ParsingException, UnsupportedEncodingException {
        
        Builder builder = new Builder();
        byte[] data = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream(100000);    
        try {
            // Write data into a byte array using encoding
            Serializer serializer = new Serializer(out, encoding);
            serializer.write(doc);
            serializer.flush();
            out.flush();
            out.close();
            data = out.toByteArray();
            InputStream in = new ByteArrayInputStream(data);
            Document reparsed = builder.build(in);
            serializer = null;
            
            Element reparsedRoot = reparsed.getRootElement();
            int childCount = reparsedRoot.getChildCount();
            for (int i = 0; i < childCount; i++) {
                Element test = (Element) reparsedRoot.getChild(i); 
                String value = test.getValue();
                int expected 
                 = Integer.parseInt(test.getAttributeValue("c"));
                int actual = value.charAt(0);
                assertEquals(expected, actual);
            } 
            
            in = null;
            
        }
        catch (UnsupportedEncodingException ex) {
            throw ex;   
        }  
        catch (IOException ex) {
            ex.printStackTrace();   
        } 
            
    }

}
