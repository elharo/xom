// Copyright 2004, 2018 Elliotte Rusty Harold
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
// elharo@ibiblio.org. Please include the word "XOM" in the
// subject line. The XOM home page is temporarily located at
// http://www.cafeconleche.org/XOM/  but will eventually move
// to https://xom.nu/

package nu.xom;

import org.xml.sax.SAXException;

import com.sun.org.apache.xerces.internal.parsers.SAXParser;
import com.sun.org.apache.xerces.internal.parsers.DTDConfiguration;
import com.sun.org.apache.xerces.internal.impl.Constants
;
/**
 * <p>
 * This class is used by the <code>Builder</code> to prevent the
 * repackaged Xerces shipped with Java 1.5 and some of the JAXP
 * reference implementations from accepting XML 1.1 documents.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.2.11
 * 
 */
class JDK15XML1_0Parser extends SAXParser {

    JDK15XML1_0Parser() throws SAXException {
      
        super(new DTDConfiguration());
        // workaround for Java 1.5 beta 2 bugs
        com.sun.org.apache.xerces.internal.util.SecurityManager manager 
          = new com.sun.org.apache.xerces.internal.util.SecurityManager();
        setProperty(Constants.XERCES_PROPERTY_PREFIX + Constants.SECURITY_MANAGER_PROPERTY, manager);
        
    }

}
