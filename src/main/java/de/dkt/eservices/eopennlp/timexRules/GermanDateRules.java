package de.dkt.eservices.eopennlp.timexRules;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import org.hibernate.engine.transaction.jta.platform.internal.SynchronizationRegistryBasedSynchronizationStrategy;

import opennlp.tools.namefind.RegexNameFinder;


/**
 * @author Julian Moreno Schneider julian.moreno_schneider@dfki.de, Peter Bourgonje peter.bourgonje@dfki.de
 *
 */

// TODO: once we include time expressions as well (rather than just date expressions), think about the endDates, as currently it's the same day. But it should be 23:59 of that day I guess

public class GermanDateRules {

	static HashMap<Integer, String> germanDateRegexMap = new HashMap<Integer, String>();
	static HashMap<String, Integer> germanMonthName2Number = new HashMap<String, Integer>(){{
		put("januar", 1);
		put("februar", 2);
		put("märz", 3);
		put("april", 4);
		put("mai", 5);
		put("juni", 6);
		put("juli", 7);
		put("august", 8);
		put("september", 9);
		put("oktober", 10);
		put("november", 11);
		put("dezember", 12);
	}};

	static HashMap<String, Integer> romanMonth2Number = new HashMap<String, Integer>(){{
		put("I", 1);
		put("II", 2);
		put("III", 3);
		put("IV", 4);
		put("V", 5);
		put("VI", 6);
		put("VII", 7);
		put("VIII", 8);
		put("IX", 9);
		put("X", 10);
		put("XI", 11);
		put("XII", 12);
	}};

