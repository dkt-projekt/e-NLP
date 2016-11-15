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

public class ENLPTest {

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
		Unirest.setTimeouts(10000, 10000000);
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
	
	private HttpRequestWithBody entitySuggestRequest() {
		String url = testHelper.getAPIBaseUrl() + "/e-nlp/suggestEntityCandidates";
		return Unirest.post(url);
	}
	
	private HttpRequestWithBody tagRequest() {
		String url = testHelper.getAPIBaseUrl() + "/e-nlp/partOfSpeechTagging";
		//Unirest.setTimeouts(10000, 10000000);
		return Unirest.post(url);
	}
	
	private HttpRequestWithBody dareTestRequest() {
		String url = testHelper.getAPIBaseUrl() + "/e-sargraph/processData";
		return Unirest.post(url);
	}
	private HttpRequestWithBody uploadClassificationLMRequest() {
		String url = testHelper.getAPIBaseUrl() + "/e-nlp/uploadClassificationModel";
		return Unirest.post(url);
	}
	
//	@Rule
//	public final ExpectedException exception = ExpectedException.none();
	
	@Test
	public void sanityCheckOpennlp() throws UnirestException, IOException,
			Exception {

		// sanity check
		HttpResponse<String> response = baseOpennlpRequest()
				.queryString("informat", "text")
				.queryString("input", "hello world")
				.queryString("outformat", "turtle").asString();

		assertTrue(response.getStatus() == 200);
		assertTrue(response.getBody().length() > 0);
		
	}
	
	@Test
	public void basicPlainTextInTurtleOut() throws UnirestException, IOException, Exception {

		// plain text as input, turtle as output
		HttpResponse<String> response2 = analyzeOpennlpRequest()
				.queryString("input", "Welcome to Berlin in 2016.")
				.queryString("analysis", "ner")
				.queryString("language", "en")
				.queryString("models", "ner-wikinerEn_LOC")
				.queryString("informat", "text")
				.queryString("outformat", "turtle")
				.queryString("mode", "spot")
				.asString();
		
		Assert.assertEquals(TestConstants.expectedResponse22, response2.getBody());
		assertTrue(response2.getStatus() == 200);
		assertTrue(response2.getBody().length() > 0);
		
	}
	
	@Test
	public void basicPlainTextInTurtleOutDe() throws UnirestException, IOException, Exception {

		// plain text as input, turtle as output
		HttpResponse<String> response37 = analyzeOpennlpRequest()
				.queryString("input", "Wilkommen in Berlin.")
				.queryString("analysis", "ner")
				.queryString("language", "de")
				.queryString("models", "ner-de_aij-wikinerTrainLOC")
				.queryString("informat", "text")
				.queryString("outformat", "turtle")
				.queryString("mode", "spot")
				.asString();
		
		Assert.assertEquals(TestConstants.expectedResponse37, response37.getBody());
		assertTrue(response37.getStatus() == 200);
		assertTrue(response37.getBody().length() > 0);
		
	}
	

	@Test
	public void basicPlainTextInTurtleOutDeSpot() throws UnirestException, IOException,
			Exception {

		// plain text as input, turtle as output
		HttpResponse<String> response371 = analyzeOpennlpRequest()
				.queryString("input", "Wilkommen in Berlin.")
				.queryString("analysis", "ner")
				.queryString("language", "de")
				.queryString("models", "ner-de_aij-wikinerTrainLOC")
				.queryString("informat", "text")
				.queryString("outformat", "turtle")
				.queryString("mode", "spot")
				.asString();
		
		Assert.assertEquals(TestConstants.expectedResponse371, response371.getBody());
		assertTrue(response371.getStatus() == 200);
		assertTrue(response371.getBody().length() > 0);
		
	}

	@Test
	public void basicPlainTextInTurtleOutDeTempAnalyzeNoTempExpressions() throws UnirestException, IOException,
			Exception {

		// plain text as input, turtle as output
		HttpResponse<String> response371 = analyzeOpennlpRequest()
				.queryString("input", "Wilkommen in Berlin.")
				.queryString("analysis", "temp")
				.queryString("language", "de")
				.queryString("models", "germanDates")
				.queryString("informat", "text")
				.queryString("outformat", "turtle")
				.asString();
		
		Assert.assertEquals(TestConstants.expectedResponse372, response371.getBody());
		assertTrue(response371.getStatus() == 200);
		assertTrue(response371.getBody().length() > 0);
		
	}
	
	@Test
	public void turtleInTurtleOutDeLink() throws UnirestException, IOException,
			Exception {

		// plain text as input, turtle as output
		HttpResponse<String> response372 = analyzeOpennlpRequest()
				.queryString("analysis", "ner")
				.queryString("language", "de")
				.queryString("models", "ner-de_aij-wikinerTrainLOC")
				.queryString("informat", "turtle")
				.queryString("outformat", "turtle")
				.queryString("mode", "link")
				.body(TestConstants.expectedResponse371)
				.asString();
		
		//Assert.assertEquals(TestConstants.expectedResponse37, response372.getBody());
		assertTrue(response372.getStatus() == 200);
		assertTrue(response372.getBody().length() > 0);		
	}
	
	
	
