// Copyright 2002-2004 Elliotte Rusty Harold
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

import java.io.DataInputStream;
import java.io.IOException;
import java.util.StringTokenizer;

/**
 * <p>
 * <code>Verifier</code>checks names and data for 
 * compliance with XML 1.0 and Namespaces in XML rules.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0d23
 * 
 */
final class Verifier {
    
    private Verifier() {}
        
    // constants for the bitflags in the characters lookup table
    private final static byte XML_CHARACTER        = 1;
    private final static byte NAME_CHARACTER       = 2;
    private final static byte NAME_START_CHARACTER = 4;
    private final static byte NCNAME_CHARACTER     = 8;
    
    private final static byte[] flags = new byte[65536];

    static {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader == null) loader = Verifier.class.getClassLoader();
        if (loader == null) throw new RuntimeException(
          "Verifier couldn't find the right ClassLoader!");
        
        DataInputStream in = new DataInputStream(
          loader.getResourceAsStream("nu/xom/characters.dat"));
        try {
            in.readFully(flags);
        }
        catch (IOException ex) {
            throw new RuntimeException("Broken XOM installation: "
              + "could not load nu/xom/characters.dat");
        }
        finally {
            try {
                in.close();
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
        
        if (text == null) throwIllegalCharacterDataException(text, "Null text");

        char[] data = text.toCharArray();
        for (int i = 0, len = data.length; i < len; i++) {
            int result = data[i];
            if (result >= 0xD800 && result <= 0xDBFF) { 
                try {
                    result = decodeSurrogatePair(result, data[i+1]);
                    i++; // increment past low surrogate
                }
                catch (ArrayIndexOutOfBoundsException ex) {
                    IllegalCharacterDataException ide 
                      = new IllegalCharacterDataException("Bad Surrogate Pair", ex);
                    ide.setData(text);
                    throw ide;
                }
                catch (IllegalCharacterDataException ex) {
                    ex.setData(text);
                    throw ex;
                }
                // all properly matched surrogate pairs are legal in PCDATA
            }  // end if 
            else if ((flags[result] & XML_CHARACTER) == 0) {
                throwIllegalCharacterDataException(text, "0x " 
                  + Integer.toHexString(result)
                  + " is not allowed in XML content");
            }

        } 

    }

    // This seems to be allowing URIs with fragment IDs.
    // Make sure this is OK everywhere it's used. It's
    // definitely needed in some places; might need separate
    // check URI and checkURIReference methods????
    /**
     * <p>
     * Checks a string to see if it is a syntactically correct 
     * RFC 2396/RFC 2732 URI. Both absolute and relative URIs 
     * are supported.
     * </p>
     * 
     * @param uri <code>String</code> containing the potential URI
     * 
     * @throws MalformedURIException if this is not a legal URI
     */
    static void checkURI(String uri) {
        
        // Are there any loosened rules for fragment
        // IDs? i.e. can they contain characters that the rest of the URI
        // can't such as [ and ]? e.g. in XPointer?
        // Same question for query strings?
        // Do I need to divide URI into base, query, and fragment and check each
        // separately????
        if ((uri == null) || uri.length() == 0) return;

        int leftBrackets = 0;
        int rightBrackets = 0;
        
        int size = uri.length();
        for (int i = 0; i < size; i++) {
            int c = uri.charAt(i);
            if (c >= 0xD800 && c <= 0xDBFF) { 
                try {
                    c = decodeSurrogatePair(c, uri.charAt(i+1));
                    i++; // increment past low surrogate
                }
                catch (Exception ex) {
                    MalformedURIException uriex = 
                      new MalformedURIException("Bad surrogate pair", ex);
                    uriex.setData(uri);
                    throw uriex;
                }
            }  // end if 
            
            if (!isURICharacter(c)) {
                throwMalformedURIException(uri, "The character 0x"
                  + Integer.toHexString(c) + " is not allowed in URIs");                
            }
            if (c == '%') { 
               try {
                   if (!isHexDigit(uri.charAt(i+1)) || !isHexDigit(uri.charAt(i+2))) {
                       throwMalformedURIException(uri, 
                         "Bad percent escape sequence");    
                   }
               }
               catch (StringIndexOutOfBoundsException ex) {
                   throwMalformedURIException(uri, 
                    "Bad percent escape sequence");    
               }
            }
            else if (c == '[') {
                leftBrackets++;  
                if (rightBrackets >= leftBrackets) {
                    throwMalformedURIException(uri, 
                        "Mismatched square brackets"
                    );                                       
                } 
            }
            else if (c == ']') {
                rightBrackets++;   
            }
        } // end for

        if (leftBrackets != rightBrackets) {
            throwMalformedURIException(uri, 
                "Mismatched square brackets"
            );                   
        }
        if (leftBrackets > 1) {
            throwMalformedURIException(uri, 
                "Multiple square brackets"
            );                   
        }
        
        if (leftBrackets == 1) { 
            // We have exactly one left and one right bracket
            String ip6Address = uri.substring(
              uri.indexOf('[')+1, uri.indexOf(']')
            );
            try {
                checkIP6Address(ip6Address);
            }
            catch (MalformedURIException ex) {
                ex.setData(uri);
                throw ex;
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


    
    private static int decodeSurrogatePair(int high, int low) {
        // This method is only called after a high-surrogate 
        // has been spotted in the stream so we know that's good.
        // Thus we only need to test the low surrogate.
        if (low < 0xDC00 || low > 0xDFFF) {
            throw new IllegalCharacterDataException("Bad surrogate pair");
        }
        // Algorithm defined in Unicode spec
        return (high-0xD800)*0x400 + (low-0xDC00) + 0x10000;
        
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

    /**
     * <p>
     * Checks a string to see if it is an RFC 2396/RFC 2732 absolute 
     * URI reference. URI references can contain fragment identifiers.
     * </p>
     * 
     * @param uri <code>String</code> to check
     * 
     * @throws MalformedURIException if this is not a legal 
     *     URI reference
     */
    static void checkAbsoluteURIReference(String uri) {
        
        // Next test is necessary if we're really testing URI 
        // references but not for namespace URIs
        if (uri == null || uri.length() == 0) {
            throwMalformedURIException(uri, 
              "Absolute URIs cannot be empty"
            );   
        }
        
        if (!isAlpha(uri.charAt(0))) {
            throwMalformedURIException(uri,
              "Absolute URIs must begin with an ASCII letter");
        }
        
        int colonLocation = -1;
        for (int i = 1; i < uri.length(); i++) {
             char c = uri.charAt(i);
             if (c == ':') {
                colonLocation = i;
                break;               
             }
             if (!isSchemeCharacter(c)) {
                throwMalformedURIException(uri,
                  "URI schemes cannot contain " + c);   
             }
        }
        
        if (colonLocation == -1) {
             throwMalformedURIException(uri,
                "Namespace URIs should be absolute.");   
        }
        int numberSharps = 0;
        for (int i = colonLocation+1; i < uri.length(); i++) {
            char c = uri.charAt(i);
            if (c == '%') {
                try {
                    char c1 = uri.charAt(i+1);
                    char c2 = uri.charAt(i+2);
                    if (!isHexDigit(c1) || !isHexDigit(c2)) {
                        throwMalformedURIException(uri,
                          ("Bad hexadecimal escape sequence %" 
                          + c1) + c2);   
                    }                      
                }
                catch (IndexOutOfBoundsException ex) {
                    throwMalformedURIException(uri,
                      "Bad hexadecimal escape sequence %");   
                }
            }
            else if (c == '#') {
                numberSharps++;
                if (numberSharps > 1) {
                    throwMalformedURIException(uri,
                      "Multiple fragments #"
                    );
                }
            }
            else if (!isURICharacter(c)) {
                throwMalformedURIException(uri,
                  "URIs cannot contain " + c
                );
            }
            
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
    

    private static boolean isURICharacter(int c) {
        switch(c) {
            case '!': return true;
            case '"': return false;
            // really checking for URI references
            case '#': return true;
            case '$': return true;
            case '%': return true;
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
            case '[': return true;
            case '\\': return false;
            case ']': return true;
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

}