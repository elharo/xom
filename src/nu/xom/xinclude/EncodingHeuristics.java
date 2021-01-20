/* Copyright 2002, 2003, 2005, 2006 Elliotte Rusty Harold
   
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
   elharo@ibiblio.org. Please include the word "XOM" in the
   subject line. The XOM home page is located at https://xom.nu/
*/

package nu.xom.xinclude;

import java.io.IOException;
import java.io.InputStream;

/**
 * <p>
 * <code>EncodingHeuristics</code> reads from a stream
 * (which should be buffered) and attempts to guess
 * what the encoding of the text in the stream is.
 * Byte order marks are stripped from the stream.
 * If it fails to determine the type of the encoding,
 * it returns the default UTF-8. 
 * </p>
 *
 *
 * @author Elliotte Rusty Harold
 * @version 1.2d1
 */
class EncodingHeuristics {

  // No instances allowed
  private EncodingHeuristics() {}

  
  /**
    * <p>
    * This utility method uses a variety of heuristics to
    * attempt to guess the encoding from the initial
    * characters.
    * </p>
    *
    * @param  in      <code>InputStream</code> to read from. 
    * @return String  The name of the encoding.
    * @throws IOException if the stream cannot be reset back 
    *      to where it was when the method was invoked.
    */    
    public static String readEncodingFromStream(InputStream in)
      throws IOException {
     
        // This may fail if there are a lot of space 
        // characters before the end of the encoding declaration
        in.mark(1024);
        
        try {
          // Lots of things can go wrong here. If any do,  
          // return "UTF-8" as the default.
            int byte1 = in.read();
            int byte2 = in.read();
            if (byte1 == 0xFE && byte2 == 0xFF) {
                // Don't reset because the byte order mark should not be 
                // included per section 4.3 of the XInclude spec
                return "UnicodeBig";          
            }        
            else if (byte1 == 0xFF && byte2 == 0xFE) {
                // Don't reset because the byte order mark should not be 
                // included per section 4.3 of the XInclude spec
                return "UnicodeLittle";        
            }        
            
            /* In accordance with the Character Model,
               when the text format is a Unicode encoding, the XInclude 
               processor must fail the inclusion when the text in the 
               selected range is non-normalized. When transcoding 
               characters to a Unicode encoding from a legacy encoding,
               a normalizing transcoder must be used. */
                    
            int byte3 = in.read();
            // check for UTF-8 byte order mark
            if (byte1 == 0xEF && byte2 == 0xBB && byte3 == 0xBF) {
                // Don't reset because the byte order mark should not be 
                // included per section 4.3 of the XInclude spec
                return "UTF-8";          
            }
            
            int byte4 = in.read();
            if (byte1 == 0x00 
              && byte2 == 0x00 && byte3 == 0xFE && byte4 == 0xFF) {
                // Don't reset because the byte order mark should not be 
                // included per section 4.3 of the XInclude spec
                // Most Java VMs don't support this next one
                return "UTF32BE";          
            }
            else if (byte1 == 0x00 && byte2 == 0x00 
              && byte3 == 0xFF && byte4 == 0xFE) {
                // Don't reset because the byte order mark should not be 
                // included per section 4.3 of the XInclude spec
                // Most Java VMs don't support this next one
                return "UTF32LE";         
            }
            
            // no byte order mark present; first character must be 
            // less than sign or white space
            // Let's look for less-than signs first
            if (byte1 == 0x00 && byte2 == 0x00 
              && byte3 == 0x00 && byte4 == '<') {
                in.reset();
                return "UTF32BE";          
            }
            else if (byte1 == '<' && byte2 == 0x00 
              && byte3 == 0x00 && byte4 == 0x00) {
                in.reset();
                return "UTF32LE";          
            }
            else if (byte1 == 0x00 && byte2 == '<' 
              && byte3 == 0x00 && byte4 == '?') {
                in.reset();
                return "UnicodeBigUnmarked";          
            }
            else if (byte1 == '<' && byte2 == 0x00 
              && byte3 == '?' && byte4 == 0x00) {
                in.reset();
                return "UnicodeLittleUnmarked";          
            }
            else if (byte1 == '<' && byte2 == '?' 
              && byte3 == 'x' && byte4 == 'm') {
              // ASCII compatible, must read encoding declaration. 
              // 1024 bytes will be far enough to read most 
              // XML declarations
              byte[] data = new byte[1024];
              data[0] = (byte) byte1;
              data[1] = (byte) byte2;
              data[2] = (byte) byte3;
              data[3] = (byte) byte4;
              int length = in.read(data, 4, 1020) + 4;
              // Use Latin-1 (ISO-8859-1) because it's ASCII compatible
              // and all byte sequences are legal Latin-1 sequences 
              // so I don't have to worry about encoding errors if I  
              // slip past the end of the XML/text declaration
              String declaration = new String(data, 0, length, "8859_1");
              
              // If any of these throw a 
              // StringIndexOutOfBoundsException,
              // we just fall into the catch block and return null
              // since this can't be well-formed XML
              String encoding = findEncodingDeclaration(declaration);
              in.reset();
              return encoding;
              
            }
            else if (byte1 == 0x4C && byte2 == 0x6F 
              && byte3 == 0xA7 && byte4 == 0x94) {
              // EBCDIC compatible, must read encoding declaration 
              byte[] buffer = new byte[1016];
              for (int i = 0; i < buffer.length; i++) {
                  int c = in.read();
                  if (c == -1) break;
                  buffer[i] = (byte) c;
              }
              in.reset();
              // Most EBCDIC encodings are compatible with Cp037 over
              // the range we care about
              return findEncodingDeclaration(new String(buffer, "Cp037"));
            }
        
        }   
        catch (IOException ex) {
            in.reset();
            return "UTF-8";        
        }
        catch (RuntimeException ex) {
            in.reset();
            return "UTF-8";        
        }
        
        // no XML or text declaration present
        in.reset();
        return "UTF-8";
        
    }

    
    private static String findEncodingDeclaration(String declaration)
        throws IOException {
        
          int position = declaration.indexOf("encoding") + 8;
          char c;
          // get rid of white space before equals sign
          while (true) {
              c = declaration.charAt(position++);
              if (c !=' ' && c != '\t' && c != '\r' && c != '\n') {
                  break;
              }
          }
          if (c != '=') { // malformed
              throw new IOException("Couldn't determine encoding");
          }
          // get rid of white space after equals sign
          while (true) {
              c = declaration.charAt(position++);
              if (c !=' ' && c != '\t' && c != '\r' && c != '\n') {
                  break;
              }
          }
          char delimiter = c;
          if (delimiter != '\'' && delimiter != '"') { // malformed
              return "UTF-8";
          }
          // now positioned to read encoding name
          StringBuilder encodingName = new StringBuilder();
          while (true) {
              c = declaration.charAt(position++);
              if (c == delimiter) break;
              encodingName.append(c);
          }
          return encodingName.toString();
          
    }

}