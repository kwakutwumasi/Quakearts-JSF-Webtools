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

public class PackageFilter extends DefaultFilter {

	private String[] scanPackages;
	
    public PackageFilter(String... scanPackages) {
		this.scanPackages = scanPackages;
	}


    /* (non-Javadoc)
     * @see com.quakearts.classannotationscanner.DefaultFilter#shouldScan(java.lang.String)
     */
    @Override
    protected boolean shouldScan(String packageName) {
    	for(String scanPackage:scanPackages){
	        if (packageName.startsWith(scanPackage + ".")) {
	            return true;
	        }
    	}
        return false;
    }
}
