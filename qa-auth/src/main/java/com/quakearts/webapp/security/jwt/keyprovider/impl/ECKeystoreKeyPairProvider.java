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
import java.security.KeyStore.PrivateKeyEntry;
import java.security.interfaces.ECPublicKey;

import com.quakearts.webapp.security.jwt.keyprovider.KeystoreKeyPairProvider;

public class ECKeystoreKeyPairProvider extends KeystoreKeyPairProvider {

	private static final String ECALGORITHM = "EC";
	private ECDSAKeySize keySize = ECDSAKeySize.ES256KEY;
	public static enum ECDSAKeySize {
		ES256KEY(256),
		ES384KEY(384),
		ES512KEY(521);

		int keySize;
		
		private ECDSAKeySize(int keySize) {
			this.keySize = keySize;
		}
	}
	
	public ECKeystoreKeyPairProvider() {
	}
	
	public ECKeystoreKeyPairProvider(ECDSAKeySize keySize) {
		this.keySize = keySize;
	}

	@Override
	protected void validateKeyType(PrivateKeyEntry privateKeyEntry) throws InvalidKeyException {
		validatePrivateKeyEntry(ECALGORITHM, privateKeyEntry);
		
		if(!(privateKeyEntry.getCertificate().getPublicKey() instanceof ECPublicKey)
				|| ((ECPublicKey)privateKeyEntry.getCertificate().getPublicKey()).getParams().getOrder().bitLength() != keySize.keySize){
			throw new InvalidKeyException("Key is not a valid EC key of key size "+keySize.keySize);
		}
	}
}
