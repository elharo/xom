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

package nu.xom;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * <p>
 * The <code>Builder</code> class is responsible  
 * for creating XOM <code>Document</code> objects 
 * from a URL, file, string, or input stream by reading   
 * an XML document. A SAX parser is used to read the   
 * document and report any well-formedness errors.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0d22
 * 
 */
public class Builder {

    private XMLReader   parser;
    private NodeFactory factory;
    private boolean     validate = false;
    
    /**
     * <p>
     * This constructor uses the default factory and
     * chooses any available SAX2 parser in the
     * following order:
     * </p>
     * 
     * <ol>
     * <li>Xerces 2.x (a.k.a. IBM XML parser for Java)</li>
     * <li>Crimson</li>
     * <li>Piccolo</li>
     * <li>GNU &AElig;lfred</li>
     * <li>Oracle</li>
     * <li>XP</li>
     * <li>Saxon's &AElig;lfred</li>
     * <li>dom4j's &AElig;lfred</li>
     * <li>The platform default specified by the 
     *     <code>org.xml.sax.driver</code> system property</li>
     * </ol>
     * 
     * <p>
     * Parsers must implicitly or explicitly support the 
     * http://xml.org/sax/features/external-general-entities
     * and
     * http://xml.org/sax/features/external-parameter-entities
     * features XOM requires. Parsers that don't are rejected 
     * automatically. 
     * </p>
     * 
     * @throws XMLException if no satisfactory parser is 
     *     installed in the local class path
     */
    public Builder() {       
        this(false);     
    }
    
    /**
     * <p>
     * This constructor creates a <code>Builder</code> 
     * based on a fully validating
     * parser. If the <code>validate</code> argument 
     * is true, then a validity error while
     * parsing will cause a fatal error; that is,
     * it will throw a <code>ValidityException</code>.
     * </p>
     * 
     * @param validate true if the parser should 
     *     validate the document while parsing
     *
     * @throws XMLException if no satisfactory parser 
     *     is installed in the local class path
     */
    public Builder(boolean validate) {     
         this(findParser(validate), validate, null); 
    }

    /**
     * <p>
     * This constructor creates a <code>Builder</code> based on a fully
     * validating parser that builds node objects with the supplied 
     * factory. If the <code>validate</code> argument is true, then 
     * a validity error while parsing will cause a fatal error; that 
     * is, it will throw a <code>ValidityException</code>.
     * </p>
     * 
     * @param validate true if the parser should 
     *     validate the document while parsing
     * @param factory the <code>NodeFactory</code> that creates 
     *     the node objects for this <code>Builder</code>
     *
     * @throws XMLException if no satisfactory parser 
     *     is installed in the local class path
     */
    public Builder(boolean validate, NodeFactory factory) {     
         this(findParser(validate), validate, factory); 
    }

    // These are stored in the order of preference.
    private static String[] parsers = {
        "org.apache.xerces.parsers.SAXParser",
        "gnu.xml.aelfred2.XmlReader",
        "org.apache.crimson.parser.XMLReaderImpl",
        "com.bluecast.xml.Piccolo",
        "oracle.xml.parser.v2.SAXParser",
        "com.jclark.xml.sax.SAX2Driver",
        "com.icl.saxon.aelfred.SAXDriver",
        "org.dom4j.io.aelfred.SAXDriver"
    };

    private static XMLReader findParser(boolean validate) {
        
        // XMLReaderFactory.createXMLReader never returns
        // null. If it can't locate the parser, it throws
        // a SAXException.
        XMLReader parser = null;
        for (int i = 0; i < parsers.length; i++) {
            try { 
                parser = XMLReaderFactory.createXMLReader(parsers[i]);
                setupParser(parser, validate);
                return parser;
            }
            catch (SAXException ex) {
                parser = null;
                // try the next one 
            }       
        }
        
        if (parser == null) {
            try { // default
                parser = XMLReaderFactory.createXMLReader();
                setupParser(parser, validate);
            }
            catch (SAXException ex) {
                throw new XMLException(
                  "Could not find a suitable SAX2 parser", ex);
            }
        }
        
        return parser;
        
    }    
    
