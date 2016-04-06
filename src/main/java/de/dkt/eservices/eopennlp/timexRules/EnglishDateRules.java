package de.dkt.eservices.eopennlp.timexRules;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Pattern;

import opennlp.tools.namefind.RegexNameFinder;

public class EnglishDateRules {
	
	static HashMap<Integer, String> englishDateRegexMap = new HashMap<Integer, String>();
	static HashMap<String, Integer> englishMonthName2Number = new HashMap<String, Integer>(){{
		put("january", 1);
		put("february", 2);
		put("march", 3);
		put("april", 4);
		put("may", 5);
		put("june", 6);
		put("july", 7);
		put("august", 8);
		put("september", 9);
		put("october", 10);
		put("november", 11);
		put("december", 12);
	}};

	
	public static RegexNameFinder initEnglishDateFinder(){

		final String monthName = "(?i)(january|february|march|april|may|june|july|august|september|october|november|december)";
		final String monthNumber = "(0?[0-9]|1[0-2])";
		
		final String dayName = "(?i)(monday|tuesday|wednesday|thursday|friday|saturday|sunday)";
		final String dayNumber = "(0?[1-9]|1[0-9]|2[0-9]|3[0-1])";
		final String season = "(?i)(summer|winter|spring|autumn|fall)";
		
		final String yearNumber = "([1-2]\\d{3})";
		
		final String beginningMiddleEnd = "(?i)(start|beginning|middle|end)";
		final String beforeChrist = "(?i)\\d{1,4} b\\.?c\\.?";
		
		final String alphaNumber = "(?i)(one|two|three|four|five|six|seven|eight|nine|ten|eleven|twelve|thirteen|fourteen|fifteen|sixteen|seventeen|eighteen|nineteen|twenty|twenty-one|twenty-two|twenty-three|twenty-four|twenty-five|twenty-six|twenty-seven|twenty-eight|twenty-nine|thirty|thirty-one|thirty-two|thirty-three|thirty-four|thirty-five|thirty-six|thirty-seven|thirty-eight|thirty-nine|forty|forty-one|forty-two|forty-three|forty-four|forty-five|forty-six|forty-seven|forty-eight|forty-nine|fifty|fifty-one|fifty-two|fifty-three|fifty-four|fifty-five|fifty-six|fifty-seven|fifty-eight|fifty-nine|sixty|sixty-one|sixty-two|sixty-three|sixty-four|sixty-five|sixty-six|sixty-seven|sixty-eight|sixty-nine|seventy|seventy-one|seventy-two|seventy-three|seventy-four|seventy-five|seventy-six|seventy-seven|seventy-eight|seventy-nine|eighty|eighty-one|eighty-two|eighty-three|eighty-four|eighty-five|eighty-six|eighty-seven|eighty-eight|eighty-nine|ninety|ninety-one|ninety-two|ninety-three|ninety-four|ninety-five|ninety-six|ninety-seven|ninety-eight|ninety-nine|(one )?hundred)";
		final String day = "(?i)days?";
		final String week = "(?i)weeks?";
		final String month = "(?i)months?";
		final String year = "(?i)years?";
		
		final String holidays = "(?i)(christmas( day)?|easter( monday)?|new year'?s day|may day|boxing day|independence day|labor day|memorial day|veteran'?s day|thanksgiving)";
		
		
		englishDateRegexMap.put(1, String.format("\\b%s %s(,? %s)?\\b", dayNumber, monthName, yearNumber));
		englishDateRegexMap.put(2, String.format("\\b%s( %s(st|nd|rd|th)?)?,?( %s)?\\b", monthName, dayNumber, yearNumber)); // TODO: debug for/also allow superscript for th?
		englishDateRegexMap.put(3, String.format("\\b%s\\b", yearNumber));
		englishDateRegexMap.put(4, String.format("\\b%s[/\\-\\.]%s([/\\-\\.]%s)?\\b", dayNumber, monthNumber, yearNumber));
		englishDateRegexMap.put(5, String.format("\\bmid(\\-| )%s\\b", yearNumber));
		englishDateRegexMap.put(6, String.format("\\bmid(\\-| )%s\\b", monthName));
		englishDateRegexMap.put(7, String.format("\\b%s( (of )?%s)?\\b", season, yearNumber));
		englishDateRegexMap.put(8, String.format("\\b%s of %s\\b", beginningMiddleEnd, yearNumber));
		englishDateRegexMap.put(9, String.format("\\b%s of %s\\b", beginningMiddleEnd, monthName));
		englishDateRegexMap.put(10, String.format("\\bthe %s(st|nd|rd|th)\\b", dayNumber)); // TODO: debug for/also allow superscript for th?
		englishDateRegexMap.put(11, String.format("\\b%s", beforeChrist));
		englishDateRegexMap.put(12, String.format("\\b(early|late) %s\\b", yearNumber));
		englishDateRegexMap.put(13, String.format("\\b(early|late) %s\\b", monthName));
		englishDateRegexMap.put(14, String.format("(?i)\\b(today|(right )?now|this (day|date))\\b"));
		englishDateRegexMap.put(15, String.format("(?i)\\b%s (%s|%s|%s|%s) (later|after|earlier|before)\\b", alphaNumber, day, week, month, year));
		englishDateRegexMap.put(16, String.format("\\b%ss\\b", yearNumber));
		englishDateRegexMap.put(17, String.format("\\b%s\\b", dayName));
		englishDateRegexMap.put(18, String.format("\\b%s( %s)?\\b", holidays, yearNumber));
		
		Pattern[] regexes = {
			Pattern.compile(englishDateRegexMap.get(1)),
			Pattern.compile(englishDateRegexMap.get(2)),
			Pattern.compile(englishDateRegexMap.get(3)),
			Pattern.compile(englishDateRegexMap.get(4)),
			Pattern.compile(englishDateRegexMap.get(5)),
			Pattern.compile(englishDateRegexMap.get(6)),
			Pattern.compile(englishDateRegexMap.get(7)),
			Pattern.compile(englishDateRegexMap.get(8)),
			Pattern.compile(englishDateRegexMap.get(9)),
			Pattern.compile(englishDateRegexMap.get(10)),
			Pattern.compile(englishDateRegexMap.get(11)),
			Pattern.compile(englishDateRegexMap.get(12)),
			Pattern.compile(englishDateRegexMap.get(13)),
			Pattern.compile(englishDateRegexMap.get(14)),
			Pattern.compile(englishDateRegexMap.get(15)),
			Pattern.compile(englishDateRegexMap.get(16)),
			Pattern.compile(englishDateRegexMap.get(17)),
			Pattern.compile(englishDateRegexMap.get(18)),
		};
	
		RegexNameFinder rnf = new RegexNameFinder(regexes, null);
		return rnf;

	}