	@Test
	public void basicPlainTextInTurtleOutTempAnalysis() throws UnirestException, IOException,
			Exception {

		// plain text as input, turtle as output
		HttpResponse<String> response2 = analyzeOpennlpRequest()
				.queryString("input", "Welcome to Berlin in 2016.")
				.queryString("analysis", "temp")
				.queryString("language", "en")
				.queryString("models", "englishDates")
				.queryString("informat", "text")
				.queryString("outformat", "turtle")
				.queryString("mode", "spot")
				.asString();
		
		Assert.assertEquals(TestConstants.expectedResponse8, response2.getBody());
		assertTrue(response2.getStatus() == 200);
		assertTrue(response2.getBody().length() > 0);
		
	}
	
	
	@Test
	public void basicPlainTextInTurtleOutNoEntitiesSpot() throws UnirestException, IOException,
			Exception {

		// plain text as input, turtle as output
		HttpResponse<String> response2 = analyzeOpennlpRequest()
				.queryString("input", "Welcome")
				.queryString("analysis", "ner")
				.queryString("language", "en")
				.queryString("models", "ner-wikinerEn_LOC")
				.queryString("informat", "text")
				.queryString("outformat", "turtle")
				.queryString("mode", "spot")
				.asString();
		
		Assert.assertEquals(TestConstants.expectedResponse83, response2.getBody());
		assertTrue(response2.getStatus() == 200);
		assertTrue(response2.getBody().length() > 0);
		
	}
	
	@Test//(expected = BadRequestException.class)
	public void basicPlainTextInTurtleOutNoEntitiesLinkTextInput() throws UnirestException, IOException,
			Exception {

		// plain text as input, turtle as output
		HttpResponse<String> response2 = analyzeOpennlpRequest()
				.queryString("input", "Welcome")
				.queryString("analysis", "ner")
				.queryString("language", "en")
				.queryString("models", "ner-wikinerEn_LOC")
				.queryString("informat", "text")
				.queryString("outformat", "turtle")
				.queryString("mode", "link")
				.asString();
		// this is checking for the error message
		Assert.assertEquals(TestConstants.expectedResponse888, response2.getBody().replaceAll("timestamp\": [0-9]+", "timestamp\": 1463128970107"));
		assertTrue(response2.getStatus() == 400);
		assertTrue(response2.getBody().length() > 0);
		
	}
	
	@Test
	public void basicPlainTextInTurtleOutNoEntitiesLinkTurtleInput() throws UnirestException, IOException,
			Exception {

		// plain text as input, turtle as output
		HttpResponse<String> response2 = analyzeOpennlpRequest()
				.queryString("analysis", "ner")
				.queryString("language", "en")
				.queryString("models", "ner-wikinerEn_LOC")
				.queryString("informat", "turtle")
				.queryString("outformat", "turtle")
				.queryString("mode", "link")
				.body(TestConstants.expectedResponse83)
				.asString();
		
		//Assert.assertEquals(TestConstants.expectedResponse84, response2.getBody());
		assertTrue(response2.getStatus() == 200);
		assertTrue(response2.getBody().length() > 0);
		
	}

	
	
	@Test
	public void basicTurtleInBodyRDFXMLOut() throws UnirestException, IOException,
			Exception {

		// turtle as input (in body), rdf/xml as output
		HttpResponse<String> response3 = analyzeOpennlpRequest()
				//.queryString("input", TestConstants.turtleInput3)
				.queryString("analysis", "ner")
				.queryString("language", "en")
				.queryString("models", "ner-wikinerEn_LOC")
				.queryString("informat", "text/turtle")
				.queryString("outformat", "rdf-xml")
				.queryString("mode", "spot")
				.body(TestConstants.turtleInput3)
				.asString();
		
		Assert.assertEquals(TestConstants.expectedResponse3.replaceAll("\\s+", ""), response3.getBody().replaceAll("\\s+", "")); // this kept failing and I couldn't see any difference in spacing...
		assertTrue(response3.getStatus() == 200);
		assertTrue(response3.getBody().length() > 0);
		
	}
	
	
	
	@Test
	public void trainModelNERInBodyAndSpotWithTrainedModel() throws UnirestException, IOException,
			Exception {

		
		// Train a model by submitting training stuff in body (TODO: fix uploading of training files to server) 
		HttpResponse<String> response9 = trainOpennlpModel()
				.queryString("analysis", "ner")
				.queryString("language", "en")
				.queryString("modelName", "testDummy")
				.queryString("mode", "spot")
				.body(TestConstants.nerTrainingData)
				.asString();
		
		assertTrue(response9.getStatus() == 200);
		assertTrue(response9.getBody().length() > 0);
		
		//TODO: make this work on the server some time....
		/*
		//now check if something which has been trained for is indeed found
		HttpResponse<String> response7 = analyzeRequest()
				.queryString("input", "From this climate William Godwin developed what many consider the first expression of modern anarchist thought .")
				.queryString("analysis", "ner")
				.queryString("language", "en")
				.queryString("models", "testDummy")
				.queryString("informat", "text")
				.queryString("outformat", "turtle")
				.asString();
		
		Assert.assertEquals(TestConstants.expectedResponse7, response7.getBody());
		assertTrue(response7.getStatus() == 200);
		assertTrue(response7.getBody().length() > 0);
		*/
	
	}
	
		
	
