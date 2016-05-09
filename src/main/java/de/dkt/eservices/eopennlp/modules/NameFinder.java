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
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.hibernate.engine.transaction.jta.platform.internal.SynchronizationRegistryBasedSynchronizationStrategy;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import com.hp.hpl.jena.rdf.model.Model;
import de.dkt.common.filemanagement.FileFactory;

import de.dkt.common.niftools.DBO;
import de.dkt.common.niftools.DKTNIF;
import de.dkt.common.niftools.GEO;
import de.dkt.common.niftools.ITSRDF;
import de.dkt.common.niftools.NIF;
import de.dkt.common.niftools.NIFReader;
import de.dkt.common.niftools.NIFWriter;
import de.dkt.common.niftools.TIME;
//import de.dkt.eservices.eopennlp.TestConstants;
import de.dkt.eservices.erattlesnakenlp.modules.Sparqler;
import eu.freme.bservices.testhelper.TestHelper;
import eu.freme.bservices.testhelper.api.IntegrationTestSetup;
import eu.freme.common.conversion.rdf.RDFConstants.RDFSerialization;
import eu.freme.common.exception.BadRequestException;
import eu.freme.common.exception.ExternalServiceFailedException;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.NameSample;
import opennlp.tools.namefind.NameSampleDataStream;
import opennlp.tools.namefind.TokenNameFinderFactory;
import opennlp.tools.namefind.TokenNameFinderModel;
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
	public static String modelsDirectory = "trainedModels" + File.separator + "ner" + File.separator;
	static Logger logger = Logger.getLogger(NameFinder.class);

	static HashMap<String, Object> nameFinderPreLoadedModels = new HashMap<String, Object>();
	
	
	public static void initializeModels() {
	

		try {
			File df = FileFactory.generateOrCreateDirectoryInstance(modelsDirectory);
			for (File f : df.listFiles()) {
				Date start = new Date();
				InputStream tnfNERModel = new FileInputStream(f);
				TokenNameFinderModel tnfModel = new TokenNameFinderModel(tnfNERModel);
				NameFinderME nameFinder = new NameFinderME(tnfModel);
				nameFinderPreLoadedModels.put(f.getName(), nameFinder);
				Date end = new Date();
				long seconds = (end.getTime()-start.getTime()) / 1000;
				logger.info("Initializing " + f.getName() + " took " + seconds + " seconds.");
			}
		} catch (IOException e) {
			logger.error("Failed to initialize models in modelsDirectory:" + modelsDirectory);
		}
		
	

	}
	
	
	public static String[] readLines(String filename) throws IOException {
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

	/*
	private static HttpRequestWithBody elinkingRequest() {
		String url = testHelper.getAPIBaseUrl() + "/e-link/documents";
		return Unirest.post(url);
	}
	*/
	
//	public static Model detectEntitiesNIF(Model nifModel, ArrayList<String> nerModels, String sentModel, String language, String link) throws ExternalServiceFailedException, IOException {
//		
//		String docURI = NIFReader.extractDocumentURI(nifModel);
//		HashMap<ArrayList, HashMap<String, Double>> entityMap = new HashMap<>();
//		String content = NIFReader.extractIsString(nifModel);
//		Span[] sentenceSpans = SentenceDetector.detectSentenceSpans(content, sentModel);
//		for (String nerModel : nerModels){
//			entityMap = detectEntitiesWithModel(entityMap, content, sentenceSpans, nerModel);
//		}
//		// filter hashmap for duplicates and keep the one with highest probability
//		for (Entry<ArrayList, HashMap<String, Double>> outerMap : entityMap.entrySet()) {
//		    ArrayList<Integer> spanList = outerMap.getKey();
//		    HashMap<String, Double> spanMap = outerMap.getValue();
//		    Double highestProb = 0.0;
//		    String finalType = null;
//		    for (HashMap.Entry<String, Double> innerMap : spanMap.entrySet()) {
//		        String type = innerMap.getKey();
//		        Double prob = innerMap.getValue();
//		        if (prob > highestProb){
//		        	finalType = type;
//		        	highestProb = prob;
//		        }
//		    }
//		    // finalType is now the type with the highest probability, so get DBpedia URI and add to the nifModel
//		    int nameStart = spanList.get(0);
//		    int nameEnd = spanList.get(1);
//		    String foundName = content.substring(nameStart, nameEnd);
//		    String nerType = null;
//		    if (finalType.equals("LOC")){
//				nerType = DFKINIF.location.toString();
//			}
//			else if (finalType.equals("PER")){
//				nerType = DFKINIF.person.toString();
//			}
//			else if (finalType.equals("ORG")){
//				nerType = DFKINIF.organization.toString();
//			}
//		    
//		    String sparqlService = null;
//		    String defaultGraph = null;
//		    if (language.equalsIgnoreCase("en")){
//		    	sparqlService = "http://dbpedia.org/sparql";
//		    	defaultGraph = "http://dbpedia.org";
//		    }
//		    else if (language.equalsIgnoreCase("de")){
//		    	sparqlService = "http://de.dbpedia.org/sparql";
//		    	defaultGraph = "http://de.dbpedia.org";
//		    }
//		    else{
//		    	//add more languages here
//		    }
//		    //List<String> entURIs = new ArrayList<String>();
//		    String entURI = null;
//		    if (link.equalsIgnoreCase("yes")){
//		    	entURI = Sparqler.getDBPediaURI(foundName, language, sparqlService, defaultGraph);
//		    	if (!(entURI == null)){
//		    		NIFWriter.addAnnotationEntities(nifModel, nameStart, nameEnd, foundName, entURI, nerType);
//		    	}
//		    	else{
//		    		NIFWriter.addAnnotationEntitiesWithoutURI(nifModel, nameStart, nameEnd, foundName, nerType);
//		    	}
//		    }
//		    else if (link.equalsIgnoreCase("no")){
//		    	NIFWriter.addAnnotationEntitiesWithoutURI(nifModel, nameStart, nameEnd, foundName, nerType);
//		    	
//		    }
//		    //TODO: currently every single entity is looked up. TODO: implement the modes (like in FREME), then do first spotting, then do lookup only on unique entities. As long as we have no means of disambiguation, this is a lot better and faster
//		    
//			
//		 // collect and add type-specific information from DBpedia:
//			if (!(entURI == null)){
//				//TODO: use e-linking here instead of self-defined sparql stuff, like so:
//				//call e-link api here (if dbpedia uri is retrieved, to get lat/long etc.)
//				/*
//				try {
//					HttpResponse<String> elinkingResponse = elinkingRequest()
//							.queryString("informat", "turtle")
//							.queryString("outformat", "turtle")
//							.queryString("templateid", "TODO")
//							.body(NIFReader.model2String(nifModel, "TTL"))//TODO; check if TTL is correct, or if it should be turtle
//							.asString();
//					System.out.println("DEBUGGING elinkingResult:" + elinkingResponse.getBody());
//							
//				} catch (UnirestException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				*/
//				
//				if (nerType.equals(DFKINIF.location.toString())){
//					NIFWriter.addPrefixToModel(nifModel, "geo", GEO.uri);
//					// NOTE: the name in our outout model suggests that this is using the w3.org lat and long ones, but it's not. 
//					Sparqler.queryDBPedia(nifModel, docURI, nameStart, nameEnd, "http://www.georss.org/georss/point", GEO.latitude, sparqlService); //www.w3.org/2003/01/geo/wgs84_pos#lat
//					Sparqler.queryDBPedia(nifModel, docURI, nameStart, nameEnd, "http://www.georss.org/georss/point", GEO.longitude, sparqlService); //www.w3.org/2003/01/geo/wgs84_pos#long
//				}
//				else if (nerType.equals(DFKINIF.person.toString())){
//					NIFWriter.addPrefixToModel(nifModel, "dbo", DBO.uri);
//					Sparqler.queryDBPedia(nifModel, docURI, nameStart, nameEnd, "http://dbpedia.org/ontology/birthDate", DBO.birthDate, sparqlService);
//					Sparqler.queryDBPedia(nifModel, docURI, nameStart, nameEnd, "http://dbpedia.org/ontology/deathDate", DBO.deathDate, sparqlService);
//				}
//				else if (nerType.equals(DFKINIF.organization.toString())){
//					Sparqler.queryDBPedia(nifModel, docURI, nameStart, nameEnd, "http://dbpedia.org/ontology/background", NIF.orgType, sparqlService);
//				}
//			}
//
//			
//
//		}
//		
//		// if there was a location in there, add document stats for geopoints 
//		if (Sparqler.latitudes.size() > 0 || Sparqler.longitudes.size() > 0){
//			Sparqler.addGeoStats(nifModel, content, docURI);
//		}
//		
//		return nifModel;
//		
//	}
	
public static Model linkEntitiesNIF(Model nifModel, String language){
	
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
					Sparqler.queryDBPedia(nifModel, documentURI, nameStart, nameEnd, "http://www.georss.org/georss/point", GEO.latitude, sparqlService);
					Sparqler.queryDBPedia(nifModel, documentURI, nameStart, nameEnd, "http://www.georss.org/georss/point", GEO.longitude, sparqlService);
				}
				//else if (e[2].equals(DFKINIF.person.toString())){
				else if (e[2].equals(DBO.person.toString())){
					NIFWriter.addPrefixToModel(nifModel, "dbo", DBO.uri);
					Sparqler.queryDBPedia(nifModel, documentURI, nameStart, nameEnd, "http://dbpedia.org/ontology/birthDate", DBO.birthDate, sparqlService);
					Sparqler.queryDBPedia(nifModel, documentURI, nameStart, nameEnd, "http://dbpedia.org/ontology/deathDate", DBO.deathDate, sparqlService);
				}
				//else if (e[2].equals(DFKINIF.organization.toString())){
				else if (e[2].equals(DBO.organisation.toString())){
					Sparqler.queryDBPedia(nifModel, documentURI, nameStart, nameEnd, "http://dbpedia.org/ontology/background", NIF.orgType, sparqlService);
				}

			}
		}

		// add doc level stats
		if (Sparqler.latitudes.size() > 0 || Sparqler.longitudes.size() > 0){
			Sparqler.addGeoStats(nifModel, NIFReader.extractIsString(nifModel), documentURI);
		}


		return nifModel;
	}
}
	
