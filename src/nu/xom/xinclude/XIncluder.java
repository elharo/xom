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

package nu.xom.xinclude;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Stack;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.DocType;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;
import nu.xom.NodeFactory;
import nu.xom.Nodes;
import nu.xom.ParentNode;
import nu.xom.ParsingException;
import nu.xom.Text;

/**
 * <p>
 *   Implements XInclude resolution as specified in the 
 *   <a href="http://www.w3.org/TR/2003/WD-xinclude-20031110">November
 *   10, 2003 2nd Last Call Working Draft of <cite>XML Inclusions
 *   (XInclude) Version 1.0</cite></a>. Fallbacks are supported.
 *   The XPointer <code>element()</code> scheme and shorthand XPointers
 *   are also supported. The XPointer <code>xpointer()</code> scheme
 *   is not supported.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0d23
 *
 */
public class XIncluder {

    // prevent instantiation
    private XIncluder() {}

    /**
     * <p>
     *   The namespace name of all XInclude elements.
     * </p>
     */
    public final static String XINCLUDE_NS 
      = "http://www.w3.org/2003/XInclude";

    /**
     * <p>
     * Returns a copy of the argument <code>Document</code> 
     * in which all <code>xinclude:include</code> elements have been  
     * replaced by their referenced content. The original 
     * <code>Document</code> object is not modified.
     * Resolution is recursive; that is, include elements
     * in the included documents are themselves resolved.
     * The <code>Document</code> returned contains no
     * include elements.
     * </p>
     * 
     * @param in the <code>Document</code> in which include elements
     *     should be resolved
     * 
     * @return copy of the argument <code>Document</code> in which
     *     all XInclude have been replaced by their referenced content
     * 
     * @throws BadParseAttributeException if an <code>include</code>  
     *     element has a <code>parse</code> attribute with any value 
     *     other than <code>text</code> or <code>parse</code>
     * @throws CircularIncludeException if this <code>Element</code>  
     *     contains an XInclude element that attempts to include 
     *     a document in which this element is directly or indirectly 
     *     included.
     * @throws IOException if an included document could not be loaded,
     *     and no fallback was available
     * @throws MissingHrefException if an <code>xinclude:include</code> 
     *      element does not have an <code>href</code> attribute.
     * @throws ParsingException if an included XML document 
     *     was malformed
     * @throws UnsupportedEncodingException if an included document 
     *     used an encoding this parser does not support, and no
     *     fallback was available
     * @throws XIncludeException if the document violates the
     *     syntax rules of XInclude
     * @throws XMLException if resolving an include element would 
     *      result in a malformed document,
     */
     public static Document resolve(Document in)  
       throws BadParseAttributeException, CircularIncludeException, 
             IOException, MissingHrefException, ParsingException, 
             UnsupportedEncodingException, XIncludeException {        
        return resolve(in, new Builder());   
    }

    /**
     * <p>
     * Returns a copy of the argument <code>Document</code> 
     * in which all <code>xinclude:include</code> elements have been  
     * replaced by their referenced content. The original 
     * <code>Document</code> object is not modified.
     * Resolution is recursive; that is, include elements
     * in the included documents are themselves resolved.
     * The <code>Document</code> returned contains no
     * include elements.
     * </p>
     * 
     * @param in the <code>Document</code> in which include elements
     *     should be resolved
     * @param builder the <code>Builder</code> used to build the
     *     nodes included from other documents
     * 
     * @return copy of the argument <code>Document</code> in which
     *     all XInclude have been replaced by their referenced content
     * 
     * @throws BadParseAttributeException if an <code>include</code>  
     *     element has a <code>parse</code> attribute with any value 
     *     other than <code>text</code> or <code>parse</code>
     * @throws CircularIncludeException if this <code>Element</code>  
     *     contains an XInclude element that attempts to include 
     *     a document in which this element is directly or indirectly 
     *     included.
     * @throws IOException if an included document could not be loaded,
     *     and no fallback was available
     * @throws MissingHrefException if an <code>xinclude:include</code> 
     *      element does not have an href attribute.
     * @throws ParsingException if an included XML document 
     *     was malformed
     * @throws UnsupportedEncodingException if an included document 
     *     used an encoding this parser does not support, and no
     *     fallback was available
     * @throws XIncludeException if the document violates the
     *     syntax rules of XInclude
     * @throws XMLException if resolving an include element would 
     *      result in a malformed document,
     */
     public static Document resolve(Document in, Builder builder)  
       throws BadParseAttributeException, CircularIncludeException, 
             IOException, MissingHrefException, ParsingException, 
             UnsupportedEncodingException, XIncludeException {        
        Document copy = new Document(in);
        resolveInPlace(copy, builder);
        return copy;   
    }