	static HashMap<String, Integer> germanDayName2Integer = new HashMap<String, Integer>(){{
		put("sonntag", 1);
		put("montag", 2);
		put("dienstag", 3);
		put("mittoch", 4);
		put("donnerstag", 5);
		put("freitag", 6);
		put("samstag", 7);
	}};
	
	
	

	
	public static RegexNameFinder initGermanDateFinder(){

		final String monthName = "(?i)(januar|februar|märz|april|mai|juni|juli|august|september|oktober|november|dezember)";
		final String monthNumber = "(0?[0-9]|1[0-2])";
		final String monthRomanNumber = "[IVX]+";
		
		final String dayName = "(?i)(montag|dienstag|mittwoch|donnerstag|freitag|samstag|sonnabend|sonntag)";
		final String dayNumber = "(0?[1-9]|1[0-9]|2[0-9]|3[0-1])";
		
		final String yearNumber = "([1-2]\\d{3})"; //FIXME: note that this doesn't work for (pre-)medieval year indications (containing only 3 (or less) digits. 
		final String nineteenHundred_twoDigitYearNumber = "[0-9][0-9]";
		
		final String beforeChrist = "(?i)\\d{1,4} v\\. Chr\\.";
		
		final String dayPartName = "(?i)(morgen|gestern)?(morgens?|(vor|nach)?mittags?|abends?|nachts?)";
		
		final String day = "(?i)tage?";
		final String week = "(?i)wochen?";
		final String month = "(?i)monate?";
		final String year = "(?i)jahre?";
		
		final String season = "(?i)(frühjahr|frühling|sommer|herbst|winter)";
		
		final String alphaNumber = "(?i)(ein(s|en)?|zwei|drei|vier|fünf|sechs|sieben|acht|neun|zehn|elf|zwölf|dreizehn|vierzehn|fünfzehn|sechzehn|siebzehn|achtzehn|neunzehn|zwanzig|einundzwanzig|zweiundzwanzig|dreiundzwanzig|vierundzwanzig|fünfundzwanzig|sechsundzwanzig|siebenundzwanzig|achtundzwanzig|neunundzwanzig|dreiβig|einunddreiβig|zweiundreißig|dreiunddreißig|vierunddreißig|fünfunddreißig|sechsunddreißig|siebenunddreißig|achtunddreißig|neununddreißig|vierzig|einundvierzig|zweiundvierzig|dreiundvierzig|vierundvierzig|fünfundvierzig|sechsundvierzig|siebenundvierzig|achtundvierzig|neunundvierzig|fünfzig|einundfünfzig|zweiundfünfzig|dreiundfünfzig|vierundfünfzig|fünfundfünfzig|sechsundfünfzig|siebenundfünfzig|achtundfünfzig|neunundfünfzig|sechzig|einundsechzig|zweiundsechzig|dreiundsechzig|vierundsechzig|fünfundsechzig|sechsundsechzig|siebenundsechzig|achtundsechzig|neunundsechzig|siebzig|einundsiebzig|zweiundsiebzig|dreiundsiebzig|vierundsiebzig|fünfundsiebzig|sechsundsiebzig|siebenundsiebzig|achtundsiebzig|neunundsiebzig|achtzig|einundachtzig|zweiundachtzig|dreiundachtzig|vierundachtzig|fünfundachtzig|sechsundachtzig|siebenundachtzig|achtundachtzig|neunundachtzig|neunzig|einundneunzig|zweiundneunzig|dreiundneunzig|vierundneunzig|fünfundneunzig|sechsundneunzig|siebenundneunzig|achtundneunzig|neunundneunzig|(ein)?hundert)";
		
		final String anfangMitteEnde = "(?i)(anfang|mitte|ende)";
		final String gegenwärtig = "(?i)(gegenwärtig)";
		final String jetzt = "(?i)jetzt";
		final String heute = "(?i)heute?";
		//final String bald = "(?i)bald";
		
		final String holidays = "(?i)(weihnacht(en|stag)?|ostern|himmelfahrt|pfingsten|silvester(nacht)?|neujahr(stag)?|maifeiertag|tag der arbeit|tag der deutschen einheit)"; //TODO I probably forgot some
		final String zeitpunkt = "(?i)zeitpunkt";
		
		final String gestern = "(?i)(vor)?gestern";
		final String morgen = "(?i)(über)?morgen";
		
		//TODO: think about most efficient way to do word boundaries 
		
		
		// this feels a bit abundant, but is used for normalization afterwards, so I know which regex matches...
		//HashMap<String, String> regexMap = new HashMap<String, String>() {{
		germanDateRegexMap.put(1, String.format("\\b%s\\.? %s(,? %s)?\\b", dayNumber, monthName, yearNumber));
		germanDateRegexMap.put(2, String.format("\\b(%s )?%s\\b", year, yearNumber));
		germanDateRegexMap.put(3, String.format("\\b%s,? %s\\b", monthName, yearNumber));
		germanDateRegexMap.put(4, String.format("\\b%s[-/\\.]%s[-/\\.]%s\\b", yearNumber, monthNumber, dayNumber));
		germanDateRegexMap.put(5, String.format("\\b%s[-/\\.]%s[-/\\.]%s\\b", dayNumber, monthNumber, yearNumber));
		germanDateRegexMap.put(6, String.format("\\b%s[-/\\.]%s\\b", monthNumber, yearNumber));
		germanDateRegexMap.put(7, String.format("\\b%s", beforeChrist));
		germanDateRegexMap.put(8, String.format("\\b%s\\b", dayPartName));
		germanDateRegexMap.put(9, String.format("\\b%s %s( %s)?\\b", anfangMitteEnde, monthName, yearNumber)); // check this one; add Mitte/End Oktober? // NOTE: linking this one with the one below (just monthName) causes problems. TODO: debug to see what's going on
		germanDateRegexMap.put(10, String.format("\\b%s\\b", monthName));
		germanDateRegexMap.put(11, String.format("\\b%s\\b", day));
		germanDateRegexMap.put(12, String.format("\\b%s( %s)?\\b", season, yearNumber));
		germanDateRegexMap.put(13, String.format("\\b(%s) (%s|%s|%s|%s) ((zu)?vor|später|danach)\\b", alphaNumber,day, week, month, year));
		germanDateRegexMap.put(14, String.format("(?i)\\b%s %s vor (jahresende|monatsende)\\b", alphaNumber, day));
		germanDateRegexMap.put(15, String.format("\\b%s\\b", gegenwärtig));
		germanDateRegexMap.put(16, String.format("\\bnächste(r|s|n)? (%s|%s|%s|%s)\\b", day, week, month, year));
		germanDateRegexMap.put(17, String.format("(?i)\\bdiese(s|r|m|n)? (%s|%s|%s|%s)\\b", day, week, month, year));
		//germanDateRegexMap.put(18, String.format("\\b(%s|%s)\\b", jetzt, heute));
		germanDateRegexMap.put(19, String.format("\\b%s( %s)?\\b", holidays, yearNumber));
		germanDateRegexMap.put(20, String.format("\\b(\\d{1,3}) %s\\b", day));
		germanDateRegexMap.put(21, String.format("\\b%s %s\\b", anfangMitteEnde, yearNumber));
		germanDateRegexMap.put(22, String.format("(?i)\\bnach %s (%s|%s|%s|%s)\\b", alphaNumber, day, week, month, year));
		germanDateRegexMap.put(23, String.format("(?i)\\b(am|im) selben (tag|monat)\\b"));
		germanDateRegexMap.put(24, String.format("\\b%s[-/\\.]%s[-/\\.]%s\\b", dayNumber, monthRomanNumber, nineteenHundred_twoDigitYearNumber));
		germanDateRegexMap.put(25, String.format("\\b%s([- ]?%s)?\\b", dayName, dayPartName));
		germanDateRegexMap.put(26, String.format("\\b%s\\b", heute));
		germanDateRegexMap.put(27, String.format("\\b%s\\b", jetzt));
		germanDateRegexMap.put(28, String.format("\\b%s\\b", gestern));
		germanDateRegexMap.put(29, String.format("\\b%s\\b", morgen));

	
		
		Pattern[] regexes = {
			Pattern.compile(germanDateRegexMap.get(1)),	
			Pattern.compile(germanDateRegexMap.get(2)),
			Pattern.compile(germanDateRegexMap.get(3)),
			Pattern.compile(germanDateRegexMap.get(4)),
			Pattern.compile(germanDateRegexMap.get(5)),
			Pattern.compile(germanDateRegexMap.get(6)),
			Pattern.compile(germanDateRegexMap.get(7)),
			Pattern.compile(germanDateRegexMap.get(8)),
			Pattern.compile(germanDateRegexMap.get(9)),
			Pattern.compile(germanDateRegexMap.get(10)),
			Pattern.compile(germanDateRegexMap.get(11)),
			Pattern.compile(germanDateRegexMap.get(12)),
			Pattern.compile(germanDateRegexMap.get(13)),
			Pattern.compile(germanDateRegexMap.get(14)),
			Pattern.compile(germanDateRegexMap.get(15)),
			Pattern.compile(germanDateRegexMap.get(16)),
			Pattern.compile(germanDateRegexMap.get(17)),
			//Pattern.compile(germanDateRegexMap.get(18)),
			Pattern.compile(germanDateRegexMap.get(19)),
			Pattern.compile(germanDateRegexMap.get(20)),
			Pattern.compile(germanDateRegexMap.get(21)),
			Pattern.compile(germanDateRegexMap.get(22)),
			Pattern.compile(germanDateRegexMap.get(23)),
			Pattern.compile(germanDateRegexMap.get(24)),
			Pattern.compile(germanDateRegexMap.get(25)),
			Pattern.compile(germanDateRegexMap.get(26)),
			Pattern.compile(germanDateRegexMap.get(27)),
			Pattern.compile(germanDateRegexMap.get(28)),
			Pattern.compile(germanDateRegexMap.get(29))
		};
	
		RegexNameFinder rnf = new RegexNameFinder(regexes, null);
		return rnf;

	}

