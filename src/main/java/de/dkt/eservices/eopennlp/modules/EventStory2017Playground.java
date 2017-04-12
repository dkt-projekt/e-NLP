package de.dkt.eservices.eopennlp.modules;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.base.Joiner;

import de.dkt.common.filemanagement.FileFactory;
import de.dkt.eservices.ecorenlp.modules.Tagger;
import de.dkt.eservices.erattlesnakenlp.linguistic.DepParserTree;
import de.dkt.eservices.erattlesnakenlp.linguistic.StanfordLemmatizer;
import de.dkt.eservices.erattlesnakenlp.modules.RattleSpan;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.ling.WordLemmaTag;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.TypedDependency;
import opennlp.tools.cmdline.namefind.TokenNameFinderModelLoader;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.NameSample;
import opennlp.tools.namefind.NameSampleDataStream;
import opennlp.tools.namefind.TokenNameFinderFactory;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.Span;
import opennlp.tools.util.TrainingParameters;


public class EventStory2017Playground {

	public static HashMap<String, List<String>> splitDataRandom(String filePath){

	    HashMap<String, List<String>> returnMap = new HashMap<String, List<String>>();
	    
	    try {
	    	Path p = new File(filePath).toPath();
			List<String> flines = Files.readAllLines(p);
			int n = (int)(flines.size() * 0.9);
			Collections.shuffle(flines);
			List<String> training = new ArrayList<String>();
			List<String> test = new ArrayList<String>();
			for (int i = 0; i < flines.size(); i++){
				if (i < n){
					training.add(flines.get(i));
				}
				else{
					test.add(flines.get(i));
				}
			}
			returnMap.put("training", training);
		    returnMap.put("test", test);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	    return returnMap;
	}
	
	
	public static void trainModel(String filePath, String modelName) throws IOException{
		

		Charset charset = Charset.forName("UTF-8");
		InputStreamFactory isf = new InputStreamFactory() {
            public InputStream createInputStream() throws IOException {
                return new FileInputStream(filePath);
            }
        };
		ObjectStream<String> lineStream = new PlainTextByLineStream(isf, charset);
		ObjectStream<NameSample> sampleStream = new NameSampleDataStream(lineStream);
		OutputStream modelOut = null;
		File newModel = null;
		
		try {
			TokenNameFinderModel model;
			newModel = new File("C:\\Users\\pebo01\\Desktop\\dbpediaNamedEventData" + File.separator + modelName + ".bin");
			newModel.createNewFile();
			TrainingParameters tp = new TrainingParameters();
			tp.put(TrainingParameters.CUTOFF_PARAM, "1");
			tp.put(TrainingParameters.ITERATIONS_PARAM, "10");//100
			TokenNameFinderFactory tnff = new TokenNameFinderFactory();
			model = NameFinderME.train("en", modelName, sampleStream, tp, tnff);
			modelOut = new BufferedOutputStream(new FileOutputStream(newModel));
			model.serialize(modelOut);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			sampleStream.close();
			if (modelOut != null)
				modelOut.close();
		}
	}
	
	public static List<String> getConll(List<String> annotatedLines){
		
		List<String> conll = new ArrayList<String>();
		
		int id = 0;
		for (String s : annotatedLines){
			s = s.replaceAll("\\s+", " ");
			String[] tokens = s.split(" ");
			boolean e = false;
			for (String t : tokens){
				if (t.matches("<START:[^>]+>")){
					e = true;
				}
				else if (t.matches("<END>")){
					e = false;
				}
				else{
					id++;
					String line = String.format("%s\t%s\t%s", Integer.toString(id), t, Boolean.toString(e));
					conll.add(line);
				}
			}
		}
		
		return conll;
		
	}
	
	public static String[] staticreadLines(String filename) throws IOException {
		FileReader fileReader = new FileReader(filename);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		List<String> lines = new ArrayList<String>();
		String line = null;
		while ((line = bufferedReader.readLine()) != null) {
			lines.add(line);
		}
		bufferedReader.close();
		return lines.toArray(new String[lines.size()]);
	}
	
	public static List<String> spotNames2conll(String modelName, String testFilePath) throws IOException{
		
		NameFinderME nameFinder = null;
		InputStream tnfNERModel;
		File modelbin = FileFactory.generateOrCreateFileInstance("C:\\Users\\pebo01\\Desktop\\dbpediaNamedEventData\\" + File.separator + modelName + ".bin");
		tnfNERModel = new FileInputStream(modelbin);
		TokenNameFinderModel tnfModel = new TokenNameFinderModel(tnfNERModel);
		
		nameFinder = new NameFinderME(tnfModel);
		tnfNERModel.close();
		ArrayList<String> conllLines = new ArrayList<String>();
		int id = 0;
		String[] lines = staticreadLines(testFilePath);
		for (String l : lines){
			l = l.trim();
			String sent = l.replaceAll("\\s+", " ");
			//String[] sents = SentenceDetector.detectSentences(l, "en-sent.bin");
			//for (String sent : sents){
				//Span tokenSpans[] = Tokenizer.simpleTokenizeIndices(sent);
				Span tokenSpans[] = Tokenizer.whitespaceTokenizeIndices(sent);
				String tokens[] = Span.spansToStrings(tokenSpans, sent);
				Span[] nameSpans;
				synchronized (nameFinder) {
					nameSpans = nameFinder.find(tokens);
				}
				ArrayList<String> names = new ArrayList<String>();
				for (Span s : nameSpans){
					String nameType = s.getType();
					int nameStartIndex = 0;
					int nameEndIndex = 0;
					for (int i = 0; i <= tokenSpans.length; i++) {
						if (i == s.getStart()) {
							nameStartIndex = tokenSpans[i].getStart();
						} else if (i == s.getEnd()) {
							nameEndIndex = tokenSpans[i - 1].getEnd();
						}
					}
					//System.out.println("DEBUG: recognized:" + sent.substring(nameStartIndex, nameEndIndex));
					//System.out.println("DEBUG type:" + nameType);
					for (String sub : sent.substring(nameStartIndex, nameEndIndex).split(" ")){
						names.add(sub);
					}

				}
				for (Span ts : tokenSpans){
					boolean e = false;
					id++;
					if (names.contains(sent.substring(ts.getStart(), ts.getEnd()))){
						// also check indices to be sure
						for (Span s : nameSpans){
							if (ts.getStart() >= s.getStart() || ts.getEnd() <= s.getEnd()){
								e = true;
							}
						}
					}
					conllLines.add(String.format("%s\t%s\t%s", Integer.toString(id), sent.substring(ts.getStart(), ts.getEnd()), Boolean.toString(e)));
				}
			//}
		}
		return conllLines;
	}
	
	public static HashMap<String, Double> evaluate(List<String> goldLines, List<String> testLines){
		
		HashMap<String, Double> scores = new HashMap<String, Double>();
		
		if (goldLines.size() != testLines.size()){
			System.out.println(String.format("WARNING: length of files does not match (%s vs %s). Skipping this run.", Integer.toString(goldLines.size()), Integer.toString(testLines.size())));
			//System.exit(1);
			scores.put("precision", -1.0);
			scores.put("recall", -1.0);
			scores.put("f", -1.0);
			return scores;
		}
		
		double tp = 0;
		double fp = 0;
		double tn = 0;
		double fn = 0;
		for (int i = 0; i < goldLines.size(); i++){
			String goldId = goldLines.get(i).split("\t")[0];
			String testId = testLines.get(i).split("\t")[0];
			if (!goldId.equalsIgnoreCase(testId)){
				System.out.println(String.format("WARNING: ids do not match (%s vs %s). Skipping this run.", goldId, testId));
				//System.exit(1);
				scores.put("precision", -1.0);
				scores.put("recall", -1.0);
				scores.put("f", -1.0);
				return scores;
			}
			else{
				String goldVal = goldLines.get(i).split("\t")[2];
				String testVal = testLines.get(i).split("\t")[2];
				if (goldVal.equalsIgnoreCase("false") && testVal.equals("false")) {
					tn++;
				} else if (goldVal.equalsIgnoreCase("false") && testVal.equalsIgnoreCase("true")) {
					fp++;
				} else if (goldVal.equalsIgnoreCase("true") && testVal.equalsIgnoreCase("true")) {
					tp++;
				} else if (goldVal.equalsIgnoreCase("true") && testVal.equalsIgnoreCase("false")) {
					fn++;
				}
			}
		}
		
		//System.out.println(String.format("INFO: Comparison based on %s of (%s, %s) rows.", Integer.toString(matching), goldLines.size(), testLines.size()));
		double precision = tp / (tp + fp);
		double recall = tp / (tp + fn);
		double f = 2 * ((precision * recall) / (precision + recall));
		scores.put("precision", precision);
		scores.put("recall", recall);
		scores.put("f", f);
		System.out.println("Debugging p for individual run:" + precision);
		System.out.println("Debugging r for individual run:" + recall);
		System.out.println("Debugging f for individual run:" + f);
		
		
		return scores;
	}
	
	
	public static NameFinderME initiateNameFinder(String modelPath){
		
		NameFinderME nameFinder = null;
		//InputStream tnfNERModel;
		File modelbin;
		try {
			modelbin = FileFactory.generateOrCreateFileInstance(modelPath);
			//tnfNERModel = new FileInputStream(modelbin);
			//TokenNameFinderModel tnfModel = new TokenNameFinderModel(tnfNERModel);
			
			TokenNameFinderModel model = new TokenNameFinderModelLoader().load(modelbin);
			nameFinder = new NameFinderME(model);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return nameFinder;
	}
	
	public static ArrayList<Span> combineNamesWithDepgraphs(HashMap<List<TaggedWord>, GrammaticalStructure> gsMap, NameFinderME nameFinder){
		
		ArrayList<Span> nameSpans = new ArrayList<Span>();
		
		nameFinder.clearAdaptiveData();
		
		//nameFinder = new NameFinderME(tnfModel);
		//tnfNERModel.close();
		for (List<TaggedWord> tagged : gsMap.keySet()){
			List<String> sl = new ArrayList<String>();
			for (TaggedWord tw : tagged){
				sl.add(tw.word());
			}
			String sentence = Joiner.on(" ").join(sl);
			Span[] tokenSpans = Tokenizer.whitespaceTokenizeIndices(sentence);
			String tokens[] = Span.spansToStrings(tokenSpans, sentence);
			System.out.println("Sentence: " + sentence);
			Span[] ns = null;
			//synchronized (nameFinder) {
			//ns = nameFinder.find(tokens);
			List<Span> names = new ArrayList<Span>();
			Collections.addAll(names, nameFinder.find(tokens));
			Span reducedNames[] = NameFinderME.dropOverlappingSpans(names.toArray(new Span[names.size()]));
			//}
			for (Span s : reducedNames) {
				int nameStartIndex = 0;
				int nameEndIndex = 0;
				for (int i = 0; i <= tokenSpans.length; i++) {
					if (i == s.getStart()) {
						nameStartIndex = tokenSpans[i].getStart();
					} else if (i == s.getEnd()) {
						nameEndIndex = tokenSpans[i - 1].getEnd();
					}
				}
				String name = sentence.substring(nameStartIndex, nameEndIndex);
				nameSpans.add(s);
				System.out.println("\tEVENT FOUND: " + name + " (type): " + s.getType());
				for (TypedDependency td : gsMap.get(tagged).typedDependencies()){
					GrammaticalRelation reln = td.reln();
					IndexedWord gov = td.gov();
					IndexedWord dep = td.dep();
					if (!gov.value().equalsIgnoreCase("ROOT")) { // TODO: write another condition for this
						int govStart = tagged.get(gov.index() - 1).beginPosition();
						int govEnd = tagged.get(gov.index() - 1).endPosition();
						int depStart = tagged.get(dep.index() - 1).beginPosition();
						int depEnd = tagged.get(dep.index() - 1).endPosition();
//							System.out.println("name: " + nameStartIndex + "|" + nameEndIndex);
//							System.out.println("td:" + td);
//							System.out.println("gov: " + govStart + "|" + govEnd);
//							System.out.println("dep: " + depStart + "|" + depEnd);
						if (nameStartIndex <= govStart && nameEndIndex >= govEnd) { // TODO: get some more suitable data/good example and work this out. (if found event is gov or dep, get governing node, get dep/gov from there and connect the two)
//								System.out.println("\tEntity is gov: " + gov.word());
						} 
						else if (nameStartIndex <= depStart && nameEndIndex >= depEnd) {
//								System.out.println("\tEntity is dep: " + dep.word());
						}
					}
					
				}
			}
			//}
		}
		
		
		
		return nameSpans;
		
		
	}
	
	public static HashMap<List<TaggedWord>, GrammaticalStructure> getDepGraphs(String[] flines){
		
		HashMap<List<TaggedWord>, GrammaticalStructure> gsMap= new HashMap<List<TaggedWord>, GrammaticalStructure>();
//		final StanfordLemmatizer lemmatizer = StanfordLemmatizer.getInstance(); 
		Tagger.initTagger("en");
		DepParserTree.initParser("en");
		
		
		for (String line : flines){
			DocumentPreprocessor tokenizer = new DocumentPreprocessor(new StringReader(line));
			for (List<HasWord> sentence : tokenizer) {
				List<TaggedWord> tagged = Tagger.tagger.tagSentence(sentence);
//				final List<WordLemmaTag> tlSentence = new ArrayList<WordLemmaTag>();
//				for (TaggedWord tw : tagged) {
//					tlSentence.add((lemmatizer).lemmatize(tw));
//				}
				GrammaticalStructure gs = DepParserTree.parser.predict(tagged);
				gsMap.put(tagged, gs);

			}

		}
		return gsMap;
		
	}
	
	//TODO after lunch: take data from ankit, extract plaintext from ace xmls(sgmls) and check character indices of trigger verbs, calculate overlap. This will be very low. Next step would be to combine this with Jan's code of triggerverb extraction through dep parsing
	
	public static ArrayList<RattleSpan> extractEventSpansFromACEAnnotations(String fp){
		
		ArrayList<RattleSpan> eventSpans = new ArrayList<RattleSpan>();
		
		try {
			File inputFile = new File(fp);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();
//			System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
			//System.out.println("complete text:" + doc.getDocumentElement().getTextContent());
			NodeList events = doc.getElementsByTagName("event");
//			System.out.println("----------------------------");
			for (int temp = 0; temp < events.getLength(); temp++) {
				Node eventNode = events.item(temp);
				//System.out.println("\nCurrent Element :" + nNode.getNodeName());
				NodeList eventMentions = eventNode.getChildNodes();
				for (int i = 0; i < eventMentions.getLength(); i++){
					NodeList mentionSubnodes = eventMentions.item(i).getChildNodes();
					for (int j = 0; j < mentionSubnodes.getLength(); j++){
						if (mentionSubnodes.item(j).getNodeName().equalsIgnoreCase("anchor")){
							String anchorText = mentionSubnodes.item(j).getTextContent();
							//System.out.println("\nAnchortext: " + anchorText);
							Node subnode = mentionSubnodes.item(j);
							Node charseq = subnode.getChildNodes().item(1);
							String startVal = charseq.getAttributes().getNamedItem("START").getNodeValue();
							String endVal = charseq.getAttributes().getNamedItem("END").getNodeValue();
							RattleSpan sp = new RattleSpan(Integer.parseInt(startVal), Integer.parseInt(endVal+1), anchorText); // the +1 because they have a somewhat weird convention in ACE...
							eventSpans.add(sp);
						}
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return eventSpans;

	}
	
	public static String parseACE_SGML(String filePath){
		
		String fullText = "";
		try{
			File inputFile = new File(filePath);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();
			Element de = doc.getDocumentElement();
			fullText = de.getTextContent();
			//System.out.println(fullText);
			//System.out.println("debug: " + fullText.substring(940,  943)); // NOTE that we have to do a +1 for end index for the words to match. Weird convention from ACE....
		}catch (Exception e){
			e.printStackTrace();
		}
		
		return fullText;
	}
	
	public static Integer getWordCount(String plainText){
		int wordCount = 0;
		for (String sentence : SentenceDetector.detectSentences(plainText, "en-sent.bin")){
			String[] tokens = Tokenizer.simpleTokenizeInput(sentence);
			wordCount += tokens.length;
		}
		return wordCount;
	}
	public static Integer getSentenceCount(String plainText){
		String[] sents =  SentenceDetector.detectSentences(plainText, "en-sent.bin");
		return sents.length;
	}
	
	public static ArrayList<RattleSpan> getRattleNameSpans(String plainText, NameFinderME nameFinder){
		
		ArrayList<RattleSpan> names = new ArrayList<RattleSpan>();
		
		nameFinder.clearAdaptiveData();
		Span[] sentenceSpans = SentenceDetector.detectSentenceSpans(plainText, "en-sent.bin");
		for (Span sentspan : sentenceSpans){
			String sentence = plainText.substring(sentspan.getStart(), sentspan.getEnd());
			Span[] tokenSpans = Tokenizer.simpleTokenizeIndices(sentence);
			String tokens[] = Span.spansToStrings(tokenSpans, sentence);
			Span[] nameSpans = nameFinder.find(tokens);
			for (Span ns : nameSpans){
				int nameStartIndex = 0;
				int nameEndIndex = 0;
				for (int i = 0; i <= tokenSpans.length; i++) {
					if (i == ns.getStart()) {
						nameStartIndex = tokenSpans[i].getStart() + sentspan.getStart();
					} else if (i == ns.getEnd()) {
						nameEndIndex = tokenSpans[i - 1].getEnd() + sentspan.getStart();
					}
				}
				String nameText = plainText.substring(nameStartIndex, nameEndIndex);
				RattleSpan rs = new RattleSpan(nameStartIndex, nameEndIndex, nameText);
				names.add(rs);
			}
		}
		return names;
	}
	
	public static ArrayList<RattleSpan> matchACESpansWithEventNames(ArrayList<RattleSpan> ACENames, ArrayList<RattleSpan> eventNames, String plainText){
		
		ArrayList<RattleSpan> overlap = new ArrayList<RattleSpan>();
		boolean debug = true;

		for (RattleSpan eventNameSpan : eventNames) {
			for (RattleSpan aceName : ACENames) {
				if (debug){
					if (eventNameSpan.getText().equalsIgnoreCase(aceName.getText())){
						System.out.println("DEBUG: comparing '" + eventNameSpan.getText() + "' and '" + aceName.getText() + "', in context:");
						int contextStartENS = Math.max(0, eventNameSpan.getBegin() - 10);
						int contextEndENS = Math.min(plainText.length(), eventNameSpan.getEnd() + 10);
						int contextStartAN = Math.max(0, aceName.getBegin() - 10);
						int contextEndAN = Math.min(plainText.length(), aceName.getEnd() + 10);
						System.out.println("Event Name Span: '" + plainText.substring(contextStartENS, contextEndENS) + "'");
						System.out.println("ACE trigger: '" + plainText.substring(contextStartAN, contextEndAN) + "'");
					}
				}
				if (eventNameSpan.getBegin() <= aceName.getBegin() && eventNameSpan.getEnd() >= aceName.getEnd()) {
					overlap.add(eventNameSpan);
				}

			}
		}
		return overlap;		
	}
	
	public static HashMap<String, Double> getWikiCorpusTriggerVerbs(String trainingData){
		
		final StanfordLemmatizer lemmatizer = StanfordLemmatizer.getInstance(); 
		Tagger.initTagger("en");
		DepParserTree.initParser("en");
		
		HashMap<String, Integer> depMap = new HashMap<String, Integer>();
		HashMap<String, Integer> govMap = new HashMap<String, Integer>();
		HashMap<String, Integer> verbs = new HashMap<String, Integer>();
		
		try {
			String[] flines = staticreadLines(trainingData);
			// not feeling like doing all this stuff with character indices, assuming that if a string is annotated as an event, it always is (within the same sentence, so assume that "This is <START:event> an event <END> and this is not an event" does not happen... (think that is reasonable)
			for (String line : flines) {
				Matcher m = Pattern.compile("<START:event> ([^<]+) <END>").matcher(line);
//				System.out.println(line);
				ArrayList<String> namedEvents = new ArrayList<String>();
				while (m.find()) {
					String namedEvent = m.group(1);
					namedEvents.add(namedEvent);
				}
				String newline = line.replaceAll("<START:event> ", "").replaceAll(" <END>", "");
				ArrayList<Span> events = new ArrayList<Span>();
				for (String ne : namedEvents){
					Matcher m2 = Pattern.compile(ne).matcher(newline);
					while (m2.find()){
						Span s = new Span(m2.start(), m2.end());
						events.add(s);
					}
				}
				DocumentPreprocessor tokenizer = new DocumentPreprocessor(new StringReader(newline));
				for (List<HasWord> sentence : tokenizer) {
					List<TaggedWord> tagged = Tagger.tagger.tagSentence(sentence);
					final List<WordLemmaTag> tlSentence = new ArrayList<WordLemmaTag>();
					for (TaggedWord tw : tagged) {
						String lemma = (lemmatizer).lemmatize(tw).lemma();
						tlSentence.add((lemmatizer).lemmatize(tw));
						if (tw.tag().startsWith("V")){
							int c = verbs.containsKey(lemma) ? verbs.get(lemma) + 1 : 1;
							verbs.put(lemma, c);
						}
					}
					GrammaticalStructure gs = DepParserTree.parser.predict(tagged);
//					System.out.println("Debug gstd: " + gs.typedDependencies());
					for (TypedDependency td : gs.typedDependencies()){
//						System.out.println("td: " + td);
						GrammaticalRelation reln = td.reln();
						IndexedWord gov = td.gov();
						IndexedWord dep = td.dep();
						if (!gov.value().equalsIgnoreCase("ROOT")) {
							int govStart = tagged.get(gov.index() - 1).beginPosition();
							int govEnd = tagged.get(gov.index() - 1).endPosition();
							int depStart = tagged.get(dep.index() - 1).beginPosition();
							int depEnd = tagged.get(dep.index() - 1).endPosition();
							for (Span s : events) {
//								System.out.println("name found: " + newline.substring(s.getStart(), s.getEnd()));
								if (s.getStart() <= govStart && s.getEnd() >= govEnd) {
									// check if dep is verb
									String depPosType = tagged.get(dep.index()-1).tag();
									if (depPosType.startsWith("V")){
										String lemma = tlSentence.get(dep.index()-1).lemma();
										//System.out.println("pos type lemma: " + lemma);
										int c = depMap.containsKey(lemma) ? depMap.get(lemma) + 1 : 1;
										depMap.put(lemma, c);
									}
									
									//System.out.println("named event is (part of) gov: " + gov);
								} else if (s.getStart() <= depStart && s.getEnd() >= depEnd) {
									// check if gov is verb
									String govPosType = tagged.get(gov.index()-1).tag();
									if (govPosType.startsWith("V")){
										String lemma = tlSentence.get(gov.index()-1).lemma();
										//System.out.println("pos type lemma: " + lemma);
										int c = govMap.containsKey(lemma) ? govMap.get(lemma) + 1 : 1;
										govMap.put(lemma, c);
									}
									//System.out.println("named event is (part of) dep: " + dep);
								}
							}
						}
					}
				}
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// combining gov's and dep's for now. Maybe split this at some point...
		HashMap<String, Double> triggerVerbs = new HashMap<String, Double>();
		for (String k : govMap.keySet()){
			Double d = triggerVerbs.containsKey(k) ? triggerVerbs.get(k) + govMap.get(k) : govMap.get(k); // implicit conversion to integer! Oh behave!
			triggerVerbs.put(k,  d);
		}
		for (String k : depMap.keySet()){
			Double d = triggerVerbs.containsKey(k) ? triggerVerbs.get(k) + depMap.get(k) : depMap.get(k); // implicit conversion to integer! Oh behave!
			triggerVerbs.put(k,  d);
		}
		
		// normalize
		for (String k : triggerVerbs.keySet()){
			Double d = triggerVerbs.get(k) / verbs.get(k);
			triggerVerbs.put(k, d);
		}
		return triggerVerbs;
	}
	
	
	public static ArrayList<RattleSpan> annotateTriggerVerbsAsNames(HashMap<String, Double> triggerVerbs, Double threshold, String plainText){
		
		final StanfordLemmatizer lemmatizer = StanfordLemmatizer.getInstance(); 
		DocumentPreprocessor tokenizer = new DocumentPreprocessor(new StringReader(plainText));
		ArrayList<RattleSpan> tvSpans = new ArrayList<RattleSpan>();
		for (List<HasWord> sentence : tokenizer) {
			List<TaggedWord> tagged = Tagger.tagger.tagSentence(sentence);
			for (TaggedWord tw : tagged) {
				int startIndex = tw.beginPosition();
				int endIndex = tw.endPosition();
				WordLemmaTag lemma = (lemmatizer).lemmatize(tw); 
				for (String tv : triggerVerbs.keySet()){
					if (triggerVerbs.get(tv) > threshold){
						if (lemma.lemma().equalsIgnoreCase(tv)){
							RattleSpan rs = new RattleSpan(startIndex, endIndex, tv);
							tvSpans.add(rs);
						}
					}
				}
			}
		}
		return tvSpans;
		
	}
	
	
	public static void main(String[] args){
		
		
		NameFinderME nameFinder = null;
		//NameFinderME nameFinder = initiateNameFinder("C:\\Users\\pebo01\\Desktop\\dbpediaNamedEventData\\eventStoryToymodel.bin");
		nameFinder = initiateNameFinder("C:\\Users\\pebo01\\Desktop\\dbpediaNamedEventData\\namedEventModel.bin");
		
		System.out.println("INFO: Getting triggerVerbs.");
		HashMap<String, Double> triggerVerbs = getWikiCorpusTriggerVerbs("C:\\Users\\pebo01\\Desktop\\dbpediaNamedEventData\\opennlpSmall.train");
		System.out.println("INFO: Finished getting triggerVerbs.");
//		for (String tv : triggerVerbs.keySet()){
//			System.out.println("tv: " + tv);
//			System.out.println("val: " + triggerVerbs.get(tv));
//		}

		HashMap<String, String> basename2sgm = new HashMap<String, String>(); 
		HashMap<String, String> basename2xml = new HashMap<String, String>();
		ArrayList<RattleSpan> sl = new ArrayList<RattleSpan>();
		File df1;
		try {
			df1 = FileFactory.generateOrCreateDirectoryInstance("C:\\Users\\pebo01\\Desktop\\EventStory2017\\ACE\\for_Peter");
			for (File f : df1.listFiles()){
				String basename = f.getName().replaceAll("\\.apf\\.xml$", "").replaceAll("\\.sgm$", "");
				if (f.getAbsolutePath().matches(".*\\.sgm$")){
					basename2sgm.put(basename, f.getAbsolutePath());
				}
				else if (f.getAbsolutePath().matches(".*\\.xml$")){
					basename2xml.put(basename, f.getAbsolutePath());
				}
			}
			
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		if (basename2sgm.keySet().size() != basename2xml.keySet().size()){
			System.out.println("ERROR: filesets do not match. Dying now.");
			System.exit(1);
		}
		int wordCount = 0;
		int sentenceCount = 0;
		int aceEventTriggers = 0;
		int namedEventInstances = 0;
		int c = 0;
		int tvo = 0;
		int tvSpans = 0;
		for (String basename : basename2sgm.keySet()){
			//System.out.println("INFO: Processing file: " + basename);
			String plainText = parseACE_SGML(basename2sgm.get(basename));
			wordCount += getWordCount(plainText);
			sentenceCount += getSentenceCount(plainText);
			ArrayList<RattleSpan> eventNameSpans = getRattleNameSpans(plainText, nameFinder);
			sl = extractEventSpansFromACEAnnotations(basename2xml.get(basename));
			//TODO: extract not event anchors, but arguments of event mentions
			ArrayList<RattleSpan> overlap = matchACESpansWithEventNames(sl, eventNameSpans, plainText);
			aceEventTriggers += sl.size();
			namedEventInstances += eventNameSpans.size();
			c += overlap.size();
			// now annotate triggerverbs as names and check overlap
			ArrayList<RattleSpan> triggerVerbSpans = annotateTriggerVerbsAsNames(triggerVerbs, 0.5, plainText);
			ArrayList<RattleSpan> tvOverlap = matchACESpansWithEventNames(sl, triggerVerbSpans, plainText);
			tvSpans += triggerVerbSpans.size();
			tvo += tvOverlap.size();
			
		}
		System.out.println("INFO: wordCount and sentenceCount for ACE corpus: " + wordCount + "|" + sentenceCount);
		System.out.println("INFO: ACE event triggers in data: " + aceEventTriggers);
		System.out.println("INFO: Wkipedia Named Events in data: " + namedEventInstances);
		System.out.println("INFO: Overlap: " + c);
		System.out.println("INFO: Wikipedia extracted trigger verbs in data: " + tvSpans);
		System.out.println("INFO: Overlap of extracted trigger verbs and ACE triggers: " + tvo);
		
		
		
//		System.exit(1);
		
		
		
		System.exit(1);
		//TODO: get the trigger verbs myself through dep parser, then calculate overlap between trigger verbs and ace anchor verbs (normalize trigger verbs by dividing by total frequency)
		
		
		
		File df;
		try {
			df = FileFactory.generateOrCreateDirectoryInstance("C:\\Users\\pebo01\\Desktop\\dbpediaNamedEventData\\forsabine\\");
			for (File f : df.listFiles()) {
				System.out.println("INFO: Processing " + f.getName());
				String[] flines = staticreadLines(f.getAbsolutePath());
				HashMap<List<TaggedWord>, GrammaticalStructure> gsMap = getDepGraphs(flines);
				combineNamesWithDepgraphs(gsMap, nameFinder);
				
				//TODO; do NP extraction (must be something in coref code for this) to fix cases like ""Israeli Foreign Minister Silvan Shalom on Wednesday stressed Qatar 's relationship..."
				// where only Qatar is found as an entity
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		System.exit(1);
		
		int numIterations = 1;
		double p = 0;
		double r = 0;
		double f = 0;
		
		for (int q = 0; q < numIterations; q++){
		
			try {
				PrintWriter goldDebug = new PrintWriter("C:\\Users\\pebo01\\Desktop\\dbpediaNamedEventData\\goldConllDebug.txt");
				PrintWriter testDebug = new PrintWriter("C:\\Users\\pebo01\\Desktop\\dbpediaNamedEventData\\testConllDebug.txt");
				HashMap<String, List<String>> data = splitDataRandom("C:\\Users\\pebo01\\Desktop\\dbpediaNamedEventData\\opennlp.train");
				String tempTrainPath = "C:\\Users\\pebo01\\Desktop\\dbpediaNamedEventData\\tempTrain.txt";
				String tempTestPath = "C:\\Users\\pebo01\\Desktop\\dbpediaNamedEventData\\tempTest.txt";
				PrintWriter tempTrain = new PrintWriter(new File(tempTrainPath));
				PrintWriter tempTest = new PrintWriter(new File(tempTestPath));
				List<String> trainLines = data.get("training");
				List<String> testLines = data.get("test");
				List<String> testConllGold = getConll(testLines);
				for (String s : testConllGold){
					goldDebug.println(s.trim());
				}
				
				for (String s : trainLines) {
					tempTrain.println(s);
				}
				tempTrain.close();
				for (String s : testLines) {
					s = s.replaceAll("<START:[^>]+>", "");
					s = s.replaceAll("<END>", "");
					tempTest.println(s);
				}
				tempTest.close();
				
				String modelName = "eventStoryToymodel";
				trainModel(tempTrainPath, modelName);
				System.out.println(String.format("INFO: Training done. (%s of %s)", Integer.toString(q+1), Integer.toString(numIterations)));
				
				
				
				List<String> testConll = spotNames2conll(modelName, tempTestPath);
				for (String s : testConll){
					testDebug.println(s);
				}
				System.out.println(String.format("INFO: Spotting done. (%s of %s)", Integer.toString(q+1), Integer.toString(numIterations)));
				
				HashMap<String, Double> scores = evaluate(testConllGold, testConll);
				if (scores.get("precision") == -1.0 && scores.get("recall") == -1.0 && scores.get("f") == -1.0){
					q--;
					// ugly hack to do a re-try if it didn't work (bug I didn't want to get to the bottom of...)
					System.out.println("WARNING: Failed run, trying again.");
				}
				else{
					System.out.println(String.format("INFO: Evaluation done. (%s of %s)", Integer.toString(q + 1), Integer.toString(numIterations)));
					p += scores.get("precision");
					r += scores.get("recall");
					f += scores.get("f");
					// System.out.println("precision:" +
					// scores.get("precision"));
					// System.out.println("recall:" + scores.get("recall"));
					// System.out.println("f:" + scores.get("f"));

					testDebug.close();
					goldDebug.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		System.out.println(String.format("Average precision over %s runs: %s", Integer.toString(numIterations), Double.toString(p / numIterations)));		
		System.out.println(String.format("Average recall over %s runs: %s", Integer.toString(numIterations), Double.toString(r / numIterations)));
		System.out.println(String.format("Average f over %s runs: %s", Integer.toString(numIterations), Double.toString(f / numIterations)));
		
	}
	
	
	
	
}
