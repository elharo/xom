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

package nu.xom.xslt;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;

import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import nu.xom.Document;
import nu.xom.Nodes;

/**
 * <p>
 * This class serves as an interface to a 
 * TrAX aware XSLT processor such
 * as Xalan or Saxon.
 * </p>
 * 
 * <p>
 * The following example shows how to apply an XSL Transformation
 * to a XOM document and get the transformation result in the form
 * of a XOM <code>Nodes</code>:</p>
 * <blockquote><pre>
 * public static Nodes transform(Document in) 
 *   throws XSLException {
 *     XSLTransform stylesheet = new XSLTransform("mystylesheet.xsl");
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
 *   <li>
 *     Saxon 6.x: <code>com.icl.saxon.TransformerFactoryImpl</code>
 *   </li>
 *  <li>
 *    Saxon 7.x: <code>net.sf.saxon.TransformerFactoryImpl</code>
 *  </li>
 *  <li>Xalan: 
 *    <code>org.apache.xalan.processor.TransformerFactoryImpl</code>
 *  </li>
 *  <li>jd.xslt: 
 *    <code>jd.xml.xslt.trax.TransformerFactoryImpl</code>
 *  </li>
 *  <li>Oracle: 
 *     <code>oracle.xml.jaxp.JXSAXTransformerFactory</code>
 *   </li>
 *  </ul>
 *  <p>
 *   This property can be set in all the usual ways a Java system 
 *   property can be set. TrAX picks from them in this order:</p>
 *   <ol>
 *   <li> Invoking <code>System.setProperty( 
 *     "javax.xml.transform.TransformerFactory", 
 *     "<i><code>classname</code></i>")</code></li>
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
 *    this is Xalan 2.2d10. In Java 1.4.2, this is Xalan 2.4. </li>
 *    </ol>
 *
 *
 * @author Elliotte Rusty Harold
 * @version 1.0d22
 */
public final class XSLTransform {

  /**
   * <p>
   * The compiled form of the XSLT stylesheet that this object
   * represents. This can be safely used across multiple threads
   * unlike a <code>Transformer</code> object.
   * </p>
   */
    private Templates templates;  
    
    // I could use one TransformerFactory field instead of local
    // variables but then I'd have to synchronize it; and it would
    // be hard to change the class used to transform

    /**
     * <p>
     *  This will create a new <code>XSLTransform</code> by
     *  reading the stylesheet from the specified <code>Source</code>.
     * </p>
     *
     * @param source TrAX <code>Source</code> from 
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
     *   This will create a new <code>XSLTransform</code> by
     *   reading the stylesheet from the specified 
     *   <code>InputStream</code>.
     * </p>
     *
     * @param stylesheet <code>InputStream</code> from 
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
     *   This will create a new <code>XSLTransform</code> by
     *   reading the stylesheet from the specified 
     *   <code>Reader</code>.
     * </p>
     *
     * @param stylesheet <code>Reader</code> from which the stylesheet
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
     * This will create a new <code>XSLTransform</code> 
     * by reading the stylesheet from the specified <code>File</code>.
     * </p>
     *
     * @param stylesheet <code>File</code> from which the 
     *      stylesheet is read
     * 
     * @throws XSLException when an IOException, format error, or
     * something else prevents the stylesheet from being compiled 
     */ 
    public XSLTransform(File stylesheet) throws XSLException {
        this(new StreamSource(stylesheet));
    }
  
    /**
     * <p>
     * This will create a new <code>XSLTransform</code> by
     * reading the stylesheet from the specified <code>Document</code>.
     * </p>
     *
     * @param stylesheet <code>Document</code> containing 
     *      the stylesheet
     * 
     * @throws XSLException when the supplied <code>Document</code>
     *      is not syntactically correct XSLT
     */ 
    public XSLTransform(Document stylesheet) throws XSLException {
        this(new XOMSource(stylesheet));
    }
  
    /**
     * <p>
     * This will create a new <code>XSLTransform</code> by
     *  reading the stylesheet from the specified URL.
     * </p>
     *
     * @param systemID URL from which the stylesheet is read
     * 
     * @throws XSLException when an IOException, format error, or
     *      something else prevents the stylesheet from being compiled
     */ 
    public XSLTransform(String systemID) throws XSLException {
        this(new StreamSource(systemID));
    }
  
    /**
     * <p>
     * This will create a new <code>Nodes</code> from the
     * input <code>Document</code> by applying this object's
     * stylesheet. The original <code>Document</code> is not 
     * changed.
     * </p>
     *
     * @param in <code>Document</code> to transform
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
     * This will create a new <code>Nodes</code> from the
     * input <code>Nodes</code> by applying this object's
     * stylesheet. The original <code>Nodes</code> is not 
     * changed.
     * </p>
     *
     * @param in <code>Document</code> to transform
     * 
     * @return a <code>Nodes</code> containing the result of 
     *     the transformation
     * 
     * @throws XSLException if the transformation fails, normally
     *     due to an XSLT error
     */ 
    public Nodes transform(Nodes in) throws XSLException {
        return transform(new XOMSource(in));
    }

    /**
     * <p>
     * This will create a new <code>Nodes</code> from the
     * input <code>Source</code> by applying this object's
     * stylesheet. The original <code>Source</code> is not 
     * changed.
     * </p>
     *
     * @param in TrAX <code>Source</code> to transform
     * 
     * @return a <code>Nodes</code> containing the result of 
     *     the transformation
     * 
     * @throws XSLException if the transformation fails, normally
     *     due to an XSLT error
     */ 
    private Nodes transform(Source in) throws XSLException {
        try {
            XOMResult out = new XOMResult();
            Transformer transformer = templates.newTransformer();
            transformer.transform(in, out);
            return out.getResult();
        }
        catch (TransformerException ex) {
            throw new XSLException("XSLT Transformation failed", ex);
        }
    }
  
    /**
     * <p>
     *  This returns a <code>String</code> form of this
     *  <code>XSLTransform</code>, suitable for debugging.
     * </p>
     *
     * @return debugging string
     */
    public String toString() {
        return "[" + getClass().getName() + ": " + templates + "]";   
    }
  
}