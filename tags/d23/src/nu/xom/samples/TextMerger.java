// Copyright 2004 Elliotte Rusty Harold
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

package nu.xom.samples;

import nu.xom.Element;
import nu.xom.Node;
import nu.xom.ParentNode;
import nu.xom.Text;

/**
 * <p>
 *  Utility methods for merging all consaecutive text nodes.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0d23
 *
 */
public class TextMerger {

    
    public static void merge(ParentNode parent) {
        
        for (int i = 0; i < parent.getChildCount(); i++) {
            Node child = parent.getChild(i);
            if (child instanceof Text) {
                StringBuffer sb = new StringBuffer(child.getValue());
                Node nextChild;
                while ((nextChild = parent.getChild(i+1)) instanceof Text) {
                    sb.append(nextChild.getValue());
                    parent.removeChild(nextChild);
                    if (i+1 == parent.getChildCount()) break;
                }
                parent.replaceChild(child, new Text(sb.toString()));
            }
            else if (child instanceof Element) {
                merge((ParentNode) child);
            }
        }
        
    }
    

}
