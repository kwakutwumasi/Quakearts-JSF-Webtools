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
package com.quakearts.security.cryptography.factory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.crypto.NoSuchPaddingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.quakearts.security.cryptography.CryptoResource;
import com.quakearts.security.cryptography.exception.KeyProviderException;
import com.quakearts.security.cryptography.provider.KeyProvider;

/**Factory for creating {@linkplain CryptoResource}
 * @author kwakutwumasi-afriyie
 *
 */
public class CryptoServiceImpl implements CryptoService {
	private static final String CRYPTO_PROPERTIES = ".crypto.properties";

	private CryptoServiceImpl() {
	}

	private static final Logger log = LoggerFactory.getLogger(CryptoServiceImpl.class);

	private static final CryptoService instance = new CryptoServiceImpl();

	private static Properties properties;

	public static CryptoService getInstance() {
		return instance;
	}

	private static Map<String, Key> loadedKeys = new ConcurrentHashMap<>();
	
	/* (non-Javadoc)
	 * @see com.quakearts.security.cryptography.factory.CryptoService#getCryptoResource(java.lang.String, java.lang.String, java.util.Map, java.lang.String)
	 */
	@Override
	public CryptoResource getCryptoResource(String instance, String keyProviderClass, Map<Object, Object> properties,
			String name) throws ClassNotFoundException, InstantiationException, IllegalAccessException,
			KeyProviderException, NoSuchAlgorithmException, NoSuchPaddingException {
		Key key = loadedKeys.get(name);
		if(key == null) {
			KeyProvider provider;
	
			provider = KeyProviderFactory.createKeyProvider(keyProviderClass);
	
			if (properties == null) {
				log.warn("Parameter 'properties' is null. Key Provider may not startup properly.");
			} else {
				provider.setProperties(properties);
			}
	
			key = provider.getKey();
			loadedKeys.put(name, key);
		}
		
		return new CryptoResource(key, instance, name);
	}

	/* (non-Javadoc)
	 * @see com.quakearts.security.cryptography.factory.CryptoService#getCryptoResource(java.lang.String)
	 */
	@Override
	public CryptoResource getCryptoResource(String name)
			throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException,
			NoSuchAlgorithmException, NoSuchPaddingException, KeyProviderException {
		if(properties == null) {
			properties = new Properties();
	
			InputStream in = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream(name + CRYPTO_PROPERTIES);
			if (in == null)
				throw new FileNotFoundException("Unable to find " + name + CRYPTO_PROPERTIES);
	
			properties.load(in);
		}
		
		String instance = properties.getProperty("crypto.instance");
		String keyProviderClass = properties.getProperty("crypto.key.provider.class");

		CryptoResource resource = getCryptoResource(instance, keyProviderClass, properties, name);

		return resource;
	}
}