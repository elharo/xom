package nu.xom.tools;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.DocType;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.ParsingException;
import nu.xom.Serializer;

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

/**
 * <p>
 * This class converts standard Sun JavaDoc to well-formed
 * XHTML. It requires the use of John Cowan's TagSoup.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0b2
 * 
 */
class XHTMLJavaDoc {
    
    private static Builder builder 
      = new Builder(new org.ccil.cowan.tagsoup.Parser());


    private static class HTMLFilter implements FileFilter {

        public boolean accept(File pathname) {
            if (pathname.getName().endsWith(".html")) return true;
            if (pathname.isDirectory()) return true;
            return false;
        }

    }
    
    
    public static void main(String[] args) {
        
        try {
            File indir = new File(args[0]);
            process(indir);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        
    }


    private static void process(File indir) {
        
        FileFilter htmlfilter = new HTMLFilter();
        if (indir.exists() && indir.isDirectory()) {
            File[] files = indir.listFiles(htmlfilter);
            for (int i = 0; i < files.length; i++) {
                File f = files[i];
                if (f.isDirectory()) {
                    process(f);
                }
                else {
                    try {
                        // use a NodeFactory that turns i's and b's into CSS????
                        Document doc = builder.build(f);
                        DocType doctype = new DocType("html", 
                          "-//W3C//DTD XHTML 1.0 Frameset//EN",
                          "http://www.w3.org/TR/2000/REC-xhtml1-20000126/DTD/xhtml1-frameset.dtd");
                        doc.setDocType(doctype);
                        Attribute en = new Attribute("lang", "en-US");
                        Attribute xmlen = new Attribute("xml:lang", 
                          "http://www.w3.org/XML/1998/namespace", "en-US");
                        Element root = doc.getRootElement();
                        root.addAttribute(en);
                        root.addAttribute(xmlen);
                        Attribute version = root.getAttribute("version");
                        if (version != null) root.removeAttribute(version);
                        Element body = root.getFirstChildElement("body", "http://www.w3.org/1999/xhtml");
                        Element frameset = root.getFirstChildElement("frameset", "http://www.w3.org/1999/xhtml");
                        if (frameset != null && body != null) {
                            root.removeChild(body);
                        }
                        Serializer serializer = new HTMLSerializer(new FileOutputStream(f));
                        serializer.write(doc);
                        serializer.flush();
                    } 
                    catch (ParsingException ex) {
                        ex.printStackTrace();
                    } 
                    catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
        else {
            System.err.println("Could not locate source directory: " + indir);
        }
        
    }
    
    private static class HTMLSerializer extends Serializer {
        
        HTMLSerializer(OutputStream out) {
            super(out);
        }
        
        protected void writeXMLDeclaration() {
        }
        
    }
    
    
    
}
