/* Copyright 2002-2006 Elliotte Rusty Harold
   
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

package nu.xom.canonical;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.xml.sax.helpers.NamespaceSupport;

import nu.xom.Attribute;
import nu.xom.Comment;
import nu.xom.DocType;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Namespace;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParentNode;
import nu.xom.ProcessingInstruction;
import nu.xom.Serializer;
import nu.xom.Text;
import nu.xom.XPathContext;

/**
 * <p>
 *   Writes XML in the format specified by <a target="_top"
 *   href="http://www.w3.org/TR/2001/REC-xml-c14n-20010315">Canonical
 *   XML Version 1.0</a> or <a target="_top"
 *   href="http://www.w3.org/TR/2002/REC-xml-exc-c14n-20020718/">Exclusive
 *   XML Canonicalization Version 1.0</a>. 
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.2d1
 *
 */
public class Canonicalizer {

    private boolean withComments;
    private boolean exclusive = false;
    private CanonicalXMLSerializer serializer;
    private List inclusiveNamespacePrefixes = new ArrayList();
    
    private static Comparator comparator = new AttributeComparator();
    
    
    public final static String CANONICAL_XML =  
     "http://www.w3.org/TR/2001/REC-xml-c14n-20010315";
    public final static String CANONICAL_XML_WITH_COMMENTS =  
     "http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments";
    public final static String EXCLUSIVE_XML_CANONICALIZATION = 
      "http://www.w3.org/2001/10/xml-exc-c14n#";
    public final static String EXCLUSIVE_XML_CANONICALIZATION_WITH_COMMENTS = 
      "http://www.w3.org/2001/10/xml-exc-c14n#WithComments";
    
    
    private static class AttributeComparator implements Comparator {
        
        public int compare(Object o1, Object o2) {
            Attribute a1 = (Attribute) o1;   
            Attribute a2 = (Attribute) o2;   
            
            String namespace1 = a1.getNamespaceURI();
            String namespace2 = a2.getNamespaceURI();
            if (namespace1.equals(namespace2)) { 
                return a1.getLocalName().compareTo(a2.getLocalName());             
            }
            else if (namespace1.equals("")) {
                 return -1;   
            }
            else if (namespace2.equals("")) {
                 return 1;   
            }
            else { // compare namespace URIs
                return namespace1.compareTo(namespace2);               
            }
            
        }

    }
    
    
    /**
     * <p>
     *   Creates a <code>Canonicalizer</code> that outputs a 
     *   canonical XML document with comments.
     * </p>
     * 
     * @param out the output stream the document
     *     is written onto
     */
    public Canonicalizer(OutputStream out) {
        this(out, true, false);
    }

    
    /**
     * <p>
     *   Creates a <code>Canonicalizer</code> that outputs a 
     *   canonical XML document with or without comments.
     * </p>
     * 
     * @param out the output stream the document
     *     is written onto
     * @param withComments true if comments should be included 
     *     in the output, false otherwise
     */
    public Canonicalizer(
      OutputStream out, boolean withComments) {
        this(out, withComments, false);
    }


    /**
     * <p>
     * Creates a <code>Canonicalizer</code> that outputs a 
     * canonical XML document with or without comments,
     * using either the original or the exclusive canonicalization
     * algorithm. 
     * </p>
     * 
     * @param out the output stream the document
     *     is written onto
     * @param withComments true if comments should be included 
     *     in the output, false otherwise
     * @param exclusive true if exclusive XML canonicalization 
     *     should be performed, false if regular XML canonicalization
     *     should be performed
     */
    private Canonicalizer(
      OutputStream out, boolean withComments, boolean exclusive) {
        
        this.serializer = new CanonicalXMLSerializer(out);
        serializer.setLineSeparator("\n");
        this.withComments = withComments;
        this.exclusive = exclusive;
        
    }


