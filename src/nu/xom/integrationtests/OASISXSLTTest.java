/* Copyright 2002-2006, 2009, 2018 Elliotte Rusty Harold
   
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
   elharo@ibiblio.org. Please include the word "XOM" in the
   subject line. The XOM home page is located at https://xom.nu/
*/
package nu.xom.integrationtests;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.UnknownHostException;
import java.util.MissingResourceException;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.IllegalAddException;
import nu.xom.MalformedURIException;
import nu.xom.NodeFactory;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import nu.xom.Text;
import nu.xom.ValidityException;
import nu.xom.XMLException;
import nu.xom.tests.XOMTestCase;
import nu.xom.xslt.XSLException;
import nu.xom.xslt.XSLTransform;

/**
 * <p>
 * Integration tests for the XSLT engine based on the OASISXSLT test suite
 * which is not bundled and must be separately installed.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.2.11
 *
 */
public class OASISXSLTTest extends XOMTestCase {

    public OASISXSLTTest(String name) {
        super(name);   
    }
    
    
    // This class tests a lot of error conditions, which
    // Xalan annoyingly logs to System.err. This hides System.err 
    // before each test and restores it after each test.
    private PrintStream systemErr = System.err;
    
    private File inputDir;
    
    protected void setUp() {
        
        System.setErr(new PrintStream(new ByteArrayOutputStream()));
        
        inputDir = new File("data");
        inputDir = new File(inputDir, "xslt");
        inputDir = new File(inputDir, "input");
        
    } 
    
    
    protected void tearDown() {
        System.setErr(systemErr);
    }  

    
    /**
     * This test fails when run with Java 1.4.2. It passes with 1.5 or 1.6.
     * I think there's some sort of bug in the XML/XSL libraries bundled with 1.4.2.
     * This bug can also get triggered if the 1.4.2 classes somehow get loaded 
     * into a 1.5 or later VM, as I've seen happen on occasion when running this 
     * as part of the complete test suite. In particular, the test in DocTypeTest
     * that loads Crimson may cause this test to fail. 
     */
    public void testKeysPerfRepro3()  
      throws IOException, ParsingException, XSLException {
        
        Builder builder = new Builder();
        File base = new File("data");
        base = new File(base, "oasis-xslt-testsuite");
        base = new File(base, "TESTS");

        File input = new File(base, "MSFT_CONFORMANCE_TESTS/KEYS/input.xml");
        File style = new File(base, "MSFT_CONFORMANCE_TESTS/KEYS/input.xsl");
        File output = new File(base, "MSFT_CONFORMANCE_TESTS/KEYS/out/PerfRepro3.txt");
 
        Document styleDoc = builder.build(style);
        Document inputDoc = builder.build(input);
        XSLTransform xform = new XSLTransform(styleDoc);
        Nodes result = xform.transform(inputDoc);
        Document expectedResult = builder.build(output);
        Document actualResult = XSLTransform.toDocument(result);
        assertEquals(expectedResult, actualResult);
     
    } 
    
    
    /**
     * Like the previous test, this test fails when run with Java 1.4.2. 
     * It passes with 1.5 or 1.6.
     */
    public void testAxes_Axes62()  
      throws IOException, ParsingException, XSLException { 
        xalanTestCase("axes/axes62");
    }
    
    

    private static boolean indentYes(Document styleDoc) {
        
        Element output = styleDoc
          .getRootElement()
          .getFirstChildElement("output", 
             "http://www.w3.org/1999/XSL/Transform");
        if (output == null) return false;
        
        String indent = output.getAttributeValue("indent");
        if ("yes".equals(indent)) {
            return true;
        }
        else return false;
        
    }
    
    
    private static class StrippingFactory extends NodeFactory {
    
        public Nodes makeText(String s) {
            
            String stripped = stripSpace(s);
            if (stripped.length() == 0) return new Nodes();
            Text result = new Text(stripped);
            return new Nodes(result);
        }
        
        public Nodes makeAttribute(String name, String URI, 
          String value, Attribute.Type type) {
            return new Nodes(new Attribute(name, URI, stripSpace(value), type));
        }        
        
        private String stripSpace(String s) {
            
        	StringBuilder sb = new StringBuilder();
            for (int i = 0; i < s.length(); i++) {
                if (!Character.isWhitespace(s.charAt(i))) {
                    sb.append(s.charAt(i));
                }
            }
            
            return sb.toString();
            
        }
        
    }

