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

import javax.xml.transform.sax.SAXSource;

import nu.xom.Document;
import nu.xom.Nodes;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

/**
 * @author Elliotte Rusty Harold
 * @version 1.1b4
 *
 */
class XOMSource extends SAXSource {
    

    private final Nodes source;

    
    /**
     * <p>
     * Creates a new <code>XOMSource</code> object from a 
     * <code>Document</code>. The <code>Document</code> object 
     * is read but not changed by any method in this class.
     * </p>
     * 
     * @param source 
     */
    XOMSource(Document source) {
        this.source = new Nodes(source);
    }
    
    
    /**
     * <p>
     * Creates a new <code>XOMSource</code> object 
     * from a <code>Nodes</code>.
     * </p>
     * 
     * @param source
     */
    public XOMSource(Nodes source) {
        this.source = source;
    }

    
    public InputSource getInputSource() {
        return new XOMInputSource(source);
    }

    
    public XMLReader getXMLReader() {
        return new XOMReader();
    }
    
    
    public String getSystemId() {
        if (this.source.size() == 0) return null;
        else return this.source.get(0).getBaseURI();
    }      

    
}
