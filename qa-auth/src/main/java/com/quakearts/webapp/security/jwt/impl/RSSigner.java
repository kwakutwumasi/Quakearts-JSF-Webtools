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
package com.quakearts.webapp.security.jwt.impl;

import com.quakearts.webapp.security.jwt.JWTHeader;
import com.quakearts.webapp.security.jwt.signature.RSASignature;
import com.quakearts.webapp.security.jwt.signature.RSASignature.RSAAlgorithmType;
import com.quakearts.webapp.security.jwt.signature.SignatureBase;

public class RSSigner extends KeyStoreSignerBase {

	private RSAAlgorithmType algorithmType;
	@Override
	public KeyStoreSignerBase setAlgorithim(String algorithm) {
		algorithmType = RSAAlgorithmType.valueOf(algorithm);
		return this;
	}

	@Override
	public KeyStoreSignerBase setParameter(String name, Object parameter) {
		doSetParameter(name, parameter);
		return this;
	}

	@Override
	protected SignatureBase createSignatureInstance() {
		return new RSASignature(algorithmType, alias, file, password, storeType, keyType);
	}

	@Override
	protected void setHeaderAlgorithm(JWTHeader header) {
		header.setAlgorithm(algorithmType.toString());
	}

}
