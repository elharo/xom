/* Copyright 2004, 2018 Elliotte Rusty Harold
   
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
   subject line. The XOM home page is located at http://www.xom.nu/
*/

package nu.xom.tests;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * <p>
 *  Test the main() method for the JAR file.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.3.1
 *
 */
public class InfoTest extends XOMTestCase {

    
    public InfoTest(String name) {
        super(name);
    }
 
    
    // Note the subversion of access protection
    public void testInfo() throws ClassNotFoundException, NoSuchMethodException,
      IllegalAccessException, InvocationTargetException {
        
        PrintStream systemOut = System.out;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        Class<?> info = Class.forName("nu.xom.Info");
        Class[] args = {String[].class};
        Method main = info.getMethod("main", args);
        main.setAccessible(true);
        Object[] wrappedArgs = new Object[1];
        wrappedArgs[0] = new String[0];
        main.invoke(null, wrappedArgs);
        System.out.flush();
        System.setOut(systemOut);
        String output = new String(out.toByteArray());
        assertTrue(output.indexOf("Copyright 2002") > 0);
        assertEquals(19, output.indexOf(" Elliotte Rusty Harold") - output.indexOf("Copyright 2002"));
        assertTrue(output.indexOf("http://www.xom.nu") > 0);
        assertTrue(output.indexOf("General Public License") > 0);
        assertTrue(output.indexOf("GNU") > 0);
        assertTrue(output.indexOf("WITHOUT ANY WARRANTY") > 0);
        assertTrue(output.indexOf("without even the implied warranty") > 0);
        
    }
 
    
}