    /**
     * <p>
     * Creates a <code>Canonicalizer</code> that outputs a 
     * canonical XML document using the specified algorithm. 
     * Currently, four algorithms are defined and supported:
     * </p>
     * 
     * <ul>
     * <li>Canonical XML without comments: 
     * <code>http://www.w3.org/TR/2001/REC-xml-c14n-20010315</code></li>
     * <li>Canonical XML with comments: 
     * <code>http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments</code></li>
     * <li>Exclusive XML canonicalization without comments: 
     * <code>http://www.w3.org/2001/10/xml-exc-c14n#</code></li>
     * <li>Exclusive XML canonicalization with comments: 
     * <code>http://www.w3.org/2001/10/xml-exc-c14n#WithComments</code></li>
     * </ul>
     * 
     * @param out the output stream the document
     *     is written onto
     * @param algorithm the URI for the canonicalization algorithm
     * 
     * @throws CanonicalizationException if the algorithm is 
     *     not recognized
     * @throws NullPointerException if the algorithm is null
     * 
     */
    public Canonicalizer(
      OutputStream out, String algorithm) {
        
        if (algorithm == null) {
            throw new NullPointerException("Null algorithm");
        }
        this.serializer = new CanonicalXMLSerializer(out);
        serializer.setLineSeparator("\n");
        if (algorithm.equals(CANONICAL_XML)) {
            this.withComments = false;
            this.exclusive = false;
        }
        else if (algorithm.equals(CANONICAL_XML_WITH_COMMENTS)) {
            this.withComments = true;
            this.exclusive = false;
        }
        else if (algorithm.equals(EXCLUSIVE_XML_CANONICALIZATION)) {
            this.withComments = false;
            this.exclusive = true;            
        }
        else if (algorithm.equals(EXCLUSIVE_XML_CANONICALIZATION_WITH_COMMENTS)) {
            this.withComments = true;
            this.exclusive = true;            
        }
        else {
            throw new CanonicalizationException(
              "Unsupported canonicalization algorithm: " + algorithm);
        }
        
    }


    private class CanonicalXMLSerializer extends Serializer {
        
        // If nodes is null we're canonicalizing all nodes;
        // the entire document; this is somewhat easier than when
        // canonicalizing only a document subset embedded in nodes
        private Nodes nodes;
        private NamespaceSupport inScope;

        /**
         * <p>
         *   Creates a <code>Serializer</code> that outputs a 
         *   canonical XML document with or without comments.
         * </p>
         * 
         * @param out the <code>OutputStream</code> the document
         *     is written onto
         * @param withComments true if comments should be included 
         *     in the output, false otherwise
         */
        CanonicalXMLSerializer(OutputStream out) {
            super(out);
            setLineSeparator("\n");
        }

        
        /**
         * <p>
         * Serializes a document onto the output 
         * stream using the canonical XML algorithm.
         * </p>
         * 
         * @param doc the <code>Document</code> to serialize
         * 
         * @throws IOException if the underlying <code>OutputStream</code>
         *      encounters an I/O error
         */
         public final void write(Document doc) throws IOException {
            
            inScope = new NamespaceSupport();
            int position = 0;        
            while (true) {
                Node child = doc.getChild(position);
                if (nodes == null || child instanceof Element || nodes.contains(child)) {
                    writeChild(child); 
                    if (child instanceof ProcessingInstruction) breakLine();
                    else if (child instanceof Comment && withComments) {
                        breakLine();
                    }
                }
                position++;
                if (child instanceof Element) break;
            }       
            
            for (int i = position; i < doc.getChildCount(); i++) {
                Node child = doc.getChild(i);
                if (nodes == null || child instanceof Element || nodes.contains(child)) {
                    if (child instanceof ProcessingInstruction) breakLine();
                    else if (child instanceof Comment && withComments) {
                        breakLine();
                    }
                    writeChild(child);
                }
            }
            
            flush();
            
        }  
     
         
        /**
         * <p>
         * Serializes an element onto the output stream using the canonical
         * XML algorithm.  The result is guaranteed to be well-formed. 
         * If <code>element</code> does not have a parent element, it will
         * also be namespace well-formed.
         * </p>
         * 
         * @param element the <code>Element</code> to serialize
         * 
         * @throws IOException if the underlying <code>OutputStream</code>
         *     encounters an I/O error
         */
        protected final void write(Element element) 
          throws IOException {

            // treat empty elements differently to avoid an
            // instanceof test
            if (element.getChildCount() == 0) {
                writeStartTag(element, false);
                writeEndTag(element);                
            }
            else {
                Node current = element;
                boolean end = false;
                int index = -1;
                int[] indexes = new int[10];
                int top = 0;
                indexes[0] = -1;
                while (true) {                   
                    if (!end && current.getChildCount() > 0) {
                       writeStartTag((Element) current, false);
                       current = current.getChild(0);
                       index = 0;
                       top++;
                       indexes = grow(indexes, top);
                       indexes[top] = 0;
                    }
                    else {
                        if (end) {
                            writeEndTag((Element) current);
                            if (current == element) break;
                        }
                        else {
                            writeChild(current);
                        }
                        end = false;
                        ParentNode parent = current.getParent();
                        if (parent.getChildCount() - 1 == index) {
                            current = parent;
                            top--;
                            if (current != element) {
                                index = indexes[top];
                            }
                            end = true;
                        }
                        else {
                            index++;
                            indexes[top] = index;
                            current = parent.getChild(index);
                        }
                    }
                }   
            }
            
        } 
    
        
        private int[] grow(int[] indexes, int top) {
            
            if (top < indexes.length) return indexes;
            int[] result = new int[indexes.length*2];
            System.arraycopy(indexes, 0, result, 0, indexes.length);
            return result;
            
        }


