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

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * <p>
 * This class represents an XML element. Each 
 * element has the following properties:
 * </p>
 * 
 * <ul>
 *   <li>Local name</li>
 *   <li>Prefix (which may be null or the empty string) </li>
 *   <li>Namespace URI (which may be null or the empty string) </li>
 *   <li>A list of attributes</li>
 *   <li>A list of namespace declarations for this element
 *       (not including those inherited from its parent)</li>
 *   <li>A list of child nodes</li>
 * </ul>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0d25
 *
 */
public class Element extends ParentNode {

    private String localName;
    private String prefix;
    private String URI;

    private Attributes attributes = null;
    private Namespaces namespaces = null;

    /**
     * <p>
     * Creates a new element in no namespace.
     * </p>
     * 
     * @param name the name of the element
     * 
     * @throws IllegalNameException if <code>name</code>
     *     is not a legal XML 1.0 non-colonized name
     */
    public Element(String name) {
        this(name, "");
    }

    
    /**
     * <p>
     * Creates a new element in a namespace.
     * </p>
     * 
     * @param name the qualified name of the element
     * @param uri the namespace URI of the element
     * 
     * @throws IllegalNameException if <code>name</code>  
     *     is not a legal XML 1.0 non-colonized name
     * @throws NamespaceConflictException if <code>name</code>'s prefix  
     *     cannot be used with <code>uri</code>
     * @throws MalformedURIException if <code>uri</code>  
     *     is not an RFC 2396 absolute URI reference
     */
    public Element(String name, String uri) {
        
        // The shadowing is important here.
        // I don't want to set the prefix field just yet.
        String prefix = "";
        String localName = name;
        int colon = name.indexOf(':');
        if (colon > 0) {
            prefix = name.substring(0, colon);   
            localName = name.substring(colon + 1);   
        }
        
        // The order of these next two calls
        // matters a great deal.
        setNamespacePrefix(prefix);
        setNamespaceURI(uri);
        try {
            setLocalName(localName);
        }
        catch (IllegalNameException ex) {
            ex.setData(name);
            throw ex;
        }
        
    }

    
    private Element() {}

    static Element build(String name, String uri) {
        Element result = new Element();
        String prefix = "";
        String localName = name;
        int colon = name.indexOf(':');
        if (colon >= 0) {
            prefix = name.substring(0, colon);   
            localName = name.substring(colon + 1);   
        }
        result.prefix = prefix;
        result.localName = localName;
        // We do need to verify the URI here because parsers are 
        // allowing relative URIs which XOM forbids, for reasons
        // of canonical XML if nothing else
        result.setNamespaceURI(uri);
        return result;
        
    }


    /**
     * <p>
     * Creates a deep copy of an element.
     * The copy is disconnected from the tree, and does not
     * have a parent.
     * </p>
     * 
     * @param element the element to copy
     * 
     */
    public Element(Element element) {
        
        this.prefix = element.prefix;
        this.localName = element.localName;
        this.URI = element.URI;
        
        // Attach additional namespaces
        if (element.namespaces != null) {
            this.namespaces = element.namespaces.copy();
        }
        
        // Attach clones of attributes
        if (element.attributes != null) {
            this.attributes = element.attributes.copy();
        } 
        
        this.setActualBaseURI(element.getActualBaseURI());
        
        // non-recursive algorithm for filling in children
        // doesn't yet work
        /*
        Node current = element;
        Node newNode = this;
        ParentNode copyParent = this;
        boolean end = false;
        int index = -1;
        while (true) {
            
            // 1. Append a copy of the current node to the current parent
            if (index != -1 && !end) {
                if (current.isElement()) {
                    copyParent.appendChild(copyTag((Element) current));
                }
                else {
                    copyParent.appendChild(current.copy());
                }
            }
            
            
            // 2. Select next following node
            if (!end && current.getChildCount() > 0) {
               current = current.getChild(0);
               index = 0;
            }
            else  if (current == element) break;
            else {
              end = false;
              ParentNode parent = current.getParent();
              if (parent.getChildCount() - 1 == index) {
                current = parent;
                if (current != element) {
                    parent = current.getParent();
                    index = parent.indexOf(current);
                }
                end = true;
              }
              else {
                 index++;
                 current = parent.getChild(index);
              }
              
            }

        }        
        */
        
        for (int i = 0; i < element.getChildCount(); i++) {
           Node child = element.getChild(i);
           this.appendChild(child.copy());
        }
        
    }


    private static Element copyTag(Element original) {
    
        Element copy = new Element();
        copy.prefix = original.prefix;
        copy.localName = original.localName;
        copy.URI = original.URI;
        
        // Attach additional namespaces
        if (original.namespaces != null) {
            copy.namespaces = original.namespaces.copy();
        }
        
        // Attach clones of attributes
        if (original.attributes != null) {
            copy.attributes = original.attributes.copy();
        } 
        
        copy.setActualBaseURI(original.getActualBaseURI());
        return copy;
        
    }


