package de.dkt.eservices.eopennlp;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;

import de.dkt.common.feedback.InteractionManagement;
import de.dkt.common.filemanagement.FileFactory;
import de.dkt.common.tools.ParameterChecker;
import de.dkt.eservices.eopennlp.modules.DictionaryNameF;
import de.dkt.eservices.eopennlp.modules.NameFinder;
import eu.freme.common.conversion.rdf.RDFConstants;
import eu.freme.common.exception.BadRequestException;
import eu.freme.common.exception.ExternalServiceFailedException;
import eu.freme.common.rest.BaseRestController;
import eu.freme.common.rest.NIFParameterSet;


@RestController
public class EOpenNLPServiceStandAlone extends BaseRestController {

	Logger logger = Logger.getLogger(EOpenNLPServiceStandAlone.class);
	
	@Autowired
	EOpenNLPService service;
	
	@RequestMapping(value = "/e-opennlp/testURL", method = { RequestMethod.POST, RequestMethod.GET })
	public ResponseEntity<String> testURL(
			@RequestParam(value = "preffix", required = false) String preffix,
            @RequestBody(required = false) String postBody) throws Exception {

    	HttpHeaders responseHeaders = new HttpHeaders();
    	responseHeaders.add("Content-Type", "text/plain");
    	ResponseEntity<String> response = new ResponseEntity<String>("The restcontroller is working properly", responseHeaders, HttpStatus.OK);
    	return response;
	}
	
	@RequestMapping(value = "/e-nlp/trainModel", method = {
            RequestMethod.POST, RequestMethod.GET })
	public ResponseEntity<String> trainModel(
			HttpServletRequest request,
			@RequestParam(value = "trainingData", required = false) String trainingData,
			@RequestParam(value = "t", required = false) String t,
			@RequestParam(value = "analysis", required = false) String analysis,
			@RequestParam(value = "a", required = false) String a,
			@RequestParam(value = "language", required = false) String language,
			@RequestParam(value = "modelName", required = false) String modelName,
			@RequestParam(value = "m", required = false) String m,
			@RequestHeader(value = "Accept", required = false) String acceptHeader,
			@RequestHeader(value = "Content-Type", required = false) String contentTypeHeader,
            @RequestParam Map<String, String> allParams,
            @RequestBody(required = false) String postBody) throws Exception {
		// Check the language parameter.
		ParameterChecker.checkInList(language, "en;de", "language", logger);
		// merge long and short parameters - long parameters override short
		// parameters
		if (trainingData == null) {
			trainingData = t;
		}
		if (analysis == null) {
			analysis = a;
		}
		if (modelName == null) {
			modelName = m;
		}
		
        if(allParams.get("trainingData")==null){
        	allParams.put("trainingData", trainingData);
        }
        if(allParams.get("analysis")==null){
        	allParams.put("analysis", analysis);
        }
        if(allParams.get("modelName")==null){
        	allParams.put("modelName", modelName);
        }
        if(allParams.get("language")==null){
        	allParams.put("language", language);
        }
        
        if (allParams.get("trainingData") != null) {
        	// trainingdata is sent as value of the trainingdata parameter
            trainingData = allParams.get("trainingData");
        } else {
        	trainingData = postBody;
            if (trainingData == null) {
    			String msg = "No training data to process.";
    			logger.error(msg);
    			InteractionManagement.sendInteraction("dkt-usage@"+request.getRemoteAddr(), "error", "e-NLP/openNLP/trainModel", msg, 
    					"", "Exception", msg, "");
    			throw new BadRequestException(msg);
            }
        }

		
        try {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.add("Content-Type", "text/plain");
            // trainingData is now expected to be a String (containing the annotated data). Perhaps we want to change this to accept a file at some point, since annotated data can grow large.
            String trainedModelName = service.trainModel(trainingData, modelName, language, analysis);
            String body = "The model ["+trainedModelName+"] has been generated.";
    		ResponseEntity<String> response = new ResponseEntity<String>(body, responseHeaders, HttpStatus.OK);

    		InteractionManagement.sendInteraction("dkt-usage@"+request.getRemoteAddr(), "usage", "e-NLP/openNLP/trainModel", "Success", "", "", "", "");

            return response;
            
        } catch (BadRequestException e) {
        	logger.error(e.getMessage());
			InteractionManagement.sendInteraction("dkt-usage@"+request.getRemoteAddr(), "error", "e-NLP/openNLP/trainModel", e.getMessage(), "", "Exception", e.getMessage(), "");
            throw e;
        } catch (ExternalServiceFailedException e) {
        	logger.error(e.getMessage());
			InteractionManagement.sendInteraction("dkt-usage@"+request.getRemoteAddr(), "error", "e-NLP/openNLP/trainModel", e.getMessage(), "", "Exception", e.getMessage(), "");
            throw e;
        }
	}
	


