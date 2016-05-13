package de.dkt.eservices.eopennlp;

import java.io.IOException;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import com.hp.hpl.jena.rdf.model.Model;
import de.dkt.common.niftools.DKTNIF;
import de.dkt.common.niftools.NIFReader;
import de.dkt.common.niftools.NIFWriter;
import de.dkt.common.tools.ParameterChecker;
import de.dkt.eservices.eopennlp.modules.DictionaryNameF;
import de.dkt.eservices.eopennlp.modules.NameFinder;
import de.dkt.eservices.eopennlp.modules.RegexFinder;
import de.dkt.eservices.erattlesnakenlp.modules.LanguageIdentificator;
import eu.freme.common.conversion.rdf.RDFConstants;
import eu.freme.common.exception.BadRequestException;
import eu.freme.common.exception.ExternalServiceFailedException;

/**
 * @author Julian Moreno Schneider julian.moreno_schneider@dfki.de, Peter Bourgonje peter.bourgonje@dfki.de
 *
 * The whole documentation about openNLP examples can be found in https://opennlp.apache.org/documentation/1.6.0/manual/opennlp.html
 *
 */

@Component
public class EOpenNLPService {

	Logger logger = Logger.getLogger(EOpenNLPService.class);
	
	public EOpenNLPService(){
		NameFinder.initializeModels();
	}

	public Model analyze(String textToProcess, String languageParam, String analysisType, String models,  RDFConstants.RDFSerialization inFormat, String mode) throws ExternalServiceFailedException, BadRequestException,
					IOException, Exception {
		ParameterChecker.checkNotNullOrEmpty(languageParam, "language", logger);
		ParameterChecker.checkNotNullOrEmpty(analysisType, "analysis type", logger);
        try {
        	Model nifModel = null;
        	if (inFormat.equals(RDFConstants.RDFSerialization.PLAINTEXT)){
    			nifModel = NIFWriter.initializeOutputModel();
    			NIFWriter.addInitialString(nifModel, textToProcess, DKTNIF.getDefaultPrefix());
    		}
    		else {
    			nifModel = NIFReader.extractModelFromFormatString(textToProcess,inFormat);
    		}
        	String sentModel = null;
    		String[] nerModels = models.split(";");
    		         		
    		if (languageParam.equals("en") || languageParam.equals("de")){
    			sentModel = languageParam + "-sent.bin";
    		}
    		else{
    			logger.error("Unsupported combination of language ["+languageParam+"] and analysis: "+ analysisType);
    			throw new BadRequestException("Unsupported combination of language ["+languageParam+"] and analysis: "+ analysisType);
    		}

    		//LanguageIdentificator.detectLanguageNIF(nifModel); //currently only for ACL paper!
    		
        	if(analysisType.equalsIgnoreCase("ner")){
        		
        		if (mode.equals("spot")){
        			ArrayList<String> statModels = new ArrayList<String>();
            		for (String nerModel : nerModels){
            			String storedModel = nerModel + ".bin";
            			ClassPathResource cprNERModel = new ClassPathResource(NameFinder.modelsDirectory + storedModel);
            			if (!cprNERModel.exists()){
                			throw new BadRequestException("Unsupported model name for analysis: " + analysisType + " and model: " + nerModel + ". Please train a model with this name first.");
                		}
            			statModels.add(storedModel);
            		}
            		nifModel = NameFinder.spotEntitiesNIF(nifModel, statModels, sentModel, languageParam);
        			
        		}
        		else if (mode.equals("link")){
        			nifModel = NameFinder.linkEntitiesNIF(nifModel, languageParam);
        		}
        		else if (mode.equals("all")){
        			ArrayList<String> statModels = new ArrayList<String>();
            		for (String nerModel : nerModels){
            			String storedModel = nerModel + ".bin";
            			ClassPathResource cprNERModel = new ClassPathResource(NameFinder.modelsDirectory + storedModel);
            			if (!cprNERModel.exists()){
                			throw new BadRequestException("Unsupported model name for analysis: " + analysisType + " and model: " + nerModel + ". Please train a model with this name first.");
                		}
            			statModels.add(storedModel);
            		}
            		nifModel = NameFinder.spotEntitiesNIF(nifModel, statModels, sentModel, languageParam);
            		nifModel = NameFinder.linkEntitiesNIF(nifModel, languageParam);
        		}
        		else{
        			throw new BadRequestException("Unsupported mode: " + mode);
        		}
        		
//        		ArrayList<String> statModels = new ArrayList<String>();
//        		for (String nerModel : nerModels){
//        			String storedModel = nerModel + ".bin";
//        			ClassPathResource cprNERModel = new ClassPathResource(NameFinder.modelsDirectory + storedModel);
//        			if (!cprNERModel.exists()){
//            			throw new BadRequestException("Unsupported model name for analysis: " + analysisType + " and model: " + nerModel + ". Please train a model with this name first.");
//            		}
//        			statModels.add(storedModel);
//        		}
//        		nifModel = NameFinder.detectEntitiesNIF(nifModel, statModels, sentModel, languageParam, link);
        		
        		return nifModel;
        	}
        	else if(analysisType.equalsIgnoreCase("dict")){
        		ArrayList<String> dictionaries = new ArrayList<String>();
        		for (String nerModel : nerModels){
        			ClassPathResource cprNERModel = new ClassPathResource(DictionaryNameF.dictionariesDirectory + nerModel);
    				if (!cprNERModel.exists()){
        				throw new BadRequestException("Unsupported model name for analysis: " + analysisType + " and model: " + nerModel + ". Please train a model with this name first.");
        			}
    				dictionaries.add(nerModel);
        		}
        		
        		nifModel = DictionaryNameF.detectEntitiesNIF(nifModel, dictionaries, sentModel);
        		return nifModel;
        		
        	}
        	else if (analysisType.equalsIgnoreCase("temp")){
        		for (String nerModel : nerModels){
        			if (nerModel.equalsIgnoreCase("germanDates") || nerModel.equalsIgnoreCase("englishDates")){
        				nifModel = RegexFinder.detectEntitiesNIF(nifModel, sentModel, languageParam, null);
        			}
        			else{
        				throw new BadRequestException("Temporal analysis not supported for language: " + languageParam);
        			}
        		}
        		return nifModel;
        	}
        	else{
        		logger.error("Unsupported analysis: "+analysisType);
        		throw new BadRequestException("Unsupported analysis: "+analysisType);
        	}
        } catch (BadRequestException e) {
        	logger.error(e.getMessage());
            throw e;
    	} catch (ExternalServiceFailedException e2) {
        	logger.error(e2.getMessage());
    		throw e2;
    	}
        
	}
	
