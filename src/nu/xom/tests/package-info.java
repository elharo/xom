/*
 * Copyright 2002, 2004, 2005 Elliotte Rusty Harold
 *   
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of version 2.1 of 
 * the GNU Lesser General Public License as published by the 
 * Free Software Foundation.
 *   
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *   
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *   
 * You can contact Elliotte Rusty Harold by sending e-mail to
 * elharo@ibiblio.org. Please include the word "XOM" in the
 * subject line. The XOM home page is http://www.xom.nu/
 */

/**
 * <p>
 * <code>nu.xom.tests</code> contains the <a href="https://junit.org/junit4/">JUnit</a>
 * based test suite for XOM. JUnit 3.7 or later is required.
 * (Earlier versions don't have the <code>assertTrue</code> method
 * these tests depend on.)
 * </p>
 * 
 * <p>
 * The <code>XOMTests</code> class runs all the tests, except those that
 * take an exceptionally long time to run (<code>MegaTest</code>).
 * Some of the tests, especially the builder tests,
 * depend on the underlying parser, and are known to fail 
 * if the parser is buggy. Xerces 2.6.1 is the only parser which is currently
 * known to be able to run all the tests. Earlier versions of Xerces 2.x
 * cannot successfully run all the tests.
 * The IBM JVM 1.4.1 bundles an earlier version of Xerces which overrides the one bundled with XOM.
 * For this reason, a few tests will fail when using that VM. These tests should pass when using the
 * Sun VMs. 
 * </p>
 * 
 * <p>
 * Similarly some of the XSLT tests depend on the 
 * underlying TrAX engine. Xalan 2.7 and Saxon 6.5.4 both do a pretty good job,
 * but neither can pass all thne tests included with XOM. 
 * testOASISMicrosoftConformanceSuite
 * is the most likely test to fail.
 * </p>
 * 
 * <p>
 * A few tests rely on access to external network resources,
 *  and may fail sproadically if the remote server is down or unreachable.
 * However, in general, they pass. 
 * </p>
 * 
 * <p>
 * The only other test in this class which is known to fail is 
 * testBuildFromFileThatContainsPlane1CharacterInName. This test fails on Mac OS X due to bugs in that platform's VM. However, it passes on other platforms. 
 * </p>
 * 
 * <p>
 * There are numerous public classes and methods in this package because JUnit requires
 * all test cases to be public. However, the only <em>published</em> class is
 * <code>XOMTestCase</code> which provides methods for asserting the equality
 * of documents, elements, processing instructions, and other node types.
 * This may be useful for your own tests. The remainder of this package
 * should be treated as unstable and your code should not depend on it.
 * </p>
 * 
 * @since 1.0
 */
package nu.xom.tests;