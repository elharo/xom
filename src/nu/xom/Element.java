/* Copyright 2002-2006, 2011, 2013, 2019 Elliotte Rusty Harold
   
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.LinkedHashSet;

/**
 * <p>
 * This class represents an XML element. Each
 * element has the following properties:
 * </p>
 *
 * <ul>
 * <li>Local name</li>
 * <li>Prefix (which may be null or the empty string) </li>
 * <li>Namespace URI (which may be null or the empty string) </li>
 * <li>A list of attributes</li>
 * <li>A list of namespace declarations for this element
 * (not including those inherited from its parent)</li>
 * <li>A list of child nodes</li>
 * </ul>
 *
 * @author Elliotte Rusty Harold
 * @version 1.3.1
 *
 */
public class Element extends ParentNode {

    private String localName;
    private String prefix;
    private String URI;

    private Attribute[] attributes = null;
    private int         numAttributes = 0;
            Namespaces  namespaces = null;

    /**
     * <p>
     * Creates a new element in no namespace.
     * </p>
     *
     * @param name the name of the element
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
     * @throws IllegalNameException if <code>name</code>
     *     is not a legal XML 1.0 name
     * @throws MalformedURIException if <code>uri</code>
     *     is not an RFC 3986 absolute URI reference
     * @throws NamespaceConflictException if <code>name</code>'s prefix
     *     cannot be used with <code>uri</code>
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
        _setNamespacePrefix(prefix);
        _setNamespaceURI(uri);
        try {
            _setLocalName(localName);
        }
        catch (IllegalNameException ex) {
            ex.setData(name);
            throw ex;
        }
        
    }

    
    private Element() {}

    
    static Element build(String name, String uri, String localName) {
        
        Element result = new Element();
        String prefix = "";
        int colon = name.indexOf(':');
        if (colon >= 0) {
            prefix = name.substring(0, colon);  
        }
        result.prefix = prefix;
        result.localName = localName;
        // We do need to verify the URI here because parsers are 
        // allowing relative URIs which XOM forbids, for reasons
        // of canonical XML if nothing else. But we only have to verify
        // that it's an absolute base URI. I don't have to verify 
        // no conflicts.
        if (! "".equals(uri)) Verifier.checkAbsoluteURIReference(uri);
        result.URI = uri;
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
            this.attributes = element.copyAttributes(this);
            this.numAttributes = element.numAttributes;
        } 
        
        this.actualBaseURI = element.findActualBaseURI();
        
        copyChildren(element, this);
        
    }
    

    private Attribute[] copyAttributes(Element newParent) {

        if (numAttributes == 0) return null;
        Attribute[] copy = new Attribute[numAttributes];
        for (int i = 0; i < numAttributes; i++) {
            copy[i] = (Attribute) attributes[i].copy();
            copy[i].setParent(newParent);
        }
        return copy;
        
    }
    
    
    private static Element copyTag(final Element source) {
        
        Element result = source.shallowCopy();
        
        // Attach additional namespaces
        if (source.namespaces != null) {
            result.namespaces = source.namespaces.copy();
        }
        
        // Attach clones of attributes
        if (source.attributes != null) {
            result.attributes = source.copyAttributes(result);
            result.numAttributes = source.numAttributes;
        } 
        
        result.actualBaseURI = source.findActualBaseURI();
        
        return result;
        
    }


    private static void copyChildren(final Element sourceElement, 
      Element resultElement) {
        
        if (sourceElement.getChildCount() == 0) return;
        ParentNode resultParent = resultElement;
        Node sourceCurrent = sourceElement;
        int index = 0;
        int[] indexes = new int[10];
        int top = 0;
        indexes[0] = 0;
        
        // true if processing the element for the 2nd time; 
        // i.e. the element's end-tag
        boolean endTag = false; 
        
        while (true) {
            if (!endTag && sourceCurrent.getChildCount() > 0) {
               sourceCurrent = sourceCurrent.getChild(0);
               index = 0;
               top++;
               indexes = grow(indexes, top);
               indexes[top] = 0;
            }
            else {
                endTag = false;
                ParentNode sourceParent = sourceCurrent.getParent(); 
                if (sourceParent.getChildCount() - 1 == index) {
                    sourceCurrent = sourceParent; 
                    top--;
                    if (sourceCurrent == sourceElement) break;
                    // switch parent up
                    resultParent = (Element) resultParent.getParent();
                    index = indexes[top];
                    endTag = true;
                    continue;
                }
                else {
                    index++;
                    indexes[top] = index;
                    sourceCurrent = sourceParent.getChild(index); 
                }
            }
            
            if (sourceCurrent.isElement()) {
                Element child = copyTag((Element) sourceCurrent);
                resultParent.appendChild(child); 
                if (sourceCurrent.getChildCount() > 0) { 
                    resultParent = child;
                }
            }
            else {
                Node child = sourceCurrent.copy();
                resultParent.appendChild(child);
            }
            
        } 
        
    }


    private static int[] grow(int[] indexes, int top) {
        
        if (top < indexes.length) return indexes;
        int[] result = new int[indexes.length*2];
        System.arraycopy(indexes, 0, result, 0, indexes.length);
        return result;
        
    }


    /**
     * <p>
     * Returns a list of the child elements of
     * this element with the specified name in no namespace.
     * The elements returned are in document order.
     * </p>
     *
     * @param name the name of the elements included in the list
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
     * The elements returned are in document order.
     * </p>
     *
     * @param localName the name of the elements included in the list
     * @param namespaceURI the namespace URI of the elements included
     *     in the list
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
     * of this element in document order.
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
     * @throws MultipleParentException if the attribute is already
     *      attached to an element
     * @throws NamespaceConflictException if the attribute's prefix
     *      is mapped to a different namespace URI than the same prefix
     *      is mapped to by this element, another attribute of 
     *      this element, or an additional namespace declaration
     *      of this element
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
            
        }
        
        if (attributes == null) attributes = new Attribute[1];
        checkPrefixConflict(attribute);
        
        // Is there already an attribute with this local name
        // and namespace? If so, remove it.
        Attribute oldAttribute = getAttribute(attribute.getLocalName(),
          attribute.getNamespaceURI());
        if (oldAttribute != null) remove(oldAttribute);
        
        add(attribute);
        attribute.setParent(this);
        
    }
    
    
    private void add(Attribute attribute) {

        if (numAttributes == attributes.length) {
            Attribute[] newAttributes = new Attribute[attributes.length * 2];
            System.arraycopy(attributes, 0, newAttributes, 0, numAttributes);
            this.attributes = newAttributes;
        }
        attributes[numAttributes] = attribute;
        numAttributes++;
        
    }


    private boolean remove(Attribute attribute) {

        int index = -1;
        for (int i = 0; i < attributes.length; i++) {
            if (attributes[i] == attribute) {
                index = i;
                break;
            }
        }
        
        if (index == -1) return false;
        
        int toCopy = numAttributes-index-1;
        if (toCopy > 0) {
            System.arraycopy(attributes, index+1, attributes, index, toCopy);
        }
        numAttributes--;
        attributes[numAttributes] = null;
        return true;
        
    }


    void fastAddAttribute(Attribute attribute) {
        if (attributes == null) attributes = new Attribute[1];
        add(attribute);
        attribute.setParent(this);
    }


    /**
     * <p>
     * Removes an attribute from this element.
     * </p>
     *
     * @param attribute the attribute to remove
     * @return the attribute that was removed
     * @throws NoSuchAttributeException if this element is not the
     *     parent of attribute
     *
     */
    public Attribute removeAttribute(Attribute attribute) {
        
        if (attributes == null) {
            throw new NoSuchAttributeException(
              "Tried to remove attribute "
              + attribute.getQualifiedName() 
              + " from non-parent element");
        }        
        if (attribute == null) {
            throw new NullPointerException(
              "Tried to remove null attribute");
        }        
        if (remove(attribute)) {
            attribute.setParent(null);
            return attribute;
        }
        else {
            throw new NoSuchAttributeException(
              "Tried to remove attribute "
              + attribute.getQualifiedName() 
              + " from non-parent element");            
        }
        
    }


