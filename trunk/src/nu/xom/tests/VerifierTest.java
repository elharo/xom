// Copyright 2002-2004 Elliotte Rusty Harold
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
import nu.xom.DocType;
import nu.xom.Element;
import nu.xom.IllegalDataException;
import nu.xom.IllegalNameException;
import nu.xom.MalformedURIException;
import nu.xom.Text;

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
 * @version 1.0b6
 *
 */
public class VerifierTest extends XOMTestCase {
 
    private final static char[] subdelims = {'!', '$', '&', '\'', '(', ')' , '*', '+', ',', ';', '='};
    private final static char[] unreserved = {'-', '.', '_', '~'};
    private final static char[] unwise = {'{', '}', '|', '\\', '^', '[', ']', '`'};
    private final static char[] delims = {'<', '>', '#', '%', '^', '"'};
    
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
                   assertEquals(String.valueOf(c), success.getData());
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
  
    // From RFC 2396 reallowed into IRIs
    //   "{" | "}" | "|" | "\" | "^" | "[" | "]" | "`"
    public void testLegalIRIs() {
        
        int[] legalChars = {
          '{', '}', '<', '>', '"', '|', '\\', '^', '`', '\u007F', 
          0xA0, 0xD7FF, 0xF900, 0xFDCF, 0xFDF0, 
          0xFFEF, 0x10000, 0x1FFFD, 0x20000, 0x2FFFD, 0x30000, 
          0x3FFFD, 0x40000, 0x4FFFD, 0x50000, 0x5FFFD, 0x60000, 
          0x6FFFD, 0x70000, 0x7FFFD, 0x80000, 0x8FFFD, 0x90000, 
          0x9FFFD, 0xA0000, 0xAFFFD, 0xB0000, 0xBFFFD, 0xC0000, 
          0xCFFFD, 0xD0000, 0xDFFFD, 0xE1000, 0xEFFFD, 0xCFFFD};
        
        Element element = new Element("test");
        for (int i = 0; i < legalChars.length; i++) {
            String utf16 = convertToUTF16(legalChars[i]);
            String url = "http://www.example.com/" + utf16 + ".xml";
            element.addAttribute(new Attribute("xml:base", 
              "http://www.w3.org/XML/1998/namespace", url));
            assertEquals(url, element.getAttributeValue("base", 
              "http://www.w3.org/XML/1998/namespace"));
        }    
        
    }
    
    
    public void testAllASCIILettersAllowedToBeginSchemeNames() {
        
        Element e = new Element("e");
        
        for (char c = 'A'; c <= 'Z'; c++) {
            String uri = c + "scheme:schemeSpecificData";
            e.setNamespaceURI(uri);
            assertEquals(uri, e.getNamespaceURI());
        }
        
        for (char c = 'a'; c <= 'z'; c++) {
            String uri = c + "scheme:schemeSpecificData";
            e.setNamespaceURI(uri);
            assertEquals(uri, e.getNamespaceURI());
        }      
        
    }

    
    public void testAllASCIILettersAllowedInSchemeNames() {
        
        Element e = new Element("e");
        
        for (char c = 'A'; c <= 'Z'; c++) {
            String uri = "scheme" + c + ":schemeSpecificData";
            e.setNamespaceURI(uri);
            assertEquals(uri, e.getNamespaceURI());
        }
        
        for (char c = 'a'; c <= 'z'; c++) {
            String uri = "scheme" + c + ":schemeSpecificData";
            e.setNamespaceURI(uri);
            assertEquals(uri, e.getNamespaceURI());
        }      
        
    }
    
    
    public void testAllASCIILettersAllowedInQueryStrings() {
        
        Element e = new Element("e");
        
        for (char c = 'A'; c <= 'Z'; c++) {
            String uri = "http://www.example.com/?name=" + c;
            e.setNamespaceURI(uri);
            assertEquals(uri, e.getNamespaceURI());
        }
        
        for (char c = 'a'; c <= 'z'; c++) {
            String uri = "http://www.example.com/?name=" + c;
            e.setNamespaceURI(uri);
            assertEquals(uri, e.getNamespaceURI());
        }      
        
    }
    
    
    public void testAllASCIIDigitsAllowedInQueryStrings() {
        
        Element e = new Element("e");
        
        for (char c = '0'; c <= '9'; c++) {
            String uri = "http://www.example.com/?value=" + c;
            e.setNamespaceURI(uri);
            assertEquals(uri, e.getNamespaceURI());
        }   
        
    }
    
    
    public void testSlashAllowedInQueryString() {
        
        Element e = new Element("e");
        
        String uri = "http://www.example.com/?path=/home/elharo/docs/";
        e.setNamespaceURI(uri);
        assertEquals(uri, e.getNamespaceURI());
        
    }
    
    
    public void testQuestionMarkAllowedInQueryString() {
        
        Element e = new Element("e");
        
        String uri = "http://www.example.com/?path=?home?elharo?docs?";
        e.setNamespaceURI(uri);
        assertEquals(uri, e.getNamespaceURI());
        
    }
    
    
    public void testColonAllowedInQueryString() {
        
        Element e = new Element("e");
        
        String uri = "http://www.example.com/?path=:home:elharo:docs:";
        e.setNamespaceURI(uri);
        assertEquals(uri, e.getNamespaceURI());
        
    }
    
    
    public void testAtSignAllowedInQueryString() {
        
        Element e = new Element("e");
        
        String uri = "http://www.example.com/?path=@home@elharo@docs@";
        e.setNamespaceURI(uri);
        assertEquals(uri, e.getNamespaceURI());
        
    }
    
    
    public void testNonASCIICharactersNotAllowedInQueryStrings() {
        
        Element e = new Element("e");
        
        for (char c = 128; c <= 1024; c++) {
            String uri = "http://www.example.com/?value=" + c;
            try {
                e.setNamespaceURI(uri);
                fail("Allowed unescaped non-ASCII character " + c + " in query string");
            }
            catch (MalformedURIException success) {
                assertEquals(uri, success.getData());
            }
        }   
        
    }

    
    public void testDelimsNotAllowedInQueryStrings() {
        
        Element e = new Element("e");
        
        for (int i = 0; i < delims.length; i++) {
            String uri = "http://www.example.com/?value=" + delims[i] + "#Must_Use_Fragment_ID";
            try {
                e.setNamespaceURI(uri);
                fail("Allowed delimiter character " + delims[i] + " in query string");
            }
            catch (MalformedURIException success) {
                assertEquals(uri, success.getData());
            }
        }   
        
    }

    
    public void testUnwiseCharactersNotAllowedInQueryStrings() {
        
        Element e = new Element("e");
        
        for (int i = 0; i < unwise.length; i++) {
            String uri = "http://www.example.com/?value=" + unwise[i];
            try {
                e.setNamespaceURI(uri);
                fail("Allowed unwise character " + unwise[i] + " in query string");
            }
            catch (MalformedURIException success) {
                assertEquals(uri, success.getData());
            }
        }   
        
    }

    
    public void testUnwiseCharactersNotAllowedInUserInfo() {
        
        Element e = new Element("e");
        
        for (int i = 0; i < unwise.length; i++) {
            String uri = "http://user" + unwise[i] + "name@www.example.com/?value=" + unwise[i];
            try {
                e.setNamespaceURI(uri);
                fail("Allowed unwise character " + unwise[i] + " in user info");
            }
            catch (MalformedURIException success) {
                assertEquals(uri, success.getData());
            }
        }   
        
    }

    
    public void testUnwiseCharactersNotAllowedInHost() {
        
        Element e = new Element("e");
        
        for (int i = 0; i < unwise.length; i++) {
            String uri = "http://u" + unwise[i] + "www.example.com/";
            try {
                e.setNamespaceURI(uri);
                fail("Allowed unwise character " + unwise[i] + " in host");
            }
            catch (MalformedURIException success) {
                assertEquals(uri, success.getData());
            }
        }   
        
    }

    
    public void testDelimsNotAllowedInHost() {
        
        Element e = new Element("e");
        
        for (int i = 0; i < delims.length; i++) {
            String uri = "http://u" + delims[i] + "www.example.com/#value";
            try {
                e.setNamespaceURI(uri);
                fail("Allowed unwise character " + delims[i] + " in host");
            }
            catch (MalformedURIException success) {
                assertEquals(uri, success.getData());
            }
        }   
        
    }

    
    public void testUnwiseCharactersNotAllowedInPath() {
        
        Element e = new Element("e");
        
        for (int i = 0; i < unwise.length; i++) {
            String uri = "http://www.example.com/path" + unwise[i] + "/path";
            try {
                e.setNamespaceURI(uri);
                fail("Allowed unwise character " + unwise[i] + " in path");
            }
            catch (MalformedURIException success) {
                assertEquals(uri, success.getData());
            }
        }   
        
    }

    
    public void testAllASCIILettersAllowedInHostNames() {
        
        Element e = new Element("e");
        
        for (char c = 'A'; c <= 'Z'; c++) {
            String uri = "http://" + c + ".com/";
            e.setNamespaceURI(uri);
            assertEquals(uri, e.getNamespaceURI());
        }
        
        for (char c = 'a'; c <= 'z'; c++) {
            String uri = "http://" + c + ".com/";
            e.setNamespaceURI(uri);
            assertEquals(uri, e.getNamespaceURI());
        }      
        
    }
    
    
    public void testAllASCIIDigitsAllowedInHostNames() {
        
        Element e = new Element("e");
        
        for (char c = '0'; c <= '9'; c++) {
            String uri = "http://c" + c + ".com/";
            e.setNamespaceURI(uri);
            assertEquals(uri, e.getNamespaceURI());
        }   
        
    }
    
    
    public void testNonASCIICharactersNotAllowedInHostNames() {
        
        Element e = new Element("e");
        
        for (char c = 128; c <= 1024; c++) {
            String uri = "http://c" + c + ".com/";
            try {
                e.setNamespaceURI(uri);
                fail("Allowed unescaped non-ASCII character " + c + " in host name");
            }
            catch (MalformedURIException success) {
                assertEquals(uri, success.getData());
            }
        }   
        
    }

    
    public void testAllASCIILettersAllowedInUserInfo() {
        
        Element e = new Element("e");
        
        for (char c = 'A'; c <= 'Z'; c++) {
            String uri = "http://" + c + "@c.com/";
            e.setNamespaceURI(uri);
            assertEquals(uri, e.getNamespaceURI());
        }
        
        for (char c = 'a'; c <= 'z'; c++) {
            String uri = "http://" + c + "@c.com/";
            e.setNamespaceURI(uri);
            assertEquals(uri, e.getNamespaceURI());
        }      
        
    }
    
    
    public void testAllSubDelimsAllowedInUserInfo() {
        
        Element e = new Element("e");
        
        for (int i = 0; i < subdelims.length; i++) {
            String uri = "http://c" + subdelims[i] + "x@c.com/";
            e.setNamespaceURI(uri);
            assertEquals(uri, e.getNamespaceURI());
        }  
        
    }
    
    
    public void testAllSubDelimsAllowedInPath() {
        
        Element e = new Element("e");
        
        for (int i = 0; i < subdelims.length; i++) {
            String uri = "http://cc.com/path" + subdelims[i] +".html";
            e.setNamespaceURI(uri);
            assertEquals(uri, e.getNamespaceURI());
        }  
        
    }
    
    
    public void testAllUnreservedPunctuationMarksAllowedInUserInfo() {
        
        Element e = new Element("e");
        
        for (int i = 0; i < unreserved.length; i++) {
            String uri = "http://c" + unreserved[i] + "x@c.com/";
            e.setNamespaceURI(uri);
            assertEquals(uri, e.getNamespaceURI());
        }  
        
    }
    
    
    public void testAllUnreservedPunctuationMarksAllowedInHost() {
        
        Element e = new Element("e");
        
        for (int i = 0; i < unreserved.length; i++) {
            String uri = "http://c" + unreserved[i] + "xc.com/";
            e.setNamespaceURI(uri);
            assertEquals(uri, e.getNamespaceURI());
        }  
        
    }
    
    
    public void testAllSubDelimsAllowedInQueryString() {
        
        Element e = new Element("e");
        
        for (int i = 0; i < subdelims.length; i++) {
            String uri = "http://cx@c.com/?name=" + subdelims[i];
            e.setNamespaceURI(uri);
            assertEquals(uri, e.getNamespaceURI());
        }  
        
    }
    
    
    public void testAllSubDelimsAllowedInHost() {
        
        Element e = new Element("e");
        
        for (int i = 0; i < subdelims.length; i++) {
            String uri = "http://cx" + subdelims[i] + "c.com/";
            e.setNamespaceURI(uri);
            assertEquals(uri, e.getNamespaceURI());
        }  
        
    }
    
    
    public void testAllUnreservedPunctuationMarksAllowedInQueryString() {
        
        Element e = new Element("e");
        
        for (int i = 0; i < unreserved.length; i++) {
            String uri = "http://cx@c.com/?name=" + unreserved[i];
            e.setNamespaceURI(uri);
            assertEquals(uri, e.getNamespaceURI());
        }  
        
    }
    
    
    public void testAllASCIIDigitsAllowedInUserInfo() {
        
        Element e = new Element("e");
        
        for (char c = '0'; c <= '9'; c++) {
            String uri = "http://" + c + "@c.com/";
            e.setNamespaceURI(uri);
            assertEquals(uri, e.getNamespaceURI());
        }   
        
    }
    
    
    public void testNonASCIICharactersNotAllowedInUserInfo() {
        
        Element e = new Element("e");
        
        for (char c = 128; c <= 1024; c++) {
            String uri = "http://" + c + "@c.com/";
            try {
                e.setNamespaceURI(uri);
                fail("Allowed unescaped non-ASCII character " + c + " in user info");
            }
            catch (MalformedURIException success) {
                assertEquals(uri, success.getData());
            }
        }   
        
    }

    
    public void testDelimCharactersNotAllowedInUserInfo() {
        
        Element e = new Element("e");
        
        for (int i = 0; i < delims.length; i++) {
            String uri = "http://c" + delims[i] + "c@c.com/?name=value#fragID";
            try {
                e.setNamespaceURI(uri);
                fail("Allowed delim character " + delims[i] + " in user info");
            }
            catch (MalformedURIException success) {
                assertEquals(uri, success.getData());
            }
        }   
        
    }
    
    
    public void testMalformedURI() {
        
        Element e = new Element("e");
    
        String uri = "http://c#c@c.com/?name=value#fragID";
        try {
            e.setNamespaceURI(uri);
            fail("Allowed http://c#c@c.com/?name=value#fragID as URI");
        }
        catch (MalformedURIException success) {
            assertEquals(uri, success.getData());
        }
            
    }

    
    public void testFragmentIDContainsQuestionMark() {
        
        Element e = new Element("e");
    
        String uri = "http://cc@c.com/?name=value#fragID?home/?elharo?";
        e.setNamespaceURI(uri);
        assertEquals(uri, e.getNamespaceURI());
            
        uri = "http://cc@c.com/#fragID?name=value";
        e.setNamespaceURI(uri);
        assertEquals(uri, e.getNamespaceURI());
            
    }

    
    public void testFragmentIDContainsFirstColon() {
        
        Element e = new Element("e");
    
        String uri = "http://c.com/#fragID:home";
        e.setNamespaceURI(uri);
        assertEquals(uri, e.getNamespaceURI());
            
        uri = "http://c.com/#fragID:home@eharo.com/somewhere";
        e.setNamespaceURI(uri);
        assertEquals(uri, e.getNamespaceURI());
            
    }

    
    public void testEmptyHostAllowed() {
        
        Element e = new Element("e");
    
        String uri = "scheme://elharo@:80/data";
        e.setNamespaceURI(uri);
        assertEquals(uri, e.getNamespaceURI());
            
    }

    
    public void testC0ControlsNotAllowedInUserInfo() {
        
        Element e = new Element("e");
        
        for (char c = 0; c <= ' '; c++) {
            String uri = "http://" + c + "@c.com/";
            try {
                e.setNamespaceURI(uri);
                fail("Allowed C0 control 0x" + Integer.toHexString(c) + " in user info");
            }
            catch (MalformedURIException success) {
                assertEquals(uri, success.getData());
            }
        }   
        
    }

    
    public void testC0ControlsNotAllowedInPath() {
        
        Element e = new Element("e");
        
        for (char c = 0; c <= ' '; c++) {
            String uri = "http://www.example.com/test/" + c + "data/";
            try {
                e.setNamespaceURI(uri);
                fail("Allowed C0 control 0x" + Integer.toHexString(c) + " in path");
            }
            catch (MalformedURIException success) {
                assertEquals(uri, success.getData());
            }
        }   
        
    }

    
    public void testC0ControlsNotAllowedInQueryString() {
        
        Element e = new Element("e");
        
        for (char c = 0; c <= ' '; c++) {
            String uri = "http://www.c.com/?name=" + c + "&value=7";
            try {
                e.setNamespaceURI(uri);
                fail("Allowed C0 control 0x" + Integer.toHexString(c) + " in query string");
            }
            catch (MalformedURIException success) {
                assertEquals(uri, success.getData());
            }
        }   
        
    }

    
    public void testHostNameTooLong() {
     
        StringBuffer uri = new StringBuffer("http://");
        for (int i = 0; i < 255; i++) uri.append('c');
        uri.append(".com/");
        Element e = new Element("e");
        try {
            e.setNamespaceURI(uri.toString());
            fail("Allowed excessively long host name");
        }
        catch (MalformedURIException success) {
            assertNotNull(success.getMessage());
        }
        
    }
    
    
    public void testSymbolsNotAllowedInSchemeNames() {
        
        Element e = new Element("e");
        
        char[] disallowed = { ';', '@', '&', '=', '$', ',', '"', '?', '#', '/', '\\', '|',
                 '_', '!', '~', '*', '\'', '(', ')', '<', '>', '[', ']', '{', '}', '^', '`'};
        
        for (int i = 0; i < disallowed.length; i++) {
            String uri = "scheme" + disallowed[i] + ":schemeSpecificData";
            try {
                e.setNamespaceURI(uri);
                fail("allowed " + uri + " as namespace URI");
            }
            catch (MalformedURIException success) {
                assertEquals(uri, success.getData());
            }
        }     
        
    }
    
    
    public void testNonASCIILettersNotAllowedToBeginSchemeNames() {
        
        Element e = new Element("e");
        
        for (char c = 'Z' +1; c < 'a'; c++) {
            String uri = c + "scheme:schemeSpecificData";
            try {
                e.setNamespaceURI(uri);
                fail("allowed " + uri + " as namespace URI");
            }
            catch (MalformedURIException success) {
                assertEquals(uri, success.getData());
            }
        }      
        
    }

    
    public void testBadHexEscapeInQueryString() {
        
        Element e = new Element("e");
        
        String uri = "scheme:schemeSpecificData?test%5test";
        try {
            e.setNamespaceURI(uri);
            fail("allowed " + uri + " as namespace URI");
        }
        catch (MalformedURIException success) {
            assertEquals(uri, success.getData());
        }  
        
        uri = "scheme:schemeSpecificData?test%5";
        try {
            e.setNamespaceURI(uri);
            fail("allowed " + uri + " as namespace URI");
        }
        catch (MalformedURIException success) {
            assertEquals(uri, success.getData());
        }  
        
    }
    

