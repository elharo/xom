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
import org.jaxen.UnsupportedAxisException;
import org.jaxen.XPath;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;


class JaxenNavigator extends DefaultNavigator {

    
    public String getNamespacePrefix(Object o) {
        
        if (o instanceof Element) return ((Element) o).getNamespacePrefix();
        else if (o instanceof Attribute) return ((Attribute) o).getNamespacePrefix();
        return null; // ???? throw exception????
        
    }

    
     
    public String translateNamespacePrefixToUri(String prefix, Object o) {
        
        Element element = (Element) o;
        return element.getNamespaceURI(prefix);
        
    }
    
    
    public String getNamespaceStringValue(Object o) {
        if (o instanceof Element) return ((Element) o).getNamespaceURI();
        else if (o instanceof Attribute) return ((Attribute) o).getNamespaceURI();
        return null; // ???? throw exception????
    }

    
    
    public Iterator getNamespaceAxisIterator(Object o) throws UnsupportedAxisException {
        throw new UnsupportedAxisException("Namespace axis not supported yet");
    }
    
    public Iterator getParentAxisIterator(Object o)  {
        
        Node n = (Node) o;
        Node parent = n.getParent();
        if (parent == null) return new EmptyIterator();
        else {
            List l = new ArrayList(1);
            l.add(parent);
            return l.iterator();
        }
        
    }
    
    
    public Object getDocumentNode(Object o) {
    
        Node node = (Node) o;
        return node.getDocument();
        
    }
    
        
    public Object getDocument(String url) throws FunctionCallException {
        
        Builder builder = new Builder();
        try {
            return builder.build(url);
        }
        catch (ParsingException ex) {
            throw new FunctionCallException(ex.getMessage(), ex);
        }
        catch (IOException ex) {
            throw new FunctionCallException(ex.getMessage(), ex);
        }
        
    }
    
    public Iterator getAttributeAxisIterator(Object o) {
        
        Element element = (Element) o;
        if (element.attributes == null) return new EmptyIterator();
        else return element.attributes.iterator();
        
    }
    
    
    private static class EmptyIterator implements Iterator {

        public boolean hasNext() {
            return false;
        }

        public Object next() {
            return null;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }  
        
    }
    

    public Iterator getChildAxisIterator(Object o) {
        
        if (o instanceof ParentNode) {
            return new ChildIterator((ParentNode) o);
        }
        else {
            return new EmptyIterator();
        }
        
    }
    
    
    public Object getParentNode(Object o) {
        Node n = (Node) o;
        return n.getParent();
    }

    
    private static int getXPathChildCount(ParentNode parent) {
    
        if (parent instanceof Document) {
            return parent.getChildCount();
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
        
        List t = (List) o;
        String result = "";
        // iterate this????
        for (int i = 0; i < t.size(); i++) {
            Object item = t.get(i);
            result += ((Text) item).getValue();
        }
        return result;
        
    }
    
    
    // seems to be necessary to avoid some reflection based
    // issues inside jaxen; double check if we still need this????
    private static class XOMList extends ArrayList {}


    private static Object getXPathChild(int request, ParentNode parent) {
    
        if (parent instanceof Document) {
            return parent.getChild(request);
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
        
        return null; // or throw exception ????
        
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
        return object instanceof Document;
    }

    
    public boolean isElement(Object object) {
        return object instanceof Element;
    }

    
    public boolean isAttribute(Object object) {
        return object instanceof Attribute;
    }

    
    public boolean isNamespace(Object object) {
        return false;
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
