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

/**
 * <p>
 * This class represents an attribute such as 
 * <code>type="empty"</code> or 
 * <code>xlink:href="http://www.example.com"</code>.
 * </p>
 * 
 * <p>
 * Attributes that declare namespaces such as
 * <code>xmlns="http://www.w3.org/TR/1999/xhtml"</code>
 * or <code>xmlns:xlink="http://www.w3.org/TR/1999/xlink"</code>
 * are stored separately on the elements where they
 * appear. They are never represented as <code>Attribute</code>
 * objects.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0a1
 * 
 */
public class Attribute extends Node {
    
    private String localName;
    private String prefix;
    private String URI;
    private String value = "";
    private Type   type;

    
    /**
     * <p>
     * Creates a new attribute in no namespace with the
     * specified name and value and undeclared type.
     * </p>
     * 
     * @param localName the unprefixed attribute name
     * @param value the attribute value
     * 
     * @throws IllegalNameException if the local name is not 
     *     a namespace well-formed, non-colonized name
     * @throws IllegalDataException if the value contains characters  
     *     which are not legal in XML such as vertical tab or a null.
     *     Characters such as " and &amp; are legal, but will be  
     *     automatically escaped when the attribute is serialized.
     */
    public Attribute(String localName, String value) {
        this(localName, "", value, Type.UNDECLARED);
    }

    
    /**
     * <p>
     * Creates a new attribute in no namespace with the
     * specified name, value, and type.
     * </p>
     * 
     * @param localName the unprefixed attribute name
     * @param value the attribute value
     * @param type the attribute type
     * 
     * @throws IllegalNameException if the local name is 
     *     not a namespace well-formed non-colonized name
     * @throws IllegalDataException if the value contains 
     *     characters which are not legal in
     *     XML such as vertical tab or a null. Note that 
     *     characters such as " and &amp; are legal,
     *     but will be automatically escaped when the 
     *     attribute is serialized.
     */
    public Attribute(String localName, String value, Type type) {
        this(localName, "", value, type);
    }

    
    /**
     * <p>
     * Creates a new attribute in the specified namespace with the
     * specified name and value and undeclared type.
     * </p>
     * 
     * @param name the prefixed attribute name
     * @param URI the namespace URI
     * @param value the attribute value
     * 
     * @throws IllegalNameException  if the name is not a namespace 
     *     well-formed prefixed name
     * @throws IllegalDataException if the value contains characters 
     *     which are not legal in XML such as vertical tab or a null. 
     *     Note that characters such as " and &amp; are legal, but will
     *     be automatically escaped when the attribute is serialized.
     * @throws MalformedURIException if <code>URI</code> is not 
     *     an RFC2396 URI reference
     */
    public Attribute(String name, String URI, String value) {
        this(name, URI, value, Type.UNDECLARED);
    }

    
    /**
     * <p>
     * Creates a new attribute in the specified namespace with the
     * specified name, namespace, value, and type.
     * </p>
     * 
     * @param name  the prefixed attribute name
     * @param URI the namespace URI
     * @param value the attribute value
     * @param type the attribute type
     * 
     * @throws IllegalNameException if the name is not a namespace 
     *     well-formed prefixed name
     * @throws IllegalDataException if the value contains 
     *     characters which are not legal in XML such as 
     *     vertical tab or a null. Note that characters such as 
     *     " and &amp; are legal, but will be automatically escaped 
     *     when the attribute is serialized.
     * @throws MalformedURIException if <code>URI</code> is not 
     *     an RFC2396 absolute URI reference
     */
    public Attribute(
      String name, String URI, String value, Type type) {

        prefix = "";
        String localName = name;
        // Warning: do not cache the value returned by indexOf here
        // and in build
        // without profiling. My current tests show doing this slows 
        // down the parse by about 7%. I can't explain it.
        if (name.indexOf(':') > 0) {
            prefix = name.substring(0, name.indexOf(':'));   
            localName = name.substring(name.indexOf(':') + 1);
        }

        try {
            setLocalName(localName);
        }
        catch (IllegalNameException ex) {
            ex.setData(name);
            throw ex;
        }
        setNamespace(prefix, URI);
        setValue(value);
        setType(type);
        
    }

    
    /**
     * <p>
     * Creates a copy of the specified attribute.
     * </p>
     * 
     * @param attribute the attribute to copy
     * 
     */
    public Attribute(Attribute attribute) {
        
        // These are all immutable types
        this.localName = attribute.localName;
        this.prefix    = attribute.prefix;
        this.URI       = attribute.URI;
        this.value     = attribute.value;
        this.type      = attribute.type;
        
    }

    
    private Attribute() {}
    