    // XXX need to upgrade to final release of XSLT test suites
    // and then split into individual tests
    public void testOASISXalanConformanceSuite()  
      throws IOException, ParsingException, XSLException {
        Builder builder = new Builder();
        NodeFactory stripper = new StrippingFactory();
        Builder strippingBuilder = new Builder(stripper);
        
        File base = new File("data");
        base = new File(base, "oasis-xslt-testsuite");
        base = new File(base, "TESTS");
        base = new File(base, "Xalan_Conformance_Tests");
        File catalog = new File(base, "catalog.xml");
        
        // The test suite needs to be installed separately. If we can't
        // find the catalog, we just don't run these tests.
        if (catalog.exists()) {
            Document doc = builder.build(catalog);
            Element testsuite = doc.getRootElement();
            Elements submitters = testsuite.getChildElements("test-catalog");
            for (int i = 0; i < submitters.size(); i++) {
                Element submitter = submitters.get(i);
                Elements testcases = submitter.getChildElements("test-case");
                for (int j = 0; j < testcases.size(); j++) {
                    
                    Element testcase = testcases.get(j);
                    String id = testcase.getAttributeValue("id");
                    if (id.startsWith("output_")) {
                        // These test cases are mostly about producing 
                        // HTML and plain text output that isn't 
                        // relevant to XOM
                        continue;
                    }
                    else if (id.equals("axes_axes62")) {
                        // Possible Xalan bug. Pulled out into 
                        // separate test method and investigating
                        continue;
                    }
                    else if (id.equals("select_select85")) {
                        // Same possible Xalan bug. Pulled out into 
                        // separate test method and investigating
                        continue;
                    }
                    File root = new File(base, testcase.getFirstChildElement("file-path").getValue());
                    File input = null;
                    File style = null;
                    File output = null;
                    Element scenario = testcase.getFirstChildElement("scenario");
                    Elements inputs = scenario.getChildElements("input-file");
                    for (int k = 0; k < inputs.size(); k++) {
                        Element file = inputs.get(k);
                        String role = file.getAttributeValue("role");
                        if ("principal-data".equals(role)) {
                            input = new File(root, file.getValue());
                        }
                        else if ("principal-stylesheet".equals(role)) {
                            style = new File(root, file.getValue());
                        }
                    }
                    Elements outputs = scenario.getChildElements("output-file");
                    for (int k = 0; k < outputs.size(); k++) {
                        Element file = outputs.get(k);
                        String role = file.getAttributeValue("role");
                        if ("principal".equals(role)) {
                            // Fix up OASIS catalog bugs
                            File parent = new File(root.getParent());
                            parent = new File(parent, "REF_OUT");
                            parent = new File(parent, root.getName());
                            String outputFileName = file.getValue();
                            output = new File(parent, outputFileName);
                        }
                    }
                    
                    try {
                        Document inputDoc = builder.build(input);
                        Document styleDoc = builder.build(style);
                        // If the transform specifies indent="yes".
                        // we remove all white space before comparing
                        XSLTransform xform;
                        if (indentYes(styleDoc)) {
                            xform = new XSLTransform(styleDoc, stripper);
                        }
                        else xform = new XSLTransform(styleDoc);
                        Nodes result = xform.transform(inputDoc);
                        if (output == null) {
                            // transform should have failed
                            fail("Transformed " + id);
                        }
                        else { 
                            try {
                                Document expectedResult;
                                if (indentYes(styleDoc)) {
                                    expectedResult = strippingBuilder.build(output);
                                }
                                else {
                                    expectedResult = builder.build(output);
                                }
                                Document actualResult = XSLTransform.toDocument(result);
                                
                                if (id.equals("attribset_attribset40")) {
                                    // This test does not necessarily 
                                    // produce an identical infoset due
                                    // to necessary remapping of 
                                    // namespace prefixes.
                                    continue;
                                }
                                else if (id.equals("axes_axes129")) {
                                    // Xalan bug. Fixed in more recent 
                                    // version than bundled with the JDK 1.4.2_05
                                }
                                else if (id.equals("copy_copy56") 
                                  || id.equals("copy_copy58")
                                  || id.equals("copy_copy60")
                                  || id.equals("copy_copy59")) {
                                    // Xalan bug;
                                    // See http://issues.apache.org/jira/browse/XALANJ-1081
                                    // Also see erratum E27 to the XSLT spec.
                                } 
                                else if (id.equals("expression_expression02")) {
                                    // requires unparsed entities XOM doesn't support
                                } 
                                else if (id.equals("idkey_idkey31")) {
                                    // Known Xalan bug
                                    // See http://issues.apache.org/jira/browse/XALANJ-1325
                                } 
                                else if (id.equals("idkey_idkey61")
                                  || id.equals("idkey_idkey62")) {
                                    // Xalan bug. Fixed in more recent 
                                    // version than bundled with the JDK 1.4.2_05
                                    // See http://issues.apache.org/jira/browse/XALANJ-1318
                                } 
                                else if (id.equals("impincl_impincl11")) {
                                    // Test case bug; reported 2004-09-18
                                    // See http://lists.oasis-open.org/archives/xslt-conformance-comment/200409/msg00001.html
                                }
                                else if (id.equals("math_math110")
                                  || id.equals("math_math111")) {
                                    // Xalan bug. Fixed in more recent 
                                    // version than bundled with the JDK 1.4.2_05
                                    // See http://issues.apache.org/jira/browse/XALANJ-1278
                                }
                                else if (id.equals("numbering_numbering17")
                                  || id.equals("numbering_numbering79")) {
                                    // test suite bug per XSLT 1.0 erratum 24. See
                                    // See http://issues.apache.org/jira/browse/XALANJ-1979
                                }
                                else if (id.equals("position_position104")) {
                                    // Xalan bug. Fixed in more recent 
                                    // version than bundled with the JDK 1.4.2_05
                                }
                                else if (id.equals("position_position106")) {
                                    // Xalan bug. Fixed in more recent 
                                    // version than bundled with the JDK 1.4.2_05
                                }
                                else if (id.equals("position_position107")
                                  || id.equals("position_position109")) {
                                    // Xalan bug. Fixed in more recent 
                                    // version than bundled with the JDK 1.4.2_05
                                    // See http://issues.apache.org/jira/browse/XALANJ-1289
                                } 
                                else {
                                    assertEquals("Problem with " + id,
                                      expectedResult, actualResult);
                                }
                            }
                            catch (ParsingException ex) {  
                                // a few of the test cases generate 
                                // text or HTML output rather than 
                                // well-formed XML. For the moment, I 
                                // just skip these.
                                continue;
                            }
                            catch (IllegalAddException ex) {
                                // A few of the test cases generate 
                                // incomplete documents so we can't
                                // compare output. Perhaps I could
                                // wrap in an element, then get children
                                // to build a Nodes object rather than a
                                // Document???? i.e. a fragment parser?
                                // Could use a SequenceInputStream to hack this
                            }
                        }
                        
                    }
                    catch (ParsingException ex) {
                        // Some of the test cases contain relative 
                        // namespace URIs XOM does not support
                        if (ex.getCause() instanceof MalformedURIException) continue;
                        throw ex;
                    }
                    catch (XSLException ex) {
                        // If the output was null the transformation 
                        // was expected to fail
                        if (output != null) {
                            // a few of the test cases use relative namespace URIs
                            // XOM doesn't support
                            Throwable cause = ex.getCause();
                            if (cause instanceof MalformedURIException) {
                                continue;
                            }
                            
                            if ("impincl_impincl27".equals(id)) {  
                                // Test case uses file: URI XOM doesn't support
                                continue;
                            }
                            else if ("numberformat_numberformat45".equals(id)
                              || "numberformat_numberformat46".equals(id)) {  
                                // This has been fixed in Xalan 2.5.2.
                                // However, it's a bug in earlier versions of Xalan
                                // including the one bundled with the JDK 1.4.2_05
                                // See http://issues.apache.org/jira/browse/XALANJ-805
                                // XXX I think this might need an updated version of the test cases
                                // and expected output at this point.
                                continue;
                            }
                            
                            System.out.println(id);
                            System.out.println(ex.getMessage());
                            throw ex;
                        }
                    }
                    
                }
            } 
            
        }
     
    } 
    
