// XMLWriter.java - serialize an XML document.
// Written by David Megginson, david@megginson.com
// NO WARRANTY!  This class is in the public domain.


package nu.xom.tests;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Hashtable;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.NamespaceSupport;
import org.xml.sax.helpers.XMLFilterImpl;


/**
 * <p>
 *   The original version of this class was written and placed in the
 *   public domain by David Megginson. Elliotte Rusty Harold added 
 *   <code>LexicalHandler</code> support. It is included here purely
 *   for help with testing the <code>SAXConverter</code> class. It is 
 *   not part of the XOM API; nor is it used internally by XOM anywhere
 *   except in the <code>SAXConverter</code> tests.
 * </p>
 * 
 * <p>
 *   This class does not properly preserve additional namespace
 *   declarations in non-root elements. If you encounter that, the 
 *   bug is here, not in <code>SAXConverter</code>.
 * </p>
 *
 * @author David Megginson, Elliotte Rusty Harold
 * @version 1.0d23
 */
class XMLWriter extends XMLFilterImpl implements LexicalHandler {

    ///////////////////////////////////////////////////////////////////
    // Constructors.
    ///////////////////////////////////////////////////////////////////


    /**
     * <p>
     * Create a new XML writer.
     * </p>
     * 
     * <p>Write to standard output.</p>
     */
    public XMLWriter() {
    	init(null);
    }
    

    /**
     * <p>
     * Create a new XML writer.
     * </p>
     * 
     * <p>Write to the writer provided.</p>
     *
     * @param writer the output destination, or null to use standard
     *        output
     */
    public XMLWriter (Writer writer) {
	   init(writer);
    }

    /**
     * <p>
     * Internal initialization method.
     * </p>
     * 
     * <p>All of the public constructors invoke this method.
     *
     * @param writer the output destination, or null to use
     *        standard output.
     */
    private void init (Writer writer) {
    	setOutput(writer);
    	nsSupport = new NamespaceSupport();
    	prefixTable = new Hashtable();
    	forcedDeclTable = new Hashtable();
    	doneDeclTable = new Hashtable();
    }


    ///////////////////////////////////////////////////////////////////
    // Public methods.
    ///////////////////////////////////////////////////////////////////


    /**
     * <p>
     * Reset the writer.
     * </p>
     * 
     * <p>This method is especially useful if the writer throws an
     * exception before it is finished, and you want to reuse the
     * writer for a new document.  It is usually a good idea to
     * invoke {@link #flush flush} before resetting the writer,
     * to make sure that no output is lost.</p>
     *
     * <p>This method is invoked automatically by the
     * {@link #startDocument startDocument} method before writing
     * a new document.</p>
     *
     * <p><strong>Note:</strong> this method will <em>not</em>
     * clear the prefix or URI information in the writer or
     * the selected output writer.</p>
     *
     * @see #flush
     */
    public void reset() {
    	elementLevel = 0;
    	prefixCounter = 0;
    	nsSupport.reset();
    }
    

    /**
     * <p>
     * Flush the output.
     * </p>
     * 
     * <p>This method flushes the output stream.  It is especially useful
     * when you need to make certain that the entire document has
     * been written to output but do not want to close the output
     * stream.</p>
     *
     * <p>This method is invoked automatically by the
     * {@link #endDocument endDocument} method after writing a
     * document.</p>
     *
     * @see #reset
     */
    public void flush() throws IOException {
	   output.flush();
    }
    

    /**
     * <p>
     * Set a new output destination for the document.
     * </p>
     * 
     * @param writer the output destination, or null to use
     *        standard output
     * 
     * @return the current output writer
     * 
     * @see #flush
     */
    public void setOutput (Writer writer) {
    	if (writer == null) {
    	    output = new OutputStreamWriter(System.out);
    	} 
        else {
    	    output = writer;
    	}
    }


