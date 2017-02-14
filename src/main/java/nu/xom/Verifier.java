/* Copyright 2002-2006 Elliotte Rusty Harold
   
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

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.StringTokenizer;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * <p>
 * <code>Verifier</code> checks names and data for 
 * compliance with XML 1.0 and Namespaces in XML rules.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.2d1
 * 
 */
final class Verifier {
    
    private Verifier() {}
        
    // constants for the bit flags in the characters lookup table
    private final static byte XML_CHARACTER        = 1;
    private final static byte NAME_CHARACTER       = 2;
    private final static byte NAME_START_CHARACTER = 4;
    private final static byte NCNAME_CHARACTER     = 8;
    
    private       static byte[] flags = null;

    static {
        
        ClassLoader loader = Verifier.class.getClassLoader();
        if (loader != null) loadFlags(loader);
        // If that didn't work, try a different ClassLoader
        if (flags == null) {
            loader = Thread.currentThread().getContextClassLoader();
            loadFlags(loader);
        }
        
    }
    
    
    private static void loadFlags(ClassLoader loader) {
        
        DataInputStream in = null;
        try {
            InputStream raw = loader.getResourceAsStream(
              "nu/xom/characters.dat");
            if (raw == null) {
                throw new RuntimeException("Broken XOM installation: "
                  + "could not load nu/xom/characters.dat");
            }
            // buffer this????
            in = new DataInputStream(raw);
            flags = new byte[65536];
            in.readFully(flags);
        }
        catch (IOException ex) {
            throw new RuntimeException("Broken XOM installation: "
              + "could not load nu/xom/characters.dat");
        }
        finally {
            try {
                if (in != null) in.close();
            }
            catch (IOException ex) {
                // no big deal
            }
        }
        
    }


    /**
     * <p>
     * Check whether <code>name</code> is 
     * a non-colonized name as defined in 
     * <cite>Namespaces in XML</cite>.
     * </p>
     * 
     * @param name <code>String</code> name to check
     * 
     * @throws IllegalNameException if <code>name</code> is not a 
     *     non-colonized name
     */
    static void checkNCName(String name) {

        if (name == null) {
            throwIllegalNameException(name, "NCNames cannot be null");
        }
        
        int length = name.length();
        if (length == 0) {
            throwIllegalNameException(name, "NCNames cannot be empty");
        }
        
        char first = name.charAt(0);
        if ((flags[first] & NAME_START_CHARACTER) == 0) {
            throwIllegalNameException(name, "NCNames cannot start " +
              "with the character " + Integer.toHexString(first));
        }
        
        for (int i = 1; i < length; i++) {
            char c = name.charAt(i);
            if ((flags[c] & NCNAME_CHARACTER) == 0) {
                if (c == ':') {
                    throwIllegalNameException(name, "NCNames cannot contain colons");                    
                }
                else {
                    throwIllegalNameException(name, "0x" 
                      + Integer.toHexString(c) + " is not a legal NCName character");
                }
            }
        }

    }

    
    private static void throwIllegalNameException(String name, String message) {
        IllegalNameException ex = new IllegalNameException(message);
        ex.setData(name);
        throw ex;
    }


    private static void throwIllegalCharacterDataException(String data, String message) {
        IllegalDataException ex = new IllegalCharacterDataException(message);
        ex.setData(data);
        throw ex;
    }


    private static void throwMalformedURIException(String uri, String message) {
        MalformedURIException ex = new MalformedURIException(message);
        ex.setData(uri);
        throw ex;
    }


