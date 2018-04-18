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
package com.quakearts.webapp.security.jwt;

import com.quakearts.webapp.security.jwt.exception.JWTException;

public interface JWTVerifier extends JWTBase {
	void verify(byte[] token) throws JWTException;
	void verify(String token) throws JWTException;
	JWTHeader getHeader();
	JWTClaims getClaims();
}
