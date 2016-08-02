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
//		String sampleText = "Iceland is a little country far north in the cold sea. Men found it and\n" +
//				"went there to live more than a thousand years ago. During the warm\n" +
//				"season they used to fish and make fish-oil and hunt sea-birds and gather\n" +
//				"feathers and tend their sheep and make hay. But the winters were long\n" +
//				"and dark and cold. Men and women and children stayed in the house and\n" +
//				"carded and spun and wove and knit. A whole family sat for hours around\n" +
//				"the fire in the middle of the room. That fire gave the only light.\n" +
//				"Shadows flitted in the dark corners. Smoke curled along the high beams\n" +
//				"in the ceiling. The children sat on the dirt floor close by the fire.\n" +
//				"The grown people were on a long narrow bench that they had pulled up to\n" +
//				"the light and warmth. Everybody's hands were busy with wool. The work\n" +
//				"left their minds free to think and their lips to talk. What was there to\n" +
//				"talk about? The summer's fishing, the killing of a fox, a voyage to\n" +
//				"Norway. But the people grew tired of this little gossip. Fathers looked\n" +
//				"at their children and thought:\n" +
//				"\n" +
//				"\"They are not learning much. What will make them brave and wise? What\n" +
//				"will teach them to love their country and old Norway? Will not the\n" +
//				"stories of battles, of brave deeds, of mighty men, do this?\"\n" +
//				"\n" +
//				"So, as the family worked in the red fire-light, the father told of the\n" +
//				"kings of Norway, of long voyages to strange lands, of good fights. And\n" +
//				"in farmhouses all through Iceland these old tales were told over and\n" +
//				"over until everybody knew them and loved them. Some men could sing and\n" +
//				"play the harp. This made the stories all the more interesting. People\n" +
//				"called such men \"skalds,\" and they called their songs \"sagas.\"\n" +
//				"\n" +
//				"Every midsummer there was a great meeting. Men from all over Iceland\n" +
//				"came to it and made laws. During the day there were rest times, when no\n" +
//				"business was going on. Then some skald would take his harp and walk to a\n" +
//				"large stone or a knoll and stand on it and begin a song of some brave\n" +
//				"deed of an old Norse hero. At the first sound of the harp and the\n" +
//				"voice, men came running from all directions, crying out:\n" +
//				"\n" +
//				"\"The skald! The skald! A saga!\"\n" +
//				"\n" +
//				"They stood about for hours and listened. They shouted applause. When the\n" +
//				"skald was tired, some other man would come up from the crowd and sing or\n" +
//				"tell a story. As the skald stepped down from his high position, some\n" +
//				"rich man would rush up to him and say:\n" +
//				"\n" +
//				"\"Come and spend next winter at my house. Our ears are thirsty for song.\"\n" +
//				"\n" +
//				"So the best skalds traveled much and visited many people. Their songs\n" +
//				"made them welcome everywhere. They were always honored with good seats\n" +
//				"at a feast. They were given many rich gifts. Even the King of Norway\n" +
//				"would sometimes send across the water to Iceland, saying to some famous\n" +
//				"skald:\n" +
//				"\n" +
//				"\"Come and visit me. You shall not go away empty-handed. Men say that the\n" +
//				"sweetest songs are in Iceland. I wish to hear them.\"\n" +
//				"\n" +
//				"These tales were not written. Few men wrote or read in those days.\n" +
//				"Skalds learned songs from hearing them sung. At last people began to\n" +
//				"write more easily. Then they said:\n" +
//				"\n" +
//				"\"These stories are very precious. We must write them down to save them\n" +
//				"from being forgotten.\"\n" +
//				"\n" +
//				"After that many men in Iceland spent their winters in writing books.\n" +
//				"They wrote on sheepskin; vellum, we call it. Many of these old vellum\n" +
//				"books have been saved for hundreds of years, and are now in museums in\n" +
//				"Norway. Some leaves are lost, some are torn, all are yellow and\n" +
//				"crumpled. But they are precious. They tell us all that we know about\n" +
//				"that olden time. There are the very words that the men of Iceland wrote\n" +
//				"so long ago--stories of kings and of battles and of ship-sailing. Some\n" +
//				"of those old stories I have told in this book.\n";
//		ess.processData(sampleText, "en", RDFSerialization.PLAINTEXT);
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
//				RelationValidator rv = DefaultRelationValidatorFactory.getValidator(FileFactory.generateFileInstance(RELATION_VALIDATOR_CONFIG).getAbsolutePath());
//				//RelationValidator rv = DefaultRelationValidatorFactory.getValidator(RELATION_VALIDATOR_CONFIG);
//			    System.currentTimeMillis();
//			    Set<String> nfoRelevantSentences = new HashSet<>();
//			    Set<String> nfoUtilizedSentences = new HashSet<>();
//			    XmlBatchWriter<ExtractedRelationMention> mentionWriter = XmlBatchWriterFactory.getWriter(ExtractedRelationMention.class, new File(dareTempFolder));
//				ObjectZipLoader<AnnotatedSentence> sentLoader = new ObjectZipLoader<AnnotatedSentence>(new File(DARE_SENTENCE));
//				SentenceIndexSearcher sentSearcher = new SentenceIndexSearcher(sentLoader, new File(DARE_SENTENCE_DPINDEXER));
//				
//				System.out.println("DEBUGGING content:" + new File(darePatterns).listFiles());
//				for (File ruleDir : new File(darePatterns).listFiles()) {
//					System.out.println("DEBUG: ruleDir:" + ruleDir.getName());
//					try (XmlBatchReader<RePattern> patternReader = XmlBatchReaderFactory.getReader(RePattern.class, ruleDir)) {
//						List<RePattern> patterns;
//						while (null != (patterns = patternReader.read())) {
//							for (RePattern pattern : patterns) {
//								System.out.println("DEBUG: looping with pattern:" + pattern.getRelationType());
//								int tmp;
//								try {
//									tmp = Integer.parseInt(pattern.getFeatureSet().get(PatternSemanticKAdder.PATTERN_FEATURENAME_KVALUE).get(0));
//								} catch (Exception e) {
//									tmp = -1;
//								}
//								final int kValue = tmp;
//								final int freq = pattern.getLearningTracks().size();
//								int cntExtractedMentionsForCurrentPattern = 0;
//								Set<AnnotatedSentence> relevantSentencesForCurrentPattern = sentSearcher.getTargetSentences(pattern);
//								if (null == relevantSentencesForCurrentPattern || relevantSentencesForCurrentPattern.isEmpty()) {
//									//TODO: log something
//									continue;
//								}
//								
//								for (AnnotatedSentence relevantSentence : relevantSentencesForCurrentPattern) {
//									nfoRelevantSentences.add(relevantSentence.getId());
//									List<ExtractedRelationMention> erms = matcher.apply(relevantSentence, pattern, rv);
//									if (erms == null || erms.isEmpty())
//										continue;
//									erms.forEach(erm -> {
//										if (kValue != -1)
//											erm.getFeatures().addFeature(Postprocessor.ERM_FEATURE_KVALUE, kValue + "");
//										erm.getFeatures().addFeature(Postprocessor.ERM_FEATURE_FREQUENCY, freq + "");
//										erm.getFeatures().addFeature(Postprocessor.ERM_FEATURE_OFFSET,
//												relevantSentence.getStart() + "");
//									});
//									nfoUtilizedSentences.add(relevantSentence.getId());
//									cntExtractedMentionsForCurrentPattern += erms.size();
//									erms.size();
//									mentionWriter.write(erms);
//								}
//								
//								if (cntExtractedMentionsForCurrentPattern > 0)
//									System.out.println("Found something!");
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