	public static LinkedList<String> normalizeEnglishDate(String foundDate) {

		// this is directly coupled to the dateRegexMap thing. If that changes, this needs to be checked too
		Date normalizedStartDate = new Date();
		Date normalizedEndDate = new Date();
		Calendar cal = Calendar.getInstance();
		
		Iterator it = englishDateRegexMap.entrySet().iterator();
		LinkedList<String> dates = new LinkedList<String>();
		
		
		while (it.hasNext()){
			Map.Entry pair = (Map.Entry)it.next();
			int key = (Integer) pair.getKey();
			String p = (String) pair.getValue();
			// compile with ^ and $ since I want the whole match/no submatches (not sure if this works though, debug!)
			if (Pattern.matches(String.format("^%s$", p), foundDate)){

				//englishDateRegexMap.put("1", String.format("\\b%s %s(,? %s)?\\b", dayNumber, monthName, yearNumber));
				if (key == 1){
					String[] parts = foundDate.split("\\s");
					int dayNumber = Integer.parseInt(parts[0]);
					int monthNumber = englishMonthName2Number.get(parts[1].toLowerCase());
					int yearNumber = DateCommons.getYearFromAnchorDate();
					if (parts.length > 2){
						yearNumber = Integer.parseInt(parts[2]);
					}
					else{
						yearNumber = DateCommons.getYearFromAnchorDate();
					}
					cal.set(yearNumber, monthNumber-1, dayNumber, 0, 0, 0);
					normalizedStartDate = cal.getTime();
					normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, 1, normalizedStartDate);
					dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
					dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
					DateCommons.updateAnchorDate(normalizedStartDate);
					
				}
				
				
				//englishDateRegexMap.put(2, String.format("\\b%s( %s(st|nd|rd|th)?)?,?( %s)?\\b", monthName, dayNumber, yearNumber)); // TODO: debug for/also allow superscript for th?
				if (key == 2){
					String[] parts = foundDate.split("\\s");
					int monthNumber = englishMonthName2Number.get(parts[0].toLowerCase());
					int dayNumber = 1;
					int yearNumber = DateCommons.getYearFromAnchorDate(); // TODO: guess I should use tense here as well. If someone says "in February", guess it depends on the tense of the sentence if last or next feb is meant
					int increaseCalendarUnit = Calendar.DATE;
					if (parts.length > 1){
						if (parts[1].matches("\\d{1,2}(st|nd|rd|th)?")){
							dayNumber = Integer.parseInt(parts[1].replaceAll("(st|nd|rd|th)", "").replaceAll(",",""));	
						}
						else if (parts[1].matches("\\d{4}")){
							yearNumber = Integer.parseInt(parts[1]);
						}
						else{
							yearNumber = DateCommons.getYearFromAnchorDate();
						}
					}
					else {
						increaseCalendarUnit = Calendar.MONTH;
					}
					//System.out.println("DEBUG:" + yearNumber + "|" + monthNumber + "|" + dayNumber);
					cal.set(yearNumber,  monthNumber-1, dayNumber,0,0,0);
					normalizedStartDate = cal.getTime();
					normalizedEndDate = DateCommons.increaseCalendar(increaseCalendarUnit, 1, normalizedStartDate); // TODO: this is problematic in case of "January 2017"
					dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
					dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
					DateCommons.updateAnchorDate(normalizedStartDate);
				}
				
				
				//englishDateRegexMap.put(3, String.format("\\b%s\\b", yearNumber));
				if (key == 3){
					int dayNumber = 1;
					int monthNumber = 1;
					int yearNumber = Integer.parseInt(foundDate);
					cal.set(yearNumber, monthNumber-1, dayNumber,0,0,0);
					normalizedStartDate = cal.getTime();
					normalizedEndDate = DateCommons.increaseCalendar(Calendar.YEAR, 1, normalizedStartDate);
					dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
					dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
					DateCommons.updateAnchorDate(normalizedStartDate);
				}
				
				//englishDateRegexMap.put(4, String.format("\\b%s[/\\-\\.]%s([/\\-\\.]%s)?\\b", dayNumber, monthNumber, yearNumber));
				if (key == 4){
					String[] parts = foundDate.split("[^\\d]+");
					int dayNumber = Integer.parseInt(parts[0]);
					int monthNumber = Integer.parseInt(parts[1]);
					int yearNumber = 0;
					if (parts.length > 2){
						yearNumber = Integer.parseInt(parts[2]);
					}
					else{
						yearNumber = DateCommons.getYearFromAnchorDate();
					}
					cal.set(yearNumber,  monthNumber-1, dayNumber, 0, 0, 0);
					normalizedStartDate = cal.getTime();
					normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, 1, normalizedStartDate);
					dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
					dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
					DateCommons.updateAnchorDate(normalizedStartDate);
				}
				
				//englishDateRegexMap.put(5, String.format("\\bmid\\-%s\\b", yearNumber));
				if (key == 5){
					String[] parts = foundDate.split("(-| )");
					int yearNumber = Integer.parseInt(parts[1].trim());
					int monthNumber = 6;
					int dayNumber = 1;
					cal.set(yearNumber,  monthNumber-1, dayNumber,0,0,0);
					normalizedStartDate = cal.getTime();
					normalizedEndDate = DateCommons.increaseCalendar(Calendar.MONTH, 2, normalizedStartDate);
					dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
					dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
					DateCommons.updateAnchorDate(normalizedStartDate);
				}
				
				//englishDateRegexMap.put(6, String.format("\\bmid\\-%s\\b", monthName));
				if (key == 6){
					String[] parts = foundDate.split("(-| )");
					int yearNumber = DateCommons.getYearFromAnchorDate();
					int monthNumber = englishMonthName2Number.get(parts[1].toLowerCase().trim());
					int dayNumber = 10;
					cal.set(yearNumber, monthNumber-1, dayNumber,0,0,0);
					normalizedStartDate = cal.getTime();
					normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, 10, normalizedStartDate);
					dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
					dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
					DateCommons.updateAnchorDate(normalizedStartDate);
				}
				