    /**
     * <p>
     * Modifies a <code>Document</code> by replacing all  
     * <code>xinclude:include</code> elements 
     * by their referenced content. 
     * Resolution is recursive; that is, include elements
     * in the included documents are themselves resolved.
     * The <code>Document</code> returned contains no
     * include elements.
     * </p>
     * 
     * @param in the <code>Document</code> in which include elements
     *     should be resolved
     * @param builder the <code>Builder</code> used to build the
     *     nodes included from other documents
     *
     * @throws BadParseAttributeException if an <code>include</code>  
     *     element has a <code>parse</code> attribute
     *     with any value other than <code>text</code> 
     *     or <code>parse</code>
     * @throws CircularIncludeException if this <code>Element</code> 
     *     contains an XInclude element that attempts to include a  
     *     document in which this element is directly or indirectly 
     *     included
     * @throws IOException if an included document could not be loaded,
     *     and no fallback was available
     * @throws MissingHrefException if an <code>xinclude:include</code>
     *     element does not have an <code>href</code> attribute.
     * @throws ParsingException if an included XML document
     *    was malformed
     * @throws UnsupportedEncodingException if an included document 
     *     used an encoding this parser does not support, and no 
     *     fallback was available
     * @throws XIncludeException if the document violates the
     *     syntax rules of XInclude
     * @throws XMLException if resolving an include element would 
     *     result in a malformed document
     */
    public static void resolveInPlace(Document in) 
      throws BadParseAttributeException, CircularIncludeException,  
             IOException, MissingHrefException, ParsingException, 
             UnsupportedEncodingException, XIncludeException {        
        resolveInPlace(in, new Builder());
    }

    /**
     * <p>
     * Modifies a <code>Document</code> by replacing all 
     * <code>xinclude:include</code> elements with their referenced 
     * content. Resolution is recursive; that is, include elements
     * in the included documents are themselves resolved.
     * The <code>Document</code> returned contains no
     * include elements.
     * </p>
     * 
     * @param in the <code>Document</code> in which include elements
     *     should be resolved
     * @param builder the <code>Builder</code> used to build the
     *     nodes included from other documents
     * 
     * @throws BadParseAttributeException if an <code>include</code>  
     *     element has a <code>parse</code> attribute
     *     with any value other than <code>text</code> 
     *     or <code>parse</code>
     * @throws CircularIncludeException if this <code>Element</code> 
     *     contains an XInclude element that attempts to include a  
     *     document in which this element is directly or indirectly 
     *     included
     * @throws IOException if an included document could not be loaded,
     *     and no fallback was available
     * @throws MissingHrefException if an <code>xinclude:include</code>
     *     element does not have an <code>href</code> attribute.
     * @throws ParsingException if an included XML document
     *    was malformed
     * @throws UnsupportedEncodingException if an included document 
     *     used an encoding this parser does not support, and no 
     *     fallback was available
     * @throws XIncludeException if the document violates the
     *     syntax rules of XInclude
     * @throws XMLException if resolving an include element would 
     *     result in a malformed document
     */
    public static void resolveInPlace(Document in, Builder builder) 
      throws BadParseAttributeException, CircularIncludeException,  
             IOException, MissingHrefException, ParsingException, 
             UnsupportedEncodingException, XIncludeException {        
        resolveInPlace(in, builder, new Stack()); /**&&**/
    }

    /**
     * <p>
     * Modifies a <code>Nodes</code> object by replacing all 
     * <code>xinclude:include</code> elements with their referenced 
     * content. Resolution is recursive; that is, include elements
     * in the included documents are themselves resolved.
     * Furthermore, include elements that are children or 
     * descendants of elements in this list are also resolved.
     * The <code>Nodes</code> object returned contains no
     * include elements.
     * </p>
     * 
     * @param in the <code>Nodes</code> object in which include 
     *     elements should be resolved.
     * 
     * @throws BadParseAttributeException if an <code>include</code>  
     *     element has a <code>parse</code> attribute
     *     with any value other than <code>text</code> 
     *     or <code>parse</code>
     * @throws CircularIncludeException if this <code>Element</code> 
     *     contains an XInclude element that attempts to include a  
     *     document in which this element is directly or indirectly 
     *     included
     * @throws IOException if an included document could not be loaded,
     *     and no fallback was available
     * @throws MissingHrefException if an <code>xinclude:include</code>
     *     element does not have an <code>href</code> attribute.
     * @throws ParsingException if an included XML document 
     *     was malformed
     * @throws UnsupportedEncodingException if an included document 
     *     used an encoding this parser does not support, and no 
     *     fallback was available
     * @throws XIncludeException if the document violates the
     *     syntax rules of XInclude
     * @throws XMLException if resolving an include element would 
     *     result in a malformed document
     */
    /* private static void resolveInPlace(Nodes in) 
      throws IOException, ParsingException, XIncludeException { 
        resolveInPlace(in, new Builder());
    } */