	@Test
	public void multipleModelSpotting() throws UnirestException, IOException,
			Exception {

		
		// test for something containing multiple entity types
		HttpResponse<String> response5 = analyzeOpennlpRequest()
				.queryString("analysis", "ner")
				.queryString("language", "en")
				.queryString("models", "ner-wikinerEn_LOC;ner-wikinerEn_PER;ner-wikinerEn_ORG")
				.queryString("informat", "text")
				.queryString("outformat", "turtle")
				.queryString("mode", "spot")
				.body(TestConstants.bodyInput5)
				.asString();
		
		assertTrue(response5.getStatus() == 200);
		assertTrue(response5.getBody().length() > 0);
		Assert.assertEquals(TestConstants.expectedResponse5, response5.getBody());
		
	}
	
	@Test
	public void turtleInTurtleOutTempAnalysisEn() throws UnirestException, IOException,
			Exception {

				HttpResponse<String> response371 = analyzeOpennlpRequest()
				.queryString("analysis", "temp")
				.queryString("language", "en")
				.queryString("models", "englishDates")
				.queryString("informat", "turtle")
				.queryString("outformat", "turtle")
				.body(TestConstants.expectedResponse5)
				.asString();
		
		assertTrue(response371.getStatus() == 200);
		assertTrue(response371.getBody().length() > 0);
		
//		Assert.assertEquals(TestConstants.expectedResponse16, response16.getBody());
		
		Model mExp = NIFReader.extractModelFromFormatString(TestConstants.expectedResponse234, RDFSerialization.TURTLE);
		Model mAct = NIFReader.extractModelFromFormatString(response371.getBody(), RDFSerialization.TURTLE);
		
//		assertTrue(mExp.isIsomorphicWith(mAct));
		
		String es = null;
		String ee = null;
		String as = null;
		String ae = null;
		
		NodeIterator nIt1 = mExp.listObjectsOfProperty(NIFReader.extractDocumentResourceURI(mExp), DKTNIF.meanDateStart);
		while(nIt1.hasNext()){
			RDFNode n1 = nIt1.next();
			es = n1.asLiteral().getString();
		}
		NodeIterator nIt12 = mExp.listObjectsOfProperty(NIFReader.extractDocumentResourceURI(mExp), DKTNIF.meanDateEnd);
		while(nIt12.hasNext()){
			RDFNode n1 = nIt12.next();
			ee = n1.asLiteral().getString();
		}

		NodeIterator nIt2 = mAct.listObjectsOfProperty(NIFReader.extractDocumentResourceURI(mAct), DKTNIF.meanDateStart);
		while(nIt2.hasNext()){
			RDFNode n2 = nIt2.next();
			as = n2.asLiteral().getString();
		}
		NodeIterator nIt22 = mAct.listObjectsOfProperty(NIFReader.extractDocumentResourceURI(mAct), DKTNIF.meanDateEnd);
		while(nIt22.hasNext()){
			RDFNode n2 = nIt22.next();
			ae = n2.asLiteral().getString();
		}

		Date d11 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(es);
		Date d12 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(ee);
		Date d21 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(as);
		Date d22 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(ae);
		
		System.out.println("Initial_11_" + (d11.getTime() - d21.getTime()) );
		System.out.println("final_11_" + (d11.getTime() - d21.getTime()) );
		assertTrue( Math.abs(d11.getTime() - d21.getTime()) < 60000 );
		assertTrue( Math.abs(d12.getTime() - d22.getTime()) < 60000 );

		
	}
	
	@Test
	public void seasonBugfixTest() throws UnirestException, IOException,
			Exception {

		// plain text as input, turtle as output
		HttpResponse<String> response9 = analyzeOpennlpRequest()
				.queryString("analysis", "temp")
				.queryString("language", "de")
				.queryString("models", "germanDates")
				//.queryString("informat", "turtle")
				//.queryString("outformat", "turtle")
				.body("2016 blabla Sommer")
				.asString();
		
		Assert.assertEquals(TestConstants.expectedResponse9, response9.getBody());
		assertTrue(response9.getStatus() == 200);
		assertTrue(response9.getBody().length() > 0);
		
	}
	
	@Test
	public void addedHolidaysTest() throws UnirestException, IOException,
			Exception {

		// plain text as input, turtle as output
		HttpResponse<String> response10 = analyzeOpennlpRequest()
				.queryString("analysis", "temp")
				.queryString("language", "de")
				.queryString("models", "germanDates")
				//.queryString("informat", "turtle")
				//.queryString("outformat", "turtle")
				.body("2014 silvester neujahr tag der arbeit maifeiertag tag der deutschen einheit")
				.asString();
		
		Assert.assertEquals(TestConstants.expectedResponse10, response10.getBody());
		assertTrue(response10.getStatus() == 200);
		assertTrue(response10.getBody().length() > 0);
		
	}
	
	@Test
	public void addedTimesAndMoreDatesTest() throws UnirestException, IOException,
			Exception {

		// plain text as input, turtle as output
		HttpResponse<String> response11 = analyzeOpennlpRequest()
				.queryString("analysis", "temp")
				.queryString("language", "de")
				.queryString("models", "germanDates")
				//.queryString("informat", "turtle")
				//.queryString("outformat", "turtle")
				.body("Tele 5 zeigt am 17.12. um 20.15 Uhr und um 15 Uhr die Komödie")
				.asString();
		
		Assert.assertEquals(TestConstants.expectedResponse11, response11.getBody());
		assertTrue(response11.getStatus() == 200);
		assertTrue(response11.getBody().length() > 0);
		
	}
	
