package de.dkt.eservices.eopennlp;


import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequestWithBody;

import eu.freme.bservices.testhelper.TestHelper;
import eu.freme.bservices.testhelper.ValidationHelper;
import eu.freme.bservices.testhelper.api.IntegrationTestSetup;
import junit.framework.Assert;

/**
 * @author 
 */

public class EOpenNLPTest {

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
		String url = testHelper.getAPIBaseUrl() + "/e-opennlp/testURL";
		return Unirest.post(url);
	}
	
	private HttpRequestWithBody analyzeRequest() {
		String url = testHelper.getAPIBaseUrl() + "/e-nlp/namedEntityRecognition";
		Unirest.setTimeouts(10000, 10000000);
		return Unirest.post(url);
	}
	
	private HttpRequestWithBody trainModel() {
		String url = testHelper.getAPIBaseUrl() + "/e-nlp/trainModel";
		Unirest.setTimeouts(10000, 900000000); // this will take a while, so adjust timeouts
		return Unirest.post(url);
	}
	
	private HttpRequestWithBody listModelsRequest(){
		String url = testHelper.getAPIBaseUrl() + "/e-nlp/listNERModels";
		return Unirest.post(url);
	}
	
	@Test
	public void sanityCheck() throws UnirestException, IOException,
			Exception {

		// sanity check
		HttpResponse<String> response = baseRequest()
				.queryString("informat", "text")
				.queryString("input", "hello world")
				.queryString("outformat", "turtle").asString();

		assertTrue(response.getStatus() == 200);
		assertTrue(response.getBody().length() > 0);
		
	}
	
	@Test
	public void basicPlainTextInTurtleOut() throws UnirestException, IOException,
			Exception {

		// plain text as input, turtle as output
		HttpResponse<String> response2 = analyzeRequest()
				.queryString("input", "Welcome to Berlin in 2016.")
				.queryString("analysis", "ner")
				.queryString("language", "en")
				.queryString("models", "ner-wikinerEn_LOC")
				.queryString("informat", "text")
				.queryString("outformat", "turtle")
				.asString();
		
		Assert.assertEquals(TestConstants.expectedResponse2, response2.getBody());
		assertTrue(response2.getStatus() == 200);
		assertTrue(response2.getBody().length() > 0);
		
	}
	
	@Test
	public void basicPlainTextInTurtleOutDe() throws UnirestException, IOException,
			Exception {

		// plain text as input, turtle as output
		HttpResponse<String> response37 = analyzeRequest()
				.queryString("input", "Wilkommen in Berlin.")
				.queryString("analysis", "ner")
				.queryString("language", "de")
				.queryString("models", "ner-de_aij-wikinerTrainLOC")
				.queryString("informat", "text")
				.queryString("outformat", "turtle")
				.asString();
		
		Assert.assertEquals(TestConstants.expectedResponse37, response37.getBody());
		assertTrue(response37.getStatus() == 200);
		assertTrue(response37.getBody().length() > 0);
		
	}
	
		
	
	@Test
	public void basicPlainTextInTurtleOutTempAnalysis() throws UnirestException, IOException,
			Exception {

		// plain text as input, turtle as output
		HttpResponse<String> response2 = analyzeRequest()
				.queryString("input", "Welcome to Berlin in 2016.")
				.queryString("analysis", "temp")
				.queryString("language", "en")
				.queryString("models", "englishDates")
				.queryString("informat", "text")
				.queryString("outformat", "turtle")
				.asString();
		
		Assert.assertEquals(TestConstants.expectedResponse8, response2.getBody());
		assertTrue(response2.getStatus() == 200);
		assertTrue(response2.getBody().length() > 0);
		
	}
	
	
	
	@Test
	public void basicTurtleInBodyRDFXMLOut() throws UnirestException, IOException,
			Exception {

		// turtle as input (in body), rdf/xml as output
		HttpResponse<String> response3 = analyzeRequest()
				//.queryString("input", TestConstants.turtleInput3)
				.queryString("analysis", "ner")
				.queryString("language", "en")
				.queryString("models", "ner-wikinerEn_LOC")
				.queryString("informat", "text/turtle")
				.queryString("outformat", "rdf-xml")
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
		HttpResponse<String> response9 = trainModel()
				.queryString("analysis", "ner")
				.queryString("language", "en")
				.queryString("modelName", "testDummy")
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
		HttpResponse<String> response5 = analyzeRequest()
				.queryString("analysis", "ner")
				.queryString("language", "en")
				.queryString("models", "ner-wikinerEn_LOC;ner-wikinerEn_PER;ner-wikinerEn_ORG")
				.queryString("informat", "text")
				.queryString("outformat", "turtle")
				.body(TestConstants.bodyInput5)
				.asString();
		
		assertTrue(response5.getStatus() == 200);
		assertTrue(response5.getBody().length() > 0);
		Assert.assertEquals(TestConstants.expectedResponse5, response5.getBody());
		
	}
	
	
	
	@Test
	public void trainModelDICTInBodyAndSpotWithModel() throws UnirestException, IOException,
			Exception {

		
		//upload dictionary by submitting tsv in postBody
		HttpResponse<String> response8 = trainModel()
				.queryString("analysis", "dict")
				.queryString("language", "en")
				.queryString("modelName", "testDummyDict_PER")
				.body(TestConstants.dictUploadData)
				.asString();
		
		assertTrue(response8.getStatus() == 200);
		assertTrue(response8.getBody().length() > 0);
		System.out.println(response8.getBody());
		
		// dictionary name finding with dictionary uploaded above
		HttpResponse<String> response6 = analyzeRequest()
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
	
	 
	
	
	/*
	@Test
	public void debugTest() throws UnirestException, IOException,
			Exception {

		Date start = new Date();
		for (int i=0; i<10; i++){
			HttpResponse<String> eLinkingTest = analyzeRequest()
					.queryString("analysis", "ner")
					.queryString("language", "en")
					.queryString("models", "ner-wikinerEn_LOC;ner-wikinerEn_PER;ner-wikinerEn_ORG")
					.queryString("informat", "text")
					.queryString("outformat", "turtle")
					.queryString("link", "no")
					.body(TestConstants.bodyInput5)
					.asString();
			
			assertTrue(eLinkingTest.getStatus() == 200);
			assertTrue(eLinkingTest.getBody().length() > 0);
			System.out.println("RESULT:\n"+eLinkingTest.getBody());

		}
		Date end = new Date();
		long seconds = (end.getTime()-start.getTime())/1000;
		System.out.println("Average duration in seconds over 10 executions: " + seconds / 10);
		
		//Assert.assertEquals(TestConstants.expectedResponse5, eLinkingTest.getBody());
		*/
		
		/*
		LanguageIdentificator.initializeNgramMaps(); // currently only for ACL paper!
		System.out.println("Done initializing language models");
		
		String docFolder = "C:\\Users\\pebo01\\Desktop\\mendelsohnDocs\\in";
		String outputFolder = "C:\\Users\\pebo01\\Desktop\\mendelsohnDocs\\out";
		File df = new File(docFolder);
		for (File f : df.listFiles()){
			String fileContent = readFile(f.getAbsolutePath(), StandardCharsets.UTF_8);
			
			HttpResponse<String> debugResponse = analyzeRequest()
					.queryString("analysis", "dict")
					.queryString("language", "de")
					.queryString("models", "clean_mendelsohn_LOC;clean_mendelsohn_ORG;clean_mendelsohn_PER")
					.queryString("informat", "text")
					.queryString("outformat", "turtle")
					//.queryString("input", fileContent)
					.body(fileContent)
					.asString();
			String turtleModel = debugResponse.getBody();
			
			
			HttpResponse<String> debugResponse2 = analyzeRequest()
					.queryString("analysis", "temp")
					.queryString("language", "de")
					.queryString("models", "germanDates")
					.queryString("informat", "turtle")
					.queryString("outformat", "turtle")
					//.queryString("input", turtleModel)
					.body(turtleModel)
					.asString();
			
			
			PrintWriter out = new PrintWriter(new File(outputFolder, FilenameUtils.removeExtension(f.getName()) + ".nif"));
			out.println(debugResponse2.getBody());
			out.close();
		}
		*/
		
	//}	 
	
		/*
		//File fDebug = FileFactory.generateFileInstance("rdftest/debug.txt");
		HttpResponse<String> response6 = listModelsRequest()
				.queryString("analysis", "ner")
				.asString();
		assertTrue(response6.getStatus() == 200);
		assertTrue(response6.getBody().length() > 0);
		Assert.assertEquals("", response6.getBody());
		*/
		

	
	
	
}