    /**
     * <p>
     * Specify a preferred prefix for a namespace URI.
     * </p>
     * 
     * <p>Note that this method does not actually force the namespace
     * to be declared; to do that, use the {@link 
     * #forceNSDecl(java.lang.String) forceNSDecl} method as well.</p>
     *
     * @param uri the namespace URI
     * @param prefix the preferred prefix, or "" to select
     *        the default namespace
     * 
     * @see #getPrefix
     * @see #forceNSDecl(java.lang.String)
     * @see #forceNSDecl(java.lang.String,java.lang.String)
     */    
    public void setPrefix (String uri, String prefix) {
	   prefixTable.put(uri, prefix);
    }
    

    /**
     * <p>
     * Get the current or preferred prefix for a namespace URI.
     * </p>
     * 
     * @param uri the namespace URI
     * 
     * @return the preferred prefix, or "" for the default namespace
     * 
     * @see #setPrefix
     */
    public String getPrefix (String uri) {
	   return (String)prefixTable.get(uri);
    }
    
    public void startPrefixMapping(String prefix, String uri) {
        this.forceNSDecl(uri, prefix);
    }  

    /**
     * <p>
     * Force a namespace to be declared on the root element.
     * </p>
     * 
     * <p>By default, the XMLWriter will declare only the namespaces
     * needed for an element; as a result, a namespace may be
     * declared many places in a document if it is not used on the
     * root element.</p>
     *
     * <p>This method forces a namespace to be declared on the root
     * element even if it is not used there, and reduces the number
     * of xmlns attributes in the document.</p>
     *
     * @param uri the namespace URI to declare
     * 
     * @see #forceNSDecl(java.lang.String,java.lang.String)
     * @see #setPrefix
     */
    public void forceNSDecl (String uri) {
	   forcedDeclTable.put(uri, Boolean.TRUE);
    }
    

    /**
     * <p>
     * Force a namespace declaration with a preferred prefix.
     * </p>
     * 
     * <p>This is a convenience method that invokes {@link
     * #setPrefix setPrefix} then {@link #forceNSDecl(java.lang.String)
     * forceNSDecl}.</p>
     *
     * @param uri the namespace URI to declare on the root element
     * @param prefix the preferred prefix for the namespace, or ""
     *        for the default namespace
     * 
     * @see #setPrefix
     * @see #forceNSDecl(java.lang.String)
     */
    public void forceNSDecl (String uri, String prefix) {
    	setPrefix(uri, prefix);
    	forceNSDecl(uri);
    }
    

    ///////////////////////////////////////////////////////////////////
    // Methods from org.xml.sax.ContentHandler.
    ///////////////////////////////////////////////////////////////////


    /**
     * <p>
     * Write the XML declaration at the beginning of the document.
     * </p>
     * 
     * <p>
     * Pass the event on down the filter chain for further processing.
     * </p>
     * 
     * @throws org.xml.sax.SAXException if there is an error
     *            writing the XML declaration, or if a handler further 
     *            down the filter chain raises an exception
     * 
     * @see org.xml.sax.ContentHandler#startDocument
     */
    public void startDocument() throws SAXException {
    	reset();
    	write("<?xml version=\"1.0\" standalone=\"yes\"?>\n\n");
    	super.startDocument();
    }


    /**
     * <p>
     * Write a newline at the end of the document.
     * </p>
     * 
     * <p>
     * Pass the event on down the filter chain for further processing.
     * </p>
     * 
     * @throws org.xml.sax.SAXException if there is an error
     *            writing the newline, or if a handler further down
     *            the filter chain raises an exception
     * 
     * @see org.xml.sax.ContentHandler#endDocument
     */
    public void endDocument() throws SAXException {
    	write('\n');
    	super.endDocument();
    	try {
    	    flush();
    	} catch (IOException ex) {
    	    throw new SAXException(ex);
    	}
    }
    

