/* Copyright 2002-2004 Elliotte Rusty Harold
   
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
   elharo@metalab.unc.edu. Please include the word "XOM" in the
   subject line. The XOM home page is located at http://www.xom.nu/
*/

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

import com.ibm.icu.text.UTF16;

/**
 * <p>
 *   Check serialization of almost all of Unicode
 *   in a variety of encodings.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0b7
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
        // strings encode them as surrogate pairs. We'll test with
        // the characters from 1D100 to 1D1FF (the musical symbols)
        for (int i = 0; i < 256; i++) {
            int u = 0x1D100 + i;
            // algorithm from RFC 2781
            /* int uprime = u - 0x10000;
            int W1 = 0xD800;
            int W2 = 0xDC00;
            W2 = W2 | (uprime & 0x7FF );
            W1 = W1 | (uprime & 0xFF800); */
            Element data = new Element("d");
            // data.appendChild( String.valueOf(((char) W1)) + ((char) W2) );
            String s = UTF16.valueOf(u);
            data.appendChild( s );
            data.addAttribute(new Attribute("c", String.valueOf(u)));
            // data.addAttribute(new Attribute("c", String.valueOf(W1)));
            root.appendChild(data);
        }        
        
    }
    
    
    protected void tearDown() {
      doc = null;
      System.gc();   
    } 
    
    
    public void testEUCJP() throws ParsingException, IOException {
        checkAll("EUC-JP");
    } 

    
    public void testShift_JIS() throws ParsingException, IOException {
        checkAll("Shift_JIS");
    } 


    public void testISO2022JP() throws ParsingException, IOException {
        checkAll("ISO-2022-JP");
    } 


    public void testGeneric() throws ParsingException, IOException {
        checkAll("Cp1252");
    }
    

    // Main purpose here is to test a character set whose name is 
    // case dependent
    public void testMacRoman() throws ParsingException, IOException {
        checkAll("MacRoman");
    }
    

    public void testBig5() throws ParsingException, IOException {
        checkAll("Big5");
    } 

    public void testUSASCII() throws ParsingException, IOException {
        checkAll("US-ASCII");
    }
    
    public void testASCII() throws ParsingException, IOException {
        checkAll("ASCII");
    }

    public void testLatin1() throws ParsingException, IOException {       
        checkAll("ISO-8859-1");        
    }

    public void testLatin2() throws ParsingException, IOException {
        checkAll("ISO-8859-2");
    }
    
    public void testLatin3() throws ParsingException, IOException {
        checkAll("ISO-8859-3");
    }
    
    public void testLatin4() throws ParsingException, IOException {
        checkAll("ISO-8859-4");
    }
    
    public void testCyrillic() throws ParsingException, IOException {
        checkAll("ISO-8859-5");
    }
    
    public void testArabic() throws ParsingException, IOException {
        checkAll("ISO-8859-6");
    }
    
    public void testGreek() throws ParsingException, IOException {
        checkAll("ISO-8859-7");
    }
    
    public void testThai() throws ParsingException, IOException {
        checkAll("TIS-620");
    }
    
    public void testHebrew() throws ParsingException, IOException {
        checkAll("ISO-8859-8");
    }
    
    public void testLatin5() throws ParsingException, IOException {
        checkAll("ISO-8859-9");
    }

    public void testUTF8() throws ParsingException, IOException {
        checkAll("UTF-8");
    }
    
    public void testUTF16() throws ParsingException, IOException {
        checkAll("UTF-16");
    } 

    public void testUCS2() throws ParsingException, IOException {
        checkAll("ISO-10646-UCS-2");
    }
    
    public void testEBCDIC() throws ParsingException, IOException {
        checkAll("Cp037");
    }
    
    // These encodings are only available after Java 1.3
    private static boolean java14OrLater = false;
    
    static {
        String version = System.getProperty("java.version");
        String majorVersion = version.substring(0, 3);
        double versionNumber = Double.parseDouble(majorVersion);
        if (versionNumber >= 1.4) java14OrLater = true; 
    }   
    
    public void testLatin7() throws ParsingException, IOException {
        if (java14OrLater) checkAll("ISO-8859-13");
    }
    
    public void testLatin9() throws ParsingException, IOException {
        if (java14OrLater) checkAll("ISO-8859-15");
    } 

    public void testGB18030() throws ParsingException, IOException {
        if (java14OrLater) checkAll("GB18030");
    } 

    // These encodings are not installed in all distributions by 
    // default. They are only found currently in IBM's Java 1.4.1 VM. 
    // They don't seem to be supported in the 1.5 alpha
    // either.    
    public void testUCS4() throws ParsingException, IOException {
        if (charsetAvailable("ISO-10646-UCS-4")) checkAll("ISO-10646-UCS-4");
    } 

    public void testLatin6() throws ParsingException, IOException {
        if (charsetAvailable("ISO-8859-10")) checkAll("ISO-8859-10");
    } 

    public void testLatin8() throws ParsingException, IOException {
        if (charsetAvailable("ISO-8859-14")) checkAll("ISO-8859-14");
    }

    public void testLatin10() throws ParsingException, IOException {
        if (charsetAvailable("ISO-8859-16")) checkAll("ISO-8859-16");
    }     
        
    
    // Test that with an encoding XOM does not specifically support
    // but the VM does, everything still works.
    public void testUnsupportedEncoding() 
      throws ParsingException, IOException {
        checkAll("Cp1252");
    } 
    

    private static boolean charsetAvailable(String name) {
        // hack to avoid using 1.4 classes
        try {
            "d".getBytes(name);
            return true;
        }
        catch (UnsupportedEncodingException ex) {
            return false;   
        }        
        
    }
       
    
    private void checkAll(String encoding) 
      throws ParsingException, IOException {
        
        Builder builder = new Builder();
        byte[] data = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream(100000);    
        // Write data into a byte array using encoding
        Serializer serializer = new Serializer(out, encoding);
        serializer.write(doc);
        serializer.flush();
        out.flush();
        out.close();
        data = out.toByteArray();
        InputStream in = new ByteArrayInputStream(data);
        Document reparsed = builder.build(in);
        in.close();
        serializer = null;
        
        Element reparsedRoot = reparsed.getRootElement();
        int childCount = reparsedRoot.getChildCount();
        for (int i = 0; i < childCount; i++) {
            Element test = (Element) reparsedRoot.getChild(i); 
            String value = test.getValue();
            int expected 
              = Integer.parseInt(test.getAttributeValue("c"));
            // workaround for EBCDIC bugs
            if (expected == 133 && encoding.equalsIgnoreCase("Cp037")) {
                continue;
            }
            int actual = value.charAt(0);
            if (value.length() > 1) {
                actual = UTF16.charAt(value, 0);
            }
            // This doesn't work for all encodings, because there are
            // a few cases where you write a Unicode compatibility 
            // character such as an Arabic presentation form,
            // but read back what is essentially a different version 
            // of the same character. That is the mapping from some
            // legacy character sets to Unicode is not always 1-1.
            assertEquals("Expected 0x" 
              + Integer.toHexString(expected).toUpperCase()
              + " but was 0x" 
              + Integer.toHexString(actual).toUpperCase(), expected, actual);
        } 
        
        in = null;
            
    }

    
    private void checkSome(String encoding) 
      throws ParsingException, IOException {
        
        Builder builder = new Builder();
        byte[] data = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream(100000);    
        // Write data into a byte array using encoding
        Serializer serializer = new Serializer(out, encoding);
        serializer.write(doc);
        serializer.flush();
        out.flush();
        out.close();
        data = out.toByteArray();
        InputStream in = new ByteArrayInputStream(data);
        Document reparsed = builder.build(in);
        in.close();
        serializer = null;
        
        Element reparsedRoot = reparsed.getRootElement();
        int childCount = reparsedRoot.getChildCount();
        for (int i = 0; i < childCount; i++) {
            Element test = (Element) reparsedRoot.getChild(i); 
            String value = test.getValue();
            int expected 
              = Integer.parseInt(test.getAttributeValue("c"));
            // workaround for EBCDIC bugs
            if (expected == 133 && encoding.equalsIgnoreCase("Cp037")) {
                continue;
            }
            int actual = value.charAt(0);
            if (value.length() > 1) {
                actual = UTF16.charAt(value, 0);
            }
            if (expected != actual) System.err.println(expected);
        } 
        
        in = null;
            
    }
    
    
}