        protected void writeStartTag(Element element, boolean isEmpty) 
          throws IOException {
            
            boolean writeElement = nodes == null || nodes.contains(element);
            if (writeElement) {
                inScope.pushContext();
                writeRaw("<");
                writeRaw(element.getQualifiedName());
            }
            
            SortedMap map = new TreeMap();
            if (nodes == null) {
                ParentNode parent = element.getParent();
                Element parentElement = null;
                if (parent instanceof Element) {
                    parentElement = (Element) parent; 
                } 
                for (int i = 0; 
                     i < element.getNamespaceDeclarationCount(); 
                     i++) {
                    String prefix = element.getNamespacePrefix(i);
                    String uri = element.getNamespaceURI(prefix);
                    
                    if (uri.equals(inScope.getURI(prefix))) {
                        continue;
                    }
                    else if (exclusive) {
                        if (needToDeclareNamespace(element, prefix, uri)) {
                            map.put(prefix, uri);
                        }
                    }
                    else if (uri.equals("")) {
                        // no need to say xmlns=""
                        if (parentElement == null) continue;    
                        if ("".equals(parentElement.getNamespaceURI(""))) {
                            continue;
                        }
                        map.put(prefix, uri);
                    }
                    else {
                        map.put(prefix, uri);
                    }
                    
                } 
                
                writeNamespaceDeclarations(map);
                
            }
            else {
                int position = indexOf(element);
                // do we need to undeclare a default namespace?
                // You know, should I instead create an output tree and then just
                // canonicalize that? probably not
                if (position != -1 && "".equals(element.getNamespaceURI())) {
                    ParentNode parent = element.getParent();
                    // Here we have to check for the nearest default on parents in the
                    // output tree, not the input tree
                    while (parent instanceof Element 
                      && !(nodes.contains(parent))) {
                        parent = parent.getParent();
                    }
                    if (parent instanceof Element) {
                        String uri = ((Element) parent).getNamespaceURI("");
                        if (! "".equals(uri)) {
                            map.put("", "");
                        }
                    }
                }
                
                for (int i = position+1; i < nodes.size(); i++) {
                    Node next = nodes.get(i);
                    if ( !(next instanceof Namespace) ) break;
                    Namespace namespace = (Namespace) next;
                    String prefix = namespace.getPrefix();
                    String uri = namespace.getValue();
                    
                    if (uri.equals(inScope.getURI(prefix))) {
                        continue;
                    }
                    else if (exclusive) {
                        if (needToDeclareNamespace(element, prefix, uri)) {
                            map.put(prefix, uri);
                        }
                    }
                    else {
                        map.put(prefix, uri);
                    }
                    
                } 
                
                writeNamespaceDeclarations(map);
                
            }
            
            Attribute[] sorted = sortAttributes(element);        
            for (int i = 0; i < sorted.length; i++) {
                if (nodes == null || nodes.contains(sorted[i]) 
                   || (sorted[i].getNamespaceURI().equals(Namespace.XML_NAMESPACE) 
                       && sorted[i].getParent() != element)) {
                    write(sorted[i]);
                }
            }       
            
            if (writeElement) {
                writeRaw(">");
            }
            
        }


