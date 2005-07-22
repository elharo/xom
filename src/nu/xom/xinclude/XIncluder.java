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
import java.util.Locale;
import java.util.ArrayList;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.DocType;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.MalformedURIException;
import nu.xom.Node;
import nu.xom.NodeFactory;
import nu.xom.Nodes;
import nu.xom.ParentNode;
import nu.xom.ParsingException;
import nu.xom.Text;

/**
 * <p>
 * Implements XInclude resolution as specified in 
 * <a href="http://www.w3.org/TR/2004/REC-xinclude-20041220/"
 * target="_top"><cite>XML Inclusions (XInclude) Version 
 * 1.0</cite></a>. Fallbacks are supported. 
 * The XPointer <code>element()</code> scheme and 
 * shorthand XPointers are also supported. The XPointer 
 * <code>xpointer()</code> scheme is not supported. 
 * The <code>accept</code> and <code>accept-language</code> 
 * attributes are supported.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.1b3
 *
 */
public class XIncluder {
    
    private static String version = System.getProperty("java.version");   

    // could rewrite this to handle only elements in documents 
    // (no parentless elements) and then add code to handle Nodes 
    // and parentless elements by sticking each one in a Document

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
     * Returns a copy of the document in which all 
     * <code>xinclude:include</code> elements have been  
     * replaced by their referenced content. The original 
     * <code>Document</code> object is not modified.
     * Resolution is recursive; that is, include elements
     * in the included documents are themselves resolved.
     * The <code>Document</code> returned contains no
     * include elements.
     * </p>
     * 
     * @param in the document in which include elements
     *     should be resolved
     * 
     * @return copy of the document in which
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
     *      element does not have an <code>href</code> attribute
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
         
