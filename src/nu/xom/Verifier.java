package nu.xom;

// This is the one file in XOM derived from JDOM.
// It is subject to the following license:
/*
 Copyright (C) 2000 Jason Hunter & Brett McLaughlin.
 All rights reserved.
 
 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:
 
 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions, and the following disclaimer.
 
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions, and the disclaimer that follows 
    these conditions in the documentation and/or other materials 
    provided with the distribution.

 3. The name "JDOM" must not be used to endorse or promote products
    derived from this software without prior written permission.  For
    written permission, please contact <request_AT_jdom_DOT_org>.
 
 4. Products derived from this software may not be called "JDOM", nor
    may "JDOM" appear in their name, without prior written permission
    from the JDOM Project Management <request_AT_jdom_DOT_org>.
 
 In addition, we request (but do not require) that you include in the 
 end-user documentation provided with the redistribution and/or in the 
 software itself an acknowledgement equivalent to the following:
     "This product includes software developed by the
      JDOM Project (http://www.jdom.org/)."
 Alternatively, the acknowledgment may be graphical using the logos 
 available at http://www.jdom.org/images/logos.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED.  IN NO EVENT SHALL THE JDOM AUTHORS OR THE PROJECT
 CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 SUCH DAMAGE.

 This software consists of voluntary contributions made by many 
 individuals on behalf of the JDOM Project and was originally 
 created by Jason Hunter <jhunter_AT_jdom_DOT_org> and
 Brett McLaughlin <brett_AT_jdom_DOT_org>.  For more information
 on the JDOM Project, please see <http://www.jdom.org/>.
 
 */

