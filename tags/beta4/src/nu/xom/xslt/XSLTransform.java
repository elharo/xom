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

package nu.xom.xslt;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.SAXParseException;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.NodeFactory;
import nu.xom.Nodes;
import nu.xom.XMLException;

/**
 * <p>
 * Serves as an interface to a TrAX aware XSLT processor such as Xalan
 * or Saxon. The following example shows how to apply an XSL 
 * Transformation to a XOM document and get the transformation result 
 * in the form of a XOM <code>Nodes</code>:</p>
 * <blockquote><pre>public static Nodes transform(Document in) 
 *   throws XSLException, ParsingException, IOException {
 *     Builder builder = new Builder();
 *     Document stylesheet = builder.build("mystylesheet.xsl");
 *     XSLTransform stylesheet = new XSLTransform(stylesheet);
 *     return stylesheet.transform(doc);
 * } </pre></blockquote>
 *
 * <p>
 *  XOM relies on TrAX to perform the transformation.
 *  The <code>javax.xml.transform.TransformerFactory</code> Java 
 *  system property determines which XSLT engine TrAX uses. Its 
 *  value should be the fully qualified name of the implementation 
 *  of the abstract <code>javax.xml.transform.TransformerFactory</code>
 *  class. Values of this property for popular XSLT processors include:
 *  </p>
 *  <ul>
 *   <li>Saxon 6.x: 
 *    <code>com.icl.saxon.TransformerFactoryImpl</code>
 *   </li>
 *   <li>Saxon 7.x and 8.x: 
 *    <code>net.sf.saxon.TransformerFactoryImpl</code>
 *   </li>
 *   <li>Xalan interpretive: 
 *    <code>org.apache.xalan.processor.TransformerFactoryImpl</code>
 *   </li>
 *   <li>Xalan XSLTC: 
 *    <code>org.apache.xalan.xsltc.trax.TransformerFactoryImpl</code>
 *   </li>
 *   <li>jd.xslt: 
 *    <code>jd.xml.xslt.trax.TransformerFactoryImpl</code>
 *   </li>
 *   <li>Oracle: 
 *    <code>oracle.xml.jaxp.JXSAXTransformerFactory</code>
 *   </li>
 *   <li>Java 1.5 bundled Xalan: 
 *    <code>com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl</code>
 *   </li>
 *  </ul>
 *  <p>
 *   This property can be set in all the usual ways a Java system 
 *   property can be set. TrAX picks from them in this order:</p>
 *   <ol>
 *   <li>The most recent value specified by invoking 
 *   <code>System.setProperty("javax.xml.transform.TransformerFactory", 
 *   "<i><code>classname</code></i>")</code></li>
 *   <li>The value specified at the command line using the 
 * <samp>-Djavax.xml.transform.TransformerFactory=<i>classname</i></samp>
 *      option to the <b>java</b> interpreter</li>
 *    <li>The class named in the  <code>lib/jaxp.properties</code> 
 *       properties file in the JRE directory, in a line like this one:
 * <pre>javax.xml.parsers.DocumentBuilderFactory=<i>classname</i></pre>
 *    </li>
 *    <li>The class named in the 
 * <code>META-INF/services/javax.xml.transform.TransformerFactory</code>
 *   file in the JAR archives available to the runtime</li>
 *   <li>Finally, if all of the above options fail,
 *    a default implementation is chosen. In Sun's JDK 1.4.0 and 1.4.1,
 *    this is Xalan 2.2d10. In JDK 1.4.2, this is Xalan 2.4. 
 *    In JDK 1.4.2_02, this is Xalan 2.4.1.
 *    In JDK 1.4.2_03, 1.5 beta 2, and 1.5 RC1 this is Xalan 2.5.2. 
 *    In JDK 1.4.2_05, this is Xalan 2.4.1. (Yes, Sun appears to have
 *    reverted to 2.4.1 in 1.4.2_05.)
 *    </li>
 *    </ol>
 *
 * @author Elliotte Rusty Harold
 * @version 1.0b4
 */