    /**
     * <p>
     * Returns a list of the child elements of 
     * this element with the specified name in no namespace.
     * </p>
     * 
     * @param name the name of the elements included in the list
     * 
     * @return a comatose list containing the child elements of this 
     *     element with the specified name
     */
    public final Elements getChildElements(String name) {
        return getChildElements(name, "");
    }

    
    /**
     * <p>
     * Returns a list of the immediate child elements of this 
     * element with the specified local name and namespace URI.
     * Passing the empty string or null as the local name
     * returns all elements in the specified namespace.
     * Passing null or the empty string as the namespace URI 
     * returns elements with the specified name in no namespace.
     * </p>
     * 
     * @param localName the name of the elements included in the list
     * @param namespaceURI the namespace URI of the elements included
     *     in the list
     * 
     * @return a comatose list containing the child  
     *    elements of this element with the specified
     *    name in the specified namespace
     */
    public final Elements getChildElements(String localName, 
     String namespaceURI) {
        
        if (namespaceURI == null) namespaceURI = "";
        if (localName == null) localName = "";
        
        Elements elements = new Elements();
        for (int i = 0; i < getChildCount(); i++) {
            Node child = getChild(i);
            if (child.isElement()) {
                Element element = (Element) child;
                if ((localName.equals(element.getLocalName()) 
                  || localName.length() == 0)
                  && namespaceURI.equals(element.getNamespaceURI())) {
                    elements.add(element);
                }
            }
        }
        return elements;    
        
    }

    
    /**
     * <p>
     * Returns a list of all the child elements 
     * of this element.
     * </p>
     * 
     * @return a comatose list containing all  
     *    child elements of this element
     */
    public final Elements getChildElements() {
        
        Elements elements = new Elements();
        for (int i = 0; i < getChildCount(); i++) {
            Node child = getChild(i);
            if (child.isElement()) {
                Element element = (Element) child;
                elements.add(element);  
            }
        }
        return elements;    
        
    }

    
    /**
     * <p>
     * Returns the first child
     * element with the specified name in no namespace.
     * If there is no such element, it returns null.
     * </p>
     * 
     * @param name the name of the element to return
     * 
     * @return the first child element with the specified local name 
     *    in no namespace or null if there is no such element
     */
    public final Element getFirstChildElement(String name) {
        return getFirstChildElement(name, "");
    }

    
    /**
     * <p>
     * Returns the first child
     * element with the specified local name and namespace URI.
     * If there is no such element, it returns null.
     * </p>
     * 
     * @param localName the local name of the element to return
     * @param namespaceURI the namespace URI of the element to return
     * 
     * @return the first child with the specified local name in the
     *      specified namespace, or null if there is no such element
     */
    public final Element getFirstChildElement(String localName, 
     String namespaceURI) {
        
        for (int i = 0; i < getChildCount(); i++) {
            Node child = getChild(i);
            if (child.isElement()) {
                Element element = (Element) child;
                if (localName.equals(element.getLocalName())
                  && namespaceURI.equals(element.getNamespaceURI())) {
                    return element;
                }   
            }
        }
        return null;    
        
    }


    /**
     * <p>
     * Adds an attribute to this element, replacing any existing 
     * attribute with the same local name and namespace URI.
     * </p>
     * 
     * @param attribute the attribute to add
     * 
     * @throws MultipleParentException if the attribute is already
     *      attached to an element
     * @throws NamespaceConflictException if the attribute's prefix   
     *      is mapped to a different namespace URI than the same prefix
     *      is mapped to by the element itself, another attribute of 
     *      the same element, or an additional namespace declaration
     *      of that element
     */
    public void addAttribute(Attribute attribute) {

        if (attribute.getParent() != null) {
            throw new MultipleParentException(
              "Attribute already has a parent");
        }
        
        // check for namespace conflicts
        String attPrefix = attribute.getNamespacePrefix();
        if (attPrefix.length() != 0 && !"xml".equals(attPrefix)) {
            if (prefix.equals(attribute.getNamespacePrefix())
              && !(getNamespaceURI()
               .equals(attribute.getNamespaceURI()))) {
                throw new NamespaceConflictException("Prefix of " 
                  + attribute.getQualifiedName() 
                  + " conflicts with element prefix " + prefix);  
            }
            // check for conflicts with additional namespaces
            if (namespaces != null) {
                String existing 
                 = namespaces.getURI(attribute.getNamespacePrefix());
                if (existing != null 
                  && !existing.equals(attribute.getNamespaceURI())) {
                    throw new NamespaceConflictException("Attribute prefix  " 
                      + attPrefix 
                      + " conflicts with namespace declaration.");            
                }
            }
            // The Attributes class checks for conflicts with the 
            // namespaces of other attributes
        }
        
        if (attributes == null) attributes = new Attributes();
        attributes.add(attribute);
        attribute.setParent(this);
        
    }

    
    /**
     * <p>
     * Removes an attribute from this element.
     * </p>
     * 
     * @param attribute the attribute to remove
     * 
     * @return the attribute that was removed
     * 
     * @throws NullPointerException if the argument is null
     * @throws NoSuchAttributeException if this element is not the  
     *     parent of attribute
     * 
     */
    public Attribute removeAttribute(Attribute attribute) {
        
        if (attributes == null) {
            throw new NoSuchAttributeException( "Tried to remove attribute "
              + attribute.getQualifiedName() 
              + " from non-parent element");
        }        
        attributes.remove(attribute);
        attribute.setParent(null);
        return attribute;
        
    }


