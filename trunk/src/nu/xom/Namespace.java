/* Copyright 2005 Elliotte Rusty Harold
   
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
   subject line. The XOM home page is located at http://www.xom.nu/
*/

package nu.xom;

import nu.xom.Element;

/**
 * <p>
 * This class represents a namespace in scope. It is used by XOM's 
 * XPath implementation for the namespace axis. However, it is not 
 * really part of the XOM data model. Namespace objects are only
 * created as needed when evaluating XPath.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.1d4
 *
 */
public final class Namespace extends Node {
    
    private String prefix;
    private String uri;
    private Element parent;
    
    public final static String XML_NAMESPACE 
      = "http://www.w3.org/XML/1998/namespace";

    public Namespace(String prefix, String uri, Element parent) {
        this.prefix = prefix;
        this.uri = uri;
        super.setParent(parent);
    }
    
    
    /**
     * @return the namespace prefix
     */
    public String getPrefix() {
        return prefix;
    }


    public String getValue() {
        return uri;
    }


    public Node getChild(int position) {
        throw new IndexOutOfBoundsException(
          "Namespaces do not have children");
    }


    public int getChildCount() {
        return 0;
    }


    public Node copy() {
        return new Namespace(prefix, uri, null);
    }


    public String toXML() {
        String colon = prefix.equals("") ? "" : ":";
        return "xmlns" + colon + prefix + "=\"" + uri + "\"";
    }
    
    
    public String toString() {
        return "[Namespace: " + this.toXML() + "]";
    }
    
    
    boolean isNamespace() {
        return true;
    }
    
    
}