public final class XSLTransform {

    
    /**
     * <p>
     * The compiled form of the XSLT stylesheet that this object
     * represents. This can be safely used across multiple threads
     * unlike a <code>Transformer</code> object.
     * </p>
     */
    private Templates   templates;  
    private NodeFactory factory = new NodeFactory();  
    
    
    // I could use one TransformerFactory field instead of local
    // variables but then I'd have to synchronize it; and it would
    // be hard to change the class used to transform

    
    /**
     * <p>
     *  Creates a new <code>XSLTransform</code> by
     *  reading the stylesheet from the specified source.
     * </p>
     *
     * @param source TrAX <code>Source</code> object from 
     *      which the input document is read
     * 
     * @throws XSLException when an IOException, format error, or
     *     something else prevents the stylesheet from being compiled 
     */ 
     private XSLTransform(Source source) throws XSLException {
         
        try {
            TransformerFactory factory 
              = TransformerFactory.newInstance();
            this.templates = factory.newTemplates(source);
        }
        catch (TransformerFactoryConfigurationError error) {
           throw new XSLException(
             "Could not locate a TrAX TransformerFactory", error
           );    
        } catch (TransformerConfigurationException ex) {
           throw new XSLException(
             "Syntax error in stylesheet", ex
           );    
        }
        
    }
    
    
    /**
     * <p>
     * Creates a new <code>XSLTransform</code> by reading the 
     * stylesheet from the specified input stream.
     * </p>
     *
     * @deprecated  This method does not provide a base URL for the
     * stylesheet so relative URLs used by xsl:import, xsl:include,
     * and the document() function cannot be resolved. Instead, use
     * a {@link nu.xom.Builder} to load the stylesheet and call
     * {@link #XSLTransform(Document)} instead.
     * 
     * @param stylesheet input stream from 
     *      which the stylesheet is read
     * 
     * @throws XSLException when an IOException, format error, or
     *     something else prevents the stylesheet from being compiled 
     */ 
    public XSLTransform(InputStream stylesheet) throws XSLException {
        this(new StreamSource(stylesheet));
    }

    
    /**
     * <p>
     * Creates a new <code>XSLTransform</code> by reading the 
     * stylesheet from the specified reader.
     * </p>
     * 
     * @deprecated  This method does not provide a base URL for the
     * stylesheet so relative URLs used by xsl:import, xsl:include,
     * and the document() function cannot be resolved. Instead, use
     * a {@link nu.xom.Builder} to load the stylesheet and call
     * {@link #XSLTransform(Document)} instead.
     * 
     * @param stylesheet character stream from which the stylesheet
     *     is read
     * 
     * @throws XSLException when an IOException, format error, or
     *     something else prevents the stylesheet from being compiled 
     */ 
    public XSLTransform(Reader stylesheet) throws XSLException {
        this(new StreamSource(stylesheet));
    }
  
    
    /**
     * <p>
     * Creates a new <code>XSLTransform</code> 
     * by reading the stylesheet from the specified file.
     * </p>
     *
     * @deprecated  Use a {@link nu.xom.Builder} to load the stylesheet
     *  and call {@link #XSLTransform(Document)} instead.
     *
     * @param stylesheet file from which the stylesheet is read
     * 
     * @throws XSLException when an IOException, format error, or
     *     something else prevents the stylesheet from being compiled 
     */ 
    public XSLTransform(File stylesheet) throws XSLException {
        this(new StreamSource(stylesheet));
    }
  
    
    /**
     * <p>
     * Creates a new <code>XSLTransform</code> by
     * reading the stylesheet from the specified document.
     * </p>
     * 
     * @param stylesheet document containing the stylesheet
     * 
     * @throws XSLException when the supplied document
     *      is not syntactically correct XSLT
     */ 
    public XSLTransform(Document stylesheet) throws XSLException {
        this(new XOMSource(stylesheet));
    }
  
    
    /**
     * <p>
     * Creates a new <code>XSLTransform</code> by
     * reading the stylesheet from the specified URL.
     * </p>
     *
     * @deprecated  Use a {@link nu.xom.Builder} to load the stylesheet
     *  and call {@link #XSLTransform(Document)} instead.
     *
     * @param systemID URL from which the stylesheet is read
     * 
     * @throws XSLException when an IOException, format error, or
     *     something else prevents the stylesheet from being compiled
     */ 
    public XSLTransform(String systemID) throws XSLException {
        this(new StreamSource(systemID));
    }
  
    
    /**
     * <p>
     * Creates a new <code>Nodes</code> from the
     * input <code>Document</code> by applying this object's
     * stylesheet. The original <code>Document</code> is not 
     * changed.
     * </p>
     *
     * @param in document to transform
     * 
     * @return a <code>Nodes</code> containing the result of the
     *     transformation
     * 
     * @throws XSLException if the transformation fails, normally
     *     due to an XSLT error
     */ 
    public Nodes transform(Document in) throws XSLException {
        return transform(new XOMSource(in));
    }
  
    
    /**
     * <p>
     * Creates a new <code>Nodes</code> object from the
     * input <code>Nodes</code> object by applying this object's
     * stylesheet. The original <code>Nodes</code> object is not 
     * changed.
     * </p>
     *
     * @param in document to transform
     * 
     * @return a <code>Nodes</code> containing the result of 
     *     the transformation
     * 
     * @throws XSLException if the transformation fails, normally
     *     due to an XSLT error
     */ 
    public Nodes transform(Nodes in) throws XSLException {
        
        if (in.size() == 0) return new Nodes();
        XOMSource source = new XOMSource(in);
        return transform(source);
        
    }

    
    /**
     * <p>
     * Creates a new <code>Nodes</code> object from the
     * input <code>Source</code> object by applying this object's
     * stylesheet. 
     * </p>
     *
     * @param in TrAX <code>Source</code> to transform
     * 
     * @return a <code>Nodes</code> object containing the result of 
     *     the transformation
     * 
     * @throws XSLException if the transformation fails, normally
     *     due to an XSLT error
     */ 
    private Nodes transform(Source in) throws XSLException {
        
        try {
            XOMResult out = new XOMResult(factory);
            Transformer transformer = templates.newTransformer();
            // work around Xalan bug
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.transform(in, out);
            return out.getResult();
        }
        catch (Exception ex) {
            // workaround bugs that wrap RuntimeExceptions
            Throwable cause = ex;
            if (cause instanceof TransformerException) {
                TransformerException tex = (TransformerException) cause;
                Throwable nested = tex.getException();
                if (nested != null) {
                    cause = nested;
                    if (cause instanceof SAXParseException) {
                        nested = ((SAXParseException) cause).getException();
                        if (nested != null) cause = nested;
                    }
                }
            }
            throw new XSLException(ex.getMessage(), cause);
        }  
        
    }
    
    
    /**
     * <p>
     * Sets the factory used to construct nodes in the output tree.
     * Passing null uses a 
     * <code>{@link nu.xom.NodeFactory nu.xom.NodeFactory}</code>.
     * </p>
     * 
     * @param factory the node factory used to construct
     *     nodes in the output tree
     */
    public void setNodeFactory(NodeFactory factory) {
        if (factory == null) this.factory = new NodeFactory();
        else this.factory = factory;
    }
    
    
    /**
     * <p>
     * Builds a <code>Document</code> object from a 
     * <code>Nodes</code> object. This is useful when the stylesheet
     * is known to produce a well-formed document with a single root 
     * element and no text or attribute nodes. If the stylesheet 
     * produces anything else, an <code>XMLException</code> is thrown.
     * </p>
     * 
     * @param nodes the nodes to be placed in the new document
     * 
     * @return a document containing the nodes
     * 
     * @throws XMLException if <code>nodes</code> does not contain
     *     exactly one element or if it contains any text nodes or
     *     attributes
     */
    public static Document toDocument(Nodes nodes) {
        
        Element root = null;
        int rootPosition = 0;
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i) instanceof Element) {
                rootPosition = i;
                root = (Element) nodes.get(i);
                break;
            }
        }
        
        if (root == null) {
            throw new XMLException("No root element");
        }
        
        Document result = new Document(root);
        
        for (int i = 0; i < rootPosition; i++) {
            result.insertChild(nodes.get(i), i);
        }
        
        for (int i = rootPosition+1; i < nodes.size(); i++) {
            result.appendChild(nodes.get(i));
        }
        
        return result;
        
    }
  
    
    /**
     * <p>
     *  Returns a string form of this <code>XSLTransform</code>, 
     *  suitable for debugging.
     * </p>
     *
     * @return debugging string
     */
    public String toString() {
        return "[" + getClass().getName() + ": " + templates + "]";   
    }
  
    
}