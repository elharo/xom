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

/**
 * <p>
 * <code>XIncludeException</code> is the generic superclass
 * for all checked exceptions that may be thrown as a result
 * of a violation of XPointer grammar.
 * </p>
 *
 * @author Elliotte Rusty Harold
 * @version 1.0d15
 */
class XPointerSyntaxException extends Exception {

    private Throwable rootCause = null;

    /**
     * <p>
     * Constructs an <code>XPointerSyntaxException</code> 
     * with <code>null</code> as its error detail message.
     * </p>
     */
    public XPointerSyntaxException() {}

    /**
     * Constructs an <code>XPointerSyntaxException</code> with the specified 
     * detail message. The error message string <code>message</code> 
     * can later be retrieved by the 
     * <code>{@link java.lang.Throwable#getMessage}</code>
     * method of class <code>java.lang.Throwable</code>.
     *
     * @param message the detail message.
     */
    public XPointerSyntaxException(String message) {
        super(message);
    }

    /**
     * When an <code>StringIndexOutOfBoundsexception</code>,  
     * or other generic exception 
     * is thrown while processing an XML document
     * for XIncludes, it is customarily replaced
     * by some form of <code>XIncludeException</code>.  
     * This method allows you to store the original exception.
     *
     * @param nestedException the underlying exception which 
     *     caused the <code>XPointerSyntaxException</code> to be thrown
     */
    public void setRootCause(Throwable nestedException) {
        this.rootCause = nestedException;     
    }

    /**
     * <p>
     * When an <code>IOException</code>,  
     * <code>MalformedURLException</code>, or other generic  
     * exception is thrown while processing an XML document
     * for XIncludes, it is customarily replaced
     * by some form of <code>XPointerSyntaxException</code>.  
     * This method allows you to retrieve the original exception.
     * It returns null if no such exception caused this 
     * <code>XPointerSyntaxException</code>.
     *</p>
     * 
     * @return the underlying exception which 
           caused the XIncludeException to be thrown
     */
    public Throwable getRootCause() {
        return this.rootCause;     
    }

}
