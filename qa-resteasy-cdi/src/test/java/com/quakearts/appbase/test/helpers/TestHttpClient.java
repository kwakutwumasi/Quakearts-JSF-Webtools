package com.quakearts.appbase.test.helpers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.quakearts.rest.client.HttpClient;
import com.quakearts.rest.client.HttpResponse;
import com.quakearts.rest.client.HttpVerb;
import com.quakearts.rest.client.exception.HttpClientException;

public class TestHttpClient extends HttpClient {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2821092267795823373L;

	static {
		try {
			TrustManager[] manager = new TrustManager[] {
					new X509TrustManager() {
						
						@Override
						public X509Certificate[] getAcceptedIssuers() {
							return null;
						}
						
						@Override
						public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
						}
						
						@Override
						public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
						}
					}
			};
			
			SSLContext context = SSLContext.getInstance("TLS");
			context.init(null, manager, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
			HttpsURLConnection.setDefaultHostnameVerifier((string, sslContext)->{
				return true;
			});
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public HttpResponse sendRequest(String file, String requestValue, HttpVerb method)
			throws MalformedURLException, IOException, HttpClientException {
		return super.sendRequest(file, requestValue, method, "text/plain");
	}
}