/* Copyright 2002-2005 Elliotte Rusty Harold
   
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

import java.io.IOException;
import java.io.Writer;

/**
 * <p>
 *   This class is responsible for writing strings with the 
 *   necessary escaping for their context.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.1b7
 *
 */
abstract class TextWriter {

    protected final Writer out;
    protected final String encoding;
    
    private String lineSeparator = "\r\n";
    // true if the user has requested a specific 
    // line separator
            boolean lineSeparatorSet = false;
    private boolean inDocType = false;
    private int     maxLength = 0;
    private int     indent = 0;
    private String  indentString = "";
    protected int   column = 0;
    // Is an xml:space="preserve" attribute in scope?
    private boolean preserveSpace = false;
    protected boolean normalize = false;
    
    protected TextWriter(Writer out, String encoding) {
        this.out = out; 
        this.encoding = encoding;
    }
    
    
    void reset() {
        column = 0; 
        fakeIndents = 0; 
        lastCharacterWasSpace = false;
        skipFollowingLinefeed = false; 
    }

    
    protected boolean lastCharacterWasSpace = false;
    
    /**
     * Indicates whether a linefeed is just half of a \r\n pair
     * used for a line break.
     */
    protected boolean skipFollowingLinefeed = false;
    
    // Needed for memory between calls.
    private char highSurrogate;
    
    
    private boolean isHighSurrogate(int c) {
        return c >= 0xD800 && c <= 0xDBFF;  
    }
    
    
    private boolean isLowSurrogate(int c) {
        return c >= 0xDC00 && c <= 0xDFFF;  
    }
    
    
    final void writePCDATA(char c) throws IOException {
        
        switch(c) {
            case '\r':
                if (!adjustingWhiteSpace()  && !lineSeparatorSet) {
                    out.write("&#x0D;");
                    column += 6;
                    justBroke=false;
                }
                else {
                    breakLine();
                    lastCharacterWasSpace = true;              
                }
                skipFollowingLinefeed = true;
                break;
            case 14: // unreachable
            case 15: // unreachable
            case 16: // unreachable
            case 17: // unreachable
            case 18: // unreachable
            case 19: // unreachable
            case 20: // unreachable
            case 21: // unreachable
            case 22: // unreachable
            case 23: // unreachable
            case 24: // unreachable
            case 25: // unreachable
            case 26: // unreachable
            case 27: // unreachable
            case 28: // unreachable
            case 29: // unreachable
            case 30: // unreachable
            case 31: // unreachable
                throw new XMLException("Bad character snuck into document");
            case ' ':
                write(c);
                break;
            case '!':
                write(c);
                break;
            case '"':
                write(c);
                break;
            case '#':
                write(c);
                break;
            case '$':
                write(c);
                break;
            case '%':
                write(c);
                break;
            case '&':
                out.write("&amp;");
                column += 5;
                lastCharacterWasSpace = false;
                skipFollowingLinefeed = false; 
                justBroke = false;
                break;
            case '\'':
                write(c);
                break;
            case '(':
                write(c);
                break;
            case ')':
                write(c);
                break;
            case '*':
                write(c);
                break;
            case '+':
                write(c);
                break;
            case ',':
                write(c);
                break;
            case '-':
                write(c);
                break;
            case '.':
                write(c);
                break;
            case '/':
                write(c);
                break;
            case '0':
                write(c);
                break;
            case '1':
                write(c);
                break;
            case '2':
                write(c);
                break;
            case '3':
                write(c);
                break;
            case '4':
                write(c);
                break;
            case '5':
                write(c);
                break;
            case '6':
                write(c);
                break;
            case '7':
                write(c);
                break;
            case '8':
                write(c);
                break;
            case '9':
                write(c);
                break;
            case ':':
                write(c);
                break;
            case ';':
                write(c);
                break;
            case '<':
                out.write("&lt;");
                column += 4;
                lastCharacterWasSpace = false; 
                skipFollowingLinefeed = false;
                justBroke = false;
                break;
            case '=':
                write(c);
                break;
            case '>':
                out.write("&gt;");
                column += 4;
                lastCharacterWasSpace = false;  
                skipFollowingLinefeed = false;
                justBroke = false;
                break;
            default:
                if (needsEscaping(c)) writeEscapedChar(c);
                else write(c);
        }
        
    }
    
    
    private void writeEscapedChar(char c) throws IOException {

        if (isHighSurrogate(c)) {
            //store and wait for low half
            highSurrogate = c;
        }
        else if (isLowSurrogate(c)) {
            // decode and write entity reference
            // I am assuming here that nothing allows the
            // text to be created with a malformed surrogate
            // pair such as a low surrogate that is not immediately
            // preceded by a high surrogate
            int uchar = UnicodeUtil.combineSurrogatePair(highSurrogate, c);
            String s = "&#x" + Integer.toHexString(uchar).toUpperCase() + ';';
            out.write(s);
            column += s.length();
            lastCharacterWasSpace = false;
            skipFollowingLinefeed = false;
            justBroke = false;
        }
        else {
            String s = "&#x" + Integer.toHexString(c).toUpperCase() + ';';
            out.write(s);
            column += s.length();
            lastCharacterWasSpace = false;
            skipFollowingLinefeed = false;
            justBroke=false;
        }
        
    }


