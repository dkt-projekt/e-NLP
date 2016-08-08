//package de.dkt.eservices.esargraph;
//
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.FileWriter;
//import java.io.OutputStreamWriter;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//import org.springframework.stereotype.Component;
//
//import com.hp.hpl.jena.rdf.model.Model;
//import com.hp.hpl.jena.sparql.engine.optimizer.Pattern;
//
//import de.dfki.lt.dare.element.AnnotatedSentence;
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
//	public static void main(String[] args) {
//		ESargraphService ess = new ESargraphService();
//		String sampleText = "On May 3, 2003 , LeBlanc married Melissa McKnight.\n" +
//				"Personal life Finch married free agent pitcher Casey Daigle on January 15, 2005, at the Crystal Cathedral in Garden Grove, California.\n" +
//				"Fashion model Natalia Vodianova married Justin Portman, on September 1, 2002.\n" +
//				"On August 28, 2004, in Santa Barbara , California , Hargitay married Peter Hermann , an actor and writer whom she met on the set of Law & Order: SVU , on which Hermann plays the recurring role of Defense Attorney Trevor Langan on SVU .\n" +
//				"Alicia Rickter married U.S. Major League Baseball player Mike Piazza on January 29, 2005.\n" +
//				"Gordon married Vandebosch in a small, private ceremony in Mexico on Nov. 7.\n" +
//				"Biography-continued Dahl married American actress Patricia Neal on 2nd July 1953 at Trinity Church in New York City.\n" +
//				"Taccone married Marielle Heller on June 30, 2007 at Mission Ranch, Carmel Jorma's brother, Asa, often helps The Lonely Island by making music for their shorts.\n" +
//				"Yes, Jon Foreman married Emily Foreman on January 26th, 2002.\n" +
//				"Jessica Alba married her boyfriend Cash Warren on May 19, 2008.\n" +
//				"Yes, Jude Law married Sadie Frost on September 2, 1997.";
//		ess.processData(sampleText, "en", RDFSerialization.PLAINTEXT);
//		
//	}
//	
//    public Model processData(String textToProcess, String languageParam, RDFSerialization inFormat) throws ExternalServiceFailedException, BadRequestException {
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
////			    Set<String> nfoUtilizedSentences = new HashSet<>();
////			    XmlBatchWriter<ExtractedRelationMention> mentionWriter = XmlBatchWriterFactory.getWriter(ExtractedRelationMention.class, new File(dareTempFolder));
//				//ObjectZipLoader<AnnotatedSentence> sentLoader = new ObjectZipLoader<AnnotatedSentence>(new File(DARE_SENTENCE));
//			    // hardcoded for debugging:
//			    //ObjectZipLoader<AnnotatedSentence> sentLoader = new ObjectZipLoader<AnnotatedSentence>(new File("C:\\Users\\pebo01\\workspace\\e-NLP\\dareTempFiles\\dare_sentence.zip"));
//			    //SentenceIndexSearcher sentSearcher = new SentenceIndexSearcher(sentLoader, new File(DARE_SENTENCE_DPINDEXER));
//				
//				//for (File ruleDir : new File(darePatterns).listFiles()) {
//				for (File ruleDir : new File("C:\\Users\\pebo01\\workspace\\e-NLP\\src\\main\\resources\\darePatterns").listFiles()) { // for debugging only
//					try (XmlBatchReader<RePattern> patternReader = XmlBatchReaderFactory.getReader(RePattern.class, ruleDir)) {
//						List<RePattern> patterns;
//						while (null != (patterns = patternReader.read())) {
//							for (RePattern pattern : patterns) {
//								int tmp;
//								try {
//									tmp = Integer.parseInt(pattern.getFeatureSet().get(PatternSemanticKAdder.PATTERN_FEATURENAME_KVALUE).get(0));
//								} catch (Exception e) {
//									tmp = -1;
//								}
//								final int kValue = tmp;
//								final int freq = pattern.getLearningTracks().size();
////								int cntExtractedMentionsForCurrentPattern = 0;
////								Set<AnnotatedSentence> relevantSentencesForCurrentPattern = sentSearcher.getTargetSentences(pattern);
////								
////								if (null == relevantSentencesForCurrentPattern || relevantSentencesForCurrentPattern.isEmpty()) {
////									//TODO: log something
////									continue;
////								}
//								
//								//for (AnnotatedSentence relevantSentence : relevantSentencesForCurrentPattern) {
//								for (AnnotatedSentence relevantSentence : annotatedSentences) { // seems to be less efficient as the commented/original lines above, but for a first stage, just to get it to work
//									nfoRelevantSentences.add(relevantSentence.getId());
//									List<ExtractedRelationMention> erms = matcher.apply(relevantSentence, pattern, rv);
//									if (erms == null || erms.isEmpty())
//										continue;
//									erms.forEach(erm -> {
//										if (kValue != -1)
//											erm.getFeatures().addFeature(Postprocessor.ERM_FEATURE_KVALUE, kValue + "");
//											erm.getFeatures().addFeature(Postprocessor.ERM_FEATURE_FREQUENCY, freq + "");
//											erm.getFeatures().addFeature(Postprocessor.ERM_FEATURE_OFFSET,
//											relevantSentence.getStart() + "");
//											System.out.println("Found pattern: " + pattern.getId());
//											System.out.println("Sentence: " + relevantSentence.getText());
//											System.out.println("start:" + relevantSentence.getStart());
//											System.out.println("end:" + relevantSentence.getEnd());
//											System.out.println("relationMentions:" + relevantSentence.getRelationMentions());
//											System.out.println("conceptMentions" + relevantSentence.getConceptMentions());
//											System.out.println("relation type:" + pattern.getRelationType());
//											System.out.println("edge set:" + pattern.edgeSet());
//											System.out.println("graphToString:" + pattern.graphToString());
//											XmlIOUtils.write(pattern, f);+
//											System.out.println("arity:" + pattern.getArity());
//											System.out.println("sent graph:" + relevantSentence.getGraph());
//											System.out.println("semantic terms:" + relevantSentence.getSemanticTerms());
//											System.out.println("source unit id:" + relevantSentence.getSourceUnitId());
//											System.out.println("pattern graphId:" + pattern.getGraphId());
//											System.out.println("root set:" + pattern.rootSet());
//											System.out.println("vertex set:" + pattern.vertexSet());
//											System.out.println("annotator:" + relevantSentence.getAnnotator());
//											//TODO:
//											System.out.println(erm.getArgumentNames());
//											System.out.println(erm.getArguments());
//											for(String argName : erm.geraRRguments
//											System.out.println(erm.getArgument();
//											
//											
//											
//											
//											System.out.println("\n");
//
//									});
////									nfoUtilizedSentences.add(relevantSentence.getId());
////									cntExtractedMentionsForCurrentPattern += erms.size();
////									erms.size();
////									mentionWriter.write(erms);
//								}
//								
////								if (cntExtractedMentionsForCurrentPattern > 0)
////									System.out.println("Found pattern: " + pattern.getId());
////									System.out.println("\tSentence: " + relevantSentence);
//									
//									//pattern.getId(),
//				                  	//cntExtractedMentionsForCurrentPattern,
//				                  	//relevantSentencesForCurrentPattern.size()));
//								 {
//								}
//				                  
//				        	  }
//				          }
//					}
//				}
//			    
//				
//			} catch (Exception | Error e) {
//				System.out.println("ERROR!!!");
//				e.printStackTrace();
//			}
//            
//        	
//        	sentenceIdWriter.close();
//        	objectZipSaver.close();
//        	plainSentenceWriter.close();
//        	dependencyParseIndexer.close();
//        	
//
//            return nifModel;
//        } catch (Exception e) {
//            throw new ExternalServiceFailedException(e.getMessage());
//        }
//    }
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