    /**
     * <p>
     * This methods checks whether a string contains only
     * characters allowed by the XML 1.0 specification. 
     * </p>
     *
     * @param text <code>String</code> value to verify
     * 
     * @throws IllegalCharacterDataException if <code>text</code> is  
     *     not legal PCDATA
     */
    static void checkPCDATA(String text) {
        
        if (text == null) throw new IllegalCharacterDataException("Null text");

        char[] data = text.toCharArray();
        for (int i = 0, len = data.length; i < len; i++) {
            int result = data[i];
            if (result >= 0xD800 && result <= 0xDBFF) { 
                try {
                    int low = data[i+1];
                    if (low < 0xDC00 || low > 0xDFFF) {
                        IllegalCharacterDataException ex 
                          = new IllegalCharacterDataException("Bad surrogate pair");
                        ex.setData(text);
                        throw ex;
                    }
                    i++; // increment past low surrogate
                }
                catch (ArrayIndexOutOfBoundsException ex) {
                    IllegalCharacterDataException ide 
                      = new IllegalCharacterDataException("Bad Surrogate Pair", ex);
                    ide.setData(text);
                    throw ide;
                }
                // all properly matched surrogate pairs are legal in PCDATA
            }  // end if 
            else if ((flags[result] & XML_CHARACTER) == 0) {
                throwIllegalCharacterDataException(text, "0x" 
                  + Integer.toHexString(result)
                  + " is not allowed in XML content");
            }

        } 

    }

    
    /**
     * <p>
     * Checks a string to see if it is a syntactically correct 
     * RFC 3986 URI reference. Both absolute and relative  
     * URIs are supported, as are URIs with fragment identifiers.
     * </p>
     * 
     * @param uri <code>String</code> containing the potential URI
     * 
     * @throws MalformedURIException if this is not a 
     *     legal URI reference
     */
    static void checkURIReference(String uri) {
        
        if ((uri == null) || uri.length() == 0) return;

        URIUtil.ParsedURI parsed = new URIUtil.ParsedURI(uri);
        try {
            if (parsed.scheme != null) checkScheme(parsed.scheme);
            if (parsed.authority != null) checkAuthority(parsed.authority);
            checkPath(parsed.path);
            if (parsed.fragment != null) checkFragment(parsed.fragment);
            if (parsed.query != null) checkQuery(parsed.query);
        }
        catch (MalformedURIException ex) {
            ex.setData(uri);
            throw ex;
        }
        
    }
    

    private static void checkQuery(final String query) {
        
        int length = query.length();
        for (int i = 0; i < length; i++) {
            char c = query.charAt(i);
            if (c == '%') {
               try {
                   if (!isHexDigit(query.charAt(i+1)) || !isHexDigit(query.charAt(i+2))) {
                       throwMalformedURIException(query, 
                         "Bad percent escape sequence");    
                   }
               }
               catch (StringIndexOutOfBoundsException ex) {
                   throwMalformedURIException(query, 
                     "Bad percent escape sequence");                       
               }
               i += 2;
            }
            else if (!isQueryCharacter(c)) {
                throw new MalformedURIException(
                  "Illegal query character " + c
                );
            }
        }
        
    }

    
    // same for fragment ID
    private static boolean isQueryCharacter(char c) {
        
        switch(c) {
            case '!': return true;
            case '"': return false;
            case '#': return false;
            case '$': return true;
            case '%': return false; // tested in checkQuery
            case '&': return true;
            case '\'': return true;
            case '(': return true;
            case ')': return true;
            case '*': return true;
            case '+': return true;
            case ',': return true;
            case '-': return true;
            case '.': return true;
            case '/': return true;
            case '0': return true;
            case '1': return true;
            case '2': return true;
            case '3': return true;
            case '4': return true;
            case '5': return true;
            case '6': return true;
            case '7': return true;
            case '8': return true;
            case '9': return true;
            case ':': return true;
            case ';': return true;
            case '<': return false;
            case '=': return true;
            case '>': return false;
            case '?': return true;
            case '@': return true;
            case 'A': return true;
            case 'B': return true;
            case 'C': return true;
            case 'D': return true;
            case 'E': return true;
            case 'F': return true;
            case 'G': return true;
            case 'H': return true;
            case 'I': return true;
            case 'J': return true;
            case 'K': return true;
            case 'L': return true;
            case 'M': return true;
            case 'N': return true;
            case 'O': return true;
            case 'P': return true;
            case 'Q': return true;
            case 'R': return true;
            case 'S': return true;
            case 'T': return true;
            case 'U': return true;
            case 'V': return true;
            case 'W': return true;
            case 'X': return true;
            case 'Y': return true;
            case 'Z': return true;
            case '[': return false;
            case '\\': return false;
            case ']': return false;
            case '^': return false;
            case '_': return true;
            case '`': return false;
            case 'a': return true;
            case 'b': return true;
            case 'c': return true;
            case 'd': return true;
            case 'e': return true;
            case 'f': return true;
            case 'g': return true;
            case 'h': return true;
            case 'i': return true;
            case 'j': return true;
            case 'k': return true;
            case 'l': return true;
            case 'm': return true;
            case 'n': return true;
            case 'o': return true;
            case 'p': return true;
            case 'q': return true;
            case 'r': return true;
            case 's': return true;
            case 't': return true;
            case 'u': return true;
            case 'v': return true;
            case 'w': return true;
            case 'x': return true;
            case 'y': return true;
            case 'z': return true;
            case '{': return false;
            case '|': return false;
            case '}': return false;
            case '~': return true;
        }
        return false;
        
    }


