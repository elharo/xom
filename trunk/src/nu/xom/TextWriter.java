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

import java.io.IOException;
import java.io.Writer;

import com.ibm.icu.text.Normalizer;

/**
 * <p>
 *   This class is responsible for writing strings with the 
 *   necessary escaping for their context.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0a5
 *
 */
abstract class TextWriter {

    protected Writer out;
    protected String encoding;
    
    private String lineSeparator = "\r\n";
    // true if the user has requested a specific 
    // line separator
    private boolean lineSeparatorSet = false;
    private int     maxLength = 0;
    private int     indent = 0;
    private String  indentString = "";
    private int     column = 0;
    // Is an xml:space="preserve" attribute in scope?
    private boolean preserveSpace = false;
    private boolean normalize = false;
    
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

    
    private boolean lastCharacterWasSpace = false;
    
    /**
     * Indicates whether a linefeed is just half of a \r\n pair
     * used for a line break.
     */
    private boolean skipFollowingLinefeed = false;
    
    private int highSurrogate;
    
    
    private boolean isHighSurrogate(int c) {
        return c >= 0xD800 && c <= 0xDBFF;  
    }
    
    
    private boolean isLowSurrogate(int c) {
        return c >= 0xDC00 && c <= 0xDFFF;  
    }
    
    
    final void writePCDATA(char c) 
    
      throws IOException {
        if (needsEscaping(c)) {
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
                int high =  highSurrogate & 0x7FF;
                int low = c - 0xDC00;
                int highShifted = high << 10;
                int combined = highShifted | low; 
                int uchar = combined + 0x10000;
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
        else if (c == '&') {
            out.write("&amp;");
            column += 5;
            lastCharacterWasSpace = false;
            skipFollowingLinefeed = false; 
            justBroke = false;
        }
        else if (c == '<') {
            out.write("&lt;");
            column += 4;
            lastCharacterWasSpace = false; 
            skipFollowingLinefeed = false;
            justBroke = false;
        }
        else if (c == '>') {
            out.write("&gt;");
            column += 4;
            lastCharacterWasSpace = false;  
            skipFollowingLinefeed = false;
            justBroke = false;
        }
        else if (c == '\r') {
            if (!adjustingWhiteSpace()  && !lineSeparatorSet) {
                out.write("&#x0D;");
                column += 6;
                justBroke=false;
            }
            else if (!adjustingWhiteSpace()  && lineSeparatorSet) {
                escapeBreakLine();
            }
            else {
                breakLine();
                lastCharacterWasSpace = true;              
            }
            skipFollowingLinefeed = true;
        }
        else {
            write(c);   
        }
        
    }
    
    
    private boolean adjustingWhiteSpace() {
        return maxLength > 0 || indent > 0;
    }

    
    // This is the same as writePCDATA except that it
    // also needs to escape " as &quot;
    // I'm not escaping the single quote because Serializer
    // always uses double quotes to contain 
    // values.
    final void writeAttributeValue(char c) 
      throws IOException {
        
        if (needsEscaping(c)) {
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
                int high =  highSurrogate & 0x7FF;
                int low = c - 0xDC00;
                int highShifted = high << 10;
                int combined = highShifted | low; 
                int uchar = combined + 0x10000;
                String s = "&#x" + Integer.toHexString(uchar).toUpperCase() + ';';
                out.write(s);
                column += s.length();
                lastCharacterWasSpace = false;
                skipFollowingLinefeed = false;
                justBroke=false;
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
        // Handle white space that the parser might normalize
        // on roundtrip. We only escape them if the serializer
        // is not adjusting white space; that is indent is 0
        // and maxLength is 0.
        else if (c == '\t' && !adjustingWhiteSpace()) {
            out.write("&#x09;");
            column += 6;
            lastCharacterWasSpace = true;
            skipFollowingLinefeed = false;
            justBroke=false;
        }
        else if (c == '\n') {
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
        }
        else if (c == '"') {
            out.write("&quot;");
            column += 6;
            lastCharacterWasSpace = false;
            skipFollowingLinefeed = false;
            justBroke=false;
        }
        else if (c == '\r') {
            if (adjustingWhiteSpace()) {
                out.write(" ");
                lastCharacterWasSpace = true;
                skipFollowingLinefeed = true;  
                justBroke=false;
            }
            else {
                if (lineSeparatorSet) {
                    escapeBreakLine();
                }
                else {
                    out.write("&#x0D;");
                    column += 6;
                    justBroke=false;
                }
                skipFollowingLinefeed = true; 
            }
        }
        // Handle characters that are illegal in attribute values
        else if (c == '&') {
            out.write("&amp;");
            column += 5;
            lastCharacterWasSpace = false;
            skipFollowingLinefeed = false;
            justBroke=false;
        }
        else if (c == '<') {
            out.write("&lt;");
            column += 4;
            lastCharacterWasSpace = false; 
            skipFollowingLinefeed = false; 
            justBroke=false;
        }
        else if (c == '>') {
            out.write("&gt;");
            column += 4;
            lastCharacterWasSpace = false;  
            skipFollowingLinefeed = false;
            justBroke=false;
        }
        else {
            write(c);  
        }            
    }

    
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
        
        if (!lineSeparatorSet || preserveSpace) out.write(c);
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
    
    
    private boolean justBroke = false;
    
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
    
    
    final void escapeBreakLine() throws IOException {
        
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

    
    final void writePCDATA(String s) throws IOException {
        
        if (normalize) {
            s = Normalizer.normalize(s, Normalizer.NFC);   
        }
        for (int i=0; i < s.length(); i++) {
            writePCDATA(s.charAt(i));
        }   
        
    }

    
    final void writeAttributeValue(String s) 
      throws IOException {
        if (normalize) {
            s = Normalizer.normalize(s, Normalizer.NFC);   
        }
        for (int i=0; i < s.length(); i++) {
            writeAttributeValue(s.charAt(i));
        }   
    }

    
    final void writeMarkup(String s) throws IOException {
        if (normalize) {
            s = Normalizer.normalize(s, Normalizer.NFC);   
        }
        for (int i=0; i < s.length(); i++) {
            writeMarkup(s.charAt(i));
        }   
    }
    
    
    boolean isIndenting() {
        return indentString.length() > 0;   
    }


    private int fakeIndents = 0;
    
    void incrementIndent() {
        
        StringBuffer newIndent = new StringBuffer(indentString);
        for (int i = 0; i < indent; i++) {
            newIndent.append(' ');
        }
        
        // limit maximum indent to half of max length
        if (maxLength > 0 && newIndent.length() > maxLength / 2) {
            fakeIndents++; 
        }
        else this.indentString = newIndent.toString();
        
    }
    
    
    void decrementIndent() {
        if (fakeIndents > 0) fakeIndents --;        
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
     * @return true if an <ocde>xml:space="true"</code> attribute 
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
