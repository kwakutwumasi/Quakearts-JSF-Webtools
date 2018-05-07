/*******************************************************************************
 * Copyright (C) 2017 Kwaku Twumasi-Afriyie <kwaku.twumasi@quakearts.com>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Kwaku Twumasi-Afriyie <kwaku.twumasi@quakearts.com> - initial API and implementation
 ******************************************************************************/
package com.quakearts.syshub.webapp.beans;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;

import com.quakearts.webapp.facelets.base.BaseBean;
import com.quakearts.webapp.orm.query.Range;
import com.quakearts.webapp.orm.query.helper.ParameterMapBuilder;
import com.quakearts.webapp.orm.exception.DataStoreException;
import com.quakearts.syshub.model.ResultExceptionLog;
import com.quakearts.syshub.SysHub;
import com.quakearts.syshub.core.runner.AgentRunner;
import com.quakearts.syshub.exception.ProcessingException;
import com.quakearts.syshub.model.AgentConfiguration;

@ManagedBean(name="resultExceptionLogPage")
@ViewScoped
public class ResultExceptionLogPage extends BaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -106873496623437310L;

	@Inject SysHub sysHub;
	
	private static Logger log = Logger.getLogger(ResultExceptionLogPage.class.getName());

	private ResultExceptionLog resultExceptionLog;
	private WebApplicationMain webappmain;
	private transient ResultExceptionLogFinder finder = new ResultExceptionLogFinder();
		
	public ResultExceptionLogPage(){
		webappmain = new WebApplicationMain();
	}
	
	public WebApplicationMain getWebappmain(){
		return webappmain;
	}
	
	public ResultExceptionLog getResultExceptionLog() {
		if(resultExceptionLog==null){
			if(hasParameter("resultExceptionLog")){
				setResultExceptionLog(finder.getById(getParameterLong("resultExceptionLog")));
				webappmain.setMode("read");
			} else {
    			resultExceptionLog = new ResultExceptionLog();
			}
		}
		
		return resultExceptionLog;
	}
    	
	public void setResultExceptionLog(ResultExceptionLog resultExceptionLog) {
		this.resultExceptionLog = resultExceptionLog;
		if(resultExceptionLog!=null){
			AgentConfiguration agentConfiguration = resultExceptionLog.getAgentConfiguration();
			if(agentConfiguration!=null){
				getAgentConfigurationDropdownHelper().addToFoundItemsList(agentConfiguration);
			}
		}
	}
	
	private AgentConfigurationDropdownHelper agentConfigurationDropdownHelper;

	public AgentConfigurationDropdownHelper getAgentConfigurationDropdownHelper(){
		if(agentConfigurationDropdownHelper == null)
			agentConfigurationDropdownHelper = new AgentConfigurationDropdownHelper();
			
		return agentConfigurationDropdownHelper;
	}
	
	private List<ResultExceptionLog> resultExceptionLogList;
	
	public List<ResultExceptionLog> getResultExceptionLogList(){
		return resultExceptionLogList;
	}
	
	private Range exceptionDtRange = new Range();
	
	public Range getExceptionDtRange() {
		return exceptionDtRange;
	}
	
	public void findResultExceptionLog(ActionEvent event){
		ParameterMapBuilder parameterBuilder = new ParameterMapBuilder();
		if(resultExceptionLog.getAgentConfiguration() != null){
			parameterBuilder.add("agentConfiguration", resultExceptionLog.getAgentConfiguration());
		}
		if(resultExceptionLog.getAgentModule() != null){
			parameterBuilder.add("agentModule", resultExceptionLog.getAgentModule());
		}
		if(!exceptionDtRange.isEmpty()){
			parameterBuilder.add("exceptionDt", exceptionDtRange);
		}
		if(resultExceptionLog.getExceptionType() != null && ! resultExceptionLog.getExceptionType().trim().isEmpty()){
			parameterBuilder.addVariableString("exceptionType", resultExceptionLog.getExceptionType());
		}
		if(resultExceptionLog.getResultData() != null){
			parameterBuilder.add("resultData", resultExceptionLog.getResultData());
		}
    		
		try {
			resultExceptionLogList = finder.findObjects(parameterBuilder.build());
		} catch (DataStoreException e) {
			addError("Search error", "An error occured while searching for Result Exception Log", FacesContext.getCurrentInstance());
			log.severe("Exception of type " + e.getClass().getName() + " was thrown. Message is " + e.getMessage()
					+ ". Exception occured whiles searching for ResultExceptionLog");
		}		
	}
    	
	public void removeResultExceptionLog(ActionEvent event){
		if(resultExceptionLog!=null && resultExceptionLogList!=null){
			resultExceptionLogList.remove(resultExceptionLog);
		}
	}
	
	public boolean isInCreateOrEditMode(){
		return FacesContext.getCurrentInstance().getViewRoot().getViewId().endsWith("create.xhtml") || "edit".equals(webappmain.getMode());
	}
	
	public void retryProcessingResult(AjaxBehaviorEvent event) {
		if(getResultExceptionLog().getId()==0){
			addError("Invalid Data", "Select a valid ResultExceptionLog to continue", FacesContext.getCurrentInstance());
			return;
		}
			
		if(sysHub != null){
			if (getResultExceptionLog().getAgentConfiguration() != null
					&& getResultExceptionLog().getAgentConfiguration().getAgentName() != null
					&& !getResultExceptionLog().getAgentConfiguration().getAgentName().trim().isEmpty()) {
				AgentRunner agentRunner = sysHub.fetchAgentRunner(getResultExceptionLog().getAgentConfiguration());
				if(agentRunner != null){
					try {
						agentRunner.getProcessingAgent().reprocessResultExceptionLog(getResultExceptionLog());
					} catch (ClassNotFoundException | IOException | ProcessingException e) {
						addError("System Error", "Unable to reprocess result: "+e.getMessage(), FacesContext.getCurrentInstance());
					}
				} else {
					addError("System Error", "Agent Runner "
							+getResultExceptionLog().getAgentConfiguration().getAgentName()
							+" has not been deployed.", FacesContext.getCurrentInstance());
				}
			}			
		}
	}
}
