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

import com.quakearts.classannotationscanner.listener.ClassAnnotationObjectScanningListener;
import com.quakearts.classannotationscanner.listener.ClassAnnotationScanningListener;

/**A scanner for discovering loadable classes by their annotations
 * @author Kwaku Twumasi Afriyie (kwaku.twumasi@quakearts.com)
 *
 */
public interface Scanner {
	/**Add an implementation of {@link ClassAnnotationScanningListener} to the list of listeners
	 * @param listener The listener
	 */
	Scanner addAnnotationListener(ClassAnnotationScanningListener listener);
	/**Add an implementation of {@link ClassAnnotationObjectScanningListener} to the list of listeners
	 * @param listener The listener
	 */
	Scanner addAnnotationListener(ClassAnnotationObjectScanningListener listener);
	/**Get the filter for this scanner
	 * @return The {@link Filter}
	 */
	Filter getFilter();
	/**Sets the filter for this scanner
	 * @param filter The {@link Filter}
	 */
	void setFilter(Filter filter);
	/**Scans for classes and executes listeners registered for the class annotations
	 * 
	 */
	void scan();
}