    /**
     * <p>
     * Returns the attribute with the specified name in no namespace,
     * or null if this element does not have an attribute 
     * with that name in no namespace.
     * </p>
     * 
     * @param name the name of the attribute
     * 
     * @return the attribute of this element with the specified name
     */
    public final Attribute getAttribute(String name) {
        return getAttribute(name, "");
    }

    
    /**
     * <p>
     * Returns the attribute with the specified name and namespace URI,
     * or null if this element does not have an attribute 
     * with that name in that namespace.
     * </p>
     * 
     * @param localName the local name of the attribute
     * @param namespaceURI the namespace of the attribute
     * 
     * @return the attribute of this element 
     *     with the specified name and namespace
     */
    public final Attribute getAttribute(String localName,
      String namespaceURI) {
        if (attributes == null) return null;
        return attributes.get(localName, namespaceURI);
    }

    
    /**
     * <p>
     * Returns the value of the attribute with the specified 
     * name in no namespace,
     * or null if this element does not have an attribute 
     * with that name.
     * </p>
     * 
     * @param name the name of the attribute
     * 
     * @return the value of the attribute of this element 
     *     with the specified name
     */
    public final String getAttributeValue(String name) {
        return getAttributeValue(name, "");
    } 

    
    /**
     * 
     * <p>
     * Returns the number of attributes of this <code>Element</code>,
     * not counting namespace declarations.
     * This is always a non-negative number.
     * </p>
     * 
     * @return the number of attributes in the container
     */
    public final int getAttributeCount() {
        if (attributes == null) return 0;
        return attributes.size();   
    }
    
    
    /**
     * 
     * <p>
     * Selects an attribute by index.
     * The index is purely for convenience and has no particular 
     * meaning. In particular, it is <em>not</em> necessarily the 
     * position of this attribute in the original document from 
     * which this <code>Element</code> object was read.
     * As with most lists in Java, attributes are numbered  
     * from 0 to one less than the length of the list.
     * </p>
     * 
     * <p>
     * In general, you should not add attributes to or remove 
     * attriubtes from the list while iterating across it. 
     * Doing so will change the indexes of the other attributes in 
     * the list. it is, however, safe to remove an attribute from 
     * either end of the list (0 or <code>getAttributeCount()-1</code>)
     * until there are no attributes left. 
     * </p>
     * 
     * @param index the attribute to return
     * 
     * @return the index<sup>th</sup> Attribute in this element
     * 
     * @throws IndexOutofBoundsException if the index is negative 
     *   or greater than or equal to the number of attributes 
     *   of this element
     * 
     */
    public final Attribute getAttribute(int index) {
        if (attributes == null) {
            throw new IndexOutOfBoundsException(
              "Element does not have any attributes"
            );
        }
        return attributes.get(index);   
    }


    /**
     * <p>
     * Returns the value of the attribute with the 
     * specified name and namespace URI,
     * or null if this element does not have such an attribute.
     * </p>
     * 
     * @param localName the name of the attribute
     * @param namespaceURI the namespace of the attribute
     * 
     * @return the value of the attribute of this element 
     *     with the specified name and namespace
     */
    public final String getAttributeValue(String localName, 
                                          String namespaceURI) {
        
        if (attributes == null) return null;                                      
        Attribute attribute = attributes.get(localName, namespaceURI);
        if (attribute == null) return null;
        else return attribute.getValue();
        
    }
    
    
    /**
     * <p>
     * Returns the local name of this element, not including the 
     * namespace prefix or colon.
     * </p>
     * 
     * @return the local name of this element
     */
    public final String getLocalName() {
        return localName;
    }

    
    /**
     * <p>
     * Returns the complete name of this element, including the 
     * namespace prefix if this element has one.
     * </p>
     * 
     * @return the qualified name of this element
     */
    public final String getQualifiedName() {
        if (prefix.length() == 0) return localName;
        else return prefix + ":" + localName;
    }

    
    /**
     * <p>
     * Returns the prefix of this element, or the empty string
     * if this element does not have a prefix.
     * </p>
     * 
     * @return the prefix of this element
     */
    public final String getNamespacePrefix() {
        return prefix;
    }

    
    /**
     * <p>
     * Returns the namespace URI of this element,
     * or the empty string if this element is not
     * in a namespace.
     * </p>
     * 
     * @return  the namespace URI of this element
     */
    public final String getNamespaceURI() {
        return URI;
    }

    
    /**
     * <p>
     * Returns the namespace URI mapped to the specified
     * prefix within this element. Returns null if this prefix
     * is not associated with a URI.
     * </p>
     * 
     * @param prefix the namespace prefix whose URI is desired
     *
     * @return the namespace URI mapped to <code>prefix</code>
     */
    public final String getNamespaceURI(String prefix) {
        
        Element current = this;
        String result = getLocalNamespaceURI(prefix);
        while (result == null) {
            ParentNode parent = current.getParent();
            if (parent == null || parent.isDocument()) break;
            current = (Element) parent; 
            result = current.getLocalNamespaceURI(prefix);
        }
        if (result == null && "".equals(prefix)) result = "";
        return result;

    }

    
    String getLocalNamespaceURI(String prefix) {
        
        if (prefix.equals(this.prefix)) return this.URI;
        
        if ("xml".equals(prefix)) {
            return "http://www.w3.org/XML/1998/namespace";
        }
        // This next line uses the original Namespaces 1.0 
        // specification rules.
        // Namespaces 1.0 + errata is different
        if ("xmlns".equals(prefix)) return "";
        // Look in the additional namespace declarations
        if (namespaces != null) {
            String result = namespaces.getURI(prefix);
            if (result != null) return result;
        }
        // Look in the attributes
        if (prefix.length() != 0 && attributes != null) {
            for (int i = 0; i < attributes.size(); i++) {
                Attribute a = attributes.get(i);
                if (a.getNamespacePrefix().equals(prefix)) {
                    return a.getNamespaceURI();
                }   
            }
        }       
        
        return null;
        
    }

   
    /**
     * <p>
     * Sets the local name of this element.
     * </p>
     * 
     * @param localName the new local name
     * 
     * @throws IllegalNameException if <code>localName</code> is not
     *     a legal, non-colonized name
     */
    public void setLocalName(String localName) {
        
        Verifier.checkNCName(localName);
        this.localName = localName;
        
    }


