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
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import de.dkt.common.filemanagement.FileFactory;
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

// Class for evaluation of namefinder, etc.

public class Evaluator {

	public static void main(String[] args) {
		
		// ints are numIterations (of whole procedure), numTrainingIterations (for individual trainings), cutoffValue
		evaluateNameFinder(10, 20, 1, "C:\\Users\\pebo01\\Desktop\\various\\nerModels\\aij-wikiner-de.LOC.opennlp.out", "evalDummy", "C:\\Users\\pebo01\\Desktop\\EventStory2017\\tempTrainFile.txt", "C:\\Users\\pebo01\\Desktop\\EventStory2017\\tempTestFile.txt");
		
	}

	public static HashMap<String, List<String>> splitDataRandom(String filePath) {

		HashMap<String, List<String>> returnMap = new HashMap<String, List<String>>();

		try {
			Path p = new File(filePath).toPath();
			List<String> flines = Files.readAllLines(p);
			int n = (int) (flines.size() * 0.9);
			Collections.shuffle(flines);
			List<String> training = new ArrayList<String>();
			List<String> test = new ArrayList<String>();
			for (int i = 0; i < flines.size(); i++) {
				if (i < n) {
					training.add(flines.get(i));
				} else {
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

	public static HashMap<String, Double> evaluate(List<String> goldLines, List<String> testLines) {

		HashMap<String, Double> scores = new HashMap<String, Double>();

		if (goldLines.size() != testLines.size()) {
			System.out.println(String.format("WARNING: length of files does not match (%s vs %s). Skipping this run.",
					Integer.toString(goldLines.size()), Integer.toString(testLines.size())));
			// System.exit(1);
			scores.put("precision", -1.0);
			scores.put("recall", -1.0);
			scores.put("f", -1.0);
			return scores;
		}

		double tp = 0;
		double fp = 0;
		double tn = 0;
		double fn = 0;
		for (int i = 0; i < goldLines.size(); i++) {
			String goldId = goldLines.get(i).split("\t")[0];
			String testId = testLines.get(i).split("\t")[0];
			if (!goldId.equalsIgnoreCase(testId)) {
				System.out.println(
						String.format("WARNING: ids do not match (%s vs %s). Skipping this run.", goldId, testId));
				// System.exit(1);
				scores.put("precision", -1.0);
				scores.put("recall", -1.0);
				scores.put("f", -1.0);
				return scores;
			} else {
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

		// System.out.println(String.format("INFO: Comparison based on %s of
		// (%s, %s) rows.", Integer.toString(matching), goldLines.size(),
		// testLines.size()));
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

	public static void trainModel(String filePath, String modelPath, int trainingIterations, int cutoffValue) throws IOException {

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
			newModel = new File(modelPath);
					//"C:\\Users\\pebo01\\Desktop\\dbpediaNamedEventData" + File.separator + modelName + ".bin");
			newModel.createNewFile();
			TrainingParameters tp = new TrainingParameters();
			tp.put(TrainingParameters.CUTOFF_PARAM, Integer.toString(cutoffValue));
			tp.put(TrainingParameters.ITERATIONS_PARAM, Integer.toString(trainingIterations));// 100
			TokenNameFinderFactory tnff = new TokenNameFinderFactory();
			File f = new File(modelPath);
			model = NameFinderME.train("en", f.getName(), sampleStream, tp, tnff);
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

	public static List<String> getConll(List<String> annotatedLines) {

		List<String> conll = new ArrayList<String>();

		int id = 0;
		for (String s : annotatedLines) {
			s = s.replaceAll("\\s+", " ");
			String[] tokens = s.split(" ");
			boolean e = false;
			for (String t : tokens) {
				if (t.matches("<START:[^>]+>")) {
					e = true;
				} else if (t.matches("<END>")) {
					e = false;
				} else {
					id++;
					String line = String.format("%s\t%s\t%s", Integer.toString(id), t, Boolean.toString(e));
					conll.add(line);
				}
			}
		}

		return conll;

	}

	public static List<String> spotNames2conll(String modelPath, String testFilePath) throws IOException {

		NameFinderME nameFinder = null;
		InputStream tnfNERModel;
		File modelbin = FileFactory.generateOrCreateFileInstance(modelPath);
				//"C:\\Users\\pebo01\\Desktop\\dbpediaNamedEventData\\" + File.separator + modelName + ".bin");
		tnfNERModel = new FileInputStream(modelbin);
		TokenNameFinderModel tnfModel = new TokenNameFinderModel(tnfNERModel);

		nameFinder = new NameFinderME(tnfModel);
		tnfNERModel.close();
		ArrayList<String> conllLines = new ArrayList<String>();
		int id = 0;
		String[] lines = staticreadLines(testFilePath);
		for (String l : lines) {
			l = l.trim();
			String sent = l.replaceAll("\\s+", " ");
			// String[] sents = SentenceDetector.detectSentences(l,
			// "en-sent.bin");
			// for (String sent : sents){
			// Span tokenSpans[] = Tokenizer.simpleTokenizeIndices(sent);
			Span tokenSpans[] = Tokenizer.whitespaceTokenizeIndices(sent);
			String tokens[] = Span.spansToStrings(tokenSpans, sent);
			Span[] nameSpans;
			synchronized (nameFinder) {
				nameSpans = nameFinder.find(tokens);
			}
			ArrayList<String> names = new ArrayList<String>();
			for (Span s : nameSpans) {
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
				// System.out.println("DEBUG: recognized:" +
				// sent.substring(nameStartIndex, nameEndIndex));
				// System.out.println("DEBUG type:" + nameType);
				for (String sub : sent.substring(nameStartIndex, nameEndIndex).split(" ")) {
					names.add(sub);
				}

			}
			for (Span ts : tokenSpans) {
				boolean e = false;
				id++;
				if (names.contains(sent.substring(ts.getStart(), ts.getEnd()))) {
					// also check indices to be sure
					for (Span s : nameSpans) {
						if (ts.getStart() >= s.getStart() || ts.getEnd() <= s.getEnd()) {
							e = true;
						}
					}
				}
				conllLines.add(String.format("%s\t%s\t%s", Integer.toString(id),
						sent.substring(ts.getStart(), ts.getEnd()), Boolean.toString(e)));
			}
			// }
		}
		return conllLines;
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

	public static void evaluateNameFinder(int numIterations, int trainingIterations, int cutoffValue, String trainingDataFilePath, String modelName, String tempTrainPath, String tempTestPath) {

		double p = 0;
		double r = 0;
		double f = 0;

		for (int q = 0; q < numIterations; q++) {
			int numIt = q+1;
			System.out.println("INFO: At iteration " + numIt);
			try {
//				PrintWriter goldDebug = new PrintWriter(
//						"C:\\Users\\pebo01\\Desktop\\dbpediaNamedEventData\\goldConllDebug.txt");
//				PrintWriter testDebug = new PrintWriter(
//						"C:\\Users\\pebo01\\Desktop\\dbpediaNamedEventData\\testConllDebug.txt");
				HashMap<String, List<String>> data = splitDataRandom(trainingDataFilePath);
						//"C:\\Users\\pebo01\\Desktop\\dbpediaNamedEventData\\opennlp.train");
				//String tempTrainPath = "C:\\Users\\pebo01\\Desktop\\dbpediaNamedEventData\\tempTrain.txt";
				//String tempTestPath = "C:\\Users\\pebo01\\Desktop\\dbpediaNamedEventData\\tempTest.txt";
				PrintWriter tempTrain = new PrintWriter(new File(tempTrainPath));
				PrintWriter tempTest = new PrintWriter(new File(tempTestPath));
				List<String> trainLines = data.get("training");
				List<String> testLines = data.get("test");
				List<String> testConllGold = getConll(testLines);
//				for (String s : testConllGold) {
//					goldDebug.println(s.trim());
//				}

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

				//String modelName = "eventStoryToymodel";
				trainModel(tempTrainPath, modelName, trainingIterations, cutoffValue);
				System.out.println(String.format("INFO: Training done. (%s of %s)", Integer.toString(q + 1),
						Integer.toString(numIterations)));

				List<String> testConll = spotNames2conll(modelName, tempTestPath);
//				for (String s : testConll) {
//					testDebug.println(s);
//				}
				System.out.println(String.format("INFO: Spotting done. (%s of %s)", Integer.toString(q + 1),
						Integer.toString(numIterations)));

				HashMap<String, Double> scores = evaluate(testConllGold, testConll);
				if (scores.get("precision") == -1.0 && scores.get("recall") == -1.0 && scores.get("f") == -1.0) {
					q--;
					// ugly hack to do a re-try if it didn't work (bug I didn't
					// want to get to the bottom of...)
					System.out.println("WARNING: Failed run, trying again.");
				} else {
					System.out.println(String.format("INFO: Evaluation done. (%s of %s)", Integer.toString(q + 1),
							Integer.toString(numIterations)));
					p += scores.get("precision");
					r += scores.get("recall");
					f += scores.get("f");
					// System.out.println("precision:" +
					// scores.get("precision"));
					// System.out.println("recall:" + scores.get("recall"));
					// System.out.println("f:" + scores.get("f"));

//					testDebug.close();
//					goldDebug.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		System.out.println(String.format("Average precision over %s runs: %s", Integer.toString(numIterations),
				Double.toString(p / numIterations)));
		System.out.println(String.format("Average recall over %s runs: %s", Integer.toString(numIterations),
				Double.toString(r / numIterations)));
		System.out.println(String.format("Average f over %s runs: %s", Integer.toString(numIterations),
				Double.toString(f / numIterations)));

	}

}
