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


class URIUtil {

    
    static boolean isOpaque(String uri) {
        
        int colon = uri.indexOf(':');
        if (colon < 1) return false;
        // This next line is the difference between absolute and opaque
        if (uri.substring(colon+1).startsWith("/")) return false;
        if (!Verifier.isAlpha(uri.charAt(0))) return false;
        for (int i = 1; i < colon; i++) {
             if (!Verifier.isSchemeCharacter(uri.charAt(i))) {
                 return false;
             }
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
    
    
    // This doesn't do enough error checking to be a public API.
    static String absolutize(String baseURI, String spec) {
        
        if ("".equals(baseURI) || baseURI == null) return spec;
        
        ParsedURI base = new ParsedURI(baseURI);
        ParsedURI R = new ParsedURI(spec);
        ParsedURI T = new ParsedURI();
        
        if (R.scheme != null) {
            T.scheme    = R.scheme;
            T.authority = R.authority;
            T.query     = R.query;
            T.path      = removeDotSegments(R.path); 
        }
        else {
            if (R.authority != null) {
                T.authority = R.authority;
                T.query     = R.query;
                T.path      = removeDotSegments(R.path); 
            }
            else {
                if ("".equals(R.path)) {
                    T.path = base.path;
                    if (R.query != null) {
                        T.query = R.query;
                    }
                    else {
                        T.query = base.query;
                    }
                }
                else {
                    if (R.path.startsWith("/")) {
                       T.path = removeDotSegments(R.path);
                    }
                    else {
                       T.path = merge(base, R.path);
                       T.path = removeDotSegments(T.path);
                    }
                    T.query = R.query;
                }
                T.authority = base.authority;
            }
            T.scheme = base.scheme;
        }
        // Fragment ID of base URI is never considered
        T.fragment = R.fragment; 
        
        return T.toString();
        
    }
    
    
    private static String merge(ParsedURI base, String relativePath) {
    
        if (base.authority != null && "".equals(base.path) 
          && !"".equals(base.authority)) {
            return "/" + relativePath;
        }
    
        int lastSlash = base.path.lastIndexOf('/');
        if (lastSlash == -1) return relativePath;
        String topPath = base.path.substring(0, lastSlash+1);
        return topPath + relativePath;
        
    }
    
    
    private static String removeDotSegments(String path) {
    
        String output = "";

        while (path.length() > 0) {
            if (path.startsWith("../")) {
                path = path.substring(3);
            }
            else if (path.startsWith("./")) {
                path = path.substring(2);
            }
            else if (path.startsWith("/./")) {
                path = '/' + path.substring(3);
            }
            else if (path.equals("/.")) {
                path = "/";
            }
            else if (path.startsWith("/../")) {
                path = '/' + path.substring(4);
                int lastSlash = output.lastIndexOf('/');
                if (lastSlash != -1) output = output.substring(0, lastSlash);
            }
            else if (path.equals("/..")) {
                path = "/";
                int lastSlash = output.lastIndexOf('/');
                if (lastSlash != -1) {
                    output = output.substring(0, lastSlash);
                }
            }
            else if (path.equals(".") || path.equals("..")) {
                path = "";
            }
            else {
                int nextSlash = path.indexOf('/');
                if (nextSlash == 0) nextSlash = path.indexOf('/', 1);
                if (nextSlash == -1) {
                    output += path;
                    path = "";
                }
                else {
                    output += path.substring(0, nextSlash);
                    path = path.substring(nextSlash);
                }
            }
        }
        
        return output;
        
    }


    // really just a struct
    static class ParsedURI {
     
        String scheme;
        String schemeSpecificPart;
        String query;
        String fragment;
        String authority;
        String path = "";
        
        ParsedURI(String spec) {
            
            int colon = spec.indexOf(':');
            int question = -1;
            
            // URIs can only contain one sharp sign
            int sharp = spec.lastIndexOf('#'); 
            
            // Fragment IDs can contain question marks so we only read 
            // the question mark before the fragment ID, if any
            if (sharp == -1) question = spec.indexOf('?');
            else question = spec.substring(0, sharp).indexOf('?');

            if (colon != -1) scheme = spec.substring(0, colon);
            
            if (question == -1 && sharp == -1) {
                schemeSpecificPart = spec.substring(colon+1);
            }
            else if (question != -1) {
                if (question < colon) {
                    MalformedURIException ex = new MalformedURIException("Unparseable URI");
                    ex.setData(spec);
                    throw ex;
                }
                schemeSpecificPart = spec.substring(colon+1, question);                
            }
            else {
                if (sharp < colon) {
                    MalformedURIException ex 
                      = new MalformedURIException("Unparseable URI");
                    ex.setData(spec);
                    throw ex;
                }
                schemeSpecificPart = spec.substring(colon+1, sharp);                
            }
            
            if (sharp != -1) {
                fragment = spec.substring(sharp+1);
            }
            
            if (question != -1) {
                if (sharp == -1) {
                    query = spec.substring(question+1);
                }
                else {
                    query = spec.substring(question+1, sharp);
                }
            }

            if (schemeSpecificPart.startsWith("//")) {
                int authorityBegin = 2;
                int authorityEnd = schemeSpecificPart.indexOf('/', authorityBegin);
                if (authorityEnd == -1) {
                    authority = schemeSpecificPart.substring(2);
                    path = "";
                }
                else {
                    authority = schemeSpecificPart.substring(authorityBegin, authorityEnd);
                    path = schemeSpecificPart.substring(authorityEnd);
                } 
            }
            else {
                path = schemeSpecificPart;
            }
            
        }

        ParsedURI() {}
        
        public String toString() {
        
            StringBuffer result = new StringBuffer(20);
            if (scheme != null) result.append(scheme + ':');
            if (schemeSpecificPart != null) {
                result.append(schemeSpecificPart);
            }
            else {
                result.append("//");
                if (authority != null) result.append(authority);
                result.append(path);
            }
            
            if (query != null) result.append('?' + query);
            if (fragment != null) result.append('#' + fragment);
            
            return result.toString();
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

    
    static String percentEscape(char c) {
        
        StringBuffer result = new StringBuffer(3);
        String s = String.valueOf(c);
        try {
            byte[] data = s.getBytes("UTF8");
            for (int i = 0; i < data.length; i++) {
                result.append('%');
                String hex = Integer.toHexString(data[i]).toUpperCase();
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