    public void testOASISMicrosoftConformanceSuite()  
      throws IOException, ParsingException, XSLException {
        Builder builder = new Builder();
        NodeFactory stripper = new StrippingFactory();
        Builder strippingBuilder = new Builder(stripper);
        File base = new File("data");
        base = new File(base, "oasis-xslt-testsuite");
        base = new File(base, "TESTS");
        File catalog = new File(base, "catalog.xml");
        
        // The test suite need to be installed separately. If we can't
        // find the catalog, we just don't run these tests.
        if (catalog.exists()) {
            Document doc = builder.build(catalog);
            Element testsuite = doc.getRootElement();
            Elements submitters = testsuite.getChildElements("test-catalog");
            Element submitter = submitters.get(1);
            Elements testcases = submitter.getChildElements("test-case");
            for (int j = 0; j < testcases.size(); j++) {
                Element testcase = testcases.get(j);
                String id = testcase.getAttributeValue("id");
                File root = new File(base, "MSFT_Conformance_Tests");
                root = new File(root, testcase.getFirstChildElement("file-path").getValue());
                File input = null;
                File style = null;
                File output = null;
                Element scenario = testcase.getFirstChildElement("scenario");
                Elements inputs = scenario.getChildElements("input-file");
                for (int k = 0; k < inputs.size(); k++) {
                    Element file = inputs.get(k);
                    String role = file.getAttributeValue("role");
                    if ("principal-data".equals(role)) {
                        input = new File(root, file.getValue());
                    }
                    else if ("principal-stylesheet".equals(role)) {
                        style = new File(root, file.getValue());
                    }
                }  // end for 
                Elements outputs = scenario.getChildElements("output-file");
                for (int k = 0; k < outputs.size(); k++) {
                    Element file = outputs.get(k);
                    String role = file.getAttributeValue("role");
                    if ("principal".equals(role)) {
                        // Fix up OASIS catalog bugs
                        File parent = new File(root.getParent());
                        parent = new File(parent, "REF_OUT");
                        parent = new File(parent, root.getName());
                        String outputFileName = file.getValue();
                        output = new File(parent, outputFileName);
                    }
                }  // end for 
                
                try {
                    Document styleDoc = builder.build(style);
                    boolean strip = indentYes(styleDoc);
                    if ("BVTs_bvt002".equals(id) || "BVTs_bvt077".equals(id)) {
                        // This has been fixed at least as of Xalan 2.6.0.
                        // However, it's a bug in earlier versions of Xalan
                        // including the one bundled with the JDK 1.4.2_05
                        continue;
                    } 
                    else if ("XSLTFunctions_Bug76984".equals(id)) {
                        // This has been fixed at least as of Xalan 2.6.0.
                        // However, it's a bug in earlier versions of Xalan
                        // including the one bundled with the JDK 1.4.2_05
                        continue;
                    } 
                    else if ("BVTs_bvt020".equals(id) || "BVTs_bvt022".equals(id)
                      || "BVTs_bvt024".equals(id) || "BVTs_bvt058".equals(id)) {
                        // Either a test suite bug, or a recoverable 
                        // error Xalan doesn't recover from.
                        continue;
                    } 
                    else if ("BVTs_bvt038".equals(id) 
                      || "Namespace-alias__91785".equals(id)
                      || "Namespace-alias__91786".equals(id)) {
                        // a recoverable error Xalan doesn't recover from properly
                        // http://issues.apache.org/jira/browse/XALANJ-1957
                        continue;
                    } 
                    else if ("Namespace_XPath_CopyNamespaceNodeToOutput".equals(id)) {
                        // Xalan bug
                        // http://issues.apache.org/jira/browse/XALANJ-1959
                        continue;
                    } 
                    else if ("Namespace-alias_Namespace-Alias_WithinRTF".equals(id)) {
                        // Xalan bug
                        // http://issues.apache.org/jira/browse/XALANJ-1960
                        continue;
                    } 
                    else if ("Completeness__84361".equals(id) 
                      || "Namespace-alias__91781".equals(id)
                      || "Namespace-alias__91782".equals(id)
                      || "Namespace-alias_Namespace-Alias_Test1".equals(id)
                      || "Namespace-alias_Namespace-Alias_Test2".equals(id)
                      ) {
                        // a recoverable error Xalan doesn't recover from
                        continue;
                    } 
                    else if ("Output__84008".equals(id)) {
                        // a recoverable error Xalan doesn't recover from
                        continue;
                    } 
                    else if ("XSLTFunctions_ElementAvailFunctionFalseTest".equals(id)) {
                        // Xalan bug
                        // http://issues.apache.org/jira/browse/XALANJ-1961
                        continue;
                    } 
                    else if ("XSLTFunctions_GenereateIdAppliedToNamespaceNodesOnDifferentElements".equals(id)) {
                        // Xalan bug
                        // http://issues.apache.org/jira/browse/XALANJ-1962
                        continue;
                    } 
                    else if ("XSLTFunctions__specialCharInPattern".equals(id)) {
                        // a recoverable error Xalan doesn't recover from
                        continue;                        
                    }
                    else if ("XSLTFunctions_DocumentFunctionWithAbsoluteArgument".equals(id)) {
                        // test case bug; bad URL passed to document function
                        continue;
                    }
                    else if ("BVTs_bvt052".equals(id) || "Keys_PerfRepro2".equals(id)) {
                        // Requires a non-standard extension function
                        continue;
                    } 
                    else if ("Keys_PerfRepro3".equals(id)) {
                        // Moved to a separate test case; investigating state dependent 
                        // test failure; doesn't fail in isolation; only fails
                        // when run as part of FastTests
                        continue;
                    } 
                    else if ("BVTs_bvt044".equals(id)) {
                        // a recoverable error Xalan doesn't recover from
                        // http://issues.apache.org/jira/browse/XALANJ-1957
                        continue;
                    } 
                    else if ("BVTs_bvt039".equals(id)) {
                        // Xalan bug
                        continue;
                    } 
                    else if ("BVTs_bvt033".equals(id) || "BVTs_bvt034".equals(id)) {
                        // Test suite bug; 2.0 is not unrecognized
                        continue;
                    } 
                    else if ("Text__78274".equals(id) || "Text__78276".equals(id)) {
                        // Test suite bug; no xsl:preserve-space attribute
                        continue;                           
                    }
                    else if ("XSLTFunctions__minimumValue".equals(id)
                     || "XSLTFunctions__minimalValue".equals(id)) {
                        // test suite bug
                        continue;
                    } 
                    else if ("Errors_err073".equals(id)) {
                        // Xalan bug: StackOverflowError
                        continue;
                    } 
                    else if ("Sorting_SortExprWithCurrentInsideForEach1".equals(id)) {
                        // Xalan bug
                        // http://issues.apache.org/jira/browse/XALANJ-1970
                        continue;
                    }
                    else if ("BVTs_bvt041".equals(id) || "BVTs_bvt063".equals(id)
                        || "BVTs_bvt070".equals(id)) {
                        // Xalan bundled with JDK 1.4.2_05 does not recover 
                        // from this error involving multiple conflicting 
                        // xsl:output at same import precedence, though
                        // 2.6.0 does
                        continue;
                    } 
                    Document inputDoc = builder.build(input);
                    XSLTransform xform;
                    if (strip) xform = new XSLTransform(styleDoc, stripper);
                    else xform = new XSLTransform(styleDoc);
                    Nodes result = xform.transform(inputDoc);
                    if (output == null) {
                        if ("Attributes__89463".equals(id)
                          || "Attributes__89465".equals(id)) {
                            // Processors are allowed to recover from
                            // this problem.
                            assertEquals(0, result.size());
                        }
                        else if ("Attributes__89464".equals(id)) {
                            // Processors are allowed to recover from
                            // this problem.
                            assertEquals(0, ((Element) result.get(0)).getAttributeCount());
                        }
                        else if ("Namespace-alias__91772".equals(id)
                          || "Namespace-alias__91774".equals(id)
                          || "Namespace-alias__91780".equals(id)
                          || "Namespace-alias__91790".equals(id)
                          || "Namespace-alias__91791".equals(id)
                          || "Sorting__84006".equals(id)
                          || "Sorting__91754".equals(id)
                          ) {
                            // Processors are allowed to recover from
                            // this problem.
                            continue;
                        }
                        else if (id.startsWith("Errors_")) {
                            // Processors are allowed to recover from
                            // most of these problems.
                        }
                        else if (id.startsWith("FormatNumber")) {
                            // Processors are allowed to recover from
                            // most of these problems.
                        }
                        else if ("BVTs_bvt074".equals(id)) {
                            // Processors are allowed to recover from
                            // this problem.
                            assertEquals(0, result.get(0).getChildCount());
                        }
                        else if ("XSLTFunctions__currency".equals(id)
                          || "XSLTFunctions__mixingInvalids".equals(id)) {
                            // Processors are allowed to recover from
                            // this problem.
                            continue;
                        }
                        else if ("Attributes_Attribute_UseXmlnsNsAsNamespaceForAttribute".equals(id)
                          || "Attributes_Attribute_UseXmlnsAsNamespaceForAttributeImplicitly".equals(id)
                          || "Elements_Element_UseXslElementWithNameSpaceAttrEqualToXmlnsUri".equalsIgnoreCase(id)
                          || "Elements_Element_UseXslElementWithNameSpaceEqualToXmlnsUri".equalsIgnoreCase(id)
                          ) {
                            // test follows namespace errata we don't accept
                        }
                        else if ("AttributeSets_RefToUndefinedAttributeSet".equals(id)) {
                            // I think the test case is wrong; I see 
                            // nothing in the spec that says this is
                            // an error.
                        }
                        else if ("Namespace__77665".equals(id)
                          || "Namespace__77675".equals(id)) {
                            // I think the test case is wrong; I see 
                            // nothing in the spec that says this is
                            // an error. See
                            // http://lists.oasis-open.org/archives/xslt-conformance-comment/200409/msg00007.html
                        }
                        else if ("Variables__84633".equals(id)
                          || "Variables__84634".equals(id)
                          || "Variables__84697".equals(id)
                          || "Variables__84710".equals(id)
                          ) {
                            // An error. See 11.4
                            // but are processors allowed to recover?
                            // Hmm according to section 17, the 
                            // processor must signal these errors
                            // and may but need not recover from them. 
                            // Xalan recovers. Microsoft doesn't.
                        }
                        else if ("Output__78176".equals(id)) {
                            // I think the test case is wrong; I see 
                            // nothing in the spec that says this is
                            // an error.
                        }
                        else if (id.startsWith("XSLTFunctions__100")) {
                            // I think these test cases are all wrong  
                            // except perhaps XSLTFunctions__10026; I  
                            // see nothing in the spec that says this 
                            // is an error. These are all about the 
                            // unparsed-entity-uri function.
                        }
                        else if ("Namespace__78027".equals(id)) {
                            // Test case is incorrect. This should 
                            // operate in forwards compatible mode.
                            // Xalan gets this right.
                        } 
                        else if ("Output_Output_UseStandAloneAttributeWithMultipleRoots".equals(id)) {
                            // Error only appears when document is serialized;
                            // not before
                        }
                        else { // transform should have failed
                            fail("Transformed " + style + "\n id: "
                              + testcase.getAttributeValue("id"));
                        }
                    }
                    else { 
                        try { 
                            if ("Attributes_xsl_attribute_dup_attr_with_namespace_conflict".equals(id)
                               || "BVTs_bvt057".equals(id)) {
                                // This test case requires namespace prefix rewriting,
                                // so the output won't be exactly the same between processors
                                continue;
                            }
                            else if ("Comment_DisableOutputEscaping_XslTextInXslComment".equals(id)) {
                               // Test case output is wrong
                                continue;
                            } 
                            else if ("Output__77927".equals(id)
                              || "Output__77928".equals(id)
                              || "Output__84304".equals(id)
                              || "Output__84305".equals(id)
                              || "Output__84312".equals(id)
                              || "Output__84619".equals(id)
                              || "Output__84620".equals(id)
                              || "Output_EntityRefInAttribHtml".equals(id)
                            ) {
                                // These test cases have incorrect line 
                                //  breaks in the reference output.
                                continue;
                            }
                            else if ("Output_Modified84433".equals(id)) {
                                // This test case uses disable output escaping
                                // so the results don't match up
                                continue;
                            }
                            else if ("Sorting_Sort_SortTextWithNonTextCharacters".equals(id)) {
                               // Xalan and MSXML don't sort non alphabetic characters 
                               // exactly the same, but that's legal
                                continue;
                            }
                            else if ("Text_DoeWithCdataInText".equals(id)) {
                               // Requires disable-output-escaping 
                                continue;
                            } 
                            else if ("Whitespaces__91443".equals(id)
                              || "Whitespaces__91444".equals(id)) { 
                                // Xalan bug
                                // See http://issues.apache.org/jira/browse/XALANJ-1969 
                                continue;
                            } 
                            else if ("AVTs__77591".equals(id)) {
                                // test suite bug; doesn't escape tabs in output. See
                                // http://lists.oasis-open.org/archives/xslt-conformance-comment/200409/msg00017.html
                            }
                            else if ("Keys_MultipltKeysInclude".equals(id) ) {
                               // Xalan bug
                               // http://issues.apache.org/jira/browse/XALANJ-1956
                            } 
                            /* else if ("Keys_PerfRepro3".equals(id) ) {
                               // Suspected Xalan bug 
                               // http://issues.apache.org/jira/browse/XALANJ-1955
                            } */
                            else if ("Number__84683".equals(id)) {
                               // test suite bug
                            }
                            else if ("Number__84687".equals(id)) {
                               // test suite bug
                            }
                            else if ("Number__84692".equals(id)) {
                               // test suite bug
                            }
                            else if ("Number__84694".equals(id)) {
                               // Test suite expects Roman number for zero
                               // to be the empty string while Xalan uses 0
                            }
                            else if ("Number__84699".equals(id)) {
                               // Xalan bug
                            }
                            else if ("Number__84700".equals(id)) {
                               // Xalan bug; extra whitespace. Possibly
                               // the same as 
                            }
                            else if ("Number__84716".equals(id)) {
                               // Xalan doesn't support Russian
                                // number formatting
                            }
                            else if ("Number__84717".equals(id)) {
                               // Xalan supports more Japanese than the
                               // test case does
                            }
                            else if ("Number__84722".equals(id)
                              || "Number__84723".equals(id)
                              || "Number__84724".equals(id)
                              || "Number__84725".equals(id)
                            ) {
                                // Acceptable locale support differences
                            }
                            else if ("Number_NaNOrInvalidValue".equals(id)) {
                                // Double bug! Test case is wrong and 
                                // Xalan gets this wrong!
                            }
                            else if ("Number_ValueAsNodesetTest1".equals(id)
                              || "Number_ValueAsEmptyNodeset".equals(id)) {
                                // Another double bug! Test case is wrong and 
                                // Xalan gets this wrong!
                            }
                            else if (id.equals("XSLTFunctions_BooleanFunction")) {
                                // I think the test case is wrong; or perhaps unspecified
                            } 
                            else if (id.equals("XSLTFunctions_TestIdFuncInComplexStruct")) {
                                // I think the Xalan output white space is wrong; 
                                // http://issues.apache.org/jira/browse/XALANJ-1947
                            }
                            else if (id.equals("XSLTFunctions__testOn-0.00")) {
                                // Possible test suite bug. See
                                // http://issues.apache.org/jira/browse/XALANJ-2226
                            }
                            else {
                                Document expectedResult;
                                if (strip) expectedResult = strippingBuilder.build(output);
                                else expectedResult = builder.build(output);
                                Document actualResult = XSLTransform.toDocument(result);
                                assertEquals("Mismatch with " + id,
                                  expectedResult, actualResult);
                            }
                        } // end try
                        catch (ParsingException ex) {  
                            // a few of the test cases generate 
                            // text or HTML output rather than 
                            // well-formed XML. For the moment, I 
                            // just skip these.
                            continue;
                        }
                        catch (IllegalAddException ex) {
                            // A few of the test cases generate 
                            // incomplete documents so we can't
                            // compare output. Perhaps I could
                            // wrap in an element, then get children
                            // to build a Node object rather than a
                            // Document???? i.e. a fragment parser?
                            // Could use a SequenceInputStream to hack this
                        }
                    } // end else
                    
                } // end try
                catch (MalformedURIException ex) {
                    
                }
                catch (FileNotFoundException ex) {
                    // The catalog doesn't always match what's on disk
                }
                catch (UnknownHostException ex) {
                    // A few tests like ProcessingInstruction__78197 
                    // point to external DTD subsets that can't be loaded
                }
                catch (ParsingException ex) {
                    // several stylesheets use relative namespace URIs XOM
                    // does not support; skip the test
                    if (ex.getCause() instanceof MalformedURIException) {
                        continue;
                    }
                    
                    String operation = scenario.getAttributeValue("operation");
                    if (!"execution-error".equals(operation)) {
                        if ("Namespace_XPath_PredefinedPrefix_XML".equals(id)) {
                            // uses relative namespace URIs
                        }
                        else if ("Sorting__78191".equals(id)
                          || "Text__78245".equals(id)
                          || "Text__78273".equals(id)
                          || "Text__78281".equals(id)
                        ) {
                            // binds XML namespace to prefix other than xml
                        }
                        else if ("ProcessingInstruction__78200".equals(id)
                                || "ProcessingInstruction__78202".equals(id)
                                || "ProcessingInstruction__78203".equals(id)
                                ) {
                            // bad test case; uses non-absolute domain name to locate DTD
                        }
                        else {
                            System.out.println(id + ": " + ex.getMessage());
                            throw ex;
                        }
                    }
                }
                catch (XSLException ex) {
                    // If the output was null the transformation 
                    // was expected to fail
                    if (output != null) {
                        Throwable cause = ex.getCause();
                        if ("Attributes__81487".equals(id)
                          || "Attributes__81551".equals(id)) {
                            // spec inconsistency; see 
                            // http://lists.w3.org/Archives/Public/xsl-editors/2004JulSep/0003.html
                            continue;
                        }
                        else if (cause instanceof MissingResourceException) {
                            // Xalan bug;
                            // http://issues.apache.org/jira/secure/ManageAttachments.jspa?id=27366
                        } 
                        else if ("Include_Include_IncludedStylesheetShouldHaveDifferentBaseUri".equals(id)) {
                           // This test case is wrong; Uses backslash in URI
                        }
                        else if ("Elements__89070".equals(id)) {
                            // bug fixed in later versions of Xalan
                        }
                        else if ("Namespace-alias_Namespace-Alias_NSAliasForDefaultWithExcludeResPref".equals(id)) {
                           // This test case is wrong; it uses a backslash in a URI 
                        }
                        else if ("Variables_VariableWithinVariable".equals(id)) {
                            // Xalan does not recover from this one
                        }
                        else if ("BVTs_bvt054".equals(id)) {
                            // Xalan bug 
                            // http://issues.apache.org/jira/browse/XALANJ-1952 
                            continue;
                        } 
                        else if ("BVTs_bvt094".equals(id)) {
                            // Xalan bug 
                            // http://issues.apache.org/jira/browse/XALANJ-1953 
                            continue;
                        } 
                        else if ("Output__78177".equals(id)
                          || "Output__84009".equals(id)) {
                           // Xalan does not recover from this error 
                           // which involves duplicate and possibly conflicting xsl:output elements
                            continue;
                        }
                        else if ("Comment_Comment_CDATAWithSingleHyphen".equals(id)
                          || "Comment_Comment_DoubleHypenEntitywithDelCharacter".equals(id)
                          || "Comment_Comment_LineOfAllHyphens".equals(id)
                          || "Comment_Comment_SingleHyphenOnly".equals(id)
                          || "Comment_Comment_DoubleHyphenONLY".equals(id)) {
                           // Begins comment data with hyphen, which XOM doesn't allow 
                            continue;
                        }
                        else if ("ProcessingInstruction_ValueOfandTextWithDoeInProcInstr".equals(id)) {
                           // Begins processing instruction data with white space, which XOM doesn't allow   
                            continue;
                        }
                        else if ("Elements__89716".equals(id)
                          || "Elements__89717".equals(id)
                          || "Elements__89718".equals(id)
                          || "Output__84309".equals(id)
                          || "Namespace__77670".equals(id))
                          {
                           // Xalan doesn't recover from these, though recovery is allowed   
                            continue;
                        }
                        else if ("Output__84306".equals(id)) {
                            // Xalan bug
                            // http://issues.apache.org/jira/browse/XALANJ-1954
                            continue;
                        }
                        else if ("Output__84014".equals(id)) {
                            // Fixed in later version of Xalan than is bundled with JDK  
                            continue;
                        } 
                        else if (cause instanceof MalformedURIException) {
                            // Some of the tests generate relative namespace URIs
                            // XOM doesn't support
                            continue;
                        }
                        else {
                            System.out.println(id + ": " + ex.getMessage());
                            System.out.println("in " + style);
                            if (cause != null) {
                                System.out.println("cause: " + cause.getMessage());                                
                            }
                            throw ex;
                        }
                    }
                } // end catch
                catch (XMLException ex) {
                    if ("Text_modified78309".equals(id)) {
                       // output is not a full document   
                    }
                    else {
                        System.err.println(id);
                        throw ex;
                    }
                }
                
            } // end for 
            
        } // end if 
     
    }