	public static LinkedList<String> normalizeGermanDate(String foundDate) {
	
		// this is directly coupled to the dateRegexMap thing. If that changes, this needs to be checked too
		Date normalizedStartDate = new Date();
		Date normalizedEndDate = new Date();
		Calendar cal = Calendar.getInstance();
		
		Iterator it = germanDateRegexMap.entrySet().iterator();
		LinkedList<String> dates = new LinkedList<String>();
		
		
		while (it.hasNext()){
			Map.Entry pair = (Map.Entry)it.next();
			int key = (Integer) pair.getKey();
			String p = (String) pair.getValue();
			// compile with ^ and $ since I want the whole match/no submatches (not sure if this works though, debug!)
			if (Pattern.matches(String.format("^%s$", p), foundDate)){
				//System.out.println("Matching with key:" + key);
				//dateRegexMap.put(1, String.format("\\b%s\\.? %s(,? %s)?\\b", dayNumber, monthName, yearNumber));
				if (key == 1){
					foundDate = foundDate.replaceAll("\\.", "");
					foundDate = foundDate.replaceAll(",", "");
					String[] parts = foundDate.split("\\s");
					int dayNumber = 1;
					int monthNumber = 1;
					int yearNumber = DateCommons.getYearFromAnchorDate();
					if (parts.length == 3){
						dayNumber = Integer.parseInt(parts[0]);
						monthNumber = DateCommons.getMonthNumber(parts[1].toLowerCase(), germanMonthName2Number);
						yearNumber = Integer.parseInt(parts[2]);
						
					}
					else if (parts.length == 2){
						dayNumber = Integer.parseInt(parts[0]);
						monthNumber = DateCommons.getMonthNumber(parts[1].toLowerCase(), germanMonthName2Number);
						yearNumber = DateCommons.getYearFromAnchorDate();
					}
					cal.set(yearNumber,  monthNumber-1, dayNumber,0,0,0);
					normalizedStartDate = cal.getTime();
					normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, 1, normalizedStartDate);
					dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
					dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
					DateCommons.updateAnchorDate(normalizedStartDate);
					
					
				}
				
				//dateRegexMap.put(2, String.format("\\b(%s )?%s\\b", year, yearNumber));
				if (key == 2){
					String[] parts = foundDate.split("\\s");
					int yearNumber = Integer.parseInt(parts[parts.length-1]);
					int monthNumber = 1;
					int dayNumber = 1;
					cal.set(yearNumber,  monthNumber-1, dayNumber,0,0,0);
					normalizedStartDate = cal.getTime();
					normalizedEndDate = DateCommons.increaseCalendar(Calendar.YEAR, 1, normalizedStartDate);
					dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
					dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
					DateCommons.updateAnchorDate(normalizedStartDate);
				}
				
				
				//dateRegexMap.put(3, String.format("\\b%s,? %s\\b", monthName, yearNumber));
				if (key == 3){
					foundDate = foundDate.replaceAll(",", "");
					String[] parts = foundDate.split("\\s");
					int dayNumber = 1;
					int monthNumber = DateCommons.getMonthNumber(parts[0].toLowerCase(), germanMonthName2Number);
					int yearNumber = Integer.parseInt(parts[1]);
					
					cal.set(yearNumber,  monthNumber-1, dayNumber,0,0,0);
					normalizedStartDate = cal.getTime();
					normalizedEndDate = DateCommons.increaseCalendar(Calendar.MONTH, 1, normalizedStartDate);
					dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
					dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
					DateCommons.updateAnchorDate(normalizedStartDate);
				}
				
				//dateRegexMap.put(4, String.format("\\b%s[-/\\.]%s[-/\\.]%s\\b", yearNumber, monthNumber, dayNumber));
				if (key == 4){
					String[] parts = foundDate.split("[-/\\.]");
					int yearNumber = Integer.parseInt(parts[0]);
					int monthNumber = Integer.parseInt(parts[1]);
					int dayNumber = Integer.parseInt(parts[2]);
					cal.set(yearNumber,  monthNumber-1, dayNumber,0,0,0);
					normalizedStartDate = cal.getTime();
					normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, 1, normalizedStartDate);
					dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
					dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
					DateCommons.updateAnchorDate(normalizedStartDate);
				}
				
				//dateRegexMap.put(5, String.format("\\b%s[-/\\.]%s[-/\\.]%s\\b", dayNumber, monthNumber, yearNumber));
				if (key == 5){
					String[] parts = foundDate.split("[-/\\.]");
					int dayNumber = Integer.parseInt(parts[0]);
					int monthNumber = Integer.parseInt(parts[1]);
					int yearNumber = Integer.parseInt(parts[2]);
					cal.set(yearNumber,  monthNumber-1, dayNumber,0,0,0);
					normalizedStartDate = cal.getTime();
					normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, 1, normalizedStartDate);
					dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
					dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
					DateCommons.updateAnchorDate(normalizedStartDate);
				}
				
				//dateRegexMap.put(6, String.format("\\b%s[-/\\.]%s\\b", monthNumber, yearNumber));
				if (key == 6){
					String[] parts = foundDate.split("[-/\\.]");
					int monthNumber = Integer.parseInt(parts[0]);
					int yearNumber = Integer.parseInt(parts[1]);
					int dayNumber = 1;
					cal.set(yearNumber,  monthNumber-1, dayNumber,0,0,0);
					normalizedStartDate = cal.getTime();
					normalizedEndDate = DateCommons.increaseCalendar(Calendar.MONTH, 1, normalizedStartDate);
					dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
					dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
					DateCommons.updateAnchorDate(normalizedStartDate);
				}
				
