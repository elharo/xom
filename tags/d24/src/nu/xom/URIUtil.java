// Copyright 2004 Elliotte Rusty Harold
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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.xerces.util.URI;

/**
 * Prefer URI resolution to be done by Xerces if available;
 * Java 1.4 if Xerces isn't available, and java.net.URL
 * as a last-ditch fallback.
 */
class URIUtil {

    
    static boolean isOpaque(String uri) {
        
        int colon = uri.indexOf(':');
        if (colon < 1) return false;
        if (uri.substring(colon+1).startsWith("//")) return false;
        // Java seems to occasionally use URLs for file scheme
        // that look like file:/home/elharo/...
        // According to RFC 2396 these are opaque, but we can't treat 
        // them that way. 
        if (uri.startsWith("file:/")) return false;
        if (!Verifier.isAlpha(uri.charAt(0))) return false;
        for (int i = 1; i < colon; i++) {
             if (!Verifier.isSchemeCharacter(uri.charAt(i))) return false;
        }
        return true;
        
    }

    
    static boolean isAbsolute(String uri) {
        
        int colon = uri.indexOf(':');
        if (colon < 1) return false;
        if (!Verifier.isAlpha(uri.charAt(0))) return false;
        for (int i = 1; i < colon; i++) {
             if (!Verifier.isSchemeCharacter(uri.charAt(i))) return false;
        }
        return true;
        
    }

    
    // Prefer Xerces resolution if available. It's less buggy.
    static String absolutizeWithJava14(String base, String spec) {
        
        // Trying to avoid dependence on Java 1.4
        try {
            Class javanetURI = Class.forName("java.net.URI");
            Class[] params = {String.class};
            Constructor constructor = javanetURI.getConstructor(params);
            Object[] args = {base};
            Object resolver = constructor.newInstance(args);
            Method resolve = javanetURI.getMethod("resolve", params);
            String[] args2 = {spec};
            Object result = resolve.invoke(resolver, args2);
            return result.toString();
        }
        catch (InvocationTargetException e) {
            MalformedURIException ex = new MalformedURIException(e.getMessage(), e);
            throw ex;
        } 
        catch (Throwable e) {
            try {
                // fallback to java.net.URL
                URL u = new URL(new URL(base), spec);
                return u.toExternalForm();
            }
            catch (MalformedURLException ex) {
                throw new MalformedURIException(ex.getMessage()); 
            }
        }        
        
    }
    

    static String absolutize(String base, String spec) {
        
        try {
            URI u = new URI(base);
            URI resolved = new URI(u, spec);
            return resolved.toString();
        } 
        catch (Exception e) {
            throw new nu.xom.MalformedURIException("bad");
        } 
        catch (NoClassDefFoundError error) {
            return absolutizeWithJava14(base, spec);
        } 
     
    }


}