    /**
     * <p>
     * Modifies a <code>Nodes</code> object by replacing all 
     * XInclude elements with their referenced content.
     * Resolution is recursive; that is, include elements
     * in the included documents are themselves resolved.
     * Furthermore, include elements that are children or 
     * descendants of elements in this list are also resolved.
     * The <code>Nodes</code> object returned contains no
     * include elements.
     * </p>
     * 
     * @param in the <code>Nodes</code> object in which include 
     *     elements should be resolved.
     * 
     * @throws BadParseAttributeException if an <code>include</code>  
     *     element has a <code>parse</code> attribute
     *     with any value other than <code>text</code> 
     *     or <code>parse</code>
     * @throws CircularIncludeException if this <code>Element</code> 
     *     contains an XInclude element that attempts to include a  
     *     document in which this element is directly or indirectly 
     *     included
     * @throws IOException if an included document could not be loaded,
     *     and no fallback was available
     * @throws MissingHrefException if an <code>xinclude:include</code>
     *     element does not have an <code>href</code> attribute.
     * @throws ParsingException if an included XML document 
     *     was malformed
     * @throws UnsupportedEncodingException if an included document 
     *     used an encoding this parser does not support, and no 
     *     fallback was available
     * @throws XIncludeException if the document violates the
     *     syntax rules of XInclude
     * @throws XMLException if resolving an include element would 
     *     result in a malformed document
     */
    /* private static void resolveInPlace(Nodes in, Builder builder) 
      throws IOException, ParsingException, XIncludeException { 
        for (int i = 0; i < in.size(); i++) {
            Node child = in.get(i);
            if (child instanceof Element) { 
                if (isIncludeElement((Element) child)) {
                    // Need to replace the old nodes with the result
                    // of including this element????
                }
                else {
                    resolve((Element) child, builder, new Stack());
                }
            }
            else if (child instanceof Document) {
                resolveInPlace((Document) child, builder,  new Stack());   
            }
        }
    } */

    /**
     * <p>
     * Returns a <code>Nodes</code> object in which all 
     * <code>xinclude:include</code> elements have been replaced
     * by their referenced content. Resolution is deep; that is, 
     * include elements that are descendants of nodes in this list
     * are replaced as well.
     * </p>
     * 
     * @param in the <code>Nodes</code> object in which include 
     *     elements should be resolved
     * 
     * @return a new Nodes object which does not contain any 
     *   <code>xinclude:include</code> elements
     * 
     * @throws BadParseAttributeException if an <code>include</code>  
     *     element has a <code>parse</code> attribute
     *     with any value other than <code>text</code> 
     *     or <code>parse</code>
     * @throws CircularIncludeException if this <code>Element</code> 
     *     contains an XInclude element that attempts to include a  
     *     document in which this element is directly or indirectly 
     *     included
     * @throws IOException if an included document could not be loaded,
     *     and no fallback was available
     * @throws MissingHrefException if an <code>xinclude:include</code>
     *     element does not have an <code>href</code> attribute
     * @throws ParsingException if an included XML document 
     *     was malformed
     * @throws UnsupportedEncodingException if an included document 
     *     used an encoding this parser does not support, and no 
     *     fallback was available
     * @throws XIncludeException if the document violates the
     *     syntax rules of XInclude
     * @throws XMLException if resolving an include element would 
     *     result in a malformed document
     * 
     */
    private static Nodes resolve(Nodes in) 
      throws IOException, ParsingException, XIncludeException { 
        return resolve(in, new Builder());
    }

    /**
     * <p>
     * Modifies a <code>Nodes</code> object by replacing all 
     * <code>xinclude:include</code> elements with their referenced 
     * content. Resolution is recursive; that is, include elements
     * in the included documents are themselves resolved.
     * Furthermore, include elements that are children or 
     * descendants of elements in this list are also resolved.
     * The <code>Nodes</code> object returned contains no
     * include elements.
     * </p>
     * 
     * @param in the <code>Nodes</code> object in which include 
     *     elements should be resolved.
     * 
     * @throws BadParseAttributeException if an <code>include</code>  
     *     element has a <code>parse</code> attribute
     *     with any value other than <code>text</code> 
     *     or <code>parse</code>
     * @throws CircularIncludeException if this <code>Element</code> 
     *     contains an XInclude element that attempts to include a  
     *     document in which this element is directly or indirectly 
     *     included
     * @throws IOException if an included document could not be loaded,
     *     and no fallback was available
     * @throws MissingHrefException if an <code>xinclude:include</code>
     *     element does not have an <code>href</code> attribute.
     * @throws ParsingException if an included XML document 
     *     was malformed
     * @throws UnsupportedEncodingException if an included document 
     *     used an encoding this parser does not support, and no 
     *     fallback was available
     * @throws XIncludeException if the document violates the
     *     syntax rules of XInclude
     * @throws XMLException if resolving an include element would 
     *     result in a malformed document
     */
    private static void resolveInPlace(Nodes in) 
      throws IOException, ParsingException, XIncludeException { 
        resolveInPlace(in, new Builder());
    }

