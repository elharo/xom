/* Copyright 2005 Elliotte Rusty Harold
   
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

import java.io.InputStream;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

/**
 * <p>
 *   
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.1a3
 *
 */
final class UnicodeUtil {
    
    
    private static final int CANONICAL_COMBINING_CLASS_NOT_REORDERED = 0;
    private static final int CANONICAL_COMBINING_CLASS_OVERLAY = 1;
    private static final int CANONICAL_COMBINING_CLASS_NUKTA = 7;
    private static final int CANONICAL_COMBINING_CLASS_KANA_VOICING = 8;
    private final static int CANONICAL_COMBINING_CLASS_VIRAMA = 9;
    private final static int CANONICAL_COMBINING_CLASS_10 = 10;
    private final static int CANONICAL_COMBINING_CLASS_11 = 11;
    private final static int CANONICAL_COMBINING_CLASS_12 = 12;
    private final static int CANONICAL_COMBINING_CLASS_13 = 13;
    private final static int CANONICAL_COMBINING_CLASS_14 = 14;
    private final static int CANONICAL_COMBINING_CLASS_15 = 15;
    private final static int CANONICAL_COMBINING_CLASS_16 = 16;
    private final static int CANONICAL_COMBINING_CLASS_17 = 17;
    private final static int CANONICAL_COMBINING_CLASS_18 = 18;
    private final static int CANONICAL_COMBINING_CLASS_19 = 19;
    private final static int CANONICAL_COMBINING_CLASS_20 = 20;
    private final static int CANONICAL_COMBINING_CLASS_21 = 21;
    private final static int CANONICAL_COMBINING_CLASS_22 = 22;
    private final static int CANONICAL_COMBINING_CLASS_23 = 23;
    private final static int CANONICAL_COMBINING_CLASS_24 = 24;
    private final static int CANONICAL_COMBINING_CLASS_25 = 25;
    private final static int CANONICAL_COMBINING_CLASS_26 = 26;
    private final static int CANONICAL_COMBINING_CLASS_27 = 27;
    private final static int CANONICAL_COMBINING_CLASS_28 = 28;
    private final static int CANONICAL_COMBINING_CLASS_29 = 29;
    private final static int CANONICAL_COMBINING_CLASS_30 = 30;
    private final static int CANONICAL_COMBINING_CLASS_31 = 31;
    private final static int CANONICAL_COMBINING_CLASS_32 = 32;
    private final static int CANONICAL_COMBINING_CLASS_33 = 33;
    private final static int CANONICAL_COMBINING_CLASS_34 = 34;
    private final static int CANONICAL_COMBINING_CLASS_35 = 35;
    private final static int CANONICAL_COMBINING_CLASS_36 = 36;
    private final static int CANONICAL_COMBINING_CLASS_84 = 84;
    private final static int CANONICAL_COMBINING_CLASS_91 = 91;
    private final static int CANONICAL_COMBINING_CLASS_103 = 103;
    private final static int CANONICAL_COMBINING_CLASS_107 = 107;
    private final static int CANONICAL_COMBINING_CLASS_118 = 118;
    private final static int CANONICAL_COMBINING_CLASS_122 = 122;
    private final static int CANONICAL_COMBINING_CLASS_129 = 129;
    private final static int CANONICAL_COMBINING_CLASS_130 = 130;
    private final static int CANONICAL_COMBINING_CLASS_132 = 132;
    private final static int CANONICAL_COMBINING_CLASS_ATTACHED_BELOW = 202;
    private final static int CANONICAL_COMBINING_CLASS_ATTACHED_ABOVE_RIGHT = 216;
    private final static int CANONICAL_COMBINING_CLASS_BELOW_LEFT = 218;
    private final static int CANONICAL_COMBINING_CLASS_BELOW = 220;
    private final static int CANONICAL_COMBINING_CLASS_BELOW_RIGHT = 222;
    private final static int CANONICAL_COMBINING_CLASS_LEFT = 224;
    private final static int CANONICAL_COMBINING_CLASS_RIGHT = 226;
    private final static int CANONICAL_COMBINING_CLASS_ABOVE_LEFT = 228;
    private final static int CANONICAL_COMBINING_CLASS_ABOVE = 230;
    private final static int CANONICAL_COMBINING_CLASS_ABOVE_RIGHT = 232;
    private final static int CANONICAL_COMBINING_CLASS_DOUBLE_BELOW = 233;
    private final static int CANONICAL_COMBINING_CLASS_DOUBLE_ABOVE = 234;
    private final static int CANONICAL_COMBINING_CLASS_IOTA_SUBSCRIPT = 240;
    
