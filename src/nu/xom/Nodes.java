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

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * <p>
 * Implements a list of nodes for traversal purposes.
 * Changes to the document from which this list was generated
 * are not reflected in this list. Changes to the individual
 * <code>Node</code> objects in the list are reflected.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0d23
 * 
 *
 */
public final class Nodes {
    
    private List nodes;
    
    // would it be quicker to lazy allocate the nodes only
    // when the first element is appended????
    
    /**
     * <p>
     * Creates an empty node list. 
     * </p>
     */
    public Nodes() {
        nodes = new ArrayList();
    }
    
    /**
     * <p>
     * Creates a node list containing a single node.
     * </p>
     * 
     * @param node the node to insert in the list
     */
    public Nodes(Node node) {
        nodes = new ArrayList(1);
        nodes.add(node);
    }
    
    /**
     * <p>
     * Returns the number of nodes in the list.
     * This is guaranteed non-negative. 
     * </p>
     * 
     * @return the number of nodes in the list
     */
    public int size() {
        return nodes.size(); 
    }
    
    /**
     * <p>
     * Returns the index<sup>th</sup>node in the list.
     * The first node has index 0. The last node
     * has index <code>size()-1</code>.
     * </p>
     * 
     * @param index the node to return
     * 
     * @return the node at the specified position
     * 
     * @throws <code>IndexOutOfBoundsException</code> if index is  
     *     negative or greater than or equal to the size of the list
     */
    public Node get(int index) {
        return (Node) nodes.get(index);   
    }
    
    
    /**
     * <p>
     * Adds a node at the end of this list.
     * </p>
     * 
     * @param node the node to add to the list
     */
    public void append(Node node) {
        nodes.add(node);
    }

}