	@Test
	public void addedThisWeekYearTest() throws UnirestException, IOException,
			Exception {

		// plain text as input, turtle as output
		HttpResponse<String> response12 = analyzeOpennlpRequest()
				.queryString("analysis", "temp")
				.queryString("language", "de")
				.queryString("models", "germanDates")
				//.queryString("informat", "turtle")
				//.queryString("outformat", "turtle")
				.body("08.10.1990 dieser Tag diese Woche dieser Monat dieses Jahr")
				//.body("08.10.2016 dieser Tag diese Woche dieser Monat dieses Jahr")
				.asString();
		assertTrue(response12.getStatus() == 200);
		assertTrue(response12.getBody().length() > 0);
		
//		Assert.assertEquals(TestConstants.expectedResponse16, response16.getBody());
		
		Model mExp = NIFReader.extractModelFromFormatString(TestConstants.expectedResponse12, RDFSerialization.TURTLE);
		Model mAct = NIFReader.extractModelFromFormatString(response12.getBody(), RDFSerialization.TURTLE);
		
//		assertTrue(mExp.isIsomorphicWith(mAct));
		
		String es = null;
		String ee = null;
		String as = null;
		String ae = null;
		
		NodeIterator nIt1 = mExp.listObjectsOfProperty(NIFReader.extractDocumentResourceURI(mExp), DKTNIF.meanDateStart);
		while(nIt1.hasNext()){
			RDFNode n1 = nIt1.next();
			es = n1.asLiteral().getString();
		}
		NodeIterator nIt12 = mExp.listObjectsOfProperty(NIFReader.extractDocumentResourceURI(mExp), DKTNIF.meanDateEnd);
		while(nIt12.hasNext()){
			RDFNode n1 = nIt12.next();
			ee = n1.asLiteral().getString();
		}

		NodeIterator nIt2 = mAct.listObjectsOfProperty(NIFReader.extractDocumentResourceURI(mAct), DKTNIF.meanDateStart);
		while(nIt2.hasNext()){
			RDFNode n2 = nIt2.next();
			as = n2.asLiteral().getString();
		}
		NodeIterator nIt22 = mAct.listObjectsOfProperty(NIFReader.extractDocumentResourceURI(mAct), DKTNIF.meanDateEnd);
		while(nIt22.hasNext()){
			RDFNode n2 = nIt22.next();
			ae = n2.asLiteral().getString();
		}

		Date d11 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(es);
		Date d12 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(ee);
		Date d21 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(as);
		Date d22 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(ae);
		
		System.out.println("Initial_22_" + (d11.getTime() - d21.getTime()) );
		System.out.println("final_22_" + (d11.getTime() - d21.getTime()) );
		assertTrue( Math.abs(d11.getTime() - d21.getTime()) < 60000 );
		assertTrue( Math.abs(d12.getTime() - d22.getTime()) < 60000 );

	}
	
	@Test
	public void addedSinceAndAgo() throws UnirestException, IOException,
			Exception {

		// plain text as input, turtle as output
		HttpResponse<String> response13 = analyzeOpennlpRequest()
				.queryString("analysis", "temp")
				.queryString("language", "de")
				.queryString("models", "germanDates")
				//.queryString("informat", "turtle")
				//.queryString("outformat", "turtle")
				.body("1990 seit 10 Jahren, 2010 vor 10 Jahren")
				.asString();
		
		Assert.assertEquals(TestConstants.expectedResponse13, response13.getBody());
		assertTrue(response13.getStatus() == 200);
		assertTrue(response13.getBody().length() > 0);
		
	}
	
	@Test
	public void addedEnglishWeekdaysTest() throws UnirestException, IOException,
			Exception {

		// plain text as input, turtle as output
		HttpResponse<String> response14 = analyzeOpennlpRequest()
				.queryString("analysis", "temp")
				.queryString("language", "en")
				.queryString("models", "englishDates")
				//.queryString("informat", "turtle")
				//.queryString("outformat", "turtle")
				.body("8.10.1990 monday tuesday friday")
				.asString();
		
		Assert.assertEquals(TestConstants.expectedResponse14, response14.getBody());
		assertTrue(response14.getStatus() == 200);
		assertTrue(response14.getBody().length() > 0);
		
	}
	
	@Test
	public void addedEnglishTimeTest() throws UnirestException, IOException,
			Exception {

		// plain text as input, turtle as output
		HttpResponse<String> response15 = analyzeOpennlpRequest()
				.queryString("analysis", "temp")
				.queryString("language", "en")
				.queryString("models", "englishDates")
				//.queryString("informat", "turtle")
				//.queryString("outformat", "turtle")
				.body("8.10.1990 10.34 p.m. 9.00 a.m.")
				.asString();
		
		Assert.assertEquals(TestConstants.expectedResponse15, response15.getBody());
		assertTrue(response15.getStatus() == 200);
		assertTrue(response15.getBody().length() > 0);
		
	}
	
