/* Copyright 2005, 2006 Elliotte Rusty Harold
   
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
   elharo@ibiblio.org. Please include the word "XOM" in the
   subject line. The XOM home page is located at http://www.xom.nu/
*/

package nu.xom;

/**
 * 
 * <p>
 * Used in XPath when querying a subtree that is not part of a 
 * document. This class is purely internal. Instances should never 
 * be visible in the public API.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.2d1
 *
 */
class DocumentFragment extends ParentNode {

    void insertionAllowed(Node child, int position) {
        // Everything can be inserted
    }


    public void setBaseURI(String URI) {
        throw new UnsupportedOperationException("XOM bug");
    }


    public String getValue() {
        throw new UnsupportedOperationException("XOM bug");
    }

    
    public Node copy() {
        throw new UnsupportedOperationException("XOM bug");
    }


    public String toXML() {
        throw new UnsupportedOperationException("XOM bug");
    }

    
    boolean isDocumentFragment() {
        return true;
    }
    
}