	public String trainModel(String trainData, String modelName, String languageParam, String analysis)
            throws ExternalServiceFailedException, BadRequestException {
        try {
    		ParameterChecker.checkNotNullOrEmpty(trainData, "training data", logger);
    		ParameterChecker.checkNotNullOrEmpty(languageParam, "language", logger);
    		ParameterChecker.checkNotNullOrEmpty(analysis, "analysis type", logger);

        	String trainedModelName = null;
        	if (analysis.equalsIgnoreCase("ner")){
        		if(languageParam.equals("es") || languageParam.equals("en") || languageParam.equals("de") || languageParam.equals("nl")){
        			//modelName = languageParam+"-ner";
        		}
        		else{
        			throw new BadRequestException("Unsupported language for analysis: "+analysis);
        		}
        		trainedModelName = NameFinder.trainModel(trainData, modelName, languageParam);
        	}
        	
        	else if (analysis.equalsIgnoreCase("dict")){
        		if (!modelName.contains("_")){
        			throw new BadRequestException("Dictionary name should end on _<TYPE>. (e.g. 'mendelsohn_PER'). Please use underscore and type in dictionary name.");
        		}
        		trainedModelName = DictionaryNameF.createDictionaryFromString("tsv", trainData, modelName); // TODO: to accept more input formats, get around hard-coded tsv here
        	}
        	
        	// Training other models (tokenization model, sentence model, etc) can happen here, if necessary
        	
        	else {
        		throw new BadRequestException("Unsupported analysis: "+analysis);
        	}

        	if(trainedModelName!=null){
               	return trainedModelName;
        	}
            return null;
        } catch (BadRequestException e) {
        	logger.error(e.getMessage());
            throw e;
    	} 

    }
      


	public ResponseEntity<String> createDictionary(String dictionaryDataType, String inputData, String dictionaryName) throws ExternalServiceFailedException, BadRequestException {
    	try {
    		ParameterChecker.checkNotNullOrEmpty(inputData, "training data", logger);
    		ParameterChecker.checkNotNullOrEmpty(dictionaryName, "dictionary name", logger);

    		String result = null;
   			result = DictionaryNameF.createDictionaryFromString(dictionaryDataType, inputData, dictionaryName);

    		if(result!=null){
    			HttpHeaders responseHeaders = new HttpHeaders();
    			responseHeaders.add("Content-Type", "text/plain");
    			String body = result;
    			ResponseEntity<String> response = new ResponseEntity<String>(body, responseHeaders, HttpStatus.OK);
    			return response;
    		}
    		return null;
    	} catch (BadRequestException e) {
        	logger.error(e.getMessage());
    		throw e;
    	} 
    }

	public static void main(String[] args) throws Exception {
	}
	
}
