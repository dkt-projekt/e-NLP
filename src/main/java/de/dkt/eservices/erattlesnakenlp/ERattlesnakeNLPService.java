package de.dkt.eservices.erattlesnakenlp;

import de.dkt.common.niftools.DKTNIF;
import de.dkt.common.niftools.NIFReader;
import de.dkt.common.niftools.NIFWriter;
import de.dkt.common.tools.FileReadUtilities;
import de.dkt.eservices.erattlesnakenlp.modules.LanguageIdentificator;
import de.dkt.eservices.erattlesnakenlp.modules.ParagraphDetector;
import eu.freme.common.conversion.rdf.RDFConstants;
import eu.freme.common.exception.BadRequestException;

import java.io.IOException;

import org.apache.log4j.Logger;
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
	

}
