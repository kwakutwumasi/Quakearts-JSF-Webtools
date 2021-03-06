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
package com.quakearts.webapp.security.jwt.keyprovider.impl;

import java.security.InvalidKeyException;
import java.security.KeyStore.Entry;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;

import com.quakearts.webapp.security.jwt.keyprovider.KeystoreKeyPairProvider;

public class RSAKeystoreKeyPairProvider extends KeystoreKeyPairProvider {

	private static final String RSAALGORITHM = "RSA";
	private static final int MINIMUMKEYSIZE = 2048;
	
	@Override
	protected void validateKeyType(Entry entry) throws InvalidKeyException {
		validatePrivateKeyEntry(RSAALGORITHM, entry);
		
		PublicKey publicKey = getPublicKey(entry);
		if(!(publicKey instanceof RSAPublicKey)
				||((RSAPublicKey)publicKey).getModulus().bitLength()<MINIMUMKEYSIZE)
			throw new InvalidKeyException("Key is not a valid RSA key of length greater than "+MINIMUMKEYSIZE);
	}

}