    /**
     * <p>
     * Modifies a <code>Nodes</code> object by replacing all 
     * XInclude elements with their referenced content.
     * Resolution is recursive; that is, include elements
     * in the included documents are themselves resolved.
     * Furthermore, include elements that are children or 
     * descendants of elements in this list are also resolved.
     * The <code>Nodes</code> object returned contains no
     * include elements.
     * </p>
     * 
     * @param in the <code>Nodes</code> object in which include 
     *     elements should be resolved.
     * 
     * @throws BadParseAttributeException if an <code>include</code>  
     *     element has a <code>parse</code> attribute
     *     with any value other than <code>text</code> 
     *     or <code>parse</code>
     * @throws CircularIncludeException if this <code>Element</code> 
     *     contains an XInclude element that attempts to include a  
     *     document in which this element is directly or indirectly 
     *     included
     * @throws IOException if an included document could not be loaded,
     *     and no fallback was available
     * @throws MissingHrefException if an <code>xinclude:include</code>
     *     element does not have an <code>href</code> attribute.
     * @throws ParsingException if an included XML document 
     *     was malformed
     * @throws UnsupportedEncodingException if an included document 
     *     used an encoding this parser does not support, and no 
     *     fallback was available
     * @throws XIncludeException if the document violates the
     *     syntax rules of XInclude
     * @throws XMLException if resolving an include element would 
     *     result in a malformed document
     */
    private static void resolveInPlace(Nodes in, Builder builder) 
      throws IOException, ParsingException, XIncludeException { 
        for (int i = 0; i < in.size(); i++) {
            Node child = in.get(i);
            if (child instanceof Element) {
                Element element = (Element) child;
                if (isIncludeElement(element)) {
                    Nodes nodes = resolveSilently(element, builder, new Stack());
                    in.remove(i);
                    for (int j = 0; j < nodes.size(); j++) {
                        in.insert(nodes.get(j), i++);
                    }
                }
                else {       
                    resolve((Element) child, builder, new Stack());
                }
            }
            else if (child instanceof Document) {
                resolveInPlace((Document) child, builder,  new Stack());   
            }
        }
    }

    // could any of the resolve(nodes) methods be removed????

    /**
     * <p>
     * Returns a <code>Nodes</code> object in which all 
     * <code>xinclude:include</code> elements have been replaced
     * by their referenced content. Resolution is deep; that is, 
     * include elements that are descendants of nodes in this list
     * are replaced as well.
     * </p>
     * 
     * @param in the <code>Nodes</code> object in which include 
     *     elements should be resolved
     * 
     * @return a new Nodes object which contains no 
     *   <code>xinclude:include</code> elements
     * 
     * @throws BadParseAttributeException if an <code>include</code>  
     *     element has a <code>parse</code> attribute
     *     with any value other than <code>text</code> 
     *     or <code>parse</code>
     * @throws CircularIncludeException if this <code>Element</code> 
     *     contains an XInclude element that attempts to include a  
     *     document in which this element is directly or indirectly 
     *     included
     * @throws IOException if an included document could not be loaded,
     *     and no fallback was available
     * @throws MissingHrefException if an <code>xinclude:include</code>
     *     element does not have an <code>href</code> attribute
     * @throws ParsingException if an included XML document 
     *     was malformed
     * @throws UnsupportedEncodingException if an included document 
     *     used an encoding this parser does not support, and no 
     *     fallback was available
     * @throws XIncludeException if the document violates the
     *     syntax rules of XInclude
     * @throws XMLException if resolving an include element would 
     *     result in a malformed document
     */
    private static Nodes resolve(Nodes in, Builder builder) 
      throws IOException, ParsingException, XIncludeException {
          
        Nodes result = new Nodes(); 
        for (int i = 0; i < in.size(); i++) {
            Node child = in.get(i).copy();
            if (child instanceof Element) { 
                Document doc = new Document((Element) child);
                resolveInPlace(doc, builder, new Stack());
                for (int j = 0; j < doc.getChildCount(); j++) {
                    Node kid = doc.getChild(j);
                    result.append(kid);
                    if (kid instanceof Element) {
                        doc.setRootElement(new Element("temp"));
                    } 
                    kid.detach();
                }                  
            }
            else if (child instanceof Document) {
                resolveInPlace((Document) child, builder,  new Stack());
                result.append(child);  
            }
            else {
                result.append(child);   
            }
            
        }
        return result;
    }

    private static boolean contains(ParentNode ancestor, Node descendant) {
        
        for (Node parent = descendant; parent != null; parent=parent.getParent()) {
            if (parent == ancestor) return true;  
        }    
        
        return false;   
    }

    private static void resolveInPlace(
      Document in, Builder builder, Stack baseURLs) 
      throws IOException, ParsingException, XIncludeException {
        
        String base = in.getBaseURI();
        if (baseURLs.indexOf(base) != -1) {
            throw new CircularIncludeException(
              "Tried to include the already included document" + base +
              " from " + in.getBaseURI(), in.getBaseURI());
        } 
        baseURLs.push(base);       
        resolve(in.getRootElement(), builder, baseURLs);
        baseURLs.pop();
    }

