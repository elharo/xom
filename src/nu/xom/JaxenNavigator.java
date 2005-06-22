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

/**
 * <p>
 * Interface between Jaxen and XOM.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.1b2
 *
 */

import org.jaxen.DefaultNavigator;
import org.jaxen.FunctionCallException;
import org.jaxen.JaxenConstants;
import org.jaxen.JaxenException;
import org.jaxen.NamedAccessNavigator;
import org.jaxen.UnsupportedAxisException;
import org.jaxen.XPath;
import org.jaxen.util.SingleObjectIterator;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.NoSuchElementException;


class JaxenNavigator extends DefaultNavigator implements NamedAccessNavigator {
    
    public Iterator getSelfAxisIterator(Object contextNode) {
        
        if (contextNode instanceof Text) {
            // wrap text nodes in a list
            Text node = (Text) contextNode;
            ArrayList temp = new ArrayList();
            ParentNode parent = node.getParent();
            // parent is never null here due to DocumentFragment
            int index = parent.indexOf(node);
            int first = index;
            int last = index;
            while (first > 0 && parent.getChild(first-1).isText()) {
                first--;
            }
            while (last < parent.getChildCount()-1 && parent.getChild(last+1).isText()) {
                last++;
            }
            for (int i = first; i <= last; i++) {
                temp.add(parent.getChild(i));
            }
            contextNode = temp;
        }
        return new SingleObjectIterator(contextNode);
        
    }
    
    
    public Object getElementById(Object node, String id) {
        
        Node original;
        if (node instanceof ArrayList) {
            original = (Node) ((List) node).get(0);
        }
        else {
            original = (Node) node;
        }
        ParentNode parent;
        if (original.isElement() || original.isDocument()) {
            parent = (ParentNode) original;
        }
        else {
            parent = original.getParent();
        }
        
        // find highest parent node
        ParentNode high = parent;
        while (parent != null) {
            high = parent;
            parent = parent.getParent();
        }
        
        // Now search down from the highest point for the requested ID
        Element root;
        if (high.isDocument()) {
            root = ((Document) high).getRootElement();
        }
        else { // document fragment
            Node first = high.getChild(0);
            if (first.isElement()) {
                root = (Element) high.getChild(0);
            }
            else {
                return null;
            }
        }
        
        return findByID(root, id);
        
    }
    
    
    // ????remove recursion
    public static Element findByID(Element top, String id) {
        
        if (hasID(top, id)) return top;
        else {
            Elements children = top.getChildElements();
            for (int i = 0; i < children.size(); i++) {
                Element result = findByID(children.get(i), id);
                if (result != null) return result;
            }
        }
        return null;
        
    }
    
    
    private static boolean hasID(Element top, String id) {

        for (int i = 0; i < top.getAttributeCount(); i++) {
            Attribute a = top.getAttribute(i);
            if (Attribute.Type.ID == a.getType()) {
                // ???? really need to fully normalize here
                return a.getValue().trim().equals(id);
            }
        }
        return false;
    }


    public String getNamespacePrefix(Object o) {
        Namespace ns = (Namespace) o;
        return ns.getPrefix();
    }
    
    
    public String getNamespaceStringValue(Object o) {
        Namespace ns = (Namespace) o;
        return ns.getValue();
    }

    
    public Iterator getNamespaceAxisIterator(Object contextNode) {
        
        try {
            Element element = (Element) contextNode;
            // ???? can probably avoid this list copy
            Map bindings = element.getNamespacePrefixesInScope();
            Iterator iterator = bindings.entrySet().iterator();
            List result = new ArrayList(bindings.size()+1);
            result.add(new Namespace("xml", 
              "http://www.w3.org/XML/1998/namespace", element));

            while (iterator.hasNext()) {
                Map.Entry binding = (Map.Entry) iterator.next();
                String prefix = (String) binding.getKey();
                String uri = (String) binding.getValue();
                if (! "".equals(prefix) || ! "".equals(uri)) {
                    Namespace ns = new Namespace(prefix, uri, element);
                    result.add(ns);
                }
            }
            return result.iterator();
        }
        catch (ClassCastException ex) {
            return JaxenConstants.EMPTY_ITERATOR;
        }
        
    }
    
    
    public Iterator getParentAxisIterator(Object contextNode)  {
        
        Node parent = (Node) getParentNode(contextNode);
        if (parent == null) return JaxenConstants.EMPTY_ITERATOR;
        else return new SingleObjectIterator(parent);
        
    }
    
    
    public Object getDocumentNode(Object o) {
    
        Node node = (Node) o;
        return node.getRoot();
        
    }
    
        
    public Object getDocument(String url) throws FunctionCallException {
        throw new FunctionCallException("document() function not supported");
    }
    
