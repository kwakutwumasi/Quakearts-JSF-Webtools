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
package com.quakearts.classannotationscanner.resource;

import java.io.InputStream;

/**Interface for iterating over a set of resources
 * @author Kwaku Twumasi Afriyie (kwaku.twumasi@quakearts.com)
 *
 */
public interface ResourceInputStreamIterator {

    /**Moves to the next resource
     * @return The {@link InputStream} of the resource. Null if there are no more resources
     */
    InputStream next();
    /**Cleans up and frees expensive resources
     * 
     */
    void close();
}