    private static Nodes resolveSilently(Element element, Builder builder, Stack baseURLs)
      throws IOException, ParsingException, XIncludeException {
        
        if (isIncludeElement(element)) {
            String parse = element.getAttributeValue("parse");
            if (parse == null) parse = "xml";
            String xpointer = element.getAttributeValue("xpointer");
            String encoding = element.getAttributeValue("encoding");
            String href = element.getAttributeValue("href");
            if (href == null && xpointer == null) {
                throw new MissingHrefException(
                  "Missing href attribute", 
                  element.getDocument().getBaseURI()
                );   
            }
            if (href != null) href = convertToURI(href);
            
            testForMultipleFallbacks(element);

            // ParentNode parent = element.getParent();
            String base = element.getBaseURI();
            URL baseURL = null;
            if (base != null) {
                try {
                    baseURL = new URL(base);     
                }
                catch (MalformedURLException ex) {
                   // don't use base   
                }
            }
            URL url = null;
            try {
                // xml:base attributes added to maintain the 
                // base URI should not have fragment IDs

                if (baseURL != null && href != null) url = new URL(baseURL, href);
                else if (href != null) url = new URL(href);                
                if (parse.equals("xml")) {
                    Nodes replacements;
                    if (url != null) { 
                        replacements  = downloadXMLDocument(url, xpointer, builder, baseURLs);
                        // Add base URIs. Base URIs added by XInclusion require
                        // the element to maintain the same base URI as it had  
                        // in the original document. Since its base URI in the 
                        // original document does not contain a fragment ID,
                        // therefore its base URI after inclusion shouldn't, 
                        // and this special case is unnecessary. Base URI fixup
                        // should not add the fragment ID. 
                        for (int i = 0; i < replacements.size(); i++) {
                            Node child = replacements.get(i);
                            if (child instanceof Element) {
                                String noFragment = url.toExternalForm();
                                if (noFragment.indexOf('#') >= 0) {
                                    noFragment = noFragment.substring(
                                      0, noFragment.indexOf('#'));
                                }
                                Element baseless = (Element) child;
                                Attribute baseAttribute = new Attribute(
                                  "xml:base", 
                                  "http://www.w3.org/XML/1998/namespace", 
                                  noFragment 
                                );
                                baseless.addAttribute(baseAttribute);   
                            }
                        }  
                    }
                    else {
                        Nodes originals = XPointer.resolve(element.getDocument(), xpointer);
                        replacements = new Nodes(); 
                        for (int i = 0; i < originals.size(); i++) {
                            Node original = originals.get(i);
                            if (original instanceof Element) {
                                if (contains((Element) original, element)) {
                                    throw new CircularIncludeException("Element tried to include itself"); 
                                }  
                            }
                            replacements.append(original.copy());        
                        }  
                        replacements = resolve(replacements, builder);  
                        // Write a unit test where an xpointer includes itself
                        // indirectly through intermediary documents???? to make
                        // sure it still throws CircularIncludeException                         
                    }
                    return replacements; 
                }  // end parse="xml"
                else if (parse.equals("text")) {                   
                    Text replacement 
                      = downloadTextDocument(url, encoding, builder);
                    return new Nodes(replacement);
                }
                else {
                   throw new BadParseAttributeException(
                     "Bad value for parse attribute: " + parse, 
                     element.getDocument().getBaseURI());   
                }
            
            }
            catch (IOException ex) {
                return processFallback(element, builder, baseURLs, ex);
            }
            catch (XPointerSyntaxException ex) {
                return processFallback(element, builder, baseURLs, ex);
            }
            catch (XPointerResourceException ex) {
                // Process fallbacks;  I'm not sure this is correct 
                // behavior. Possibly this should include nothing. See
                // http://lists.w3.org/Archives/Public/www-xml-xinclude-comments/2003Aug/0000.html
                // Daniel Veillard thinks this is correct. See
                // http://lists.w3.org/Archives/Public/www-xml-xinclude-comments/2003Aug/0001.html
                return processFallback(element, builder, baseURLs, ex);
            }
            
        }
        else if (isFallbackElement(element)) {
            throw new MisplacedFallbackException(
              "Fallback element outside include element", 
              element.getDocument().getBaseURI()
            );
        }
        else {
            Elements children = element.getChildElements();
            for (int i = 0; i < children.size(); i++) {
                resolve(children.get(i), builder, baseURLs);   
            } 
            return new Nodes(element);
        }
        
    }

