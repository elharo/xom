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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * <p>
 *   Collect most of XOM's test cases into a single class,
 *   excepting those tests that are insanely expensive or 
 *   that fail due to Java bugs.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.1d2
 *
 */
public class XOMTests extends TestCase {

    
    public XOMTests() {
        super("All XOM Tests");   
    }

    
    public static Test suite() {
        TestSuite result = new TestSuite();
        result.addTest(new TestSuite(XOMTestCase.class));
        result.addTest(new TestSuite(XPathTest.class));
        result.addTest(new TestSuite(VerifierTest.class));
        result.addTest(new TestSuite(SubclassTest.class));
        result.addTest(new TestSuite(NodeFactoryTest.class));
        result.addTest(new TestSuite(ParentNodeTest.class));
        result.addTest(new TestSuite(LeafNodeTest.class));
        result.addTest(new TestSuite(AttributeTest.class));
        result.addTest(new TestSuite(AttributeTypeTest.class));
        result.addTest(new TestSuite(ElementTest.class));
        result.addTest(new TestSuite(CommentTest.class));
        result.addTest(new TestSuite(ProcessingInstructionTest.class));
        result.addTest(new TestSuite(DocumentTest.class));
        result.addTest(new TestSuite(DocTypeTest.class));
        result.addTest(new TestSuite(AttributesTest.class));
        result.addTest(new TestSuite(NamespacesTest.class));
        // Too slow to run routinely
        // result.addTest(new TestSuite(MegaTest.class));
        result.addTest(new TestSuite(XMLExceptionTest.class));
        result.addTest(new TestSuite(XPathExceptionTest.class));
        result.addTest(new TestSuite(ValidityExceptionTest.class));
        result.addTest(new TestSuite(ParsingExceptionTest.class));
        result.addTest(new TestSuite(XSLExceptionTest.class));
        result.addTest(new TestSuite(XIncludeExceptionTest.class));
        result.addTest(new TestSuite(CDATASectionTest.class));
        result.addTest(new TestSuite(NodesTest.class));
        // EBCDIC test fails due to bugs in the Java class library
        // result.addTest(new TestSuite(EBCDICTest.class));
        result.addTest(new TestSuite(RoundTripTest.class));
        result.addTest(new TestSuite(DOMConverterTest.class));
        result.addTest(new TestSuite(InfoTest.class));
        result.addTest(new TestSuite(SerializerTest.class));
        result.addTest(new TestSuite(CanonicalizerTest.class));
        result.addTest(new TestSuite(BuilderTest.class));
        result.addTest(new TestSuite(XIncludeTest.class));
        result.addTest(new TestSuite(SAXConverterTest.class));
        result.addTest(new TestSuite(BaseURITest.class));
        result.addTest(new TestSuite(TextTest.class));
        result.addTest(new TestSuite(XSLTransformTest.class));
        result.addTest(new TestSuite(EncodingTest.class));
        return result;
        
    }

    
}