    static Attribute build(
      String name, String URI, String value, Type type) {
        
        Attribute result = new Attribute();
    
        String prefix = "";
        String localName = name;
        if (name.indexOf(':') >= 0) {
            prefix = name.substring(0, name.indexOf(':'));   
            localName = name.substring(name.indexOf(':') + 1);
        }        
        
        result.localName = localName;
        result.prefix = prefix;
        result.type = type;
        result.URI = URI;
        result.value = value;
        
        return result;
    }


    /**
     * <p>
     * Returns the DTD type of this attribute. 
     * If this attribute does not have a type, then
     * <code>Type.UNDECLARED</code> is returned.
     * </p>
     * 
     * @return the DTD type of this attribute
     */
    public final Type getType() {
        return type;
    }

    
    /**
     * <p>
     * Sets the type of this attribute to one of the ten
     * DTD types or <code>Type.UNDECLARED</code>. 
     * </p>
     * 
     * @param type the DTD type of this attribute
     */
    public final void setType(Type type) {
        checkType(type);
        this.type = type;
    }

    
    /**
     * <p>
     * This method is called before the specified 
     * type is assigned to this attribute. By default it does nothing.
     * However, subclasses can override it and prevent certain
     * types from being assigned to the attribute 
     * by throwing an <code>XMLException</code>.
     * </p>
     * 
     * @param type the DTD type of this attribute
     * 
     * @throws XMLException if the subclass rejects the requested type.
     */
    protected void checkType(Type type) {}

    
    /**
     * <p>
     * Returns the attribute value. If the attribute was
     * originally created by a parser, it will have been
     * normalized according to its type.
     * However, attributes created in memory are not normalized.
     * </p>
     * 
     * @return the value of the attribute
     * @see nu.xom.Node#getValue()
     * 
     */
    public final String getValue() {
        return value;
    }

    
    /**
     * <p>
     * Sets the attribute's value to the specified string,
     * replacing any previous value. The value is not normalized
     * automatically.
     * </p>
     * 
     * @param value the value assigned to the attribute
     * @throws IllegalDataException if the value contains characters 
     *     which are not legal in XML such as vertical tab or a null. 
     *     Characters such as " and &amp; are legal, but will be 
     *     automatically escaped when the attribute is serialized.
     * @throws MalformedURIException if this is an 
     *     <code>xml:base</code> attribute, and the value is not a
     *     legal IRI
     */
    public final void setValue(String value) {
        Verifier.checkPCDATA(value);
        checkValue(value);
        this.value = value;
    }

    
    /**
     * <p>
     * Subclasses can override this method to check the value for 
     * an attribute before it's created or its value is changed.
     * </p>
     * 
     * @param value the proposed attribute value
     * 
     * @throws XMLException if <code>value</code> does not satisfy the 
     *     local constraints
     * 
     */
    protected void checkValue(String value) {}


    /**
     * <p>
     * Returns the local name of this attribute,
     * not including the prefix.
     * </p>
     * 
     * @return the attribute's local name
     */
    public final String getLocalName() {
        return localName;
    }

    
    /**
     * <p>
     * Sets the local name of the attribute.
     * </p>
     * 
     * @param localName the new local name
     * 
     * @throws IllegalNameException if <code>localName</code>
     *      is not a namespace well-formed, non-colonized name
     * 
     */
    public final void setLocalName(String localName) {
        Verifier.checkNCName(localName);
        if (localName.equals("xmlns")) {
            throw new IllegalNameException("The Attribute class is not"
              + " used for namespace declaration attributes.");
        }
        checkLocalName(localName);
        this.localName = localName;
    }   
    
    
    /**
     * <p>
     * This method is called before the specified 
     * local name is assigned to this attribute.
     * By default it does nothing. However,
     * subclasses can override it and prevent certain
     * local names from being assigned to the attribute 
     * by throwing an <code>XMLException</code>.
     * </p>
     * 
     * @param localName the proposed local name of the attribute
     * 
     * @throws XMLException if the subclass rejects the name.
     */
    protected void checkLocalName(String localName) {}


