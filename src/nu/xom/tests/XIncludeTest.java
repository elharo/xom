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
import java.net.URL;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;
import nu.xom.Serializer;
import nu.xom.xinclude.BadParseAttributeException;
import nu.xom.xinclude.CircularIncludeException;
import nu.xom.xinclude.MissingHrefException;
import nu.xom.xinclude.XIncludeException;
import nu.xom.xinclude.XIncluder;

/**
 * <p>
 *   Unit tests for the XInclude and XPointer engines.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0d23
 *
 */
public class XIncludeTest extends XOMTestCase {

    public XIncludeTest(String name) {
        super(name);
    }

    private Builder builder;
    
    protected void setUp() {        
        builder = new Builder();       
    }

    public void test1() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/test.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        // For debugging
        /* dumpResult(input, result); */
        Document expectedResult = builder.build(
          new File("data/xinclude/output/test.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    public void testBaseURIsPreservedINSameDocumentInclusion() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/includefromsamedocumentwithbase.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        // For debugging
        // dumpResult(input, result); 
        Document expectedResult = builder.build(
          new File("data/xinclude/output/includefromsamedocumentwithbase.xml")
        );
        assertEquals(expectedResult, result);
        
    }

    // Tests that use XPointer to
    // grab a part of the document that contains an include element
    // and make sure that's fully resolved too
    public void testResolveThroughXPointer() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/resolvethruxpointer.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        // For debugging
        dumpResult(input, result); 
        Document expectedResult = builder.build(
          new File("data/xinclude/output/resolvethruxpointer.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    /* public void testResolveNodes() 
      throws IOException, ParsingException, XIncludeException {
        File dir = new File("data/xinclude/input/");
        Element include = new Element("xi:include", XIncluder.XINCLUDE_NS);
        include.setBaseURI(dir.toURL().toExternalForm());
        include.addAttribute(new Attribute("href", "disclaimer.xml"));
        Nodes in = new Nodes(include);  
        Nodes out = XIncluder.resolve(in);
        assertEquals(1, out.size());
        Element result = (Element) out.get(0);
        assertEquals("disclaimer",result.getQualifiedName());
    } */

    // need to test this method resolve(nodes) when it returns more than one node????
    
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
      throws ParsingException, IOException, XIncludeException {
      
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
      throws ParsingException, IOException, XIncludeException {
      
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
      throws ParsingException, IOException, XIncludeException {
      
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
    /*public void testC5() throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/c5.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(new File("data/xinclude/output/c5.xml"));
        XMLAssert.assertEquals(expectedResult, result);
        
    } */
    
    public void testRecurseWithinSameDocument() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/recursewithinsamedocument.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/recursewithinsamedocument.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    public void testSiblingIncludes() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/paralleltest.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/paralleltest.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    public void testNamespaces() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/namespacetest.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/namespacetest.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    public void testIncludeReferencesItItself() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/internalcircular.xml");
        Document doc = builder.build(input);
        try {
            XIncluder.resolve(doc);
            fail("Allowed include element to reference itself");
        }
        catch (CircularIncludeException success) {
            assertNotNull(success.getMessage());   
        }
        
    }
    
    public void testIncludeReferencesItsAncestor() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/internalcircularviaancestor.xml");
        Document doc = builder.build(input);
        try {
            XIncluder.resolve(doc);
            fail("Allowed include element to reference its own ancestor");
        }
        catch (CircularIncludeException success) {
            assertNotNull(success.getMessage());   
        }
        
    }
    
    public void testNoInclusions() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/latin1.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/latin1.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    public void test2() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/simple.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/simple.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    public void testReplaceRoot() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/roottest.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/roottest.xml")
        );
        assertEquals(expectedResult, result);
        
    }

    
    public void testCircle1() 
      throws ParsingException, IOException, XIncludeException {
        File input = new File("data/xinclude/input/circle1.xml");
        Document doc = builder.build(input);
        try {
            XIncluder.resolve(doc);
            fail("allowed parsed include of self");
        }
        catch (CircularIncludeException ex) {
            // success   
            assertNotNull(ex.getMessage());
            assertEquals(input.toURL().toExternalForm(), ex.getURI());           
        }
    }
    
    public void testCircle2() 
      throws ParsingException, IOException, XIncludeException {
        File input = new File("data/xinclude/input/circle2a.xml");
        File errorFile = new File("data/xinclude/input/circle2b.xml");
        Document doc = builder.build(input);
        try {
            XIncluder.resolve(doc);
            fail("allowed parsed include of self");
        }
        catch (CircularIncludeException ex) {
            // success   
            assertNotNull(ex.getMessage());
            assertEquals(errorFile.toURL().toExternalForm(), ex.getURI());           
        }
    }
    
    public void testMissingHref() 
      throws ParsingException, IOException, XIncludeException {
        File input = new File("data/xinclude/input/missinghref.xml");
        Document doc = builder.build(input);
        try {
            XIncluder.resolve(doc);
            fail("allowed missing href");
        }
        catch (MissingHrefException ex) {
            // success   
            assertNotNull(ex.getMessage());
            assertEquals(doc.getBaseURI(), ex.getURI());           
        }
    }
    
    public void testBadParseAttribute() 
      throws ParsingException, IOException, XIncludeException {
        File input = new File("data/xinclude/input/badparseattribute.xml");
        Document doc = builder.build(input);
        try {
            XIncluder.resolve(doc);
            fail("allowed bad parse attribute");
        }
        catch (BadParseAttributeException ex) {
            // success   
            assertNotNull(ex.getMessage());
            URL u1 = input.toURL();
            URL u2 = new URL(ex.getURI());
            assertEquals(u1, u2);
        }
    }
    
    public void testUnavailableResource() 
      throws ParsingException, IOException, XIncludeException {
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
      throws ParsingException, IOException, XIncludeException {
        File input = new File("data/xinclude/input/fallbacktest.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/fallbacktest.xml")
        );
        assertEquals(expectedResult, result);
    }
    
    public void testFallbackWithRecursiveInclude() 
      throws ParsingException, IOException, XIncludeException {
        File input = new File("data/xinclude/input/fallbacktest2.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/fallbacktest2.xml")
        );
        assertEquals(expectedResult, result);
    }

    public void testEncodingAttribute() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/utf16.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/utf16.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    public void testXPointerBareNameID() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/xptridtest.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/xptridtest.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    public void testShorthandXPointerMatchesNothing() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/xptridtest2.xml");
        Document doc = builder.build(input);
        try {
            Document result = XIncluder.resolve(doc);
            fail("Resolved a document with an XPointer " +              "that selects no subresource");
        }
        catch (XIncludeException ex) {
            // success   
            assertNotNull(ex.getMessage());
            // Must compare URLs instead of strings here to avoid 
            // issues of whether a file URL begins file:/ or file:///
            URL u1 = input.toURL();
            URL u2 = new URL(ex.getURI());
            assertEquals(u1, u2);  
        }
        
        
        /* I used to think this case included nothing.
           Now I think an XPointer that matches no
           subresource, and does not have a fallback is in error.
        Document expectedResult = builder.build(
          new File("data/xinclude/output/xptridtest2.xml")
        );
        assertEquals(expectedResult, result);
        */
        
    }
    
