/* Copyright 2011 Elliotte Rusty Harold
   
   This library is free software; you can redistribute it and/or modify
   it under the terms of version 2.1 of the GNU Lesser General Public 
   License as published by the Free Software Foundation.
   
   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
   GNU Lesser General Public License for more details.
   
   You should have received a copy of the GNU Lesser General Public
   License along with this library. If not, see
   <https://www.gnu.org/licenses/>.
   
   You can contact Elliotte Rusty Harold by sending e-mail to
   elharo@ibiblio.org. Please include the word "XOM" in the
   subject line. The XOM home page is located at https://xom.nu/
*/

package nu.xom.canonical;

import nu.xom.MalformedURIException;


/**
 * This class implements the modified RFC 3986 algorithm for URI merging 
 * found in section 2.4 of the canonical XML 1.1 specification
 * http://www.w3.org/TR/xml-c14n11/
 * 
 * @author Elliotte Rusty Harold
 * @version 1.2.7
 *
 */
class URIJoiner {
    
    // This doesn't do enough error checking to be a public API.
    static String absolutize(String baseURI, String spec) {
        
        if ("".equals(baseURI) || baseURI == null) return spec;
        
        ParsedURI base = new ParsedURI(baseURI);
        
        // This seems to be necessary to handle base URLs like
        // http://www.example.com/test/data/..
        // but I don't think it's part of the 3986 algorithm. 
        // ???? It may be a bug in that algorithm. Check.
        if (base.path.endsWith("/..")) base.path += '/';
        
        // The variable names R and T violate Java naming conventions.
        // They are taken from the pseudo-code in the RFC 3986 spec.
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
    
    
    static String merge(ParsedURI base, String relativePath) {
    
        if (base.authority != null && "".equals(base.path) 
          && !"".equals(base.authority)) {
            return "/" + relativePath;
        }
    
        int lastSlash = base.path.lastIndexOf('/');
        if (lastSlash == -1) return relativePath;
        String topPath = base.path.substring(0, lastSlash+1);
        return topPath + relativePath;
        
    }
    
    
    static String removeDotSegments(String path) {
    
    	StringBuilder output = new StringBuilder();

        while (path.length() > 0) {
            if (path.startsWith("/./")) {
                path = '/' + path.substring(3);
            }
            else if (path.equals("/.")) {
                path = "/";
            }
 /*           else if (path.startsWith("/../")) {
                path = '/' + path.substring(4);
                int lastSlash = output.toString().lastIndexOf('/');
                if (lastSlash != -1) output.setLength(lastSlash);
            }*/
            else if (path.equals("/..")) {
                path = "/";
                int lastSlash = output.toString().lastIndexOf('/');
                if (lastSlash != -1) output.setLength(lastSlash);
            }
            else if (path.equals(".") /*|| path.equals("..")*/) {
                path = "";
            }
            /*else if (path.startsWith("../")) {
                path = path.substring(3);
            }*/
            else if (path.startsWith("./")) {
                path = path.substring(2);
            }
            else {
                int nextSlash = path.indexOf('/');
                if (nextSlash == 0) nextSlash = path.indexOf('/', 1);
                if (nextSlash == -1) {
                    output.append(path);
                    path = "";
                }
                else {
                    output.append(path.substring(0, nextSlash));
                    path = path.substring(nextSlash);
                }
            }
        }
        
        return output.toString();
        
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
            int question;
            
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
                    MalformedURIException ex 
                      = new MalformedURIException("Unparseable URI");
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
        
        	StringBuilder result = new StringBuilder(30);
            
            if (scheme != null) {
                result.append(scheme);
                result.append(':');
            }
            
            if (schemeSpecificPart != null) {
                result.append(schemeSpecificPart);
            }
            else {
                if (scheme != null) result.append("//");
                if (authority != null) result.append(authority);
                result.append(path);
            }
            
            if (query != null) {
                result.append('?');
                result.append(query);
            }
            
            if (fragment != null) {
                result.append('#');
                result.append(fragment);                
            }
            
            return result.toString();
            
        }
        
    }
 
}
