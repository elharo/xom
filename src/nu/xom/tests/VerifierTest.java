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

import nu.xom.Element;
import nu.xom.IllegalNameException;

import org.apache.xerces.util.XMLChar;

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
    

}