    /**
     * <p>
     * Sets the namespace URI of this element.
     * </p>
     * 
     * @param uri the new namespace URI
     * 
     * @throws MalformedURIException if <code>uri</code>  
     *     is not an absolute RFC2396 URI reference
     * @throws NamespaceException if this element has a prefix 
     *     and <code>uri</code> is null or the empty string;
     *     or if the element's prefix is shared by an attribute
     *     or additional namespace
     */
    public void setNamespaceURI(String uri) {
        
        if (uri == null) uri = "";
        // Next line is needed to avoid unintentional
        // exceptions below when checking for conflicts
        if (uri.equals(this.URI)) return;
        if (uri.length() == 0) { // faster than "".equals(uri)
            if (prefix.length() != 0) {
                throw new NamespaceConflictException(
                 "Prefixed elements must have namespace URIs."
                );  
            }
        }
        else Verifier.checkAbsoluteURIReference(uri);
        // Make sure this doesn't conflict with any local
        // attribute prefixes or additional namespace declarations
        // Note that if the prefix equals the prefix, then the
        // URI must equal the old URI, so the URI can't easily be
        // changed. (you'd need to detach everything first;
        // change the URIs, then put it all back together
        if (namespaces != null) {
        String result = namespaces.getURI(prefix);
            if (result != null) {  
                throw new NamespaceConflictException(
                  "new URI conflicts with existing prefix"
                );
            }
        }
        // Look in the attributes
        if (uri.length() > 0 && attributes != null) {
            for (int i = 0; i < attributes.size(); i++) {
                Attribute a = attributes.get(i);
                String attPrefix = a.getNamespacePrefix();
                if (attPrefix.length() == 0) continue;
                if (a.getNamespacePrefix().equals(prefix)) {
                    throw new NamespaceConflictException(
                      "new element URI " + uri 
                      + " conflicts with attribute " 
                      + a.getQualifiedName()
                    );
                }   
            } 
        }      

        if ("http://www.w3.org/XML/1998/namespace".equals(uri) 
          && ! "xml".equals(prefix)) {
            throw new NamespaceConflictException(
              "Wrong prefix " + prefix + 
              " for the http://www.w3.org/XML/1998/namespace namespace URI"
            );      
        }
        else if ("xml".equals(prefix) && 
          !"http://www.w3.org/XML/1998/namespace".equals(uri)) {
            throw new NamespaceConflictException(
              "Wrong namespace URI " + uri + " for the xml prefix"
            );      
        }
        
        this.URI = uri;
        
    }

    
    /**
     * <p>
     * Sets the namespace prefix of this element.
     * You can pass null or the empty string to remove the prefix.
     * </p>
     * 
     * @param prefix the new namespace prefix
     * 
     * @throws IllegalNameException if <code>prefix</code> is 
     *     not a legal XML non-colonized name
     * @throws NamespaceConflictException if <code>prefix</code> is 
     *     already in use by an attribute or additional
     *     namespace with a different URI than the element
     *     itself
     */
    public void setNamespacePrefix(String prefix) {
        
        if (prefix == null) prefix = "";
        if (prefix.length() != 0) Verifier.checkNCName(prefix);

        // Check how this affects or conflicts with
        // attribute namespaces and additional
        // namespace declarations.
        String uri = getLocalNamespaceURI(prefix);
        if (uri != null) {
            if (!uri.equals(this.URI) && !"xml".equals(prefix)) {
                throw new NamespaceConflictException(prefix 
                 + " conflicts with existing prefix");
            }
        }
        else if ("".equals(this.URI) && !"".equals(prefix)) {
            throw new NamespaceConflictException(
              "Cannot assign prefix to element in no namespace");            
        } 

        this.prefix = prefix;

    }


    /**
     * <p>
     * Inserts a child node at the specified position.
     * Inserting at position 0 makes the child the first child
     * of this node. Inserting at the position 
     * <code>getChildCount()</code>
     * makes the child the last child of the node.
     * </p>
     * 
     * <p>
     * All the other methods that add a node to the tree,
     * invoke this method ultimately.
     * </p>
     * 
     * @param position where to insert the child
     * @param child the node to insert
     * 
     * @throws IllegalAddException if <code>child</code> 
     *   is a <code>Document</code>
     * @throws MultipleParentException if <code>child</code> 
     *   already has a parent
     * @throws NullPointerException if <code>child</code> is null
     * @throws IndexOutOfBoundsException if the position is negative 
     *     or greater than the number of children of this element.
     */
    void insertionAllowed(Node child, int position) {
        
        if (child == null) {
            throw new NullPointerException(
             "Tried to insert a null child in the tree");
        }
        else if (child.getParent() != null) {
            throw new MultipleParentException(child.toString() 
              + " child already has a parent.");
        }
        else if (child.isElement()) {
            if (child == this) {
                throw new CycleException(
                  "Cannot add a node to itself");  
            }
            else if (child.getChildCount() > 0 && isCycle(child, this)) {
                throw new CycleException(
                  "Cannot add an ancestor as a child");                   
            }
            return;            
        }
        else if (child.isText()
          || child.isProcessingInstruction()
          || child.isComment()) {
            return;
        }
        else {
            throw new IllegalAddException("Cannot add a "
             + child.getClass().getName() + " to an Element.");
        }

    }
    
    
    private static boolean isCycle(Node child, ParentNode parent) {       
        
        while (parent != null) {
            if (parent == child) return true;
            parent = parent.getParent();
        }
        return false;
        
    }

    
    /**
     * <p>
     * Converts a string to a text node and inserts that
     * node at the specified position.
     * </p>
     * 
     * @param position where to insert the child
     * @param text the string to convert to a text node and insert
     * 
     * @throws NullPointerException if text is null
     * @throws IndexOutOfBoundsException if the position is negative
     *     or greater than the number of children of the node
     */
    public void insertChild(String text, int position) {
        
       if (text == null) {
              throw new NullPointerException("Inserted null string");
       }
       super.insertChild(new Text(text), position);
       
    } 


    /**
     * <p>
     * Converts a string to a text node
     * and appends that node to the children of this node.
     * </p>
     * 
     * @param text String to add to this node
     * 
     * @throws IllegalAddException if this node cannot 
     *     have children of this type
     * @throws NullPointerException if <code>text</code> is null
     */
    public void appendChild(String text) {
        insertChild(new Text(text), getChildCount());
    } 


