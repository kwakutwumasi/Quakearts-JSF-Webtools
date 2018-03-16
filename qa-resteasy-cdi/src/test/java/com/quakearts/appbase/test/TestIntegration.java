package com.quakearts.appbase.test;

import static org.junit.Assert.*;
import static org.hamcrest.core.Is.*;

import org.junit.Test;

import com.quakearts.appbase.Main;
import com.quakearts.appbase.test.experiments.TestInjectImpl;
import com.quakearts.appbase.test.experiments.TestSubInjectDecorator;
import com.quakearts.appbase.test.experiments.TestSubInjectImpl;
import com.quakearts.appbase.test.helpers.TestHttpClient;
import com.quakearts.appbase.test.helpers.TestHttpClientBuilder;
import com.quakearts.appbase.test.helpers.TestMain;
import com.quakearts.appbase.test.helpers.TestResource;
import com.quakearts.rest.client.HttpResponse;
import com.quakearts.rest.client.HttpVerb;

public class TestIntegration {

	@Test
	public void testRestIntegration() throws Exception {
		Main.main(new String[] {TestMain.class.getName(),"-dontwaitinmain"});

		TestHttpClient client = new TestHttpClientBuilder()
				.setHostAs("localhost")
				.setPortAs(8180)
				.thenBuild();
		
		HttpResponse response = client.sendRequest("/test/rest/test", null, HttpVerb.GET);
		assertThat(response.getHttpCode(), is(200));
		assertThat(response.getOutput().contains("OK"), is(true));
		assertThat(TestInjectImpl.saidHello(), is(true));
		assertThat(TestInjectImpl.testSubInjectLoaded(), is(true));		
		assertThat(TestSubInjectImpl.hasDoneSomething(), is(true));
		assertThat(TestSubInjectDecorator.decoratedSubInject(), is(true));
		
		response = client.sendRequest("/test/rest/test", "Test value", HttpVerb.POST);
		assertThat(response.getHttpCode(), is(200));
		assertThat(TestResource.transactionWorked(), is(true));
		assertThat(TestResource.getContent(), is("Test value"));		
	}

}