				//englishDateRegexMap.put(7, String.format("\\b%s( of)? %s\\b", season, yearNumber));
				if (key == 7){
					String[] parts = foundDate.split("\\s");
					String season = parts[0];
					int yearNumber = DateCommons.getYearFromAnchorDate();
					if (parts.length > 1){
						yearNumber = Integer.parseInt(parts[parts.length-1]);	
					}
					else{
						yearNumber = DateCommons.getYearFromAnchorDate();
					}
					
					//final String season = "(?i)(summer|winter|spring|autumn|fall)";
					int startDay = 1;
					int startMonth = 0;
					int endDay = 1;
					int endMonth = 0;
					if (season.equalsIgnoreCase("spring")){
						startMonth = 3;
						endDay = 31;
						endMonth = 5;
					}
					else if (season.equalsIgnoreCase("summer")){
						startMonth = 6;
						endDay = 31;
						endMonth = 8;
					}
					else if (season.equalsIgnoreCase("(autumn|fall)")){
						startMonth = 9;
						endDay = 30;
						endMonth = 11;
					}
					else if (season.equalsIgnoreCase("winter")){
						startMonth = 12;
						endDay = 28;
						endMonth = 2;
						if (((GregorianCalendar) cal).isLeapYear(yearNumber)){
							endDay = 29;
						}
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
				
				//englishDateRegexMap.put(8, String.format("\\b%s of %s\\b", beginningMiddleEnd, yearNumber));
				if (key == 8){
					String[] parts = foundDate.split("\\s");
					int yearNumber = Integer.parseInt(parts[parts.length-1]);
					int dayNumber = 1;
					int monthNumber = 0;
					//final String beginningMiddleEnd = "(?i)(beginning|middle|end)";
					if (parts[0].matches("(?i)(start|beginning)")){
						monthNumber = 1;
					}
					else if (parts[0].matches("(?i)middle")){
						monthNumber = 5;
					}
					else if (parts[0].matches("(?i)end")){
						monthNumber = 10;
					}
					cal.set(yearNumber, monthNumber-1, dayNumber,0,0,0);
					normalizedStartDate = cal.getTime();
					normalizedEndDate = DateCommons.increaseCalendar(Calendar.MONTH, 3, normalizedStartDate);
					dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
					dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
					DateCommons.updateAnchorDate(normalizedStartDate);
				}
				
				
				//englishDateRegexMap.put(9, String.format("\\b%s of %s\\b", beginningMiddleEnd, monthName));
				if (key == 9){
					String[] parts = foundDate.split("\\s");
					int yearNumber = DateCommons.getYearFromAnchorDate();
					int monthNumber = englishMonthName2Number.get(parts[parts.length-1].toLowerCase());
					int dayNumber = 1;
					if (parts[0].matches("(?i)(start|beginning)")){ // redundant...
						dayNumber = 1;
					}
					else if (parts[0].matches("(?i)middle")){
						dayNumber = 10;
					}
					else if (parts[0].matches("(?i)end")){
						dayNumber = DateCommons.getLastDayOfMonth(monthNumber, yearNumber) - 10;
					}
					cal.set(yearNumber,  monthNumber-1, dayNumber,0,0,0);
					normalizedStartDate = cal.getTime();
					normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, 10, normalizedStartDate);
					dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
					dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
					DateCommons.updateAnchorDate(normalizedStartDate);
				}
				
				//englishDateRegexMap.put(10, String.format("\\bthe %s(st|nd|rd|th)\\b", dayNumber)); // TODO: debug for/also allow superscript for th?
				if (key == 10){
					String[] parts = foundDate.split("\\s");
					int dayNumber = Integer.parseInt(parts[1].replaceAll("(st|nd|rd|th)", ""));
					int monthNumber = DateCommons.getMonthFromAnchorDate();
					int yearNumber = DateCommons.getYearFromAnchorDate();
					if (monthNumber == 0){
						monthNumber += 1;
					}
					cal.set(yearNumber,  monthNumber-1, dayNumber,0,0,0);
					normalizedStartDate = cal.getTime();
					normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, 1, normalizedStartDate);
					dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
					dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
					DateCommons.updateAnchorDate(normalizedStartDate);
				}
				