        private void writeNamespaceDeclarations(SortedMap map) throws IOException {

            Iterator prefixes = map.entrySet().iterator();
            while (prefixes.hasNext()) {
                Map.Entry entry = (Entry) prefixes.next();
                String prefix = (String) entry.getKey();
                String uri = (String) entry.getValue();
                writeRaw(" ");
                writeNamespaceDeclaration(prefix, uri);
                inScope.declarePrefix(prefix, uri);
            }
            
        }


        private boolean needToDeclareNamespace(
          Element parent, String prefix, String uri) {

            boolean match = visiblyUtilized(parent, prefix, uri);
        
            if (match || inclusiveNamespacePrefixes.contains(prefix)) {
                return noOutputAncestorUsesPrefix(parent, prefix, uri);
            }
            
            return false;
            
        }


        private boolean visiblyUtilized(Element element, String prefix, String uri) {

            boolean match = false;
            String pfx = element.getNamespacePrefix();
            String local = element.getNamespaceURI();
            if (prefix.equals(pfx) && local.equals(uri)) {
                match = true;
            }
            else {
                for (int i = 0; i < element.getAttributeCount(); i++) {
                    Attribute attribute = element.getAttribute(i);
                    if (nodes == null || nodes.contains(attribute)) {
                        pfx = attribute.getNamespacePrefix();
                        if (prefix.equals(pfx)) {
                            match = true;
                            break;
                        }
                    }
                }
            }
            return match;
        }


        private boolean noOutputAncestorUsesPrefix(Element original, String prefix, String uri) {

            ParentNode parent = original.getParent();
            if (parent instanceof Document && "".equals(uri)) {
                return false;
            }
            
            while (parent != null && !(parent instanceof Document)) {
                if (nodes == null || nodes.contains(parent)) {
                    Element element = (Element) parent;
                    String pfx = element.getNamespacePrefix();
                    if (pfx.equals(prefix)) {
                        String newURI = element.getNamespaceURI(prefix);
                        return ! newURI.equals(uri);                        
                    }
                    
                    for (int i = 0; i < element.getAttributeCount(); i++) {
                        Attribute attribute = element.getAttribute(i);
                        String current = attribute.getNamespacePrefix();
                        if (current.equals(prefix)) {
                            String newURI = element.getNamespaceURI(prefix);
                            return ! newURI.equals(uri);
                        }
                    }
                }
                parent = parent.getParent();
            }
            return true;
            
        }


        // ???? move into Nodes?
        private int indexOf(Element element) {
            for (int i = 0; i < nodes.size(); i++) {
                if (nodes.get(i) == element) return i;
            }
            return -1;
        }


        protected void write(Attribute attribute) throws IOException {
            
            writeRaw(" ");
            writeRaw(attribute.getQualifiedName());
            writeRaw("=\"");
            writeRaw(prepareAttributeValue(attribute));
            writeRaw("\"");
            
        }
        
        
        protected void writeEndTag(Element element) throws IOException {
            
            if (nodes == null || nodes.contains(element)) {
                writeRaw("</");
                writeRaw(element.getQualifiedName());
                writeRaw(">");
                inScope.popContext();
            }
            
        }    
        
        private final XPathContext xmlcontext = new XPathContext("xml", Namespace.XML_NAMESPACE);
        
