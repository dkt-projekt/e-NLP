package de.dkt.eservices.eopennlp;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.apache.jena.riot.RiotException;
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
import de.dkt.eservices.enlp.ENLPPerformanceTest;
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
	
	NameFinder nameFinder;
	
	public EOpenNLPService(){
		nameFinder = new NameFinder();
	}

	@PostConstruct
	public void initializeModels(){
		nameFinder.initializeModels();
	}
	
	public Model analyze(String textToProcess, String languageParam, String analysisType, String models,  RDFConstants.RDFSerialization inFormat, String mode, String prefix) 
			throws ExternalServiceFailedException, BadRequestException,IOException, Exception {
		ParameterChecker.checkNotNullOrEmpty(languageParam, "language", logger);
		ParameterChecker.checkNotNullOrEmpty(analysisType, "analysis type", logger);
		
		Date d_inter_initial = new Date();
		MemoryUsage m_inter_initial = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();

        try {
        	Model nifModel = null;
        	if (inFormat.equals(RDFConstants.RDFSerialization.PLAINTEXT)){
    			nifModel = NIFWriter.initializeOutputModel();
    			NIFWriter.addInitialString(nifModel, textToProcess, prefix);
    		}
    		else {
    			try{
    				nifModel = NIFReader.extractModelFromFormatString(textToProcess,inFormat);
    			}
    			catch(RiotException e){
    				throw new BadRequestException("Check the input format ["+inFormat+"]!!");
    			}
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

    		System.gc();
    		Date d_inter_final = new Date();
    		MemoryUsage m_inter_final = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
    		ENLPPerformanceTest.printUsageData(ENLPPerformanceTest.bw, "NER Initialization", d_inter_initial, d_inter_final, m_inter_initial, m_inter_final);

    		//LanguageIdentificator.detectLanguageNIF(nifModel); //currently only for ACL paper!
    		
    		Date d_inter_initial2 = new Date();
    		MemoryUsage m_inter_initial2 = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
    		
        	if(analysisType.equalsIgnoreCase("ner")){
        		if (mode.equals("spot")){
        			
            		Date d_inter_initial3 = new Date();
            		MemoryUsage m_inter_initial3 = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();

        			ArrayList<String> statModels = new ArrayList<String>();
            		for (String nerModel : nerModels){
            			String storedModel = nerModel + ".bin";
            			ClassPathResource cprNERModel = new ClassPathResource(nameFinder.modelsDirectory + storedModel);
            			if (!cprNERModel.exists()){
                			throw new BadRequestException("Unsupported model name for analysis: " + analysisType + " and model: " + nerModel + ". Please train a model with this name first.");
                		}
        				cprNERModel =null;
            			statModels.add(storedModel);
            		}
            		
            		System.gc();
            		Date d_inter_final3 = new Date();
            		MemoryUsage m_inter_final3 = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
            		ENLPPerformanceTest.printUsageData(ENLPPerformanceTest.bw, "NER Processing", d_inter_initial3, d_inter_final3, m_inter_initial3, m_inter_final3);

            		Date d_inter_initial4 = new Date();
            		MemoryUsage m_inter_initial4 = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();

            		nifModel = nameFinder.spotEntitiesNIF(nifModel, statModels, sentModel, languageParam);

            		System.gc();
            		Date d_inter_final4 = new Date();
            		MemoryUsage m_inter_final4 = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
            		ENLPPerformanceTest.printUsageData(ENLPPerformanceTest.bw, "Spoting Processing", d_inter_initial4, d_inter_final4, m_inter_initial4, m_inter_final4);


        		}
        		else if (mode.equals("link")){
        			nifModel = nameFinder.linkEntitiesNIF(nifModel, languageParam);
        		}
        		else if (mode.equals("all")){
        			ArrayList<String> statModels = new ArrayList<String>();
            		for (String nerModel : nerModels){
            			String storedModel = nerModel + ".bin";
            			ClassPathResource cprNERModel = new ClassPathResource(nameFinder.modelsDirectory + storedModel);
            			if (!cprNERModel.exists()){
                			throw new BadRequestException("Unsupported model name for analysis: " + analysisType + " and model: " + nerModel + ". Please train a model with this name first.");
                		}
        				cprNERModel =null;
            			statModels.add(storedModel);
            		}
            		nifModel = nameFinder.spotEntitiesNIF(nifModel, statModels, sentModel, languageParam);
            		nifModel = nameFinder.linkEntitiesNIF(nifModel, languageParam);
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
        	}
        	else if(analysisType.equalsIgnoreCase("dict")){
        		ArrayList<String> dictionaries = new ArrayList<String>();
        		for (String nerModel : nerModels){
        			ClassPathResource cprNERModel = new ClassPathResource(DictionaryNameF.dictionariesDirectory + nerModel);
    				if (!cprNERModel.exists()){
        				throw new BadRequestException("Unsupported model name for analysis: " + analysisType + " and model: " + nerModel + ". Please train a model with this name first.");
        			}
    				cprNERModel =null;
    				dictionaries.add(nerModel);
        		}
        		nifModel = DictionaryNameF.detectEntitiesNIF(nifModel, dictionaries, sentModel);
        	}
        	else if (analysisType.equalsIgnoreCase("temp")){
        		for (String nerModel : nerModels){
        			if (nerModel.equalsIgnoreCase("germanDates") || nerModel.equalsIgnoreCase("englishDates")){
        				nifModel = RegexFinder.detectEntitiesNIF(nifModel, sentModel, languageParam, null);
        			}
        			else{
        				throw new BadRequestException("Please use germanDates or englishDates: temporal analysis not supported for other model names");
        			}
        		}
        	}
        	else{
        		logger.error("Unsupported analysis: "+analysisType);
        		throw new BadRequestException("Unsupported analysis: "+analysisType);
        	}
        	
    		System.gc();
    		Date d_inter_final2 = new Date();
    		MemoryUsage m_inter_final2 = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
    		ENLPPerformanceTest.printUsageData(ENLPPerformanceTest.bw, "NER Processing", d_inter_initial2, d_inter_final2, m_inter_initial2, m_inter_final2);

    		return nifModel;
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
        		trainedModelName = nameFinder.trainModel(trainData, modelName, languageParam);
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