    private static void setupParser(XMLReader parser, boolean validate)
      throws SAXNotRecognizedException, SAXNotSupportedException {
        
        if (!validate) {
            parser.setFeature(
              "http://xml.org/sax/features/namespace-prefixes", true);
            if (parser.getClass().getName().equals(  // Crimson workaround
              "org.apache.crimson.parser.XMLReaderImpl")) {
                parser.setErrorHandler(
                  new NamespaceWellformednessRequired()
                );
            }
            else {
                parser.setFeature(
                  "http://xml.org/sax/features/external-general-entities",
                  true
                );
                parser.setFeature(
                 "http://xml.org/sax/features/external-parameter-entities",
                  true
                );
            }
        }
        else {  
            parser.setFeature(
              "http://xml.org/sax/features/namespace-prefixes", true);
            parser.setFeature(
              "http://xml.org/sax/features/validation", true);
            parser.setErrorHandler(new ValidityRequired());
        }                      
        
        try {
            parser.setFeature(
              "http://xml.org/sax/features/string-interning", true);
        }
        catch (SAXException ex) {
            // This parser does not support string interning.
            // We can live without that.
        }
        
        // A couple of Xerces specific properties
        if (parser.getClass().getName().equals(  
            "org.apache.xerces.parsers.SAXParser")) {
            try {
                parser.setFeature(
                 "http://apache.org/xml/features/allow-java-encodings", true);
            }
            catch (SAXException ex) {
                // Possibly an earlier version of Xerces; no big deal.
                // We can live without this feature.   
            }
            // See http://nagoya.apache.org/bugzilla/show_bug.cgi?id=23768
            // If this bug gets fixed, we could uncomment this
            /*
            try {
                parser.setFeature(
                 "http://apache.org/xml/features/standard-uri-conformant", true);
            }
            catch (SAXException ex) {
                // Possibly an earlier version of Xerces; no big deal.
                // We can live without these.   
            } 
            */
        }
        
    }        
    
    // This is one of the few places where the 
    // SAXness is exposed. What if the object is changed after
    // being passed to this method? Wrong features set, etc.????
    // Could/should I eliminate this? Perhaps after adding
    // get/setFeature/property? Would it then be necessary to
    // add a variation in SAXConverter to handle special, non-XML 
    // reader like my SQLReader?
    
    /**
     * <p>
     * Creates a new <code>Builder</code> based 
     * on the specified SAX parser <code>XMLReader</code>.
     * Custom SAX features and properties such as  
     * schema validation can be set on this <code>XMLReader</code> 
     * before passing it to this method.
     * </p>
     * 
     * @param parser the SAX2 <code>XMLReader</code> that  
     *     parses the document
     * 
     * @throws XMLException if parser does not support the  
     *     features XOM requires
     */ 
    public Builder(XMLReader parser) {
        this(parser, false);
    }
    
    /**
     * <p>
     * Creates a new <code>Builder</code> that uses 
     * the specified <code>NodeFactory</code> to create
     * node objects.
     * </p>
     * 
     * @param factory the <code>NodeFactory</code> that creates 
     *     the node objects for this <code>Builder</code>
     * 
     * @throws XMLException if parser does not support the  
     *     features XOM requires
     */ 
    public Builder(NodeFactory factory) {
        this(findParser(false), false, factory);
    }
    

    /**
     * <p>
     * Creates a new <code>Builder</code> based 
     * on the specified parser object.
     * Custom SAX features and properties such 
     * as schema validation can be set on this
     * <code>XMLReader</code> before passing it 
     * to this method.
     * </p>
     * 
     * <p>
     * If the validate argument is true, then a validity error
     *  while parsing will cause a fatal error; that is, it 
     * will throw a <code>ParsingException</code>
     * </p>
     * 
     * @param parser the SAX2 <code>XMLReader</code> that parses
     *               the document
     * 
     * @param validate true if the parser should validate 
     *   the document while parsing
     * 
     */ 
    public Builder(XMLReader parser, boolean validate) {
        this(parser, validate, null);
    }
    