    /**
     * <p>
     * Write a start-tag.
     * </p>
     * 
     * <p>
     * Pass the event on down the filter chain for further processing.
     * </p>
     * 
     * @param uri the namespace URI, or the empty string if none
     *        is available
     * @param localName the element's local (unprefixed) name (required)
     * @param qualifiedName the element's qualified (prefixed) name,
     *        or the empty string is none is available.  This method 
     *        will use the qualified name as a template for generating
     *        a prefix if necessary, but it is not guaranteed to use 
     *        the same qualified name.
     * @param atts the element's attribute list (must not be null)
     * 
     * @throws org.xml.sax.SAXException if there is an error
     *            writing the start-tag, or if a handler further down
     *            the filter chain raises an exception
     * 
     * @see org.xml.sax.ContentHandler#startElement
     */
    public void startElement (String uri, String localName,
	  String qualifiedName, Attributes atts) throws SAXException {
    	elementLevel++;
    	nsSupport.pushContext();
    	write('<');
    	writeName(uri, localName, qualifiedName, true);
    	writeAttributes(atts);
    	if (elementLevel == 1) {
    	    forceNSDecls();
    	}
    	writeNSDecls();
    	write('>');
    	super.startElement(uri, localName, qualifiedName, atts);
    }


    /**
     * <p>
     * Write an end-tag.
     * </p>
     * 
     * <p>
     * Pass the event on down the filter chain for further processing.
     * </p>
     * 
     * @param uri the namespace URI, or the empty string if none
     *        is available
     * @param localName the element's local (unprefixed) name (required)
     * @param qualifiedName the element's qualified (prefixed) name, or the
     *        empty string is none is available.  This method will
     *        use the qName as a template for generating a prefix
     *        if necessary, but it is not guaranteed to use the
     *        same qualified name.
     * 
     * @throws org.xml.sax.SAXException if there is an error
     *            writing the end-tag, or if a handler further down
     *            the filter chain raises an exception
     * 
     * @see org.xml.sax.ContentHandler#endElement
     */
    public void endElement (String uri, String localName, String qualifiedName)
	  throws SAXException {
    	write("</");
    	writeName(uri, localName, qualifiedName, true);
    	write('>');
    	if (elementLevel == 1) {
    	    write('\n');
    	}
    	super.endElement(uri, localName, qualifiedName);
    	nsSupport.popContext();
    	elementLevel--;
    }
    

    /**
     * <p>
     * Write character data.
     * </p>
     * 
     * <p>
     * Pass the event on down the filter chain for further processing.
     * </p>
     * 
     * @param ch the array of characters to write
     * @param start the starting position in the array
     * @param length the number of characters to write
     * 
     * @throws org.xml.sax.SAXException if there is an error
     *            writing the characters, or if a handler further down
     *            the filter chain raises an exception
     * 
     * @see org.xml.sax.ContentHandler#characters
     */
    public void characters (char[] ch, int start, int len)
      throws SAXException {
    	writeEsc(ch, start, len, false);
    	super.characters(ch, start, len);
    }
    

    /**
     * <p>
     * Write ignorable whitespace.
     * </p>
     * 
     * <p>
     * Pass the event on down the filter chain for further processing.
     * </p>
     * 
     * @param ch the array of characters to write
     * @param start the starting position in the array
     * @param length the number of characters to write
     * 
     * @throws org.xml.sax.SAXException if there is an error
     *            writing the whitespace, or if a handler further down
     *            the filter chain raises an exception
     * 
     * @see org.xml.sax.ContentHandler#ignorableWhitespace
     */
    public void ignorableWhitespace (char[] ch, int start, int length)
	  throws SAXException {
    	writeEsc(ch, start, length, false);
    	super.ignorableWhitespace(ch, start, length);
    }
    