    private boolean adjustingWhiteSpace() {
        return maxLength > 0 || indent > 0;
    }

    
    // This is the same as writePCDATA except that it
    // also needs to escape " as &quot; and tab as "&#x09;".
    // I'm not escaping the single quote because Serializer
    // always uses double quotes to contain 
    // values.
    final void writeAttributeValue(char c) 
      throws IOException {
        
        switch(c) {
            // Handle white space that the parser might normalize
            // on roundtrip. We only escape them if the serializer
            // is not adjusting white space; that is indent is 0
            // and maxLength is 0.
            case '\t':
                if (!adjustingWhiteSpace()) {
                    out.write("&#x09;");
                    column += 6;
                    lastCharacterWasSpace = true;
                    skipFollowingLinefeed = false;
                    justBroke=false;
                }
                else {
                    write(' ');
                }
                break;
            case '\n':
                if (skipFollowingLinefeed) {
                    skipFollowingLinefeed = false;
                    return;
                }
                else if (adjustingWhiteSpace()) {
                    out.write(" ");
                    lastCharacterWasSpace = true;
                    justBroke=false;
                }
                else {
                    if (lineSeparatorSet) {
                        escapeBreakLine();
                    }
                    else {
                        out.write("&#x0A;");
                        column += 6; 
                        justBroke=false;
                    }
                    lastCharacterWasSpace = true;
                }
                break;
            case 11:
                // unreachable
            case 12:
                // unreachable
                throw new XMLException("Bad character snuck into document");
            case '\r':
                if (adjustingWhiteSpace()) {
                    out.write(" ");
                    lastCharacterWasSpace = true;
                    skipFollowingLinefeed = true;  
                    justBroke=false;
                }
                else {
                    if (lineSeparatorSet) {
                        escapeBreakLine();
                        skipFollowingLinefeed = true;
                    }
                    else {
                        out.write("&#x0D;");
                        column += 6;
                        justBroke=false;
                    }
                }
                break;
            case 14:
                // unreachable
            case 15:
                // unreachable
            case 16:
                // unreachable
            case 17:
                // unreachable
            case 18:
                // unreachable
            case 19:
                // unreachable
            case 20:
                // unreachable
            case 21:
                // unreachable
            case 22:
                // unreachable
            case 23:
                // unreachable
            case 24:
                // unreachable
            case 25:
                // unreachable
            case 26:
                // unreachable
            case 27:
                // unreachable
            case 28:
                // unreachable
            case 29:
                // unreachable
            case 30:
                // unreachable
            case 31:
                // unreachable
                throw new XMLException("Bad character snuck into document");
            case ' ':
                write(c);
                break;
            case '!':
                write(c);
                break;
            case '"':
                out.write("&quot;");
                column += 6;
                lastCharacterWasSpace = false;
                skipFollowingLinefeed = false;
                justBroke=false;
                break;
            case '#':
                write(c);
                break;
            case '$':
                write(c);
                break;
            case '%':
                write(c);
                break;
            case '&':
                out.write("&amp;");
                column += 5;
                lastCharacterWasSpace = false;
                skipFollowingLinefeed = false; 
                justBroke = false;
                break;
            case '\'':
                write(c);
                break;
            case '(':
                write(c);
                break;
            case ')':
                write(c);
                break;
            case '*':
                write(c);
                break;
            case '+':
                write(c);
                break;
            case ',':
                write(c);
                break;
            case '-':
                write(c);
                break;
            case '.':
                write(c);
                break;
            case '/':
                write(c);
                break;
            case '0':
                write(c);
                break;
            case '1':
                write(c);
                break;
            case '2':
                write(c);
                break;
            case '3':
                write(c);
                break;
            case '4':
                write(c);
                break;
            case '5':
                write(c);
                break;
            case '6':
                write(c);
                break;
            case '7':
                write(c);
                break;
            case '8':
                write(c);
                break;
            case '9':
                write(c);
                break;
            case ':':
                write(c);
                break;
            case ';':
                write(c);
                break;
            case '<':
                out.write("&lt;");
                column += 4;
                lastCharacterWasSpace = false; 
                skipFollowingLinefeed = false;
                justBroke = false;
                break;
            case '=':
                write(c);
                break;
            case '>':
                out.write("&gt;");
                column += 4;
                lastCharacterWasSpace = false;  
                skipFollowingLinefeed = false;
                justBroke = false;
                break;
            default:
                if (needsEscaping(c)) writeEscapedChar(c);
                else write(c);
        }

    }

    
    // XXX We might be able to optimize this by using switch statements
    // in the methods that call this to separate out the special cases.
    // --\n, \t, space, etc.--and passing them to a different method
    // thsu avoiding the if tests here. See if this method shows up as 
    // a HotSpot in profiling.
    private void write(char c) throws IOException {
        
      // Carriage returns are completely handled by
      // writePCDATA and writeAttributeValue. They never
      // enter this method.
      if ((c == ' ' || c == '\n' || c == '\t')) {
            if (needsBreak()) {
                breakLine();
                skipFollowingLinefeed = false;
            }
            else if (preserveSpace || (indent <= 0 && maxLength <= 0)) {
                // We're neither indenting nor wrapping
                // so we need to preserve white space
                if (c == ' ' ||  c == '\t') {
                    out.write(c);
                    skipFollowingLinefeed = false;
                    column++;
                    justBroke=false;
                } 
                else { // (c == '\n')
                    if (!lineSeparatorSet ||
                        !skipFollowingLinefeed) {
                        writeLineSeparator(c);
                    } 
                    skipFollowingLinefeed = false;
                    column = 0;
                }   
            }
            else if (!lastCharacterWasSpace) {
                out.write(' ');
                column++;
                skipFollowingLinefeed = false;
                justBroke=false;
            }
            lastCharacterWasSpace = true;
        }
        else {  
            out.write(c);
            // don't increment column for high surrogate, only low surrogate
            if (c < 0xd800 || c > 0xDBFF) column++; 
            lastCharacterWasSpace = false;
            skipFollowingLinefeed = false;
            justBroke=false;
        } 
      
    }

    
    private void writeLineSeparator(char c) 
      throws IOException {
        
        if (!inDocType && (!lineSeparatorSet || preserveSpace)) out.write(c);
        else if (lineSeparator.equals("\r\n")) {
            out.write("\r\n");    
        } 
        else if (lineSeparator.equals("\n")) {
            out.write('\n');    
        } 
        else  { // lineSeparator.equals("\r")) 
            out.write('\r');    
        } 
        // Remember, there are only three possible line separators

    }


