/* Copyright 2004 Elliotte Rusty Harold
   
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
 * If you want to set custom SAX features and properties 
 * on the <code>XMLReader</code> before creating a 
 * <code>Builder</code>, you should use this class in preference
 * to <code>org.apache.xerces.parsers.SAXParser</code>. 
 * </p>
 * 
 * <p>
 * This class does not support schema validation. If you want to use
 * the W3C XML Schema Language, you'll need to download and install 
 * the full version of Xerces from 
 * <a href="http://xml.apache.org/xerces2-j">http://xml.apache.org</a>.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0a4
 * 
 */
class XML1_0Parser extends SAXParser {

    XML1_0Parser() {
        super(new DTDConfiguration());
    }

}
