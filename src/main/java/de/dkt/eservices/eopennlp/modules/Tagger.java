package de.dkt.eservices.eopennlp.modules;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.springframework.core.io.ClassPathResource;

import com.google.common.base.Charsets;

import eu.freme.common.exception.BadRequestException;
import eu.freme.common.exception.ExternalServiceFailedException;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.postag.WordTagSampleStream;
import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.model.ModelType;

public class Tagger {

	public static String[] tagInput(String inputText, String model) throws ExternalServiceFailedException {
		String modelsDirectory = "trainedModels/";
		String result [] = new String [0];
		InputStream modelIn = null;
		ClassPathResource cpr;
		POSModel POSModel = null;
		try{
			cpr = new ClassPathResource(modelsDirectory + model);
			modelIn = new FileInputStream(cpr.getFile());
			POSModel = new POSModel(modelIn);
		}
		catch(IOException e){
			throw new BadRequestException(e.getMessage());
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
		
		POSTaggerME tagger = new POSTaggerME(POSModel);
		
		String tokens[] = Tokenizer.simpleTokenizeInput(inputText);
		result = tagger.tag(tokens);
		return result;
	}
	
	
	public static String trainModel(String inputTrainData, String modelName, String language) throws BadRequestException, ExternalServiceFailedException {
		
		/*
		 * NOTE: this method is not tested yet.
		 */
		
		String modelsDirectory = "trainedModels/";
		
		ClassPathResource trainDataCPR = new ClassPathResource(inputTrainData);

		POSModel model = null;
		InputStream dataIn = null;
		try {

			
			dataIn = new FileInputStream("en-pos.train");
			  ObjectStream<String> lineStream = new PlainTextByLineStream((InputStreamFactory) dataIn, "UTF-8");
			  ObjectStream<POSSample> sampleStream = new WordTagSampleStream(lineStream);
//		  dataIn = new FileInputStream(trainDataCPR.getPath());
//		  ObjectStream<String> lineStream =	new PlainTextByLineStream((InputStreamFactory) dataIn, "UTF-8");
//
//		  dataIn = new FileInputStream(trainDataCPR.getPath());
////		  InputStreamFactory isf = new InputStreamFactory() {
////	            public InputStream createInputStream() throws IOException {
////	                return new FileInputStream(trainDataCPR.getPath());
////	            }
////	        };
////			ObjectStream<String> lineStream = new PlainTextByLineStream(isf, Charsets.UTF_8);
//		  ObjectStream<String> lineStream =	new PlainTextByLineStream((isf, "UTF-8");
		  
		  //ObjectStream<POSSample> sampleStream = new WordTagSampleStream(lineStream);

		  model = POSTaggerME.train("en", sampleStream, null, null);
		}
		catch (IOException e) {
		  e.printStackTrace();
		}
		finally {
		  if (dataIn != null) {
		    try {
		      dataIn.close();
		    }
		    catch (IOException e) {
		      e.printStackTrace();
		    }
		  }
		}
		return modelsDirectory+modelName+".bin";
	}
	

	public static void main(String[] args) throws Exception{
		
	String testSent = "Pierre Vinken , 61 years old , will join the board as a nonexecutive director Nov. 29 ." +
			"Mr. Vinken is chairman of Elsevier N.V. , the Dutch publishing group .";
	String[] postagged = tagInput(testSent, "en-pos-maxent.bin");
	System.out.println("RESULT:" + Arrays.toString(postagged));
	
	}
	
}
