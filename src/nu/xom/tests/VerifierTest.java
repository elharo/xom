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

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.IllegalNameException;
import nu.xom.MalformedURIException;

import org.apache.xerces.util.XMLChar;

import com.ibm.icu.text.UTF16;

/**
 * <p>
 *  Tests to make sure name and character rules are enforced.
 *  The rules are tested by comparison with the rules in
 *  the org.apache.xerces.util.XMLChar class.
 *  This is an undocumented class so this is potentially dangerous
 *  in the long run. it also means the tests depend on Xerces 2
 *  specifically. However, this dependence does not extend into the 
 *  core API.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0d23
 *
 */
public class VerifierTest extends XOMTestCase {

    public VerifierTest(String name) {
        super(name);
    }

    
    public void testElementNames() {
        
        for (char c = 0; c < 65535; c++) {

            if (XMLChar.isNCNameStart(c)) {
               String name = String.valueOf(c);
               Element e = new Element(name);   
               assertEquals(name, e.getLocalName());                               
            }
            else {
               try {
                   new Element(String.valueOf(c));  
                   fail("Allowed illegal name start character " 
                     + Integer.toHexString(c) + " in element name"); 
               } 
               catch (IllegalNameException success) {
                   assertNotNull(success.getMessage());
               }                
            }
            
            if (XMLChar.isNCName(c)) {
               String name = "a" + c;
               Element e = new Element(name);   
               assertEquals(name, e.getLocalName());               
            }
            else {
               try {
                   new Element(String.valueOf(c));  
                   fail("Allowed illegal character " 
                     + Integer.toHexString(c) + " in element name"); 
               } 
               catch (IllegalNameException success) {
                   assertNotNull(success.getMessage());
               }
            }
            
        }
        
    }
    
    // From IRI draft:
    /* ucschar = %xA0-D7FF / %xF900-FDCF / %xFDF0-FFEF /
           / %x10000-1FFFD / %x20000-2FFFD / %x30000-3FFFD
           / %x40000-4FFFD / %x50000-5FFFD / %x60000-6FFFD
           / %x70000-7FFFD / %x80000-8FFFD / %x90000-9FFFD
           / %xA0000-AFFFD / %xB0000-BFFFD / %xC0000-CFFFD
           / %xD0000-DFFFD / %xE1000-EFFFD  */
    
    public void testLegalIRIs() {
        
        int[] legalChars = {0xA0, 0xD7FF, 0xF900, 0xFDCF, 0xFDF0, 
            0xFFEF, 0x10000, 0x1FFFD, 0x20000, 0x2FFFD, 0x30000, 
            0x3FFFD, 0x40000, 0x4FFFD, 0x50000, 0x5FFFD, 0x60000, 
            0x6FFFD, 0x70000, 0x7FFFD, 0x80000, 0x8FFFD, 0x90000, 
            0x9FFFD, 0xA0000, 0xAFFFD, 0xB0000, 0xBFFFD, 0xC0000, 
            0xCFFFD, 0xD0000, 0xDFFFD, 0xE1000, 0xEFFFD, 0xCFFFD};
        
        Element element = new Element("test");
        for (int i = 0; i < legalChars.length; i++) {
            String utf16 = convertToUTF16(legalChars[i]);
            String url = "http://www.example.com/" + utf16 + ".xml";
            element.addAttribute(new Attribute("xml:base", "http://www.w3.org/XML/1998/namespace", url));
            assertEquals(url, element.getAttributeValue("base", "http://www.w3.org/XML/1998/namespace"));
        }    
        
    }

    
    public void testIllegalIRIs() {
        
        int[] illegalChars = {0x00, 0xDC00, 0xE7FF, 0xF899, 0xD800, 
            0xFDE0, 0xFFFF, 0x1FFFE,  0x2FFFE, 0x3FFFF, 0x4FFFE, 
            0x4FFFF, 0x5FFFE, 0x6FFFF, 0x7FFFE, 0x8FFFF, 0x9FFFE,
            0xAFFFE, 0xBFFFF, 0xCFFFE, 0xDFFFE, 0xEFFFF, 0xFDDF};
        
        Element element = new Element("test");
        for (int i = 0; i < illegalChars.length; i++) {
            String utf16 = convertToUTF16(illegalChars[i]);
            String url = "http://www.example.com/" + utf16 + ".xml";
            try {
                element.addAttribute(new Attribute("xml:base", "http://www.w3.org/XML/1998/namespace", url));
                fail("Allowed IRI containing 0x" + 
                  Integer.toHexString(illegalChars[i]).toUpperCase());
            }
            catch (MalformedURIException success) {
                assertNotNull(success.getMessage());
            }
        }    
        
    }

    
    private static String convertToUTF16(int c) {
        if (c <= 0xFFFF) return "" + (char) c;
        char high = UTF16.getLeadSurrogate(c);
        char low = UTF16.getTrailSurrogate(c);
        StringBuffer sb = new StringBuffer(2);
        sb.append(high);
        sb.append(low);
        return sb.toString().toLowerCase();
    }
    

}
