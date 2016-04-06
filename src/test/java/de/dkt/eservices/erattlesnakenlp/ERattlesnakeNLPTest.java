package de.dkt.eservices.erattlesnakenlp;


import static org.junit.Assert.assertTrue;

import java.io.IOException;

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

public class ERattlesnakeNLPTest {

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
		String url = testHelper.getAPIBaseUrl() + "/e-rattlesnakenlp/testURL";
		return Unirest.post(url);
	}
	
	private HttpRequestWithBody segmentParagraphs() {
		String url = testHelper.getAPIBaseUrl() + "/e-rattlesnakenlp/segmentParagraphs";
		return Unirest.post(url);
	}
	
	
	@Test
	public void testERattlesnakeNLPBasic() throws UnirestException, IOException,
			Exception {

		HttpResponse<String> response = baseRequest()
				.queryString("informat", "text")
				.queryString("input", "hello world")
				.queryString("outformat", "turtle").asString();

		System.out.println("BODY: "+response.getBody());
		System.out.println("STATUS:" + response.getStatus());

		assertTrue(response.getStatus() == 200);
		assertTrue(response.getBody().length() > 0);
		
		String testFilePath = "rdftest/paragraphTest.txt";
		HttpResponse<String> response2 = segmentParagraphs()
				.queryString("inputFile", testFilePath)
				.queryString("language", "en")
				.asString();
		
		
		System.out.println("DEBUGGING actual:\n" + response2.getBody() + "|||");
		
		String expectedNIF = "<rdf:RDF\n" +
				"    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n" +
				"    xmlns:itsrdf=\"http://www.w3.org/2005/11/its/rdf#\"\n" +
				"    xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"\n" +
				"    xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"\n" +
				"    xmlns:nif=\"http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#\" > \n" +
				"  <rdf:Description rdf:about=\"http://dkt.dfki.de/examples/#char=36,59\">\n" +
				"    <nif:endIndex rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">59</nif:endIndex>\n" +
				"    <nif:beginIndex rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">36</nif:beginIndex>\n" +
				"    <rdf:type rdf:resource=\"http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#RFC5147String\"/>\n" +
				"    <rdf:type rdf:resource=\"http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#String\"/>\n" +
				"  </rdf:Description>\n" +
				"  <rdf:Description rdf:about=\"http://dkt.dfki.de/examples/#char=0,35\">\n" +
				"    <nif:endIndex rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">35</nif:endIndex>\n" +
				"    <nif:beginIndex rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">0</nif:beginIndex>\n" +
				"    <rdf:type rdf:resource=\"http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#RFC5147String\"/>\n" +
				"    <rdf:type rdf:resource=\"http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#String\"/>\n" +
				"  </rdf:Description>\n" +
				"  <rdf:Description rdf:about=\"http://dkt.dfki.de/examples/#char=0,60\">\n" +
				"    <nif:endIndex rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">60</nif:endIndex>\n" +
				"    <nif:beginIndex rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">0</nif:beginIndex>\n" +
				"    <nif:isString rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">Sample paragraph. Another sentence.\n" +
				"\n" +
				"And another paragraph.\n" +
				"</nif:isString>\n" +
				"    <rdf:type rdf:resource=\"http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#RFC5147String\"/>\n" +
				"    <rdf:type rdf:resource=\"http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#String\"/>\n" +
				"    <rdf:type rdf:resource=\"http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#Context\"/>\n" +
				"  </rdf:Description>\n" +
				"</rdf:RDF>\n";
		
		//System.out.println("DEBUGGING expected:\n" + expectedNIF + "|||");
		
		Assert.assertEquals(expectedNIF, response2.getBody());
		
		assertTrue(response2.getStatus() == 200);
		assertTrue(response2.getBody().length() > 0);
		
		//System.out.println("BODY2: "+response2.getBody());
		System.out.println("STATUS2:" + response2.getStatus());
	}
}
