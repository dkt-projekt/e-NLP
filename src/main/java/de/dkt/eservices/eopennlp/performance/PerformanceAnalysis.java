package de.dkt.eservices.eopennlp.performance;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.hp.hpl.jena.util.FileUtils;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

public class PerformanceAnalysis {

	static HashMap<String,List<String>> hmDocumentsEN = new HashMap<String, List<String>>();
	static HashMap<String,List<String>> hmAnalysedDocumentsEN = new HashMap<String, List<String>>();
	static 	HashMap<String,String> hmNifCollectionsEN =  new HashMap<String,String>();
	static 	HashMap<String,List<String>> hmDocumentsDE = new HashMap<String, List<String>>();
	static 	HashMap<String,List<String>> hmAnalysedDocumentsDE = new HashMap<String, List<String>>();
	static HashMap<String,String> hmNifCollectionsDE = new HashMap<String,String>();

	public static void perform(String baseUrl, String fileOutput, String collectionsFolder) throws Exception {

//		String baseUrl = "https://dfki-3059.dfki.de/api/";
//		String baseUrl = args[0];
		
//		String fileOutput = args[1];
//		String fileOutput = "testOutput/logging.txt";
		
//		String collectionsFolder = args[2];
//		String collectionsFolder = "/Users/jumo04/Documents/DFKI/DataCollections/PerformanceTest/";
		
		File f = new File(fileOutput);
//		System.out.println(f.getAbsolutePath());
		if(!f.exists()){
			f.createNewFile();
		}
		
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileOutput), "utf-8"));

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
				for (File file2 : typesFolder) {
					if(file2.getName().startsWith(".")){
						continue;
					}
					File[] dataFiles = file2.listFiles();
					for (File file3 : dataFiles) {
						if(file3.getName().startsWith(".")){
							continue;
						}
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
		
		System.out.println("ENGLISH DOCUMENTS: " + hmDocumentsEN.get("collection1").size() + "  " + hmDocumentsEN.get("collection2").size() + "  ");
		System.out.println("GERMAN DOCUMENTS: " + hmDocumentsDE.get("collection1").size() + "  " + hmDocumentsDE.get("collection2").size() );

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
		
		Date d_initial = new Date();
		MemoryUsage m_initial = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();

		collectionTest(bw, baseUrl, "COL1", "collection1");
		collectionTest(bw, baseUrl, "COL2", "collection2");
		collectionTest(bw, baseUrl, "COL3", "collection3");
		collectionTest(bw, baseUrl, "COL4", "collection4");
////		collectionTest(bw, baseUrl, "COL5", 4);

		System.gc();
		Date d_final = new Date();
		MemoryUsage m_final = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();

		printUsageData(bw, "Total Test", d_initial, d_final, m_initial, m_final);
		bw.close();
	}

	public static void printUsageData(BufferedWriter bw, String name, Date d_initial, Date d_final, MemoryUsage m_initial, MemoryUsage m_final) throws Exception {
		bw.write(name+"\t"+d_initial.getTime()+"\t"+d_final.getTime()+"\t"+(d_final.getTime() - d_initial.getTime())+"\t");
		bw.write(m_initial.getInit()+"\t"+m_final.getInit()+"\t"+(m_final.getInit() - m_initial.getInit()) + "\t");
		bw.write(m_initial.getMax()+"\t"+m_final.getMax()+"\t"+(m_final.getMax() - m_initial.getMax()) + "\t");
		bw.write(m_initial.getUsed()+"\t"+m_final.getUsed()+"\t"+(m_final.getUsed() - m_initial.getUsed()) + "\n");
		bw.flush();
	}
	
	public static void collectionTest(BufferedWriter bw, String baseUrl,String collection,String collectionName) throws Exception {

		System.gc();

		Date d_inter_initial = new Date();
		MemoryUsage m_inter_initial = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();

		System.out.print("Starting test...");
		requestNLP(bw, "NER_LOC_EN_"+collection, hmDocumentsEN.get(collectionName), baseUrl+"e-nlp/namedEntityRecognition", "ner", "ner-wikinerEn_LOC", "en", "text", "turtle", "spot");
		System.out.print("...");
		requestNLP(bw, "NER_PER_EN_"+collection, hmDocumentsEN.get(collectionName), baseUrl+"e-nlp/namedEntityRecognition", "ner", "ner-wikinerEn_PER", "en", "text", "turtle", "spot");
		System.out.print("...");
		requestNLP(bw, "NER_ORG_EN_"+collection, hmDocumentsEN.get(collectionName), baseUrl+"e-nlp/namedEntityRecognition", "ner", "ner-wikinerEn_ORG", "en", "text", "turtle", "spot");
		System.out.print("...");
		requestNLP(bw, "NER_LOC_DE_"+collection, hmDocumentsDE.get(collectionName), baseUrl+"e-nlp/namedEntityRecognition", "ner", "ner-wikinerEn_LOC", "de", "text", "turtle", "spot");
		System.out.print("...");
		requestNLP(bw, "NER_PER_DE_"+collection, hmDocumentsDE.get(collectionName), baseUrl+"e-nlp/namedEntityRecognition", "ner", "ner-wikinerEn_PER", "de", "text", "turtle", "spot");
		System.out.print("...");
		requestNLP(bw, "NER_ORG_DE_"+collection, hmDocumentsDE.get(collectionName), baseUrl+"e-nlp/namedEntityRecognition", "ner", "ner-wikinerEn_ORG", "de", "text", "turtle", "spot");
		System.out.print("...");
		requestNLPDICT(bw, "NER_DIC_EN_"+collection, hmDocumentsEN.get(collectionName), baseUrl+"e-nlp/namedEntityRecognition", "dict", "mendelsohnDictionary_PER", "en", "text", "turtle");
		System.out.print("...");
		requestNLPDICT(bw, "NER_DIC_DE_"+collection, hmDocumentsDE.get(collectionName), baseUrl+"e-nlp/namedEntityRecognition", "dict", "mendelsohnDictionary_PER", "de", "text", "turtle");
		System.out.print("...");
				
		requestNLPTIME(bw, "TIMEX_EN_"+collection, hmDocumentsEN.get(collectionName), baseUrl+"e-nlp/namedEntityRecognition", "temp", "englishDates", "en", "text", "turtle");
		System.out.print("...");
		requestNLPTIME(bw, "TIMEX_DE_"+collection, hmDocumentsDE.get(collectionName), baseUrl+"e-nlp/namedEntityRecognition", "temp", "germanDates", "de", "text", "turtle");
		System.out.print("...");
//		requestCoref(bw, "COREF_EN_"+collection, analysedDocumentsEN.get(index), baseUrl+"e-nlp/CoreferenceResolution", "", "", "en", "turtle", "turtle", "");
//		System.out.print("...");
		requestSentiment(bw, "SENT_DIC_EN_"+collection, hmAnalysedDocumentsEN.get(collectionName), baseUrl+"e-sentimentanalysis", "dfki", "en", "turtle", "turtle");
		System.out.print("...");
//		requestSentiment(bw, "SENT_CN_EN_"+collection, analysedDocumentsEN.get(index), baseUrl+"e-sentimentanalysis", "corenlp", "en", "turtle", "turtle");
//		System.out.print("...");
//		requestClustering(bw, "CLU_EM_EN_"+collection, nifCollectionsEN.get(index), baseUrl+"e-clustering/clusterCollection", "em", "en", "turtle", "turtle", "wordappearance", "DATASET");
//		System.out.print("...");
//		requestClustering(bw, "CLU_KM_EN_"+collection, nifCollectionsEN.get(index), baseUrl+"e-clustering/clusterCollection", "kmeans", "en", "turtle", "turtle", "wordappearance", "DATASET");
//		System.out.print("...");
//		requestClustering(bw, "CLU_EM_DE_"+collection, nifCollectionsDE.get(index), baseUrl+"e-clustering/clusterCollection", "em", "de", "turtle", "turtle", "wordappearance", "DATASET");
//		System.out.print("...");
//		requestClustering(bw, "CLU_KM_DE_"+collection, nifCollectionsDE.get(index), baseUrl+"e-clustering/clusterCollection", "kmeans", "de", "turtle", "turtle", "wordappearance", "DATASET");
//		System.out.print("...");
		requestLucene(bw, "LUC_EN_"+collection, hmAnalysedDocumentsEN.get(collectionName), baseUrl+"e-lucene/indexes", "en", "turtle", "turtle");
		System.out.print("...");
		requestLucene(bw, "LUC_DE_"+collection, hmAnalysedDocumentsDE.get(collectionName), baseUrl+"e-lucene/indexes", "de", "turtle", "turtle");
		System.out.println("...Finished test: "+collection);
		
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
			
			HttpResponse<String> response = Unirest.post(url)
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

	public static void requestNLPDICT(BufferedWriter bw, String name, List<String> documents, String url, String analysis, String models, String language, String informat, String outformat) throws Exception {
		System.gc();
		Date d_initial = new Date();
		MemoryUsage m_initial = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
		int cnt = 0;
		for (String string : documents) {
			Date d_ind_initial = new Date();
			MemoryUsage m_ind_initial = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
			
			HttpResponse<String> response = Unirest.post(url)
					.queryString("analysis", analysis)
					.queryString("language", language)
					.queryString("models", models)
					.queryString("informat", informat)
					.queryString("outformat", outformat)
					.body(string)
					.asString();
			if(response.getStatus()!=200){
				System.out.println("BIG ERROR IN ");
				bw.write("Error at executing test: "+name+"\n");
				bw.flush();
			}
			else{
				System.out.println(response.getBody());
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

	public static void requestNLPTIME(BufferedWriter bw, String name, List<String> documents, String url, String analysis, String models, String language, String informat, String outformat) throws Exception {
		System.gc();
		Date d_initial = new Date();
		MemoryUsage m_initial = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
		int cnt = 0;
		for (String string : documents) {
			Date d_ind_initial = new Date();
			MemoryUsage m_ind_initial = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
			
			HttpResponse<String> response = Unirest.post(url)
					.queryString("analysis", analysis)
					.queryString("language", language)
					.queryString("models", models)
					.queryString("informat", informat)
					.queryString("outformat", outformat)
					.body(string)
					.asString();
			if(response.getStatus()!=200){
				System.out.println("BIG ERROR IN ");
				bw.write("Error at executing test: "+name+"\n");
				bw.flush();
			}
			else{
				System.out.println(response.getBody());
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

	public static void requestCoref(BufferedWriter bw, String name, List<String> documents, String url, String analysis, String models, String language, String informat, String outformat, String mode) throws Exception {
		System.gc();
		Date d_initial = new Date();
		MemoryUsage m_initial = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
		int cnt = 0;
		for (String string : documents) {
			Date d_ind_initial = new Date();
			MemoryUsage m_ind_initial = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
			
			HttpResponse<String> response = Unirest.post(url)
//					.queryString("input", string)
//					.queryString("analysis", analysis)
					.queryString("language", language)
//					.queryString("models", models)
					.queryString("informat", informat)
					.queryString("outformat", outformat)
//					.queryString("mode", mode)
					.body(string)
					.asString();
			if(response.getStatus()!=200){
				System.out.println("BIG ERROR IN ");
				bw.write("Error at executing test: "+name+"\n");
				bw.flush();
			}
			else{
				System.out.println(response.getBody());
				Date d_ind_final = new Date();
				MemoryUsage m_ind_final = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
				printUsageData(bw, "Ind"+cnt, d_ind_initial, d_ind_final, m_ind_initial, m_ind_final);
			}
			cnt++;
		}
		System.gc();
		Date d_final = new Date();
		MemoryUsage m_final = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
		printUsageData(bw, "Test COREF "+name, d_initial, d_final, m_initial, m_final);
	}

	public static void requestSentiment(BufferedWriter bw, String name, List<String> documents, String url, String engine, String language, String informat, String outformat) throws Exception {
		System.gc();
		Date d_initial = new Date();
		MemoryUsage m_initial = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
		int cnt = 0;
		for (String string : documents) {
			Date d_ind_initial = new Date();
			MemoryUsage m_ind_initial = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
			
			HttpResponse<String> response = Unirest.post(url)
					//.queryString("input", string)
					.queryString("language", language)
					.queryString("sentimentEngine", engine)
					.queryString("informat", informat)
					.queryString("outformat", outformat)
					.body(string)
					.asString();
			if(response.getStatus()!=200){
				System.out.println("BIG ERROR IN ");
				System.out.println(response.getStatusText());
				bw.write("Error at executing test: "+name+"\n");
				bw.flush();
			}
			else{
				System.out.println(response.getBody());
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
	
	public static void requestClustering(BufferedWriter bw, String name, String collection, String url, String algorithm, 
			String language, String informat, String outformat, String arffGeneratorType, String arffDataSetName) throws Exception {
		System.gc();
		Date d_initial = new Date();
		MemoryUsage m_initial = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();

		HttpResponse<String> response = Unirest.post(url)
				.queryString("algorithm", algorithm)
				.queryString("language", language)
				.queryString("informat", informat)
				.queryString("outformat", outformat)
				.queryString("arffGeneratorType", arffGeneratorType)
				//					.queryString("arffGeneratorType", "wordappearance")
				.queryString("arffDataSetName", arffDataSetName)
				.body(collection)
				.asString();
		if(response.getStatus()!=200){
			System.out.println("BIG ERROR IN ");
			bw.write("Error at executing test: "+name+"\n");
			bw.flush();
		}
		else{
			System.out.println(response.getBody());
			System.gc();
			Date d_final = new Date();
			MemoryUsage m_final = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
			printUsageData(bw, "Test "+name, d_initial, d_final, m_initial, m_final);
		}
	}

	public static void requestLucene(BufferedWriter bw, String name, List<String> documents, String url, 
			String language, String informat, String outformat) throws Exception {
		System.gc();
		Date d_initial = new Date();
		MemoryUsage m_initial = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
		
		Date d_cre_initial = new Date();
		MemoryUsage m_cre_initial = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
		
		HttpResponse<String> response = Unirest.post(url+"")
				.queryString("indexName", "indexPerformance")
				.queryString("language", "en")
				.queryString("fields", "all")
				.queryString("analyzers", "standard")
//				.queryString("overwrite", true)
				.asString();
		if(response.getStatus()!=200){
			System.out.println("BIG ERROR IN ");
			bw.write("Error at creating index test: "+name+"\n");
			bw.flush();
		}
		else{
			Date d_cre_final = new Date();
			MemoryUsage m_cre_final = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
			printUsageData(bw, "Test CREATION "+name, d_cre_initial, d_cre_final, m_cre_initial, m_cre_final);
		}
		
		int cnt = 0;
		for (String string : documents) {
			Date d_ind_initial = new Date();
			MemoryUsage m_ind_initial = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
			
			HttpResponse<String> response2 = Unirest.post(url+"/indexPerformance")
					.queryString("language", language)
					.queryString("informat", informat)
					.queryString("outformat", outformat)
					.body(string)
					.asString();

			if(response2.getStatus()!=200){
				System.out.println("BIG ERROR IN ");
				bw.write("Error at executing test: "+name+"\n");
				bw.flush();
			}
			else{
				System.out.println(response.getBody());
				Date d_ind_final = new Date();
				MemoryUsage m_ind_final = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
				printUsageData(bw, "Ind"+cnt, d_ind_initial, d_ind_final, m_ind_initial, m_ind_final);
			}
			cnt++;
		}
		
		Date d_del_initial = new Date();
		MemoryUsage m_del_initial = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
		HttpResponse<String> response3 = Unirest.delete(url+"/indexPerformance")
				.queryString("language", "en")
				.queryString("fields", "all")
				.queryString("analyzers", "standard")
				.asString();

		if(response3.getStatus()!=200){
			System.out.println("BIG ERROR IN ");
			bw.write("Error at deleting index test: "+name+"\n");
			bw.flush();
		}
		else{
			Date d_del_final = new Date();
			MemoryUsage m_del_final = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
			printUsageData(bw, "Test DELETION "+name, d_del_initial, d_del_final, m_del_initial, m_del_final);
		}
		System.gc();
		Date d_final = new Date();
		MemoryUsage m_final = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
		printUsageData(bw, "Test "+name, d_initial, d_final, m_initial, m_final);
	}
	
}
