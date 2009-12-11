/* Copyright 2003, 2004 Elliotte Rusty Harold
   
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

import nu.xom.Attribute;
import nu.xom.Comment;
import nu.xom.DocType;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.ProcessingInstruction;
import nu.xom.Text;


/**
 * <p>
 *  Tests for subclasses of XOM classes.
 *  This makes sure XOM is sufficiently polymorphic.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0
 *
 */
public class SubclassTest extends XOMTestCase {

    private Element root;
    private Document doc;
    
    
    public SubclassTest(String name) {
        super(name);
    }

    
    protected void setUp() {
        root = new Element("root");
        doc = new Document(new ElementSubclass("root"));   
    }
    
    
    public void testAttributeClassInCopy() {
        root.addAttribute(new AttributeSubclass("name", "value"));
        assertTrue(root.getAttribute(0) instanceof AttributeSubclass);
        Element copy = (Element) root.copy();
        assertTrue(copy.getAttribute(0) instanceof AttributeSubclass);
    }
    
    
    private class AttributeSubclass extends Attribute {
        
        AttributeSubclass(String name, String value) {
            super(name, value);   
        }
        
        public Node copy() {
            return new AttributeSubclass(this.getQualifiedName(), this.getValue());   
        }
        
    } 

    
    public void testTextClassInCopy() {
        root.appendChild(new TextSubclass("value"));
        assertTrue(root.getChild(0) instanceof TextSubclass);
        Element copy = (Element) root.copy();
        assertTrue(copy.getChild(0) instanceof TextSubclass);
    }
    
    
    private class TextSubclass extends Text {
        
        TextSubclass(String value) {
            super(value);   
        }
        
        public Node copy() {
            return new TextSubclass(this.getValue());   
        }        
    } 

    
    public void testElementClassInCopy() {
        root.appendChild(new ElementSubclass("child"));
        assertTrue(root.getChild(0) instanceof ElementSubclass);
        Element copy = (Element) root.copy();
        assertTrue(copy.getChild(0) instanceof ElementSubclass);
    }
    
    
    private class ElementSubclass extends Element {
        
        ElementSubclass(String name) {
            super(name);   
        }

        protected Element shallowCopy() {
            return new ElementSubclass(this.getQualifiedName());   
        }
        
    } 


    public void testCommentClassInCopy() {
        root.appendChild(new CommentSubclass("value"));
        assertTrue(root.getChild(0) instanceof CommentSubclass);
        Element copy = (Element) root.copy();
        assertTrue(copy.getChild(0) instanceof CommentSubclass);
    }
    
    
    private class CommentSubclass extends Comment {
        
        CommentSubclass(String value) {
            super(value);   
        }
        
        public Node copy() {
            return new CommentSubclass(this.getValue());   
        }
        
    } 

    
    private class DocTypeSubclass extends DocType {
        
        DocTypeSubclass(String name) {
            super(name);   
        }

        public Node copy() {
            return new DocTypeSubclass(this.getRootElementName());   
        }        
    } 

    
    public void testProcessingInstructionClassInCopy() {
        root.appendChild(new ProcessingInstructionSubclass("target", "value"));
        assertTrue(root.getChild(0) instanceof ProcessingInstructionSubclass);
        Element copy = (Element) root.copy();
        assertTrue(copy.getChild(0) instanceof ProcessingInstructionSubclass);
    }
    
    
    private class ProcessingInstructionSubclass extends ProcessingInstruction {
        
        ProcessingInstructionSubclass(String target, String data) {
            super(target, data);   
        }
        
        public Node copy() {
            return new ProcessingInstructionSubclass(this.getTarget(), this.getValue());   
        }
        
    } 
    
    
    public void testProcessingInstructionClassInDocCopy() {
        doc.insertChild(new ProcessingInstructionSubclass("target", "value"), 0);
        assertTrue(doc.getChild(0) instanceof ProcessingInstructionSubclass);
        Document copy = (Document) doc.copy();
        assertTrue(copy.getChild(0) instanceof ProcessingInstructionSubclass);
    }
    
    
    public void testCommentClassInDocCopy() {
        doc.insertChild(new CommentSubclass("target"), 0);
        assertTrue(doc.getChild(0) instanceof CommentSubclass);
        Document copy = (Document) doc.copy();
        assertTrue(copy.getChild(0) instanceof CommentSubclass);
    }
    
    
    public void testElementClassInDocCopy() {
        assertTrue(doc.getChild(0) instanceof ElementSubclass);
        Document copy = (Document) doc.copy();
        assertTrue(copy.getChild(0) instanceof ElementSubclass);
    }
    
    
    public void testDocTypeClassInDocCopy() {
        doc.insertChild(new DocTypeSubclass("root"), 0);
        assertTrue(doc.getChild(0) instanceof DocTypeSubclass);
        Document copy = (Document) doc.copy();
        assertTrue(copy.getChild(0) instanceof DocTypeSubclass);
    }

    
}
