
 package de.dkt.eservices.eopennlp.timexRules;
     import java.text.SimpleDateFormat;
     import java.util.Arrays;
     import java.util.Calendar;
     import java.util.Date;
     import java.util.GregorianCalendar;
     import java.util.HashMap;
     import java.util.Iterator;
     import java.util.LinkedList;
     import java.util.List;
     import java.util.regex.Pattern;
     import opennlp.tools.namefind.RegexNameFinder;
     import de.dkt.eservices.eopennlp.modules.RegexFinder;
     import opennlp.tools.namefind.RegexNameFinder;
     import opennlp.tools.util.Span;
     import java.util.Map;
     
     public class AutomaticEnglishDateRules {
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
       int counter = 0;
       List<Pattern> patterns = new LinkedList<Pattern>();



String digit="0|1|2|3|4|5|6|7|8|9";
String ceroone="0|1";
String onetwo="1|2";
String daynumber="(0)?("+digit+")|1("+digit+")|2("+digit+")|3("+ceroone+")";
String monthname="january|february|march|april|may|june|july|august|september|october|november|december";
String yearnumber="("+onetwo+")("+digit+")("+digit+")("+digit+")";

counter++;englishDateRegexMap.put(counter, String.format("("+daynumber+") ("+monthname+")((,)? ("+yearnumber+"))?"));
       patterns.add(Pattern.compile(englishDateRegexMap.get(counter)));

counter++;englishDateRegexMap.put(counter, String.format("("+monthname+") ("+daynumber+")(st|nd|rd|th)?(,)?( ("+yearnumber+"))?"));
       patterns.add(Pattern.compile(englishDateRegexMap.get(counter)));

counter++;englishDateRegexMap.put(counter, String.format("("+yearnumber+")"));
       patterns.add(Pattern.compile(englishDateRegexMap.get(counter)));

             int counter2=0;
             Pattern[] patts = new Pattern[patterns.size()];
             for (Pattern p : patterns) {
              patts[counter2]=p;
              counter2++;
       }
       RegexNameFinder rnf = new RegexNameFinder(patts, null);
       return rnf;
       }
                      public static void main(String[] args) {
                    	  String digit="0|1|2|3|4|5|6|7|8|9";
                    	  String ceroone="0|1";
                    	  String onetwo="1|2";
                    	  String daynumber="(0)?("+digit+")|1("+digit+")|2("+digit+")|3("+ceroone+")";
                    	  String monthname="january|february|march|april|may|june|july|august|september|october|november|december";
                    	  String yearnumber="("+onetwo+")("+digit+")("+digit+")("+digit+")";
         RegexNameFinder timeFinder= AutomaticEnglishDateRules.initEnglishDateFinder();
         String input = " october 21 1990";
         RegexFinder rf = new RegexFinder();
         List<Span> timeSpans = rf.filterFind(timeFinder, input);
         for (Span s : timeSpans){
          System.out.println("DEBUGGING:" + s.getStart() + "=--="+s.getEnd());
          System.out.println("DEBUGGING span:" + input.substring(s.getStart(), s.getEnd()));
         LinkedList<String> normalizedStartAndEnd = normalizeEnglishDate(input.substring(s.getStart(), s.getEnd()));
         System.out.println("("+monthname+") ("+daynumber+")(st|nd|rd|th)?(,)?( ("+yearnumber+"))?");
               System.out.println("DEBUGGING norm:" + normalizedStartAndEnd);
         }
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
           
// needed info: key, beginYear, beginMonth, beginDay, beginHour, endYear, endMonth, endDay, endHour
    if (key == 1){
       int dayNumber = 1;
       int monthNumber = 1;
       int yearNumber = 1000;
     String[] parts = foundDate.split("\\s");
        //dayNumber = Integer.parseInt(parts[1].replaceAll("\\p{P}", ""));;
        monthNumber = englishMonthName2Number.get(parts[1].toLowerCase().replaceAll("\\p{P}", ""));
        yearNumber = DateCommons.getYearFromAnchorDate();
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
    if (key == 2){
       int dayNumber = 1;
       int monthNumber = 1;
       int yearNumber = 1000;
     String[] parts = foundDate.split("\\s");
        if (parts[1].matches("((0)?[1-9]|1[0-9]|2[0-9]|3[0-1])(st|nd|rd|th)?(,)?")){
        	
        //if (parts[1].matches("\\d{1,2}(st|nd|rd|th)?(,)?")){
              dayNumber = Integer.parseInt(parts[1].replaceAll("(st|nd|rd|th)", "").replaceAll(",",""));
              };
        monthNumber = englishMonthName2Number.get(parts[0].toLowerCase().replaceAll("\\p{P}", ""));
        yearNumber = DateCommons.getYearFromAnchorDate();
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
    if (key == 3){
       int dayNumber = 1;
       int monthNumber = 1;
       int yearNumber = 1000;
     String[] parts = foundDate.split("\\s");
        yearNumber = DateCommons.getYearFromAnchorDate();
    if (parts.length > 0){
     yearNumber = Integer.parseInt(parts[0]);
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


               }
            }
            
              return dates;}
            }
           
