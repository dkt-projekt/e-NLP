package de.dkt.eservices.erattlesnakenlp.modules;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import de.dkt.common.niftools.NIFReader;
import de.dkt.common.niftools.NIFWriter;
import de.dkt.common.tools.FileReadUtilities;

public class ParagraphDetector {

	public static ResponseEntity<String> detectParagraphsNIF(Model nifModel) {
		/*
		 * NOTE:  for now this is ridiculously simple; just finding the indices of newlines in the string
		 */
		String str = NIFReader.extractIsString(nifModel);
		
		//System.out.println("DEBUGGING whole string:" + str);
		int beginIndex = 0;
		//System.out.println("DEBUGGINg str length:" + str.length());
		List<Span> spanList = new ArrayList<Span>();
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) == '\n'){
				//System.out.println("DEBUGGING paragraph found from to:" + str.substring(beginIndex, i) + beginIndex + "|"+  i);
				Span parSpan = new Span();
				parSpan.setBegin(beginIndex);
				parSpan.setEnd(i);
				spanList.add(parSpan);
				beginIndex = i;
			}
		}
		
		for (Span s : spanList){
			if (!str.substring(s.getBegin(), s.getEnd()).matches("^\\s+$")){
				NIFWriter.addParagraphEntity(nifModel, s.getBegin(), s.getEnd());
			}
		}
		
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "RDF/XML");
            
    	StringWriter writer = new StringWriter();
    	nifModel.write(writer, "RDF/XML");
    	try {
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	String rdfString = writer.toString();
        return new ResponseEntity<String>(rdfString, responseHeaders, HttpStatus.OK);
	}

	public static void main(String[] args) throws Exception {
		String inputFile = "C:\\Users\\pebo01\\Desktop\\nietzscheBio.txt";
		String inputText = FileReadUtilities.readFile2String(inputFile);
		
		Model nifModel = ModelFactory.createDefaultModel();
		NIFWriter.addInitialString(nifModel, inputText, "http://dkt.dfki.de/examples/");
		detectParagraphsNIF(nifModel);
	}
	
}