    /**
     * <p>
     * Returns the qualified name of this attribute,
     * including the prefix if this attribute is in a namespace.
     * </p>
     * 
     * @return the attribute's qualified name
     */
    public final String getQualifiedName() {
        if (prefix.length() == 0) return localName;
        else return prefix + ":" + localName;
    }
    
    
    /**
     * <p>
     * Returns the namespace URI of this attribute, or the empty string
     * if this attribute is not in a namespace.
     * </p>
     * 
     * @return the attribute's namespace URI
     */ 
    public final String getNamespaceURI() {
        return URI;
    }

    
    /**
     * <p>
     * Returns the prefix of this attribute,
     * or the empty string if this attribute 
     * is not in a namespace.
     * </p>
     * 
     * @return the attribute's prefix
     */
    public final String getNamespacePrefix() {
        return prefix;
    }

    
    /**
     * <p>
     * Sets the attribute's namespace prefix and URI.
     * Because attributes must be prefixed in order to have a  
     * namespace URI (and vice versa) this must be done 
     * simultaneously.
     * </p>
     * 
     * @param prefix the new namespace prefix
     * @param URI the new namespace URI
     * 
     * @throws MalformedURIException if <code>URI</code> is 
     *     not an RFC2396 URI reference
     * @throws IllegalNameException if
     *  <ul>
     *      <li>The prefix is <code>xmlns</code></li>
     *      <li>The prefix is null or the empty string.</li>
     *      <li>The URI is null or the empty string.</li>
     * </ul>
     * @throws NamespaceConflictException if
     *  <ul>
     *      <li>The prefix is <code>xml</code> and the namespace URI is
     *          not <code>http://www.w3.org/XML/1998/namespace</code></li>
     *      <li>The prefix conflicts with an existing declaration
     *          on the attribute's parent element.</li>
     * </ul>
     */
    public final void setNamespace(String prefix, String URI) {
        
        if (URI == null) URI = "";
        if (prefix == null) prefix = "";
        
        if (prefix.equals("xmlns")) {
            throw new IllegalNameException(
              "Attribute objects are not used to represent "
              + " namespace declarations"); 
        }
        else if (prefix.equals("xml") 
          && !(URI.equals("http://www.w3.org/XML/1998/namespace"))) {
            throw new NamespaceConflictException(
              "Wrong namespace URI for xml prefix: " + URI); 
        }
        else if (URI.equals("http://www.w3.org/XML/1998/namespace")
          && !prefix.equals("xml")) {
            throw new NamespaceConflictException(
              "Wrong prefix for the XML namespace: " + prefix); 
        }
        else if (prefix.length() == 0) {
            if (URI.length() == 0) {
                this.prefix = "";
                this.URI = "";
                return; 
            }
            else {
                throw new NamespaceConflictException(
                  "Prefixed attributes cannot be in a default namespace.");
            }
        }
        else if (URI.length() == 0) {
            throw new NamespaceConflictException(
             "Attribute prefixes must be declared.");
        }
        
        ParentNode parent = this.getParent();
        if (parent != null) {
           // test for namespace conflicts 
           Element element = (Element) parent;
           String  currentURI = element.getLocalNamespaceURI(prefix);
           if (currentURI != null && !currentURI.equals(URI)) {
                throw new NamespaceConflictException(
                  "New prefix " + prefix 
                  + "conflicts with existing namespace declaration"
                );
           } 
        }
        
        
        Verifier.checkAbsoluteURIReference(URI);
        Verifier.checkNCName(prefix);
        checkNamespace(prefix, URI);
        
        this.URI = URI;
        this.prefix = prefix;
        
    }

    
    /**
     * <p>
     * This method is called before the specified 
     * prefix and URI are assigned to this attribute.
     * By default it does nothing. However,
     * subclasses can override it and prevent certain
     * namespace URIs from being assigned to the attribute 
     * by throwing an <code>XMLException</code>.
     * </p>
     * 
     * @param prefix the proposed namespace prefix for this attribute
     * @param URI the proposed namespace URI for this attribute
     * 
     * @throws XMLException if the subclass rejects the name.
     */
    protected void checkNamespace(String prefix, String URI) {}

    
    /**
     * <p>
     * Returns false because attributes do not have children.
     * </p>
     * 
     * @return false
     * @see nu.xom.Node#hasChildren()
     */
    public final boolean hasChildren() {
        return false;   
    }
    
    
    /**
     * <p>
     *  Throws <code>IndexOutOfBoundsException</code>
     *  because attribute do not have children.
     * </p>
     *
     * @param position the child to return
     *
     * @return nothing. This method always throws an exception.
     *
     * @throws IndexOutOfBoundsException because attributes do 
     *     not have children
     */
    public final Node getChild(int position) {
        throw new IndexOutOfBoundsException(
          "Attributes do not have children"
        );        
    }

    
    /**
     * <p>
     * Returns 0 because attributes do not have children.
     * </p>
     * 
     * @return zero
     * @see nu.xom.Node#getChildCount()
     */
    public final int getChildCount() {
        return 0;   
    }

    
    /**
     * <p>
     * Creates a deep copy of this attribute that   
     * is not attached to an element.
     * </p>
     * 
     * @return a copy of this <code>Attribute</code>
     * 
     */ 
    public Node copy() {
        return new Attribute(this);
    }

    
    /**
     * <p>
     * Returns a string representation of the attribute 
     * that is a well-formed XML attribute. 
     * </p>
     * 
     * @return a string containing the XML form of this attribute
     *
     * @see nu.xom.Node#toXML()
     */
    public final String toXML() {
        return getQualifiedName() + "=\"" 
         + escapeText(value) + "\"";    
    }

    
    /**
     * <p>
     * Returns a string representation of the attribute suitable for 
     * debugging and diagnosis. However, this is not necessarily 
     * a well-formed XML attribute.
     * </p>
     * 
     *  @return a non-XML string representation of this attribute
     *
     * @see java.lang.Object#toString()
     */
    public final String toString() {
        return "[" + getClass().getName() + ": " 
         + getQualifiedName() + "=\"" + getValue() + "\"]";
    }

    
    private static String escapeText(String s) {
        
        StringBuffer result = new StringBuffer(s.length());
        for (int i = 0; i < s.length(); i++) {
            escapeChar(result, s.charAt(i));
        }
        return result.toString();
        
    }
    
    
    private static void escapeChar(StringBuffer result, char c) {
        
        switch (c) {
            case '<':
                result.append("&lt;");
                break;
            case '>':
                result.append("&gt;");
                break;
            case '&':
                result.append("&amp;");
                break;
            case '"':
                result.append("&quot;");
                break;
            case '\'':
                result.append("&apos;");
                break;
            case '\n':
                result.append("&#x0A;");
                break;
            case '\r':
                result.append("&#x0D;");
                break;
            case '\t':
                result.append("&#x09;");
                break;
            default: 
                result.append(c);   
        }
        
    }
 
    
    boolean isAttribute() {
        return true;   
    } 
    
    
    /**
     * <p>
     * Uses the type-safe enumeration 
     * design pattern to represent attribute types,
     * as used in XML DTDs. 
     * </p>
     * 
     * <p>
     *   XOM enforces well-formedness, but it does not enforce 
     *   validity. Thus it is possible fpr a single element to have 
     *   multiple ID type attributest, or ID type attributes 
     *   on different elements to have the same value, 
     *   or NMTOKEN type attributes not to contain legal 
     *   XML name tokens, and so forth.
     * </p>
     * 
     * @author Elliotte Rusty Harold
     * @version 1.0a1
     *
     */
    public static final class Type {