    public Iterator getAttributeAxisIterator(Object contextNode) {
        
        try {
            Element element = (Element) contextNode;
            return element.attributeIterator();
        }
        catch (ClassCastException ex) {
            return JaxenConstants.EMPTY_ITERATOR;
        }
        
    }
    
    
    public Iterator getChildAxisIterator(Object o) {
        
        if (o instanceof ParentNode) {
            return new ChildIterator((ParentNode) o);
        }
        else {
            return JaxenConstants.EMPTY_ITERATOR;
        }
        
    }
    
    
    public Iterator getFollowingSiblingAxisIterator(Object o) {
        
        Node start;
        if (o instanceof ArrayList) {
            List l = (ArrayList) o;
            start = (Node) l.get(l.size()-1);
        }
        else {
            start = (Node) o;
        }
        ParentNode parent = start.getParent();
        if (parent == null) return JaxenConstants.EMPTY_ITERATOR;
        int startPos = parent.indexOf(start) + 1;
        return new ChildIterator(parent, startPos);
        
    }
    
    
    public Object getParentNode(Object o) {
        
        Node n;
        if (o instanceof ArrayList) {
            n = (Node) ((List) o).get(0);
        }
        else {
            n = (Node) o;
        }
        return n.getParent();
        
    }

    
    public String getTextStringValue(Object o) {
        
        // ???? really need to return multiple consecutive nodes combined

        List texts = (List) o;
        StringBuffer result = new StringBuffer();
        Iterator iterator = texts.iterator();
        while (iterator.hasNext()) {
            Text text = (Text) iterator.next();
            result.append(text.getValue());
        }
        return result.toString();
        
    }
    

    private static class ChildIterator implements Iterator {
    
        private ParentNode parent;

        private int xomIndex = 0;
        private int xomCount;
        
        ChildIterator(ParentNode parent) {
            this.parent = parent;
            this.xomCount = parent.getChildCount();
        }
      
        
        ChildIterator(ParentNode parent, int startNode) {
            this.parent = parent;
            this.xomIndex = startNode;
            this.xomCount = parent.getChildCount();
        }
      
        
        public boolean hasNext() {
            
            for (int i = xomIndex; i < xomCount; i++) {
                Node next = parent.getChild(i); 
                if (next.isText()) {
                    if (! ((Text) next).isEmpty()) {
                        return true;
                    }
                }
                else return true;
            }
            return false;
            
        }
        

        public Object next() {
            
            Object result;
            Node next = parent.getChild(xomIndex++);
            if (next.isText()) {
                Text t = (Text) next;
                // Is this an empty text node?
                boolean empty = t.isEmpty();
                List texts = new ArrayList(1);
                texts.add(t);
                while (xomIndex < xomCount) {
                    Node nextText = parent.getChild(xomIndex);
                    if (! nextText.isText()) break;
                    xomIndex++;
                    texts.add(nextText);
                    if (empty) {
                        if (! ((Text) nextText).isEmpty()) {
                            empty = false;
                        }
                    }
                }
                // need to make sure at least one of these texts is non-empty
                if (empty) return next();
                else result = texts;
                // XXX test a child that ends in several empty text nodes preceded by child elements
            }
            else if (next.isDocType()) {
                return next();
            }
            else {
                result = next;
            }
            return result;
            
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
        
    }
    

    private static class NamedChildIterator implements Iterator {
    
        private ParentNode parent;

        private int index = -1;
        private int xomCount;
        private Element next;
        private String localName;
        private String URI;
        
        NamedChildIterator(ParentNode parent, String localName, String prefix, String namespaceURI) {
            this.parent = parent;
            this.xomCount = parent.getChildCount();
            this.localName = localName;
            if (namespaceURI == null) this.URI = "";
            else this.URI = namespaceURI;
            
            findNext();
        }
      
