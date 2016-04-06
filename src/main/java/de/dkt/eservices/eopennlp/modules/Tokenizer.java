package de.dkt.eservices.eopennlp.modules;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Date;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

import de.dkt.common.niftools.NIF;
import de.dkt.common.niftools.NIFTransferPrefixMapping;
import de.dkt.common.niftools.NIFUriHelper;
import de.dkt.common.niftools.NIFWriter;
import eu.freme.common.exception.BadRequestException;
import eu.freme.common.exception.ExternalServiceFailedException;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.tokenize.TokenSample;
import opennlp.tools.tokenize.TokenSampleStream;
import opennlp.tools.tokenize.TokenizerFactory;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.Span;
import opennlp.tools.util.TrainingParameters;

/**
 * @author Julian Moreno Schneider julian.moreno_schneider@dfki.de
 *
 */
public class Tokenizer {
	

	
	// almost duplicate of the one below, but this one should keep token indices stored somewhere
	public static Span[] tokenizeIndices(String inputText, String modelName) throws ExternalServiceFailedException {
		String modelsDirectory = "trainedModels/";
		Span[] tokens;
		//String result [] = new String [0];
		InputStream modelIn;
		ClassPathResource cpr;
		try{
			//cpr = new ClassPathResource(modelsDirectory + "en-token.bin");
			cpr = new ClassPathResource(modelsDirectory + modelName);
			modelIn = new FileInputStream(cpr.getFile());
//			modelIn = new FileInputStream(modelsDirectory + modelName + ".bin");
		}
		catch(FileNotFoundException e){
			throw new BadRequestException(e.getMessage());
		}
		catch(IOException e){
			throw new BadRequestException(e.getMessage());
		}
		try {
			TokenizerModel model = new TokenizerModel(modelIn);
			TokenizerME tokenizer= new TokenizerME(model);

			tokens = tokenizer.tokenizePos(inputText);
			
			//result = tokens;

		}
		catch (IOException e) {
			e.printStackTrace();
			throw new ExternalServiceFailedException(e.getMessage());
		}
		finally {
			if (modelIn != null) {
				try {
					modelIn.close();
				}
				catch (IOException e) {
				}
			}
		}
		return tokens;
	}
	
	public static String[] tokenizeInput(String inputText, String modelName) throws ExternalServiceFailedException {
		String modelsDirectory = "trainedModels/";
		String result [] = new String [0];
		InputStream modelIn;
		ClassPathResource cpr;
		try{
//			cpr = new ClassPathResource(modelsDirectory + "en-token.bin");
			cpr = new ClassPathResource(modelsDirectory + modelName);
			modelIn = new FileInputStream(cpr.getFile());
//			modelIn = new FileInputStream(modelsDirectory + modelName + ".bin");
		}
		catch(FileNotFoundException e){
			throw new BadRequestException(e.getMessage());
		}
		catch(IOException e){
			throw new BadRequestException(e.getMessage());
		}
		try {
			TokenizerModel model = new TokenizerModel(modelIn);
			TokenizerME tokenizer= new TokenizerME(model);
			
			String tokens[] = tokenizer.tokenize(inputText);
			
			result = tokens;
			/*
			for (String string : tokens) {
				System.out.println(string);
			}
			*/
		}
		catch (IOException e) {
			e.printStackTrace();
			throw new ExternalServiceFailedException(e.getMessage());
		}
		finally {
			if (modelIn != null) {
				try {
					modelIn.close();
				}
				catch (IOException e) {
				}
			}
		}
		return result;
	}
	
	public static Span[] simpleTokenizeIndices(String inputText) throws ExternalServiceFailedException {
		SimpleTokenizer tokenizer= new SimpleTokenizer();
		Span[] tokens = tokenizer.tokenizePos(inputText);
		return tokens;
	}

	public static String[] simpleTokenizeInput(String inputText) throws ExternalServiceFailedException {
		SimpleTokenizer tokenizer= new SimpleTokenizer();
		String[] tokens = tokenizer.tokenize(inputText);
		return tokens;
	}
	
	//	public static String[] tokenizeText(String inputText, String modelName) throws ExternalServiceFailedException {
//		String modelsDirectory = "trainedModels/";
//		InputStream modelIn;
//		ClassPathResource cpr = new ClassPathResource(modelsDirectory + modelName);
//		try{
//			modelIn = new FileInputStream(cpr.getFile());
//		}
//		catch(FileNotFoundException e){
//			throw new BadRequestException(e.getMessage());
//		}
//		catch(IOException e){
//			throw new BadRequestException(e.getMessage());
//		}
//		try {
//			
//			TokenizerModel model = new TokenizerModel(modelIn);
//			
//			TokenizerME tokenizer = new TokenizerME(model);
//			 
//			String tokens[] = tokenizer.tokenize(inputText);
//			Span spans[] = tokenizer.tokenizePos(inputText);		
//						
////			The tokenSpans array now contain 5 elements. To get the text for one span call Span.getCoveredText which takes a span and the input text. The TokenizerME is able to output the probabilities for the detected tokens. The getTokenProbabilities method must be called directly after one of the tokenize methods was called.
//
//			double probs[] = tokenizer.getTokenProbabilities();
//			
//			return tokens;
//		}
//		catch (IOException e) {
//			e.printStackTrace();
//			throw new ExternalServiceFailedException(e.getMessage());
//		}
//		finally {
//			if (modelIn != null) {
//				try {
//					modelIn.close();
//				}
//				catch (IOException e) {
//				}
//			}
//		}
//	}

