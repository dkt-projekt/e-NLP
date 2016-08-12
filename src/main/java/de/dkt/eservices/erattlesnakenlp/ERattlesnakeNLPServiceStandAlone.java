package de.dkt.eservices.erattlesnakenlp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import org.apache.jena.riot.RiotException;
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
import de.dkt.common.niftools.DKTNIF;
import de.dkt.common.niftools.NIFManagement;
import de.dkt.common.niftools.NIFReader;
import de.dkt.common.niftools.NIFWriter;
import de.dkt.common.tools.ParameterChecker;
import eu.freme.common.conversion.rdf.RDFConstants;
import eu.freme.common.conversion.rdf.RDFConversionService;
import eu.freme.common.exception.BadRequestException;
import eu.freme.common.exception.ExternalServiceFailedException;
import eu.freme.common.rest.BaseRestController;
import eu.freme.common.rest.NIFParameterSet;

@RestController
public class ERattlesnakeNLPServiceStandAlone extends BaseRestController {
    
	Logger logger = Logger.getLogger(ERattlesnakeNLPServiceStandAlone.class);
	
	@Autowired
	ERattlesnakeNLPService service;
		
	@RequestMapping(value = "/e-rattlesnakenlp/testURL", method = { RequestMethod.POST, RequestMethod.GET })
	public ResponseEntity<String> testURL(
			@RequestParam(value = "prefix", required = false) String preffix,
			@RequestBody(required = false) String postBody) throws Exception {

	    HttpHeaders responseHeaders = new HttpHeaders();
	    responseHeaders.add("Content-Type", "text/plain");
	    ResponseEntity<String> response = new ResponseEntity<String>("The restcontroller is working properly", responseHeaders, HttpStatus.OK);
	    return response;
	}
	
	
	@RequestMapping(value = "/e-rattlesnakenlp/segmentParagraphs", method = {
            RequestMethod.POST, RequestMethod.GET })
	public ResponseEntity<String> segmentParagraphs(
			HttpServletRequest request,
			@RequestParam(value = "inputFile", required = false) String inputFile,
			@RequestParam(value = "language", required = false) String language,
			@RequestHeader(value = "Accept", required = false) String acceptHeader,
			@RequestHeader(value = "Content-Type", required = false) String contentTypeHeader,
            @RequestParam Map<String, String> allParams,
            @RequestBody(required = false) String postBody) throws Exception {
		ParameterChecker.checkInList(language, "en;de;es;da;nl;pt;se", "language", logger);
		ParameterChecker.checkNotNull(inputFile, "inputFile", logger);
		try {
			ResponseEntity<String> response =service.segmentParagraphs(inputFile, language);

			InteractionManagement.sendInteraction("dkt-usage@"+request.getRemoteAddr(), "usage", "e-NLP/rattlesnakeNLP/segmentParagraphs", "Success", "", "", "", "");

        	return response;
        } catch (Exception e) {
        	logger.error(e.getMessage());
			InteractionManagement.sendInteraction("dkt-usage@"+request.getRemoteAddr(), "error", "e-NLP/rattlesnakeNLP/segmentParagraphs", e.getMessage(), "", "Exception", e.getMessage(), "");
        	throw e;
        }
		
	}
	
