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
package com.quakearts.syshub.core.utils;

import com.quakearts.webapp.orm.DataStore;

/**Interface for the manager of the system {@linkplain DataStore}
 * @author kwakutwumasi-afriyie
 *
 */
public interface SystemDataStoreManager {

	/**Getter for the {@linkplain DataStore}
	 * @return the {@linkplain DataStore}
	 */
	DataStore getDataStore();

}