    /**
     * <p>
     * This constructor creates a <code>Builder</code> based 
     * on the specified parser object.
     * Custom SAX features and properties such   
     * as schema validation can be set on this
     * <code>XMLReader</code> before passing 
     * it to this method.
     * </p>
     * 
     * <p>
     * If the validate argument is true, then a validity error
     *  while parsing will cause a fatal error; that is, 
     * it will throw a <code>ParsingException</code>
     * </p>
     * 
     * @param parser The SAX2 <code>XMLReader</code> that parses 
     *     the document
     * @param validate true if the parser should validate the 
     *     document while parsing
     * @param factory the <code>NodeFactory</code> 
     *     this builder uses to create objects in the tree
     * 
     * @throws XMLException if parser does not support 
     *   the features XOM requires
     * 
     */ 
    public Builder(
      XMLReader parser, boolean validate, NodeFactory factory) {
               
        this.validate = validate;     
        try { 
            setupParser(parser, validate);
        }
        catch (SAXException ex) {
            if (validate) {
                throw new XMLException(parser.getClass().getName() 
                  + " does not support validation.", ex);
            }
            else {
                throw new XMLException(parser.getClass().getName()
                  + " does not support the entity resolution"
                  + " features XOM requires.", ex);
            }
        }
        
        // setup the handlers
        this.parser = parser;
        if (factory != null) {
            setHandlers(factory);             
        }
        else if (!(parser instanceof XMLFilter)) {
            setHandlers(new NonVerifyingFactory()); 
        }
        else {
            setHandlers(new NodeFactory());              
        }  

    }
    
    
    private void setHandlers(NodeFactory factory) {
        
        this.factory = factory;
        XOMHandler handler = new XOMHandler(factory);
        parser.setContentHandler(handler);
        parser.setDTDHandler(handler);
        
        try {
            parser.setProperty(
              "http://xml.org/sax/properties/lexical-handler", 
              handler);
        }
        catch (SAXException ex) {
            // This parser does not support lexical events.
            // We can live without them, though it does mean
            // there won't be any comments or a doctype declaration 
            // in the tree.
        }
                
        try {
            parser.setProperty(
              "http://xml.org/sax/properties/declaration-handler", 
              handler);
        }
        catch (SAXException ex) {
            // This parser does not support declaration events.
            // We can live without them, though it does mean
            // there won't be any internal DTD subset.
        }
        
    }
    
    /**
     * <p>
     * This method parses the document at the specified URL.
     * </p>
     * 
     * <p>
     * Note that relative URLs generally do not work here, as
     * there's no base to resolve them against. This includes 
     * relative URLs that point into the file system, though this 
     * is somewhat platform dependent. Furthermore, <code>file</code> 
     * URLs often only work when they adhere exactly to RFC 2396 
     * syntax. URLs that work in Internet Explorer often fail when 
     * used in Java. If you're reading XML from a file, more reliable 
     * results are obtained by using the <code>build</code> method 
     * that takes a <code>java.io.File</code> object as an argument.
     * </p>
     * 
     * @param systemID the URL (generally absolute) 
     *     from which the document is read.
     *     The URL's scheme must be one supported by the Java VM. 
     * 
     * @return the parsed <code>Document</code>
     * 
     * @throws ValidityException if a validity error is detected. This 
     *     is only thrown if the builder has been instructed to validate.
     * @throws ParsingException if a well-formedness error is detected
     * @throws IOException if an I/O error such as a broken socket  
     *     prevents the document from being fully read.
     */
    public Document build(String systemID) 
      throws ParsingException, ValidityException, IOException {

        systemID = canonicalizeURL(systemID);
        InputSource source = new InputSource(systemID);
        return build(source);
        
    }

    /**
     * <p>
     * This method reads the document from an input stream.
     * </p>
     * 
     * @param in the <code>InputStream</code> from which the 
     *     document is read.
     * 
     * @return the parsed <code>Document</code>
     * 
     * @throws ValidityException if a validity error is detected; 
     *     only thrown if the builder has been instructed to validate.
     * @throws ParsingException  if a well-formedness error is detected
     * @throws IOException  if an I/O error such as a broken 
     *     socket prevents the document from being fully read.
     */
    public Document build(InputStream in) 
      throws ParsingException, ValidityException, IOException {

        InputSource source = new InputSource(in);
        return build(source);
        
    }


