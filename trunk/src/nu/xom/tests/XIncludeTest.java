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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParseException;
import nu.xom.Serializer;
import nu.xom.xinclude.BadParseAttributeException;
import nu.xom.xinclude.CircularIncludeException;
import nu.xom.xinclude.MissingHrefException;
import nu.xom.xinclude.XIncludeException;
import nu.xom.xinclude.XIncluder;

/**
 * @author Elliotte Rusty Harold
 * @version 1.0d19
 *
 */
public class XIncludeTest extends XOMTestCase {

    // might want to test encoding heuristics on files in different encodings????

    public XIncludeTest(String name) {
        super(name);
    }

    private Builder builder;
    
    protected void setUp() {        
        builder = new Builder();       
    }

    public void test1() 
      throws ParseException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/test.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        // For debugging
        /* dumpResult(original, result); */
        Document expectedResult = builder.build(
          new File("data/xinclude/output/test.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    private void dumpResult(File original, Document result)
      throws IOException {
        
        String name = original.getName();
        File debug = new File("data/xinclude/debug/");
        File output = new File(debug, name);
        FileOutputStream out = new FileOutputStream(output);
        Serializer serializer = new Serializer(out);
        serializer.write(result);        
    }
    
    // from the XInclude CR
    public void testC1() 
      throws ParseException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/c1.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/c1.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    // from the XInclude CR
    public void testC2() 
      throws ParseException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/c2.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/c2.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    // from the XInclude CR
    public void testC3() 
      throws ParseException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/c3.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/c3.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    // C4 skipped for the moment because it uses XPointers
    // that I don't yet support

    // from the XInclude CR
    // Don't use this one yet, because there appear to be 
    // mistakes in the spec examples
    /*public void testC5() throws ParseException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/c5.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(new File("data/xinclude/output/c5.xml"));
        XMLAssert.assertEquals(expectedResult, result);
        
    } */
    
    public void testSiblingIncludes() 
      throws ParseException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/paralleltest.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/paralleltest.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    public void testNamespaces() 
      throws ParseException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/namespacetest.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/namespacetest.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    public void testNoInclusions() 
      throws ParseException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/latin1.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/latin1.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    public void test2() 
      throws ParseException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/simple.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/simple.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    public void testReplaceRoot() 
      throws ParseException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/roottest.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/roottest.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    public void testCircle1() 
      throws ParseException, IOException, XIncludeException {
        File input = new File("data/xinclude/input/circle1.xml");
        Document doc = builder.build(input);
        try {
            XIncluder.resolve(doc);
            fail("allowed parsed include of self");
        }
        catch (CircularIncludeException ex) {
            // success   
        }
    }
    
    public void testCircle2() 
      throws ParseException, IOException, XIncludeException {
        File input = new File("data/xinclude/input/circle2a.xml");
        Document doc = builder.build(input);
        try {
            XIncluder.resolve(doc);
            fail("allowed parsed include of self");
        }
        catch (CircularIncludeException ex) {
            // success   
        }
    }
    
    public void testMissingHref() 
      throws ParseException, IOException, XIncludeException {
        File input = new File("data/xinclude/input/missinghref.xml");
        Document doc = builder.build(input);
        try {
            XIncluder.resolve(doc);
            fail("allowed missing href");
        }
        catch (MissingHrefException ex) {
            // success   
        }
    }
    
    public void testBadParseAttribute() 
      throws ParseException, IOException, XIncludeException {
        File input = new File("data/xinclude/input/badparseattribute.xml");
        Document doc = builder.build(input);
        try {
            XIncluder.resolve(doc);
            fail("allowed bad parse attribute");
        }
        catch (BadParseAttributeException ex) {
            // success   
        }
    }
    
    public void testUnavailableResource() 
      throws ParseException, IOException, XIncludeException {
        File input = new File("data/xinclude/input/missingfile.xml");
        Document doc = builder.build(input);
        try {
            XIncluder.resolve(doc);
            fail("allowed unresolvable resource");
        }
        catch (IOException ex) {
            // success   
        }
    }
    
    public void testFallback() 
      throws ParseException, IOException, XIncludeException {
        File input = new File("data/xinclude/input/fallbacktest.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/fallbacktest.xml")
        );
        assertEquals(expectedResult, result);
    }
    
    public void testFallbackWithRecursiveInclude() 
      throws ParseException, IOException, XIncludeException {
        File input = new File("data/xinclude/input/fallbacktest2.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/fallbacktest2.xml")
        );
        assertEquals(expectedResult, result);
    }

