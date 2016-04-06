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

import org.springframework.stereotype.Component;

import com.hp.hpl.jena.rdf.model.Model;

import eu.freme.common.conversion.rdf.RDFConstants;
import eu.freme.common.conversion.rdf.RDFSerializationFormats;
import eu.freme.common.exception.BadRequestException;
import eu.freme.common.exception.ExternalServiceFailedException;
import de.dkt.common.niftools.DFKINIF;
import de.dkt.common.niftools.NIFReader;
import de.dkt.common.niftools.NIFWriter;
import de.dkt.eservices.ecorenlp.modules.Lemmatizer;
import de.dkt.eservices.ecorenlp.modules.Tagger;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;


@Component
public class ECoreNLPService {
    
    public String callLemmatizer(String text, String languageParam)
            throws ExternalServiceFailedException, BadRequestException {
        try {
        	if(text==null){
                throw new BadRequestException("Bad request: no text specified");
        	}
        	if(languageParam==null){
                throw new BadRequestException("Bad request: no language specified");
        	}
        	Lemmatizer lemmy = new Lemmatizer(); // TODO: check with Julian/Jan if this is correct (e.g. defining the Lemmatizer right here, instead of just inside the class)
        	return lemmy.lemmatize(text, languageParam).toString();
        } catch (Exception e) {
            throw new ExternalServiceFailedException(e.getMessage());
        }
//        } catch (UnsupportedEncodingException ex) {
//            Logger.getLogger(ELuceneService.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        throw new ExternalServiceFailedException("External service failed to process the request.");
    }
    
    
    public Model tagInput(String textToProcess, String languageParam, RDFConstants.RDFSerialization inFormat)
            throws ExternalServiceFailedException, BadRequestException {
    	
    	try {
        	Model nifModel = null;
        	if (inFormat.equals(RDFConstants.RDFSerialization.PLAINTEXT)){
        		nifModel = NIFWriter.initializeOutputModel();
        		NIFWriter.addInitialString(nifModel, textToProcess, DFKINIF.getDefaultPrefix());
    		}
    		else {
    			nifModel = NIFReader.extractModelFromFormatString(textToProcess, inFormat);
    		}
        	String sentModel = null;
    		if (languageParam.equals("en") || languageParam.equals("de")){
    			sentModel = languageParam + "-sent.bin";
    		}
    		else{
    			throw new BadRequestException("No sentence model available for language:"+languageParam);
    		}
    		MaxentTagger tagger = Tagger.initTagger(languageParam);
    		nifModel = Tagger.tagNIF(tagger, nifModel, inFormat.toString(), sentModel);
        	return nifModel;//tagger.tagString(text);
        } catch (Exception e) {
            throw new ExternalServiceFailedException(e.getMessage());
        }
    }
    
}
