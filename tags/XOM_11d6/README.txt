XOM is a library. By itself, it doesn't do much of anything. It exists
only to be used by other programs. It requires Java 1.2 or later. 

To install it you'll need to place the XOM JAR archive somewhere in your
CLASSPATH. This archive is the file named something like xom-1.0.jar. 
(The version number may have changed if I've forgotten
to update this document.) You can either put it in your jre/lib/ext
directory, add xom-1.0.jar to your CLASSPATH environment variable, or
use the -classpath option when invoking javac and java.

To check your download you can run one of the sample programs found in
the xom-samples.jar file. For instance, nu.xom.samples.PrettyPrinter
class formats an XML document by inserting and removing white space
around element boundaries. In Java 1.4 and later you can run it from the
command line like this:

$ java -classpath xom-samples.jar:xom-1.0.jar nu.xom.samples.PrettyPrinter filename.xml

Java 1.3 and earlier do not have a built-in XML parser so in these environments you'll also need to install XOM's supporting libraries. These include xalan.jar, xercesImpl.jar, normalizer.jar, and xmlParserAPIs.jar, and are found in the lib directory. The versions shipped with XOM are quite a bit faster and less buggy than the ones bundled with the JDK, so you may well want to use them even in Java 1.4 and later. For example,

$ java -classpath xom-samples.jar:xom-1.0.jar:lib/xmlParserAPIs.jar:lib/xercesImpl.jar:lib/normalizer.jar:lib/xalan.jar nu.xom.samples.PrettyPrinter filename.xml

You could leave out xalan.jar if you don't use any of the classes in
nu.xom.xslt. normalizer.jar is needed in all versions of Java. However,
it's only actually used by the setUnicodeNormalizationFormC() method in
Serializer. If you don't call this method, you can omit this archive in
space-limited environments. junit.jar is only used for testing, and is
not needed for normal operation of XOM.

If you want to build XOM from source, you'll need to have Apache Ant
installed. See http://ant.apache.org/. Once Ant is installed, building
should be as simple as typing "ant compile" or "ant jar" in the xom
directory. Type "ant help" to see other possible options. Most of the
targets build in Ant 1.5 or later. However the betterdocs target
requires Ant 1.6, and the test target only works in Ant 1.5. It does not
work in Ant 1.6, though the testui target does.

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
