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

import nu.xom.xslt.*;
import nu.xom.*;

import java.io.*;

// Need to test some transforms that don't produce full documents????

/**
 * @author Elliotte Rusty Harold
 * @version 1.0d19
 *
 */
public class XSLTransformTest extends XOMTestCase {

    // not a literal result element as stylesheet 
    // because it's missing the xsl:version attribute
    private String notAStyleSheet = 
     "<?xml-stylesheet href=\"file.css\" type=\"text/css\"?>" 
     + "<!-- test -->"
     + "<test xmlns:xlink='http://www.w3.org/TR/1999/xlink'>Hello dear"
     + "\r\n<em id=\"p1\" xmlns:none=\"http://www.example.com\">"
     + "very important</em>"
     + "<span xlink:type='simple'>here&apos;s the link</span>\r\n"
     + "<svg:svg xmlns:svg='http://www.w3.org/TR/2000/svg'>"
     + "<svg:text>text in a namespace</svg:text></svg:svg>\r\n"
     + "<svg xmlns='http://www.w3.org/TR/2000/svg'>"
     + "<text>text in a namespace</text></svg>"
     + "</test>\r\n"
     + "<!--epilog-->";

    public void testReaderConstructor() {
        
        try {
            new XSLTransform(new StringReader(notAStyleSheet));
            fail("Compiled non-stylesheet");
        }
        catch (XSLException ex) {
            // success   
        }
        
    }

    public void testInputStreamConstructor() throws IOException {
        
        try {
            byte[] data = notAStyleSheet.getBytes("UTF-8");
            new XSLTransform(new ByteArrayInputStream(data));
            fail("Compiled non-stylesheet");
        }
        catch (XSLException ex) {
            // success   
        }
        
    }

    public void testDocumentConstructor() 
      throws ParseException, IOException {
        
        try {
            Builder builder = new Builder();
            Document doc = builder.build(notAStyleSheet, 
              "http://www.example.com");
            new XSLTransform(doc);
            fail("Compiled non-stylesheet");
        }
        catch (XSLException ex) {
            // success   
        }
        
    }

    public void testFileConstructor() 
      throws ParseException, IOException {
        
        try {
            File f = new File("data/schematest.xml");
            new XSLTransform(f);
            fail("Compiled non-stylesheet");
        }
        catch (XSLException ex) {
            // success   
        }
        
    }

    public void testURLConstructor() 
      throws ParseException, IOException {
        
        try {
            File f = new File("data/schematest.xml");
            new XSLTransform(f.toURL().toExternalForm());
            fail("Compiled non-stylesheet");
        }
        catch (XSLException ex) {
            // success   
        }
        
    }

    public void testTransform() 
      throws ParseException, IOException, XSLException {
        
        File doc = new File("data/xslt/input/8-1.xml");
        File stylesheet = new File("data/xslt/input/8-8.xsl");
        Builder builder = new Builder();
        XSLTransform xform = new XSLTransform(stylesheet);
        NodeList output = xform.transform(builder.build(doc));
        assertEquals(1, output.size());
        Document result = new Document((Element) (output.get(0)));
        // For debugging
     /* File debug = new File("data/xslt/debug/8-8.xml");
        FileOutputStream out = new FileOutputStream(debug);
        Serializer serializer = new Serializer(out);
        serializer.write(result); */

        Document expected = builder.build("data/xslt/output/8-8.xml");
        assertEquals(expected, result);
        
    }

    public void testTransform2() 
      throws ParseException, IOException, XSLException {
        
        File doc = new File("data/xslt/input/8-1.xml");
        File stylesheet = new File("data/xslt/input/8-12.xsl");
        Builder builder = new Builder();
        XSLTransform xform = new XSLTransform(stylesheet);
        NodeList output = xform.transform(builder.build(doc));
        assertEquals(1, output.size());
        Document result = new Document((Element) (output.get(0)));
        // For debugging
     /* File debug = new File("data/xslt/debug/8-12.xml");
        FileOutputStream out = new FileOutputStream(debug);
        Serializer serializer = new Serializer(out);
        serializer.write(result); */

        Document expected = builder.build("data/xslt/output/8-12.xml");
        assertEquals(expected, result);
        
    }

    public void testTransformWithNamespaces() 
      throws ParseException, IOException, XSLException {
        
        File doc = new File("data/xslt/input/8-14.xml");
        File stylesheet = new File("data/xslt/input/8-15.xsl");
        Builder builder = new Builder();
        XSLTransform xform = new XSLTransform(stylesheet);
        Document input = builder.build(doc);
        NodeList output = xform.transform(input);
        assertEquals(1, output.size());
        Document result = new Document((Element) (output.get(0)));
        // For debugging
    /*  File debug = new File("data/xslt/debug/8-15.xml");
        FileOutputStream out = new FileOutputStream(debug);
        Serializer serializer = new Serializer(out);
        serializer.write(result); */

        Document expected = builder.build("data/xslt/output/8-15.xml");
        assertEquals(expected, result);
        
    }

}