    private static void checkFragment(String fragment) {
        // The BNF for fragments is the same as for query strings
        checkQuery(fragment);
    }

    
    // Besides the legal characters issues, a path must
    // not contain two consecutive forward slashes
    private static void checkPath(final String path) {
        
        int length = path.length();
        char[] text = path.toCharArray();
        for (int i = 0; i < length; i++) {
            char c = text[i];
            if (c == '/') {
                if (i < length-1) {
                    if (text[i+1] == '/') {
                        throwMalformedURIException(path, 
                          "Double slash (//) in path");
                    }
                }
            }
            else if (c == '%') {
               try {
                   if (!isHexDigit(text[i+1]) 
                     || !isHexDigit(text[i+2])) {
                       throwMalformedURIException(path, 
                         "Bad percent escape sequence");    
                   }
               }
               catch (ArrayIndexOutOfBoundsException ex) {
                   throwMalformedURIException(path, 
                     "Bad percent escape sequence");                       
               }
               i += 2;
            }
            else if (!isPathCharacter(c)) {
                throwMalformedURIException(path, 
                  "Illegal path character " + c
                );
            }
        }
        
    }


    private static void checkAuthority(String authority) {
        
        String userInfo = null;
        String host = null;
        String port = null;
        
        int atSign = authority.indexOf('@');
        if (atSign != -1) {
            userInfo = authority.substring(0, atSign);
            authority = authority.substring(atSign+1);
        }
        
        int colon;
        if (authority.startsWith("[")) {
            colon = authority.indexOf("]:");
            if (colon != -1) colon = colon+1;
        }
        else colon = authority.indexOf(':');
        
        if (colon != -1) {
            host = authority.substring(0, colon);
            port = authority.substring(colon+1);
        }
        else {
            host = authority;
        }
        
        if (userInfo != null) checkUserInfo(userInfo);
        if (port != null) checkPort(port);
        checkHost(host);
        
    }


    private static void checkHost(final String host) {
    
        int length = host.length();
        if (length == 0) return; // file URI
        
        char[] text = host.toCharArray();
        if (text[0] == '[') {
            if (text[length-1] != ']') {
                throw new MalformedURIException("Missing closing ]");
            }
                            // trim [ and ] from ends of host
            checkIP6Address(host.substring(1, length-1));
        }
        else {
            if (length > 255) {
                throw new MalformedURIException("Host name too long: " + host);
            }
            
            for (int i = 0; i < length; i++) {
                char c = text[i];
                if (c == '%') {
                   try {
                       if (!isHexDigit(text[i+1]) || !isHexDigit(text[i+2])) {
                           throwMalformedURIException(host, 
                             "Bad percent escape sequence");    
                       }
                   }
                   catch (ArrayIndexOutOfBoundsException ex) {
                       throwMalformedURIException(host, 
                         "Bad percent escape sequence");                       
                   }
                   i += 2;
                }
                else if (!isRegNameCharacter(c)) {
                    throwMalformedURIException(host, 
                      "Illegal host character " + c
                    );
                }
            }
        }
    }


