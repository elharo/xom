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
 * @version 1.1a2
 *
 */
import nu.xom.Attribute;
import nu.xom.Comment;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.ProcessingInstruction;
import nu.xom.Text;
import nu.xom.Node;
import nu.xom.ParentNode;

import org.jaxen.DefaultNavigator;
import org.jaxen.FunctionCallException;
import org.jaxen.JaxenException;
import org.jaxen.XPath;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;


class JaxenNavigator extends DefaultNavigator {

    
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
        List l = new ArrayList(1);
        l.add(contextNode);
        return l.iterator();
        
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
            Iterator iterator = bindings.keySet().iterator();
            List result = new ArrayList(bindings.size()+1);
            result.add(new Namespace("xml", 
              "http://www.w3.org/XML/1998/namespace", element));

            while (iterator.hasNext()) {
                String prefix = (String) iterator.next();
                String uri = (String) bindings.get(prefix);
                if (! "".equals(prefix) || ! "".equals(uri)) {
                    Namespace ns = new Namespace(prefix, uri, element);
                    result.add(ns);
                }
            }
            return result.iterator();
        }
        catch (ClassCastException ex) {
            return Collections.EMPTY_LIST.iterator();
        }
        
    }
    
    
    public Iterator getParentAxisIterator(Object contextNode)  {
        
        Node parent = (Node) getParentNode(contextNode);
        if (parent == null) return Collections.EMPTY_LIST.iterator();
        else {
            List l = new ArrayList(1);
            l.add(parent);
            return l.iterator();
        }
        
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
            if (element.attributes == null) {
                return Collections.EMPTY_LIST.iterator();
            }
            else return element.attributes.iterator();
        }
        catch (ClassCastException ex) {
            return Collections.EMPTY_LIST.iterator();
        }
        
    }
    
    
    public Iterator getChildAxisIterator(Object o) {
        
        if (o instanceof ParentNode) {
            return new ChildIterator((ParentNode) o);
        }
        else {
            return Collections.EMPTY_LIST.iterator();
        }
        
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
        
        List texts = (List) o;
        String result = "";
        Iterator iterator = texts.iterator();
        while (iterator.hasNext()) {
            Text text = (Text) iterator.next();
            result += text.getValue();
        }
        return result;
        
    }
    

    private static class ChildIterator implements Iterator {
    
        private ParentNode parent;

        private int xomIndex = 0;
        private int xomCount;
        
        ChildIterator(ParentNode parent) {
            this.parent = parent;
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
            // xpathIndex++;
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
        return object instanceof ArrayList;
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
    
    
}
