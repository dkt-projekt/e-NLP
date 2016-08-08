//package de.dkt.eservices.esargraph;
//
//import java.util.ArrayList;
//import java.util.Map;
//
//import org.apache.log4j.Logger;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestHeader;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.hp.hpl.jena.rdf.model.Model;
//import com.hp.hpl.jena.rdf.model.ModelFactory;
//import com.hp.hpl.jena.rdf.model.RDFNode;
//import com.hp.hpl.jena.rdf.model.ResIterator;
//import com.hp.hpl.jena.rdf.model.Resource;
//import com.hp.hpl.jena.vocabulary.OWL;
//import com.hp.hpl.jena.vocabulary.RDF;
//
//import de.dkt.common.niftools.NIFWriter;
//import de.dkt.common.tools.ParameterChecker;
//import eu.freme.common.conversion.rdf.RDFConstants;
//import eu.freme.common.conversion.rdf.RDFConstants.RDFSerialization;
//import eu.freme.common.exception.BadRequestException;
//import eu.freme.common.exception.ExternalServiceFailedException;
//import eu.freme.common.rest.BaseRestController;
//import eu.freme.common.rest.NIFParameterSet;
//
//@RestController
//public class ESargraphServiceStandAlone extends BaseRestController {
//    
//	Logger logger = Logger.getLogger(ESargraphServiceStandAlone.class);
//
//	@Autowired
//	ESargraphService service;
//		
//	@RequestMapping(value = "/e-sargraph/testURL", method = { RequestMethod.POST, RequestMethod.GET })
//	public ResponseEntity<String> testURL(@RequestBody(required = false) String postBody) throws Exception {
//	    HttpHeaders responseHeaders = new HttpHeaders();
//	    responseHeaders.add("Content-Type", "text/plain");
//	    ResponseEntity<String> response = new ResponseEntity<String>("The restcontroller is working properly", responseHeaders, HttpStatus.OK);
//	    return response;
//	}
//	
//	@RequestMapping(value = "/e-sargraph/processData", method = { RequestMethod.POST, RequestMethod.GET })
//    //public ResponseEntity<String> processData(String data, String languageParam, RDFSerialization inFormat)
//	public ResponseEntity<String> processData(
//			@RequestParam(value = "input", required = false) String input,
//			@RequestParam(value = "language", required = false) String language,
//			@RequestParam(value = "informat", required = false) String informat,
//			@RequestHeader(value = "Accept", required = false) String acceptHeader,
//			@RequestHeader(value = "Content-Type", required = false) String contentTypeHeader,
//			@RequestParam Map<String, String> allParams, @RequestBody(required = false) String postBody)
//					throws Exception {
//
//		NIFParameterSet nifParameters = this.normalizeNif(postBody, acceptHeader, contentTypeHeader, allParams, false);
//
//		// throws ExternalServiceFailedException, BadRequestException {
//		try {
//			ParameterChecker.checkNotNullOrEmpty(input, "data", logger);
//
//			Model nifModel = service.processData(input, language, nifParameters.getInformat());
//
//			return createSuccessResponse(nifModel, RDFSerialization.RDF_XML); // TODO: check if first arg should be model, or string of the model
//		} catch (Exception e) {
//			throw new ExternalServiceFailedException(e.getMessage());
//		}
//	}
//
//	@RequestMapping(value = "/e-sargraph/storeData", method = { RequestMethod.POST, RequestMethod.GET })
//    public ResponseEntity<String> storeData(String data, String languageParam, String model)
//            throws ExternalServiceFailedException, BadRequestException {
//        try {
//        	ParameterChecker.checkNotNullOrEmpty(data, "data", logger);
//        	        	
//        	String nif = service.storeData(data, languageParam, model);
//        	
//        	
//        	return createSuccessResponse(nif, RDFSerialization.RDF_XML);
//        } catch (Exception e) {
//            throw new ExternalServiceFailedException(e.getMessage());
//        }
//    }
//
//	public ResponseEntity<String> createSuccessResponse(String body,RDFConstants.RDFSerialization rdfFormat) {
//		HttpHeaders responseHeaders = new HttpHeaders();
//		responseHeaders.add("Content-Type", rdfFormat.contentType());
////		String rdfString;
////		try {
////			rdfString = serializeNif(rdf, rdfFormat);
////		} catch (Exception e) {
////			throw new InternalServerErrorException();
////		}
//		return new ResponseEntity<String>(body, responseHeaders, HttpStatus.OK);
//	}
//	
//	@RequestMapping(value = "/e-sargraph/recognizePatterns", method = {
//            RequestMethod.POST, RequestMethod.GET })
//	public ResponseEntity<String> analyzeText(
//			@RequestParam(value = "input", required = false) String input,
//			@RequestParam(value = "language", required = false) String language,
//			@RequestParam(value = "i", required = false) String i,
//			@RequestParam(value = "informat", required = false) String informat,
//			@RequestParam(value = "f", required = false) String f,
//			@RequestParam(value = "outformat", required = false) String outformat,
//			@RequestParam(value = "o", required = false) String o,
//			@RequestParam(value = "mode", required = false) String mode,
//			@RequestParam(value = "prefix", required = false) String prefix,
//			@RequestParam(value = "p", required = false) String p,
//			@RequestHeader(value = "Accept", required = false) String acceptHeader,
//			@RequestHeader(value = "Content-Type", required = false) String contentTypeHeader,
//
//			@RequestParam(value = "analysis", required = false) String analysis, // either ner or dict
//			@RequestParam(value = "models", required = false) String models,
//
//			@RequestParam Map<String, String> allParams,
//            @RequestBody(required = false) String postBody) throws Exception {
//        
//		// Check the language parameter.
//		ParameterChecker.checkInList(language, "en;de", "language", logger);
//		ParameterChecker.checkNotNull(analysis, "analysis", logger);
//
//        if(allParams.get("input")==null){
//        	allParams.put("input", input);
//        }
//        if(allParams.get("informat")==null){
//        	allParams.put("informat", informat);
//        }
//        if(allParams.get("outformat")==null){
//        	allParams.put("outformat", outformat);
//        }
//        if(allParams.get("prefix")==null){
//        	allParams.put("prefix", prefix);
//        }
//        
//        NIFParameterSet nifParameters = this.normalizeNif(postBody, acceptHeader, contentTypeHeader, allParams, false);
//        
//        Model inModel = ModelFactory.createDefaultModel();
//
//        String textForProcessing = null;
//        if (nifParameters.getInformat().equals(RDFConstants.RDFSerialization.PLAINTEXT)) {
//        	// input is sent as value of the input parameter
//            textForProcessing = nifParameters.getInput();
//            //rdfConversionService.plaintextToRDF(inModel, textForProcessing,language, nifParameters.getPrefix());
//        } else {
//            //inModel = rdfConversionService.unserializeRDF(nifParameters.getInput(), nifParameters.getInformat());
//        	textForProcessing = postBody;
//            if (textForProcessing == null) {
//            	logger.error("No text to process.");
//                throw new BadRequestException("No text to process.");
//            }
//        }
//        try {
//        	Model outModel = NIFWriter.initializeOutputModel();
//        	//Model outModel = service.analyze(textForProcessing, language, analysis, models, nifParameters.getInformat(), mode);
//        	return createSuccessResponse(outModel, nifParameters.getOutformat());
//            
//        } catch (BadRequestException e) {
//        	logger.error(e.getMessage());
//            throw e;
//        } catch (ExternalServiceFailedException e) {
//        	logger.error(e.getMessage());
//            throw e;
//        }
//    }
//
//}
