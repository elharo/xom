/* Copyright 2004 Elliotte Rusty Harold
   
   This library is free software; you can redistribute it and/or modify
   it under the terms of version 2.1 of the GNU Lesser General Public 
   License as published by the Free Software Foundation.
   
   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
   GNU Lesser General Public License for more details.
   
   You should have received a copy of the GNU Lesser General Public
   License along with this library. If not, see
   <https://www.gnu.org/licenses/>.
   
   You can contact Elliotte Rusty Harold by sending e-mail to
   elharo@ibiblio.org. Please include the word "XOM" in the
   subject line. The XOM home page is located at https://xom.nu/
*/

package nu.xom.samples;

import nu.xom.Element;
import nu.xom.Node;
import nu.xom.ParentNode;
import nu.xom.Text;

/**
 * <p>
 *  Utility methods for merging all consecutive text nodes.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0
 *
 */
public class TextMerger {

    
    public static void merge(ParentNode parent) {
        
        for (int i = 0; i < parent.getChildCount(); i++) {
            Node child = parent.getChild(i);
            if (child instanceof Text) {
            	StringBuilder sb = new StringBuilder(child.getValue());
                Node nextChild;
                while ((nextChild = parent.getChild(i+1)) instanceof Text) {
                    sb.append(nextChild.getValue());
                    parent.removeChild(nextChild);
                    if (i+1 == parent.getChildCount()) break;
                }
                if (sb.length() == 0) parent.removeChild(child);
                else parent.replaceChild(child, new Text(sb.toString()));
            }
            else if (child instanceof Element) {
                merge((ParentNode) child);
            }
        }
        
    }
    
}