    /**
     * <p>
     * Detaches all children from this node. 
     * </p>
     * 
     * <p>
     * Subclassers should note that the default implementation of this
     * method does <strong>not</strong> call <code>removeChild</code>
     * or <code>detach</code>. If you override 
     * <code>removeChild</code>, you'll probably need to override this
     * method as well.
     * </p>
     * 
     * @return a list of all the children removed in the order they
     *     appeared in the element
     */
    public Nodes removeChildren() {
        
        int length = this.getChildCount();
        Nodes result = new Nodes();
        for (int i = 0; i < length; i++) {
            Node child = getChild(i);
            child.setParent(null);
            result.append(child);
        }   
        this.children = null;
        
        return result;
        
    }

    
    /**
     * <p>
     * Declares a namespace prefix. This is only necessary when
     * prefixes are used in element content and attribute values,
     * as in XSLT and the W3C XML Schema Language. Do not use 
     * this method to declare prefixes for element and attribute 
     * names.
     * </p>
     * 
     * <p>
     *   If you do redeclare a prefix that is already used
     *   by an element or attribute name, the additional 
     *   namespace is added if and only if the URI is the same.
     *   Conflicting namespace declarations will throw an exception.
     * </p>
     * 
     * @param prefix the prefix to declare
     * @param uri the absolute URI reference to map the prefix to
     * 
     * @throws MalformedURIException if <code>URI</code> 
     *      is not an RFC2396 URI reference
     * @throws IllegalNameException  if <code>prefix</code> is not 
     *      a legal XML non-colonized name
     * @throws NamespaceConflictException if the mapping conflicts 
     *     with an existing element, attribute,
     *     or additional namespace declaration
     */
    public void addNamespaceDeclaration(String prefix, String uri) {

        if (prefix == null) prefix = "";
        if (uri == null) uri = "";
        
        if (prefix.length() != 0) {
            Verifier.checkNCName(prefix);
            Verifier.checkAbsoluteURIReference(uri);
        }
        else if (uri.length() != 0) {
            // Make sure we're not trying to undeclare 
            // the default namespace; this is legal.
            Verifier.checkAbsoluteURIReference(uri);
        }
        
        // check to see if this is the xmlns or xml prefix
        if (prefix.equals("xmlns")) {
            throw new NamespaceConflictException(
             "The xmlns prefix cannot bound to any URI");   
        }
        else if (prefix.equals("xml")) {
            if (uri.equals("http://www.w3.org/XML/1998/namespace")) {
                // This is done automatically
                return; 
            }
            else {
                throw new NamespaceConflictException(
                 "Wrong namespace URI for xml prefix: " + uri);
            }   
        }
        else if (uri.equals("http://www.w3.org/XML/1998/namespace")) {
            throw new NamespaceConflictException(
               "Wrong prefix for http://www.w3.org/XML/1998/namespace namespace: " 
               + prefix);  
        }
        
        String currentBinding = getLocalNamespaceURI(prefix);
        if (currentBinding != null && !currentBinding.equals(uri)) {
            throw new NamespaceConflictException(
              "Additional namespace " + uri 
              + " conflicts with existing namespace binding."
            );   
        }
        
        if (namespaces == null) namespaces = new Namespaces();
        namespaces.put(prefix, uri);
        
    }