    private static int HI_SURROGATE_START  = 0xD800;
    private static int HI_SURROGATE_END    = 0xDBFF;
    private static int LOW_SURROGATE_START = 0xDC00;
    
    
    private static Map compositions;
    
    
    private static void loadCompositions() {
       
        ClassLoader loader = UnicodeUtil.class.getClassLoader();
        // FIXME use Verifier logic for loading this
        if (loader == null) {
            loader = Thread.currentThread().getContextClassLoader();
        }
        
        InputStream in = loader.getResourceAsStream("nu/xom/comp.xml");
        if (in == null) return;
        
        try {
            Document doc = (new Builder()).build(in);
            Element root = doc.getRootElement();
            Elements mappings = root.getChildElements();
            compositions = new HashMap();
            for (int i = 0; i < mappings.size(); i++) {
                String composed = mappings.get(i).getFirstChildElement("composed").getValue();
                String decomposed = mappings.get(i).getFirstChildElement("decomposed").getValue();
                compositions.put(decomposed, composed);
            }
        }
        catch (Exception ex) {
            throw new RuntimeException("Broken XOM installation: "
              + "could not load nu/xom/comp.xml", ex);
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
    

    static boolean isStarter(int character) {
        return getCombiningClass(character) == 0;
    }

    
    static int getCombiningClass(int c) {
        // ???? optimize
        
        if (c >= 0x0000 && c <= 0x001F) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0020) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0021 && c <= 0x0023) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0024) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0025 && c <= 0x0027) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0028) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0029) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x002A) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x002B) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x002C) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x002D) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x002E && c <= 0x002F) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0030 && c <= 0x0039) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x003A && c <= 0x003B) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x003C && c <= 0x003E) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x003F && c <= 0x0040) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0041 && c <= 0x005A) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x005B) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x005C) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x005D) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x005E) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x005F) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0060) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0061 && c <= 0x007A) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x007B) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x007C) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x007D) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x007E) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x007F && c <= 0x009F) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x00A0) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x00A1) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x00A2 && c <= 0x00A5) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x00A6 && c <= 0x00A7) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x00A8) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x00A9) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x00AA) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x00AB) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x00AC) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x00AD) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x00AE) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x00AF) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x00B0) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x00B1) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x00B2 && c <= 0x00B3) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x00B4) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x00B5) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x00B6) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x00B7) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x00B8) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x00B9) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x00BA) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x00BB) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x00BC && c <= 0x00BE) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x00BF) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x00C0 && c <= 0x00D6) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x00D7) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x00D8 && c <= 0x00F6) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x00F7) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x00F8 && c <= 0x01BA) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x01BB) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x01BC && c <= 0x01BF) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x01C0 && c <= 0x01C3) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x01C4 && c <= 0x0236) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0250 && c <= 0x02AF) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x02B0 && c <= 0x02C1) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x02C2 && c <= 0x02C5) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x02C6 && c <= 0x02D1) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x02D2 && c <= 0x02DF) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x02E0 && c <= 0x02E4) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x02E5 && c <= 0x02ED) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x02EE) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x02EF && c <= 0x02FF) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x034F) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0374 && c <= 0x0375) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x037A) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x037E) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0384 && c <= 0x0385) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0386) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0387) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0388 && c <= 0x038A) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x038C) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x038E && c <= 0x03A1) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x03A3 && c <= 0x03CE) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x03D0 && c <= 0x03F5) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x03F6) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x03F7 && c <= 0x03FB) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0400 && c <= 0x0481) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0482) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0488 && c <= 0x0489) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x048A && c <= 0x04CE) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x04D0 && c <= 0x04F5) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x04F8 && c <= 0x04F9) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0500 && c <= 0x050F) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0531 && c <= 0x0556) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0559) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x055A && c <= 0x055F) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0561 && c <= 0x0587) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0589) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x058A) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x05BE) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x05C0) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x05C3) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x05D0 && c <= 0x05EA) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x05F0 && c <= 0x05F2) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x05F3 && c <= 0x05F4) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0600 && c <= 0x0603) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x060C && c <= 0x060D) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x060E && c <= 0x060F) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x061B) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x061F) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0621 && c <= 0x063A) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0640) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0641 && c <= 0x064A) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0660 && c <= 0x0669) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x066A && c <= 0x066D) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x066E && c <= 0x066F) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0671 && c <= 0x06D3) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x06D4) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x06D5) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x06DD) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x06DE) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x06E5 && c <= 0x06E6) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x06E9) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x06EE && c <= 0x06EF) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x06F0 && c <= 0x06F9) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x06FA && c <= 0x06FC) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x06FD && c <= 0x06FE) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x06FF) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0700 && c <= 0x070D) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x070F) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0710) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0712 && c <= 0x072F) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x074D && c <= 0x074F) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0780 && c <= 0x07A5) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x07A6 && c <= 0x07B0) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x07B1) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0901 && c <= 0x0902) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0903) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0904 && c <= 0x0939) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x093D) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x093E && c <= 0x0940) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0941 && c <= 0x0948) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0949 && c <= 0x094C) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0950) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0958 && c <= 0x0961) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0962 && c <= 0x0963) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0964 && c <= 0x0965) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0966 && c <= 0x096F) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0970) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0981) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0982 && c <= 0x0983) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0985 && c <= 0x098C) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x098F && c <= 0x0990) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0993 && c <= 0x09A8) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x09AA && c <= 0x09B0) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x09B2) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x09B6 && c <= 0x09B9) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x09BD) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x09BE && c <= 0x09C0) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x09C1 && c <= 0x09C4) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x09C7 && c <= 0x09C8) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x09CB && c <= 0x09CC) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x09D7) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x09DC && c <= 0x09DD) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x09DF && c <= 0x09E1) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x09E2 && c <= 0x09E3) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x09E6 && c <= 0x09EF) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x09F0 && c <= 0x09F1) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x09F2 && c <= 0x09F3) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x09F4 && c <= 0x09F9) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x09FA) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0A01 && c <= 0x0A02) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0A03) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0A05 && c <= 0x0A0A) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0A0F && c <= 0x0A10) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0A13 && c <= 0x0A28) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0A2A && c <= 0x0A30) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0A32 && c <= 0x0A33) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0A35 && c <= 0x0A36) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0A38 && c <= 0x0A39) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0A3E && c <= 0x0A40) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0A41 && c <= 0x0A42) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0A47 && c <= 0x0A48) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0A4B && c <= 0x0A4C) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0A59 && c <= 0x0A5C) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0A5E) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0A66 && c <= 0x0A6F) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0A70 && c <= 0x0A71) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0A72 && c <= 0x0A74) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0A81 && c <= 0x0A82) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0A83) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0A85 && c <= 0x0A8D) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0A8F && c <= 0x0A91) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0A93 && c <= 0x0AA8) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0AAA && c <= 0x0AB0) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0AB2 && c <= 0x0AB3) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0AB5 && c <= 0x0AB9) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0ABD) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0ABE && c <= 0x0AC0) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0AC1 && c <= 0x0AC5) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0AC7 && c <= 0x0AC8) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0AC9) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0ACB && c <= 0x0ACC) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0AD0) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0AE0 && c <= 0x0AE1) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0AE2 && c <= 0x0AE3) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0AE6 && c <= 0x0AEF) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0AF1) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0B01) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0B02 && c <= 0x0B03) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0B05 && c <= 0x0B0C) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0B0F && c <= 0x0B10) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0B13 && c <= 0x0B28) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0B2A && c <= 0x0B30) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0B32 && c <= 0x0B33) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0B35 && c <= 0x0B39) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0B3D) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0B3E) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0B3F) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0B40) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0B41 && c <= 0x0B43) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0B47 && c <= 0x0B48) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0B4B && c <= 0x0B4C) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0B56) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0B57) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0B5C && c <= 0x0B5D) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0B5F && c <= 0x0B61) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0B66 && c <= 0x0B6F) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0B70) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0B71) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0B82) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0B83) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0B85 && c <= 0x0B8A) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0B8E && c <= 0x0B90) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0B92 && c <= 0x0B95) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0B99 && c <= 0x0B9A) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0B9C) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0B9E && c <= 0x0B9F) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0BA3 && c <= 0x0BA4) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0BA8 && c <= 0x0BAA) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0BAE && c <= 0x0BB5) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0BB7 && c <= 0x0BB9) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0BBE && c <= 0x0BBF) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0BC0) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0BC1 && c <= 0x0BC2) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0BC6 && c <= 0x0BC8) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0BCA && c <= 0x0BCC) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0BD7) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0BE7 && c <= 0x0BEF) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0BF0 && c <= 0x0BF2) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0BF3 && c <= 0x0BF8) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0BF9) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0BFA) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0C01 && c <= 0x0C03) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0C05 && c <= 0x0C0C) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0C0E && c <= 0x0C10) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0C12 && c <= 0x0C28) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0C2A && c <= 0x0C33) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0C35 && c <= 0x0C39) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0C3E && c <= 0x0C40) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0C41 && c <= 0x0C44) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0C46 && c <= 0x0C48) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0C4A && c <= 0x0C4C) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0C60 && c <= 0x0C61) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0C66 && c <= 0x0C6F) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0C82 && c <= 0x0C83) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0C85 && c <= 0x0C8C) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0C8E && c <= 0x0C90) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0C92 && c <= 0x0CA8) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0CAA && c <= 0x0CB3) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0CB5 && c <= 0x0CB9) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0CBD) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0CBE) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0CBF) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0CC0 && c <= 0x0CC4) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0CC6) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0CC7 && c <= 0x0CC8) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0CCA && c <= 0x0CCB) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0CCC) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0CD5 && c <= 0x0CD6) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0CDE) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0CE0 && c <= 0x0CE1) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0CE6 && c <= 0x0CEF) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0D02 && c <= 0x0D03) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0D05 && c <= 0x0D0C) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0D0E && c <= 0x0D10) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0D12 && c <= 0x0D28) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0D2A && c <= 0x0D39) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0D3E && c <= 0x0D40) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0D41 && c <= 0x0D43) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0D46 && c <= 0x0D48) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0D4A && c <= 0x0D4C) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0D57) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0D60 && c <= 0x0D61) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0D66 && c <= 0x0D6F) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0D82 && c <= 0x0D83) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0D85 && c <= 0x0D96) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0D9A && c <= 0x0DB1) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0DB3 && c <= 0x0DBB) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0DBD) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0DC0 && c <= 0x0DC6) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0DCF && c <= 0x0DD1) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0DD2 && c <= 0x0DD4) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0DD6) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0DD8 && c <= 0x0DDF) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0DF2 && c <= 0x0DF3) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0DF4) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0E01 && c <= 0x0E30) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0E31) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0E32 && c <= 0x0E33) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0E34 && c <= 0x0E37) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0E3F) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0E40 && c <= 0x0E45) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0E46) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0E47) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0E4C && c <= 0x0E4E) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0E4F) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0E50 && c <= 0x0E59) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0E5A && c <= 0x0E5B) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0E81 && c <= 0x0E82) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0E84) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0E87 && c <= 0x0E88) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0E8A) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0E8D) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0E94 && c <= 0x0E97) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0E99 && c <= 0x0E9F) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0EA1 && c <= 0x0EA3) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0EA5) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0EA7) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0EAA && c <= 0x0EAB) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0EAD && c <= 0x0EB0) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0EB1) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0EB2 && c <= 0x0EB3) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0EB4 && c <= 0x0EB7) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0EBB && c <= 0x0EBC) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0EBD) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0EC0 && c <= 0x0EC4) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0EC6) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0ECC && c <= 0x0ECD) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0ED0 && c <= 0x0ED9) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0EDC && c <= 0x0EDD) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0F00) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0F01 && c <= 0x0F03) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0F04 && c <= 0x0F12) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0F13 && c <= 0x0F17) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0F1A && c <= 0x0F1F) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0F20 && c <= 0x0F29) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0F2A && c <= 0x0F33) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0F34) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0F36) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0F38) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0F3A) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0F3B) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0F3C) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0F3D) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0F3E && c <= 0x0F3F) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0F40 && c <= 0x0F47) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0F49 && c <= 0x0F6A) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0F73) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0F75 && c <= 0x0F79) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0F7E) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0F7F) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0F81) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0F85) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0F88 && c <= 0x0F8B) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0F90 && c <= 0x0F97) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0F99 && c <= 0x0FBC) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0FBE && c <= 0x0FC5) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0FC7 && c <= 0x0FCC) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x0FCF) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1000 && c <= 0x1021) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1023 && c <= 0x1027) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1029 && c <= 0x102A) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x102C) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x102D && c <= 0x1030) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x1031) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x1032) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x1036) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x1038) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1040 && c <= 0x1049) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x104A && c <= 0x104F) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1050 && c <= 0x1055) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1056 && c <= 0x1057) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1058 && c <= 0x1059) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x10A0 && c <= 0x10C5) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x10D0 && c <= 0x10F8) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x10FB) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1100 && c <= 0x1159) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x115F && c <= 0x11A2) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x11A8 && c <= 0x11F9) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1200 && c <= 0x1206) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1208 && c <= 0x1246) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x1248) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x124A && c <= 0x124D) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1250 && c <= 0x1256) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x1258) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x125A && c <= 0x125D) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1260 && c <= 0x1286) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x1288) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x128A && c <= 0x128D) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1290 && c <= 0x12AE) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x12B0) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x12B2 && c <= 0x12B5) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x12B8 && c <= 0x12BE) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x12C0) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x12C2 && c <= 0x12C5) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x12C8 && c <= 0x12CE) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x12D0 && c <= 0x12D6) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x12D8 && c <= 0x12EE) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x12F0 && c <= 0x130E) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x1310) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1312 && c <= 0x1315) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1318 && c <= 0x131E) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1320 && c <= 0x1346) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1348 && c <= 0x135A) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1361 && c <= 0x1368) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1369 && c <= 0x1371) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1372 && c <= 0x137C) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x13A0 && c <= 0x13F4) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1401 && c <= 0x166C) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x166D && c <= 0x166E) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x166F && c <= 0x1676) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x1680) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1681 && c <= 0x169A) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x169B) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x169C) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x16A0 && c <= 0x16EA) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x16EB && c <= 0x16ED) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x16EE && c <= 0x16F0) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1700 && c <= 0x170C) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x170E && c <= 0x1711) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1712 && c <= 0x1713) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1720 && c <= 0x1731) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1732 && c <= 0x1733) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1735 && c <= 0x1736) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1740 && c <= 0x1751) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1752 && c <= 0x1753) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1760 && c <= 0x176C) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x176E && c <= 0x1770) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1772 && c <= 0x1773) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1780 && c <= 0x17B3) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x17B4 && c <= 0x17B5) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x17B6) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x17B7 && c <= 0x17BD) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x17BE && c <= 0x17C5) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x17C6) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x17C7 && c <= 0x17C8) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x17C9 && c <= 0x17D1) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x17D3) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x17D4 && c <= 0x17D6) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x17D7) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x17D8 && c <= 0x17DA) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x17DB) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x17DC) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x17E0 && c <= 0x17E9) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x17F0 && c <= 0x17F9) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1800 && c <= 0x1805) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x1806) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1807 && c <= 0x180A) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x180B && c <= 0x180D) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x180E) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1810 && c <= 0x1819) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1820 && c <= 0x1842) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x1843) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1844 && c <= 0x1877) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1880 && c <= 0x18A8) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1900 && c <= 0x191C) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1920 && c <= 0x1922) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1923 && c <= 0x1926) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1927 && c <= 0x1928) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1929 && c <= 0x192B) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1930 && c <= 0x1931) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x1932) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1933 && c <= 0x1938) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x1940) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1944 && c <= 0x1945) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1946 && c <= 0x194F) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1950 && c <= 0x196D) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1970 && c <= 0x1974) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x19E0 && c <= 0x19FF) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1D00 && c <= 0x1D2B) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1D2C && c <= 0x1D61) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1D62 && c <= 0x1D6B) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1E00 && c <= 0x1E9B) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1EA0 && c <= 0x1EF9) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1F00 && c <= 0x1F15) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1F18 && c <= 0x1F1D) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1F20 && c <= 0x1F45) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1F48 && c <= 0x1F4D) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1F50 && c <= 0x1F57) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x1F59) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x1F5B) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x1F5D) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1F5F && c <= 0x1F7D) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1F80 && c <= 0x1FB4) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1FB6 && c <= 0x1FBC) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x1FBD) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x1FBE) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1FBF && c <= 0x1FC1) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1FC2 && c <= 0x1FC4) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1FC6 && c <= 0x1FCC) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1FCD && c <= 0x1FCF) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1FD0 && c <= 0x1FD3) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1FD6 && c <= 0x1FDB) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1FDD && c <= 0x1FDF) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1FE0 && c <= 0x1FEC) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1FED && c <= 0x1FEF) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1FF2 && c <= 0x1FF4) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1FF6 && c <= 0x1FFC) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1FFD && c <= 0x1FFE) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x2000 && c <= 0x200A) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x200B && c <= 0x200F) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x2010 && c <= 0x2015) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x2016 && c <= 0x2017) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x2018) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x2019) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x201A) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x201B && c <= 0x201C) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x201D) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x201E) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x201F) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x2020 && c <= 0x2027) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x2028) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x2029) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x202A && c <= 0x202E) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x202F) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x2030 && c <= 0x2038) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x2039) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x203A) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x203B && c <= 0x203E) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x203F && c <= 0x2040) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x2041 && c <= 0x2043) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x2044) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x2045) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x2046) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x2047 && c <= 0x2051) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x2052) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x2053) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x2054) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x2057) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x205F) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x2060 && c <= 0x2063) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x206A && c <= 0x206F) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x2070) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x2071) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x2074 && c <= 0x2079) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x207A && c <= 0x207C) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x207D) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x207E) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x207F) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x2080 && c <= 0x2089) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x208A && c <= 0x208C) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x208D) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x208E) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x20A0 && c <= 0x20B1) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x20DD && c <= 0x20E0) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x20E2 && c <= 0x20E4) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x2100 && c <= 0x2101) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x2102) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x2103 && c <= 0x2106) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x2107) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x2108 && c <= 0x2109) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x210A && c <= 0x2113) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x2114) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x2115) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x2116 && c <= 0x2118) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x2119 && c <= 0x211D) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x211E && c <= 0x2123) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x2124) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x2125) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x2126) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x2127) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x2128) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x2129) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x212A && c <= 0x212D) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x212E) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x212F && c <= 0x2131) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x2132) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x2133 && c <= 0x2134) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x2135 && c <= 0x2138) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x2139) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x213A && c <= 0x213B) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x213D && c <= 0x213F) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x2140 && c <= 0x2144) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x2145 && c <= 0x2149) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x214A) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x214B) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x2153 && c <= 0x215F) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x2160 && c <= 0x2183) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x2190 && c <= 0x2194) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x2195 && c <= 0x2199) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x219A && c <= 0x219B) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x219C && c <= 0x219F) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x21A0) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x21A1 && c <= 0x21A2) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x21A3) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x21A4 && c <= 0x21A5) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x21A6) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x21A7 && c <= 0x21AD) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x21AE) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x21AF && c <= 0x21CD) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x21CE && c <= 0x21CF) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x21D0 && c <= 0x21D1) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x21D2) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x21D3) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x21D4) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x21D5 && c <= 0x21F3) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x21F4 && c <= 0x22FF) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x2300 && c <= 0x2307) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x2308 && c <= 0x230B) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x230C && c <= 0x231F) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x2320 && c <= 0x2321) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x2322 && c <= 0x2328) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x2329) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x232A) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x232B && c <= 0x237B) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x237C) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x237D && c <= 0x239A) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x239B && c <= 0x23B3) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x23B4) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x23B5) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x23B6) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x23B7 && c <= 0x23D0) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x2400 && c <= 0x2426) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x2440 && c <= 0x244A) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x2460 && c <= 0x249B) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x249C && c <= 0x24E9) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x24EA && c <= 0x24FF) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x2500 && c <= 0x25B6) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x25B7) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x25B8 && c <= 0x25C0) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x25C1) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x25C2 && c <= 0x25F7) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x25F8 && c <= 0x25FF) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x2600 && c <= 0x2617) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x2619 && c <= 0x266E) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x266F) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x2670 && c <= 0x267D) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x2680 && c <= 0x2691) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x26A0 && c <= 0x26A1) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x2701 && c <= 0x2704) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x2706 && c <= 0x2709) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x270C && c <= 0x2727) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x2729 && c <= 0x274B) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x274D) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x274F && c <= 0x2752) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x2756) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x2758 && c <= 0x275E) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x2761 && c <= 0x2767) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x2768) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x2769) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x276A) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x276B) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x276C) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x276D) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x276E) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x276F) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x2770) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x2771) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x2772) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x2773) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x2774) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x2775) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x2776 && c <= 0x2793) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x2794) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x2798 && c <= 0x27AF) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x27B1 && c <= 0x27BE) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x27D0 && c <= 0x27E5) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x27E6) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x27E7) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x27E8) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x27E9) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x27EA) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x27EB) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x27F0 && c <= 0x27FF) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x2800 && c <= 0x28FF) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x2900 && c <= 0x2982) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x2983) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x2984) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x2985) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x2986) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x2987) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x2988) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x2989) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x298A) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x298B) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x298C) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x298D) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x298E) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x298F) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x2990) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x2991) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x2992) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x2993) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x2994) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x2995) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x2996) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x2997) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x2998) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x2999 && c <= 0x29D7) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x29D8) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x29D9) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x29DA) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x29DB) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x29DC && c <= 0x29FB) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x29FC) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x29FD) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x29FE && c <= 0x2AFF) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x2B00 && c <= 0x2B0D) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x2E80 && c <= 0x2E99) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x2E9B && c <= 0x2EF3) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x2F00 && c <= 0x2FD5) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x2FF0 && c <= 0x2FFB) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x3000) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x3001 && c <= 0x3003) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x3004) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x3005) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x3006) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x3007) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x3008) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x3009) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x300A) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x300B) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x300C) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x300D) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x300E) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x300F) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x3010) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x3011) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x3012 && c <= 0x3013) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x3014) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x3015) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x3016) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x3017) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x3018) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x3019) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x301A) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x301B) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x301C) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x301D) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x301E && c <= 0x301F) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x3020) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x3021 && c <= 0x3029) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x3030) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x3031 && c <= 0x3035) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x3036 && c <= 0x3037) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x3038 && c <= 0x303A) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x303B) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x303C) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x303D) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x303E && c <= 0x303F) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x3041 && c <= 0x3096) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x309B && c <= 0x309C) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x309D && c <= 0x309E) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x309F) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x30A0) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x30A1 && c <= 0x30FA) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x30FB) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x30FC && c <= 0x30FE) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x30FF) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x3105 && c <= 0x312C) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x3131 && c <= 0x318E) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x3190 && c <= 0x3191) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x3192 && c <= 0x3195) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x3196 && c <= 0x319F) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x31A0 && c <= 0x31B7) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x31F0 && c <= 0x31FF) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x3200 && c <= 0x321E) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x3220 && c <= 0x3229) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x322A && c <= 0x3243) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x3250) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x3251 && c <= 0x325F) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x3260 && c <= 0x327D) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0x327F) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x3280 && c <= 0x3289) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x328A && c <= 0x32B0) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x32B1 && c <= 0x32BF) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x32C0 && c <= 0x32FE) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x3300 && c <= 0x33FF) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x3400 && c <= 0x4DB5) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x4DC0 && c <= 0x4DFF) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x4E00 && c <= 0x9FA5) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0xA000 && c <= 0xA48C) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0xA490 && c <= 0xA4C6) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0xAC00 && c <= 0xD7A3) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0xE000 && c <= 0xF8FF) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0xF900 && c <= 0xFA2D) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0xFA30 && c <= 0xFA6A) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0xFB00 && c <= 0xFB06) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0xFB13 && c <= 0xFB17) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0xFB1D) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0xFB1F && c <= 0xFB28) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0xFB29) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0xFB2A && c <= 0xFB36) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0xFB38 && c <= 0xFB3C) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0xFB3E) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0xFB40 && c <= 0xFB41) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0xFB43 && c <= 0xFB44) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0xFB46 && c <= 0xFBB1) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0xFBD3 && c <= 0xFD3D) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0xFD3E) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0xFD3F) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0xFD50 && c <= 0xFD8F) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0xFD92 && c <= 0xFDC7) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0xFDF0 && c <= 0xFDFB) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0xFDFC) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0xFDFD) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0xFE00 && c <= 0xFE0F) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0xFE30) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0xFE31 && c <= 0xFE32) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0xFE33 && c <= 0xFE34) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0xFE35) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0xFE36) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0xFE37) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0xFE38) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0xFE39) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0xFE3A) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0xFE3B) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0xFE3C) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0xFE3D) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0xFE3E) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0xFE3F) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0xFE40) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0xFE41) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0xFE42) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0xFE43) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0xFE44) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0xFE45 && c <= 0xFE46) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0xFE47) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0xFE48) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0xFE49 && c <= 0xFE4C) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0xFE4D && c <= 0xFE4F) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0xFE50 && c <= 0xFE52) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0xFE54 && c <= 0xFE57) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0xFE58) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0xFE59) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0xFE5A) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0xFE5B) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0xFE5C) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0xFE5D) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0xFE5E) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0xFE5F && c <= 0xFE61) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0xFE62) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0xFE63) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0xFE64 && c <= 0xFE66) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0xFE68) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0xFE69) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0xFE6A && c <= 0xFE6B) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0xFE70 && c <= 0xFE74) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0xFE76 && c <= 0xFEFC) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0xFEFF) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0xFF01 && c <= 0xFF03) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0xFF04) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0xFF05 && c <= 0xFF07) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0xFF08) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0xFF09) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0xFF0A) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0xFF0B) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0xFF0C) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0xFF0D) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0xFF0E && c <= 0xFF0F) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0xFF10 && c <= 0xFF19) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0xFF1A && c <= 0xFF1B) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0xFF1C && c <= 0xFF1E) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0xFF1F && c <= 0xFF20) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0xFF21 && c <= 0xFF3A) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0xFF3B) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0xFF3C) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0xFF3D) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0xFF3E) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0xFF3F) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0xFF40) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0xFF41 && c <= 0xFF5A) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0xFF5B) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0xFF5C) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0xFF5D) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0xFF5E) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0xFF5F) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0xFF60) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0xFF61) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0xFF62) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0xFF63) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0xFF64) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0xFF65) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0xFF66 && c <= 0xFF6F) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0xFF70) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0xFF71 && c <= 0xFF9D) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0xFF9E && c <= 0xFF9F) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0xFFA0 && c <= 0xFFBE) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0xFFC2 && c <= 0xFFC7) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0xFFCA && c <= 0xFFCF) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0xFFD2 && c <= 0xFFD7) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0xFFDA && c <= 0xFFDC) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0xFFE0 && c <= 0xFFE1) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0xFFE2) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0xFFE3) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0xFFE4) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0xFFE5 && c <= 0xFFE6) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c == 0xFFE8) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0xFFE9 && c <= 0xFFEC) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0xFFED && c <= 0xFFEE) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0xFFF9 && c <= 0xFFFB) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0xFFFC && c <= 0xFFFD) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x10000 && c <= 0x1000B) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1000D && c <= 0x10026) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x10028 && c <= 0x1003A) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1003C && c <= 0x1003D) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1003F && c <= 0x1004D) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x10050 && c <= 0x1005D) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x10080 && c <= 0x100FA) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x10100 && c <= 0x10101) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x10107 && c <= 0x10133) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x10137 && c <= 0x1013F) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x10300 && c <= 0x1031E) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x10320 && c <= 0x10323) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x10330 && c <= 0x10349) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x10380 && c <= 0x1039D) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x10400 && c <= 0x1044F) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x10450 && c <= 0x1049D) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x104A0 && c <= 0x104A9) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x10800 && c <= 0x10805) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1080A && c <= 0x10835) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x10837 && c <= 0x10838) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1D000 && c <= 0x1D0F5) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1D100 && c <= 0x1D126) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1D12A && c <= 0x1D164) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1D16A && c <= 0x1D16C) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1D173 && c <= 0x1D17A) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1D183 && c <= 0x1D184) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1D18C && c <= 0x1D1A9) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1D1AE && c <= 0x1D1DD) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1D300 && c <= 0x1D356) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1D400 && c <= 0x1D454) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1D456 && c <= 0x1D49C) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1D49E && c <= 0x1D49F) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1D4A5 && c <= 0x1D4A6) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1D4A9 && c <= 0x1D4AC) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1D4AE && c <= 0x1D4B9) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1D4BD && c <= 0x1D4C3) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1D4C5 && c <= 0x1D505) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1D507 && c <= 0x1D50A) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1D50D && c <= 0x1D514) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1D516 && c <= 0x1D51C) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1D51E && c <= 0x1D539) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1D53B && c <= 0x1D53E) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1D540 && c <= 0x1D544) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1D54A && c <= 0x1D550) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1D552 && c <= 0x1D6A3) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1D6A8 && c <= 0x1D6C0) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1D6C2 && c <= 0x1D6DA) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1D6DC && c <= 0x1D6FA) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1D6FC && c <= 0x1D714) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1D716 && c <= 0x1D734) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1D736 && c <= 0x1D74E) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1D750 && c <= 0x1D76E) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1D770 && c <= 0x1D788) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1D78A && c <= 0x1D7A8) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1D7AA && c <= 0x1D7C2) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1D7C4 && c <= 0x1D7C9) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x1D7CE && c <= 0x1D7FF) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x20000 && c <= 0x2A6D6) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x2F800 && c <= 0x2FA1D) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0xE0020 && c <= 0xE007F) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0xE0100 && c <= 0xE01EF) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0xF0000 && c <= 0xFFFFD) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x100000 && c <= 0x10FFFD) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (c >= 0x0334 && c <= 0x0338) return CANONICAL_COMBINING_CLASS_OVERLAY;
        if (c >= 0x20D2 && c <= 0x20D3) return CANONICAL_COMBINING_CLASS_OVERLAY;
        if (c >= 0x20D8 && c <= 0x20DA) return CANONICAL_COMBINING_CLASS_OVERLAY;
        if (c >= 0x20E5 && c <= 0x20E6) return CANONICAL_COMBINING_CLASS_OVERLAY;
        if (c == 0x20EA) return CANONICAL_COMBINING_CLASS_OVERLAY;
        if (c >= 0x1D167 && c <= 0x1D169) return CANONICAL_COMBINING_CLASS_OVERLAY;
        if (c == 0x093C) return CANONICAL_COMBINING_CLASS_NUKTA;
        if (c == 0x09BC) return CANONICAL_COMBINING_CLASS_NUKTA;
        if (c == 0x0A3C) return CANONICAL_COMBINING_CLASS_NUKTA;
        if (c == 0x0ABC) return CANONICAL_COMBINING_CLASS_NUKTA;
        if (c == 0x0B3C) return CANONICAL_COMBINING_CLASS_NUKTA;
        if (c == 0x0CBC) return CANONICAL_COMBINING_CLASS_NUKTA;
        if (c == 0x1037) return CANONICAL_COMBINING_CLASS_NUKTA;
        if (c >= 0x3099 && c <= 0x309A) return CANONICAL_COMBINING_CLASS_KANA_VOICING;
        if (c == 0x094D) return CANONICAL_COMBINING_CLASS_VIRAMA;
        if (c == 0x09CD) return CANONICAL_COMBINING_CLASS_VIRAMA;
        if (c == 0x0A4D) return CANONICAL_COMBINING_CLASS_VIRAMA;
        if (c == 0x0ACD) return CANONICAL_COMBINING_CLASS_VIRAMA;
        if (c == 0x0B4D) return CANONICAL_COMBINING_CLASS_VIRAMA;
        if (c == 0x0BCD) return CANONICAL_COMBINING_CLASS_VIRAMA;
        if (c == 0x0C4D) return CANONICAL_COMBINING_CLASS_VIRAMA;
        if (c == 0x0CCD) return CANONICAL_COMBINING_CLASS_VIRAMA;
        if (c == 0x0D4D) return CANONICAL_COMBINING_CLASS_VIRAMA;
        if (c == 0x0DCA) return CANONICAL_COMBINING_CLASS_VIRAMA;
        if (c == 0x0E3A) return CANONICAL_COMBINING_CLASS_VIRAMA;
        if (c == 0x0F84) return CANONICAL_COMBINING_CLASS_VIRAMA;
        if (c == 0x1039) return CANONICAL_COMBINING_CLASS_VIRAMA;
        if (c == 0x1714) return CANONICAL_COMBINING_CLASS_VIRAMA;
        if (c == 0x1734) return CANONICAL_COMBINING_CLASS_VIRAMA;
        if (c == 0x17D2) return CANONICAL_COMBINING_CLASS_VIRAMA;
        if (c == 0x05B0) return CANONICAL_COMBINING_CLASS_10;
        if (c == 0x05B1) return CANONICAL_COMBINING_CLASS_11;
        if (c == 0x05B2) return CANONICAL_COMBINING_CLASS_12;
        if (c == 0x05B3) return CANONICAL_COMBINING_CLASS_13;
        if (c == 0x05B4) return CANONICAL_COMBINING_CLASS_14;
        if (c == 0x05B5) return CANONICAL_COMBINING_CLASS_15;
        if (c == 0x05B6) return CANONICAL_COMBINING_CLASS_16;
        if (c == 0x05B7) return CANONICAL_COMBINING_CLASS_17;
        if (c == 0x05B8) return CANONICAL_COMBINING_CLASS_18;
        if (c == 0x05B9) return CANONICAL_COMBINING_CLASS_19;
        if (c == 0x05BB) return CANONICAL_COMBINING_CLASS_20;
        if (c == 0x05BC) return CANONICAL_COMBINING_CLASS_21;
        if (c == 0x05BD) return CANONICAL_COMBINING_CLASS_22;
        if (c == 0x05BF) return CANONICAL_COMBINING_CLASS_23;
        if (c == 0x05C1) return CANONICAL_COMBINING_CLASS_24;
        if (c == 0x05C2) return CANONICAL_COMBINING_CLASS_25;
        if (c == 0xFB1E) return CANONICAL_COMBINING_CLASS_26;
        if (c == 0x064B) return CANONICAL_COMBINING_CLASS_27;
        if (c == 0x064C) return CANONICAL_COMBINING_CLASS_28;
        if (c == 0x064D) return CANONICAL_COMBINING_CLASS_29;
        if (c == 0x064E) return CANONICAL_COMBINING_CLASS_30;
        if (c == 0x064F) return CANONICAL_COMBINING_CLASS_31;
        if (c == 0x0650) return CANONICAL_COMBINING_CLASS_32;
        if (c == 0x0651) return CANONICAL_COMBINING_CLASS_33;
        if (c == 0x0652) return CANONICAL_COMBINING_CLASS_34;
        if (c == 0x0670) return CANONICAL_COMBINING_CLASS_35;
        if (c == 0x0711) return CANONICAL_COMBINING_CLASS_36;
        if (c == 0x0C55) return CANONICAL_COMBINING_CLASS_84;
        if (c == 0x0C56) return CANONICAL_COMBINING_CLASS_91;
        if (c >= 0x0E38 && c <= 0x0E39) return CANONICAL_COMBINING_CLASS_103;
        if (c >= 0x0E48 && c <= 0x0E4B) return CANONICAL_COMBINING_CLASS_107;
        if (c >= 0x0EB8 && c <= 0x0EB9) return CANONICAL_COMBINING_CLASS_118;
        if (c >= 0x0EC8 && c <= 0x0ECB) return CANONICAL_COMBINING_CLASS_122;
        if (c == 0x0F71) return CANONICAL_COMBINING_CLASS_129;
        if (c == 0x0F72) return CANONICAL_COMBINING_CLASS_130;
        if (c >= 0x0F7A && c <= 0x0F7D) return CANONICAL_COMBINING_CLASS_130;
        if (c == 0x0F80) return CANONICAL_COMBINING_CLASS_130;
        if (c == 0x0F74) return CANONICAL_COMBINING_CLASS_132;
        if (c >= 0x0321 && c <= 0x0322) return CANONICAL_COMBINING_CLASS_ATTACHED_BELOW;
        if (c >= 0x0327 && c <= 0x0328) return CANONICAL_COMBINING_CLASS_ATTACHED_BELOW;
        if (c == 0x031B) return CANONICAL_COMBINING_CLASS_ATTACHED_ABOVE_RIGHT;
        if (c == 0x0F39) return CANONICAL_COMBINING_CLASS_ATTACHED_ABOVE_RIGHT;
        if (c >= 0x1D165 && c <= 0x1D166) return CANONICAL_COMBINING_CLASS_ATTACHED_ABOVE_RIGHT;
        if (c >= 0x1D16E && c <= 0x1D172) return CANONICAL_COMBINING_CLASS_ATTACHED_ABOVE_RIGHT;
        if (c == 0x302A) return CANONICAL_COMBINING_CLASS_BELOW_LEFT;
        if (c >= 0x0316 && c <= 0x0319) return CANONICAL_COMBINING_CLASS_BELOW;
        if (c >= 0x031C && c <= 0x0320) return CANONICAL_COMBINING_CLASS_BELOW;
        if (c >= 0x0323 && c <= 0x0326) return CANONICAL_COMBINING_CLASS_BELOW;
        if (c >= 0x0329 && c <= 0x0333) return CANONICAL_COMBINING_CLASS_BELOW;
        if (c >= 0x0339 && c <= 0x033C) return CANONICAL_COMBINING_CLASS_BELOW;
        if (c >= 0x0347 && c <= 0x0349) return CANONICAL_COMBINING_CLASS_BELOW;
        if (c >= 0x034D && c <= 0x034E) return CANONICAL_COMBINING_CLASS_BELOW;
        if (c >= 0x0353 && c <= 0x0356) return CANONICAL_COMBINING_CLASS_BELOW;
        if (c == 0x0591) return CANONICAL_COMBINING_CLASS_BELOW;
        if (c == 0x0596) return CANONICAL_COMBINING_CLASS_BELOW;
        if (c == 0x059B) return CANONICAL_COMBINING_CLASS_BELOW;
        if (c >= 0x05A3 && c <= 0x05A7) return CANONICAL_COMBINING_CLASS_BELOW;
        if (c == 0x05AA) return CANONICAL_COMBINING_CLASS_BELOW;
        if (c >= 0x0655 && c <= 0x0656) return CANONICAL_COMBINING_CLASS_BELOW;
        if (c == 0x06E3) return CANONICAL_COMBINING_CLASS_BELOW;
        if (c == 0x06EA) return CANONICAL_COMBINING_CLASS_BELOW;
        if (c == 0x06ED) return CANONICAL_COMBINING_CLASS_BELOW;
        if (c == 0x0731) return CANONICAL_COMBINING_CLASS_BELOW;
        if (c == 0x0734) return CANONICAL_COMBINING_CLASS_BELOW;
        if (c >= 0x0737 && c <= 0x0739) return CANONICAL_COMBINING_CLASS_BELOW;
        if (c >= 0x073B && c <= 0x073C) return CANONICAL_COMBINING_CLASS_BELOW;
        if (c == 0x073E) return CANONICAL_COMBINING_CLASS_BELOW;
        if (c == 0x0742) return CANONICAL_COMBINING_CLASS_BELOW;
        if (c == 0x0744) return CANONICAL_COMBINING_CLASS_BELOW;
        if (c == 0x0746) return CANONICAL_COMBINING_CLASS_BELOW;
        if (c == 0x0748) return CANONICAL_COMBINING_CLASS_BELOW;
        if (c == 0x0952) return CANONICAL_COMBINING_CLASS_BELOW;
        if (c >= 0x0F18 && c <= 0x0F19) return CANONICAL_COMBINING_CLASS_BELOW;
        if (c == 0x0F35) return CANONICAL_COMBINING_CLASS_BELOW;
        if (c == 0x0F37) return CANONICAL_COMBINING_CLASS_BELOW;
        if (c == 0x0FC6) return CANONICAL_COMBINING_CLASS_BELOW;
        if (c == 0x193B) return CANONICAL_COMBINING_CLASS_BELOW;
        if (c == 0x20E8) return CANONICAL_COMBINING_CLASS_BELOW;
        if (c >= 0x1D17B && c <= 0x1D182) return CANONICAL_COMBINING_CLASS_BELOW;
        if (c >= 0x1D18A && c <= 0x1D18B) return CANONICAL_COMBINING_CLASS_BELOW;
        if (c == 0x059A) return CANONICAL_COMBINING_CLASS_BELOW_RIGHT;
        if (c == 0x05AD) return CANONICAL_COMBINING_CLASS_BELOW_RIGHT;
        if (c == 0x1939) return CANONICAL_COMBINING_CLASS_BELOW_RIGHT;
        if (c == 0x302D) return CANONICAL_COMBINING_CLASS_BELOW_RIGHT;
        if (c >= 0x302E && c <= 0x302F) return CANONICAL_COMBINING_CLASS_LEFT;
        if (c == 0x05AE) return CANONICAL_COMBINING_CLASS_ABOVE_LEFT;
        if (c == 0x18A9) return CANONICAL_COMBINING_CLASS_ABOVE_LEFT;
        if (c == 0x302B) return CANONICAL_COMBINING_CLASS_ABOVE_LEFT;
        if (c >= 0x0300 && c <= 0x0314) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (c >= 0x033D && c <= 0x0344) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (c == 0x0346) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (c >= 0x034A && c <= 0x034C) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (c >= 0x0350 && c <= 0x0352) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (c == 0x0357) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (c >= 0x0363 && c <= 0x036F) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (c >= 0x0483 && c <= 0x0486) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (c >= 0x0592 && c <= 0x0595) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (c >= 0x0597 && c <= 0x0599) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (c >= 0x059C && c <= 0x05A1) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (c >= 0x05A8 && c <= 0x05A9) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (c >= 0x05AB && c <= 0x05AC) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (c == 0x05AF) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (c == 0x05C4) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (c >= 0x0610 && c <= 0x0615) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (c >= 0x0653 && c <= 0x0654) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (c >= 0x0657 && c <= 0x0658) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (c >= 0x06D6 && c <= 0x06DC) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (c >= 0x06DF && c <= 0x06E2) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (c == 0x06E4) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (c >= 0x06E7 && c <= 0x06E8) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (c >= 0x06EB && c <= 0x06EC) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (c == 0x0730) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (c >= 0x0732 && c <= 0x0733) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (c >= 0x0735 && c <= 0x0736) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (c == 0x073A) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (c == 0x073D) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (c >= 0x073F && c <= 0x0741) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (c == 0x0743) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (c == 0x0745) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (c == 0x0747) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (c >= 0x0749 && c <= 0x074A) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (c == 0x0951) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (c >= 0x0953 && c <= 0x0954) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (c >= 0x0F82 && c <= 0x0F83) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (c >= 0x0F86 && c <= 0x0F87) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (c == 0x17DD) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (c == 0x193A) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (c >= 0x20D0 && c <= 0x20D1) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (c >= 0x20D4 && c <= 0x20D7) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (c >= 0x20DB && c <= 0x20DC) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (c == 0x20E1) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (c == 0x20E7) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (c == 0x20E9) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (c >= 0xFE20 && c <= 0xFE23) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (c >= 0x1D185 && c <= 0x1D189) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (c >= 0x1D1AA && c <= 0x1D1AD) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (c == 0x0315) return CANONICAL_COMBINING_CLASS_ABOVE_RIGHT;
        if (c == 0x031A) return CANONICAL_COMBINING_CLASS_ABOVE_RIGHT;
        if (c == 0x302C) return CANONICAL_COMBINING_CLASS_ABOVE_RIGHT;
        if (c == 0x035F) return CANONICAL_COMBINING_CLASS_DOUBLE_BELOW;
        if (c == 0x0362) return CANONICAL_COMBINING_CLASS_DOUBLE_BELOW;
        if (c >= 0x035D && c <= 0x035E) return CANONICAL_COMBINING_CLASS_DOUBLE_ABOVE;
        if (c >= 0x0360 && c <= 0x0361) return CANONICAL_COMBINING_CLASS_DOUBLE_ABOVE;
        if (c == 0x0345) return CANONICAL_COMBINING_CLASS_IOTA_SUBSCRIPT;    
        
        return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
    }
    
    
    
    private static boolean isExcluded(int c) {
        
        // ???? optimize
        if (c == 0x0958) return true; // DEVANAGARI LETTER QA
        if (c == 0x0959) return true; // DEVANAGARI LETTER KHHA
        if (c == 0x095A) return true; // DEVANAGARI LETTER GHHA
        if (c == 0x095B) return true; // DEVANAGARI LETTER ZA
        if (c == 0x095C) return true; // DEVANAGARI LETTER DDDHA
        if (c == 0x095D) return true; // DEVANAGARI LETTER RHA
        if (c == 0x095E) return true; // DEVANAGARI LETTER FA
        if (c == 0x095F) return true; // DEVANAGARI LETTER YYA
        if (c == 0x09DC) return true; // BENGALI LETTER RRA
        if (c == 0x09DD) return true; // BENGALI LETTER RHA
        if (c == 0x09DF) return true; // BENGALI LETTER YYA
        if (c == 0x0A33) return true; // GURMUKHI LETTER LLA
        if (c == 0x0A36) return true; // GURMUKHI LETTER SHA
        if (c == 0x0A59) return true; // GURMUKHI LETTER KHHA
        if (c == 0x0A5A) return true; // GURMUKHI LETTER GHHA
        if (c == 0x0A5B) return true; // GURMUKHI LETTER ZA
        if (c == 0x0A5E) return true; // GURMUKHI LETTER FA
        if (c == 0x0B5C) return true; // ORIYA LETTER RRA
        if (c == 0x0B5D) return true; // ORIYA LETTER RHA
        if (c == 0x0F43) return true; // TIBETAN LETTER GHA
        if (c == 0x0F4D) return true; // TIBETAN LETTER DDHA
        if (c == 0x0F52) return true; // TIBETAN LETTER DHA
        if (c == 0x0F57) return true; // TIBETAN LETTER BHA
        if (c == 0x0F5C) return true; // TIBETAN LETTER DZHA
        if (c == 0x0F69) return true; // TIBETAN LETTER KSSA
        if (c == 0x0F76) return true; // TIBETAN VOWEL SIGN VOCALIC R
        if (c == 0x0F78) return true; // TIBETAN VOWEL SIGN VOCALIC L
        if (c == 0x0F93) return true; // TIBETAN SUBJOINED LETTER GHA
        if (c == 0x0F9D) return true; // TIBETAN SUBJOINED LETTER DDHA
        if (c == 0x0FA2) return true; // TIBETAN SUBJOINED LETTER DHA
        if (c == 0x0FA7) return true; // TIBETAN SUBJOINED LETTER BHA
        if (c == 0x0FAC) return true; // TIBETAN SUBJOINED LETTER DZHA
        if (c == 0x0FB9) return true; // TIBETAN SUBJOINED LETTER KSSA
        if (c == 0xFB1D) return true; // HEBREW LETTER YOD WITH HIRIQ
        if (c == 0xFB1F) return true; // HEBREW LIGATURE YIDDISH YOD YOD PATAH
        if (c == 0xFB2A) return true; // HEBREW LETTER SHIN WITH SHIN DOT
        if (c == 0xFB2B) return true; // HEBREW LETTER SHIN WITH SIN DOT
        if (c == 0xFB2C) return true; // HEBREW LETTER SHIN WITH DAGESH AND SHIN DOT
        if (c == 0xFB2D) return true; // HEBREW LETTER SHIN WITH DAGESH AND SIN DOT
        if (c == 0xFB2E) return true; // HEBREW LETTER ALEF WITH PATAH
        if (c == 0xFB2F) return true; // HEBREW LETTER ALEF WITH QAMATS
        if (c == 0xFB30) return true; // HEBREW LETTER ALEF WITH MAPIQ
        if (c == 0xFB31) return true; // HEBREW LETTER BET WITH DAGESH
        if (c == 0xFB32) return true; // HEBREW LETTER GIMEL WITH DAGESH
        if (c == 0xFB33) return true; // HEBREW LETTER DALET WITH DAGESH
        if (c == 0xFB34) return true; // HEBREW LETTER HE WITH MAPIQ
        if (c == 0xFB35) return true; // HEBREW LETTER VAV WITH DAGESH
        if (c == 0xFB36) return true; // HEBREW LETTER ZAYIN WITH DAGESH
        if (c == 0xFB38) return true; // HEBREW LETTER TET WITH DAGESH
        if (c == 0xFB39) return true; // HEBREW LETTER YOD WITH DAGESH
        if (c == 0xFB3A) return true; // HEBREW LETTER FINAL KAF WITH DAGESH
        if (c == 0xFB3B) return true; // HEBREW LETTER KAF WITH DAGESH
        if (c == 0xFB3C) return true; // HEBREW LETTER LAMED WITH DAGESH
        if (c == 0xFB3E) return true; // HEBREW LETTER MEM WITH DAGESH
        if (c == 0xFB40) return true; // HEBREW LETTER NUN WITH DAGESH
        if (c == 0xFB41) return true; // HEBREW LETTER SAMEKH WITH DAGESH
        if (c == 0xFB43) return true; // HEBREW LETTER FINAL PE WITH DAGESH
        if (c == 0xFB44) return true; // HEBREW LETTER PE WITH DAGESH
        if (c == 0xFB46) return true; // HEBREW LETTER TSADI WITH DAGESH
        if (c == 0xFB47) return true; // HEBREW LETTER QOF WITH DAGESH
        if (c == 0xFB48) return true; // HEBREW LETTER RESH WITH DAGESH
        if (c == 0xFB49) return true; // HEBREW LETTER SHIN WITH DAGESH
        if (c == 0xFB4A) return true; // HEBREW LETTER TAV WITH DAGESH
        if (c == 0xFB4B) return true; // HEBREW LETTER VAV WITH HOLAM
        if (c == 0xFB4C) return true; // HEBREW LETTER BET WITH RAFE
        if (c == 0xFB4D) return true; // HEBREW LETTER KAF WITH RAFE
        if (c == 0xFB4E) return true; // HEBREW LETTER PE WITH RAFE

        // Post Composition Version precomposed characters

        if (c == 0x2ADC) return true; // FORKING
        if (c == 0x1D15E) return true; // MUSICAL SYMBOL HALF NOTE
        if (c == 0x1D15F) return true; // MUSICAL SYMBOL QUARTER NOTE
        if (c == 0x1D160) return true; // MUSICAL SYMBOL EIGHTH NOTE
        if (c == 0x1D161) return true; // MUSICAL SYMBOL SIXTEENTH NOTE
        if (c == 0x1D162) return true; // MUSICAL SYMBOL THIRTY-SECOND NOTE
        if (c == 0x1D163) return true; // MUSICAL SYMBOL SIXTY-FOURTH NOTE
        if (c == 0x1D164) return true; // MUSICAL SYMBOL ONE HUNDRED TWENTY-EIGHTH NOTE
        if (c == 0x1D1BB) return true; // MUSICAL SYMBOL MINIMA
        if (c == 0x1D1BC) return true; // MUSICAL SYMBOL MINIMA BLACK
        if (c == 0x1D1BD) return true; // MUSICAL SYMBOL SEMIMINIMA WHITE
        if (c == 0x1D1BE) return true; // MUSICAL SYMBOL SEMIMINIMA BLACK
        if (c == 0x1D1BF) return true; // MUSICAL SYMBOL FUSA WHITE
        if (c == 0x1D1C0) return true; // MUSICAL SYMBOL FUSA BLACK

        // Singleton Decompositions

        if (c == 0x0340) return true; // COMBINING GRAVE TONE MARK
        if (c == 0x0341) return true; // COMBINING ACUTE TONE MARK
        if (c == 0x0343) return true; // COMBINING GREEK KORONIS
        if (c == 0x0374) return true; // GREEK NUMERAL SIGN
        if (c == 0x037E) return true; // GREEK QUESTION MARK
        if (c == 0x0387) return true; // GREEK ANO TELEIA
        if (c == 0x1F71) return true; // GREEK SMALL LETTER ALPHA WITH OXIA
        if (c == 0x1F73) return true; // GREEK SMALL LETTER EPSILON WITH OXIA
        if (c == 0x1F75) return true; // GREEK SMALL LETTER ETA WITH OXIA
        if (c == 0x1F77) return true; // GREEK SMALL LETTER IOTA WITH OXIA
        if (c == 0x1F79) return true; // GREEK SMALL LETTER OMICRON WITH OXIA
        if (c == 0x1F7B) return true; // GREEK SMALL LETTER UPSILON WITH OXIA
        if (c == 0x1F7D) return true; // GREEK SMALL LETTER OMEGA WITH OXIA
        if (c == 0x1FBB) return true; // GREEK CAPITAL LETTER ALPHA WITH OXIA
        if (c == 0x1FBE) return true; // GREEK PROSGEGRAMMENI
        if (c == 0x1FC9) return true; // GREEK CAPITAL LETTER EPSILON WITH OXIA
        if (c == 0x1FCB) return true; // GREEK CAPITAL LETTER ETA WITH OXIA
        if (c == 0x1FD3) return true; // GREEK SMALL LETTER IOTA WITH DIALYTIKA AND OXIA
        if (c == 0x1FDB) return true; // GREEK CAPITAL LETTER IOTA WITH OXIA
        if (c == 0x1FE3) return true; // GREEK SMALL LETTER UPSILON WITH DIALYTIKA AND OXIA
        if (c == 0x1FEB) return true; // GREEK CAPITAL LETTER UPSILON WITH OXIA
        if (c == 0x1FEE) return true; // GREEK DIALYTIKA AND OXIA
        if (c == 0x1FEF) return true; // GREEK VARIA
        if (c == 0x1FF9) return true; // GREEK CAPITAL LETTER OMICRON WITH OXIA
        if (c == 0x1FFB) return true; // GREEK CAPITAL LETTER OMEGA WITH OXIA
        if (c == 0x1FFD) return true; // GREEK OXIA
        if (c == 0x2000) return true; // EN QUAD
        if (c == 0x2001) return true; // EM QUAD
        if (c == 0x2126) return true; // OHM SIGN
        if (c == 0x212A) return true; // KELVIN SIGN
        if (c == 0x212B) return true; // ANGSTROM SIGN
        if (c == 0x2329) return true; // LEFT-POINTING ANGLE BRACKET
        if (c == 0x232A) return true; // RIGHT-POINTING ANGLE BRACKET
        if (c >= 0xF900 && c <= 0xFA0D) return true; // CJK COMPATIBILITY IDEOGRAPH-F900..CJK COMPATIBILITY IDEOGRAPH-FA0D
        if (c == 0xFA10) return true; // CJK COMPATIBILITY IDEOGRAPH-FA10
        if (c == 0xFA12) return true; // CJK COMPATIBILITY IDEOGRAPH-FA12
        if (c >= 0xFA15 && c <= 0xFA1E) return true; // CJK COMPATIBILITY IDEOGRAPH-FA15..CJK COMPATIBILITY IDEOGRAPH-FA1E
        if (c == 0xFA20) return true; // CJK COMPATIBILITY IDEOGRAPH-FA20
        if (c == 0xFA22) return true; // CJK COMPATIBILITY IDEOGRAPH-FA22
        if (c == 0xFA25) return true; // CJK COMPATIBILITY IDEOGRAPH-FA25
        if (c == 0xFA26) return true; // CJK COMPATIBILITY IDEOGRAPH-FA26
        if (c >= 0xFA2A && c <= 0xFA2D) return true; // CJK COMPATIBILITY IDEOGRAPH-FA2A..CJK COMPATIBILITY IDEOGRAPH-FA2D
        if (c >= 0xFA30 && c <= 0xFA6A) return true; // CJK COMPATIBILITY IDEOGRAPH-FA30..CJK COMPATIBILITY IDEOGRAPH-FA6A
        if (c >= 0x2F800 && c <= 0x2FA1D) return true; // CJK COMPATIBILITY IDEOGRAPH-2F800..CJK COMPATIBILITY IDEOGRAPH-2FA1D

        // Non-Starter Decompositions
        if (c == 0x0344) return true; // COMBINING GREEK DIALYTIKA TONOS
        if (c == 0x0F73) return true; // TIBETAN VOWEL SIGN II
        if (c == 0x0F75) return true; // TIBETAN VOWEL SIGN UU
        if (c == 0x0F81) return true; // TIBETAN VOWEL SIGN REVERSED II

        return false;
    }
    
    
    private static boolean isComposite(int c) {

        // XXX verify
        if (c <= 0xA0) return false;
        if (c >= HANGUL_SYLLABLE_BEGIN && c <= HANGUL_SYLLABLE_END) {
            return true;
        }
        if (isExcluded(c)) return false;
        
        return true;
    }
    
    
    static int unicodeCharAt(String s, int character) {
        
        int length = s.length();
        int count = 0;
        
        for (int i = 0; i < length; i++) {
            char c = s.charAt(i);
            if (count == character) {
                if (isHighSurrogate(c)) {
                    return combineSurrogatePair(c, s.charAt(i+1));
                }
                else return c;
            }
            else {
                count++;
                if (isHighSurrogate(c)) {
                    i++;
                }
            }
        }
        
        throw new StringIndexOutOfBoundsException(); // message????
        
    }


    static boolean isHighSurrogate(char c) {
        return c >= HI_SURROGATE_START && c <= HI_SURROGATE_END;
    }

    
    static int combineSurrogatePair(char highSurrogate, char lowSurrogate) {

        int high = highSurrogate & 0x7FF;
        int low  = lowSurrogate - 0xDC00;
        int highShifted = high << 10;
        int combined = highShifted | low; 
        int codePoint = combined + 0x10000;
        return codePoint;
        
    }

    private static String makeSurrogatePair(int codePoint) {

        StringBuffer s = new StringBuffer(2);
        if (codePoint <= 0xFFFF) s.append((char) codePoint);
        else {
            char high = (char) (0xD800 - (0x10000 >> 10) + (codePoint >> 10));
            char low = (char) (0xDC00 + (codePoint & 0x3FF));
            s.append(high);
            s.append(low);
        }
        return s.toString();
        
    }

    static char getHighSurrogate(int codepoint) {

        char x = (char) codepoint;
        int u = (codepoint >> 16) & ((1<<5) - 1);
        char w = (char) (u - 1);
        return (char) (HI_SURROGATE_START | (w << 6) | x >> 10);
        
    }


    static char getLowSurrogate(int codepoint) {

        char x = (char) codepoint;
        return (char) (LOW_SURROGATE_START | x & ((1<<10) - 1));
        
    }
    

     static String normalize(String s) {

        boolean needsNormalizing = false;
            
        int length = s.length();
        for (int i = 0; i < length; i++) {
            char c = s.charAt(i);
            if (c > 255) {
                needsNormalizing = true;
                break;
            }
        } 
        
        if (needsNormalizing) {
            UnicodeString ustring = new UnicodeString(s);
            UnicodeString decomposed = ustring.decompose(); 
            UnicodeString recomposed = decomposed.compose();
            String result = recomposed.toString();
            return result;
        }
        
        return s;
        
    }

    
    // return -1 if the character cannot be combined with the starter; 
    // otherwise return the composed character
    private static int composeCharacter(int starter, int c) {
        
        StringBuffer decomposed = new StringBuffer(4);
        // FIXME encode in UTF-8
        decomposed.append((char) starter);
        decomposed.append((char) c);
        String recomposed = (String) compositions.get(decomposed.toString());
        if (recomposed == null) return -1;
        else if (recomposed.length() == 1) return recomposed.charAt(0);
        else return combineSurrogatePair(recomposed.charAt(0), recomposed.charAt(1));

    }

    
    // FIXME must recurse this
    private static String decompose(int character) {
        
        if (character < 0x00C0) {
            return String.valueOf((char) character);
        }
        else if (character >= HANGUL_SYLLABLE_BEGIN && character <= HANGUL_SYLLABLE_END) {
            return decomposeHangul((char) character);
        }
        
        StringBuffer sb = new StringBuffer(2);
        if (character == 0x00C0) {
            sb.append((char) 0x0041);
            sb.append((char) 0x0300);
        }
        else if (character == 0x00C1) {
            sb.append((char) 0x0041);
            sb.append((char) 0x0301);
        }
        else if (character == 0x00C2) {
            sb.append((char) 0x0041);
            sb.append((char) 0x0302);
        }
        else if (character == 0x00C3) {
            sb.append((char) 0x0041);
            sb.append((char) 0x0303);
        }
        else if (character == 0x00C4) {
            sb.append((char) 0x0041);
            sb.append((char) 0x0308);
        }
        else if (character == 0x00C5) {
            sb.append((char) 0x0041);
            sb.append((char) 0x030A);
        }
        else if (character == 0x00C7) {
            sb.append((char) 0x0043);
            sb.append((char) 0x0327);
        }
        else if (character == 0x00C8) {
            sb.append((char) 0x0045);
            sb.append((char) 0x0300);
        }
        else if (character == 0x00C9) {
            sb.append((char) 0x0045);
            sb.append((char) 0x0301);
        }
        else if (character == 0x00CA) {
            sb.append((char) 0x0045);
            sb.append((char) 0x0302);
        }
        else if (character == 0x00CB) {
            sb.append((char) 0x0045);
            sb.append((char) 0x0308);
        }
        else if (character == 0x00CC) {
            sb.append((char) 0x0049);
            sb.append((char) 0x0300);
        }
        else if (character == 0x00CD) {
            sb.append((char) 0x0049);
            sb.append((char) 0x0301);
        }
        else if (character == 0x00CE) {
            sb.append((char) 0x0049);
            sb.append((char) 0x0302);
        }
        else if (character == 0x00CF) {
            sb.append((char) 0x0049);
            sb.append((char) 0x0308);
        }
        else if (character == 0x00D1) {
            sb.append((char) 0x004E);
            sb.append((char) 0x0303);
        }
        else if (character == 0x00D2) {
            sb.append((char) 0x004F);
            sb.append((char) 0x0300);
        }
        else if (character == 0x00D3) {
            sb.append((char) 0x004F);
            sb.append((char) 0x0301);
        }
        else if (character == 0x00D4) {
            sb.append((char) 0x004F);
            sb.append((char) 0x0302);
        }
        else if (character == 0x00D5) {
            sb.append((char) 0x004F);
            sb.append((char) 0x0303);
        }
        else if (character == 0x00D6) {
            sb.append((char) 0x004F);
            sb.append((char) 0x0308);
        }
        else if (character == 0x00D9) {
            sb.append((char) 0x0055);
            sb.append((char) 0x0300);
        }
        else if (character == 0x00DA) {
            sb.append((char) 0x0055);
            sb.append((char) 0x0301);
        }
        else if (character == 0x00DB) {
            sb.append((char) 0x0055);
            sb.append((char) 0x0302);
        }
        else if (character == 0x00DC) {
            sb.append((char) 0x0055);
            sb.append((char) 0x0308);
        }
        else if (character == 0x00DD) {
            sb.append((char) 0x0059);
            sb.append((char) 0x0301);
        }
        else if (character == 0x00E0) {
            sb.append((char) 0x0061);
            sb.append((char) 0x0300);
        }
        else if (character == 0x00E1) {
            sb.append((char) 0x0061);
            sb.append((char) 0x0301);
        }
        else if (character == 0x00E2) {
            sb.append((char) 0x0061);
            sb.append((char) 0x0302);
        }
        else if (character == 0x00E3) {
            sb.append((char) 0x0061);
            sb.append((char) 0x0303);
        }
        else if (character == 0x00E4) {
            sb.append((char) 0x0061);
            sb.append((char) 0x0308);
        }
        else if (character == 0x00E5) {
            sb.append((char) 0x0061);
            sb.append((char) 0x030A);
        }
        else if (character == 0x00E7) {
            sb.append((char) 0x0063);
            sb.append((char) 0x0327);
        }
        else if (character == 0x00E8) {
            sb.append((char) 0x0065);
            sb.append((char) 0x0300);
        }
        else if (character == 0x00E9) {
            sb.append((char) 0x0065);
            sb.append((char) 0x0301);
        }
        else if (character == 0x00EA) {
            sb.append((char) 0x0065);
            sb.append((char) 0x0302);
        }
        else if (character == 0x00EB) {
            sb.append((char) 0x0065);
            sb.append((char) 0x0308);
        }
        else if (character == 0x00EC) {
            sb.append((char) 0x0069);
            sb.append((char) 0x0300);
        }
        else if (character == 0x00ED) {
            sb.append((char) 0x0069);
            sb.append((char) 0x0301);
        }
        else if (character == 0x00EE) {
            sb.append((char) 0x0069);
            sb.append((char) 0x0302);
        }
        else if (character == 0x00EF) {
            sb.append((char) 0x0069);
            sb.append((char) 0x0308);
        }
        else if (character == 0x00F1) {
            sb.append((char) 0x006E);
            sb.append((char) 0x0303);
        }
        else if (character == 0x00F2) {
            sb.append((char) 0x006F);
            sb.append((char) 0x0300);
        }
        else if (character == 0x00F3) {
            sb.append((char) 0x006F);
            sb.append((char) 0x0301);
        }
        else if (character == 0x00F4) {
            sb.append((char) 0x006F);
            sb.append((char) 0x0302);
        }
        else if (character == 0x00F5) {
            sb.append((char) 0x006F);
            sb.append((char) 0x0303);
        }
        else if (character == 0x00F6) {
            sb.append((char) 0x006F);
            sb.append((char) 0x0308);
        }
        else if (character == 0x00F9) {
            sb.append((char) 0x0075);
            sb.append((char) 0x0300);
        }
        else if (character == 0x00FA) {
            sb.append((char) 0x0075);
            sb.append((char) 0x0301);
        }
        else if (character == 0x00FB) {
            sb.append((char) 0x0075);
            sb.append((char) 0x0302);
        }
        else if (character == 0x00FC) {
            sb.append((char) 0x0075);
            sb.append((char) 0x0308);
        }
        else if (character == 0x00FD) {
            sb.append((char) 0x0079);
            sb.append((char) 0x0301);
        }
        else if (character == 0x00FF) {
            sb.append((char) 0x0079);
            sb.append((char) 0x0308);
        }
        else if (character == 0x0100) {
            sb.append((char) 0x0041);
            sb.append((char) 0x0304);
        }
        else if (character == 0x0101) {
            sb.append((char) 0x0061);
            sb.append((char) 0x0304);
        }
        else if (character == 0x0102) {
            sb.append((char) 0x0041);
            sb.append((char) 0x0306);
        }
        else if (character == 0x0103) {
            sb.append((char) 0x0061);
            sb.append((char) 0x0306);
        }
        else if (character == 0x0104) {
            sb.append((char) 0x0041);
            sb.append((char) 0x0328);
        }
        else if (character == 0x0105) {
            sb.append((char) 0x0061);
            sb.append((char) 0x0328);
        }
        else if (character == 0x0106) {
            sb.append((char) 0x0043);
            sb.append((char) 0x0301);
        }
        else if (character == 0x0107) {
            sb.append((char) 0x0063);
            sb.append((char) 0x0301);
        }
        else if (character == 0x0108) {
            sb.append((char) 0x0043);
            sb.append((char) 0x0302);
        }
        else if (character == 0x0109) {
            sb.append((char) 0x0063);
            sb.append((char) 0x0302);
        }
        else if (character == 0x010A) {
            sb.append((char) 0x0043);
            sb.append((char) 0x0307);
        }
        else if (character == 0x010B) {
            sb.append((char) 0x0063);
            sb.append((char) 0x0307);
        }
        else if (character == 0x010C) {
            sb.append((char) 0x0043);
            sb.append((char) 0x030C);
        }
        else if (character == 0x010D) {
            sb.append((char) 0x0063);
            sb.append((char) 0x030C);
        }
        else if (character == 0x010E) {
            sb.append((char) 0x0044);
            sb.append((char) 0x030C);
        }
        else if (character == 0x010F) {
            sb.append((char) 0x0064);
            sb.append((char) 0x030C);
        }
        else if (character == 0x0112) {
            sb.append((char) 0x0045);
            sb.append((char) 0x0304);
        }
        else if (character == 0x0113) {
            sb.append((char) 0x0065);
            sb.append((char) 0x0304);
        }
        else if (character == 0x0114) {
            sb.append((char) 0x0045);
            sb.append((char) 0x0306);
        }
        else if (character == 0x0115) {
            sb.append((char) 0x0065);
            sb.append((char) 0x0306);
        }
        else if (character == 0x0116) {
            sb.append((char) 0x0045);
            sb.append((char) 0x0307);
        }
        else if (character == 0x0117) {
            sb.append((char) 0x0065);
            sb.append((char) 0x0307);
        }
        else if (character == 0x0118) {
            sb.append((char) 0x0045);
            sb.append((char) 0x0328);
        }
        else if (character == 0x0119) {
            sb.append((char) 0x0065);
            sb.append((char) 0x0328);
        }
        else if (character == 0x011A) {
            sb.append((char) 0x0045);
            sb.append((char) 0x030C);
        }
        else if (character == 0x011B) {
            sb.append((char) 0x0065);
            sb.append((char) 0x030C);
        }
        else if (character == 0x011C) {
            sb.append((char) 0x0047);
            sb.append((char) 0x0302);
        }
        else if (character == 0x011D) {
            sb.append((char) 0x0067);
            sb.append((char) 0x0302);
        }
        else if (character == 0x011E) {
            sb.append((char) 0x0047);
            sb.append((char) 0x0306);
        }
        else if (character == 0x011F) {
            sb.append((char) 0x0067);
            sb.append((char) 0x0306);
        }
        else if (character == 0x0120) {
            sb.append((char) 0x0047);
            sb.append((char) 0x0307);
        }
        else if (character == 0x0121) {
            sb.append((char) 0x0067);
            sb.append((char) 0x0307);
        }
        else if (character == 0x0122) {
            sb.append((char) 0x0047);
            sb.append((char) 0x0327);
        }
        else if (character == 0x0123) {
            sb.append((char) 0x0067);
            sb.append((char) 0x0327);
        }
        else if (character == 0x0124) {
            sb.append((char) 0x0048);
            sb.append((char) 0x0302);
        }
        else if (character == 0x0125) {
            sb.append((char) 0x0068);
            sb.append((char) 0x0302);
        }
        else if (character == 0x0128) {
            sb.append((char) 0x0049);
            sb.append((char) 0x0303);
        }
        else if (character == 0x0129) {
            sb.append((char) 0x0069);
            sb.append((char) 0x0303);
        }
        else if (character == 0x012A) {
            sb.append((char) 0x0049);
            sb.append((char) 0x0304);
        }
        else if (character == 0x012B) {
            sb.append((char) 0x0069);
            sb.append((char) 0x0304);
        }
        else if (character == 0x012C) {
            sb.append((char) 0x0049);
            sb.append((char) 0x0306);
        }
        else if (character == 0x012D) {
            sb.append((char) 0x0069);
            sb.append((char) 0x0306);
        }
        else if (character == 0x012E) {
            sb.append((char) 0x0049);
            sb.append((char) 0x0328);
        }
        else if (character == 0x012F) {
            sb.append((char) 0x0069);
            sb.append((char) 0x0328);
        }
        else if (character == 0x0130) {
            sb.append((char) 0x0049);
            sb.append((char) 0x0307);
        }
        else if (character == 0x0134) {
            sb.append((char) 0x004A);
            sb.append((char) 0x0302);
        }
        else if (character == 0x0135) {
            sb.append((char) 0x006A);
            sb.append((char) 0x0302);
        }
        else if (character == 0x0136) {
            sb.append((char) 0x004B);
            sb.append((char) 0x0327);
        }
        else if (character == 0x0137) {
            sb.append((char) 0x006B);
            sb.append((char) 0x0327);
        }
        else if (character == 0x0139) {
            sb.append((char) 0x004C);
            sb.append((char) 0x0301);
        }
        else if (character == 0x013A) {
            sb.append((char) 0x006C);
            sb.append((char) 0x0301);
        }
        else if (character == 0x013B) {
            sb.append((char) 0x004C);
            sb.append((char) 0x0327);
        }
        else if (character == 0x013C) {
            sb.append((char) 0x006C);
            sb.append((char) 0x0327);
        }
        else if (character == 0x013D) {
            sb.append((char) 0x004C);
            sb.append((char) 0x030C);
        }
        else if (character == 0x013E) {
            sb.append((char) 0x006C);
            sb.append((char) 0x030C);
        }
        else if (character == 0x0143) {
            sb.append((char) 0x004E);
            sb.append((char) 0x0301);
        }
        else if (character == 0x0144) {
            sb.append((char) 0x006E);
            sb.append((char) 0x0301);
        }
        else if (character == 0x0145) {
            sb.append((char) 0x004E);
            sb.append((char) 0x0327);
        }
        else if (character == 0x0146) {
            sb.append((char) 0x006E);
            sb.append((char) 0x0327);
        }
        else if (character == 0x0147) {
            sb.append((char) 0x004E);
            sb.append((char) 0x030C);
        }
        else if (character == 0x0148) {
            sb.append((char) 0x006E);
            sb.append((char) 0x030C);
        }
        else if (character == 0x014C) {
            sb.append((char) 0x004F);
            sb.append((char) 0x0304);
        }
        else if (character == 0x014D) {
            sb.append((char) 0x006F);
            sb.append((char) 0x0304);
        }
        else if (character == 0x014E) {
            sb.append((char) 0x004F);
            sb.append((char) 0x0306);
        }
        else if (character == 0x014F) {
            sb.append((char) 0x006F);
            sb.append((char) 0x0306);
        }
        else if (character == 0x0150) {
            sb.append((char) 0x004F);
            sb.append((char) 0x030B);
        }
        else if (character == 0x0151) {
            sb.append((char) 0x006F);
            sb.append((char) 0x030B);
        }
        else if (character == 0x0154) {
            sb.append((char) 0x0052);
            sb.append((char) 0x0301);
        }
        else if (character == 0x0155) {
            sb.append((char) 0x0072);
            sb.append((char) 0x0301);
        }
        else if (character == 0x0156) {
            sb.append((char) 0x0052);
            sb.append((char) 0x0327);
        }
        else if (character == 0x0157) {
            sb.append((char) 0x0072);
            sb.append((char) 0x0327);
        }
        else if (character == 0x0158) {
            sb.append((char) 0x0052);
            sb.append((char) 0x030C);
        }
        else if (character == 0x0159) {
            sb.append((char) 0x0072);
            sb.append((char) 0x030C);
        }
        else if (character == 0x015A) {
            sb.append((char) 0x0053);
            sb.append((char) 0x0301);
        }
        else if (character == 0x015B) {
            sb.append((char) 0x0073);
            sb.append((char) 0x0301);
        }
        else if (character == 0x015C) {
            sb.append((char) 0x0053);
            sb.append((char) 0x0302);
        }
        else if (character == 0x015D) {
            sb.append((char) 0x0073);
            sb.append((char) 0x0302);
        }
        else if (character == 0x015E) {
            sb.append((char) 0x0053);
            sb.append((char) 0x0327);
        }
        else if (character == 0x015F) {
            sb.append((char) 0x0073);
            sb.append((char) 0x0327);
        }
        else if (character == 0x0160) {
            sb.append((char) 0x0053);
            sb.append((char) 0x030C);
        }
        else if (character == 0x0161) {
            sb.append((char) 0x0073);
            sb.append((char) 0x030C);
        }
        else if (character == 0x0162) {
            sb.append((char) 0x0054);
            sb.append((char) 0x0327);
        }
        else if (character == 0x0163) {
            sb.append((char) 0x0074);
            sb.append((char) 0x0327);
        }
        else if (character == 0x0164) {
            sb.append((char) 0x0054);
            sb.append((char) 0x030C);
        }
        else if (character == 0x0165) {
            sb.append((char) 0x0074);
            sb.append((char) 0x030C);
        }
        else if (character == 0x0168) {
            sb.append((char) 0x0055);
            sb.append((char) 0x0303);
        }
        else if (character == 0x0169) {
            sb.append((char) 0x0075);
            sb.append((char) 0x0303);
        }
        else if (character == 0x016A) {
            sb.append((char) 0x0055);
            sb.append((char) 0x0304);
        }
        else if (character == 0x016B) {
            sb.append((char) 0x0075);
            sb.append((char) 0x0304);
        }
        else if (character == 0x016C) {
            sb.append((char) 0x0055);
            sb.append((char) 0x0306);
        }
        else if (character == 0x016D) {
            sb.append((char) 0x0075);
            sb.append((char) 0x0306);
        }
        else if (character == 0x016E) {
            sb.append((char) 0x0055);
            sb.append((char) 0x030A);
        }
        else if (character == 0x016F) {
            sb.append((char) 0x0075);
            sb.append((char) 0x030A);
        }
        else if (character == 0x0170) {
            sb.append((char) 0x0055);
            sb.append((char) 0x030B);
        }
        else if (character == 0x0171) {
            sb.append((char) 0x0075);
            sb.append((char) 0x030B);
        }
        else if (character == 0x0172) {
            sb.append((char) 0x0055);
            sb.append((char) 0x0328);
        }
        else if (character == 0x0173) {
            sb.append((char) 0x0075);
            sb.append((char) 0x0328);
        }
        else if (character == 0x0174) {
            sb.append((char) 0x0057);
            sb.append((char) 0x0302);
        }
        else if (character == 0x0175) {
            sb.append((char) 0x0077);
            sb.append((char) 0x0302);
        }
        else if (character == 0x0176) {
            sb.append((char) 0x0059);
            sb.append((char) 0x0302);
        }
        else if (character == 0x0177) {
            sb.append((char) 0x0079);
            sb.append((char) 0x0302);
        }
        else if (character == 0x0178) {
            sb.append((char) 0x0059);
            sb.append((char) 0x0308);
        }
        else if (character == 0x0179) {
            sb.append((char) 0x005A);
            sb.append((char) 0x0301);
        }
        else if (character == 0x017A) {
            sb.append((char) 0x007A);
            sb.append((char) 0x0301);
        }
        else if (character == 0x017B) {
            sb.append((char) 0x005A);
            sb.append((char) 0x0307);
        }
        else if (character == 0x017C) {
            sb.append((char) 0x007A);
            sb.append((char) 0x0307);
        }
        else if (character == 0x017D) {
            sb.append((char) 0x005A);
            sb.append((char) 0x030C);
        }
        else if (character == 0x017E) {
            sb.append((char) 0x007A);
            sb.append((char) 0x030C);
        }
        else if (character == 0x01A0) {
            sb.append((char) 0x004F);
            sb.append((char) 0x031B);
        }
        else if (character == 0x01A1) {
            sb.append((char) 0x006F);
            sb.append((char) 0x031B);
        }
        else if (character == 0x01AF) {
            sb.append((char) 0x0055);
            sb.append((char) 0x031B);
        }
        else if (character == 0x01B0) {
            sb.append((char) 0x0075);
            sb.append((char) 0x031B);
        }
        else if (character == 0x01CD) {
            sb.append((char) 0x0041);
            sb.append((char) 0x030C);
        }
        else if (character == 0x01CE) {
            sb.append((char) 0x0061);
            sb.append((char) 0x030C);
        }
        else if (character == 0x01CF) {
            sb.append((char) 0x0049);
            sb.append((char) 0x030C);
        }
        else if (character == 0x01D0) {
            sb.append((char) 0x0069);
            sb.append((char) 0x030C);
        }
        else if (character == 0x01D1) {
            sb.append((char) 0x004F);
            sb.append((char) 0x030C);
        }
        else if (character == 0x01D2) {
            sb.append((char) 0x006F);
            sb.append((char) 0x030C);
        }
        else if (character == 0x01D3) {
            sb.append((char) 0x0055);
            sb.append((char) 0x030C);
        }
        else if (character == 0x01D4) {
            sb.append((char) 0x0075);
            sb.append((char) 0x030C);
        }
        else if (character == 0x01D5) {
            sb.append((char) 0x00DC);
            sb.append((char) 0x0304);
        }
        else if (character == 0x01D6) {
            sb.append((char) 0x00FC);
            sb.append((char) 0x0304);
        }
        else if (character == 0x01D7) {
            sb.append((char) 0x00DC);
            sb.append((char) 0x0301);
        }
        else if (character == 0x01D8) {
            sb.append((char) 0x00FC);
            sb.append((char) 0x0301);
        }
        else if (character == 0x01D9) {
            sb.append((char) 0x00DC);
            sb.append((char) 0x030C);
        }
        else if (character == 0x01DA) {
            sb.append((char) 0x00FC);
            sb.append((char) 0x030C);
        }
        else if (character == 0x01DB) {
            sb.append((char) 0x00DC);
            sb.append((char) 0x0300);
        }
        else if (character == 0x01DC) {
            sb.append((char) 0x00FC);
            sb.append((char) 0x0300);
        }
        else if (character == 0x01DE) {
            sb.append((char) 0x00C4);
            sb.append((char) 0x0304);
        }
        else if (character == 0x01DF) {
            sb.append((char) 0x00E4);
            sb.append((char) 0x0304);
        }
        else if (character == 0x01E0) {
            sb.append((char) 0x0226);
            sb.append((char) 0x0304);
        }
        else if (character == 0x01E1) {
            sb.append((char) 0x0227);
            sb.append((char) 0x0304);
        }
        else if (character == 0x01E2) {
            sb.append((char) 0x00C6);
            sb.append((char) 0x0304);
        }
        else if (character == 0x01E3) {
            sb.append((char) 0x00E6);
            sb.append((char) 0x0304);
        }
        else if (character == 0x01E6) {
            sb.append((char) 0x0047);
            sb.append((char) 0x030C);
        }
        else if (character == 0x01E7) {
            sb.append((char) 0x0067);
            sb.append((char) 0x030C);
        }
        else if (character == 0x01E8) {
            sb.append((char) 0x004B);
            sb.append((char) 0x030C);
        }
        else if (character == 0x01E9) {
            sb.append((char) 0x006B);
            sb.append((char) 0x030C);
        }
        else if (character == 0x01EA) {
            sb.append((char) 0x004F);
            sb.append((char) 0x0328);
        }
        else if (character == 0x01EB) {
            sb.append((char) 0x006F);
            sb.append((char) 0x0328);
        }
        else if (character == 0x01EC) {
            sb.append((char) 0x01EA);
            sb.append((char) 0x0304);
        }
        else if (character == 0x01ED) {
            sb.append((char) 0x01EB);
            sb.append((char) 0x0304);
        }
        else if (character == 0x01EE) {
            sb.append((char) 0x01B7);
            sb.append((char) 0x030C);
        }
        else if (character == 0x01EF) {
            sb.append((char) 0x0292);
            sb.append((char) 0x030C);
        }
        else if (character == 0x01F0) {
            sb.append((char) 0x006A);
            sb.append((char) 0x030C);
        }
        else if (character == 0x01F4) {
            sb.append((char) 0x0047);
            sb.append((char) 0x0301);
        }
        else if (character == 0x01F5) {
            sb.append((char) 0x0067);
            sb.append((char) 0x0301);
        }
        else if (character == 0x01F8) {
            sb.append((char) 0x004E);
            sb.append((char) 0x0300);
        }
        else if (character == 0x01F9) {
            sb.append((char) 0x006E);
            sb.append((char) 0x0300);
        }
        else if (character == 0x01FA) {
            sb.append((char) 0x00C5);
            sb.append((char) 0x0301);
        }
        else if (character == 0x01FB) {
            sb.append((char) 0x00E5);
            sb.append((char) 0x0301);
        }
        else if (character == 0x01FC) {
            sb.append((char) 0x00C6);
            sb.append((char) 0x0301);
        }
        else if (character == 0x01FD) {
            sb.append((char) 0x00E6);
            sb.append((char) 0x0301);
        }
        else if (character == 0x01FE) {
            sb.append((char) 0x00D8);
            sb.append((char) 0x0301);
        }
        else if (character == 0x01FF) {
            sb.append((char) 0x00F8);
            sb.append((char) 0x0301);
        }
        else if (character == 0x0200) {
            sb.append((char) 0x0041);
            sb.append((char) 0x030F);
        }
        else if (character == 0x0201) {
            sb.append((char) 0x0061);
            sb.append((char) 0x030F);
        }
        else if (character == 0x0202) {
            sb.append((char) 0x0041);
            sb.append((char) 0x0311);
        }
        else if (character == 0x0203) {
            sb.append((char) 0x0061);
            sb.append((char) 0x0311);
        }
        else if (character == 0x0204) {
            sb.append((char) 0x0045);
            sb.append((char) 0x030F);
        }
        else if (character == 0x0205) {
            sb.append((char) 0x0065);
            sb.append((char) 0x030F);
        }
        else if (character == 0x0206) {
            sb.append((char) 0x0045);
            sb.append((char) 0x0311);
        }
        else if (character == 0x0207) {
            sb.append((char) 0x0065);
            sb.append((char) 0x0311);
        }
        else if (character == 0x0208) {
            sb.append((char) 0x0049);
            sb.append((char) 0x030F);
        }
        else if (character == 0x0209) {
            sb.append((char) 0x0069);
            sb.append((char) 0x030F);
        }
        else if (character == 0x020A) {
            sb.append((char) 0x0049);
            sb.append((char) 0x0311);
        }
        else if (character == 0x020B) {
            sb.append((char) 0x0069);
            sb.append((char) 0x0311);
        }
        else if (character == 0x020C) {
            sb.append((char) 0x004F);
            sb.append((char) 0x030F);
        }
        else if (character == 0x020D) {
            sb.append((char) 0x006F);
            sb.append((char) 0x030F);
        }
        else if (character == 0x020E) {
            sb.append((char) 0x004F);
            sb.append((char) 0x0311);
        }
        else if (character == 0x020F) {
            sb.append((char) 0x006F);
            sb.append((char) 0x0311);
        }
        else if (character == 0x0210) {
            sb.append((char) 0x0052);
            sb.append((char) 0x030F);
        }
        else if (character == 0x0211) {
            sb.append((char) 0x0072);
            sb.append((char) 0x030F);
        }
        else if (character == 0x0212) {
            sb.append((char) 0x0052);
            sb.append((char) 0x0311);
        }
        else if (character == 0x0213) {
            sb.append((char) 0x0072);
            sb.append((char) 0x0311);
        }
        else if (character == 0x0214) {
            sb.append((char) 0x0055);
            sb.append((char) 0x030F);
        }
        else if (character == 0x0215) {
            sb.append((char) 0x0075);
            sb.append((char) 0x030F);
        }
        else if (character == 0x0216) {
            sb.append((char) 0x0055);
            sb.append((char) 0x0311);
        }
        else if (character == 0x0217) {
            sb.append((char) 0x0075);
            sb.append((char) 0x0311);
        }
        else if (character == 0x0218) {
            sb.append((char) 0x0053);
            sb.append((char) 0x0326);
        }
        else if (character == 0x0219) {
            sb.append((char) 0x0073);
            sb.append((char) 0x0326);
        }
        else if (character == 0x021A) {
            sb.append((char) 0x0054);
            sb.append((char) 0x0326);
        }
        else if (character == 0x021B) {
            sb.append((char) 0x0074);
            sb.append((char) 0x0326);
        }
        else if (character == 0x021E) {
            sb.append((char) 0x0048);
            sb.append((char) 0x030C);
        }
        else if (character == 0x021F) {
            sb.append((char) 0x0068);
            sb.append((char) 0x030C);
        }
        else if (character == 0x0226) {
            sb.append((char) 0x0041);
            sb.append((char) 0x0307);
        }
        else if (character == 0x0227) {
            sb.append((char) 0x0061);
            sb.append((char) 0x0307);
        }
        else if (character == 0x0228) {
            sb.append((char) 0x0045);
            sb.append((char) 0x0327);
        }
        else if (character == 0x0229) {
            sb.append((char) 0x0065);
            sb.append((char) 0x0327);
        }
        else if (character == 0x022A) {
            sb.append((char) 0x00D6);
            sb.append((char) 0x0304);
        }
        else if (character == 0x022B) {
            sb.append((char) 0x00F6);
            sb.append((char) 0x0304);
        }
        else if (character == 0x022C) {
            sb.append((char) 0x00D5);
            sb.append((char) 0x0304);
        }
        else if (character == 0x022D) {
            sb.append((char) 0x00F5);
            sb.append((char) 0x0304);
        }
        else if (character == 0x022E) {
            sb.append((char) 0x004F);
            sb.append((char) 0x0307);
        }
        else if (character == 0x022F) {
            sb.append((char) 0x006F);
            sb.append((char) 0x0307);
        }
        else if (character == 0x0230) {
            sb.append((char) 0x022E);
            sb.append((char) 0x0304);
        }
        else if (character == 0x0231) {
            sb.append((char) 0x022F);
            sb.append((char) 0x0304);
        }
        else if (character == 0x0232) {
            sb.append((char) 0x0059);
            sb.append((char) 0x0304);
        }
        else if (character == 0x0233) {
            sb.append((char) 0x0079);
            sb.append((char) 0x0304);
        }
        else if (character == 0x0340) {
            sb.append((char) 0x0300);
        }
        else if (character == 0x0341) {
            sb.append((char) 0x0301);
        }
        else if (character == 0x0343) {
            sb.append((char) 0x0313);
        }
        else if (character == 0x0344) {
            sb.append((char) 0x0308);
            sb.append((char) 0x0301);
        }
        else if (character == 0x0374) {
            sb.append((char) 0x02B9);
        }
        else if (character == 0x037E) {
            sb.append((char) 0x003B);
        }
        else if (character == 0x0385) {
            sb.append((char) 0x00A8);
            sb.append((char) 0x0301);
        }
        else if (character == 0x0386) {
            sb.append((char) 0x0391);
            sb.append((char) 0x0301);
        }
        else if (character == 0x0387) {
            sb.append((char) 0x00B7);
        }
        else if (character == 0x0388) {
            sb.append((char) 0x0395);
            sb.append((char) 0x0301);
        }
        else if (character == 0x0389) {
            sb.append((char) 0x0397);
            sb.append((char) 0x0301);
        }
        else if (character == 0x038A) {
            sb.append((char) 0x0399);
            sb.append((char) 0x0301);
        }
        else if (character == 0x038C) {
            sb.append((char) 0x039F);
            sb.append((char) 0x0301);
        }
        else if (character == 0x038E) {
            sb.append((char) 0x03A5);
            sb.append((char) 0x0301);
        }
        else if (character == 0x038F) {
            sb.append((char) 0x03A9);
            sb.append((char) 0x0301);
        }
        else if (character == 0x0390) {
            sb.append((char) 0x03CA);
            sb.append((char) 0x0301);
        }
        else if (character == 0x03AA) {
            sb.append((char) 0x0399);
            sb.append((char) 0x0308);
        }
        else if (character == 0x03AB) {
            sb.append((char) 0x03A5);
            sb.append((char) 0x0308);
        }
        else if (character == 0x03AC) {
            sb.append((char) 0x03B1);
            sb.append((char) 0x0301);
        }
        else if (character == 0x03AD) {
            sb.append((char) 0x03B5);
            sb.append((char) 0x0301);
        }
        else if (character == 0x03AE) {
            sb.append((char) 0x03B7);
            sb.append((char) 0x0301);
        }
        else if (character == 0x03AF) {
            sb.append((char) 0x03B9);
            sb.append((char) 0x0301);
        }
        else if (character == 0x03B0) {
            sb.append((char) 0x03CB);
            sb.append((char) 0x0301);
        }
        else if (character == 0x03CA) {
            sb.append((char) 0x03B9);
            sb.append((char) 0x0308);
        }
        else if (character == 0x03CB) {
            sb.append((char) 0x03C5);
            sb.append((char) 0x0308);
        }
        else if (character == 0x03CC) {
            sb.append((char) 0x03BF);
            sb.append((char) 0x0301);
        }
        else if (character == 0x03CD) {
            sb.append((char) 0x03C5);
            sb.append((char) 0x0301);
        }
        else if (character == 0x03CE) {
            sb.append((char) 0x03C9);
            sb.append((char) 0x0301);
        }
        else if (character == 0x03D3) {
            sb.append((char) 0x03D2);
            sb.append((char) 0x0301);
        }
        else if (character == 0x03D4) {
            sb.append((char) 0x03D2);
            sb.append((char) 0x0308);
        }
        else if (character == 0x0400) {
            sb.append((char) 0x0415);
            sb.append((char) 0x0300);
        }
        else if (character == 0x0401) {
            sb.append((char) 0x0415);
            sb.append((char) 0x0308);
        }
        else if (character == 0x0403) {
            sb.append((char) 0x0413);
            sb.append((char) 0x0301);
        }
        else if (character == 0x0407) {
            sb.append((char) 0x0406);
            sb.append((char) 0x0308);
        }
        else if (character == 0x040C) {
            sb.append((char) 0x041A);
            sb.append((char) 0x0301);
        }
        else if (character == 0x040D) {
            sb.append((char) 0x0418);
            sb.append((char) 0x0300);
        }
        else if (character == 0x040E) {
            sb.append((char) 0x0423);
            sb.append((char) 0x0306);
        }
        else if (character == 0x0419) {
            sb.append((char) 0x0418);
            sb.append((char) 0x0306);
        }
        else if (character == 0x0439) {
            sb.append((char) 0x0438);
            sb.append((char) 0x0306);
        }
        else if (character == 0x0450) {
            sb.append((char) 0x0435);
            sb.append((char) 0x0300);
        }
        else if (character == 0x0451) {
            sb.append((char) 0x0435);
            sb.append((char) 0x0308);
        }
        else if (character == 0x0453) {
            sb.append((char) 0x0433);
            sb.append((char) 0x0301);
        }
        else if (character == 0x0457) {
            sb.append((char) 0x0456);
            sb.append((char) 0x0308);
        }
        else if (character == 0x045C) {
            sb.append((char) 0x043A);
            sb.append((char) 0x0301);
        }
        else if (character == 0x045D) {
            sb.append((char) 0x0438);
            sb.append((char) 0x0300);
        }
        else if (character == 0x045E) {
            sb.append((char) 0x0443);
            sb.append((char) 0x0306);
        }
        else if (character == 0x0476) {
            sb.append((char) 0x0474);
            sb.append((char) 0x030F);
        }
        else if (character == 0x0477) {
            sb.append((char) 0x0475);
            sb.append((char) 0x030F);
        }
        else if (character == 0x04C1) {
            sb.append((char) 0x0416);
            sb.append((char) 0x0306);
        }
        else if (character == 0x04C2) {
            sb.append((char) 0x0436);
            sb.append((char) 0x0306);
        }
        else if (character == 0x04D0) {
            sb.append((char) 0x0410);
            sb.append((char) 0x0306);
        }
        else if (character == 0x04D1) {
            sb.append((char) 0x0430);
            sb.append((char) 0x0306);
        }
        else if (character == 0x04D2) {
            sb.append((char) 0x0410);
            sb.append((char) 0x0308);
        }
        else if (character == 0x04D3) {
            sb.append((char) 0x0430);
            sb.append((char) 0x0308);
        }
        else if (character == 0x04D6) {
            sb.append((char) 0x0415);
            sb.append((char) 0x0306);
        }
        else if (character == 0x04D7) {
            sb.append((char) 0x0435);
            sb.append((char) 0x0306);
        }
        else if (character == 0x04DA) {
            sb.append((char) 0x04D8);
            sb.append((char) 0x0308);
        }
        else if (character == 0x04DB) {
            sb.append((char) 0x04D9);
            sb.append((char) 0x0308);
        }
        else if (character == 0x04DC) {
            sb.append((char) 0x0416);
            sb.append((char) 0x0308);
        }
        else if (character == 0x04DD) {
            sb.append((char) 0x0436);
            sb.append((char) 0x0308);
        }
        else if (character == 0x04DE) {
            sb.append((char) 0x0417);
            sb.append((char) 0x0308);
        }
        else if (character == 0x04DF) {
            sb.append((char) 0x0437);
            sb.append((char) 0x0308);
        }
        else if (character == 0x04E2) {
            sb.append((char) 0x0418);
            sb.append((char) 0x0304);
        }
        else if (character == 0x04E3) {
            sb.append((char) 0x0438);
            sb.append((char) 0x0304);
        }
        else if (character == 0x04E4) {
            sb.append((char) 0x0418);
            sb.append((char) 0x0308);
        }
        else if (character == 0x04E5) {
            sb.append((char) 0x0438);
            sb.append((char) 0x0308);
        }
        else if (character == 0x04E6) {
            sb.append((char) 0x041E);
            sb.append((char) 0x0308);
        }
        else if (character == 0x04E7) {
            sb.append((char) 0x043E);
            sb.append((char) 0x0308);
        }
        else if (character == 0x04EA) {
            sb.append((char) 0x04E8);
            sb.append((char) 0x0308);
        }
        else if (character == 0x04EB) {
            sb.append((char) 0x04E9);
            sb.append((char) 0x0308);
        }
        else if (character == 0x04EC) {
            sb.append((char) 0x042D);
            sb.append((char) 0x0308);
        }
        else if (character == 0x04ED) {
            sb.append((char) 0x044D);
            sb.append((char) 0x0308);
        }
        else if (character == 0x04EE) {
            sb.append((char) 0x0423);
            sb.append((char) 0x0304);
        }
        else if (character == 0x04EF) {
            sb.append((char) 0x0443);
            sb.append((char) 0x0304);
        }
        else if (character == 0x04F0) {
            sb.append((char) 0x0423);
            sb.append((char) 0x0308);
        }
        else if (character == 0x04F1) {
            sb.append((char) 0x0443);
            sb.append((char) 0x0308);
        }
        else if (character == 0x04F2) {
            sb.append((char) 0x0423);
            sb.append((char) 0x030B);
        }
        else if (character == 0x04F3) {
            sb.append((char) 0x0443);
            sb.append((char) 0x030B);
        }
        else if (character == 0x04F4) {
            sb.append((char) 0x0427);
            sb.append((char) 0x0308);
        }
        else if (character == 0x04F5) {
            sb.append((char) 0x0447);
            sb.append((char) 0x0308);
        }
        else if (character == 0x04F8) {
            sb.append((char) 0x042B);
            sb.append((char) 0x0308);
        }
        else if (character == 0x04F9) {
            sb.append((char) 0x044B);
            sb.append((char) 0x0308);
        }
        else if (character == 0x0622) {
            sb.append((char) 0x0627);
            sb.append((char) 0x0653);
        }
        else if (character == 0x0623) {
            sb.append((char) 0x0627);
            sb.append((char) 0x0654);
        }
        else if (character == 0x0624) {
            sb.append((char) 0x0648);
            sb.append((char) 0x0654);
        }
        else if (character == 0x0625) {
            sb.append((char) 0x0627);
            sb.append((char) 0x0655);
        }
        else if (character == 0x0626) {
            sb.append((char) 0x064A);
            sb.append((char) 0x0654);
        }
        else if (character == 0x06C0) {
            sb.append((char) 0x06D5);
            sb.append((char) 0x0654);
        }
        else if (character == 0x06C2) {
            sb.append((char) 0x06C1);
            sb.append((char) 0x0654);
        }
        else if (character == 0x06D3) {
            sb.append((char) 0x06D2);
            sb.append((char) 0x0654);
        }
        else if (character == 0x0929) {
            sb.append((char) 0x0928);
            sb.append((char) 0x093C);
        }
        else if (character == 0x0931) {
            sb.append((char) 0x0930);
            sb.append((char) 0x093C);
        }
        else if (character == 0x0934) {
            sb.append((char) 0x0933);
            sb.append((char) 0x093C);
        }
        else if (character == 0x0958) {
            sb.append((char) 0x0915);
            sb.append((char) 0x093C);
        }
        else if (character == 0x0959) {
            sb.append((char) 0x0916);
            sb.append((char) 0x093C);
        }
        else if (character == 0x095A) {
            sb.append((char) 0x0917);
            sb.append((char) 0x093C);
        }
        else if (character == 0x095B) {
            sb.append((char) 0x091C);
            sb.append((char) 0x093C);
        }
        else if (character == 0x095C) {
            sb.append((char) 0x0921);
            sb.append((char) 0x093C);
        }
        else if (character == 0x095D) {
            sb.append((char) 0x0922);
            sb.append((char) 0x093C);
        }
        else if (character == 0x095E) {
            sb.append((char) 0x092B);
            sb.append((char) 0x093C);
        }
        else if (character == 0x095F) {
            sb.append((char) 0x092F);
            sb.append((char) 0x093C);
        }
        else if (character == 0x09CB) {
            sb.append((char) 0x09C7);
            sb.append((char) 0x09BE);
        }
        else if (character == 0x09CC) {
            sb.append((char) 0x09C7);
            sb.append((char) 0x09D7);
        }
        else if (character == 0x09DC) {
            sb.append((char) 0x09A1);
            sb.append((char) 0x09BC);
        }
        else if (character == 0x09DD) {
            sb.append((char) 0x09A2);
            sb.append((char) 0x09BC);
        }
        else if (character == 0x09DF) {
            sb.append((char) 0x09AF);
            sb.append((char) 0x09BC);
        }
        else if (character == 0x0A33) {
            sb.append((char) 0x0A32);
            sb.append((char) 0x0A3C);
        }
        else if (character == 0x0A36) {
            sb.append((char) 0x0A38);
            sb.append((char) 0x0A3C);
        }
        else if (character == 0x0A59) {
            sb.append((char) 0x0A16);
            sb.append((char) 0x0A3C);
        }
        else if (character == 0x0A5A) {
            sb.append((char) 0x0A17);
            sb.append((char) 0x0A3C);
        }
        else if (character == 0x0A5B) {
            sb.append((char) 0x0A1C);
            sb.append((char) 0x0A3C);
        }
        else if (character == 0x0A5E) {
            sb.append((char) 0x0A2B);
            sb.append((char) 0x0A3C);
        }
        else if (character == 0x0B48) {
            sb.append((char) 0x0B47);
            sb.append((char) 0x0B56);
        }
        else if (character == 0x0B4B) {
            sb.append((char) 0x0B47);
            sb.append((char) 0x0B3E);
        }
        else if (character == 0x0B4C) {
            sb.append((char) 0x0B47);
            sb.append((char) 0x0B57);
        }
        else if (character == 0x0B5C) {
            sb.append((char) 0x0B21);
            sb.append((char) 0x0B3C);
        }
        else if (character == 0x0B5D) {
            sb.append((char) 0x0B22);
            sb.append((char) 0x0B3C);
        }
        else if (character == 0x0B94) {
            sb.append((char) 0x0B92);
            sb.append((char) 0x0BD7);
        }
        else if (character == 0x0BCA) {
            sb.append((char) 0x0BC6);
            sb.append((char) 0x0BBE);
        }
        else if (character == 0x0BCB) {
            sb.append((char) 0x0BC7);
            sb.append((char) 0x0BBE);
        }
        else if (character == 0x0BCC) {
            sb.append((char) 0x0BC6);
            sb.append((char) 0x0BD7);
        }
        else if (character == 0x0C48) {
            sb.append((char) 0x0C46);
            sb.append((char) 0x0C56);
        }
        else if (character == 0x0CC0) {
            sb.append((char) 0x0CBF);
            sb.append((char) 0x0CD5);
        }
        else if (character == 0x0CC7) {
            sb.append((char) 0x0CC6);
            sb.append((char) 0x0CD5);
        }
        else if (character == 0x0CC8) {
            sb.append((char) 0x0CC6);
            sb.append((char) 0x0CD6);
        }
        else if (character == 0x0CCA) {
            sb.append((char) 0x0CC6);
            sb.append((char) 0x0CC2);
        }
        else if (character == 0x0CCB) {
            sb.append((char) 0x0CCA);
            sb.append((char) 0x0CD5);
        }
        else if (character == 0x0D4A) {
            sb.append((char) 0x0D46);
            sb.append((char) 0x0D3E);
        }
        else if (character == 0x0D4B) {
            sb.append((char) 0x0D47);
            sb.append((char) 0x0D3E);
        }
        else if (character == 0x0D4C) {
            sb.append((char) 0x0D46);
            sb.append((char) 0x0D57);
        }
        else if (character == 0x0DDA) {
            sb.append((char) 0x0DD9);
            sb.append((char) 0x0DCA);
        }
        else if (character == 0x0DDC) {
            sb.append((char) 0x0DD9);
            sb.append((char) 0x0DCF);
        }
        else if (character == 0x0DDD) {
            sb.append((char) 0x0DDC);
            sb.append((char) 0x0DCA);
        }
        else if (character == 0x0DDE) {
            sb.append((char) 0x0DD9);
            sb.append((char) 0x0DDF);
        }
        else if (character == 0x0F43) {
            sb.append((char) 0x0F42);
            sb.append((char) 0x0FB7);
        }
        else if (character == 0x0F4D) {
            sb.append((char) 0x0F4C);
            sb.append((char) 0x0FB7);
        }
        else if (character == 0x0F52) {
            sb.append((char) 0x0F51);
            sb.append((char) 0x0FB7);
        }
        else if (character == 0x0F57) {
            sb.append((char) 0x0F56);
            sb.append((char) 0x0FB7);
        }
        else if (character == 0x0F5C) {
            sb.append((char) 0x0F5B);
            sb.append((char) 0x0FB7);
        }
        else if (character == 0x0F69) {
            sb.append((char) 0x0F40);
            sb.append((char) 0x0FB5);
        }
        else if (character == 0x0F73) {
            sb.append((char) 0x0F71);
            sb.append((char) 0x0F72);
        }
        else if (character == 0x0F75) {
            sb.append((char) 0x0F71);
            sb.append((char) 0x0F74);
        }
        else if (character == 0x0F76) {
            sb.append((char) 0x0FB2);
            sb.append((char) 0x0F80);
        }
        else if (character == 0x0F78) {
            sb.append((char) 0x0FB3);
            sb.append((char) 0x0F80);
        }
        else if (character == 0x0F81) {
            sb.append((char) 0x0F71);
            sb.append((char) 0x0F80);
        }
        else if (character == 0x0F93) {
            sb.append((char) 0x0F92);
            sb.append((char) 0x0FB7);
        }
        else if (character == 0x0F9D) {
            sb.append((char) 0x0F9C);
            sb.append((char) 0x0FB7);
        }
        else if (character == 0x0FA2) {
            sb.append((char) 0x0FA1);
            sb.append((char) 0x0FB7);
        }
        else if (character == 0x0FA7) {
            sb.append((char) 0x0FA6);
            sb.append((char) 0x0FB7);
        }
        else if (character == 0x0FAC) {
            sb.append((char) 0x0FAB);
            sb.append((char) 0x0FB7);
        }
        else if (character == 0x0FB9) {
            sb.append((char) 0x0F90);
            sb.append((char) 0x0FB5);
        }
        else if (character == 0x1026) {
            sb.append((char) 0x1025);
            sb.append((char) 0x102E);
        }
        else if (character == 0x1E00) {
            sb.append((char) 0x0041);
            sb.append((char) 0x0325);
        }
        else if (character == 0x1E01) {
            sb.append((char) 0x0061);
            sb.append((char) 0x0325);
        }
        else if (character == 0x1E02) {
            sb.append((char) 0x0042);
            sb.append((char) 0x0307);
        }
        else if (character == 0x1E03) {
            sb.append((char) 0x0062);
            sb.append((char) 0x0307);
        }
        else if (character == 0x1E04) {
            sb.append((char) 0x0042);
            sb.append((char) 0x0323);
        }
        else if (character == 0x1E05) {
            sb.append((char) 0x0062);
            sb.append((char) 0x0323);
        }
        else if (character == 0x1E06) {
            sb.append((char) 0x0042);
            sb.append((char) 0x0331);
        }
        else if (character == 0x1E07) {
            sb.append((char) 0x0062);
            sb.append((char) 0x0331);
        }
        else if (character == 0x1E08) {
            sb.append((char) 0x00C7);
            sb.append((char) 0x0301);
        }
        else if (character == 0x1E09) {
            sb.append((char) 0x00E7);
            sb.append((char) 0x0301);
        }
        else if (character == 0x1E0A) {
            sb.append((char) 0x0044);
            sb.append((char) 0x0307);
        }
        else if (character == 0x1E0B) {
            sb.append((char) 0x0064);
            sb.append((char) 0x0307);
        }
        else if (character == 0x1E0C) {
            sb.append((char) 0x0044);
            sb.append((char) 0x0323);
        }
        else if (character == 0x1E0D) {
            sb.append((char) 0x0064);
            sb.append((char) 0x0323);
        }
        else if (character == 0x1E0E) {
            sb.append((char) 0x0044);
            sb.append((char) 0x0331);
        }
        else if (character == 0x1E0F) {
            sb.append((char) 0x0064);
            sb.append((char) 0x0331);
        }
        else if (character == 0x1E10) {
            sb.append((char) 0x0044);
            sb.append((char) 0x0327);
        }
        else if (character == 0x1E11) {
            sb.append((char) 0x0064);
            sb.append((char) 0x0327);
        }
        else if (character == 0x1E12) {
            sb.append((char) 0x0044);
            sb.append((char) 0x032D);
        }
        else if (character == 0x1E13) {
            sb.append((char) 0x0064);
            sb.append((char) 0x032D);
        }
        else if (character == 0x1E14) {
            sb.append((char) 0x0112);
            sb.append((char) 0x0300);
        }
        else if (character == 0x1E15) {
            sb.append((char) 0x0113);
            sb.append((char) 0x0300);
        }
        else if (character == 0x1E16) {
            sb.append((char) 0x0112);
            sb.append((char) 0x0301);
        }
        else if (character == 0x1E17) {
            sb.append((char) 0x0113);
            sb.append((char) 0x0301);
        }
        else if (character == 0x1E18) {
            sb.append((char) 0x0045);
            sb.append((char) 0x032D);
        }
        else if (character == 0x1E19) {
            sb.append((char) 0x0065);
            sb.append((char) 0x032D);
        }
        else if (character == 0x1E1A) {
            sb.append((char) 0x0045);
            sb.append((char) 0x0330);
        }
        else if (character == 0x1E1B) {
            sb.append((char) 0x0065);
            sb.append((char) 0x0330);
        }
        else if (character == 0x1E1C) {
            sb.append((char) 0x0228);
            sb.append((char) 0x0306);
        }
        else if (character == 0x1E1D) {
            sb.append((char) 0x0229);
            sb.append((char) 0x0306);
        }
        else if (character == 0x1E1E) {
            sb.append((char) 0x0046);
            sb.append((char) 0x0307);
        }
        else if (character == 0x1E1F) {
            sb.append((char) 0x0066);
            sb.append((char) 0x0307);
        }
        else if (character == 0x1E20) {
            sb.append((char) 0x0047);
            sb.append((char) 0x0304);
        }
        else if (character == 0x1E21) {
            sb.append((char) 0x0067);
            sb.append((char) 0x0304);
        }
        else if (character == 0x1E22) {
            sb.append((char) 0x0048);
            sb.append((char) 0x0307);
        }
        else if (character == 0x1E23) {
            sb.append((char) 0x0068);
            sb.append((char) 0x0307);
        }
        else if (character == 0x1E24) {
            sb.append((char) 0x0048);
            sb.append((char) 0x0323);
        }
        else if (character == 0x1E25) {
            sb.append((char) 0x0068);
            sb.append((char) 0x0323);
        }
        else if (character == 0x1E26) {
            sb.append((char) 0x0048);
            sb.append((char) 0x0308);
        }
        else if (character == 0x1E27) {
            sb.append((char) 0x0068);
            sb.append((char) 0x0308);
        }
        else if (character == 0x1E28) {
            sb.append((char) 0x0048);
            sb.append((char) 0x0327);
        }
        else if (character == 0x1E29) {
            sb.append((char) 0x0068);
            sb.append((char) 0x0327);
        }
        else if (character == 0x1E2A) {
            sb.append((char) 0x0048);
            sb.append((char) 0x032E);
        }
        else if (character == 0x1E2B) {
            sb.append((char) 0x0068);
            sb.append((char) 0x032E);
        }
        else if (character == 0x1E2C) {
            sb.append((char) 0x0049);
            sb.append((char) 0x0330);
        }
        else if (character == 0x1E2D) {
            sb.append((char) 0x0069);
            sb.append((char) 0x0330);
        }
        else if (character == 0x1E2E) {
            sb.append((char) 0x00CF);
            sb.append((char) 0x0301);
        }
        else if (character == 0x1E2F) {
            sb.append((char) 0x00EF);
            sb.append((char) 0x0301);
        }
        else if (character == 0x1E30) {
            sb.append((char) 0x004B);
            sb.append((char) 0x0301);
        }
        else if (character == 0x1E31) {
            sb.append((char) 0x006B);
            sb.append((char) 0x0301);
        }
        else if (character == 0x1E32) {
            sb.append((char) 0x004B);
            sb.append((char) 0x0323);
        }
        else if (character == 0x1E33) {
            sb.append((char) 0x006B);
            sb.append((char) 0x0323);
        }
        else if (character == 0x1E34) {
            sb.append((char) 0x004B);
            sb.append((char) 0x0331);
        }
        else if (character == 0x1E35) {
            sb.append((char) 0x006B);
            sb.append((char) 0x0331);
        }
        else if (character == 0x1E36) {
            sb.append((char) 0x004C);
            sb.append((char) 0x0323);
        }
        else if (character == 0x1E37) {
            sb.append((char) 0x006C);
            sb.append((char) 0x0323);
        }
        else if (character == 0x1E38) {
            sb.append((char) 0x1E36);
            sb.append((char) 0x0304);
        }
        else if (character == 0x1E39) {
            sb.append((char) 0x1E37);
            sb.append((char) 0x0304);
        }
        else if (character == 0x1E3A) {
            sb.append((char) 0x004C);
            sb.append((char) 0x0331);
        }
        else if (character == 0x1E3B) {
            sb.append((char) 0x006C);
            sb.append((char) 0x0331);
        }
        else if (character == 0x1E3C) {
            sb.append((char) 0x004C);
            sb.append((char) 0x032D);
        }
        else if (character == 0x1E3D) {
            sb.append((char) 0x006C);
            sb.append((char) 0x032D);
        }
        else if (character == 0x1E3E) {
            sb.append((char) 0x004D);
            sb.append((char) 0x0301);
        }
        else if (character == 0x1E3F) {
            sb.append((char) 0x006D);
            sb.append((char) 0x0301);
        }
        else if (character == 0x1E40) {
            sb.append((char) 0x004D);
            sb.append((char) 0x0307);
        }
        else if (character == 0x1E41) {
            sb.append((char) 0x006D);
            sb.append((char) 0x0307);
        }
        else if (character == 0x1E42) {
            sb.append((char) 0x004D);
            sb.append((char) 0x0323);
        }
        else if (character == 0x1E43) {
            sb.append((char) 0x006D);
            sb.append((char) 0x0323);
        }
        else if (character == 0x1E44) {
            sb.append((char) 0x004E);
            sb.append((char) 0x0307);
        }
        else if (character == 0x1E45) {
            sb.append((char) 0x006E);
            sb.append((char) 0x0307);
        }
        else if (character == 0x1E46) {
            sb.append((char) 0x004E);
            sb.append((char) 0x0323);
        }
        else if (character == 0x1E47) {
            sb.append((char) 0x006E);
            sb.append((char) 0x0323);
        }
        else if (character == 0x1E48) {
            sb.append((char) 0x004E);
            sb.append((char) 0x0331);
        }
        else if (character == 0x1E49) {
            sb.append((char) 0x006E);
            sb.append((char) 0x0331);
        }
        else if (character == 0x1E4A) {
            sb.append((char) 0x004E);
            sb.append((char) 0x032D);
        }
        else if (character == 0x1E4B) {
            sb.append((char) 0x006E);
            sb.append((char) 0x032D);
        }
        else if (character == 0x1E4C) {
            sb.append((char) 0x00D5);
            sb.append((char) 0x0301);
        }
        else if (character == 0x1E4D) {
            sb.append((char) 0x00F5);
            sb.append((char) 0x0301);
        }
        else if (character == 0x1E4E) {
            sb.append((char) 0x00D5);
            sb.append((char) 0x0308);
        }
        else if (character == 0x1E4F) {
            sb.append((char) 0x00F5);
            sb.append((char) 0x0308);
        }
        else if (character == 0x1E50) {
            sb.append((char) 0x014C);
            sb.append((char) 0x0300);
        }
        else if (character == 0x1E51) {
            sb.append((char) 0x014D);
            sb.append((char) 0x0300);
        }
        else if (character == 0x1E52) {
            sb.append((char) 0x014C);
            sb.append((char) 0x0301);
        }
        else if (character == 0x1E53) {
            sb.append((char) 0x014D);
            sb.append((char) 0x0301);
        }
        else if (character == 0x1E54) {
            sb.append((char) 0x0050);
            sb.append((char) 0x0301);
        }
        else if (character == 0x1E55) {
            sb.append((char) 0x0070);
            sb.append((char) 0x0301);
        }
        else if (character == 0x1E56) {
            sb.append((char) 0x0050);
            sb.append((char) 0x0307);
        }
        else if (character == 0x1E57) {
            sb.append((char) 0x0070);
            sb.append((char) 0x0307);
        }
        else if (character == 0x1E58) {
            sb.append((char) 0x0052);
            sb.append((char) 0x0307);
        }
        else if (character == 0x1E59) {
            sb.append((char) 0x0072);
            sb.append((char) 0x0307);
        }
        else if (character == 0x1E5A) {
            sb.append((char) 0x0052);
            sb.append((char) 0x0323);
        }
        else if (character == 0x1E5B) {
            sb.append((char) 0x0072);
            sb.append((char) 0x0323);
        }
        else if (character == 0x1E5C) {
            sb.append((char) 0x1E5A);
            sb.append((char) 0x0304);
        }
        else if (character == 0x1E5D) {
            sb.append((char) 0x1E5B);
            sb.append((char) 0x0304);
        }
        else if (character == 0x1E5E) {
            sb.append((char) 0x0052);
            sb.append((char) 0x0331);
        }
        else if (character == 0x1E5F) {
            sb.append((char) 0x0072);
            sb.append((char) 0x0331);
        }
        else if (character == 0x1E60) {
            sb.append((char) 0x0053);
            sb.append((char) 0x0307);
        }
        else if (character == 0x1E61) {
            sb.append((char) 0x0073);
            sb.append((char) 0x0307);
        }
        else if (character == 0x1E62) {
            sb.append((char) 0x0053);
            sb.append((char) 0x0323);
        }
        else if (character == 0x1E63) {
            sb.append((char) 0x0073);
            sb.append((char) 0x0323);
        }
        else if (character == 0x1E64) {
            sb.append((char) 0x015A);
            sb.append((char) 0x0307);
        }
        else if (character == 0x1E65) {
            sb.append((char) 0x015B);
            sb.append((char) 0x0307);
        }
        else if (character == 0x1E66) {
            sb.append((char) 0x0160);
            sb.append((char) 0x0307);
        }
        else if (character == 0x1E67) {
            sb.append((char) 0x0161);
            sb.append((char) 0x0307);
        }
        else if (character == 0x1E68) {
            sb.append((char) 0x1E62);
            sb.append((char) 0x0307);
        }
        else if (character == 0x1E69) {
            sb.append((char) 0x1E63);
            sb.append((char) 0x0307);
        }
        else if (character == 0x1E6A) {
            sb.append((char) 0x0054);
            sb.append((char) 0x0307);
        }
        else if (character == 0x1E6B) {
            sb.append((char) 0x0074);
            sb.append((char) 0x0307);
        }
        else if (character == 0x1E6C) {
            sb.append((char) 0x0054);
            sb.append((char) 0x0323);
        }
        else if (character == 0x1E6D) {
            sb.append((char) 0x0074);
            sb.append((char) 0x0323);
        }
        else if (character == 0x1E6E) {
            sb.append((char) 0x0054);
            sb.append((char) 0x0331);
        }
        else if (character == 0x1E6F) {
            sb.append((char) 0x0074);
            sb.append((char) 0x0331);
        }
        else if (character == 0x1E70) {
            sb.append((char) 0x0054);
            sb.append((char) 0x032D);
        }
        else if (character == 0x1E71) {
            sb.append((char) 0x0074);
            sb.append((char) 0x032D);
        }
        else if (character == 0x1E72) {
            sb.append((char) 0x0055);
            sb.append((char) 0x0324);
        }
        else if (character == 0x1E73) {
            sb.append((char) 0x0075);
            sb.append((char) 0x0324);
        }
        else if (character == 0x1E74) {
            sb.append((char) 0x0055);
            sb.append((char) 0x0330);
        }
        else if (character == 0x1E75) {
            sb.append((char) 0x0075);
            sb.append((char) 0x0330);
        }
        else if (character == 0x1E76) {
            sb.append((char) 0x0055);
            sb.append((char) 0x032D);
        }
        else if (character == 0x1E77) {
            sb.append((char) 0x0075);
            sb.append((char) 0x032D);
        }
        else if (character == 0x1E78) {
            sb.append((char) 0x0168);
            sb.append((char) 0x0301);
        }
        else if (character == 0x1E79) {
            sb.append((char) 0x0169);
            sb.append((char) 0x0301);
        }
        else if (character == 0x1E7A) {
            sb.append((char) 0x016A);
            sb.append((char) 0x0308);
        }
        else if (character == 0x1E7B) {
            sb.append((char) 0x016B);
            sb.append((char) 0x0308);
        }
        else if (character == 0x1E7C) {
            sb.append((char) 0x0056);
            sb.append((char) 0x0303);
        }
        else if (character == 0x1E7D) {
            sb.append((char) 0x0076);
            sb.append((char) 0x0303);
        }
        else if (character == 0x1E7E) {
            sb.append((char) 0x0056);
            sb.append((char) 0x0323);
        }
        else if (character == 0x1E7F) {
            sb.append((char) 0x0076);
            sb.append((char) 0x0323);
        }
        else if (character == 0x1E80) {
            sb.append((char) 0x0057);
            sb.append((char) 0x0300);
        }
        else if (character == 0x1E81) {
            sb.append((char) 0x0077);
            sb.append((char) 0x0300);
        }
        else if (character == 0x1E82) {
            sb.append((char) 0x0057);
            sb.append((char) 0x0301);
        }
        else if (character == 0x1E83) {
            sb.append((char) 0x0077);
            sb.append((char) 0x0301);
        }
        else if (character == 0x1E84) {
            sb.append((char) 0x0057);
            sb.append((char) 0x0308);
        }
        else if (character == 0x1E85) {
            sb.append((char) 0x0077);
            sb.append((char) 0x0308);
        }
        else if (character == 0x1E86) {
            sb.append((char) 0x0057);
            sb.append((char) 0x0307);
        }
        else if (character == 0x1E87) {
            sb.append((char) 0x0077);
            sb.append((char) 0x0307);
        }
        else if (character == 0x1E88) {
            sb.append((char) 0x0057);
            sb.append((char) 0x0323);
        }
        else if (character == 0x1E89) {
            sb.append((char) 0x0077);
            sb.append((char) 0x0323);
        }
        else if (character == 0x1E8A) {
            sb.append((char) 0x0058);
            sb.append((char) 0x0307);
        }
        else if (character == 0x1E8B) {
            sb.append((char) 0x0078);
            sb.append((char) 0x0307);
        }
        else if (character == 0x1E8C) {
            sb.append((char) 0x0058);
            sb.append((char) 0x0308);
        }
        else if (character == 0x1E8D) {
            sb.append((char) 0x0078);
            sb.append((char) 0x0308);
        }
        else if (character == 0x1E8E) {
            sb.append((char) 0x0059);
            sb.append((char) 0x0307);
        }
        else if (character == 0x1E8F) {
            sb.append((char) 0x0079);
            sb.append((char) 0x0307);
        }
        else if (character == 0x1E90) {
            sb.append((char) 0x005A);
            sb.append((char) 0x0302);
        }
        else if (character == 0x1E91) {
            sb.append((char) 0x007A);
            sb.append((char) 0x0302);
        }
        else if (character == 0x1E92) {
            sb.append((char) 0x005A);
            sb.append((char) 0x0323);
        }
        else if (character == 0x1E93) {
            sb.append((char) 0x007A);
            sb.append((char) 0x0323);
        }
        else if (character == 0x1E94) {
            sb.append((char) 0x005A);
            sb.append((char) 0x0331);
        }
        else if (character == 0x1E95) {
            sb.append((char) 0x007A);
            sb.append((char) 0x0331);
        }
        else if (character == 0x1E96) {
            sb.append((char) 0x0068);
            sb.append((char) 0x0331);
        }
        else if (character == 0x1E97) {
            sb.append((char) 0x0074);
            sb.append((char) 0x0308);
        }
        else if (character == 0x1E98) {
            sb.append((char) 0x0077);
            sb.append((char) 0x030A);
        }
        else if (character == 0x1E99) {
            sb.append((char) 0x0079);
            sb.append((char) 0x030A);
        }
        else if (character == 0x1E9B) {
            sb.append((char) 0x017F);
            sb.append((char) 0x0307);
        }
        else if (character == 0x1EA0) {
            sb.append((char) 0x0041);
            sb.append((char) 0x0323);
        }
        else if (character == 0x1EA1) {
            sb.append((char) 0x0061);
            sb.append((char) 0x0323);
        }
        else if (character == 0x1EA2) {
            sb.append((char) 0x0041);
            sb.append((char) 0x0309);
        }
        else if (character == 0x1EA3) {
            sb.append((char) 0x0061);
            sb.append((char) 0x0309);
        }
        else if (character == 0x1EA4) {
            sb.append((char) 0x00C2);
            sb.append((char) 0x0301);
        }
        else if (character == 0x1EA5) {
            sb.append((char) 0x00E2);
            sb.append((char) 0x0301);
        }
        else if (character == 0x1EA6) {
            sb.append((char) 0x00C2);
            sb.append((char) 0x0300);
        }
        else if (character == 0x1EA7) {
            sb.append((char) 0x00E2);
            sb.append((char) 0x0300);
        }
        else if (character == 0x1EA8) {
            sb.append((char) 0x00C2);
            sb.append((char) 0x0309);
        }
        else if (character == 0x1EA9) {
            sb.append((char) 0x00E2);
            sb.append((char) 0x0309);
        }
        else if (character == 0x1EAA) {
            sb.append((char) 0x00C2);
            sb.append((char) 0x0303);
        }
        else if (character == 0x1EAB) {
            sb.append((char) 0x00E2);
            sb.append((char) 0x0303);
        }
        else if (character == 0x1EAC) {
            sb.append((char) 0x1EA0);
            sb.append((char) 0x0302);
        }
        else if (character == 0x1EAD) {
            sb.append((char) 0x1EA1);
            sb.append((char) 0x0302);
        }
        else if (character == 0x1EAE) {
            sb.append((char) 0x0102);
            sb.append((char) 0x0301);
        }
        else if (character == 0x1EAF) {
            sb.append((char) 0x0103);
            sb.append((char) 0x0301);
        }
        else if (character == 0x1EB0) {
            sb.append((char) 0x0102);
            sb.append((char) 0x0300);
        }
        else if (character == 0x1EB1) {
            sb.append((char) 0x0103);
            sb.append((char) 0x0300);
        }
        else if (character == 0x1EB2) {
            sb.append((char) 0x0102);
            sb.append((char) 0x0309);
        }
        else if (character == 0x1EB3) {
            sb.append((char) 0x0103);
            sb.append((char) 0x0309);
        }
        else if (character == 0x1EB4) {
            sb.append((char) 0x0102);
            sb.append((char) 0x0303);
        }
        else if (character == 0x1EB5) {
            sb.append((char) 0x0103);
            sb.append((char) 0x0303);
        }
        else if (character == 0x1EB6) {
            sb.append((char) 0x1EA0);
            sb.append((char) 0x0306);
        }
        else if (character == 0x1EB7) {
            sb.append((char) 0x1EA1);
            sb.append((char) 0x0306);
        }
        else if (character == 0x1EB8) {
            sb.append((char) 0x0045);
            sb.append((char) 0x0323);
        }
        else if (character == 0x1EB9) {
            sb.append((char) 0x0065);
            sb.append((char) 0x0323);
        }
        else if (character == 0x1EBA) {
            sb.append((char) 0x0045);
            sb.append((char) 0x0309);
        }
        else if (character == 0x1EBB) {
            sb.append((char) 0x0065);
            sb.append((char) 0x0309);
        }
        else if (character == 0x1EBC) {
            sb.append((char) 0x0045);
            sb.append((char) 0x0303);
        }
        else if (character == 0x1EBD) {
            sb.append((char) 0x0065);
            sb.append((char) 0x0303);
        }
        else if (character == 0x1EBE) {
            sb.append((char) 0x00CA);
            sb.append((char) 0x0301);
        }
        else if (character == 0x1EBF) {
            sb.append((char) 0x00EA);
            sb.append((char) 0x0301);
        }
        else if (character == 0x1EC0) {
            sb.append((char) 0x00CA);
            sb.append((char) 0x0300);
        }
        else if (character == 0x1EC1) {
            sb.append((char) 0x00EA);
            sb.append((char) 0x0300);
        }
        else if (character == 0x1EC2) {
            sb.append((char) 0x00CA);
            sb.append((char) 0x0309);
        }
        else if (character == 0x1EC3) {
            sb.append((char) 0x00EA);
            sb.append((char) 0x0309);
        }
        else if (character == 0x1EC4) {
            sb.append((char) 0x00CA);
            sb.append((char) 0x0303);
        }
        else if (character == 0x1EC5) {
            sb.append((char) 0x00EA);
            sb.append((char) 0x0303);
        }
        else if (character == 0x1EC6) {
            sb.append((char) 0x1EB8);
            sb.append((char) 0x0302);
        }
        else if (character == 0x1EC7) {
            sb.append((char) 0x1EB9);
            sb.append((char) 0x0302);
        }
        else if (character == 0x1EC8) {
            sb.append((char) 0x0049);
            sb.append((char) 0x0309);
        }
        else if (character == 0x1EC9) {
            sb.append((char) 0x0069);
            sb.append((char) 0x0309);
        }
        else if (character == 0x1ECA) {
            sb.append((char) 0x0049);
            sb.append((char) 0x0323);
        }
        else if (character == 0x1ECB) {
            sb.append((char) 0x0069);
            sb.append((char) 0x0323);
        }
        else if (character == 0x1ECC) {
            sb.append((char) 0x004F);
            sb.append((char) 0x0323);
        }
        else if (character == 0x1ECD) {
            sb.append((char) 0x006F);
            sb.append((char) 0x0323);
        }
        else if (character == 0x1ECE) {
            sb.append((char) 0x004F);
            sb.append((char) 0x0309);
        }
        else if (character == 0x1ECF) {
            sb.append((char) 0x006F);
            sb.append((char) 0x0309);
        }
        else if (character == 0x1ED0) {
            sb.append((char) 0x00D4);
            sb.append((char) 0x0301);
        }
        else if (character == 0x1ED1) {
            sb.append((char) 0x00F4);
            sb.append((char) 0x0301);
        }
        else if (character == 0x1ED2) {
            sb.append((char) 0x00D4);
            sb.append((char) 0x0300);
        }
        else if (character == 0x1ED3) {
            sb.append((char) 0x00F4);
            sb.append((char) 0x0300);
        }
        else if (character == 0x1ED4) {
            sb.append((char) 0x00D4);
            sb.append((char) 0x0309);
        }
        else if (character == 0x1ED5) {
            sb.append((char) 0x00F4);
            sb.append((char) 0x0309);
        }
        else if (character == 0x1ED6) {
            sb.append((char) 0x00D4);
            sb.append((char) 0x0303);
        }
        else if (character == 0x1ED7) {
            sb.append((char) 0x00F4);
            sb.append((char) 0x0303);
        }
        else if (character == 0x1ED8) {
            sb.append((char) 0x1ECC);
            sb.append((char) 0x0302);
        }
        else if (character == 0x1ED9) {
            sb.append((char) 0x1ECD);
            sb.append((char) 0x0302);
        }
        else if (character == 0x1EDA) {
            sb.append((char) 0x01A0);
            sb.append((char) 0x0301);
        }
        else if (character == 0x1EDB) {
            sb.append((char) 0x01A1);
            sb.append((char) 0x0301);
        }
        else if (character == 0x1EDC) {
            sb.append((char) 0x01A0);
            sb.append((char) 0x0300);
        }
        else if (character == 0x1EDD) {
            sb.append((char) 0x01A1);
            sb.append((char) 0x0300);
        }
        else if (character == 0x1EDE) {
            sb.append((char) 0x01A0);
            sb.append((char) 0x0309);
        }
        else if (character == 0x1EDF) {
            sb.append((char) 0x01A1);
            sb.append((char) 0x0309);
        }
        else if (character == 0x1EE0) {
            sb.append((char) 0x01A0);
            sb.append((char) 0x0303);
        }
        else if (character == 0x1EE1) {
            sb.append((char) 0x01A1);
            sb.append((char) 0x0303);
        }
        else if (character == 0x1EE2) {
            sb.append((char) 0x01A0);
            sb.append((char) 0x0323);
        }
        else if (character == 0x1EE3) {
            sb.append((char) 0x01A1);
            sb.append((char) 0x0323);
        }
        else if (character == 0x1EE4) {
            sb.append((char) 0x0055);
            sb.append((char) 0x0323);
        }
        else if (character == 0x1EE5) {
            sb.append((char) 0x0075);
            sb.append((char) 0x0323);
        }
        else if (character == 0x1EE6) {
            sb.append((char) 0x0055);
            sb.append((char) 0x0309);
        }
        else if (character == 0x1EE7) {
            sb.append((char) 0x0075);
            sb.append((char) 0x0309);
        }
        else if (character == 0x1EE8) {
            sb.append((char) 0x01AF);
            sb.append((char) 0x0301);
        }
        else if (character == 0x1EE9) {
            sb.append((char) 0x01B0);
            sb.append((char) 0x0301);
        }
        else if (character == 0x1EEA) {
            sb.append((char) 0x01AF);
            sb.append((char) 0x0300);
        }
        else if (character == 0x1EEB) {
            sb.append((char) 0x01B0);
            sb.append((char) 0x0300);
        }
        else if (character == 0x1EEC) {
            sb.append((char) 0x01AF);
            sb.append((char) 0x0309);
        }
        else if (character == 0x1EED) {
            sb.append((char) 0x01B0);
            sb.append((char) 0x0309);
        }
        else if (character == 0x1EEE) {
            sb.append((char) 0x01AF);
            sb.append((char) 0x0303);
        }
        else if (character == 0x1EEF) {
            sb.append((char) 0x01B0);
            sb.append((char) 0x0303);
        }
        else if (character == 0x1EF0) {
            sb.append((char) 0x01AF);
            sb.append((char) 0x0323);
        }
        else if (character == 0x1EF1) {
            sb.append((char) 0x01B0);
            sb.append((char) 0x0323);
        }
        else if (character == 0x1EF2) {
            sb.append((char) 0x0059);
            sb.append((char) 0x0300);
        }
        else if (character == 0x1EF3) {
            sb.append((char) 0x0079);
            sb.append((char) 0x0300);
        }
        else if (character == 0x1EF4) {
            sb.append((char) 0x0059);
            sb.append((char) 0x0323);
        }
        else if (character == 0x1EF5) {
            sb.append((char) 0x0079);
            sb.append((char) 0x0323);
        }
        else if (character == 0x1EF6) {
            sb.append((char) 0x0059);
            sb.append((char) 0x0309);
        }
        else if (character == 0x1EF7) {
            sb.append((char) 0x0079);
            sb.append((char) 0x0309);
        }
        else if (character == 0x1EF8) {
            sb.append((char) 0x0059);
            sb.append((char) 0x0303);
        }
        else if (character == 0x1EF9) {
            sb.append((char) 0x0079);
            sb.append((char) 0x0303);
        }
        else if (character == 0x1F00) {
            sb.append((char) 0x03B1);
            sb.append((char) 0x0313);
        }
        else if (character == 0x1F01) {
            sb.append((char) 0x03B1);
            sb.append((char) 0x0314);
        }
        else if (character == 0x1F02) {
            sb.append((char) 0x1F00);
            sb.append((char) 0x0300);
        }
        else if (character == 0x1F03) {
            sb.append((char) 0x1F01);
            sb.append((char) 0x0300);
        }
        else if (character == 0x1F04) {
            sb.append((char) 0x1F00);
            sb.append((char) 0x0301);
        }
        else if (character == 0x1F05) {
            sb.append((char) 0x1F01);
            sb.append((char) 0x0301);
        }
        else if (character == 0x1F06) {
            sb.append((char) 0x1F00);
            sb.append((char) 0x0342);
        }
        else if (character == 0x1F07) {
            sb.append((char) 0x1F01);
            sb.append((char) 0x0342);
        }
        else if (character == 0x1F08) {
            sb.append((char) 0x0391);
            sb.append((char) 0x0313);
        }
        else if (character == 0x1F09) {
            sb.append((char) 0x0391);
            sb.append((char) 0x0314);
        }
        else if (character == 0x1F0A) {
            sb.append((char) 0x1F08);
            sb.append((char) 0x0300);
        }
        else if (character == 0x1F0B) {
            sb.append((char) 0x1F09);
            sb.append((char) 0x0300);
        }
        else if (character == 0x1F0C) {
            sb.append((char) 0x1F08);
            sb.append((char) 0x0301);
        }
        else if (character == 0x1F0D) {
            sb.append((char) 0x1F09);
            sb.append((char) 0x0301);
        }
        else if (character == 0x1F0E) {
            sb.append((char) 0x1F08);
            sb.append((char) 0x0342);
        }
        else if (character == 0x1F0F) {
            sb.append((char) 0x1F09);
            sb.append((char) 0x0342);
        }
        else if (character == 0x1F10) {
            sb.append((char) 0x03B5);
            sb.append((char) 0x0313);
        }
        else if (character == 0x1F11) {
            sb.append((char) 0x03B5);
            sb.append((char) 0x0314);
        }
        else if (character == 0x1F12) {
            sb.append((char) 0x1F10);
            sb.append((char) 0x0300);
        }
        else if (character == 0x1F13) {
            sb.append((char) 0x1F11);
            sb.append((char) 0x0300);
        }
        else if (character == 0x1F14) {
            sb.append((char) 0x1F10);
            sb.append((char) 0x0301);
        }
        else if (character == 0x1F15) {
            sb.append((char) 0x1F11);
            sb.append((char) 0x0301);
        }
        else if (character == 0x1F18) {
            sb.append((char) 0x0395);
            sb.append((char) 0x0313);
        }
        else if (character == 0x1F19) {
            sb.append((char) 0x0395);
            sb.append((char) 0x0314);
        }
        else if (character == 0x1F1A) {
            sb.append((char) 0x1F18);
            sb.append((char) 0x0300);
        }
        else if (character == 0x1F1B) {
            sb.append((char) 0x1F19);
            sb.append((char) 0x0300);
        }
        else if (character == 0x1F1C) {
            sb.append((char) 0x1F18);
            sb.append((char) 0x0301);
        }
        else if (character == 0x1F1D) {
            sb.append((char) 0x1F19);
            sb.append((char) 0x0301);
        }
        else if (character == 0x1F20) {
            sb.append((char) 0x03B7);
            sb.append((char) 0x0313);
        }
        else if (character == 0x1F21) {
            sb.append((char) 0x03B7);
            sb.append((char) 0x0314);
        }
        else if (character == 0x1F22) {
            sb.append((char) 0x1F20);
            sb.append((char) 0x0300);
        }
        else if (character == 0x1F23) {
            sb.append((char) 0x1F21);
            sb.append((char) 0x0300);
        }
        else if (character == 0x1F24) {
            sb.append((char) 0x1F20);
            sb.append((char) 0x0301);
        }
        else if (character == 0x1F25) {
            sb.append((char) 0x1F21);
            sb.append((char) 0x0301);
        }
        else if (character == 0x1F26) {
            sb.append((char) 0x1F20);
            sb.append((char) 0x0342);
        }
        else if (character == 0x1F27) {
            sb.append((char) 0x1F21);
            sb.append((char) 0x0342);
        }
        else if (character == 0x1F28) {
            sb.append((char) 0x0397);
            sb.append((char) 0x0313);
        }
        else if (character == 0x1F29) {
            sb.append((char) 0x0397);
            sb.append((char) 0x0314);
        }
        else if (character == 0x1F2A) {
            sb.append((char) 0x1F28);
            sb.append((char) 0x0300);
        }
        else if (character == 0x1F2B) {
            sb.append((char) 0x1F29);
            sb.append((char) 0x0300);
        }
        else if (character == 0x1F2C) {
            sb.append((char) 0x1F28);
            sb.append((char) 0x0301);
        }
        else if (character == 0x1F2D) {
            sb.append((char) 0x1F29);
            sb.append((char) 0x0301);
        }
        else if (character == 0x1F2E) {
            sb.append((char) 0x1F28);
            sb.append((char) 0x0342);
        }
        else if (character == 0x1F2F) {
            sb.append((char) 0x1F29);
            sb.append((char) 0x0342);
        }
        else if (character == 0x1F30) {
            sb.append((char) 0x03B9);
            sb.append((char) 0x0313);
        }
        else if (character == 0x1F31) {
            sb.append((char) 0x03B9);
            sb.append((char) 0x0314);
        }
        else if (character == 0x1F32) {
            sb.append((char) 0x1F30);
            sb.append((char) 0x0300);
        }
        else if (character == 0x1F33) {
            sb.append((char) 0x1F31);
            sb.append((char) 0x0300);
        }
        else if (character == 0x1F34) {
            sb.append((char) 0x1F30);
            sb.append((char) 0x0301);
        }
        else if (character == 0x1F35) {
            sb.append((char) 0x1F31);
            sb.append((char) 0x0301);
        }
        else if (character == 0x1F36) {
            sb.append((char) 0x1F30);
            sb.append((char) 0x0342);
        }
        else if (character == 0x1F37) {
            sb.append((char) 0x1F31);
            sb.append((char) 0x0342);
        }
        else if (character == 0x1F38) {
            sb.append((char) 0x0399);
            sb.append((char) 0x0313);
        }
        else if (character == 0x1F39) {
            sb.append((char) 0x0399);
            sb.append((char) 0x0314);
        }
        else if (character == 0x1F3A) {
            sb.append((char) 0x1F38);
            sb.append((char) 0x0300);
        }
        else if (character == 0x1F3B) {
            sb.append((char) 0x1F39);
            sb.append((char) 0x0300);
        }
        else if (character == 0x1F3C) {
            sb.append((char) 0x1F38);
            sb.append((char) 0x0301);
        }
        else if (character == 0x1F3D) {
            sb.append((char) 0x1F39);
            sb.append((char) 0x0301);
        }
        else if (character == 0x1F3E) {
            sb.append((char) 0x1F38);
            sb.append((char) 0x0342);
        }
        else if (character == 0x1F3F) {
            sb.append((char) 0x1F39);
            sb.append((char) 0x0342);
        }
        else if (character == 0x1F40) {
            sb.append((char) 0x03BF);
            sb.append((char) 0x0313);
        }
        else if (character == 0x1F41) {
            sb.append((char) 0x03BF);
            sb.append((char) 0x0314);
        }
        else if (character == 0x1F42) {
            sb.append((char) 0x1F40);
            sb.append((char) 0x0300);
        }
        else if (character == 0x1F43) {
            sb.append((char) 0x1F41);
            sb.append((char) 0x0300);
        }
        else if (character == 0x1F44) {
            sb.append((char) 0x1F40);
            sb.append((char) 0x0301);
        }
        else if (character == 0x1F45) {
            sb.append((char) 0x1F41);
            sb.append((char) 0x0301);
        }
        else if (character == 0x1F48) {
            sb.append((char) 0x039F);
            sb.append((char) 0x0313);
        }
        else if (character == 0x1F49) {
            sb.append((char) 0x039F);
            sb.append((char) 0x0314);
        }
        else if (character == 0x1F4A) {
            sb.append((char) 0x1F48);
            sb.append((char) 0x0300);
        }
        else if (character == 0x1F4B) {
            sb.append((char) 0x1F49);
            sb.append((char) 0x0300);
        }
        else if (character == 0x1F4C) {
            sb.append((char) 0x1F48);
            sb.append((char) 0x0301);
        }
        else if (character == 0x1F4D) {
            sb.append((char) 0x1F49);
            sb.append((char) 0x0301);
        }
        else if (character == 0x1F50) {
            sb.append((char) 0x03C5);
            sb.append((char) 0x0313);
        }
        else if (character == 0x1F51) {
            sb.append((char) 0x03C5);
            sb.append((char) 0x0314);
        }
        else if (character == 0x1F52) {
            sb.append((char) 0x1F50);
            sb.append((char) 0x0300);
        }
        else if (character == 0x1F53) {
            sb.append((char) 0x1F51);
            sb.append((char) 0x0300);
        }
        else if (character == 0x1F54) {
            sb.append((char) 0x1F50);
            sb.append((char) 0x0301);
        }
        else if (character == 0x1F55) {
            sb.append((char) 0x1F51);
            sb.append((char) 0x0301);
        }
        else if (character == 0x1F56) {
            sb.append((char) 0x1F50);
            sb.append((char) 0x0342);
        }
        else if (character == 0x1F57) {
            sb.append((char) 0x1F51);
            sb.append((char) 0x0342);
        }
        else if (character == 0x1F59) {
            sb.append((char) 0x03A5);
            sb.append((char) 0x0314);
        }
        else if (character == 0x1F5B) {
            sb.append((char) 0x1F59);
            sb.append((char) 0x0300);
        }
        else if (character == 0x1F5D) {
            sb.append((char) 0x1F59);
            sb.append((char) 0x0301);
        }
        else if (character == 0x1F5F) {
            sb.append((char) 0x1F59);
            sb.append((char) 0x0342);
        }
        else if (character == 0x1F60) {
            sb.append((char) 0x03C9);
            sb.append((char) 0x0313);
        }
        else if (character == 0x1F61) {
            sb.append((char) 0x03C9);
            sb.append((char) 0x0314);
        }
        else if (character == 0x1F62) {
            sb.append((char) 0x1F60);
            sb.append((char) 0x0300);
        }
        else if (character == 0x1F63) {
            sb.append((char) 0x1F61);
            sb.append((char) 0x0300);
        }
        else if (character == 0x1F64) {
            sb.append((char) 0x1F60);
            sb.append((char) 0x0301);
        }
        else if (character == 0x1F65) {
            sb.append((char) 0x1F61);
            sb.append((char) 0x0301);
        }
        else if (character == 0x1F66) {
            sb.append((char) 0x1F60);
            sb.append((char) 0x0342);
        }
        else if (character == 0x1F67) {
            sb.append((char) 0x1F61);
            sb.append((char) 0x0342);
        }
        else if (character == 0x1F68) {
            sb.append((char) 0x03A9);
            sb.append((char) 0x0313);
        }
        else if (character == 0x1F69) {
            sb.append((char) 0x03A9);
            sb.append((char) 0x0314);
        }
        else if (character == 0x1F6A) {
            sb.append((char) 0x1F68);
            sb.append((char) 0x0300);
        }
        else if (character == 0x1F6B) {
            sb.append((char) 0x1F69);
            sb.append((char) 0x0300);
        }
        else if (character == 0x1F6C) {
            sb.append((char) 0x1F68);
            sb.append((char) 0x0301);
        }
        else if (character == 0x1F6D) {
            sb.append((char) 0x1F69);
            sb.append((char) 0x0301);
        }
        else if (character == 0x1F6E) {
            sb.append((char) 0x1F68);
            sb.append((char) 0x0342);
        }
        else if (character == 0x1F6F) {
            sb.append((char) 0x1F69);
            sb.append((char) 0x0342);
        }
        else if (character == 0x1F70) {
            sb.append((char) 0x03B1);
            sb.append((char) 0x0300);
        }
        else if (character == 0x1F71) {
            sb.append((char) 0x03AC);
        }
        else if (character == 0x1F72) {
            sb.append((char) 0x03B5);
            sb.append((char) 0x0300);
        }
        else if (character == 0x1F73) {
            sb.append((char) 0x03AD);
        }
        else if (character == 0x1F74) {
            sb.append((char) 0x03B7);
            sb.append((char) 0x0300);
        }
        else if (character == 0x1F75) {
            sb.append((char) 0x03AE);
        }
        else if (character == 0x1F76) {
            sb.append((char) 0x03B9);
            sb.append((char) 0x0300);
        }
        else if (character == 0x1F77) {
            sb.append((char) 0x03AF);
        }
        else if (character == 0x1F78) {
            sb.append((char) 0x03BF);
            sb.append((char) 0x0300);
        }
        else if (character == 0x1F79) {
            sb.append((char) 0x03CC);
        }
        else if (character == 0x1F7A) {
            sb.append((char) 0x03C5);
            sb.append((char) 0x0300);
        }
        else if (character == 0x1F7B) {
            sb.append((char) 0x03CD);
        }
        else if (character == 0x1F7C) {
            sb.append((char) 0x03C9);
            sb.append((char) 0x0300);
        }
        else if (character == 0x1F7D) {
            sb.append((char) 0x03CE);
        }
        else if (character == 0x1F80) {
            sb.append((char) 0x1F00);
            sb.append((char) 0x0345);
        }
        else if (character == 0x1F81) {
            sb.append((char) 0x1F01);
            sb.append((char) 0x0345);
        }
        else if (character == 0x1F82) {
            sb.append((char) 0x1F02);
            sb.append((char) 0x0345);
        }
        else if (character == 0x1F83) {
            sb.append((char) 0x1F03);
            sb.append((char) 0x0345);
        }
        else if (character == 0x1F84) {
            sb.append((char) 0x1F04);
            sb.append((char) 0x0345);
        }
        else if (character == 0x1F85) {
            sb.append((char) 0x1F05);
            sb.append((char) 0x0345);
        }
        else if (character == 0x1F86) {
            sb.append((char) 0x1F06);
            sb.append((char) 0x0345);
        }
        else if (character == 0x1F87) {
            sb.append((char) 0x1F07);
            sb.append((char) 0x0345);
        }
        else if (character == 0x1F88) {
            sb.append((char) 0x1F08);
            sb.append((char) 0x0345);
        }
        else if (character == 0x1F89) {
            sb.append((char) 0x1F09);
            sb.append((char) 0x0345);
        }
        else if (character == 0x1F8A) {
            sb.append((char) 0x1F0A);
            sb.append((char) 0x0345);
        }
        else if (character == 0x1F8B) {
            sb.append((char) 0x1F0B);
            sb.append((char) 0x0345);
        }
        else if (character == 0x1F8C) {
            sb.append((char) 0x1F0C);
            sb.append((char) 0x0345);
        }
        else if (character == 0x1F8D) {
            sb.append((char) 0x1F0D);
            sb.append((char) 0x0345);
        }
        else if (character == 0x1F8E) {
            sb.append((char) 0x1F0E);
            sb.append((char) 0x0345);
        }
        else if (character == 0x1F8F) {
            sb.append((char) 0x1F0F);
            sb.append((char) 0x0345);
        }
        else if (character == 0x1F90) {
            sb.append((char) 0x1F20);
            sb.append((char) 0x0345);
        }
        else if (character == 0x1F91) {
            sb.append((char) 0x1F21);
            sb.append((char) 0x0345);
        }
        else if (character == 0x1F92) {
            sb.append((char) 0x1F22);
            sb.append((char) 0x0345);
        }
        else if (character == 0x1F93) {
            sb.append((char) 0x1F23);
            sb.append((char) 0x0345);
        }
        else if (character == 0x1F94) {
            sb.append((char) 0x1F24);
            sb.append((char) 0x0345);
        }
        else if (character == 0x1F95) {
            sb.append((char) 0x1F25);
            sb.append((char) 0x0345);
        }
        else if (character == 0x1F96) {
            sb.append((char) 0x1F26);
            sb.append((char) 0x0345);
        }
        else if (character == 0x1F97) {
            sb.append((char) 0x1F27);
            sb.append((char) 0x0345);
        }
        else if (character == 0x1F98) {
            sb.append((char) 0x1F28);
            sb.append((char) 0x0345);
        }
        else if (character == 0x1F99) {
            sb.append((char) 0x1F29);
            sb.append((char) 0x0345);
        }
        else if (character == 0x1F9A) {
            sb.append((char) 0x1F2A);
            sb.append((char) 0x0345);
        }
        else if (character == 0x1F9B) {
            sb.append((char) 0x1F2B);
            sb.append((char) 0x0345);
        }
        else if (character == 0x1F9C) {
            sb.append((char) 0x1F2C);
            sb.append((char) 0x0345);
        }
        else if (character == 0x1F9D) {
            sb.append((char) 0x1F2D);
            sb.append((char) 0x0345);
        }
        else if (character == 0x1F9E) {
            sb.append((char) 0x1F2E);
            sb.append((char) 0x0345);
        }
        else if (character == 0x1F9F) {
            sb.append((char) 0x1F2F);
            sb.append((char) 0x0345);
        }
        else if (character == 0x1FA0) {
            sb.append((char) 0x1F60);
            sb.append((char) 0x0345);
        }
        else if (character == 0x1FA1) {
            sb.append((char) 0x1F61);
            sb.append((char) 0x0345);
        }
        else if (character == 0x1FA2) {
            sb.append((char) 0x1F62);
            sb.append((char) 0x0345);
        }
        else if (character == 0x1FA3) {
            sb.append((char) 0x1F63);
            sb.append((char) 0x0345);
        }
        else if (character == 0x1FA4) {
            sb.append((char) 0x1F64);
            sb.append((char) 0x0345);
        }
        else if (character == 0x1FA5) {
            sb.append((char) 0x1F65);
            sb.append((char) 0x0345);
        }
        else if (character == 0x1FA6) {
            sb.append((char) 0x1F66);
            sb.append((char) 0x0345);
        }
        else if (character == 0x1FA7) {
            sb.append((char) 0x1F67);
            sb.append((char) 0x0345);
        }
        else if (character == 0x1FA8) {
            sb.append((char) 0x1F68);
            sb.append((char) 0x0345);
        }
        else if (character == 0x1FA9) {
            sb.append((char) 0x1F69);
            sb.append((char) 0x0345);
        }
        else if (character == 0x1FAA) {
            sb.append((char) 0x1F6A);
            sb.append((char) 0x0345);
        }
        else if (character == 0x1FAB) {
            sb.append((char) 0x1F6B);
            sb.append((char) 0x0345);
        }
        else if (character == 0x1FAC) {
            sb.append((char) 0x1F6C);
            sb.append((char) 0x0345);
        }
        else if (character == 0x1FAD) {
            sb.append((char) 0x1F6D);
            sb.append((char) 0x0345);
        }
        else if (character == 0x1FAE) {
            sb.append((char) 0x1F6E);
            sb.append((char) 0x0345);
        }
        else if (character == 0x1FAF) {
            sb.append((char) 0x1F6F);
            sb.append((char) 0x0345);
        }
        else if (character == 0x1FB0) {
            sb.append((char) 0x03B1);
            sb.append((char) 0x0306);
        }
        else if (character == 0x1FB1) {
            sb.append((char) 0x03B1);
            sb.append((char) 0x0304);
        }
        else if (character == 0x1FB2) {
            sb.append((char) 0x1F70);
            sb.append((char) 0x0345);
        }
        else if (character == 0x1FB3) {
            sb.append((char) 0x03B1);
            sb.append((char) 0x0345);
        }
        else if (character == 0x1FB4) {
            sb.append((char) 0x03AC);
            sb.append((char) 0x0345);
        }
        else if (character == 0x1FB6) {
            sb.append((char) 0x03B1);
            sb.append((char) 0x0342);
        }
        else if (character == 0x1FB7) {
            sb.append((char) 0x1FB6);
            sb.append((char) 0x0345);
        }
        else if (character == 0x1FB8) {
            sb.append((char) 0x0391);
            sb.append((char) 0x0306);
        }
        else if (character == 0x1FB9) {
            sb.append((char) 0x0391);
            sb.append((char) 0x0304);
        }
        else if (character == 0x1FBA) {
            sb.append((char) 0x0391);
            sb.append((char) 0x0300);
        }
        else if (character == 0x1FBB) {
            sb.append((char) 0x0386);
        }
        else if (character == 0x1FBC) {
            sb.append((char) 0x0391);
            sb.append((char) 0x0345);
        }
        else if (character == 0x1FBE) {
            sb.append((char) 0x03B9);
        }
        else if (character == 0x1FC1) {
            sb.append((char) 0x00A8);
            sb.append((char) 0x0342);
        }
        else if (character == 0x1FC2) {
            sb.append((char) 0x1F74);
            sb.append((char) 0x0345);
        }
        else if (character == 0x1FC3) {
            sb.append((char) 0x03B7);
            sb.append((char) 0x0345);
        }
        else if (character == 0x1FC4) {
            sb.append((char) 0x03AE);
            sb.append((char) 0x0345);
        }
        else if (character == 0x1FC6) {
            sb.append((char) 0x03B7);
            sb.append((char) 0x0342);
        }
        else if (character == 0x1FC7) {
            sb.append((char) 0x1FC6);
            sb.append((char) 0x0345);
        }
        else if (character == 0x1FC8) {
            sb.append((char) 0x0395);
            sb.append((char) 0x0300);
        }
        else if (character == 0x1FC9) {
            sb.append((char) 0x0388);
        }
        else if (character == 0x1FCA) {
            sb.append((char) 0x0397);
            sb.append((char) 0x0300);
        }
        else if (character == 0x1FCB) {
            sb.append((char) 0x0389);
        }
        else if (character == 0x1FCC) {
            sb.append((char) 0x0397);
            sb.append((char) 0x0345);
        }
        else if (character == 0x1FCD) {
            sb.append((char) 0x1FBF);
            sb.append((char) 0x0300);
        }
        else if (character == 0x1FCE) {
            sb.append((char) 0x1FBF);
            sb.append((char) 0x0301);
        }
        else if (character == 0x1FCF) {
            sb.append((char) 0x1FBF);
            sb.append((char) 0x0342);
        }
        else if (character == 0x1FD0) {
            sb.append((char) 0x03B9);
            sb.append((char) 0x0306);
        }
        else if (character == 0x1FD1) {
            sb.append((char) 0x03B9);
            sb.append((char) 0x0304);
        }
        else if (character == 0x1FD2) {
            sb.append((char) 0x03CA);
            sb.append((char) 0x0300);
        }
        else if (character == 0x1FD3) {
            sb.append((char) 0x0390);
        }
        else if (character == 0x1FD6) {
            sb.append((char) 0x03B9);
            sb.append((char) 0x0342);
        }
        else if (character == 0x1FD7) {
            sb.append((char) 0x03CA);
            sb.append((char) 0x0342);
        }
        else if (character == 0x1FD8) {
            sb.append((char) 0x0399);
            sb.append((char) 0x0306);
        }
        else if (character == 0x1FD9) {
            sb.append((char) 0x0399);
            sb.append((char) 0x0304);
        }
        else if (character == 0x1FDA) {
            sb.append((char) 0x0399);
            sb.append((char) 0x0300);
        }
        else if (character == 0x1FDB) {
            sb.append((char) 0x038A);
        }
        else if (character == 0x1FDD) {
            sb.append((char) 0x1FFE);
            sb.append((char) 0x0300);
        }
        else if (character == 0x1FDE) {
            sb.append((char) 0x1FFE);
            sb.append((char) 0x0301);
        }
        else if (character == 0x1FDF) {
            sb.append((char) 0x1FFE);
            sb.append((char) 0x0342);
        }
        else if (character == 0x1FE0) {
            sb.append((char) 0x03C5);
            sb.append((char) 0x0306);
        }
        else if (character == 0x1FE1) {
            sb.append((char) 0x03C5);
            sb.append((char) 0x0304);
        }
        else if (character == 0x1FE2) {
            sb.append((char) 0x03CB);
            sb.append((char) 0x0300);
        }
        else if (character == 0x1FE3) {
            sb.append((char) 0x03B0);
        }
        else if (character == 0x1FE4) {
            sb.append((char) 0x03C1);
            sb.append((char) 0x0313);
        }
        else if (character == 0x1FE5) {
            sb.append((char) 0x03C1);
            sb.append((char) 0x0314);
        }
        else if (character == 0x1FE6) {
            sb.append((char) 0x03C5);
            sb.append((char) 0x0342);
        }
        else if (character == 0x1FE7) {
            sb.append((char) 0x03CB);
            sb.append((char) 0x0342);
        }
        else if (character == 0x1FE8) {
            sb.append((char) 0x03A5);
            sb.append((char) 0x0306);
        }
        else if (character == 0x1FE9) {
            sb.append((char) 0x03A5);
            sb.append((char) 0x0304);
        }
        else if (character == 0x1FEA) {
            sb.append((char) 0x03A5);
            sb.append((char) 0x0300);
        }
        else if (character == 0x1FEB) {
            sb.append((char) 0x038E);
        }
        else if (character == 0x1FEC) {
            sb.append((char) 0x03A1);
            sb.append((char) 0x0314);
        }
        else if (character == 0x1FED) {
            sb.append((char) 0x00A8);
            sb.append((char) 0x0300);
        }
        else if (character == 0x1FEE) {
            sb.append((char) 0x0385);
        }
        else if (character == 0x1FEF) {
            sb.append((char) 0x0060);
        }
        else if (character == 0x1FF2) {
            sb.append((char) 0x1F7C);
            sb.append((char) 0x0345);
        }
        else if (character == 0x1FF3) {
            sb.append((char) 0x03C9);
            sb.append((char) 0x0345);
        }
        else if (character == 0x1FF4) {
            sb.append((char) 0x03CE);
            sb.append((char) 0x0345);
        }
        else if (character == 0x1FF6) {
            sb.append((char) 0x03C9);
            sb.append((char) 0x0342);
        }
        else if (character == 0x1FF7) {
            sb.append((char) 0x1FF6);
            sb.append((char) 0x0345);
        }
        else if (character == 0x1FF8) {
            sb.append((char) 0x039F);
            sb.append((char) 0x0300);
        }
        else if (character == 0x1FF9) {
            sb.append((char) 0x038C);
        }
        else if (character == 0x1FFA) {
            sb.append((char) 0x03A9);
            sb.append((char) 0x0300);
        }
        else if (character == 0x1FFB) {
            sb.append((char) 0x038F);
        }
        else if (character == 0x1FFC) {
            sb.append((char) 0x03A9);
            sb.append((char) 0x0345);
        }
        else if (character == 0x1FFD) {
            sb.append((char) 0x00B4);
        }
        else if (character == 0x2000) {
            sb.append((char) 0x2002);
        }
        else if (character == 0x2001) {
            sb.append((char) 0x2003);
        }
        else if (character == 0x2126) {
            sb.append((char) 0x03A9);
        }
        else if (character == 0x212A) {
            sb.append((char) 0x004B);
        }
        else if (character == 0x212B) {
            sb.append((char) 0x00C5);
        }
        else if (character == 0x219A) {
            sb.append((char) 0x2190);
            sb.append((char) 0x0338);
        }
        else if (character == 0x219B) {
            sb.append((char) 0x2192);
            sb.append((char) 0x0338);
        }
        else if (character == 0x21AE) {
            sb.append((char) 0x2194);
            sb.append((char) 0x0338);
        }
        else if (character == 0x21CD) {
            sb.append((char) 0x21D0);
            sb.append((char) 0x0338);
        }
        else if (character == 0x21CE) {
            sb.append((char) 0x21D4);
            sb.append((char) 0x0338);
        }
        else if (character == 0x21CF) {
            sb.append((char) 0x21D2);
            sb.append((char) 0x0338);
        }
        else if (character == 0x2204) {
            sb.append((char) 0x2203);
            sb.append((char) 0x0338);
        }
        else if (character == 0x2209) {
            sb.append((char) 0x2208);
            sb.append((char) 0x0338);
        }
        else if (character == 0x220C) {
            sb.append((char) 0x220B);
            sb.append((char) 0x0338);
        }
        else if (character == 0x2224) {
            sb.append((char) 0x2223);
            sb.append((char) 0x0338);
        }
        else if (character == 0x2226) {
            sb.append((char) 0x2225);
            sb.append((char) 0x0338);
        }
        else if (character == 0x2241) {
            sb.append((char) 0x223C);
            sb.append((char) 0x0338);
        }
        else if (character == 0x2244) {
            sb.append((char) 0x2243);
            sb.append((char) 0x0338);
        }
        else if (character == 0x2247) {
            sb.append((char) 0x2245);
            sb.append((char) 0x0338);
        }
        else if (character == 0x2249) {
            sb.append((char) 0x2248);
            sb.append((char) 0x0338);
        }
        else if (character == 0x2260) {
            sb.append((char) 0x003D);
            sb.append((char) 0x0338);
        }
        else if (character == 0x2262) {
            sb.append((char) 0x2261);
            sb.append((char) 0x0338);
        }
        else if (character == 0x226D) {
            sb.append((char) 0x224D);
            sb.append((char) 0x0338);
        }
        else if (character == 0x226E) {
            sb.append((char) 0x003C);
            sb.append((char) 0x0338);
        }
        else if (character == 0x226F) {
            sb.append((char) 0x003E);
            sb.append((char) 0x0338);
        }
        else if (character == 0x2270) {
            sb.append((char) 0x2264);
            sb.append((char) 0x0338);
        }
        else if (character == 0x2271) {
            sb.append((char) 0x2265);
            sb.append((char) 0x0338);
        }
        else if (character == 0x2274) {
            sb.append((char) 0x2272);
            sb.append((char) 0x0338);
        }
        else if (character == 0x2275) {
            sb.append((char) 0x2273);
            sb.append((char) 0x0338);
        }
        else if (character == 0x2278) {
            sb.append((char) 0x2276);
            sb.append((char) 0x0338);
        }
        else if (character == 0x2279) {
            sb.append((char) 0x2277);
            sb.append((char) 0x0338);
        }
        else if (character == 0x2280) {
            sb.append((char) 0x227A);
            sb.append((char) 0x0338);
        }
        else if (character == 0x2281) {
            sb.append((char) 0x227B);
            sb.append((char) 0x0338);
        }
        else if (character == 0x2284) {
            sb.append((char) 0x2282);
            sb.append((char) 0x0338);
        }
        else if (character == 0x2285) {
            sb.append((char) 0x2283);
            sb.append((char) 0x0338);
        }
        else if (character == 0x2288) {
            sb.append((char) 0x2286);
            sb.append((char) 0x0338);
        }
        else if (character == 0x2289) {
            sb.append((char) 0x2287);
            sb.append((char) 0x0338);
        }
        else if (character == 0x22AC) {
            sb.append((char) 0x22A2);
            sb.append((char) 0x0338);
        }
        else if (character == 0x22AD) {
            sb.append((char) 0x22A8);
            sb.append((char) 0x0338);
        }
        else if (character == 0x22AE) {
            sb.append((char) 0x22A9);
            sb.append((char) 0x0338);
        }
        else if (character == 0x22AF) {
            sb.append((char) 0x22AB);
            sb.append((char) 0x0338);
        }
        else if (character == 0x22E0) {
            sb.append((char) 0x227C);
            sb.append((char) 0x0338);
        }
        else if (character == 0x22E1) {
            sb.append((char) 0x227D);
            sb.append((char) 0x0338);
        }
        else if (character == 0x22E2) {
            sb.append((char) 0x2291);
            sb.append((char) 0x0338);
        }
        else if (character == 0x22E3) {
            sb.append((char) 0x2292);
            sb.append((char) 0x0338);
        }
        else if (character == 0x22EA) {
            sb.append((char) 0x22B2);
            sb.append((char) 0x0338);
        }
        else if (character == 0x22EB) {
            sb.append((char) 0x22B3);
            sb.append((char) 0x0338);
        }
        else if (character == 0x22EC) {
            sb.append((char) 0x22B4);
            sb.append((char) 0x0338);
        }
        else if (character == 0x22ED) {
            sb.append((char) 0x22B5);
            sb.append((char) 0x0338);
        }
        else if (character == 0x2329) {
            sb.append((char) 0x3008);
        }
        else if (character == 0x232A) {
            sb.append((char) 0x3009);
        }
        else if (character == 0x2ADC) {
            sb.append((char) 0x2ADD);
            sb.append((char) 0x0338);
        }
        else if (character == 0x304C) {
            sb.append((char) 0x304B);
            sb.append((char) 0x3099);
        }
        else if (character == 0x304E) {
            sb.append((char) 0x304D);
            sb.append((char) 0x3099);
        }
        else if (character == 0x3050) {
            sb.append((char) 0x304F);
            sb.append((char) 0x3099);
        }
        else if (character == 0x3052) {
            sb.append((char) 0x3051);
            sb.append((char) 0x3099);
        }
        else if (character == 0x3054) {
            sb.append((char) 0x3053);
            sb.append((char) 0x3099);
        }
        else if (character == 0x3056) {
            sb.append((char) 0x3055);
            sb.append((char) 0x3099);
        }
        else if (character == 0x3058) {
            sb.append((char) 0x3057);
            sb.append((char) 0x3099);
        }
        else if (character == 0x305A) {
            sb.append((char) 0x3059);
            sb.append((char) 0x3099);
        }
        else if (character == 0x305C) {
            sb.append((char) 0x305B);
            sb.append((char) 0x3099);
        }
        else if (character == 0x305E) {
            sb.append((char) 0x305D);
            sb.append((char) 0x3099);
        }
        else if (character == 0x3060) {
            sb.append((char) 0x305F);
            sb.append((char) 0x3099);
        }
        else if (character == 0x3062) {
            sb.append((char) 0x3061);
            sb.append((char) 0x3099);
        }
        else if (character == 0x3065) {
            sb.append((char) 0x3064);
            sb.append((char) 0x3099);
        }
        else if (character == 0x3067) {
            sb.append((char) 0x3066);
            sb.append((char) 0x3099);
        }
        else if (character == 0x3069) {
            sb.append((char) 0x3068);
            sb.append((char) 0x3099);
        }
        else if (character == 0x3070) {
            sb.append((char) 0x306F);
            sb.append((char) 0x3099);
        }
        else if (character == 0x3071) {
            sb.append((char) 0x306F);
            sb.append((char) 0x309A);
        }
        else if (character == 0x3073) {
            sb.append((char) 0x3072);
            sb.append((char) 0x3099);
        }
        else if (character == 0x3074) {
            sb.append((char) 0x3072);
            sb.append((char) 0x309A);
        }
        else if (character == 0x3076) {
            sb.append((char) 0x3075);
            sb.append((char) 0x3099);
        }
        else if (character == 0x3077) {
            sb.append((char) 0x3075);
            sb.append((char) 0x309A);
        }
        else if (character == 0x3079) {
            sb.append((char) 0x3078);
            sb.append((char) 0x3099);
        }
        else if (character == 0x307A) {
            sb.append((char) 0x3078);
            sb.append((char) 0x309A);
        }
        else if (character == 0x307C) {
            sb.append((char) 0x307B);
            sb.append((char) 0x3099);
        }
        else if (character == 0x307D) {
            sb.append((char) 0x307B);
            sb.append((char) 0x309A);
        }
        else if (character == 0x3094) {
            sb.append((char) 0x3046);
            sb.append((char) 0x3099);
        }
        else if (character == 0x309E) {
            sb.append((char) 0x309D);
            sb.append((char) 0x3099);
        }
        else if (character == 0x30AC) {
            sb.append((char) 0x30AB);
            sb.append((char) 0x3099);
        }
        else if (character == 0x30AE) {
            sb.append((char) 0x30AD);
            sb.append((char) 0x3099);
        }
        else if (character == 0x30B0) {
            sb.append((char) 0x30AF);
            sb.append((char) 0x3099);
        }
        else if (character == 0x30B2) {
            sb.append((char) 0x30B1);
            sb.append((char) 0x3099);
        }
        else if (character == 0x30B4) {
            sb.append((char) 0x30B3);
            sb.append((char) 0x3099);
        }
        else if (character == 0x30B6) {
            sb.append((char) 0x30B5);
            sb.append((char) 0x3099);
        }
        else if (character == 0x30B8) {
            sb.append((char) 0x30B7);
            sb.append((char) 0x3099);
        }
        else if (character == 0x30BA) {
            sb.append((char) 0x30B9);
            sb.append((char) 0x3099);
        }
        else if (character == 0x30BC) {
            sb.append((char) 0x30BB);
            sb.append((char) 0x3099);
        }
        else if (character == 0x30BE) {
            sb.append((char) 0x30BD);
            sb.append((char) 0x3099);
        }
        else if (character == 0x30C0) {
            sb.append((char) 0x30BF);
            sb.append((char) 0x3099);
        }
        else if (character == 0x30C2) {
            sb.append((char) 0x30C1);
            sb.append((char) 0x3099);
        }
        else if (character == 0x30C5) {
            sb.append((char) 0x30C4);
            sb.append((char) 0x3099);
        }
        else if (character == 0x30C7) {
            sb.append((char) 0x30C6);
            sb.append((char) 0x3099);
        }
        else if (character == 0x30C9) {
            sb.append((char) 0x30C8);
            sb.append((char) 0x3099);
        }
        else if (character == 0x30D0) {
            sb.append((char) 0x30CF);
            sb.append((char) 0x3099);
        }
        else if (character == 0x30D1) {
            sb.append((char) 0x30CF);
            sb.append((char) 0x309A);
        }
        else if (character == 0x30D3) {
            sb.append((char) 0x30D2);
            sb.append((char) 0x3099);
        }
        else if (character == 0x30D4) {
            sb.append((char) 0x30D2);
            sb.append((char) 0x309A);
        }
        else if (character == 0x30D6) {
            sb.append((char) 0x30D5);
            sb.append((char) 0x3099);
        }
        else if (character == 0x30D7) {
            sb.append((char) 0x30D5);
            sb.append((char) 0x309A);
        }
        else if (character == 0x30D9) {
            sb.append((char) 0x30D8);
            sb.append((char) 0x3099);
        }
        else if (character == 0x30DA) {
            sb.append((char) 0x30D8);
            sb.append((char) 0x309A);
        }
        else if (character == 0x30DC) {
            sb.append((char) 0x30DB);
            sb.append((char) 0x3099);
        }
        else if (character == 0x30DD) {
            sb.append((char) 0x30DB);
            sb.append((char) 0x309A);
        }
        else if (character == 0x30F4) {
            sb.append((char) 0x30A6);
            sb.append((char) 0x3099);
        }
        else if (character == 0x30F7) {
            sb.append((char) 0x30EF);
            sb.append((char) 0x3099);
        }
        else if (character == 0x30F8) {
            sb.append((char) 0x30F0);
            sb.append((char) 0x3099);
        }
        else if (character == 0x30F9) {
            sb.append((char) 0x30F1);
            sb.append((char) 0x3099);
        }
        else if (character == 0x30FA) {
            sb.append((char) 0x30F2);
            sb.append((char) 0x3099);
        }
        else if (character == 0x30FE) {
            sb.append((char) 0x30FD);
            sb.append((char) 0x3099);
        }
        else if (character == 0xF900) {
            sb.append((char) 0x8C48);
        }
        else if (character == 0xF901) {
            sb.append((char) 0x66F4);
        }
        else if (character == 0xF902) {
            sb.append((char) 0x8ECA);
        }
        else if (character == 0xF903) {
            sb.append((char) 0x8CC8);
        }
        else if (character == 0xF904) {
            sb.append((char) 0x6ED1);
        }
        else if (character == 0xF905) {
            sb.append((char) 0x4E32);
        }
        else if (character == 0xF906) {
            sb.append((char) 0x53E5);
        }
        else if (character == 0xF907) {
            sb.append((char) 0x9F9C);
        }
        else if (character == 0xF908) {
            sb.append((char) 0x9F9C);
        }
        else if (character == 0xF909) {
            sb.append((char) 0x5951);
        }
        else if (character == 0xF90A) {
            sb.append((char) 0x91D1);
        }
        else if (character == 0xF90B) {
            sb.append((char) 0x5587);
        }
        else if (character == 0xF90C) {
            sb.append((char) 0x5948);
        }
        else if (character == 0xF90D) {
            sb.append((char) 0x61F6);
        }
        else if (character == 0xF90E) {
            sb.append((char) 0x7669);
        }
        else if (character == 0xF90F) {
            sb.append((char) 0x7F85);
        }
        else if (character == 0xF910) {
            sb.append((char) 0x863F);
        }
        else if (character == 0xF911) {
            sb.append((char) 0x87BA);
        }
        else if (character == 0xF912) {
            sb.append((char) 0x88F8);
        }
        else if (character == 0xF913) {
            sb.append((char) 0x908F);
        }
        else if (character == 0xF914) {
            sb.append((char) 0x6A02);
        }
        else if (character == 0xF915) {
            sb.append((char) 0x6D1B);
        }
        else if (character == 0xF916) {
            sb.append((char) 0x70D9);
        }
        else if (character == 0xF917) {
            sb.append((char) 0x73DE);
        }
        else if (character == 0xF918) {
            sb.append((char) 0x843D);
        }
        else if (character == 0xF919) {
            sb.append((char) 0x916A);
        }
        else if (character == 0xF91A) {
            sb.append((char) 0x99F1);
        }
        else if (character == 0xF91B) {
            sb.append((char) 0x4E82);
        }
        else if (character == 0xF91C) {
            sb.append((char) 0x5375);
        }
        else if (character == 0xF91D) {
            sb.append((char) 0x6B04);
        }
        else if (character == 0xF91E) {
            sb.append((char) 0x721B);
        }
        else if (character == 0xF91F) {
            sb.append((char) 0x862D);
        }
        else if (character == 0xF920) {
            sb.append((char) 0x9E1E);
        }
        else if (character == 0xF921) {
            sb.append((char) 0x5D50);
        }
        else if (character == 0xF922) {
            sb.append((char) 0x6FEB);
        }
        else if (character == 0xF923) {
            sb.append((char) 0x85CD);
        }
        else if (character == 0xF924) {
            sb.append((char) 0x8964);
        }
        else if (character == 0xF925) {
            sb.append((char) 0x62C9);
        }
        else if (character == 0xF926) {
            sb.append((char) 0x81D8);
        }
        else if (character == 0xF927) {
            sb.append((char) 0x881F);
        }
        else if (character == 0xF928) {
            sb.append((char) 0x5ECA);
        }
        else if (character == 0xF929) {
            sb.append((char) 0x6717);
        }
        else if (character == 0xF92A) {
            sb.append((char) 0x6D6A);
        }
        else if (character == 0xF92B) {
            sb.append((char) 0x72FC);
        }
        else if (character == 0xF92C) {
            sb.append((char) 0x90CE);
        }
        else if (character == 0xF92D) {
            sb.append((char) 0x4F86);
        }
        else if (character == 0xF92E) {
            sb.append((char) 0x51B7);
        }
        else if (character == 0xF92F) {
            sb.append((char) 0x52DE);
        }
        else if (character == 0xF930) {
            sb.append((char) 0x64C4);
        }
        else if (character == 0xF931) {
            sb.append((char) 0x6AD3);
        }
        else if (character == 0xF932) {
            sb.append((char) 0x7210);
        }
        else if (character == 0xF933) {
            sb.append((char) 0x76E7);
        }
        else if (character == 0xF934) {
            sb.append((char) 0x8001);
        }
        else if (character == 0xF935) {
            sb.append((char) 0x8606);
        }
        else if (character == 0xF936) {
            sb.append((char) 0x865C);
        }
        else if (character == 0xF937) {
            sb.append((char) 0x8DEF);
        }
        else if (character == 0xF938) {
            sb.append((char) 0x9732);
        }
        else if (character == 0xF939) {
            sb.append((char) 0x9B6F);
        }
        else if (character == 0xF93A) {
            sb.append((char) 0x9DFA);
        }
        else if (character == 0xF93B) {
            sb.append((char) 0x788C);
        }
        else if (character == 0xF93C) {
            sb.append((char) 0x797F);
        }
        else if (character == 0xF93D) {
            sb.append((char) 0x7DA0);
        }
        else if (character == 0xF93E) {
            sb.append((char) 0x83C9);
        }
        else if (character == 0xF93F) {
            sb.append((char) 0x9304);
        }
        else if (character == 0xF940) {
            sb.append((char) 0x9E7F);
        }
        else if (character == 0xF941) {
            sb.append((char) 0x8AD6);
        }
        else if (character == 0xF942) {
            sb.append((char) 0x58DF);
        }
        else if (character == 0xF943) {
            sb.append((char) 0x5F04);
        }
        else if (character == 0xF944) {
            sb.append((char) 0x7C60);
        }
        else if (character == 0xF945) {
            sb.append((char) 0x807E);
        }
        else if (character == 0xF946) {
            sb.append((char) 0x7262);
        }
        else if (character == 0xF947) {
            sb.append((char) 0x78CA);
        }
        else if (character == 0xF948) {
            sb.append((char) 0x8CC2);
        }
        else if (character == 0xF949) {
            sb.append((char) 0x96F7);
        }
        else if (character == 0xF94A) {
            sb.append((char) 0x58D8);
        }
        else if (character == 0xF94B) {
            sb.append((char) 0x5C62);
        }
        else if (character == 0xF94C) {
            sb.append((char) 0x6A13);
        }
        else if (character == 0xF94D) {
            sb.append((char) 0x6DDA);
        }
        else if (character == 0xF94E) {
            sb.append((char) 0x6F0F);
        }
        else if (character == 0xF94F) {
            sb.append((char) 0x7D2F);
        }
        else if (character == 0xF950) {
            sb.append((char) 0x7E37);
        }
        else if (character == 0xF951) {
            sb.append((char) 0x964B);
        }
        else if (character == 0xF952) {
            sb.append((char) 0x52D2);
        }
        else if (character == 0xF953) {
            sb.append((char) 0x808B);
        }
        else if (character == 0xF954) {
            sb.append((char) 0x51DC);
        }
        else if (character == 0xF955) {
            sb.append((char) 0x51CC);
        }
        else if (character == 0xF956) {
            sb.append((char) 0x7A1C);
        }
        else if (character == 0xF957) {
            sb.append((char) 0x7DBE);
        }
        else if (character == 0xF958) {
            sb.append((char) 0x83F1);
        }
        else if (character == 0xF959) {
            sb.append((char) 0x9675);
        }
        else if (character == 0xF95A) {
            sb.append((char) 0x8B80);
        }
        else if (character == 0xF95B) {
            sb.append((char) 0x62CF);
        }
        else if (character == 0xF95C) {
            sb.append((char) 0x6A02);
        }
        else if (character == 0xF95D) {
            sb.append((char) 0x8AFE);
        }
        else if (character == 0xF95E) {
            sb.append((char) 0x4E39);
        }
        else if (character == 0xF95F) {
            sb.append((char) 0x5BE7);
        }
        else if (character == 0xF960) {
            sb.append((char) 0x6012);
        }
        else if (character == 0xF961) {
            sb.append((char) 0x7387);
        }
        else if (character == 0xF962) {
            sb.append((char) 0x7570);
        }
        else if (character == 0xF963) {
            sb.append((char) 0x5317);
        }
        else if (character == 0xF964) {
            sb.append((char) 0x78FB);
        }
        else if (character == 0xF965) {
            sb.append((char) 0x4FBF);
        }
        else if (character == 0xF966) {
            sb.append((char) 0x5FA9);
        }
        else if (character == 0xF967) {
            sb.append((char) 0x4E0D);
        }
        else if (character == 0xF968) {
            sb.append((char) 0x6CCC);
        }
        else if (character == 0xF969) {
            sb.append((char) 0x6578);
        }
        else if (character == 0xF96A) {
            sb.append((char) 0x7D22);
        }
        else if (character == 0xF96B) {
            sb.append((char) 0x53C3);
        }
        else if (character == 0xF96C) {
            sb.append((char) 0x585E);
        }
        else if (character == 0xF96D) {
            sb.append((char) 0x7701);
        }
        else if (character == 0xF96E) {
            sb.append((char) 0x8449);
        }
        else if (character == 0xF96F) {
            sb.append((char) 0x8AAA);
        }
        else if (character == 0xF970) {
            sb.append((char) 0x6BBA);
        }
        else if (character == 0xF971) {
            sb.append((char) 0x8FB0);
        }
        else if (character == 0xF972) {
            sb.append((char) 0x6C88);
        }
        else if (character == 0xF973) {
            sb.append((char) 0x62FE);
        }
        else if (character == 0xF974) {
            sb.append((char) 0x82E5);
        }
        else if (character == 0xF975) {
            sb.append((char) 0x63A0);
        }
        else if (character == 0xF976) {
            sb.append((char) 0x7565);
        }
        else if (character == 0xF977) {
            sb.append((char) 0x4EAE);
        }
        else if (character == 0xF978) {
            sb.append((char) 0x5169);
        }
        else if (character == 0xF979) {
            sb.append((char) 0x51C9);
        }
        else if (character == 0xF97A) {
            sb.append((char) 0x6881);
        }
        else if (character == 0xF97B) {
            sb.append((char) 0x7CE7);
        }
        else if (character == 0xF97C) {
            sb.append((char) 0x826F);
        }
        else if (character == 0xF97D) {
            sb.append((char) 0x8AD2);
        }
        else if (character == 0xF97E) {
            sb.append((char) 0x91CF);
        }
        else if (character == 0xF97F) {
            sb.append((char) 0x52F5);
        }
        else if (character == 0xF980) {
            sb.append((char) 0x5442);
        }
        else if (character == 0xF981) {
            sb.append((char) 0x5973);
        }
        else if (character == 0xF982) {
            sb.append((char) 0x5EEC);
        }
        else if (character == 0xF983) {
            sb.append((char) 0x65C5);
        }
        else if (character == 0xF984) {
            sb.append((char) 0x6FFE);
        }
        else if (character == 0xF985) {
            sb.append((char) 0x792A);
        }
        else if (character == 0xF986) {
            sb.append((char) 0x95AD);
        }
        else if (character == 0xF987) {
            sb.append((char) 0x9A6A);
        }
        else if (character == 0xF988) {
            sb.append((char) 0x9E97);
        }
        else if (character == 0xF989) {
            sb.append((char) 0x9ECE);
        }
        else if (character == 0xF98A) {
            sb.append((char) 0x529B);
        }
        else if (character == 0xF98B) {
            sb.append((char) 0x66C6);
        }
        else if (character == 0xF98C) {
            sb.append((char) 0x6B77);
        }
        else if (character == 0xF98D) {
            sb.append((char) 0x8F62);
        }
        else if (character == 0xF98E) {
            sb.append((char) 0x5E74);
        }
        else if (character == 0xF98F) {
            sb.append((char) 0x6190);
        }
        else if (character == 0xF990) {
            sb.append((char) 0x6200);
        }
        else if (character == 0xF991) {
            sb.append((char) 0x649A);
        }
        else if (character == 0xF992) {
            sb.append((char) 0x6F23);
        }
        else if (character == 0xF993) {
            sb.append((char) 0x7149);
        }
        else if (character == 0xF994) {
            sb.append((char) 0x7489);
        }
        else if (character == 0xF995) {
            sb.append((char) 0x79CA);
        }
        else if (character == 0xF996) {
            sb.append((char) 0x7DF4);
        }
        else if (character == 0xF997) {
            sb.append((char) 0x806F);
        }
        else if (character == 0xF998) {
            sb.append((char) 0x8F26);
        }
        else if (character == 0xF999) {
            sb.append((char) 0x84EE);
        }
        else if (character == 0xF99A) {
            sb.append((char) 0x9023);
        }
        else if (character == 0xF99B) {
            sb.append((char) 0x934A);
        }
        else if (character == 0xF99C) {
            sb.append((char) 0x5217);
        }
        else if (character == 0xF99D) {
            sb.append((char) 0x52A3);
        }
        else if (character == 0xF99E) {
            sb.append((char) 0x54BD);
        }
        else if (character == 0xF99F) {
            sb.append((char) 0x70C8);
        }
        else if (character == 0xF9A0) {
            sb.append((char) 0x88C2);
        }
        else if (character == 0xF9A1) {
            sb.append((char) 0x8AAA);
        }
        else if (character == 0xF9A2) {
            sb.append((char) 0x5EC9);
        }
        else if (character == 0xF9A3) {
            sb.append((char) 0x5FF5);
        }
        else if (character == 0xF9A4) {
            sb.append((char) 0x637B);
        }
        else if (character == 0xF9A5) {
            sb.append((char) 0x6BAE);
        }
        else if (character == 0xF9A6) {
            sb.append((char) 0x7C3E);
        }
        else if (character == 0xF9A7) {
            sb.append((char) 0x7375);
        }
        else if (character == 0xF9A8) {
            sb.append((char) 0x4EE4);
        }
        else if (character == 0xF9A9) {
            sb.append((char) 0x56F9);
        }
        else if (character == 0xF9AA) {
            sb.append((char) 0x5BE7);
        }
        else if (character == 0xF9AB) {
            sb.append((char) 0x5DBA);
        }
        else if (character == 0xF9AC) {
            sb.append((char) 0x601C);
        }
        else if (character == 0xF9AD) {
            sb.append((char) 0x73B2);
        }
        else if (character == 0xF9AE) {
            sb.append((char) 0x7469);
        }
        else if (character == 0xF9AF) {
            sb.append((char) 0x7F9A);
        }
        else if (character == 0xF9B0) {
            sb.append((char) 0x8046);
        }
        else if (character == 0xF9B1) {
            sb.append((char) 0x9234);
        }
        else if (character == 0xF9B2) {
            sb.append((char) 0x96F6);
        }
        else if (character == 0xF9B3) {
            sb.append((char) 0x9748);
        }
        else if (character == 0xF9B4) {
            sb.append((char) 0x9818);
        }
        else if (character == 0xF9B5) {
            sb.append((char) 0x4F8B);
        }
        else if (character == 0xF9B6) {
            sb.append((char) 0x79AE);
        }
        else if (character == 0xF9B7) {
            sb.append((char) 0x91B4);
        }
        else if (character == 0xF9B8) {
            sb.append((char) 0x96B8);
        }
        else if (character == 0xF9B9) {
            sb.append((char) 0x60E1);
        }
        else if (character == 0xF9BA) {
            sb.append((char) 0x4E86);
        }
        else if (character == 0xF9BB) {
            sb.append((char) 0x50DA);
        }
        else if (character == 0xF9BC) {
            sb.append((char) 0x5BEE);
        }
        else if (character == 0xF9BD) {
            sb.append((char) 0x5C3F);
        }
        else if (character == 0xF9BE) {
            sb.append((char) 0x6599);
        }
        else if (character == 0xF9BF) {
            sb.append((char) 0x6A02);
        }
        else if (character == 0xF9C0) {
            sb.append((char) 0x71CE);
        }
        else if (character == 0xF9C1) {
            sb.append((char) 0x7642);
        }
        else if (character == 0xF9C2) {
            sb.append((char) 0x84FC);
        }
        else if (character == 0xF9C3) {
            sb.append((char) 0x907C);
        }
        else if (character == 0xF9C4) {
            sb.append((char) 0x9F8D);
        }
        else if (character == 0xF9C5) {
            sb.append((char) 0x6688);
        }
        else if (character == 0xF9C6) {
            sb.append((char) 0x962E);
        }
        else if (character == 0xF9C7) {
            sb.append((char) 0x5289);
        }
        else if (character == 0xF9C8) {
            sb.append((char) 0x677B);
        }
        else if (character == 0xF9C9) {
            sb.append((char) 0x67F3);
        }
        else if (character == 0xF9CA) {
            sb.append((char) 0x6D41);
        }
        else if (character == 0xF9CB) {
            sb.append((char) 0x6E9C);
        }
        else if (character == 0xF9CC) {
            sb.append((char) 0x7409);
        }
        else if (character == 0xF9CD) {
            sb.append((char) 0x7559);
        }
        else if (character == 0xF9CE) {
            sb.append((char) 0x786B);
        }
        else if (character == 0xF9CF) {
            sb.append((char) 0x7D10);
        }
        else if (character == 0xF9D0) {
            sb.append((char) 0x985E);
        }
        else if (character == 0xF9D1) {
            sb.append((char) 0x516D);
        }
        else if (character == 0xF9D2) {
            sb.append((char) 0x622E);
        }
        else if (character == 0xF9D3) {
            sb.append((char) 0x9678);
        }
        else if (character == 0xF9D4) {
            sb.append((char) 0x502B);
        }
        else if (character == 0xF9D5) {
            sb.append((char) 0x5D19);
        }
        else if (character == 0xF9D6) {
            sb.append((char) 0x6DEA);
        }
        else if (character == 0xF9D7) {
            sb.append((char) 0x8F2A);
        }
        else if (character == 0xF9D8) {
            sb.append((char) 0x5F8B);
        }
        else if (character == 0xF9D9) {
            sb.append((char) 0x6144);
        }
        else if (character == 0xF9DA) {
            sb.append((char) 0x6817);
        }
        else if (character == 0xF9DB) {
            sb.append((char) 0x7387);
        }
        else if (character == 0xF9DC) {
            sb.append((char) 0x9686);
        }
        else if (character == 0xF9DD) {
            sb.append((char) 0x5229);
        }
        else if (character == 0xF9DE) {
            sb.append((char) 0x540F);
        }
        else if (character == 0xF9DF) {
            sb.append((char) 0x5C65);
        }
        else if (character == 0xF9E0) {
            sb.append((char) 0x6613);
        }
        else if (character == 0xF9E1) {
            sb.append((char) 0x674E);
        }
        else if (character == 0xF9E2) {
            sb.append((char) 0x68A8);
        }
        else if (character == 0xF9E3) {
            sb.append((char) 0x6CE5);
        }
        else if (character == 0xF9E4) {
            sb.append((char) 0x7406);
        }
        else if (character == 0xF9E5) {
            sb.append((char) 0x75E2);
        }
        else if (character == 0xF9E6) {
            sb.append((char) 0x7F79);
        }
        else if (character == 0xF9E7) {
            sb.append((char) 0x88CF);
        }
        else if (character == 0xF9E8) {
            sb.append((char) 0x88E1);
        }
        else if (character == 0xF9E9) {
            sb.append((char) 0x91CC);
        }
        else if (character == 0xF9EA) {
            sb.append((char) 0x96E2);
        }
        else if (character == 0xF9EB) {
            sb.append((char) 0x533F);
        }
        else if (character == 0xF9EC) {
            sb.append((char) 0x6EBA);
        }
        else if (character == 0xF9ED) {
            sb.append((char) 0x541D);
        }
        else if (character == 0xF9EE) {
            sb.append((char) 0x71D0);
        }
        else if (character == 0xF9EF) {
            sb.append((char) 0x7498);
        }
        else if (character == 0xF9F0) {
            sb.append((char) 0x85FA);
        }
        else if (character == 0xF9F1) {
            sb.append((char) 0x96A3);
        }
        else if (character == 0xF9F2) {
            sb.append((char) 0x9C57);
        }
        else if (character == 0xF9F3) {
            sb.append((char) 0x9E9F);
        }
        else if (character == 0xF9F4) {
            sb.append((char) 0x6797);
        }
        else if (character == 0xF9F5) {
            sb.append((char) 0x6DCB);
        }
        else if (character == 0xF9F6) {
            sb.append((char) 0x81E8);
        }
        else if (character == 0xF9F7) {
            sb.append((char) 0x7ACB);
        }
        else if (character == 0xF9F8) {
            sb.append((char) 0x7B20);
        }
        else if (character == 0xF9F9) {
            sb.append((char) 0x7C92);
        }
        else if (character == 0xF9FA) {
            sb.append((char) 0x72C0);
        }
        else if (character == 0xF9FB) {
            sb.append((char) 0x7099);
        }
        else if (character == 0xF9FC) {
            sb.append((char) 0x8B58);
        }
        else if (character == 0xF9FD) {
            sb.append((char) 0x4EC0);
        }
        else if (character == 0xF9FE) {
            sb.append((char) 0x8336);
        }
        else if (character == 0xF9FF) {
            sb.append((char) 0x523A);
        }
        else if (character == 0xFA00) {
            sb.append((char) 0x5207);
        }
        else if (character == 0xFA01) {
            sb.append((char) 0x5EA6);
        }
        else if (character == 0xFA02) {
            sb.append((char) 0x62D3);
        }
        else if (character == 0xFA03) {
            sb.append((char) 0x7CD6);
        }
        else if (character == 0xFA04) {
            sb.append((char) 0x5B85);
        }
        else if (character == 0xFA05) {
            sb.append((char) 0x6D1E);
        }
        else if (character == 0xFA06) {
            sb.append((char) 0x66B4);
        }
        else if (character == 0xFA07) {
            sb.append((char) 0x8F3B);
        }
        else if (character == 0xFA08) {
            sb.append((char) 0x884C);
        }
        else if (character == 0xFA09) {
            sb.append((char) 0x964D);
        }
        else if (character == 0xFA0A) {
            sb.append((char) 0x898B);
        }
        else if (character == 0xFA0B) {
            sb.append((char) 0x5ED3);
        }
        else if (character == 0xFA0C) {
            sb.append((char) 0x5140);
        }
        else if (character == 0xFA0D) {
            sb.append((char) 0x55C0);
        }
        else if (character == 0xFA10) {
            sb.append((char) 0x585A);
        }
        else if (character == 0xFA12) {
            sb.append((char) 0x6674);
        }
        else if (character == 0xFA15) {
            sb.append((char) 0x51DE);
        }
        else if (character == 0xFA16) {
            sb.append((char) 0x732A);
        }
        else if (character == 0xFA17) {
            sb.append((char) 0x76CA);
        }
        else if (character == 0xFA18) {
            sb.append((char) 0x793C);
        }
        else if (character == 0xFA19) {
            sb.append((char) 0x795E);
        }
        else if (character == 0xFA1A) {
            sb.append((char) 0x7965);
        }
        else if (character == 0xFA1B) {
            sb.append((char) 0x798F);
        }
        else if (character == 0xFA1C) {
            sb.append((char) 0x9756);
        }
        else if (character == 0xFA1D) {
            sb.append((char) 0x7CBE);
        }
        else if (character == 0xFA1E) {
            sb.append((char) 0x7FBD);
        }
        else if (character == 0xFA20) {
            sb.append((char) 0x8612);
        }
        else if (character == 0xFA22) {
            sb.append((char) 0x8AF8);
        }
        else if (character == 0xFA25) {
            sb.append((char) 0x9038);
        }
        else if (character == 0xFA26) {
            sb.append((char) 0x90FD);
        }
        else if (character == 0xFA2A) {
            sb.append((char) 0x98EF);
        }
        else if (character == 0xFA2B) {
            sb.append((char) 0x98FC);
        }
        else if (character == 0xFA2C) {
            sb.append((char) 0x9928);
        }
        else if (character == 0xFA2D) {
            sb.append((char) 0x9DB4);
        }
        else if (character == 0xFA30) {
            sb.append((char) 0x4FAE);
        }
        else if (character == 0xFA31) {
            sb.append((char) 0x50E7);
        }
        else if (character == 0xFA32) {
            sb.append((char) 0x514D);
        }
        else if (character == 0xFA33) {
            sb.append((char) 0x52C9);
        }
        else if (character == 0xFA34) {
            sb.append((char) 0x52E4);
        }
        else if (character == 0xFA35) {
            sb.append((char) 0x5351);
        }
        else if (character == 0xFA36) {
            sb.append((char) 0x559D);
        }
        else if (character == 0xFA37) {
            sb.append((char) 0x5606);
        }
        else if (character == 0xFA38) {
            sb.append((char) 0x5668);
        }
        else if (character == 0xFA39) {
            sb.append((char) 0x5840);
        }
        else if (character == 0xFA3A) {
            sb.append((char) 0x58A8);
        }
        else if (character == 0xFA3B) {
            sb.append((char) 0x5C64);
        }
        else if (character == 0xFA3C) {
            sb.append((char) 0x5C6E);
        }
        else if (character == 0xFA3D) {
            sb.append((char) 0x6094);
        }
        else if (character == 0xFA3E) {
            sb.append((char) 0x6168);
        }
        else if (character == 0xFA3F) {
            sb.append((char) 0x618E);
        }
        else if (character == 0xFA40) {
            sb.append((char) 0x61F2);
        }
        else if (character == 0xFA41) {
            sb.append((char) 0x654F);
        }
        else if (character == 0xFA42) {
            sb.append((char) 0x65E2);
        }
        else if (character == 0xFA43) {
            sb.append((char) 0x6691);
        }
        else if (character == 0xFA44) {
            sb.append((char) 0x6885);
        }
        else if (character == 0xFA45) {
            sb.append((char) 0x6D77);
        }
        else if (character == 0xFA46) {
            sb.append((char) 0x6E1A);
        }
        else if (character == 0xFA47) {
            sb.append((char) 0x6F22);
        }
        else if (character == 0xFA48) {
            sb.append((char) 0x716E);
        }
        else if (character == 0xFA49) {
            sb.append((char) 0x722B);
        }
        else if (character == 0xFA4A) {
            sb.append((char) 0x7422);
        }
        else if (character == 0xFA4B) {
            sb.append((char) 0x7891);
        }
        else if (character == 0xFA4C) {
            sb.append((char) 0x793E);
        }
        else if (character == 0xFA4D) {
            sb.append((char) 0x7949);
        }
        else if (character == 0xFA4E) {
            sb.append((char) 0x7948);
        }
        else if (character == 0xFA4F) {
            sb.append((char) 0x7950);
        }
        else if (character == 0xFA50) {
            sb.append((char) 0x7956);
        }
        else if (character == 0xFA51) {
            sb.append((char) 0x795D);
        }
        else if (character == 0xFA52) {
            sb.append((char) 0x798D);
        }
        else if (character == 0xFA53) {
            sb.append((char) 0x798E);
        }
        else if (character == 0xFA54) {
            sb.append((char) 0x7A40);
        }
        else if (character == 0xFA55) {
            sb.append((char) 0x7A81);
        }
        else if (character == 0xFA56) {
            sb.append((char) 0x7BC0);
        }
        else if (character == 0xFA57) {
            sb.append((char) 0x7DF4);
        }
        else if (character == 0xFA58) {
            sb.append((char) 0x7E09);
        }
        else if (character == 0xFA59) {
            sb.append((char) 0x7E41);
        }
        else if (character == 0xFA5A) {
            sb.append((char) 0x7F72);
        }
        else if (character == 0xFA5B) {
            sb.append((char) 0x8005);
        }
        else if (character == 0xFA5C) {
            sb.append((char) 0x81ED);
        }
        else if (character == 0xFA5D) {
            sb.append((char) 0x8279);
        }
        else if (character == 0xFA5E) {
            sb.append((char) 0x8279);
        }
        else if (character == 0xFA5F) {
            sb.append((char) 0x8457);
        }
        else if (character == 0xFA60) {
            sb.append((char) 0x8910);
        }
        else if (character == 0xFA61) {
            sb.append((char) 0x8996);
        }
        else if (character == 0xFA62) {
            sb.append((char) 0x8B01);
        }
        else if (character == 0xFA63) {
            sb.append((char) 0x8B39);
        }
        else if (character == 0xFA64) {
            sb.append((char) 0x8CD3);
        }
        else if (character == 0xFA65) {
            sb.append((char) 0x8D08);
        }
        else if (character == 0xFA66) {
            sb.append((char) 0x8FB6);
        }
        else if (character == 0xFA67) {
            sb.append((char) 0x9038);
        }
        else if (character == 0xFA68) {
            sb.append((char) 0x96E3);
        }
        else if (character == 0xFA69) {
            sb.append((char) 0x97FF);
        }
        else if (character == 0xFA6A) {
            sb.append((char) 0x983B);
        }
        else if (character == 0xFB1D) {
            sb.append((char) 0x05D9);
            sb.append((char) 0x05B4);
        }
        else if (character == 0xFB1F) {
            sb.append((char) 0x05F2);
            sb.append((char) 0x05B7);
        }
        else if (character == 0xFB2A) {
            sb.append((char) 0x05E9);
            sb.append((char) 0x05C1);
        }
        else if (character == 0xFB2B) {
            sb.append((char) 0x05E9);
            sb.append((char) 0x05C2);
        }
        else if (character == 0xFB2C) {
            sb.append((char) 0xFB49);
            sb.append((char) 0x05C1);
        }
        else if (character == 0xFB2D) {
            sb.append((char) 0xFB49);
            sb.append((char) 0x05C2);
        }
        else if (character == 0xFB2E) {
            sb.append((char) 0x05D0);
            sb.append((char) 0x05B7);
        }
        else if (character == 0xFB2F) {
            sb.append((char) 0x05D0);
            sb.append((char) 0x05B8);
        }
        else if (character == 0xFB30) {
            sb.append((char) 0x05D0);
            sb.append((char) 0x05BC);
        }
        else if (character == 0xFB31) {
            sb.append((char) 0x05D1);
            sb.append((char) 0x05BC);
        }
        else if (character == 0xFB32) {
            sb.append((char) 0x05D2);
            sb.append((char) 0x05BC);
        }
        else if (character == 0xFB33) {
            sb.append((char) 0x05D3);
            sb.append((char) 0x05BC);
        }
        else if (character == 0xFB34) {
            sb.append((char) 0x05D4);
            sb.append((char) 0x05BC);
        }
        else if (character == 0xFB35) {
            sb.append((char) 0x05D5);
            sb.append((char) 0x05BC);
        }
        else if (character == 0xFB36) {
            sb.append((char) 0x05D6);
            sb.append((char) 0x05BC);
        }
        else if (character == 0xFB38) {
            sb.append((char) 0x05D8);
            sb.append((char) 0x05BC);
        }
        else if (character == 0xFB39) {
            sb.append((char) 0x05D9);
            sb.append((char) 0x05BC);
        }
        else if (character == 0xFB3A) {
            sb.append((char) 0x05DA);
            sb.append((char) 0x05BC);
        }
        else if (character == 0xFB3B) {
            sb.append((char) 0x05DB);
            sb.append((char) 0x05BC);
        }
        else if (character == 0xFB3C) {
            sb.append((char) 0x05DC);
            sb.append((char) 0x05BC);
        }
        else if (character == 0xFB3E) {
            sb.append((char) 0x05DE);
            sb.append((char) 0x05BC);
        }
        else if (character == 0xFB40) {
            sb.append((char) 0x05E0);
            sb.append((char) 0x05BC);
        }
        else if (character == 0xFB41) {
            sb.append((char) 0x05E1);
            sb.append((char) 0x05BC);
        }
        else if (character == 0xFB43) {
            sb.append((char) 0x05E3);
            sb.append((char) 0x05BC);
        }
        else if (character == 0xFB44) {
            sb.append((char) 0x05E4);
            sb.append((char) 0x05BC);
        }
        else if (character == 0xFB46) {
            sb.append((char) 0x05E6);
            sb.append((char) 0x05BC);
        }
        else if (character == 0xFB47) {
            sb.append((char) 0x05E7);
            sb.append((char) 0x05BC);
        }
        else if (character == 0xFB48) {
            sb.append((char) 0x05E8);
            sb.append((char) 0x05BC);
        }
        else if (character == 0xFB49) {
            sb.append((char) 0x05E9);
            sb.append((char) 0x05BC);
        }
        else if (character == 0xFB4A) {
            sb.append((char) 0x05EA);
            sb.append((char) 0x05BC);
        }
        else if (character == 0xFB4B) {
            sb.append((char) 0x05D5);
            sb.append((char) 0x05B9);
        }
        else if (character == 0xFB4C) {
            sb.append((char) 0x05D1);
            sb.append((char) 0x05BF);
        }
        else if (character == 0xFB4D) {
            sb.append((char) 0x05DB);
            sb.append((char) 0x05BF);
        }
        else if (character == 0xFB4E) {
            sb.append((char) 0x05E4);
            sb.append((char) 0x05BF);
        }
        else if (character == 0x1D15E) {
            sb.append((char) 0xd834);
            sb.append((char) 0xdd57);
            sb.append((char) 0xd834);
            sb.append((char) 0xdd65);
        }
        else if (character == 0x1D15F) {
            sb.append((char) 0xd834);
            sb.append((char) 0xdd58);
            sb.append((char) 0xd834);
            sb.append((char) 0xdd65);
        }
        else if (character == 0x1D160) {
            sb.append((char) 0xd834);
            sb.append((char) 0xdd5f);
            sb.append((char) 0xd834);
            sb.append((char) 0xdd6e);
        }
        else if (character == 0x1D161) {
            sb.append((char) 0xd834);
            sb.append((char) 0xdd5f);
            sb.append((char) 0xd834);
            sb.append((char) 0xdd6f);
        }
        else if (character == 0x1D162) {
            sb.append((char) 0xd834);
            sb.append((char) 0xdd5f);
            sb.append((char) 0xd834);
            sb.append((char) 0xdd70);
        }
        else if (character == 0x1D163) {
            sb.append((char) 0xd834);
            sb.append((char) 0xdd5f);
            sb.append((char) 0xd834);
            sb.append((char) 0xdd71);
        }
        else if (character == 0x1D164) {
            sb.append((char) 0xd834);
            sb.append((char) 0xdd5f);
            sb.append((char) 0xd834);
            sb.append((char) 0xdd72);
        }
        else if (character == 0x1D1BB) {
            sb.append((char) 0xd834);
            sb.append((char) 0xddb9);
            sb.append((char) 0xd834);
            sb.append((char) 0xdd65);
        }
        else if (character == 0x1D1BC) {
            sb.append((char) 0xd834);
            sb.append((char) 0xddba);
            sb.append((char) 0xd834);
            sb.append((char) 0xdd65);
        }
        else if (character == 0x1D1BD) {
            sb.append((char) 0xd834);
            sb.append((char) 0xddbb);
            sb.append((char) 0xd834);
            sb.append((char) 0xdd6e);
        }
        else if (character == 0x1D1BE) {
            sb.append((char) 0xd834);
            sb.append((char) 0xddbc);
            sb.append((char) 0xd834);
            sb.append((char) 0xdd6e);
        }
        else if (character == 0x1D1BF) {
            sb.append((char) 0xd834);
            sb.append((char) 0xddbb);
            sb.append((char) 0xd834);
            sb.append((char) 0xdd6f);
        }
        else if (character == 0x1D1C0) {
            sb.append((char) 0xd834);
            sb.append((char) 0xddbc);
            sb.append((char) 0xd834);
            sb.append((char) 0xdd6f);
        }
        else if (character == 0x2F800) {
            sb.append((char) 0x4E3D);
        }
        else if (character == 0x2F801) {
            sb.append((char) 0x4E38);
        }
        else if (character == 0x2F802) {
            sb.append((char) 0x4E41);
        }
        else if (character == 0x2F803) {
            sb.append((char) 0xd840);
            sb.append((char) 0xdd22);
        }
        else if (character == 0x2F804) {
            sb.append((char) 0x4F60);
        }
        else if (character == 0x2F805) {
            sb.append((char) 0x4FAE);
        }
        else if (character == 0x2F806) {
            sb.append((char) 0x4FBB);
        }
        else if (character == 0x2F807) {
            sb.append((char) 0x5002);
        }
        else if (character == 0x2F808) {
            sb.append((char) 0x507A);
        }
        else if (character == 0x2F809) {
            sb.append((char) 0x5099);
        }
        else if (character == 0x2F80A) {
            sb.append((char) 0x50E7);
        }
        else if (character == 0x2F80B) {
            sb.append((char) 0x50CF);
        }
        else if (character == 0x2F80C) {
            sb.append((char) 0x349E);
        }
        else if (character == 0x2F80D) {
            sb.append((char) 0xd841);
            sb.append((char) 0xde3a);
        }
        else if (character == 0x2F80E) {
            sb.append((char) 0x514D);
        }
        else if (character == 0x2F80F) {
            sb.append((char) 0x5154);
        }
        else if (character == 0x2F810) {
            sb.append((char) 0x5164);
        }
        else if (character == 0x2F811) {
            sb.append((char) 0x5177);
        }
        else if (character == 0x2F812) {
            sb.append((char) 0xd841);
            sb.append((char) 0xdd1c);
        }
        else if (character == 0x2F813) {
            sb.append((char) 0x34B9);
        }
        else if (character == 0x2F814) {
            sb.append((char) 0x5167);
        }
        else if (character == 0x2F815) {
            sb.append((char) 0x518D);
        }
        else if (character == 0x2F816) {
            sb.append((char) 0xd841);
            sb.append((char) 0xdd4b);
        }
        else if (character == 0x2F817) {
            sb.append((char) 0x5197);
        }
        else if (character == 0x2F818) {
            sb.append((char) 0x51A4);
        }
        else if (character == 0x2F819) {
            sb.append((char) 0x4ECC);
        }
        else if (character == 0x2F81A) {
            sb.append((char) 0x51AC);
        }
        else if (character == 0x2F81B) {
            sb.append((char) 0x51B5);
        }
        else if (character == 0x2F81C) {
            sb.append((char) 0xd864);
            sb.append((char) 0xdddf);
        }
        else if (character == 0x2F81D) {
            sb.append((char) 0x51F5);
        }
        else if (character == 0x2F81E) {
            sb.append((char) 0x5203);
        }
        else if (character == 0x2F81F) {
            sb.append((char) 0x34DF);
        }
        else if (character == 0x2F820) {
            sb.append((char) 0x523B);
        }
        else if (character == 0x2F821) {
            sb.append((char) 0x5246);
        }
        else if (character == 0x2F822) {
            sb.append((char) 0x5272);
        }
        else if (character == 0x2F823) {
            sb.append((char) 0x5277);
        }
        else if (character == 0x2F824) {
            sb.append((char) 0x3515);
        }
        else if (character == 0x2F825) {
            sb.append((char) 0x52C7);
        }
        else if (character == 0x2F826) {
            sb.append((char) 0x52C9);
        }
        else if (character == 0x2F827) {
            sb.append((char) 0x52E4);
        }
        else if (character == 0x2F828) {
            sb.append((char) 0x52FA);
        }
        else if (character == 0x2F829) {
            sb.append((char) 0x5305);
        }
        else if (character == 0x2F82A) {
            sb.append((char) 0x5306);
        }
        else if (character == 0x2F82B) {
            sb.append((char) 0x5317);
        }
        else if (character == 0x2F82C) {
            sb.append((char) 0x5349);
        }
        else if (character == 0x2F82D) {
            sb.append((char) 0x5351);
        }
        else if (character == 0x2F82E) {
            sb.append((char) 0x535A);
        }
        else if (character == 0x2F82F) {
            sb.append((char) 0x5373);
        }
        else if (character == 0x2F830) {
            sb.append((char) 0x537D);
        }
        else if (character == 0x2F831) {
            sb.append((char) 0x537F);
        }
        else if (character == 0x2F832) {
            sb.append((char) 0x537F);
        }
        else if (character == 0x2F833) {
            sb.append((char) 0x537F);
        }
        else if (character == 0x2F834) {
            sb.append((char) 0xd842);
            sb.append((char) 0xde2c);
        }
        else if (character == 0x2F835) {
            sb.append((char) 0x7070);
        }
        else if (character == 0x2F836) {
            sb.append((char) 0x53CA);
        }
        else if (character == 0x2F837) {
            sb.append((char) 0x53DF);
        }
        else if (character == 0x2F838) {
            sb.append((char) 0xd842);
            sb.append((char) 0xdf63);
        }
        else if (character == 0x2F839) {
            sb.append((char) 0x53EB);
        }
        else if (character == 0x2F83A) {
            sb.append((char) 0x53F1);
        }
        else if (character == 0x2F83B) {
            sb.append((char) 0x5406);
        }
        else if (character == 0x2F83C) {
            sb.append((char) 0x549E);
        }
        else if (character == 0x2F83D) {
            sb.append((char) 0x5438);
        }
        else if (character == 0x2F83E) {
            sb.append((char) 0x5448);
        }
        else if (character == 0x2F83F) {
            sb.append((char) 0x5468);
        }
        else if (character == 0x2F840) {
            sb.append((char) 0x54A2);
        }
        else if (character == 0x2F841) {
            sb.append((char) 0x54F6);
        }
        else if (character == 0x2F842) {
            sb.append((char) 0x5510);
        }
        else if (character == 0x2F843) {
            sb.append((char) 0x5553);
        }
        else if (character == 0x2F844) {
            sb.append((char) 0x5563);
        }
        else if (character == 0x2F845) {
            sb.append((char) 0x5584);
        }
        else if (character == 0x2F846) {
            sb.append((char) 0x5584);
        }
        else if (character == 0x2F847) {
            sb.append((char) 0x5599);
        }
        else if (character == 0x2F848) {
            sb.append((char) 0x55AB);
        }
        else if (character == 0x2F849) {
            sb.append((char) 0x55B3);
        }
        else if (character == 0x2F84A) {
            sb.append((char) 0x55C2);
        }
        else if (character == 0x2F84B) {
            sb.append((char) 0x5716);
        }
        else if (character == 0x2F84C) {
            sb.append((char) 0x5606);
        }
        else if (character == 0x2F84D) {
            sb.append((char) 0x5717);
        }
        else if (character == 0x2F84E) {
            sb.append((char) 0x5651);
        }
        else if (character == 0x2F84F) {
            sb.append((char) 0x5674);
        }
        else if (character == 0x2F850) {
            sb.append((char) 0x5207);
        }
        else if (character == 0x2F851) {
            sb.append((char) 0x58EE);
        }
        else if (character == 0x2F852) {
            sb.append((char) 0x57CE);
        }
        else if (character == 0x2F853) {
            sb.append((char) 0x57F4);
        }
        else if (character == 0x2F854) {
            sb.append((char) 0x580D);
        }
        else if (character == 0x2F855) {
            sb.append((char) 0x578B);
        }
        else if (character == 0x2F856) {
            sb.append((char) 0x5832);
        }
        else if (character == 0x2F857) {
            sb.append((char) 0x5831);
        }
        else if (character == 0x2F858) {
            sb.append((char) 0x58AC);
        }
        else if (character == 0x2F859) {
            sb.append((char) 0xd845);
            sb.append((char) 0xdce4);
        }
        else if (character == 0x2F85A) {
            sb.append((char) 0x58F2);
        }
        else if (character == 0x2F85B) {
            sb.append((char) 0x58F7);
        }
        else if (character == 0x2F85C) {
            sb.append((char) 0x5906);
        }
        else if (character == 0x2F85D) {
            sb.append((char) 0x591A);
        }
        else if (character == 0x2F85E) {
            sb.append((char) 0x5922);
        }
        else if (character == 0x2F85F) {
            sb.append((char) 0x5962);
        }
        else if (character == 0x2F860) {
            sb.append((char) 0xd845);
            sb.append((char) 0xdea8);
        }
        else if (character == 0x2F861) {
            sb.append((char) 0xd845);
            sb.append((char) 0xdeea);
        }
        else if (character == 0x2F862) {
            sb.append((char) 0x59EC);
        }
        else if (character == 0x2F863) {
            sb.append((char) 0x5A1B);
        }
        else if (character == 0x2F864) {
            sb.append((char) 0x5A27);
        }
        else if (character == 0x2F865) {
            sb.append((char) 0x59D8);
        }
        else if (character == 0x2F866) {
            sb.append((char) 0x5A66);
        }
        else if (character == 0x2F867) {
            sb.append((char) 0x36EE);
        }
        else if (character == 0x2F868) {
            sb.append((char) 0x36FC);
        }
        else if (character == 0x2F869) {
            sb.append((char) 0x5B08);
        }
        else if (character == 0x2F86A) {
            sb.append((char) 0x5B3E);
        }
        else if (character == 0x2F86B) {
            sb.append((char) 0x5B3E);
        }
        else if (character == 0x2F86C) {
            sb.append((char) 0xd846);
            sb.append((char) 0xddc8);
        }
        else if (character == 0x2F86D) {
            sb.append((char) 0x5BC3);
        }
        else if (character == 0x2F86E) {
            sb.append((char) 0x5BD8);
        }
        else if (character == 0x2F86F) {
            sb.append((char) 0x5BE7);
        }
        else if (character == 0x2F870) {
            sb.append((char) 0x5BF3);
        }
        else if (character == 0x2F871) {
            sb.append((char) 0xd846);
            sb.append((char) 0xdf18);
        }
        else if (character == 0x2F872) {
            sb.append((char) 0x5BFF);
        }
        else if (character == 0x2F873) {
            sb.append((char) 0x5C06);
        }
        else if (character == 0x2F874) {
            sb.append((char) 0x5F53);
        }
        else if (character == 0x2F875) {
            sb.append((char) 0x5C22);
        }
        else if (character == 0x2F876) {
            sb.append((char) 0x3781);
        }
        else if (character == 0x2F877) {
            sb.append((char) 0x5C60);
        }
        else if (character == 0x2F878) {
            sb.append((char) 0x5C6E);
        }
        else if (character == 0x2F879) {
            sb.append((char) 0x5CC0);
        }
        else if (character == 0x2F87A) {
            sb.append((char) 0x5C8D);
        }
        else if (character == 0x2F87B) {
            sb.append((char) 0xd847);
            sb.append((char) 0xdde4);
        }
        else if (character == 0x2F87C) {
            sb.append((char) 0x5D43);
        }
        else if (character == 0x2F87D) {
            sb.append((char) 0xd847);
            sb.append((char) 0xdde6);
        }
        else if (character == 0x2F87E) {
            sb.append((char) 0x5D6E);
        }
        else if (character == 0x2F87F) {
            sb.append((char) 0x5D6B);
        }
        else if (character == 0x2F880) {
            sb.append((char) 0x5D7C);
        }
        else if (character == 0x2F881) {
            sb.append((char) 0x5DE1);
        }
        else if (character == 0x2F882) {
            sb.append((char) 0x5DE2);
        }
        else if (character == 0x2F883) {
            sb.append((char) 0x382F);
        }
        else if (character == 0x2F884) {
            sb.append((char) 0x5DFD);
        }
        else if (character == 0x2F885) {
            sb.append((char) 0x5E28);
        }
        else if (character == 0x2F886) {
            sb.append((char) 0x5E3D);
        }
        else if (character == 0x2F887) {
            sb.append((char) 0x5E69);
        }
        else if (character == 0x2F888) {
            sb.append((char) 0x3862);
        }
        else if (character == 0x2F889) {
            sb.append((char) 0xd848);
            sb.append((char) 0xdd83);
        }
        else if (character == 0x2F88A) {
            sb.append((char) 0x387C);
        }
        else if (character == 0x2F88B) {
            sb.append((char) 0x5EB0);
        }
        else if (character == 0x2F88C) {
            sb.append((char) 0x5EB3);
        }
        else if (character == 0x2F88D) {
            sb.append((char) 0x5EB6);
        }
        else if (character == 0x2F88E) {
            sb.append((char) 0x5ECA);
        }
        else if (character == 0x2F88F) {
            sb.append((char) 0xd868);
            sb.append((char) 0xdf92);
        }
        else if (character == 0x2F890) {
            sb.append((char) 0x5EFE);
        }
        else if (character == 0x2F891) {
            sb.append((char) 0xd848);
            sb.append((char) 0xdf31);
        }
        else if (character == 0x2F892) {
            sb.append((char) 0xd848);
            sb.append((char) 0xdf31);
        }
        else if (character == 0x2F893) {
            sb.append((char) 0x8201);
        }
        else if (character == 0x2F894) {
            sb.append((char) 0x5F22);
        }
        else if (character == 0x2F895) {
            sb.append((char) 0x5F22);
        }
        else if (character == 0x2F896) {
            sb.append((char) 0x38C7);
        }
        else if (character == 0x2F897) {
            sb.append((char) 0xd84c);
            sb.append((char) 0xdeb8);
        }
        else if (character == 0x2F898) {
            sb.append((char) 0xd858);
            sb.append((char) 0xddda);
        }
        else if (character == 0x2F899) {
            sb.append((char) 0x5F62);
        }
        else if (character == 0x2F89A) {
            sb.append((char) 0x5F6B);
        }
        else if (character == 0x2F89B) {
            sb.append((char) 0x38E3);
        }
        else if (character == 0x2F89C) {
            sb.append((char) 0x5F9A);
        }
        else if (character == 0x2F89D) {
            sb.append((char) 0x5FCD);
        }
        else if (character == 0x2F89E) {
            sb.append((char) 0x5FD7);
        }
        else if (character == 0x2F89F) {
            sb.append((char) 0x5FF9);
        }
        else if (character == 0x2F8A0) {
            sb.append((char) 0x6081);
        }
        else if (character == 0x2F8A1) {
            sb.append((char) 0x393A);
        }
        else if (character == 0x2F8A2) {
            sb.append((char) 0x391C);
        }
        else if (character == 0x2F8A3) {
            sb.append((char) 0x6094);
        }
        else if (character == 0x2F8A4) {
            sb.append((char) 0xd849);
            sb.append((char) 0xded4);
        }
        else if (character == 0x2F8A5) {
            sb.append((char) 0x60C7);
        }
        else if (character == 0x2F8A6) {
            sb.append((char) 0x6148);
        }
        else if (character == 0x2F8A7) {
            sb.append((char) 0x614C);
        }
        else if (character == 0x2F8A8) {
            sb.append((char) 0x614E);
        }
        else if (character == 0x2F8A9) {
            sb.append((char) 0x614C);
        }
        else if (character == 0x2F8AA) {
            sb.append((char) 0x617A);
        }
        else if (character == 0x2F8AB) {
            sb.append((char) 0x618E);
        }
        else if (character == 0x2F8AC) {
            sb.append((char) 0x61B2);
        }
        else if (character == 0x2F8AD) {
            sb.append((char) 0x61A4);
        }
        else if (character == 0x2F8AE) {
            sb.append((char) 0x61AF);
        }
        else if (character == 0x2F8AF) {
            sb.append((char) 0x61DE);
        }
        else if (character == 0x2F8B0) {
            sb.append((char) 0x61F2);
        }
        else if (character == 0x2F8B1) {
            sb.append((char) 0x61F6);
        }
        else if (character == 0x2F8B2) {
            sb.append((char) 0x6210);
        }
        else if (character == 0x2F8B3) {
            sb.append((char) 0x621B);
        }
        else if (character == 0x2F8B4) {
            sb.append((char) 0x625D);
        }
        else if (character == 0x2F8B5) {
            sb.append((char) 0x62B1);
        }
        else if (character == 0x2F8B6) {
            sb.append((char) 0x62D4);
        }
        else if (character == 0x2F8B7) {
            sb.append((char) 0x6350);
        }
        else if (character == 0x2F8B8) {
            sb.append((char) 0xd84a);
            sb.append((char) 0xdf0c);
        }
        else if (character == 0x2F8B9) {
            sb.append((char) 0x633D);
        }
        else if (character == 0x2F8BA) {
            sb.append((char) 0x62FC);
        }
        else if (character == 0x2F8BB) {
            sb.append((char) 0x6368);
        }
        else if (character == 0x2F8BC) {
            sb.append((char) 0x6383);
        }
        else if (character == 0x2F8BD) {
            sb.append((char) 0x63E4);
        }
        else if (character == 0x2F8BE) {
            sb.append((char) 0xd84a);
            sb.append((char) 0xdff1);
        }
        else if (character == 0x2F8BF) {
            sb.append((char) 0x6422);
        }
        else if (character == 0x2F8C0) {
            sb.append((char) 0x63C5);
        }
        else if (character == 0x2F8C1) {
            sb.append((char) 0x63A9);
        }
        else if (character == 0x2F8C2) {
            sb.append((char) 0x3A2E);
        }
        else if (character == 0x2F8C3) {
            sb.append((char) 0x6469);
        }
        else if (character == 0x2F8C4) {
            sb.append((char) 0x647E);
        }
        else if (character == 0x2F8C5) {
            sb.append((char) 0x649D);
        }
        else if (character == 0x2F8C6) {
            sb.append((char) 0x6477);
        }
        else if (character == 0x2F8C7) {
            sb.append((char) 0x3A6C);
        }
        else if (character == 0x2F8C8) {
            sb.append((char) 0x654F);
        }
        else if (character == 0x2F8C9) {
            sb.append((char) 0x656C);
        }
        else if (character == 0x2F8CA) {
            sb.append((char) 0xd84c);
            sb.append((char) 0xdc0a);
        }
        else if (character == 0x2F8CB) {
            sb.append((char) 0x65E3);
        }
        else if (character == 0x2F8CC) {
            sb.append((char) 0x66F8);
        }
        else if (character == 0x2F8CD) {
            sb.append((char) 0x6649);
        }
        else if (character == 0x2F8CE) {
            sb.append((char) 0x3B19);
        }
        else if (character == 0x2F8CF) {
            sb.append((char) 0x6691);
        }
        else if (character == 0x2F8D0) {
            sb.append((char) 0x3B08);
        }
        else if (character == 0x2F8D1) {
            sb.append((char) 0x3AE4);
        }
        else if (character == 0x2F8D2) {
            sb.append((char) 0x5192);
        }
        else if (character == 0x2F8D3) {
            sb.append((char) 0x5195);
        }
        else if (character == 0x2F8D4) {
            sb.append((char) 0x6700);
        }
        else if (character == 0x2F8D5) {
            sb.append((char) 0x669C);
        }
        else if (character == 0x2F8D6) {
            sb.append((char) 0x80AD);
        }
        else if (character == 0x2F8D7) {
            sb.append((char) 0x43D9);
        }
        else if (character == 0x2F8D8) {
            sb.append((char) 0x6717);
        }
        else if (character == 0x2F8D9) {
            sb.append((char) 0x671B);
        }
        else if (character == 0x2F8DA) {
            sb.append((char) 0x6721);
        }
        else if (character == 0x2F8DB) {
            sb.append((char) 0x675E);
        }
        else if (character == 0x2F8DC) {
            sb.append((char) 0x6753);
        }
        else if (character == 0x2F8DD) {
            sb.append((char) 0xd84c);
            sb.append((char) 0xdfc3);
        }
        else if (character == 0x2F8DE) {
            sb.append((char) 0x3B49);
        }
        else if (character == 0x2F8DF) {
            sb.append((char) 0x67FA);
        }
        else if (character == 0x2F8E0) {
            sb.append((char) 0x6785);
        }
        else if (character == 0x2F8E1) {
            sb.append((char) 0x6852);
        }
        else if (character == 0x2F8E2) {
            sb.append((char) 0x6885);
        }
        else if (character == 0x2F8E3) {
            sb.append((char) 0xd84d);
            sb.append((char) 0xdc6d);
        }
        else if (character == 0x2F8E4) {
            sb.append((char) 0x688E);
        }
        else if (character == 0x2F8E5) {
            sb.append((char) 0x681F);
        }
        else if (character == 0x2F8E6) {
            sb.append((char) 0x6914);
        }
        else if (character == 0x2F8E7) {
            sb.append((char) 0x3B9D);
        }
        else if (character == 0x2F8E8) {
            sb.append((char) 0x6942);
        }
        else if (character == 0x2F8E9) {
            sb.append((char) 0x69A3);
        }
        else if (character == 0x2F8EA) {
            sb.append((char) 0x69EA);
        }
        else if (character == 0x2F8EB) {
            sb.append((char) 0x6AA8);
        }
        else if (character == 0x2F8EC) {
            sb.append((char) 0xd84d);
            sb.append((char) 0xdea3);
        }
        else if (character == 0x2F8ED) {
            sb.append((char) 0x6ADB);
        }
        else if (character == 0x2F8EE) {
            sb.append((char) 0x3C18);
        }
        else if (character == 0x2F8EF) {
            sb.append((char) 0x6B21);
        }
        else if (character == 0x2F8F0) {
            sb.append((char) 0xd84e);
            sb.append((char) 0xdca7);
        }
        else if (character == 0x2F8F1) {
            sb.append((char) 0x6B54);
        }
        else if (character == 0x2F8F2) {
            sb.append((char) 0x3C4E);
        }
        else if (character == 0x2F8F3) {
            sb.append((char) 0x6B72);
        }
        else if (character == 0x2F8F4) {
            sb.append((char) 0x6B9F);
        }
        else if (character == 0x2F8F5) {
            sb.append((char) 0x6BBA);
        }
        else if (character == 0x2F8F6) {
            sb.append((char) 0x6BBB);
        }
        else if (character == 0x2F8F7) {
            sb.append((char) 0xd84e);
            sb.append((char) 0xde8d);
        }
        else if (character == 0x2F8F8) {
            sb.append((char) 0xd847);
            sb.append((char) 0xdd0b);
        }
        else if (character == 0x2F8F9) {
            sb.append((char) 0xd84e);
            sb.append((char) 0xdefa);
        }
        else if (character == 0x2F8FA) {
            sb.append((char) 0x6C4E);
        }
        else if (character == 0x2F8FB) {
            sb.append((char) 0xd84f);
            sb.append((char) 0xdcbc);
        }
        else if (character == 0x2F8FC) {
            sb.append((char) 0x6CBF);
        }
        else if (character == 0x2F8FD) {
            sb.append((char) 0x6CCD);
        }
        else if (character == 0x2F8FE) {
            sb.append((char) 0x6C67);
        }
        else if (character == 0x2F8FF) {
            sb.append((char) 0x6D16);
        }
        else if (character == 0x2F900) {
            sb.append((char) 0x6D3E);
        }
        else if (character == 0x2F901) {
            sb.append((char) 0x6D77);
        }
        else if (character == 0x2F902) {
            sb.append((char) 0x6D41);
        }
        else if (character == 0x2F903) {
            sb.append((char) 0x6D69);
        }
        else if (character == 0x2F904) {
            sb.append((char) 0x6D78);
        }
        else if (character == 0x2F905) {
            sb.append((char) 0x6D85);
        }
        else if (character == 0x2F906) {
            sb.append((char) 0xd84f);
            sb.append((char) 0xdd1e);
        }
        else if (character == 0x2F907) {
            sb.append((char) 0x6D34);
        }
        else if (character == 0x2F908) {
            sb.append((char) 0x6E2F);
        }
        else if (character == 0x2F909) {
            sb.append((char) 0x6E6E);
        }
        else if (character == 0x2F90A) {
            sb.append((char) 0x3D33);
        }
        else if (character == 0x2F90B) {
            sb.append((char) 0x6ECB);
        }
        else if (character == 0x2F90C) {
            sb.append((char) 0x6EC7);
        }
        else if (character == 0x2F90D) {
            sb.append((char) 0xd84f);
            sb.append((char) 0xded1);
        }
        else if (character == 0x2F90E) {
            sb.append((char) 0x6DF9);
        }
        else if (character == 0x2F90F) {
            sb.append((char) 0x6F6E);
        }
        else if (character == 0x2F910) {
            sb.append((char) 0xd84f);
            sb.append((char) 0xdf5e);
        }
        else if (character == 0x2F911) {
            sb.append((char) 0xd84f);
            sb.append((char) 0xdf8e);
        }
        else if (character == 0x2F912) {
            sb.append((char) 0x6FC6);
        }
        else if (character == 0x2F913) {
            sb.append((char) 0x7039);
        }
        else if (character == 0x2F914) {
            sb.append((char) 0x701E);
        }
        else if (character == 0x2F915) {
            sb.append((char) 0x701B);
        }
        else if (character == 0x2F916) {
            sb.append((char) 0x3D96);
        }
        else if (character == 0x2F917) {
            sb.append((char) 0x704A);
        }
        else if (character == 0x2F918) {
            sb.append((char) 0x707D);
        }
        else if (character == 0x2F919) {
            sb.append((char) 0x7077);
        }
        else if (character == 0x2F91A) {
            sb.append((char) 0x70AD);
        }
        else if (character == 0x2F91B) {
            sb.append((char) 0xd841);
            sb.append((char) 0xdd25);
        }
        else if (character == 0x2F91C) {
            sb.append((char) 0x7145);
        }
        else if (character == 0x2F91D) {
            sb.append((char) 0xd850);
            sb.append((char) 0xde63);
        }
        else if (character == 0x2F91E) {
            sb.append((char) 0x719C);
        }
        else if (character == 0x2F91F) {
            sb.append((char) 0xd850);
            sb.append((char) 0xdfab);
        }
        else if (character == 0x2F920) {
            sb.append((char) 0x7228);
        }
        else if (character == 0x2F921) {
            sb.append((char) 0x7235);
        }
        else if (character == 0x2F922) {
            sb.append((char) 0x7250);
        }
        else if (character == 0x2F923) {
            sb.append((char) 0xd851);
            sb.append((char) 0xde08);
        }
        else if (character == 0x2F924) {
            sb.append((char) 0x7280);
        }
        else if (character == 0x2F925) {
            sb.append((char) 0x7295);
        }
        else if (character == 0x2F926) {
            sb.append((char) 0xd851);
            sb.append((char) 0xdf35);
        }
        else if (character == 0x2F927) {
            sb.append((char) 0xd852);
            sb.append((char) 0xdc14);
        }
        else if (character == 0x2F928) {
            sb.append((char) 0x737A);
        }
        else if (character == 0x2F929) {
            sb.append((char) 0x738B);
        }
        else if (character == 0x2F92A) {
            sb.append((char) 0x3EAC);
        }
        else if (character == 0x2F92B) {
            sb.append((char) 0x73A5);
        }
        else if (character == 0x2F92C) {
            sb.append((char) 0x3EB8);
        }
        else if (character == 0x2F92D) {
            sb.append((char) 0x3EB8);
        }
        else if (character == 0x2F92E) {
            sb.append((char) 0x7447);
        }
        else if (character == 0x2F92F) {
            sb.append((char) 0x745C);
        }
        else if (character == 0x2F930) {
            sb.append((char) 0x7471);
        }
        else if (character == 0x2F931) {
            sb.append((char) 0x7485);
        }
        else if (character == 0x2F932) {
            sb.append((char) 0x74CA);
        }
        else if (character == 0x2F933) {
            sb.append((char) 0x3F1B);
        }
        else if (character == 0x2F934) {
            sb.append((char) 0x7524);
        }
        else if (character == 0x2F935) {
            sb.append((char) 0xd853);
            sb.append((char) 0xdc36);
        }
        else if (character == 0x2F936) {
            sb.append((char) 0x753E);
        }
        else if (character == 0x2F937) {
            sb.append((char) 0xd853);
            sb.append((char) 0xdc92);
        }
        else if (character == 0x2F938) {
            sb.append((char) 0x7570);
        }
        else if (character == 0x2F939) {
            sb.append((char) 0xd848);
            sb.append((char) 0xdd9f);
        }
        else if (character == 0x2F93A) {
            sb.append((char) 0x7610);
        }
        else if (character == 0x2F93B) {
            sb.append((char) 0xd853);
            sb.append((char) 0xdfa1);
        }
        else if (character == 0x2F93C) {
            sb.append((char) 0xd853);
            sb.append((char) 0xdfb8);
        }
        else if (character == 0x2F93D) {
            sb.append((char) 0xd854);
            sb.append((char) 0xdc44);
        }
        else if (character == 0x2F93E) {
            sb.append((char) 0x3FFC);
        }
        else if (character == 0x2F93F) {
            sb.append((char) 0x4008);
        }
        else if (character == 0x2F940) {
            sb.append((char) 0x76F4);
        }
        else if (character == 0x2F941) {
            sb.append((char) 0xd854);
            sb.append((char) 0xdcf3);
        }
        else if (character == 0x2F942) {
            sb.append((char) 0xd854);
            sb.append((char) 0xdcf2);
        }
        else if (character == 0x2F943) {
            sb.append((char) 0xd854);
            sb.append((char) 0xdd19);
        }
        else if (character == 0x2F944) {
            sb.append((char) 0xd854);
            sb.append((char) 0xdd33);
        }
        else if (character == 0x2F945) {
            sb.append((char) 0x771E);
        }
        else if (character == 0x2F946) {
            sb.append((char) 0x771F);
        }
        else if (character == 0x2F947) {
            sb.append((char) 0x771F);
        }
        else if (character == 0x2F948) {
            sb.append((char) 0x774A);
        }
        else if (character == 0x2F949) {
            sb.append((char) 0x4039);
        }
        else if (character == 0x2F94A) {
            sb.append((char) 0x778B);
        }
        else if (character == 0x2F94B) {
            sb.append((char) 0x4046);
        }
        else if (character == 0x2F94C) {
            sb.append((char) 0x4096);
        }
        else if (character == 0x2F94D) {
            sb.append((char) 0xd855);
            sb.append((char) 0xdc1d);
        }
        else if (character == 0x2F94E) {
            sb.append((char) 0x784E);
        }
        else if (character == 0x2F94F) {
            sb.append((char) 0x788C);
        }
        else if (character == 0x2F950) {
            sb.append((char) 0x78CC);
        }
        else if (character == 0x2F951) {
            sb.append((char) 0x40E3);
        }
        else if (character == 0x2F952) {
            sb.append((char) 0xd855);
            sb.append((char) 0xde26);
        }
        else if (character == 0x2F953) {
            sb.append((char) 0x7956);
        }
        else if (character == 0x2F954) {
            sb.append((char) 0xd855);
            sb.append((char) 0xde9a);
        }
        else if (character == 0x2F955) {
            sb.append((char) 0xd855);
            sb.append((char) 0xdec5);
        }
        else if (character == 0x2F956) {
            sb.append((char) 0x798F);
        }
        else if (character == 0x2F957) {
            sb.append((char) 0x79EB);
        }
        else if (character == 0x2F958) {
            sb.append((char) 0x412F);
        }
        else if (character == 0x2F959) {
            sb.append((char) 0x7A40);
        }
        else if (character == 0x2F95A) {
            sb.append((char) 0x7A4A);
        }
        else if (character == 0x2F95B) {
            sb.append((char) 0x7A4F);
        }
        else if (character == 0x2F95C) {
            sb.append((char) 0xd856);
            sb.append((char) 0xdd7c);
        }
        else if (character == 0x2F95D) {
            sb.append((char) 0xd856);
            sb.append((char) 0xdea7);
        }
        else if (character == 0x2F95E) {
            sb.append((char) 0xd856);
            sb.append((char) 0xdea7);
        }
        else if (character == 0x2F95F) {
            sb.append((char) 0x7AEE);
        }
        else if (character == 0x2F960) {
            sb.append((char) 0x4202);
        }
        else if (character == 0x2F961) {
            sb.append((char) 0xd856);
            sb.append((char) 0xdfab);
        }
        else if (character == 0x2F962) {
            sb.append((char) 0x7BC6);
        }
        else if (character == 0x2F963) {
            sb.append((char) 0x7BC9);
        }
        else if (character == 0x2F964) {
            sb.append((char) 0x4227);
        }
        else if (character == 0x2F965) {
            sb.append((char) 0xd857);
            sb.append((char) 0xdc80);
        }
        else if (character == 0x2F966) {
            sb.append((char) 0x7CD2);
        }
        else if (character == 0x2F967) {
            sb.append((char) 0x42A0);
        }
        else if (character == 0x2F968) {
            sb.append((char) 0x7CE8);
        }
        else if (character == 0x2F969) {
            sb.append((char) 0x7CE3);
        }
        else if (character == 0x2F96A) {
            sb.append((char) 0x7D00);
        }
        else if (character == 0x2F96B) {
            sb.append((char) 0xd857);
            sb.append((char) 0xdf86);
        }
        else if (character == 0x2F96C) {
            sb.append((char) 0x7D63);
        }
        else if (character == 0x2F96D) {
            sb.append((char) 0x4301);
        }
        else if (character == 0x2F96E) {
            sb.append((char) 0x7DC7);
        }
        else if (character == 0x2F96F) {
            sb.append((char) 0x7E02);
        }
        else if (character == 0x2F970) {
            sb.append((char) 0x7E45);
        }
        else if (character == 0x2F971) {
            sb.append((char) 0x4334);
        }
        else if (character == 0x2F972) {
            sb.append((char) 0xd858);
            sb.append((char) 0xde28);
        }
        else if (character == 0x2F973) {
            sb.append((char) 0xd858);
            sb.append((char) 0xde47);
        }
        else if (character == 0x2F974) {
            sb.append((char) 0x4359);
        }
        else if (character == 0x2F975) {
            sb.append((char) 0xd858);
            sb.append((char) 0xded9);
        }
        else if (character == 0x2F976) {
            sb.append((char) 0x7F7A);
        }
        else if (character == 0x2F977) {
            sb.append((char) 0xd858);
            sb.append((char) 0xdf3e);
        }
        else if (character == 0x2F978) {
            sb.append((char) 0x7F95);
        }
        else if (character == 0x2F979) {
            sb.append((char) 0x7FFA);
        }
        else if (character == 0x2F97A) {
            sb.append((char) 0x8005);
        }
        else if (character == 0x2F97B) {
            sb.append((char) 0xd859);
            sb.append((char) 0xdcda);
        }
        else if (character == 0x2F97C) {
            sb.append((char) 0xd859);
            sb.append((char) 0xdd23);
        }
        else if (character == 0x2F97D) {
            sb.append((char) 0x8060);
        }
        else if (character == 0x2F97E) {
            sb.append((char) 0xd859);
            sb.append((char) 0xdda8);
        }
        else if (character == 0x2F97F) {
            sb.append((char) 0x8070);
        }
        else if (character == 0x2F980) {
            sb.append((char) 0xd84c);
            sb.append((char) 0xdf5f);
        }
        else if (character == 0x2F981) {
            sb.append((char) 0x43D5);
        }
        else if (character == 0x2F982) {
            sb.append((char) 0x80B2);
        }
        else if (character == 0x2F983) {
            sb.append((char) 0x8103);
        }
        else if (character == 0x2F984) {
            sb.append((char) 0x440B);
        }
        else if (character == 0x2F985) {
            sb.append((char) 0x813E);
        }
        else if (character == 0x2F986) {
            sb.append((char) 0x5AB5);
        }
        else if (character == 0x2F987) {
            sb.append((char) 0xd859);
            sb.append((char) 0xdfa7);
        }
        else if (character == 0x2F988) {
            sb.append((char) 0xd859);
            sb.append((char) 0xdfb5);
        }
        else if (character == 0x2F989) {
            sb.append((char) 0xd84c);
            sb.append((char) 0xdf93);
        }
        else if (character == 0x2F98A) {
            sb.append((char) 0xd84c);
            sb.append((char) 0xdf9c);
        }
        else if (character == 0x2F98B) {
            sb.append((char) 0x8201);
        }
        else if (character == 0x2F98C) {
            sb.append((char) 0x8204);
        }
        else if (character == 0x2F98D) {
            sb.append((char) 0x8F9E);
        }
        else if (character == 0x2F98E) {
            sb.append((char) 0x446B);
        }
        else if (character == 0x2F98F) {
            sb.append((char) 0x8291);
        }
        else if (character == 0x2F990) {
            sb.append((char) 0x828B);
        }
        else if (character == 0x2F991) {
            sb.append((char) 0x829D);
        }
        else if (character == 0x2F992) {
            sb.append((char) 0x52B3);
        }
        else if (character == 0x2F993) {
            sb.append((char) 0x82B1);
        }
        else if (character == 0x2F994) {
            sb.append((char) 0x82B3);
        }
        else if (character == 0x2F995) {
            sb.append((char) 0x82BD);
        }
        else if (character == 0x2F996) {
            sb.append((char) 0x82E6);
        }
        else if (character == 0x2F997) {
            sb.append((char) 0xd85a);
            sb.append((char) 0xdf3c);
        }
        else if (character == 0x2F998) {
            sb.append((char) 0x82E5);
        }
        else if (character == 0x2F999) {
            sb.append((char) 0x831D);
        }
        else if (character == 0x2F99A) {
            sb.append((char) 0x8363);
        }
        else if (character == 0x2F99B) {
            sb.append((char) 0x83AD);
        }
        else if (character == 0x2F99C) {
            sb.append((char) 0x8323);
        }
        else if (character == 0x2F99D) {
            sb.append((char) 0x83BD);
        }
        else if (character == 0x2F99E) {
            sb.append((char) 0x83E7);
        }
        else if (character == 0x2F99F) {
            sb.append((char) 0x8457);
        }
        else if (character == 0x2F9A0) {
            sb.append((char) 0x8353);
        }
        else if (character == 0x2F9A1) {
            sb.append((char) 0x83CA);
        }
        else if (character == 0x2F9A2) {
            sb.append((char) 0x83CC);
        }
        else if (character == 0x2F9A3) {
            sb.append((char) 0x83DC);
        }
        else if (character == 0x2F9A4) {
            sb.append((char) 0xd85b);
            sb.append((char) 0xdc36);
        }
        else if (character == 0x2F9A5) {
            sb.append((char) 0xd85b);
            sb.append((char) 0xdd6b);
        }
        else if (character == 0x2F9A6) {
            sb.append((char) 0xd85b);
            sb.append((char) 0xdcd5);
        }
        else if (character == 0x2F9A7) {
            sb.append((char) 0x452B);
        }
        else if (character == 0x2F9A8) {
            sb.append((char) 0x84F1);
        }
        else if (character == 0x2F9A9) {
            sb.append((char) 0x84F3);
        }
        else if (character == 0x2F9AA) {
            sb.append((char) 0x8516);
        }
        else if (character == 0x2F9AB) {
            sb.append((char) 0xd85c);
            sb.append((char) 0xdfca);
        }
        else if (character == 0x2F9AC) {
            sb.append((char) 0x8564);
        }
        else if (character == 0x2F9AD) {
            sb.append((char) 0xd85b);
            sb.append((char) 0xdf2c);
        }
        else if (character == 0x2F9AE) {
            sb.append((char) 0x455D);
        }
        else if (character == 0x2F9AF) {
            sb.append((char) 0x4561);
        }
        else if (character == 0x2F9B0) {
            sb.append((char) 0xd85b);
            sb.append((char) 0xdfb1);
        }
        else if (character == 0x2F9B1) {
            sb.append((char) 0xd85c);
            sb.append((char) 0xdcd2);
        }
        else if (character == 0x2F9B2) {
            sb.append((char) 0x456B);
        }
        else if (character == 0x2F9B3) {
            sb.append((char) 0x8650);
        }
        else if (character == 0x2F9B4) {
            sb.append((char) 0x865C);
        }
        else if (character == 0x2F9B5) {
            sb.append((char) 0x8667);
        }
        else if (character == 0x2F9B6) {
            sb.append((char) 0x8669);
        }
        else if (character == 0x2F9B7) {
            sb.append((char) 0x86A9);
        }
        else if (character == 0x2F9B8) {
            sb.append((char) 0x8688);
        }
        else if (character == 0x2F9B9) {
            sb.append((char) 0x870E);
        }
        else if (character == 0x2F9BA) {
            sb.append((char) 0x86E2);
        }
        else if (character == 0x2F9BB) {
            sb.append((char) 0x8779);
        }
        else if (character == 0x2F9BC) {
            sb.append((char) 0x8728);
        }
        else if (character == 0x2F9BD) {
            sb.append((char) 0x876B);
        }
        else if (character == 0x2F9BE) {
            sb.append((char) 0x8786);
        }
        else if (character == 0x2F9BF) {
            sb.append((char) 0x45D7);
        }
        else if (character == 0x2F9C0) {
            sb.append((char) 0x87E1);
        }
        else if (character == 0x2F9C1) {
            sb.append((char) 0x8801);
        }
        else if (character == 0x2F9C2) {
            sb.append((char) 0x45F9);
        }
        else if (character == 0x2F9C3) {
            sb.append((char) 0x8860);
        }
        else if (character == 0x2F9C4) {
            sb.append((char) 0x8863);
        }
        else if (character == 0x2F9C5) {
            sb.append((char) 0xd85d);
            sb.append((char) 0xde67);
        }
        else if (character == 0x2F9C6) {
            sb.append((char) 0x88D7);
        }
        else if (character == 0x2F9C7) {
            sb.append((char) 0x88DE);
        }
        else if (character == 0x2F9C8) {
            sb.append((char) 0x4635);
        }
        else if (character == 0x2F9C9) {
            sb.append((char) 0x88FA);
        }
        else if (character == 0x2F9CA) {
            sb.append((char) 0x34BB);
        }
        else if (character == 0x2F9CB) {
            sb.append((char) 0xd85e);
            sb.append((char) 0xdcae);
        }
        else if (character == 0x2F9CC) {
            sb.append((char) 0xd85e);
            sb.append((char) 0xdd66);
        }
        else if (character == 0x2F9CD) {
            sb.append((char) 0x46BE);
        }
        else if (character == 0x2F9CE) {
            sb.append((char) 0x46C7);
        }
        else if (character == 0x2F9CF) {
            sb.append((char) 0x8AA0);
        }
        else if (character == 0x2F9D0) {
            sb.append((char) 0x8AED);
        }
        else if (character == 0x2F9D1) {
            sb.append((char) 0x8B8A);
        }
        else if (character == 0x2F9D2) {
            sb.append((char) 0x8C55);
        }
        else if (character == 0x2F9D3) {
            sb.append((char) 0xd85f);
            sb.append((char) 0xdca8);
        }
        else if (character == 0x2F9D4) {
            sb.append((char) 0x8CAB);
        }
        else if (character == 0x2F9D5) {
            sb.append((char) 0x8CC1);
        }
        else if (character == 0x2F9D6) {
            sb.append((char) 0x8D1B);
        }
        else if (character == 0x2F9D7) {
            sb.append((char) 0x8D77);
        }
        else if (character == 0x2F9D8) {
            sb.append((char) 0xd85f);
            sb.append((char) 0xdf2f);
        }
        else if (character == 0x2F9D9) {
            sb.append((char) 0xd842);
            sb.append((char) 0xdc04);
        }
        else if (character == 0x2F9DA) {
            sb.append((char) 0x8DCB);
        }
        else if (character == 0x2F9DB) {
            sb.append((char) 0x8DBC);
        }
        else if (character == 0x2F9DC) {
            sb.append((char) 0x8DF0);
        }
        else if (character == 0x2F9DD) {
            sb.append((char) 0xd842);
            sb.append((char) 0xdcde);
        }
        else if (character == 0x2F9DE) {
            sb.append((char) 0x8ED4);
        }
        else if (character == 0x2F9DF) {
            sb.append((char) 0x8F38);
        }
        else if (character == 0x2F9E0) {
            sb.append((char) 0xd861);
            sb.append((char) 0xddd2);
        }
        else if (character == 0x2F9E1) {
            sb.append((char) 0xd861);
            sb.append((char) 0xdded);
        }
        else if (character == 0x2F9E2) {
            sb.append((char) 0x9094);
        }
        else if (character == 0x2F9E3) {
            sb.append((char) 0x90F1);
        }
        else if (character == 0x2F9E4) {
            sb.append((char) 0x9111);
        }
        else if (character == 0x2F9E5) {
            sb.append((char) 0xd861);
            sb.append((char) 0xdf2e);
        }
        else if (character == 0x2F9E6) {
            sb.append((char) 0x911B);
        }
        else if (character == 0x2F9E7) {
            sb.append((char) 0x9238);
        }
        else if (character == 0x2F9E8) {
            sb.append((char) 0x92D7);
        }
        else if (character == 0x2F9E9) {
            sb.append((char) 0x92D8);
        }
        else if (character == 0x2F9EA) {
            sb.append((char) 0x927C);
        }
        else if (character == 0x2F9EB) {
            sb.append((char) 0x93F9);
        }
        else if (character == 0x2F9EC) {
            sb.append((char) 0x9415);
        }
        else if (character == 0x2F9ED) {
            sb.append((char) 0xd862);
            sb.append((char) 0xdffa);
        }
        else if (character == 0x2F9EE) {
            sb.append((char) 0x958B);
        }
        else if (character == 0x2F9EF) {
            sb.append((char) 0x4995);
        }
        else if (character == 0x2F9F0) {
            sb.append((char) 0x95B7);
        }
        else if (character == 0x2F9F1) {
            sb.append((char) 0xd863);
            sb.append((char) 0xdd77);
        }
        else if (character == 0x2F9F2) {
            sb.append((char) 0x49E6);
        }
        else if (character == 0x2F9F3) {
            sb.append((char) 0x96C3);
        }
        else if (character == 0x2F9F4) {
            sb.append((char) 0x5DB2);
        }
        else if (character == 0x2F9F5) {
            sb.append((char) 0x9723);
        }
        else if (character == 0x2F9F6) {
            sb.append((char) 0xd864);
            sb.append((char) 0xdd45);
        }
        else if (character == 0x2F9F7) {
            sb.append((char) 0xd864);
            sb.append((char) 0xde1a);
        }
        else if (character == 0x2F9F8) {
            sb.append((char) 0x4A6E);
        }
        else if (character == 0x2F9F9) {
            sb.append((char) 0x4A76);
        }
        else if (character == 0x2F9FA) {
            sb.append((char) 0x97E0);
        }
        else if (character == 0x2F9FB) {
            sb.append((char) 0xd865);
            sb.append((char) 0xdc0a);
        }
        else if (character == 0x2F9FC) {
            sb.append((char) 0x4AB2);
        }
        else if (character == 0x2F9FD) {
            sb.append((char) 0xd865);
            sb.append((char) 0xdc96);
        }
        else if (character == 0x2F9FE) {
            sb.append((char) 0x980B);
        }
        else if (character == 0x2F9FF) {
            sb.append((char) 0x980B);
        }
        else if (character == 0x2FA00) {
            sb.append((char) 0x9829);
        }
        else if (character == 0x2FA01) {
            sb.append((char) 0xd865);
            sb.append((char) 0xddb6);
        }
        else if (character == 0x2FA02) {
            sb.append((char) 0x98E2);
        }
        else if (character == 0x2FA03) {
            sb.append((char) 0x4B33);
        }
        else if (character == 0x2FA04) {
            sb.append((char) 0x9929);
        }
        else if (character == 0x2FA05) {
            sb.append((char) 0x99A7);
        }
        else if (character == 0x2FA06) {
            sb.append((char) 0x99C2);
        }
        else if (character == 0x2FA07) {
            sb.append((char) 0x99FE);
        }
        else if (character == 0x2FA08) {
            sb.append((char) 0x4BCE);
        }
        else if (character == 0x2FA09) {
            sb.append((char) 0xd866);
            sb.append((char) 0xdf30);
        }
        else if (character == 0x2FA0A) {
            sb.append((char) 0x9B12);
        }
        else if (character == 0x2FA0B) {
            sb.append((char) 0x9C40);
        }
        else if (character == 0x2FA0C) {
            sb.append((char) 0x9CFD);
        }
        else if (character == 0x2FA0D) {
            sb.append((char) 0x4CCE);
        }
        else if (character == 0x2FA0E) {
            sb.append((char) 0x4CED);
        }
        else if (character == 0x2FA0F) {
            sb.append((char) 0x9D67);
        }
        else if (character == 0x2FA10) {
            sb.append((char) 0xd868);
            sb.append((char) 0xdcce);
        }
        else if (character == 0x2FA11) {
            sb.append((char) 0x4CF8);
        }
        else if (character == 0x2FA12) {
            sb.append((char) 0xd868);
            sb.append((char) 0xdd05);
        }
        else if (character == 0x2FA13) {
            sb.append((char) 0xd868);
            sb.append((char) 0xde0e);
        }
        else if (character == 0x2FA14) {
            sb.append((char) 0xd868);
            sb.append((char) 0xde91);
        }
        else if (character == 0x2FA15) {
            sb.append((char) 0x9EBB);
        }
        else if (character == 0x2FA16) {
            sb.append((char) 0x4D56);
        }
        else if (character == 0x2FA17) {
            sb.append((char) 0x9EF9);
        }
        else if (character == 0x2FA18) {
            sb.append((char) 0x9EFE);
        }
        else if (character == 0x2FA19) {
            sb.append((char) 0x9F05);
        }
        else if (character == 0x2FA1A) {
            sb.append((char) 0x9F0F);
        }
        else if (character == 0x2FA1B) {
            sb.append((char) 0x9F16);
        }
        else if (character == 0x2FA1C) {
            sb.append((char) 0x9F3B);
        }
        else if (character == 0x2FA1D) {
            sb.append((char) 0xd869);
            sb.append((char) 0xde00);
        }
        else { // not decomposable
            if (character <= 0xFFFF) {
                sb.append((char) character);
             }
             else {
                 sb.append(getHighSurrogate(character));
                 sb.append(getLowSurrogate(character));
             }
        }
        
        return sb.toString();
        
    }    

    
    private final static int HANGUL_SYLLABLE_BEGIN = 0xAC00;
    private final static int HANGUL_SYLLABLE_END = 0xAC00;

    private static String decomposeHangul(char c) {
        
        int lBase = 0x1100;
        int vBase = 0x1161;
        int tBase = 0x11A7;
        int lCount = 19;
        int vCount = 21;
        int tCount = 28;
        int nCount = vCount * tCount; 
        int sCount = lCount * nCount;

        int sIndex = c - HANGUL_SYLLABLE_BEGIN;
        if (sIndex < 0 || sIndex >= sCount) {
            return String.valueOf(c);
        }
        
        StringBuffer result = new StringBuffer(3);
        int L = lBase + sIndex / nCount;
        int V = vBase + (sIndex % nCount) / tCount;
        int T = tBase + sIndex % tCount;
        result.append((char) L);
        result.append((char) V);
        if (T != tBase) result.append((char) T);
        return result.toString();
        
    }   
    
    
    private static class UnicodeString {
        
        int[] data;
        int length = 0; 
        
        UnicodeString(String s) {
            
            int length = s.length();
            data = new int[length];
            int index = 0;
            for (int i = 0; i < length; i++) {
                char c = s.charAt(i);
                int codePoint = c;
                if (isHighSurrogate(c)) {
                    i++;
                    codePoint = combineSurrogatePair(c, s.charAt(i));
                }
                data[index] = codePoint;
                index++;
            }
            this.length = index;
            
        }
        
        
        UnicodeString(int length) {
            this.length = 0;
            data = new int[length];
        }
        
        
        UnicodeString decompose() {
            
            UnicodeString result = new UnicodeString(length);
            for (int i = 0; i < length; i++) {
                int c = data[i];
                String d = UnicodeUtil.decompose(c);
                result.append(d);
            }    
            
            /* now put into canonical order */

            for (int i = 0; i < result.length-1; i++) {
                int first = result.data[i];
                int second = result.data[i+1];
                int secondClass = getCombiningClass(second);
                if (secondClass == 0) continue;
                int firstClass = getCombiningClass(first);
                if (firstClass > secondClass ) {
                    result.data[i] = second;
                    result.data[i+1] = first;
                    i -= 2;
                    if (i == -2) i = -1;
                }
            }
            
            return result;
            
        }
        
        
        UnicodeString compose() {
        
            if (compositions == null) loadCompositions();
            
            UnicodeString composed = new UnicodeString(length);
    
            int index = 0;
            int lastStarter = -1;
            int lastStarterIndex = -1;
            
            for (int i = 0; i < length; i++) {
                int c = data[i];
                // assuming starters are never combined with preceding characters; true????
                // Kannada vowel sign OO may be counter example
                /* if (isStarter(c) ) {
                    lastStarter = c;
                    lastStarterIndex = i;
                    composed.append(c);
                }
                else if (lastStarter == -1 || isBlocked(lastStarterIndex, i)) {
                    composed.append(c);
                }
                else  {
                    int composedChar = composeCharacter(lastStarter, c);
                    if (composedChar == -1) {
                        composed.append(c);
                    }
                    else {
                        lastStarter = composedChar;
                        // XXX dangerous side effects
                        data[lastStarterIndex] = composedChar;
                        data[i] = 0;
                        
                        composed.data[lastStarterIndex] = composedChar; 
                    }
                } */
                if (lastStarter == -1 || isBlocked(lastStarterIndex, i)) {
                    composed.append(c);
                    if (isStarter(c) ) {
                        lastStarter = c;
                        lastStarterIndex = i;
                    }
                }
                else  {
                    int composedChar = composeCharacter(lastStarter, c);
                    if (composedChar == -1) {
                        composed.append(c);
                        if (isStarter(c) ) {
                            lastStarter = c;
                            lastStarterIndex = i;
                            composed.append(c);
                        }
                    }
                    else {
                        lastStarter = composedChar;
                        // XXX dangerous side effects
                        data[lastStarterIndex] = composedChar;
                        data[i] = 0;
                        
                        composed.data[lastStarterIndex] = composedChar; 
                    }
                }
            }
            
            return composed;
        
        }

    
        void append(String s) {
            
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (isHighSurrogate(c)) {
                    append(UnicodeUtil.combineSurrogatePair(c, s.charAt(i+1)));
                    i++;
                }
                else {
                    append(c);
                }
            }
            
        }
        
        
        void append(int c) {
            
            if (length < data.length-1) {
                data[length] = c;
                length++;
            }
            else {
                int[] array = new int[data.length+10];
                System.arraycopy(data, 0, array, 0, length);
                data = array;
                append(c);
            }
            
        }
        
        public String toString() {
         
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < length; i++) {
                int c = data[i];
                if (c <= 0xFFFF) sb.append((char) c);
                else {
                    sb.append(makeSurrogatePair(c));
                }
            }
            return sb.toString();
            
        }
        
        private boolean isBlocked(int lastStarterIndex, int index) {
          
            int combiningClass = getCombiningClass(data[index]);
            for (int i = lastStarterIndex+1; i < index; i++) {
                if (data[i] !=0 && combiningClass == getCombiningClass(data[i])) {
                    return true;
                }
            }
            return false;
            
        }
        
    }
    
    
}