    /**
     * <p>
     * This method reads the document from an input stream while
     * allowing a base URI to be specified.
     * </p>
     * 
     * @param in the <code>InputStream</code> from which the document
     *    is read.
     * @param baseURI the base URI for this document.
     * 
     * @return the parsed <code>Document</code>
     * 
     * @throws ValidityException if a validity error is detected; 
     *     only thrown if the builder has been instructed to validate.
     * @throws ParsingException  if a well-formedness error is detected
     * @throws IOException  if an I/O error such as a broken
     *       socket prevents the document from being fully read.
     */
    public Document build(InputStream in, String baseURI) 
      throws ParsingException, ValidityException, IOException {

        baseURI = canonicalizeURL(baseURI);
        InputSource source = new InputSource(in);
        source.setSystemId(baseURI);
        return build(source);
        
    }

    // Nasty hack to make sure we get the right form
    // of file URLs on Windows
    private static String fileURLPrefix = "file://";
    private static boolean isWindows = false;
    
    static {
        String os = System.getProperty("os.name", "Unix");
        if (os.indexOf("Windows") >= 0) {
            fileURLPrefix = "file:/";
            isWindows = true;
        }
    }

    /**
     * <p>
     * This method reads the document from a file.
     * The base URI of the document is set to the 
     * location of the file. 
     * </p>
     * 
     * @param in the <code>File</code> from which the document is read.
     * 
     * @return the parsed <code>Document</code>
     * 
     * @throws ParsingException    if a well-formedness error is detected
     * @throws IOException       if an I/O error such as a bad disk 
     *     prevents the file from being read
     * @throws ValidityException if a validity error is detected. This 
     *   is only thrown if the builder has been instructed to validate.
     */
    public Document build(File in) 
      throws ParsingException, ValidityException, IOException {

        InputStream fin = new FileInputStream(in);
        // Java's toURL method doesn't properly escape file
        // names so we have to do it manually
        String absolute = in.getAbsolutePath();
        StringBuffer url = new StringBuffer(fileURLPrefix);
        for (int i = 0; i < absolute.length(); i++) {
            char c = absolute.charAt(i);
            if (c == File.separatorChar) url.append('/');
            else if (c == ':' && isWindows) url.append(':');
            else url.append(URLEncoder.encode(String.valueOf(c)));
        }
        
        String base = url.toString();
        return build(fin, base);
        
    }

    
    /**
     * <p>
     * This method reads the document from a reader.
     * </p>
     * 
     * @param in the <code>Reader</code> from which the 
     *   document is read. 
     * 
     * @return  the parsed <code>Document</code>
     * 
     * @throws ParsingException  if a well-formedness error is detected
     * @throws IOException       if an I/O error such as a bad disk.
     * @throws ValidityException if a validity error is detected. This 
     *   is only thrown if the builder has been instructed to validate.
     */
    public Document build(Reader in) 
      throws ParsingException, ValidityException, IOException {

        InputSource source = new InputSource(in);
        return build(source);
        
    }

    /**
     * <p>
     * This method reads the document from an input stream while
     * allowing a base URI to be specified.
     * </p>
     * 
     * @param in the <code>Reader</code> from which the document 
     *   is read. 
     * @param baseURI the base URI for this document.
     * 
     * @return  the parsed <code>Document</code>
     * 
     * @throws ParsingException    if a well-formedness error is detected
     * @throws IOException       if an I/O error such as a bad disk 
     *     prevents the document from being completely read
     * @throws ValidityException if a validity error is detected. This 
     *   is only thrown if the builder has been instructed to validate.
     */
    public Document build(Reader in, String baseURI) 
      throws ParsingException, ValidityException, IOException {
          
        baseURI = canonicalizeURL(baseURI);
        InputSource source = new InputSource(in);
        source.setSystemId(baseURI);
        return build(source);
        
    }
    
    /**
     * <p>
     * This method reads the document from the contents of a 
     * <code>String</code>.
     * </p>
     * 
     * @param document the <code>String</code> that contains 
     *      the XML document. 
     * @param baseURI the base URI for this document.
     * 
     * @return  the parsed <code>Document</code>
     * 
     * @throws IOException       if an I/O error such as a bad disk 
     *     prevents the doucment's external DTD subset from being read
     * @throws ParsingException    if a well-formedness error is detected
     * @throws ValidityException if a validity error is detected. This 
     *     is only thrown if the builder has been instructed to 
     *     validate.
     */
    public Document build(String document, String baseURI) 
      throws ParsingException, ValidityException, IOException {

        Reader reader = new StringReader(document);
        return build(reader, baseURI);
        
    }
    
