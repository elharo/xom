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

import java.io.UnsupportedEncodingException;
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
        catch (Throwable t) {
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
    

    // XXX remove dependence on Xerces URI class
    static String absolutize(String base, String spec) {
        
        if (!isAbsolute(base)) {
           // Xerces can't handle two relative URIs so use Java instead
           return absolutizeWithJava14(base, spec);
        }
        try {
            URI u = new URI(base);
            URI resolved = new URI(u, spec);
            return resolved.toString();
        } 
        catch (Exception ex) {
            throw new nu.xom.MalformedURIException("bad");
        } 
        catch (NoClassDefFoundError error) {
            return absolutizeWithJava14(base, spec);
        } 
     
    }


    static String toURI(String iri) {
    
        StringBuffer uri = new StringBuffer(iri.length());
        int length = iri.length();
        for (int i = 0; i < length; i++) {
            char c = iri.charAt(i);
            switch(c) {
                case ' ':  
                    uri.append("%20");
                    break;
                case '!': 
                    uri.append(c);
                    break;
                case '"': 
                    uri.append("%22");
                    break;
                case '#':  
                    uri.append(c);
                    break;
                case '$':  
                    uri.append(c);
                    break;
                case '%':  
                    uri.append(c);
                    break;
                case '&':  
                    uri.append(c);
                    break;
                case '\'':  
                    uri.append(c);
                    break;
                case '(':  
                    uri.append(c);
                    break;
                case ')':  
                    uri.append(c);
                    break;
                case '*':  
                    uri.append(c);
                    break;
                case '+':  
                    uri.append(c);
                    break;
                case ',':  
                    uri.append(c);
                    break;
                case '-':  
                    uri.append(c);
                    break;
                case '.':  
                    uri.append(c);
                    break;
                case '/':  
                    uri.append(c);
                    break;
                case '0':  
                    uri.append(c);
                    break;
                case '1':  
                    uri.append(c);
                    break;
                case '2':  
                    uri.append(c);
                    break;
                case '3':  
                    uri.append(c);
                    break;
                case '4':  
                    uri.append(c);
                    break;
                case '5':  
                    uri.append(c);
                    break;
                case '6':  
                    uri.append(c);
                    break;
                case '7':  
                    uri.append(c);
                    break;
                case '8':  
                    uri.append(c);
                    break;
                case '9':  
                    uri.append(c);
                    break;
                case ':':  
                    uri.append(c);
                    break;
                case ';':  
                    uri.append(c);
                    break;
                case '<':  
                    uri.append("%3C");
                    break;
                case '=':  
                    uri.append(c);
                    break;
                case '>':  
                    uri.append("%3E");
                    break;
                case '?':  
                    uri.append(c);
                    break;
                case '@':  
                    uri.append(c);
                    break;
                case 'A':  
                    uri.append(c);
                    break;
                case 'B':  
                    uri.append(c);
                    break;
                case 'C':  
                    uri.append(c);
                    break;
                case 'D':  
                    uri.append(c);
                    break;
                case 'E':  
                    uri.append(c);
                    break;
                case 'F':  
                    uri.append(c);
                    break;
                case 'G':  
                    uri.append(c);
                    break;
                case 'H':  
                    uri.append(c);
                    break;
                case 'I':  
                    uri.append(c);
                    break;
                case 'J':  
                    uri.append(c);
                    break;
                case 'K':  
                    uri.append(c);
                    break;
                case 'L':  
                    uri.append(c);
                    break;
                case 'M':  
                    uri.append(c);
                    break;
                case 'N':  
                    uri.append(c);
                    break;
                case 'O':  
                    uri.append(c);
                    break;
                case 'P':  
                    uri.append(c);
                    break;
                case 'Q':  
                    uri.append(c);
                    break;
                case 'R':  
                    uri.append(c);
                    break;
                case 'S':  
                    uri.append(c);
                    break;
                case 'T':  
                    uri.append(c);
                    break;
                case 'U':  
                    uri.append(c);
                    break;
                case 'V':  
                    uri.append(c);
                    break;
                case 'W':  
                    uri.append(c);
                    break;
                case 'X':  
                    uri.append(c);
                    break;
                case 'Y':  
                    uri.append(c);
                    break;
                case 'Z':  
                    uri.append(c);
                    break;
                case '[':  
                    uri.append(c);
                    break;
                case '\\':  
                    uri.append("%5C");
                    break;
                case ']':  
                    uri.append(c);
                    break;
                case '^':  
                    uri.append("%5E");
                    break;
                case '_':  
                    uri.append(c);
                    break;
                case '`':  
                    uri.append("%60");
                    break;
                case 'a':  
                    uri.append(c);
                    break;
                case 'b':  
                    uri.append(c);
                    break;
                case 'c':  
                    uri.append(c);
                    break;
                case 'd':  
                    uri.append(c);
                    break;
                case 'e':  
                    uri.append(c);
                    break;
                case 'f':  
                    uri.append(c);
                    break;
                case 'g':  
                    uri.append(c);
                    break;
                case 'h':  
                    uri.append(c);
                    break;
                case 'i':  
                    uri.append(c);
                    break;
                case 'j':  
                    uri.append(c);
                    break;
                case 'k':  
                    uri.append(c);
                    break;
                case 'l':  
                    uri.append(c);
                    break;
                case 'm':  
                    uri.append(c);
                    break;
                case 'n':  
                    uri.append(c);
                    break;
                case 'o':  
                    uri.append(c);
                    break;
                case 'p':  
                    uri.append(c);
                    break;
                case 'q':  
                    uri.append(c);
                    break;
                case 'r':  
                    uri.append(c);
                    break;
                case 's':  
                    uri.append(c);
                    break;
                case 't':  
                    uri.append(c);
                    break;
                case 'u':  
                    uri.append(c);
                    break;
                case 'v':  
                    uri.append(c);
                    break;
                case 'w':  
                    uri.append(c);
                    break;
                case 'x':  
                    uri.append(c);
                    break;
                case 'y':  
                    uri.append(c);
                    break;
                case 'z':  
                    uri.append(c);
                    break;
                case '{':  
                    uri.append("%7B");
                    break;
                case '|':  
                    uri.append("%7C");
                    break;
                case '}':  
                    uri.append("%7D");
                    break;
                case '~':  
                    uri.append(c);
                    break;
                default: 
                    uri.append(percentEscape(c));
            }
        }    
        return uri.toString();
        
    }

    
    private static String percentEscape(char c) {
        
        StringBuffer result = new StringBuffer(3);
        String s = String.valueOf(c);
        try {
            byte[] data = s.getBytes("UTF8");
            for (int i = 0; i < data.length; i++) {
                result.append('%');
                String hex = Integer.toHexString(data[i]);
                result.append(hex.substring(hex.length()-2));
            }
            return result.toString();
        }
        catch (UnsupportedEncodingException ex) {
            throw new RuntimeException( 
              "Broken VM: does not recognize UTF-8 encoding");   
        }
        
    }

    
}