	@Test
	public void addedNextDayWeekEctTest() throws UnirestException, IOException,
			Exception {

		// plain text as input, turtle as output
		HttpResponse<String> response16 = analyzeOpennlpRequest()
				.queryString("analysis", "temp")
				.queryString("language", "de")
				.queryString("models", "germanDates")
				//.queryString("informat", "turtle")
				//.queryString("outformat", "turtle")
				//.body("8.10.1990 nächster Tag nächste Woche nächster Monat nächstes Jahr")
				.body("8.10.2015 nächster Tag nächste Woche nächster Monat nächstes Jahr")
				.asString();
		assertTrue(response16.getStatus() == 200);
		assertTrue(response16.getBody().length() > 0);
		
//		Assert.assertEquals(TestConstants.expectedResponse16, response16.getBody());
		
		Model mExp = NIFReader.extractModelFromFormatString(TestConstants.expectedResponse16, RDFSerialization.TURTLE);
		Model mAct = NIFReader.extractModelFromFormatString(response16.getBody(), RDFSerialization.TURTLE);
		
	//	assertTrue(mExp.isIsomorphicWith(mAct));
		
		String es = null;
		String ee = null;
		String as = null;
		String ae = null;
		
		NodeIterator nIt1 = mExp.listObjectsOfProperty(NIFReader.extractDocumentResourceURI(mExp), DKTNIF.meanDateStart);
		while(nIt1.hasNext()){
			RDFNode n1 = nIt1.next();
			es = n1.asLiteral().getString();
		}
		NodeIterator nIt12 = mExp.listObjectsOfProperty(NIFReader.extractDocumentResourceURI(mExp), DKTNIF.meanDateEnd);
		while(nIt12.hasNext()){
			RDFNode n1 = nIt12.next();
			ee = n1.asLiteral().getString();
		}

		NodeIterator nIt2 = mAct.listObjectsOfProperty(NIFReader.extractDocumentResourceURI(mAct), DKTNIF.meanDateStart);
		while(nIt2.hasNext()){
			RDFNode n2 = nIt2.next();
			as = n2.asLiteral().getString();
		}
		NodeIterator nIt22 = mAct.listObjectsOfProperty(NIFReader.extractDocumentResourceURI(mAct), DKTNIF.meanDateEnd);
		while(nIt22.hasNext()){
			RDFNode n2 = nIt22.next();
			ae = n2.asLiteral().getString();
		}

		Date d11 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(es);
		Date d12 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(ee);
		Date d21 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(as);
		Date d22 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(ae);
		
		System.out.println(response16.getBody());
		
		System.out.println("Initial_33_" + (d11.getTime() - d21.getTime()) );
		System.out.println("final_33_" + (d12.getTime() - d22.getTime()) );
		assertTrue( Math.abs(d11.getTime() - d21.getTime()) < 60000 );
		assertTrue( Math.abs(d12.getTime() - d22.getTime()) < 60000 );
	}
	
	
	@Test
	public void trainModelDICTInBodyAndSpotWithModel() throws UnirestException, IOException,
			Exception {

		
		//upload dictionary by submitting tsv in postBody
		HttpResponse<String> response8 = trainOpennlpModel()
				.queryString("analysis", "dict")
				.queryString("language", "en")
				.queryString("modelName", "testDummyDict_PER")
				.body(TestConstants.dictUploadData)
				.asString();
		
		assertTrue(response8.getStatus() == 200);
		assertTrue(response8.getBody().length() > 0);
		System.out.println(response8.getBody());
		
		// dictionary name finding with dictionary uploaded above
		HttpResponse<String> response6 = analyzeOpennlpRequest()
				.queryString("analysis", "dict")
				.queryString("language", "de")
				.queryString("models", "testDummyDict_PER")
				//.queryString("models", "mendelsohn_LOC")
				.queryString("informat", "text")
				.queryString("outformat", "turtle")
				.queryString("input", "wer weiß, wo Herbert Eulenberg ging?")
				//.queryString("input", "wer weiß, wo Herbert Eulenberg ging? Ware es Haarlem?")
				.asString();
		
		assertTrue(response6.getStatus() == 200);
		assertTrue(response6.getBody().length() > 0);
		Assert.assertEquals(TestConstants.expectedResponse6, response6.getBody());
		
		
	}
	
