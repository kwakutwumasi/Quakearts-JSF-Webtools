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

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import com.quakearts.webapp.security.jwt.JWTClaims;
import com.quakearts.webapp.security.jwt.JWTHeader;
import com.quakearts.webapp.security.jwt.JWTSigner;
import com.quakearts.webapp.security.jwt.JWTVerifier;
import com.quakearts.webapp.security.jwt.exception.JWTException;
import com.quakearts.webapp.security.jwt.factory.JWTFactory;
import com.quakearts.webapp.security.util.UtilityMethods;

public abstract class SignerBase implements JWTSigner, JWTVerifier {

	private byte[] signature;
	private byte[] payload;
	private JWTHeader header;
	private JWTClaims claims;
	
	@Override
	public String sign(JWTHeader header, JWTClaims claims) throws JWTException {
		byte[] signed;
		setHeaderAlgorithm(header);
		String payloadString = prepare(header, claims);
		try {
			signed = doSigning(payloadString);
		} catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException e) {
			throw new JWTException("Unable to sign: "+e.getMessage());
		}
		return payloadString + "." + UtilityMethods.base64EncodeWithoutPadding(signed);
	}

	protected abstract void setHeaderAlgorithm(JWTHeader header);

	protected abstract byte[] doSigning(String prepare)
			throws InvalidKeyException, NoSuchAlgorithmException, SignatureException;

	protected String prepare(JWTHeader header, JWTClaims claims) {
		return header.compact() + "." + claims.compact();
	}

	@Override
	public void verify(byte[] token) throws JWTException {
		split(token);

		byte[] signatureDecoded;
		signatureDecoded = UtilityMethods.base64DecodeMissingPaddingToBytes(new String(signature));

		try {
			doVerification(payload, signatureDecoded);
		} catch (InvalidKeyException | SignatureException | NoSuchAlgorithmException e) {
			throw new JWTException("Unable to verify token: "+e.getMessage());
		}
	}

	@Override
	public void verify(String token) throws JWTException {
		try {
			verify(token.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			//never happens
		}
	}
	
	protected abstract void doVerification(byte[] payload, byte[] signatureDecoded)
			throws InvalidKeyException, SignatureException, NoSuchAlgorithmException;

	private void split(byte[] token) throws JWTException {
		int signatureSplitPoint = -1;
		int payloadSplitPoint = -1;
		int currPos = 0;
		int pointCount = 0;
		for (byte c : token) {
			if (c == '.') {
				if (payloadSplitPoint == -1)
					payloadSplitPoint = currPos;
				else
					signatureSplitPoint = currPos;

				pointCount++;
			}
			currPos++;
		}

		if (pointCount != 2)
			throw new JWTException("Invalid token. Must be separated by two '.'");

		payload = new byte[signatureSplitPoint];
		signature = new byte[token.length - signatureSplitPoint - 1];

		System.arraycopy(token, 0, payload, 0, payload.length);
		System.arraycopy(token, payload.length + 1, signature, 0, signature.length);
		
		if(payloadSplitPoint+1 == payload.length)
			throw new JWTException("Invalid token. Claims are not present");			
		
		byte[] headerBytes = new byte[payloadSplitPoint];
		byte[] claimsBytes = new byte[payload.length - payloadSplitPoint - 1];

		System.arraycopy(payload, 0, headerBytes, 0, headerBytes.length);
		System.arraycopy(payload, headerBytes.length + 1, claimsBytes, 0, claimsBytes.length);
		
		header = JWTFactory.getInstance().createJWTHeaderFromBytes(headerBytes);
		claims = JWTFactory.getInstance().createJWTClaimsFromBytes(claimsBytes);
	}

	@Override
	public JWTHeader getHeader() {
		return header;
	}

	@Override
	public JWTClaims getClaims() {
		return claims;
	}
		
}