    // needed to work around a bug in Xerces and Crimson
    // for URLs with no trailing slashes (no path part) 
    // such as http://www.cafeconleche.org
    private String canonicalizeURL(String uri) {
        
        try {
            URL u = new URL(uri);
            String path = u.getFile();
            if (path == null || path.length() == 0 
              || ("/".equals(path) && !(uri.endsWith("/")))) {
                uri += '/';
            }
            return uri;
        }
        catch (MalformedURLException ex) {
            return uri;
        }
    }
    
    /**
     * <p>
     * This method reads the document from a 
     * SAX <code>InputSource</code>.
     * </p>
     * 
     * @param in the <code>InputSource</code> from 
     *     which the document is read. 
     * 
     * @return the parsed <code>Document</code>
     * 
     * @throws ParsingException  if a well-formedness error is detected
     * @throws IOException       if an I/O error such as a bad disk
     *     prevents the doucment from being read
     * @throws ValidityException if a validity error is detected. This 
     *     is only thrown if the builder has been instructed to 
     *     validate.
     */
    private Document build(InputSource in) 
      throws ParsingException, ValidityException, IOException {

        try {
            parser.parse(in);
        }
        catch (SAXParseException ex) {
            ParsingException pex = new ParsingException(
                ex.getMessage(),
                ex.getLineNumber(),
                ex.getColumnNumber(),
                ex.getException());
            throw pex;
        }
        catch (SAXException ex) {
            ParsingException pex 
              = new ParsingException(ex.getMessage(), ex);
            throw pex;
        }

        XOMHandler handler = (XOMHandler) (parser.getContentHandler());
        ErrorHandler errorHandler = parser.getErrorHandler();
        Document result = handler.getDocument();
        
        if (errorHandler instanceof ValidityRequired) {
            ValidityRequired validityHandler 
              = (ValidityRequired) errorHandler;
            if (!validityHandler.isValid())  {
                ValidityException vex = validityHandler.vexception;
                vex.setDocument(result);
                validityHandler.reset();
                throw vex;
            }      
        }        
        return result;
        
    }
    
    private static class ValidityRequired implements ErrorHandler {

        ValidityException vexception = null;

        void reset() {
            vexception = null;   
        }

        public void warning(SAXParseException exception) {
            // ignore warnings
        }
      
        public void error(SAXParseException exception) { 
              
            if (vexception == null) {
                vexception = new ValidityException(
                  exception.getMessage(),
                  exception.getLineNumber(), 
                  exception.getColumnNumber(), 
                  exception);
            }
            vexception.addError(exception);           
        }
      
        public void fatalError(SAXParseException exception) 
          throws SAXParseException {
            throw exception;            
        } 
        
        boolean isValid() {
            return vexception == null;   
        }        
        
    }

    // Because Crimson doesn't report namespace errors as fatal    
    private static class NamespaceWellformednessRequired 
      implements ErrorHandler {

        public void warning(SAXParseException exception) {
            // ignore warnings
        }
      
        public void error(SAXParseException exception) 
          throws SAXParseException { 
            throw exception;            
        }
      
        public void fatalError(SAXParseException exception) 
          throws SAXParseException {
            throw exception;            
        }        
        
    }

    // I added this because XIncluder needed it.
    // Note that this method is careful to not return an instance
    // of NonVerifyingFactory.
    /**
     * <p>
     *   If a custom <code>NodeFactory</code> was passed to the 
     *   constructor, then this method returns a reference to it.
     *   If no <code>NodeFactory</code> was passed to the 
     *   constructor, then this method returns null.
     * </p>
     * 
     * @return the node factory this builder uses, or null
     *     if none was specified in the constructor
     */
    public NodeFactory getNodeFactory() {
        
        if (!(factory instanceof NonVerifyingFactory)) return factory;
        else return null;
        
    }

}