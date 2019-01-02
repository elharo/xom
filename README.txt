XOM is a library. By itself, it doesn't do much of anything. It exists
only to be used by other programs. It requires Java 1.6 or later. 

To install it you'll need to place the XOM JAR archive somewhere in your
CLASSPATH. This archive is the file named something like xom-1.3.0.jar. 
(The version number may have changed if I've forgotten
to update this document.) You can either put it in your jre/lib/ext
directory, add xom-1.3.0.jar to your CLASSPATH environment variable, or
use the -classpath option when invoking javac and java.

To check your download you can run one of the sample programs found in
the xom-samples.jar file. For instance, nu.xom.samples.PrettyPrinter
class formats an XML document by inserting and removing white space
around element boundaries. You can run it from the
command line like this:

$ java -classpath xom-samples.jar:xom-1.3.0.jar nu.xom.samples.PrettyPrinter filename.xml

XOM's supporting libraries including xalan.jar, xercesImpl.jar, and xml-apis.jar, 
are found in the lib directory. The versions shipped with XOM 
are quite a bit faster and less buggy than the ones bundled with the JDK, 
so you may well want to use them. For example,

$ java -classpath xom-samples.jar:xom-1.3.0.jar:lib/xml-apis.jar:lib/xercesImpl.jar:lib/xalan.jar nu.xom.samples.PrettyPrinter filename.xml

You could leave out xalan.jar if you don't use any of the classes in
nu.xom.xslt. junit.jar is only used for testing, and is
not needed for normal operation of XOM.

If you want to build XOM from source, you'll need to have Apache Ant
1.6 or later installed. See http://ant.apache.org/. Once Ant is installed, building
should be as simple as typing "ant compile" or "ant jar" in the xom
directory. Type "ant help" to see other possible options.

=======================================================================
XOM is Copyright 2004, 2005, 2009, 2018, 2019 Elliotte Rusty Harold
   
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
subject line. For more information see https://xom.nu/ 
or ask a question on the xom-interest mailing list.
