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

import java.io.Reader;
import java.io.StringReader;

import javax.xml.transform.sax.SAXSource;

import nu.xom.Document;
import nu.xom.Node;
import nu.xom.Nodes;

import org.xml.sax.InputSource;

/**
 * @author Elliotte Rusty Harold
 * @version 1.0d22
 *
 */
class XOMSource extends SAXSource {

  /**
   * <p>
   *  Used to determine if a 
   *  <code>TransformerFactory</code> natively supports XOM input.
   *  Factories that do not natively support XOM can use 
   *  <code>XOMSource</code> objects as instances of
   *  <code>SAXSource</code> instead.
   * <p>
   */
    public final static String XOM_FEATURE
       = "http://nu.xom/XOMResultFeature";

    // private Document document;
    private Nodes source;

    /**
     * <p>
     * Creates a new <code>XOMSource</code> object from a 
     * <code>Document</code>. The <code>Document</code> object 
     * is read but not changed by any method in this class.
     * </p>
     */
    public XOMSource(Document source) {
        this.source = new Nodes();
        this.source.append(source);
    }
    
    /**
     * <p>
     * Creates a new <code>XOMSource</code> object 
     * from a <code>Nodes</code>.
     * </p>
     */
    public XOMSource(Nodes source) {
        this.source = source;
    }
    
    public void setInputSource(InputSource inputSource) {
        throw new UnsupportedOperationException(
          "XOM isn't really SAX"
        ); 
    }

    public InputSource getInputSource() {
        StringBuffer data = new StringBuffer();
        for (int i = 0; i < source.size(); i++) {
            data.append(source.get(i).toXML());
        }
        Reader in = new StringReader(data.toString());
        InputSource source = new InputSource(in);
        Node first = this.source.get(0);
        if (first != null) source.setSystemId(first.getBaseURI()); 
        return source; 
    }

    public void setSystemId(String systemID) {
        throw new UnsupportedOperationException(
          "System ID is read from the document's base URI");   
    }

    public String getSystemId() {
        Node first = this.source.get(0);
        if (first == null) return null;
        else return first.getBaseURI();
    }      

}