	@RequestMapping(value = "/e-nlp/namedEntityRecognition", method = {
            RequestMethod.POST, RequestMethod.GET })
	public ResponseEntity<String> analyzeText(
			HttpServletRequest request,
			@RequestParam(value = "input", required = false) String input,
			@RequestParam(value = "analysis", required = false) String analysis, // either ner or dict
			@RequestParam(value = "models", required = false) String models,
			@RequestParam(value = "language", required = false) String language,
			@RequestParam(value = "i", required = false) String i,
			@RequestParam(value = "informat", required = false) String informat,
			@RequestParam(value = "f", required = false) String f,
			@RequestParam(value = "outformat", required = false) String outformat,
			@RequestParam(value = "o", required = false) String o,
			@RequestParam(value = "mode", required = false) String mode,
			@RequestParam(value = "prefix", required = false) String prefix,
			@RequestParam(value = "p", required = false) String p,
			@RequestHeader(value = "Accept", required = false) String acceptHeader,
			@RequestHeader(value = "Content-Type", required = false) String contentTypeHeader,
            @RequestParam Map<String, String> allParams,
            @RequestBody(required = false) String postBody) throws Exception {
        
		// Check the language parameter.
		ParameterChecker.checkInList(language, "en;de", "language", logger);
        ParameterChecker.checkNotNullOrEmpty(models, "models", logger);

		ArrayList<String> rMode = new ArrayList<>();

		if (mode != null) {
			String[] modes = mode.split(";");
			for (String m : modes) {
				if (m.equals("spot") || m.equals("link") || m.equals("all")) {
					rMode.add(m);
				} else {
	    			String msg = "Unsupported mode: " + m;
	    			logger.error(msg);
	    			InteractionManagement.sendInteraction("dkt-usage@"+request.getRemoteAddr(), "error", "e-NLP/openNLP/namedEntityRecognition", msg, 
	    					"", "Exception", msg, "");
	    			throw new BadRequestException(msg);
				}
			}
			if (rMode.contains("link") && (!rMode.contains("spot")) && informat.equals("text")) {
    			String msg = "Unsupported mode combination: Either provide NIF input or use link in combination with spot.";
    			logger.error(msg);
    			InteractionManagement.sendInteraction("dkt-usage@"+request.getRemoteAddr(), "error", "e-NLP/openNLP/namedEntityRecognition", msg, 
    					"", "Exception", msg, "");
    			throw new BadRequestException(msg);
			}

			if (rMode.contains("all")) {
				rMode.clear();
				rMode.add("all");
			}

		} else { // if none specified, default is to do all
			rMode.add("all");
		}

		if (rMode.contains("all") || (rMode.contains("spot") && rMode.contains("link"))) {
			mode = "all";
		} else if (rMode.contains("spot") && !(rMode.contains("link"))) {
			mode = "spot";
		} else if (rMode.contains("link") && !(rMode.contains("spot"))) { // redundant check; just else would also do, but find this more readable
			mode = "link";
		}

        if(analysis == null) {
			String msg = "Unspecified analysis type.";
			logger.error(msg);
			InteractionManagement.sendInteraction("dkt-usage@"+request.getRemoteAddr(), "error", "e-NLP/openNLP/namedEntityRecognition", msg, 
					"", "Exception", msg, "");
			throw new BadRequestException(msg);
        }
        else if (analysis.equalsIgnoreCase("dict") || analysis.equalsIgnoreCase("temp")){
        	// mode in combination with dict or temp analysis makes no sense. Possibly may want to tell this to the user here... (But now mode is set to all by default, so this will always be triggered, also when user did not specify mode
        	//if (mode != null){
        		//throw new BadRequestException("Analysis type " + analysis + " in combination with mode " + mode + " not supported.");
        	//}
        	
        }


        
        if(allParams.get("input")==null){
        	allParams.put("input", input);
        }
        if(allParams.get("informat")==null){
        	allParams.put("informat", informat);
        }
        if(allParams.get("outformat")==null){
        	allParams.put("outformat", outformat);
        }
        if(allParams.get("prefix")==null){
        	allParams.put("prefix", prefix);
        }
        
        NIFParameterSet nifParameters = this.normalizeNif(postBody, acceptHeader, contentTypeHeader, allParams, false);
        
        Model inModel = ModelFactory.createDefaultModel();

        String textForProcessing = null;
        if (nifParameters.getInformat().equals(RDFConstants.RDFSerialization.PLAINTEXT)) {
        	// input is sent as value of the input parameter
            textForProcessing = nifParameters.getInput();
            //rdfConversionService.plaintextToRDF(inModel, textForProcessing,language, nifParameters.getPrefix());
        } else {
            //inModel = rdfConversionService.unserializeRDF(nifParameters.getInput(), nifParameters.getInformat());
        	textForProcessing = postBody;
            if (textForProcessing == null) {
    			String msg = "No text to process.";
    			logger.error(msg);
    			InteractionManagement.sendInteraction("dkt-usage@"+request.getRemoteAddr(), "error", "e-NLP/openNLP/namedEntityRecognition", msg, 
    					"", "Exception", msg, "");
    			throw new BadRequestException(msg);
            }
        }
        
        
        try {
        	//ResponseEntity<String> result = service.analyze(textForProcessing, language, analysis, models, informat, nifParameters.getOutformat().toString());
        	Model outModel = service.analyze(textForProcessing, language, analysis, models, nifParameters.getInformat(), mode);
            //Model outModel = getRdfConversionService().unserializeRDF(result.getBody(), nifParameters.getOutformat());
        	//outModel.read(new ByteArrayInputStream(result.getBody().getBytes()), null, informat);
            outModel.add(inModel);
            // remove unwanted info
            outModel.removeAll(null, RDF.type, OWL.ObjectProperty);
            outModel.removeAll(null, RDF.type, OWL.DatatypeProperty);
            outModel.removeAll(null, RDF.type, OWL.Class);
            outModel.removeAll(null, RDF.type, OWL.Class);
            ResIterator resIter = outModel.listResourcesWithProperty(RDF.type, outModel.getResource("http://persistence.uni-leipzig.org/nlp2rdf/ontologies/rlog#Entry"));
            while (resIter.hasNext()) {
                Resource res = resIter.next();
                outModel.removeAll(res, null, (RDFNode) null);
            }
    		InteractionManagement.sendInteraction("dkt-usage@"+request.getRemoteAddr(), "usage", "e-NLP/openNLP/namedEntityRecognition", "Success", "", "", "", "");

            return createSuccessResponse(outModel, nifParameters.getOutformat());
            
        } catch (BadRequestException e) {
			InteractionManagement.sendInteraction("dkt-usage@"+request.getRemoteAddr(), "error", "e-NLP/openNLP/namedEntityRecognition", e.getMessage(), "", "Exception", e.getMessage(), "");
        	logger.error(e.getMessage());
            throw e;
        } catch (ExternalServiceFailedException e) {
			InteractionManagement.sendInteraction("dkt-usage@"+request.getRemoteAddr(), "error", "e-NLP/openNLP/namedEntityRecognition", e.getMessage(), "", "Exception", e.getMessage(), "");
        	logger.error(e.getMessage());
            throw e;
        }
    }
	