    public void testHexEscapeInUserInfo() {
        
        Element e = new Element("e");
        
        String uri = "scheme://user%C3%80TED@www.example.com/";
        e.setNamespaceURI(uri);
        assertEquals(uri, e.getNamespaceURI());  
        
    }

    
    public void testHexEscapeInHost() {
        
        Element e = new Element("e");
        
        String uri = "scheme://user%C3%80www.example.com/";
        e.setNamespaceURI(uri);
        assertEquals(uri, e.getNamespaceURI());  
        
    }

    
    public void testBadHexEscapeInUserInfo() {
        
        Element e = new Element("e");
        
        String uri = "scheme://user%5TED@www.example.com/";
        try {
            e.setNamespaceURI(uri);
            fail("allowed " + uri + " as namespace URI");
        }
        catch (MalformedURIException success) {
            assertEquals(uri, success.getData());
        }  
        
        uri = "scheme://user%5@www.example.com/";
        try {
            e.setNamespaceURI(uri);
            fail("allowed " + uri + " as namespace URI");
        }
        catch (MalformedURIException success) {
            assertEquals(uri, success.getData());
        }  
        
    }


    public void testBadHexEscapeInHost() {
        
        Element e = new Element("e");
        
        String uri = "scheme://user%5TEDwww.example.com/";
        try {
            e.setNamespaceURI(uri);
            fail("allowed " + uri + " as namespace URI");
        }
        catch (MalformedURIException success) {
            assertEquals(uri, success.getData());
        }  
        
        uri = "scheme://www.example.co%5/";
        try {
            e.setNamespaceURI(uri);
            fail("allowed " + uri + " as namespace URI");
        }
        catch (MalformedURIException success) {
            assertEquals(uri, success.getData());
        }  
        
    }


