// Copyright 2002-2004 Elliotte Rusty Harold
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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
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
 *   <a href="http://www.w3.org/TR/2004/CR-xinclude-20040413">April
 *   13, 2004 2nd Candidate Recommendation of <cite>XML Inclusions
 *   (XInclude) Version 1.0</cite></a>. Fallbacks are supported.
 *   The XPointer <code>element()</code> scheme and shorthand XPointers
 *   are also supported. The XPointer <code>xpointer()</code> scheme
 *   is not supported. The <code>accept</code> and 
 *   <code>accept-language</code> attributes are supported.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0a1
 *
 */
public class XIncluder {
    
    // rewrite this to handle only elements in documents (no parentless
    // elements) and then add code to handle Nodes and parentless elements
    // by sticking each one in a Document????

    // prevent instantiation
    private XIncluder() {}

    /**
     * <p>
     *   The namespace name of all XInclude elements.
     * </p>
     */
    public final static String XINCLUDE_NS 
      = "http://www.w3.org/2001/XInclude";

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
     *     all <code>xinclude:include</code> elements
     *     have been replaced by their referenced content
     * 
     * @throws BadParseAttributeException if an <code>include</code>  
     *     element has a <code>parse</code> attribute with any value 
     *     other than <code>text</code> or <code>parse</code>
     * @throws InclusionLoopException if the document  
     *     contains an XInclude element that attempts to include 
     *     a document in which this element is directly or indirectly 
     *     included.
     * @throws IOException if an included document could not be loaded,
     *     and no fallback was available
     * @throws NoIncludeLocationException if an <code>xinclude:include</code> 
     *      element does not have an <code>href</code> attribute.
     * @throws ParsingException if an included XML document 
     *     was malformed
     * @throws UnsupportedEncodingException if an included document 
     *     used an encoding this parser does not support, and no
     *     fallback was available
     * @throws XIncludeException if the document violates the
     *     syntax rules of XInclude
     * @throws XMLException if resolving an include element would 
     *      result in a malformed document
     */
     public static Document resolve(Document in)  
       throws BadParseAttributeException, InclusionLoopException, 
             IOException, NoIncludeLocationException, ParsingException, 
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
     *     all <code>xinclude:include</code> elements
     *     have been replaced by their referenced content
     * 
     * @throws BadParseAttributeException if an <code>include</code>  
     *     element has a <code>parse</code> attribute with any value 
     *     other than <code>text</code> or <code>parse</code>
     * @throws InclusionLoopException if the document 
     *     contains an XInclude element that attempts to include 
     *     a document in which this element is directly or indirectly 
     *     included.
     * @throws IOException if an included document could not be loaded,
     *     and no fallback was available
     * @throws NoIncludeLocationException if an <code>xinclude:include</code> 
     *      element does not have an href attribute.
     * @throws ParsingException if an included XML document 
     *     was malformed
     * @throws UnsupportedEncodingException if an included document 
     *     used an encoding this parser does not support, and no
     *     fallback was available
     * @throws XIncludeException if the document violates the
     *     syntax rules of XInclude
     * @throws XMLException if resolving an include element would 
     *      result in a malformed document
     */
     public static Document resolve(Document in, Builder builder)  
       throws BadParseAttributeException, InclusionLoopException, 
             IOException, NoIncludeLocationException, ParsingException, 
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
     * The resolved <code>Document</code> contains no
     * <code>xinclude:include</code> elements.
     * </p>
     * 
     * <p>
     * If the inclusion fails for any reason&mdash;XInclude syntax
     * error, missing resource with no fallback, etc.&mdash;the document
     * may be left in a partially resolved state.
     * </p>
     * 
     * @param in the <code>Document</code> in which include elements
     *     should be resolved
     *
     * @throws BadParseAttributeException if an <code>include</code>  
     *     element has a <code>parse</code> attribute
     *     with any value other than <code>text</code> 
     *     or <code>parse</code>
     * @throws InclusionLoopException if the document 
     *     contains an XInclude element that attempts to include a  
     *     document in which this element is directly or indirectly 
     *     included
     * @throws IOException if an included document could not be loaded,
     *     and no fallback was available
     * @throws NoIncludeLocationException if an <code>xinclude:include</code>
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
      throws BadParseAttributeException, InclusionLoopException,  
             IOException, NoIncludeLocationException, ParsingException, 
             UnsupportedEncodingException, XIncludeException {        
        resolveInPlace(in, new Builder());
    }

