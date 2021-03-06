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
package com.quakearts.webapp.facelets.bootstrap.renderers;

import java.io.IOException;
import java.util.Map;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.ConverterException;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.render.Renderer;

import com.quakearts.webapp.facelets.bootstrap.components.BootFileInput;
import com.quakearts.webapp.facelets.bootstrap.renderkit.Attribute;
import com.quakearts.webapp.facelets.bootstrap.servlet.BootServlet;
import com.quakearts.webapp.facelets.bootstrap.utils.BootFileUpload;
import static com.quakearts.webapp.facelets.bootstrap.renderkit.RenderKitUtils.*;
import static com.quakearts.webapp.facelets.util.UtilityMethods.*;

public class BootFileInputRenderer extends Renderer {
	
	@Override
	public void decode(FacesContext context, UIComponent component) {
		if (context == null)
        	throw new NullPointerException();
        
        if(!(component instanceof BootFileInput)) {
		    throw new AbortProcessingException("Component must be of type "+BootFileInput.class.getName());
		}
		
		BootFileInput input = (BootFileInput) component;
		Map<String, Object> ctxMap= context.getExternalContext().getSessionMap();
		BootFileUpload upload = (BootFileUpload) ctxMap.get(input.getTicket());
		
		if(upload==null){
			LOGGER.fine("No upload for ticket "+input.getTicket());
			return;
		}
		
		ctxMap.remove(input.getTicket());//Garbage collection
		Integer count = (Integer) ctxMap.get(BootServlet.FILELOADCOUNT);
		ctxMap.put(BootServlet.FILELOADCOUNT, --count);
		
		ValueExpression dataExpression, valueExpression;
		if((dataExpression=component.getValueExpression("data"))!=null){
			dataExpression.setValue(context.getELContext(), upload.getData());
		} else if((valueExpression=component.getValueExpression("value"))!=null) {
			valueExpression.setValue(context.getELContext(), upload);
		} else {
			throw new AbortProcessingException("One of attribtes 'data' or 'value' is required and must be a ValueExpression");
		}
		
		ValueExpression nameExpression = component.getValueExpression("name");
		if(nameExpression!=null){
			nameExpression.setValue(context.getELContext(), upload.getFileName());
		}
        component.queueEvent(new ActionEvent(component));		
	}

	@Override
	public void encodeBegin(FacesContext context, UIComponent component)
			throws IOException {
	}

	@Override
	public void encodeChildren(FacesContext context, UIComponent component)
			throws IOException {
	}

