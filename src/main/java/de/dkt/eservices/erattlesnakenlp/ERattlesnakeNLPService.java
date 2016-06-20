package de.dkt.eservices.erattlesnakenlp;

import de.dkt.common.niftools.DKTNIF;
import de.dkt.common.niftools.NIFReader;
import de.dkt.common.niftools.NIFWriter;
import de.dkt.common.tools.FileReadUtilities;
import de.dkt.common.tools.ParameterChecker;
import de.dkt.eservices.eopennlp.modules.DictionaryNameF;
import de.dkt.eservices.eopennlp.modules.NameFinder;
import de.dkt.eservices.eopennlp.modules.RegexFinder;
import de.dkt.eservices.erattlesnakenlp.linguistic.EntityCandidateExtractor;
import de.dkt.eservices.erattlesnakenlp.modules.LanguageIdentificator;
import de.dkt.eservices.erattlesnakenlp.modules.ParagraphDetector;
import eu.freme.common.conversion.rdf.RDFConstants;
import eu.freme.common.exception.BadRequestException;
import eu.freme.common.exception.ExternalServiceFailedException;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.jena.riot.RiotException;
import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

@Component
public class ERattlesnakeNLPService {
    
	Logger logger = Logger.getLogger(ERattlesnakeNLPService.class);

	public ResponseEntity<String> segmentParagraphs(String inputFile, String language) {
		ResponseEntity<String> responseCode = null;
		FileSystemResource fsr = new FileSystemResource(inputFile);
		String inputText;
		try {
			inputText = FileReadUtilities.readFile2String(fsr.getPath());
		} catch (IOException e) {
			logger.error("Error at reading input file");
			throw new BadRequestException("Error at reading input file");
		}
		Model nifModel = ModelFactory.createDefaultModel();
		//Model nifModel = NIFWriter.initializeOutputModel();
		NIFWriter.addInitialString(nifModel, inputText, DKTNIF.createDocumentURI());
		responseCode = ParagraphDetector.detectParagraphsNIF(nifModel);
		return responseCode;
	}
		
	public Model identifyInputLanguage(String textToProcess, RDFConstants.RDFSerialization inFormat) {
		
		
		Model nifModel = null;
    	if (inFormat.equals(RDFConstants.RDFSerialization.PLAINTEXT)){
			nifModel = NIFWriter.initializeOutputModel();
			NIFWriter.addInitialString(nifModel, textToProcess, DKTNIF.getDefaultPrefix());
		}
		else {
			try {
				nifModel = NIFReader.extractModelFromFormatString(textToProcess,inFormat);
			} catch (Exception e) {
				logger.error("Could not unserialize NIF string\n" + textToProcess + "\nwith format:\n" + inFormat);
				throw new BadRequestException("Unable to unserialize input NIF. Please validate.");
			}
		}
    	// ideally do initialization of ngramMaps as early as possible (or at least make sure that it is done only once). Depending on the size of the training corpus, it can take a while.
    	if (LanguageIdentificator.ngrampMapDict.isEmpty()){
    		System.out.println("ngramMap is empty, initializing...");
    		LanguageIdentificator.initializeNgramMaps();
    	}
    	
		LanguageIdentificator.detectLanguageNIF(nifModel);
    	return nifModel;
	}
	
	public ArrayList<String> suggestEntityCandidates(String textToProcess, String languageParam, RDFConstants.RDFSerialization inFormat, double thresholdValue)
					throws ExternalServiceFailedException, BadRequestException, IOException, Exception {
		ParameterChecker.checkNotNullOrEmpty(languageParam, "language", logger);

		ArrayList<String> entityCandidates = new ArrayList<String>();
		try {
			Model nifModel = null;
			if (inFormat.equals(RDFConstants.RDFSerialization.PLAINTEXT)) {
				nifModel = NIFWriter.initializeOutputModel();
				NIFWriter.addInitialString(nifModel, textToProcess, DKTNIF.getDefaultPrefix());
			} else {
				try {
					nifModel = NIFReader.extractModelFromFormatString(textToProcess, inFormat);
				} catch (RiotException e) {
					throw new BadRequestException("Check the input format [" + inFormat + "]!!");
				}
			}

			entityCandidates = EntityCandidateExtractor.suggestCandidates(nifModel, languageParam, thresholdValue);
			
		} catch (BadRequestException e) {
			logger.error(e.getMessage());
			throw e;
		} catch (ExternalServiceFailedException e2) {
			logger.error(e2.getMessage());
			throw e2;
		}

		return entityCandidates;
	
	}
	


}
