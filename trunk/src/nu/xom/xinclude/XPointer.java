// Copyright 2002, 2003 Elliotte Rusty Harold
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

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.IllegalNameException;
import nu.xom.Node;
import nu.xom.NodeList;
import nu.xom.ParentNode;

/**
 * 
 * <p>
 *   Right now this is just for experiemnts,
 *   and hence is non-public. Once it's more baked it will
 *   probably become public and move to a package of its own.
 * </p>
 * 
 * 
 * @author Elliotte Rusty Harold
 *
 */
class XPointer {
    
    public static NodeList resolve(Document doc, String xptr) 
        throws XPointerSyntaxException {    
            
        try {   
            xptr = decode(xptr);
            NodeList result = new NodeList();
            // Is this a bare name?
            try {
                new Element(xptr);
                Element identified = findByID(doc.getRootElement(), xptr); 
                if (identified != null) {
                    result.append(identified);   
                }
                return result;
            }
            catch (IllegalNameException ex) {
               // not a bare name   
            }          
            
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
                    catch (IllegalNameException ex) {
                        // not a bare name 
                        throw new XPointerSyntaxException(
                          "bad element scheme data " + elementSchemeData
                        );  
                    }  
                    Element identified = findByID(doc.getRootElement(), currentData); 
                    if (identified != null) {
                        result.append(identified);   
                        return result;                
                    }
                }
                else if (!currentData.startsWith("/")) {
                    String id = currentData.substring(0, currentData.indexOf('/'));
                    current = findByID(doc.getRootElement(), id);       
                    keys = split(currentData.substring(currentData.indexOf('/')));   
                }
                else {
                    keys = split(currentData);   
                }
                
                for (int j = 0; j < keys.length; j++) {
                    current = findNthChildElement(current, keys[j]);
                    if (current == null) break;
                }
            
                if (current != null) {
                    result.append(current);
                    return result;
                }
            }
              
        }
        catch (StringIndexOutOfBoundsException ex) {
            XPointerSyntaxException ex2 = new XPointerSyntaxException(xptr 
              + " is not a syntactically correct XPointer"); 
            ex2.setRootCause(ex);
            throw ex2; 
        }
        catch (NumberFormatException ex) {
            XPointerSyntaxException ex2 = new XPointerSyntaxException(xptr 
              + " is not a syntactically correct XPointer"); 
            ex2.setRootCause(ex);
            throw ex2; 
        }
        
        // If we get here and still haven't been able to match an
        // element, the XPointer has failed. Change this to a resourceexception????
        // see XPointer spec
        throw new XPointerSyntaxException(
          "XPointer " + xptr 
          + " did not locate any nodes in the document "
          + doc.getBaseURI()
        );
    }
    
    
    private static Element findNthChildElement(ParentNode parent, int position) {
        
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
    
    private static int[] split(String tumbler) {
          
        int numberOfParts = 0;
        for (int i = 0; i < tumbler.length(); i++) {
          if (tumbler.charAt(i) == '/') numberOfParts++;   
        }
        
        int[] result = new int[numberOfParts];
        int index = 0;
        StringBuffer part = new StringBuffer(3);
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
        
        return result;
    }
    
    private static List findElementSchemeData(String xpointer) 
      throws XPointerSyntaxException {
        
        List result = new ArrayList(1);
        
        String firstElementSchemeData = null;
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
        
        if (scheme.toString().equals("element")) {
            result.add(schemeData.toString());
        }

        if (i + 1 < xptr.length()) {
            result.addAll(findElementSchemeData(xptr.substring(i)));
        } 
        
        return result;
    }
    
    public static Element findByID(Element element, String id) {
         
        for (int i = 0; i < element.getAttributeCount(); i++) {
            Attribute att = element.getAttribute(i);
            if (att.getType() == Attribute.Type.ID) {
                if (att.getValue().trim().equals(id)) {
                    return element;   
                }
            }   
        }
        
        Elements children = element.getChildElements();
        for (int i = 0; i < children.size(); i++) {
            Element result = findByID(children.get(i), id);
            if (result != null) return result; 
        }
        
        return null;
    }
    
    private static String decode(String xptr) {
        StringBuffer result = new StringBuffer(xptr);
        for (int i = 0; i < result.length(); i++) {
            char c = result.charAt(i);
            if (c == '%') {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                while (c == '%') {
                    result.deleteCharAt(i);
                    String hex = result.substring(i, i+2);
                    byte character = (byte) Integer.parseInt(hex, 16);
                    out.write(character);
                    result.deleteCharAt(i);
                    result.deleteCharAt(i);                    
                    c = result.charAt(i);
                }
                byte[] raw = out.toByteArray();
                try {
                    String data = new String(raw, "UTF-8");
                    result.insert(i, data);
                } catch (UnsupportedEncodingException ex) {
                    throw new RuntimeException(
                      "Broken VM does not support UTF-8"
                    );
                }
                
            }
        }
        return result.toString();
    }  

}