	@RequestMapping(value = "/e-nlp/listModels", method = {
            RequestMethod.POST, RequestMethod.GET })
	public ResponseEntity<String> listModels(
			HttpServletRequest request,
			@RequestParam(value = "analysis", required = false) String analysis, // either ner or dict
			@RequestParam(value = "prefix", required = false) String prefix,
			@RequestParam(value = "p", required = false) String p,
			@RequestHeader(value = "Accept", required = false) String acceptHeader,
			@RequestHeader(value = "Content-Type", required = false) String contentTypeHeader,
            @RequestParam Map<String, String> allParams,
            @RequestBody(required = false) String postBody) throws Exception {
        
		// Check the document or directory parameter.
        if(analysis == null) {
			String msg = "Unspecified analysis type.";
			logger.error(msg);
			InteractionManagement.sendInteraction("dkt-usage@"+request.getRemoteAddr(), "error", "e-NLP/openNLP/listModels", msg, "", "Exception", msg, "");
			throw new BadRequestException(msg);
        }
        
        String models = "";
        if (analysis.equalsIgnoreCase("ner")){
        	File modelFolder = FileFactory.generateOrCreateDirectoryInstance(NameFinder.modelsDirectory);
        	if(modelFolder.isDirectory()){
            	File [] files = modelFolder.listFiles();
            	for (File ff: files) {
            		models += ff.getName().replaceAll("\\.bin$", "") + "\n";
            	}
            }
        }
        else if (analysis.equalsIgnoreCase("dict")){
        	File modelFolder = FileFactory.generateOrCreateDirectoryInstance(DictionaryNameF.dictionariesDirectory);
        	if(modelFolder.isDirectory()){
            	File [] files = modelFolder.listFiles();
            	for (File ff: files) {
            		models += ff.getName() + "\n";
            	}
            }
        }
        else if (analysis.equalsIgnoreCase("temp")){
        	models += "germanDates" + "\n";
        	models += "englishDates" + "\n";
        }
        else{
			String msg = "Analysis type unknown.";
			logger.error(msg);
			InteractionManagement.sendInteraction("dkt-usage@"+request.getRemoteAddr(), "error", "e-NLP/openNLP/listModels", msg, "", "Exception", msg, "");
			throw new BadRequestException(msg);
        }
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "text/plain");
        ResponseEntity<String> response = new ResponseEntity<String>(models, responseHeaders, HttpStatus.OK);
		InteractionManagement.sendInteraction("dkt-usage@"+request.getRemoteAddr(), "usage", "e-NLP/openNLP/listModels", "Success", "", "", "", "");
        return response;
	}
}

	