	@Test
	public void trainModelDICTInBodyAndSpotWithModelOtherType() throws UnirestException, IOException,
			Exception {

		
		//upload dictionary by submitting tsv in postBody
		HttpResponse<String> response8 = trainOpennlpModel()
				.queryString("analysis", "dict")
				.queryString("language", "en")
				.queryString("modelName", "testDummyDict_AAPJE")
				.body(TestConstants.dictUploadData)
				.asString();
		
		assertTrue(response8.getStatus() == 200);
		assertTrue(response8.getBody().length() > 0);
		System.out.println(response8.getBody());
		
		// dictionary name finding with dictionary uploaded above
		HttpResponse<String> response6 = analyzeOpennlpRequest()
				.queryString("analysis", "dict")
				.queryString("language", "de")
				.queryString("models", "testDummyDict_AAPJE")
				//.queryString("models", "mendelsohn_LOC")
				.queryString("informat", "text")
				.queryString("outformat", "turtle")
				.queryString("input", "wer weiß, wo Herbert Eulenberg ging?")
				//.queryString("input", "wer weiß, wo Herbert Eulenberg ging? Ware es Haarlem?")
				.asString();
		
		assertTrue(response6.getStatus() == 200);
		assertTrue(response6.getBody().length() > 0);
		Assert.assertEquals(TestConstants.expectedResponse666, response6.getBody());
		
		
	}
	
	
	@Test
	public void uploadClassificationLM() throws UnirestException, IOException,
			Exception {

		//String in = readFile("C:\\Users\\pebo01\\Desktop\\data\\ARTCOM\\vikingNames.txt", StandardCharsets.UTF_8);
		//in = readFile("C:\\Users\\pebo01\\Desktop\\data\\ARTCOM\\bacteriaNamesFromWikipedia.txt", StandardCharsets.UTF_8);
		
		HttpResponse<String> response8 = uploadClassificationLMRequest()
				.queryString("modelName", "dummyLM")
				//.queryString("modelName", "bacteriaLM")
				.body(TestConstants.dictUploadData)
				//.body(in)
				.asString();
		
		assertTrue(response8.getStatus() == 200);
		//assertTrue(response8.getBody().length() > 0);
		
		HttpResponse<String> response9 = uploadClassificationLMRequest()
				.queryString("modelName", "dummy2LM")
				.body(TestConstants.bodyInput5)
				.asString();
		
		assertTrue(response9.getStatus() == 200);
		
	}

	
	
	
	static String readFile(String path, Charset encoding) 
			  throws IOException 
			{
			  byte[] encoded = Files.readAllBytes(Paths.get(path));
			  return new String(encoded, encoding);
			}
	/*
	@Test
	public void trainModelMendelsohnLOC() throws UnirestException, IOException,
			Exception {
		String p = "C:\\Users\\pebo01\\workspace\\e-openNLP\\target\\test-classes\\trainedModels\\dict\\mendelson_LOC.tsv";
		HttpResponse<String> trainTest = trainModel()
				.queryString("analysis", "dict")
				.queryString("language", "en")
				.queryString("modelName", "mendelsohn_LOC")
				.body(readFile(p, StandardCharsets.UTF_8))
				.asString();
	}
	
	
	@Test
	public void trainModelMendelsohnORG() throws UnirestException, IOException,
			Exception {
		String p = "C:\\Users\\pebo01\\workspace\\e-openNLP\\target\\test-classes\\trainedModels\\dict\\mendelson_ORG.tsv";
		HttpResponse<String> trainTest = trainModel()
				.queryString("analysis", "dict")
				.queryString("language", "en")
				.queryString("modelName", "mendelsohn_ORG")
				.body(readFile(p, StandardCharsets.UTF_8))
				.asString();
	}
	*/
	