    /**
     * <p>
     * Modifies a <code>Document</code> by replacing all 
     * <code>xinclude:include</code> elements with their referenced 
     * content. Resolution is recursive; that is, include elements
     * in the included documents are themselves resolved.
     * The resolved <code>Document</code> contains no
     * <code>xinclude:include</code> elements.
     * </p>
     * 
     * <p>
     * If the inclusion fails for any reason--XInclude syntax
     * error, missing resource with no fallback, etc.--the document
     * may be left in a partially resolved state.
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
     * @throws InclusionLoopException if this <code>Element</code> 
     *     contains an XInclude element that attempts to include a  
     *     document in which this element is directly or indirectly 
     *     included
     * @throws IOException if an included document could not be loaded,
     *     and no fallback was available
     * @throws NoIncludeLocationException if an <code>xinclude:include</code>
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
      throws BadParseAttributeException, InclusionLoopException,  
             IOException, NoIncludeLocationException, ParsingException, 
             UnsupportedEncodingException, XIncludeException {       
        resolveInPlace(in, builder, new Stack()); 
    }

    
    private static void resolveInPlace(
      Document in, Builder builder, Stack baseURLs) 
      throws IOException, ParsingException, XIncludeException {
        
        String base = in.getBaseURI();
        // workaround a bug in Sun VMs
        if (base != null && base.startsWith("file:///")) {
            base = "file:/" + base.substring(8);
        }
        
        // this is the wrong place to test this. It's too late
        /*if (baseURLs.indexOf(base) != -1) {
            throw new InclusionLoopException(
              "Tried to include the already included document " + base +
              " from " + baseURLs.peek(), (String) baseURLs.peek());
        } */
        baseURLs.push(base);       
        resolve(in.getRootElement(), builder, baseURLs);
        baseURLs.pop();
        
    }

    
    private static void resolve(
      Element element, Builder builder, Stack baseURLs)
      throws IOException, ParsingException, XIncludeException {
        
        resolve(element, builder, baseURLs, null);
        
    }
    
    
    private static void resolve(
      Element element, Builder builder, Stack baseURLs, Document originalDoc)
      throws IOException, ParsingException, XIncludeException {
        
        if (isIncludeElement(element)) {
            String parse = element.getAttributeValue("parse");
            if (parse == null) parse = "xml";
            String xpointer = element.getAttributeValue("xpointer");
            String encoding = element.getAttributeValue("encoding");
            String href = element.getAttributeValue("href");
            // empty string href is same as no href attribute
            if ("".equals(href)) href = null;
            if (href == null && xpointer == null) {
                throw new NoIncludeLocationException(
                  "Missing href attribute", 
                  element.getDocument().getBaseURI()
                );   
            }
            if (href != null) href = convertToURI(href);
            
            testForForbiddenChildElements(element);

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
                
                String accept = element.getAttributeValue("accept");
                checkHeader(accept);
                String acceptLanguage = element.getAttributeValue("accept-language"); 
                checkHeader(acceptLanguage);
                
                if (parse.equals("xml")) {
                    Nodes replacements;
                    if (url != null) { 
                        replacements = downloadXMLDocument(url, 
                          xpointer, builder, baseURLs, accept, acceptLanguage);
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
                        Document parentDoc = element.getDocument();
                        if (parentDoc == null) {
                            parentDoc = originalDoc;
                        }
                        Nodes originals = XPointer.query(parentDoc, xpointer);
                        replacements = new Nodes(); 
                        for (int i = 0; i < originals.size(); i++) {
                            Node original = originals.get(i);
                            if (original instanceof Element) {
                                if (contains((Element) original, element)) {
                                    throw new InclusionLoopException("Element tried to include itself"); 
                                }  
                            }
                            Node copy = original.copy();
                            if (copy instanceof Element) {
                                Element copyElement = (Element) copy;
                                // XXX can I delete this or not? copyElement.setBaseURI(original.getBaseURI());
                                // the copy actually sets the base URI to the original's actual base URI,
                                // not always the same as getBaseURI
                            } 
                            replacements.append(copy);        
                        }  
                        replacements = resolveXPointerSelection(replacements, builder, baseURLs, parentDoc);  
                                                 
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
                    else {  // root element needs special treatment
                        // I am assuming here that it is not possible 
                        // for parent to be null. I think this is true 
                        // in the current version, but it could change 
                        // if I made it possible to directly resolve an
                        // element or a Nodes.
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
                    Nodes replacements 
                      = downloadTextDocument(url, encoding, builder, accept, acceptLanguage);
                    for (int j = 0; j < replacements.size(); j++) {
                        Node replacement = replacements.get(j);
                        if (replacement instanceof Attribute) {
                            ((Element) parent).addAttribute((Attribute) replacement);
                        }
                        else {
                            parent.insertChild(replacement, parent.indexOf(element));
                        }   
                    }                    
                    parent.removeChild(element);
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
     * Modifies a <code>Nodes</code> object by replacing all 
     * XInclude elements with their referenced content.
     * Resolution is recursive; that is, include elements
     * in the included documents are themselves resolved.
     * Furthermore, include elements that are children or 
     * descendants of elements in this list are also resolved.
     * The <code>Nodes</code> object returned contains no
     * include elements.
     * </p>
     */
    private static void resolveInPlace(Nodes in, Builder builder, Stack baseURLs) 
      throws IOException, ParsingException, XIncludeException { 
        for (int i = 0; i < in.size(); i++) {
            Node child = in.get(i);
            if (child instanceof Element) {
                Element element = (Element) child;
                if (isIncludeElement(element)) {
                    Nodes nodes = resolveSilently(element, builder, baseURLs);
                    in.remove(i);
                    for (int j = 0; j < nodes.size(); j++) {
                        in.insert(nodes.get(j), i++);
                    }
                }
                else {       
                    resolve((Element) child, builder, baseURLs);
                }
            }
            else if (child instanceof Document) {
                resolveInPlace((Document) child, builder,  baseURLs);   
            }
        }
    }
    
    
    // This assumes current implementation of XPointer
    // that always selects exactly zero or one element
    private static Nodes resolveXPointerSelection(Nodes in, 
      Builder builder, Stack baseURLs, Document original) 
      throws IOException, ParsingException, XIncludeException {

        if (in.size() == 0) return new Nodes();
        Element preinclude = (Element) in.get(0);
        return resolveSilently(preinclude, builder, baseURLs, original);
        
    }
    

    private static boolean contains(ParentNode ancestor, Node descendant) {
        
        for (Node parent = descendant; 
             parent != null; 
             parent=parent.getParent()) {
            if (parent == ancestor) return true;  
        }    
        
        return false;   
    }

    
    // resolveSilently seems to be primarily used for resolving
    // elements selected by an XPointer
    private static Nodes resolveSilently(
      Element element, Builder builder, Stack baseURLs) 
      throws IOException, ParsingException, XIncludeException {
        return resolveSilently(element, builder, baseURLs, null);
    }
    
    
    private static Nodes resolveSilently(
      Element element, Builder builder, Stack baseURLs, Document originalDoc)
      throws IOException, ParsingException, XIncludeException {
        
        if (isIncludeElement(element)) {
            String parse = element.getAttributeValue("parse");
            if (parse == null) parse = "xml";
            String xpointer = element.getAttributeValue("xpointer");
            String encoding = element.getAttributeValue("encoding");
            String href = element.getAttributeValue("href");
            String accept = element.getAttributeValue("accept");
            String acceptLanguage = element.getAttributeValue("accept-language"); 
            
            if ("".equals(href)) href = null;
            if (href == null && xpointer == null) {
                throw new NoIncludeLocationException(
                  "Missing href attribute", 
                  element.getDocument().getBaseURI()
                );   
            }
            if (href != null) href = convertToURI(href);
            
            testForForbiddenChildElements(element);

            String base = element.getBaseURI();
            URL baseURL = null;
            try {
                baseURL = new URL(base);     
            }
            catch (Exception ex) {
               // don't use base   
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
                        replacements = downloadXMLDocument(url, 
                          xpointer, builder, baseURLs, accept, acceptLanguage);
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
                        Document parentDoc = element.getDocument();
                        if (parentDoc == null) {
                            parentDoc = originalDoc;
                        }
                        Nodes originals = XPointer.query(parentDoc, xpointer);
                        replacements = new Nodes(); 
                        for (int i = 0; i < originals.size(); i++) {
                            Node original = originals.get(i);
                            if (original instanceof Element) {
                                if (contains((Element) original, element)) {
                                    throw new InclusionLoopException("Element tried to include itself"); 
                                }  
                            }
                            replacements.append(original.copy());        
                        }  
                        replacements = resolveXPointerSelection(replacements, builder, baseURLs, parentDoc);                           
                    }
                    return replacements; 
                }  // end parse="xml"
                else if (parse.equals("text")) {                   
                    return downloadTextDocument(url, encoding, builder, accept, acceptLanguage);
                }
                else {
                   throw new BadParseAttributeException(
                     "Bad value for parse attribute: " + parse, 
                     element.getDocument().getBaseURI());   
                }
            
            }
            catch (IOException ex) {
                return processFallbackSilently(element, builder, baseURLs, ex);
            }
            catch (XPointerSyntaxException ex) {
                return processFallbackSilently(element, builder, baseURLs, ex);
            }
            catch (XPointerResourceException ex) {
                // Process fallbacks;  I'm not sure this is correct 
                // behavior. Possibly this should include nothing. See
                // http://lists.w3.org/Archives/Public/www-xml-xinclude-comments/2003Aug/0000.html
                // Daniel Veillard thinks this is correct. See
                // http://lists.w3.org/Archives/Public/www-xml-xinclude-comments/2003Aug/0001.html
                return processFallbackSilently(element, builder, baseURLs, ex);
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
                resolve(children.get(i), builder, baseURLs, originalDoc);   
            } 
            return new Nodes(element);
        }
        
    }

    
    private static void testForForbiddenChildElements(Element element) 
      throws XIncludeException {
        Elements fallbacks 
          = element.getChildElements("fallback", XINCLUDE_NS);
        if (fallbacks.size() > 1) {
            throw new XIncludeException("Multiple fallback elements", 
              element.getDocument().getBaseURI());   
        }
        
        // while we're at it let's test to see if there are any
        // other children from the XInclude namespace
        Elements children = element.getChildElements();
        for (int i = 0; i < children.size(); i++) {
            Element child = children.get(i);
            if (XINCLUDE_NS.equals(child.getNamespaceURI())) {
                if (!("fallback".equals(child.getLocalName()))) {
                    throw new XIncludeException(
                      "Include element contains an include child",
                      element.getDocument().getBaseURI());     
                }
            }
        }
        
    }

    
    private static void processFallback(Element includeElement, 
      Builder builder, Stack baseURLs, ParentNode parent, Exception ex)
        throws XIncludeException, IOException, ParsingException {
           Element fallback 
              = includeElement.getFirstChildElement("fallback", XINCLUDE_NS);
           if (fallback == null) {
                if (ex instanceof IOException) throw (IOException) ex;
                XIncludeException ex2 = new XIncludeException(
                  ex.getMessage(), includeElement.getDocument().getBaseURI());
                ex2.initCause(ex);
                throw ex2;
           }
             
           while (fallback.getChildCount() > 0) {
                Node child = fallback.getChild(0);
                if (child instanceof Element) {
                    resolve((Element) child, builder, baseURLs);
                }
                child = fallback.getChild(0);
                child.detach();
                parent.insertChild(child, parent.indexOf(includeElement)); 
           }
           includeElement.detach();
    }

    
    private static Nodes processFallbackSilently(
      Element includeElement, Builder builder, Stack baseURLs, Exception ex)
        throws XIncludeException, IOException, ParsingException {
           Element fallback 
              = includeElement.getFirstChildElement("fallback", XINCLUDE_NS);
           if (fallback == null) {
                if (ex instanceof IOException) throw (IOException) ex;
                XIncludeException ex2 = new XIncludeException(
                  ex.getMessage(), includeElement.getDocument().getBaseURI());
                ex2.initCause(ex);
                throw ex2;
           }

           Nodes result = new Nodes();
           for (int i = 0; i < fallback.getChildCount(); i++) {
                Node child = fallback.getChild(i);
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
      URL source, String xpointer, Builder builder, Stack baseURLs,
      String accept, String language) 
      throws IOException, ParsingException, XIncludeException, 
             XPointerSyntaxException, XPointerResourceException {

        String base = source.toExternalForm();
        if (xpointer == null && baseURLs.indexOf(base) != -1) {
            throw new InclusionLoopException(
              "Tried to include the already included document " + base +
              " from " + baseURLs.peek(), (String) baseURLs.peek());
        }      
        
        URLConnection uc = source.openConnection();
        setHeaders(uc, accept, language);
        Document doc = builder.build(
          uc.getInputStream(), source.toExternalForm()); 
          
        Nodes included;
        if (xpointer != null && xpointer.length() != 0) {
            included = XPointer.query(doc, xpointer); 
            resolveInPlace(included, builder, baseURLs); 
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
    * @param builder the <code>Builder</code> used to build the
    *     nodes included from other documents
    * 
    * @return the document retrieved from the source <code>URL</code>
    * 
    * @throws IOException if the remote document cannot
    *     be read due to an I/O error
    */    
    private static Nodes downloadTextDocument(
      URL source, String encoding, Builder builder,
      String accept, String language) 
      throws IOException, XIncludeException {
         
        if (encoding == null || encoding.length() == 0) {
            encoding = "UTF-8"; 
        }

        URLConnection uc = source.openConnection();
        setHeaders(uc, accept, language);
        
        String encodingFromHeader = uc.getContentEncoding();
        String contentType = uc.getContentType();
        int contentLength = uc.getContentLength();
        if (contentLength < 0) contentLength = 1024;
        InputStream in = new BufferedInputStream(uc.getInputStream());
        try {
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
            Reader reader = new BufferedReader(
              new InputStreamReader(in, encoding)
            );
            int c;
            StringBuffer sb = new StringBuffer(contentLength);
            while ((c = reader.read()) != -1) {
              sb.append((char) c);
            }
            
            NodeFactory factory = builder.getNodeFactory();
            if (factory != null) {
                return factory.makeText(sb.toString());
            }
            else return new Nodes(new Text(sb.toString()));
        }
        finally {
            in.close();   
        }
      
    }
    
    
    private static void setHeaders(URLConnection uc, String accept, 
      String language) throws BadHTTPHeaderException {
      
        if (accept != null) {
            checkHeader(accept);
            uc.setRequestProperty("accept", accept);
        }
        if (language != null) {
            checkHeader(language);
            uc.setRequestProperty("accept-language", language);
        }
        
    }
    
    
    private static void checkHeader(String header) 
      throws BadHTTPHeaderException {
     
        if (header == null) return;
        int length = header.length();
        for (int i = 0; i < length; i++) {
            char c = header.charAt(i);
            if (c < 0x20 || c > 0x7E) {
                throw new BadHTTPHeaderException(
                  "Header contains illegal character 0x" 
                  + Integer.toHexString(c).toUpperCase());
            }
        }
        
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
            byte[] utf8Data = iri.getBytes("UTF8");
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
    
    
    // XXX could switch to lookup table
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