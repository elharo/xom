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


import nu.xom.Attribute;
import nu.xom.Comment;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.ProcessingInstruction;
import nu.xom.Text;
import nu.xom.Node;
import nu.xom.Builder;
import nu.xom.ParentNode;

import org.jaxen.DefaultNavigator;
import org.jaxen.FunctionCallException;
import org.jaxen.JaxenException;
import org.jaxen.XPath;

import java.io.IOException;
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
            XOMList temp = new XOMList();
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
        
        Node original = (Node) node;
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
        XPathNamespaceNode ns = (XPathNamespaceNode) o;
        return ns.prefix;
    }
    
    
    public String getNamespaceStringValue(Object o) {
        XPathNamespaceNode ns = (XPathNamespaceNode) o;
        return ns.uri;
    }

    
    static class XPathNamespaceNode {
        
        XPathNamespaceNode(String prefix, String uri) {
            this.prefix = prefix;
            this.uri = uri;
        }
        
        String prefix;
        String uri;
        
    }
    
    
    public Iterator getNamespaceAxisIterator(Object contextNode) {
        
        try {
            Element element = (Element) contextNode;
            // ???? can probably avoid this list copy
            Map bindings = element.getNamespacePrefixesInScope();
            Iterator iterator = bindings.keySet().iterator();
            List result = new ArrayList(bindings.size()+1);
            result.add(new XPathNamespaceNode("xml", "http://www.w3.org/XML/1998/namespace"));

            while (iterator.hasNext()) {
                String prefix = (String) iterator.next();
                String uri = (String) bindings.get(prefix);
                if (! "".equals(prefix) || ! "".equals(uri)) {
                    XPathNamespaceNode ns = new XPathNamespaceNode(prefix, uri);
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
        
        Node n = (Node) contextNode;
        Node parent = n.getParent();
        if (parent == null) return Collections.EMPTY_LIST.iterator();
        else {
            List l = new ArrayList(1);
            l.add(parent);
            return l.iterator();
        }
        
    }
    
    
    public Object getDocumentNode(Object o) {
    
        Node node = (Node) o;
        //return node.getDocument();
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
        Node n = (Node) o;
        return n.getParent();
    }

    
    private static int getXPathChildCount(ParentNode parent) {
    
        if (parent instanceof Document) {
            DocType doctype = ((Document) parent).getDocType();
            if (doctype == null) return parent.getChildCount();
            else return parent.getChildCount() - 1;
        }
        int children = 0;
        
        boolean previousWasText = false;
        for (int i = 0; i < parent.getChildCount(); i++) {
            Node child = parent.getChild(i);
            if (child instanceof Text && !previousWasText) {
                if (child.getValue().length() != 0) {
                    children++;
                    previousWasText = true;
                }
            }
            else {
                previousWasText = false;
                children++;
            }
        }
        return children;
        
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
    
    
    // seems to be necessary to avoid some reflection based
    // issues inside Jaxen; double check if we still need this????
    private static class XOMList extends ArrayList {}

    
    // Need to make sure we don't count DocType in children when 
    // working with XPath
    private static Object getXPathChild(int request, Document parent) {
     
        DocType doctype = parent.getDocType();
        if (doctype == null) return parent.getChild(request);
        else {
            int doctypePosition = parent.indexOf(doctype);
            if (request < doctypePosition) {
                return parent.getChild(request);
            }
            else return parent.getChild(request+1);
        }
        
    }
    

    private static Object getXPathChild(int request, ParentNode parent) {
    
        if (parent instanceof Document) {
            return getXPathChild(request, (Document) parent);
        }
        
        int childCount = 0;
        
        boolean previousWasText = false;
        for (int i = 0; i < parent.getChildCount(); i++) {
            Node child = parent.getChild(i);
            
            // check to see if previous was text and if so go to next
            if (child instanceof Text && previousWasText) continue;
            
            if (request == childCount) {
                if (child instanceof Text) {
                    StringBuffer sb = new StringBuffer();
                    List list = new XOMList();
                    int textCount = i;
                    do {
                        Text temp = (Text) child;
                        list.add(temp);
                        sb.append(child.getValue());
                        textCount++;
                        if (textCount == parent.getChildCount()) break;
                        child = parent.getChild(textCount);
                    } while (child instanceof Text);
                     
                    
                    if (sb.length() != 0) return list;
                }
                else {
                    return child;
                }
            }
            else {
                if (child instanceof Text && !previousWasText) {
                    if (child.getValue().length() != 0) {
                      childCount++;
                      previousWasText = true;
                    }
                }
                else {
                    previousWasText = false;
                    childCount++;
                }
            }
        }
        
        return null; 
        
    }
    

    private static class ChildIterator implements Iterator {
    
        private ParentNode parent;

        private int index = 0;
        private int end;
        
        ChildIterator(ParentNode parent) {
            this.parent = parent;
            this.end = getXPathChildCount(parent);
        }
        
        public boolean hasNext() {
            return index < end;
        }
        
        public Object next() {
            return getXPathChild(index++, parent);
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
        return object instanceof XPathNamespaceNode;
    }

    
    public boolean isComment(Object object) {
        return object instanceof Comment;
    }

    
    public boolean isText(Object object) {
        return object instanceof XOMList;
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