    private static boolean isRegNameCharacter(char c) {

        switch(c) {
            case '!': return true;
            case '"': return false;
            case '#': return false;
            case '$': return true;
            case '%': return false; // checked separately
            case '&': return true;
            case '\'': return true;
            case '(': return true;
            case ')': return true;
            case '*': return true;
            case '+': return true;
            case ',': return true;
            case '-': return true;
            case '.': return true;
            case '/': return false;
            case '0': return true;
            case '1': return true;
            case '2': return true;
            case '3': return true;
            case '4': return true;
            case '5': return true;
            case '6': return true;
            case '7': return true;
            case '8': return true;
            case '9': return true;
            case ':': return false;
            case ';': return true;
            case '<': return false;
            case '=': return true;
            case '>': return false;
            case '?': return false;
            case '@': return false;
            case 'A': return true;
            case 'B': return true;
            case 'C': return true;
            case 'D': return true;
            case 'E': return true;
            case 'F': return true;
            case 'G': return true;
            case 'H': return true;
            case 'I': return true;
            case 'J': return true;
            case 'K': return true;
            case 'L': return true;
            case 'M': return true;
            case 'N': return true;
            case 'O': return true;
            case 'P': return true;
            case 'Q': return true;
            case 'R': return true;
            case 'S': return true;
            case 'T': return true;
            case 'U': return true;
            case 'V': return true;
            case 'W': return true;
            case 'X': return true;
            case 'Y': return true;
            case 'Z': return true;
            case '[': return false;
            case '\\': return false;
            case ']': return false;
            case '^': return false;
            case '_': return true;
            case '`': return false;
            case 'a': return true;
            case 'b': return true;
            case 'c': return true;
            case 'd': return true;
            case 'e': return true;
            case 'f': return true;
            case 'g': return true;
            case 'h': return true;
            case 'i': return true;
            case 'j': return true;
            case 'k': return true;
            case 'l': return true;
            case 'm': return true;
            case 'n': return true;
            case 'o': return true;
            case 'p': return true;
            case 'q': return true;
            case 'r': return true;
            case 's': return true;
            case 't': return true;
            case 'u': return true;
            case 'v': return true;
            case 'w': return true;
            case 'x': return true;
            case 'y': return true;
            case 'z': return true;
            case '{': return false;
            case '|': return false;
            case '}': return false;
            case '~': return true;
        }
        return false;
        
    }


    private static void checkPort(String port) {
        
        for (int i = port.length()-1; i >= 0; i--) {
            char c = port.charAt(i);
            if (c < '0' || c > '9') {
                throw new MalformedURIException("Bad port: " + port);
            }
        }

    }


    private static void checkUserInfo(String userInfo) {

        int length = userInfo.length();
        for (int i = 0; i < length; i++) {
            char c = userInfo.charAt(i);
            if (c == '%') {
               try {
                   if (!isHexDigit(userInfo.charAt(i+1)) 
                     || !isHexDigit(userInfo.charAt(i+2))) {
                       throwMalformedURIException(userInfo, 
                         "Bad percent escape sequence");    
                   }
               }
               catch (StringIndexOutOfBoundsException ex) {
                   throwMalformedURIException(userInfo, 
                     "Bad percent escape sequence");                       
               }
               i += 2;
            }
            else if (!isUserInfoCharacter(c)) {
                throw new MalformedURIException("Bad user info: " + userInfo);
            }
        }
        
    }


    private static void checkScheme(String scheme) {

        // http is probably 99% of cases so check it first 
        if ("http".equals(scheme)) return;
        
        if (scheme.length() == 0) {
            throw new MalformedURIException("URIs cannot begin with a colon");            
        }
        char c = scheme.charAt(0);
        if (!isAlpha(c)) {
            throw new MalformedURIException(
              "Illegal initial scheme character " + c);
        }
        
        for (int i = scheme.length()-1; i >= 1; i--) {
            c = scheme.charAt(i);
            if (!isSchemeCharacter(c)) {
                throw new MalformedURIException(
                  "Illegal scheme character " + c
                );
            }
        }
        
    }