    public void testXPointerPureTumbler() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/xptrtumblertest.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/xptrtumblertest.xml")
        );
        assertEquals(expectedResult, result);
        
    }

    public void testXPointerSyntaxErrorInSecondPart() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/laterfailure.xml");
        Document doc = builder.build(input);
        try {
            Document result = XIncluder.resolve(doc);
            fail("Didn't find syntax error in 2nd XPointer part" +
                " when the first part resolved successfully");
        }
        catch (XIncludeException ex) {
            // success   
            assertNotNull(ex.getMessage());
        }  
        
    }

    public void testXPointerSyntaxErrorMissingFinalParenthesis() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/laterfailure2.xml");
        Document doc = builder.build(input);
        try {
            Document result = XIncluder.resolve(doc);
            fail("Didn't find syntax error in 2nd XPointer part" +
                " when the first part resolved successfully");
        }
        catch (XIncludeException ex) {
            // success   
            assertNotNull(ex.getMessage());
        }  
        
    }

    // Test we can inlcude form same document using only an xpointer attribute
    public void testOnlyXPointer() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/onlyxpointer.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/onlyxpointer.xml")
        );
        // dumpResult(input, result); 
        
        assertEquals(expectedResult, result);
        
    }



    // Test with 3 element schemes in the XPointer.
    // The first and second one point to nothing. The third one
    // selects something.
    public void testXPointerTripleTumbler() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/xptr2tumblertest.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/xptrtumblertest.xml")
        );
        assertEquals(expectedResult, result);
        
    }

    // Test with 2 element schemes in the XPointer.
    // The first one uses an ID that doesn't exist 
    // and points to nothing. The second one
    // selects something.
    public void testXPointerDoubleTumbler() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/xptrdoubletumblertest.xml");
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
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(
          "data/xinclude/input/xptrtumblerfailsbutfallback.xml"
        );
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        // For debugging
        // dumpResult(input, result); 
        Document expectedResult = builder.build(
          new File("data/xinclude/output/xptrtumblertest.xml")
        );
        assertEquals(expectedResult, result);
        
    }

    // Make sure XPointer syntax errors are treated as a resource 
    // error, not a fatal error per section 4.2 of XInclude CR
    /* Resources that are unavailable for any reason 
      (for example the resource doesn't exist, connection 
      difficulties or security restrictions prevent it from being 
      fetched, the URI scheme isn't a fetchable one, the resource 
      is in an unsuppored encoding, the resource is determined 
      through implementation-specific mechanisms not to be XML, or a 
      syntax error in an [XPointer Framework]) result in a resource 
      error.  */
    public void testXPointerSyntaxErrorIsAResourceError() 
      throws ParsingException, IOException, XIncludeException {
      
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
      throws ParsingException, IOException, XIncludeException {
      
        File input 
          = new File("data/xinclude/input/xptrtumblertest3.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/xptrtumblertest.xml")
        );
        assertEquals(expectedResult, result);
        
    }

    public void testXPointerTumblerMatchesNothing() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(
          "data/xinclude/input/xptrtumblertest2.xml"
        );
        Document doc = builder.build(input);
        try {
            Document result = XIncluder.resolve(doc);
            fail("Did not error on XPointer matching nothing");
        }
        catch (XIncludeException ex) {
            // success   
            assertNotNull(ex.getMessage());
            URL u1 = input.toURL();
            URL u2 = new URL(ex.getURI());
            assertEquals(u1, u2);            
        }
        
    }
    
    public void testMalformedXPointer() 
      throws ParsingException, IOException, XIncludeException {   
        
        File input = new File("data/xinclude/input/badxptr.xml");
        Document doc = builder.build(input);
        try {
            XIncluder.resolve(doc);
        }
        catch (XIncludeException ex) {
            // success   
            assertNotNull(ex.getMessage());
            URL u1 = input.toURL();
            URL u2 = new URL(ex.getURI());
            assertEquals(u1, u2);            
        }
        
    }
    
    public void testAnotherMalformedXPointer() 
      throws ParsingException, IOException, XIncludeException {
        
        // testing use of non NCNAME as ID
        File input = new File("data/xinclude/input/badxptr2.xml");
        Document doc = builder.build(input);
        try {
            XIncluder.resolve(doc);
        }
        catch (XIncludeException ex) {
            // success   
            assertNotNull(ex.getMessage());
            URL u1 = input.toURL();
            URL u2 = new URL(ex.getURI());
            assertEquals(u1, u2);            
        }
        
    }
    
    public void testMalformedXPointerWithFallback() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/xptrfallback.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(new File(
          "data/xinclude/output/xptrfallback.xml")
        );
        assertEquals(expectedResult, result);
                
    }
    
    public void testIDAndTumbler() 
      throws ParsingException, IOException, XIncludeException {
      
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
      throws ParsingException, IOException, XIncludeException {
      
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
      throws ParsingException, IOException, XIncludeException {
      
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
      throws ParsingException, IOException, XIncludeException {
      
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

    public void testAutoDetectUnicodeBigUnmarked() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(
          "data/xinclude/input/UnicodeBigUnmarked.xml"
        );
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/UnicodeBigUnmarked.xml")
        );
        assertEquals(expectedResult, result);
                
    }

    public void testUnicodeLittleUnmarked() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(
          "data/xinclude/input/UnicodeLittleUnmarked.xml"
        );
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/UnicodeLittleUnmarked.xml")
        );
        assertEquals(expectedResult, result);
                
    }

