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
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.postag.WordTagSampleStream;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;

/**
 * @author Julian Moreno Schneider julian.moreno_schneider@dfki.de, Peter Bourgonje peter.bourgonje@dfki.de
 *
 */
public class PartOfSpeechTagging {
	
	public static String[] tagTokens(String[] tokens, String modelName, String languageParam) throws ExternalServiceFailedException {
		String modelsDirectory = "trainedModels/";
		String sentModel = languageParam + "-sent.bin";
		InputStream modelIn;
		ClassPathResource cpr = new ClassPathResource(modelsDirectory + languageParam + "-" + modelName);
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

			POSModel model = new POSModel(modelIn);				
			POSTaggerME tagger = new POSTaggerME(model);
						
			//String sent[] = SentenceDetector.detectSentences(inputText, sentModel);
			//String tags[] = tagger.tag(sent);
			String tags[] = tagger.tag(tokens);
						
			double probs[] = tagger.probs();
						
			//Retrieve the n-best pos tag sequences and not only the best sequence.
			//The sequence can be retrieved via Sequence.getOutcomes() which returns a tags array and Sequence.getProbs() returns the probability array for this sequence.
			//Sequence topSequences[] = tagger.topKSequences(sent);
			
			return tags;
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

	public static ResponseEntity<String> parseTextNIF(String inputText, String modelName, String language) throws ExternalServiceFailedException {
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

			POSModel model = new POSModel(modelIn);				
			POSTaggerME tagger = new POSTaggerME(model);
						
			String sent[]=null;// = new String[]{"Most", "large", "cities", "in", "the", "US", "had", "morning", "and", "afternoon", "newspapers", "."};
			//TODO extract tokens from NIF input. 
			if(sent==null){
				//If there are no tokens, taken text and split it based on " " and ".".
				sent = inputText.replace(".", " .").split(" ");
			}
			
			String tags[] = tagger.tag(sent);
			double probs[] = tagger.probs();
			
			//Retrieve the n-best pos tag sequences and not only the best sequence.
			//The sequence can be retrieved via Sequence.getOutcomes() which returns a tags array and Sequence.getProbs() returns the probability array for this sequence.
			//Sequence topSequences[] = tagger.topKSequences(sent);
			
            Model outModel = ModelFactory.createDefaultModel();            
            outModel.setNsPrefixes(NIFTransferPrefixMapping.getInstance());

            String documentURI = "http://example.dkt.de/partOfSpeechText1";
            
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
            for (int i = 0; i < tags.length; i++) {
				System.out.println(tags[i]);
//                NIFWriter.addSpan(outModel, documentResource, inputText, documentURI, spans[i].getStart(), spans[i].getEnd());
                NIFWriter.addAnnotation(outModel, documentResource, documentURI, meaningId, "#PartOfSppechTag");
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
		ObjectStream<POSSample> sampleStream;
		ClassPathResource trainDataCPR = new ClassPathResource(inputTrainData);
		
		
		POSModel model = null;
		try{
			charset = Charset.forName("UTF-8");
			lineStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(trainDataCPR.getFile()), charset);
			sampleStream = new WordTagSampleStream(lineStream);

			try {
				model = POSTaggerME.train("en", sampleStream, TrainingParameters.defaultParams(), null);				
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
		ResponseEntity<String> result = PartOfSpeechTagging.parseTextNIF("  First sentence. Second sentence. ", "en-sent.bin", "en");
		System.out.println(result.toString());
	}
}