    private boolean needsBreak() {
        
        if (maxLength <= 0 || preserveSpace) return false;
        // Better algorithm needed: Should look ahead in the 
        // stream, see if there's a white space character 
        // between here and the maxLength, Then again, simple is good.
        // Here we just assume there's probably space somewhere
        // within the next ten characters 
        
        return column >= maxLength - 10; 
        
    }
    
    
    protected boolean justBroke = false;
    
    boolean justBroke() {
        return justBroke;
    }
    
    
    final void breakLine() throws IOException {
        
        out.write(lineSeparator);
        out.write(indentString);
        column = indentString.length();
        lastCharacterWasSpace = true;
        justBroke = true;
        
    }
    
    
    private final void escapeBreakLine() throws IOException {
        
        if ("\n".equals(lineSeparator)) {
            out.write("&#x0A;");
            column += 6;
        }
        else if ("\r\n".equals(lineSeparator)) {
            out.write("&#x0D;&#x0A;");
            column += 12;
        }
        else {
            out.write("&#x0D;");
            column += 6;
        }
        lastCharacterWasSpace = true;
        
    }
    
    
    // Note that when this method is called directly, then 
    // normalization is not performed on c. Currently this is 
    // only called for ASCII characters like <, >, and the space, 
    // which should be OK
    protected final void writeMarkup(char c) throws IOException {
        
        if (needsEscaping(c)) {
            throw new UnavailableCharacterException(c, encoding);
        }
        write(c);   

    }
    
