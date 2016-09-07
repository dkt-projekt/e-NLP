//package de.dkt.eservices.esargraph;
//
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.OutputStream;
//import java.io.OutputStreamWriter;
//import java.io.PrintWriter;
//import java.nio.charset.Charset;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//import org.json.JSONObject;
//import org.springframework.stereotype.Component;
//
//import com.hp.hpl.jena.rdf.model.Model;
//import com.hp.hpl.jena.sparql.engine.optimizer.Pattern;
//
//import de.dfki.lt.dare.element.AnnotatedSentence;
//import de.dfki.lt.dare.element.Mention;
//import de.dfki.lt.dare.element.RelationMention;
//import de.dkt.eservices.esargraph.modules.element.RelationGraphIndexer;
//import de.dfki.lt.dare.element.io.XmlBatchReader;
//import de.dfki.lt.dare.element.io.XmlBatchReaderFactory;
//import de.dfki.lt.dare.element.io.XmlBatchWriter;
//import de.dfki.lt.dare.element.io.XmlBatchWriterFactory;
//import de.dfki.lt.dare.element.io.XmlIOUtils;
//import de.dfki.lt.dare.element.io.XmlZipSaver;
//import de.dfki.lt.dare.element.io.XmlZipSaverFactory;
//import de.dfki.lt.dare.gui.pattern.Pattern2ImageFileTransformer;
//import de.dkt.eservices.esargraph.modules.element.DefaultRelationValidatorFactory;
//import de.dkt.eservices.esargraph.modules.element.RelationValidator;
//import de.dfki.lt.dare.pattern.RePattern;
//import de.dkt.eservices.esargraph.modules.re.ExtractedRelationMention;
//import de.dkt.eservices.esargraph.modules.re.SentenceIndexSearcher;
//import de.dkt.eservices.esargraph.modules.re.SimplePatternMatcher;
//import edu.stanford.nlp.patterns.PatternsAnnotations.ProcessedTextAnnotation;
//import de.dfki.lt.dare.sargraph2.element.xml.XmlSerializableSarGraph;
//import de.dfki.lt.dare.util.DareUtils;
//import de.dfki.lt.dare.util.io.ObjectZipLoader;
//import de.dfki.lt.dare.util.io.ObjectZipSaver;
//import de.dkt.common.filemanagement.FileFactory;
//import de.dkt.common.niftools.DKTNIF;
//import de.dkt.common.niftools.NIFReader;
//import de.dkt.common.niftools.NIFWriter;
//import de.dkt.eservices.esargraph.modules.experiment_process.PatternSemanticKAdder;
//import de.dkt.eservices.esargraph.modules.experiment_process.Postprocessor;
//import de.dkt.eservices.esargraph.modules.experiment_process.Preprocessor;
//import eu.freme.common.conversion.rdf.RDFConstants;
//import eu.freme.common.conversion.rdf.RDFConstants.RDFSerialization;
//import eu.freme.common.exception.BadRequestException;
//import eu.freme.common.exception.ExternalServiceFailedException;
//
//@Component
//public class ESargraphService {
//	
//	// the stuff in here is being overwritten every time processData is called (hence it all resides in the dareTempFiles)
//	private static final String DARE_SENTENCE_ID = "dare_sentence_ids.txt";
//	private static final String DARE_SENTENCE_PLAIN = "dare_sentence_plain.txt";
//	private static final String DARE_SENTENCE_IN_XML = "dare_sentence_xml.zip";
//	private static final String DARE_SENTENCE = "dare_sentence.zip";
//	private static final String DARE_SENTENCE_DPINDEXER = "dare_sentence_dp_indexer";
//	private static final String dareTempFolder = "dareTempFiles";
//	public static final String RELATION_VALIDATOR_CONFIG = "de" + File.separator + "dfki" + File.separator + "lt" + File.separator + "dare" + File.separator +  "adapter" + File.separator + "element" + File.separator + "extendedFBRelations2.xml";
//	private static final String darePatterns = "darePatterns";
//    
//	static String readFile(String path, Charset encoding) 
//			  throws IOException 
//			{
//			  byte[] encoded = Files.readAllBytes(Paths.get(path));
//			  return new String(encoded, encoding);
//			}
//	
//	public static void main(String[] args) throws IOException {
//		ESargraphService ess = new ESargraphService();
//		
////		String sampleText = //"On May 3, 2003 , LeBlanc married Melissa McKnight.\n" +
////				//"Google Earth is a virtual globe, map and geographic information program that was originally called Earth Viewer, and was created by Keyhole, Inc, a company acquired by Google in 2004.\n" +
////				//"FileNet , a company acquired by IBM , developed software to help enterprises manage their content and business processes .\n" +
////				"Sample sentence here.\n" +
////				"Google Earth - wiki.GIS.com Google Earth is a virtual globe, map and geographic information program that was originally called Earth Viewer, and was created by Keyhole, Inc, a company acquired by Google in 2004."; 
////		ess.processData(sampleText, "en", RDFSerialization.PLAINTEXT);
//		
//		HashMap<String,HashMap<String,HashMap<String,Integer>>> hm = new HashMap<String,HashMap<String,HashMap<String,Integer>>>();
//		String docFolder = "C:\\Users\\pebo01\\Desktop\\data\\ARTCOM\\DKT_Texte_AC\\DKT_Texte_AC\\Vikings\\Wikipedia\\TXT\\textfilesonly";
//		File df = new File(docFolder);
//		for (File f : df.listFiles()){
//			System.out.println("Processing file:" + f.getName());
//			String fileContent = readFile(f.getAbsolutePath(), StandardCharsets.UTF_8);
//			hm = processDataAndReturnHashMapForJSONStructure(hm, fileContent, "en", RDFSerialization.PLAINTEXT);
//		}
//		
//
//    	// write to json map to output file here
//    	PrintWriter out = new PrintWriter(new File("C:\\Users\\pebo01\\Desktop", "debug.txt"));
//    	JSONObject jsonMap = new JSONObject(hm);
//		out.write(jsonMap.toString(4));
//		out.close();
//		
//	}
//	
//    public Model processData(String textToProcess, String languageParam, RDFSerialization inFormat) throws ExternalServiceFailedException, BadRequestException {
//
//    	Model nifModel = null;
//    	if (inFormat.equals(RDFConstants.RDFSerialization.PLAINTEXT)){
//			nifModel = NIFWriter.initializeOutputModel();
//			NIFWriter.addInitialString(nifModel, textToProcess, DKTNIF.getDefaultPrefix());
//		}
//		else {
//			try{
//				nifModel = NIFReader.extractModelFromFormatString(textToProcess,inFormat);
//			}
//			catch(Exception e){
//				throw new BadRequestException("Check the input format ["+inFormat+"]!!");
//			}
//		}
//    	
//		try {
//			
//
//			String nifIsString = NIFReader.extractIsString(nifModel);
//			Preprocessor pfpt = new Preprocessor();
//			FileWriter sentenceIdWriter = new FileWriter(new File(dareTempFolder, DARE_SENTENCE_ID));
//			//FileWriter sentenceIdWriter = new FileWriter(FileFactory.generateOrCreateFileInstance(dareTempFolder + File.separator + DARE_SENTENCE_ID));//new File(dareTempFolder, DARE_SENTENCE_ID));
//			XmlZipSaver<AnnotatedSentence> xmlZipSaver = XmlZipSaverFactory.getSaver(new File(dareTempFolder, DARE_SENTENCE_IN_XML));
//			ObjectZipSaver<AnnotatedSentence> objectZipSaver = new ObjectZipSaver<>(new File(dareTempFolder, DARE_SENTENCE));
//			BufferedWriter plainSentenceWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(dareTempFolder, DARE_SENTENCE_PLAIN)), DareUtils.DEFAULT_ENCODING));
//			RelationGraphIndexer dependencyParseIndexer = new RelationGraphIndexer(new File(dareTempFolder, DARE_SENTENCE_DPINDEXER));
//			
//			try {
//				List<AnnotatedSentence> annotatedSentences = pfpt.convertDocument(nifIsString, NIFReader.extractDocumentURI(nifModel)); // not sure if it makes sense to give docURI as identifier here
//				//just keep them as a list and proceed
//				for (AnnotatedSentence sent : annotatedSentences) {
//					xmlZipSaver.save(sent.getId() + ".xml", sent);
//					objectZipSaver.save(sent.getId(), sent);
//					sentenceIdWriter.write(sent.getId() + DareUtils.LINE);
//					plainSentenceWriter.write("[" + sent.getId() + "] " + sent.getText() + DareUtils.LINE);
//					dependencyParseIndexer.index(sent);
//				}
//				
//				SimplePatternMatcher matcher = new SimplePatternMatcher();
//				//RelationValidator rv = DefaultRelationValidatorFactory.getValidator(RELATION_VALIDATOR_CONFIG);
//				// hardcoded for debugging:
//				RelationValidator rv = DefaultRelationValidatorFactory.getValidator("C:\\Users\\pebo01\\workspace\\e-NLP\\src\\main\\resources\\de\\dfki\\lt\\dare\\element\\extendedFBRelations2.xml");
//			    System.currentTimeMillis();
//			    Set<String> nfoRelevantSentences = new HashSet<>();
//
//			    
//			    
//			    
//			    
//				for (AnnotatedSentence as : annotatedSentences) {
//
//					// for (File ruleDir : new File(darePatterns).listFiles()) {
//					for (File ruleDir : new File(
//							"C:\\Users\\pebo01\\workspace\\e-NLP\\src\\main\\resources\\darePatterns").listFiles()) { // for
//																														// debugging
//																														// only
//						try (XmlBatchReader<RePattern> patternReader = XmlBatchReaderFactory.getReader(RePattern.class,
//								ruleDir)) {
//							List<RePattern> patterns;
//							while (null != (patterns = patternReader.read())) {
//								
//								long max = 0;
//								HashMap<Long, Integer> m1 = new HashMap<Long, Integer>();
//								for (RePattern pattern : patterns){
//									// NOTE since erm.getScore (later on) or any of the others didn't really return useful results, I'm just using the number of learning tracks to decide which unique relation we take for a sentence.
//									// This depends on the pattern, not on the individual sentence, I guess. May not be brilliant, but I want to filter the resulting relations a bit.
//									if (pattern.getLearningTracks().size() > max){
//										max = pattern.getLearningTracks().size();
//									}
//									m1.put(pattern.getId(), pattern.getLearningTracks().size());
//								}
//								
//								for (RePattern pattern : patterns) {
//									int tmp;
//									try {
//										tmp = Integer.parseInt(pattern.getFeatureSet()
//												.get(PatternSemanticKAdder.PATTERN_FEATURENAME_KVALUE).get(0));
//									} catch (Exception e) {
//										tmp = -1;
//									}
//									final int kValue = tmp;
//									final int freq = pattern.getLearningTracks().size();
//
//									nfoRelevantSentences.add(as.getId());
//									List<ExtractedRelationMention> erms = matcher.apply(as, pattern, rv);
//									if (erms == null || erms.isEmpty())
//										continue;
//									if (m1.get(pattern.getId()) == max){
//										
//										
//										for (RelationMention erm : erms) {
//											if (kValue != -1)
//												erm.getFeatures().addFeature(Postprocessor.ERM_FEATURE_KVALUE,
//														kValue + "");
//											erm.getFeatures().addFeature(Postprocessor.ERM_FEATURE_FREQUENCY,
//													freq + "");
//											erm.getFeatures().addFeature(Postprocessor.ERM_FEATURE_OFFSET,
//													as.getStart() + "");
//											String relationType = pattern.getRelationType();
//											HashMap<String, List<Integer>> argIndices = new HashMap<String, List<Integer>>();
//											HashMap<String, String> argAnchor = new HashMap<String, String>();
//											String relationTypeForJSON = erm.getType();
//											String relationSubject = null;
//											String relationObject = null;
//											for (String argName : erm.getArgumentNames()) {
//												
////												System.out.println("Argument name:" + argName);
////												System.out.println("erm type:" + erm.getType());
////												System.out.println("Argument value:" + erm.getArgument(argName));
//												Set<Mention> argSet = erm.getArgument(argName);
//												for (Mention m : argSet) {
//													List<Integer> indices = new ArrayList<>(
//															Arrays.asList(m.getStart(), m.getEnd()));
//													argIndices.put(argName, indices);
//													argAnchor.put(argName, m.getText());
////													System.out.println("text:" + m.getText());
////													System.out.println(m.getInstanceId()); //TODO: assuming here that instanceId refers to arity and that the arg with lowest id is the subject/active/agent of the relation. Check this!
//													if (m.getInstanceId() == erm.getArity()){
//														relationObject = m.getText();
//													}
//													else{
//														relationSubject = m.getText();
//													}
//												}
//												
//											}
//											
//											
//											NIFWriter.addDareRelationAnnotation(nifModel, as.getStart(), as.getEnd(),
//													argIndices, argAnchor, relationType);
//										}
//
//									}
//								}
//							}
//						}
//					}
//				}
//			    
//			   	    
//				
//			} catch (Exception | Error e) {
//				System.out.println("ERROR!!!");
//				e.printStackTrace();
//			}
//            //System.out.println("NIFMODEL:\n" + NIFReader.model2String(nifModel, RDFSerialization.TURTLE));
//        	
//        	sentenceIdWriter.close();
//        	objectZipSaver.close();
//        	plainSentenceWriter.close();
//        	dependencyParseIndexer.close();
//        	        	
//            return nifModel;
//        } catch (Exception e) {
//        	//e.printStackTrace();
//            throw new ExternalServiceFailedException(e.getMessage());
//        }
//    }
//
//    public static HashMap<String,HashMap<String,HashMap<String,Integer>>> processDataAndReturnHashMapForJSONStructure(HashMap<String,HashMap<String,HashMap<String,Integer>>> hm , String textToProcess, String languageParam, RDFSerialization inFormat) throws ExternalServiceFailedException, BadRequestException {
//
//    	 
//    	Model nifModel = null;
//    	if (inFormat.equals(RDFConstants.RDFSerialization.PLAINTEXT)){
//			nifModel = NIFWriter.initializeOutputModel();
//			NIFWriter.addInitialString(nifModel, textToProcess, DKTNIF.getDefaultPrefix());
//		}
//		else {
//			try{
//				nifModel = NIFReader.extractModelFromFormatString(textToProcess,inFormat);
//			}
//			catch(Exception e){
//				throw new BadRequestException("Check the input format ["+inFormat+"]!!");
//			}
//		}
//    	
//		try {
//			
//
//			String nifIsString = NIFReader.extractIsString(nifModel);
//			Preprocessor pfpt = new Preprocessor();
//			FileWriter sentenceIdWriter = new FileWriter(new File(dareTempFolder, DARE_SENTENCE_ID));
//			//FileWriter sentenceIdWriter = new FileWriter(FileFactory.generateOrCreateFileInstance(dareTempFolder + File.separator + DARE_SENTENCE_ID));//new File(dareTempFolder, DARE_SENTENCE_ID));
//			XmlZipSaver<AnnotatedSentence> xmlZipSaver = XmlZipSaverFactory.getSaver(new File(dareTempFolder, DARE_SENTENCE_IN_XML));
//			ObjectZipSaver<AnnotatedSentence> objectZipSaver = new ObjectZipSaver<>(new File(dareTempFolder, DARE_SENTENCE));
//			BufferedWriter plainSentenceWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(dareTempFolder, DARE_SENTENCE_PLAIN)), DareUtils.DEFAULT_ENCODING));
//			RelationGraphIndexer dependencyParseIndexer = new RelationGraphIndexer(new File(dareTempFolder, DARE_SENTENCE_DPINDEXER));
//			
//			try {
//				List<AnnotatedSentence> annotatedSentences = pfpt.convertDocument(nifIsString, NIFReader.extractDocumentURI(nifModel)); // not sure if it makes sense to give docURI as identifier here
//				//just keep them as a list and proceed
//				for (AnnotatedSentence sent : annotatedSentences) {
//					xmlZipSaver.save(sent.getId() + ".xml", sent);
//					objectZipSaver.save(sent.getId(), sent);
//					sentenceIdWriter.write(sent.getId() + DareUtils.LINE);
//					plainSentenceWriter.write("[" + sent.getId() + "] " + sent.getText() + DareUtils.LINE);
//					dependencyParseIndexer.index(sent);
//				}
//				
//				SimplePatternMatcher matcher = new SimplePatternMatcher();
//				//RelationValidator rv = DefaultRelationValidatorFactory.getValidator(RELATION_VALIDATOR_CONFIG);
//				// hardcoded for debugging:
//				RelationValidator rv = DefaultRelationValidatorFactory.getValidator("C:\\Users\\pebo01\\workspace\\e-NLP\\src\\main\\resources\\de\\dfki\\lt\\dare\\element\\extendedFBRelations2.xml");
//			    System.currentTimeMillis();
//			    Set<String> nfoRelevantSentences = new HashSet<>();
//
//			    
//			    
//			    
//			    
//				for (AnnotatedSentence as : annotatedSentences) {
//
//					// for (File ruleDir : new File(darePatterns).listFiles()) {
//					for (File ruleDir : new File(
//							"C:\\Users\\pebo01\\workspace\\e-NLP\\src\\main\\resources\\darePatterns").listFiles()) { // for
//																														// debugging
//																														// only
//						try (XmlBatchReader<RePattern> patternReader = XmlBatchReaderFactory.getReader(RePattern.class,
//								ruleDir)) {
//							List<RePattern> patterns;
//							while (null != (patterns = patternReader.read())) {
//								
//								long max = 0;
//								HashMap<Long, Integer> m1 = new HashMap<Long, Integer>();
//								for (RePattern pattern : patterns){
//									// NOTE since erm.getScore (later on) or any of the others didn't really return useful results, I'm just using the number of learning tracks to decide which unique relation we take for a sentence.
//									// This depends on the pattern, not on the individual sentence, I guess. May not be brilliant, but I want to filter the resulting relations a bit.
//									if (pattern.getLearningTracks().size() > max){
//										max = pattern.getLearningTracks().size();
//									}
//									m1.put(pattern.getId(), pattern.getLearningTracks().size());
//								}
//								
//								for (RePattern pattern : patterns) {
//									int tmp;
//									try {
//										tmp = Integer.parseInt(pattern.getFeatureSet()
//												.get(PatternSemanticKAdder.PATTERN_FEATURENAME_KVALUE).get(0));
//									} catch (Exception e) {
//										tmp = -1;
//									}
//									final int kValue = tmp;
//									final int freq = pattern.getLearningTracks().size();
//
//									nfoRelevantSentences.add(as.getId());
//									List<ExtractedRelationMention> erms = matcher.apply(as, pattern, rv);
//									if (erms == null || erms.isEmpty())
//										continue;
//									if (m1.get(pattern.getId()) == max){
//										
//										
//										for (RelationMention erm : erms) {
//											if (kValue != -1)
//												erm.getFeatures().addFeature(Postprocessor.ERM_FEATURE_KVALUE,
//														kValue + "");
//											erm.getFeatures().addFeature(Postprocessor.ERM_FEATURE_FREQUENCY,
//													freq + "");
//											erm.getFeatures().addFeature(Postprocessor.ERM_FEATURE_OFFSET,
//													as.getStart() + "");
//											String relationType = pattern.getRelationType();
//											HashMap<String, List<Integer>> argIndices = new HashMap<String, List<Integer>>();
//											HashMap<String, String> argAnchor = new HashMap<String, String>();
//											String relationTypeForJSON = erm.getType();
//											String relationSubject = null;
//											String relationObject = null;
//											//TODO WARNING: I'm afraid that this here will only put one instance of a found relation in here. It should be one level lower I think, but not sure, debug.
//											for (String argName : erm.getArgumentNames()) {
//												
////												System.out.println("Argument name:" + argName);
////												System.out.println("erm type:" + erm.getType());
////												System.out.println("Argument value:" + erm.getArgument(argName));
//												Set<Mention> argSet = erm.getArgument(argName);
//												for (Mention m : argSet) {
//													List<Integer> indices = new ArrayList<>(
//															Arrays.asList(m.getStart(), m.getEnd()));
//													argIndices.put(argName, indices);
//													argAnchor.put(argName, m.getText());
////													System.out.println("text:" + m.getText());
////													System.out.println(m.getInstanceId()); //TODO: assuming here that instanceId refers to arity and that the arg with lowest id is the subject/active/agent of the relation. Check this!
//													if (m.getInstanceId() == erm.getArity()){
//														relationObject = m.getText();
//													}
//													else{
//														relationSubject = m.getText();
//													}
//												}
//												
//											}
//											
//											if (hm.containsKey(relationSubject)){
//												HashMap<String, HashMap<String, Integer>> relMap = hm.get(relationSubject);
//												if (relMap.containsKey(relationTypeForJSON)) {
//													HashMap<String, Integer> objectMap = relMap.get(relationTypeForJSON);
//													if (objectMap.containsKey(relationObject)) {
//														Integer currentObjectCount = objectMap.get(relationObject);
//														objectMap.put(relationObject, currentObjectCount + 1);
//													} else {
//														objectMap.put(relationObject, 1);
//													}
//													relMap.put(relationTypeForJSON, objectMap);
//												} else {
//													HashMap<String, Integer> objectMap = new HashMap<String, Integer>();
//													objectMap.put(relationObject, 1);
//													relMap.put(relationTypeForJSON, objectMap);
//												}
//												hm.put(relationSubject, relMap);
//											}
//											else{
//												HashMap<String, HashMap<String, Integer>> relMap = new HashMap<String, HashMap<String, Integer>>();
//												HashMap<String, Integer> objectMap = new HashMap<String, Integer>();
//												objectMap.put(relationObject, 1);
//												relMap.put(relationTypeForJSON, objectMap);
//												hm.put(relationSubject, relMap);
//											}
//											
//											NIFWriter.addDareRelationAnnotation(nifModel, as.getStart(), as.getEnd(),
//													argIndices, argAnchor, relationType);
//										}
//
//									}
//								}
//							}
//						}
//					}
//				}
//			    
//			   	    
//				
//			} catch (Exception | Error e) {
//				System.out.println("ERROR!!!");
//				e.printStackTrace();
//			}
//            //System.out.println("NIFMODEL:\n" + NIFReader.model2String(nifModel, RDFSerialization.TURTLE));
//        	
//        	sentenceIdWriter.close();
//        	objectZipSaver.close();
//        	plainSentenceWriter.close();
//        	dependencyParseIndexer.close();
//        	
//        	
//            return hm;
//        } catch (Exception e) {
//        	//e.printStackTrace();
//            throw new ExternalServiceFailedException(e.getMessage());
//        }
//    }
//
//    
//    
//    
//    public String storeData(String data, String languageParam, String index) throws ExternalServiceFailedException, BadRequestException {
//        try {
//            String nif = "We will return the document: " + data + " in the language: " + languageParam;
//            
//            //the xml file path in file system or in class path
//            String patternFile = "patterns/acquisition.xml";
//            List<RePattern> patterns = XmlIOUtils.readList(RePattern.class, patternFile);
//            
//            XmlIOUtils.write(RePattern.class, patterns,new File("output-patterns.xml"));
//            
//            String sargraphFile = "sargraph/acquisition.xml";
//            XmlSerializableSarGraph g = XmlIOUtils.read(XmlSerializableSarGraph.class, sargraphFile);
//            
//            XmlIOUtils.write(g, new File("output-sargraph.xml"));
//
//            
//            //TODO How to store new relation
//            
//            return nif;
//        } catch (Exception e) {
//            throw new ExternalServiceFailedException(e.getMessage());
//        }
//    }
//}
