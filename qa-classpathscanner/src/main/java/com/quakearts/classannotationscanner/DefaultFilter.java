/*******************************************************************************
* Copyright (C) 2016 Kwaku Twumasi-Afriyie <kwaku.twumasi@quakearts.com>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Kwaku Twumasi-Afriyie <kwaku.twumasi@quakearts.com> - initial API and implementation
 ******************************************************************************/
package com.quakearts.classannotationscanner;

import java.io.File;

/**Default implementation of the {@link Filter} interface. Skips packages from well known vendors.
 * @author Kwaku Twumasi Afriyie (kwaku.twumasi@quakearts.com)
 *
 */
public class DefaultFilter implements Filter {
    /**Default set of packages to ignore
     * 
     */
    protected static final String[] IGNORED_PACKAGES = {
        "java", "javax",
        "sun", "com.sun",
        "apple", "com.apple",
        "org.apache","org.w3c",
        "com.oracle","oracle",
        "javafx","jdk","org.eclipse",
        "com.mysql","org.jboss",
        "org.objectweb","bsh",
        "com.wutka","org.hibernate",
        "org.hornetq","org.hsqldb",
        "org.jaxen","org.jnp",
        "org.opennms","org.quartz",
        "org.slf4j","com.ibm",
        "org.jcp","EDU.oswego",
        "org.dom4j","javassist",
        "org.kohsuke","org.relaxng",
        "gnu.trove","org.infinispan",
        "com.microsoft","microsoft",
        "org.jvnet","com.ctc",
        "org.codehaus","com.google",
        "org.yaml","org.scannotation",
        "biz.source_code.base64Coder",
        "scalaj", "scala.tools.jline", "org.scala_tools.time",
        "javassist", "com.quakearts.classpathscanner"
    };

    /**List of packages to ignore
     * 
     */
    private String[] ignoredPackages;

	/**Default constructors. Uses the default list of packages to ignore
	 * 
	 */
	public DefaultFilter() {
		this.ignoredPackages = IGNORED_PACKAGES;
	}

	/**Constructor for providing a custom list of packages to ignore
	 * @param ignoredPackages
	 */
	public DefaultFilter(String[] ignoredPackages) {
		this.ignoredPackages = ignoredPackages;
	}

    /* (non-Javadoc)
     * @see com.quakearts.classpathscanner.Filter#accepts(java.lang.String)
     */
    @Override
    public boolean accepts(String name) {
        if (name.endsWith(".class")) {
        	String separator = name.contains(File.separator)?File.separator:"/";
            if (name.startsWith(separator)) {
                name = name.substring(1);
            }
            return shouldScan(name.replace(separator, "."));
        }
        return false;
    }

    /**Determin if the package name passed in should be scanned
     * @param packageName The package name to check
     * @return true of the package should be scanned
     */
    protected boolean shouldScan(String packageName) {
        for (String ignored : ignoredPackages) {
            if (packageName.startsWith(ignored + ".")) {
                return false;
            }
        }
        return true;
    }
}
