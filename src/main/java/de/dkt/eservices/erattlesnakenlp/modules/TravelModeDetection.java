package de.dkt.eservices.erattlesnakenlp.modules;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hp.hpl.jena.rdf.model.Model;

import de.dkt.common.niftools.NIFReader;
import de.dkt.common.niftools.NIFWriter;
import de.dkt.eservices.eopennlp.EOpenNLPService;
import de.dkt.eservices.eopennlp.modules.SentenceDetector;
import eu.freme.common.conversion.rdf.RDFConstants;
import opennlp.tools.util.Span;

@Component
public class TravelModeDetection {

	HashMap<String, List<String>> dictionaries;
	
	@Autowired
	EOpenNLPService openNLPService;

	@PostConstruct
	public void initializeModels(){
		dictionaries = new HashMap<String, List<String>>();
		
		List<String> dictEN = new LinkedList<String>();
		dictEN.add("plain");
		dictEN.add("train");
		dictEN.add("car");
		dictEN.add("bus");
		dictEN.add("boat");
		dictEN.add("ship");
//		dictEN.add("");

		dictionaries.put("en", dictEN);
		
		List<String> dictDE = new LinkedList<String>();
		dictDE.add("flugzeug");
		dictDE.add("zug");
		dictDE.add("wagen");
		dictDE.add("fahrzeug");
		dictDE.add("bus");
		dictDE.add("schift");
//		dictDE.add("");

		dictionaries.put("de", dictDE);
	}
	
	public Model detectTransportationModes(Model nifModel, String languageParam, RDFConstants.RDFSerialization inFormat) throws IOException{
		try{
			String documentURI = NIFReader.extractDocumentWholeURI(nifModel);
			String inputText = NIFReader.extractIsString(nifModel).toLowerCase();
			List<String> dict = dictionaries.get(languageParam);
			for (String word : dict) {
				int counter = 0;
				while(counter!=-1){
					counter=inputText.indexOf(word, counter);
					if(counter!=-1){
						NIFWriter.addMAETransportationMode(nifModel, documentURI, word, counter, counter+word.length());
						counter++;
					}
				}
			}
//			System.out.println(NIFReader.model2String(auxModel, RDFSerialization.TURTLE));				
			return nifModel;
		}
		catch(Exception e){
			e.printStackTrace();
			return nifModel;
		}
	}

	public Model detectIndirectTransportationModes(Model nifModel, String languageParam, RDFConstants.RDFSerialization inFormat) throws IOException{
		try{
			String documentURI = NIFReader.extractDocumentWholeURI(nifModel);
			String inputText = NIFReader.extractIsString(nifModel).toLowerCase();
			Model auxModel = null;
			/**
			 * Detect sentences.
			 */
			SentenceDetector sd = new SentenceDetector();
			String sentModelName = languageParam + "-sent.bin";
			String[] sentences = sd.detectSentences(inputText, sentModelName);
			Span[] sentencesSpans = sd.detectSentenceSpans(inputText, sentModelName);

			
			/**
			 * Check the sentences and the entities that are between the sentences.
			 */
			
			
			
			
//			System.out.println(NIFReader.model2String(auxModel, RDFSerialization.TURTLE));				
			return auxModel;
		}
		catch(Exception e){
			e.printStackTrace();
			return nifModel;
		}
	}

}
