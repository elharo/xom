/* Copyright 2004 Elliotte Rusty Harold
   
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

package nu.xom.xslt;

import java.io.IOException;

import nu.xom.converters.SAXConverter;

import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;

/**
 * 
 * <p>
 * This is just for XSLTransform, and implements only the functionality
 * that class requires. Other classes should not use this.
 * It is far from a conformant implementation of XMLReader. 
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0b7
 *
 */
class XOMReader implements XMLReader {

    private SAXConverter converter;
        
    public boolean getFeature(String uri) 
      throws SAXNotRecognizedException, SAXNotSupportedException {
        
        if ("http://xml.org/sax/features/namespace-prefixes".equals(uri)
          || "http://xml.org/sax/features/namespaces".equals(uri)) {
            return true;   
        }
        throw new SAXNotRecognizedException("XOMReader doesn't support features");
        
    }

    
    public void setFeature(String uri, boolean value)
      throws SAXNotRecognizedException, SAXNotSupportedException {

    }

    
    public Object getProperty(String uri) throws SAXNotRecognizedException,
            SAXNotSupportedException {
        
        if ("http://xml.org/sax/properties/lexical-handler".equals(uri)) {
            return converter.getLexicalHandler();
        }
        else {
            throw new SAXNotRecognizedException("XOMReader doesn't support features");
        }
        
    }

    
    public void setProperty(String uri, Object value)
      throws SAXNotRecognizedException, SAXNotSupportedException {

        if ("http://xml.org/sax/properties/lexical-handler".equals(uri)) {
            LexicalHandler handler = (LexicalHandler) value;
            converter.setLexicalHandler(handler);
        }
        else {
            throw new SAXNotRecognizedException(
              "XOMReader doesn't support " + uri);
        }

    }

    
    public void setEntityResolver(EntityResolver resolver) {
        throw new UnsupportedOperationException();
    }

    
    public EntityResolver getEntityResolver() {
        return null;
    }

    
    public void setDTDHandler(DTDHandler handler) {
        // throw new UnsupportedOperationException();
    }

    
    public DTDHandler getDTDHandler() {
        return null;
    }

    
    public void setContentHandler(ContentHandler handler) {
        converter = new SAXConverter(handler);
    }

    
    public ContentHandler getContentHandler() {
        return null;
    }

    
    public void setErrorHandler(ErrorHandler handler) {
    }

    
    public ErrorHandler getErrorHandler() {
        return null;
    }

    
    public void parse(InputSource source) 
      throws IOException, SAXException {
        
        XOMInputSource xis = (XOMInputSource) source;
        converter.convert(xis.getNodes());
        
    }

    
    public void parse(String url) throws IOException, SAXException {
        throw new UnsupportedOperationException();
    }

    
}