    /**
     * <p>
     * Removes the mapping of the specified prefix. This method only
     * removes additional namespaces added with 
     * <code>addNamespaceDeclaration</code>.
     * It has no effect on namespaces of elements and attributes.
     * If the prefix is not used on this element, this method
     * does nothing.
     * </p>
     * 
     * @param prefix the prefix whose declaration should be removed
     */
    public void removeNamespaceDeclaration(String prefix) {

        if (namespaces != null) {
            namespaces.remove(prefix);
        }
        
    }

    
    /**
     * <p>
     * Returns the number of namespace declarations on this 
     * element. This counts the namespace of the element 
     * itself (which may be the empty string), the namespace  
     * of each attribute, and each namespace added  
     * by <code>addNamespaceDeclaration</code>.
     * However, prefixes used multiple times are only counted 
     * once, and the <code>xml</code> prefix used for 
     * <code>xml:base</code>, <code>xml:lang</code>, and 
     * <code>xml:space</code> is not counted even if one of these 
     * attributes is present on the element.
     * </p>
     * 
     * <p>
     * The return value is almost always positive. It can be zero 
     * if and only if the element itself has the prefix 
     * <code>xml</code>; e.g. <code>&lt;xml:space /></code>.
     * This is not endorsed by the XML specification. The prefix
     * <code>xml</code> is reserved for use by the W3C, which has only
     * used it for attributes to date. You really shouldn't do this.
     * Nonetheless, this is not malformed so XOM allows it.
     * </p>
     * 
     * @return the number of namespaces declared by this element
     */
    public final int getNamespaceDeclarationCount() {
        
        Set allPrefixes;
        if (namespaces != null) {
            allPrefixes = new HashSet(namespaces.getPrefixes());
        } 
        else allPrefixes = new HashSet(3);
        if (!"xml".equals(prefix)) allPrefixes.add(prefix);
        // add attribute prefixes
        for (int i = 0; i < getAttributeCount(); i++) {
            Attribute att = getAttribute(i);
            String attPrefix = att.getNamespacePrefix();
            if (attPrefix.length() != 0 && !"xml".equals(attPrefix)) {
                allPrefixes.add(attPrefix);    
            }
        }
        return allPrefixes.size();
        
    }

    
    /**
     * <p>
     * Returns the index<sup>th</sup> namespace prefix declared on
     * this element. The index is purely for convenience, and has no
     * meaning in itself. This includes the namespaces of the element
     * name and of all attributes' names (except for those with the 
     * prefix <code>xml</code> such as <code>xml:space</code>) as well 
     * as additional declarations made for attribute values and element 
     * content. However, prefixes used multiple times (e.g. on several
     * attribute values) are only reported once. The default
     * namespace is reported with an empty string prefix if present.
     * Like most lists in Java, the first prefix is at index 0.
     * </p>
     * 
     * <p>
     *   If the namespaces on the element change for any reason 
     *  (adding or removing an attribute in a namespace, adding 
     *   or removing a namespace declaration, changing the prefix 
     *   of an element, etc.) then then this method may skip or 
     *   repeat prefixes. Don't change the prefixes of an element  
     *   while iterating across them. 
     * </p>
     * 
     * @param index the prefix to return
     * 
     * @return the prefix
     * 
     * @throws IndexOutOfBoundsException if <code>index</code> is 
     *     negative or greater than or equal to the number of 
     *     namespaces declared by this element.
     * 
     */
    public final String getNamespacePrefix(int index) {
        
        SortedSet allPrefixes;
        if (namespaces != null) {
            allPrefixes = new TreeSet(namespaces.getPrefixes());
        } 
        else allPrefixes = new TreeSet();
        
        if (!("xml".equals(prefix))) allPrefixes.add(prefix);
        
        // add attribute prefixes
        for (int i = 0; i < getAttributeCount(); i++) {
            Attribute att = getAttribute(i);
            String attPrefix = att.getNamespacePrefix();
            if (attPrefix.length() != 0 && !("xml".equals(attPrefix))) {
                allPrefixes.add(attPrefix);    
            }
        }
        
        Iterator iterator = allPrefixes.iterator();
        try {
            for (int i = 0; i < index; i++) {
                iterator.next();   
            }
            return (String) iterator.next();
        }
        catch (NoSuchElementException ex) {
            throw new IndexOutOfBoundsException(
              "No " + index + "th namespace");   
        }
        
    }    

    
    /**
     * 
     * <p>
     * Sets the URI from which this element was loaded,
     * and against which relative URLs in this node will be resolved,
     * unless an <code>xml:base</code> attribute overrides this.
     * </p>
     * 
     * @param URI the new base URI for this node
     * 
     * @throws MalformedURIException if <code>URI</code> is 
     *     not a legal RFC 2396 URI
     */
    public void setBaseURI(String URI) { 
        setActualBaseURI(URI);       
    }
    
    
    /**
     * 
     * <p>
     * Returns the base URI against which relative URIs in this
     * element should be resolved.
     * <code>xml:base</code> attributes take precedence over the
     * actual base URI.
     * </p>
     * 
     * <p>
     * If possible, this URI is made absolute before it is returned 
     * by resolving the information in this element against the 
     * information in its parent element and document entity.
     * However, it is not always possible to fully absolutize the
     * URI in all circumstances. Sometimes, a relative URI may be 
     * returned instead. If the base URI cannot be determined,
     * this method returns null. 
     * </p>
     * 
     * @return the base URI of this element 
     * 
     * @see Node#getBaseURI()
     */
    public final String getBaseURI() {
        
        // Check to see if this element has an xml:base
        // attribute. If so, this takes precedence over
        // everything else.
        String actualBase = getActualBaseURI();
        ParentNode parent = getParent();
        Attribute baseAttribute = this.getAttribute("base", 
          "http://www.w3.org/XML/1998/namespace");

        // This element has an xml:base attribute
        if (baseAttribute != null) {
            String base = baseAttribute.getValue();
            // The base attribute contains an IRI, not a URI.
            // Thus the first thing we have to do is escape it
            // to convert illegal characters to hexadecimal escapes.
            base = toURI(base);  // ???? move this method to URIUtil
            
            if (!URIUtil.isAbsolute(base)) {
                // absolutize the URI if possible
                if (parent != null) {                   
                    String parentActualBase = parent.getActualBaseURI();
                    // can element be considered to come from same entity as its parent?
                    if (actualBase == null || actualBase.equals(parentActualBase)) {
                        try {
                            String parentBase = parent.getBaseURI();
                            if (!nu.xom.URIUtil.isOpaque(parentBase)) {
                                base = URIUtil.absolutize(parentBase, base);    
                            }
                        }
                        catch (MalformedURIException ex) {
                            // System.err.println("Oops " + base);
                            return null;
                        }
                    }
                    else {
                        try {
                            if (!URIUtil.isOpaque(actualBase)) {
                                // should the absolutize method be able to
                                // combine two relative bases????
                                base = URIUtil.absolutize(actualBase, base);    
                            } 
                        }
                        catch (MalformedURIException ex) {
                            // System.err.println("Oops " + base);
                            return null;
                        }
                    }
                }
            }
            
            try {
                Verifier.checkURI(base);
            }
            catch (MalformedURIException ex) {
                base = null;
            }
            
            return base;
            
        }
        
        // This element does not have an xml:base attribute
        // 2. If this node doesn't have a parent,
        //    then return its own base URI.
        if (parent == null) return actualBase;
               
        // 3. This element does not declare a base URI,
        //    so it uses its parent's base URI.
        if (actualBase == null) return parent.getBaseURI();      
               
        // 4. If the parent is loaded from the same entity,
        //    then the parent's xml:base attributes count.
        if (actualBase.equals(parent.getActualBaseURI())) {
            return parent.getBaseURI();      
        }
               
        // The parent is loaded from a different entity.
        // Therefore just return the actual base.
        return actualBase;    
        
    }
    