	public static ResponseEntity<String> tokenizeTextNIF(String inputText, String modelName, String language) throws ExternalServiceFailedException {
		String modelsDirectory = "trainedModels/";
		InputStream modelIn;
		ClassPathResource cpr = new ClassPathResource(modelsDirectory + modelName);
		try{
			modelIn = new FileInputStream(cpr.getFile());
		}
		catch(FileNotFoundException e){
			throw new BadRequestException(e.getMessage());
		}
		catch(IOException e){
			throw new BadRequestException(e.getMessage());
		}
		try {
			
			TokenizerModel model = new TokenizerModel(modelIn);
			TokenizerME tokenizer = new TokenizerME(model);
			 
			String tokens[] = tokenizer.tokenize(inputText);
			Span spans[] = tokenizer.tokenizePos(inputText);
			double probs[] = tokenizer.getTokenProbabilities();

            Model outModel = ModelFactory.createDefaultModel();
  
            outModel.setNsPrefixes(NIFTransferPrefixMapping.getInstance());

            String documentURI = "http://example.dkt.de/tokenizerText1";
            
            int end = inputText.codePointCount(0, inputText.length());
            String documentUri = NIFUriHelper.getNifUri(documentURI, 0, end);
            
            Resource documentResource = outModel.createResource(documentUri);
            outModel.add(documentResource, RDF.type, NIF.Context);
            outModel.add(documentResource, RDF.type, NIF.String);
            outModel.add(documentResource, RDF.type, NIF.RFC5147String);
            // TODO add language to String
            outModel.add(documentResource, NIF.isString,
                    outModel.createTypedLiteral(inputText, XSDDatatype.XSDstring));
            outModel.add(documentResource, NIF.beginIndex,
                    outModel.createTypedLiteral(0, XSDDatatype.XSDnonNegativeInteger));
            outModel.add(documentResource, NIF.endIndex,
                    outModel.createTypedLiteral(end, XSDDatatype.XSDnonNegativeInteger));
            // add annotations
            int meaningId = 0;
            
            for (int i = 0; i < spans.length; i++) {
				System.out.println(spans[i].toString());
				System.out.println(tokens[i]);
                NIFWriter.addSpan(outModel, documentResource, inputText, documentURI, spans[i].getStart(), spans[i].getEnd());
                NIFWriter.addAnnotation(outModel, documentResource, documentURI, meaningId);
                ++meaningId;
			}
                        
            System.out.println(outModel.toString());
            
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.add("Content-Type", "RDF/XML");
            
    		StringWriter writer = new StringWriter();
    		outModel.write(writer, "RDF/XML");
    		try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
    		String rdfString = writer.toString();

           	return new ResponseEntity<String>(rdfString, responseHeaders, HttpStatus.OK);
		}
		catch (IOException e) {
			e.printStackTrace();
			throw new ExternalServiceFailedException(e.getMessage());
		}
		finally {
			if (modelIn != null) {
				try {
					modelIn.close();
				}
				catch (IOException e) {
				}
			}
		}
	}

	/**
	 * 
	 * 
	 * @param inputTrainData Stream of training data
	 * @param modelName Name to be assigned to the model
	 * @return true if the model has been successfully trained
	 */
	public static String trainModel(String inputTrainData, String modelName, String language) throws BadRequestException, ExternalServiceFailedException {
		String modelsDirectory = "trainedModels/";
		
		Charset charset;				
		ObjectStream<String> lineStream;
		ObjectStream<TokenSample> sampleStream;
		ClassPathResource trainDataCPR = new ClassPathResource(inputTrainData);
		try{
			charset = Charset.forName("UTF-8");
			lineStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(trainDataCPR.getFile()), charset);
			sampleStream = new TokenSampleStream(lineStream);

			TokenizerModel model;

			try {
				model = TokenizerME.train(sampleStream, new TokenizerFactory(language, null, false, null), TrainingParameters.defaultParams());
			}
			finally {
				sampleStream.close();
			}

			OutputStream modelOut = null;
			try {
				ClassPathResource cprOut = new ClassPathResource(modelsDirectory+language + "-" + modelName+".bin");
				modelOut = new BufferedOutputStream(new FileOutputStream(cprOut.getFile()));
				model.serialize(modelOut);
			} finally {
				if (modelOut != null) 
					modelOut.close();      
			}
		}
		catch(FileNotFoundException e){
			throw new BadRequestException(e.getMessage());
		}
		catch(IOException e){
			throw new ExternalServiceFailedException(e.getMessage());
		}

		return modelsDirectory+modelName+".bin";
	}

	public static void main(String[] args) throws Exception{
		
	Date before = new Date();
	String testSent = "This is just a test sentence.";
	for (int i = 0; i < 1000; i++){
		simpleTokenizeIndices(testSent);
	}
	Date after = new Date();
	System.out.println((after.getTime()-before.getTime()) +" miliseconds for tokenization.");
	
	}
}
