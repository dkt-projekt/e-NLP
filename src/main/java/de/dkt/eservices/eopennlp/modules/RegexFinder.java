package de.dkt.eservices.eopennlp.modules;

import java.io.File;
import java.io.FileWriter;

import java.io.IOException;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.core.io.FileSystemResource;
//import org.aksw.gerbil.transfer.nif.Document;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.google.common.base.Joiner;
import com.hp.hpl.jena.rdf.model.Model;

import de.dkt.common.filemanagement.FileFactory;
import de.dkt.common.niftools.DKTNIF;
import de.dkt.common.niftools.GEO;
import de.dkt.common.niftools.NIFReader;
import de.dkt.common.niftools.NIFWriter;
import de.dkt.common.niftools.TIME;
import de.dkt.eservices.eopennlp.timexRules.DateCommons;
import de.dkt.eservices.eopennlp.timexRules.EnglishDateRules;
import de.dkt.eservices.eopennlp.timexRules.GermanDateRules;
import eu.freme.common.exception.ExternalServiceFailedException;
import opennlp.tools.namefind.RegexNameFinder;
import opennlp.tools.util.Span;

/**
 * @author Julian Moreno Schneider julian.moreno_schneider@dfki.de, Peter Bourgonje peter.bourgonje@dfki.de
 *
 */
public class RegexFinder {
	
	static List<String> dateList = new ArrayList<String>();
	
	public static List<String> getDateList(){
		return dateList;
	}
	 