    private static void checkIP6Address(String ip6Address) {

        StringTokenizer st = new StringTokenizer(ip6Address, ":", true);
        int numTokens = st.countTokens();
        if (numTokens > 15 || numTokens < 2) {
            throw new MalformedURIException(
              "Illegal IP6 host address: " + ip6Address
            );                                              
        }
        for (int i = 0; i < numTokens; i++) {
            String hexPart = st.nextToken();
            if (":".equals(hexPart)) continue;
            try {
                int part = Integer.parseInt(hexPart, 16);
                if (part < 0) {
                      throw new MalformedURIException( 
                      "Illegal IP6 host address: " + ip6Address
                    );                                                            
                }
            }
            catch (NumberFormatException ex) {
                if (i == numTokens-1) {
                    checkIP4Address(hexPart, ip6Address);        
                }
                else {
                    throwMalformedURIException(ip6Address,
                      "Illegal IP6 host address: " + ip6Address
                    );                                                            
                }
            }
        }
        
        if (ip6Address.indexOf("::") != ip6Address.lastIndexOf("::")) {
            throw new MalformedURIException(
              "Illegal IP6 host address: " + ip6Address
            );                                                          
        }
        
    }

    
    private static void checkIP4Address(String address, String ip6Address) {

        StringTokenizer st = new StringTokenizer(address, ".");
        int numTokens = st.countTokens();
        if (numTokens != 4) {
            throw new MalformedURIException(
              "Illegal IP6 host address: " + ip6Address
            );                                              
        }
        for (int i = 0; i < 4; i++) {
            String decPart = st.nextToken();
            try {
                int dec = Integer.parseInt(decPart);
                if (dec > 255 || dec < 0) {
                    throw new MalformedURIException(
                      "Illegal IP6 host address: " + ip6Address
                    );                                                                                
                }
            }
            catch (NumberFormatException ex) {
                throw new MalformedURIException(
                  "Illegal IP6 host address: " + ip6Address
                );                                                            
            }
        }
        
    }    

    
    static void checkXMLName(String name) {
        
        if (name == null) {
            throwIllegalNameException(name, "XML names cannot be null");
        }
        
        int length = name.length();
        if (length == 0) {
            throwIllegalNameException(name, "XML names cannot be empty");
        }
        
        char first = name.charAt(0);
        if ((flags[first] & NAME_START_CHARACTER) == 0) {
            throwIllegalNameException(name, "XML names cannot start " +
              "with the character " + Integer.toHexString(first));
        }
        
        for (int i = 1; i < length; i++) {
            char c = name.charAt(i);
            if ((flags[c] & NAME_CHARACTER) == 0) {
                throwIllegalNameException(name, "0x" 
                  + Integer.toHexString(c) 
                  + " is not a legal name character");
            }
        }

    }


    private static boolean[] C0Table = new boolean[0x21];
    static {
        C0Table['\n'] = true;
        C0Table['\r'] = true;
        C0Table['\t'] = true;
        C0Table[' '] = true;
    }

    
    static boolean isXMLSpaceCharacter(char c) {
        if (c > ' ') return false;
        return C0Table[c];
    }
    

    private static boolean isHexDigit(char c) {

        switch(c) {
            case '0': return true;
            case '1': return true;
            case '2': return true;
            case '3': return true;
            case '4': return true;
            case '5': return true;
            case '6': return true;
            case '7': return true;
            case '8': return true;
            case '9': return true;
            case ':': return false;
            case ';': return false;
            case '<': return false;
            case '=': return false;
            case '>': return false;
            case '?': return false;
            case '@': return false;
            case 'A': return true;
            case 'B': return true;
            case 'C': return true;
            case 'D': return true;
            case 'E': return true;
            case 'F': return true;
            case 'G': return false;
            case 'H': return false;
            case 'I': return false;
            case 'J': return false;
            case 'K': return false;
            case 'L': return false;
            case 'M': return false;
            case 'N': return false;
            case 'O': return false;
            case 'P': return false;
            case 'Q': return false;
            case 'R': return false;
            case 'S': return false;
            case 'T': return false;
            case 'U': return false;
            case 'V': return false;
            case 'W': return false;
            case 'X': return false;
            case 'Y': return false;
            case 'Z': return false;
            case '[': return false;
            case '\\': return false;
            case ']': return false;
            case '^': return false;
            case '_': return false;
            case '`': return false;
            case 'a': return true;
            case 'b': return true;
            case 'c': return true;
            case 'd': return true;
            case 'e': return true;
            case 'f': return true;
        }
        return false; 
    }
    
    
    // Since namespace URIs are commonly repeated, we can save a lot 
    // of redundant code by storing the ones we've seen before. 
    private static URICache cache = new URICache(); 

    private final static class URICache {
     
        private final static int LOAD = 6;
        private String[] cache = new String[LOAD];
        private int position = 0;
        