				//englishDateRegexMap.put(11, String.format("\\b%s", beforeChrist));
				if (key == 11){
					String parts[] = foundDate.split("\\s");
					String yearNumberBeforeChrist = parts[0];
					int yearSubtract = cal.get(Calendar.YEAR) + Integer.parseInt(yearNumberBeforeChrist);
					cal.add(Calendar.YEAR, -yearSubtract);
					normalizedStartDate = cal.getTime();
					normalizedEndDate = DateCommons.increaseCalendar(Calendar.YEAR, 1, normalizedStartDate);
					dates.add(DateCommons.fullDateFormat.format(normalizedStartDate)); // start date
					dates.add(DateCommons.fullDateFormat.format(normalizedEndDate)); // end date
					DateCommons.updateAnchorDate(normalizedEndDate);
				}
				
				//englishDateRegexMap.put(12, String.format("\\b(early|late) %s\\b", yearNumber));
				if (key == 12){
					String[] parts = foundDate.split("\\s");
					int yearNumber = Integer.parseInt(parts[1]);
					int monthNumber = 0;
					int dayNumber = 1;
					if (parts[0].toLowerCase().matches("early")){
						monthNumber = 1;
					}
					else if (parts[0].toLowerCase().matches("late")){
						monthNumber = 10;
					}
					cal.set(yearNumber,  monthNumber-1, dayNumber,0,0,0);
					normalizedStartDate = cal.getTime();
					normalizedEndDate = DateCommons.increaseCalendar(Calendar.MONTH, 3, normalizedStartDate);
					dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
					dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
					DateCommons.updateAnchorDate(normalizedStartDate);
				}
				
				
				//englishDateRegexMap.put(13, String.format("\\b(early|late) %s\\b", monthName));
				if (key == 13){
					String[] parts = foundDate.split("\\s");
					int yearNumber = DateCommons.getYearFromAnchorDate();
					int monthNumber = englishMonthName2Number.get(parts[1].toLowerCase());
					int dayNumber = 1;
					if (parts[0].toLowerCase().matches("early")){
						dayNumber = 1;
					}
					else if (parts[0].toLowerCase().matches("late")){
						dayNumber = DateCommons.getLastDayOfMonth(monthNumber, yearNumber) - 10; 
					}
					cal.set(yearNumber,  monthNumber-1, dayNumber,0,0,0);
					normalizedStartDate = cal.getTime();
					normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, 10, normalizedStartDate);
					dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
					dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
					DateCommons.updateAnchorDate(normalizedStartDate);
				}
				
				//englishDateRegexMap.put(14, String.format("\\b(today|now|this day)\\b"));
				if (key == 14){
					// Just taking the current time here. Think about/discuss what to do here. Ideally, this should be time of writing the document. But that's not available in all cases.
					
					SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
					String formatted = sdf.format(cal.getTime());
					normalizedStartDate = DateCommons.normParseDate(formatted);
					normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, 1, normalizedStartDate);
					dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
					dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
					DateCommons.updateAnchorDate(normalizedStartDate);
				}
				
				//englishDateRegexMap.put(15, String.format("(?i)\\b%s (%s|%s|%s|%s) (later|after|earlier|before)\\b", alphaNumber, day, week, month, year));
				if (key == 15){
					String[] parts = foundDate.split("\\s");
					int direction = 0;
					if (parts[2].toLowerCase().matches("(later|after)")){
						direction = 1;
					}
					else if (parts[2].toLowerCase().matches("(earlier|before)")){
						direction = -1;
					}
					int increment = convertEnglishAlphaNumber(parts[0].toLowerCase()) * direction;
					int yearNumber = DateCommons.getYearFromAnchorDate();
					int monthNumber = DateCommons.getMonthFromAnchorDate();
					if (monthNumber == 0){
						monthNumber += 1;
					}
					int dayNumber = DateCommons.getDayFromAnchorDate();
					cal.set(yearNumber,  monthNumber-1, dayNumber,0,0,0);
					Date anchor = cal.getTime();
					//final String day = "(?i)days?";
					//final String week = "(?i)weeks?";
					//final String month = "(?i)months?";
					//final String year = "(?i)years?";
					if (parts[1].toLowerCase().matches("(?i)days?")){
						normalizedStartDate = DateCommons.increaseCalendar(Calendar.DATE, increment, anchor);
						normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, 1, normalizedStartDate);
					}
					else if (parts[1].toLowerCase().matches("(?i)weeks?")){
						normalizedStartDate = DateCommons.increaseCalendar(Calendar.DATE, increment * 7, anchor);
						normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, 1 * 7, normalizedStartDate);
					}
					else if (parts[1].toLowerCase().matches("(?i)months?")){
						normalizedStartDate = DateCommons.increaseCalendar(Calendar.MONTH, increment, anchor);
						normalizedEndDate = DateCommons.increaseCalendar(Calendar.MONTH, 1, normalizedStartDate);
					}
					else if (parts[1].toLowerCase().matches("(?i)years?")){
						normalizedStartDate = DateCommons.increaseCalendar(Calendar.YEAR, increment, anchor);
						normalizedEndDate = DateCommons.increaseCalendar(Calendar.YEAR, 1, normalizedStartDate);
					}
					dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
					dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
					DateCommons.updateAnchorDate(normalizedStartDate);
				}
				
				//englishDateRegexMap.put(16, String.format("\\b%ss\\b", yearNumber));
				if (key == 16){
					int yearNumber = Integer.parseInt(foundDate.replace("s", ""));
					int monthNumber = 1;
					int dayNumber = 1;
					cal.set(yearNumber, monthNumber-1, dayNumber,0,0,0);
					normalizedStartDate = cal.getTime();
					normalizedEndDate = DateCommons.increaseCalendar(Calendar.YEAR, 10, normalizedStartDate);
					dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
					dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
					DateCommons.updateAnchorDate(normalizedStartDate);
				}
				
				//englishDateRegexMap.put(17, String.format("\\b%s\\b", dayName));
				if (key == 17){
					//TODO: fix this once I have sentence tense in place. The approach below is a simplified version, and should really be properly debugged before using it.
					/*
					int yearNumber = DateCommons.getYearFromAnchorDate();
					int monthNumber = DateCommons.getMonthFromAnchorDate();
					int dayNumber = DateCommons.getDayFromAnchorDate();
					cal.set(yearNumber, monthNumber, dayNumber);
					if (foundDate.equalsIgnoreCase("monday")){
						cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);	
					}
					else if (foundDate.equalsIgnoreCase("tuesday")){
						cal.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
					}
					else if (foundDate.equalsIgnoreCase("wednesday")){
						cal.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
					}
					else if (foundDate.equalsIgnoreCase("thursday")){
						cal.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
					}
					else if (foundDate.equalsIgnoreCase("friday")){
						cal.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
					}
					else if (foundDate.equalsIgnoreCase("saturday")){
						cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
					}
					else if (foundDate.equalsIgnoreCase("sunday")){
						cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
					}
					normalizedStartDate = cal.getTime();
					normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, 1, normalizedStartDate);
					dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
					dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
					DateCommons.updateAnchorDate(normalizedStartDate);
					*/
				}
				
				//final String holidays = "(?i)(christmas( day)?|easter( monday)?|new year'?s day|may day|boxing day|independence day|labor day|memorial day|veterans day|thanksgiving)";
				//englishDateRegexMap.put(18, String.format("\\b%s( %s)?\\b", holidays, yearNumber));
				if (key == 18){
					int yearNumber = DateCommons.getYearFromAnchorDate();
					String[] parts = foundDate.split("\\s");
					if (parts[parts.length-1].matches("([1-2]\\d{3})")){
						yearNumber = Integer.parseInt(foundDate.split("\\s")[foundDate.split("\\s").length-1]);
					}
					else{
						yearNumber = DateCommons.getYearFromAnchorDate();
					}
					int monthNumber = 1;
					int dayNumber = 1;
					if (parts[0].toLowerCase().matches("christmas")){
						monthNumber = 12;
						dayNumber = 25;
					}
					else if (parts[0].toLowerCase().matches("boxing")){
						monthNumber = 12;
						dayNumber = 26;
					}
					else if (parts[0].toLowerCase().matches("thanksgiving")){
						monthNumber =  07;
						dayNumber = 04;
					}
					else if (parts[0].toLowerCase().matches("veteran'?s")){
						monthNumber = 11 ;
						dayNumber = 11;
					}
					else if (parts[0].toLowerCase().matches("new")){
						monthNumber = 1;
						dayNumber = 1;
					}
					else if (parts[0].toLowerCase().matches("may")){
						monthNumber = 5;
						dayNumber = 1;
					}
					else if (parts[0].toLowerCase().matches("labor")){
						monthNumber = 9;
						dayNumber = DateCommons.getFirstSpecificDayOfMonth(yearNumber, monthNumber, "monday");
					}
					else if (parts[0].toLowerCase().matches("memorial")){
						monthNumber = 5;
						dayNumber = DateCommons.getLastSpecificDayOfMonth(yearNumber, monthNumber, "monday");
					}
					else if (parts[0].toLowerCase().matches("easter")){
						Calendar loCal = Calendar.getInstance();
						loCal.setTime(DateCommons.getEasterDate(yearNumber));
						monthNumber = loCal.get(Calendar.MONTH);
						dayNumber = loCal.get(Calendar.DATE);
					}
					//TODO: include pentecost, ascension, etc.
					
					cal.set(yearNumber, monthNumber-1, dayNumber,0,0,0);
					normalizedStartDate = cal.getTime();
					normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, 1, normalizedStartDate);
					dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
					dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
					DateCommons.updateAnchorDate(normalizedStartDate);
				}
			}

		}
		
		return dates;
	}

	


	private static Integer convertEnglishAlphaNumber(String alphaNumber){
		
		HashMap<String,Integer> m = new HashMap<String,Integer>();
		m.put("one",1);
		m.put("two",2);
		m.put("three",3);
		m.put("four",4);
		m.put("five",5);
		m.put("six",6);
		m.put("seven",7);
		m.put("eight",8);
		m.put("nine",9);
		m.put("ten",10);
		m.put("eleven",11);
		m.put("twelve",12);
		m.put("thirteen",13);
		m.put("fourteen",14);
		m.put("fifteen",15);
		m.put("sixteen",16);
		m.put("seventeen",17);
		m.put("eighteen",18);
		m.put("nineteen",19);
		m.put("twenty",20);
		m.put("twenty-one",21);
		m.put("twenty-two",22);
		m.put("twenty-three",23);
		m.put("twenty-four",24);
		m.put("twenty-five",25);
		m.put("twenty-six",26);
		m.put("twenty-seven",27);
		m.put("twenty-eight",28);
		m.put("twenty-nine",29);
		m.put("thirty",30);
		m.put("thirty-one",31);
		m.put("thirty-two",32);
		m.put("thirty-three",33);
		m.put("thirty-four",34);
		m.put("thirty-five",35);
		m.put("thirty-six",36);
		m.put("thirty-seven",37);
		m.put("thirty-eight",38);
		m.put("thirty-nine",39);
		m.put("forty",40);
		m.put("forty-one",41);
		m.put("forty-two",42);
		m.put("forty-three",43);
		m.put("forty-four",44);
		m.put("forty-five",45);
		m.put("forty-six",46);
		m.put("forty-seven",47);
		m.put("forty-eight",48);
		m.put("forty-nine",49);
		m.put("fifty",50);
		m.put("fifty-one",51);
		m.put("fifty-two",52);
		m.put("fifty-three",53);
		m.put("fifty-four",54);
		m.put("fifty-five",55);
		m.put("fifty-six",56);
		m.put("fifty-seven",57);
		m.put("fifty-eight",58);
		m.put("fifty-nine",59);
		m.put("sixty",60);
		m.put("sixty-one",61);
		m.put("sixty-two",62);
		m.put("sixty-three",63);
		m.put("sixty-four",64);
		m.put("sixty-five",65);
		m.put("sixty-six",66);
		m.put("sixty-seven",67);
		m.put("sixty-eight",68);
		m.put("sixty-nine",69);
		m.put("seventy",70);
		m.put("seventy-one",71);
		m.put("seventy-two",72);
		m.put("seventy-three",73);
		m.put("seventy-four",74);
		m.put("seventy-five",75);
		m.put("seventy-six",76);
		m.put("seventy-seven",77);
		m.put("seventy-eight",78);
		m.put("seventy-nine",79);
		m.put("eighty",80);
		m.put("eighty-one",81);
		m.put("eighty-two",82);
		m.put("eighty-three",83);
		m.put("eighty-four",84);
		m.put("eighty-five",85);
		m.put("eighty-six",86);
		m.put("eighty-seven",87);
		m.put("eighty-eight",88);
		m.put("eighty-nine",89);
		m.put("ninety",90);
		m.put("ninety-one",91);
		m.put("ninety-two",92);
		m.put("ninety-three",93);
		m.put("ninety-four",94);
		m.put("ninety-five",95);
		m.put("ninety-six",96);
		m.put("ninety-seven",97);
		m.put("ninety-eight",98);
		m.put("ninety-nine",99);
		

		int r = 0;
		if (alphaNumber.matches("(one )?hundred")){
			r = 100;
		}
		else{
			r = m.get(alphaNumber.toLowerCase());
		}
		
		return r;
	}


}
