/* Copyright 2002-2005 Elliotte Rusty Harold
   
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

package nu.xom.tests;

import junit.framework.ComparisonFailure;
import junit.framework.TestCase;
import nu.xom.Attribute;
import nu.xom.Comment;
import nu.xom.DocType;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Namespace;
import nu.xom.Node;
import nu.xom.ProcessingInstruction;
import nu.xom.Text;


/**
 * <p>
 * Provides utility methods to compare nodes for deep equality in an 
 * infoset sense.
 * </p>
 *
 * @author Elliotte Rusty Harold
 * @version 1.1d7
 *
 */
public class XOMTestCase extends TestCase {

    /**
     * <p>
     * Create a new <code>XOMTestCase</code> with the specified name.
     * </p>
     */
    public XOMTestCase(String name) {
        super(name);   
    }  
    
    /**
     * <p>
     * Asserts that two text nodes are equal. Text nodes are considered
     * equal if they are identical char by char, or if both are null. 
     * Unicode and whitespace normalization is not performed before 
     * comparison. If the two nodes are not equal, a
     * <code>ComparisonFailure</code> is thrown.
     * </p>
     * 
     * @param expected the text the test should produce
     * @param actual the text the test does produce
     * 
     * @throws ComparisonFailure if the text nodes are not equal
     */
    public static void assertEquals(Text expected, Text actual) {
        assertEquals(null, expected, actual);
    }
    
    
    /**
     * <p>
     * Asserts that two text nodes are equal. Text nodes are considered
     * equal if they are identical char by char, or if both are null. 
     * Unicode and whitespace normalization is not performed before 
     * comparison. If the two nodes are not equal, a
     * <code>ComparisonFailure</code> is thrown with the given 
     * message.
     * </p>
     * 
     * @param message printed if the texts are not equal
     * @param expected the text the test should produce
     * @param actual the text the test does produce
     * 
     * @throws ComparisonFailure if the text nodes are not equal
     */
    public static void assertEquals(
      String message, Text expected, Text actual) {
        
        if (actual == expected) return;
        nullCheck(message, expected, actual);

        assertEquals(message, expected.getValue(), actual.getValue());
    }

    
    private static void nullCheck(String message, Node expected, Node actual) {
        
        if (expected == null) {
            throw new ComparisonFailure(message, null, actual.toXML());
        }
        else if (actual == null) {
            throw new ComparisonFailure(message, expected.toXML(), null);
        }
        
    }