        synchronized boolean contains(String s) {
            
            for (int i = 0; i < LOAD; i++) {
                // Here I'm assuming the namespace URIs are interned.
                // This is commonly but not always true. This won't
                // break if they haven't been. Using equals() instead 
                // of == is faster when the namespace URIs haven't been
                // interned but slower if they have.
                if (s == cache[i]) {
                    return true;
                }
            }
            return false;
            
        }

        synchronized void put(String s) {
            cache[position] = s;
            position++;
            if (position == LOAD) position = 0;
        }
        
    }
    
    
    /**
     * <p>
     * Checks a string to see if it is an RFC 3986 absolute 
     * URI reference. URI references can contain fragment identifiers.
     * Absolute URI references must have a scheme.
     * </p>
     * 
     * @param uri <code>String</code> to check
     * 
     * @throws MalformedURIException if this is not a legal 
     *     URI reference
     */
    static void checkAbsoluteURIReference(String uri) {
        
        if (cache.contains(uri)) {
            return;
        }
        URIUtil.ParsedURI parsed = new URIUtil.ParsedURI(uri);
        try {
            if (parsed.scheme == null) {
                throwMalformedURIException(
                  uri, "Missing scheme in absolute URI reference");
            }
            checkScheme(parsed.scheme);
            if (parsed.authority != null) checkAuthority(parsed.authority);
            checkPath(parsed.path);
            if (parsed.fragment != null) checkFragment(parsed.fragment);
            if (parsed.query != null) checkQuery(parsed.query);
            cache.put(uri);
        }
        catch (MalformedURIException ex) {
            ex.setData(uri);
            throw ex;
        }
   
    }

    
    static boolean isAlpha(char c) {
        
        switch(c) {
            case 'A': return true;
            case 'B': return true;
            case 'C': return true;
            case 'D': return true;
            case 'E': return true;
            case 'F': return true;
            case 'G': return true;
            case 'H': return true;
            case 'I': return true;
            case 'J': return true;
            case 'K': return true;
            case 'L': return true;
            case 'M': return true;
            case 'N': return true;
            case 'O': return true;
            case 'P': return true;
            case 'Q': return true;
            case 'R': return true;
            case 'S': return true;
            case 'T': return true;
            case 'U': return true;
            case 'V': return true;
            case 'W': return true;
            case 'X': return true;
            case 'Y': return true;
            case 'Z': return true;
            case '[': return false;
            case '\\': return false;
            case ']': return false;
            case '^': return false;
            case '_': return false;
            case '`': return false;
            case 'a': return true;
            case 'b': return true;
            case 'c': return true;
            case 'd': return true;
            case 'e': return true;
            case 'f': return true;
            case 'g': return true;
            case 'h': return true;
            case 'i': return true;
            case 'j': return true;
            case 'k': return true;
            case 'l': return true;
            case 'm': return true;
            case 'n': return true;
            case 'o': return true;
            case 'p': return true;
            case 'q': return true;
            case 'r': return true;
            case 's': return true;
            case 't': return true;
            case 'u': return true;
            case 'v': return true;
            case 'w': return true;
            case 'x': return true;
            case 'y': return true;
            case 'z': return true;
        }
        
        return false;
        
    } 
    
    
    static boolean isSchemeCharacter(char c) {
        
        /* The : and the ? cannot be reached here because they'll
         * have been parsed out separately before this method is
         * called. They're included here strictly for alignment
         * so the compiler will generate a table lookup.
         */
        switch(c) {
            case '+': return true;
            case ',': return false;
            case '-': return true;
            case '.': return true;
            case '/': return false;
            case '0': return true;
            case '1': return true;
            case '2': return true;
            case '3': return true;
            case '4': return true;
            case '5': return true;
            case '6': return true;
            case '7': return true;
            case '8': return true;
            case '9': return true;
            case ':': return false;  // unreachable
            case ';': return false;
            case '<': return false;
            case '=': return false;
            case '>': return false;
            case '?': return false;  // unreachable
            case '@': return false;
            case 'A': return true;
            case 'B': return true;
            case 'C': return true;
            case 'D': return true;
            case 'E': return true;
            case 'F': return true;
            case 'G': return true;
            case 'H': return true;
            case 'I': return true;
            case 'J': return true;
            case 'K': return true;
            case 'L': return true;
            case 'M': return true;
            case 'N': return true;
            case 'O': return true;
            case 'P': return true;
            case 'Q': return true;
            case 'R': return true;
            case 'S': return true;
            case 'T': return true;
            case 'U': return true;
            case 'V': return true;
            case 'W': return true;
            case 'X': return true;
            case 'Y': return true;
            case 'Z': return true;
            case '[': return false;
            case '\\': return false;
            case ']': return false;
            case '^': return false;
            case '_': return false;
            case '`': return false;
            case 'a': return true;
            case 'b': return true;
            case 'c': return true;
            case 'd': return true;
            case 'e': return true;
            case 'f': return true;
            case 'g': return true;
            case 'h': return true;
            case 'i': return true;
            case 'j': return true;
            case 'k': return true;
            case 'l': return true;
            case 'm': return true;
            case 'n': return true;
            case 'o': return true;
            case 'p': return true;
            case 'q': return true;
            case 'r': return true;
            case 's': return true;
            case 't': return true;
            case 'u': return true;
            case 'v': return true;
            case 'w': return true;
            case 'x': return true;
            case 'y': return true;
            case 'z': return true;
        }
        
        return false;
        
    }


