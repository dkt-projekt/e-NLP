package de.dkt.eservices.enlp;


import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

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
import com.hp.hpl.jena.util.FileUtils;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequestWithBody;

import de.dkt.common.filemanagement.FileFactory;
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

public class ENLPPerformanceTest {

	static TestHelper testHelper;
	ValidationHelper validationHelper;
	ApplicationContext context;

	static HashMap<String,List<String>> hmDocumentsEN = new HashMap<String, List<String>>();
	static HashMap<String,List<String>> hmAnalysedDocumentsEN = new HashMap<String, List<String>>();
	static 	HashMap<String,String> hmNifCollectionsEN =  new HashMap<String,String>();
	static 	HashMap<String,List<String>> hmDocumentsDE = new HashMap<String, List<String>>();
	static 	HashMap<String,List<String>> hmAnalysedDocumentsDE = new HashMap<String, List<String>>();
	static HashMap<String,String> hmNifCollectionsDE = new HashMap<String,String>();

	public static BufferedWriter bw;
	
	@Before
	public void setup() {
		//ApplicationContext context = IntegrationTestSetup.getContext(TestConstants.pathToPackage);
		context = IntegrationTestSetup.getContext(TestConstants.pathToPackage);
		testHelper = context.getBean(TestHelper.class);
		validationHelper = context.getBean(ValidationHelper.class);
 
	}
	
	private static HttpRequestWithBody analyzeOpennlpRequest() {
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
	
	private HttpRequestWithBody entitySuggestRequest() {
		String url = testHelper.getAPIBaseUrl() + "/e-nlp/suggestEntityCandidates";
		return Unirest.post(url);
	}
	
	private HttpRequestWithBody tagRequest() {
		String url = testHelper.getAPIBaseUrl() + "/e-nlp/partOfSpeechTagging";
		//Unirest.setTimeouts(10000, 10000000);
		return Unirest.post(url);
	}
		
	@Test
	public void basicPlainTextInTurtleOut() throws UnirestException, IOException, Exception {
		
		System.out.println("SLEEEPING.........");
		Thread.sleep(5000);
		String baseUrl = "https://dfki-3059.dfki.de/api/";
		String fileOutput = "logs/loggingSmall2_Init.txt";
		String collectionsFolder = "/Users/jumo04/Documents/DFKI/DataCollections/PerformanceTestSmall2/";
		
//		File f = FileFactory.generateOrCreateFileInstance(fileOutput);
		File f = new File(fileOutput);
		System.out.println(f.getAbsolutePath());
		if(!f.exists()){
			f.createNewFile();
		}
		bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), "utf-8"));
		Unirest.setTimeouts(100000000, 100000000);
		Date d_inter_initial = new Date();
		MemoryUsage m_inter_initial = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();

		/**
		 * The information of the collections to be used for the tests must be charged in memory before 
		 *   so that it does not affect the memory or time measurements of the service executions 
		 */		
		File folder = new File(collectionsFolder);
		File[] langFolders = folder.listFiles();
		for (File file : langFolders) {
			if(file.getName().startsWith(".")){
				continue;
			}
			MemoryUsage m_inter_final = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
			System.out.println(m_inter_final.toString());
//			System.out.println(file);
			File[] colsFolder = file.listFiles();
			for (File file1 : colsFolder) {
				if(file1.getName().startsWith(".")){
					continue;
				}

				List<String> docs = new LinkedList<String>();
				List<String> adocs = new LinkedList<String>();
				String nifCol = "";
				File[] typesFolder = file1.listFiles();
				int counter = 0;
				for (File file2 : typesFolder) {
					if(file2.getName().startsWith(".")){
						continue;
					}
					File[] dataFiles = file2.listFiles();
					for (File file3 : dataFiles) {
						if(file3.getName().startsWith(".")){
							continue;
						}
						MemoryUsage m_inter_final2 = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
						System.out.println("DOC ("+counter+"): " + m_inter_final2.toString());
						counter++;
						
//						BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file3)));
//						String content = "";
//						String line= br.readLine();
//						while(line!=null){
//							content += line;
//							line=br.readLine();
//						}
//						br.close();
						String content = FileUtils.readWholeFileAsUTF8(file3.getAbsolutePath());
						if(file2.getName().equalsIgnoreCase("raw")){
							docs.add(content);
						}
						else if(file2.getName().equalsIgnoreCase("analized")){
							adocs.add(content);
						}
						else{
							nifCol = content;
						}
					}
				}
//				int index = 0;
//				if(file1.getName().equalsIgnoreCase("Collection1")){
//					index=0;
//				}
//				else if(file1.getName().equalsIgnoreCase("Collection2")){
//					index=1;
//				}
//				else if(file1.getName().equalsIgnoreCase("Collection3")){
//					index=2;
//				}
//				else if(file1.getName().equalsIgnoreCase("Collection4")){
//					index=3;
//				}
//				else{
//					index = 4;
//				}
				if(file.getName().equalsIgnoreCase("en")){
					System.out.println(file1.getName());
					hmDocumentsEN.put(file1.getName(), docs);
					hmAnalysedDocumentsEN.put(file1.getName(), adocs);
					hmNifCollectionsEN.put(file1.getName(), nifCol);
				}
				else{
					hmDocumentsDE.put(file1.getName(), docs);
					hmAnalysedDocumentsDE.put(file1.getName(), adocs);
					hmNifCollectionsDE.put(file1.getName(), nifCol);
				}
			}
		}
		
