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

package nu.xom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
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
 * @version 1.3.0
 * 
 *
 */
public final class Elements implements Iterable<Element> {

    
    private List<Element> elements = new ArrayList<Element>(1);
    
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

    @Override
    public Iterator<Element> iterator() {
        return Collections.unmodifiableList(elements).iterator();
    }

}