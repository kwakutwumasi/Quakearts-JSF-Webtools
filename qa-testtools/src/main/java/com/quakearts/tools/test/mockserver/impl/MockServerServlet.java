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
package com.quakearts.tools.test.mockserver.impl;

import static com.quakearts.tools.test.mockserver.util.HttpVerbUtil.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.quakearts.tools.test.mockserver.configuration.Configuration;
import com.quakearts.tools.test.mockserver.context.ProcessingContext;
import com.quakearts.tools.test.mockserver.exception.MockServerProcessingException;
import com.quakearts.tools.test.mockserver.fi.DefaultAction;
import com.quakearts.tools.test.mockserver.model.HttpHeader;
import com.quakearts.tools.test.mockserver.model.HttpRequest;
import com.quakearts.tools.test.mockserver.model.HttpResponse;
import com.quakearts.tools.test.mockserver.model.MockAction;
import com.quakearts.tools.test.mockserver.model.impl.HttpHeaderImpl;
import com.quakearts.tools.test.mockserver.model.impl.HttpMessageBuilder;
import com.quakearts.tools.test.mockserver.model.impl.HttpMessageBuilder.HttpResponseBuilder;
import com.quakearts.tools.test.mockserver.store.exception.HttpMessageStoreException;
import com.quakearts.tools.test.mockserver.store.impl.MockServletHttpMessageStore;

public class MockServerServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4680208631254192578L;
	private final Set<MockAction> mockActions;
	private final Set<DefaultAction> defaultActions;
	private final Configuration configuration;
	
	MockServerServlet(Set<MockAction> mockActions, Set<DefaultAction> defaultActions,
			Configuration configuration) {
		this.mockActions = mockActions;
		this.defaultActions = defaultActions;
		this.configuration = configuration;
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		ProcessingContext context = getProcessingContext(req, resp);
		try {
			processDefaultActions(context);
		} catch (MockServerProcessingException e) {
			throw new ServletException(e);
		}
		
		if(context.responseSent())
			return;
		
		try {
			switch (configuration.getMockingMode()) {
			case MOCK:
				mock(context, false);
				return;
			case RECORD:
				record(context);
				return;
			case MIXED:
				mock(context, true);
				if(!context.responseSent())
					record(context);
			}
		} catch (MockServerProcessingException | HttpMessageStoreException e) {
			resp.sendError(500, e.getMessage()+(e.getCause()!=null?"; Caused by "+e.getCause().getMessage():""));
		}
	}

	private void processDefaultActions(ProcessingContext context) throws MockServerProcessingException {
		if(defaultActions!=null && !defaultActions.isEmpty())
			for (DefaultAction action : defaultActions) {
				action.performAction(context);
				if(context.responseSent())
					break;
			}
	}

	private ProcessingContext getProcessingContext(HttpServletRequest req, HttpServletResponse resp) {
		return MockServletProcessingContextBuilder.createProcessingContext(req, resp, configuration.getURLToRecord());
	}

	private void mock(ProcessingContext context, boolean mixed) throws MockServerProcessingException {
		for(MockAction action:mockActions) {
			if(action.requestMatches(context.getHttpRequest())) {
				HttpResponse httpResponse = action.executeAction(context.getHttpRequest());
				if(!context.responseSent())
					context.sendResponse(httpResponse);
				return;
			}
		}
		if(!mixed)
			context.sendHttpError(500, "No matching httpRequest found");
	}

	private void record(ProcessingContext context) throws 
			MockServerProcessingException, HttpMessageStoreException {
		HttpRequest request = context.getHttpRequest();
		try {
			HttpURLConnection con = prepareConnection(request);
			con.connect();
			byte[] responseContent;
			if(returningInputMethodsInclude(request.getMethod()) || configuration.dishonorRESTContract()) {
				responseContent = getResponseContent(con);
			} else {
				responseContent = null;
			}
			HttpResponse response = getAndStoreResponse(request, con, responseContent);			
			context.sendResponse(response);
		} catch (IOException e) {
			throw new MockServerProcessingException("Unable to connect to server", e);
		}
	}

	private HttpURLConnection prepareConnection(HttpRequest request)
			throws IOException, MockServerProcessingException {
		HttpURLConnection con = (HttpURLConnection) new URL(configuration.getURLToRecord()
				+ request.getResource()).openConnection();
		if(con instanceof HttpsURLConnection 
				&& (configuration.getURLToRecord().startsWith("https://localhost")
						||configuration.getURLToRecord().startsWith("https://127.0.0.1"))) {
			((HttpsURLConnection)con).setHostnameVerifier((hostname,session) -> (hostname.equals("localhost")
					|| hostname.equals("127.0.0.1")));
		}
		
		con.setRequestMethod(request.getMethod());
		for(HttpHeader header:request.getHeaders()) {
			for(String value:header.getValues())
				con.addRequestProperty(header.getName(), value);
		}
		
		if(configuration.getConnectTimeout()>0)
			con.setConnectTimeout(configuration.getConnectTimeout());
		
		con.setInstanceFollowRedirects(true);
		
		if(configuration.getReadTimeout()>0)
			con.setReadTimeout(configuration.getReadTimeout());
		
		if(configuration.dishonorRESTContract() 
				|| returningInputMethodsInclude(request.getMethod()))
			con.setDoInput(true);
		
		if(!configuration.dishonorRESTContract()
				&& requiringOutputMethodsInclude(request.getMethod()) 
				&& request.getContentBytes() == null)
			throw new MockServerProcessingException("Invalid http input");
				
		writeRequestToOutputStream(request, con);
		return con;
	}

	private void writeRequestToOutputStream(HttpRequest request, HttpURLConnection con)
			throws MockServerProcessingException, IOException {
		if(request.getContentBytes()!=null) {
			if(!configuration.dishonorRESTContract()
					&& !requiringOutputMethodsInclude(request.getMethod())
					&& !optionalOutputMethodsInlude(request.getMethod()))
				throw new MockServerProcessingException("Invalid http input");

			con.setDoOutput(true);
			con.getOutputStream().write(request.getContentBytes());
		}
	}

	private byte[] getResponseContent(HttpURLConnection con) throws IOException {
		byte[] responseContent;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		InputStream in;
		if(con.getResponseCode() >= 200 && con.getResponseCode() <= 299)
			in = con.getInputStream();
		else
			in = con.getErrorStream();
		
		if(in!=null) {
			int read;
			while ((read = in.read())!=-1) {
				bos.write(read);
			}
		}
		responseContent = bos.toByteArray();
		return responseContent;
	}

	private HttpResponse getAndStoreResponse(HttpRequest request, HttpURLConnection con, byte[] responseContent)
			throws IOException, HttpMessageStoreException {
		HttpResponseBuilder builder = HttpMessageBuilder.createNewHttpResponse()
				.setResponseCodeAs(con.getResponseCode())
				.setContentBytes(responseContent);
		
		Map<String, List<String>> headerMap = con.getHeaderFields();
		List<HttpHeader> headers = headerMap.entrySet().stream().map(entry->new HttpHeaderImpl(entry.getKey(), entry.getValue()))
				.collect(Collectors.toList());

		builder.addHeaders(headers.toArray(new HttpHeader[headers.size()]));

		
		HttpResponse response = builder.thenBuild();
		HttpRequest requestToStore = HttpMessageBuilder.use(request)
				.setResponseAs(response).thenBuild();
		MockServletHttpMessageStore.getInstance().storeRequest(requestToStore);
		return response;
	}
}
