// Copyright 2002-2004 Elliotte Rusty Harold
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
 * A read-only list of elements for traversal purposes.
 * Changes to the document from which this list was generated
 * are not reflected in this list. Changes to the individual 
 * <code>Element</code> objects in the list are reflected.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0a4
 * 
 *
 */
public final class Elements {

    
    private List elements = new ArrayList(1);
    
    // non-public constructor to prevent instantiation
    Elements() {}
    
    /**
     * <p>
     * Returns the number of elements in the list.
     * This is guaranteed non-negative. 
     * </p>
     * 
     * @return the number of elements in the list
     */
    public int size() {
        return elements.size(); 
    }
    
    /**
     * <p>
     * Returns the index<sup>th</sup> element in the list.
     * The first element has index 0. The last element
     * has index <code>size()-1</code>.
     * </p>
     * 
     * @param index the element to return
     * 
     * @return the element at the specified position
     * 
     * @throws IndexOutOfBoundsException if index is negative 
     *     or greater than or equal to the size of the list
     */
    public Element get(int index) {
        return (Element) elements.get(index);   
    }
    
    
    // Add the specified Element object to the list
    void add(Element element) {
        elements.add(element);
    }

}