    /**
     * <p>
     * Write a processing instruction.
     * </p>
     * 
     * <p>
     * Pass the event on down the filter chain for further processing.
     * </p>
     * 
     * @param target the processing instruction target
     * @param data the processing instruction data
     * 
     * @throws org.xml.sax.SAXException if there is an error
     *            writing the PI, or if a handler further down
     *            the filter chain raises an exception
     * 
     * @see org.xml.sax.ContentHandler#processingInstruction
     */
    public void processingInstruction (String target, String data)
	  throws SAXException {
    	write("<?");
    	write(target);
    	write(' ');
    	write(data);
    	write("?>");
    	if (elementLevel < 1) {
    	    write('\n');
    	}
    	super.processingInstruction(target, data);
    }
    


    ///////////////////////////////////////////////////////////////////
    // Additional markup.
    ///////////////////////////////////////////////////////////////////

    /**
     * <p>
     * Write an empty element.
     * </p>
     * 
     * <p>
     * This method writes an empty-element tag rather than a start-tag
     * followed by an end-tag.  Both a {@link #startElement
     * startElement} and an {@link #endElement endElement} event will
     * be passed on down the filter chain.
     * </p>
     *
     * @param uri the element's namespace URI, or the empty string
     *        if the element has no namespace or if namespace
     *        processing is not being performed
     * @param localName the element's local name (without prefix).  This
     *        parameter must be provided.
     * @param qName the element's qualified name (with prefix), or
     *        the empty string if none is available.  This parameter
     *        is strictly advisory: the writer may or may not use
     *        the prefix attached.
     * @param atts the element's attribute list
     * 
     * @throws org.xml.sax.SAXException if there is an error
     *            writing the empty tag, or if a handler further down
     *            the filter chain raises an exception
     * 
     * @see #startElement
     * @see #endElement 
     */
    public void emptyElement (String uri, String localName,
	  String qualifiedName, Attributes atts) throws SAXException {
    	nsSupport.pushContext();
    	write('<');
    	writeName(uri, localName, qualifiedName, true);
    	writeAttributes(atts);
    	if (elementLevel == 1) {
    	    forceNSDecls();
    	}
    	writeNSDecls();
    	write("/>");
    	super.startElement(uri, localName, qualifiedName, atts);
    	super.endElement(uri, localName, qualifiedName);
    }



    ///////////////////////////////////////////////////////////////////
    // Convenience methods.
    ///////////////////////////////////////////////////////////////////
    


    /**
     * <p>
     * Start a new element without a qualified name or attributes.
     * </p>
     * 
     * <p>This method will provide a default empty attribute
     * list and an empty string for the qualified name.  
     * It invokes {@link 
     * #startElement(String, String, String, Attributes)}
     * directly.</p>
     *
     * @param uri the element's namespace URI
     * @param localName the element's local name
     * 
     * @throws org.xml.sax.SAXException if there is an error
     *            writing the start-tag, or if a handler further down
     *            the filter chain raises an exception
     * 
     * @see #startElement(String, String, String, Attributes)
     */
    public void startElement (String uri, String localName) 
      throws SAXException {
        startElement(uri, localName, "", EMPTY_ATTS);
    }


    /**
     * <p>
     * Start a new element without a qualified name, 
     * attributes or a namespace URI.</p>
     *
     * <p>This method will provide an empty string for the
     * namespace URI, and empty string for the qualified name,
     * and a default empty attribute list. It invokes
     * #startElement(String, String, String, Attributes)}
     * directly.</p>
     *
     * @param localName the element's local name
     * 
     * @throws org.xml.sax.SAXException if there is an error
     *            writing the start-tag, or if a handler further down
     *            the filter chain raises an exception
     * 
     * @see #startElement(String, String, String, Attributes)
     */
    public void startElement (String localName) throws SAXException {
	   startElement("", localName, "", EMPTY_ATTS);
    }