        /**
         * <p>
         *   The type of attributes declared to have type CDATA
         *   in the DTD. The most general attribute type.
         *   All well-formed attribute values are valid for 
         *   attributes of type CDATA.
         * </p>
         */
        public static final Type CDATA = new Type(1);

        /**
         * <p>
         *   The type of attributes declared to have type ID
         *   in the DTD. In order to be valid, an ID type attribute
         *   must contain an XML name which is unique among other 
         *   ID type attributes in the document.
         *   Furthermore, each element may contain no more than one
         *   ID type attribute. However, XOM does not enforce
         *   such validity constraints.
         * </p>
         */
        public static final Type ID = new Type(2);
        
        /**
         * <p>
         *   The type of attributes declared to have type IDREF
         *   in the DTD. In order to be valid, an IDREF type attribute
         *   must contain an XML name which is also the value of  
         *   ID type attribute of some element in the document. 
         *   However, XOM does not enforce such validity constraints.
         * </p>
         *
         */
        public static final Type IDREF = new Type(3);

        /**
         * <p>
         *   The type of attributes declared to have type IDREFS
         *   in the DTD. In order to be valid, an IDREFS type attribute
         *   must contain a white space separated list of
         *   XML names, each of which is also the value of  
         *   ID type attribute of some element in the document. 
         *   However,XOM does not enforce such validity constraints.
         * </p>
         *
         */
        public static final Type IDREFS = new Type(4);

        /**
         * <p>
         *   The type of attributes declared to have type NMTOKEN
         *   in the DTD. In order to be valid, a NMTOKEN type 
         *   attribute must contain a single XML name token. However, 
         *   XOM does not enforce such validity constraints.
         * </p>
         *
         */
        public static final Type NMTOKEN = new Type(5);

        /**
         * <p>
         *   The type of attributes declared to have type NMTOKENS
         *   in the DTD. In order to be valid, a NMTOKENS type attribute
         *   must contain a white space separated list of XML name  
         *   tokens. However, XOM does not enforce such validity 
         *   constraints.
         * </p>
         *
         */
        public static final Type NMTOKENS = new Type(6);


