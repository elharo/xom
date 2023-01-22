/* Copyright 2002-2004 Elliotte Rusty Harold
   
   This library is free software; you can redistribute it and/or modify
   it under the terms of version 2.1 of the GNU Lesser General Public 
   License as published by the Free Software Foundation.
   
   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
   GNU Lesser General Public License for more details.
   
   You should have received a copy of the GNU Lesser General Public
   License along with this library. If not, see
   <https://www.gnu.org/licenses/>.
   
   You can contact Elliotte Rusty Harold by sending e-mail to
   elharo@ibiblio.org. Please include the word "XOM" in the
   subject line. The XOM home page is located at https://xom.nu/
*/

package nu.xom.samples;

import nu.xom.Comment;
import nu.xom.DocType;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.ProcessingInstruction;
import nu.xom.Text;
import nu.xom.XMLException;

/**
 * 
 * <p>
 * Demonstrates the creation and serialization of an SVG document
 * that uses namespaces and the default namespace
 * and includes a document type declaration.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0
 *
 */
public class SimpleSVG {

  public static void main(String[] args) {
     
    try {
      // Create the document
      DocType svgDOCTYPE = new DocType(
       "svg", "-//W3C//DTD SVG 1.0//EN", 
       "http://www.w3.org/TR/2001/REC-SVG-20010904/DTD/svg10.dtd"
      );
       
      // Fill the document
      Element root = new Element("svg", 
        "http://www.w3.org/2000/svg");
      Document doc = new Document(root);
      doc.insertChild(svgDOCTYPE, 0);
      ProcessingInstruction xmlstylesheet = new ProcessingInstruction("xml-stylesheet",
       "type=\"text/css\" href=\"standard.css\"");
      doc.insertChild(xmlstylesheet, 0);
      Comment comment = new Comment(
       "An example from Chapter 10 of Processing XML with Java");
      doc.insertChild(comment, doc.indexOf(root));
      Element desc = new Element("desc", "http://www.w3.org/2000/svg");
      root.appendChild(desc);
      Text descText = new Text("An example from Processing XML with Java");
      desc.appendChild(descText);
      
      // Serialize the document onto System.out
      System.out.println(doc.toXML());
        
    }
    catch (XMLException ex) {
      System.err.println(ex); 
    }
  
  }

}

