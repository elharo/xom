/* Copyright 2002, 2003 Elliotte Rusty Harold
   
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

package nu.xom.tests;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.ParsingException;
import nu.xom.Serializer;

/**
 * <p>
 *  This mostly verifies that white space
 *  is properly escaped on output.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0
 *
 */
public class RoundTripTest extends XOMTestCase {

    private Builder builder; 
    private Serializer serializer;
    private ByteArrayOutputStream out;

    public RoundTripTest(String name) {
        super(name);
    }

    protected void setUp() {
        builder = new Builder();
        out = new ByteArrayOutputStream();
        serializer = new Serializer(out);
    }

    public void testTabInAttributeValue() 
      throws IOException, ParsingException {
        Element test = new Element("test");
        test.addAttribute(new Attribute("tab", "\t"));
        Document doc = new Document(test);
        serializer.write(doc);
        byte[] input = out.toByteArray();
        Document reparsed = builder.build(new ByteArrayInputStream(input));
        Element root = reparsed.getRootElement();
        Attribute tab = root.getAttribute("tab");
        assertEquals(
          "Round trip did not preserve tab in attribute value", 
          "\t", tab.getValue()
        );
        assertEquals("Unexpected error on round trip", doc, reparsed);
    }

    public void testCarriageReturnInAttributeValue() 
      throws IOException, ParsingException {
        Element test = new Element("test");
        test.addAttribute(new Attribute("cr", "\r"));
        Document doc = new Document(test);
        serializer.write(doc);
        byte[] input = out.toByteArray();
        Document reparsed = builder.build(new ByteArrayInputStream(input));
        Element root = reparsed.getRootElement();
        Attribute cr = root.getAttribute("cr");
        assertEquals(
          "Round trip did not preserve carriage return in attribute value", 
          "\r", cr.getValue()
        );
        assertEquals("Unexpected error on round trip", doc, reparsed);
    }

    public void testCarriageReturnInText() 
      throws IOException, ParsingException {
        Element test = new Element("test");
        test.appendChild("\r");
        Document doc = new Document(test);
        serializer.write(doc);
        byte[] input = out.toByteArray();
        Document reparsed = builder.build(new ByteArrayInputStream(input));
        Element root = reparsed.getRootElement();
        String value = root.getValue();
        assertEquals(
          "Round trip did not preserve carriage return in text", 
          "\r", value
        );
        assertEquals("Unexpected error on round trip", doc, reparsed);
    }

    public void testLineFeedInAttributeValue() 
      throws IOException, ParsingException {
        Element test = new Element("test");
        test.addAttribute(new Attribute("lf", "\n"));
        Document doc = new Document(test);
        serializer.write(doc);
        byte[] input = out.toByteArray();
        Document reparsed = builder.build(new ByteArrayInputStream(input));
        Element root = reparsed.getRootElement();
        Attribute lf = root.getAttribute("lf");
        assertEquals(
          "Round trip did not preserve carriage return in attribute value", 
          "\n", lf.getValue()
        );
        assertEquals("Unexpected error on round trip", doc, reparsed);
    }

    public void testSpacesInAttributeValue() 
      throws IOException, ParsingException {
        Element test = new Element("test");
        test.addAttribute(new Attribute("spaces", "    "));
        Document doc = new Document(test);
        serializer.write(doc);
        byte[] input = out.toByteArray();
        Document reparsed = builder.build(new ByteArrayInputStream(input));
        Element root = reparsed.getRootElement();
        Attribute spaces = root.getAttribute("spaces");
        assertEquals(
          "Round trip did not preserve spaces in attribute value", 
          "    ", spaces.getValue()
        );
        assertEquals("Unexpected error on round trip", doc, reparsed);
    }

}