        /**
         * <p>
         *   The type of attributes declared to have type NOTATION
         *   in the DTD. In order to be valid, a NOTATION type 
         *   attribute must contain the name of a notation declared  
         *   in the DTD. However, XOM does not enforce such 
         *   validity constraints.
         * </p>
          *
         */
        public static final Type NOTATION = new Type(7);

        /**
         * <p>
         *   The type of attributes declared to have type ENTITY
         *   in the DTD. In order to be valid, a  ENTITY type attribute
         *   must contain the name of an unparsed entity declared in  
         *   the DTD. However, XOM does not enforce such 
         *   validity constraints.
         * </p>
         *
         */
        public static final Type ENTITY = new Type(8);

        /**
         * <p>
         *   The type of attributes declared to have type ENTITIES
         *   in the DTD. In order to be valid, an ENTITIES type 
         *   attribute must contain a white space separated list of 
         *   names of unparsed entities declared in the DTD.  
         *   However, XOM does not enforce such validity constraints.
         * </p>
         *
         */
        public static final Type ENTITIES = new Type(9);

        /**
         * <p>
         *   The type of attributes declared by an enumeration
         *   in the DTD. In order to be valid, a enumeration type 
         *   attribute must contain exactly one of the names given  
         *   in the enumeration in the DTD. However, XOM does not 
         *   enforce such validity constraints.
         * </p>
         * 
         * <p>
         *   Most parsers report attributes of type enumeration as 
         *   having type NMTOKEN. In this case, XOM will not  
         *   distinguish NMTOKEN and enumerated attributes.
         * </p>
         *
         */
        public static final Type ENUMERATION = new Type(10);
        
        /**
         * <p>
         *   The type of attributes not declared in the DTD.
         *   This type only appears in invalid documents.
         *   This is the default type for all attributes in
         *   documents without DTDs.
         * </p>
         * 
         * <p>
         *   Most parsers report attributes of undeclared 
         *   type as having type CDATA. In this case, XOM 
         *   will not distinguish CDATA and undeclared attributes.
         * </p>
         */
        public static final Type UNDECLARED = new Type(0);

        
        /**
         * <p>
         * Returns the string name of this type as might 
         * be used in a DTD; for example, "ID", "CDATA", etc. 
         * </p>
         * 
         *  @return an XML string representation of this type
         *
         * @see java.lang.Object#toString()
         */
        public String getName() {  
            
            switch (type) {
              case 0:
                return "UNDECLARED";   
              case 1:
                return "CDATA";  
              case 2:
                return "ID";  
              case 3:
                return "IDREF";   
              case 4:
                return "IDREFS";   
              case 5:
                return "NMTOKEN";  
              case 6:
                return "NMTOKENS";   
              case 7:
                return "NOTATION";   
              case 8:
                return "ENTITY";   
              case 9:
                return "ENTITIES";  
              case 10:
                return "ENUMERATION"; 
              default: 
                throw new RuntimeException(
                  "Bug in XOM: unexpected attribute type: " + type); 
            }
            
        }   


        private int type;

        private Type(int type) {
          this.type = type;
        }
        
        
        /**
         * <p>
         * Returns a unique identifier for this type.
         * </p>
         * 
         * @return a unique identifier for this type
         * 
         * @see java.lang.Object#hashCode()
         */
        public int hashCode() {
            return this.type;   
        }
        
        
        /**
         * <p>
         * Tests for type equality. This is only necessary,
         * to handle the case where two <code>Type</code> objects
         * are loaded by different class loaders. 
         * </p>
         * 
         * @param o the object compared for equality to this type
         * 
         * @return true if and only if <code>o</code> represents 
         *      the same type
         * 
         * @see java.lang.Object#equals(Object)
         */
        public boolean equals(Object o) {
            
            if (o == this) return true; 
            if (o == null) return false;      
            if (this.hashCode() != o.hashCode()) return false;           
            if (!o.getClass().getName().equals("nu.xom.Attribute.Type")) {
                return false;
            }
            return true;   
            
        }          
        
        
        /**
         * <p>
         * Returns a string representation of the type  
         * suitable for debugging and diagnosis. 
         * </p>
         * 
         *  @return a non-XML string representation of this type
         *
         * @see java.lang.Object#toString()
         */
         public String toString() {    
             
            StringBuffer result 
              = new StringBuffer("[Attribute.Type: ");
            result.append(getName()); 
            result.append("]");
            return result.toString();    
            
        }         

         
    }

    
}