    // Should this method simply return a Nodes and not do any
    // of the detaching and adding and so forth???? leaving that
    // till higher in the call chain????
    private static void resolve(
      Element element, Builder builder, Stack baseURLs)
      throws IOException, ParsingException, XIncludeException {
        
        if (isIncludeElement(element)) {
            String parse = element.getAttributeValue("parse");
            if (parse == null) parse = "xml";
            String xpointer = element.getAttributeValue("xpointer");
            String encoding = element.getAttributeValue("encoding");
            String href = element.getAttributeValue("href");
            if (href == null && xpointer == null) {
                throw new MissingHrefException(
                  "Missing href attribute", 
                  element.getDocument().getBaseURI()
                );   
            }
            if (href != null) href = convertToURI(href);
            
            testForMultipleFallbacks(element);

            ParentNode parent = element.getParent();
            String base = element.getBaseURI();
            URL baseURL = null;
            if (base != null) {
                try {
                    baseURL = new URL(base);     
                }
                catch (MalformedURLException ex) {
                   // don't use base   
                }
            }
            URL url = null;
            try {
                // xml:base attributes added to maintain the 
                // base URI should not have fragment IDs

                if (baseURL != null && href != null) url = new URL(baseURL, href);
                else if (href != null) url = new URL(href);                
                if (parse.equals("xml")) {
                    Nodes replacements;
                    if (url != null) { 
                        replacements  = downloadXMLDocument(url, xpointer, builder, baseURLs);
                        // Add base URIs. Base URIs added by XInclusion require
                        // the element to maintain the same base URI as it had  
                        // in the original document. Since its base URI in the 
                        // original document does not contain a fragment ID,
                        // therefore its base URI after inclusion shouldn't, 
                        // and this special case is unnecessary. Base URI fixup
                        // should not add the fragment ID. 
                        for (int i = 0; i < replacements.size(); i++) {
                            Node child = replacements.get(i);
                            if (child instanceof Element) {
                                String noFragment = url.toExternalForm();
                                if (noFragment.indexOf('#') >= 0) {
                                    noFragment = noFragment.substring(
                                      0, noFragment.indexOf('#'));
                                }
                                Element baseless = (Element) child;
                                Attribute baseAttribute = new Attribute(
                                  "xml:base", 
                                  "http://www.w3.org/XML/1998/namespace", 
                                  noFragment 
                                );
                                baseless.addAttribute(baseAttribute);   
                            }
                        }  
                    }
                    else {
                        Nodes originals = XPointer.resolve(element.getDocument(), xpointer);
                        replacements = new Nodes(); 
                        for (int i = 0; i < originals.size(); i++) {
                            Node original = originals.get(i);
                            if (original instanceof Element) {
                                if (contains((Element) original, element)) {
                                    throw new CircularIncludeException("Element tried to include itself"); 
                                }  
                            }
                            replacements.append(original.copy());        
                        }  
                        replacements = resolve(replacements, builder);  
                        // Write a unit test where an xpointer includes itself
                        // indirectly through intermediary documents???? to make
                        // sure it still throws CircularIncludeException
                                                 
                    }
                      
                    // Will fail if we're replacing the root element with 
                    // a node list containing zero or multiple elements,
                    // but that should fail. However, I may wish to 
                    // adjust the type of exception thrown. This is only
                    // relevant if I add support for the xpointer scheme
                    // since otherwise you can only point at one element
                    // or document.
                    if (parent instanceof Element) {
                        int position = parent.indexOf(element);
                        for (int i = 0; i < replacements.size(); i++) {
                            Node child = replacements.get(i);
                            parent.insertChild(child, position+i); 
                        }
                        element.detach();
                    }
                    else if (parent == null) {
                        // what to do????   
                    }
                    else {  // root element needs special treatment
                        Document doc = (Document) parent;
                        int i = 0;
                        // prolog and root
                        while (true) {
                            Node child = replacements.get(i);
                            i++;
                            if (child instanceof Element) {
                                doc.setRootElement((Element) child);
                                break;   
                            }
                            else {
                                doc.insertChild(
                                  child, doc.indexOf(element)
                                ); 
                            }

                        }
                        // epilog
                        Element root = doc.getRootElement();
                        int position = doc.indexOf(root);
                        for (int j=i; j < replacements.size(); j++) {
                            doc.insertChild(
                              replacements.get(j), position+1+j-i
                            );                             
                        }
                    }
                }
                else if (parse.equals("text")) {                   
                    Text replacement 
                      = downloadTextDocument(url, encoding, builder);
                    parent.replaceChild(element, replacement);
                }
                else {
                   throw new BadParseAttributeException(
                     "Bad value for parse attribute: " + parse, 
                     element.getDocument().getBaseURI());   
                }
            
            }
            catch (IOException ex) {
                processFallback(element, builder, baseURLs, parent, ex);
            }
            catch (XPointerSyntaxException ex) {
                processFallback(element, builder, baseURLs, parent, ex);
            }
            catch (XPointerResourceException ex) {
                // Process fallbacks;  I'm not sure this is correct 
                // behavior. Possibly this should include nothing. See
                // http://lists.w3.org/Archives/Public/www-xml-xinclude-comments/2003Aug/0000.html
                // Daniel Veillard thinks this is correct. See
                // http://lists.w3.org/Archives/Public/www-xml-xinclude-comments/2003Aug/0001.html
                processFallback(element, builder, baseURLs, parent, ex);
            }
            
        }
        else if (isFallbackElement(element)) {
            throw new MisplacedFallbackException(
              "Fallback element outside include element", 
              element.getDocument().getBaseURI()
            );
        }
        else {
            Elements children = element.getChildElements();
            for (int i = 0; i < children.size(); i++) {
                resolve(children.get(i), builder, baseURLs);   
            } 
        }
        
    }