    /**
     * <p>
     * Returns the attribute with the specified name in no namespace,
     * or null if this element does not have an attribute
     * with that name in no namespace.
     * </p>
     *
     * @param name the name of the attribute
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
     * @return the attribute of this element
     *     with the specified name and namespace
     */
    public final Attribute getAttribute(String localName,
      String namespaceURI) {
        
        if (attributes == null) return null;
        for (int i = 0; i < numAttributes; i++) {
            Attribute a = attributes[i];
            if (a.getLocalName().equals(localName) 
             && a.getNamespaceURI().equals(namespaceURI)) {
                return a;
            }   
        }
        
        return null;
        
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
     * @return the value of the attribute of this element
     *     with the specified name
     */
    public final String getAttributeValue(String name) {
        return getAttributeValue(name, "");
    } 

    
    /**
     * <p>
     * Returns the number of attributes of this <code>Element</code>,
     * not counting namespace declarations.
     * This is always a non-negative number.
     * </p>
     *
     * @return the number of attributes in the container
     */
    public final int getAttributeCount() {
        return numAttributes;
    }
    
    
    /**
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
     * attributes from the list while iterating across it.
     * Doing so will change the indexes of the other attributes in
     * the list. it is, however, safe to remove an attribute from
     * either end of the list (0 or <code>getAttributeCount()-1</code>)
     * until there are no attributes left.
     * </p>
     *
     * @param index the attribute to return
     * @return the index<sup>th</sup> attribute of this element
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
        return attributes[index]; 
        
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
     * @return the value of the attribute of this element
     *     with the specified name and namespace
     */
    public final String getAttributeValue(String localName, 
                                          String namespaceURI) {
        
        Attribute attribute = getAttribute(localName, namespaceURI);
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

    
    final String getLocalNamespaceURI(String prefix) {
        
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
            for (int i = 0; i < numAttributes; i++) {
                Attribute a = attributes[i];
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
     * @throws IllegalNameException if <code>localName</code> is not
     *     a legal, non-colonized name
     */
    public void setLocalName(String localName) {       
        _setLocalName(localName);
    }


    private void _setLocalName(String localName) {
        Verifier.checkNCName(localName);
        this.localName = localName;
    }


    /**
     * <p>
     * Sets the namespace URI of this element.
     * </p>
     *
     * @param uri the new namespace URI
     * @throws MalformedURIException if <code>uri</code>
     *     is not an absolute RFC 3986 URI reference
     * @throws NamespaceException if this element has a prefix
     *     and <code>uri</code> is null or the empty string;
     *     or if the element's prefix is shared by an attribute
     *     or additional namespace
     */
    public void setNamespaceURI(String uri) {
        _setNamespaceURI(uri);
    }

    
    private void _setNamespaceURI(String uri) {
        
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
            for (int i = 0; i < numAttributes; i++) {
                Attribute a = attributes[i];
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
     * @throws IllegalNameException if <code>prefix</code> is
     *     not a legal XML non-colonized name
     * @throws NamespaceConflictException if <code>prefix</code> is
     *     already in use by an attribute or additional
     *     namespace with a different URI than the element
     *     itself
     */
    public void setNamespacePrefix(String prefix) {
        _setNamespacePrefix(prefix);
    }


    private void _setNamespacePrefix(String prefix) {
        
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
     * @throws IllegalAddException if <code>child</code>
     *   is a <code>Document</code>
     * @throws IndexOutOfBoundsException if the position is negative
     *     or greater than the number of children of this element
     * @throws MultipleParentException if <code>child</code>
     *   already has a parent
     * @throws NullPointerException if <code>child</code> is null
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
            checkCycle(child, this);
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
    
    
    private static void checkCycle(Node child, ParentNode parent) {       
        
        if (child == parent) {
            throw new CycleException("Cannot add a node to itself");
        }
        if (child.getChildCount() == 0) return;
        while ((parent = parent.getParent()) != null) {
            if (parent == child) {
                throw new CycleException(
                  "Cannot add an ancestor as a child");
            }
        }
        
    }

    
    /**
     * <p>
     * Converts a string to a text node and inserts that
     * node at the specified position.
     * </p>
     *
     * @param position where to insert the child
     * @param text the string to convert to a text node and insert
     * @throws IndexOutOfBoundsException if the position is negative
     *     or greater than the number of children of the node
     * @throws NullPointerException if text is null
     */
    public void insertChild(String text, int position) {
        
       if (text == null) {
           throw new NullPointerException("Inserted null string");
       }
       super.fastInsertChild(new Text(text), position);
       
    } 


    /**
     * <p>
     * Converts a string to a text node
     * and appends that node to the children of this node.
     * </p>
     *
     * @param text string to add to this node
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
            if (child.isElement()) fillInBaseURI((Element) child);
            child.setParent(null);
            result.append(child);
        }   
        this.children = null;
        this.childCount = 0;
        
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
     * You can supply an empty string for the prefix to declare a
     * default namespace, provided the element itself has a prefix.
     * </p>
     *
     * <p>
     * If you do redeclare a prefix that is already used
     * by an element or attribute name, the additional
     * namespace is added if and only if the URI is the same.
     * Conflicting namespace declarations will throw an exception.
     * </p>
     *
     * @param prefix the prefix to declare
     * @param uri the absolute URI reference to map the prefix to
     * @throws IllegalNameException  if <code>prefix</code> is not
     *      a legal XML non-colonized name or the empty string
     * @throws MalformedURIException if <code>URI</code>
     *      is not an RFC 3986 URI reference
     * @throws NamespaceConflictException if the mapping conflicts
     *     with an existing element, attribute,
     *     or additional namespace declaration
     */
    public void addNamespaceDeclaration(String prefix, String uri) {

        if (prefix == null) prefix = "";
        if (uri == null) uri = "";
        
        // check to see if this is the xmlns or xml prefix
        if (prefix.equals("xmlns")) {
            if (uri.equals("")) {
                // This is done automatically
                return; 
            }
            throw new NamespaceConflictException(
             "The xmlns prefix cannot bound to any URI");   
        }
        else if (prefix.equals("xml")) {
            if (uri.equals("http://www.w3.org/XML/1998/namespace")) {
                // This is done automatically
                return; 
            }
            throw new NamespaceConflictException(
              "Wrong namespace URI for xml prefix: " + uri);
        }
        else if (uri.equals("http://www.w3.org/XML/1998/namespace")) {
            throw new NamespaceConflictException(
               "Wrong prefix for http://www.w3.org/XML/1998/namespace namespace: " 
               + prefix);  
        }
        
        if (prefix.length() != 0) {
            Verifier.checkNCName(prefix);
            Verifier.checkAbsoluteURIReference(uri);
        }
        else if (uri.length() != 0) {
            // Make sure we're not trying to undeclare 
            // the default namespace; this is legal.
            Verifier.checkAbsoluteURIReference(uri);
        }
        
        String currentBinding = getLocalNamespaceURI(prefix);
        if (currentBinding != null && !currentBinding.equals(uri)) {
            
            String message;
            if (prefix.equals("")) {
                message =  "Additional namespace " + uri 
                  + " conflicts with existing default namespace "
                  + currentBinding;
            }
            else {
                message = "Additional namespace " + uri 
                  + " for the prefix " + prefix 
                  + " conflicts with existing namespace binding "
                  + currentBinding;
            }
            throw new NamespaceConflictException(message);   
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
     * once; and the <code>xml</code> prefix used for
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
        
        // This seems to be a hot spot for DOM conversion.
        // I'm trying to avoid the overhead of creating and adding
        // to a HashSet for the simplest case of an element, none 
        // of whose attributes are in namespaces, and which has no
        // additional namespace declarations. In this case, the 
        // namespace count is exactly one, which is here indicated
        // by a null prefix set.
        Set<String> allPrefixes = null;
        if (namespaces != null) {
            allPrefixes = new HashSet<String>(namespaces.getPrefixes());
            allPrefixes.add(prefix);
        } 
        if ("xml".equals(prefix)) allPrefixes = new HashSet<String>();
        // add attribute prefixes
        int count = getAttributeCount();
        for (int i = 0; i < count; i++) {
            Attribute att = attributes[i];
            String attPrefix = att.getNamespacePrefix();
            if (attPrefix.length() != 0 && !"xml".equals(attPrefix)) {
                if (allPrefixes == null) {
                    allPrefixes = new HashSet<String>();
                    allPrefixes.add(prefix);
                }
                allPrefixes.add(attPrefix);    
            }
        }
        
        if (allPrefixes == null) return 1; 
        return allPrefixes.size();
        
    }
    
    
    // Used for XPath and serialization
    Map<String, String> getNamespacePrefixesInScope() {
        
        HashMap<String, String> namespaces = new HashMap<String, String>();
        
        Element current = this;
        while (true) {
            
            if (!("xml".equals(current.prefix))) {
                addPrefixIfNotAlreadyPresent(namespaces, current, current.prefix);
            }
            
            // add attribute prefixes
            if (current.attributes != null) {
                int count = current.numAttributes;
                for (int i = 0; i < count; i++) {
                    Attribute att = current.attributes[i];
                    String attPrefix = att.getNamespacePrefix();
                    if (attPrefix.length() != 0 && !("xml".equals(attPrefix))) {
                        addPrefixIfNotAlreadyPresent(namespaces, current, attPrefix);
                    }
                }
            }
            
            // add additional namespace prefixes
            // ???? should add more methods to Namespaces to avoid access to private data
            if (current.namespaces != null) {
                int namespaceCount = current.namespaces.size();
                for (int i = 0; i < namespaceCount; i++) {
                    String nsPrefix = current.namespaces.getPrefix(i);
                    addPrefixIfNotAlreadyPresent(namespaces, current, nsPrefix);
                }
            }
            
            ParentNode parent = current.getParent();
            if (parent == null || parent.isDocument() || parent.isDocumentFragment()) {
                break;
            }
            current = (Element) parent;
        }

        return namespaces;
        
    }


    private void addPrefixIfNotAlreadyPresent(HashMap<String, String> namespaces, Element current, String prefix) {
        if (!namespaces.containsKey(prefix)) {
            namespaces.put(prefix, current.getLocalNamespaceURI(prefix));
        }
    }

    
    /**
     * <p>
     * Returns the index<sup>th</sup> namespace prefix <em>declared</em> on
     * this element. Namespaces inherited from ancestors are not included.
     * The index is purely for convenience, and has no
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
     * If the namespaces on the element change for any reason
     * (adding or removing an attribute in a namespace, adding
     * or removing a namespace declaration, changing the prefix
     * of an element, etc.) then this method may skip or repeat
     * prefixes. Don't change the prefixes of an element while
     * iterating across them.
     * </p>
     *
     * @param index the prefix to return
     * @return the prefix
     * @throws IndexOutOfBoundsException if <code>index</code> is
     *     negative or greater than or equal to the number of 
     *     namespaces declared by this element
     *
     */
    public final String getNamespacePrefix(int index) {
        
        // XXX can I reuse set? or not use a Set at all?
        // suppose the zeroth prefix is the namespace prefix is the first prefix
        // found for the element, attributes, and namespace declarations, in that order.
        // The second prefix is the next and so on.
        
        if (index < 0) {
            throw new IndexOutOfBoundsException(
              "Negative prefix number " + index);
        }
        else if (index == 0) {
            if (!("xml".equals(prefix))) return prefix;
        }
        
        
        Set<String> allPrefixes = getNamespacePrefixes();
        Iterator<String> iterator = allPrefixes.iterator();
        try {
            for (int i = 0; i < index; i++) {
                iterator.next();   
            }
            return iterator.next();
        }
        catch (NoSuchElementException ex) {
            throw new IndexOutOfBoundsException(
              // ???? fix to use 3rd, 2nd, 1st as appropriate
              "No " + index + "th namespace");   
        }
        
    }


    private Set<String> getNamespacePrefixes() {

        Set<String> allPrefixes = new LinkedHashSet<String>();
        if (!("xml".equals(prefix))) allPrefixes.add(prefix);
        if (namespaces != null) {
            allPrefixes.addAll(namespaces.getPrefixes());
        }
        
        if (attributes != null) {
            int count = getAttributeCount();
            for (int i = 0; i < count; i++) {
                Attribute att = attributes[i];
                String attPrefix = att.getNamespacePrefix();
                if (attPrefix.length() != 0 && !("xml".equals(attPrefix))) {
                    allPrefixes.add(attPrefix);    
                }
            }
        }
        return allPrefixes;
        
    } 
    
    
    /**
     * <p>
     * Sets the URI from which this element was loaded,
     * and against which relative URLs in this element will be
     * resolved, unless an <code>xml:base</code> attribute overrides
     * this. Setting the base URI to null or the empty string removes
     * any existing base URI.
     * </p>
     *
     * @param URI the new base URI for this element
     * @throws MalformedURIException if <code>URI</code> is
     *     not a legal RFC 3986 absolute URI
     */
    public void setBaseURI(String URI) { 
        setActualBaseURI(URI);       
    }
    
    
    /**
     * <p>
     * Returns the absolute base URI against which relative URIs in
     * this element should be resolved. <code>xml:base</code>
     * attributes <em>in the same entity</em> take precedence over the
     * actual base URI of the document where the element was found
     * or which was set by <code>setBaseURI</code>.
     * </p>
     *
     * <p>
     * This URI is made absolute before it is returned
     * by resolving the information in this element against the
     * information in its parent element and document entity.
     * However, it is not always possible to fully absolutize the
     * URI in all circumstances. In this case, this method returns the
     * empty string to indicate the base URI of the current entity.
     * </p>
     *
     * <p>
     * If the element's <code>xml:base</code> attribute contains a
     * value that is a syntactically illegal URI (e.g. %GF.html"),
     * then the base URI is application dependent. XOM's choice is
     * to behave as if the element did not have an <code>xml:base</code>
     * attribute.
     * </p>
     *
     * @return the base URI of this element
     */
     public String getBaseURI() {

        String baseURI = "";
        String sourceEntity = this.getActualBaseURI();
        
        ParentNode current = this;
        
        while (true) {
            String currentEntity = current.getActualBaseURI();
            if (sourceEntity.length() != 0 
              && ! sourceEntity.equals(currentEntity)) {
                baseURI = URIUtil.absolutize(sourceEntity, baseURI);
                break;
            }
            
            if (current.isDocument()) {
                baseURI = URIUtil.absolutize(currentEntity, baseURI);
                break;
            }
            Attribute baseAttribute = ((Element) current).getAttribute("base", 
              "http://www.w3.org/XML/1998/namespace");
            if (baseAttribute != null) {
                String baseIRI = baseAttribute.getValue();
                // The base attribute contains an IRI, not a URI.
                // Thus the first thing we have to do is escape it
                // to convert illegal characters to hexadecimal escapes.
                String base = URIUtil.toURI(baseIRI);
                if ("".equals(base)) {
                    baseURI = getEntityURI();
                    break;
                }
                else if (legalURI(base)) {
                    if ("".equals(baseURI)) baseURI = base;
                    else if (URIUtil.isOpaque(base)) break; 
                    else baseURI = URIUtil.absolutize(base, baseURI);
                    if (URIUtil.isAbsolute(base)) break;  // ???? base or baseURI
                }
            }
            current = current.getParent();
            if (current == null) {
                baseURI = URIUtil.absolutize(currentEntity, baseURI);
                break;
            }
        }
        
        if (URIUtil.isAbsolute(baseURI)) return baseURI;
        return "";
            
    }
    
    
    private String getEntityURI() {
     
        ParentNode current = this;
        while (current != null) {
            if (current.actualBaseURI != null 
              && current.actualBaseURI.length() != 0) {
                return current.actualBaseURI;
            }
            current = current.getParent();
        }
        return "";
        
    }
        
    
    private boolean legalURI(String base) {
        
        try {
            Verifier.checkURIReference(base);
            return true;
        }
        catch (MalformedURIException ex) {
            return false;
        }
        
    }


    /**
     * <p>
     * Returns a string containing the XML serialization of this
     * element. This includes the element and all its attributes
     * and descendants. However, it does not contain namespace
     * declarations for namespaces inherited from ancestor elements.
     * </p>
     *
     * @return the XML representation of this element
     *
     */
    public final String toXML() {
        
    	StringBuilder result = new StringBuilder(1024);
        Node current = this;
        boolean endTag = false;
        int index = -1;
        int[] indexes = new int[10];
        int top = 0;
        indexes[0] = -1;
        
        while (true) {
            
            if (!endTag && current.getChildCount() > 0) {
               writeStartTag((Element) current, result);
               current = current.getChild(0);
               index = 0;
               top++;
               indexes = grow(indexes, top);
               indexes[top] = 0;
            }
            else {
              if (endTag) {
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
              endTag = false;
              ParentNode parent = current.getParent();
              if (parent.getChildCount() - 1 == index) {
                current = parent;
                top--;
                if (current != this) {
                    index = indexes[top];
                }
                endTag = true;
              }
              else {
                 index++;
                 indexes[top] = index;
                 current = parent.getChild(index);
              }
              
            }

        }        

        return result.toString();
        
    }
    
    
    private static void writeStartTag(Element element, StringBuilder result) {
        
        result.append('<');
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
            result.append(escape(uri));   
            result.append('"');
        }
        
        // attributes
        if (element.attributes != null) {
            for (int i = 0; i < element.numAttributes; i++) {
                Attribute attribute = element.attributes[i];
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

    
    private static String escape(String s) {
        
        int length = s.length();
        // Give the string buffer enough room for a couple of escaped characters 
        StringBuilder result = new StringBuilder(length+12);
        for (int i = 0; i < length; i++) {
            char c = s.charAt(i);
            if (c == '&') result.append("&amp;");
            else result.append(c);
        }
        return result.toString();
        
    }


    private static void writeEndTag(
      Element element, StringBuilder result) {
        
        result.append("</");
        result.append(element.getQualifiedName());
        result.append('>');
        
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
     */
    public final String getValue() {

        // non-recursive algorithm avoids stack size limitations
        int childCount = this.getChildCount();
        if (childCount == 0) return "";

        Node current = this.getChild(0);
        // optimization for common case where element 
        // has a single text node child
        if (childCount == 1 && current.isText()) {
            return current.getValue();
        }   
        
        StringBuilder result = new StringBuilder();
        int index = 0;
        int[] indexes = new int[10];
        int top = 0;
        indexes[0] = 0;
        
        boolean endTag = false;
        while (true) {
            if (!endTag && current.getChildCount() > 0) {
               current = current.getChild(0);
               index = 0;
               top++;
               indexes = grow(indexes, top);
               indexes[top] = 0;            }
            else {
                endTag = false;
                if (current.isText()) result.append(current.getValue());
                ParentNode parent = current.getParent();
                if (parent.getChildCount() - 1 == index) {
                    current = parent;
                    top--;
                    if (current == this) break;
                    index = indexes[top];
                    endTag = true;
                }
                else {
                    index++;
                    indexes[top] = index;
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
     * <p>
     * Subclassers should be wary. Implementing this method is trickier
     * than it might seem, especially if you wish to avoid potential
     * stack overflows in deep documents. In particular, you should not
     * rely on the obvious recursive algorithm. Most subclasses should
     * override the {@link nu.xom.Element#shallowCopy() shallowCopy}
     * method instead.
     * </p>
     *
     * @return a deep copy of this element with no parent
     */
    public Element copy() {
        Element result = copyTag(this);
        copyChildren(this, result);
        return result;
    }
    
    
    /**
     * <p>
     * Creates a very shallow copy of the element with the same name
     * and namespace URI, but no children, attributes, base URI, or
     * namespace declaration. This method is invoked as necessary
     * by the {@link nu.xom.Element#copy() copy} method
     * and the {@link nu.xom.Element#Element(nu.xom.Element)
     * copy constructor}.
     * </p>
     *
     * <p>
     * Subclasses should override this method so that it
     * returns an instance of the subclass so that types
     * are preserved when copying. This method should not add any
     * attributes, namespace declarations, or children to the
     * shallow copy. Any such items will be overwritten.
     * </p>
     *
     * @return an empty element with the same name and
     *     namespace as this element
     */
    protected Element shallowCopy() {      
        return new Element(getQualifiedName(), getNamespaceURI());
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
    public String toString() {
        return 
          "[" + getClass().getName() + ": " + getQualifiedName() + "]";
    }

    
    boolean isElement() {
        return true;   
    } 
    
    
    private void checkPrefixConflict(Attribute attribute) {
        
        String prefix = attribute.getNamespacePrefix();
        String namespaceURI = attribute.getNamespaceURI();
        
        // Look for conflicts
        for (int i = 0; i < numAttributes; i++) {
            Attribute a = attributes[i];
            if (a.getNamespacePrefix().equals(prefix)) {
              if (a.getNamespaceURI().equals(namespaceURI)) return;
              throw new NamespaceConflictException(
                "Prefix of " + attribute.getQualifiedName() 
                + " conflicts with " + a.getQualifiedName());
            }   
        }
        
    }


    private class AttributeIterator implements Iterator<Attribute> {
      
      private int next = 0;
      
      public boolean hasNext() {
          return next < numAttributes;
      }

      public Attribute next() throws NoSuchElementException {
          
          if (hasNext()) {
              Attribute a = attributes[next];
              next++;
              return a;
          }
          throw new NoSuchElementException("No such attribute");
          
      }

      public void remove() {
          throw new UnsupportedOperationException();
      }
    }
    
    Iterator<Attribute> attributeIterator() {

        return new AttributeIterator();
    }

    
}
