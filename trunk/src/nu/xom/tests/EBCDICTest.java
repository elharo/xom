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

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.ParseException;
import nu.xom.Serializer;

/**
 * <p>
 *    Tests suport for the typical U.S. EBCDIC encoding.
 *    Unfortuantley this test exposes a bug in the handling
 *    of NEL, character 0x85, in Sun's JDK. Specifically
 *    InputStreamReader maps 0x85 to a line feed rather than 
 *    NEL. I've reported the bug to the Java Developer Connection,
 *    but until it's fixed this test fails. I don't have an easy
 *    workaround. 
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0d19
 *
 */
public class EBCDICTest extends XOMTestCase {
    
    public final static char NEL = 0x85;

    public EBCDICTest(String name) {
        super(name);
    }

    private Document doc;
    private String data;

    protected void setUp() {
        Element root = new Element("r");
        doc = new Document(root); 
        data = "\u0085";          
        root.appendChild(data);        
    }
  
    // This test will only pass if Java's NEL handling is fixed
    public void testEBCDIC037() 
      throws ParseException, UnsupportedEncodingException {
        
        Builder builder = new Builder(); 
        ByteArrayOutputStream out = new ByteArrayOutputStream();    
        try {
            // Write data into a byte array using encoding
            Serializer serializer = new Serializer(out, "Cp037");
            serializer.write(doc);
            serializer.flush();
            out.flush();
            out.close();
            byte[] result = out.toByteArray();

            // We have to look directly rather than converting to
            // a String because java gets the conversion of NEL to
            // Unicode wrong
            boolean foundNEL = false;
            for (int i = 0; i < result.length; i++) {
                if (result[i] == 0x15) foundNEL = true;        
            }
            assertTrue("Bad NEL output", foundNEL);

            InputStream in = new ByteArrayInputStream(result);
            Document reparsed = builder.build(in);
            assertEquals(doc, reparsed); 
        }
        catch (UnsupportedEncodingException ex) {
            throw ex;   
        }  
        catch (IOException ex) {
            ex.printStackTrace();   
        }  
        /* catch (ParseException ex) {
            throw ex;  
        }  */
            
    }
    

}
