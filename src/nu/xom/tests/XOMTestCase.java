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

package nu.xom.tests;

import junit.framework.TestCase;
import nu.xom.Attribute;
import nu.xom.Comment;
import nu.xom.DocType;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.ProcessingInstruction;
import nu.xom.Text;


/*
 * <p>
 * This class provides utility methods to
 * compare nodes for deep equality in an infoset
 * sense.
 * </p>
 *
 * @author Elliotte Rusty Harold
 * @version 1.0d19
 *
 */
public class XOMTestCase extends TestCase {

    public XOMTestCase() {}
    
    public XOMTestCase(String name) {
        super(name);   
    }
    
    public static void assertEquals(Text expected, Text actual) {
        assertEquals(null, expected, actual);
    }
    
    public static void assertEquals(
      String message, Text expected, Text actual) {
        assertEquals(message, expected.getValue(), actual.getValue());
    }

    public static void assertEquals(
      Attribute expected, Attribute actual) {
        assertEquals(null, expected, actual);   
    }

    
    public static void assertEquals(
      String message, Attribute expected, Attribute actual) {
        
        String value1 = expected.getValue();
        String value2 = actual.getValue();
        if ("xml:base".equals(expected.getQualifiedName())) {
            // not 100% sure this is kosher but needed for the
            // XInclude conformance tests????
            // discuss with XInclude WG, can base URIs have
            // fragment IDs?
            if (value1.indexOf('#') >= 0) {
                value1 = value1.substring(0, value1.indexOf('#')); 
            }
            if (value2.indexOf('#') >= 0) {
                value2 = value2.substring(0, value2.indexOf('#')); 
            }
            assertTrue(value1 + " " + value2, 
              value1.endsWith(value2) || value2.endsWith(value1));
        } 
        else {
            assertEquals(value1, value2);
            assertEquals(expected.getLocalName(), actual.getLocalName());
            assertEquals(
              expected.getQualifiedName(), actual.getQualifiedName()
            );
            assertEquals(
              expected.getNamespaceURI(), actual.getNamespaceURI()
            );
        }

    }

    public static void assertEquals(DocType expected, DocType actual) {
        assertEquals(null, expected, actual);
    }

    public static void assertEquals(
      String message, DocType expected, DocType actual) {
        
        /* assertEquals(
          type1.getInternalDTDSubset(), 
          type2.getInternalDTDSubset()
        ); */
        assertEquals(message,
          expected.getPublicID(), 
          actual.getPublicID()
        );
        assertEquals(message,
          expected.getSystemID(), 
          actual.getSystemID()
        );
        assertEquals(message,
          expected.getRootElementName(), 
          actual.getRootElementName()
        );
    }

    public static void assertEquals(
      Element expected, Element actual) {
        assertEquals(null, expected, actual);

    }

    public static void assertEquals(String message,
      Element expected, Element actual) {
        
        assertEquals(message,
          expected.getLocalName(), 
          actual.getLocalName()
        );
        assertEquals(message,
          expected.getQualifiedName(), 
          actual.getQualifiedName()
        );
        assertEquals(message,
          expected.getNamespaceURI(), 
          actual.getNamespaceURI()
        );

        assertEquals(message,
          expected.getAttributeCount(), 
          actual.getAttributeCount()
        );
        
        for (int i = 0; i < expected.getAttributeCount(); i++ ) {
            Attribute att1 = expected.getAttribute(i);
            Attribute att2 
              = actual.getAttribute(
                att1.getLocalName(), 
                att1.getNamespaceURI()
                );
            assertNotNull(message, att2);
            assertEquals(message, att1, att2);
        }

        // Check namespaces in scope by listing all the prefixes
        // on element1 and making sure element2 gives the same value
        // for those prefixes, and vice versa. This is necessary
        // to handle a few weird cases that arise in XInclude
        // when prefixes are declared multiple times, to account for
        // the fact that some serializers may drop redundant
        // namespace declarations 
        for (int i = 0; 
             i < expected.getNamespaceDeclarationCount(); 
             i++ ) {
            String prefix1 = expected.getNamespacePrefix(i);
            String uri1 = expected.getNamespaceURI(prefix1);
            assertNotNull(message, actual.getNamespaceURI(prefix1));
            assertEquals(message,
              uri1, actual.getNamespaceURI(prefix1)
            );                      
        }
        for (int i = 0; 
             i < actual.getNamespaceDeclarationCount(); 
             i++ ) {
            String prefix1 = actual.getNamespacePrefix(i);
            String uri1 = actual.getNamespaceURI(prefix1);
            assertNotNull(expected.getNamespaceURI(prefix1));
            assertEquals(
              uri1, expected.getNamespaceURI(prefix1)
            );                      
        }
        
        if (expected.getChildCount() != actual.getChildCount()) {
           // combine text nodes; this modifies the elements
           // but shouldn't be a big problem as long as 
           // this is only used for unit testing  
           combineTextNodes(expected);
           combineTextNodes(actual);
           
        }
        assertEquals(actual.toXML(),
          expected.getChildCount(), actual.getChildCount());
        for (int i = 0; i < expected.getChildCount(); i++) {
            Node child1 = expected.getChild(i);
            Node child2 = actual.getChild(i);
            assertEquals(message, child1, child2);
        }

    }
    