    private static boolean isPathCharacter(char c) {

        switch(c) {
            case '!': return true;
            case '"': return false;
            case '#': return false;
            case '$': return true;
            case '%': return false; // checked separately
            case '&': return true;
            case '\'': return true;
            case '(': return true;
            case ')': return true;
            case '*': return true;
            case '+': return true;
            case ',': return true;
            case '-': return true;
            case '.': return true;
            case '/': return false; // handled separately
            case '0': return true;
            case '1': return true;
            case '2': return true;
            case '3': return true;
            case '4': return true;
            case '5': return true;
            case '6': return true;
            case '7': return true;
            case '8': return true;
            case '9': return true;
            case ':': return true;
            case ';': return true;
            case '<': return false;
            case '=': return true;
            case '>': return false;
            case '?': return false;
            case '@': return true;
            case 'A': return true;
            case 'B': return true;
            case 'C': return true;
            case 'D': return true;
            case 'E': return true;
            case 'F': return true;
            case 'G': return true;
            case 'H': return true;
            case 'I': return true;
            case 'J': return true;
            case 'K': return true;
            case 'L': return true;
            case 'M': return true;
            case 'N': return true;
            case 'O': return true;
            case 'P': return true;
            case 'Q': return true;
            case 'R': return true;
            case 'S': return true;
            case 'T': return true;
            case 'U': return true;
            case 'V': return true;
            case 'W': return true;
            case 'X': return true;
            case 'Y': return true;
            case 'Z': return true;
            case '[': return false;
            case '\\': return false;
            case ']': return false;
            case '^': return false;
            case '_': return true;
            case '`': return false;
            case 'a': return true;
            case 'b': return true;
            case 'c': return true;
            case 'd': return true;
            case 'e': return true;
            case 'f': return true;
            case 'g': return true;
            case 'h': return true;
            case 'i': return true;
            case 'j': return true;
            case 'k': return true;
            case 'l': return true;
            case 'm': return true;
            case 'n': return true;
            case 'o': return true;
            case 'p': return true;
            case 'q': return true;
            case 'r': return true;
            case 's': return true;
            case 't': return true;
            case 'u': return true;
            case 'v': return true;
            case 'w': return true;
            case 'x': return true;
            case 'y': return true;
            case 'z': return true;
            case '{': return false;
            case '|': return false;
            case '}': return false;
            case '~': return true;
        }
        
        return false;
        
    }
    