    /**
     * <p>
     * End an element without a qualfied name.
     * </p>
     * 
     * <p>This method will supply an empty string for the qName.
     * It invokes {@link #endElement(String, String, String)}
     * directly.</p>
     *
     * @param uri the element's namespace URI
     * @param localName the element's local name
     * 
     * @throws org.xml.sax.SAXException if there is an error
     *            writing the end-tag, or if a handler further down
     *            the filter chain raises an exception
     * 
     * @see #endElement(String, String, String)
     */
    public void endElement(String uri, String localName)
	  throws SAXException {
	    endElement(uri, localName, "");
    }


    /**
     * <p>
     * End an element without a namespace URI or qualfiied name.
     * </p>
     * 
     * <p>This method will supply an empty string for the qName
     * and an empty string for the namespace URI.
     * It invokes {@link #endElement(String, String, String)}
     * directly.</p>
     *
     * @param localName the element's local name`
     * 
     * @throws org.xml.sax.SAXException if there is an error
     *            writing the end-tag, or if a handler further down
     *            the filter chain raises an exception
     * 
     * @see #endElement(String, String, String)
     */
    public void endElement(String localName) throws SAXException {
	    endElement("", localName, "");
    }


    /**
     * <p>
     * Add an empty element without a qualified name or attributes.
     * </p>
     * 
     * <p>This method will supply an empty string for the qualified name
     * and an empty attribute list.  It invokes
     * {@link #emptyElement(String, String, String, Attributes)} 
     * directly.</p>
     *
     * @param uri the element's namespace URI
     * @param localName the element's local name
     * 
     * @throws org.xml.sax.SAXException if there is an error
     *            writing the empty tag, or if a handler further down
     *            the filter chain raises an exception
     * 
     * @see #emptyElement(String, String, String, Attributes)
     */
    public void emptyElement (String uri, String localName)
	  throws SAXException {
	    emptyElement(uri, localName, "", EMPTY_ATTS);
    }


    /**
     * <p>
     * Add an empty element without a namespace URI, qualified
     * name or attributes.
     * </p>
     * 
     * <p>This method will supply an empty string for the qualified 
     * name, and empty string for the namespace URI, and an empty
     * attribute list.  It invokes
     * {@link #emptyElement(String, String, String, Attributes)} 
     * directly.</p>
     *
     * @param localName the element's local name
     * 
     * @throws org.xml.sax.SAXException if there is an error
     *            writing the empty tag, or if a handler further down
     *            the filter chain raises an exception
     * 
     * @see #emptyElement(String, String, String, Attributes)
     */
    public void emptyElement (String localName) throws SAXException {
	   emptyElement("", localName, "", EMPTY_ATTS);
    }


    /**
     * <p>
     * Write an element with character data content.
     * </p>
     * 
     * <p>This is a convenience method to write a complete element
     * with character data content, including the start-tag
     * and end-tag.</p>
     *
     * <p>This method invokes
     * {@link #startElement(String, String, String, Attributes)},
     * followed by
     * {@link #characters(String)}, followed by
     * {@link #endElement(String, String, String)}.</p>
     *
     * @param uri the element's namespace URI
     * @param localName the element's local name
     * @param qualifiedName the element's default qualified name
     * @param atts the element's attributes
     * @param content the character data content
     * 
     * @throws org.xml.sax.SAXException if there is an error
     *            writing the empty tag, or if a handler further down
     *            the filter chain raises an exception
     * 
     * @see #startElement(String, String, String, Attributes)
     * @see #characters(String)
     * @see #endElement(String, String, String)
     */
    public void dataElement (String uri, String localName,
	  String qualifiedName, Attributes atts, String content) 
      throws SAXException {
    	startElement(uri, localName, qualifiedName, atts);
    	characters(content);
    	endElement(uri, localName, qualifiedName);
    }