    private static String toURI(String iri) {
    
        StringBuffer uri = new StringBuffer(iri.length());
        int length = iri.length();
        for (int i = 0; i < length; i++) {
            char c = iri.charAt(i);
            switch(c) {
                case ' ':  
                    uri.append("%20");
                    break;
                case '!': 
                    uri.append(c);
                    break;
                case '"': 
                    uri.append("%22");
                    break;
                case '#':  
                    uri.append(c);
                    break;
                case '$':  
                    uri.append(c);
                    break;
                case '%':  
                    uri.append(c);
                    break;
                case '&':  
                    uri.append(c);
                    break;
                case '\'':  
                    uri.append(c);
                    break;
                case '(':  
                    uri.append(c);
                    break;
                case ')':  
                    uri.append(c);
                    break;
                case '*':  
                    uri.append(c);
                    break;
                case '+':  
                    uri.append(c);
                    break;
                case ',':  
                    uri.append(c);
                    break;
                case '-':  
                    uri.append(c);
                    break;
                case '.':  
                    uri.append(c);
                    break;
                case '/':  
                    uri.append(c);
                    break;
                case '0':  
                    uri.append(c);
                    break;
                case '1':  
                    uri.append(c);
                    break;
                case '2':  
                    uri.append(c);
                    break;
                case '3':  
                    uri.append(c);
                    break;
                case '4':  
                    uri.append(c);
                    break;
                case '5':  
                    uri.append(c);
                    break;
                case '6':  
                    uri.append(c);
                    break;
                case '7':  
                    uri.append(c);
                    break;
                case '8':  
                    uri.append(c);
                    break;
                case '9':  
                    uri.append(c);
                    break;
                case ':':  
                    uri.append(c);
                    break;
                case ';':  
                    uri.append(c);
                    break;
                case '<':  
                    uri.append("%3C");
                    break;
                case '=':  
                    uri.append(c);
                    break;
                case '>':  
                    uri.append("%3E");
                    break;
                case '?':  
                    uri.append(c);
                    break;
                case '@':  
                    uri.append(c);
                    break;
                case 'A':  
                    uri.append(c);
                    break;
                case 'B':  
                    uri.append(c);
                    break;
                case 'C':  
                    uri.append(c);
                    break;
                case 'D':  
                    uri.append(c);
                    break;
                case 'E':  
                    uri.append(c);
                    break;
                case 'F':  
                    uri.append(c);
                    break;
                case 'G':  
                    uri.append(c);
                    break;
                case 'H':  
                    uri.append(c);
                    break;
                case 'I':  
                    uri.append(c);
                    break;
                case 'J':  
                    uri.append(c);
                    break;
                case 'K':  
                    uri.append(c);
                    break;
                case 'L':  
                    uri.append(c);
                    break;
                case 'M':  
                    uri.append(c);
                    break;
                case 'N':  
                    uri.append(c);
                    break;
                case 'O':  
                    uri.append(c);
                    break;
                case 'P':  
                    uri.append(c);
                    break;
                case 'Q':  
                    uri.append(c);
                    break;
                case 'R':  
                    uri.append(c);
                    break;
                case 'S':  
                    uri.append(c);
                    break;
                case 'T':  
                    uri.append(c);
                    break;
                case 'U':  
                    uri.append(c);
                    break;
                case 'V':  
                    uri.append(c);
                    break;
                case 'W':  
                    uri.append(c);
                    break;
                case 'X':  
                    uri.append(c);
                    break;
                case 'Y':  
                    uri.append(c);
                    break;
                case 'Z':  
                    uri.append(c);
                    break;
                case '[':  
                    uri.append(c);
                    break;
                case '\\':  
                    uri.append("%5C");
                    break;
                case ']':  
                    uri.append(c);
                    break;
                case '^':  
                    uri.append("%5E");
                    break;
                case '_':  
                    uri.append(c);
                    break;
                case '`':  
                    uri.append("%60");
                    break;
                case 'a':  
                    uri.append(c);
                    break;
                case 'b':  
                    uri.append(c);
                    break;
                case 'c':  
                    uri.append(c);
                    break;
                case 'd':  
                    uri.append(c);
                    break;
                case 'e':  
                    uri.append(c);
                    break;
                case 'f':  
                    uri.append(c);
                    break;
                case 'g':  
                    uri.append(c);
                    break;
                case 'h':  
                    uri.append(c);
                    break;
                case 'i':  
                    uri.append(c);
                    break;
                case 'j':  
                    uri.append(c);
                    break;
                case 'k':  
                    uri.append(c);
                    break;
                case 'l':  
                    uri.append(c);
                    break;
                case 'm':  
                    uri.append(c);
                    break;
                case 'n':  
                    uri.append(c);
                    break;
                case 'o':  
                    uri.append(c);
                    break;
                case 'p':  
                    uri.append(c);
                    break;
                case 'q':  
                    uri.append(c);
                    break;
                case 'r':  
                    uri.append(c);
                    break;
                case 's':  
                    uri.append(c);
                    break;
                case 't':  
                    uri.append(c);
                    break;
                case 'u':  
                    uri.append(c);
                    break;
                case 'v':  
                    uri.append(c);
                    break;
                case 'w':  
                    uri.append(c);
                    break;
                case 'x':  
                    uri.append(c);
                    break;
                case 'y':  
                    uri.append(c);
                    break;
                case 'z':  
                    uri.append(c);
                    break;
                case '{':  
                    uri.append("%7B");
                    break;
                case '|':  
                    uri.append("%7C");
                    break;
                case '}':  
                    uri.append("%7D");
                    break;
                case '~':  
                    uri.append(c);
                    break;
                default: 
                    uri.append(percentEscape(c));
            }
        }    
        return uri.toString();
        
    }
    
    
    private static String percentEscape(char c) {
        
        StringBuffer result = new StringBuffer(3);
        String s = String.valueOf(c);
        try {
            byte[] data = s.getBytes("UTF8");
            for (int i = 0; i < data.length; i++) {
                result.append('%');
                String hex = Integer.toHexString(data[i]);
                result.append(hex.substring(hex.length()-2));
            }
            return result.toString();
        }
        catch (UnsupportedEncodingException ex) {
            throw new RuntimeException( 
              "Broken VM: does not recognize UTF-8 encoding");   
        }
        
    }