        private Attribute[] sortAttributes(Element element) {
    
            Map nearest = new TreeMap();
            // add in any inherited xml: attributes
            if (!exclusive && nodes != null && nodes.contains(element) 
              && ! nodes.contains(element.getParent())) {
                // grab all xml: attributes
                Nodes attributes = element.query("ancestor::*/@xml:*", xmlcontext);
                if (attributes.size() != 0) {
                    // It's important to count backwards here because
                    // XPath returns all nodes in document order, which 
                    // is top-down. To get the nearest we need to go 
                    // bottom up instead.
                    for (int i = attributes.size()-1; i >= 0; i--) {
                        Attribute a = (Attribute) attributes.get(i);
                        String name = a.getLocalName();
                        if (element.getAttribute(name, Namespace.XML_NAMESPACE) != null) {
                            // this element already has that attribute
                            continue;
                        }
                        if (! nearest.containsKey(name)) {
                            Element parent = (Element) a.getParent();
                            if (! nodes.contains(parent)) {
                                nearest.put(name, a);
                            }
                            else {
                                nearest.put(name, null);
                            }
                        }
                    }
                }
                
                // remove null values
                Iterator iterator = nearest.values().iterator();
                while (iterator.hasNext()) {
                    if (iterator.next() == null) iterator.remove();
                }
                
            }
            
            int localCount = element.getAttributeCount();
            Attribute[] result 
              = new Attribute[localCount + nearest.size()];
            for (int i = 0; i < localCount; i++) {
                result[i] = element.getAttribute(i); 
            }
            
            Iterator iterator = nearest.values().iterator();
            for (int j = localCount; j < result.length; j++) {
                result[j] = (Attribute) iterator.next();
            }
            
            Arrays.sort(result, comparator);       
            
            return result;        
            
        }
    
        
        private String prepareAttributeValue(Attribute attribute) {
    
            String value = attribute.getValue();
            StringBuffer result = new StringBuffer(value.length());
    
            if (attribute.getType().equals(Attribute.Type.CDATA)
              || attribute.getType().equals(Attribute.Type.UNDECLARED)) {
                char[] data = value.toCharArray();
                for (int i = 0; i < data.length; i++) {
                    char c = data[i];
                    if (c == '\t') {
                        result.append("&#x9;");
                    }
                    else if (c == '\n') {
                        result.append("&#xA;");
                    }
                    else if (c == '\r') {
                        result.append("&#xD;");
                    }
                    else if (c == '\"') {
                        result.append("&quot;");
                    }
                    else if (c == '&') {
                        result.append("&amp;");
                    }
                    else if (c == '<') {
                        result.append("&lt;");
                    }
                    else { 
                        result.append(c);   
                    }
                }
            }
            else {
                // According to the spec, "Whitespace character references
                // other than &#x20; are not affected by attribute value 
                // normalization. For parsed documents, the parser will  
                // still replace these with the actual character. I am 
                // going to assume that if one is found here, that the 
                // user meant to put it there; and so we will escape it 
                // with a character reference
                char[] data = value.toCharArray();
                boolean seenFirstNonSpace = false;
                for (int i = 0; i < data.length; i++) {
                    if (data[i] == ' ') {
                        if (i != data.length-1 && data[i+1] != ' ' && seenFirstNonSpace) {
                             result.append(data[i]); 
                        }
                        continue;
                    } 
                    seenFirstNonSpace = true;
                    if (data[i] == '\t') {
                        result.append("&#x9;");
                    }
                    else if (data[i] == '\n') {
                        result.append("&#xA;");
                    }
                    else if (data[i] == '\r') {
                        result.append("&#xD;");
                    }
                    else if (data[i] == '\"') {
                        result.append("&quot;");
                    }
                    else if (data[i] == '&') {
                        result.append("&amp;");
                    }
                    else if (data[i] == '<') {
                        result.append("&lt;");
                    }
                    else {
                        result.append(data[i]);
                    }
                }
            }
    
            return result.toString();    
            
        }
        
        
        /**
         * <p>
         * Serializes a <code>Text</code> object
         * onto the output stream using the UTF-8 encoding.
         * The reserved characters &lt;, &gt;, and &amp;
         * are escaped using the standard entity references such as
         * <code>&amp;lt;</code>, <code>&amp;gt;</code>, 
         * and <code>&amp;amp;</code>.
         * </p>
         * 
         * @param text the <code>Text</code> to serialize
         * 
         * @throws IOException  if the underlying <code>OutputStream</code>
         *     encounters an I/O error
         */
        protected final void write(Text text) throws IOException {
            
            if (nodes == null || nodes.contains(text)) {
                String input = text.getValue();
                StringBuffer result = new StringBuffer(input.length());
                for (int i = 0; i < input.length(); i++) {
                    char c = input.charAt(i);
                    if (c == '\r') {
                        result.append("&#xD;");
                    }
                    else if (c == '&') {
                        result.append("&amp;");
                    }
                    else if (c == '<') {
                        result.append("&lt;");
                    }
                    else if (c == '>') {
                        result.append("&gt;");
                    }
                    else { 
                        result.append(c);   
                    }            
                }
                writeRaw(result.toString());
            }
            
        }   
    
        
        /**
         * <p>
         * Serializes a <code>Comment</code> object
         * onto the output stream if and only if this
         * serializer is configured to produce canonical XML
         * with comments.
         * </p>
         * 
         * @param comment the <code>Comment</code> to serialize
         * 
         * @throws IOException if the underlying <code>OutputStream</code>
         *     encounters an I/O error
         */
        protected final void write(Comment comment) 
          throws IOException {
            if (withComments && (nodes == null || nodes.contains(comment))) {
                super.write(comment);
            }
        }
        
        
        protected final void write(ProcessingInstruction pi) 
          throws IOException {
            if (nodes == null || nodes.contains(pi)) {
                super.write(pi);
            }
        }
        
        
        /**
         * <p>
         * Does nothing because canonical XML does not include
         * document type declarations.
         * </p>
         * 
         * @param doctype the document type declaration to serialize
         */
        protected final void write(DocType doctype) {
            // DocType is not serialized in canonical XML
        }