/**
 * 
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

    /**
     * <p>
     * Check whether <code>name</code> is 
     * a non-colonized name as defined in 
     * <cite>Namespaces in XML</cite>.
     * </p>
     * 
     * @param name <code>String</code> name to check
     * @throws IllegalNameException if this is not a legal NCName
     */
    public static void checkNCName(String name) {
        // Check basic XML name rules first
        checkXMLName(name);

        if (name.indexOf(":") != -1) {
            throw new NamespaceException(name 
             + " is illegal because NCNames cannot contain colons");
        }

    }

    /**
     * <p>
     * Check whether <code>name</code> is a legal 
     * local part of an
     * attribute name. This must be a non-colonized name
     * other than <code>xmlns</code>.
     * </p>
     * 
     * @param name <code>String</code> name to check
     * 
     * @throws IllegalNameException if this is not a legal 
     *     attribute name
     */
    public static void checkAttributeLocalName(String name) {

        checkNCName(name);

        if (name.equals("xmlns")) {
            throw new NamespaceException("The Attribute class is not"
              + " used for namespace declaration attributes.");
        }

        // If we got here, everything is OK
    }
    
    /**
     * <p>
     * This will check the supplied string to see if it only contains
     * characters allowed by the XML 1.0 specification. The C0 controls
     * (e.g. null, vertical tab, formfeed, etc.) are specifically 
     * excluded except for carriage return, linefeed, and the 
     * horizontal tab. Unmatched halves of surrogate pairs 
     * are also excluded.
     * </p>
     * 
     * <p>
     * This method is useful for checking element content 
     * and attribute values. Note that characters 
     * like " and &lt; are allowed in attribute values  
     * and element content. They will simply be escaped as 
     * <code>&amp;quot;</code> or <code>&amp;lt;</code> when 
     * the value is serialized. 
     * </p>
     *
     * @param text <code>String</code> value to check
     * 
     * @throws IllegalDataException if <code>text</code> is not 
     *     legal PCDATA
     */
    public static void checkCharacterData(String text) {
        if (text == null) {
            throw new IllegalDataException(
              "A null is not a legal XML value"
            );
        }

        // do check
        char[] data = text.toCharArray();
        for (int i = 0, len = data.length; i < len; i++) {
            int result = data[i];
            // high surrogate
            if (result >= 0xD800 && result <= 0xDBFF) { 
                // Decode surrogate pair
               int high = result;
               try {
                 int low = data[i+1];
                 if (low < 0xDC00 || low > 0xDFFF) {
                   throw new IllegalDataException(
                     "Bad surrogate pair"
                   );
                 }
                 // Algorithm defined in Unicode spec
                 result = (high-0xD800)*0x400 + (low-0xDC00) + 0x10000;
                 i++;
               }
               catch (IndexOutOfBoundsException ex) {
                   throw new IllegalDataException(
                     "Bad surrogate pair"
                   );
               }
            }

            if (!isXMLCharacter(result)) {
                // Likely this character can't be easily displayed
                // because it's a control so we use its hexadecimal 
                // representation in the reason.
                throw new IllegalDataException("0x" 
                 + Integer.toHexString(result)
                 + " is not a legal XML character");    
            }       
        }

        // If we got here, everything is OK
    }


    /**
     * <p>
     * Checks a string to see if it is a legal RFC 2396/RFC 2732 URI.
     * Both absolute and relative URIs are supported.
     * </p>
     * 
     * @param uri <code>String</code> to check
     * @throws MalformedURIException if this is not a legal URI
     */
    public static void checkURI(String uri) {
        // URIs can be null or empty
        if (uri == null) return;
        int uriLength =  uri.length();
        if (uriLength == 0) return;
        
        char[] data = uri.toCharArray();
        int leftBrackets = 0;
        int rightBrackets = 0;
        for (int i = 0; i < uriLength; i++) {
            char test = data[i];
            if (!isURICharacter(test)) {
                String msgNumber = "0x" + Integer.toHexString(test);
                if (test <= 0x09) {
                    msgNumber = "0x0" + Integer.toHexString(test);
                } 
                throw new MalformedURIException("URIs cannot contain "
                    + msgNumber);
            } // end if
            else if (test == '%') { 
               // must be followed by two hexadecimal digits
                   try {
                       char firstDigit = uri.charAt(i+1);
                       char secondDigit = uri.charAt(i+2);
                       if (!isHexDigit(firstDigit) 
                        || !isHexDigit(secondDigit)) {
                           throw new MalformedURIException(
                            "Percent signs in URIs must be followed "
                            + "by exactly two hexadecimal digits.");    
                       }
          
                   }
                   catch (StringIndexOutOfBoundsException ex) {
                       throw new MalformedURIException(
                        "Percent signs in URIs must be followed by "
                        + "exactly two hexadecimal digits.");    
                   }
            }
            else if (test == '[') {
                leftBrackets++;  
                if (rightBrackets >= leftBrackets) {
                    throw new MalformedURIException(
                        "Bad use of square brackets"
                    );                                       
                } 
            }
            else if (test == ']') {
                rightBrackets++;   
            }
        } // end for

        if (leftBrackets != rightBrackets || leftBrackets > 1) {
            throw new MalformedURIException(
                "Bad use of square brackets"
            );                   
        }

        // If we got here, everything is OK
        return;
    }


    /**
     * <p>
     * Checks a string to see if it is a legal IRI.
     * Both absolute and relative IRIs are supported.
     * </p>
     * 
     * @param iri <code>String</code> to check
     * @throws MalformedURIException if this is not a legal IRI
     */
    public static void checkIRI(String iri) {
        // IRIs can be null or empty
        if ((iri == null) || iri.length() == 0) {
            return;
        }

        for (int i = 0; i < iri.length(); i++) {
            char test = iri.charAt(i);
            if (!isIRICharacter(test)) {
                String msgNumber = "0x" + Integer.toHexString(test);
                if (test <= 0x09) {
                    msgNumber = "0x0" + Integer.toHexString(test);
                } 
                throw new MalformedURIException("IRIs cannot contain "
                    + msgNumber);
            } // end if
            if (test == '%') { 
            // must be followed by two hexadecimal digits
                   try {
                       char firstDigit = iri.charAt(i+1);
                       char secondDigit = iri.charAt(i+2);
                       if (!isHexDigit(firstDigit) 
                        || !isHexDigit(secondDigit)) {
                           throw new MalformedURIException(
                            "Percent signs in IRIs must be followed "
                            + "by exactly two hexadecimal digits.");    
                       }
          
                   }
                   catch (StringIndexOutOfBoundsException ex) {
                       throw new MalformedURIException(
                        "Percent signs in IRIs must be followed by "
                        + "exactly two hexadecimal digits.");    
                   }
            }
        } // end for

        // If we got here, everything is OK
        return;
    }

    /**
     * <p>
     * This will check the supplied data to see if it is legal for 
     * use as a <code>{@link ProcessingInstruction}</code> target.
     *</p>
     * 
     * @param target <code>String</code> target to check
     * 
     * @throws IllegalTargetException if this is not a legal target
     */
    public static void checkProcessingInstructionTarget(
      String target) {
        // Check basic XML name rules first
        try {
            checkNCName(target);
        }
        catch (NamespaceException ex) {
            throw new IllegalTargetException(target +
              "- Processing instruction targets cannot contain colons."
            );         
        }
        catch (IllegalNameException ex) {
            throw new IllegalTargetException(ex.getMessage());            
        }
        
        // Cannot begin with 'xml' in any case
        if (target.equalsIgnoreCase("xml")) {
            throw new IllegalTargetException(
              "Processing instructions cannot have a target of " 
              + "\"xml\" in any combination of case. (Note that the "
              + "\"<?xml ... ?>\" declaration at the beginning of a "
              + "document is not a processing instruction and should " 
              + "not  be added as one; it is written automatically "
              + "during output, e.g. by XMLOutputter.)");
        }

        return;
    }

   /**
     * <p>
     * This will check the supplied data to see if it is legal for use
     * as <code>{@link ProcessingInstruction}</code> data. Besides 
     * checking that all the characters are allowed in XML, this also 
     * checks that the data does not contain the PI end-string "?&gt;".
     * </p>
     * 
     * @param data <code>String</code> data to check
     * @throws IllegalDataException if this is not legal data
     */
    public static void checkProcessingInstructionData(String data) {
        
        // Check basic XML character rules first
        checkCharacterData(data);
        if (data.length() == 0) return;

        if (data.indexOf("?>") >= 0) {
            throw new IllegalDataException(
              "Processing instructions cannot contain the string \"?>\""
            );
        }
        if (data.indexOf('\r') >= 0) {
            throw new IllegalDataException(
              "Processing instructions cannot contain carriage returns"
            );
        }
        
        char first = data.charAt(0);
        if (first == ' ' || first == '\n' || first == '\t') {
            throw new IllegalDataException(
              "Processing instruction data cannot contain " +              "leading white space"
            );
        }

        return;
    }

    /**
     * <p>
     * Check the supplied data to see if it is legal for use as
     * <code>{@link Comment}</code> data.
     *  </p>
     * 
     * @param data <code>String</code> data to check
     */
    public static void checkCommentData(String data) {
        checkCharacterData(data);

        if (data.indexOf("--") != -1) {
            throw new IllegalDataException(
             "Comments cannot contain double hyphens (--).");
        }

        if (data.indexOf('\r') != -1) {
            throw new IllegalDataException(
             "Comments cannot contain carriage returns.");
        }

        if (data.startsWith("-")) {
            throw new IllegalDataException(
             "Comment data cannot start with a hyphen.");
        }

        if (data.endsWith("-")) {
            throw new IllegalDataException(
             "Comment data cannot end with a hyphen.");
        }

        return;
    }

    // [13] PubidChar ::= #x20 | #xD | #xA | [a-zA-Z0-9] |
    // [-'()+,./:=?;*#@$_%]
    private static boolean isXMLPublicIDCharacter(char c) {

        if (c >= 'a' && c <= 'z') return true;
        if (c >= '?' && c <= 'Z') return true;
        if (c >= '\'' && c <= ';') return true;

        if (c == ' ') return true;
        if (c == '!') return true;
        if (c == '=') return true;
        if (c == '#') return true;
        if (c == '$') return true;
        if (c == '_') return true;
        if (c == '%') return true;
        if (c == '\n') return true;
        if (c == '\r') return true;

        return false;
    }

    /**
     * <p>
     * This will ensure that the data for a public identifier
     * is legal.
     * </p>
     * 
     * @param publicID <code>String</code> public ID to check
     */
    public static void checkPublicID(String publicID) {

        if (publicID == null) return;
        // This indicates there is no public ID

        for (int i = 0; i < publicID.length(); i++) {
          char c = publicID.charAt(i);
          if (!isXMLPublicIDCharacter(c)) {
            throw new IllegalDataException(c 
             + " is not a legal character in public IDs");
          }
        }

        return;
    }


    /**
     * <p>
     * This will ensure that the data for a system literal
     * is legal.
     *  </p>
     * 
     * @param systemLiteral <code>String</code> system literal to check
     */
    public static void checkSystemLiteral(String systemLiteral) {

        if (systemLiteral == null) return;
        // This indicates there is no system ID

        if (systemLiteral.indexOf('\'') != -1
          && systemLiteral.indexOf('"') != -1) {
            throw new IllegalDataException(
             "System literals cannot simultaneously"
             + " contain both single and double quotes.");
        }
        else {
          checkURI(systemLiteral);
          if (systemLiteral.indexOf('#') >= 0) {
            throw new IllegalDataException(
              "System literals cannot contain fragment identifiers"
            );
          }
        }

        return;
    }

    /**
     * <p>
     * This is a utility function for sharing the 
     * base process of checking any XML name.
     * </p>
     * 
     * @param name <code>String</code> to check for XML name compliance
     */
    public static void checkXMLName(String name) {
        
        if (name == null) {
            throw new IllegalNameException(
              "XML names cannot be null"
            );
        }
        int nameLength = name.length();
        if (nameLength == 0) {
            throw new IllegalNameException(
              "XML names cannot be empty"
            );
        }

        char[] nameData = name.toCharArray();
        char first = nameData[0];
        if (!isXMLNameStartCharacter(first)) {
            throw new IllegalNameException(
              "XML names cannot begin with the character \"" 
              + first + "\""
            );
        }
       
        for (int i=1; i < nameLength; i++) {
            char c = nameData[i];
            if (!isXMLNameCharacter(c)) {
                String message = "XML names cannot contain the " +                  "character \"" + c + "\".";
                if (name.indexOf('/') != -1) {
                     message += "\nPerhaps you swapped a name and a namespace URI?";
                }
                throw new IllegalNameException(message);
            }
        }

        // We got here, so everything is OK
        return;
    }


    private static boolean[] C0Table = new boolean[20];
    
    static {
        C0Table['\n'] = true;
        C0Table['\r'] = true;
        C0Table['\t'] = true;
    }

    /**
     * <p>
     * This is a utility function for determining whether a specified 
     * character is a character according to production 2 of the 
     * XML 1.0 specification. Note: you have to decode surrogate pairs
     * before calling this function.
     * </p>
     * 
     * @param c <code>int</code> to check for XML compliance
     * @return true if <code>c</code> is a character, false otherwise
     */
    private static boolean isXMLCharacter(int c) {
        
        if (c <= 0xD7FF)  {
            if (c >= 0x20) return true;
            else {
                return C0Table[c];
                 /* if (c == '\n') return true;
                 if (c == '\r') return true;
                 if (c == '\t') return true;
                 return false; */
            }
        }

        if (c < 0xE000) return false;  if (c <= 0xFFFD) return true;
        if (c < 0x10000) return false;  if (c <= 0x10FFFF) return true;
        
        return false;
    }


    /**
     * <p>
     * This is a utility function for determining whether a specified 
     * character is a name character according to production 4 of the 
     * XML 1.0 specification.
     * </p>
     * 
     * @param c <code>char</code> to check for XML name compliance
     * @return true if <code>c</code> is a name character, 
     *     false otherwise
     */
    public static boolean isXMLNameCharacter(char c) {
    
      return (isXMLLetter(c) || isXMLDigit(c) || c == '.' || c == '-' 
                             || c == '_' || c == ':' || isXMLCombiningChar(c) 
                             || isXMLExtender(c));
    }

    /**
     * <p>
     * This is a utility function for determining whether a specified 
     * character is a legal name start character according to production 5
     * of the XML 1.0 specification. This production does allow names
     * to begin with colons which the Namespaces in XML Recommendation
     * disallows. 
     * </p>
     * 
     * @param c <code>char</code> to check for XML name start compliance
     * @return true if <code>c</code> is a name start character, 
     *     false otherwise
     */
    public static boolean isXMLNameStartCharacter(char c) {
    
      return (isXMLLetter(c) || c == '_' || c ==':');
    
    }

    /**
     * <p>
     * This is a utility function for determining whether a specified 
     * character is a letter or digit according to productions 84 
     * and 88 of the XML 1.0 specification.
     * </p>
     * 
     * @param c <code>char</code> to check
     * @return true if <code>c</code> is a letter or digit, 
     *     false otherwise
     */
    public static boolean isXMLLetterOrDigit(char c) {
    
      return (isXMLLetter(c) || isXMLDigit(c));
    
    }

    /**
     * <p>
     * This is a utility function for determining whether a specified 
     * character is a letter according to production 84 of the 
     * XML 1.0 specification.
     * </p>
     * 
     * @param c <code>char</code> to check for XML name compliance
     * @return <code>String</code> - true if it's a letter, false otherwise
     */
    public static boolean isXMLLetter(char c) {
        // Note that order is very important here.  The search proceeds 
        // from lowest to highest values, so that no searching occurs 
        // above the character's value.  BTW, the first line is equivalent to:
        // if (c >= 0x0041 && c <= 0x005A) return true;

        if (c < 0x0041) return false;  if (c <= 0x005a) return true;
        if (c < 0x0061) return false;  if (c <= 0x007A) return true;
        if (c < 0x00C0) return false;  if (c <= 0x00D6) return true;
        if (c < 0x00D8) return false;  if (c <= 0x00F6) return true;
        if (c < 0x00F8) return false;  if (c <= 0x00FF) return true;
        if (c < 0x0100) return false;  if (c <= 0x0131) return true;
        if (c < 0x0134) return false;  if (c <= 0x013E) return true;
        if (c < 0x0141) return false;  if (c <= 0x0148) return true;
        if (c < 0x014A) return false;  if (c <= 0x017E) return true;
        if (c < 0x0180) return false;  if (c <= 0x01C3) return true;
        if (c < 0x01CD) return false;  if (c <= 0x01F0) return true;
        if (c < 0x01F4) return false;  if (c <= 0x01F5) return true;
        if (c < 0x01FA) return false;  if (c <= 0x0217) return true;
        if (c < 0x0250) return false;  if (c <= 0x02A8) return true;
        if (c < 0x02BB) return false;  if (c <= 0x02C1) return true;
        if (c == 0x0386) return true;
        if (c < 0x0388) return false;  if (c <= 0x038A) return true;
        if (c == 0x038C) return true;
        if (c < 0x038E) return false;  if (c <= 0x03A1) return true;
        if (c < 0x03A3) return false;  if (c <= 0x03CE) return true;
        if (c < 0x03D0) return false;  if (c <= 0x03D6) return true;
        if (c == 0x03DA) return true;
        if (c == 0x03DC) return true;
        if (c == 0x03DE) return true;
        if (c == 0x03E0) return true;
        if (c < 0x03E2) return false;  if (c <= 0x03F3) return true;
        if (c < 0x0401) return false;  if (c <= 0x040C) return true;
        if (c < 0x040E) return false;  if (c <= 0x044F) return true;
        if (c < 0x0451) return false;  if (c <= 0x045C) return true;
        if (c < 0x045E) return false;  if (c <= 0x0481) return true;
        if (c < 0x0490) return false;  if (c <= 0x04C4) return true;
        if (c < 0x04C7) return false;  if (c <= 0x04C8) return true;
        if (c < 0x04CB) return false;  if (c <= 0x04CC) return true;
        if (c < 0x04D0) return false;  if (c <= 0x04EB) return true;
        if (c < 0x04EE) return false;  if (c <= 0x04F5) return true;
        if (c < 0x04F8) return false;  if (c <= 0x04F9) return true;
        if (c < 0x0531) return false;  if (c <= 0x0556) return true;
        if (c == 0x0559) return true;
        if (c < 0x0561) return false;  if (c <= 0x0586) return true;
        if (c < 0x05D0) return false;  if (c <= 0x05EA) return true;
        if (c < 0x05F0) return false;  if (c <= 0x05F2) return true;
        if (c < 0x0621) return false;  if (c <= 0x063A) return true;
        if (c < 0x0641) return false;  if (c <= 0x064A) return true;
        if (c < 0x0671) return false;  if (c <= 0x06B7) return true;
        if (c < 0x06BA) return false;  if (c <= 0x06BE) return true;
        if (c < 0x06C0) return false;  if (c <= 0x06CE) return true;
        if (c < 0x06D0) return false;  if (c <= 0x06D3) return true;
        if (c == 0x06D5) return true;
        if (c < 0x06E5) return false;  if (c <= 0x06E6) return true;
        if (c < 0x0905) return false;  if (c <= 0x0939) return true;
        if (c == 0x093D) return true;
        if (c < 0x0958) return false;  if (c <= 0x0961) return true;
        if (c < 0x0985) return false;  if (c <= 0x098C) return true;
        if (c < 0x098F) return false;  if (c <= 0x0990) return true;
        if (c < 0x0993) return false;  if (c <= 0x09A8) return true;
        if (c < 0x09AA) return false;  if (c <= 0x09B0) return true;
        if (c == 0x09B2) return true;
        if (c < 0x09B6) return false;  if (c <= 0x09B9) return true;
        if (c < 0x09DC) return false;  if (c <= 0x09DD) return true;
        if (c < 0x09DF) return false;  if (c <= 0x09E1) return true;
        if (c < 0x09F0) return false;  if (c <= 0x09F1) return true;
        if (c < 0x0A05) return false;  if (c <= 0x0A0A) return true;
        if (c < 0x0A0F) return false;  if (c <= 0x0A10) return true;
        if (c < 0x0A13) return false;  if (c <= 0x0A28) return true;
        if (c < 0x0A2A) return false;  if (c <= 0x0A30) return true;
        if (c < 0x0A32) return false;  if (c <= 0x0A33) return true;
        if (c < 0x0A35) return false;  if (c <= 0x0A36) return true;
        if (c < 0x0A38) return false;  if (c <= 0x0A39) return true;
        if (c < 0x0A59) return false;  if (c <= 0x0A5C) return true;
        if (c == 0x0A5E) return true;
        if (c < 0x0A72) return false;  if (c <= 0x0A74) return true;
        if (c < 0x0A85) return false;  if (c <= 0x0A8B) return true;
        if (c == 0x0A8D) return true;
        if (c < 0x0A8F) return false;  if (c <= 0x0A91) return true;
        if (c < 0x0A93) return false;  if (c <= 0x0AA8) return true;
        if (c < 0x0AAA) return false;  if (c <= 0x0AB0) return true;
        if (c < 0x0AB2) return false;  if (c <= 0x0AB3) return true;
        if (c < 0x0AB5) return false;  if (c <= 0x0AB9) return true;
        if (c == 0x0ABD) return true;
        if (c == 0x0AE0) return true;
        if (c < 0x0B05) return false;  if (c <= 0x0B0C) return true;
        if (c < 0x0B0F) return false;  if (c <= 0x0B10) return true;
        if (c < 0x0B13) return false;  if (c <= 0x0B28) return true;
        if (c < 0x0B2A) return false;  if (c <= 0x0B30) return true;
        if (c < 0x0B32) return false;  if (c <= 0x0B33) return true;
        if (c < 0x0B36) return false;  if (c <= 0x0B39) return true;
        if (c == 0x0B3D) return true;
        if (c < 0x0B5C) return false;  if (c <= 0x0B5D) return true;
        if (c < 0x0B5F) return false;  if (c <= 0x0B61) return true;
        if (c < 0x0B85) return false;  if (c <= 0x0B8A) return true;
        if (c < 0x0B8E) return false;  if (c <= 0x0B90) return true;
        if (c < 0x0B92) return false;  if (c <= 0x0B95) return true;
        if (c < 0x0B99) return false;  if (c <= 0x0B9A) return true;
        if (c == 0x0B9C) return true;
        if (c < 0x0B9E) return false;  if (c <= 0x0B9F) return true;
        if (c < 0x0BA3) return false;  if (c <= 0x0BA4) return true;
        if (c < 0x0BA8) return false;  if (c <= 0x0BAA) return true;
        if (c < 0x0BAE) return false;  if (c <= 0x0BB5) return true;
        if (c < 0x0BB7) return false;  if (c <= 0x0BB9) return true;
        if (c < 0x0C05) return false;  if (c <= 0x0C0C) return true;
        if (c < 0x0C0E) return false;  if (c <= 0x0C10) return true;
        if (c < 0x0C12) return false;  if (c <= 0x0C28) return true;
        if (c < 0x0C2A) return false;  if (c <= 0x0C33) return true;
        if (c < 0x0C35) return false;  if (c <= 0x0C39) return true;
        if (c < 0x0C60) return false;  if (c <= 0x0C61) return true;
        if (c < 0x0C85) return false;  if (c <= 0x0C8C) return true;
        if (c < 0x0C8E) return false;  if (c <= 0x0C90) return true;
        if (c < 0x0C92) return false;  if (c <= 0x0CA8) return true;
        if (c < 0x0CAA) return false;  if (c <= 0x0CB3) return true;
        if (c < 0x0CB5) return false;  if (c <= 0x0CB9) return true;
        if (c == 0x0CDE) return true;
        if (c < 0x0CE0) return false;  if (c <= 0x0CE1) return true;
        if (c < 0x0D05) return false;  if (c <= 0x0D0C) return true;
        if (c < 0x0D0E) return false;  if (c <= 0x0D10) return true;
        if (c < 0x0D12) return false;  if (c <= 0x0D28) return true;
        if (c < 0x0D2A) return false;  if (c <= 0x0D39) return true;
        if (c < 0x0D60) return false;  if (c <= 0x0D61) return true;
        if (c < 0x0E01) return false;  if (c <= 0x0E2E) return true;
        if (c == 0x0E30) return true;
        if (c < 0x0E32) return false;  if (c <= 0x0E33) return true;
        if (c < 0x0E40) return false;  if (c <= 0x0E45) return true;
        if (c < 0x0E81) return false;  if (c <= 0x0E82) return true;
        if (c == 0x0E84) return true;
        if (c < 0x0E87) return false;  if (c <= 0x0E88) return true;
        if (c == 0x0E8A) return true;
        if (c == 0x0E8D) return true;
        if (c < 0x0E94) return false;  if (c <= 0x0E97) return true;
        if (c < 0x0E99) return false;  if (c <= 0x0E9F) return true;
        if (c < 0x0EA1) return false;  if (c <= 0x0EA3) return true;
        if (c == 0x0EA5) return true;
        if (c == 0x0EA7) return true;
        if (c < 0x0EAA) return false;  if (c <= 0x0EAB) return true;
        if (c < 0x0EAD) return false;  if (c <= 0x0EAE) return true;
        if (c == 0x0EB0) return true;
        if (c < 0x0EB2) return false;  if (c <= 0x0EB3) return true;
        if (c == 0x0EBD) return true;
        if (c < 0x0EC0) return false;  if (c <= 0x0EC4) return true;
        if (c < 0x0F40) return false;  if (c <= 0x0F47) return true;
        if (c < 0x0F49) return false;  if (c <= 0x0F69) return true;
        if (c < 0x10A0) return false;  if (c <= 0x10C5) return true;
        if (c < 0x10D0) return false;  if (c <= 0x10F6) return true;
        if (c == 0x1100) return true;
        if (c < 0x1102) return false;  if (c <= 0x1103) return true;
        if (c < 0x1105) return false;  if (c <= 0x1107) return true;
        if (c == 0x1109) return true;
        if (c < 0x110B) return false;  if (c <= 0x110C) return true;
        if (c < 0x110E) return false;  if (c <= 0x1112) return true;
        if (c == 0x113C) return true;
        if (c == 0x113E) return true;
        if (c == 0x1140) return true;
        if (c == 0x114C) return true;
        if (c == 0x114E) return true;
        if (c == 0x1150) return true;
        if (c < 0x1154) return false;  if (c <= 0x1155) return true;
        if (c == 0x1159) return true;
        if (c < 0x115F) return false;  if (c <= 0x1161) return true;
        if (c == 0x1163) return true;
        if (c == 0x1165) return true;
        if (c == 0x1167) return true;
        if (c == 0x1169) return true;
        if (c < 0x116D) return false;  if (c <= 0x116E) return true;
        if (c < 0x1172) return false;  if (c <= 0x1173) return true;
        if (c == 0x1175) return true;
        if (c == 0x119E) return true;
        if (c == 0x11A8) return true;
        if (c == 0x11AB) return true;
        if (c < 0x11AE) return false;  if (c <= 0x11AF) return true;
        if (c < 0x11B7) return false;  if (c <= 0x11B8) return true;
        if (c == 0x11BA) return true;
        if (c < 0x11BC) return false;  if (c <= 0x11C2) return true;
        if (c == 0x11EB) return true;
        if (c == 0x11F0) return true;
        if (c == 0x11F9) return true;
        if (c < 0x1E00) return false;  if (c <= 0x1E9B) return true;
        if (c < 0x1EA0) return false;  if (c <= 0x1EF9) return true;
        if (c < 0x1F00) return false;  if (c <= 0x1F15) return true;
        if (c < 0x1F18) return false;  if (c <= 0x1F1D) return true;
        if (c < 0x1F20) return false;  if (c <= 0x1F45) return true;
        if (c < 0x1F48) return false;  if (c <= 0x1F4D) return true;
        if (c < 0x1F50) return false;  if (c <= 0x1F57) return true;
        if (c == 0x1F59) return true;
        if (c == 0x1F5B) return true;
        if (c == 0x1F5D) return true;
        if (c < 0x1F5F) return false;  if (c <= 0x1F7D) return true;
        if (c < 0x1F80) return false;  if (c <= 0x1FB4) return true;
        if (c < 0x1FB6) return false;  if (c <= 0x1FBC) return true;
        if (c == 0x1FBE) return true;
        if (c < 0x1FC2) return false;  if (c <= 0x1FC4) return true;
        if (c < 0x1FC6) return false;  if (c <= 0x1FCC) return true;
        if (c < 0x1FD0) return false;  if (c <= 0x1FD3) return true;
        if (c < 0x1FD6) return false;  if (c <= 0x1FDB) return true;
        if (c < 0x1FE0) return false;  if (c <= 0x1FEC) return true;
        if (c < 0x1FF2) return false;  if (c <= 0x1FF4) return true;
        if (c < 0x1FF6) return false;  if (c <= 0x1FFC) return true;
        if (c == 0x2126) return true;
        if (c < 0x212A) return false;  if (c <= 0x212B) return true;
        if (c == 0x212E) return true;
        if (c < 0x2180) return false;  if (c <= 0x2182) return true;
        if (c == 0x3007) return true;                          // ideographic
        if (c < 0x3021) return false;  if (c <= 0x3029) return true;  // ideo
        if (c < 0x3041) return false;  if (c <= 0x3094) return true;
        if (c < 0x30A1) return false;  if (c <= 0x30FA) return true;
        if (c < 0x3105) return false;  if (c <= 0x312C) return true;
        if (c < 0x4E00) return false;  if (c <= 0x9FA5) return true;  // ideo
        if (c < 0xAC00) return false;  if (c <= 0xD7A3) return true;
      
        return false;
        
    }

    /**
     * <p>
     * This is a utility function for determining whether a specified 
     * character is a combining character according to production 87
     * of the XML 1.0 specification.
     * </p>
     * 
     * @param c <code>char</code> to check
     * @return true if <code>c</code> is a combining character, false otherwise
     */
    public static boolean isXMLCombiningChar(char c) {
        // CombiningChar
        if (c < 0x0300) return false;  if (c <= 0x0345) return true;
        if (c < 0x0360) return false;  if (c <= 0x0361) return true;
        if (c < 0x0483) return false;  if (c <= 0x0486) return true;
        if (c < 0x0591) return false;  if (c <= 0x05A1) return true;
                                       
        if (c < 0x05A3) return false;  if (c <= 0x05B9) return true;
        if (c < 0x05BB) return false;  if (c <= 0x05BD) return true;
        if (c == 0x05BF) return true;
        if (c < 0x05C1) return false;  if (c <= 0x05C2) return true;
                                       
        if (c == 0x05C4) return true;
        if (c < 0x064B) return false;  if (c <= 0x0652) return true;
        if (c == 0x0670) return true;
        if (c < 0x06D6) return false;  if (c <= 0x06DC) return true;
                                       
        if (c < 0x06DD) return false;  if (c <= 0x06DF) return true;
        if (c < 0x06E0) return false;  if (c <= 0x06E4) return true;
        if (c < 0x06E7) return false;  if (c <= 0x06E8) return true;
                                       
        if (c < 0x06EA) return false;  if (c <= 0x06ED) return true;
        if (c < 0x0901) return false;  if (c <= 0x0903) return true;
        if (c == 0x093C) return true;
        if (c < 0x093E) return false;  if (c <= 0x094C) return true;
                                       
        if (c == 0x094D) return true;
        if (c < 0x0951) return false;  if (c <= 0x0954) return true;
        if (c < 0x0962) return false;  if (c <= 0x0963) return true;
        if (c < 0x0981) return false;  if (c <= 0x0983) return true;
                                       
        if (c == 0x09BC) return true;
        if (c == 0x09BE) return true;
        if (c == 0x09BF) return true;
        if (c < 0x09C0) return false;  if (c <= 0x09C4) return true;
        if (c < 0x09C7) return false;  if (c <= 0x09C8) return true;
                                       
        if (c < 0x09CB) return false;  if (c <= 0x09CD) return true;
        if (c == 0x09D7) return true;
        if (c < 0x09E2) return false;  if (c <= 0x09E3) return true;
        if (c == 0x0A02) return true;
        if (c == 0x0A3C) return true;
                                       
        if (c == 0x0A3E) return true;
        if (c == 0x0A3F) return true;
        if (c < 0x0A40) return false;  if (c <= 0x0A42) return true;
        if (c < 0x0A47) return false;  if (c <= 0x0A48) return true;
                                       
        if (c < 0x0A4B) return false;  if (c <= 0x0A4D) return true;
        if (c < 0x0A70) return false;  if (c <= 0x0A71) return true;
        if (c < 0x0A81) return false;  if (c <= 0x0A83) return true;
        if (c == 0x0ABC) return true;
                                       
        if (c < 0x0ABE) return false;  if (c <= 0x0AC5) return true;
        if (c < 0x0AC7) return false;  if (c <= 0x0AC9) return true;
        if (c < 0x0ACB) return false;  if (c <= 0x0ACD) return true;
                                       
        if (c < 0x0B01) return false;  if (c <= 0x0B03) return true;
        if (c == 0x0B3C) return true;
        if (c < 0x0B3E) return false;  if (c <= 0x0B43) return true;
        if (c < 0x0B47) return false;  if (c <= 0x0B48) return true;
                                       
        if (c < 0x0B4B) return false;  if (c <= 0x0B4D) return true;
        if (c < 0x0B56) return false;  if (c <= 0x0B57) return true;
        if (c < 0x0B82) return false;  if (c <= 0x0B83) return true;
                                       
        if (c < 0x0BBE) return false;  if (c <= 0x0BC2) return true;
        if (c < 0x0BC6) return false;  if (c <= 0x0BC8) return true;
        if (c < 0x0BCA) return false;  if (c <= 0x0BCD) return true;
        if (c == 0x0BD7) return true;
                                       
        if (c < 0x0C01) return false;  if (c <= 0x0C03) return true;
        if (c < 0x0C3E) return false;  if (c <= 0x0C44) return true;
        if (c < 0x0C46) return false;  if (c <= 0x0C48) return true;
                                       
        if (c < 0x0C4A) return false;  if (c <= 0x0C4D) return true;
        if (c < 0x0C55) return false;  if (c <= 0x0C56) return true;
        if (c < 0x0C82) return false;  if (c <= 0x0C83) return true;
                                       
        if (c < 0x0CBE) return false;  if (c <= 0x0CC4) return true;
        if (c < 0x0CC6) return false;  if (c <= 0x0CC8) return true;
        if (c < 0x0CCA) return false;  if (c <= 0x0CCD) return true;
                                       
        if (c < 0x0CD5) return false;  if (c <= 0x0CD6) return true;
        if (c < 0x0D02) return false;  if (c <= 0x0D03) return true;
        if (c < 0x0D3E) return false;  if (c <= 0x0D43) return true;
                                       
        if (c < 0x0D46) return false;  if (c <= 0x0D48) return true;
        if (c < 0x0D4A) return false;  if (c <= 0x0D4D) return true;
        if (c == 0x0D57) return true;
        if (c == 0x0E31) return true;
                                       
        if (c < 0x0E34) return false;  if (c <= 0x0E3A) return true;
        if (c < 0x0E47) return false;  if (c <= 0x0E4E) return true;
        if (c == 0x0EB1) return true;
        if (c < 0x0EB4) return false;  if (c <= 0x0EB9) return true;
                                       
        if (c < 0x0EBB) return false;  if (c <= 0x0EBC) return true;
        if (c < 0x0EC8) return false;  if (c <= 0x0ECD) return true;
        if (c < 0x0F18) return false;  if (c <= 0x0F19) return true;
        if (c == 0x0F35) return true;
                                       
        if (c == 0x0F37) return true;
        if (c == 0x0F39) return true;
        if (c == 0x0F3E) return true;
        if (c == 0x0F3F) return true;
        if (c < 0x0F71) return false;  if (c <= 0x0F84) return true;
                                       
        if (c < 0x0F86) return false;  if (c <= 0x0F8B) return true;
        if (c < 0x0F90) return false;  if (c <= 0x0F95) return true;
        if (c == 0x0F97) return true;
        if (c < 0x0F99) return false;  if (c <= 0x0FAD) return true;
                                       
        if (c < 0x0FB1) return false;  if (c <= 0x0FB7) return true;
        if (c == 0x0FB9) return true;
        if (c < 0x20D0) return false;  if (c <= 0x20DC) return true;
        if (c == 0x20E1) return true;
                                       
        if (c < 0x302A) return false;  if (c <= 0x302F) return true;
        if (c == 0x3099) return true;
        if (c == 0x309A) return true; 
        
        return false;
        
    }
    
    /**
     * <p>
     * This is a utility function for determining whether 
     * a specified character is an extender according to 
     * production 88 of the XML 1.0 specification.
     * </p>
     * 
     * @param c <code>char</code> to check
     * @return true if it's an extender, false otherwise
     */
    public static boolean isXMLExtender(char c) {

        if (c < 0x00B6) return false;  // quick short circuit

        // Extenders                               
        if (c == 0x00B7) return true;
        if (c == 0x02D0) return true;
        if (c == 0x02D1) return true;
        if (c == 0x0387) return true;
        if (c == 0x0640) return true;
        if (c == 0x0E46) return true;
        if (c == 0x0EC6) return true;
        if (c == 0x3005) return true;
                                       
        if (c < 0x3031) return false;  if (c <= 0x3035) return true;
        if (c < 0x309D) return false;  if (c <= 0x309E) return true;
        if (c < 0x30FC) return false;  if (c <= 0x30FE) return true;
        
        return false;
        
    }
      
    /**
     * <p>
     * This is a utility function for determining whether a specified 
     * Unicode character
     * is a digit according to production 88 of the XML 1.0 specification.
     * </p>
     * 
     * @param c <code>char</code> to check for XML digit compliance.
     * @return true if it's a digit, false otherwise
     */
    public static boolean isXMLDigit(char c) {
      
        if (c < 0x0030) return false;  if (c <= 0x0039) return true;
        if (c < 0x0660) return false;  if (c <= 0x0669) return true;
        if (c < 0x06F0) return false;  if (c <= 0x06F9) return true;
        if (c < 0x0966) return false;  if (c <= 0x096F) return true;
                                       
        if (c < 0x09E6) return false;  if (c <= 0x09EF) return true;
        if (c < 0x0A66) return false;  if (c <= 0x0A6F) return true;
        if (c < 0x0AE6) return false;  if (c <= 0x0AEF) return true;
                                       
        if (c < 0x0B66) return false;  if (c <= 0x0B6F) return true;
        if (c < 0x0BE7) return false;  if (c <= 0x0BEF) return true;
        if (c < 0x0C66) return false;  if (c <= 0x0C6F) return true;
                                       
        if (c < 0x0CE6) return false;  if (c <= 0x0CEF) return true;
        if (c < 0x0D66) return false;  if (c <= 0x0D6F) return true;
        if (c < 0x0E50) return false;  if (c <= 0x0E59) return true;
                                       
        if (c < 0x0ED0) return false;  if (c <= 0x0ED9) return true;
        if (c < 0x0F20) return false;  if (c <= 0x0F29) return true; 
      
        return false;
    }

    // old version of code