    /**
     * <p>
     * Asserts that two attribute nodes are equal. 
     * Attribute nodes are considered equal if their 
     * qualified names, namespace URIs, and values
     * are equal. The type is not considered because it tends not to 
     * survive a roundtrip.  If the two nodes are not equal, a
     * <code>ComparisonFailure</code> is thrown.
     * </p>
     * 
     * <p>
     * There is special handling for the <code>xml:base</code> 
     * attribute. In order to facilitate comparison between relative 
     * and absolute URIs, two <code>xml:base</code> attributes are 
     * considered equal if one might be a relative form of the other.
     * </p>
     * 
     * @param expected the attribute the test should produce
     * @param actual the attribute the test does produce
     * 
     * @throws ComparisonFailure if the sttributes are not equal
     */
    public static void assertEquals(
      Attribute expected, Attribute actual) {
        assertEquals(null, expected, actual);   
    }

    
    /**
     * <p>
     * Asserts that two attribute nodes are equal. 
     * Attribute nodes are considered equal if their 
     * qualified names, namespace URIs, and values
     * are equal. The type is not considered because this tends not to
     * survive a roundtrip. If the two nodes are not equal, a
     * <code>ComparisonFailure</code> is thrown with the given 
     * message.
     * </p>
     * 
     * <p>
     * There is special handling for the <code>xml:base</code> 
     * attribute. In order to facilitate comparison between relative and
     * absolute URIs, two <code>xml:base</code> attributes are 
     * considered equal if one might be a relative form of the other.
     * </p>
     * 
     * @param message printed if the attributes are not equal 
     * @param expected the attribute the test should produce
     * @param actual the attribute the test does produce
     * 
     * @throws ComparisonFailure if the attributes are not equal
     */
    public static void assertEquals(
      String message, Attribute expected, Attribute actual) {
        
        if (actual == expected) return;
        nullCheck(message, expected, actual);
        
        String value1 = expected.getValue();
        String value2 = actual.getValue();
        if ("xml:base".equals(expected.getQualifiedName())) {
            // handle possibility that one is relative and other is not
            if (value1.equals(value2)) return;
            if (value1.startsWith("../")) {
                assertTrue(message, value2.endsWith(value1.substring(2)));
            }
            else {
                assertTrue(message, 
                  value1.endsWith('/' + value2) || value2.endsWith('/' + value1)); 
            }
        } 
        else { 
            assertEquals(message, value1, value2);
            assertEquals(message, expected.getLocalName(), actual.getLocalName());
            assertEquals(message,
              expected.getQualifiedName(), actual.getQualifiedName()
            );
            assertEquals(message,
              expected.getNamespaceURI(), actual.getNamespaceURI()
            );
        }

    }

    
    /**
     * <p>
     * Asserts that two <code>DocType</code> nodes are equal. 
     * <code>DocType</code> nodes are considered equal if their 
     * root element names, public IDs, and system IDs
     * are equal. The internal DTD subsets are not considered. 
     * If the two nodes are not equal, a
     * <code>ComparisonFailure</code> is thrown.
     * </p>
     * 
     * @param expected the DOCTYPE declaration the test should produce
     * @param actual the DOCTYPE declaration the test does produce
     *
     * @throws ComparisonFailure if the document type declarations 
     *     are not equal
     */
    public static void assertEquals(DocType expected, DocType actual) {
        assertEquals(null, expected, actual);
    }

    
    /**
     * <p>
     * Asserts that two <code>DocType</code> nodes are equal. 
     * <code>DocType</code> nodes are considered equal if their 
     * root element name, public ID, and system ID
     * are equal. The internal DTD subsets are not considered. 
     * If the two nodes are not equal, a
     * <code>ComparisonFailure</code> is thrown with the given 
     * message.
     * </p>
     * 
     * @param message printed if the DOCTYPE declarations are not equal
     * @param expected the DOCTYPE declaration the test should produce
     * @param actual the DOCTYPE declaration the test does produce
     *
     * @throws ComparisonFailure if the document type declarations 
     *     are not equal
     *
     */
    public static void assertEquals(
      String message, DocType expected, DocType actual) {
        
        if (actual == expected) return;
        nullCheck(message, expected, actual);

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

    
    /**
     * <p>
     * Asserts that two element nodes are equal. 
     * Element nodes are considered equal if their 
     * qualified names, namespace URI, attributes,
     * declared namespaces, and children
     * are equal. Consecutive text node children are coalesced
     * before the comparison is made. If the two nodes are not equal, 
     * a <code>ComparisonFailure</code> is thrown.
     * </p>
     * 
     * @param expected the element the test should produce
     * @param actual the element the test does produce
     *
     * @throws ComparisonFailure if the elements are not equal
     */
    public static void assertEquals(
      Element expected, Element actual) {
        assertEquals(null, expected, actual);

    }

    
    /**
     * <p>
     * Asserts that two element nodes are equal. 
     * Element nodes are considered equal if their 
     * qualified names, namespace URI, attributes,
     * declared namespaces, and children
     * are equal. Consecutive text node children are coalesced
     * before the comparison is made. If the two nodes are not equal, 
     * a <code>ComparisonFailure</code> is thrown with the given 
     * message.
     * </p>
     * 
     * @param message printed if the elements are not equal
     * @param expected the element the test should produce
     * @param actual the element the test does produce
     *
     * @throws ComparisonFailure if the elements are not equal
     */
    public static void assertEquals(String message,
      Element expected, Element actual) {
        
        if (actual == expected) return;
        nullCheck(message, expected, actual);

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

        // Check declared namespaces by listing all the prefixes
        // on element1 and making sure element2 gives the same value
        // for those prefixes, and vice versa. This is necessary
        // to handle a few weird cases that arise in XInclude
        // when prefixes are declared multiple times, to account for
        // the fact that some serializers may drop redundant
        // namespace declarations.
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
            assertNotNull(message, expected.getNamespaceURI(prefix1));
            assertEquals(message,
              uri1, expected.getNamespaceURI(prefix1)
            );                      
        }
        
        if (hasAdjacentTextNodes(expected)) expected = combineTextNodes(expected);
        if (hasAdjacentTextNodes(actual)) actual = combineTextNodes(actual);
        compareChildren(message, expected, actual);

    }
    
    
    private static boolean hasAdjacentTextNodes(Element element) {

        boolean previousWasText = false;
        for (int i = 0; i < element.getChildCount(); i++) {
            Node child = element.getChild(i);
            if (child instanceof Text) {
                if (previousWasText) return true;
                else previousWasText = true;
            }
        }
        return false;
        
    }