    public void testQuestionmarkIsNotAHexDigit() {
        
        Element e = new Element("e");
        
        // Have to do this in a fragment ID to keep it from being
        // interpreted as a query string separator
        String uri = "scheme://user@www.example.com/#fragment%?Adata";
        try {
            e.setNamespaceURI(uri);
            fail("allowed " + uri + " as namespace URI");
        }
        catch (MalformedURIException success) {
            assertEquals(uri, success.getData());
        }  
        
    }
    
    
    public void testIllegalIRIs() {
        
        int[] illegalChars = {0x00, 0xDC00, 0xE7FF, 0xF899, 0xD800, 
            0xFDE0, 0xFFFF, 0x1FFFE,  0x2FFFE, 0x3FFFF, 0x4FFFE, 
            0x4FFFF, 0x5FFFE, 0x6FFFF, 0x7FFFE, 0x8FFFF, 0x9FFFE,
            0xAFFFE, 0xBFFFF, 0xCFFFE, 0xDFFFE, 0xEFFFF, 0xFDDF};
        
        for (int i = 0; i < illegalChars.length; i++) {
            String utf16 = convertToUTF16(illegalChars[i]);
            String url = "http://www.example.com/" + utf16 + ".xml";
            try {
                new DocType("root", url);
                fail("Allowed URL containing 0x" + 
                  Integer.toHexString(illegalChars[i]).toUpperCase());
            }
            catch (MalformedURIException success) {
                assertNotNull(success.getMessage());
                assertEquals(url, success.getData());
            }
        }    
        
    }

    
    public void testLegalIP6Addresses() {
        
        String[] addresses = {
          "FEDC:BA98:7654:3210:FEDC:BA98:7654:3210",
          "1080:0:0:0:8:800:200C:4171",
          "3ffe:2a00:100:7031::1",
          "1080::8:800:200C:417A",
          "::192.9.5.5",
          "::FFFF:129.144.52.38",
          "2010:836B:4179::836B:4179",
          "1080:0:0:0:8:800:200C:417A",
          "FF01:0:0:0:0:0:0:101",
          "0:0:0:0:0:0:0:1",
          "0:0:0:0:0:0:0:0",
          "1080::8:800:200C:417A",
          "FF01::101",
          "::1",
          "::",
          "0:0:0:0:0:0:13.1.68.3",
          "0:0:0:0:0:FFFF:129.144.52.38",
          "::13.1.68.3",
          "::FFFF:129.144.52.38"
        };
        
        Element element = new Element("test");
        for (int i = 0; i < addresses.length; i++) {
            String url = "http://[" + addresses[i] + "]/";
            element.addAttribute(new Attribute("xml:base", 
              "http://www.w3.org/XML/1998/namespace", url));
            assertEquals(url, element.getBaseURI());
        }    
        
    }
    
    
    public void testIllegalIP6Addresses() {
        
        String[] addresses = {
          "FEDC:BA98:7654:3210:GEDC:BA98:7654:3 210",
          "FEDC:BA98:7654:3210:FEDC:BA98:7654:3210:4352",
          "FEDC:BA98:7654:3210:GEDC:BA98:7654:3210",
          "FEDC:BA98:7654:3210:GEDC:BA98:7654:G210",
          "FEDC:BA98:7654:3210:GEDC:BA98:7654: 3210",
          "FEDC:BA98:7654:3210:GEDC:BA98:7654:+3210",
          "FEDC:BA98:7654:3210:GEDC:BA98:7654:3210 ",
          "FEDC:BA98:7654:3210:GEDC:BA98:7654:32 10",
          "1080:0:::8:800:200C:4171",
          "3ffe::100:7031::1",
          "::192.9.5",
          "::FFFF:129.144.52.38.56",
          "::FFFF:129.144.52.A3",
          "::FFFF:129.144.52.-22",
          "::FFFF:129.144.52.+22",
          "::FFFF:256.144.52.+22",
          "::FFFF:www.apple.com",
          "1080:0:0:0:8:800:-200C:417A",
          "1080:0:0:0:-8:800:-200C:417A"
        };
        
        for (int i = 0; i < addresses.length; i++) {
            String url = "http://[" + addresses[i] + "]/";
            try {
                new DocType("root", url);
                fail("Allowed illegal IPv6 address: " +  addresses[i] );
            }
            catch (MalformedURIException success) {
                assertNotNull(success.getMessage());
                assertTrue(success.getData().indexOf(addresses[i]) >= 0);
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
    
    
    public void testC0Controls() {   
        
         for (char c = 0; c < '\t'; c++) {
             try {
                 new Text(String.valueOf(c));
             }
             catch (IllegalDataException success) {
                 assertNotNull(success.getMessage());
             }  
         }
         
         for (char c = '\r'+1; c < ' '; c++) {
             try {
                 new Text(String.valueOf(c));
             }
             catch (IllegalDataException success) {
                 assertNotNull(success.getMessage());
                 assertEquals(String.valueOf(c), success.getData());
             }  
         }
         
    }
    
    
    public void testAttributeNameThatEndsWithAColon() {
        
        try {
            new Attribute("name:", "http://www.example.com", "value", Attribute.Type.CDATA);
            fail("Allowed attribute name that ends with a colon");
        }
        catch (IllegalNameException success) {
            assertNotNull(success.getMessage());
            assertEquals("name:", success.getData());
        }
        
    }


    public void testAttributeNameThatBeginsWithAColon() {
        
        try {
            new Attribute(":name", "http://www.example.com", "value", Attribute.Type.CDATA);
            fail("Allowed attribute name that begins with a colon");
        }
        catch (IllegalNameException success) {
            assertNotNull(success.getMessage());
            assertEquals(":name", success.getData());
        }
        
    }


    public void testElementNameThatEndsWithAColon() {
        
        try {
            new Element("name:", "http://www.example.com");
            fail("Allowed element name that ends with a colon");
        }
        catch (IllegalNameException success) {
            assertNotNull(success.getMessage());
            assertEquals("name:", success.getData());
        }
        
    }


    public void testElementNameThatBeginsWithAColon() {
        
        try {
            new Element(":name", "http://www.example.com");
            fail("Allowed element name that begins with a colon");
        }
        catch (IllegalNameException success) {
            assertNotNull(success.getMessage());
            assertEquals(":name", success.getData());
        }
        
    }


}