	@Override
	public void encodeEnd(FacesContext context, UIComponent component)
			throws IOException {
		if (null == context) 
			throw new NullPointerException();
			
		if(!(component instanceof BootFileInput)) {
    		throw new IOException("Component must be of type "+BootFileInput.class.getName());
		}
		
		BootFileInput fileInput = (BootFileInput) component;
		
		boolean componentDisabled = componentIsDisabled(fileInput);
		
		String root = context.getExternalContext().getRequestContextPath();
		
		ResponseWriter writer = context.getResponseWriter();
		
		String id = component.getClientId();
		String id_js= id.replace(":", "_");
		
		String onevent=fileInput.get("onevent");
		String onerror=fileInput.get("onerror");
		String label = fileInput.get("label");
		String render = fileInput.get("render");
		if(!"@all".equals(render) && !"@form".equals(render) && !"@this".equals(render))
			render = findClientIds(render, component, context);
		
		String execute = fileInput.get("execute");
		if(!"@all".equals(execute) && !"@form".equals(execute) && !"@this".equals(execute))
			execute = findClientIds(execute, component, context);
		
		String name = fileInput.get("name");
		
		writer.startElement("div", component);
		writer.writeAttribute("id", id, "clientId");
        String style =fileInput.get("style");		
        if(style!=null)
        	writer.writeAttribute("style", style, null);
        String styleClass =fileInput.get("styleClass");		
        if(styleClass!=null)
        	writer.writeAttribute("class", styleClass, null);
		writer.write("\n");    	
		writer.startElement("div", component);
		writer.writeAttribute("id", "div_"+id_js, null);
		writer.writeAttribute("class", name==null?"collapse":"input-group", null);
		writer.writeAttribute("style", "margin-bottom:5px;", null);
		writer.write("\n");
		writer.startElement("span", component);
		writer.writeAttribute("class", "input-group-addon", null);
		writer.write("\n");    	
		writer.startElement("img", component);
		writer.writeAttribute("id", "img_ldg_"+id_js, null);
		writer.writeAttribute("alt", "Loading...", null);
		writer.writeAttribute("class", "collapse", null);
		writer.writeAttribute("src", root+"/boot-services/images/loading.gif", null);
		writer.endElement("img"); 
		writer.write("\n");
		writer.startElement("img", component);
		writer.writeAttribute("id", "img_ldd_"+id_js, null);
		writer.writeAttribute("alt", "Loaded", null);
		if(name==null)
			writer.writeAttribute("class", "collapse", null);
		writer.writeAttribute("src", root+"/boot-services/images/loaded.png", null);
		writer.endElement("img"); 
		writer.write("\n");
		writer.startElement("img", component);
		writer.writeAttribute("id", "img_lde_"+id_js, null);
		writer.writeAttribute("alt", "Loading error", null);
		writer.writeAttribute("class", "collapse", null);
		writer.writeAttribute("src", root+"/boot-services/images/loading-error.png", null);
		writer.endElement("img"); 
		writer.write("\n");
		writer.endElement("span");
		writer.write("\n");
		writer.startElement("span", component);
		writer.writeAttribute("class", "form-control", null);
		writer.writeAttribute("style", "white-space: nowrap;", null);
		writer.writeAttribute("id", "span_"+id_js, null);
		writer.write(name==null?" ":name.toString());
		writer.endElement("span");
		writer.write("\n");
		writer.endElement("div");
		writer.write("\n");
		writer.startElement("button", component);		
		writer.writeAttribute("id", "btn_"+id_js, null);
		writer.writeAttribute("type", "button", null);
		writer.writeAttribute("class", "btn btn-default", null);
		if(componentDisabled) {
			writer.writeAttribute("disabled", "disabled", "disabled");
		} else {
			writer.writeAttribute("onclick", "$('#iframe_"+id_js+"').contents().find('#upload-button').click();", null);
			writer.writeAttribute("onchange", "jsf.ajax.request(this, event, {execute:'"+id+(execute!=null?" "+execute:"")+"'"
				+(render!=null&&render.length()>0? ",render: '"+render+"'":"")
				+(onevent!=null&&onevent.length()>0? ",onevent: "+onevent+"":"")
				+(onerror!=null&&onerror.length()>0?",onerror: "+onerror+"":"")+"});", null);
        	renderHTML5DataAttributes(context, component);
        }
		
    	renderPassThruAttributes(context, writer, component, new Attribute[0]);
    	
    	UIComponent labelFacet = component.getFacet("label");
        if(labelFacet!=null){
        	labelFacet.encodeAll(context);
        } else {
            writer.write(label!=null?label:"Browse");
        }
        
		writer.endElement("button");
        writer.write("\n");
        if(!componentDisabled){
			writer.startElement("iframe", component);
			writer.writeAttribute("id", "iframe_"+id_js, null);
			writer.writeAttribute("class", "collapse", null);
			writer.writeAttribute("height", "0px", null);
			writer.writeAttribute("src", root+"/boot-services/upload?id="
							+id_js+"&ticket="+fileInput.getTicket(), null);
			writer.endElement("iframe");
        }
        writer.write("\n");
		writer.endElement("div");		
        writer.write("\n");
	}

	@Override
	public String convertClientId(FacesContext context, String clientId) {
		return clientId;
	}

	@Override
	public boolean getRendersChildren() {
		return true;// No children should be buffer by this component. This ensures that happens
	}

	@Override
	public Object getConvertedValue(FacesContext context,
			UIComponent component, Object submittedValue)
			throws ConverterException {
		return null;
	}
	
}