	//TODO: debug if this still works when dealing with B.c. dates!
	public static String getUltimateDate(String pos){
	
		List<Date> dl = DateCommons.convertStringsToDates(dateList);
		Date r = dl.get(0);
		for (Date d : dl){
			if (pos.equalsIgnoreCase("first")){
				if (d.before(r)){
					r = d;
				}		
			}
			else if (pos.equalsIgnoreCase("last")){
				if (d.after(r)){
					r = d;
				}
			}
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss"); // TODO: make this global in DateCommons, since it is declared in way too many places by now
		return sdf.format(r);
	}
	
	public static String ensureDateUnitLength(String dateUnit){
		if (dateUnit.length() == 1){
			dateUnit = "0" + dateUnit;
		}
		return dateUnit;
	}
	
	public static String getMeanDateRange(){
		/*
		 * Result is a date-range (String, -separated) that contains meanDate - stdDev and meanDate + stdDev
		 */
		Date avgDate = RegexFinder.getAverageDate();
		Integer stdDev = RegexFinder.getStandardDeviation(avgDate);
		
		Calendar loCal = Calendar.getInstance();
		loCal.setTime(avgDate);
		loCal.add(Calendar.DATE, (stdDev * -1));
		Date d1 = loCal.getTime();
		loCal.setTime(avgDate);
		loCal.add(Calendar.DATE, stdDev);
		Date d2 = loCal.getTime();
		
		SimpleDateFormat fullDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		return String.format("%s_%s", fullDateFormat.format(d1), fullDateFormat.format(d2));
		
	}
	
	public static Date getAverageDate(){
		
		if (dateList.size() == 0){
			return null;
		}
		List<Date> dl = DateCommons.convertStringsToDates(dateList);
		long totalMilliseconds = 0;
		for (Date d : dl){
			totalMilliseconds += d.getTime();
		}
		long avgMilliseconds = totalMilliseconds/dateList.size();
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(avgMilliseconds);
		
		return cal.getTime();
		
		/*
		String yearS = Integer.toString(cal.get(Calendar.YEAR));
		String monthS = Integer.toString(cal.get(Calendar.MONTH));
		String dayS = Integer.toString(cal.get(Calendar.DAY_OF_MONTH));
		String hourS = Integer.toString(cal.get(Calendar.HOUR));
		String minuteS = Integer.toString(cal.get(Calendar.MINUTE));
		String secondS = Integer.toString(cal.get(Calendar.SECOND));
		while (yearS.length() < 4){
			yearS = "0" + yearS;
		}
		monthS = ensureDateUnitLength(monthS);
		dayS = ensureDateUnitLength(dayS);
		hourS = ensureDateUnitLength(hourS);
		minuteS = ensureDateUnitLength(minuteS);
		secondS = ensureDateUnitLength(secondS);
		
		return String.format("%s%s%s%s%s%s", yearS, monthS, dayS, hourS, minuteS, secondS);
		*/
	}
	
	public static Integer getStandardDeviation(Date avgDate) {
		
		/*
		 * While calculating the average date is based on milliseconds since epoch, for standard deviation, the number of days is used. 
		 * This is because the stdev calculation involves the squared difference between date and mean. 
		 * sWhen doing this with milliseconds, I quickly hit the boundaries of long. This means that the approach below is not always 100% accurate. But it only serves as an indication anyways.
		 */
		Calendar avgCal = Calendar.getInstance();
		avgCal.setTime(avgDate);
		//try {
			//avgDate = sdf.parse(avg);
		//} catch (ParseException e) {
			//e.printStackTrace();
		//}
		
		List<Date> dl = DateCommons.convertStringsToDates(dateList);
		long totalSquareDiff = 0;
		long avgMillis = avgDate.getTime();
		for (Date d : dl){
			long diff = TimeUnit.DAYS.convert(d.getTime() - avgMillis, TimeUnit.MILLISECONDS);
			totalSquareDiff += Math.pow(diff,  2);
		}
		long meanSquaredDiff = totalSquareDiff / dateList.size();
		int stdDev = (int) Math.sqrt(meanSquaredDiff);
		
		return stdDev;
	}

	
	
	public static void evaluateAnnotatedFile(RegexNameFinder dateFinder, String inputFile, String language) throws ExternalServiceFailedException, IOException {
		
		String outputFileName = "C:\\Users\\pebo01\\Desktop\\debug.txt";
		FileWriter out = new FileWriter(outputFileName);
		
		float tp = 0;
		float fp = 0;
		float fn = 0;

		try{
			//FileSystemResource fsr = new FileSystemResource(inputFile);
			
			// communicate file creation time to dateFinder for normalization
			File f = FileFactory.generateFileInstance(inputFile);
			DateCommons.setInitialAnchorDate(Paths.get(f.getPath()));
			//DateCommons.setInitialAnchorDate(Paths.get(fsr.getPath()));
			
			String sentences[] = NameFinder.readLines(f.getPath());
			
			int c = 1;
			for (String sentence : sentences){
				
				c += 1;
				if (c % 100 == 0){
					Date d = new Date();
					//System.out.println("Processed " + Integer.toString(c) + " of " + Integer.toString(sentencesLength) + " (" + d.getTime() + ")");
				}
				
				// nameSpans now contains the timexes found by the regexes. (Should not matter that they are actually still annotated at this point) 
				List<Span> nameSpans = filterFind(dateFinder, sentence);
				
				
				
				//HashMap<Integer,String> annotations = extractAnnotationsMap(sentence);
				//HashMap<Integer,String> normalizations = extractNormalizationsMap(sentence);
				LinkedList<String> annotations = extractAnnotations(sentence);
				//LinkedList<String> annotations = extractAnnotationsWithNormalizations(sentence);
				HashMap<String,Integer> annotationsMap = list2HashMap(annotations);
				
				//System.out.println("DEBUGGING sentence:" + sentence);
				LinkedList<String> foundDates = new LinkedList<String>();
				for (Span ns : nameSpans){
					String foundDate = sentence.substring(ns.getStart(), ns.getEnd());
					if (!foundDate.equalsIgnoreCase("may")){
					//System.out.println("DEBUGGING foundDate:" + foundDate);
						foundDates.add(foundDate);
						//System.out.println("DEBUGGING foundDate normed:" + EnglishDateRules.normalizeEnglishDate(foundDate));
					}
				}
				HashMap<String,Integer> foundMap = list2HashMap(foundDates);
				
				for (Entry<String, Integer> am : annotationsMap.entrySet()) {
						 
					  String annKey = am.getKey();
					  int annCount = am.getValue();
					  int foundCount = 0;
					  if (foundMap.get(annKey) == null){
						  //default/0
					  }
					  else{
						  foundCount = foundMap.get(annKey);  
					  }

					  if (annCount == foundCount){
						  tp += annCount;
					  }
					  else if (annCount > foundCount){
						  tp += foundCount;
						  fn += annCount - foundCount;
						  out.write("False negative:" + annKey + "\n");
					  }
					  else if (foundCount > annCount){
						  tp += annCount;
						  fp += foundCount - annCount;
						  //out.write("False positive:" + annKey + "\n");
					  }
					 
					}
				for (Entry<String, Integer> fm : foundMap.entrySet()){
					String foundKey = fm.getKey();
					int foundCount = fm.getValue();
					int annCount = 0;
					if (annotationsMap.get(foundKey) == null){
						//default, 0
					}
					else{
						annCount = annotationsMap.get(foundKey);
					}
					// only check for false positives here, since the other scenarios should be covered by the loop above already
					if (foundCount > annCount){
						//System.out.println("DEBUGGING false positive:" + foundKey);
						fp += foundCount - annCount;
					}
				}
				
			}
				
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		float precision = tp / (tp + fp);
		float recall = tp / (tp + fn);
		
		float f = 2 * ((precision * recall) / (precision + recall));
		System.out.println("Scores for " + language + ":");
		System.out.println("Precision: " + precision);
		System.out.println("Recall   : " + recall);
		System.out.println("F-score  : " + f);

		out.close();
				
	}
	
	// converts list to map with string as key and count in list as value
	private static HashMap<String, Integer> list2HashMap(LinkedList<String> list){
		HashMap<String, Integer> m = new HashMap<String, Integer>();
		for (String item : list){
			int c = 0;
			for (String subItem : list){
				if (subItem.equals(item)){
					c += 1;
				}
			}
			m.put(item, c);
		}
		return m;
	}
	
	private static LinkedList<String> extractAnnotations(String sentence){
		LinkedList<String> annotations = new LinkedList<String>();
		
		Matcher m = Pattern.compile("<START:TIMEX>([^<]+)<END>").matcher(sentence); // wikiwars format: <START:TIMEX> 21. Juli <END>
		while (m.find()) {
			//Span as = new Span(m.start() + 14, m.end() - 6);
			annotations.add(m.group(1).trim());	
		}
		
		return annotations;
	}
	
	private static LinkedList<String> extractAnnotationsWithNormalizations(String sentence){
		LinkedList<String> annotations = new LinkedList<String>();
		
		Matcher m = Pattern.compile("<START:TIMEX_[0-9]+>([^<]+)<END>").matcher(sentence); // wikiwars format: <START:TIMEX> 21. Juli <END>
		while (m.find()) {
			//Span as = new Span(m.start() + 14, m.end() - 6);
			annotations.add(m.group(1).trim());	
		}
		
		return annotations;
	}
	
	public static Model detectEntitiesNIF(Model nifModel, String sentModel, String language, String inputFile) throws ExternalServiceFailedException, IOException {
		
		RegexNameFinder dateFinder = null;
		if (language.equalsIgnoreCase("de")){
			dateFinder = GermanDateRules.initGermanDateFinder();
		}
		else if (language.equalsIgnoreCase("en")){
			dateFinder = EnglishDateRules.initEnglishDateFinder();
		}
		// not sure if this will ever be specified, but if it is available, we can extract timestamp from file to set anchordate
		if(inputFile!=null){
			File f = FileFactory.generateFileInstance(inputFile);
			DateCommons.setInitialAnchorDate(Paths.get(f.getPath()));
		}
		else{
			DateCommons.setInitialAnchorDate();
		}
		
		String content = NIFReader.extractIsString(nifModel);
		Span[] sentenceSpans = SentenceDetector.detectSentenceSpans(content, sentModel);
		Joiner joiner = Joiner.on("_").skipNulls();
		for (Span sentenceSpan : sentenceSpans){
			String sentence = content.substring(sentenceSpan.getStart(), sentenceSpan.getEnd());
			//String tense = NLPCommons.getSentenceTense(sentence, language); // TODO: get sentence tense to decide if "Friday" refers to last or next Friday
			List<Span> nameSpans = filterFind(dateFinder, sentence);
			for (Span ns : nameSpans){
				int startIndex = sentenceSpan.getStart() + ns.getStart();
				int endIndex = sentenceSpan.getStart() + ns.getEnd();
				String foundDate = content.substring(startIndex, endIndex);
				// ugly, but important; hard-coded exception to not trigger on just "may", since in isolation it is much more likely to be a modal verb
				//if (!(foundDate == null)){
					if (!foundDate.equalsIgnoreCase("may")){
						LinkedList<String> normalizedStartAndEnd = new LinkedList<String>();
						if (language.equalsIgnoreCase("de")){
							normalizedStartAndEnd = GermanDateRules.normalizeGermanDate(foundDate);
						}
						else if (language.equalsIgnoreCase("en")){
							normalizedStartAndEnd = EnglishDateRules.normalizeEnglishDate(foundDate);
						}
						String normalization = joiner.join(normalizedStartAndEnd); 
						String entType = TIME.temporalEntity.toString();
						if (normalizedStartAndEnd.size() == 2){
							dateList.add(normalizedStartAndEnd.get(0));
							dateList.add(normalizedStartAndEnd.get(1));
							//String URIdummy = TIME.temporalEntity.toString() + "=" + normalization;
							//NIFWriter.addAnnotationEntity(nifModel, startIndex, endIndex, foundDate, URIdummy, entType);
							NIFWriter.addPrefixToModel(nifModel, "time", TIME.uri);
							NIFWriter.addTemporalEntity(nifModel, startIndex, endIndex, foundDate, normalization);
						}
					}
				//}
			}
			 
		}
		String docURI = NIFReader.extractDocumentURI(nifModel);
		if (dateList.size() > 0){
			String meanDateRange = getMeanDateRange();
			getDateList().clear();
			NIFWriter.addDateStats(nifModel, content, docURI, meanDateRange);
		}
		//String nifString = NIFReader.model2String(nifModel, "TTL");
		//HttpHeaders responseHeaders = new HttpHeaders();
		//responseHeaders.add("Content-Type", "RDF/XML");
		//return new ResponseEntity<String>(nifString, responseHeaders, HttpStatus.OK);
		return nifModel;

	}

	
	
	private static List<Span> filterFind(RegexNameFinder timeFinder, String input) {
		
		Span[] timeSpans = timeFinder.find(input);
		
		List<Span> spanList = new LinkedList<Span>();
		for (Span span : timeSpans) {
			spanList.add(span);
			//System.out.println("DEBUGGING span:" + input.substring(span.getStart(), span.getEnd()));
		}
		//sort list by spanLength first
		Comparator<? super Span> SpanLengthComparator = null;
		spanList.sort(SpanLengthComparator);
		
		//then remove subspans
		boolean trick = false;
		for (int i = 0; i < spanList.size(); i++){
			trick = false;
			Span ns1 = spanList.get(i);
			
			for (int j = i+1 ; j < spanList.size(); j++){
				Span ns2 = spanList.get(j);
				if ((ns2.getStart() > ns1.getStart() && ns2.getEnd() <= ns1.getEnd()) || ns2.getStart() >= ns1.getStart() && ns2.getEnd() < ns1.getEnd()){
					spanList.remove(j);
					j--;
					trick = true;
				}
			}
			if (trick){
				i--;
			}
		}
		
		return spanList;
	}
	
	public abstract class SpanLengthComparator implements Comparator<Span> {
		public int compare(Span s1, Span s2) {
			int s1Length = s1.getEnd() - s1.getStart();
			int s2Length = s2.getEnd() - s2.getStart();
			if (s1Length < s2Length) {
				return -1;
			} else if (s1Length > s2Length) {
				return 1;
			} else {
				return 0;
			}
		}
	}

	public static void main(String[] args) throws ExternalServiceFailedException, IOException {
		
		RegexNameFinder dateFinderDE = GermanDateRules.initGermanDateFinder();
		RegexNameFinder dateFinderEN = EnglishDateRules.initEnglishDateFinder();
		
		//evaluateAnnotatedFile(dateFinderEN, "C:\\Users\\pebo01\\Desktop\\ubuntuShare\\wikiwarsEN - Copy.txt", "en");
		//evaluateAnnotatedFile(dateFinderEN, "C:\\Users\\pebo01\\Desktop\\ubuntuShare\\timeMLAquaint_opennlpformat - Copy.txt", "en");
		//evaluateAnnotatedFile(dateFinderDE, "C:\\Users\\pebo01\\Desktop\\ubuntuShare\\wikiwarsTimexes.txt", "de");
		
		
		
		//File f = FileFactory.generateFileInstance("C:\\Users\\pebo01\\Desktop\\ubuntuShare\\timeMLAquaint_opennlpformat.txt");
		//String textToProcess = FileReadUtilities.readFile2String(f.getPath());
		//DateCommons.setInitialAnchorDate();
//		String textToProcess = "Allenstein 25-05-2016 weihnacht";
//		//List<Span> nameSpans = filterFind(dateFinderEN, textToProcess);
//		List<Span> nameSpans = filterFind(dateFinderDE, textToProcess);
//		for (Span ns : nameSpans){
//			int startIndex = ns.getStart();
//			int endIndex = ns.getEnd();
//			String foundDate = textToProcess.substring(startIndex, endIndex);
//			//LinkedList<String> normalizedStartAndEnd = EnglishDateRules.normalizeEnglishDate(foundDate);
//			LinkedList<String> normalizedStartAndEnd = GermanDateRules.normalizeGermanDate(foundDate);
//			
//			System.out.println("DEBUGGING date found: " + foundDate);
//			System.out.println("DEBUGGING norm found: " + normalizedStartAndEnd);
//			if (normalizedStartAndEnd.size() > 1){
//				dateList.add(normalizedStartAndEnd.get(0) + "_" + normalizedStartAndEnd.get(1));
//			}
//		}
		//Date avg = getAverageDate();
		//System.out.println(avg);
		
		
		RegexNameFinder timeFinder= GermanDateRules.initGermanDateFinder();
		
		

		String input = "tag der deutschen einheit 1990";
		//Span[] timeSpans = filterFind(timeFinder, input);

		RegexNameFinder timeFinder= GermanDateRules.initGermanDateFinder();
		
		RegexNameFinder timeFinder= GermanDateRules.initGermanDateFinder();
		
		RegexNameFinder timeFinder= GermanDateRules.initGermanDateFinder();
		
		

		String input = "Es war mal Montag";
		//Span[] timeSpans = filterFind(timeFinder, input);
		List<Span> timeSpans = filterFind(timeFinder, input);
		for (Span s : timeSpans){
			System.out.println("DEBUGGING:" + input.substring(s.getStart(), s.getEnd()));
			GermanDateRules.normalizeGermanDate(input.substring(s.getStart(), s.getEnd()));
		}
		

		

		
		
		//String inputFile = "C:\\Users\\pebo01\\Desktop\\ubuntuShare\\wikiwarsDe.test.txt";
		//String inputFile = "C:\\Users\\pebo01\\Desktop\\ubuntuShare\\aij-wikiner-de.test.txt";
		//List<Document> documents = new LinkedList<Document>();
		//RegexFinder.detectEntitiesInFile(inputFile, "de-token.bin", "de-sent.bin", "de", documents);
		
		/*
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MMM-dd");
		String dateString = "2015-dec-1";
		try {
			Date date = formatter.parse(dateString);
			System.out.println("DEBUGGING date normalization:" + date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
		Date d1  = new Date(-1875,1,1);
		Date d2  = new Date(116,6,13);
		

		//System.out.println(d2.getTime()-d1.getTime());
		
	}




}
