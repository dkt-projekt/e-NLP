package de.dkt.eservices.eopennlp.modules;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import de.dkt.common.filemanagement.FileFactory;

import de.dkt.common.niftools.DBO;
import de.dkt.common.niftools.GEO;
import de.dkt.common.niftools.NIF;
import de.dkt.common.niftools.NIFReader;
import de.dkt.common.niftools.NIFWriter;
import de.dkt.common.niftools.TIME;
import de.dkt.common.niftools.RDFS;
//import de.dkt.eservices.enlp.ENLPPerformanceTest;
//import de.dkt.eservices.eopennlp.TestConstants;
import de.dkt.eservices.erattlesnakenlp.modules.Sparqler;
import eu.freme.common.conversion.rdf.RDFConstants.RDFSerialization;
import eu.freme.common.exception.BadRequestException;
import eu.freme.common.exception.ExternalServiceFailedException;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.NameSample;
import opennlp.tools.namefind.NameSampleDataStream;
import opennlp.tools.namefind.TokenNameFinderFactory;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.Span;
import opennlp.tools.util.TrainingParameters;

//import eu.freme.eservices.elink.api.DataEnricher;
//import eu.freme.common.persistence.model.Template;


/**
 * @author Julian Moreno Schneider julian.moreno_schneider@dfki.de, Peter Bourgonje peter.bourgonje@dfki.de
 *
 */
public class NameFinder {

	//public static String modelsDirectory = File.separator + "trainedModels" + File.separator + "ner" + File.separator;
	public String modelsDirectory = "trainedModels" + File.separator + "ner" + File.separator;
	//	public static String modelsDirectory = "trainedModels" + File.separator + "ner2" + File.separator;
	Logger logger = Logger.getLogger(NameFinder.class);

	HashMap<String, Object> nameFinderPreLoadedModels = new HashMap<String, Object>();


	public void initializeModels() {
		try {
			File df = FileFactory.generateOrCreateDirectoryInstance(modelsDirectory);
			for (File f : df.listFiles()) {
				Date start = new Date();
				//					System.out.println("DATA: "+ManagementFactory.getMemoryMXBean().getHeapMemoryUsage());
				InputStream tnfNERModel = new FileInputStream(f);
				//					System.out.println("STREAM: "+ManagementFactory.getMemoryMXBean().getHeapMemoryUsage());
				//					System.out.println(((new Date()).getTime()-start.getTime()) / 1000 + " seconds");
				TokenNameFinderModel tnfModel = new TokenNameFinderModel(tnfNERModel);
				//					System.out.println("MODEL: "+ManagementFactory.getMemoryMXBean().getHeapMemoryUsage());
				//					System.out.println(((new Date()).getTime()-start.getTime()) / 1000 + " seconds");
				NameFinderME nameFinder = new NameFinderME(tnfModel);
				//					System.out.println("FINDER: "+ManagementFactory.getMemoryMXBean().getHeapMemoryUsage());
				//					System.out.println(((new Date()).getTime()-start.getTime()) / 1000 + " seconds");
				nameFinderPreLoadedModels.put(f.getName(), nameFinder);
				System.out.println("DEBUG MEM file name: "+f.getName());
				Date end = new Date();
				long seconds = (end.getTime()-start.getTime()) / 1000;
				logger.info("Initializing " + f.getName() + " took " + seconds + " seconds.");
				//					System.out.println("Initializing " + f.getName() + " took " + seconds + " seconds.");
				//					System.out.println(ManagementFactory.getMemoryMXBean().getHeapMemoryUsage());
				//break;
				tnfNERModel.close();
			}
		} catch (IOException e) {
			logger.error("Failed to initialize models in modelsDirectory:" + modelsDirectory);
		}
	}


	public String[] readLines(String filename) throws IOException {
		FileReader fileReader = new FileReader(filename);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		List<String> lines = new ArrayList<String>();
		String line = null;
		while ((line = bufferedReader.readLine()) != null) {
			lines.add(line);
		}
		bufferedReader.close();
		return lines.toArray(new String[lines.size()]);
	}

