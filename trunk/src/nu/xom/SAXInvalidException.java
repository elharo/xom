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

/**
 * <p>
 *   This class just exists because I need a RuntimeException
 *   to throw out of the SAX error() method.
 * </p>
 * 
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0d14
 *
 */
class SAXInvalidException extends XMLException {

    private int lineNumber;
    private int columnNumber;

    public SAXInvalidException(String message, int lineNumber, 
      int columnNumber, Exception parent) {
        super(message, parent);
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
    }
    
    public int getLineNumber() {
        return this.lineNumber;
    }

    public int getColumnNumber() {
        return this.columnNumber;
    }

}
