package de.dkt.eservices.enlp;


import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.validation.constraints.AssertTrue;

import org.apache.commons.io.FilenameUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.context.ApplicationContext;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequestWithBody;

import de.dkt.common.niftools.DKTNIF;
import de.dkt.common.niftools.NIFReader;
import de.dkt.eservices.enlp.TestConstants;
import eu.freme.bservices.testhelper.TestHelper;
import eu.freme.bservices.testhelper.ValidationHelper;
import eu.freme.bservices.testhelper.api.IntegrationTestSetup;
import eu.freme.common.conversion.rdf.RDFConstants.RDFSerialization;
import junit.framework.Assert;

/**
 * @author 
 */

public class ERattlesnakeTest {

	TestHelper testHelper;
	ValidationHelper validationHelper;
	ApplicationContext context;

	@Before
	public void setup() {
		//ApplicationContext context = IntegrationTestSetup.getContext(TestConstants.pathToPackage);
		context = IntegrationTestSetup.getContext(TestConstants.pathToPackage);
		testHelper = context.getBean(TestHelper.class);
		validationHelper = context.getBean(ValidationHelper.class);
 
	}
	
	private HttpRequestWithBody baseOpennlpRequest() {
		String url = testHelper.getAPIBaseUrl() + "/e-opennlp/testURL";
		return Unirest.post(url);
	}
	
	private HttpRequestWithBody analyzeOpennlpRequest() {
		String url = testHelper.getAPIBaseUrl() + "/e-nlp/namedEntityRecognition";
		Unirest.setTimeouts(999999999, 999999999);
		return Unirest.post(url);
	}
	
	private HttpRequestWithBody extractRelationsRequest() {
		String url = testHelper.getAPIBaseUrl() + "/e-nlp/extractRelations";
		Unirest.setTimeouts(10000, 10000000);
		return Unirest.post(url);
	}
	
	private HttpRequestWithBody trainOpennlpModel() {
		String url = testHelper.getAPIBaseUrl() + "/e-nlp/trainModel";
		Unirest.setTimeouts(10000, 900000000); // this will take a while, so adjust timeouts
		return Unirest.post(url);
	}
	
	private HttpRequestWithBody listModelsRequest(){
		String url = testHelper.getAPIBaseUrl() + "/e-nlp/listNERModels";
		return Unirest.post(url);
	}
	
	private HttpRequestWithBody baseRattlesnakeRequest() {
		String url = testHelper.getAPIBaseUrl() + "/e-rattlesnakenlp/testURL";
		return Unirest.post(url);
	}
	
	private HttpRequestWithBody detectEvents() {
		String url = testHelper.getAPIBaseUrl() + "/e-nlp/detectEvents";
		return Unirest.post(url);
	}	
	private HttpRequestWithBody detectSexTuples() {
		String url = testHelper.getAPIBaseUrl() + "/e-nlp/detectSextuples";
		return Unirest.post(url);
	}
		
	@Test
	public void test1_detectsEvents() throws UnirestException, IOException, // NOTE: this unit test includes DBpedia lookup and can fail because of that (if dbpedia does not respond). If so, perhaps get rid of it (or build in a link(=no) param)
			Exception {

		// this test is just a sanity check. Using german with english tfidf corpus and tagger and stoplist file...
		HttpResponse<String> response = detectEvents()
				.queryString("informat", "text")
				.queryString("language", "en")
				.body("President Obama has decided to invade Syria during summer and Bill Clinton wants to obtain some petrol.")
				.asString();
		//System.out.println("DEBUGGING response:" + response.getBody());
		assertTrue(response.getStatus() == 200);
		assertTrue(response.getBody().length() > 0);
		String expectedResp = TestConstants.expectedResponse57;
		Assert.assertEquals(expectedResp, response.getBody());
		
	}
	
	
	@Test
	public void test1_detectsSexTuples() throws UnirestException, IOException,
			Exception {

		String letter = "\n" +
				//"[Kolorierte Farbzeichnung]\n" +
				"[Kolorierte Farbzei]\n" +
				"\n" +
				"\n" +
				"\n" +
				"All our love\n" +
				"and dearest\n" +
				"wishes to all\n" +
				"of you\n" +
				"/\n" +
				"Louise+Eric Mendelsohn\n" +
				"\n" +
				"\n";
				
		HttpResponse<String> response = detectSexTuples()
				.queryString("informat", "text")
				.queryString("language", "en")
				.body(letter)
				.asString();
		//System.out.println("DEBUGGING response:" + response.getBody());
		assertTrue(response.getStatus() == 200);
		assertTrue(response.getBody().length() > 0);
		String expectedResp = TestConstants.expectedResponse57;
		Assert.assertEquals(expectedResp, response.getBody());
		
	}
	
}