	@RequestMapping(value = "/e-nlp/languageIdentification", method = {
            RequestMethod.POST, RequestMethod.GET })
	public ResponseEntity<String> identifyInputLanguage(
			HttpServletRequest request,
			@RequestParam(value = "input", required = false) String input,
			@RequestParam(value = "i", required = false) String i,
			@RequestParam(value = "informat", required = false) String informat,
			@RequestParam(value = "f", required = false) String f,
			@RequestParam(value = "outformat", required = false) String outformat,
			@RequestParam(value = "o", required = false) String o,
			@RequestParam(value = "prefix", required = false) String prefix,
			@RequestParam(value = "p", required = false) String p,
			@RequestHeader(value = "Accept", required = false) String acceptHeader,
			@RequestHeader(value = "Content-Type", required = false) String contentTypeHeader,
            @RequestParam Map<String, String> allParams,
            @RequestBody(required = false) String postBody) throws Exception {
        
		
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
    			InteractionManagement.sendInteraction("dkt-usage@"+request.getRemoteAddr(), "error", "e-NLP/rattlesnakeNLP/languageIdentification", msg, "", "Exception", msg, "");
    			throw new BadRequestException(msg);
            }
        }
        
        
        try {
        	Model outModel = service.identifyInputLanguage(textForProcessing, nifParameters.getInformat());
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
			InteractionManagement.sendInteraction("dkt-usage@"+request.getRemoteAddr(), "usage", "e-NLP/rattlesnakeNLP/languageIdentification", "Success", "", "", "", "");

            return createSuccessResponse(outModel, nifParameters.getOutformat());
            
        } catch (BadRequestException e) {
        	logger.error(e.getMessage());
            throw e;
        } catch (ExternalServiceFailedException e) {
        	logger.error(e.getMessage());
            throw e;
        }
    }


	@RequestMapping(value = "/e-nlp/suggestEntityCandidates", method = {
            RequestMethod.POST, RequestMethod.GET })
	public ResponseEntity<String> suggestEntityCandidates(
			HttpServletRequest request,
			@RequestParam(value = "input", required = false) String input,
			   @RequestParam(value = "i", required = false) String i,
			   @RequestParam(value = "informat", required = false) String informat,
			   @RequestParam(value = "f", required = false) String f,
			   @RequestParam(value = "outformat", required = false) String outformat,
			   @RequestParam(value = "o", required = false) String o,
			   @RequestParam(value = "prefix", required = false) String prefix,
			   @RequestParam(value = "p", required = false) String p,
			   @RequestParam(value = "language", required = false) String language,
			   @RequestHeader(value = "Accept", required = false) String acceptHeader,
			   @RequestHeader(value = "Content-Type", required = false) String contentTypeHeader,


			@RequestParam(value = "threshold", required = false) String thresholdValue,
			@RequestParam(value = "classificationModels", required = false) String classificationModels,
            @RequestParam Map<String, String> allParams,
            @RequestBody(required = false) String postBody) throws Exception {
		ParameterChecker.checkInList(language, "en;de", "language", logger);
		// Check the document or directory parameter.
		if (input == null) {
			input = i;
		}
		if (informat == null) {
			informat = f;
		}
		if (outformat == null) {
			outformat = o;
		}
		if (prefix == null) {
			prefix = p;
		}
        if(language == null) {
       		logger.error("Parameter language not specified.");
       		throw new BadRequestException("Parameter language not specified.");
        }
        if (language.equalsIgnoreCase("en")){
        	// all is well
        }
        // TODO: add support for German (have to find document collection to get tfIdf serialized hashmap for this
        else{
        	throw new BadRequestException("Language not supported.");
        }
        
        double t;
        if (thresholdValue != null) {
			//try to parse to double and check if in between 0 and 1.
        	try{
        		t = Double.parseDouble(thresholdValue);
        		if (!(t >= 0 && t <= 1)){
        			String msg = "Please specify a number between 0 and 1 for parameter thresholdValue. Current value: " + thresholdValue;
        			logger.error(msg);
        			InteractionManagement.sendInteraction("dkt-usage@"+request.getRemoteAddr(), "error", "/e-nlp/suggestEntityCandidates/", msg, "", "Exception", msg, "");
        			throw new BadRequestException(msg);
        		}
        	}
        	catch (NumberFormatException e){
    			String msg = "Unable to parse string to double for parameter thresholdValue: " + thresholdValue;
    			logger.error(msg);
    			InteractionManagement.sendInteraction("dkt-usage@"+request.getRemoteAddr(), "error", "/e-nlp/suggestEntityCandidates/", msg, "", "Exception", msg, "");
    			throw new BadRequestException(msg);
        	}
		} else { // if none specified, default is 0.02
			t = 0.02;
		}
        ArrayList<String> cm = null;

		if (classificationModels != null) {
			cm = new ArrayList<String>();
			String[] modes = classificationModels.split(";");
			for (String m : modes) {
				cm.add(m);
			}
		}

		NIFParameterSet nifParameters = this.normalizeNif(input, informat, outformat, postBody, acceptHeader, contentTypeHeader, prefix);
		Model inModel = null;
		String textForProcessing = null;
        if (nifParameters.getInformat().equals(RDFConstants.RDFSerialization.PLAINTEXT)) {
        	// input is sent as value of the input parameter
            textForProcessing = nifParameters.getInput();
            inModel = NIFWriter.initializeOutputModel();
            NIFWriter.addInitialString(inModel, textForProcessing, DKTNIF.getDefaultPrefix());
        }
        else {
        	try{
				inModel = NIFReader.extractModelFromFormatString(textForProcessing,nifParameters.getInformat());
			}
			catch(RiotException e){
				throw new BadRequestException("Check the input format ["+nifParameters.getInformat()+"]!!");
			}
        }
        
			        
		HashMap<String, Double> suggestionMap = service.suggestEntityCandidates(inModel, language, nifParameters.getInformat(), t, cm);
	    Set<Entry<String, Double>> set = suggestionMap.entrySet();
        List<Entry<String, Double>> list = new ArrayList<Entry<String, Double>>(set);
        Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
            public int compare(Map.Entry<String, Double> o1,
                    Map.Entry<String, Double> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });

	    String output = "";
        for (Entry<String, Double> s: list){
        	output += s.getKey() + "\n";
        }
        
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "text/plain");
        ResponseEntity<String> response = new ResponseEntity<String>(output, responseHeaders, HttpStatus.OK);

        InteractionManagement.sendInteraction("dkt-usage@"+request.getRemoteAddr(), "usage", "/e-nlp/suggestEntityCandidates", "Success", "", "", "", "");
        
        return response;
	}
	
	
	@Autowired
	 RDFConversionService rdfConversionService;
	@RequestMapping(value = "/e-nlp/suggestEntityCandidatesForCollection", method = {
            RequestMethod.POST, RequestMethod.GET })
	public ResponseEntity<String> suggestEntityCandidatesForCollection(
			HttpServletRequest request,
			@RequestParam(value = "input", required = false) String input,
			   @RequestParam(value = "i", required = false) String i,
			   @RequestParam(value = "informat", required = false) String informat,
			   @RequestParam(value = "f", required = false) String f,
			   @RequestParam(value = "outformat", required = false) String outformat,
			   @RequestParam(value = "o", required = false) String o,
			   @RequestParam(value = "prefix", required = false) String prefix,
			   @RequestParam(value = "p", required = false) String p,
			   @RequestParam(value = "language", required = false) String language,
			   @RequestHeader(value = "Accept", required = false) String acceptHeader,
			   @RequestHeader(value = "Content-Type", required = false) String contentTypeHeader,

			@RequestParam(value = "threshold", required = false) String thresholdValue,
			@RequestParam(value = "classificationModels", required = false) String classificationModels,
            @RequestParam Map<String, String> allParams,
            @RequestBody(required = false) String postBody) throws Exception {
		ParameterChecker.checkInList(language, "en;de", "language", logger);
		// Check the document or directory parameter.
		if (input == null) {
			input = i;
		}
		if (informat == null) {
			informat = f;
		}
		if (outformat == null) {
			outformat = o;
		}
		if (prefix == null) {
			prefix = p;
		}
        if(language == null) {
       		logger.error("Parameter language not specified.");
       		throw new BadRequestException("Parameter language not specified.");
        }
        if (language.equalsIgnoreCase("en")){
        	// all is well
        }
        // TODO: add support for German (have to find document collection to get tfIdf serialized hashmap for this
        else{
        	throw new BadRequestException("Language not supported.");
        }
        
        double t;
        if (thresholdValue != null) {
			//try to parse to double and check if in between 0 and 1.
        	try{
        		t = Double.parseDouble(thresholdValue);
        		if (!(t >= 0 && t <= 1)){
        			String msg = "Please specify a number between 0 and 1 for parameter thresholdValue. Current value: " + thresholdValue;
        			logger.error(msg);
        			InteractionManagement.sendInteraction("dkt-usage@"+request.getRemoteAddr(), "error", "/e-nlp/suggestEntityCandidatesForCollection", msg, "", "Exception", msg, "");
        			throw new BadRequestException(msg);
        		}
        	}
        	catch (NumberFormatException e){
    			String msg = "Unable to parse string to double for parameter thresholdValue: " + thresholdValue;
    			logger.error(msg);
    			InteractionManagement.sendInteraction("dkt-usage@"+request.getRemoteAddr(), "error", "/e-nlp/suggestEntityCandidatesForCollection", msg, "", "Exception", msg, "");
    			throw new BadRequestException(msg);
        	}
		} else { // if none specified, default is 0.02
			t = 0.02;
		}
        ArrayList<String> cm = null;

		if (classificationModels != null) {
			cm = new ArrayList<String>();
			String[] modes = classificationModels.split(";");
			for (String m : modes) {
				cm.add(m);
			}
		}

		NIFParameterSet nifParameters = this.normalizeNif(input, informat, outformat, postBody, acceptHeader, contentTypeHeader, prefix);
		Model inModel = null;
		//TODO: debug the thing below, got a nullpointerexception last time I tried
		if (nifParameters.getInformat().equals(RDFConstants.RDFSerialization.PLAINTEXT)) {
			rdfConversionService.plaintextToRDF(inModel, nifParameters.getInput(),language, nifParameters.getPrefix());
		} else {
			inModel = rdfConversionService.unserializeRDF(nifParameters.getInput(), nifParameters.getInformat());
		}
			        
        
        List<Model> documents = NIFManagement.extractDocumentsModels(inModel);
        
        String output = "";
        for (Model m : documents) {
        	HashMap<String, Double> suggestionMap = service.suggestEntityCandidates(m, language, nifParameters.getInformat(), t, cm);
        	Set<Entry<String, Double>> set = suggestionMap.entrySet();
            List<Entry<String, Double>> list = new ArrayList<Entry<String, Double>>(set);
            Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
                public int compare(Map.Entry<String, Double> o1,
                        Map.Entry<String, Double> o2) {
                    return o2.getValue().compareTo(o1.getValue());
                }
            });

            for (Entry<String, Double> s: list){
            	output += s.getKey() + "\n";
            }
		}
        
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "text/plain");
        ResponseEntity<String> response = new ResponseEntity<String>(output, responseHeaders, HttpStatus.OK);

        InteractionManagement.sendInteraction("dkt-usage@"+request.getRemoteAddr(), "usage", "/e-nlp/suggestEntityCandidatesForCollection", "Success", "", "", "", "");
        
        return response;
	}
	

	
	
	
	@RequestMapping(value = "/e-nlp/uploadClassificationModel", method = {
            RequestMethod.POST, RequestMethod.GET })
	public ResponseEntity<String> uploadClassificationModel(
			HttpServletRequest request,
			@RequestParam(value = "data", required = false) String data,
			@RequestParam(value = "modelName", required = false) String modelName,
			@RequestParam(value = "prefix", required = false) String prefix,
			@RequestParam(value = "p", required = false) String p,
			@RequestHeader(value = "Accept", required = false) String acceptHeader,
			@RequestHeader(value = "Content-Type", required = false) String contentTypeHeader,
            @RequestParam Map<String, String> allParams,
            @RequestBody(required = false) String postBody) throws Exception {
		
        if(modelName == null) {
       		logger.error("Parameter modelName not specified.");
       		throw new BadRequestException("Parameter modelName not specified.");
        }
        else if (modelName.contains(";")){
        	logger.error("Usage of ; (semi-colon) not allowed in modelName.");
       		throw new BadRequestException("Usage of ; (semi-colon) not allowed in modelName");
        }
        
        NIFParameterSet nifParameters = this.normalizeNif(postBody, acceptHeader, contentTypeHeader, allParams, false);
        
        if (allParams.get("data") != null) {
        	// trainingdata is sent as value of the trainingdata parameter
            data = allParams.get("data");
        } else {
        	data = postBody;
            if (data == null) {
    			String msg = "No data to process.";
    			logger.error(msg);
    			InteractionManagement.sendInteraction("dkt-usage@"+request.getRemoteAddr(), "error", "e-nlp/uploadClassificationModel", msg, 
    					"", "Exception", msg, "");
    			throw new BadRequestException(msg);
            }
        }
        
        
        String r = service.uploadClassificationModel(data, modelName);
        String output = "";
        
        
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "text/plain");
        ResponseEntity<String> response = new ResponseEntity<String>(output, responseHeaders, HttpStatus.OK);

        InteractionManagement.sendInteraction("dkt-usage@"+request.getRemoteAddr(), "usage", "e-nlp/uploadClassificationModel", "Success", "", "", "", "");
        
        return response;
	}

	
	
	
	

}
