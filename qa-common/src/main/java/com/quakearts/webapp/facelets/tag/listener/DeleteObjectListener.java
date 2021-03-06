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
package com.quakearts.webapp.facelets.tag.listener;

import javax.el.ValueExpression;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import java.util.logging.Logger;

import com.quakearts.webapp.facelets.tag.OrmListener;
import com.quakearts.webapp.facelets.util.ObjectExtractor;
import com.quakearts.webapp.facelets.util.UtilityMethods;

public class DeleteObjectListener extends OrmListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6659018184061783495L;
	private ValueExpression objectExpression;
	private ValueExpression messageExpression;
	private static final Logger log = Logger.getLogger(DeleteObjectListener.class.getName());

	public DeleteObjectListener(ValueExpression messageExpression,
			ValueExpression objectExpression) {
		this.messageExpression = messageExpression;
		this.objectExpression = objectExpression;
	}

	@Override
	protected void continueProcessing(ActionEvent event, FacesContext ctx) {
		Object object = objectExpression.getValue(ctx.getELContext());
		String message = ObjectExtractor.extractString(messageExpression, ctx.getELContext());
		
		try {
			if(object!=null){
				dataStore.delete(object);
				addMessage("Success",message==null?(object.getClass().getSimpleName()+" has been successfully deleted"):message, ctx);
				setOutcome("success");
			}else{
				setOutcome("error");
				addError("Invalid object", "Object attribute evaluated to null", ctx);				
			}
		} catch (Exception e) {
			log.severe("Exception of type " + e.getClass().getName()
					+ " was thrown. Message is " + e.getMessage());
			try {
				UtilityMethods.getTransaction().setRollbackOnly();
			} catch (Exception e2) {
			}
			addError("Application error", "Exception deleting object", ctx);
			setOutcome("error");
		}
	}

}
