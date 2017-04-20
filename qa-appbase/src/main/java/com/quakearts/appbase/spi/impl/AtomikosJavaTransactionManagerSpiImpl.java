package com.quakearts.appbase.spi.impl;

import javax.transaction.SystemException;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import com.atomikos.icatch.jta.UserTransactionManager;
import com.quakearts.appbase.exception.ConfigurationException;
import com.quakearts.appbase.spi.JavaTransactionManagerSpi;

public class AtomikosJavaTransactionManagerSpiImpl implements JavaTransactionManagerSpi {

	private static UserTransactionManager tm;
	
	public TransactionManager getTransactionManager() {
		return tm;
	}
	
	@Override
	public UserTransaction getUserTransaction() {
		return tm;
	}
	
	@Override
	public void initiateJavaTransactionManager() {
		if(tm==null){
			tm = new UserTransactionManager();
			try {
				tm.init();
			} catch (SystemException e) {
				throw new ConfigurationException("Unable to create Transaction Manager", e);
			}
		}
	}

	@Override
	public void shutdownJavaTransactionManager() {
		if(tm!=null){
			tm.close();
			tm = null;
		}
	}
}