//		System.out.println("ENGLISH DOCUMENTS: " + hmDocumentsEN.get("collection1").size() + "  " + hmDocumentsEN.get("collection2").size() + "  ");
//		System.out.println("GERMAN DOCUMENTS: " + hmDocumentsDE.get("collection1").size() + "  " + hmDocumentsDE.get("collection2").size() );

//		System.out.println("ENGLISH ANALYSED DOCUMENTS: " + analysedDocumentsEN.get(0).size() + "  " + analysedDocumentsEN.get(1).size() );
//		System.out.println("GERMAN ANALYSED DOCUMENTS: " + analysedDocumentsDE.get(0).size() + "  " + analysedDocumentsDE.get(1).size() );
//		System.out.println("ENGLISH DOCUMENTS: " + documentsEN.get(0).size() + "  " + documentsEN.get(1).size() + "  " + documentsEN.get(2).size() + "  " + documentsEN.get(3).size() + "  ");
//		System.out.println("GERMAN DOCUMENTS: " + documentsDE.get(0).size() + "  " + documentsDE.get(1).size() + "  " + documentsDE.get(2).size() + "  " + documentsDE.get(3).size() + "  ");
//
//		System.out.println("ENGLISH ANALYSED DOCUMENTS: " + analysedDocumentsEN.get(0).size() + "  " + analysedDocumentsEN.get(1).size() + "  " + analysedDocumentsEN.get(2).size() + "  " + analysedDocumentsEN.get(3).size() + "  ");
//		System.out.println("GERMAN ANALYSED DOCUMENTS: " + analysedDocumentsDE.get(0).size() + "  " + analysedDocumentsDE.get(1).size() + "  " + analysedDocumentsDE.get(2).size() + "  " + analysedDocumentsDE.get(3).size() + "  ");

//		System.out.println("ENGLISH COLLECTIONS: " + nifCollectionsEN.size());
//		System.out.println("GERMAN COLLECTIONS: " + nifCollectionsDE.size());

		System.gc();
		
		Date d_inter_final = new Date();
		MemoryUsage m_inter_final = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
//		bw.write(" Document Loading:: time: ["+(d_inter_final.getTime() - d_inter_initial.getTime()) + " miliseconds] ");
//		bw.write("InitMem: ["+(m_inter_final.getInit() - m_inter_initial.getInit()) + "] ");
//		bw.write("MaxMem: ["+(m_inter_final.getMax() - m_inter_initial.getMax()) + "] ");
//		bw.write("UsedMem: ["+(m_inter_final.getUsed() - m_inter_initial.getUsed()) + "]\n");
//		bw.flush();
		printUsageData(bw, "Document Loading", d_inter_initial, d_inter_final, m_inter_initial, m_inter_final);

		System.out.println("Document loading finished.");
		
		Date d_initial = new Date();
		MemoryUsage m_initial = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();