public static Model spotEntitiesNIF(Model nifModel, ArrayList<String> nerModels, String sentModel, String language) throws ExternalServiceFailedException, IOException {
		
		String docURI = NIFReader.extractDocumentURI(nifModel);
		HashMap<ArrayList, HashMap<String, Double>> entityMap = new HashMap<>();
		String content = NIFReader.extractIsString(nifModel);
		Span[] sentenceSpans = SentenceDetector.detectSentenceSpans(content, sentModel);
		for (String nerModel : nerModels){
			entityMap = detectEntitiesWithModel(entityMap, content, sentenceSpans, nerModel);
		}
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
		
		return nifModel;
		
	}

	

	public static HashMap<ArrayList, HashMap<String, Double>> detectEntitiesWithModel(HashMap<ArrayList, HashMap<String, Double>> entityMap, String text, Span[] sentenceSpans, String nerModel){
		
		NameFinderME nameFinder = null;
		// first check preLoadedModels 
		if (nameFinderPreLoadedModels.containsKey(nerModel)){
			nameFinder = (NameFinderME) nameFinderPreLoadedModels.get(nerModel);
		}
		else{
			// First try to load it again, perhaps it was trained in the mean time.
			// if not, throw exception
			// (this if prevents the need to restart the broker after training a new model)
			try {
				ClassPathResource cprNERModel = new ClassPathResource(modelsDirectory + nerModel);
				InputStream tnfNERModel;
				tnfNERModel = new FileInputStream(cprNERModel.getFile());
				TokenNameFinderModel tnfModel = new TokenNameFinderModel(tnfNERModel);
				nameFinder = new NameFinderME(tnfModel);
			} catch (Exception e ) {
				//e.printStackTrace();
				logger.error("Could not find model:" + nerModel + " in modelsDirectory:" + modelsDirectory);
				throw new BadRequestException("Model " + nerModel.substring(0, nerModel.lastIndexOf('.')) + " not found in pre-initialized map or modelsDirectory.");
			}
			
		}
		
		for (Span sentenceSpan : sentenceSpans) {
			String sentence = text.substring(sentenceSpan.getStart(), sentenceSpan.getEnd());
			Span tokenSpans[] = Tokenizer.simpleTokenizeIndices(sentence);
			String tokens[] = Span.spansToStrings(tokenSpans, sentence);
			Span nameSpans[] = nameFinder.find(tokens);
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
	
//	try {
//		ClassPathResource cprNERModel = new ClassPathResource(modelsDirectory + nerModel);
//		InputStream tnfNERModel = new FileInputStream(cprNERModel.getFile());
//		TokenNameFinderModel tnfModel = new TokenNameFinderModel(tnfNERModel);
//		NameFinderME nameFinder = new NameFinderME(tnfModel);
//		for (Span sentenceSpan : sentenceSpans){
//			String sentence = text.substring(sentenceSpan.getStart(), sentenceSpan.getEnd());
//			Span tokenSpans[] = Tokenizer.simpleTokenizeIndices(sentence);
//			String tokens[] = Span.spansToStrings(tokenSpans, sentence);
//			Span nameSpans[] = nameFinder.find(tokens);
//			for (Span s : nameSpans){
//				int nameStartIndex = 0;
//				int nameEndIndex = 0;
//				for (int i = 0; i <= tokenSpans.length ; i++){
//					if (i == s.getStart()){
//						nameStartIndex = tokenSpans[i].getStart() + sentenceSpan.getStart();
//					}
//					else if (i == s.getEnd()){
//						nameEndIndex = tokenSpans[i-1].getEnd() + sentenceSpan.getStart();
//					}
//				}
//				ArrayList<Integer> se = new ArrayList<Integer>();
//				se.add(nameStartIndex);
//				se.add(nameEndIndex);
//				// if there was another enitity of this type found at this token-span, this will not be null
//				HashMap<String, Double> spanMap = entityMap.get(se);
//				//otherwise:
//				if (spanMap == null){
//					spanMap = new HashMap<String, Double>();
//				}
//				spanMap.put(s.getType(), s.getProb());
//				//spanMap.put("LOC", 0.5); // hacking in entity of another type for testing disambiguation
//				entityMap.put(se, spanMap);
//			}
//		}
//	}
//	catch(IOException e) {
//		e.printStackTrace();
//	}
//	//System.out.println("DEBUGGING entityMap:" + entityMap);
//	return entityMap;
//}


	/**
	 * 
	 * 
	 * @param inputTrainData Stream of training data
	 * @param modelName Name to be assigned to the model
	 * @return true if the model has been successfully trained
	 */
	public static String trainModel(String inputTrainData, String modelName, String language) throws BadRequestException, ExternalServiceFailedException {
		
		//TODO: do we want to check here for valid syntax of training models? 
		//Charset charset;				
		ObjectStream<String> lineStream;
		ObjectStream<NameSample> sampleStream;
		File newModel = null;
		try{
			//charset = Charset.forName("UTF-8");
			//ClassPathResource cprOne = new ClassPathResource(inputTrainData);
			ByteArrayInputStream bais = new ByteArrayInputStream(inputTrainData.getBytes());
			lineStream = new PlainTextByLineStream(bais,"utf-8");
			//lineStream = new PlainTextByLineStream(new FileInputStream(cprOne.getFile()), charset);
			sampleStream = new NameSampleDataStream(lineStream);
			
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
	
	public static void main(String[] args) {
		
		String nifString = 
				"@prefix geo:   <http://www.w3.org/2003/01/geo/wgs84_pos/> .\n" +
						"@prefix dbo:   <http://dbpedia.org/ontology/> .\n" +
						"@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" +
						"@prefix xsd:   <http://www.w3.org/2001/XMLSchema#> .\n" +
						"@prefix itsrdf: <http://www.w3.org/2005/11/its/rdf#> .\n" +
						"@prefix dfkinif: <http://dkt.dfki.de/ontologies/nif#> .\n" +
						"@prefix nif:   <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#> .\n" +
						"@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .\n" +
						"\n" +
						"<http://dkt.dfki.de/documents/#char=565,578>\n" +
						"        a                     nif:RFC5147String , nif:String ;\n" +
						"        nif:anchorOf          \"Arctic Circle\"^^xsd:string ;\n" +
						"        nif:beginIndex        \"565\"^^xsd:nonNegativeInteger ;\n" +
						"        nif:endIndex          \"578\"^^xsd:nonNegativeInteger ;\n" +
						"        nif:entity            dfkinif:location ;\n" +
						"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,977> .\n" +
						"\n" +
						"<http://dkt.dfki.de/documents/#char=172,176>\n" +
						"        a                     nif:RFC5147String , nif:String ;\n" +
						"        nif:anchorOf          \"Asia\"^^xsd:string ;\n" +
						"        nif:beginIndex        \"172\"^^xsd:nonNegativeInteger ;\n" +
						"        nif:endIndex          \"176\"^^xsd:nonNegativeInteger ;\n" +
						"        nif:entity            dfkinif:location ;\n" +
						"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,977> ;\n" +
						"        itsrdf:taIdentRef     <http://de.dbpedia.org/resource/Asia> .\n" +
						"\n" +
						"<http://dkt.dfki.de/documents/#char=149,153>\n" +
						"        a                     nif:String , nif:RFC5147String ;\n" +
						"        nif:anchorOf          \"Iran\"^^xsd:string ;\n" +
						"        nif:beginIndex        \"149\"^^xsd:nonNegativeInteger ;\n" +
						"        nif:endIndex          \"153\"^^xsd:nonNegativeInteger ;\n" +
						"        nif:entity            dfkinif:location ;\n" +
						"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,977> ;\n" +
						"        geo:lat               \"32.49611111111111\"^^xsd:double ;\n" +
						"        geo:long              \"54.295\"^^xsd:double ;\n" +
						"        itsrdf:taIdentRef     <http://de.dbpedia.org/resource/Iran> .\n" +
						"\n" +
						"<http://dkt.dfki.de/documents/#char=0,977>\n" +
						"        a                         nif:Context , nif:String , nif:RFC5147String ;\n" +
						"        dfkinif:averageLatitude   \"32.49611111111111\"^^xsd:double ;\n" +
						"        dfkinif:averageLongitude  \"54.295\"^^xsd:double ;\n" +
						"        dfkinif:standardDeviationLatitude\n" +
						"                \"0.0\"^^xsd:double ;\n" +
						"        dfkinif:standardDeviationLongitude\n" +
						"                \"0.0\"^^xsd:double ;\n" +
						"        nif:beginIndex            \"0\"^^xsd:nonNegativeInteger ;\n" +
						"        nif:endIndex              \"977\"^^xsd:nonNegativeInteger ;\n" +
						"        nif:isString              \"Myths of Creation\\r\\n\\r\\nAlthough the Aryan inhabitants of Northern Europe are supposed by some\\r\\nauthorities to have come originally from the plateau of Iran, in the\\r\\nheart of Asia, the climate and scenery of the countries where they\\r\\nfinally settled had great influence in shaping their early religious\\r\\nbeliefs, as well as in ordering their mode of living.\\r\\n\\r\\nThe grand and rugged landscapes of Northern Europe, the midnight\\r\\nsun, the flashing rays of the aurora borealis, the ocean continually\\r\\nlashing itself into fury against the great cliffs and icebergs of\\r\\nthe Arctic Circle, could not but impress the people as vividly as\\r\\nthe almost miraculous vegetation, the perpetual light, and the blue\\r\\nseas and skies of their brief summer season. It is no great wonder,\\r\\ntherefore, that the Icelanders, for instance, to whom we owe the most\\r\\nperfect records of this belief, fancied in looking about them that the\\r\\nworld was originally created from a strange mixture of fire and ice.\"^^xsd:string .\n" +
						"";
		
		try {
			Model nifModel = NIFReader.extractModelFromFormatString(nifString, RDFSerialization.TURTLE);
			linkEntitiesNIF(nifModel, "en");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

}
