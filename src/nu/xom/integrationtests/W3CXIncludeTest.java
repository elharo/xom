/* Copyright 2002-2005, 2011, 2018 Elliotte Rusty Harold
   
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
   subject line. The XOM home page is located at http://www.xom.nu/
*/

package nu.xom.integrationtests;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;

import junit.framework.AssertionFailedError;
import nu.xom.Builder;
import nu.xom.DocType;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;
import nu.xom.Serializer;
import nu.xom.tests.XOMTestCase;
import nu.xom.xinclude.XIncludeException;
import nu.xom.xinclude.XIncluder;

/**
 * <p>
 * Integration tests for the XInclude and XPointer engines.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.2.11
 *
 */
public class W3CXIncludeTest extends XOMTestCase {

    
    public W3CXIncludeTest(String name) {
        super(name);
    }

    
    private Builder builder = new Builder();
    private File inputDir;
    private File outputDir;
    
    // This class tests error conditions, which Xerces
    // annoyingly logs to System.err. This hides System.err 
    // before each test and restores it after each test.
    private PrintStream systemErr = System.err;
    
    
    protected void setUp() {
        
        System.setErr(new PrintStream(new ByteArrayOutputStream()));
        
        inputDir = new File("data");
        inputDir = new File(inputDir, "xinclude");
        inputDir = new File(inputDir, "input");
        
        outputDir = new File("data");
        outputDir = new File(outputDir, "xinclude");
        outputDir = new File(outputDir, "output");
        
    }
    
    
    protected void tearDown() {
        System.setErr(systemErr);
    }    
    
    
    private void dumpResult(File original, Document result)
      throws IOException {
        
        String name = original.getName();
        File debug = new File("data");
        debug = new File(debug, "xinclude");
        debug = new File(debug, "debug");
        File output = new File(debug, name);
        FileOutputStream out = new FileOutputStream(output);
        Serializer serializer = new Serializer(out);
        serializer.write(result);        
        
    }


    
    // This test requires files that I have not received permission
    // to distribute so for the moment you won't be able to run it.
    // For my own use it checks to see if the files are present
    // and runs if it does find them. You can't just install the
    // XInclude-Test-Suite data as distributed by the W3C here.
    // Some of those tests rely on optional features XOM does not
    // support such as the xpointer() scheme and notations.
    // Plus some of those tests have mistakes. You need my patched 
    // version of the tests.
    public void testXIncludeTestSuite()  
      throws ParsingException, IOException, XIncludeException {
     
        File testDescription = new File("data");
        testDescription = new File(testDescription, "XInclude-Test-Suite");
        testDescription = new File(testDescription, "testdescr.xml");
        URL baseURL = testDescription.toURI().toURL();
        if (!testDescription.exists()) {
            baseURL = new URL(
              "http://dev.w3.org/cvsweb/~checkout~/2001/" +
              "XInclude-Test-Suite/testdescr.xml?content-type=text/" +
              "plain&only_with_tag=HEAD"
            );
        }
        Document master = builder.build(baseURL.toExternalForm());
        Element testsuite = master.getRootElement();
        Elements testcases = testsuite.getChildElements("testcases");
        for (int i = 0; i < testcases.size(); i++) {
            Element group = testcases.get(i);   
            String basedir = group.getAttributeValue("basedir");
            if (basedir.startsWith("Harold")) {
                // These tests are listed in the catalog but haven't 
                // yet been checked into CVS. besides, these are all
                // based on individual tests in this class anyway, so
                // running these is duplicated effort.
                continue;   
            }
            Elements cases = group.getChildElements("testcase");
            for (int j = 0; j < cases.size(); j++) {
                Element testcase = cases.get(j);
                String id = testcase.getAttributeValue("id");
                String features = testcase.getAttributeValue("features");
                if (features != null) {
                    if (features.indexOf("unexpanded-entities") >= 0) continue;
                    if (features.indexOf("unparsed-entities") >= 0) continue;
                    if (features.indexOf("xpointer-scheme") >= 0) continue;
                }
                String description 
                  = testcase.getFirstChildElement("description").getValue();
                if (!basedir.endsWith("/")) basedir += '/';
                URL input = new URL(baseURL, basedir);
                input = new URL(input, testcase.getAttributeValue("href"));
                Element output = testcase.getFirstChildElement("output");
                if (output == null) { // test failure   
                    try {
                        Document doc = builder.build(input.toExternalForm());
                        XIncluder.resolveInPlace(doc);
                        fail("Failed test " + id + ": " + description);
                    }
                    catch (XIncludeException success) {
                        assertNotNull(success.getMessage());
                    }
                    catch (IOException success) {
                       if (baseURL.getProtocol().equals("file")) {
                           assertNotNull("Problem processing " + input, success.getMessage());
                       }
                    }
                    catch (ParsingException success) {
                        assertNotNull(success.getMessage());
                    }
                }
                else {
                    URL result = new URL(baseURL, basedir);
                    result = new URL(result, output.getValue());
                    Document expected = builder.build(result.toExternalForm());
                    Document doc = builder.build(input.toExternalForm());
                    XIncluder.resolveInPlace(doc);
                    try {
                        assertEquals("Error when processing  " 
                          + result, expected, doc);
                    }
                    catch (AssertionFailedError t) {
                      // If it fails, try it without a doctype in result.
                      // A lot of the test cases have incorrect DOCTYPE
                      // declarations.  
                      DocType doctype = expected.getDocType();
                      DocType actualDoctype = doc.getDocType();
                      if (doctype != null) {
                         expected.removeChild(doctype);
                         assertEquals("Error when processing  " 
                          + input, expected, doc);                  
                      }
                      else if (actualDoctype != null) {
                         doc.removeChild(actualDoctype);
                          assertEquals("Error when processing  " 
                          + input, expected, doc); 
                      }
                      else {
                          fail();
                      }
                    }
                }
            }          
        }
        
    } 

    
}