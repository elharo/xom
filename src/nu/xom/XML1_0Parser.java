/* Copyright 2004, 2009 Elliotte Rusty Harold
   
   This library is free software; you can redistribute it and/or modify
   it under the terms of version 2.1 of the GNU Lesser General Public 
   License as published by the Free Software Foundation.
   
   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
   GNU Lesser General Public License for more details.
   
   You should have received a copy of the GNU Lesser General Public
   License along with this library. If not, see
   <https://www.gnu.org/licenses/>.
   
   You can contact Elliotte Rusty Harold by sending e-mail to
   elharo@ibiblio.org. Please include the word "XOM" in the
   subject line. The XOM home page is located at https://xom.nu/
*/
package nu.xom;

import org.apache.xerces.parsers.SAXParser;
import org.apache.xerces.parsers.DTDConfiguration;

/**
 * <p>
 * This class is used by the <code>Builder</code> to prevent Xerces
 * from accepting XML 1.1 documents. When using regular Xerces
 * (<code>org.apache.xerces.parsers.SAXParser</code>) XOM verifies
 * everything. When using this subclass, XOM will rely on Xerces
 * to verify the rules, and skip its own verification checks.
 * </p>
 * 
 * <p>
 * This class does not support schema validation. If you want to use
 * the W3C XML Schema Language, download and install 
 * the full version of Xerces from <a target="_top"
 * href="https://xerces.apache.org/xerces2-j/">https://xerces.apache.org/xerces2-j/</a>.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.2.2b2
 * 
 */
class XML1_0Parser extends SAXParser {

    XML1_0Parser() {
        super(new DTDConfiguration());
    }

}