    /**
     * <p>
     *   This is a controversial test. NIST test case 12 
     *   requires it, but I'm not convinced the XInclude spec does,
     *   in the case where there's no resource error.
     *   I've requested clarification from the working group.
     * </p>
     */
    private static void testForMultipleFallbacks(Element element) 
      throws XIncludeException {
        Elements fallbacks 
          = element.getChildElements("fallback", XINCLUDE_NS);
        if (fallbacks.size() > 1) {
            throw new XIncludeException("Multiple fallback elements", 
              element.getDocument().getBaseURI());   
        }
        
        // while we're at it let's test to see if there are any
        // xi:include children. I actually don't think the spec
        // requires this, but the test cases do. I've filed a 
        // comment with the WG
        Element include 
          = element.getFirstChildElement("include", XINCLUDE_NS);
        if (include != null) {
            throw new XIncludeException(
              "Include element contains an include child",
              element.getDocument().getBaseURI());   
        }
        
    }

    private static void processFallback(
      Element element, Builder builder, Stack baseURLs, ParentNode parent, Exception ex)
        throws XIncludeException, IOException, ParsingException {
           Elements fallbacks 
              = element.getChildElements("fallback", XINCLUDE_NS);
           if (fallbacks.size() == 0) {
                if (ex instanceof IOException) throw (IOException) ex;
                XIncludeException ex2 = new XIncludeException(
                  ex.getMessage(), element.getDocument().getBaseURI());
                ex2.initCause(ex);
                throw ex2;
           }
           else if (fallbacks.size() > 1) {
                throw new XIncludeException("Multiple fallbacks", 
                   element.getDocument().getBaseURI());
           }
             
           Element fallback = fallbacks.get(0); 
           while (fallback.getChildCount() > 0) {
                Node child = fallback.getChild(0);
                if (child instanceof Element) {
                    resolve((Element) child, builder, baseURLs);
                }
                child = fallback.getChild(0);
                child.detach();
                parent.insertChild(child, parent.indexOf(element)); 
           }
           element.detach();
    }

    private static Nodes processFallback(
      Element element, Builder builder, Stack baseURLs, Exception ex)
        throws XIncludeException, IOException, ParsingException {
           Elements fallbacks 
              = element.getChildElements("fallback", XINCLUDE_NS);
           if (fallbacks.size() == 0) {
                if (ex instanceof IOException) throw (IOException) ex;
                XIncludeException ex2 = new XIncludeException(
                  ex.getMessage(), element.getDocument().getBaseURI());
                ex2.initCause(ex);
                throw ex2;
           }
           else if (fallbacks.size() > 1) {
                throw new XIncludeException("Multiple fallbacks", 
                   element.getDocument().getBaseURI());
           }
             
           Element fallback = fallbacks.get(0); 
           Nodes result = new Nodes();
           for (int i = 0; i < fallback.getChildCount(); i++) {
                Node child = fallback.getChild(0);
                if (child instanceof Element) {
                    Nodes nodes = resolveSilently((Element) child, builder, baseURLs);
                    for (int j = 0; j < nodes.size(); j++) {
                        result.append(nodes.get(j));   
                    }
                } 
                else {
                    result.append(child);   
                } 
           }
           return result;

    }

   

    private static Nodes downloadXMLDocument(
      URL source, String xpointer, Builder builder, Stack baseURLs) 
      throws IOException, ParsingException, XIncludeException, 
             XPointerSyntaxException, XPointerResourceException {
    
        Document doc = builder.build(
          source.openStream(), source.toExternalForm()); 
          
        Nodes included;
        if (xpointer != null && xpointer.length() != 0) {
            included = XPointer.resolve(doc, xpointer); 
            resolveInPlace(included, builder);
        }
        else {
            resolveInPlace(doc, builder, baseURLs); // remove include elements
            included = new Nodes();
            for (int i = 0; i < doc.getChildCount(); i++) {
                Node child = doc.getChild(i);
                if (!(child instanceof DocType)) {
                    included.append(child);
                }            
            }
        }
        // so we can detach the old root if necessary
        doc.setRootElement(new Element("f")); 
        for (int i = 0; i < included.size(); i++) {
            included.get(i).detach();         
        }  
            
        return included;
        
    }


