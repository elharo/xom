/* Copyright 2004 Elliotte Rusty Harold
   
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

package nu.xom.benchmarks;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;


/**
 * <p>
 *  Benchmark the performance of toXML on a large, record-like
 *  document. For convenience, the document is formed by copying one
 *  record 30,000 times. Running this benchmark normally requires 
 *  increasing Java's default heap size. 
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0
 *
 */
class ToXMLBench {

    private static String elementData = "<zazy>\n"
        + "<or>476w4l73</or>\n"
        + "<kfjjiz>6729</kfjjiz>\n"
        + "<yzsyk>N-Vgj / Yvv Zjysbeu Wwvmk @Yhwc 3=</yzsyk>\n"
        + "<xibwh>gmaj/zhffyi</xibwh>\n"
        + "<okq> RUWU: 7124 GR0Z:  20</okq>\n"
        + "<fmido phpub='3' upfylj='520'>Njm Qmskwo</fmido>\n"
        + "<fmido phpub='4' upfylj='58312'>Tgnnja Fvwlc</fmido>\n"
        + "<fmido phpub='4' upfylj='53300'>Iii Pbou Ia Yniugiq</fmido>\n"
        + "<fmido phpub='5' upfylj='07978'>Vzbpqhud Fm Ptwv</fmido>\n"
        + "<fmido phpub='6' upfylj='86987'>Fu Ffe Uxvkf</fmido>\n"
        + "<fmido phpub='6' upfylj='15968'>Kw Biv Bh Ngu Rpaw</fmido>\n"
        + "<fmido phpub='5' upfylj='94511'>Pnzm Vj Qav Tapnie</fmido>\n"
        + "<fmido phpub='9' upfylj='90284'>Pawqj Dkqxaslb</fmido>\n"
        + "<fmido phpub='9' upfylj='44613'>Hsb Aotlml Faa Vpbotugw</fmido>\n"
        + "<fmido phpub='01' upfylj='369255'>Wvex Wwlrr'o Rnbklfz</fmido>\n"
        + "<fmido phpub='45' upfylj='787725'>Sdu Edrbo</fmido>\n"
        + "<fmido phpub='52' upfylj='201908'>Urvr Ot Udbq</fmido>\n"
        + "<fmido phpub='07' upfylj='025982'>Prgovq Mrm</fmido>\n"
        + "<fmido phpub='34' upfylj='297465'>Nxr'l Zgoh Zvtl</fmido>\n"
        + "<fmido phpub='97' upfylj='389353'>Vkcixopj Ccdo</fmido>\n"
        + "<fmido phpub='51' upfylj='212507'>Dlx'h Rclq Mtxiba Gim</fmido>\n"
        + "<fmido phpub='36' upfylj='214427'>Bepj Hbrvmgp</fmido>\n"
        + "<fmido phpub='08' upfylj='523903'>Khxh Anpl</fmido>\n"
        + "</zazy>\n";
    
    
    public static void main(String[] args) throws Exception {
                
        Builder builder = new Builder();
        Document dataDoc = builder.build(
          elementData, "http://www.example.com");
        Element root = new Element("root");
        Document doc = new Document(root);
        Element dataElement = dataDoc.getRootElement();
        
        for (int i = 0; i < 20000; i++) {
            root.appendChild(dataElement.copy());
        }
        
        System.out.println("Ready to bench");
        Thread.sleep(5000);
        
        long pre = System.currentTimeMillis(); 
        String xml = doc.toXML();
        long post = System.currentTimeMillis();
        System.out.println((post - pre)/1000.0
          + "s to get XML for entire Document");
        
    }
    
}
