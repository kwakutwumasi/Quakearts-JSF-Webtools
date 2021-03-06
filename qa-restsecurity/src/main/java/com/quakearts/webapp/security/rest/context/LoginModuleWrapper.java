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
package com.quakearts.webapp.security.rest.context;

import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.jboss.weld.exceptions.IllegalStateException;

/**A wrapper around login module that don't implement {@linkplain LoginModule}
 * @author kwakutwumasi-afriyie
 *
 */
public class LoginModuleWrapper implements LoginModule {

	private Object module;
	private Handles handles;
	
	public LoginModuleWrapper(Class<?> moduleClass) throws LoginException, IllegalAccessException, InstantiationException {
		try {
			module = moduleClass.newInstance();
			if(!handlesMap.containsKey(moduleClass)){
				handles = new Handles();
				Method method = moduleClass.getMethod("initialize", Subject.class, CallbackHandler.class, Map.class, Map.class);
				handles.initializeHandle = getHandle(method);
				method = moduleClass.getMethod("login");
				handles.loginHandle = getHandle(method);
				method = moduleClass.getMethod("logout");
				handles.logoutHandle = getHandle(method);
				method = moduleClass.getMethod("commit");
				handles.commitHandle = getHandle(method);
				method = moduleClass.getMethod("abort");
				handles.abortHandle = getHandle(method);
				handlesMap.put(moduleClass, handles);
			} else {
				handles = handlesMap.get(moduleClass);
			}
		} catch (NoSuchMethodException | SecurityException e) {
			throw new LoginException("Exception of type " + e.getClass().getName() + " was thrown. Message is " + e.getMessage()
			+ ". Exception occured whiles executing login() on "+moduleClass.getName());
		}
	}

	private MethodHandle getHandle(Method initializeMethod) throws IllegalAccessException {
		return new ConstantCallSite(MethodHandles.lookup().unreflect(initializeMethod)).dynamicInvoker();
	}

	@Override
	public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState,
			Map<String, ?> options) {
		try {
			handles.initializeHandle.invoke(module, subject, callbackHandler, sharedState, options);
		} catch (Throwable e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public boolean login() throws LoginException {
		try {
			return ((Boolean)handles.loginHandle.invoke(module)).booleanValue();
		} catch (Throwable e) {
			throw new LoginException("Exception of type " + e.getClass().getName() + " was thrown. Message is " + e.getMessage()
					+ ". Exception occured whiles executing login() on "+module.getClass().getName());
		}
	}

	@Override
	public boolean commit() throws LoginException {
		try {
			return ((Boolean)handles.commitHandle.invoke(module)).booleanValue();
		} catch (Throwable e) {
			throw new LoginException("Exception of type " + e.getClass().getName() + " was thrown. Message is " + e.getMessage()
					+ ". Exception occured whiles executing commit() on "+module.getClass().getName());
		}
	}

	@Override
	public boolean abort() throws LoginException {
		try {
			return ((Boolean)handles.abortHandle.invoke(module)).booleanValue();
		} catch (Throwable e) {
			throw new LoginException("Exception of type " + e.getClass().getName() + " was thrown. Message is " + e.getMessage()
					+ ". Exception occured whiles executing abort() on "+module.getClass().getName());
		}
	}

	@Override
	public boolean logout() throws LoginException {
		try {
			return ((Boolean)handles.logoutHandle.invoke(module)).booleanValue();
		} catch (Throwable e) {
			throw new LoginException("Exception of type " + e.getClass().getName() + " was thrown. Message is " + e.getMessage()
					+ ". Exception occured whiles executing logout() on "+module.getClass().getName());
		}
	}

	private static Map<Class<?>, Handles> handlesMap = new HashMap<>();
	
	private static class Handles {
		MethodHandle initializeHandle;
		MethodHandle loginHandle;
		MethodHandle logoutHandle;
		MethodHandle commitHandle;
		MethodHandle abortHandle;
	}
	
}