    private static void combineTextNodes(Element element) {
        for (int i = 0; i < element.getChildCount()-1; i++) {
            Node child = element.getChild(i);
            if (child instanceof Text) {
                  Node followingSibling = element.getChild(i+1);
                  if (followingSibling instanceof Text) {
                      Text combined = new Text(child.getValue() 
                        + followingSibling.getValue());
                      element.replaceChild(child, combined);
                      element.removeChild(followingSibling);
                      i--;
                  }
            }
        }        
    }

    public static void assertEquals(
      String message, Document expected, Document actual) {       
        assertEquals(message,
          expected.getChildCount(), 
          actual.getChildCount()
        );
        for (int i = 0; i < actual.getChildCount(); i++) {
            Node child1 = expected.getChild(i);
            Node child2 = actual.getChild(i);
            assertEquals(message, child1, child2);
        }
    }

    public static void assertEquals(
      Document expected, Document actual) {       
        assertEquals(null, expected, actual);
    }
    
    public static void assertEquals(
      String message, Comment expected, Comment actual) {
        assertEquals(message, expected.getValue(), actual.getValue());
    }
    
    public static void assertEquals(Comment expected, Comment actual) {
        assertEquals(null, expected, actual);
    }
    
    public static void assertEquals(ProcessingInstruction instruction1, 
      ProcessingInstruction instruction2) {
        assertEquals(null, instruction1, instruction2);
    }
    
    public static void assertEquals(String message, 
      ProcessingInstruction instruction1, 
      ProcessingInstruction instruction2) {
        assertEquals(message, instruction1.getValue(),
                            instruction2.getValue());
        assertEquals(message, instruction1.getTarget(), 
                            instruction2.getTarget());
    }

    public static void assertEquals(Node expected, Node actual) {
        assertEquals(null, expected, actual);
    }
    
    public static void assertEquals(
      String message, Node expected, Node actual) {
        
        try {
            if (expected instanceof Document) {
                assertEquals(message, (Document) expected, (Document) actual);
            }
            else if (expected instanceof Element) {
                assertEquals(message, (Element) expected, (Element) actual);
            }
            else if (expected instanceof Text) {
                assertEquals(message, (Text) expected, (Text) actual);
            }
            else if (expected instanceof DocType) {
                assertEquals(message, 
                  (DocType) expected, 
                  (DocType) actual
                );
            }
            else if (expected instanceof Comment) {
                assertEquals(message, 
                  (Comment) expected, 
                  (Comment) actual
                );
            }
            else if (expected instanceof ProcessingInstruction) {
                assertEquals(message,
                  (ProcessingInstruction) expected, 
                  (ProcessingInstruction) actual
                );
            }
            else {
                throw new IllegalArgumentException(
                  "Unexpected node type " 
                  + expected.getClass().getName()
                ); 
            }
        }
        catch (ClassCastException ex) {            
            fail("Mismatched node types: " 
             + expected.getClass().getName() + " != "
             + actual.getClass().getName());
        }
        
    }
    
}
