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

package nu.xom;

import java.io.CharConversionException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.UTFDataFormatException;
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

import org.apache.xerces.impl.Version;

/**
 * <p>
 * This class is responsible for creating XOM <code>Document</code> 
 * objects  from a URL, file, string, or input stream by reading   
 * an XML document. A SAX parser is used to read the   
 * document and report any well-formedness errors.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0b6
 * 
 */
public class Builder {

    
    private XMLReader   parser;
    private NodeFactory factory;
    
    private static double xercesVersion = 2.6;
    
    static {  

        try {
            String versionString = Version.getVersion();
            versionString = versionString.substring(9, 12);
            xercesVersion = Double.valueOf(versionString).doubleValue();
        }
        catch (Exception ex) {
            // The version string format changed so presumably it's
            // 2.6 or later 
        }
        catch (Error err) {
            // Xerces not installed, so none of this matters
        }
        
    }
    
    
    /**
     * <p>
     * Creates a <code>Builder</code> that uses the default node 
     * factory and chooses among any available SAX2 parsers. 
     * In order of preference, it looks for:
     * </p>
     * 
     * <ol>
     * <li>Xerces 2.x (a.k.a. IBM XML parser for Java)</li>
     * <li>GNU &AElig;lfred</li>
     * <li>Crimson</li>
     * <li>Piccolo</li>
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
     * Creates a <code>Builder</code> based on an optionally validating
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
     * Creates a <code>Builder</code> based on an optionally
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
        "nu.xom.XML1_0Parser",
        "nu.xom.JDK15XML1_0Parser",
        "org.apache.xerces.parsers.SAXParser",
        "com.sun.org.apache.xerces.internal.parsers.SAXParser",
        "gnu.xml.aelfred2.XmlReader",
        "org.apache.crimson.parser.XMLReaderImpl",
        "com.bluecast.xml.Piccolo", 
        "oracle.xml.parser.v2.SAXParser",
        "com.jclark.xml.sax.SAX2Driver",
        "net.sf.saxon.aelfred.SAXDriver",
        "com.icl.saxon.aelfred.SAXDriver",
        "org.dom4j.io.aelfred.SAXDriver"
    };

    
    private static XMLReader findParser(boolean validate) {
        
        // first look for Xerces; we only trust Xerces if
        // we set it up; and we need to configure it specially
        // so we can't load it with the XMLReaderFactory
        XMLReader parser; 
        try {
            parser = new XML1_0Parser();
            setupParser(parser, validate);
            return parser;
        } 
        catch (SAXException ex) {
            // look for next one
        }
        catch (NoClassDefFoundError err) {
            // Xerces is not available; look for next one
        } 

        try {
            parser = (XMLReader) Class.forName("nu.xom.JDK15XML1_0Parser").newInstance();
            setupParser(parser, validate);
            return parser;
        } 
        catch (SAXException ex) {
            // look for next one
        }
        catch (InstantiationException ex) {
            // look for next one
        } 
        catch (ClassNotFoundException ex) {
            // look for next one
        }
        catch (IllegalAccessException ex) {
            // look for next one
        }
        catch (NoClassDefFoundError err) {
            // Xerces is not available; look for next one
        } 
        
        // XMLReaderFactory.createXMLReader never returns
        // null. If it can't locate the parser, it throws
        // a SAXException.
        for (int i = 2; i < parsers.length; i++) {
            try { 
                parser = XMLReaderFactory.createXMLReader(parsers[i]);
                setupParser(parser, validate);
                return parser;
            }
            catch (SAXException ex) {
                // try the next one 
            }      
            catch (NoClassDefFoundError err) {
                // try the next one 
            }      
        }
        
        try { // default
            parser = XMLReaderFactory.createXMLReader();
            setupParser(parser, validate);
            return parser;
        }
        catch (SAXException ex) {
            throw new XMLException(
              "Could not find a suitable SAX2 parser", ex);
        }
        
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
        String parserName = parser.getClass().getName();
        if (parserName.equals("nu.xom.XML1_0Parser") 
         || parserName.equals("nu.xom.JDK15XML1_0Parser")
         || parserName.equals("org.apache.xerces.parsers.SAXParser")
         || parserName.equals("com.sun.org.apache.xerces.internal.parsers.SAXParser")) {
            try {
                parser.setFeature(
                 "http://apache.org/xml/features/allow-java-encodings", true);
            }
            catch (SAXException ex) {
                // Possibly an earlier version of Xerces; no big deal.
                // We can live without this feature.   
            }
            // See http://nagoya.apache.org/bugzilla/show_bug.cgi?id=23768
            // if you care to know why this line breaks unit tests on 
            // versions of Xerces prior to 2.6.1
            try {
                parser.setFeature(
                 "http://apache.org/xml/features/standard-uri-conformant", 
                 true);
            }
            catch (SAXException ex) {
                // Possibly an earlier version of Xerces, or a 
                // or a non-Xerces parser;  no big deal.
                // We can live without this.   
            } 
        }
        
    }        
    
    
    /**
     * <p>
     * Creates a <code>Builder</code> that uses 
     * the specified SAX <code>XMLReader</code>.
     * Custom SAX features and properties such as  
     * schema validation can be set on this <code>XMLReader</code> 
     * before passing it to this method.
     * </p>
     * 
     * @param parser the SAX2 <code>XMLReader</code> that  
     *     parses the document
     * 
     * @throws XMLException if <code>parser</code> does not support the
     *     features XOM requires
     */ 
    public Builder(XMLReader parser) {
        this(parser, false);
    }
    
    
    /**
     * <p>
     * Creates a <code>Builder</code> that uses 
     * the specified <code>NodeFactory</code> to create
     * node objects.
     * </p>
     * 
     * @param factory the <code>NodeFactory</code> that creates 
     *     the node objects for this <code>Builder</code>
     * 
     * @throws XMLException if no satisfactory parser is 
     *     installed in the local class path
     */ 
    public Builder(NodeFactory factory) {
        this(findParser(false), false, factory);
    }
    

