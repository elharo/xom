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
import java.util.HashMap;

/**
 * <p>
 * The <code>Namespaces</code> container is a read-only list 
 * used to hold the additional namespace declarations of an 
 * element. It provides indexed access for convenience,
 * but the order is neither predictable nor reproducible,
 * and has no meaning. 
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0d23
 */
class Namespaces {
    
    private HashMap   namespaces = new HashMap(0);
    private ArrayList prefixes   = new ArrayList(0);
    
    // prevent instantiation from outside this package
    Namespaces() {}

    void put(String prefix, String URI) {
        namespaces.put(prefix, URI);
        prefixes.remove(prefix);
        prefixes.add(prefix);        
    }

    void remove(String prefix) {
        if (prefix == null) prefix = "";
        namespaces.remove(prefix);
        prefixes.remove(prefix);
    }

    
    /**
     * <p>
     * Return the URI associated with a prefix, as determined
     * by the namespaces stored in this list.  This method 
     * returns null if the prefix is not found in the list.
     * </p>
     * 
     * @param prefix the prefix whose URI is deserved.
     * 
     * @return the namespace URI for this prefix, or null if this 
     *      prefix is not not mapped to a URI by these namespace 
     *      declarations
     */
    public String getURI(String prefix) {
        return (String) (namespaces.get(prefix));
    }

    /**
     * <p>
     * Returns the number of namespace declarations in this list.
     * This is guaranteed to be non-negative.
     * </p>
     * 
     * @return the number of namespace declarations in this list.
     */
    public int size() {
        return namespaces.size();
    }

    /**
     * <p>
     * Returns the index<sup>th</sup> prefix in this list.
     * The index is purely for convenience, and has no
     * meaning in itself.
     * </p>
     * 
     * @param index the prefix to return
     * 
     * @return the prefix
     */
    public String getPrefix(int index) {
        return (String) prefixes.get(index);
    }
    
    // This violates encapsulation. Don't change the 
    // array returned.
    ArrayList getPrefixes() {
        return this.prefixes;
    }

}