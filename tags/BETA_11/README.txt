XOM is a library. By itself, it doesn't do much of anything. It exists
only to be used by other programs.

To install it you'll need to place the XOM JAR archive somewhere in your
CLASSPATH. This is the file named something lile xom-1.0.jar or
xom-1.0b11.jar. (The version number may have changed if I've forgotten
to update this document.)

You can either put it in your jre/lib/ext directory, add xom-1.0.jar to
your CLASSPATH environment variable, or use the -classpath option when
invoking javac and java. For example,

java -classpath .:xom-1.0.jar YourClassName

I also recommend installing XOM's supporting libraries, which you'll
find in the lib directory. These include xalan.jar, xercesImpl.jar,
normalizer.jar, and xmlParserAPIs.jar. In Java 1.4 and later, you can
skip xercesImpl.jar and xmlParserAPIs.jar. However, without them XOM
will rely on Java 1.4's built-in Crimson parser, which is quite a bit
buggier and slower than Xerces. Java 1.5 bundles Xerces. However, the
version included with XOM has fixed a few bugs. xalan.jar is optional in
Java 1.4 and later, though again the version bundled with XOM is quite a
bit less buggy than the one bundled with the JDK. In Java 1.3 and
earlier, all these jar files are required. (You could leave out
xalan.jar if you don't use any of the classes in nu.xom.xslt.)

normalizer.jar is needed in all versions of Java. However, it's only
actually used by the setUnicodeNormalizationFormC() method in Serializer. 
If you don't call this method, you can omit this archive in 
space-limited environments.

junit.jar is only used for testing, and is not needed for normal
operation of XOM.

If you want to build XOM from source, you'll need to have Apache Ant
installed. See http://ant.apache.org/. Once that's installed, building
should be as simple as typing "ant compile" or "ant jar" in the xom
directory. Type "ant help" to see other possible options. 

=======================================================================
XOM is Copyright 2004 Elliotte Rusty Harold
   
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
subject line. For more information see http://www.xom.nu/ 
or ask a question on the xom-interest mailing list.