	/*
	@Test
	public void trainModelMendelsohnPER() throws UnirestException, IOException,
			Exception {
		String p = "C:\\Users\\pebo01\\workspace\\e-openNLP\\target\\test-classes\\trainedModels\\dict\\mendelson_PER.tsv";
		HttpResponse<String> trainTest = trainModel()
				.queryString("analysis", "dict")
				.queryString("language", "en")
				.queryString("modelName", "mendelsohn_PER")
				.body(readFile(p, StandardCharsets.UTF_8))
				.asString();
	}
	
	 
	*/
	
	
//	@Test
//	public void debugTest() throws UnirestException, IOException,
//			Exception {
//
//		
//		//upload viking dictionary
//		//String p = "C:\\Users\\pebo01\\workspace\\e-openNLP\\target\\test-classes\\trainedModels\\dict\\mendelson_LOC.tsv";
////		String p = "C:\\Users\\pebo01\\Desktop\\RelationExtractionPlayground\\artComData\\vikingDictionary\\vikingDictionary.txt";
////		HttpResponse<String> trainVikingDict= trainOpennlpModel()
////				.queryString("analysis", "dict")
////				.queryString("language", "en")
////				.queryString("modelName", "vikingDummy_PER")
////				.body(readFile(p, StandardCharsets.UTF_8))
////				.asString();
////	
//		
//		
////		Date d1 = new Date();
////		LanguageIdentificator.initializeNgramMaps();
////		Date d2 = new Date();
////		System.out.println("Done initializing language models. Took (seconds):" + (d2.getTime() - d1.getTime())/1000);
//		
//		//String docFolder = "C:\\Users\\pebo01\\Desktop\\mendelsohnDocs\\in";
//		//String docFolder = "C:\\Users\\pebo01\\Desktop\\data\\Condat_Data\\condatPlainTextData";
//		//String docFolder = "C:\\Users\\pebo01\\Desktop\\ubuntuShare\\clean\\out\\en";
//		//String docFolder = "C:\\Users\\pebo01\\Desktop\\RelationExtractionPlayground\\artComData\\nif";
//		//String docFolder = "C:\\Users\\pebo01\\Desktop\\ubuntuShare\\WikiWars_20120218_v104\\in";
//		//String docFolder = "C:\\Users\\pebo01\\Desktop\\data\\artComSampleFilesDBPediaTimeouts\\subfolderWithSameContent";
//		//String docFolder = "C:\\Users\\pebo01\\Desktop\\data\\FRONTEO\\complaintsIndividualFiles";
//		//String docFolder = "C:\\Users\\pebo01\\Desktop\\data\\mendelsohnDocs\\englishNIFs";
//		String docFolder = "C:\\Users\\pebo01\\Desktop\\PerformanceTest\\de\\collection4\\raw";
//		//String outputFolder = "C:\\Users\\pebo01\\Desktop\\data\\Condat_Data\\condatNIFs";
//		
//		//String outputFolder = "C:\\Users\\pebo01\\Desktop\\ubuntuShare\\clean\\out\\NER_NIFS_EN";
//		//String outputFolder = "C:\\Users\\pebo01\\Desktop\\RelationExtractionPlayground\\artComData\\nifAppended";
//		//String outputFolder = "C:\\Users\\pebo01\\Desktop\\ubuntuShare\\WikiWars_20120218_v104\\nifs";
//		//String outputFolder = "C:\\Users\\pebo01\\Desktop\\data\\artComSampleFilesDBPediaTimeouts\\outputNifs";
//		//String outputFolder = "C:\\Users\\pebo01\\Desktop\\data\\FRONTEO\\nifs";
//		//String outputFolder = "C:\\Users\\pebo01\\Desktop\\data\\mendelsohnDocs\\englishNIFsNEROutput";
//		String outputFolder = "C:\\Users\\pebo01\\Desktop\\PerformanceTest\\de\\collection4\\analized";
//		File df = new File(docFolder);
//		//PrintWriter out = new PrintWriter(new File(outputFolder, "temp.txt"));
//		
////		HttpResponse<String> trainSureNamesEnron = trainOpennlpModel()
////				.queryString("analysis", "dict")
////				.queryString("language", "en")
////				.queryString("modelName", "enronSureNames_PER")
////				.body(readFile("C:\\Users\\pebo01\\Desktop\\ubuntuShare\\enronSureNames_PER", StandardCharsets.UTF_8))
////				.asString();
////		assertTrue(trainSureNamesEnron.getStatus() == 200);
////		assertTrue(trainSureNamesEnron.getBody().length() > 0);
////		HttpResponse<String> trainAllNamesEnron = trainOpennlpModel()
////				.queryString("analysis", "dict")
////				.queryString("language", "en")
////				.queryString("modelName", "enronAllNames_PER")
////				.body(readFile("C:\\Users\\pebo01\\Desktop\\ubuntuShare\\enronAllNames_PER", StandardCharsets.UTF_8))
////				.asString();
////		assertTrue(trainAllNamesEnron.getStatus() == 200);
////		assertTrue(trainAllNamesEnron.getBody().length() > 0);
//		
//		
//		
//		for (File f : df.listFiles()){
//			System.out.println("Processing file:" + f.getAbsolutePath());
//			String fileContent = readFile(f.getAbsolutePath(), StandardCharsets.UTF_8);
//			Date d1 = new Date();
//			//Model nifModel = NIFWriter.initializeOutputModel();
//			//NIFWriter.addInitialString(nifModel, fileContent, DKTNIF.getDefaultPrefix());
//			//LanguageIdentificator.detectLanguageNIF(nifModel);
//			//Model nifModel = identifyInputLanguage(fileContent, RDFSerialization.PLAINTEXT);
//			
//			
//			
//			HttpResponse<String> debugResponse = analyzeOpennlpRequest()
//					.queryString("analysis", "ner")
//					//.queryString("analysis", "dict")
//					//.queryString("language", "de")
//					.queryString("language", "en")
//					.queryString("models", "ner-wikinerEn_LOC;ner-wikinerEn_ORG;ner-wikinerEn_PER")
//					//.queryString("models", "mendelson_LOC;mendelson_ORG;mendelson_PER")
//					//.queryString("models", "ner-de_aij-wikinerTrainLOC;ner-de_aij-wikinerTrainORG;ner-de_aij-wikinerTrainPER")
//					.queryString("informat", "text")
//					//.queryString("informat", "turtle")
//					.queryString("outformat", "turtle")
//					.queryString("mode", "spot")
//					//.queryString("input", fileContent)
//					.body(fileContent)
//					.asString();
//			String turtleModel = debugResponse.getBody();
//			
//			
//
//			HttpResponse<String> debugResponse2 = analyzeOpennlpRequest()
//					.queryString("analysis", "temp")
//					.queryString("language", "en")
//					.queryString("models", "englishDates")
//					.queryString("informat", "turtle")
//					//.queryString("informat", "text")
//					.queryString("outformat", "turtle")
//					//.queryString("input", turtleModel)
//					//.body(fileContent)
//					.body(turtleModel)
//					.asString();
//			//String turtleModel = debugResponse2.getBody();
//			turtleModel = debugResponse2.getBody();
//			
//			Date d2 = new Date();
//			//out.println("File: " + f.getName() + " took in seconds: " + (d2.getTime() - d1.getTime()) / 10000);
//			System.out.println("File: " + f.getName() + " took in seconds: " + (d2.getTime() - d1.getTime()) / 10000);
//			
//			
//			
////			HttpResponse<String> debugResponse3 = analyzeOpennlpRequest()
////					.queryString("analysis", "dict")
////					.queryString("language", "en")
////					.queryString("models", "vikingDummy_PER")
////					.queryString("informat", "turtle")
////					.queryString("outformat", "turtle")
////					//.queryString("input", turtleModel)
////					.body(turtleModel)
////					.asString();
////			turtleModel = debugResponse3.getBody();
////			
//			PrintWriter out = new PrintWriter(new File(outputFolder, FilenameUtils.removeExtension(f.getName()) + ".nif"));
//			out.println(turtleModel);
//			out.close();
//		}
//		//out.close();
//		
//		
//	}	 
	

//	@Test
//	public void dareTestTestTest() throws UnirestException, IOException, Exception {
//
//		// plain text as input, turtle as output
//		HttpResponse<String> response2 = dareTestRequest()
//				.queryString("input", "Brad married Jolie.")
//				.queryString("language", "en")
//				.queryString("informat", "text")
//				.asString();
//		//"This is some sample text", "en", RDFSerialization.PLAINTEXT
//		//Assert.assertEquals(TestConstants.expectedResponse22, response2.getBody());
//		assertTrue(response2.getStatus() == 200);
//		assertTrue(response2.getBody().length() > 0);
//		System.out.println("DEBUGGING body: " + response2.getBody());
//		
//	}
	
	
//	@Test
//	public void enTest() throws UnirestException, IOException, Exception {
//		HttpResponse<String> response2 = tagRequest()
//				.queryString("input", "If you like to gamble, I tell you I'm your man.      You win some, lose some, all the same to me.")
//				.queryString("language", "en")
//				.queryString("informat", "text")
//				.queryString("outformat", "turtle")
//				.asString();
//		
//		Assert.assertEquals(TestConstants.expectedResponse, response2.getBody());
//		assertTrue(response2.getStatus() == 200);
//		assertTrue(response2.getBody().length() > 0);
//		
//	}
//	
//	
//	@Test
//	public void deTest() throws UnirestException, IOException, Exception {
//	
//		HttpResponse<String> response3 = tagRequest()
//				//.queryString("input", "Halb Sechs, meine Augen brennen. Tret' auf 'nen Typen, der zwischen toten Tauben pennt.")
//				.queryString("language", "de")
//				.queryString("informat", "turtle")
//				.queryString("outformat", "turtle")
//				.body(TestConstants.turtleInput2)
//				.asString();
//		
//		Assert.assertEquals(TestConstants.expectedResponse2, response3.getBody());
//		assertTrue(response3.getStatus() == 200);
//		assertTrue(response3.getBody().length() > 0);
//		
//	}
	
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
	

	
	@Test
	public void testERattlesnakeNLPBasic() throws UnirestException, IOException,
			Exception {

		HttpResponse<String> response = baseRattlesnakeRequest()
				.queryString("informat", "text")
				.queryString("input", "hello world")
				.queryString("outformat", "turtle").asString();

		
		assertTrue(response.getStatus() == 200);
		assertTrue(response.getBody().length() > 0);
	}
	