    /**
     * <p>
     * Creates a optionally validating <code>Builder</code> based
     * on the specified parser object. Custom SAX features and  
     * properties such as schema validation can be set on this
     * <code>XMLReader</code> before passing it to this method.
     * </p>
     * 
     * <p>
     * If the validate argument is true, then a validity error
     * while parsing will cause a fatal error; that is, it 
     * will throw a <code>ParsingException</code>
     * </p>
     * 
     * @param parser the SAX2 <code>XMLReader</code> that parses
     *     the document
     * @param validate true if the parser should validate 
     *     the document while parsing
     * 
     */ 
    public Builder(XMLReader parser, boolean validate) {
        this(parser, validate, null);
    }
    
    
    /**
     * <p>
     * Creates an optionally validating <code>Builder</code> that reads
     * data from the specified parser object and constructs new nodes 
     * using the specified factory object. Custom SAX features and    
     * properties such as schema validation can be set on this 
     * <code>XMLReader</code> before passing it to this method.
     * </p>
     * 
     * <p>
     * If the <code>validate</code> argument is true, then a validity 
     * error while parsing will throw a <code>ParsingException</code>.
     * </p>
     * 
     * @param parser the SAX2 <code>XMLReader</code> that parses 
     *     the document
     * @param validate true if the parser should validate the 
     *     document while parsing
     * @param factory the <code>NodeFactory</code> 
     *     this builder uses to create objects in the tree
     * 
     * @throws XMLException if <code>parser</code> does not support
     *     the features XOM requires
     * 
     */ 
    public Builder(
      XMLReader parser, boolean validate, NodeFactory factory) {
                  
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
        else if (knownGoodParser(parser)) {
            setHandlers(new NonVerifyingFactory()); 
        } 
        else {
            setHandlers(new NodeFactory());             
        }

    }
    
    
    private static boolean knownGoodParser(XMLReader parser) {
         
        String parserName = parser.getClass().getName();
        
        // In general, a filter may violate the constraints of XML 1.0.
        // However, I specifically trust Norm Walsh not to do that, so 
        // if his filters are being used we look at the parent instead.
        if (parserName.equals("org.apache.xml.resolver.tools.ResolvingXMLReader")
          || parserName.equals("org.apache.xml.resolver.tools.ResolvingXMLFilter")) {
            XMLFilter filter = (XMLFilter) parser;
            parserName = filter.getParent().getClass().getName();
        }
        
        // These two parsers are known to not make all the checks
        // they're supposed to. :-(
        if (parserName.equals("gnu.xml.aelfred2.XmlReader")) return false;
        if (parserName.equals("net.sf.saxon.aelfred.SAXDriver")) return false;
        
        if (parserName.equals("org.apache.xerces.parsers.SAXParser")
            && xercesVersion >= 2.4) {
            return false;
        }
        
        for (int i = 0; i < parsers.length; i++) {
            if (parserName.equals(parsers[i])) return true;
        }
        return false;
        
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
            // there won't be any comments or a DOCTYPE declaration 
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
            // they won't be any internal DTD subset.
        }
        
    }
    
    
    /**
     * <p>
     * Parses the document at the specified URL.
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
     *     prevents the document from being fully read
     */
    public Document build(String systemID) 
      throws ParsingException, ValidityException, IOException {

        systemID = canonicalizeURL(systemID);
        InputSource source = new InputSource(systemID);
        return build(source);
        
    }

    
    /**
     * <p>
     * Reads the document from an input stream.
     * </p>
     * 
     * @param in the input stream from which the document is read
     * 
     * @return the parsed <code>Document</code>
     * 
     * @throws ValidityException if a validity error is detected; 
     *     only thrown if the builder has been instructed to validate
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
     * Reads the document from an input stream while specifying 
     * a base URI (which need not be the stream's actual URI).
     * </p>
     * 
     * @param in the input stream from which the document is read
     * @param baseURI the base URI for this document
     * 
     * @return the parsed <code>Document</code>
     * 
     * @throws ValidityException if a validity error is detected; 
     *     only thrown if the builder has been instructed to validate
     * @throws ParsingException if a well-formedness error is detected
     * @throws IOException if an I/O error such as a broken
     *       socket prevents the document from being fully read
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
     * Reads the document from a file.
     * The base URI of the document is set to the 
     * location of the file. 
     * </p>
     * 
     * @param in the file from which the document is read
     * 
     * @return the parsed <code>Document</code>
     * 
     * @throws ValidityException if a validity error is detected. This 
     *   is only thrown if the builder has been instructed to validate.
     * @throws ParsingException if a well-formedness error is detected
     * @throws IOException if an I/O error such as a bad disk 
     *     prevents the file from being read
     */
    public Document build(File in) 
      throws ParsingException, ValidityException, IOException {

        InputStream fin = new FileInputStream(in);
        // Java's toURL method doesn't properly escape file
        // names so we have to do it manually
        String absolute = in.getAbsolutePath();
        StringBuffer url = new StringBuffer(fileURLPrefix);
        int length = absolute.length();
        char separatorChar = File.separatorChar;
        for (int i = 0; i < length; i++) {
            char c = absolute.charAt(i);
            if (c == separatorChar) url.append('/');
            // Should I specify UTF-8 rather than platform default?
            // In fact, the platform default is likely to be UTF-8; 
            // at least in Java 1.4. I've verified this on  
            // Mac OS X 10.2, Windows 2000, and Linux. In other words,
            // it looks like URLEncoder on these platforms uses UTF-8 
            // to encode the URL, whatever the underlying encoding is.
            // (I'm not sure whether it's UTF-8 or not.)
            // In other words, this works on the three major platforms,
            // at least in Java 1.4, and it will compile and run in 
            // Java 1.3. So I should leave this as is and stop worrying
            // about it, unless someone files a bug report.
            else {
                switch(c) {
                    case ' ':  
                        url.append("%20");
                        break;
                    case '!': 
                        url.append(c);
                        break;
                    case '"': 
                        url.append("%22");
                        break;
                    case '#':  
                        url.append("%23");
                        break;
                    case '$':  
                        url.append(c);
                        break;
                    case '%':  
                        url.append("%25");
                        break;
                    case '&':  
                        url.append(c);
                        break;
                    case '\'':  
                        url.append(c);
                        break;
                    case '(':  
                        url.append(c);
                        break;
                    case ')':  
                        url.append(c);
                        break;
                    case '*':  
                        url.append(c);
                        break;
                    case '+':  
                        url.append(c);
                        break;
                    case ',':  
                        url.append(c);
                        break;
                    case '-':  
                        url.append(c);
                        break;
                    case '.':  
                        url.append(c);
                        break;
                    case '/':  
                        url.append(c);
                        break;
                    case '0':  
                        url.append(c);
                        break;
                    case '1':  
                        url.append(c);
                        break;
                    case '2':  
                        url.append(c);
                        break;
                    case '3':  
                        url.append(c);
                        break;
                    case '4':  
                        url.append(c);
                        break;
                    case '5':  
                        url.append(c);
                        break;
                    case '6':  
                        url.append(c);
                        break;
                    case '7':  
                        url.append(c);
                        break;
                    case '8':  
                        url.append(c);
                        break;
                    case '9':  
                        url.append(c);
                        break;
                    case ':':  
                        url.append(c);
                        break;
                    case ';':  
                        url.append(c);
                        break;
                    case '<':  
                        url.append("%3C");
                        break;
                    case '=':  
                        url.append(c);
                        break;
                    case '>':  
                        url.append("%3E");
                        break;
                    case '?':  
                        url.append(c);
                        break;
                    case '@':  
                        url.append(c);
                        break;
                    case 'A':  
                        url.append(c);
                        break;
                    case 'B':  
                        url.append(c);
                        break;
                    case 'C':  
                        url.append(c);
                        break;
                    case 'D':  
                        url.append(c);
                        break;
                    case 'E':  
                        url.append(c);
                        break;
                    case 'F':  
                        url.append(c);
                        break;
                    case 'G':  
                        url.append(c);
                        break;
                    case 'H':  
                        url.append(c);
                        break;
                    case 'I':  
                        url.append(c);
                        break;
                    case 'J':  
                        url.append(c);
                        break;
                    case 'K':  
                        url.append(c);
                        break;
                    case 'L':  
                        url.append(c);
                        break;
                    case 'M':  
                        url.append(c);
                        break;
                    case 'N':  
                        url.append(c);
                        break;
                    case 'O':  
                        url.append(c);
                        break;
                    case 'P':  
                        url.append(c);
                        break;
                    case 'Q':  
                        url.append(c);
                        break;
                    case 'R':  
                        url.append(c);
                        break;
                    case 'S':  
                        url.append(c);
                        break;
                    case 'T':  
                        url.append(c);
                        break;
                    case 'U':  
                        url.append(c);
                        break;
                    case 'V':  
                        url.append(c);
                        break;
                    case 'W':  
                        url.append(c);
                        break;
                    case 'X':  
                        url.append(c);
                        break;
                    case 'Y':  
                        url.append(c);
                        break;
                    case 'Z':  
                        url.append(c);
                        break;
                    case '[':  
                        url.append(c);
                        break;
                    case '\\':  
                        url.append("%5C");
                        break;
                    case ']':  
                        url.append(c);
                        break;
                    case '^':  
                        url.append("%5E");
                        break;
                    case '_':  
                        url.append(c);
                        break;
                    case '`':  
                        url.append("%60");
                        break;
                    case 'a':  
                        url.append(c);
                        break;
                    case 'b':  
                        url.append(c);
                        break;
                    case 'c':  
                        url.append(c);
                        break;
                    case 'd':  
                        url.append(c);
                        break;
                    case 'e':  
                        url.append(c);
                        break;
                    case 'f':  
                        url.append(c);
                        break;
                    case 'g':  
                        url.append(c);
                        break;
                    case 'h':  
                        url.append(c);
                        break;
                    case 'i':  
                        url.append(c);
                        break;
                    case 'j':  
                        url.append(c);
                        break;
                    case 'k':  
                        url.append(c);
                        break;
                    case 'l':  
                        url.append(c);
                        break;
                    case 'm':  
                        url.append(c);
                        break;
                    case 'n':  
                        url.append(c);
                        break;
                    case 'o':  
                        url.append(c);
                        break;
                    case 'p':  
                        url.append(c);
                        break;
                    case 'q':  
                        url.append(c);
                        break;
                    case 'r':  
                        url.append(c);
                        break;
                    case 's':  
                        url.append(c);
                        break;
                    case 't':  
                        url.append(c);
                        break;
                    case 'u':  
                        url.append(c);
                        break;
                    case 'v':  
                        url.append(c);
                        break;
                    case 'w':  
                        url.append(c);
                        break;
                    case 'x':  
                        url.append(c);
                        break;
                    case 'y':  
                        url.append(c);
                        break;
                    case 'z':  
                        url.append(c);
                        break;
                    case '{':  
                        url.append("%7B");
                        break;
                    case '|':  
                        url.append("%7C");
                        break;
                    case '}':  
                        url.append("%7D");
                        break;
                    case '~':  
                        url.append(c);
                        break;
                    default: 
                        if (c < 0xD800 || c > 0xDFFF) {
                            url.append(URLEncoder.encode(String.valueOf(c)));
                        }
                        else if (c <= 0xDBFF) {
                            // high surrogate; therefore we need to 
                            // grab the next half before encoding
                            i++;
                            try {
                                char low = absolute.charAt(i);
                                url.append(URLEncoder.encode(String.valueOf(c)+String.valueOf(low)));
                            }
                            catch (IndexOutOfBoundsException ex) {
                                // file name contains a high half and not a low half
                                url = new StringBuffer();
                                break;
                            }
                        }
                        else {
                            url = new StringBuffer();
                            break;
                        }
                }
            }
        }
        
        String base = url.toString();
        Document doc = build(fin, base);
        fin.close();
        return doc;
        
    }

    
    /**
     * <p>
     * Reads the document from a reader.
     * </p>
     * 
     * @param in the reader from which the document is read
     * 
     * @return the parsed <code>Document</code>
     * 
     * @throws ValidityException if a validity error is detected. This 
     *   is only thrown if the builder has been instructed to validate.
     * @throws ParsingException  if a well-formedness error is detected
     * @throws IOException       if an I/O error such as a bad disk
     *     prevents the document from being fully read
     */
    public Document build(Reader in) 
      throws ParsingException, ValidityException, IOException {

        InputSource source = new InputSource(in);
        return build(source);
        
    }

    
    /**
     * <p>
     * Reads the document from a character stream while
     * specifying a base URI.
     * </p>
     * 
     * @param in the reader from which the document 
     *     is read
     * @param baseURI the base URI for this document
     * 
     * @return the parsed <code>Document</code>
     * 
     * @throws ValidityException if a validity error is detected. This 
     *     is only thrown if the builder has been instructed to 
     *     validate.
     * @throws ParsingException  if a well-formedness error is detected
     * @throws IOException       if an I/O error such as a bad disk 
     *     prevents the document from being completely read
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
     * Reads the document from the contents of a string.
     * </p>
     * 
     * @param document the string that contains 
     *      the XML document. 
     * @param baseURI the base URI for this document
     * 
     * @return  the parsed <code>Document</code>
     * 
     * @throws ValidityException if a validity error is detected. This 
     *     is only thrown if the builder has been instructed to 
     *     validate.
     * @throws ParsingException  if a well-formedness error is detected
     * @throws IOException       if an I/O error such as a bad disk 
     *     prevents the document's external DTD subset from being read
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
     * Reads the document from a SAX <code>InputSource</code>.
     * </p>
     * 
     * @param in the input source from 
     *     which the document is read. 
     * 
     * @return the parsed <code>Document</code>
     * 
     * @throws ValidityException if a validity error is detected. This 
     *     is only thrown if the builder has been instructed to 
     *     validate.
     * @throws ParsingException  if a well-formedness error is detected
     * @throws IOException       if an I/O error such as a bad disk
     *     prevents the document from being read
     */
    private Document build(InputSource in) 
      throws ParsingException, ValidityException, IOException {

        try {
            parser.parse(in);
        }
        catch (SAXParseException ex) {
            ParsingException pex = new ParsingException(
                ex.getMessage(),
                ex.getSystemId(),
                ex.getLineNumber(),
                ex.getColumnNumber(),
                ex.getException());
            throw pex;
        }
        catch (SAXException ex) {
            ParsingException pex 
              = new ParsingException(ex.getMessage(), in.getSystemId(), ex);
            throw pex;
        }
        catch (ArrayIndexOutOfBoundsException ex) {
            // Work-around for non-conformant parsers, especially Piccolo
            ParsingException pex 
              = new ParsingException(ex.getMessage(), in.getSystemId(), ex);
            throw pex;
        }
        catch (NegativeArraySizeException ex) {
            // Work-around for non-conformant parsers, especially Piccolo
            ParsingException pex 
              = new ParsingException(ex.getMessage(), in.getSystemId(), ex);
            throw pex;
        }
        catch (NullPointerException ex) {
            // Work-around for non-conformant parsers, especially Piccolo
            // This also affects factories that return null where they shouldn't
            ParsingException pex 
              = new ParsingException(ex.getMessage(), in.getSystemId(), ex);
            throw pex;
        }
        catch (UTFDataFormatException ex) {
            // Work-around for non-conformant parsers, especially Xerces
            // http://nagoya.apache.org/bugzilla/show_bug.cgi?id=27583
            ParsingException pex 
              = new ParsingException(ex.getMessage(), in.getSystemId(), ex);
            throw pex;
        }
        catch (CharConversionException ex) {
            // Work-around for non-conformant parsers, especially Xerces
            // http://nagoya.apache.org/bugzilla/show_bug.cgi?id=27583
            ParsingException pex 
              = new ParsingException(ex.getMessage(), in.getSystemId(), ex);
            throw pex;
        }
        catch (IOException ex) {
            // Work-around for Xerces; I don't want to just catch
            // org.apache.xerces.util.URI.MalformedURIException
            // because that would introduce a dependence on Xerces
            if (ex.getClass().getName().equals(
              "org.apache.xerces.util.URI$MalformedURIException")) {
                throw new ParsingException(ex.getMessage(), in.getSystemId(), ex);
            }
            else {
                throw ex;
            }
        }
        
        XOMHandler handler = (XOMHandler) (parser.getContentHandler());
        ErrorHandler errorHandler = parser.getErrorHandler();
        Document result = handler.getDocument();
        String base = in.getSystemId();
        if (result != null && "".equals(result.getBaseURI())) {
            result.setBaseURI(in.getSystemId());
        }
        
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
                  exception.getSystemId(),
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
     * Returns this builder's <code>NodeFactory</code>, or null if
     * a factory was not supplied when the builder was created.
     * </p>
     * 
     * @return the node factory that was specified in the constructor
     */
    public NodeFactory getNodeFactory() {
        
        if (factory instanceof NonVerifyingFactory) return null;
        else return factory;
        
    }

    
}