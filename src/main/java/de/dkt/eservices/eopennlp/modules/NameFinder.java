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
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.hibernate.engine.transaction.jta.platform.internal.SynchronizationRegistryBasedSynchronizationStrategy;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import com.hp.hpl.jena.rdf.model.Model;
import de.dkt.common.niftools.DBO;
import de.dkt.common.niftools.DFKINIF;
import de.dkt.common.niftools.GEO;
import de.dkt.common.niftools.NIF;
import de.dkt.common.niftools.NIFReader;
import de.dkt.common.niftools.NIFWriter;
//import de.dkt.eservices.eopennlp.TestConstants;
import de.dkt.eservices.erattlesnakenlp.modules.Sparqler;
import eu.freme.bservices.testhelper.TestHelper;
import eu.freme.bservices.testhelper.api.IntegrationTestSetup;
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

	public static String modelsDirectory = File.separator + "trainedModels" + File.separator + "ner" + File.separator;
	//public static String modelsDirectory = "trainedModels" + File.separator + "ner" + File.separator;
	static Logger logger = Logger.getLogger(NameFinder.class);

	static HashMap<String, Object> nameFinderPreLoadedModels = new HashMap<String, Object>();
	
	
	public static void initializeModels() {

		try {
			ClassPathResource nerModelsFolder = new ClassPathResource(modelsDirectory);
			String nerAbsPath = Paths.get(ClassLoader.getSystemResource(nerModelsFolder.getPath()).toURI()).toString();
			File df = new File(nerAbsPath);
			for (File f : df.listFiles()) {
				InputStream tnfNERModel = new FileInputStream(f);
				TokenNameFinderModel tnfModel = new TokenNameFinderModel(tnfNERModel);
				NameFinderME nameFinder = new NameFinderME(tnfModel);
				nameFinderPreLoadedModels.put(f.getName(), nameFinder);

			}
		} catch (IOException | URISyntaxException e) {
			logger.error("Failed to initialize models in modelsDirectory:" + modelsDirectory);
			//e.printStackTrace();
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
	
	public static Model detectEntitiesNIF(Model nifModel, ArrayList<String> nerModels, String sentModel, String language, String link) throws ExternalServiceFailedException, IOException {
		
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
				nerType = DFKINIF.location.toString();
			}
			else if (finalType.equals("PER")){
				nerType = DFKINIF.person.toString();
			}
			else if (finalType.equals("ORG")){
				nerType = DFKINIF.organization.toString();
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
		    //List<String> entURIs = new ArrayList<String>();
		    String entURI = null;
		    if (link.equalsIgnoreCase("yes")){
		    	entURI = Sparqler.getDBPediaURI(foundName, language, sparqlService, defaultGraph);
		    	if (!(entURI == null)){
		    		NIFWriter.addAnnotationEntities(nifModel, nameStart, nameEnd, foundName, entURI, nerType);
		    	}
		    	else{
		    		NIFWriter.addAnnotationEntitiesWithoutURI(nifModel, nameStart, nameEnd, foundName, nerType);
		    	}
		    }
		    else if (link.equalsIgnoreCase("no")){
		    	NIFWriter.addAnnotationEntitiesWithoutURI(nifModel, nameStart, nameEnd, foundName, nerType);
		    	
		    }
		    
			
		 // collect and add type-specific information from DBpedia:
			if (!(entURI == null)){
				//TODO: use e-linking here instead of self-defined sparql stuff, like so:
				//call e-link api here (if dbpedia uri is retrieved, to get lat/long etc.)
				/*
				try {
					HttpResponse<String> elinkingResponse = elinkingRequest()
							.queryString("informat", "turtle")
							.queryString("outformat", "turtle")
							.queryString("templateid", "TODO")
							.body(NIFReader.model2String(nifModel, "TTL"))//TODO; check if TTL is correct, or if it should be turtle
							.asString();
					System.out.println("DEBUGGING elinkingResult:" + elinkingResponse.getBody());
							
				} catch (UnirestException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				*/
				
				if (nerType.equals(DFKINIF.location.toString())){
					NIFWriter.addPrefixToModel(nifModel, "geo", GEO.uri);
					// NOTE: the name in our outout model suggests that this is using the w3.org lat and long ones, but it's not. 
					Sparqler.queryDBPedia(nifModel, docURI, nameStart, nameEnd, "http://www.georss.org/georss/point", GEO.latitude, sparqlService); //www.w3.org/2003/01/geo/wgs84_pos#lat
					Sparqler.queryDBPedia(nifModel, docURI, nameStart, nameEnd, "http://www.georss.org/georss/point", GEO.longitude, sparqlService); //www.w3.org/2003/01/geo/wgs84_pos#long
				}
				else if (nerType.equals(DFKINIF.person.toString())){
					NIFWriter.addPrefixToModel(nifModel, "dbo", DBO.uri);
					Sparqler.queryDBPedia(nifModel, docURI, nameStart, nameEnd, "http://dbpedia.org/ontology/birthDate", DBO.birthDate, sparqlService);
					Sparqler.queryDBPedia(nifModel, docURI, nameStart, nameEnd, "http://dbpedia.org/ontology/deathDate", DBO.deathDate, sparqlService);
				}
				else if (nerType.equals(DFKINIF.organization.toString())){
					Sparqler.queryDBPedia(nifModel, docURI, nameStart, nameEnd, "http://dbpedia.org/ontology/background", NIF.orgType, sparqlService);
				}
			}

			

		}
		
		// if there was a location in there, add document stats for geopoints 
		if (Sparqler.latitudes.size() > 0 || Sparqler.longitudes.size() > 0){
			Sparqler.addGeoStats(nifModel, content, docURI);
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
		
	}
	

}