    private static void compareChildren(String message, Element expected, Element actual) {

        assertEquals(message,
          expected.getChildCount(), actual.getChildCount());
        for (int i = 0; i < expected.getChildCount(); i++) {
            Node child1 = expected.getChild(i);
            Node child2 = actual.getChild(i);
            assertEquals(message, child1, child2);
        }
        
    }

    private static Element combineTextNodes(Element element) {

        element = (Element) element.copy();
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
        return element;
        
    }

    
    /**
     * <p>
     * Asserts that two document nodes are equal. 
     * Document nodes are considered equal if their 
     * children are equal. If the two nodes are not equal, 
     * a <code>ComparisonFailure</code> is thrown.
     * </p>
     * 
     * @param expected the document the test should produce
     * @param actual the document the test does produce
     *
     * @throws ComparisonFailure if the documents are not equal
     */
    public static void assertEquals(
      Document expected, Document actual) {       
        assertEquals(null, expected, actual);
    }
    
    
    /**
     * <p>
     * Asserts that two document nodes are equal. 
     * Document nodes are considered equal if their 
     * children are equal. If the two nodes are not equal, 
     * a <code>ComparisonFailure</code> is thrown with the given 
     * message.
     * </p>
     * 
     * @param message printed if the documents are not equal
     * @param expected the document the test should produce
     * @param actual the document the test does produce
     *
     * @throws ComparisonFailure if the documents are not equal
     */
    public static void assertEquals(
      String message, Document expected, Document actual) {       

        if (actual == expected) return;
        nullCheck(message, expected, actual);

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

    
     /**
     * <p>
     * Asserts that two comment nodes are equal. Comment nodes are 
     * considered equal if they are identical char by char, or if both  
     * are null.  Unicode and whitespace normalization is not performed 
     * before comparison. If the two nodes are not equal, a
     * <code>ComparisonFailure</code> is thrown.
     * </p>
     * 
     * @param expected the comment the test should produce
     * @param actual the comment the test does produce
     *
     * @throws ComparisonFailure if the comments are not equal
     */
    public static void assertEquals(Comment expected, Comment actual) {
        assertEquals(null, expected, actual);
    }
    
    
   /**
     * <p>
     * Asserts that two comment nodes are equal. Comment nodes are considered
     * equal if they are identical char by char, or if both are null. 
     * Unicode and whitespace normalization is not performed before 
     * comparison. If the two nodes are not equal, a
     * <code>ComparisonFailure</code> is thrown with the given 
     * message.
     * </p>
     * 
     * @param message printed if the comments are not equal
     * @param expected the comment the test should produce
     * @param actual the comment the test does produce
     *
     * @throws ComparisonFailure if the comments are not equal
     */
    public static void assertEquals(
      String message, Comment expected, Comment actual) {
        
        if (actual == expected) return;
        nullCheck(message, expected, actual);
        assertEquals(message, expected.getValue(), actual.getValue());
        
    }
    
    
    /**
     * <p>
     * Asserts that two processing instruction nodes are equal.
     * Processing instruction nodes are considered
     * equal if they have the same target and the same value. 
     * If the two nodes are not equal, a
     * <code>ComparisonFailure</code> is thrown.
     * </p>
     * 
     * @param expected the processing instruction the test should produce
     * @param actual the processing instruction the test does produce
     *
     * @throws ComparisonFailure if the processing instructions 
     *     are not equal
     */
    public static void assertEquals(ProcessingInstruction expected, 
      ProcessingInstruction actual) {
        assertEquals(null, expected, actual);
    }
    
    
    /**
     * <p>
     * Asserts that two processing instruction nodes are equal.
     * Processing instruction nodes are considered
     * equal if they have the same target and the same value. 
     * If the two nodes are not equal, a
     * <code>ComparisonFailure</code> is thrown with the given 
     * message.
     * </p>
     * 
     * @param message printed if the processing instructions are 
     *     not equal
     * @param expected the processing instruction the test 
     *     should produce
     * @param actual the processing instruction the test does produce
     *
     * @throws ComparisonFailure if the processing instructions 
     *     are not equal
     */
    public static void assertEquals(String message, 
      ProcessingInstruction expected, 
      ProcessingInstruction actual) {

        if (actual == expected) return;
        nullCheck(message, expected, actual);

        assertEquals(message, expected.getValue(), actual.getValue());
        assertEquals(message, expected.getTarget(), actual.getTarget());
        
    }

    
    /**
     * <p>
     * Asserts that two namespace nodes are equal.
     * Namespace nodes are considered
     * equal if they have the same prefix and the same URI. 
     * If the two nodes are not equal, a
     * <code>ComparisonFailure</code> is thrown with the given 
     * message.
     * </p>
     * 
     * @param message printed if the namespaces are not equal
     * @param expected the namespace the test should produce
     * @param actual the namespace the test does produce
     *
     * @throws ComparisonFailure if the namespaces are not equal
     */
    public static void assertEquals(String message, 
      Namespace expected, Namespace actual) {

        if (actual == expected) return;
        nullCheck(message, expected, actual);

        assertEquals(message, expected.getValue(), actual.getValue());
        assertEquals(message, expected.getPrefix(), actual.getPrefix());
        
    }

    
    /**
     * <p>
     * Asserts that two nodes are equal. If the two nodes are not 
     * equal a <code>ComparisonFailure</code> is thrown. 
     * The subclass is not considered. The basic XOM class
     * is considered, but the subclass is not. For example,
     * a <code>Text</code> object can be equal to an object that
     * is an <code>HTMLText</code>, but it can never be equal to
     * a <code>Comment</code>.
     * </p>
     * 
     * @param expected the node the test should produce
     * @param actual the node the test does produce
     * 
     * @throws ComparisonFailure if the nodes are not equal
     */
    public static void assertEquals(Node expected, Node actual) {
        assertEquals(null, expected, actual);
    }
    
    
    /**
     * <p>
     * Asserts that two nodes are equal. If the two nodes are not 
     * equal a <code>ComparisonFailure</code> is thrown with the given 
     * message. The subclass is not considered. The basic XOM class
     * is considered, but the subclass is not. For example,
     * a <code>Text</code> object can be equal to an an 
     * <code>HTMLText</code> object, but it can never be equal to
     * a <code>Comment</code>.
     * </p>
     * 
     * @param message printed if the nodes are not equal
     * @param expected the node the test should produce
     * @param actual the node the test does produce
     * 
     * @throws ComparisonFailure if the nodes are not equal
     */
    public static void assertEquals(
      String message, Node expected, Node actual) {
        
        if (actual == expected) return;
        nullCheck(message, expected, actual);

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
            else if (expected instanceof Attribute) {
                assertEquals(message,
                  (Attribute) expected, 
                  (Attribute) actual
                );
            }
            else if (expected instanceof Namespace) {
                assertEquals(message,
                  (Namespace) expected, 
                  (Namespace) actual
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
            throw new ComparisonFailure(message 
              + "; Mismatched node types: " 
              + expected.getClass().getName() + " != "
              + actual.getClass().getName(), 
              expected.toXML(), actual.toXML());
        }
        
    }
   
    
}