	public Model linkEntitiesNIF(Model nifModel, String language){

		// first loop through to make a list of unique entities, so the slow part (DBPedia lookup) has to be done only once for each entity. If at a later point we use a more sophisticated way of doing disambiguation (e.g. depending on context), this will not be applicable anymore. But for not it should significantly increase response times.
		List<String[]> nifEntities = NIFReader.extractEntityIndices(nifModel);

		if (nifEntities == null){
			return nifModel;
		}
		else{
			ArrayList<String> uniqueEntities = new ArrayList<String>();
			for (String[] e : nifEntities){
				if ((!uniqueEntities.contains(e[1]))){
					if (!(e[2].equals(TIME.temporalEntity.toString()))){
						uniqueEntities.add(e[1]);	
					}
				}
			}
			String sparqlService = null;
			String defaultGraph = null;
			if (language.equalsIgnoreCase("en")){
				sparqlService = "http://dbpedia.org/sparql";
				defaultGraph = "http://dbpedia.org";
			}
			else if (language.equalsIgnoreCase("de")){
				sparqlService = "http://de.dbpedia.org/sparql";
				defaultGraph = "http://de.dbpedia.org";
			}
			else{
				//add more languages here
			}

			String documentURI = NIFReader.extractDocumentURI(nifModel);

			HashMap<String, String> entity2DBPediaURI = new HashMap<String, String>();
			for (String ent : uniqueEntities){
				String entURI = Sparqler.getDBPediaURI(ent, language, sparqlService, defaultGraph);
				if(!(entURI == null)){
					entity2DBPediaURI.put(ent, entURI);
				}
			}

			Sparqler sparqler = new Sparqler();
			for (String[] e : nifEntities){
				String anchor = e[1];
				if (entity2DBPediaURI.containsKey(anchor)){
					String entURI = entity2DBPediaURI.get(anchor);
					int nameStart = Integer.parseInt(e[3]);
					int nameEnd = Integer.parseInt(e[4]);
					NIFWriter.addEntityURI(nifModel, nameStart, nameEnd, documentURI, entURI);

					//if (e[2].equals(DFKINIF.location.toString())){
					if (e[2].equals(DBO.location.toString())){
						NIFWriter.addPrefixToModel(nifModel, "geo", GEO.uri);
						// NOTE: the name in our outModel suggests that this is using the w3.org lat and long ones, but it's not. 
						sparqler.queryDBPedia(nifModel, documentURI, nameStart, nameEnd, "http://www.georss.org/georss/point", GEO.latitude, sparqlService);
						sparqler.queryDBPedia(nifModel, documentURI, nameStart, nameEnd, "http://www.georss.org/georss/point", GEO.longitude, sparqlService);
					}
					//else if (e[2].equals(DFKINIF.person.toString())){
					else if (e[2].equals(DBO.person.toString())){
						NIFWriter.addPrefixToModel(nifModel, "dbo", DBO.uri);
						sparqler.queryDBPedia(nifModel, documentURI, nameStart, nameEnd, "http://dbpedia.org/ontology/birthDate", DBO.birthDate, sparqlService);
						sparqler.queryDBPedia(nifModel, documentURI, nameStart, nameEnd, "http://dbpedia.org/ontology/deathDate", DBO.deathDate, sparqlService);
					}
					//else if (e[2].equals(DFKINIF.organization.toString())){
					else if (e[2].equals(DBO.organisation.toString())){
						sparqler.queryDBPedia(nifModel, documentURI, nameStart, nameEnd, "http://dbpedia.org/ontology/background", NIF.orgType, sparqlService);
					}

				}
			}

			// add doc level stats
			if (sparqler.hasCoordinates()){
				sparqler.addGeoStats(nifModel, NIFReader.extractIsString(nifModel), documentURI);
			}


			return nifModel;
		}
	}