    /**
     * <p>
     * Returns a string containing the XML serialization of this 
     * element. This includes the element and all its attributes 
     * and descendants. However, it may not contain namespace 
     * declarations for namespaces inherited from ancestor elements.
     * </p>
     * 
     * @return the XML representation of this element
     * 
     * @see nu.xom.Node#toXML()
     * 
     */
/*    public final String toXML() {
    
        StringBuffer result = new StringBuffer();
        
        result.append("<");
        result.append(getQualifiedName());

        ParentNode parentNode = getParent();
        for (int i = 0; i < getNamespaceDeclarationCount(); i++) {
            String additionalPrefix = getNamespacePrefix(i);
            String uri = getNamespaceURI(additionalPrefix);
            if (parentNode != null && parentNode.isElement()) {
               Element parentElement = (Element) parentNode;   
               if (uri.equals(
                 parentElement.getNamespaceURI(additionalPrefix))) {
                      continue;
               } 
            }
            else if (uri.length() == 0) {
                continue; // no need to say xmlns=""   
            }
            
            result.append(" xmlns"); 
            if (additionalPrefix.length() > 0) {
                result.append(':'); 
                result.append(additionalPrefix); 
            }
            result.append("=\""); 
            result.append(uri);   
            result.append('"');
        }
        
        // attributes
        if (attributes != null) {
            for (int i = 0; i < attributes.size(); i++) {
                Attribute attribute = attributes.get(i);
                result.append(' ');
                result.append(attribute.toXML());   
            }       
        }
        // children
        if (this.getChildCount() > 0) {
            result.append('>');
            for (int i = 0; i < getChildCount(); i++) {
                result.append(getChild(i).toXML()); 
            }
            
            result.append("</");
            result.append(getQualifiedName());
            result.append(">");
        }
        else {
            result.append(" />");               
        }
        
        return result.toString();
        
    }*/
    
    // might optimize by caching index, as we're iterating through in order????
    public final String toXML() {
        
        StringBuffer result = new StringBuffer();
        Node current = this;
        boolean end = false;
        int index = -1;
        while (true) {
            
            if (!end && current.getChildCount() > 0) {
               writeStartTag((Element) current, result);
               current = current.getChild(0);
               index = 0;
            }
            else {
              if (end) {
                 writeEndTag((Element) current, result);
                 if (current == this) break;
              }
              else if (current.isElement()) {
                 writeStartTag((Element) current, result);
                 if (current == this) break;
              }
              else {
                  result.append(current.toXML());
              }
              end = false;
              ParentNode parent = current.getParent();
              if (parent.getChildCount() - 1 == index) {
                current = parent;
                if (current != this) {
                    parent = current.getParent();
                    index = parent.indexOf(current);
                }
                end = true;
              }
              else {
                 index++;
                 current = parent.getChild(index);
              }
              
            }

        }        

        return result.toString();
        
    }
    
    
    private static void writeStartTag(Element element, StringBuffer result) {
        
        result.append("<");
        result.append(element.getQualifiedName());

        ParentNode parentNode = element.getParent();
        for (int i = 0; i < element.getNamespaceDeclarationCount(); i++) {
            String additionalPrefix = element.getNamespacePrefix(i);
            String uri = element.getNamespaceURI(additionalPrefix);
            if (parentNode != null && parentNode.isElement()) {
               Element parentElement = (Element) parentNode;   
               if (uri.equals(
                 parentElement.getNamespaceURI(additionalPrefix))) {
                   continue;
               } 
            }
            else if (uri.length() == 0) {
                continue; // no need to say xmlns=""   
            }
            
            result.append(" xmlns"); 
            if (additionalPrefix.length() > 0) {
                result.append(':'); 
                result.append(additionalPrefix); 
            }
            result.append("=\""); 
            result.append(uri);   
            result.append('"');
        }
        
        // attributes
        if (element.attributes != null) {
            for (int i = 0; i < element.attributes.size(); i++) {
                Attribute attribute = element.attributes.get(i);
                result.append(' ');
                result.append(attribute.toXML());   
            }       
        }

        if (element.getChildCount() > 0) {
            result.append('>');
        }
        else {
            result.append(" />");               
        }
        
    }

    
    private static void writeEndTag(Element element, StringBuffer result) {
        result.append("</");
        result.append(element.getQualifiedName());
        result.append(">");
    }
    

    /**
     * <p>
     * Returns the value of the element as defined by XPath 1.0.
     * This is the complete PCDATA content of the element, without
     * any tags, comments, or processing instructions after all 
     * entity and character references have been resolved.
     * </p>
     * 
     * @return XPath string value of this element
     * 
     * @see nu.xom.Node#getValue()
     * 
     */
    public final String getValue() {

        // recursive algorithm limited to Java stack size
        /* for (int i = 0; i < getChildCount(); i++) {
           Node child = getChild(i);
           if (child.isText() || child.isElement()) {
               result.append(child.getValue());
           }
        } */
        // non-recursive algorithm
        if (this.getChildCount() == 0) return "";
        StringBuffer result = new StringBuffer();
        Node current = this.getChild(0);
        int index = 0;
        boolean end = false;
        while (true) {
            if (!end && current.getChildCount() > 0) {
               current = current.getChild(0);
               index = 0;
            }
            else {
                end = false;
                if (current.isText()) result.append(current.getValue());
                ParentNode parent = current.getParent();
                if (parent.getChildCount() - 1 == index) {
                    current = parent;
                    if (current == this) break;
                    index = current.getParent().indexOf(current);
                    end = true;
                }
                else {
                    index++;
                    current = parent.getChild(index);
                }
            }
        }        
        
        return result.toString();

    }

    /**
     * <p>
     * Creates a deep copy of this element with no parent,
     * that can be added to this document or a different one.
     * </p>
     * 
     * @return a deep copy of this element with no parent
     * 
     * @see nu.xom.Node#copy()
     */
    public Node copy() {
        return new Element(this);
    }

    
    /**
     * <p>
     * Returns a string representation of this element suitable
     * for debugging and diagnosis. This is <em>not</em>
     * the XML representation of the element.
     * </p>
     * 
     * @return a non-XML string representation of this element
     */
    public final String toString() {
        return 
          "[" + getClass().getName() + ": " + getQualifiedName() + "]";
    }

    
    boolean isElement() {
        return true;   
    } 

    
}