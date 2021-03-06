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
package com.quakearts.webapp.logging.monitor.impl;

import java.lang.management.ManagementFactory;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.management.JMX;
import javax.management.ObjectName;
import org.jboss.system.ServiceMBean;

public class JbossSystemMonitor extends GenericMonitor{
	
	private Map<String, ServiceMBean> cache = new ConcurrentHashMap<String, ServiceMBean>();
	
	@Override
	protected State getServiceState(String name) {
		ServiceMBean service;
		service = cache.get(name);

		if(service==null)
			try {
				service = JMX.newMBeanProxy(ManagementFactory.getPlatformMBeanServer(), new ObjectName(name), ServiceMBean.class, false);
				cache.put(name, service);
			} catch (Exception e) {
				System.err.println("["+this.getClass().getName()+"] Exception of type " + e.getClass().getName()
						+ " was thrown. Message is " + e.getMessage()+". Exception occured whiles getting getting state.");
				return State.QUERYERROR;
			} 

		try {
			if(service.getState() == ServiceMBean.STARTED)
				return State.UP;
			else
				return State.DOWN;
		} catch (Exception e) {
			return State.QUERYERROR;
		} 
	}

}