        public void write(Node node) throws IOException {

            if (node instanceof Document) {
                write((Document) node);
            } 
            else if (node instanceof Attribute) {
                write((Attribute) node);
            }
            else if (node instanceof Namespace) {
                write((Namespace) node);
            }
            else {
                writeChild(node);
            }
            
        }
        
        
        private void write(Namespace namespace) throws IOException {
            
            String prefix = namespace.getPrefix();
            String uri = namespace.getValue();
            writeRaw(" xmlns" );
            if (!"".equals(prefix)) {
                writeRaw(":");
                writeRaw(prefix);
            }
            writeRaw("=\"");
            writeAttributeValue(uri);
            writeRaw("\"");
            
        }
        
    }

    
    /**
     * <p>
     * Serializes a node onto the output stream using the specified 
     * canonicalization algorithm. If the node is a document or an 
     * element, then the node's entire subtree is written out.
     * </p>
     * 
     * @param node the node to canonicalize
     * 
     * @throws IOException if the underlying <code>OutputStream</code>
     *      encounters an I/O error
     */
    public final void write(Node node) throws IOException {
        
        // See this thread:
        // http://lists.ibiblio.org/pipermail/xom-interest/2005-October/002656.html
        if (node instanceof Element) {
            Document doc = node.getDocument();
            Element pseudoRoot = null;
            if (doc == null) {
                pseudoRoot = new Element("pseudo");
                new Document(pseudoRoot);
                ParentNode root = (ParentNode) node;
                while (root.getParent() != null) root = root.getParent();
                pseudoRoot.appendChild(root);
            }
            try {
                write(node.query(".//. | .//@* | .//namespace::*"));
            }
            finally {
                if (pseudoRoot != null) pseudoRoot.removeChild(0);
            }
        }
        else {
            serializer.nodes = null;
            serializer.write(node);
        }
        serializer.flush();
        
    }  
 
    
    /**
     * <p>
     * Serializes a document subset onto the output stream using the 
     * canonical XML algorithm. All nodes in the list must come from 
     * same document. Furthermore, they must come from a document.
     * They cannot be detached. The nodes need not be sorted. This 
     * method will sort them into the appropriate order for 
     * canonicalization.
     * </p>
     * 
     * <p>
     * In most common use cases, these nodes will be the result of 
     * evaluating an XPath expression. For example,
     * </p>
     * 
     * <pre><code> Canonicalizer canonicalizer 
     *   = new Canonicalizer(System.out, Canonicalizer.CANONICAL_XML);
     * Nodes result = doc.query("//. | //@* | //namespace::*");
     * canonicalizer.write(result);  
     * </code></pre>
     * 
     * <p>
     * Children are not output unless the subset also includes them.
     * Including an element in the subset does not automatically  
     * select all the element's children, attributes, and namespaces. 
     * Furthermore, not selecting an element does not imply that its 
     * children, namespaces, attributes will not be output. 
     * </p>
     * 
     * @param documentSubset the nodes to serialize
     * 
     * @throws IOException if the underlying <code>OutputStream</code>
     *     encounters an I/O error
     * @throws CanonicalizationException if the nodes come from more
     *     than one document; or if a detached node is in the list
     */
    public final void write(Nodes documentSubset) throws IOException { 
    
        if (documentSubset.size() > 0) {
            Document doc = documentSubset.get(0).getDocument();
            if (doc == null) {
                throw new CanonicalizationException(
                  "Canonicalization is not defined for detached nodes");
            }
            Nodes result = sort(documentSubset);
            serializer.nodes = result;
            serializer.write(doc);        
            serializer.flush();
        } 
        
    }   

    
    /**
     * <p>
     * Specifies the prefixes that will be output as specified in 
     * regular canonical XML, even when doing exclusive 
     * XML canonicalization.
     * </p>
     * 
     * @param inclusiveNamespacePrefixes a whitespace separated list 
     *     of namespace prefixes that will always be included in the 
     *     output, even in exclusive canonicalization
     */
    public final void setInclusiveNamespacePrefixList(String inclusiveNamespacePrefixes) 
      throws IOException {  
        
        this.inclusiveNamespacePrefixes.clear();
        if (this.exclusive && inclusiveNamespacePrefixes != null) {
            StringTokenizer tokenizer = new StringTokenizer(
              inclusiveNamespacePrefixes, " \t\r\n", false);
            while (tokenizer.hasMoreTokens()) {
                this.inclusiveNamespacePrefixes.add(tokenizer.nextToken());
            }
        }
       
    }   

    
    // XXX remove recursion
    // recursively descend through document; in document
    // order, and add results as they are found
    private Nodes sort(Nodes in) {

        Node root = in.get(0).getDocument();
        if (in.size() > 1) {
            Nodes out = new Nodes();
            List list = new ArrayList(in.size());
            List namespaces = new ArrayList();
            for (int i = 0; i < in.size(); i++) {
                Node node = in.get(i);
                list.add(node);
                if (node instanceof Namespace) namespaces.add(node);
            }
            sort(list, namespaces, out, (ParentNode) root);
            if (! list.isEmpty() ) {
                // Are these just duplicates; or is there really a node
                // from a different document?
                Iterator iterator = list.iterator();
                while (iterator.hasNext()) {
                    Node next = (Node) iterator.next();
                    if (root != next.getDocument()) {
                        throw new CanonicalizationException(
                          "Cannot canonicalize subsets that contain nodes from more than one document");
                    }
                }
            }
            return out;
        }
        else {
            return new Nodes(in.get(0));
        }
        
    }