    public void testEncodingAttribute() 
      throws ParseException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/utf16.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/utf16.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    public void testXPointerBareNameID() 
      throws ParseException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/xptridtest.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/xptridtest.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    public void testXPointerBareNameMatchesNothing() 
      throws ParseException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/xptridtest2.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/xptridtest2.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    public void testXPointerPureTumbler() 
      throws ParseException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/xptrtumblertest.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/xptrtumblertest.xml")
        );
        assertEquals(expectedResult, result);
        
    }

    // Test with 3 element schemes in the XPointer.
    // The first and second one point to nothing. The third one
    // selects something.
    public void testXPointerTripleTumbler() 
      throws ParseException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/xptr2tumblertest.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/xptrtumblertest.xml")
        );
        assertEquals(expectedResult, result);
        
    }

    // Make sure XPointer failures are treated as a resource error,
    // not a fatal error.
    public void testXPointerFailureIsAResourceError() 
      throws ParseException, IOException, XIncludeException {
      
        File input = new File(
          "data/xinclude/input/xptrtumblerfailsbutfallback.xml"
        );
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        // For debugging
        dumpResult(input, result); 
        Document expectedResult = builder.build(
          new File("data/xinclude/output/xptrtumblertest.xml")
        );
        assertEquals(expectedResult, result);
        
    }

    // Make sure XPointer syntax errors are treated as a resource error,
    // not a fatal error.???? see section 4.2 of XInclude CR
    /* Resources that are unavailable for any reason 
      (for example the resource doesn't exist, connection 
      difficulties or security restrictions prevent it from being 
      fetched, the URI scheme isn't a fetchable one, the resource 
      is in an unsuppored encoding, the resource is determined 
      through implementation-specific mechanisms not to be XML, or a 
      syntax error in an [XPointer Framework]) result in a resource error.  */
    public void testXPointerSyntaxErrorIsAResourceError() 
      throws ParseException, IOException, XIncludeException {
      
        File input = new File(
          "data/xinclude/input/xptrsyntaxerrorbutfallback.xml"
        );
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/xptrtumblertest.xml")
        );
        assertEquals(expectedResult, result);
        
    }

    // Test with 3 element schemes in the XPointer,
    // separated by white space.
    // The first one points to nothing. The third one
    // selects something.
    public void testXPointerTumblerWithWhiteSpace() 
      throws ParseException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/xptrtumblertest3.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/xptrtumblertest.xml")
        );
        assertEquals(expectedResult, result);
        
    }

    public void testXPointerTumblerMatchesNothing() 
      throws ParseException, IOException, XIncludeException {
      
        File input = new File(
          "data/xinclude/input/xptrtumblertest2.xml"
        );
        Document doc = builder.build(input);
        try {
            Document result = XIncluder.resolve(doc);
            fail("Did not indicate error on XPointer matching nothing");
        }
        catch (XIncludeException ex) {
            // success    
        }
        
    }
    
    public void testMalformedXPointer() 
      throws ParseException, IOException, XIncludeException {
      
        try {
            File input = new File("data/xinclude/input/badxptr.xml");
            Document doc = builder.build(input);
            XIncluder.resolve(doc);
        }
        catch (XIncludeException ex) {
            // success   
        }
        
    }
    
    public void testMalformedXPointerWithFallback() 
      throws ParseException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/xptrfallback.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(new File(
          "data/xinclude/output/xptrfallback.xml")
        );
        assertEquals(expectedResult, result);
                
    }
    
    public void testIDAndTumbler() 
      throws ParseException, IOException, XIncludeException {
      
        File input = new File(
          "data/xinclude/input/xptridandtumblertest.xml"
        );
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(new File(
          "data/xinclude/output/xptridandtumblertest.xml")
        );
        assertEquals(expectedResult, result);
                
    }

    public void testAutoDetectUTF16BigEndianWithByteOrderMark() 
      throws ParseException, IOException, XIncludeException {
      
        File input = new File(
          "data/xinclude/input/UTF16BigEndianWithByteOrderMark.xml"
        );
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(new File(
          "data/xinclude/output/UTF16BigEndianWithByteOrderMark.xml")
        );
        assertEquals(expectedResult, result);
                
    }

    public void testAutoDetectUTF16LittleEndianWithByteOrderMark() 
      throws ParseException, IOException, XIncludeException {
      
        File input = new File(
          "data/xinclude/input/UTF16LittleEndianWithByteOrderMark.xml"
        );
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(new File(
          "data/xinclude/output/UTF16LittleEndianWithByteOrderMark.xml"
        ));
        assertEquals(expectedResult, result);
                
    }

    public void testAutoDetectUTF8WithByteOrderMark() 
      throws ParseException, IOException, XIncludeException {
      
        File input = new File(
          "data/xinclude/input/UTF8WithByteOrderMark.xml"
        );
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/UTF8WithByteOrderMark.xml")
        );
        assertEquals(expectedResult, result);
                
    }

    // This test requires files that I have not recieved permission
    // to distribute so for the moment you won't be able to run it.
    // For my own use it checks to see if the files are present
    // and runs if it does find them. You can't just install the
    // XInclude-Test-Suite data as distributed by the W3C here.
    // Some of those tests rely on optional features XOM does not
    // support such as the xpointer() scheme and notations.
    // Plus some of those tests have mistakes. You need my patched 
    // version of the tests.
    public void testXIncludeTestSuite()  
      throws ParseException, IOException, XIncludeException {
        
        File testDescription = new File("data/XInclude-Test-Suite/testdescr.xml");
        if (testDescription.exists()) {
            Document master = builder.build(testDescription);
            Element testsuite = master.getRootElement();
            Elements testcases = testsuite.getChildElements("testcases");
            for (int i = 0; i < testcases.size(); i++) {
                Element group = testcases.get(i);   
                String creator = group.getAttributeValue("creator");
                String basedir = group.getAttributeValue("basedir");
                Elements cases = group.getChildElements("testcase");
                for (int j = 0; j < cases.size(); j++) {
                    Element testcase = cases.get(j);
                    String id = testcase.getAttributeValue("id");
                    String description 
                      = testcase.getFirstChildElement("description").getValue();
                    File input = new File("data/XInclude-Test-Suite/" 
                      + basedir + '/' + testcase.getAttributeValue("href"));
                    Element output = testcase.getFirstChildElement("output");
                    if (output == null) { // test failure   
                        try {
                            Document doc = builder.build(input);
                            XIncluder.resolveInPlace(doc);
                            fail("Failed test " + id + ": " + description);
                        }
                        catch (XIncludeException ex) {
                           // success   
                        }
                        catch (IOException ex) {
                           // success   
                        }
                        catch (ParseException ex) {
                           // success   
                        }
                        catch (NullPointerException ex) {
                           // success  
                           // This generally happens as a result of an element
                           // scheme that doesn't point anywhere; but is that the right
                           // exception? Should it throw a more specific exception?
                           // Probably, but I can't fix this until I publicly expose the 
                           // XPointer package. ???? 
                        }
                    }
                    else {
                        File in = new File("data/XInclude-Test-Suite/" 
                          + basedir + '/' + output.getValue());
                        Document expected = builder.build(in);
                        Document doc = builder.build(input);
                        XIncluder.resolveInPlace(doc);
                        assertEquals(expected, doc);
                    }
                }          
            }
        } 
        
    } 

  // Turn off these tests because Java doesn't support UCS4 yet
 /*   public void testAutoDetectUCS4BE() 
      throws ParseException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/UCS4BE.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/UTF8WithByteOrderMark.xml")
        );
        XMLAssert.assertEquals(expectedResult, result);
                
    }

    public void testAutoDetectUCS4LE() 
      throws ParseException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/UCS4LE.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/UTF8WithByteOrderMark.xml")
        );
        XMLAssert.assertEquals(expectedResult, result);
                
    } */

}
