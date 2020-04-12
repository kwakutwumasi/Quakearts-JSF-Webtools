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

/**An interface for implementing a filter to skip over class files while scanning
 * @author Kwaku Twumasi Afriyie (kwaku.twumasi@quakearts.com)
 *
 */
public interface Filter {

    /**Tests whether the scanner accepts the resource with the given name
     * @param name The name to test
     * @return true if the resource should be scanned
     */
    boolean accepts(String name);
}
