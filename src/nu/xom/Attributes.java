/* Copyright 2002-2004 Elliotte Rusty Harold
   
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
   subject line. The XOM home page is temporarily located at
   http://www.cafeconleche.org/XOM/  but will eventually move
   to http://www.xom.nu/  */

package nu.xom;

import java.util.ArrayList;
import java.util.Iterator;


/**
 * <p>
 * Attributes is a read-only container used 
 * to hold the  attributes of an element (not including those used
 * for namespace declarations). It provides indexed access for 
 * convenience, but the attribute order is neither predictable 
 * nor reproducible, and has no meaning. 
 * </p>
 * 
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0a1
 * 
 */
final class Attributes {

    private ArrayList attributes = new ArrayList(1);
    
    // non-public constructor to prevent instantiation
    Attributes() {}
    
    
    /**
     * 
     * <p>
     * Returns the number of attributes in the list.
     * This is always a non-negative number.
     * </p>
     * 
     * @return the number of attributes in the container
     */
    int size() {
        return attributes.size();   
    }
    
    
    /**
     * 
     * <p>
     * Selects an attribute by its position in the list.
     * The index is purely for convenience and has no particular 
     * meaning. In particular, it is <em>not</em> necessarily 
     * the position of this attribute in the original document  
     * from which this <code>Attributes</code> object was read.
     * As with most lists in Java, attributes are numbered 
     * from 0 to one less than the length of the list.
     * </p>
     * 
     * @param index the attribute to return
     * 
     * @return the index<sup>th</sup> Attribute in the container
     * 
     * @throws IndexOutofBoundsException if the index is negative 
     *     or greater than or equal to the number of attributes 
     *     in the list
     * 
     */
    Attribute get(int index) {
        return (Attribute) attributes.get(index);   
    }
    
    
    void add(Attribute attribute) {

        checkPrefixConflict(attribute);
        
        // Is there already an attribute with this local name
        // and namespace? If so, remove it.
        Attribute oldAttribute = get(attribute.getLocalName(), 
          attribute.getNamespaceURI());
        if (oldAttribute != null) remove(oldAttribute);
        
        attributes.add(attribute);
    }
    
    
    void checkPrefixConflict(Attribute attribute) {
        
        String prefix = attribute.getNamespacePrefix();
        String namespaceURI = attribute.getNamespaceURI();
        
        // Look for conflicts
        Iterator iterator = attributes.iterator();
        while (iterator.hasNext()) {
            Attribute a = (Attribute) iterator.next();
            if (a.getNamespacePrefix().equals(prefix) 
              && !(a.getNamespaceURI().equals(namespaceURI))) {
                throw new NamespaceConflictException(
                 "Prefix of " + attribute.getQualifiedName() 
                 + " conflicts with " + a.getQualifiedName());
            }   
        }
    }

    
    // Remove the specified Attribute object from the list.
    void remove(Attribute attribute) {
        
        if (attribute == null) {
            throw new NullPointerException(
              "Tried to remove null attribute"
            );
        }
        boolean removed = attributes.remove(attribute);
        if (!removed) {
            throw new NoSuchAttributeException(
              "Tried to remove attribute " 
              + attribute.getQualifiedName() 
              + " from non-parent element");
        }
        
    }

    
    /**
     * <p>
     * Retrieves the attribute with the specified local name 
     * and namespace. The prefix is not considered when matching 
     * attributes. 
     * </p>
     * 
     * @param localName  the local name of the attribute to return
     * @param namespaceURI the namespace URI of the attribute to 
     *     return, or the empty string if this attribute is not in  
     *     a namespace. (All unprefixed attributes are never in 
     *     a namespace.)
     * 
     * @return the attribute with the specified name and URI, or null
     *     if this <code>Attributes</code> object does not contain 
     *     such an attribute
     */
    Attribute get(String localName, String namespaceURI) {
        
        Iterator iterator = attributes.iterator();
        while (iterator.hasNext()) {
            Attribute a = (Attribute) iterator.next();
            if (a.getLocalName().equals(localName) 
             && a.getNamespaceURI().equals(namespaceURI)) {
                return a;
            }   
        }
        
        return null;
        
    }
    
    
    // Make copying a set of attribute easy while bypassing most checks.
    // This is only intended for the use of Element.copy()
    Attributes copy() {
        
        Attributes result = new Attributes();
        result.attributes.ensureCapacity(this.size());
        for (int i = 0; i < this.attributes.size(); i++) {
            result.attributes.add(this.get(i).copy());
        }
        return result;
        
    }
    
}
