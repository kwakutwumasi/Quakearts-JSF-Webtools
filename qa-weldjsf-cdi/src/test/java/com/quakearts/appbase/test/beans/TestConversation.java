/*******************************************************************************
 * Copyright (C) 2018 Kwaku Twumasi-Afriyie <kwaku.twumasi@quakearts.com>
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.quakearts.appbase.test.beans;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

@Named("testConversation")
@ConversationScoped
public class TestConversation implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7114315575013563091L;
	@Inject
	Conversation conversation;
	
	@PostConstruct
	public void init() {
		if(conversation.isTransient()) {
			conversation.begin();
		}	
	}
	
	public String getHashCode() {
		return Integer.toHexString(hashCode());
	}
}
