package de.dkt.eservices.erattlesnakenlp;

import java.util.Map;

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
			@RequestParam(value = "preffix", required = false) String preffix,
			@RequestBody(required = false) String postBody) throws Exception {

	    HttpHeaders responseHeaders = new HttpHeaders();
	    responseHeaders.add("Content-Type", "text/plain");
	    ResponseEntity<String> response = new ResponseEntity<String>("The restcontroller is working properly", responseHeaders, HttpStatus.OK);
	    return response;
	}
	
	
	@RequestMapping(value = "/e-rattlesnakenlp/segmentParagraphs", method = {
            RequestMethod.POST, RequestMethod.GET })
	public ResponseEntity<String> segmentParagraphs(
			@RequestParam(value = "inputFile", required = false) String inputFile,
			@RequestParam(value = "language", required = false) String language,
			@RequestHeader(value = "Accept", required = false) String acceptHeader,
			@RequestHeader(value = "Content-Type", required = false) String contentTypeHeader,
            @RequestParam Map<String, String> allParams,
            @RequestBody(required = false) String postBody) throws Exception {
		ParameterChecker.checkInList(language, "en;de;es;da;nl;pt;se", "language", logger);
		ParameterChecker.checkNotNull(inputFile, "inputFile", logger);
		try {
        	return service.segmentParagraphs(inputFile, language);            
        } catch (Exception e) {
        	logger.error(e.getMessage());
        	throw e;
        }
		
	}
	
	@RequestMapping(value = "/e-nlp/languageIdentification", method = {
            RequestMethod.POST, RequestMethod.GET })
	public ResponseEntity<String> identifyInputLanguage(
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
            	logger.error("No text to process.");
                throw new BadRequestException("No text to process.");
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
            return createSuccessResponse(outModel, nifParameters.getOutformat());
            
        } catch (BadRequestException e) {
        	logger.error(e.getMessage());
            throw e;
        } catch (ExternalServiceFailedException e) {
        	logger.error(e.getMessage());
            throw e;
        }
    }


	

}