	public Model spotEntitiesNIF(Model nifModel, ArrayList<String> nerModels, String sentModel, String language) throws ExternalServiceFailedException, IOException, Exception  {
		//		String docURI = NIFReader.extractDocumentURI(nifModel);
		
		Date d_inter_initial1 = new Date();
		MemoryUsage m_inter_initial1 = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();

		HashMap<ArrayList, HashMap<String, Double>> entityMap = new HashMap<>();
		String content = NIFReader.extractIsString(nifModel);
		Span[] sentenceSpans = SentenceDetector.detectSentenceSpans(content, sentModel);

//		System.gc();
		Date d_inter_final1 = new Date();
		MemoryUsage m_inter_final1 = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
//		ENLPPerformanceTest.printUsageData(ENLPPerformanceTest.bw, "Sentence Detector", d_inter_initial1, d_inter_final1, m_inter_initial1, m_inter_final1);

		Date d_inter_initial2 = new Date();
		MemoryUsage m_inter_initial2 = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();

		
		for (String nerModel : nerModels){
			entityMap = detectEntitiesWithModel(entityMap, content, sentenceSpans, nerModel);
		}

		
//		System.gc();
		Date d_inter_final2 = new Date();
		MemoryUsage m_inter_final2 = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
//		ENLPPerformanceTest.printUsageData(ENLPPerformanceTest.bw, "Detect Entities With models", d_inter_initial2, d_inter_final2, m_inter_initial2, m_inter_final2);
//		System.out.println("SPOT TIME: "+(d_inter_final2.getTime()-d_inter_initial2.getTime()));
		Date d_inter_initial3 = new Date();
		MemoryUsage m_inter_initial3 = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();


		// filter hashmap for duplicates and keep the one with highest probability
		for (Entry<ArrayList, HashMap<String, Double>> outerMap : entityMap.entrySet()) {
			ArrayList<Integer> spanList = outerMap.getKey();
			HashMap<String, Double> spanMap = outerMap.getValue();
			Double highestProb = 0.0;
			String finalType = null;
			for (HashMap.Entry<String, Double> innerMap : spanMap.entrySet()) {
				String type = innerMap.getKey();
				Double prob = innerMap.getValue();
				if (prob > highestProb){
					finalType = type;
					highestProb = prob;
				}
			}
			// finalType is now the type with the highest probability, so get DBpedia URI and add to the nifModel
			int nameStart = spanList.get(0);
			int nameEnd = spanList.get(1);
			String foundName = content.substring(nameStart, nameEnd);
			String nerType = null;
			if (finalType.equals("LOC")){
				//nerType = DFKINIF.location.toString();
				nerType = DBO.location.toString();
			}
			else if (finalType.equals("PER")){
				//nerType = DFKINIF.person.toString();
				nerType = DBO.person.toString();
			}
			else if (finalType.equals("ORG")){
				//nerType = DFKINIF.organization.toString();
				nerType = DBO.organisation.toString();
			}

			NIFWriter.addAnnotationEntitiesWithoutURI(nifModel, nameStart, nameEnd, foundName, nerType);
		}		    

//		System.gc();
		Date d_inter_final3 = new Date();
		MemoryUsage m_inter_final3 = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
//		System.out.println("SPOT2 TIME: "+(d_inter_final3.getTime()-d_inter_initial3.getTime()));
//		ENLPPerformanceTest.printUsageData(ENLPPerformanceTest.bw, "Adding annotation to model", d_inter_initial3, d_inter_final3, m_inter_initial3, m_inter_final3);

		return nifModel;

	}



	public HashMap<ArrayList, HashMap<String, Double>> detectEntitiesWithModel(HashMap<ArrayList, HashMap<String, Double>> entityMap, String text, Span[] sentenceSpans, String nerModel){
		NameFinderME nameFinder = null;
		if (nameFinderPreLoadedModels.containsKey(nerModel)){
			nameFinder = (NameFinderME) nameFinderPreLoadedModels.get(nerModel);
		}
		else{
			try {
				System.out.println("Loading model: "+nerModel);
				ClassPathResource cprNERModel = new ClassPathResource(modelsDirectory + nerModel);
				InputStream tnfNERModel;
				tnfNERModel = new FileInputStream(cprNERModel.getFile());
				TokenNameFinderModel tnfModel = new TokenNameFinderModel(tnfNERModel);
				nameFinder = new NameFinderME(tnfModel);
				nameFinderPreLoadedModels.put(cprNERModel.getFile().getName(), nameFinder);
				tnfNERModel.close();
			} catch (Exception e ) {
				logger.error("Could not find model:" + nerModel + " in modelsDirectory:" + modelsDirectory);
				throw new BadRequestException("Model " + nerModel.substring(0, nerModel.lastIndexOf('.')) + " not found in pre-initialized map or modelsDirectory.");
			}
		}

		for (Span sentenceSpan : sentenceSpans) {
			String sentence = text.substring(sentenceSpan.getStart(), sentenceSpan.getEnd());
			Span tokenSpans[] = Tokenizer.simpleTokenizeIndices(sentence);
			String tokens[] = Span.spansToStrings(tokenSpans, sentence);
			Span nameSpans[];

			/*
			 * This is synchronized because NameFinderME.find() is not thread-safe, but it might
			 * be called concurrently on the same instance.
			 */
			synchronized (nameFinder) {
				nameSpans = nameFinder.find(tokens);
			}

			for (Span s : nameSpans) {
				int nameStartIndex = 0;
				int nameEndIndex = 0;
				for (int i = 0; i <= tokenSpans.length; i++) {
					if (i == s.getStart()) {
						nameStartIndex = tokenSpans[i].getStart() + sentenceSpan.getStart();
					} else if (i == s.getEnd()) {
						nameEndIndex = tokenSpans[i - 1].getEnd() + sentenceSpan.getStart();
					}
				}
				ArrayList<Integer> se = new ArrayList<Integer>();
				se.add(nameStartIndex);
				se.add(nameEndIndex);
				// if there was another enitity of this type found at this
				// token-span, this will not be null
				HashMap<String, Double> spanMap = entityMap.get(se);
				// otherwise:
				if (spanMap == null) {
					spanMap = new HashMap<String, Double>();
				}
				spanMap.put(s.getType(), s.getProb());
				// spanMap.put("LOC", 0.5); // hacking in entity of another
				// type for testing disambiguation
				entityMap.put(se, spanMap);
			}
		}
		return entityMap;
	}