    /**
     * <p>
     * Write an element with character data content but no attributes.
     * </p>
     * 
     * <p>This is a convenience method to write a complete element
     * with character data content, including the start-tag
     * and end-tag.  This method provides an empty string
     * for the qualified name and an empty attribute list.</p>
     *
     * <p>This method invokes
     * {@link #startElement(String, String, String, Attributes)},
     * followed by
     * {@link #characters(String)}, followed by
     * {@link #endElement(String, String, String)}.</p>
     *
     * @param uri the element's namespace URI
     * @param localName the element's local name
     * @param content the character data content
     * 
     * @throws org.xml.sax.SAXException if there is an error
     *            writing the empty tag, or if a handler further down
     *            the filter chain raises an exception
     * 
     * @see #startElement(String, String, String, Attributes)
     * @see #characters(String)
     * @see #endElement(String, String, String)
     */
    public void dataElement(String uri, String localName, String content)
	  throws SAXException {
	   dataElement(uri, localName, "", EMPTY_ATTS, content);
    }


    /**
     * <p>
     * Write an element with character data content but no attributes 
     * or namespace URI.
     * </p>
     * 
     * <p>This is a convenience method to write a complete element
     * with character data content, including the start-tag
     * and end-tag.  The method provides an empty string for the
     * namespace URI, and empty string for the qualified name,
     * and an empty attribute list.</p>
     *
     * <p>This method invokes
     * {@link #startElement(String, String, String, Attributes)},
     * followed by
     * {@link #characters(String)}, followed by
     * {@link #endElement(String, String, String)}.</p>
     *
     * @param localName the element's local name
     * @param content the character data content
     * 
     * @throws org.xml.sax.SAXException if there is an error
     *            writing the empty tag, or if a handler further down
     *            the filter chain raises an exception
     * 
     * @see #startElement(String, String, String, Attributes)
     * @see #characters(String)
     * @see #endElement(String, String, String)
     */
    public void dataElement (String localName, String content) 
      throws SAXException {
	    dataElement("", localName, "", EMPTY_ATTS, content);
    }

    /**
     * <p>
     * Write a string of character data, with XML escaping.
     * </p>
     * 
     * <p>This is a convenience method that takes an XML
     * String, converts it to a character array, then invokes
     * {@link #characters(char[], int, int)}.</p>
     *
     * @param data the character data
     * @throws org.xml.sax.SAXException if there is an error
     *            writing the string, or if a handler further down
     *            the filter chain raises an exception
     * @see #characters(char[], int, int)
     */
    public void characters(String data) throws SAXException {
    	char[] ch = data.toCharArray();
    	characters(ch, 0, ch.length);
    }



    ///////////////////////////////////////////////////////////////////
    // Internal methods.
    ///////////////////////////////////////////////////////////////////
    

    /**
     * <p>
     * Force all namespaces to be declared.
     * </p>
     * 
     * <p>
     * This method is used on the root element to ensure that
     * the predeclared namespaces all appear.
     * </p>
     */
    private void forceNSDecls() {
    	Enumeration prefixes = forcedDeclTable.keys();
    	while (prefixes.hasMoreElements()) {
    	    String prefix = (String)prefixes.nextElement();
    	    doPrefix(prefix, null, true);
    	}
    }


