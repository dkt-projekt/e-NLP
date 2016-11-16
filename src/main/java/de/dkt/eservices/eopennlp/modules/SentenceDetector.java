package de.dkt.eservices.eopennlp.modules;

import java.io.BufferedOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

import de.dkt.common.niftools.ITSRDF;
import de.dkt.common.niftools.NIF;
import de.dkt.common.niftools.NIFTransferPrefixMapping;
import de.dkt.common.niftools.NIFUriHelper;
import eu.freme.common.exception.BadRequestException;
import eu.freme.common.exception.ExternalServiceFailedException;
import opennlp.tools.sentdetect.SentenceDetectorFactory;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.sentdetect.SentenceSample;
import opennlp.tools.sentdetect.SentenceSampleStream;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.Span;
import opennlp.tools.util.TrainingParameters;



/**
 * @author Julian Moreno Schneider julian.moreno_schneider@dfki.de
 *
 */
public class SentenceDetector {

	public static Span[] detectSentenceSpans(String inputText, String modelName) throws ExternalServiceFailedException {
		String modelsDirectory = "trainedModels" + File.separator + "sent" + File.separator;
		InputStream modelIn;
		ClassPathResource cpr = new ClassPathResource(modelsDirectory + modelName);
		Span[] sentenceSpans = null;
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
			SentenceModel model = new SentenceModel(modelIn);
			SentenceDetectorME sentenceDetector = new SentenceDetectorME(model);
			sentenceSpans = sentenceDetector.sentPosDetect(inputText);
		}
		catch (IOException e) {
			e.printStackTrace();
			throw new ExternalServiceFailedException(e.getMessage());
		}
		finally {
			if (modelIn != null) {
				try {
					modelIn.close();
					cpr=null;
				}
				catch (IOException e) {
					throw new ExternalServiceFailedException("Error closing modelInputStream.");
				}
			}
		}
		return sentenceSpans;
	}

	
	
	public static String[] detectSentences(String inputText, String modelName) throws ExternalServiceFailedException {
		String modelsDirectory = "trainedModels" + File.separator + "sent" + File.separator;
		String result [] = new String [0];
		InputStream modelIn;
		ClassPathResource cpr = new ClassPathResource(modelsDirectory + modelName);
		try{
//			modelIn = new FileInputStream("en-sent.bin");
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
			SentenceModel model = new SentenceModel(modelIn);
			SentenceDetectorME sentenceDetector = new SentenceDetectorME(model);

			//String sentences[] = sentenceDetector.sentDetect("  First sentence. Second sentence. ");
			//Span spans[] = sentenceDetector.sentPosDetect("  First sentence. Second sentence. ");
			String sentences[] = sentenceDetector.sentDetect(inputText);
			Span spans[] = sentenceDetector.sentPosDetect(inputText);

			

//			String sentences[] = sentenceDetector.sentDetect(inputText);
//			Span spans[] = sentenceDetector.sentPosDetect(inputText);
//			double probs[] = sentenceDetector.getSentenceProbabilities();

			result = sentences;
			/*
			for (String string : sentences) {
				System.out.println(string);
			}
			for (Span span : spans) {
				System.out.println(span);
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
					cpr=null;
				}
				catch (IOException e) {
				}
			}
		}
		return result;
	}

	public static ResponseEntity<String> detectSentencesNIF(String inputText, String modelName) throws ExternalServiceFailedException {
		String modelsDirectory = "trainedModels" + File.separator + "sent" + File.separator;
		InputStream modelIn;
		ClassPathResource cpr = new ClassPathResource(modelsDirectory + modelName);
		try{
//			modelIn = new FileInputStream("en-sent.bin");
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
			SentenceModel model = new SentenceModel(modelIn);
			SentenceDetectorME sentenceDetector = new SentenceDetectorME(model);

			String sentences[] = sentenceDetector.sentDetect(inputText);
			Span spans[] = sentenceDetector.sentPosDetect(inputText);
//			double probs[] = sentenceDetector.getSentenceProbabilities();
			
			for (String string : sentences) {
				System.out.println(string);
			}
			for (Span span : spans) {
				System.out.println(span);
			}

            Model outModel = ModelFactory.createDefaultModel();
//            outModel.setNsPrefix("dbpedia", "http://dbpedia.org/resource/");
//            outModel.setNsPrefix("dbpedia-de", "http://de.dbpedia.org/resource/");
//            outModel.setNsPrefix("dbpedia-nl", "http://nl.dbpedia.org/resource/");
//            outModel.setNsPrefix("dbpedia-es", "http://es.dbpedia.org/resource/");
//            outModel.setNsPrefix("dbpedia-it", "http://it.dbpedia.org/resource/");
//            outModel.setNsPrefix("dbpedia-fr", "http://fr.dbpedia.org/resource/");
//            outModel.setNsPrefix("dbpedia-ru", "http://ru.dbpedia.org/resource/");
//            outModel.setNsPrefix("dbc", "http://dbpedia.org/resource/Category:");
//            outModel.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
//            outModel.setNsPrefix("dcterms", "http://purl.org/dc/terms/");
//            outModel.setNsPrefix("freme-onto", "http://freme-project.eu/ns#");
  
            outModel.setNsPrefixes(NIFTransferPrefixMapping.getInstance());

            String documentURI = "http://example.dkt.de/doc1";
            
            int end = inputText.codePointCount(0, inputText.length());
            String documentUri = NIFUriHelper.getNifUri(documentURI, 0, end);
            
            com.hp.hpl.jena.rdf.model.Resource documentResource = outModel.createResource(documentUri);
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
            // TODO add predominant language
            // http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#predLang

            // add annotations
            int meaningId = 0;
            
            for (int i = 0; i < spans.length; i++) {
				System.out.println(spans[i].toString());
				System.out.println(sentences[i]);
                addSpan(outModel, documentResource, inputText, documentURI, spans[i].getStart(), spans[i].getEnd());
                addAnnotation(outModel, documentResource, documentURI, meaningId);
                ++meaningId;
			}
                        
//            outModel.read(new ByteArrayInputStream(output.getBytes()), null);
//            outModel.add(inModel);
            
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
					cpr=null;
				}
				catch (IOException e) {
				}
			}
		}
	}

	private static void addAnnotation(Model outModel, Resource documentResource, String documentURI, int annotationId) {
        StringBuilder uriBuilder = new StringBuilder();
        uriBuilder.append(documentURI);
        uriBuilder.append("#annotation");
        uriBuilder.append(annotationId);

        Resource annotationAsResource = outModel.createResource(uriBuilder.toString());
        outModel.add(annotationAsResource, RDF.type, NIF.Annotation);
        outModel.add(documentResource, NIF.topic, annotationAsResource);
        outModel.add(annotationAsResource, ITSRDF.taIdentRef, outModel.createResource("http://example.dkt.de/meainingUri1"));

        outModel.add(annotationAsResource, NIF.confidence,Double.toString(0), XSDDatatype.XSDstring);
	}

	private static void addSpan(Model outModel, Resource documentResource, String inputText, String documentURI,
			int start2, int end2) {
		System.out.println("Start/END positions:" + start2 + "-" + end2);
		System.out.println("inputtext length: "+inputText.length() + " inputtext: "+inputText);
        int startInJavaText = start2;
        int endInJavaText = end2;
        //int start = inputText.codePointCount(0, startInJavaText);
        //int end = start + inputText.codePointCount(startInJavaText, endInJavaText);
        int start = start2;
        int end = end2;

        String spanUri = NIFUriHelper.getNifUri(documentURI, start, end);
        Resource spanAsResource = outModel.createResource(spanUri);
        outModel.add(spanAsResource, RDF.type, NIF.String);
        outModel.add(spanAsResource, RDF.type, NIF.RFC5147String);
        // TODO add language to String
        outModel.add(spanAsResource, NIF.anchorOf,
                outModel.createTypedLiteral(inputText.substring(startInJavaText, endInJavaText), XSDDatatype.XSDstring));
        outModel.add(spanAsResource, NIF.beginIndex,
                outModel.createTypedLiteral(start, XSDDatatype.XSDnonNegativeInteger));
        outModel.add(spanAsResource, NIF.endIndex, outModel.createTypedLiteral(end, XSDDatatype.XSDnonNegativeInteger));
        outModel.add(spanAsResource, NIF.referenceContext, documentResource);

        outModel.add(spanAsResource, ITSRDF.taIdentRef, outModel.createResource("http://example.dkt.de/meainingUri1"));
        outModel.add(spanAsResource, ITSRDF.taConfidence,
        		outModel.createTypedLiteral(0, XSDDatatype.XSDdouble));
        outModel.add(spanAsResource, ITSRDF.taClassRef, outModel.createResource("http://exampledkt.de/SpanType1"));
	}

	/**
	 * 
	 * 
	 * @param inputTrainData Stream of training data
	 * @param modelName Name to be assigned to the model
	 * @return true if the model has been successfully trained
	 */
	public static String trainModel(String inputTrainData, String modelName, String language) throws BadRequestException, ExternalServiceFailedException {
		String modelsDirectory = "trained_models/";
		
		Charset charset;				
		ObjectStream<String> lineStream;
		ObjectStream<SentenceSample> sampleStream;
		ClassPathResource trainDataCPR = new ClassPathResource(inputTrainData);
		try{
			charset = Charset.forName("UTF-8");				
			lineStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(trainDataCPR.getFile()), charset);
			sampleStream = new SentenceSampleStream(lineStream);

			SentenceModel model;
			try {
				model = SentenceDetectorME.train("en", sampleStream, new SentenceDetectorFactory(language, true, null, null), TrainingParameters.defaultParams());
			}
			finally {
				sampleStream.close();
			}
	
			OutputStream modelOut = null;
			try {
				modelOut = new BufferedOutputStream(new FileOutputStream(modelsDirectory+modelName+".bin"));
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
//		ResponseEntity<String> result = SentenceDetector.detectSentencesNIF("  First sentence. Second sentence. ", "en-sent.bin");
//		System.out.println(result.toString());

		
	}
}