//		collectionTest(bw, baseUrl, "COL1", "collection1");
//		collectionTest(bw, baseUrl, "COL2", "collection2");
//		collectionTest(bw, baseUrl, "COL3", "collection3");
		collectionTest(bw, baseUrl, "COL4", "collection4");
////		collectionTest(bw, baseUrl, "COL5", 4);

		System.gc();
		Date d_final = new Date();
		MemoryUsage m_final = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();

		printUsageData(bw, "Total Test", d_initial, d_final, m_initial, m_final);
		bw.close();		
	}

	public static void collectionTest(BufferedWriter bw, String baseUrl,String collection,String collectionName) throws Exception {
		System.gc();
		Date d_inter_initial = new Date();
		MemoryUsage m_inter_initial = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
		System.out.print("Starting test...");
		requestNLP(bw, "NER_LOC_EN_"+collection, hmDocumentsEN.get(collectionName), baseUrl+"e-nlp/namedEntityRecognition", "ner", "ner-wikinerEn_LOC", "en", "text", "turtle", "spot");
		System.out.print("...");
//		requestNLP(bw, "NER_LOC_DE_"+collection, hmDocumentsDE.get(collectionName), baseUrl+"e-nlp/namedEntityRecognition", "ner", "ner-wikinerEn_LOC", "de", "text", "turtle", "spot");
//		System.out.print("...");
		System.gc();
		Date d_inter_final = new Date();
		MemoryUsage m_inter_final = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
		printUsageData(bw, collection+" test", d_inter_initial, d_inter_final, m_inter_initial, m_inter_final);
	}
	
	public static void requestNLP(BufferedWriter bw, String name, List<String> documents, String url, String analysis, String models, String language, String informat, String outformat, String mode) throws Exception {
		System.gc();
		Date d_initial = new Date();
		MemoryUsage m_initial = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
		int cnt = 0;
		for (String string : documents) {
			Date d_ind_initial = new Date();
			MemoryUsage m_ind_initial = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
			
			System.out.println("Processing document..."+cnt);
			
			HttpResponse<String> response = analyzeOpennlpRequest()
					.queryString("analysis", analysis)
					.queryString("language", language)
					.queryString("models", models)
					.queryString("informat", informat)
					.queryString("outformat", outformat)
					.queryString("mode", mode)
					.body(string)
					.asString();
			if(response.getStatus()!=200){
				System.out.println("BIG ERROR IN ");
				bw.write("Error at executing test: "+name+"\n");
				bw.flush();
			}
			else{
				System.out.println(response.getBody());
				System.gc();
				Date d_ind_final = new Date();
				MemoryUsage m_ind_final = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
				printUsageData(bw, "Ind"+cnt, d_ind_initial, d_ind_final, m_ind_initial, m_ind_final);
			}
			cnt++;
		}
		System.gc();
		Date d_final = new Date();
		MemoryUsage m_final = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
		printUsageData(bw, "Test "+name, d_initial, d_final, m_initial, m_final);
	}

	public static void printUsageData(BufferedWriter bw, String name, Date d_initial, Date d_final, MemoryUsage m_initial, MemoryUsage m_final) throws Exception {
		bw.write(name+"\t"+d_initial.getTime()+"\t"+d_final.getTime()+"\t"+(d_final.getTime() - d_initial.getTime())+"\t");
		bw.write(m_initial.getInit()+"\t"+m_final.getInit()+"\t"+(m_final.getInit() - m_initial.getInit()) + "\t");
		bw.write(m_initial.getMax()+"\t"+m_final.getMax()+"\t"+(m_final.getMax() - m_initial.getMax()) + "\t");
		bw.write(m_initial.getUsed()+"\t"+m_final.getUsed()+"\t"+(m_final.getUsed() - m_initial.getUsed()) + "\n");
		bw.flush();
	}
	
}
