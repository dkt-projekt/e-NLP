package de.dkt.eservices.ecorenlp;


import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.RequestParam;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequestWithBody;

import de.dkt.common.tools.FileReadUtilities;
import eu.freme.bservices.testhelper.TestHelper;
import eu.freme.bservices.testhelper.ValidationHelper;
import eu.freme.bservices.testhelper.api.IntegrationTestSetup;
import junit.framework.Assert;

/**
 * @author 
 */

public class ECoreNLPTest {

	TestHelper testHelper;
	ValidationHelper validationHelper;

	@Before
	public void setup() {
		ApplicationContext context = IntegrationTestSetup
				.getContext(TestConstants.pathToPackage);
		testHelper = context.getBean(TestHelper.class);
		validationHelper = context.getBean(ValidationHelper.class);
	}
	
	private HttpRequestWithBody baseRequest() {
		String url = testHelper.getAPIBaseUrl() + "/e-corenlp/testURL";
		return Unirest.post(url);
	}
	
	private HttpRequestWithBody tagRequest() {
		String url = testHelper.getAPIBaseUrl() + "/e-nlp/partOfSpeechTagging";
		//Unirest.setTimeouts(10000, 10000000);
		return Unirest.post(url);
	}
	
	@Rule
	public final ExpectedException exception = ExpectedException.none();
	
	@Test
	public void sanityCheck() throws UnirestException, IOException, Exception {

		HttpResponse<String> response = baseRequest()
				.queryString("informat", "text")
				.queryString("input", "hello world")
				.queryString("outformat", "turtle").asString();

		System.out.println("BODY: "+response.getBody());
		System.out.println("STATUS:" + response.getStatus());

		assertTrue(response.getStatus() == 200);
		assertTrue(response.getBody().length() > 0);
	}
	
	
	@Test
	public void enTest() throws UnirestException, IOException, Exception {
		HttpResponse<String> response2 = tagRequest()
				.queryString("input", "If you like to gamble, I tell you I'm your man.      You win some, lose some, all the same to me.")
				.queryString("language", "en")
				.queryString("informat", "text")
				.queryString("outformat", "turtle")
				.asString();
		
		Assert.assertEquals(TestConstants.expectedResponse, response2.getBody());
		assertTrue(response2.getStatus() == 200);
		assertTrue(response2.getBody().length() > 0);
		
	}
	
	
	@Test
	public void deTest() throws UnirestException, IOException, Exception {
	
		HttpResponse<String> response3 = tagRequest()
				//.queryString("input", "Halb Sechs, meine Augen brennen. Tret' auf 'nen Typen, der zwischen toten Tauben pennt.")
				.queryString("language", "de")
				.queryString("informat", "turtle")
				.queryString("outformat", "turtle")
				.body(TestConstants.turtleInput2)
				.asString();
		
		Assert.assertEquals(TestConstants.expectedResponse2, response3.getBody());
		assertTrue(response3.getStatus() == 200);
		assertTrue(response3.getBody().length() > 0);
		
	}
	
		//NL test:
		// TODO: not really working. Check for the correct exception thrown
		/*
		//exception.expect(eu.freme.common.exception.BadRequestException.class);
		HttpResponse<String> response4 = tagRequest()
				.queryString("inputText", "Mozes in een mandje, yeah, ik chill 'm in de Nijl.")
				.queryString("language", "nl")
				.asString();
		
		//Assert.assertEquals(TestConstants.expectedResponse3, response4.getBody());
		
		assertTrue(response4.getStatus() == 400);
		assertTrue(response4.getBody().length() > 0);
		System.out.println("STATUS3:" + response4.getStatus());
		*/

	
	
	
}