/*// Java doesn't yet support the UTF-32BE and UTF32LE encodings
    public void testUTF32BE() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(
          "data/xinclude/input/UTF32BE.xml"
        );
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/UTF32BE.xml")
        );
        assertEquals(expectedResult, result);
                
    }

    public void testUTF32LE() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(
          "data/xinclude/input/UTF32LE.xml"
        );
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/UTF32LE.xml")
        );
        assertEquals(expectedResult, result);
                
    }
*/
    public void testEBCDIC() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(
          "data/xinclude/input/EBCDIC.xml"
        );
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expected = builder.build(
          new File("data/xinclude/output/EBCDIC.xml")
        );
        assertEquals(expected, result);
                
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
                    System.out.println("Test case: " + input);
                    if (output == null) { // test failure   
                        try {
                            Document doc = builder.build(input);
                            XIncluder.resolveInPlace(doc);
                            fail("Failed test " + id + ": " + description);
                        }
                        catch (XIncludeException ex) {
                            // success   
                            assertNotNull(ex.getMessage());
                        }
                        catch (IOException ex) {
                           // success   
                        }
                        catch (ParsingException ex) {
                            // success   
                            assertNotNull(ex.getMessage());
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
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/UCS4BE.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/UTF8WithByteOrderMark.xml")
        );
        assertEquals(expectedResult, result);
                
    }

    public void testAutoDetectUCS4LE() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/UCS4LE.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/UTF8WithByteOrderMark.xml")
        );
        assertEquals(expectedResult, result);
                
    } */
    
    
    // Need a test case where A includes B, B includes C
    // and B encounters the error (e.g. a missing href)
    // to make sure B's URL is in the error message, not A's
    public void testChildDocumentSetsErrorURI() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/toplevel.xml");
        File error = new File("data/xinclude/input/onedown.xml");
        Document doc = builder.build(input);
        try {
            Document result = XIncluder.resolve(doc);
            fail("Missing HREF not detected");
        }
        catch (MissingHrefException ex) {
            // success   
            assertNotNull(ex.getMessage());
            URL u1 = error.toURL();
            URL u2 = new URL(ex.getURI());
            assertEquals(u1, u2);            
        }
                
    } 
}