        private void findNext() {
            
            while (++index < xomCount) {
                Node next = parent.getChild(index);
                if (next.isElement()) {
                    Element element = (Element) next;
                    String elementNamespace = element.getNamespaceURI();
                    if (elementNamespace.equals(URI)) {
                        // ???? check prefix?
                        if (element.getLocalName().equals(localName)) {
                            this.next = element;
                            return;
                        }
                    }
                }
            }
            next = null;
        }
        
        public boolean hasNext() {
            return next != null;
        }
        

        public Object next() {
            
            if (next == null) throw new NoSuchElementException(); // correct ???? necessary?
            Object result = next;
            findNext();
            return result;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
        
    }

    
    public String getElementNamespaceUri(Object element) {
        return ((Element) element).getNamespaceURI();
    }

    
    // In Jaxen, name means the local name only 
    public String getElementName(Object element) {
        return ((Element) element).getLocalName();
    }

    public String getElementQName(Object element) {
        return ((Element) element).getQualifiedName();
    }

    
    public String getAttributeNamespaceUri(Object attr) {
        Attribute attribute = (Attribute) attr;
        return attribute.getNamespaceURI();
    }

    
    // In Jaxen, name means the local name only 
    public String getAttributeName(Object attr) {
        Attribute attribute = (Attribute) attr;
        return attribute.getLocalName();
    }

    
    public String getAttributeQName(Object attr) {
        Attribute attribute = (Attribute) attr;
        return attribute.getQualifiedName();
    }

    public String getProcessingInstructionData(Object o) {
        ProcessingInstruction pi = (ProcessingInstruction) o;
        return pi.getValue();
    }

   
    public String getProcessingInstructionTarget(Object o) {
        ProcessingInstruction pi = (ProcessingInstruction) o;
        return pi.getTarget();
    }

    
    public boolean isDocument(Object object) {
        return object instanceof Document || object instanceof DocumentFragment;
    }

    
    public boolean isElement(Object object) {
        return object instanceof Element;
    }

    
    public boolean isAttribute(Object object) {
        return object instanceof Attribute;
    }

    
    public boolean isNamespace(Object object) {
        return object instanceof Namespace;
    }

    
    public boolean isComment(Object object) {
        return object instanceof Comment;
    }

    
    public boolean isText(Object object) {
        // ???? hack: need to use a separate special subclass of ArrayList I can identify
        if (object instanceof ArrayList) {
            Iterator iterator = ((List) object).iterator();
            while (iterator.hasNext()) {
                if (! (iterator.next() instanceof Text)) return false;
            }
            return true;
        }
        return false;
    }

    
    public boolean isProcessingInstruction(Object object) {
        return object instanceof ProcessingInstruction;
    }

    
    public String getCommentStringValue(Object comment) {
        return ((Comment) comment).getValue();
    }

    
    public String getElementStringValue(Object element) {
        return ((Element) element).getValue();
    }

    
    public String getAttributeStringValue(Object attribute) {
        return ((Attribute) attribute).getValue();
    }
    

    public XPath parseXPath(String expression) throws JaxenException {
        return new JaxenConnector(expression);
    }


    public Iterator getChildAxisIterator(Object parent, String localName, String namespacePrefix, String namespaceURI) 
      throws UnsupportedAxisException {
        
        if (parent instanceof ParentNode) {
            return new NamedChildIterator((ParentNode) parent, localName, namespacePrefix, namespaceURI);
        }
        return JaxenConstants.EMPTY_ITERATOR;
        
    }


    public Iterator getAttributeAxisIterator(Object contextNode, String localName, String namespacePrefix, String namespaceURI) 
      throws UnsupportedAxisException {

        // ???? need prefix?
        try {
            Element element = (Element) contextNode;
            Attribute result = null;
            if (namespaceURI == null) {
                result = element.getAttribute(localName);
            }
            else {
                result = element.getAttribute(localName, namespaceURI);
            }
            
            if (result == null) return JaxenConstants.EMPTY_ITERATOR;
            
            return new SingleObjectIterator(result);
        }
        catch (ClassCastException ex) {
            return JaxenConstants.EMPTY_ITERATOR;
        }
        
    }
    

    
    
}