    /**
     * <p>
     * Determine the prefix for an element or attribute name.
     * </p>
     * 
     * TODO: this method probably needs some cleanup.
     *
     * @param uri the namespace URI
     * @param qName the qualified name (optional); this will be used
     *        to indicate the preferred prefix if none is currently
     *        bound.
     * @param isElement true if this is an element name, false
     *        if it is an attribute name (which cannot use the
     *        default namespace).
     */
    private String doPrefix (String uri, String qName, boolean isElement) {
    	String defaultNS = nsSupport.getURI("");
    	if ("".equals(uri)) {
    	    if (isElement && defaultNS != null)
    		nsSupport.declarePrefix("", "");
    	    return null;
    	}
    	String prefix;
    	if (isElement && defaultNS != null && uri.equals(defaultNS)) {
    	    prefix = "";
    	} else {
    	    prefix = nsSupport.getPrefix(uri);
    	}
    	if (prefix != null) {
    	    return prefix;
    	}
    	prefix = (String) doneDeclTable.get(uri);
    	if (prefix != null &&
    	    ((!isElement || defaultNS != null) &&
    	     "".equals(prefix) || nsSupport.getURI(prefix) != null)) {
    	    prefix = null;
    	}
    	if (prefix == null) {
    	    prefix = (String) prefixTable.get(uri);
    	    if (prefix != null &&
    		((!isElement || defaultNS != null) &&
    		 "".equals(prefix) || nsSupport.getURI(prefix) != null)) {
    		prefix = null;
    	    }
    	}
    	if (prefix == null && qName != null && !"".equals(qName)) {
    	    int i = qName.indexOf(':');
    	    if (i == -1) {
    		if (isElement && defaultNS == null) {
    		    prefix = "";
    		}
    	    } else {
    		prefix = qName.substring(0, i);
    	    }
    	}
    	for (;
    	     prefix == null || nsSupport.getURI(prefix) != null;
    	     prefix = "__NS" + ++prefixCounter)
    	    ;
    	nsSupport.declarePrefix(prefix, uri);
    	doneDeclTable.put(uri, prefix);
    	return prefix;
    }
    

    /**
     * <p>
     * Write a raw character.
     * </p>
     * 
     * @param c the character to write
     * 
     * @throws org.xml.sax.SAXException if there is an error writing
     *            the character, this method will throw an IOException
     *            wrapped in a SAXException
     */
    private void write(char c) throws SAXException {
    	try {
    	    output.write(c);
    	} 
        catch (IOException ex) {
    	    throw new SAXException(ex);
    	}
    }
    

    /**
     * <p>
     * Write a raw string.
     * </p>
     * 
     * @param s
     * 
     * @throws org.xml.sax.SAXException if there is an error writing
     *            the string, this method will throw an IOException
     *            wrapped in a SAXException
     */
    private void write (String s) throws SAXException {
    	try {
    	    output.write(s);
    	} 
        catch (IOException e) {
    	    throw new SAXException(e);
    	}
    }


    /**
     * <p>
     * Write out an attribute list, escaping values.
     *</p>
     *
     * <p>
     * The names will have prefixes added to them.
     * </p>
     * 
     * @param atts the attribute list to write
     * 
     * @throws org.xml.SAXException if there is an error writing
     *            the attribute list, this method will throw an
     *            IOException wrapped in a SAXException
     */
    private void writeAttributes (Attributes atts) 
      throws SAXException {
    	int len = atts.getLength();
    	for (int i = 0; i < len; i++) {
    	    char[] ch = atts.getValue(i).toCharArray();
    	    write(' ');
    	    writeName(atts.getURI(i), atts.getLocalName(i),
    		      atts.getQName(i), false);
    	    write("=\"");
    	    writeEsc(ch, 0, ch.length, true);
    	    write('"');
    	}
    }


    /**
     * <p>
     * Write an array of data characters with escaping.
     * </p>
     * 
     * @param ch the array of characters
     * @param start the starting position
     * @param length the number of characters to use
     * @param isAttVal true if this is an attribute value literal
     * 
     * @throws org.xml.SAXException if there is an error writing
     *            the characters, this method will throw an
     *            IOException wrapped in a SAXException
     */    
    private void writeEsc (char[] ch, int start, int length, boolean isAttVal)
	  throws SAXException {
    	for (int i = start; i < start + length; i++) {
    	    switch (ch[i]) {
    	    case '&':
        		write("&amp;");
        		break;
    	    case '<':
        		write("&lt;");
        		break;
    	    case '>':
        		write("&gt;");
        		break;
    	    case '\"':
        		if (isAttVal) {
        		    write("&quot;");
        		} 
                else {
        		    write('\"');
        		}
    		    break;
    	    default:
        		if (ch[i] > '\u007f') {
        		    write("&#");
        		    write(Integer.toString(ch[i]));
        		    write(';');
        		} 
                else {
        		    write(ch[i]);
        		}
    	    }
	   }
    }

