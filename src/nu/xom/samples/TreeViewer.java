/* Copyright 2002, 2003 Elliotte Rusty Harold
   
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

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;

/**
 * 
 * <p>
 * Demonstrates using Swing to present a graphical display of 
 * the tree structure of an XML document.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.3.3
 *
 */
public class TreeViewer {

    // Initialize the per-element data structures
    public static MutableTreeNode processElement(Element element) {

        String data;
        if (element.getNamespaceURI().equals(""))
            data = element.getLocalName();
        else {
            data =
                '{'
                    + element.getNamespaceURI()
                    + "} "
                    + element.getQualifiedName();
        }

        MutableTreeNode node = new DefaultMutableTreeNode(data);
        Elements children = element.getChildElements();
        for (int i = 0; i < children.size(); i++) {
            node.insert(processElement(children.get(i)), i);
        }

        return node;

    }

    public static void display(Document doc) {

        Element root = doc.getRootElement();
        JTree tree = new JTree(processElement(root));
        JScrollPane treeView = new JScrollPane(tree);
        JFrame f = new JFrame("XML Tree");


        String version = System.getProperty("java.version");
        if (version.startsWith("1.2") || version.startsWith("1.1")) {
            f.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE); 
        }
        else {
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
        f.getContentPane().add(treeView);
        f.pack();
        f.setVisible(true);

    }

    public static void main(String[] args) {

        try {
            Builder builder = new Builder();
            for (int i = 0; i < args.length; i++) {
                Document doc = builder.build(args[i]);
                display(doc);
            }
        }
        catch (Exception ex) {
            System.err.println(ex);
        }

    } // end main()

} // end TreeViewer