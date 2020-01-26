/* Copyright 2005, 2009, 2019 Elliotte Rusty Harold
   
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

package nu.xom;

import java.io.InputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

/**
 * @author Elliotte Rusty Harold
 * @version 1.3.1
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
    private static final int CANONICAL_COMBINING_CLASS_214 = 214;
    private static final int CANONICAL_COMBINING_CLASS_ATTACHED_ABOVE_RIGHT = 216;
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

    
    private static boolean isHighSurrogate(char c) {
        return c >= HI_SURROGATE_START && c <= HI_SURROGATE_END;
    }
    
    
    private static int HI_SURROGATE_START  = 0xD800;
    private static int HI_SURROGATE_END    = 0xDBFF;
    private static int LOW_SURROGATE_START = 0xDC00;
    
    
    private static Map<String, String> compositions;
    
    private static void loadCompositions() {
    
        ClassLoader loader = Verifier.class.getClassLoader();
        if (loader != null) loadCompositions(loader);
        // If that didn't work, try a different ClassLoader
        if (compositions == null) {
            loader = Thread.currentThread().getContextClassLoader();
            loadCompositions(loader);
        }
        if (compositions == null) { 
            throw new RuntimeException("Broken XOM installation: "
              + "could not load nu/xom/compositions.dat");
        }
        
    }
    
    
    // XXX use a BiMap and make decompose much simpler?
    private static void loadCompositions(ClassLoader loader) {
        
        DataInputStream in = null;
        try {
            InputStream source = loader.getResourceAsStream("nu/xom/compositions.dat");
            in = new DataInputStream(source);
            // ???? would it make sense to store a serialized HashMap instead????
            compositions = new HashMap<String, String>();
            try {
                while (true) {
                    String composed = in.readUTF();
                    String decomposed = in.readUTF();
                    compositions.put(decomposed, composed);
                }
            }
            catch (java.io.EOFException ex) {
                // finished
            }
        }
        catch (IOException ex) {
            return;
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
    

    private static boolean isStarter(int character) {
        return getCombiningClass(character) == 0;
    }


    private static int getCombiningClass(int character) {
        // ???? optimize with table lookup?
        
        if (character <= 0x2ff) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0x314) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (character <= 0x315) return CANONICAL_COMBINING_CLASS_ABOVE_RIGHT;
        if (character <= 0x319) return CANONICAL_COMBINING_CLASS_BELOW;
        if (character <= 0x31a) return CANONICAL_COMBINING_CLASS_ABOVE_RIGHT;
        if (character <= 0x31b) return CANONICAL_COMBINING_CLASS_ATTACHED_ABOVE_RIGHT;
        if (character <= 0x320) return CANONICAL_COMBINING_CLASS_BELOW;
        if (character <= 0x322) return CANONICAL_COMBINING_CLASS_ATTACHED_BELOW;
        if (character <= 0x326) return CANONICAL_COMBINING_CLASS_BELOW;
        if (character <= 0x328) return CANONICAL_COMBINING_CLASS_ATTACHED_BELOW;
        if (character <= 0x333) return CANONICAL_COMBINING_CLASS_BELOW;
        if (character <= 0x338) return CANONICAL_COMBINING_CLASS_OVERLAY;
        if (character <= 0x33c) return CANONICAL_COMBINING_CLASS_BELOW;
        if (character <= 0x344) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (character <= 0x345) return CANONICAL_COMBINING_CLASS_IOTA_SUBSCRIPT;
        if (character <= 0x346) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (character <= 0x349) return CANONICAL_COMBINING_CLASS_BELOW;
        if (character <= 0x34c) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (character <= 0x34e) return CANONICAL_COMBINING_CLASS_BELOW;
        if (character <= 0x34f) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0x352) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (character <= 0x356) return CANONICAL_COMBINING_CLASS_BELOW;
        if (character <= 0x357) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (character <= 0x358) return CANONICAL_COMBINING_CLASS_ABOVE_RIGHT;
        if (character <= 0x35a) return CANONICAL_COMBINING_CLASS_BELOW;
        if (character <= 0x35b) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (character <= 0x35c) return CANONICAL_COMBINING_CLASS_DOUBLE_BELOW;
        if (character <= 0x35e) return CANONICAL_COMBINING_CLASS_DOUBLE_ABOVE;
        if (character <= 0x35f) return CANONICAL_COMBINING_CLASS_DOUBLE_BELOW;
        if (character <= 0x361) return CANONICAL_COMBINING_CLASS_DOUBLE_ABOVE;
        if (character <= 0x362) return CANONICAL_COMBINING_CLASS_DOUBLE_BELOW;
        if (character <= 0x36f) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (character <= 0x482) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0x487) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (character <= 0x590) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0x591) return CANONICAL_COMBINING_CLASS_BELOW;
        if (character <= 0x595) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (character <= 0x596) return CANONICAL_COMBINING_CLASS_BELOW;
        if (character <= 0x599) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (character <= 0x59a) return CANONICAL_COMBINING_CLASS_BELOW_RIGHT;
        if (character <= 0x59b) return CANONICAL_COMBINING_CLASS_BELOW;
        if (character <= 0x5a1) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (character <= 0x5a7) return CANONICAL_COMBINING_CLASS_BELOW;
        if (character <= 0x5a9) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (character <= 0x5aa) return CANONICAL_COMBINING_CLASS_BELOW;
        if (character <= 0x5ac) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (character <= 0x5ad) return CANONICAL_COMBINING_CLASS_BELOW_RIGHT;
        if (character <= 0x5ae) return CANONICAL_COMBINING_CLASS_ABOVE_LEFT;
        if (character <= 0x5af) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (character <= 0x5b0) return CANONICAL_COMBINING_CLASS_10;
        if (character <= 0x5b1) return CANONICAL_COMBINING_CLASS_11;
        if (character <= 0x5b2) return CANONICAL_COMBINING_CLASS_12;
        if (character <= 0x5b3) return CANONICAL_COMBINING_CLASS_13;
        if (character <= 0x5b4) return CANONICAL_COMBINING_CLASS_14;
        if (character <= 0x5b5) return CANONICAL_COMBINING_CLASS_15;
        if (character <= 0x5b6) return CANONICAL_COMBINING_CLASS_16;
        if (character <= 0x5b7) return CANONICAL_COMBINING_CLASS_17;
        if (character <= 0x5b8) return CANONICAL_COMBINING_CLASS_18;
        if (character <= 0x5ba) return CANONICAL_COMBINING_CLASS_19;
        if (character <= 0x5bb) return CANONICAL_COMBINING_CLASS_20;
        if (character <= 0x5bc) return CANONICAL_COMBINING_CLASS_21;
        if (character <= 0x5bd) return CANONICAL_COMBINING_CLASS_22;
        if (character <= 0x5be) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0x5bf) return CANONICAL_COMBINING_CLASS_23;
        if (character <= 0x5c0) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0x5c1) return CANONICAL_COMBINING_CLASS_24;
        if (character <= 0x5c2) return CANONICAL_COMBINING_CLASS_25;
        if (character <= 0x5c3) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0x5c4) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (character <= 0x5c5) return CANONICAL_COMBINING_CLASS_BELOW;
        if (character <= 0x5c6) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0x5c7) return CANONICAL_COMBINING_CLASS_18;
        if (character <= 0x60f) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0x617) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (character <= 0x618) return CANONICAL_COMBINING_CLASS_30;
        if (character <= 0x619) return CANONICAL_COMBINING_CLASS_31;
        if (character <= 0x61a) return CANONICAL_COMBINING_CLASS_32;
        if (character <= 0x64a) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0x64b) return CANONICAL_COMBINING_CLASS_27;
        if (character <= 0x64c) return CANONICAL_COMBINING_CLASS_28;
        if (character <= 0x64d) return CANONICAL_COMBINING_CLASS_29;
        if (character <= 0x64e) return CANONICAL_COMBINING_CLASS_30;
        if (character <= 0x64f) return CANONICAL_COMBINING_CLASS_31;
        if (character <= 0x650) return CANONICAL_COMBINING_CLASS_32;
        if (character <= 0x651) return CANONICAL_COMBINING_CLASS_33;
        if (character <= 0x652) return CANONICAL_COMBINING_CLASS_34;
        if (character <= 0x654) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (character <= 0x656) return CANONICAL_COMBINING_CLASS_BELOW;
        if (character <= 0x65b) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (character <= 0x65c) return CANONICAL_COMBINING_CLASS_BELOW;
        if (character <= 0x65e) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (character <= 0x66f) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0x670) return CANONICAL_COMBINING_CLASS_35;
        if (character <= 0x6d5) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0x6dc) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (character <= 0x6de) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0x6e2) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (character <= 0x6e3) return CANONICAL_COMBINING_CLASS_BELOW;
        if (character <= 0x6e4) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (character <= 0x6e6) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0x6e8) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (character <= 0x6e9) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0x6ea) return CANONICAL_COMBINING_CLASS_BELOW;
        if (character <= 0x6ec) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (character <= 0x6ed) return CANONICAL_COMBINING_CLASS_BELOW;
        if (character <= 0x710) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0x711) return CANONICAL_COMBINING_CLASS_36;
        if (character <= 0x72f) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0x730) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (character <= 0x731) return CANONICAL_COMBINING_CLASS_BELOW;
        if (character <= 0x733) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (character <= 0x734) return CANONICAL_COMBINING_CLASS_BELOW;
        if (character <= 0x736) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (character <= 0x739) return CANONICAL_COMBINING_CLASS_BELOW;
        if (character <= 0x73a) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (character <= 0x73c) return CANONICAL_COMBINING_CLASS_BELOW;
        if (character <= 0x73d) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (character <= 0x73e) return CANONICAL_COMBINING_CLASS_BELOW;
        if (character <= 0x741) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (character <= 0x742) return CANONICAL_COMBINING_CLASS_BELOW;
        if (character <= 0x743) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (character <= 0x744) return CANONICAL_COMBINING_CLASS_BELOW;
        if (character <= 0x745) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (character <= 0x746) return CANONICAL_COMBINING_CLASS_BELOW;
        if (character <= 0x747) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (character <= 0x748) return CANONICAL_COMBINING_CLASS_BELOW;
        if (character <= 0x74a) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (character <= 0x7ea) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0x7f1) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (character <= 0x7f2) return CANONICAL_COMBINING_CLASS_BELOW;
        if (character <= 0x7f3) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (character <= 0x93b) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0x93c) return CANONICAL_COMBINING_CLASS_NUKTA;
        if (character <= 0x94c) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0x94d) return CANONICAL_COMBINING_CLASS_VIRAMA;
        if (character <= 0x950) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0x951) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (character <= 0x952) return CANONICAL_COMBINING_CLASS_BELOW;
        if (character <= 0x954) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (character <= 0x9bb) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0x9bc) return CANONICAL_COMBINING_CLASS_NUKTA;
        if (character <= 0x9cc) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0x9cd) return CANONICAL_COMBINING_CLASS_VIRAMA;
        if (character <= 0xa3b) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0xa3c) return CANONICAL_COMBINING_CLASS_NUKTA;
        if (character <= 0xa4c) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0xa4d) return CANONICAL_COMBINING_CLASS_VIRAMA;
        if (character <= 0xabb) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0xabc) return CANONICAL_COMBINING_CLASS_NUKTA;
        if (character <= 0xacc) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0xacd) return CANONICAL_COMBINING_CLASS_VIRAMA;
        if (character <= 0xb3b) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0xb3c) return CANONICAL_COMBINING_CLASS_NUKTA;
        if (character <= 0xb4c) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0xb4d) return CANONICAL_COMBINING_CLASS_VIRAMA;
        if (character <= 0xbcc) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0xbcd) return CANONICAL_COMBINING_CLASS_VIRAMA;
        if (character <= 0xc4c) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0xc4d) return CANONICAL_COMBINING_CLASS_VIRAMA;
        if (character <= 0xc54) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0xc55) return CANONICAL_COMBINING_CLASS_84;
        if (character <= 0xc56) return CANONICAL_COMBINING_CLASS_91;
        if (character <= 0xcbb) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0xcbc) return CANONICAL_COMBINING_CLASS_NUKTA;
        if (character <= 0xccc) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0xccd) return CANONICAL_COMBINING_CLASS_VIRAMA;
        if (character <= 0xd4c) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0xd4d) return CANONICAL_COMBINING_CLASS_VIRAMA;
        if (character <= 0xdc9) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0xdca) return CANONICAL_COMBINING_CLASS_VIRAMA;
        if (character <= 0xe37) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0xe39) return CANONICAL_COMBINING_CLASS_103;
        if (character <= 0xe3a) return CANONICAL_COMBINING_CLASS_VIRAMA;
        if (character <= 0xe47) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0xe4b) return CANONICAL_COMBINING_CLASS_107;
        if (character <= 0xeb7) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0xeb9) return CANONICAL_COMBINING_CLASS_118;
        if (character <= 0xec7) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0xecb) return CANONICAL_COMBINING_CLASS_122;
        if (character <= 0xf17) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0xf19) return CANONICAL_COMBINING_CLASS_BELOW;
        if (character <= 0xf34) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0xf35) return CANONICAL_COMBINING_CLASS_BELOW;
        if (character <= 0xf36) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0xf37) return CANONICAL_COMBINING_CLASS_BELOW;
        if (character <= 0xf38) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0xf39) return CANONICAL_COMBINING_CLASS_ATTACHED_ABOVE_RIGHT;
        if (character <= 0xf70) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0xf71) return CANONICAL_COMBINING_CLASS_129;
        if (character <= 0xf72) return CANONICAL_COMBINING_CLASS_130;
        if (character <= 0xf73) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0xf74) return CANONICAL_COMBINING_CLASS_132;
        if (character <= 0xf79) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0xf7d) return CANONICAL_COMBINING_CLASS_130;
        if (character <= 0xf7f) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0xf80) return CANONICAL_COMBINING_CLASS_130;
        if (character <= 0xf81) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0xf83) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (character <= 0xf84) return CANONICAL_COMBINING_CLASS_VIRAMA;
        if (character <= 0xf85) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0xf87) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (character <= 0xfc5) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0xfc6) return CANONICAL_COMBINING_CLASS_BELOW;
        if (character <= 0x1036) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0x1037) return CANONICAL_COMBINING_CLASS_NUKTA;
        if (character <= 0x1038) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0x103a) return CANONICAL_COMBINING_CLASS_VIRAMA;
        if (character <= 0x108c) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0x108d) return CANONICAL_COMBINING_CLASS_BELOW;
        if (character <= 0x135e) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0x135f) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (character <= 0x1713) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0x1714) return CANONICAL_COMBINING_CLASS_VIRAMA;
        if (character <= 0x1733) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0x1734) return CANONICAL_COMBINING_CLASS_VIRAMA;
        if (character <= 0x17d1) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0x17d2) return CANONICAL_COMBINING_CLASS_VIRAMA;
        if (character <= 0x17dc) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0x17dd) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (character <= 0x18a8) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0x18a9) return CANONICAL_COMBINING_CLASS_ABOVE_LEFT;
        if (character <= 0x1938) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0x1939) return CANONICAL_COMBINING_CLASS_BELOW_RIGHT;
        if (character <= 0x193a) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (character <= 0x193b) return CANONICAL_COMBINING_CLASS_BELOW;
        if (character <= 0x1a16) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0x1a17) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (character <= 0x1a18) return CANONICAL_COMBINING_CLASS_BELOW;
        if (character <= 0x1b33) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0x1b34) return CANONICAL_COMBINING_CLASS_NUKTA;
        if (character <= 0x1b43) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0x1b44) return CANONICAL_COMBINING_CLASS_VIRAMA;
        if (character <= 0x1b6a) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0x1b6b) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (character <= 0x1b6c) return CANONICAL_COMBINING_CLASS_BELOW;
        if (character <= 0x1b73) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (character <= 0x1ba9) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0x1baa) return CANONICAL_COMBINING_CLASS_VIRAMA;
        if (character <= 0x1c36) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0x1c37) return CANONICAL_COMBINING_CLASS_NUKTA;
        if (character <= 0x1dbf) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0x1dc1) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (character <= 0x1dc2) return CANONICAL_COMBINING_CLASS_BELOW;
        if (character <= 0x1dc9) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (character <= 0x1dca) return CANONICAL_COMBINING_CLASS_BELOW;
        if (character <= 0x1dcc) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (character <= 0x1dcd) return CANONICAL_COMBINING_CLASS_DOUBLE_ABOVE;
        if (character <= 0x1dce) return CANONICAL_COMBINING_CLASS_214;
        if (character <= 0x1dcf) return CANONICAL_COMBINING_CLASS_BELOW;
        if (character <= 0x1dd0) return CANONICAL_COMBINING_CLASS_ATTACHED_BELOW;
        if (character <= 0x1de6) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (character <= 0x1dfd) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0x1dfe) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (character <= 0x1dff) return CANONICAL_COMBINING_CLASS_BELOW;
        if (character <= 0x20cf) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0x20d1) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (character <= 0x20d3) return CANONICAL_COMBINING_CLASS_OVERLAY;
        if (character <= 0x20d7) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (character <= 0x20da) return CANONICAL_COMBINING_CLASS_OVERLAY;
        if (character <= 0x20dc) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (character <= 0x20e0) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0x20e1) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (character <= 0x20e4) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0x20e6) return CANONICAL_COMBINING_CLASS_OVERLAY;
        if (character <= 0x20e7) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (character <= 0x20e8) return CANONICAL_COMBINING_CLASS_BELOW;
        if (character <= 0x20e9) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (character <= 0x20eb) return CANONICAL_COMBINING_CLASS_OVERLAY;
        if (character <= 0x20ef) return CANONICAL_COMBINING_CLASS_BELOW;
        if (character <= 0x20f0) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (character <= 0x2ddf) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0x2dff) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (character <= 0x3029) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0x302a) return CANONICAL_COMBINING_CLASS_BELOW_LEFT;
        if (character <= 0x302b) return CANONICAL_COMBINING_CLASS_ABOVE_LEFT;
        if (character <= 0x302c) return CANONICAL_COMBINING_CLASS_ABOVE_RIGHT;
        if (character <= 0x302d) return CANONICAL_COMBINING_CLASS_BELOW_RIGHT;
        if (character <= 0x302f) return CANONICAL_COMBINING_CLASS_LEFT;
        if (character <= 0x3098) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0x309a) return CANONICAL_COMBINING_CLASS_KANA_VOICING;
        if (character <= 0xa66e) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0xa66f) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (character <= 0xa67b) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0xa67d) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (character <= 0xa805) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0xa806) return CANONICAL_COMBINING_CLASS_VIRAMA;
        if (character <= 0xa8c3) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0xa8c4) return CANONICAL_COMBINING_CLASS_VIRAMA;
        if (character <= 0xa92a) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0xa92d) return CANONICAL_COMBINING_CLASS_BELOW;
        if (character <= 0xa952) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0xa953) return CANONICAL_COMBINING_CLASS_VIRAMA;
        if (character <= 0xfb1d) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0xfb1e) return CANONICAL_COMBINING_CLASS_26;
        if (character <= 0xfe1f) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0xfe26) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (character <= 0x101fc) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0x101fd) return CANONICAL_COMBINING_CLASS_BELOW;
        if (character <= 0x10a0c) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0x10a0d) return CANONICAL_COMBINING_CLASS_BELOW;
        if (character <= 0x10a0e) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0x10a0f) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (character <= 0x10a37) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0x10a38) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (character <= 0x10a39) return CANONICAL_COMBINING_CLASS_OVERLAY;
        if (character <= 0x10a3a) return CANONICAL_COMBINING_CLASS_BELOW;
        if (character <= 0x10a3e) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0x10a3f) return CANONICAL_COMBINING_CLASS_VIRAMA;
        if (character <= 0x1d164) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0x1d166) return CANONICAL_COMBINING_CLASS_ATTACHED_ABOVE_RIGHT;
        if (character <= 0x1d169) return CANONICAL_COMBINING_CLASS_OVERLAY;
        if (character <= 0x1d16c) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0x1d16d) return CANONICAL_COMBINING_CLASS_RIGHT;
        if (character <= 0x1d172) return CANONICAL_COMBINING_CLASS_ATTACHED_ABOVE_RIGHT;
        if (character <= 0x1d17a) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0x1d182) return CANONICAL_COMBINING_CLASS_BELOW;
        if (character <= 0x1d184) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0x1d189) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (character <= 0x1d18b) return CANONICAL_COMBINING_CLASS_BELOW;
        if (character <= 0x1d1a9) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0x1d1ad) return CANONICAL_COMBINING_CLASS_ABOVE;
        if (character <= 0x1d241) return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
        if (character <= 0x1d244) return CANONICAL_COMBINING_CLASS_ABOVE;
  
        
        return CANONICAL_COMBINING_CLASS_NOT_REORDERED;
    }
    
    
    private final static int FIRST_HANGUL_SYLLABLE    = 0xAC00;
    // even if this is not right; why do tests still pass????
    // private final static int LAST_HANGUL_SYLLABLE     = 0xAC00; // FIXME 
    private final static int LAST_HANGUL_SYLLABLE = 0xD7A3;

    
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

    private static char getHighSurrogate(int codepoint) {

        char x = (char) codepoint;
        int u = (codepoint >> 16) & ((1<<5) - 1);
        char w = (char) (u - 1);
        return (char) (HI_SURROGATE_START | (w << 6) | x >> 10);
        
    }


    private static char getLowSurrogate(int codepoint) {

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
            
            // ???? unnecessarily invoking this in many cases
            s = decomposeHangul(s);
            UnicodeString ustring = new UnicodeString(s);
            UnicodeString decomposed = ustring.decompose(); 
            UnicodeString recomposed = decomposed.compose();
            String result = recomposed.toString();
            // ???? unnecessarily invoking this in many cases
            result = composeHangul(result);
            return result;
        }
        
        return s;
        
    }

     
    private static String decomposeHangul(String s) {

        int length = s.length();
        StringBuffer sb = new StringBuffer(s.length());
        for (int i = 0; i < length; i++) {
            char c = s.charAt(i);
            if (c >= FIRST_HANGUL_SYLLABLE && c <= LAST_HANGUL_SYLLABLE) {
                sb.append(decomposeHangul(c));
            }
            else {
                sb.append(c);
            }
        }
        return sb.toString();
        
    }

    
    // return -1 if the character cannot be combined with the starter; 
    // otherwise return the composed character
    private static int composeCharacter(int starter, int c) {
        
        StringBuffer decomposed = new StringBuffer(4);
        
        if (starter > 0xFFFF) {
            decomposed.append(getHighSurrogate(starter));
            decomposed.append(getLowSurrogate(starter));
        }
        else decomposed.append((char) starter);
        
        if (c > 0xFFFF) {
            decomposed.append(getHighSurrogate(c));
            decomposed.append(getLowSurrogate(c));
        }
        else decomposed.append((char) c);
        
        String recomposed = (String) compositions.get(decomposed.toString());
        if (recomposed == null) return -1;
        else if (recomposed.length() == 1) return recomposed.charAt(0);
        else return combineSurrogatePair(recomposed.charAt(0), recomposed.charAt(1));

    }

    
    // FIXME must recurse this
    ///CLOVER:OFF
    private static String decompose(int character) {
        
        if (character < 0x00C0) {
            return String.valueOf((char) character);
        }
        else if (character >= FIRST_HANGUL_SYLLABLE && character <= LAST_HANGUL_SYLLABLE) {
            return decomposeHangul((char) character);
        }
        
        // XXX use lookup table prepopulated with large string at first use
        switch (character) {
        case 0x00C0:
            return "\u0041\u0300";
        case 0x00C1:
            return "\u0041\u0301";
        case 0x00C2:
            return "\u0041\u0302";
        case 0x00C3:
            return "\u0041\u0303";
        case 0x00C4:
            return "\u0041\u0308";
        case 0x00C5:
            return "\u0041\u030A";
        case 0x00C7:
            return "\u0043\u0327";
        case 0x00C8:
            return "\u0045\u0300";
        case 0x00C9:
            return "\u0045\u0301";
        case 0x00CA:
            return "\u0045\u0302";
        case 0x00CB:
            return "\u0045\u0308";
        case 0x00CC:
            return "\u0049\u0300";
        case 0x00CD:
            return "\u0049\u0301";
        case 0x00CE:
            return "\u0049\u0302";
        case 0x00CF:
            return "\u0049\u0308";
        case 0x00D1:
            return "\u004E\u0303";
        case 0x00D2:
            return "\u004F\u0300";
        case 0x00D3:
            return "\u004F\u0301";
        case 0x00D4:
            return "\u004F\u0302";
        case 0x00D5:
            return "\u004F\u0303";
        case 0x00D6:
            return "\u004F\u0308";
        case 0x00D9:
            return "\u0055\u0300";
        case 0x00DA:
            return "\u0055\u0301";
        case 0x00DB:
            return "\u0055\u0302";
        case 0x00DC:
            return "\u0055\u0308";
        case 0x00DD:
            return "\u0059\u0301";
        case 0x00E0:
            return "\u0061\u0300";
        case 0x00E1:
            return "\u0061\u0301";
        case 0x00E2:
            return "\u0061\u0302";
        case 0x00E3:
            return "\u0061\u0303";
        case 0x00E4:
            return "\u0061\u0308";
        case 0x00E5:
            return "\u0061\u030A";
        case 0x00E7:
            return "\u0063\u0327";
        case 0x00E8:
            return "\u0065\u0300";
        case 0x00E9:
            return "\u0065\u0301";
        case 0x00EA:
            return "\u0065\u0302";
        case 0x00EB:
            return "\u0065\u0308";
        case 0x00EC:
            return "\u0069\u0300";
        case 0x00ED:
            return "\u0069\u0301";
        case 0x00EE:
            return "\u0069\u0302";
        case 0x00EF:
            return "\u0069\u0308";
        case 0x00F1:
            return "\u006E\u0303";
        case 0x00F2:
            return "\u006F\u0300";
        case 0x00F3:
            return "\u006F\u0301";
        case 0x00F4:
            return "\u006F\u0302";
        case 0x00F5:
            return "\u006F\u0303";
        case 0x00F6:
            return "\u006F\u0308";
        case 0x00F9:
            return "\u0075\u0300";
        case 0x00FA:
            return "\u0075\u0301";
        case 0x00FB:
            return "\u0075\u0302";
        case 0x00FC:
            return "\u0075\u0308";
        case 0x00FD:
            return "\u0079\u0301";
        case 0x00FF:
            return "\u0079\u0308";
        case 0x0100:
            return "\u0041\u0304";
        case 0x0101:
            return "\u0061\u0304";
        case 0x0102:
            return "\u0041\u0306";
        case 0x0103:
            return "\u0061\u0306";
        case 0x0104:
            return "\u0041\u0328";
        case 0x0105:
            return "\u0061\u0328";
        case 0x0106:
            return "\u0043\u0301";
        case 0x0107:
            return "\u0063\u0301";
        case 0x0108:
            return "\u0043\u0302";
        case 0x0109:
            return "\u0063\u0302";
        case 0x010A:
            return "\u0043\u0307";
        case 0x010B:
            return "\u0063\u0307";
        case 0x010C:
            return "\u0043\u030C";
        case 0x010D:
            return "\u0063\u030C";
        case 0x010E:
            return "\u0044\u030C";
        case 0x010F:
            return "\u0064\u030C";
        case 0x0112:
            return "\u0045\u0304";
        case 0x0113:
            return "\u0065\u0304";
        case 0x0114:
            return "\u0045\u0306";
        case 0x0115:
            return "\u0065\u0306";
        case 0x0116:
            return "\u0045\u0307";
        case 0x0117:
            return "\u0065\u0307";
        case 0x0118:
            return "\u0045\u0328";
        case 0x0119:
            return "\u0065\u0328";
        case 0x011A:
            return "\u0045\u030C";
        case 0x011B:
            return "\u0065\u030C";
        case 0x011C:
            return "\u0047\u0302";
        case 0x011D:
            return "\u0067\u0302";
        case 0x011E:
            return "\u0047\u0306";
        case 0x011F:
            return "\u0067\u0306";
        case 0x0120:
            return "\u0047\u0307";
        case 0x0121:
            return "\u0067\u0307";
        case 0x0122:
            return "\u0047\u0327";
        case 0x0123:
            return "\u0067\u0327";
        case 0x0124:
            return "\u0048\u0302";
        case 0x0125:
            return "\u0068\u0302";
        case 0x0128:
            return "\u0049\u0303";
        case 0x0129:
            return "\u0069\u0303";
        case 0x012A:
            return "\u0049\u0304";
        case 0x012B:
            return "\u0069\u0304";
        case 0x012C:
            return "\u0049\u0306";
        case 0x012D:
            return "\u0069\u0306";
        case 0x012E:
            return "\u0049\u0328";
        case 0x012F:
            return "\u0069\u0328";
        case 0x0130:
            return "\u0049\u0307";
        case 0x0134:
            return "\u004A\u0302";
        case 0x0135:
            return "\u006A\u0302";
        case 0x0136:
            return "\u004B\u0327";
        case 0x0137:
            return "\u006B\u0327";
        case 0x0139:
            return "\u004C\u0301";
        case 0x013A:
            return "\u006C\u0301";
        case 0x013B:
            return "\u004C\u0327";
        case 0x013C:
            return "\u006C\u0327";
        case 0x013D:
            return "\u004C\u030C";
        case 0x013E:
            return "\u006C\u030C";
        case 0x0143:
            return "\u004E\u0301";
        case 0x0144:
            return "\u006E\u0301";
        case 0x0145:
            return "\u004E\u0327";
        case 0x0146:
            return "\u006E\u0327";
        case 0x0147:
            return "\u004E\u030C";
        case 0x0148:
            return "\u006E\u030C";
        case 0x014C:
            return "\u004F\u0304";
        case 0x014D:
            return "\u006F\u0304";
        case 0x014E:
            return "\u004F\u0306";
        case 0x014F:
            return "\u006F\u0306";
        case 0x0150:
            return "\u004F\u030B";
        case 0x0151:
            return "\u006F\u030B";
        case 0x0154:
            return "\u0052\u0301";
        case 0x0155:
            return "\u0072\u0301";
        case 0x0156:
            return "\u0052\u0327";
        case 0x0157:
            return "\u0072\u0327";
        case 0x0158:
            return "\u0052\u030C";
        case 0x0159:
            return "\u0072\u030C";
        case 0x015A:
            return "\u0053\u0301";
        case 0x015B:
            return "\u0073\u0301";
        case 0x015C:
            return "\u0053\u0302";
        case 0x015D:
            return "\u0073\u0302";
        case 0x015E:
            return "\u0053\u0327";
        case 0x015F:
            return "\u0073\u0327";
        case 0x0160:
            return "\u0053\u030C";
        case 0x0161:
            return "\u0073\u030C";
        case 0x0162:
            return "\u0054\u0327";
        case 0x0163:
            return "\u0074\u0327";
        case 0x0164:
            return "\u0054\u030C";
        case 0x0165:
            return "\u0074\u030C";
        case 0x0168:
            return "\u0055\u0303";
        case 0x0169:
            return "\u0075\u0303";
        case 0x016A:
            return "\u0055\u0304";
        case 0x016B:
            return "\u0075\u0304";
        case 0x016C:
            return "\u0055\u0306";
        case 0x016D:
            return "\u0075\u0306";
        case 0x016E:
            return "\u0055\u030A";
        case 0x016F:
            return "\u0075\u030A";
        case 0x0170:
            return "\u0055\u030B";
        case 0x0171:
            return "\u0075\u030B";
        case 0x0172:
            return "\u0055\u0328";
        case 0x0173:
            return "\u0075\u0328";
        case 0x0174:
            return "\u0057\u0302";
        case 0x0175:
            return "\u0077\u0302";
        case 0x0176:
            return "\u0059\u0302";
        case 0x0177:
            return "\u0079\u0302";
        case 0x0178:
            return "\u0059\u0308";
        case 0x0179:
            return "\u005A\u0301";
        case 0x017A:
            return "\u007A\u0301";
        case 0x017B:
            return "\u005A\u0307";
        case 0x017C:
            return "\u007A\u0307";
        case 0x017D:
            return "\u005A\u030C";
        case 0x017E:
            return "\u007A\u030C";
        case 0x01A0:
            return "\u004F\u031B";
        case 0x01A1:
            return "\u006F\u031B";
        case 0x01AF:
            return "\u0055\u031B";
        case 0x01B0:
            return "\u0075\u031B";
        case 0x01CD:
            return "\u0041\u030C";
        case 0x01CE:
            return "\u0061\u030C";
        case 0x01CF:
            return "\u0049\u030C";
        case 0x01D0:
            return "\u0069\u030C";
        case 0x01D1:
            return "\u004F\u030C";
        case 0x01D2:
            return "\u006F\u030C";
        case 0x01D3:
            return "\u0055\u030C";
        case 0x01D4:
            return "\u0075\u030C";
        case 0x01D5:
            return "\u00DC\u0304";
        case 0x01D6:
            return "\u00FC\u0304";
        case 0x01D7:
            return "\u00DC\u0301";
        case 0x01D8:
            return "\u00FC\u0301";
        case 0x01D9:
            return "\u00DC\u030C";
        case 0x01DA:
            return "\u00FC\u030C";
        case 0x01DB:
            return "\u00DC\u0300";
        case 0x01DC:
            return "\u00FC\u0300";
        case 0x01DE:
            return "\u00C4\u0304";
        case 0x01DF:
            return "\u00E4\u0304";
        case 0x01E0:
            return "\u0226\u0304";
        case 0x01E1:
            return "\u0227\u0304";
        case 0x01E2:
            return "\u00C6\u0304";
        case 0x01E3:
            return "\u00E6\u0304";
        case 0x01E6:
            return "\u0047\u030C";
        case 0x01E7:
            return "\u0067\u030C";
        case 0x01E8:
            return "\u004B\u030C";
        case 0x01E9:
            return "\u006B\u030C";
        case 0x01EA:
            return "\u004F\u0328";
        case 0x01EB:
            return "\u006F\u0328";
        case 0x01EC:
            return "\u01EA\u0304";
        case 0x01ED:
            return "\u01EB\u0304";
        case 0x01EE:
            return "\u01B7\u030C";
        case 0x01EF:
            return "\u0292\u030C";
        case 0x01F0:
            return "\u006A\u030C";
        case 0x01F4:
            return "\u0047\u0301";
        case 0x01F5:
            return "\u0067\u0301";
        case 0x01F8:
            return "\u004E\u0300";
        case 0x01F9:
            return "\u006E\u0300";
        case 0x01FA:
            return "\u00C5\u0301";
        case 0x01FB:
            return "\u00E5\u0301";
        case 0x01FC:
            return "\u00C6\u0301";
        case 0x01FD:
            return "\u00E6\u0301";
        case 0x01FE:
            return "\u00D8\u0301";
        case 0x01FF:
            return "\u00F8\u0301";
        case 0x0200:
            return "\u0041\u030F";
        case 0x0201:
            return "\u0061\u030F";
        case 0x0202:
            return "\u0041\u0311";
        case 0x0203:
            return "\u0061\u0311";
        case 0x0204:
            return "\u0045\u030F";
        case 0x0205:
            return "\u0065\u030F";
        case 0x0206:
            return "\u0045\u0311";
        case 0x0207:
            return "\u0065\u0311";
        case 0x0208:
            return "\u0049\u030F";
        case 0x0209:
            return "\u0069\u030F";
        case 0x020A:
            return "\u0049\u0311";
        case 0x020B:
            return "\u0069\u0311";
        case 0x020C:
            return "\u004F\u030F";
        case 0x020D:
            return "\u006F\u030F";
        case 0x020E:
            return "\u004F\u0311";
        case 0x020F:
            return "\u006F\u0311";
        case 0x0210:
            return "\u0052\u030F";
        case 0x0211:
            return "\u0072\u030F";
        case 0x0212:
            return "\u0052\u0311";
        case 0x0213:
            return "\u0072\u0311";
        case 0x0214:
            return "\u0055\u030F";
        case 0x0215:
            return "\u0075\u030F";
        case 0x0216:
            return "\u0055\u0311";
        case 0x0217:
            return "\u0075\u0311";
        case 0x0218:
            return "\u0053\u0326";
        case 0x0219:
            return "\u0073\u0326";
        case 0x021A:
            return "\u0054\u0326";
        case 0x021B:
            return "\u0074\u0326";
        case 0x021E:
            return "\u0048\u030C";
        case 0x021F:
            return "\u0068\u030C";
        case 0x0226:
            return "\u0041\u0307";
        case 0x0227:
            return "\u0061\u0307";
        case 0x0228:
            return "\u0045\u0327";
        case 0x0229:
            return "\u0065\u0327";
        case 0x022A:
            return "\u00D6\u0304";
        case 0x022B:
            return "\u00F6\u0304";
        case 0x022C:
            return "\u00D5\u0304";
        case 0x022D:
            return "\u00F5\u0304";
        case 0x022E:
            return "\u004F\u0307";
        case 0x022F:
            return "\u006F\u0307";
        case 0x0230:
            return "\u022E\u0304";
        case 0x0231:
            return "\u022F\u0304";
        case 0x0232:
            return "\u0059\u0304";
        case 0x0233:
            return "\u0079\u0304";
        case 0x0340:
            return "\u0300";
        case 0x0341:
            return "\u0301";
        case 0x0343:
            return "\u0313";
        case 0x0344:
            return "\u0308\u0301";
        case 0x0374:
            return "\u02B9";
        case 0x037E:
            return "\u003B";
        case 0x0385:
            return "\u00A8\u0301";
        case 0x0386:
            return "\u0391\u0301";
        case 0x0387:
            return "\u00B7";
        case 0x0388:
            return "\u0395\u0301";
        case 0x0389:
            return "\u0397\u0301";
        case 0x038A:
            return "\u0399\u0301";
        case 0x038C:
            return "\u039F\u0301";
        case 0x038E:
            return "\u03A5\u0301";
        case 0x038F:
            return "\u03A9\u0301";
        case 0x0390:
            return "\u03CA\u0301";
        case 0x03AA:
            return "\u0399\u0308";
        case 0x03AB:
            return "\u03A5\u0308";
        case 0x03AC:
            return "\u03B1\u0301";
        case 0x03AD:
            return "\u03B5\u0301";
        case 0x03AE:
            return "\u03B7\u0301";
        case 0x03AF:
            return "\u03B9\u0301";
        case 0x03B0:
            return "\u03CB\u0301";
        case 0x03CA:
            return "\u03B9\u0308";
        case 0x03CB:
            return "\u03C5\u0308";
        case 0x03CC:
            return "\u03BF\u0301";
        case 0x03CD:
            return "\u03C5\u0301";
        case 0x03CE:
            return "\u03C9\u0301";
        case 0x03D3:
            return "\u03D2\u0301";
        case 0x03D4:
            return "\u03D2\u0308";
        case 0x0400:
            return "\u0415\u0300";
        case 0x0401:
            return "\u0415\u0308";
        case 0x0403:
            return "\u0413\u0301";
        case 0x0407:
            return "\u0406\u0308";
        case 0x040C:
            return "\u041A\u0301";
        case 0x040D:
            return "\u0418\u0300";
        case 0x040E:
            return "\u0423\u0306";
        case 0x0419:
            return "\u0418\u0306";
        case 0x0439:
            return "\u0438\u0306";
        case 0x0450:
            return "\u0435\u0300";
        case 0x0451:
            return "\u0435\u0308";
        case 0x0453:
            return "\u0433\u0301";
        case 0x0457:
            return "\u0456\u0308";
        case 0x045C:
            return "\u043A\u0301";
        case 0x045D:
            return "\u0438\u0300";
        case 0x045E:
            return "\u0443\u0306";
        case 0x0476:
            return "\u0474\u030F";
        case 0x0477:
            return "\u0475\u030F";
        case 0x04C1:
            return "\u0416\u0306";
        case 0x04C2:
            return "\u0436\u0306";
        case 0x04D0:
            return "\u0410\u0306";
        case 0x04D1:
            return "\u0430\u0306";
        case 0x04D2:
            return "\u0410\u0308";
        case 0x04D3:
            return "\u0430\u0308";
        case 0x04D6:
            return "\u0415\u0306";
        case 0x04D7:
            return "\u0435\u0306";
        case 0x04DA:
            return "\u04D8\u0308";
        case 0x04DB:
            return "\u04D9\u0308";
        case 0x04DC:
            return "\u0416\u0308";
        case 0x04DD:
            return "\u0436\u0308";
        case 0x04DE:
            return "\u0417\u0308";
        case 0x04DF:
            return "\u0437\u0308";
        case 0x04E2:
            return "\u0418\u0304";
        case 0x04E3:
            return "\u0438\u0304";
        case 0x04E4:
            return "\u0418\u0308";
        case 0x04E5:
            return "\u0438\u0308";
        case 0x04E6:
            return "\u041E\u0308";
        case 0x04E7:
            return "\u043E\u0308";
        case 0x04EA:
            return "\u04E8\u0308";
        case 0x04EB:
            return "\u04E9\u0308";
        case 0x04EC:
            return "\u042D\u0308";
        case 0x04ED:
            return "\u044D\u0308";
        case 0x04EE:
            return "\u0423\u0304";
        case 0x04EF:
            return "\u0443\u0304";
        case 0x04F0:
            return "\u0423\u0308";
        case 0x04F1:
            return "\u0443\u0308";
        case 0x04F2:
            return "\u0423\u030B";
        case 0x04F3:
            return "\u0443\u030B";
        case 0x04F4:
            return "\u0427\u0308";
        case 0x04F5:
            return "\u0447\u0308";
        case 0x04F8:
            return "\u042B\u0308";
        case 0x04F9:
            return "\u044B\u0308";
        case 0x0622:
            return "\u0627\u0653";
        case 0x0623:
            return "\u0627\u0654";
        case 0x0624:
            return "\u0648\u0654";
        case 0x0625:
            return "\u0627\u0655";
        case 0x0626:
            return "\u064A\u0654";
        case 0x06C0:
            return "\u06D5\u0654";
        case 0x06C2:
            return "\u06C1\u0654";
        case 0x06D3:
            return "\u06D2\u0654";
        case 0x0929:
            return "\u0928\u093C";
        case 0x0931:
            return "\u0930\u093C";
        case 0x0934:
            return "\u0933\u093C";
        case 0x0958:
            return "\u0915\u093C";
        case 0x0959:
            return "\u0916\u093C";
        case 0x095A:
            return "\u0917\u093C";
        case 0x095B:
            return "\u091C\u093C";
        case 0x095C:
            return "\u0921\u093C";
        case 0x095D:
            return "\u0922\u093C";
        case 0x095E:
            return "\u092B\u093C";
        case 0x095F:
            return "\u092F\u093C";
        case 0x09CB:
            return "\u09C7\u09BE";
        case 0x09CC:
            return "\u09C7\u09D7";
        case 0x09DC:
            return "\u09A1\u09BC";
        case 0x09DD:
            return "\u09A2\u09BC";
        case 0x09DF:
            return "\u09AF\u09BC";
        case 0x0A33:
            return "\u0A32\u0A3C";
        case 0x0A36:
            return "\u0A38\u0A3C";
        case 0x0A59:
            return "\u0A16\u0A3C";
        case 0x0A5A:
            return "\u0A17\u0A3C";
        case 0x0A5B:
            return "\u0A1C\u0A3C";
        case 0x0A5E:
            return "\u0A2B\u0A3C";
        case 0x0B48:
            return "\u0B47\u0B56";
        case 0x0B4B:
            return "\u0B47\u0B3E";
        case 0x0B4C:
            return "\u0B47\u0B57";
        case 0x0B5C:
            return "\u0B21\u0B3C";
        case 0x0B5D:
            return "\u0B22\u0B3C";
        case 0x0B94:
            return "\u0B92\u0BD7";
        case 0x0BCA:
            return "\u0BC6\u0BBE";
        case 0x0BCB:
            return "\u0BC7\u0BBE";
        case 0x0BCC:
            return "\u0BC6\u0BD7";
        case 0x0C48:
            return "\u0C46\u0C56";
        case 0x0CC0:
            return "\u0CBF\u0CD5";
        case 0x0CC7:
            return "\u0CC6\u0CD5";
        case 0x0CC8:
            return "\u0CC6\u0CD6";
        case 0x0CCA:
            return "\u0CC6\u0CC2";
        case 0x0CCB:
            return "\u0CCA\u0CD5";
        case 0x0D4A:
            return "\u0D46\u0D3E";
        case 0x0D4B:
            return "\u0D47\u0D3E";
        case 0x0D4C:
            return "\u0D46\u0D57";
        case 0x0DDA:
            return "\u0DD9\u0DCA";
        case 0x0DDC:
            return "\u0DD9\u0DCF";
        case 0x0DDD:
            return "\u0DDC\u0DCA";
        case 0x0DDE:
            return "\u0DD9\u0DDF";
        case 0x0F43:
            return "\u0F42\u0FB7";
        case 0x0F4D:
            return "\u0F4C\u0FB7";
        case 0x0F52:
            return "\u0F51\u0FB7";
        case 0x0F57:
            return "\u0F56\u0FB7";
        case 0x0F5C:
            return "\u0F5B\u0FB7";
        case 0x0F69:
            return "\u0F40\u0FB5";
        case 0x0F73:
            return "\u0F71\u0F72";
        case 0x0F75:
            return "\u0F71\u0F74";
        case 0x0F76:
            return "\u0FB2\u0F80";
        case 0x0F78:
            return "\u0FB3\u0F80";
        case 0x0F81:
            return "\u0F71\u0F80";
        case 0x0F93:
            return "\u0F92\u0FB7";
        case 0x0F9D:
            return "\u0F9C\u0FB7";
        case 0x0FA2:
            return "\u0FA1\u0FB7";
        case 0x0FA7:
            return "\u0FA6\u0FB7";
        case 0x0FAC:
            return "\u0FAB\u0FB7";
        case 0x0FB9:
            return "\u0F90\u0FB5";
        case 0x1026:
            return "\u1025\u102E";
        case 0x1B06:
            return "\u1B05\u1B35";
        case 0x1B08:
            return "\u1B07\u1B35";
        case 0x1B0A:
            return "\u1B09\u1B35";
        case 0x1B0C:
            return "\u1B0B\u1B35";
        case 0x1B0E:
            return "\u1B0D\u1B35";
        case 0x1B12:
            return "\u1B11\u1B35";
        case 0x1B3B:
            return "\u1B3A\u1B35";
        case 0x1B3D:
            return "\u1B3C\u1B35";
        case 0x1B40:
            return "\u1B3E\u1B35";
        case 0x1B41:
            return "\u1B3F\u1B35";
        case 0x1B43:
            return "\u1B42\u1B35";
        case 0x1E00:
            return "\u0041\u0325";
        case 0x1E01:
            return "\u0061\u0325";
        case 0x1E02:
            return "\u0042\u0307";
        case 0x1E03:
            return "\u0062\u0307";
        case 0x1E04:
            return "\u0042\u0323";
        case 0x1E05:
            return "\u0062\u0323";
        case 0x1E06:
            return "\u0042\u0331";
        case 0x1E07:
            return "\u0062\u0331";
        case 0x1E08:
            return "\u00C7\u0301";
        case 0x1E09:
            return "\u00E7\u0301";
        case 0x1E0A:
            return "\u0044\u0307";
        case 0x1E0B:
            return "\u0064\u0307";
        case 0x1E0C:
            return "\u0044\u0323";
        case 0x1E0D:
            return "\u0064\u0323";
        case 0x1E0E:
            return "\u0044\u0331";
        case 0x1E0F:
            return "\u0064\u0331";
        case 0x1E10:
            return "\u0044\u0327";
        case 0x1E11:
            return "\u0064\u0327";
        case 0x1E12:
            return "\u0044\u032D";
        case 0x1E13:
            return "\u0064\u032D";
        case 0x1E14:
            return "\u0112\u0300";
        case 0x1E15:
            return "\u0113\u0300";
        case 0x1E16:
            return "\u0112\u0301";
        case 0x1E17:
            return "\u0113\u0301";
        case 0x1E18:
            return "\u0045\u032D";
        case 0x1E19:
            return "\u0065\u032D";
        case 0x1E1A:
            return "\u0045\u0330";
        case 0x1E1B:
            return "\u0065\u0330";
        case 0x1E1C:
            return "\u0228\u0306";
        case 0x1E1D:
            return "\u0229\u0306";
        case 0x1E1E:
            return "\u0046\u0307";
        case 0x1E1F:
            return "\u0066\u0307";
        case 0x1E20:
            return "\u0047\u0304";
        case 0x1E21:
            return "\u0067\u0304";
        case 0x1E22:
            return "\u0048\u0307";
        case 0x1E23:
            return "\u0068\u0307";
        case 0x1E24:
            return "\u0048\u0323";
        case 0x1E25:
            return "\u0068\u0323";
        case 0x1E26:
            return "\u0048\u0308";
        case 0x1E27:
            return "\u0068\u0308";
        case 0x1E28:
            return "\u0048\u0327";
        case 0x1E29:
            return "\u0068\u0327";
        case 0x1E2A:
            return "\u0048\u032E";
        case 0x1E2B:
            return "\u0068\u032E";
        case 0x1E2C:
            return "\u0049\u0330";
        case 0x1E2D:
            return "\u0069\u0330";
        case 0x1E2E:
            return "\u00CF\u0301";
        case 0x1E2F:
            return "\u00EF\u0301";
        case 0x1E30:
            return "\u004B\u0301";
        case 0x1E31:
            return "\u006B\u0301";
        case 0x1E32:
            return "\u004B\u0323";
        case 0x1E33:
            return "\u006B\u0323";
        case 0x1E34:
            return "\u004B\u0331";
        case 0x1E35:
            return "\u006B\u0331";
        case 0x1E36:
            return "\u004C\u0323";
        case 0x1E37:
            return "\u006C\u0323";
        case 0x1E38:
            return "\u1E36\u0304";
        case 0x1E39:
            return "\u1E37\u0304";
        case 0x1E3A:
            return "\u004C\u0331";
        case 0x1E3B:
            return "\u006C\u0331";
        case 0x1E3C:
            return "\u004C\u032D";
        case 0x1E3D:
            return "\u006C\u032D";
        case 0x1E3E:
            return "\u004D\u0301";
        case 0x1E3F:
            return "\u006D\u0301";
        case 0x1E40:
            return "\u004D\u0307";
        case 0x1E41:
            return "\u006D\u0307";
        case 0x1E42:
            return "\u004D\u0323";
        case 0x1E43:
            return "\u006D\u0323";
        case 0x1E44:
            return "\u004E\u0307";
        case 0x1E45:
            return "\u006E\u0307";
        case 0x1E46:
            return "\u004E\u0323";
        case 0x1E47:
            return "\u006E\u0323";
        case 0x1E48:
            return "\u004E\u0331";
        case 0x1E49:
            return "\u006E\u0331";
        case 0x1E4A:
            return "\u004E\u032D";
        case 0x1E4B:
            return "\u006E\u032D";
        case 0x1E4C:
            return "\u00D5\u0301";
        case 0x1E4D:
            return "\u00F5\u0301";
        case 0x1E4E:
            return "\u00D5\u0308";
        case 0x1E4F:
            return "\u00F5\u0308";
        case 0x1E50:
            return "\u014C\u0300";
        case 0x1E51:
            return "\u014D\u0300";
        case 0x1E52:
            return "\u014C\u0301";
        case 0x1E53:
            return "\u014D\u0301";
        case 0x1E54:
            return "\u0050\u0301";
        case 0x1E55:
            return "\u0070\u0301";
        case 0x1E56:
            return "\u0050\u0307";
        case 0x1E57:
            return "\u0070\u0307";
        case 0x1E58:
            return "\u0052\u0307";
        case 0x1E59:
            return "\u0072\u0307";
        case 0x1E5A:
            return "\u0052\u0323";
        case 0x1E5B:
            return "\u0072\u0323";
        case 0x1E5C:
            return "\u1E5A\u0304";
        case 0x1E5D:
            return "\u1E5B\u0304";
        case 0x1E5E:
            return "\u0052\u0331";
        case 0x1E5F:
            return "\u0072\u0331";
        case 0x1E60:
            return "\u0053\u0307";
        case 0x1E61:
            return "\u0073\u0307";
        case 0x1E62:
            return "\u0053\u0323";
        case 0x1E63:
            return "\u0073\u0323";
        case 0x1E64:
            return "\u015A\u0307";
        case 0x1E65:
            return "\u015B\u0307";
        case 0x1E66:
            return "\u0160\u0307";
        case 0x1E67:
            return "\u0161\u0307";
        case 0x1E68:
            return "\u1E62\u0307";
        case 0x1E69:
            return "\u1E63\u0307";
        case 0x1E6A:
            return "\u0054\u0307";
        case 0x1E6B:
            return "\u0074\u0307";
        case 0x1E6C:
            return "\u0054\u0323";
        case 0x1E6D:
            return "\u0074\u0323";
        case 0x1E6E:
            return "\u0054\u0331";
        case 0x1E6F:
            return "\u0074\u0331";
        case 0x1E70:
            return "\u0054\u032D";
        case 0x1E71:
            return "\u0074\u032D";
        case 0x1E72:
            return "\u0055\u0324";
        case 0x1E73:
            return "\u0075\u0324";
        case 0x1E74:
            return "\u0055\u0330";
        case 0x1E75:
            return "\u0075\u0330";
        case 0x1E76:
            return "\u0055\u032D";
        case 0x1E77:
            return "\u0075\u032D";
        case 0x1E78:
            return "\u0168\u0301";
        case 0x1E79:
            return "\u0169\u0301";
        case 0x1E7A:
            return "\u016A\u0308";
        case 0x1E7B:
            return "\u016B\u0308";
        case 0x1E7C:
            return "\u0056\u0303";
        case 0x1E7D:
            return "\u0076\u0303";
        case 0x1E7E:
            return "\u0056\u0323";
        case 0x1E7F:
            return "\u0076\u0323";
        case 0x1E80:
            return "\u0057\u0300";
        case 0x1E81:
            return "\u0077\u0300";
        case 0x1E82:
            return "\u0057\u0301";
        case 0x1E83:
            return "\u0077\u0301";
        case 0x1E84:
            return "\u0057\u0308";
        case 0x1E85:
            return "\u0077\u0308";
        case 0x1E86:
            return "\u0057\u0307";
        case 0x1E87:
            return "\u0077\u0307";
        case 0x1E88:
            return "\u0057\u0323";
        case 0x1E89:
            return "\u0077\u0323";
        case 0x1E8A:
            return "\u0058\u0307";
        case 0x1E8B:
            return "\u0078\u0307";
        case 0x1E8C:
            return "\u0058\u0308";
        case 0x1E8D:
            return "\u0078\u0308";
        case 0x1E8E:
            return "\u0059\u0307";
        case 0x1E8F:
            return "\u0079\u0307";
        case 0x1E90:
            return "\u005A\u0302";
        case 0x1E91:
            return "\u007A\u0302";
        case 0x1E92:
            return "\u005A\u0323";
        case 0x1E93:
            return "\u007A\u0323";
        case 0x1E94:
            return "\u005A\u0331";
        case 0x1E95:
            return "\u007A\u0331";
        case 0x1E96:
            return "\u0068\u0331";
        case 0x1E97:
            return "\u0074\u0308";
        case 0x1E98:
            return "\u0077\u030A";
        case 0x1E99:
            return "\u0079\u030A";
        case 0x1E9B:
            return "\u017F\u0307";
        case 0x1EA0:
            return "\u0041\u0323";
        case 0x1EA1:
            return "\u0061\u0323";
        case 0x1EA2:
            return "\u0041\u0309";
        case 0x1EA3:
            return "\u0061\u0309";
        case 0x1EA4:
            return "\u00C2\u0301";
        case 0x1EA5:
            return "\u00E2\u0301";
        case 0x1EA6:
            return "\u00C2\u0300";
        case 0x1EA7:
            return "\u00E2\u0300";
        case 0x1EA8:
            return "\u00C2\u0309";
        case 0x1EA9:
            return "\u00E2\u0309";
        case 0x1EAA:
            return "\u00C2\u0303";
        case 0x1EAB:
            return "\u00E2\u0303";
        case 0x1EAC:
            return "\u1EA0\u0302";
        case 0x1EAD:
            return "\u1EA1\u0302";
        case 0x1EAE:
            return "\u0102\u0301";
        case 0x1EAF:
            return "\u0103\u0301";
        case 0x1EB0:
            return "\u0102\u0300";
        case 0x1EB1:
            return "\u0103\u0300";
        case 0x1EB2:
            return "\u0102\u0309";
        case 0x1EB3:
            return "\u0103\u0309";
        case 0x1EB4:
            return "\u0102\u0303";
        case 0x1EB5:
            return "\u0103\u0303";
        case 0x1EB6:
            return "\u1EA0\u0306";
        case 0x1EB7:
            return "\u1EA1\u0306";
        case 0x1EB8:
            return "\u0045\u0323";
        case 0x1EB9:
            return "\u0065\u0323";
        case 0x1EBA:
            return "\u0045\u0309";
        case 0x1EBB:
            return "\u0065\u0309";
        case 0x1EBC:
            return "\u0045\u0303";
        case 0x1EBD:
            return "\u0065\u0303";
        case 0x1EBE:
            return "\u00CA\u0301";
        case 0x1EBF:
            return "\u00EA\u0301";
        case 0x1EC0:
            return "\u00CA\u0300";
        case 0x1EC1:
            return "\u00EA\u0300";
        case 0x1EC2:
            return "\u00CA\u0309";
        case 0x1EC3:
            return "\u00EA\u0309";
        case 0x1EC4:
            return "\u00CA\u0303";
        case 0x1EC5:
            return "\u00EA\u0303";
        case 0x1EC6:
            return "\u1EB8\u0302";
        case 0x1EC7:
            return "\u1EB9\u0302";
        case 0x1EC8:
            return "\u0049\u0309";
        case 0x1EC9:
            return "\u0069\u0309";
        case 0x1ECA:
            return "\u0049\u0323";
        case 0x1ECB:
            return "\u0069\u0323";
        case 0x1ECC:
            return "\u004F\u0323";
        case 0x1ECD:
            return "\u006F\u0323";
        case 0x1ECE:
            return "\u004F\u0309";
        case 0x1ECF:
            return "\u006F\u0309";
        case 0x1ED0:
            return "\u00D4\u0301";
        case 0x1ED1:
            return "\u00F4\u0301";
        case 0x1ED2:
            return "\u00D4\u0300";
        case 0x1ED3:
            return "\u00F4\u0300";
        case 0x1ED4:
            return "\u00D4\u0309";
        case 0x1ED5:
            return "\u00F4\u0309";
        case 0x1ED6:
            return "\u00D4\u0303";
        case 0x1ED7:
            return "\u00F4\u0303";
        case 0x1ED8:
            return "\u1ECC\u0302";
        case 0x1ED9:
            return "\u1ECD\u0302";
        case 0x1EDA:
            return "\u01A0\u0301";
        case 0x1EDB:
            return "\u01A1\u0301";
        case 0x1EDC:
            return "\u01A0\u0300";
        case 0x1EDD:
            return "\u01A1\u0300";
        case 0x1EDE:
            return "\u01A0\u0309";
        case 0x1EDF:
            return "\u01A1\u0309";
        case 0x1EE0:
            return "\u01A0\u0303";
        case 0x1EE1:
            return "\u01A1\u0303";
        case 0x1EE2:
            return "\u01A0\u0323";
        case 0x1EE3:
            return "\u01A1\u0323";
        case 0x1EE4:
            return "\u0055\u0323";
        case 0x1EE5:
            return "\u0075\u0323";
        case 0x1EE6:
            return "\u0055\u0309";
        case 0x1EE7:
            return "\u0075\u0309";
        case 0x1EE8:
            return "\u01AF\u0301";
        case 0x1EE9:
            return "\u01B0\u0301";
        case 0x1EEA:
            return "\u01AF\u0300";
        case 0x1EEB:
            return "\u01B0\u0300";
        case 0x1EEC:
            return "\u01AF\u0309";
        case 0x1EED:
            return "\u01B0\u0309";
        case 0x1EEE:
            return "\u01AF\u0303";
        case 0x1EEF:
            return "\u01B0\u0303";
        case 0x1EF0:
            return "\u01AF\u0323";
        case 0x1EF1:
            return "\u01B0\u0323";
        case 0x1EF2:
            return "\u0059\u0300";
        case 0x1EF3:
            return "\u0079\u0300";
        case 0x1EF4:
            return "\u0059\u0323";
        case 0x1EF5:
            return "\u0079\u0323";
        case 0x1EF6:
            return "\u0059\u0309";
        case 0x1EF7:
            return "\u0079\u0309";
        case 0x1EF8:
            return "\u0059\u0303";
        case 0x1EF9:
            return "\u0079\u0303";
        case 0x1F00:
            return "\u03B1\u0313";
        case 0x1F01:
            return "\u03B1\u0314";
        case 0x1F02:
            return "\u1F00\u0300";
        case 0x1F03:
            return "\u1F01\u0300";
        case 0x1F04:
            return "\u1F00\u0301";
        case 0x1F05:
            return "\u1F01\u0301";
        case 0x1F06:
            return "\u1F00\u0342";
        case 0x1F07:
            return "\u1F01\u0342";
        case 0x1F08:
            return "\u0391\u0313";
        case 0x1F09:
            return "\u0391\u0314";
        case 0x1F0A:
            return "\u1F08\u0300";
        case 0x1F0B:
            return "\u1F09\u0300";
        case 0x1F0C:
            return "\u1F08\u0301";
        case 0x1F0D:
            return "\u1F09\u0301";
        case 0x1F0E:
            return "\u1F08\u0342";
        case 0x1F0F:
            return "\u1F09\u0342";
        case 0x1F10:
            return "\u03B5\u0313";
        case 0x1F11:
            return "\u03B5\u0314";
        case 0x1F12:
            return "\u1F10\u0300";
        case 0x1F13:
            return "\u1F11\u0300";
        case 0x1F14:
            return "\u1F10\u0301";
        case 0x1F15:
            return "\u1F11\u0301";
        case 0x1F18:
            return "\u0395\u0313";
        case 0x1F19:
            return "\u0395\u0314";
        case 0x1F1A:
            return "\u1F18\u0300";
        case 0x1F1B:
            return "\u1F19\u0300";
        case 0x1F1C:
            return "\u1F18\u0301";
        case 0x1F1D:
            return "\u1F19\u0301";
        case 0x1F20:
            return "\u03B7\u0313";
        case 0x1F21:
            return "\u03B7\u0314";
        case 0x1F22:
            return "\u1F20\u0300";
        case 0x1F23:
            return "\u1F21\u0300";
        case 0x1F24:
            return "\u1F20\u0301";
        case 0x1F25:
            return "\u1F21\u0301";
        case 0x1F26:
            return "\u1F20\u0342";
        case 0x1F27:
            return "\u1F21\u0342";
        case 0x1F28:
            return "\u0397\u0313";
        case 0x1F29:
            return "\u0397\u0314";
        case 0x1F2A:
            return "\u1F28\u0300";
        case 0x1F2B:
            return "\u1F29\u0300";
        case 0x1F2C:
            return "\u1F28\u0301";
        case 0x1F2D:
            return "\u1F29\u0301";
        case 0x1F2E:
            return "\u1F28\u0342";
        case 0x1F2F:
            return "\u1F29\u0342";
        case 0x1F30:
            return "\u03B9\u0313";
        case 0x1F31:
            return "\u03B9\u0314";
        case 0x1F32:
            return "\u1F30\u0300";
        case 0x1F33:
            return "\u1F31\u0300";
        case 0x1F34:
            return "\u1F30\u0301";
        case 0x1F35:
            return "\u1F31\u0301";
        case 0x1F36:
            return "\u1F30\u0342";
        case 0x1F37:
            return "\u1F31\u0342";
        case 0x1F38:
            return "\u0399\u0313";
        case 0x1F39:
            return "\u0399\u0314";
        case 0x1F3A:
            return "\u1F38\u0300";
        case 0x1F3B:
            return "\u1F39\u0300";
        case 0x1F3C:
            return "\u1F38\u0301";
        case 0x1F3D:
            return "\u1F39\u0301";
        case 0x1F3E:
            return "\u1F38\u0342";
        case 0x1F3F:
            return "\u1F39\u0342";
        case 0x1F40:
            return "\u03BF\u0313";
        case 0x1F41:
            return "\u03BF\u0314";
        case 0x1F42:
            return "\u1F40\u0300";
        case 0x1F43:
            return "\u1F41\u0300";
        case 0x1F44:
            return "\u1F40\u0301";
        case 0x1F45:
            return "\u1F41\u0301";
        case 0x1F48:
            return "\u039F\u0313";
        case 0x1F49:
            return "\u039F\u0314";
        case 0x1F4A:
            return "\u1F48\u0300";
        case 0x1F4B:
            return "\u1F49\u0300";
        case 0x1F4C:
            return "\u1F48\u0301";
        case 0x1F4D:
            return "\u1F49\u0301";
        case 0x1F50:
            return "\u03C5\u0313";
        case 0x1F51:
            return "\u03C5\u0314";
        case 0x1F52:
            return "\u1F50\u0300";
        case 0x1F53:
            return "\u1F51\u0300";
        case 0x1F54:
            return "\u1F50\u0301";
        case 0x1F55:
            return "\u1F51\u0301";
        case 0x1F56:
            return "\u1F50\u0342";
        case 0x1F57:
            return "\u1F51\u0342";
        case 0x1F59:
            return "\u03A5\u0314";
        case 0x1F5B:
            return "\u1F59\u0300";
        case 0x1F5D:
            return "\u1F59\u0301";
        case 0x1F5F:
            return "\u1F59\u0342";
        case 0x1F60:
            return "\u03C9\u0313";
        case 0x1F61:
            return "\u03C9\u0314";
        case 0x1F62:
            return "\u1F60\u0300";
        case 0x1F63:
            return "\u1F61\u0300";
        case 0x1F64:
            return "\u1F60\u0301";
        case 0x1F65:
            return "\u1F61\u0301";
        case 0x1F66:
            return "\u1F60\u0342";
        case 0x1F67:
            return "\u1F61\u0342";
        case 0x1F68:
            return "\u03A9\u0313";
        case 0x1F69:
            return "\u03A9\u0314";
        case 0x1F6A:
            return "\u1F68\u0300";
        case 0x1F6B:
            return "\u1F69\u0300";
        case 0x1F6C:
            return "\u1F68\u0301";
        case 0x1F6D:
            return "\u1F69\u0301";
        case 0x1F6E:
            return "\u1F68\u0342";
        case 0x1F6F:
            return "\u1F69\u0342";
        case 0x1F70:
            return "\u03B1\u0300";
        case 0x1F71:
            return "\u03AC";
        case 0x1F72:
            return "\u03B5\u0300";
        case 0x1F73:
            return "\u03AD";
        case 0x1F74:
            return "\u03B7\u0300";
        case 0x1F75:
            return "\u03AE";
        case 0x1F76:
            return "\u03B9\u0300";
        case 0x1F77:
            return "\u03AF";
        case 0x1F78:
            return "\u03BF\u0300";
        case 0x1F79:
            return "\u03CC";
        case 0x1F7A:
            return "\u03C5\u0300";
        case 0x1F7B:
            return "\u03CD";
        case 0x1F7C:
            return "\u03C9\u0300";
        case 0x1F7D:
            return "\u03CE";
        case 0x1F80:
            return "\u1F00\u0345";
        case 0x1F81:
            return "\u1F01\u0345";
        case 0x1F82:
            return "\u1F02\u0345";
        case 0x1F83:
            return "\u1F03\u0345";
        case 0x1F84:
            return "\u1F04\u0345";
        case 0x1F85:
            return "\u1F05\u0345";
        case 0x1F86:
            return "\u1F06\u0345";
        case 0x1F87:
            return "\u1F07\u0345";
        case 0x1F88:
            return "\u1F08\u0345";
        case 0x1F89:
            return "\u1F09\u0345";
        case 0x1F8A:
            return "\u1F0A\u0345";
        case 0x1F8B:
            return "\u1F0B\u0345";
        case 0x1F8C:
            return "\u1F0C\u0345";
        case 0x1F8D:
            return "\u1F0D\u0345";
        case 0x1F8E:
            return "\u1F0E\u0345";
        case 0x1F8F:
            return "\u1F0F\u0345";
        case 0x1F90:
            return "\u1F20\u0345";
        case 0x1F91:
            return "\u1F21\u0345";
        case 0x1F92:
            return "\u1F22\u0345";
        case 0x1F93:
            return "\u1F23\u0345";
        case 0x1F94:
            return "\u1F24\u0345";
        case 0x1F95:
            return "\u1F25\u0345";
        case 0x1F96:
            return "\u1F26\u0345";
        case 0x1F97:
            return "\u1F27\u0345";
        case 0x1F98:
            return "\u1F28\u0345";
        case 0x1F99:
            return "\u1F29\u0345";
        case 0x1F9A:
            return "\u1F2A\u0345";
        case 0x1F9B:
            return "\u1F2B\u0345";
        case 0x1F9C:
            return "\u1F2C\u0345";
        case 0x1F9D:
            return "\u1F2D\u0345";
        case 0x1F9E:
            return "\u1F2E\u0345";
        case 0x1F9F:
            return "\u1F2F\u0345";
        case 0x1FA0:
            return "\u1F60\u0345";
        case 0x1FA1:
            return "\u1F61\u0345";
        case 0x1FA2:
            return "\u1F62\u0345";
        case 0x1FA3:
            return "\u1F63\u0345";
        case 0x1FA4:
            return "\u1F64\u0345";
        case 0x1FA5:
            return "\u1F65\u0345";
        case 0x1FA6:
            return "\u1F66\u0345";
        case 0x1FA7:
            return "\u1F67\u0345";
        case 0x1FA8:
            return "\u1F68\u0345";
        case 0x1FA9:
            return "\u1F69\u0345";
        case 0x1FAA:
            return "\u1F6A\u0345";
        case 0x1FAB:
            return "\u1F6B\u0345";
        case 0x1FAC:
            return "\u1F6C\u0345";
        case 0x1FAD:
            return "\u1F6D\u0345";
        case 0x1FAE:
            return "\u1F6E\u0345";
        case 0x1FAF:
            return "\u1F6F\u0345";
        case 0x1FB0:
            return "\u03B1\u0306";
        case 0x1FB1:
            return "\u03B1\u0304";
        case 0x1FB2:
            return "\u1F70\u0345";
        case 0x1FB3:
            return "\u03B1\u0345";
        case 0x1FB4:
            return "\u03AC\u0345";
        case 0x1FB6:
            return "\u03B1\u0342";
        case 0x1FB7:
            return "\u1FB6\u0345";
        case 0x1FB8:
            return "\u0391\u0306";
        case 0x1FB9:
            return "\u0391\u0304";
        case 0x1FBA:
            return "\u0391\u0300";
        case 0x1FBB:
            return "\u0386";
        case 0x1FBC:
            return "\u0391\u0345";
        case 0x1FBE:
            return "\u03B9";
        case 0x1FC1:
            return "\u00A8\u0342";
        case 0x1FC2:
            return "\u1F74\u0345";
        case 0x1FC3:
            return "\u03B7\u0345";
        case 0x1FC4:
            return "\u03AE\u0345";
        case 0x1FC6:
            return "\u03B7\u0342";
        case 0x1FC7:
            return "\u1FC6\u0345";
        case 0x1FC8:
            return "\u0395\u0300";
        case 0x1FC9:
            return "\u0388";
        case 0x1FCA:
            return "\u0397\u0300";
        case 0x1FCB:
            return "\u0389";
        case 0x1FCC:
            return "\u0397\u0345";
        case 0x1FCD:
            return "\u1FBF\u0300";
        case 0x1FCE:
            return "\u1FBF\u0301";
        case 0x1FCF:
            return "\u1FBF\u0342";
        case 0x1FD0:
            return "\u03B9\u0306";
        case 0x1FD1:
            return "\u03B9\u0304";
        case 0x1FD2:
            return "\u03CA\u0300";
        case 0x1FD3:
            return "\u0390";
        case 0x1FD6:
            return "\u03B9\u0342";
        case 0x1FD7:
            return "\u03CA\u0342";
        case 0x1FD8:
            return "\u0399\u0306";
        case 0x1FD9:
            return "\u0399\u0304";
        case 0x1FDA:
            return "\u0399\u0300";
        case 0x1FDB:
            return "\u038A";
        case 0x1FDD:
            return "\u1FFE\u0300";
        case 0x1FDE:
            return "\u1FFE\u0301";
        case 0x1FDF:
            return "\u1FFE\u0342";
        case 0x1FE0:
            return "\u03C5\u0306";
        case 0x1FE1:
            return "\u03C5\u0304";
        case 0x1FE2:
            return "\u03CB\u0300";
        case 0x1FE3:
            return "\u03B0";
        case 0x1FE4:
            return "\u03C1\u0313";
        case 0x1FE5:
            return "\u03C1\u0314";
        case 0x1FE6:
            return "\u03C5\u0342";
        case 0x1FE7:
            return "\u03CB\u0342";
        case 0x1FE8:
            return "\u03A5\u0306";
        case 0x1FE9:
            return "\u03A5\u0304";
        case 0x1FEA:
            return "\u03A5\u0300";
        case 0x1FEB:
            return "\u038E";
        case 0x1FEC:
            return "\u03A1\u0314";
        case 0x1FED:
            return "\u00A8\u0300";
        case 0x1FEE:
            return "\u0385";
        case 0x1FEF:
            return "\u0060";
        case 0x1FF2:
            return "\u1F7C\u0345";
        case 0x1FF3:
            return "\u03C9\u0345";
        case 0x1FF4:
            return "\u03CE\u0345";
        case 0x1FF6:
            return "\u03C9\u0342";
        case 0x1FF7:
            return "\u1FF6\u0345";
        case 0x1FF8:
            return "\u039F\u0300";
        case 0x1FF9:
            return "\u038C";
        case 0x1FFA:
            return "\u03A9\u0300";
        case 0x1FFB:
            return "\u038F";
        case 0x1FFC:
            return "\u03A9\u0345";
        case 0x1FFD:
            return "\u00B4";
        case 0x2000:
            return "\u2002";
        case 0x2001:
            return "\u2003";
        case 0x2126:
            return "\u03A9";
        case 0x212A:
            return "\u004B";
        case 0x212B:
            return "\u00C5";
        case 0x219A:
            return "\u2190\u0338";
        case 0x219B:
            return "\u2192\u0338";
        case 0x21AE:
            return "\u2194\u0338";
        case 0x21CD:
            return "\u21D0\u0338";
        case 0x21CE:
            return "\u21D4\u0338";
        case 0x21CF:
            return "\u21D2\u0338";
        case 0x2204:
            return "\u2203\u0338";
        case 0x2209:
            return "\u2208\u0338";
        case 0x220C:
            return "\u220B\u0338";
        case 0x2224:
            return "\u2223\u0338";
        case 0x2226:
            return "\u2225\u0338";
        case 0x2241:
            return "\u223C\u0338";
        case 0x2244:
            return "\u2243\u0338";
        case 0x2247:
            return "\u2245\u0338";
        case 0x2249:
            return "\u2248\u0338";
        case 0x2260:
            return "\u003D\u0338";
        case 0x2262:
            return "\u2261\u0338";
        case 0x226D:
            return "\u224D\u0338";
        case 0x226E:
            return "\u003C\u0338";
        case 0x226F:
            return "\u003E\u0338";
        case 0x2270:
            return "\u2264\u0338";
        case 0x2271:
            return "\u2265\u0338";
        case 0x2274:
            return "\u2272\u0338";
        case 0x2275:
            return "\u2273\u0338";
        case 0x2278:
            return "\u2276\u0338";
        case 0x2279:
            return "\u2277\u0338";
        case 0x2280:
            return "\u227A\u0338";
        case 0x2281:
            return "\u227B\u0338";
        case 0x2284:
            return "\u2282\u0338";
        case 0x2285:
            return "\u2283\u0338";
        case 0x2288:
            return "\u2286\u0338";
        case 0x2289:
            return "\u2287\u0338";
        case 0x22AC:
            return "\u22A2\u0338";
        case 0x22AD:
            return "\u22A8\u0338";
        case 0x22AE:
            return "\u22A9\u0338";
        case 0x22AF:
            return "\u22AB\u0338";
        case 0x22E0:
            return "\u227C\u0338";
        case 0x22E1:
            return "\u227D\u0338";
        case 0x22E2:
            return "\u2291\u0338";
        case 0x22E3:
            return "\u2292\u0338";
        case 0x22EA:
            return "\u22B2\u0338";
        case 0x22EB:
            return "\u22B3\u0338";
        case 0x22EC:
            return "\u22B4\u0338";
        case 0x22ED:
            return "\u22B5\u0338";
        case 0x2329:
            return "\u3008";
        case 0x232A:
            return "\u3009";
        case 0x2ADC:
            return "\u2ADD\u0338";
        case 0x304C:
            return "\u304B\u3099";
        case 0x304E:
            return "\u304D\u3099";
        case 0x3050:
            return "\u304F\u3099";
        case 0x3052:
            return "\u3051\u3099";
        case 0x3054:
            return "\u3053\u3099";
        case 0x3056:
            return "\u3055\u3099";
        case 0x3058:
            return "\u3057\u3099";
        case 0x305A:
            return "\u3059\u3099";
        case 0x305C:
            return "\u305B\u3099";
        case 0x305E:
            return "\u305D\u3099";
        case 0x3060:
            return "\u305F\u3099";
        case 0x3062:
            return "\u3061\u3099";
        case 0x3065:
            return "\u3064\u3099";
        case 0x3067:
            return "\u3066\u3099";
        case 0x3069:
            return "\u3068\u3099";
        case 0x3070:
            return "\u306F\u3099";
        case 0x3071:
            return "\u306F\u309A";
        case 0x3073:
            return "\u3072\u3099";
        case 0x3074:
            return "\u3072\u309A";
        case 0x3076:
            return "\u3075\u3099";
        case 0x3077:
            return "\u3075\u309A";
        case 0x3079:
            return "\u3078\u3099";
        case 0x307A:
            return "\u3078\u309A";
        case 0x307C:
            return "\u307B\u3099";
        case 0x307D:
            return "\u307B\u309A";
        case 0x3094:
            return "\u3046\u3099";
        case 0x309E:
            return "\u309D\u3099";
        case 0x30AC:
            return "\u30AB\u3099";
        case 0x30AE:
            return "\u30AD\u3099";
        case 0x30B0:
            return "\u30AF\u3099";
        case 0x30B2:
            return "\u30B1\u3099";
        case 0x30B4:
            return "\u30B3\u3099";
        case 0x30B6:
            return "\u30B5\u3099";
        case 0x30B8:
            return "\u30B7\u3099";
        case 0x30BA:
            return "\u30B9\u3099";
        case 0x30BC:
            return "\u30BB\u3099";
        case 0x30BE:
            return "\u30BD\u3099";
        case 0x30C0:
            return "\u30BF\u3099";
        case 0x30C2:
            return "\u30C1\u3099";
        case 0x30C5:
            return "\u30C4\u3099";
        case 0x30C7:
            return "\u30C6\u3099";
        case 0x30C9:
            return "\u30C8\u3099";
        case 0x30D0:
            return "\u30CF\u3099";
        case 0x30D1:
            return "\u30CF\u309A";
        case 0x30D3:
            return "\u30D2\u3099";
        case 0x30D4:
            return "\u30D2\u309A";
        case 0x30D6:
            return "\u30D5\u3099";
        case 0x30D7:
            return "\u30D5\u309A";
        case 0x30D9:
            return "\u30D8\u3099";
        case 0x30DA:
            return "\u30D8\u309A";
        case 0x30DC:
            return "\u30DB\u3099";
        case 0x30DD:
            return "\u30DB\u309A";
        case 0x30F4:
            return "\u30A6\u3099";
        case 0x30F7:
            return "\u30EF\u3099";
        case 0x30F8:
            return "\u30F0\u3099";
        case 0x30F9:
            return "\u30F1\u3099";
        case 0x30FA:
            return "\u30F2\u3099";
        case 0x30FE:
            return "\u30FD\u3099";
        case 0xF900:
            return "\u8C48";
        case 0xF901:
            return "\u66F4";
        case 0xF902:
            return "\u8ECA";
        case 0xF903:
            return "\u8CC8";
        case 0xF904:
            return "\u6ED1";
        case 0xF905:
            return "\u4E32";
        case 0xF906:
            return "\u53E5";
        case 0xF907:
            return "\u9F9C";
        case 0xF908:
            return "\u9F9C";
        case 0xF909:
            return "\u5951";
        case 0xF90A:
            return "\u91D1";
        case 0xF90B:
            return "\u5587";
        case 0xF90C:
            return "\u5948";
        case 0xF90D:
            return "\u61F6";
        case 0xF90E:
            return "\u7669";
        case 0xF90F:
            return "\u7F85";
        case 0xF910:
            return "\u863F";
        case 0xF911:
            return "\u87BA";
        case 0xF912:
            return "\u88F8";
        case 0xF913:
            return "\u908F";
        case 0xF914:
            return "\u6A02";
        case 0xF915:
            return "\u6D1B";
        case 0xF916:
            return "\u70D9";
        case 0xF917:
            return "\u73DE";
        case 0xF918:
            return "\u843D";
        case 0xF919:
            return "\u916A";
        case 0xF91A:
            return "\u99F1";
        case 0xF91B:
            return "\u4E82";
        case 0xF91C:
            return "\u5375";
        case 0xF91D:
            return "\u6B04";
        case 0xF91E:
            return "\u721B";
        case 0xF91F:
            return "\u862D";
        case 0xF920:
            return "\u9E1E";
        case 0xF921:
            return "\u5D50";
        case 0xF922:
            return "\u6FEB";
        case 0xF923:
            return "\u85CD";
        case 0xF924:
            return "\u8964";
        case 0xF925:
            return "\u62C9";
        case 0xF926:
            return "\u81D8";
        case 0xF927:
            return "\u881F";
        case 0xF928:
            return "\u5ECA";
        case 0xF929:
            return "\u6717";
        case 0xF92A:
            return "\u6D6A";
        case 0xF92B:
            return "\u72FC";
        case 0xF92C:
            return "\u90CE";
        case 0xF92D:
            return "\u4F86";
        case 0xF92E:
            return "\u51B7";
        case 0xF92F:
            return "\u52DE";
        case 0xF930:
            return "\u64C4";
        case 0xF931:
            return "\u6AD3";
        case 0xF932:
            return "\u7210";
        case 0xF933:
            return "\u76E7";
        case 0xF934:
            return "\u8001";
        case 0xF935:
            return "\u8606";
        case 0xF936:
            return "\u865C";
        case 0xF937:
            return "\u8DEF";
        case 0xF938:
            return "\u9732";
        case 0xF939:
            return "\u9B6F";
        case 0xF93A:
            return "\u9DFA";
        case 0xF93B:
            return "\u788C";
        case 0xF93C:
            return "\u797F";
        case 0xF93D:
            return "\u7DA0";
        case 0xF93E:
            return "\u83C9";
        case 0xF93F:
            return "\u9304";
        case 0xF940:
            return "\u9E7F";
        case 0xF941:
            return "\u8AD6";
        case 0xF942:
            return "\u58DF";
        case 0xF943:
            return "\u5F04";
        case 0xF944:
            return "\u7C60";
        case 0xF945:
            return "\u807E";
        case 0xF946:
            return "\u7262";
        case 0xF947:
            return "\u78CA";
        case 0xF948:
            return "\u8CC2";
        case 0xF949:
            return "\u96F7";
        case 0xF94A:
            return "\u58D8";
        case 0xF94B:
            return "\u5C62";
        case 0xF94C:
            return "\u6A13";
        case 0xF94D:
            return "\u6DDA";
        case 0xF94E:
            return "\u6F0F";
        case 0xF94F:
            return "\u7D2F";
        case 0xF950:
            return "\u7E37";
        case 0xF951:
            return "\u964B";
        case 0xF952:
            return "\u52D2";
        case 0xF953:
            return "\u808B";
        case 0xF954:
            return "\u51DC";
        case 0xF955:
            return "\u51CC";
        case 0xF956:
            return "\u7A1C";
        case 0xF957:
            return "\u7DBE";
        case 0xF958:
            return "\u83F1";
        case 0xF959:
            return "\u9675";
        case 0xF95A:
            return "\u8B80";
        case 0xF95B:
            return "\u62CF";
        case 0xF95C:
            return "\u6A02";
        case 0xF95D:
            return "\u8AFE";
        case 0xF95E:
            return "\u4E39";
        case 0xF95F:
            return "\u5BE7";
        case 0xF960:
            return "\u6012";
        case 0xF961:
            return "\u7387";
        case 0xF962:
            return "\u7570";
        case 0xF963:
            return "\u5317";
        case 0xF964:
            return "\u78FB";
        case 0xF965:
            return "\u4FBF";
        case 0xF966:
            return "\u5FA9";
        case 0xF967:
            return "\u4E0D";
        case 0xF968:
            return "\u6CCC";
        case 0xF969:
            return "\u6578";
        case 0xF96A:
            return "\u7D22";
        case 0xF96B:
            return "\u53C3";
        case 0xF96C:
            return "\u585E";
        case 0xF96D:
            return "\u7701";
        case 0xF96E:
            return "\u8449";
        case 0xF96F:
            return "\u8AAA";
        case 0xF970:
            return "\u6BBA";
        case 0xF971:
            return "\u8FB0";
        case 0xF972:
            return "\u6C88";
        case 0xF973:
            return "\u62FE";
        case 0xF974:
            return "\u82E5";
        case 0xF975:
            return "\u63A0";
        case 0xF976:
            return "\u7565";
        case 0xF977:
            return "\u4EAE";
        case 0xF978:
            return "\u5169";
        case 0xF979:
            return "\u51C9";
        case 0xF97A:
            return "\u6881";
        case 0xF97B:
            return "\u7CE7";
        case 0xF97C:
            return "\u826F";
        case 0xF97D:
            return "\u8AD2";
        case 0xF97E:
            return "\u91CF";
        case 0xF97F:
            return "\u52F5";
        case 0xF980:
            return "\u5442";
        case 0xF981:
            return "\u5973";
        case 0xF982:
            return "\u5EEC";
        case 0xF983:
            return "\u65C5";
        case 0xF984:
            return "\u6FFE";
        case 0xF985:
            return "\u792A";
        case 0xF986:
            return "\u95AD";
        case 0xF987:
            return "\u9A6A";
        case 0xF988:
            return "\u9E97";
        case 0xF989:
            return "\u9ECE";
        case 0xF98A:
            return "\u529B";
        case 0xF98B:
            return "\u66C6";
        case 0xF98C:
            return "\u6B77";
        case 0xF98D:
            return "\u8F62";
        case 0xF98E:
            return "\u5E74";
        case 0xF98F:
            return "\u6190";
        case 0xF990:
            return "\u6200";
        case 0xF991:
            return "\u649A";
        case 0xF992:
            return "\u6F23";
        case 0xF993:
            return "\u7149";
        case 0xF994:
            return "\u7489";
        case 0xF995:
            return "\u79CA";
        case 0xF996:
            return "\u7DF4";
        case 0xF997:
            return "\u806F";
        case 0xF998:
            return "\u8F26";
        case 0xF999:
            return "\u84EE";
        case 0xF99A:
            return "\u9023";
        case 0xF99B:
            return "\u934A";
        case 0xF99C:
            return "\u5217";
        case 0xF99D:
            return "\u52A3";
        case 0xF99E:
            return "\u54BD";
        case 0xF99F:
            return "\u70C8";
        case 0xF9A0:
            return "\u88C2";
        case 0xF9A1:
            return "\u8AAA";
        case 0xF9A2:
            return "\u5EC9";
        case 0xF9A3:
            return "\u5FF5";
        case 0xF9A4:
            return "\u637B";
        case 0xF9A5:
            return "\u6BAE";
        case 0xF9A6:
            return "\u7C3E";
        case 0xF9A7:
            return "\u7375";
        case 0xF9A8:
            return "\u4EE4";
        case 0xF9A9:
            return "\u56F9";
        case 0xF9AA:
            return "\u5BE7";
        case 0xF9AB:
            return "\u5DBA";
        case 0xF9AC:
            return "\u601C";
        case 0xF9AD:
            return "\u73B2";
        case 0xF9AE:
            return "\u7469";
        case 0xF9AF:
            return "\u7F9A";
        case 0xF9B0:
            return "\u8046";
        case 0xF9B1:
            return "\u9234";
        case 0xF9B2:
            return "\u96F6";
        case 0xF9B3:
            return "\u9748";
        case 0xF9B4:
            return "\u9818";
        case 0xF9B5:
            return "\u4F8B";
        case 0xF9B6:
            return "\u79AE";
        case 0xF9B7:
            return "\u91B4";
        case 0xF9B8:
            return "\u96B8";
        case 0xF9B9:
            return "\u60E1";
        case 0xF9BA:
            return "\u4E86";
        case 0xF9BB:
            return "\u50DA";
        case 0xF9BC:
            return "\u5BEE";
        case 0xF9BD:
            return "\u5C3F";
        case 0xF9BE:
            return "\u6599";
        case 0xF9BF:
            return "\u6A02";
        case 0xF9C0:
            return "\u71CE";
        case 0xF9C1:
            return "\u7642";
        case 0xF9C2:
            return "\u84FC";
        case 0xF9C3:
            return "\u907C";
        case 0xF9C4:
            return "\u9F8D";
        case 0xF9C5:
            return "\u6688";
        case 0xF9C6:
            return "\u962E";
        case 0xF9C7:
            return "\u5289";
        case 0xF9C8:
            return "\u677B";
        case 0xF9C9:
            return "\u67F3";
        case 0xF9CA:
            return "\u6D41";
        case 0xF9CB:
            return "\u6E9C";
        case 0xF9CC:
            return "\u7409";
        case 0xF9CD:
            return "\u7559";
        case 0xF9CE:
            return "\u786B";
        case 0xF9CF:
            return "\u7D10";
        case 0xF9D0:
            return "\u985E";
        case 0xF9D1:
            return "\u516D";
        case 0xF9D2:
            return "\u622E";
        case 0xF9D3:
            return "\u9678";
        case 0xF9D4:
            return "\u502B";
        case 0xF9D5:
            return "\u5D19";
        case 0xF9D6:
            return "\u6DEA";
        case 0xF9D7:
            return "\u8F2A";
        case 0xF9D8:
            return "\u5F8B";
        case 0xF9D9:
            return "\u6144";
        case 0xF9DA:
            return "\u6817";
        case 0xF9DB:
            return "\u7387";
        case 0xF9DC:
            return "\u9686";
        case 0xF9DD:
            return "\u5229";
        case 0xF9DE:
            return "\u540F";
        case 0xF9DF:
            return "\u5C65";
        case 0xF9E0:
            return "\u6613";
        case 0xF9E1:
            return "\u674E";
        case 0xF9E2:
            return "\u68A8";
        case 0xF9E3:
            return "\u6CE5";
        case 0xF9E4:
            return "\u7406";
        case 0xF9E5:
            return "\u75E2";
        case 0xF9E6:
            return "\u7F79";
        case 0xF9E7:
            return "\u88CF";
        case 0xF9E8:
            return "\u88E1";
        case 0xF9E9:
            return "\u91CC";
        case 0xF9EA:
            return "\u96E2";
        case 0xF9EB:
            return "\u533F";
        case 0xF9EC:
            return "\u6EBA";
        case 0xF9ED:
            return "\u541D";
        case 0xF9EE:
            return "\u71D0";
        case 0xF9EF:
            return "\u7498";
        case 0xF9F0:
            return "\u85FA";
        case 0xF9F1:
            return "\u96A3";
        case 0xF9F2:
            return "\u9C57";
        case 0xF9F3:
            return "\u9E9F";
        case 0xF9F4:
            return "\u6797";
        case 0xF9F5:
            return "\u6DCB";
        case 0xF9F6:
            return "\u81E8";
        case 0xF9F7:
            return "\u7ACB";
        case 0xF9F8:
            return "\u7B20";
        case 0xF9F9:
            return "\u7C92";
        case 0xF9FA:
            return "\u72C0";
        case 0xF9FB:
            return "\u7099";
        case 0xF9FC:
            return "\u8B58";
        case 0xF9FD:
            return "\u4EC0";
        case 0xF9FE:
            return "\u8336";
        case 0xF9FF:
            return "\u523A";
        case 0xFA00:
            return "\u5207";
        case 0xFA01:
            return "\u5EA6";
        case 0xFA02:
            return "\u62D3";
        case 0xFA03:
            return "\u7CD6";
        case 0xFA04:
            return "\u5B85";
        case 0xFA05:
            return "\u6D1E";
        case 0xFA06:
            return "\u66B4";
        case 0xFA07:
            return "\u8F3B";
        case 0xFA08:
            return "\u884C";
        case 0xFA09:
            return "\u964D";
        case 0xFA0A:
            return "\u898B";
        case 0xFA0B:
            return "\u5ED3";
        case 0xFA0C:
            return "\u5140";
        case 0xFA0D:
            return "\u55C0";
        case 0xFA10:
            return "\u585A";
        case 0xFA12:
            return "\u6674";
        case 0xFA15:
            return "\u51DE";
        case 0xFA16:
            return "\u732A";
        case 0xFA17:
            return "\u76CA";
        case 0xFA18:
            return "\u793C";
        case 0xFA19:
            return "\u795E";
        case 0xFA1A:
            return "\u7965";
        case 0xFA1B:
            return "\u798F";
        case 0xFA1C:
            return "\u9756";
        case 0xFA1D:
            return "\u7CBE";
        case 0xFA1E:
            return "\u7FBD";
        case 0xFA20:
            return "\u8612";
        case 0xFA22:
            return "\u8AF8";
        case 0xFA25:
            return "\u9038";
        case 0xFA26:
            return "\u90FD";
        case 0xFA2A:
            return "\u98EF";
        case 0xFA2B:
            return "\u98FC";
        case 0xFA2C:
            return "\u9928";
        case 0xFA2D:
            return "\u9DB4";
        case 0xFA30:
            return "\u4FAE";
        case 0xFA31:
            return "\u50E7";
        case 0xFA32:
            return "\u514D";
        case 0xFA33:
            return "\u52C9";
        case 0xFA34:
            return "\u52E4";
        case 0xFA35:
            return "\u5351";
        case 0xFA36:
            return "\u559D";
        case 0xFA37:
            return "\u5606";
        case 0xFA38:
            return "\u5668";
        case 0xFA39:
            return "\u5840";
        case 0xFA3A:
            return "\u58A8";
        case 0xFA3B:
            return "\u5C64";
        case 0xFA3C:
            return "\u5C6E";
        case 0xFA3D:
            return "\u6094";
        case 0xFA3E:
            return "\u6168";
        case 0xFA3F:
            return "\u618E";
        case 0xFA40:
            return "\u61F2";
        case 0xFA41:
            return "\u654F";
        case 0xFA42:
            return "\u65E2";
        case 0xFA43:
            return "\u6691";
        case 0xFA44:
            return "\u6885";
        case 0xFA45:
            return "\u6D77";
        case 0xFA46:
            return "\u6E1A";
        case 0xFA47:
            return "\u6F22";
        case 0xFA48:
            return "\u716E";
        case 0xFA49:
            return "\u722B";
        case 0xFA4A:
            return "\u7422";
        case 0xFA4B:
            return "\u7891";
        case 0xFA4C:
            return "\u793E";
        case 0xFA4D:
            return "\u7949";
        case 0xFA4E:
            return "\u7948";
        case 0xFA4F:
            return "\u7950";
        case 0xFA50:
            return "\u7956";
        case 0xFA51:
            return "\u795D";
        case 0xFA52:
            return "\u798D";
        case 0xFA53:
            return "\u798E";
        case 0xFA54:
            return "\u7A40";
        case 0xFA55:
            return "\u7A81";
        case 0xFA56:
            return "\u7BC0";
        case 0xFA57:
            return "\u7DF4";
        case 0xFA58:
            return "\u7E09";
        case 0xFA59:
            return "\u7E41";
        case 0xFA5A:
            return "\u7F72";
        case 0xFA5B:
            return "\u8005";
        case 0xFA5C:
            return "\u81ED";
        case 0xFA5D:
            return "\u8279";
        case 0xFA5E:
            return "\u8279";
        case 0xFA5F:
            return "\u8457";
        case 0xFA60:
            return "\u8910";
        case 0xFA61:
            return "\u8996";
        case 0xFA62:
            return "\u8B01";
        case 0xFA63:
            return "\u8B39";
        case 0xFA64:
            return "\u8CD3";
        case 0xFA65:
            return "\u8D08";
        case 0xFA66:
            return "\u8FB6";
        case 0xFA67:
            return "\u9038";
        case 0xFA68:
            return "\u96E3";
        case 0xFA69:
            return "\u97FF";
        case 0xFA6A:
            return "\u983B";
        case 0xFA70:
            return "\u4E26";
        case 0xFA71:
            return "\u51B5";
        case 0xFA72:
            return "\u5168";
        case 0xFA73:
            return "\u4F80";
        case 0xFA74:
            return "\u5145";
        case 0xFA75:
            return "\u5180";
        case 0xFA76:
            return "\u52C7";
        case 0xFA77:
            return "\u52FA";
        case 0xFA78:
            return "\u559D";
        case 0xFA79:
            return "\u5555";
        case 0xFA7A:
            return "\u5599";
        case 0xFA7B:
            return "\u55E2";
        case 0xFA7C:
            return "\u585A";
        case 0xFA7D:
            return "\u58B3";
        case 0xFA7E:
            return "\u5944";
        case 0xFA7F:
            return "\u5954";
        case 0xFA80:
            return "\u5A62";
        case 0xFA81:
            return "\u5B28";
        case 0xFA82:
            return "\u5ED2";
        case 0xFA83:
            return "\u5ED9";
        case 0xFA84:
            return "\u5F69";
        case 0xFA85:
            return "\u5FAD";
        case 0xFA86:
            return "\u60D8";
        case 0xFA87:
            return "\u614E";
        case 0xFA88:
            return "\u6108";
        case 0xFA89:
            return "\u618E";
        case 0xFA8A:
            return "\u6160";
        case 0xFA8B:
            return "\u61F2";
        case 0xFA8C:
            return "\u6234";
        case 0xFA8D:
            return "\u63C4";
        case 0xFA8E:
            return "\u641C";
        case 0xFA8F:
            return "\u6452";
        case 0xFA90:
            return "\u6556";
        case 0xFA91:
            return "\u6674";
        case 0xFA92:
            return "\u6717";
        case 0xFA93:
            return "\u671B";
        case 0xFA94:
            return "\u6756";
        case 0xFA95:
            return "\u6B79";
        case 0xFA96:
            return "\u6BBA";
        case 0xFA97:
            return "\u6D41";
        case 0xFA98:
            return "\u6EDB";
        case 0xFA99:
            return "\u6ECB";
        case 0xFA9A:
            return "\u6F22";
        case 0xFA9B:
            return "\u701E";
        case 0xFA9C:
            return "\u716E";
        case 0xFA9D:
            return "\u77A7";
        case 0xFA9E:
            return "\u7235";
        case 0xFA9F:
            return "\u72AF";
        case 0xFAA0:
            return "\u732A";
        case 0xFAA1:
            return "\u7471";
        case 0xFAA2:
            return "\u7506";
        case 0xFAA3:
            return "\u753B";
        case 0xFAA4:
            return "\u761D";
        case 0xFAA5:
            return "\u761F";
        case 0xFAA6:
            return "\u76CA";
        case 0xFAA7:
            return "\u76DB";
        case 0xFAA8:
            return "\u76F4";
        case 0xFAA9:
            return "\u774A";
        case 0xFAAA:
            return "\u7740";
        case 0xFAAB:
            return "\u78CC";
        case 0xFAAC:
            return "\u7AB1";
        case 0xFAAD:
            return "\u7BC0";
        case 0xFAAE:
            return "\u7C7B";
        case 0xFAAF:
            return "\u7D5B";
        case 0xFAB0:
            return "\u7DF4";
        case 0xFAB1:
            return "\u7F3E";
        case 0xFAB2:
            return "\u8005";
        case 0xFAB3:
            return "\u8352";
        case 0xFAB4:
            return "\u83EF";
        case 0xFAB5:
            return "\u8779";
        case 0xFAB6:
            return "\u8941";
        case 0xFAB7:
            return "\u8986";
        case 0xFAB8:
            return "\u8996";
        case 0xFAB9:
            return "\u8ABF";
        case 0xFABA:
            return "\u8AF8";
        case 0xFABB:
            return "\u8ACB";
        case 0xFABC:
            return "\u8B01";
        case 0xFABD:
            return "\u8AFE";
        case 0xFABE:
            return "\u8AED";
        case 0xFABF:
            return "\u8B39";
        case 0xFAC0:
            return "\u8B8A";
        case 0xFAC1:
            return "\u8D08";
        case 0xFAC2:
            return "\u8F38";
        case 0xFAC3:
            return "\u9072";
        case 0xFAC4:
            return "\u9199";
        case 0xFAC5:
            return "\u9276";
        case 0xFAC6:
            return "\u967C";
        case 0xFAC7:
            return "\u96E3";
        case 0xFAC8:
            return "\u9756";
        case 0xFAC9:
            return "\u97DB";
        case 0xFACA:
            return "\u97FF";
        case 0xFACB:
            return "\u980B";
        case 0xFACC:
            return "\u983B";
        case 0xFACD:
            return "\u9B12";
        case 0xFACE:
            return "\u9F9C";
        case 0xFACF:
            return "\ud84a\udc4a";
        case 0xFAD0:
            return "\ud84a\udc44";
        case 0xFAD1:
            return "\ud84c\udfd5";
        case 0xFAD2:
            return "\u3B9D";
        case 0xFAD3:
            return "\u4018";
        case 0xFAD4:
            return "\u4039";
        case 0xFAD5:
            return "\ud854\ude49";
        case 0xFAD6:
            return "\ud857\udcd0";
        case 0xFAD7:
            return "\ud85f\uded3";
        case 0xFAD8:
            return "\u9F43";
        case 0xFAD9:
            return "\u9F8E";
        case 0xFB1D:
            return "\u05D9\u05B4";
        case 0xFB1F:
            return "\u05F2\u05B7";
        case 0xFB2A:
            return "\u05E9\u05C1";
        case 0xFB2B:
            return "\u05E9\u05C2";
        case 0xFB2C:
            return "\uFB49\u05C1";
        case 0xFB2D:
            return "\uFB49\u05C2";
        case 0xFB2E:
            return "\u05D0\u05B7";
        case 0xFB2F:
            return "\u05D0\u05B8";
        case 0xFB30:
            return "\u05D0\u05BC";
        case 0xFB31:
            return "\u05D1\u05BC";
        case 0xFB32:
            return "\u05D2\u05BC";
        case 0xFB33:
            return "\u05D3\u05BC";
        case 0xFB34:
            return "\u05D4\u05BC";
        case 0xFB35:
            return "\u05D5\u05BC";
        case 0xFB36:
            return "\u05D6\u05BC";
        case 0xFB38:
            return "\u05D8\u05BC";
        case 0xFB39:
            return "\u05D9\u05BC";
        case 0xFB3A:
            return "\u05DA\u05BC";
        case 0xFB3B:
            return "\u05DB\u05BC";
        case 0xFB3C:
            return "\u05DC\u05BC";
        case 0xFB3E:
            return "\u05DE\u05BC";
        case 0xFB40:
            return "\u05E0\u05BC";
        case 0xFB41:
            return "\u05E1\u05BC";
        case 0xFB43:
            return "\u05E3\u05BC";
        case 0xFB44:
            return "\u05E4\u05BC";
        case 0xFB46:
            return "\u05E6\u05BC";
        case 0xFB47:
            return "\u05E7\u05BC";
        case 0xFB48:
            return "\u05E8\u05BC";
        case 0xFB49:
            return "\u05E9\u05BC";
        case 0xFB4A:
            return "\u05EA\u05BC";
        case 0xFB4B:
            return "\u05D5\u05B9";
        case 0xFB4C:
            return "\u05D1\u05BF";
        case 0xFB4D:
            return "\u05DB\u05BF";
        case 0xFB4E:
            return "\u05E4\u05BF";
        case 0x1D15E:
            return "\ud834\udd57\ud834\udd65";
        case 0x1D15F:
            return "\ud834\udd58\ud834\udd65";
        case 0x1D160:
            return "\ud834\udd5f\ud834\udd6e";
        case 0x1D161:
            return "\ud834\udd5f\ud834\udd6f";
        case 0x1D162:
            return "\ud834\udd5f\ud834\udd70";
        case 0x1D163:
            return "\ud834\udd5f\ud834\udd71";
        case 0x1D164:
            return "\ud834\udd5f\ud834\udd72";
        case 0x1D1BB:
            return "\ud834\uddb9\ud834\udd65";
        case 0x1D1BC:
            return "\ud834\uddba\ud834\udd65";
        case 0x1D1BD:
            return "\ud834\uddbb\ud834\udd6e";
        case 0x1D1BE:
            return "\ud834\uddbc\ud834\udd6e";
        case 0x1D1BF:
            return "\ud834\uddbb\ud834\udd6f";
        case 0x1D1C0:
            return "\ud834\uddbc\ud834\udd6f";
        case 0x2F800:
            return "\u4E3D";
        case 0x2F801:
            return "\u4E38";
        case 0x2F802:
            return "\u4E41";
        case 0x2F803:
            return "\ud840\udd22";
        case 0x2F804:
            return "\u4F60";
        case 0x2F805:
            return "\u4FAE";
        case 0x2F806:
            return "\u4FBB";
        case 0x2F807:
            return "\u5002";
        case 0x2F808:
            return "\u507A";
        case 0x2F809:
            return "\u5099";
        case 0x2F80A:
            return "\u50E7";
        case 0x2F80B:
            return "\u50CF";
        case 0x2F80C:
            return "\u349E";
        case 0x2F80D:
            return "\ud841\ude3a";
        case 0x2F80E:
            return "\u514D";
        case 0x2F80F:
            return "\u5154";
        case 0x2F810:
            return "\u5164";
        case 0x2F811:
            return "\u5177";
        case 0x2F812:
            return "\ud841\udd1c";
        case 0x2F813:
            return "\u34B9";
        case 0x2F814:
            return "\u5167";
        case 0x2F815:
            return "\u518D";
        case 0x2F816:
            return "\ud841\udd4b";
        case 0x2F817:
            return "\u5197";
        case 0x2F818:
            return "\u51A4";
        case 0x2F819:
            return "\u4ECC";
        case 0x2F81A:
            return "\u51AC";
        case 0x2F81B:
            return "\u51B5";
        case 0x2F81C:
            return "\ud864\udddf";
        case 0x2F81D:
            return "\u51F5";
        case 0x2F81E:
            return "\u5203";
        case 0x2F81F:
            return "\u34DF";
        case 0x2F820:
            return "\u523B";
        case 0x2F821:
            return "\u5246";
        case 0x2F822:
            return "\u5272";
        case 0x2F823:
            return "\u5277";
        case 0x2F824:
            return "\u3515";
        case 0x2F825:
            return "\u52C7";
        case 0x2F826:
            return "\u52C9";
        case 0x2F827:
            return "\u52E4";
        case 0x2F828:
            return "\u52FA";
        case 0x2F829:
            return "\u5305";
        case 0x2F82A:
            return "\u5306";
        case 0x2F82B:
            return "\u5317";
        case 0x2F82C:
            return "\u5349";
        case 0x2F82D:
            return "\u5351";
        case 0x2F82E:
            return "\u535A";
        case 0x2F82F:
            return "\u5373";
        case 0x2F830:
            return "\u537D";
        case 0x2F831:
            return "\u537F";
        case 0x2F832:
            return "\u537F";
        case 0x2F833:
            return "\u537F";
        case 0x2F834:
            return "\ud842\ude2c";
        case 0x2F835:
            return "\u7070";
        case 0x2F836:
            return "\u53CA";
        case 0x2F837:
            return "\u53DF";
        case 0x2F838:
            return "\ud842\udf63";
        case 0x2F839:
            return "\u53EB";
        case 0x2F83A:
            return "\u53F1";
        case 0x2F83B:
            return "\u5406";
        case 0x2F83C:
            return "\u549E";
        case 0x2F83D:
            return "\u5438";
        case 0x2F83E:
            return "\u5448";
        case 0x2F83F:
            return "\u5468";
        case 0x2F840:
            return "\u54A2";
        case 0x2F841:
            return "\u54F6";
        case 0x2F842:
            return "\u5510";
        case 0x2F843:
            return "\u5553";
        case 0x2F844:
            return "\u5563";
        case 0x2F845:
            return "\u5584";
        case 0x2F846:
            return "\u5584";
        case 0x2F847:
            return "\u5599";
        case 0x2F848:
            return "\u55AB";
        case 0x2F849:
            return "\u55B3";
        case 0x2F84A:
            return "\u55C2";
        case 0x2F84B:
            return "\u5716";
        case 0x2F84C:
            return "\u5606";
        case 0x2F84D:
            return "\u5717";
        case 0x2F84E:
            return "\u5651";
        case 0x2F84F:
            return "\u5674";
        case 0x2F850:
            return "\u5207";
        case 0x2F851:
            return "\u58EE";
        case 0x2F852:
            return "\u57CE";
        case 0x2F853:
            return "\u57F4";
        case 0x2F854:
            return "\u580D";
        case 0x2F855:
            return "\u578B";
        case 0x2F856:
            return "\u5832";
        case 0x2F857:
            return "\u5831";
        case 0x2F858:
            return "\u58AC";
        case 0x2F859:
            return "\ud845\udce4";
        case 0x2F85A:
            return "\u58F2";
        case 0x2F85B:
            return "\u58F7";
        case 0x2F85C:
            return "\u5906";
        case 0x2F85D:
            return "\u591A";
        case 0x2F85E:
            return "\u5922";
        case 0x2F85F:
            return "\u5962";
        case 0x2F860:
            return "\ud845\udea8";
        case 0x2F861:
            return "\ud845\udeea";
        case 0x2F862:
            return "\u59EC";
        case 0x2F863:
            return "\u5A1B";
        case 0x2F864:
            return "\u5A27";
        case 0x2F865:
            return "\u59D8";
        case 0x2F866:
            return "\u5A66";
        case 0x2F867:
            return "\u36EE";
        case 0x2F868:
            return "\u36FC";
        case 0x2F869:
            return "\u5B08";
        case 0x2F86A:
            return "\u5B3E";
        case 0x2F86B:
            return "\u5B3E";
        case 0x2F86C:
            return "\ud846\uddc8";
        case 0x2F86D:
            return "\u5BC3";
        case 0x2F86E:
            return "\u5BD8";
        case 0x2F86F:
            return "\u5BE7";
        case 0x2F870:
            return "\u5BF3";
        case 0x2F871:
            return "\ud846\udf18";
        case 0x2F872:
            return "\u5BFF";
        case 0x2F873:
            return "\u5C06";
        case 0x2F874:
            return "\u5F53";
        case 0x2F875:
            return "\u5C22";
        case 0x2F876:
            return "\u3781";
        case 0x2F877:
            return "\u5C60";
        case 0x2F878:
            return "\u5C6E";
        case 0x2F879:
            return "\u5CC0";
        case 0x2F87A:
            return "\u5C8D";
        case 0x2F87B:
            return "\ud847\udde4";
        case 0x2F87C:
            return "\u5D43";
        case 0x2F87D:
            return "\ud847\udde6";
        case 0x2F87E:
            return "\u5D6E";
        case 0x2F87F:
            return "\u5D6B";
        case 0x2F880:
            return "\u5D7C";
        case 0x2F881:
            return "\u5DE1";
        case 0x2F882:
            return "\u5DE2";
        case 0x2F883:
            return "\u382F";
        case 0x2F884:
            return "\u5DFD";
        case 0x2F885:
            return "\u5E28";
        case 0x2F886:
            return "\u5E3D";
        case 0x2F887:
            return "\u5E69";
        case 0x2F888:
            return "\u3862";
        case 0x2F889:
            return "\ud848\udd83";
        case 0x2F88A:
            return "\u387C";
        case 0x2F88B:
            return "\u5EB0";
        case 0x2F88C:
            return "\u5EB3";
        case 0x2F88D:
            return "\u5EB6";
        case 0x2F88E:
            return "\u5ECA";
        case 0x2F88F:
            return "\ud868\udf92";
        case 0x2F890:
            return "\u5EFE";
        case 0x2F891:
            return "\ud848\udf31";
        case 0x2F892:
            return "\ud848\udf31";
        case 0x2F893:
            return "\u8201";
        case 0x2F894:
            return "\u5F22";
        case 0x2F895:
            return "\u5F22";
        case 0x2F896:
            return "\u38C7";
        case 0x2F897:
            return "\ud84c\udeb8";
        case 0x2F898:
            return "\ud858\uddda";
        case 0x2F899:
            return "\u5F62";
        case 0x2F89A:
            return "\u5F6B";
        case 0x2F89B:
            return "\u38E3";
        case 0x2F89C:
            return "\u5F9A";
        case 0x2F89D:
            return "\u5FCD";
        case 0x2F89E:
            return "\u5FD7";
        case 0x2F89F:
            return "\u5FF9";
        case 0x2F8A0:
            return "\u6081";
        case 0x2F8A1:
            return "\u393A";
        case 0x2F8A2:
            return "\u391C";
        case 0x2F8A3:
            return "\u6094";
        case 0x2F8A4:
            return "\ud849\uded4";
        case 0x2F8A5:
            return "\u60C7";
        case 0x2F8A6:
            return "\u6148";
        case 0x2F8A7:
            return "\u614C";
        case 0x2F8A8:
            return "\u614E";
        case 0x2F8A9:
            return "\u614C";
        case 0x2F8AA:
            return "\u617A";
        case 0x2F8AB:
            return "\u618E";
        case 0x2F8AC:
            return "\u61B2";
        case 0x2F8AD:
            return "\u61A4";
        case 0x2F8AE:
            return "\u61AF";
        case 0x2F8AF:
            return "\u61DE";
        case 0x2F8B0:
            return "\u61F2";
        case 0x2F8B1:
            return "\u61F6";
        case 0x2F8B2:
            return "\u6210";
        case 0x2F8B3:
            return "\u621B";
        case 0x2F8B4:
            return "\u625D";
        case 0x2F8B5:
            return "\u62B1";
        case 0x2F8B6:
            return "\u62D4";
        case 0x2F8B7:
            return "\u6350";
        case 0x2F8B8:
            return "\ud84a\udf0c";
        case 0x2F8B9:
            return "\u633D";
        case 0x2F8BA:
            return "\u62FC";
        case 0x2F8BB:
            return "\u6368";
        case 0x2F8BC:
            return "\u6383";
        case 0x2F8BD:
            return "\u63E4";
        case 0x2F8BE:
            return "\ud84a\udff1";
        case 0x2F8BF:
            return "\u6422";
        case 0x2F8C0:
            return "\u63C5";
        case 0x2F8C1:
            return "\u63A9";
        case 0x2F8C2:
            return "\u3A2E";
        case 0x2F8C3:
            return "\u6469";
        case 0x2F8C4:
            return "\u647E";
        case 0x2F8C5:
            return "\u649D";
        case 0x2F8C6:
            return "\u6477";
        case 0x2F8C7:
            return "\u3A6C";
        case 0x2F8C8:
            return "\u654F";
        case 0x2F8C9:
            return "\u656C";
        case 0x2F8CA:
            return "\ud84c\udc0a";
        case 0x2F8CB:
            return "\u65E3";
        case 0x2F8CC:
            return "\u66F8";
        case 0x2F8CD:
            return "\u6649";
        case 0x2F8CE:
            return "\u3B19";
        case 0x2F8CF:
            return "\u6691";
        case 0x2F8D0:
            return "\u3B08";
        case 0x2F8D1:
            return "\u3AE4";
        case 0x2F8D2:
            return "\u5192";
        case 0x2F8D3:
            return "\u5195";
        case 0x2F8D4:
            return "\u6700";
        case 0x2F8D5:
            return "\u669C";
        case 0x2F8D6:
            return "\u80AD";
        case 0x2F8D7:
            return "\u43D9";
        case 0x2F8D8:
            return "\u6717";
        case 0x2F8D9:
            return "\u671B";
        case 0x2F8DA:
            return "\u6721";
        case 0x2F8DB:
            return "\u675E";
        case 0x2F8DC:
            return "\u6753";
        case 0x2F8DD:
            return "\ud84c\udfc3";
        case 0x2F8DE:
            return "\u3B49";
        case 0x2F8DF:
            return "\u67FA";
        case 0x2F8E0:
            return "\u6785";
        case 0x2F8E1:
            return "\u6852";
        case 0x2F8E2:
            return "\u6885";
        case 0x2F8E3:
            return "\ud84d\udc6d";
        case 0x2F8E4:
            return "\u688E";
        case 0x2F8E5:
            return "\u681F";
        case 0x2F8E6:
            return "\u6914";
        case 0x2F8E7:
            return "\u3B9D";
        case 0x2F8E8:
            return "\u6942";
        case 0x2F8E9:
            return "\u69A3";
        case 0x2F8EA:
            return "\u69EA";
        case 0x2F8EB:
            return "\u6AA8";
        case 0x2F8EC:
            return "\ud84d\udea3";
        case 0x2F8ED:
            return "\u6ADB";
        case 0x2F8EE:
            return "\u3C18";
        case 0x2F8EF:
            return "\u6B21";
        case 0x2F8F0:
            return "\ud84e\udca7";
        case 0x2F8F1:
            return "\u6B54";
        case 0x2F8F2:
            return "\u3C4E";
        case 0x2F8F3:
            return "\u6B72";
        case 0x2F8F4:
            return "\u6B9F";
        case 0x2F8F5:
            return "\u6BBA";
        case 0x2F8F6:
            return "\u6BBB";
        case 0x2F8F7:
            return "\ud84e\ude8d";
        case 0x2F8F8:
            return "\ud847\udd0b";
        case 0x2F8F9:
            return "\ud84e\udefa";
        case 0x2F8FA:
            return "\u6C4E";
        case 0x2F8FB:
            return "\ud84f\udcbc";
        case 0x2F8FC:
            return "\u6CBF";
        case 0x2F8FD:
            return "\u6CCD";
        case 0x2F8FE:
            return "\u6C67";
        case 0x2F8FF:
            return "\u6D16";
        case 0x2F900:
            return "\u6D3E";
        case 0x2F901:
            return "\u6D77";
        case 0x2F902:
            return "\u6D41";
        case 0x2F903:
            return "\u6D69";
        case 0x2F904:
            return "\u6D78";
        case 0x2F905:
            return "\u6D85";
        case 0x2F906:
            return "\ud84f\udd1e";
        case 0x2F907:
            return "\u6D34";
        case 0x2F908:
            return "\u6E2F";
        case 0x2F909:
            return "\u6E6E";
        case 0x2F90A:
            return "\u3D33";
        case 0x2F90B:
            return "\u6ECB";
        case 0x2F90C:
            return "\u6EC7";
        case 0x2F90D:
            return "\ud84f\uded1";
        case 0x2F90E:
            return "\u6DF9";
        case 0x2F90F:
            return "\u6F6E";
        case 0x2F910:
            return "\ud84f\udf5e";
        case 0x2F911:
            return "\ud84f\udf8e";
        case 0x2F912:
            return "\u6FC6";
        case 0x2F913:
            return "\u7039";
        case 0x2F914:
            return "\u701E";
        case 0x2F915:
            return "\u701B";
        case 0x2F916:
            return "\u3D96";
        case 0x2F917:
            return "\u704A";
        case 0x2F918:
            return "\u707D";
        case 0x2F919:
            return "\u7077";
        case 0x2F91A:
            return "\u70AD";
        case 0x2F91B:
            return "\ud841\udd25";
        case 0x2F91C:
            return "\u7145";
        case 0x2F91D:
            return "\ud850\ude63";
        case 0x2F91E:
            return "\u719C";
        case 0x2F91F:
            return "\ud850\udfab";
        case 0x2F920:
            return "\u7228";
        case 0x2F921:
            return "\u7235";
        case 0x2F922:
            return "\u7250";
        case 0x2F923:
            return "\ud851\ude08";
        case 0x2F924:
            return "\u7280";
        case 0x2F925:
            return "\u7295";
        case 0x2F926:
            return "\ud851\udf35";
        case 0x2F927:
            return "\ud852\udc14";
        case 0x2F928:
            return "\u737A";
        case 0x2F929:
            return "\u738B";
        case 0x2F92A:
            return "\u3EAC";
        case 0x2F92B:
            return "\u73A5";
        case 0x2F92C:
            return "\u3EB8";
        case 0x2F92D:
            return "\u3EB8";
        case 0x2F92E:
            return "\u7447";
        case 0x2F92F:
            return "\u745C";
        case 0x2F930:
            return "\u7471";
        case 0x2F931:
            return "\u7485";
        case 0x2F932:
            return "\u74CA";
        case 0x2F933:
            return "\u3F1B";
        case 0x2F934:
            return "\u7524";
        case 0x2F935:
            return "\ud853\udc36";
        case 0x2F936:
            return "\u753E";
        case 0x2F937:
            return "\ud853\udc92";
        case 0x2F938:
            return "\u7570";
        case 0x2F939:
            return "\ud848\udd9f";
        case 0x2F93A:
            return "\u7610";
        case 0x2F93B:
            return "\ud853\udfa1";
        case 0x2F93C:
            return "\ud853\udfb8";
        case 0x2F93D:
            return "\ud854\udc44";
        case 0x2F93E:
            return "\u3FFC";
        case 0x2F93F:
            return "\u4008";
        case 0x2F940:
            return "\u76F4";
        case 0x2F941:
            return "\ud854\udcf3";
        case 0x2F942:
            return "\ud854\udcf2";
        case 0x2F943:
            return "\ud854\udd19";
        case 0x2F944:
            return "\ud854\udd33";
        case 0x2F945:
            return "\u771E";
        case 0x2F946:
            return "\u771F";
        case 0x2F947:
            return "\u771F";
        case 0x2F948:
            return "\u774A";
        case 0x2F949:
            return "\u4039";
        case 0x2F94A:
            return "\u778B";
        case 0x2F94B:
            return "\u4046";
        case 0x2F94C:
            return "\u4096";
        case 0x2F94D:
            return "\ud855\udc1d";
        case 0x2F94E:
            return "\u784E";
        case 0x2F94F:
            return "\u788C";
        case 0x2F950:
            return "\u78CC";
        case 0x2F951:
            return "\u40E3";
        case 0x2F952:
            return "\ud855\ude26";
        case 0x2F953:
            return "\u7956";
        case 0x2F954:
            return "\ud855\ude9a";
        case 0x2F955:
            return "\ud855\udec5";
        case 0x2F956:
            return "\u798F";
        case 0x2F957:
            return "\u79EB";
        case 0x2F958:
            return "\u412F";
        case 0x2F959:
            return "\u7A40";
        case 0x2F95A:
            return "\u7A4A";
        case 0x2F95B:
            return "\u7A4F";
        case 0x2F95C:
            return "\ud856\udd7c";
        case 0x2F95D:
            return "\ud856\udea7";
        case 0x2F95E:
            return "\ud856\udea7";
        case 0x2F95F:
            return "\u7AEE";
        case 0x2F960:
            return "\u4202";
        case 0x2F961:
            return "\ud856\udfab";
        case 0x2F962:
            return "\u7BC6";
        case 0x2F963:
            return "\u7BC9";
        case 0x2F964:
            return "\u4227";
        case 0x2F965:
            return "\ud857\udc80";
        case 0x2F966:
            return "\u7CD2";
        case 0x2F967:
            return "\u42A0";
        case 0x2F968:
            return "\u7CE8";
        case 0x2F969:
            return "\u7CE3";
        case 0x2F96A:
            return "\u7D00";
        case 0x2F96B:
            return "\ud857\udf86";
        case 0x2F96C:
            return "\u7D63";
        case 0x2F96D:
            return "\u4301";
        case 0x2F96E:
            return "\u7DC7";
        case 0x2F96F:
            return "\u7E02";
        case 0x2F970:
            return "\u7E45";
        case 0x2F971:
            return "\u4334";
        case 0x2F972:
            return "\ud858\ude28";
        case 0x2F973:
            return "\ud858\ude47";
        case 0x2F974:
            return "\u4359";
        case 0x2F975:
            return "\ud858\uded9";
        case 0x2F976:
            return "\u7F7A";
        case 0x2F977:
            return "\ud858\udf3e";
        case 0x2F978:
            return "\u7F95";
        case 0x2F979:
            return "\u7FFA";
        case 0x2F97A:
            return "\u8005";
        case 0x2F97B:
            return "\ud859\udcda";
        case 0x2F97C:
            return "\ud859\udd23";
        case 0x2F97D:
            return "\u8060";
        case 0x2F97E:
            return "\ud859\udda8";
        case 0x2F97F:
            return "\u8070";
        case 0x2F980:
            return "\ud84c\udf5f";
        case 0x2F981:
            return "\u43D5";
        case 0x2F982:
            return "\u80B2";
        case 0x2F983:
            return "\u8103";
        case 0x2F984:
            return "\u440B";
        case 0x2F985:
            return "\u813E";
        case 0x2F986:
            return "\u5AB5";
        case 0x2F987:
            return "\ud859\udfa7";
        case 0x2F988:
            return "\ud859\udfb5";
        case 0x2F989:
            return "\ud84c\udf93";
        case 0x2F98A:
            return "\ud84c\udf9c";
        case 0x2F98B:
            return "\u8201";
        case 0x2F98C:
            return "\u8204";
        case 0x2F98D:
            return "\u8F9E";
        case 0x2F98E:
            return "\u446B";
        case 0x2F98F:
            return "\u8291";
        case 0x2F990:
            return "\u828B";
        case 0x2F991:
            return "\u829D";
        case 0x2F992:
            return "\u52B3";
        case 0x2F993:
            return "\u82B1";
        case 0x2F994:
            return "\u82B3";
        case 0x2F995:
            return "\u82BD";
        case 0x2F996:
            return "\u82E6";
        case 0x2F997:
            return "\ud85a\udf3c";
        case 0x2F998:
            return "\u82E5";
        case 0x2F999:
            return "\u831D";
        case 0x2F99A:
            return "\u8363";
        case 0x2F99B:
            return "\u83AD";
        case 0x2F99C:
            return "\u8323";
        case 0x2F99D:
            return "\u83BD";
        case 0x2F99E:
            return "\u83E7";
        case 0x2F99F:
            return "\u8457";
        case 0x2F9A0:
            return "\u8353";
        case 0x2F9A1:
            return "\u83CA";
        case 0x2F9A2:
            return "\u83CC";
        case 0x2F9A3:
            return "\u83DC";
        case 0x2F9A4:
            return "\ud85b\udc36";
        case 0x2F9A5:
            return "\ud85b\udd6b";
        case 0x2F9A6:
            return "\ud85b\udcd5";
        case 0x2F9A7:
            return "\u452B";
        case 0x2F9A8:
            return "\u84F1";
        case 0x2F9A9:
            return "\u84F3";
        case 0x2F9AA:
            return "\u8516";
        case 0x2F9AB:
            return "\ud85c\udfca";
        case 0x2F9AC:
            return "\u8564";
        case 0x2F9AD:
            return "\ud85b\udf2c";
        case 0x2F9AE:
            return "\u455D";
        case 0x2F9AF:
            return "\u4561";
        case 0x2F9B0:
            return "\ud85b\udfb1";
        case 0x2F9B1:
            return "\ud85c\udcd2";
        case 0x2F9B2:
            return "\u456B";
        case 0x2F9B3:
            return "\u8650";
        case 0x2F9B4:
            return "\u865C";
        case 0x2F9B5:
            return "\u8667";
        case 0x2F9B6:
            return "\u8669";
        case 0x2F9B7:
            return "\u86A9";
        case 0x2F9B8:
            return "\u8688";
        case 0x2F9B9:
            return "\u870E";
        case 0x2F9BA:
            return "\u86E2";
        case 0x2F9BB:
            return "\u8779";
        case 0x2F9BC:
            return "\u8728";
        case 0x2F9BD:
            return "\u876B";
        case 0x2F9BE:
            return "\u8786";
        case 0x2F9BF:
            return "\u45D7";
        case 0x2F9C0:
            return "\u87E1";
        case 0x2F9C1:
            return "\u8801";
        case 0x2F9C2:
            return "\u45F9";
        case 0x2F9C3:
            return "\u8860";
        case 0x2F9C4:
            return "\u8863";
        case 0x2F9C5:
            return "\ud85d\ude67";
        case 0x2F9C6:
            return "\u88D7";
        case 0x2F9C7:
            return "\u88DE";
        case 0x2F9C8:
            return "\u4635";
        case 0x2F9C9:
            return "\u88FA";
        case 0x2F9CA:
            return "\u34BB";
        case 0x2F9CB:
            return "\ud85e\udcae";
        case 0x2F9CC:
            return "\ud85e\udd66";
        case 0x2F9CD:
            return "\u46BE";
        case 0x2F9CE:
            return "\u46C7";
        case 0x2F9CF:
            return "\u8AA0";
        case 0x2F9D0:
            return "\u8AED";
        case 0x2F9D1:
            return "\u8B8A";
        case 0x2F9D2:
            return "\u8C55";
        case 0x2F9D3:
            return "\ud85f\udca8";
        case 0x2F9D4:
            return "\u8CAB";
        case 0x2F9D5:
            return "\u8CC1";
        case 0x2F9D6:
            return "\u8D1B";
        case 0x2F9D7:
            return "\u8D77";
        case 0x2F9D8:
            return "\ud85f\udf2f";
        case 0x2F9D9:
            return "\ud842\udc04";
        case 0x2F9DA:
            return "\u8DCB";
        case 0x2F9DB:
            return "\u8DBC";
        case 0x2F9DC:
            return "\u8DF0";
        case 0x2F9DD:
            return "\ud842\udcde";
        case 0x2F9DE:
            return "\u8ED4";
        case 0x2F9DF:
            return "\u8F38";
        case 0x2F9E0:
            return "\ud861\uddd2";
        case 0x2F9E1:
            return "\ud861\udded";
        case 0x2F9E2:
            return "\u9094";
        case 0x2F9E3:
            return "\u90F1";
        case 0x2F9E4:
            return "\u9111";
        case 0x2F9E5:
            return "\ud861\udf2e";
        case 0x2F9E6:
            return "\u911B";
        case 0x2F9E7:
            return "\u9238";
        case 0x2F9E8:
            return "\u92D7";
        case 0x2F9E9:
            return "\u92D8";
        case 0x2F9EA:
            return "\u927C";
        case 0x2F9EB:
            return "\u93F9";
        case 0x2F9EC:
            return "\u9415";
        case 0x2F9ED:
            return "\ud862\udffa";
        case 0x2F9EE:
            return "\u958B";
        case 0x2F9EF:
            return "\u4995";
        case 0x2F9F0:
            return "\u95B7";
        case 0x2F9F1:
            return "\ud863\udd77";
        case 0x2F9F2:
            return "\u49E6";
        case 0x2F9F3:
            return "\u96C3";
        case 0x2F9F4:
            return "\u5DB2";
        case 0x2F9F5:
            return "\u9723";
        case 0x2F9F6:
            return "\ud864\udd45";
        case 0x2F9F7:
            return "\ud864\ude1a";
        case 0x2F9F8:
            return "\u4A6E";
        case 0x2F9F9:
            return "\u4A76";
        case 0x2F9FA:
            return "\u97E0";
        case 0x2F9FB:
            return "\ud865\udc0a";
        case 0x2F9FC:
            return "\u4AB2";
        case 0x2F9FD:
            return "\ud865\udc96";
        case 0x2F9FE:
            return "\u980B";
        case 0x2F9FF:
            return "\u980B";
        case 0x2FA00:
            return "\u9829";
        case 0x2FA01:
            return "\ud865\uddb6";
        case 0x2FA02:
            return "\u98E2";
        case 0x2FA03:
            return "\u4B33";
        case 0x2FA04:
            return "\u9929";
        case 0x2FA05:
            return "\u99A7";
        case 0x2FA06:
            return "\u99C2";
        case 0x2FA07:
            return "\u99FE";
        case 0x2FA08:
            return "\u4BCE";
        case 0x2FA09:
            return "\ud866\udf30";
        case 0x2FA0A:
            return "\u9B12";
        case 0x2FA0B:
            return "\u9C40";
        case 0x2FA0C:
            return "\u9CFD";
        case 0x2FA0D:
            return "\u4CCE";
        case 0x2FA0E:
            return "\u4CED";
        case 0x2FA0F:
            return "\u9D67";
        case 0x2FA10:
            return "\ud868\udcce";
        case 0x2FA11:
            return "\u4CF8";
        case 0x2FA12:
            return "\ud868\udd05";
        case 0x2FA13:
            return "\ud868\ude0e";
        case 0x2FA14:
            return "\ud868\ude91";
        case 0x2FA15:
            return "\u9EBB";
        case 0x2FA16:
            return "\u4D56";
        case 0x2FA17:
            return "\u9EF9";
        case 0x2FA18:
            return "\u9EFE";
        case 0x2FA19:
            return "\u9F05";
        case 0x2FA1A:
            return "\u9F0F";
        case 0x2FA1B:
            return "\u9F16";
        case 0x2FA1C:
            return "\u9F3B";
        case 0x2FA1D:
            return "\ud869\ude00";
        default: // not decomposable
            if (character <= 0xFFFF) {
                return String.valueOf((char) character);
             }
             else {
                 StringBuffer sb = new StringBuffer(2);
                 sb.append(getHighSurrogate(character));
                 sb.append(getLowSurrogate(character));
                 return sb.toString();
             }
        }
        
    }
    ///CLOVER:ON


    private static String decomposeHangul(char c) {
        
        final int firstLeadingConsonant  = 0x1100;
        final int firstMedialVowel       = 0x1161;
        final int firstTrailingConsonant = 0x11A7;
        
        final int numberOfLeadingConsonants  = 19;
        final int numberOfMedialVowels       = 21;
        final int numberOfTrailingConsonants = 28;
        
        final int numberOfFinalPairs 
          = numberOfMedialVowels * numberOfTrailingConsonants; 
        final int numberOfSyllables 
          = numberOfLeadingConsonants * numberOfFinalPairs;

        final int syllable = c - FIRST_HANGUL_SYLLABLE;
        
        if (syllable < 0 || syllable >= numberOfSyllables) {
            return String.valueOf(c);
        }
        
        int leadingConsonant = firstLeadingConsonant 
          + syllable / numberOfFinalPairs;
        int medialVowel = firstMedialVowel 
          + (syllable % numberOfFinalPairs) / numberOfTrailingConsonants;
        int trailingConsonant = firstTrailingConsonant 
          + syllable % numberOfTrailingConsonants;
        
        StringBuffer result = new StringBuffer(3);        
        result.append((char) leadingConsonant);
        result.append((char) medialVowel);
        if (trailingConsonant != firstTrailingConsonant) {
            result.append((char) trailingConsonant);
        }
        
        return result.toString();
        
    }   
    
    
    private static String composeHangul(String source) {

        final int firstLeadingConsonant = 0x1100;
        final int firstMedialVowel = 0x1161;
        final int firstTrailingConsonant = 0x11A7;
        
        final int numberOfLeadingConsonants  = 19;
        final int numberOfMedialVowels       = 21;
        final int numberOfTrailingConsonants = 28;
        
        final int numberOfFinalPairs 
          = numberOfMedialVowels * numberOfTrailingConsonants;
        final int numberOfSyllables 
          = numberOfLeadingConsonants * numberOfFinalPairs;
        
        final int length = source.length();
        if (length == 0) return "";
        StringBuffer result = new StringBuffer(length);
        char previous = source.charAt(0); 
        result.append(previous);

        for (int i = 1; i < length; ++i) {
            char c = source.charAt(i);

            int leadingConsonant = previous - firstLeadingConsonant;
            if (0 <= leadingConsonant && leadingConsonant < numberOfLeadingConsonants) {
                int medialVowel = c - firstMedialVowel;
                if (medialVowel >= 0 && medialVowel < numberOfMedialVowels) {
                    previous = (char) (FIRST_HANGUL_SYLLABLE 
                      + (leadingConsonant * numberOfMedialVowels + medialVowel) 
                      * numberOfTrailingConsonants);
                    result.setCharAt(result.length()-1, previous);
                    continue; 
                }
            }

            int syllable = previous - FIRST_HANGUL_SYLLABLE;
            if (syllable >= 0 && syllable < numberOfSyllables 
              && (syllable % numberOfTrailingConsonants) == 0) {
                int trailingConsonant = c - firstTrailingConsonant;
                if (trailingConsonant >= 0 && trailingConsonant <= numberOfTrailingConsonants) {
                    previous += trailingConsonant;
                    result.setCharAt(result.length()-1, previous);
                    continue; 
                }
            }

            previous = c;
            result.append(c);
        }
        
        return result.toString();
        
    } 
 
    
    private static class UnicodeString {
        
        private int[] data;
        private int   size = 0; 
        
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
            this.size = index;
            
        }
        
        
        UnicodeString(int length) {
            this.size = 0;
            data = new int[length];
        }
        
        
        UnicodeString decompose() {
            
            UnicodeString result = new UnicodeString(size);
            for (int i = 0; i < size; i++) {
                int c = data[i];
                String d = UnicodeUtil.decompose(c);
                result.append(d);
            }    
            
            /* now put into canonical order */

            for (int i = 0; i < result.size-1; i++) {
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
            
            UnicodeString composed = new UnicodeString(size);
    
            int lastStarter = -1;
            int lastStarterIndex = -1;
            int composedLastStarterIndex = -1;
            
            for (int i = 0; i < size; i++) {
                int c = data[i];
                if (lastStarter == -1 || isBlocked(lastStarterIndex, i)) {
                    composed.append(c);
                    if (isStarter(c) ) {
                        lastStarter = c;
                        lastStarterIndex = i;
                        composedLastStarterIndex = composed.size-1;
                    }
                }
                else  {
                    int composedChar = composeCharacter(lastStarter, c);
                    if (composedChar == -1) {
                        composed.append(c);
                        if (isStarter(c) ) {
                            lastStarter = c;
                            lastStarterIndex = i;
                            composedLastStarterIndex = composed.size-1;
                        }
                    }
                    else {
                        lastStarter = composedChar;
                        // XXX dangerous side effects
                        data[lastStarterIndex] = composedChar;
                        data[i] = 0;
                        composed.data[composedLastStarterIndex] = composedChar;
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
            
            if (size < data.length-1) {
                data[size] = c;
                size++;
            }
            else {
                int[] array = new int[data.length+10];
                System.arraycopy(data, 0, array, 0, size);
                data = array;
                append(c);
            }
            
        }
        
        public String toString() {
         
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < size; i++) {
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
