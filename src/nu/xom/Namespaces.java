/* Copyright 2002-2006, 2019 Elliotte Rusty Harold
   
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
   subject line. The XOM home page is located at https://xom.nu/
*/

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
 * @version 1.3.1
 */
class Namespaces {
    
    private HashMap<String, String> namespaces = new HashMap<String, String>(1);
    private ArrayList<String> prefixes = new ArrayList<String>(1);
    

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
     * @param prefix the prefix whose URI is desired
     * 
     * @return the namespace URI for this prefix, or null if this 
     *      prefix is not not mapped to a URI by these namespace 
     *      declarations
     */
    String getURI(String prefix) {
        return (String) (namespaces.get(prefix));
    }
    
    
    // This violates encapsulation. Don't change the 
    // array returned.
    ArrayList<String> getPrefixes() {
        return this.prefixes;
    }
    
    
    Namespaces copy() {
        
        Namespaces result = new Namespaces();
        // shallow copies work here because these collections only
        // contain immutable strings
        result.namespaces = (HashMap<String, String>) this.namespaces.clone();
        result.prefixes   = (ArrayList<String>) this.prefixes.clone();
        return result;
        
    }


    int size() {
        return prefixes.size();
    }


    String getPrefix(int i) {
        return (String) prefixes.get(i);
    }

}