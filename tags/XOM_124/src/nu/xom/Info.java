/* Copyright 2002-2004, 2009 Elliotte Rusty Harold
   
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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * <p>
 * A simple class used to make the JAR archive do something sensible
 * when a user tries <samp>java -jar xom.jar</samp>.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.2.4
 * 
 */
class Info {
    
    
    public static void main(String[] args) {
    
        String version = "1.2.4 or later";
        try {
            InputStream stream = ClassLoader.getSystemResourceAsStream("nu/xom/version.txt");
            BufferedReader in = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            version = in.readLine();
        } catch (Exception ex) {
            version = "1.2.4b1 or later";
        }
        
        System.out.println("This is XOM " + version + ", a new XML Object Model.");
        System.out.println("Copyright 2002-2009 Elliotte Rusty Harold");
        System.out.println("http://www.xom.nu/");
        System.out.println();
        System.out.println("XOM is a class library intended to be used with other programs.");
        System.out.println("By itself, it doesn't really do anything.");
        System.out.println("For more information see http://www.xom.nu/");
        System.out.println();
        System.out.println("This library is free software; you can redistribute it and/or modify it");
        System.out.println("under the terms of version 2.1 of the GNU Lesser General Public License");
        System.out.println("as published by the Free Software Foundation.");
        System.out.println();
        System.out.println("This library is distributed in the hope that it will be useful,");
        System.out.println("but WITHOUT ANY WARRANTY; without even the implied warranty of");
        System.out.println("MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. ");
        System.out.println("See the GNU Lesser General Public License for more details.");
        System.out.println();
        System.out.println("You should have received a copy of the GNU Lesser General"); 
        System.out.println("Public License along with this library; if not, write to the");
        System.out.println(); 
        System.out.println("Free Software Foundation, Inc."); 
        System.out.println("59 Temple Place"); 
        System.out.println("Suite 330,"); 
        System.out.println("Boston, MA  02111-1307");
        System.out.println("USA");
        
    }

    
}
