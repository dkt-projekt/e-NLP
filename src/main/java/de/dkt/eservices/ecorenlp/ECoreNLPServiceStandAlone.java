/**
 * Copyright (C) 2015 3pc, Art+Com, Condat, Deutsches Forschungszentrum 
 * für Künstliche Intelligenz, Kreuzwerke (http://)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.dkt.eservices.ecorenlp;

import java.util.Map;

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
import eu.freme.common.conversion.rdf.RDFConstants;
import eu.freme.common.exception.BadRequestException;
import eu.freme.common.exception.ExternalServiceFailedException;
import eu.freme.common.rest.BaseRestController;
import eu.freme.common.rest.NIFParameterSet;

@RestController
public class ECoreNLPServiceStandAlone extends BaseRestController {
	
	
	
	@Autowired
	ECoreNLPService service;
	

	@RequestMapping(value = "/e-corenlp/testURL", method = { RequestMethod.POST, RequestMethod.GET })
	public ResponseEntity<String> testURL(
			@RequestParam(value = "preffix", required = false) String preffix,
            @RequestBody(required = false) String postBody) throws Exception {

    	HttpHeaders responseHeaders = new HttpHeaders();
    	responseHeaders.add("Content-Type", "text/plain");
    	ResponseEntity<String> response = new ResponseEntity<String>("The restcontroller is working properly", responseHeaders, HttpStatus.OK);
    	return response;
	}	
	
	@RequestMapping(value = "/e-nlp/lemmatize", method = {
            RequestMethod.POST, RequestMethod.GET })
	public ResponseEntity<String> lemmatize(
	//public String executeRetrieval(
			@RequestParam(value = "inputText", required = false) String inputText,
			@RequestParam(value = "i", required = false) String i,
			//@RequestParam(value = "text", required = false) String text,
			//@RequestParam(value = "t", required = false) String t,
			@RequestParam(value = "language", required = false) String language,
            @RequestBody(required = false) String postBody) throws Exception {
		
        // Check the language parameter.
        if(language == null) {
            throw new BadRequestException("Parameter language is not specified");
        } else {
            if(language.equals("en") 
//                    || language.equals("de") 
//                    || language.equals("nl")
//                    || language.equals("it")
//                    || language.equals("fr")
//                    || language.equals("es")
//                    || language.equals("ru")
                   ) {
                // OK, the language is supported.
            } else {
                // The language specified with the language parameter is not supported.
                throw new BadRequestException("Unsupported language.");
            }
        }

        // Check the text parameter.
        if(inputText== null) {
        	if(i==null){
        		throw new BadRequestException("Unspecified text query.");
        	}
        	else{
        		inputText = i;
        	}
        }

        try {
        	String lemmas = service.callLemmatizer(inputText, language);
        	HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.add("Content-Type", "RDF/XML");
            return new ResponseEntity<String>(lemmas, responseHeaders, HttpStatus.OK); //TODO may want to do conversion to ResponseEntity in callLemmatizer (or Lemmatizer itself) already
            
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (ExternalServiceFailedException e) {
            throw e;
        }
	}
	
	@RequestMapping(value = "/e-nlp/partOfSpeechTagging", method = {
            RequestMethod.POST, RequestMethod.GET })
	public ResponseEntity<String> tagInput(
			@RequestParam(value = "input", required = false) String input,
			@RequestParam(value = "language", required = false) String language,
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
        
	
		// Check the language parameter.
		if(language == null) {
			throw new BadRequestException("Parameter language is not specified");
		} else {
			if(language.equals("en")
					|| language.equals("de") 
					//|| language.equals("es")
					//|| language.equals("da")
					//|| language.equals("nl")
					//|| language.equals("pt")
					//|| language.equals("se")
					) {
				// OK, the language is supported.
            } else {
                // The language specified with the language parameter is not supported.
                throw new BadRequestException("Unsupported language.");
            }
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
        
        String textForProcessing = null;
        if (nifParameters.getInformat().equals(RDFConstants.RDFSerialization.PLAINTEXT)) {
        	// input is sent as value of the input parameter
            textForProcessing = nifParameters.getInput();
            //rdfConversionService.plaintextToRDF(inModel, textForProcessing,language, nifParameters.getPrefix());
        } else {
            //inModel = rdfConversionService.unserializeRDF(nifParameters.getInput(), nifParameters.getInformat());
        	textForProcessing = postBody;
            if (textForProcessing == null) {
                throw new BadRequestException("No text to process.");
            }
        }

		
		try {
			Model outModel = service.tagInput(textForProcessing, language, nifParameters.getInformat());
			return createSuccessResponse(outModel, nifParameters.getOutformat());           
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (ExternalServiceFailedException e) {
            throw e;
        }
		
	}
	
	
	        	

}
