package de.dkt.eservices.erattlesnakenlp;

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
import de.dkt.common.tools.ParameterChecker;
import eu.freme.common.conversion.rdf.RDFConstants;
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

			InteractionManagement.sendInteraction("dkt-usage@"+request.getRemoteAddr(), "usage", "e-NLP/rattlesnakeNLP/segmentParagraphs", "Success", "", "Exception", "", "");

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
			InteractionManagement.sendInteraction("dkt-usage@"+request.getRemoteAddr(), "usage", "e-NLP/rattlesnakeNLP/languageIdentification", "Success", "", "Exception", "", "");

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
	public ResponseEntity<String> listModels(
			HttpServletRequest request,
			@RequestParam(value = "language", required = false) String language,
			@RequestParam(value = "threshold", required = false) String thresholdValue,
			@RequestParam(value = "prefix", required = false) String prefix,
			@RequestParam(value = "p", required = false) String p,
			@RequestHeader(value = "Accept", required = false) String acceptHeader,
			@RequestHeader(value = "Content-Type", required = false) String contentTypeHeader,
            @RequestParam Map<String, String> allParams,
            @RequestBody(required = false) String postBody) throws Exception {
		ParameterChecker.checkInList(language, "en;de", "language", logger);
		// Check the document or directory parameter.
        if(language == null) {
       		logger.error("Parameter language not specified.");
       		throw new BadRequestException("Parameter language not specified.");
        }
        double t;
        if (thresholdValue != null) {
			//try to parse to double and check if in between 0 and 1.
        	try{
        		t = Double.parseDouble(thresholdValue);
        		if (!(t >= 0 && t <= 1)){
        			String msg = "Please specify a number between 0 and 1 for parameter thresholdValue. Current value: " + thresholdValue;
        			logger.error(msg);
        			InteractionManagement.sendInteraction("dkt-usage@"+request.getRemoteAddr(), "error", "e-NLP/rattlesnakeNLP/suggestEntityCandidates", msg, "", "Exception", msg, "");
        			throw new BadRequestException(msg);
        		}
        	}
        	catch (NumberFormatException e){
    			String msg = "Unable to parse string to double for parameter thresholdValue: " + thresholdValue;
    			logger.error(msg);
    			InteractionManagement.sendInteraction("dkt-usage@"+request.getRemoteAddr(), "error", "e-NLP/rattlesnakeNLP/suggestEntityCandidates", msg, "", "Exception", msg, "");
    			throw new BadRequestException(msg);
        	}
		} else { // if none specified, default is 0.5
			t = 0.5;
		}
        
        NIFParameterSet nifParameters = this.normalizeNif(postBody, acceptHeader, contentTypeHeader, allParams, false);
        
        String textForProcessing = null;
        if (nifParameters.getInformat().equals(RDFConstants.RDFSerialization.PLAINTEXT)) {
            textForProcessing = nifParameters.getInput();
        } else {
        	textForProcessing = postBody;
            if (textForProcessing == null) {
    			String msg = "No text to process.";
    			logger.error(msg);
    			InteractionManagement.sendInteraction("dkt-usage@"+request.getRemoteAddr(), "error", "e-NLP/rattlesnakeNLP/suggestEntityCandidates", msg, "", "Exception", msg, "");
    			throw new BadRequestException(msg);
            }
        }
        ArrayList<String> entityCandidates = service.suggestEntityCandidates(textForProcessing, language, nifParameters.getInformat(), t);
        String output = "";
        for (String s: entityCandidates){
        	output += s + "\n";
        }
        
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "text/plain");
        ResponseEntity<String> response = new ResponseEntity<String>(output, responseHeaders, HttpStatus.OK);

        InteractionManagement.sendInteraction("dkt-usage@"+request.getRemoteAddr(), "usage", "e-NLP/rattlesnakeNLP/suggestEntityCandidates", "Success", "", "Exception", "", "");
        
        return response;
	}


}
