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


package nu.xom;

/**
 * <p>
 *  This class represents the leaf nodes in an XML document;
 *  i.e. those tree nodes that cannot have children:
 * </p>
 * 
 * <ul>
 *   <li><code>Text</code></li>
 *   <li><code>Comment</code></li>
 *   <li><code>ProcessingInstruction</code></li>
 *   <li><code>DocType</code></li>
 * </ul>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0d13
 * 
 */
public abstract class LeafNode extends Node {

    // prevent direct subclassing from outside this package
    LeafNode() {}

    /**
     * <p>
     * Returns false because leaf nodes do not have children.
     * </p>
     * 
     * @return false
     * @see nu.xom.ParentNode#hasChildren()
     */
    public final boolean hasChildren() {
        return false;   
    }

    /**
     * <p>
     * Throws <code>IndexOutOfBoundsException</code> because 
     * leaf nodes do not have children.
     * </p>
     * 
     * @return never returns because leaf nodes do not have children.
     *     Always throws an exception.
     * 
     * @param position the index of the child node to return
     * 
     * @throws IndexOutOfBoundsException because leaf nodes 
     *     do not have children
     */
    public final Node getChild(int position) {
        throw new IndexOutOfBoundsException(
          "LeafNodes do not have children");        
    }

    /**
     * <p>
     * Returns 0 because leaf nodes do not have children.
     * </p>
     * 
     * @return zero
     * @see nu.xom.ParentNode#getChildCount()
     */
    public final int getChildCount() {
        return 0;   
    }

}