    // XXX should we have a special package protected 
    // method to be used only for ASCII characters we know don't need escaping or
    // normalization such as <, /, A-Z, etc.?

    
    void writePCDATA(String s) throws IOException {
        
        s = normalize(s);
        int length = s.length();
        for (int i=0; i < length; i++) {
            writePCDATA(s.charAt(i));
        }   
        
    }


    void writeAttributeValue(String s) 
      throws IOException {
        
        s = normalize(s);
        int length = s.length();
        for (int i=0; i < length; i++) {
            writeAttributeValue(s.charAt(i));
        }
        
    }

    
    void writeMarkup(String s) throws IOException {
        
        s = normalize(s);
        int length = s.length();
        for (int i=0; i < length; i++) {
            writeMarkup(s.charAt(i));
        }
        
    }
    
    
     protected String normalize(String s) {

        if (normalize) {
            return UnicodeUtil.normalize(s);
        }
        return s;
        
    }
    


   boolean isIndenting() {
        return indentString.length() > 0;   
    }


    private int fakeIndents = 0;
    
    private final static String _128_SPACES="                                                                                                                                ";
    private final static int    _128 = 128;
    
    void incrementIndent() {
        
        if (indent == 0) return;
        
        String newIndent;
        int length = indentString.length() + indent;
        if (indentString.length() + indent < _128) {
            newIndent = _128_SPACES.substring(0, length);
        }
        else {
            StringBuffer sb = new StringBuffer(length);
            sb.append(_128_SPACES);
            for (int i = _128; i < length; i++) {
                sb.append(' ');
            }
            newIndent = sb.toString();
        }
        
        // limit maximum indent to half of maximum line length
        if (maxLength > 0 && newIndent.length() > maxLength / 2) {
            fakeIndents++; 
        }
        else this.indentString = newIndent;
        
    }
    
    
    void decrementIndent() {
        
        if (indent == 0) return;
        else if (fakeIndents > 0) fakeIndents--;
        else {
            indentString = indentString.substring(
              0, indentString.length()-indent
            );
        }
        
    }


