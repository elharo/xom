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

import java.net.MalformedURLException;
import java.net.URL;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/**
 * <p>
 *   This class is used to make sure that relative URLs for 
 *   system IDs are resolved relative to the document's base 
 *   URI instead of the current working directory.
 * </p>
 * 
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0d11
 *
 */
class BaseRelativeResolver implements EntityResolver {

    private URL base;

   public BaseRelativeResolver(String base) {
        
        if (base == null) return;
        try {
            this.base = new URL(base);
        }
        catch (MalformedURLException ex) {
          // can't do anything; just use defaults for all resolutions
        }
    }


    /**
     * @see org.xml.sax.EntityResolver#resolveEntity(String, String)
     */
    public InputSource resolveEntity(
      String publicID, String systemID) {

        if (base == null) return null;
        try {
            URL absolute = new URL(base, systemID);   
            return new InputSource(absolute.toExternalForm());
        }
        catch (MalformedURLException ex) { 
            // NullPointerPointerException or MalformedURLException
            return null;
        }

    }

}