				//dateRegexMap.put(7, String.format("\\b%s\\b", beforeChrist));
				if (key == 7){
					String[] parts = foundDate.split("\\s");
					int yearNumberBeforeChrist = Integer.parseInt(parts[0]);
					int yearSubtract = cal.get(Calendar.YEAR) + yearNumberBeforeChrist;
					cal.add(Calendar.YEAR, -yearSubtract);
					normalizedStartDate = cal.getTime();
					dates.add(DateCommons.fullDateFormat.format(normalizedStartDate)); // start date
					cal.add(Calendar.YEAR, 1);
					normalizedEndDate = cal.getTime();
					dates.add(DateCommons.fullDateFormat.format(normalizedEndDate)); // end date
					DateCommons.updateAnchorDate(normalizedEndDate);
				}
				
				
				//dateRegexMap.put(8, String.format("\\b%s\\b", dayPartName));
				//final String dayPartName = "(?i)(morgen|gestern)?(morgens?|(vor|nach)?mittags?|abends?|nachts?)";
				if (key == 8){
					
					int dayNumber = DateCommons.getDayFromAnchorDate();
					int monthNumber = DateCommons.getMonthFromAnchorDate();
					int yearNumber = DateCommons.getYearFromAnchorDate();
					int hour = 0;
					//cal.set(yearNumber,  monthNumber, dayNumber,0,0,0);
					if(foundDate.contains("morgen")){
						hour = 6;
					}
					else if (foundDate.contains("mittag")){
						hour = 12; 
					}
					else if (foundDate.contains("abend")){
						hour = 18;
					}
					else if (foundDate.contains("nacht")){
						hour = 0;
						dayNumber += 1;
					}
					if (foundDate.toLowerCase().startsWith("morgen")){
						dayNumber += 1;
					}
					else if (foundDate.toLowerCase().startsWith("gestern")){
						dayNumber -= 1;
					}
					else{
						// do nothing
					}
					cal.set(yearNumber,  monthNumber, dayNumber, hour, 0, 0);
					normalizedStartDate = cal.getTime();
					normalizedEndDate = DateCommons.increaseCalendar(Calendar.HOUR, 6, normalizedStartDate);
					dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
					dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
					DateCommons.updateAnchorDate(normalizedStartDate);
					
				}
				
