/* Copyright 2002-2004, 2009 Elliotte Rusty Harold
   
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
 * @version 1.3.9
 * 
 */
class Info {
    
    
    public static void main(String[] args) {
    
        String version = "1.3.9 or later";
        try {
            InputStream stream = ClassLoader.getSystemResourceAsStream("nu/xom/version.txt");
            BufferedReader in = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            version = in.readLine();
        } catch (Exception ex) {
            version = "1.3.9 or later";
        }
        
        System.out.println("This is XOM " + version + ", a new XML Object Model.");
        System.out.println("Copyright 2002-2009 Elliotte Rusty Harold");
        System.out.println("https://xom.nu/");
        System.out.println();
        System.out.println("XOM is a class library intended to be used with other programs.");
        System.out.println("By itself, it doesn't really do anything.");
        System.out.println("For more information see https://xom.nu/");
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
        System.out.println("Public License along with this library. If not, see");
        System.out.println("<https://www.gnu.org/licenses/>");
        
    }

    
}