    private static void sort(List in, List namespaces, Nodes out, ParentNode parent) {

        if (in.isEmpty()) return;
        if (in.contains(parent)) {
            out.append(parent);
            in.remove(parent);
            // I'm fairly sure this next line is unreachable, but just
            // in case it isn't I'll leave this comment here.
            // if (in.isEmpty()) return;
        }
        
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            Node child = parent.getChild(i);
            if (child instanceof Element) {
                Element element = (Element) child;
                if (in.contains(element)) {
                    out.append(element);
                    in.remove(element);
                }
                // attach namespaces
                if (!namespaces.isEmpty()) {
                    Iterator iterator = in.iterator();
                    while (iterator.hasNext()) {
                        Object o = iterator.next();
                        if (o instanceof Namespace) {
                            Namespace n = (Namespace) o;
                            if (element == n.getParent()) {
                                out.append(n);
                                iterator.remove();
                            }
                        }
                    }
                }
                
                // attach attributes
                for (int a = 0; a < element.getAttributeCount(); a++) {
                    Attribute att = element.getAttribute(a);
                    if (in.contains(att)) {
                        out.append(att);
                        in.remove(att);
                        if (in.isEmpty()) return;
                    }
                }
                sort(in, namespaces, out, element);
            }
            else {
                if (in.contains(child)) {
                    out.append(child);
                    in.remove(child);
                    if (in.isEmpty()) return;
                }
            }
        }
        
    }
 
    
}