    /**
     * <p>
     * Write an array of data characters without escaping.
     * </p>
     * 
     * @param ch the array of characters
     * @param start the starting position
     * @param length the number of characters to use
     * 
     * @throws org.xml.SAXException if there is an error writing
     *            the characters, this method will throw an
     *            IOException wrapped in a SAXException.
     */    
    private void write(char[] ch, int start, int length)
      throws SAXException {   
          
        try {
            output.write(ch, start, length);
        } 
        catch (IOException e) {
            throw new SAXException(e);
        }  
        
    }


    /**
     * <p>
     * Write out the list of namespace declarations.
     * </p>
     * 
     * @throws org.xml.sax.SAXException This method will throw
     *            an IOException wrapped in a SAXException if
     *            there is an error writing the namespace
     *            declarations
     */    
    private void writeNSDecls() throws SAXException {
    	Enumeration prefixes = nsSupport.getDeclaredPrefixes();
    	while (prefixes.hasMoreElements()) {
    	    String prefix = (String) prefixes.nextElement();
    	    String uri = nsSupport.getURI(prefix);
    	    if (uri == null) {
    		  uri = "";
    	    }
    	    char[] ch = uri.toCharArray();
    	    write(' ');
    	    if ("".equals(prefix)) {
    		  write("xmlns=\"");
    	    } 
            else {
        		write("xmlns:");
        		write(prefix);
        		write("=\"");
    	    }
    	    writeEsc(ch, 0, ch.length, true);
    	    write('\"');
    	}
    }
    

    /**
     * <p>
     * Write an element or attribute name.
     * </p>
     *
     * @param uri the namespace URI
     * @param localName the local name
     * @param qualifiedName the prefixed name, if available, 
     *        or the empty string.
     * @param isElement true if this is an element name, false if it
     *        is an attribute name
     * 
     * @throws org.xml.sax.SAXException this method will throw an
     *            IOException wrapped in a SAXException if there is
     *            an error writing the name
     */
    private void writeName (String uri, String localName,
	  String qualifiedName, boolean isElement)
	  throws SAXException {
    	String prefix = doPrefix(uri, qualifiedName, isElement);
    	if (prefix != null && !"".equals(prefix)) {
    	    write(prefix);
    	    write(':');
    	}
    	write(localName);
    }



    ////////////////////////////////////////////////////////////////////
    // Constants.
    ////////////////////////////////////////////////////////////////////

    private final Attributes EMPTY_ATTS = new AttributesImpl();


    ////////////////////////////////////////////////////////////////////
    // Internal state.
    ////////////////////////////////////////////////////////////////////

    private Hashtable        prefixTable;
    private Hashtable        forcedDeclTable;
    private Hashtable        doneDeclTable;
    private int              elementLevel = 0;
    private Writer           output;
    private NamespaceSupport nsSupport;
    private int              prefixCounter = 0;


    ///////////////////////////////////////////////////////////////////
    // LexicalHandler methods.
    ///////////////////////////////////////////////////////////////////
    

    public void endCDATA() {}
    
    public void endDTD() throws SAXException {
        write(">");
    }
    
    public void startCDATA() {}
    
    public void comment(char[] ch, int start, int length) 
      throws SAXException {
        write("<!--");
        write(ch, start, length);
        write("-->");
        if (elementLevel < 1) {
            write('\n');
        }
    }

    public void endEntity(String name) {}
    public void startEntity(String name) {}
    
    public void startDTD(String name, String publicID, String systemID)
      throws SAXException {
        write("<!DOCTYPE ");
        write(name);
        if (systemID != null) {
            if (publicID != null) {
                write(" PUBLIC \"");
                write(publicID);
                write("\" \"");
                write(systemID);
                write("\"");
            }
            else {
                write(" SYSTEM \"");
                write(systemID);
                write("\"");
            }
        }

    }
    
}

// end of XMLWriter.java
 
