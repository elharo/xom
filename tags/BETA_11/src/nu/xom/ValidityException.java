/* Copyright 2002-2004 Elliotte Rusty Harold
   
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

package nu.xom;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.SAXParseException;

/**
 * <p>
 *  Signals a validity error in a document being parsed.
 *  These are not thrown by default, unless you specifically 
 *  request that the builder validate.
 * </p>

 * @author Elliotte Rusty Harold
 * @version 1.0a5
 *
 */
public class ValidityException extends ParsingException {
    
    private List saxExceptions = new ArrayList();
    private transient Document document;
    

    /**
     * <p>
     * Creates a new <code>ValidityException</code> 
     * with a detail message and an underlying root cause.
     * </p>
     * 
     * @param message a string indicating the specific problem
     * @param cause the original cause of this exception
     */
    public ValidityException(String message, Throwable cause) {
        super(message, cause);
    }

    
    /**
     * <p>
     * Creates a new <code>ValidityException</code> 
     * with a detail message and line and column numbers.
     * </p>
     * 
     * @param message a string indicating the specific problem
     * @param lineNumber the approximate line number 
     *     where the problem occurs
     * @param columnNumber the approximate column number
     *     where the problem occurs
     */
    public ValidityException(
        String message,
        int lineNumber,
        int columnNumber) {
        super(message, lineNumber, columnNumber);
    }

    
    /**
     * <p>
     * Creates a new <code>ValidityException</code> 
     * with a detail message, line and column numbers, 
     * and an underlying exception.
     * </p>
     * 
     * @param message a string indicating the specific problem
     * @param lineNumber the approximate line number 
     *     where the problem occurs
     * @param columnNumber the approximate column number 
     *     where the problem occurs
     * @param cause the original cause of this exception
     */
    public ValidityException(
        String message,
        int lineNumber,
        int columnNumber,
        Throwable cause) {
        super(message, lineNumber, columnNumber, cause);
    }

    
    /**
     * <p>
     * Creates a new <code>ValidityException</code> 
     * with a detail message, the URI of the document that contained
     * the error, and approximate line and column numbers of the
     * first validity error.
     * </p>
     * 
     * @param message a string indicating the specific problem
     * @param lineNumber the approximate line number 
     *     where the problem occurs
     * @param columnNumber the approximate column number
     *     where the problem occurs
     */
    public ValidityException(String message, String uri,
      int lineNumber, int columnNumber) {
        super(message, uri, lineNumber, columnNumber);
    }

    
    /**
     * <p>
     * Creates a new <code>ValidityException</code> 
     * with a detail message, URI of the document containing the
     * validity error, line and column numbers of the error, 
     * and an underlying exception.
     * </p>
     * 
     * @param message a string indicating the specific problem
     * @param lineNumber the approximate line number 
     *     where the problem occurs
     * @param columnNumber the approximate column number 
     *     where the problem occurs
     * @param cause the original cause of this exception
     */
    public ValidityException(
        String message,
        String uri,
        int lineNumber,
        int columnNumber,
        Throwable cause) {
        super(message, uri, lineNumber, columnNumber, cause);
    }
    
    
    /**
     * <p>
     * Creates a new <code>ValidityException</code> 
     * with a detail message.
     * </p>
     * 
     * @param message a string indicating the specific problem
     */
    public ValidityException(String message) {
        super(message);
    }

    
    /**
     * <p>
     * Returns a <code>Document</code> object for the document that
     * caused this exception. This is useful if you want notification
     * of validity errors, but nonetheless wish to further process 
     * the invalid document.
     * </p>
     * 
     * @return the invalid document
     */
    public Document getDocument() {
        return document;    
    }
    
    
    void setDocument(Document doc) {
        this.document = doc;
    }
    
    
    void addError(SAXParseException ex) {
        saxExceptions.add(ex);
    }
    
    
    /**
     * <p>
     *   Returns the number of validity errors the parser detected
     *   in the document. This is likely to not be consistent from one
     *   parser to another.
     * </p>
     * 
     * @return the number of validity errors the parser detected
     */
    public int getErrorCount() {
        return saxExceptions.size();   
    }
    
    
    /**
     * <p>
     *   Returns a message indicating a specific validity problem
     *   in the input document as detected by the parser. Normally,
     *   these will be in the order they appear in the document.
     *   For instance, an error in the root element is likely
     *   to appear before an error in a child element. However, this
     *   depends on the underlying parser and is not guaranteed.
     * </p>
     * 
     * @param n the index of the validity error to report
     * 
     * @return a string describing the n<i>th</i> validity error
     * 
     * @throws IndexOutOfBoundsException if <code>n</code> is greater
     *     than or equal to the number of errors detected
     */
    public String getValidityError(int n) {
        Exception ex = (Exception) saxExceptions.get(n); 
        return ex.getMessage();  
    }

    
    /**
     * <p>
     *   Returns the line number of the <i>n</i>th validity
     *   error. It returns -1 if this is not known. This number
     *   may be helpful for debugging, but should not be relied on.
     *   Different parsers may set it differently. For instance 
     *   a problem with an element might be reported using the 
     *   line number of the start-tag or the line number of the 
     *   end-tag. 
     * </p>
     * 
     * @param n the index of the validity error to report
     * @return the approximate line number where the n<i>th</i> 
     *     validity error was detected
     * 
     * @throws IndexOutOfBoundsException if <code>n</code> is greater
     *     than or equal to the number of errors detected
     */
    public int getLineNumber(int n) {
        SAXParseException ex = (SAXParseException) saxExceptions.get(n);
        return ex.getLineNumber();  
    }

    
    /**
     * <p>
     *   Returns the column number of the <i>n</i>th validity
     *   error. It returns -1 if this is not known. This number
     *   may be helpful for debugging, but should not be relied on.
     *   Different parsers may set it differently. For instance 
     *   a problem with an element might be reported using the 
     *   column of the <code>&lt;</code> or the <code>&gt;</code>
     *   of the start-tag 
     * </p>
     * 
     * @param n the index of the validity error to report
     * 
     * @return the approximate column where the n<i>th</i> 
     *     validity error was detected
     * 
     * @throws IndexOutOfBoundsException if <code>n</code> is greater
     *     than or equal to the number of errors detected
     */
    public int getColumnNumber(int n) {
        SAXParseException ex = (SAXParseException) saxExceptions.get(n);
        return ex.getColumnNumber();  
    }

    
}