    String getEncoding() {
        return this.encoding;   
    }

    
    /**
     * <p>
     * Returns the String used as a line separator.
     * This is always "\n", "\r", or "\r\n".
     * </p>
     * 
     * @return the line separator
     */
    String getLineSeparator() {
        return lineSeparator;
    }

    
    /**
     * <p>
     * Sets the lineSeparator. This  
     * can only be one of the three
     * strings "\n", "\r", or "\r\n".
     * All other values are forbidden.
     * </p>
     * 
     * @param lineSeparator the lineSeparator to set
     * 
     * @throws IllegalArgumentException if you attempt to use 
     *      any line separator other than "\n", "\r", or "\r\n".
     * 
     */
    void setLineSeparator(String lineSeparator) {
        
        if (lineSeparator.equals("\n") 
          || lineSeparator.equals("\r")
          || lineSeparator.equals("\r\n")) { 
            this.lineSeparator = lineSeparator;
            this.lineSeparatorSet = true;
        }
        else {
            throw new IllegalArgumentException(
              "Illegal Line Separator");
        }  
        
    }

    
    void setInDocType(boolean inDocType) {
        this.inDocType = inDocType;  
    }

    
    /**
     * <p>
     * Returns the number of spaces this serializer indents.
     * </p>
     * 
     * @return the number of spaces this serializer indents
     */
    int getIndent() {
        return indent;
    }


    /**
     * <p>
     * Returns the maximum line length.
     * </p>
     * 
     * @return the maximum line length.
     */
    int getMaxLength() {
        return maxLength;
    }

    /**
     * <p>
     * Sets the suggested maximum line length for this serializer.
     * In some circumstances this may not be respected.
     * </p>
     * 
     * @param maxLength the maxLength to set
     */
    void setMaxLength(int maxLength) {
        if (maxLength < 0) maxLength = 0;
        this.maxLength = maxLength;
    }

    
   /**
     * <p>
     * Sets the number of spaces to indent each successive level in the
     *  hierarchy. Use 0 for no extra indenting.
     * </p>
     * 
     * @param indent the indent to set
     */
    void setIndent(int indent) {
        this.indent = indent;
    }


    void flush() throws IOException {
        out.flush();    
    }

    
    abstract boolean needsEscaping(char c);

    
    /**
     * <p>
     *  Used to track the current status of xml:space.
     *  This is false by default, unless an xml:space="preserve"
     *  attribute is in-scope. When such an attribute is in-scope,
     *  white space is not adjusted even if indenting and/or
     *  a maximum line length has been requested.
     * </p>
     *
     * 
     * @return true if an <code>xml:space="true"</code> attribute 
     *      is in-scope
     */
    boolean isPreserveSpace() {
        return preserveSpace;
    }

    
    /**
     * @param preserveSpace whether to preserve all white space
     */
    void setPreserveSpace(boolean preserveSpace) {
        this.preserveSpace = preserveSpace;
    }

    
    /**
     * @return the current column number
     */
    int getColumnNumber() {
        return this.column;
    }

    
    /**
     * <p>
     *   If true, this property indicates serialization will
     *   perform Unicode normalization on all data using normalization
     *   form C (NFC). Performing Unicode normalization
     *   does change the document's infoset. 
     *   The default is false; do not normalize.
     * </p>
     * 
     * <p>
     *   This feature has not yet been benchmarked or optimized.
     *   It may result in substantially slower code. 
     * </p>
     * 
     * @param normalize true if normalization is performed; 
     *     false if it isn't.
     */
    void setNFC(boolean normalize) {
        this.normalize = normalize;   
    }

    
    /**
     * <p>
     *   If true, this property indicates serialization will
     *   perform Unicode normalization on all data using normalization
     *   form C (NFC). The default is false; do not normalize.
     * </p>
     * 
     * @return true if this serialization performs Unicode 
     *     normalization; false if it doesn't.
     */
    boolean getNFC() {
        return this.normalize;   
    }

    
}