/*    public static boolean isURICharacter(char c) {

        if (c <= 0x0020) return false;  if (c <= 0x0021) return true;
        if (c <= 0x0022) return false;  if (c <= 0x003B) return true;
        if (c <= 0x003C) return false;  if (c <= 0x003D) return true;

        if (c <= 0x003E) return false;  if (c <= 0x005A) return true;
        if (c <= 0x005E) return false;  if (c <= 0x005F) return true;
        if (c <= 0x0060) return false;  if (c <= 0x007A) return true;

        if (c < 0x007D) return false;  if (c <= 0x007E) return true;

        return false;
    } */

    /**
     * <p>
     * This is a utility function for determining whether a specified
     * Unicode character is a hexadecimal digit as defined in RFC 2396;
     * that is, one of the ASCII characters 0-9, a-f, or A-F
     * </p>
     * 
     * @param c  to check for hex digit.
     * @return  true if it's allowed, false otherwise.
     */
    public static boolean isHexDigit(char c) {

    // I suspect most characters passed to this method will be
    // correct hexadecimal digits, so I test for the true cases
    // first. If this proves to be a performance bottleneck 
    // a switch statement or lookup table
    // might optimize this. 
        if (c >= '0' && c <= '9') return true; 
        if (c >= 'A' && c <= 'F') return true; 
        if (c >= 'a' && c <= 'f') return true; 

        return false;
    }

    /**
     * <p>
     * Checks a string to see if it is an RFC 2396 absolute URI.
     * Absolute URIs may not contain fragment identifiers.
     * </p>
     * 
     * @param uri <code>String</code> to check
     * 
     * @throws MalformedURIException if this is not an absolute URI
     */
    public static void checkAbsoluteURI(String uri) {
        
        // All legal URIs are legal URI references
        checkAbsoluteURIReference(uri);
        
        // Only difference is a URI can't contain a fragment ID
        if (uri.indexOf('#') >= 0) {
            throw new MalformedURIException(
              "URIs (as opposed to URI references) "
              + "cannot contain fragement identifiers."
            ); 
        }

    }


    /**
     * <p>
     * Checks a string to see if it is an RFC 2396 absolute URI
     * reference. URI references can contain fragment identifiers.
     * </p>
     * 
     * @param uri <code>String</code> to check
     * 
     * @throws MalformedURIException if this is not a legal 
     *     URI reference
     */
    public static void checkAbsoluteURIReference(String uri) {
        
        // Next test is necessary if we're really testing URI 
        // references but not for namespace URIs
        if (uri == null || uri.length() == 0) {
            throw new MalformedURIException(
              "Absolute URIs cannot be empty"
            );   
        }
        
        if (!isAlpha(uri.charAt(0))) {
            throw new MalformedURIException(
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
                throw new MalformedURIException(
                  "URI schemes cannot contain " + c);   
             }
        }
        
        if (colonLocation == -1) {
             throw new MalformedURIException(
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
                        throw new MalformedURIException(
                          ("Bad hexadecimal escape sequence %" 
                          + c1) + c2);   
                    }                      
                }
                catch (IndexOutOfBoundsException ex) {
                    throw new MalformedURIException(
                      "Bad hexadecimal escape sequence %");   
                }
            }
            else if (c == '#') {
                numberSharps++;
                if (numberSharps > 1) {
                    throw new MalformedURIException(
                      "Multiple fragments #"
                    );
                }
            }
            else if (!isURICharacter(c)) {
                throw new MalformedURIException(
                  "URIs cannot contain " + c
                );
            }
            
        }       
   
    }

    
    private static boolean isAlpha(char c) {
        if (c >= 'a' && c <= 'z') return true;
        if (c >= 'A' && c <= 'Z') return true;
        return false;
    } 
    
    private static boolean isSchemeCharacter(char c) {
        if (c >= 'a' && c <= 'z') return true;
        if (c >= 'A' && c <= 'Z') return true;
        if (c >= '0' && c <= '9') return true;
        if (c == '-') return true;
        if (c == '.') return true;
        if (c == '+') return true;
        return false;
    }   
    
    /**
     * <p>
     * This is a utility function for determining whether 
     * a specified Unicode character is legal in URI  
     * references as determined by RFC 2396.
     * </p>
     * 
     * @param c <code>char</code> to check for URI reference compliance
     * @return true if it's allowed, false otherwise.
     * 
     */
    private static boolean isURICharacter(int c) {
        if (c >= 'a' && c <= 'z') return true;
        if (c >= 'A' && c <= 'Z') return true;
        if (c >= '0' && c <= '9') return true;
        if (c == '/') return true;
        if (c == '-') return true;
        if (c == '.') return true;
        if (c == '=') return true;
        if (c == '&') return true;
        if (c == '%') return true;
        if (c == '?') return true;
        // Since we're really checking for URI references
        if (c == '#') return true;
        if (c == ':') return true;
        if (c == '@') return true;
        if (c == '+') return true;
        if (c == '$') return true;
        if (c == ',') return true;

        if (c == '_') return true;
        if (c == '!') return true;
        if (c == '~') return true;
        if (c == '*') return true;
        if (c == '\'') return true;
        if (c == '(') return true;
        if (c == ')') return true;
        // These next two were reallowed by RFC 2732
        // for IPv6 addresses
        if (c == '[') return true;
        if (c == ']') return true;
        return false;
    } 

    /**
     * <p>
     * This is a utility function for determining 
     * whether a specified Unicode character
     * is legal in an IRI references as determined by 
     * http://www.w3.org/International/iri-edit/draft-duerst-iri.html
     * </p>
     * 
     * @param c <code>char</code> to check for IRI 
     *          reference compliance.
     * @return true if it's allowed, false otherwise.
     * 
     */
    private static boolean isIRICharacter(int c) {
        
        if (c < ' ') return false; // control characters not allowed
        if (c <  0x80)   return isURICharacter(c);
        if (c <= 0xD7FF) return true;
        if (c <  0xF900) return false;
        if (c <= 0xFDCF) return true;
        if (c <  0xFDF0) return false;
        if (c <= 0xFFEF) return true;
        
        if (c <  0x10000) return false;
        if (c <= 0x1FFFD) return true;
        if (c <  0x20000) return false;
        if (c <= 0x2FFFD) return true;
        if (c <  0x30000) return false;
        if (c <= 0x3FFFD) return true;
        if (c <  0x40000) return false;
        if (c <= 0x4FFFD) return true;
        if (c <  0x50000) return false;
        if (c <= 0x5FFFD) return true;
        if (c <  0x60000) return false;
        if (c <= 0x6FFFD) return true;
        if (c <  0x70000) return false;
        if (c <= 0x7FFFD) return true;
        if (c <  0x80000) return false;
        if (c <= 0x8FFFD) return true;
        if (c <  0x90000) return false;
        if (c <= 0x9FFFD) return true;
        if (c <  0xA0000) return false;
        if (c <= 0xAFFFD) return true;
        if (c <  0xB0000) return false;
        if (c <= 0xBFFFD) return true;
        if (c <  0xC0000) return false;
        if (c <= 0xCFFFD) return true;
        if (c <  0xC0000) return false;
        if (c <= 0xCFFFD) return true;
        if (c <  0xD0000) return false;
        if (c <= 0xDFFFD) return true;
        if (c <  0xE1000) return false;
        if (c <= 0xEFFFD) return true;

        return false;
    } 

}