	/**
	 * 
	 * 
	 * @param inputTrainData Stream of training data
	 * @param modelName Name to be assigned to the model
	 * @return true if the model has been successfully trained
	 */
	public String trainModel(String inputTrainData, String modelName, String language) throws BadRequestException, ExternalServiceFailedException {

		//TODO: find a way to validate the format of the training data and provide some intelligible error message when it's not right 
		
		//Charset charset;				
		ObjectStream<String> lineStream;
		ObjectStream<NameSample> sampleStream;
		File newModel = null;
		try{
		
			File f = FileFactory.generateOrCreateFileInstance("trainedModels" + File.separator + "ner" + "tempTrainFile"); // ugly, but all of a sudden this code complained about the cast to InputStreamFactory of the ByteArrayInputStream (see below), so writing to temp file...
			PrintWriter pw = new PrintWriter(f);
			pw.write(inputTrainData);
			pw.close();
			InputStreamFactory isf = new InputStreamFactory() {
	            public InputStream createInputStream() throws IOException {
	                return new FileInputStream(f.getAbsolutePath());
	            }
	        };

	        Charset charset = Charset.forName("UTF-8");
	        lineStream = new PlainTextByLineStream(isf, charset);
	        sampleStream = new NameSampleDataStream(lineStream);
			
			
			//charset = Charset.forName("UTF-8");
			//ClassPathResource cprOne = new ClassPathResource(inputTrainData);
			///ByteArrayInputStream bais = new ByteArrayInputStream(inputTrainData.getBytes());
			///lineStream = new PlainTextByLineStream((InputStreamFactory) bais,"utf-8");
			///lineStream = new PlainTextByLineStream((InputStreamFactory) bais,"utf-8");
			//lineStream = new PlainTextByLineStream((InputStreamFactory) new FileInputStream(f), "utf-8");
			//sampleStream = new NameSampleDataStream(lineStream);

			TokenNameFinderModel model;

			// create file for new model
			ClassPathResource cprDir = new ClassPathResource(modelsDirectory);
			File fModelsDirectory = cprDir.getFile();
			//newModel = new File(fModelsDirectory, language + "-" + modelName + ".bin");
			newModel = new File(fModelsDirectory, modelName + ".bin");
			newModel.createNewFile();

			try {
				TrainingParameters tp = new TrainingParameters();
				tp.put(TrainingParameters.CUTOFF_PARAM, "1"); // TODO: may want to have this as input argument (optional)
				tp.put(TrainingParameters.ITERATIONS_PARAM, "100"); // TODO: same here
				TokenNameFinderFactory tnff = new TokenNameFinderFactory();
				model = NameFinderME.train(language, modelName, sampleStream, tp, tnff);

			}
			finally {
				sampleStream.close();
				lineStream.close();
			}

			OutputStream modelOut = null;

			try {
				modelOut = new BufferedOutputStream(new FileOutputStream(newModel));
				model.serialize(modelOut);
			} finally {
				if (modelOut != null) 
					modelOut.close();      
			}
		}
		catch(FileNotFoundException e){
			throw new BadRequestException(e.getMessage());
		}
		catch(IOException e){
			throw new ExternalServiceFailedException(e.getMessage());
		}

		return FilenameUtils.removeExtension(newModel.getName());//newModel.getName();//.getPath();
	}


//	static String readFile(String path, Charset encoding) 
//			  throws IOException 
//			{
//			  byte[] encoded = Files.readAllBytes(Paths.get(path));
//			  return new String(encoded, encoding);
//			}
	