				//dateRegexMap.put(9, String.format("\\b%s %s( %s)?\\b", anfangMitteEnde, monthName, yearNumber));
				if (key == 9){
					String[] parts = foundDate.split("\\s");
					String dayPartIndication = parts[0];
					int monthNumber = DateCommons.getMonthNumber(parts[1].toLowerCase(), germanMonthName2Number);
					int yearNumber = DateCommons.getYearFromAnchorDate();
					int dayNumber = 1;
					if (parts.length == 3){
						yearNumber = Integer.parseInt(parts[2]);
					}
					else{
						yearNumber = DateCommons.getYearFromAnchorDate();
					}
					if (dayPartIndication.toLowerCase().contains("anfang")){ // redundant...
						dayNumber = 1;
					}
					else if (dayPartIndication.toLowerCase().contains("mitte")){
						dayNumber = 10;
					}
					else if (dayPartIndication.toLowerCase().contains("ende")){
						dayNumber = DateCommons.getLastDayOfMonth(monthNumber, yearNumber) - 10;
					}
					cal.set(yearNumber,  monthNumber-1, dayNumber,0,0,0);
					normalizedStartDate = cal.getTime();
					normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, 10, normalizedStartDate);
					dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
					dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
					DateCommons.updateAnchorDate(normalizedStartDate);
				}
				
				//dateRegexMap.put(10, String.format("\\b%s\\b", monthName));
				if (key == 10){
					int monthNumber = DateCommons.getMonthNumber(foundDate.toLowerCase(), germanMonthName2Number);
					int yearNumber = DateCommons.getYearFromAnchorDate();
					int dayNumber = 1;
					cal.set(yearNumber,  monthNumber-1, dayNumber,0,0,0);
					normalizedStartDate = cal.getTime();
					normalizedEndDate = DateCommons.increaseCalendar(Calendar.MONTH, 1, normalizedStartDate);
					dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
					dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
					DateCommons.updateAnchorDate(normalizedStartDate);
				}
				
				
				//dateRegexMap.put(11, String.format("\\b%s\\b", day));
				if (key == 11){
					
					//TODO: anchoring with the help of tense of the sentence, to decide if day should be next of last monday/tuesday/wednesday...
					
				}
				
				//germanDateRegexMap.put(12, String.format("\\b%s( %s)?\\b", season, yearNumber));
				if (key == 12){
					String[] parts = foundDate.split("\\s");
					String season = parts[0];
					int yearNumber = DateCommons.getYearFromAnchorDate();
					if (parts.length > 1){
						yearNumber = Integer.parseInt(parts[1]);	
					}
					else{
						yearNumber = DateCommons.getYearFromAnchorDate();
					}
					
					// taking the "meteorological seasons" here
					//final String season = "(?i)(frühjahr|sommer|herbst|winter)";
					int startDay = 1;
					int startMonth = 1;
					int endDay = 1;
					int endMonth = 1;
					if (season.equalsIgnoreCase("frühjahr") || season.equalsIgnoreCase("frühling")){
						startMonth = 3;
						endDay = 31;
						endMonth = 5;
					}
					else if (season.equalsIgnoreCase("sommer")){
						startMonth = 6;
						endDay = 31;
						endMonth = 8;
					}
					else if (season.equalsIgnoreCase("herbst")){
						startMonth = 9;
						endDay = 30;
						endMonth = 11;
					}
					else if (season.equalsIgnoreCase("winter")){
						startMonth = 12;
						endDay = 28;
						if (((GregorianCalendar) cal).isLeapYear(yearNumber)){
							endDay= 29;
						}
						endMonth = 2;
					}
					if (season.equalsIgnoreCase("winter")){
						cal.set(yearNumber-1, startMonth-1, startDay,0,0,0);
					}
					else{
						cal.set(yearNumber, startMonth-1, startDay,0,0,0);
					}
					normalizedStartDate = cal.getTime();
					cal.set(yearNumber, endMonth-1, endDay,0,0,0);
					normalizedEndDate = cal.getTime();
					dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
					dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
					DateCommons.updateAnchorDate(normalizedStartDate);
				}
				
				//dateRegexMap.put(13, String.format("\\b(%s) (%s|%s|%s) ((zu)?vor|später|danach)\\b", oneDigitNumber,days, months, years));
				if (key == 13){
					String[] parts = foundDate.split("\\s");
					int number = convertGermanAlphaNumber(parts[0]);
					final String day = "(?i)tage?";
					final String week = "(?i)wochen?";
					final String month = "(?i)monate?";
					final String year = "(?i)jahre?";
					
					int monthNumber = DateCommons.getMonthFromAnchorDate();
					int yearNumber = DateCommons.getYearFromAnchorDate();
					int dayNumber = DateCommons.getDayFromAnchorDate();
					
					cal.set(yearNumber, monthNumber, dayNumber, 0,0,0);
					Date current = cal.getTime();
					String direction = null;
					if (parts[2].matches("(zu)?vor")){
						direction = "backwards";
					}
					else if (parts[2].matches("später|danach")){
						direction = "forwards";
					}
					
					if (parts[1].matches(day)){
						if (direction.equals("backwards")){
							normalizedStartDate = DateCommons.increaseCalendar(Calendar.DATE, number * -1, current);
							normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, -1, normalizedStartDate);
							//cal.add(Calendar.DATE, number * -1);
						}
						else if (direction.equals("forwards")){
							normalizedStartDate = DateCommons.increaseCalendar(Calendar.DATE, number, current);
							normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, 1, normalizedStartDate);
							//cal.add(Calendar.DATE, number);
						}
					}
					else if (parts[1].matches(week)){
						if (direction.equals("backwards")){
							normalizedStartDate = DateCommons.increaseCalendar(Calendar.DATE, number * -7, current);
							normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, -7, normalizedStartDate);
							//cal.add(Calendar.DATE, number * -7);
						}
						else if (direction.equals("forwards")){
							normalizedStartDate = DateCommons.increaseCalendar(Calendar.DATE, number * 7, current);
							normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, 7, normalizedStartDate);
							//cal.add(Calendar.DATE, number * 7);
						}
					}
					else if (parts[1].matches(month)){
						if (direction.equals("backwards")){
							normalizedStartDate = DateCommons.increaseCalendar(Calendar.MONTH, number * -1, current);
							normalizedEndDate = DateCommons.increaseCalendar(Calendar.MONTH, -1, normalizedStartDate);
							//cal.add(Calendar.MONTH, number * -1);
						}
						else if (direction.equals("forwards")){
							normalizedStartDate = DateCommons.increaseCalendar(Calendar.MONTH, number, current);
							normalizedEndDate = DateCommons.increaseCalendar(Calendar.MONTH, 1, normalizedStartDate);
							//cal.add(Calendar.MONTH, number);
						}
					}
					else if (parts[1].matches(year)){
						if (direction.equals("backwards")){
							normalizedStartDate = DateCommons.increaseCalendar(Calendar.YEAR, number * -1, current);
							normalizedEndDate = DateCommons.increaseCalendar(Calendar.YEAR, -1, normalizedStartDate);
							//cal.add(Calendar.YEAR, number * -1);
						}
						else if (direction.equals("forwards")){
							normalizedStartDate = DateCommons.increaseCalendar(Calendar.YEAR, number, current);
							normalizedEndDate = DateCommons.increaseCalendar(Calendar.YEAR, 1, normalizedStartDate);
							//cal.add(Calendar.YEAR, number);
						}
						
					}
					//normalizedEndDate = cal.getTime();
					dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
					dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
					DateCommons.updateAnchorDate(normalizedEndDate);
					
				}
				
				//dateRegexMap.put(14, String.format("(?i)\\b%s %s vor (jahresende|monatsende)\\b", oneDigitNumber, days)); //TODO: add months? (weeks?)
				if (key == 14){

					int yearNumber = DateCommons.getYearFromAnchorDate();
					int monthNumber = DateCommons.getMonthFromAnchorDate();
					String[] parts = foundDate.split("\\s");
					int numberOfDays = convertGermanAlphaNumber(parts[0]);
					
					if (parts[3].matches("(?i)jahresende")){
						cal.set(yearNumber,  11, 31, 0,0,0); // TODO: DEBUG MONTHNUMBER!!!!
						cal.add(Calendar.DATE, numberOfDays * -1);
						normalizedStartDate = cal.getTime();
						normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, 1, normalizedStartDate);
					}
					else if (parts[3].matches("(?i)monatsende")){
						cal.set(yearNumber, monthNumber, DateCommons.getLastDayOfMonth(monthNumber+1, yearNumber), 0,0,0); // +1 for monthNumber here because the monthNumber coming from the anchorDate is zero-based
						cal.add(Calendar.DATE, numberOfDays * -1);
						normalizedStartDate = cal.getTime();
						normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, 1, normalizedStartDate);
					}
					dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
					dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
					DateCommons.updateAnchorDate(normalizedStartDate);
				}
				
				//dateRegexMap.put(15, String.format("\\b%s\\b", gegenwärtig));
				if (key == 15){
					// if we have metadata on document creation date, use that. In the meantime:
					Calendar currentCal = Calendar.getInstance();
					normalizedStartDate = currentCal.getTime();
					normalizedEndDate = normalizedStartDate;
					dates.add(DateCommons.fullDateFormat.format(normalizedStartDate)); // start date
					dates.add(DateCommons.fullDateFormat.format(normalizedEndDate)); // end date
				}
				
				//germanDateRegexMap.put(16, String.format("\\bnächste(r|s|n)? (%s|%s|%s|%s)\\b", day, week, month, year));
				if (key == 16){
					//TODO: anchoring
				}
				
				//germanDateRegexMap.put(17, String.format("(?i)\\bdiese(s|r|m|n)? (%s|%s|%s|%s)\\b", day, week, month, year));
				if (key == 17){
					//TODO: anchoring
				}
				
				
				//dateRegexMap.put(18, String.format("\\b(%s|%s)\\b", jetzt, heute));
				if (key == 18){
					Calendar currentCal = Calendar.getInstance();
					normalizedStartDate = currentCal.getTime();
					normalizedEndDate = normalizedStartDate;
					dates.add(DateCommons.fullDateFormat.format(normalizedStartDate)); // start date
					dates.add(DateCommons.fullDateFormat.format(normalizedEndDate)); // end date
				}
				
				
				//germanDateRegexMap.put(19, String.format("\\b%s( %s)?\\b", holidays, yearNumber));
				
				if (key == 19){
					
					int yearNumber = DateCommons.getYearFromAnchorDate();
					int monthNumber = 1;
					int dayNumber = 1;
					String[] parts = foundDate.split("\\s");
					
					if (parts[parts.length-1].matches("([1-2]\\d{3})")){
						yearNumber = Integer.parseInt(parts[parts.length-1]);
					}
					else{
						yearNumber = DateCommons.getYearFromAnchorDate();
					}
					//final String holidays = "(?i)(weihnacht(en|stag)?|ostern|himmelfahrt|pfingsten
					//|silvester(nacht)?|neujahr(stag)?|
					//maifeiertag|tag der deutschen einheit"
					
					if (parts[0].toLowerCase().contains("weihnacht")){
						monthNumber = 12;
						dayNumber = 25;
						cal.set(yearNumber,  monthNumber-1, dayNumber,0,0,0);
						normalizedStartDate = cal.getTime();
					}
					else if (parts[0].matches("(?i)ostern")){
						normalizedStartDate = DateCommons.getEasterDate(yearNumber);
					}
					else if (parts[0].matches("(?i)himmelfahrt")){
						normalizedStartDate = DateCommons.getAscensionDate(yearNumber);
					}
					else if (parts[0].matches("(?i)pfingsten")){
						normalizedStartDate = DateCommons.getPentecostDate(yearNumber);
					}
					else if (parts[0].toLowerCase().contains("silvester")){
						monthNumber = 12;
						dayNumber = 31;
						cal.set(yearNumber,  monthNumber-1, dayNumber,0,0,0);
						normalizedStartDate = cal.getTime();
					}
					else if (parts[0].toLowerCase().contains("neujahr")){
						monthNumber = 1;
						dayNumber = 1;
						cal.set(yearNumber,  monthNumber-1, dayNumber,0,0,0);
						normalizedStartDate = cal.getTime();
					}
					else if (foundDate.matches("(?i)(maifeiertag|tag der arbeit)( [1-2]\\d{3})?")){
						monthNumber = 5;
						dayNumber = 1;
						cal.set(yearNumber,  monthNumber-1, dayNumber,0,0,0);
						normalizedStartDate = cal.getTime();
		
					}
					else if (foundDate.matches("(?i)tag der deutschen einheit( [1-2]\\d{3})?")){
						monthNumber = 10;
						dayNumber = 3;
						cal.set(yearNumber,  monthNumber-1, dayNumber,0,0,0);
						normalizedStartDate = cal.getTime();
						
					}
					normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, 1, normalizedStartDate);
					dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
					dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
					DateCommons.updateAnchorDate(normalizedStartDate);
				}
				
				
				//dateRegexMap.put(20, String.format("\\b(\\d{1,3}) %s\\b", days));
				if (key == 20){
					//TODO: anchoring
				}
				
				//germanDateRegexMap.put(21, String.format("\\b%s %s\\b", anfangMitteEnde, year));
				if (key == 21){
					
					String[] parts = foundDate.split("\\s");
					int monthNumber = 1;
					int dayNumber = 1;
					if (parts[0].toLowerCase().equals("anfang")){ // redundant...
						monthNumber = 1;
					}
					else if (parts[0].toLowerCase().equals("mitte")){
						monthNumber = 5;
					}
					else if (parts[0].toLowerCase().equals("ende")){
						monthNumber = 10;
					}
					int yearNumber = Integer.parseInt(parts[1]);
					cal.set(yearNumber,  monthNumber-1, dayNumber,0,0,0);
					normalizedStartDate = cal.getTime();
					normalizedEndDate = DateCommons.increaseCalendar(Calendar.MONTH, 3, normalizedStartDate);
					dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
					dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
					DateCommons.updateAnchorDate(normalizedStartDate);
				}
				
				
				//germanDateRegexMap.put(22, String.format("(?i)\\bnach %s (%s|%s|%s|%s)\\b", alphaNumber, day, week, month, year));
				if (key == 22){
					String[] parts = foundDate.split("\\s");
					int number = convertGermanAlphaNumber(parts[1]);
					int dayNumber = DateCommons.getDayFromAnchorDate();
					int monthNumber = DateCommons.getMonthFromAnchorDate();
					int yearNumber = DateCommons.getYearFromAnchorDate();
					System.out.println("DEBUG:" + yearNumber + "|" + monthNumber + "|" + dayNumber);
					cal.set(yearNumber,  monthNumber, dayNumber,0,0,0);
					final String day = "(?i)tage?";
					final String week = "(?i)wochen?";
					final String month = "(?i)monate?";
					final String year = "(?i)jahre?";
					if (parts[2].matches(day)){
						cal.add(Calendar.DATE, number);
						normalizedStartDate = cal.getTime();
						normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, 1, normalizedStartDate);
					}
					else if (parts[2].matches(week)){
						cal.add(Calendar.DATE, number * 7);
						normalizedStartDate = cal.getTime();
						normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, 7, normalizedStartDate);
					}
					else if (parts[2].matches(month)){
						cal.add(Calendar.MONTH, number);
						normalizedStartDate = cal.getTime();
						normalizedEndDate = DateCommons.increaseCalendar(Calendar.MONTH, 1, normalizedStartDate);
					}
					else if (parts[2].matches(year)){
						cal.add(Calendar.YEAR, number);
						normalizedStartDate = cal.getTime();
						normalizedEndDate = DateCommons.increaseCalendar(Calendar.YEAR, 1, normalizedStartDate);
					}
					dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
					dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
					DateCommons.updateAnchorDate(normalizedStartDate);
				}
			
				//germanDateRegexMap.put(23, String.format("(?i)\\b(am|im) selben (tag|monat)\\b"));
				if (key == 23){
					String[] parts = foundDate.split("\\s");
					int dayNumber = DateCommons.getDayFromAnchorDate();
					int monthNumber = DateCommons.getMonthFromAnchorDate();
					int yearNumber = DateCommons.getYearFromAnchorDate();
					cal.set(yearNumber,  monthNumber, dayNumber,0,0,0);
					if (parts[2].matches("(?i)tag")){
						normalizedStartDate = cal.getTime();
						normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, 1, normalizedStartDate);
					}
					else if (parts[2].matches("(?i)monat")){
						cal.set(yearNumber,  monthNumber, 1,0,0,0);
						normalizedStartDate = cal.getTime();
						normalizedEndDate = DateCommons.increaseCalendar(Calendar.MONTH, 1, normalizedStartDate);
					}
					dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
					dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
					DateCommons.updateAnchorDate(normalizedStartDate);
				}
				
				//germanDateRegexMap.put(24, String.format("\\b%s[-/\\.]%s[-/\\.]%s\\b", dayNumber, monthRomanNumber, nineteenHundred_twoDigitYearNumber));
				if (key == 24){
					String[] parts = foundDate.split("[-/\\.]");
					int dayNumber = Integer.parseInt(parts[0]);
					int monthNumber = romanMonth2Number.get((parts[1]));
					int yearNumber = Integer.parseInt("19" + parts[2]);
					cal.set(yearNumber,  monthNumber-1, dayNumber,0,0,0);
					normalizedStartDate = cal.getTime();
					normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, 1, normalizedStartDate);
					dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
					dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
					DateCommons.updateAnchorDate(normalizedStartDate);
				}
				
				//germanDateRegexMap.put(25, String.format("\\b%s([- ]?%s)?\\b", dayName, dayPartName));
				if (key == 25){
					//String[] parts = foundDate.split("\\s");
					int dayNumber = DateCommons.getDayFromAnchorDate();
					int monthNumber = DateCommons.getMonthFromAnchorDate();
					int yearNumber = DateCommons.getYearFromAnchorDate();
					cal.set(yearNumber,  monthNumber, dayNumber,0,0,0);
					int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
					foundDate = foundDate.replaceAll("(?i)((morgen|gestern)?morgen|(vor|nach)?mittag|abend|nacht)", "");
					//TODO: also take into account the mittag/abend stuff for normalization!
					if (germanDayName2Integer.containsKey(foundDate.trim().toLowerCase())){
						int x = germanDayName2Integer.get(foundDate.trim().toLowerCase());
						int daysOfIncrease = 0;
						if (x > dayOfWeek){
							// it is in the rest of the week
							daysOfIncrease = x - dayOfWeek;
						}
						else if (x == dayOfWeek){
							// do nothing, it is the current day
							daysOfIncrease = 0;
						}
						else{ // else x < currentDayOfWeek and the day is in the next week
							daysOfIncrease = 7 - dayOfWeek + x;
						}
						normalizedStartDate = DateCommons.increaseCalendar(Calendar.DATE, daysOfIncrease, cal.getTime());
						normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, 1, normalizedStartDate);
						
						dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
						dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
						DateCommons.updateAnchorDate(normalizedStartDate);
					}
					
				}
				
				//germanDateRegexMap.put(26, String.format("\\b%s\\b", heute));
				if (key == 26){
					int dayNumber = DateCommons.getDayFromAnchorDate();
					int monthNumber = DateCommons.getMonthFromAnchorDate();
					int yearNumber = DateCommons.getYearFromAnchorDate();
					cal.set(yearNumber,  monthNumber, dayNumber,0,0,0);
					normalizedStartDate = cal.getTime();
					normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, 1, normalizedStartDate);
					dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
					dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
					DateCommons.updateAnchorDate(normalizedStartDate);
					
				}
				
				//germanDateRegexMap.put(27, String.format("\\b%s\\b", jetzt));
				if (key == 27){
					int dayNumber = DateCommons.getDayFromAnchorDate();
					int monthNumber = DateCommons.getMonthFromAnchorDate();
					int yearNumber = DateCommons.getYearFromAnchorDate();
					cal.set(yearNumber,  monthNumber, dayNumber,0,0,0);
					normalizedStartDate = cal.getTime();
					normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, 1, normalizedStartDate);
					dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
					dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
					DateCommons.updateAnchorDate(normalizedStartDate);
					
				}
				
				
				//final String gestern = "(?i)(vor)?gestern";
				if (key == 28){
					int dayNumber = DateCommons.getDayFromAnchorDate();
					int monthNumber = DateCommons.getMonthFromAnchorDate();
					int yearNumber = DateCommons.getYearFromAnchorDate();
					cal.set(yearNumber,  monthNumber, dayNumber,0,0,0);
					if (foundDate.toLowerCase().startsWith("vor")){
						normalizedStartDate = DateCommons.increaseCalendar(Calendar.DATE, -2, cal.getTime());
					}
					else{
						normalizedStartDate = DateCommons.increaseCalendar(Calendar.DATE, -1, cal.getTime());	
					}
					normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, 1, normalizedStartDate);
					dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
					dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
					DateCommons.updateAnchorDate(normalizedStartDate);
					
				}
				
				//final String morgen = "(?i)(über)?morgen";
				if (key == 29){
					int dayNumber = DateCommons.getDayFromAnchorDate();
					int monthNumber = DateCommons.getMonthFromAnchorDate();
					int yearNumber = DateCommons.getYearFromAnchorDate();
					cal.set(yearNumber,  monthNumber, dayNumber,0,0,0);
					if (foundDate.toLowerCase().startsWith("über")){
						normalizedStartDate = DateCommons.increaseCalendar(Calendar.DATE, 2, cal.getTime());
					}
					else{
						normalizedStartDate = DateCommons.increaseCalendar(Calendar.DATE, 1, cal.getTime());
					}
					normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, 1, normalizedStartDate);
					dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
					dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
					DateCommons.updateAnchorDate(normalizedStartDate);
					
				}
				
			}
			
			 
		}
		
		
		return dates;
	}
	

	
	private static Integer convertGermanAlphaNumber(String alphaNumber){

		HashMap<String,Integer> m = new HashMap<String,Integer>();
		m.put("zwei", 2);
		m.put("drei", 3);
		m.put("vier", 4);
		m.put("fünf", 5);
		m.put("sechs", 6);
		m.put("sieben", 7);
		m.put("acht", 8);
		m.put("neun", 9);
		m.put("zehn", 10);
		m.put("elf", 11);
		m.put("zwölf", 12);
		m.put("dreizehn", 13);
		m.put("vierzehn", 14);
		m.put("fünfzehn", 15);
		m.put("sechzehn", 16);
		m.put("siebzehn", 17);
		m.put("achtzehn", 18);
		m.put("neunzehn", 19);
		m.put("zwanzig", 20);
		m.put("einundzwanzig", 21);
		m.put("zweiundzwanzig", 22);
		m.put("dreiundzwanzig", 23);
		m.put("vierundzwanzig", 24);
		m.put("fünfundzwanzig", 25);
		m.put("sechsundzwanzig", 26);
		m.put("siebenundzwanzig", 27);
		m.put("achtundzwanzig", 28);
		m.put("neunundzwanzig", 29);
		m.put("dreiβig", 30);
		m.put("einunddreiβig", 31);
		m.put("zweiundreißig", 32);
		m.put("dreiunddreißig", 33);
		m.put("vierunddreißig", 34);
		m.put("fünfunddreißig", 35);
		m.put("sechsunddreißig", 36);
		m.put("siebenunddreißig", 37);
		m.put("achtunddreißig", 38);
		m.put("neununddreißig", 39);
		m.put("vierzig", 40);
		m.put("einundvierzig", 41);
		m.put("zweiundvierzig", 42);
		m.put("dreiundvierzig", 43);
		m.put("vierundvierzig", 44);
		m.put("fünfundvierzig", 45);
		m.put("sechsundvierzig", 46);
		m.put("siebenundvierzig", 47);
		m.put("achtundvierzig", 48);
		m.put("neunundvierzig", 49);
		m.put("fünfzig", 50);
		m.put("einundfünfzig", 51);
		m.put("zweiundfünfzig", 52);
		m.put("dreiundfünfzig", 53);
		m.put("vierundfünfzig", 54);
		m.put("fünfundfünfzig", 55);
		m.put("sechsundfünfzig", 56);
		m.put("siebenundfünfzig", 57);
		m.put("achtundfünfzig", 58);
		m.put("neunundfünfzig", 59);
		m.put("sechzig", 60);
		m.put("einundsechzig", 61);
		m.put("zweiundsechzig", 62);
		m.put("dreiundsechzig", 63);
		m.put("vierundsechzig", 64);
		m.put("fünfundsechzig", 65);
		m.put("sechsundsechzig", 66);
		m.put("siebenundsechzig", 67);
		m.put("achtundsechzig", 68);
		m.put("neunundsechzig", 69);
		m.put("siebzig", 70);
		m.put("einundsiebzig", 71);
		m.put("zweiundsiebzig", 72);
		m.put("dreiundsiebzig", 73);
		m.put("vierundsiebzig", 74);
		m.put("fünfundsiebzig", 75);
		m.put("sechsundsiebzig", 76);
		m.put("siebenundsiebzig", 77);
		m.put("achtundsiebzig", 78);
		m.put("neunundsiebzig", 79);
		m.put("achtzig", 80);
		m.put("einundachtzig", 81);
		m.put("zweiundachtzig", 82);
		m.put("dreiundachtzig", 83);
		m.put("vierundachtzig", 84);
		m.put("fünfundachtzig", 85);
		m.put("sechsundachtzig", 86);
		m.put("siebenundachtzig", 87);
		m.put("achtundachtzig", 88);
		m.put("neunundachtzig", 89);
		m.put("neunzig", 90);
		m.put("einundneunzig", 91);
		m.put("zweiundneunzig", 92);
		m.put("dreiundneunzig", 93);
		m.put("vierundneunzig", 94);
		m.put("fünfundneunzig", 95);
		m.put("sechsundneunzig", 96);
		m.put("siebenundneunzig", 97);
		m.put("achtundneunzig", 98);
		m.put("neunundneunzig", 99);

		
		int r = 0;
		// think eins? and (ein)?hundert are the only ones with variation, so:
		if (alphaNumber.toLowerCase().matches("ein(s|en)?")){
			r = 1;
		}
		else if (alphaNumber.toLowerCase().matches("(ein)?hundert")){
			r = 100;
		}
		else{
			r = m.get(alphaNumber.toLowerCase());
		}
		return r;
	}
	
}
