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



package nu.xom.xinclude;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.IllegalNameException;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParentNode;
import nu.xom.XMLException;

/**
 * 
 * <p>
 *   Right now this is just for XInclude, and hence is non-public.
 *   Once it's more baked it will probably become public and move 
 *   to a package of its own.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0b5
 *
 */
class XPointer {
    
    
    // prevent instantiation
    private XPointer() {}
    
    
    public static Nodes query(Document doc, String xptr) 
      throws XPointerSyntaxException, XPointerResourceException {    
            
        Nodes result = new Nodes();
        boolean found = false;
        
        try { // Is this a shorthand XPointer?
            // Need to include a URI in case this is a colonized scheme name 
            new Element(xptr, "http://www.example.com");
            Element identified = findByID(doc.getRootElement(), xptr); 
            if (identified != null) {
                result.append(identified);   
                return result;
            }
        }
        catch (IllegalNameException ex) {
            // not a bare name; try element() scheme
            List elementSchemeData = findElementSchemeData(xptr);
            if (elementSchemeData.size() == 0) {
                // This may be a legal XPointer, but it doesn't 
                // have an element() scheme so we can't handle it. 
                throw new XPointerSyntaxException(
                  "No supported XPointer schemes found"
                );    
            }
        
            for (int i = 0; i < elementSchemeData.size(); i++) {
                String currentData = (String) (elementSchemeData.get(i));
                int[] keys = new int[0];
                ParentNode current = doc;
                if (currentData.indexOf('/') == -1) {
                    // raw id in element like element(f2)
                    try {
                        new Element(currentData);
                    }
                    catch (IllegalNameException inex) {
                        // not a bare name; and doesn't contain a /
                        // This doesn't adhere to the element scheme. 
                        // Therefore, according to the XPointer element
                        // scheme spec, " if scheme data in a pointer 
                        // part with the element() scheme does not 
                        // conform to the syntax defined in this 
                        // section the pointer part does not identify 
                        // a subresource."
                        continue; 
                    }  
                    Element identified = findByID(
                      doc.getRootElement(), currentData); 
                    if (identified != null) {
                        if (!found) result.append(identified); 
                        found = true;                
                    }
                }
                else if (!currentData.startsWith("/")) {
                    String id = currentData.substring(
                      0, currentData.indexOf('/'));
                    // Check to make sure this is a legal 
                    // XML name/ID value
                    try {
                        new Element(id);   
                    }
                    catch (XMLException inex) {
                        // doesn't adhere to the element scheme spec;
                        // Therefore this pointer part does not identify
                        // a subresource (See 2nd paragraph of section  
                        // 3 of http://www.w3.org/TR/xptr-element/ )
                        // This is not a resource error unless no 
                        // XPointer part identifies a subresource.
                        continue;
                    }
                    current = findByID(doc.getRootElement(), id);                         
                    keys = split(currentData.substring(
                      currentData.indexOf('/')));
                    
                    if (current == null) continue;                   
                }
                else {
                    keys = split(currentData);   
                }
                
                for (int j = 0; j < keys.length; j++) {
                    current = findNthChildElement(current, keys[j]);
                    if (current == null) break;
                }
            
                if (current != doc && current != null) {
                    if (!found) result.append(current); 
                    found = true;
                }
              
            }
            
        }
        
        if (found) return result;
        else {
            // If we get here and still haven't been able to match an
            // element, the XPointer has failed. 
            throw new XPointerResourceException(
              "XPointer " + xptr 
              + " did not locate any nodes in the document "
              + doc.getBaseURI()
            );
        }
        
    }
    
    
    private static Element findNthChildElement(
      ParentNode parent, int position) {  
        // watch out for 1-based indexing of tumblers
        int elementCount = 1;
        for (int i = 0; i < parent.getChildCount(); i++) {
            Node child = parent.getChild(i);
            if (child instanceof Element) {
                if (elementCount == position) return (Element) child;   
                elementCount++;
            }
        }
        return null;
    }
    
    
    private static int[] split(String tumbler)
      throws XPointerSyntaxException {
  
        int numberOfParts = 0;
        for (int i = 0; i < tumbler.length(); i++) {
          if (tumbler.charAt(i) == '/') numberOfParts++;   
        }
        
        int[] result = new int[numberOfParts];
        int index = 0;
        StringBuffer part = new StringBuffer(3);
        try {
            for (int i = 1; i < tumbler.length(); i++) {
                if (tumbler.charAt(i) == '/') {
                    result[index] = Integer.parseInt(part.toString()); 
                    index++;
                    part = new StringBuffer(3);
                }   
                else {
                    part.append(tumbler.charAt(i));   
                }   
            }
            result[result.length-1] = Integer.parseInt(part.toString());
        }
        catch (NumberFormatException ex) {
            XPointerSyntaxException ex2 
              = new XPointerSyntaxException(tumbler
                + " is not syntactically correct", ex); 
            throw ex2; 
        }
        
        return result;
    }
    
    private static List findElementSchemeData(String xpointer) 
      throws XPointerSyntaxException {
        
        List result = new ArrayList(1);
        
        StringBuffer xptr = new StringBuffer(xpointer.trim());
        StringBuffer scheme = new StringBuffer();
        int i = 0;
        while (i < xptr.length()) {
            char c = xptr.charAt(i);
            if (c == '(') break;
            else scheme.append(c); 
            i++; 
        }
        
        // need to verify that scheme is a QName
        try {
            // ugly hack because Verifier isn't public
            new Element(scheme.toString(), "http://www.example.com/");   
        }
        catch (IllegalNameException ex) {
            throw new XPointerSyntaxException(ex.getMessage());   
        }
        
        int open = 1; // parentheses count
        i++;
        StringBuffer schemeData = new StringBuffer();
        try {
            while (open > 0) {
                char c = xptr.charAt(i);   
                if (c == '^') {
                    c = xptr.charAt(i+1);
                    schemeData.append(c);
                    if (c != '^' && c != '(' && c != ')') {
                        throw new XPointerSyntaxException(
                          "Illegal XPointer escape sequence"
                        );   
                    }
                    i++;
                }
                else if (c == '(') {
                    schemeData.append(c);
                    open++;   
                }
                else if (c == ')') {
                    open--;
                    if (open > 0) schemeData.append(c);
                }
                else {
                    schemeData.append(c);   
                }
                i++;
            }
        }
        catch (StringIndexOutOfBoundsException ex) {
            throw new XPointerSyntaxException("Unbalanced parentheses");   
        }
        
        if (scheme.toString().equals("element")) {
            result.add(schemeData.toString());
        }

        if (i + 1 < xptr.length()) {
            result.addAll(findElementSchemeData(xptr.substring(i)));
        } 
        
        return result;
    }
    
    
    public static Element findByID(Element element, String id) {
         
        Node current = element;
        boolean end = false;
        int index = -1;
        while (true) {
            
            if (current instanceof Element) {
                Element currentElement = (Element) current;
                for (int i = 0; i < currentElement.getAttributeCount(); i++) {
                    Attribute att = currentElement.getAttribute(i);
                    if (att.getType() == Attribute.Type.ID) {
                        if (att.getValue().trim().equals(id)) {
                            return currentElement;   
                        }
                    }   
                }
            }
            
            if (!end && current.getChildCount() > 0) {
               current = current.getChild(0);
               index = 0;
            }
            else {
                if (end) {
                    if (current == element) break;
                }
                else {
                    ;
                }
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
        
        return null;
        
    }

    
}