	public static String[] staticreadLines(String filename) throws IOException {
		FileReader fileReader = new FileReader(filename);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		List<String> lines = new ArrayList<String>();
		String line = null;
		while ((line = bufferedReader.readLine()) != null) {
			lines.add(line);
		}
		bufferedReader.close();
		return lines.toArray(new String[lines.size()]);
	}
	
	public static void main(String[] args) throws IOException {
		
//		Charset charset = Charset.forName("UTF-8");
//		ObjectStream<String> lineStream = new PlainTextByLineStream(new FileInputStream("C:\\Users\\pebo01\\Desktop\\dbpediaNamedEventData\\opennlp.train"), charset);
//		ObjectStream<NameSample> sampleStream = new NameSampleDataStream(lineStream);
//
//		String modelName = "namedEventModel";
//		TokenNameFinderModel model;
//		OutputStream modelOut = null;
//		File newModel = null;
//		newModel = new File("C:\\Users\\pebo01\\Desktop\\dbpediaNamedEventData\\namedEventModel.bin");
//		newModel.createNewFile();
//		try {
//			
//			TrainingParameters tp = new TrainingParameters();
//			tp.put(TrainingParameters.CUTOFF_PARAM, "1");
//			tp.put(TrainingParameters.ITERATIONS_PARAM, "10");//100
//			TokenNameFinderFactory tnff = new TokenNameFinderFactory();
//			model = NameFinderME.train("en", modelName, sampleStream, tp, tnff);
//			
//			modelOut = new BufferedOutputStream(new FileOutputStream(newModel));
//			model.serialize(modelOut);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} finally {
//			sampleStream.close();
//			if (modelOut != null)
//				modelOut.close();
//		}

		
		///////////////////spotting section
		NameFinderME nameFinder = null;
		InputStream tnfNERModel;
		File modelbin = FileFactory.generateOrCreateFileInstance("C:\\Users\\pebo01\\Desktop\\dbpediaNamedEventData\\namedEventModel.bin");
		tnfNERModel = new FileInputStream(modelbin);
		TokenNameFinderModel tnfModel = new TokenNameFinderModel(tnfNERModel);
		nameFinder = new NameFinderME(tnfModel);
		tnfNERModel.close();
		
		
		String folder = "C:\\Users\\pebo01\\Desktop\\dbpediaNamedEventData\\forsabine";
		PrintWriter debug = new PrintWriter("C:\\Users\\pebo01\\Desktop\\debug.txt");
		File df = FileFactory.generateOrCreateDirectoryInstance(folder);
		for (File f : df.listFiles()) {
			String[] lines = staticreadLines(f.getAbsolutePath());
			for (String l : lines){
				String[] sents = SentenceDetector.detectSentences(l, "en-sent.bin");
				for (String sent : sents){
					//System.out.println("Sentence:" + sent);
					debug.println(sent);
					Span tokenSpans[] = Tokenizer.simpleTokenizeIndices(sent);
					String tokens[] = Span.spansToStrings(tokenSpans, sent);
					Span[] nameSpans;
					synchronized (nameFinder) {
						nameSpans = nameFinder.find(tokens);
					}
					//Span[] nameSpans = nameFinder.find(tokens);
					for (Span s : nameSpans){
						int nameStartIndex = 0;
						int nameEndIndex = 0;
						for (int i = 0; i <= tokenSpans.length; i++) {
							if (i == s.getStart()) {
								nameStartIndex = tokenSpans[i].getStart();
							} else if (i == s.getEnd()) {
								nameEndIndex = tokenSpans[i - 1].getEnd();
							}
						}
						debug.println("\tFound entity: " + sent.substring(nameStartIndex, nameEndIndex));
						//System.out.println("Found:" + sent.substring(nameStartIndex, nameEndIndex));
					}
				}
			}
			System.out.println("INFO: Processed file " + f.getName());
		}
		System.out.println("INFO: Done.");
		
				
	}
	
	


}
