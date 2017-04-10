package de.dkt.eservices.eheideltime;

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
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;

import de.dkt.common.feedback.InteractionManagement;
import de.dkt.common.niftools.DKTNIF;
import de.dkt.common.tools.ParameterChecker;
import eu.freme.common.conversion.rdf.RDFConstants;
import eu.freme.common.exception.BadRequestException;
import eu.freme.common.exception.ExternalServiceFailedException;
import eu.freme.common.rest.BaseRestController;
import eu.freme.common.rest.NIFParameterSet;


@RestController
public class EHeideltimeRestController extends BaseRestController {

	Logger logger = Logger.getLogger(EHeideltimeRestController.class);
	
	@Autowired
	EHeideltimeService service;
	
	@RequestMapping(value = "/e-heideltime/testURL", method = { RequestMethod.POST, RequestMethod.GET })
	public ResponseEntity<String> testURL(
			@RequestParam(value = "preffix", required = false) String preffix,
            @RequestBody(required = false) String postBody) throws Exception {

    	HttpHeaders responseHeaders = new HttpHeaders();
    	responseHeaders.add("Content-Type", "text/plain");
    	ResponseEntity<String> response = new ResponseEntity<String>("The restcontroller is working properly", responseHeaders, HttpStatus.OK);
    	return response;
	}
	
	
	@RequestMapping(value = "/e-heideltime/analyze", method = {
            RequestMethod.POST, RequestMethod.GET })
	public ResponseEntity<String> analyzeText(
			HttpServletRequest request,
			@RequestParam(value = "input", required = false) String input,
			@RequestParam(value = "analysis", required = false) String analysis, // either ner or dict or temp or tfidf
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
        if (prefix == null || prefix.equalsIgnoreCase("")){
			prefix = DKTNIF.getDefaultPrefix();
		}

//        if(analysis == null) {
//			String msg = "Unspecified analysis type.";
//			logger.error(msg);
//			InteractionManagement.sendInteraction("dkt-usage@"+request.getRemoteAddr(), "error", "e-NLP/openNLP/namedEntityRecognition", msg, "", "Exception", msg, "");
//			throw new BadRequestException(msg);
//        }
//        else if (analysis.equalsIgnoreCase("dict") || analysis.equalsIgnoreCase("temp")){
//        	// mode in combination with dict or temp analysis makes no sense. Possibly may want to tell this to the user here... (But now mode is set to all by default, so this will always be triggered, also when user did not specify mode
//        	//if (mode != null){
//        		//throw new BadRequestException("Analysis type " + analysis + " in combination with mode " + mode + " not supported.");
//        	//}
//        }
        
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
        
        String textForProcessing = null;
        if (nifParameters.getInformat().equals(RDFConstants.RDFSerialization.PLAINTEXT)) {
            textForProcessing = nifParameters.getInput();
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
        	Model outModel = service.analyze(textForProcessing, language, analysis, models, nifParameters.getInformat(), mode, nifParameters.getPrefix());
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
	
}

	
