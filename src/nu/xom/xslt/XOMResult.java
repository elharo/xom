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
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0d23
 *
 */
class XOMResult extends SAXResult {

  /**
   * <p>
   *  Used to determine if a <code>TransformerFactory</code> natively 
   *  supports XOM output. Factories that do not natively support XOM
   *  can use <code>XOMResult</code> objects as instances of
   *  <code>SAXResult</code> instead.
   * <p>
   */
   public final static String XOM_FEATURE 
     = "http://nu.xom/XOMResultFeature";

    /**
     * Constructor for XOMResult.
     */
    public XOMResult() {
        super(new XSLTHandler(new NodeFactory()));
        this.setLexicalHandler((LexicalHandler) this.getHandler());
    }
    
    // Currently the assumption is that only the standard NodeFactory
    // class will be used. If we ever allow the XOMResult to have a 
    // different subclass of NodeFactory (which might be a good idea
    // on its own merits????) some changes will need to be made in 
    // XSLTHandler as well as here

    public Nodes getResult() {
        XSLTHandler handler = (XSLTHandler) this.getHandler();
        return handler.getResult();   
    }

}
