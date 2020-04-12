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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Level;

import com.quakearts.classannotationscanner.exception.ScannerRuntimeException;
import com.quakearts.classannotationscanner.resource.ResourceInputStreamIterator;

public abstract class URLResourceScanner extends ScannerImplBase {

	/**Get an array of {@link URL}s to scan
	 * @return the array of {@link URL}s
	 */
	protected abstract URL[] findResources();

    /* (non-Javadoc)
	 * @see com.quakearts.classpathscanner.Scanner#scan()
	 */
    @Override
	public final void scan() {
    	long starttime = System.nanoTime();
        URL[] resources = findResources();
        for (URL resource : resources) {
            try {
                ResourceInputStreamIterator itr = getResourceIterator(resource, getFilter());
                if (itr != null) {
                    InputStream is = null;
                    while ((is = itr.next()) != null) {
                       processInputStream(is);
                    }
                }
            } catch (IOException e) {
                throw new ScannerRuntimeException(e);
            }
        }
    	log.log(Level.INFO, "Annotation scanning completed in {0} ms",
    			((System.nanoTime()-starttime)/1000000));
    }

    protected abstract ResourceInputStreamIterator getResourceIterator(URL url, Filter filter) throws IOException;
}
