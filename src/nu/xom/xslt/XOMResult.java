/* Copyright 2002-2004 Elliotte Rusty Harold
   
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

package nu.xom.xslt;

import javax.xml.transform.sax.SAXResult;

import nu.xom.NodeFactory;
import nu.xom.Nodes;
import org.xml.sax.ext.LexicalHandler;

/**
 * <p>
 *   Interface to TrAX.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0
 *
 */
class XOMResult extends SAXResult {
    
   
    XOMResult(NodeFactory factory) {
        super(new XSLTHandler(factory));
        this.setLexicalHandler((LexicalHandler) this.getHandler());
    }
    
    
    public Nodes getResult() {
        XSLTHandler handler = (XSLTHandler) this.getHandler();
        return handler.getResult();   
    }

    
}
