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
package com.quakearts.webapp.facelets.tag;

import java.util.logging.Logger;

import javax.security.jacc.PolicyContextException;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.quakearts.webapp.facelets.tag.utils.SubjectHelper;

public class SubjectSessionListener implements HttpSessionListener {

	private static final Logger log = Logger.getLogger(SubjectSessionListener.class.getName());
	
	@Override
	public void sessionCreated(HttpSessionEvent event) {
		try {
			SubjectHelper helper = new SubjectHelper();
			event.getSession().setAttribute(SubjectHelper.KEY, helper);
		} catch (PolicyContextException e) {
			log.severe("Exception of type " + e.getClass().getName()
					+ " was thrown. Message is " + e.getMessage());
		}
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
	}

}