        Builder builder = new Builder();
        return resolve(in, builder);
        
    }

    /**
     * <p>
     * Returns a copy of the document in which all 
     * <code>xinclude:include</code> elements have been  
     * replaced by their referenced content as loaded by the builder.
     * The original <code>Document</code> object is not modified.
     * Resolution is recursive; that is, include elements
     * in the included documents are themselves resolved.
     * The document returned contains no <code>include</code> elements.
     * </p>
     * 
     * @param in the document in which include elements
     *     should be resolved
     * @param builder the builder used to build the
     *     nodes included from other documents
     * 
     * @return copy of the document in which
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
     * Modifies a document by replacing all  
     * <code>xinclude:include</code> elements 
     * by their referenced content. 
     * Resolution is recursive; that is, include elements
     * in the included documents are themselves resolved.
     * The resolved document contains no
     * <code>xinclude:include</code> elements.
     * </p>
     * 
     * <p>
     * If the inclusion fails for any reason&mdash;XInclude syntax
     * error, missing resource with no fallback, etc.&mdash;the document
     * may be left in a partially resolved state.
     * </p>
     * 
     * @param in the document in which include elements
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
     *     element does not have an <code>href</code> attribute
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
     * Modifies a document by replacing all 
     * <code>xinclude:include</code> elements with their referenced 
     * content as loaded by the builder. Resolution is recursive; 
     * that is, <code>include</code> elements in the included documents 
     * are themselves resolved. The resolved document contains no 
     * <code>xinclude:include</code> elements.
     * </p>
     * 
     * <p>
     * If the inclusion fails for any reason &mdash; XInclude syntax
     * error, missing resource with no fallback, etc. &mdash; the 
     * document may be left in a partially resolved state.
     * </p>
     * 
     * @param in the document in which include elements
     *     should be resolved
     * @param builder the builder used to build the
     *     nodes included from other documents
     * 
     * @throws BadParseAttributeException if an <code>include</code>  
     *     element has a <code>parse</code> attribute
     *     with any value other than <code>text</code> 
     *     or <code>parse</code>
     * @throws InclusionLoopException if this element 
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
        
        ArrayList stack = new ArrayList();
        resolveInPlace(in, builder, stack);
        
    }

    
    private static void resolveInPlace(
      Document in, Builder builder, ArrayList baseURLs) 
      throws IOException, ParsingException, XIncludeException {
        
        String base = in.getBaseURI();
        // workaround a bug in Sun VMs
        if (base != null && base.startsWith("file:///")) {
            base = "file:/" + base.substring(8);
        }
        
        baseURLs.add(base);   
        Element root = in.getRootElement();
        resolve(root, builder, baseURLs);
        baseURLs.remove(baseURLs.size()-1);
        
    }

    
    private static void resolve(
      Element element, Builder builder, ArrayList baseURLs)
      throws IOException, ParsingException, XIncludeException {
        
        resolve(element, builder, baseURLs, null);
        
    }
    
    
    private static void resolve(
      Element element, Builder builder, ArrayList baseURLs, Document originalDoc)
      throws IOException, ParsingException, XIncludeException {
        
        if (isIncludeElement(element)) {
            verifyIncludeElement(element);
            
            String parse = element.getAttributeValue("parse");
            if (parse == null) parse = "xml";
            String xpointer = element.getAttributeValue("xpointer");
            String encoding = element.getAttributeValue("encoding");
            String href = element.getAttributeValue("href");
            // empty string href is same as no href attribute
            if ("".equals(href)) href = null;
            
            ParentNode parent = element.getParent();
            String base = element.getBaseURI();
            URL baseURL = null;
            try {
                baseURL = new URL(base);     
            }
            catch (MalformedURLException ex) {
               // don't use base   
            }
            URL url = null;
            try {
                // xml:base attributes added to maintain the 
                // base URI should not have fragment IDs

                if (baseURL != null && href != null) {
                    url = absolutize(baseURL, href);
                }
                else if (href != null) {
                    try {
                        testURISyntax(href);
                        url = new URL(href); 
                    }
                    catch (MalformedURIException ex) {
                        if (baseURL == null) {
                            throw new BadHrefAttributeException(
                              "Could not resolve relative URI " + href
                              + " because the xi:include element does" 
                              + " not have a base URI.", href);    
                        }
                        throw new BadHrefAttributeException("Illegal IRI in href attribute", href);
                    }
                }
                
                String accept = element.getAttributeValue("accept");
                checkHeader(accept);
                String acceptLanguage = element.getAttributeValue("accept-language"); 
                checkHeader(acceptLanguage);
                
                if (parse.equals("xml")) {
                    
                    String parentLanguage = "";
                    if (parent instanceof Element) {
                        parentLanguage = getXMLLangValue((Element) parent);
                    }
                    
                    Nodes replacements;
                    if (url != null) { 
                        replacements = downloadXMLDocument(url, 
                          xpointer, builder, baseURLs, accept, acceptLanguage, parentLanguage);
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
                                String noFragment = child.getBaseURI();
                                if (noFragment.indexOf('#') >= 0) {
                                    noFragment = noFragment.substring(
                                      0, noFragment.indexOf('#'));
                                }
                                Element baseless = (Element) child;
                                
                                // parent is null here; need to get real parent
                                String parentBase = parent.getBaseURI();
                                if (parentBase != null && ! "".equals(parentBase)) {
                                    parentBase = getDirectoryBase(parentBase);
                                }
                                
                                if (noFragment.startsWith(parentBase)) {
                                    noFragment = noFragment.substring(parentBase.length());
                                }
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
                            // current implementation of XPointer never returns non-elements
                            if (contains((Element) original, element)) {
                                throw new InclusionLoopException(
                                  "Element tried to include itself"
                                ); 
                            }  
                            Node copy = original.copy();
                            replacements.append(copy);        
                        }  
                        replacements = resolveXPointerSelection(
                          replacements, builder, baseURLs, parentDoc);  
                                                 
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
    
    
    // ???? Move this into URIUtil when it goes public
    private static String getDirectoryBase(String parentBase) {
        if (parentBase.endsWith("/")) return parentBase;
        int lastSlash = parentBase.lastIndexOf('/');
        return parentBase.substring(0, lastSlash+1);
    }
    
    
    
    private static void verifyIncludeElement(Element element) 
      throws XIncludeException {

        testHref(element);
        testForFragmentIdentifier(element);
        verifyEncoding(element);
        testForForbiddenChildElements(element);
    }

    
    private static void testHref(Element include) throws NoIncludeLocationException {

        String href = include.getAttributeValue("href");
        String xpointer = include.getAttributeValue("xpointer");
        if (href == null && xpointer == null) {
            throw new NoIncludeLocationException(
              "Missing href attribute", 
              include.getDocument().getBaseURI()
            );   
        }
    }

    
    private static void testForFragmentIdentifier(Element include) 
      throws BadHrefAttributeException {

        String href = include.getAttributeValue("href");
        if (href != null) {
            if (href.indexOf('#') > -1) {
                throw new BadHrefAttributeException(
                  "fragment identifier in URI " + href, include.getBaseURI()
                );
            }
        }
        
    }

    
    private static void verifyEncoding(Element include) 
      throws BadEncodingAttributeException {

        String encoding = include.getAttributeValue("encoding");
        if (encoding == null) return;
        // production 81 of XML spec
        // EncName :=[A-Za-z] ([A-Za-z0-9._] | '-')*
        char[] text = encoding.toCharArray();
        if (text.length == 0) {
            throw new BadEncodingAttributeException(
              "Empty encoding attribute", include.getBaseURI());
        }
        char c = text[0];
        if (!((c >= 'A' &&  c <= 'Z') || (c >= 'a' &&  c <= 'z'))) {
            throw new BadEncodingAttributeException(
              "Illegal value for encoding attribute: " + encoding, include.getBaseURI()
            );
        }
        for (int i = 1; i < text.length; i++) {
            c = text[i];
            if ((c >= 'A' &&  c <= 'Z') || (c >= 'a' &&  c <= 'z')
              || (c >= '0' &&  c <= '9') || c == '-' || c == '_' || c == '.') {
                continue;
            }
            throw new BadEncodingAttributeException(
              "Illegal value for encoding attribute: " + encoding, include.getBaseURI()
            );
        }
        
    }

    
    // hack because URIUtil isn't public
    private static URL absolutize(URL baseURL, String href) 
      throws MalformedURLException, BadHrefAttributeException {
        
        Element parent = new Element("c");
        parent.setBaseURI(baseURL.toExternalForm());
        Element child = new Element("c");
        parent.appendChild(child);
        child.addAttribute(new Attribute(
          "xml:base", "http://www.w3.org/XML/1998/namespace", href));
        URL result = new URL(child.getBaseURI());
        if (!"".equals(href) && result.equals(baseURL)) {
            if (! baseURL.toExternalForm().endsWith(href)) {
                throw new BadHrefAttributeException(href 
                  + " is not a syntactically correct IRI");
            }
        }
        return result;
        
    }

    
    private static void testURISyntax(String href) {       
        Element e = new Element("e");
        e.setNamespaceURI(href);
    }

    
    private static String getXMLLangValue(Element element) {
        
        while (true) {
           Attribute lang = element.getAttribute(
             "lang", "http://www.w3.org/XML/1998/namespace");
           if (lang != null) return lang.getValue();
           ParentNode parent = element.getParent();
           if (parent == null) return "";
           else if (parent instanceof Document) return "";
           else element = (Element) parent;
        }
        
    }
    
    
    // This assumes current implementation of XPointer that
    // always selects exactly one element or throws an exception.
    private static Nodes resolveXPointerSelection(Nodes in, 
      Builder builder, ArrayList baseURLs, Document original) 
      throws IOException, ParsingException, XIncludeException {

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

    
    private static Nodes resolveSilently(
      Element element, Builder builder, ArrayList baseURLs, Document originalDoc)
      throws IOException, ParsingException, XIncludeException {
        
        // There is no possibility the element passed to this method 
        // is an include or a fallback element 
        if (isIncludeElement(element) || isFallbackElement(element) ) {
            throw new RuntimeException(
              "XOM BUG: include or fallback element passed to resolveSilently;"
              + " please report with a test case");
        }
        
        Elements children = element.getChildElements();
        for (int i = 0; i < children.size(); i++) {
            resolve(children.get(i), builder, baseURLs, originalDoc);   
        } 
        return new Nodes(element);
        
    }

    
    private static void testForForbiddenChildElements(Element element) 
      throws XIncludeException {
        
        int fallbacks = 0;
        Elements children = element.getChildElements();
        int size = children.size();
        for (int i = 0; i < size; i++) {
            Element child = children.get(i);
            if (XINCLUDE_NS.equals(child.getNamespaceURI())) {
                if ("fallback".equals(child.getLocalName())) {
                    fallbacks++;
                    if (fallbacks > 1) {
                        throw new XIncludeException("Multiple fallback elements", 
                          element.getDocument().getBaseURI()); 
                    }
                }
                else {
                    throw new XIncludeException(
                      "Include element contains an include child",
                      element.getDocument().getBaseURI());     
                }
            }
        }
        
    }

    
    private static void processFallback(Element includeElement, 
      Builder builder, ArrayList baseURLs, ParentNode parent, Exception ex)
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

    
    // I could probably move the xpointer out of this method
    private static Nodes downloadXMLDocument(
      URL source, String xpointer, Builder builder, ArrayList baseURLs,
      String accept, String acceptLanguage, String parentLanguage) 
      throws IOException, ParsingException, XIncludeException, 
        XPointerSyntaxException, XPointerResourceException {

        String base = source.toExternalForm();
        if (xpointer == null && baseURLs.indexOf(base) != -1) {
            throw new InclusionLoopException(
              "Tried to include the already included document " + base +
              " from " + baseURLs.get(baseURLs.size()-1), (String) baseURLs.get(baseURLs.size()-1));
        }      
        
        URLConnection uc = source.openConnection();
        setHeaders(uc, accept, acceptLanguage);
        InputStream in = new BufferedInputStream(uc.getInputStream());
        Document doc;
        try {
            doc = builder.build(in, source.toExternalForm());
        }
        finally {
            in.close();
        }
          
        resolveInPlace(doc, builder, baseURLs); 
        Nodes included;
        if (xpointer != null && xpointer.length() != 0) {
            included = XPointer.query(doc, xpointer); 
            // fill in lang attributes here
            for (int i = 0; i < included.size(); i++) {
                Node node = included.get(i);
                // Current implementation can only select elements
                Element top = (Element) node;
                Attribute lang = top.getAttribute("lang", 
                  "http://www.w3.org/XML/1998/namespace");
                if (lang == null) {
                    String childLanguage = getXMLLangValue(top);
                    if (!parentLanguage.equals(childLanguage)) {
                        top.addAttribute(new Attribute("xml:lang", 
                          "http://www.w3.org/XML/1998/namespace", 
                          childLanguage));
                    }
                }
            }
        }
        else {
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
            Node node = included.get(i);
            // Take account of xml:base attribute, which we normally 
            // don't do when detaching
            String noFragment = node.getBaseURI();
            if (noFragment.indexOf('#') >= 0) {
                noFragment = noFragment.substring(0, noFragment.indexOf('#'));
            }
            node.detach();
            if (node instanceof Element) {
                ((Element) node).setBaseURI(noFragment);
            }
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
                    contentType = contentType.toLowerCase(Locale.ENGLISH);
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
            if (version.startsWith("1.2")  || version.startsWith("1.1")) {
                if (encoding.equalsIgnoreCase("UTF-16")) {
                    // is it  big-endian or little-endian?
                    in.mark(2);
                    int first = in.read();
                    if (first == 0xFF) encoding = "UnicodeLittle";
                    else encoding="UnicodeBig";
                    in.reset();  
                }
                else if (encoding.equalsIgnoreCase("UnicodeBigUnmarked")) {
                    encoding = "UnicodeBig";
                }
                else if (encoding.equalsIgnoreCase("UnicodeLittleUnmarked")) {
                    encoding = "UnicodeLittle";
                }
            }
            Reader reader = new BufferedReader(
              new InputStreamReader(in, encoding)
            );
            StringBuffer sb = new StringBuffer(contentLength);
            for (int c = reader.read(); c != -1; c = reader.read()) {
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


}