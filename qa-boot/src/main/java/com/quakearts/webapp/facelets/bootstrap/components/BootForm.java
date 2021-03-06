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
package com.quakearts.webapp.facelets.bootstrap.components;

import java.util.ArrayList;
import java.util.HashMap;

import javax.faces.component.html.HtmlForm;

import com.quakearts.webapp.facelets.util.ObjectExtractor;

public class BootForm extends HtmlForm {
	public static final String COMPONENT_FAMILY="com.quakearts.bootstrap.form";
	public static final String RENDERER_TYPE="com.quakearts.bootstrap.form.renderer";

	public BootForm() {
		getAttributes().put("javax.faces.component.UIComponentBase.attributesThatAreSet", new ArrayList<String>());
	}
	
	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}
	
	@Override
	public String getRendererType() {
		return RENDERER_TYPE;
	}
	
	@Override
	public void setRendererType(String rendererType) {
	}
	
	private static final HashMap<String, String> VALID_TYPES = new HashMap<String, String>();
	static{
		VALID_TYPES.put("inline", "");
		VALID_TYPES.put("horizontal", "");
		VALID_TYPES.put("navbar", "");
	}
	
	public boolean isValid(String type){
		return VALID_TYPES.get(type)!=null;
	}
	
	public String get(String attribute) {
		String attributeValue = ObjectExtractor
				.extractString(getValueExpression(attribute), getFacesContext()
						.getELContext());
		if (attributeValue == null)
			attributeValue = (String) getAttributes().get(attribute);

		return attributeValue;
	}
}
