package com.quakearts.security.cryptography.jpa;

import java.util.logging.Logger;

import javax.naming.InitialContext;

import com.quakearts.security.cryptography.CryptoProxy;
import com.quakearts.security.cryptography.CryptoResource;
import com.quakearts.security.cryptography.CryptoUtils;
import com.quakearts.security.cryptography.factory.CrytpoServiceFactory;
import com.quakearts.webapp.orm.DataStoreFactory;

public abstract class EncryptedTypeBase {

	private transient CryptoResource resource;
	private static final Logger log = Logger.getLogger(EncryptedStringConverter.class.getName());
	
	protected CryptoResource getCryptoResource(){
		if(resource==null){
			try {
				String serviceJNDIname = DataStoreFactory.getInstance()
						.getDataStore().getConfigurationProperty("com.quakearts.cryptoservice");
				if(serviceJNDIname!=null){
					InitialContext ctx = CryptoUtils.getInitialContext();
					CryptoProxy proxy =(CryptoProxy) ctx.lookup(serviceJNDIname);
					resource = proxy.getResource();
				} else {
					String resourceName = DataStoreFactory.getInstance()
							.getDataStore().getConfigurationProperty("com.quakearts.cryptoname");
					
					resource = CrytpoServiceFactory.getInstance().getCryptoResource(resourceName);
				}
			} catch (Exception e) {
				log.severe("Cannot perform cryptography: "+e.getMessage()+". "+e.getClass().getName());
				throw new RuntimeException();
			}
		}
		return resource;
	}
}