    private static boolean isUserInfoCharacter(char c) {

        switch(c) {
            case '!': return true;
            case '"': return false;
            case '#': return false;
            case '$': return true;
            case '%': return false; // checked separately
            case '&': return true;
            case '\'': return true;
            case '(': return true;
            case ')': return true;
            case '*': return true;
            case '+': return true;
            case ',': return true;
            case '-': return true;
            case '.': return true;
            case '/': return true;
            case '0': return true;
            case '1': return true;
            case '2': return true;
            case '3': return true;
            case '4': return true;
            case '5': return true;
            case '6': return true;
            case '7': return true;
            case '8': return true;
            case '9': return true;
            case ':': return true;
            case ';': return true;
            case '<': return false;
            case '=': return true;
            case '>': return false;
            case '?': return false;
            case '@': return false;
            case 'A': return true;
            case 'B': return true;
            case 'C': return true;
            case 'D': return true;
            case 'E': return true;
            case 'F': return true;
            case 'G': return true;
            case 'H': return true;
            case 'I': return true;
            case 'J': return true;
            case 'K': return true;
            case 'L': return true;
            case 'M': return true;
            case 'N': return true;
            case 'O': return true;
            case 'P': return true;
            case 'Q': return true;
            case 'R': return true;
            case 'S': return true;
            case 'T': return true;
            case 'U': return true;
            case 'V': return true;
            case 'W': return true;
            case 'X': return true;
            case 'Y': return true;
            case 'Z': return true;
            case '[': return false;
            case '\\': return false;
            case ']': return false;
            case '^': return false;
            case '_': return true;
            case '`': return false;
            case 'a': return true;
            case 'b': return true;
            case 'c': return true;
            case 'd': return true;
            case 'e': return true;
            case 'f': return true;
            case 'g': return true;
            case 'h': return true;
            case 'i': return true;
            case 'j': return true;
            case 'k': return true;
            case 'l': return true;
            case 'm': return true;
            case 'n': return true;
            case 'o': return true;
            case 'p': return true;
            case 'q': return true;
            case 'r': return true;
            case 's': return true;
            case 't': return true;
            case 'u': return true;
            case 'v': return true;
            case 'w': return true;
            case 'x': return true;
            case 'y': return true;
            case 'z': return true;
            case '{': return false;
            case '|': return false;
            case '}': return false;
            case '~': return true;
        }
        
        return false;
        
    }
    
        
    /**
     * Check to see that this string is an absolute URI,
     * neither a relative URI nor a URI reference.
     * 
     */
    static void checkAbsoluteURI(String uri) {
        
        URIUtil.ParsedURI parsed = new URIUtil.ParsedURI(uri);
        try {
            if (parsed.scheme == null) {
                throwMalformedURIException(uri, "Missing scheme in absolute URI");
            }
            checkScheme(parsed.scheme);
            if (parsed.authority != null) checkAuthority(parsed.authority);
            checkPath(parsed.path);
            if (parsed.fragment != null) {
                throwMalformedURIException(uri, "URIs cannot have fragment identifiers");
            }
            if (parsed.query != null) checkQuery(parsed.query);
        }
        catch (MalformedURIException ex) {
            ex.setData(uri);
            throw ex;
        }        

    }

    
    // For use in checking internal DTD subsets 
    private static XMLReader parser;

    static synchronized void checkInternalDTDSubset(String subset) {

        if (parser == null) {
            final InputSource empty = new InputSource(new EmptyReader());
            parser = Builder.findParser(false);
            // parser = new org.apache.crimson.parser.XMLReaderImpl();
            // Now let's stop this parser from loading any external
            // entities the subset references
            parser.setEntityResolver(new EntityResolver() {

                public InputSource resolveEntity(String publicID, String systemID) {
                    return empty;
                }   
            
            });
        }
        
        String doc = "<!DOCTYPE a [" + subset + "]><a/>";
        try {
            InputSource source = new InputSource(new StringReader(doc));
            // just to make sure relative URLs can be resolved; don't
            // actually need to connect to this; the EntityResolver
            // prevents that
            source.setSystemId("http://www.example.org/");
            parser.parse(source);
        }
        catch (SAXException ex) {
            IllegalDataException idex = new IllegalDataException(
              "Malformed internal DTD subset: " + ex.getMessage(), ex);
            idex.setData(subset);
            throw idex;
        }
        catch (IOException ex) {
            throw new RuntimeException("BUG: I don't think this can happen");
        }
        
    } 
    
    
    // A reader that immediately returns end of stream. This is a great
    // big hack to avoid reading anything when setting the internal 
    // DTD subset. I could use the 
    // http://xml.org/sax/features/external-parameter-entities SAX 
    // feature, but many  parsers don't reliably implement that so  
    // instead we simply pretend that all URLs point to empty files.
    private static class EmptyReader extends Reader {

        public int read(char[] text, int start, int length) throws IOException {
            return -1;
        }

        public void close() {}
        
    }

    
}