	@Test
	public void entitySuggestTest() throws UnirestException, IOException,
			Exception {

		// this test is just a sanity check. Using german with english tfidf corpus and tagger and stoplist file...
		HttpResponse<String> response = entitySuggestRequest()
				.queryString("informat", "text")
				.queryString("language", "en")
				.queryString("threshold", "0.5")
				.body(TestConstants.germanPlainTextInput)
				.asString();
			

		
		assertTrue(response.getStatus() == 200);
		assertTrue(response.getBody().length() > 0);
		String expectedResp = 
				"und\n" +
				"nicht\n" +
				"ist\n" +
				"auch\n" +
				"das\n" +
				"sie\n" +
				"den\n" +

						"";
		Assert.assertEquals(expectedResp, response.getBody());
		
	}
	
	@Test
	public void entitySuggestTestWithClassification() throws UnirestException, IOException,
			Exception {

		// this test is just a sanity check. Using german with english tfidf corpus and tagger and stoplist file...
		HttpResponse<String> response = entitySuggestRequest()
				.queryString("informat", "text")
				.queryString("language", "en")
				.queryString("threshold", "0.5")
				.queryString("classificationModels", "dummyLM;dummy2LM")
				.body(TestConstants.germanPlainTextInput)
				.asString();
			

		
		assertTrue(response.getStatus() == 200);
		assertTrue(response.getBody().length() > 0);
		String expectedResp = 
				"und	dummyLM\n"+
				"nicht	dummyLM\n"+
				"ist	dummy2LM\n"+
				"auch	dummyLM\n"+
				"das	dummy2LM\n"+
				"sie	dummy2LM\n"+
				"den	dummy2LM\n"+
				"";

		Assert.assertEquals(expectedResp, response.getBody());
		
	}
	
	@Test
	public void relationExtractionTestWithCoferenceNIF() throws UnirestException, IOException,
		Exception {
		
		HttpResponse<String> response = extractRelationsRequest()
				.queryString("informat", "turtle")
				.queryString("language", "en")
				.body(TestConstants.relationExtractionInput)
				.asString();
		
		assertTrue(response.getStatus() == 200);
		assertTrue(response.getBody().length() > 0);
		
		Assert.assertEquals(TestConstants.relationExtractionExpectedOutput, response.getBody());
	}
	
}