  /**
    * <p>
    * This utility method reads a document at a specified URL
    * and returns the contents of that document as a <code>Text</code>.
    * It's used to include files with <code>parse="text"</code>.
    * </p>
    *
    * @param source   <code>URL</code> of the document to download 
    * @param encoding encoding of the document; e.g. UTF-8,
    *                  ISO-8859-1, etc.
    * @return the document retrieved from the source <code>URL</code>
    * @throws <code>UnavailableResourceException</code> if the source
    *     document cannot be located or cannot be read
    */    
    private static Text downloadTextDocument(
      URL source, String encoding, Builder builder) 
      throws IOException, XIncludeException {
         
        if (encoding == null || encoding.length() == 0) {
            encoding = "UTF-8"; 
        }

        URLConnection uc = source.openConnection();
        String encodingFromHeader = uc.getContentEncoding();
        String contentType = uc.getContentType();
        int contentLength = uc.getContentLength();
        if (contentLength < 0) contentLength = 1024;
        InputStream in = new BufferedInputStream(uc.getInputStream());
        if (encodingFromHeader != null) encoding = encodingFromHeader;
        else {
            if (contentType != null) {
                contentType = contentType.toLowerCase();
                if (contentType.equals("text/xml") 
                  || contentType.equals("application/xml")   
                  || (contentType.startsWith("text/") 
                        && contentType.endsWith("+xml") ) 
                  || (contentType.startsWith("application/") 
                        && contentType.endsWith("+xml"))) {
                     encoding 
                       = EncodingHeuristics.readEncodingFromStream(in);
                }
            }
        }
        // workaround for pre-1.3 VMs that don't recognize UTF-16
        if (encoding.equalsIgnoreCase("UTF-16")) {
            String version = System.getProperty("java.version");   
            if (version.startsWith("1.2") 
              || version.startsWith("1.1")) {
                // is it  big-endian or little-endian?
                in.mark(2);
                int first = in.read();
                if (first == 0xFF) encoding = "UnicodeLittle";
                else encoding="UnicodeBig";
                in.reset();  
            }
        }
        InputStreamReader reader = new InputStreamReader(in, encoding);
        int c;
        StringBuffer sb = new StringBuffer(contentLength);
        while ((c = reader.read()) != -1) {
          sb.append((char) c);
        }
        
        NodeFactory factory = builder.getNodeFactory();
        if (factory != null) {
            Nodes results = factory.makeText(sb.toString());
            sb = new StringBuffer(sb.length());
            for (int i = 0; i < results.size(); i++) {
                try {
                    sb.append((Text) (results.get(i))); 
                }
                catch (ClassCastException ex) {
                    throw new XIncludeException(
                      "Factory made a non-text node when parse=\"text\"");   
                }
            }
            
        }
        return new Text(sb.toString());
      
    }
    
    private static boolean isIncludeElement(Element element) {
     
        return element.getLocalName().equals("include")
          && element.getNamespaceURI().equals(XINCLUDE_NS);
        
    }

    private static boolean isFallbackElement(Element element) {
     
        return element.getLocalName().equals("fallback")
          && element.getNamespaceURI().equals(XINCLUDE_NS);
        
    }

/* The algorithm used is that defined in Namespaces in XML 1.1:
  Some characters are disallowed in URI references, even if they 
  are allowed in XML; the disallowed characters, according to [RFC2396]
   and [RFC2732], are the control characters #x0 to #x1F and #x7F, 
   space #x20, the delimiters '<' #x3C, '>' #x3E and '"' #x22, the 
   unwise characters '{' #x7B, '}' #x7D, '|' #x7C, '\' #x5C, '^' #x5E 
   and '`' #x60, as well as all characters above #x7F.

[Definition: An IRI reference is a string that can be converted to a  
URI reference by escaping all disallowed characters as follows: ]

   1. Each disallowed character is converted to UTF-8 [Unicode 3.2] 
      as one or more bytes.
   2. The resulting bytes are escaped with the URI escaping mechanism 
      (that is, converted to %HH, where HH is the hexadecimal notation 
      of the byte value).
   3. The original character is replaced by the resulting 
      character sequence. 
*/   
    private static String convertToURI(String iri) {
        
        try {
            byte[] utf8Data = iri.getBytes("UTF-8");
            StringBuffer uri = new StringBuffer(utf8Data.length);
            for (int i = 0; i < utf8Data.length; i++) {
                if (needsEscaping(utf8Data[i])) {
                    uri.append(hexEscape(utf8Data[i]));
                }
                else {
                    uri.append((char) utf8Data[i]);   
                }
                
            }      
            
            return uri.toString();
        }
        catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(
              "VM is broken. It does not support UTF-8.");   
        }       
        
    }
    
    private static boolean needsEscaping(byte c) {
        
        // This first test includes high-byte characters
        // which are negative as bytes
        if (c <= 0x20) return true;
        if (c <= 0x21) return false;
        if (c <= 0x22) return true;
        if (c <= 0x3B) return false;
        if (c <= 0x3C) return true;
        if (c <= 0x3D) return false;
        if (c <= 0x3E) return true;
        if (c <= 0x5B) return false;
        if (c <= 0x5C) return true;
        if (c <= 0x5D) return false;
        if (c <= 0x5E) return true;
        if (c <= 0x5F) return false;
        if (c <= 0x60) return true;
        if (c <= 0x7E) return false;
        return true;
        
    }
    
    private static String hexEscape(byte c) {
        
        StringBuffer result = new StringBuffer(3);
        result.append('%');
        if (c <= 0x0F) result.append('0');
        result.append(Integer.toHexString(c));
        
        return result.toString();
        
    }

}