    public void testSelect_Select65()  
      throws IOException, ParsingException, XSLException { 
        xalanTestCase("select/select65");
    }

    private void xalanTestCase(String path) 
      throws ParsingException, ValidityException, IOException, XSLException {
        
        Builder builder = new Builder();
        File base = new File("data");
        base = new File(base, "oasis-xslt-testsuite");
        base = new File(base, "TESTS");
        base = new File(base, "Xalan_Conformance_Tests");
          
        File input = new File(base, path + ".xml");
        File style = new File(base, path + ".xsl");
          
        base = new File(base, "REF_OUT");
        File output = new File(base, path + ".out");
        
        Document styleDoc = builder.build(style);
        Document inputDoc = builder.build(input);
        XSLTransform xform = new XSLTransform(styleDoc);
        Nodes result = xform.transform(inputDoc);
        Document expectedResult = builder.build(output);
        Document actualResult = XSLTransform.toDocument(result);
        assertEquals(expectedResult, actualResult);
        
    }   
  

    public void testSorting__89749()  
      throws IOException, ParsingException, XSLException {
        
        Builder builder = new Builder();
        File base = new File("data");
        base = new File(base, "oasis-xslt-testsuite");
        base = new File(base, "TESTS");

        File input = new File(base, "MSFT_CONFORMANCE_TESTS/Sorting/sorttest.xml");
        File style = new File(base, "MSFT_CONFORMANCE_TESTS/Sorting/2_5_13_repeat.xsl");
  
        Document styleDoc = builder.build(style);
        Document inputDoc = builder.build(input);
        XSLTransform xform = new XSLTransform(styleDoc);
        Nodes result = xform.transform(inputDoc);
        /*
        File output = new File(base, "MSFT_CONFORMANCE_TESTS/Sorting/out/89749.txt"); 
        Document expectedResult = builder.build(output);
        Document actualResult = XSLTransform.toDocument(result);
        assertEquals(expectedResult, actualResult); */
     
    }
    
}
