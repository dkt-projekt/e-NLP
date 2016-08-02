
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
String cerofive="0|1|2|3|4|5";
String onetwo="1|2";
String ceroonetwo="0|1|2";
String daynumber="(0)?("+digit+")|1("+digit+")|2("+digit+")|3("+ceroone+")";
String monthname="january|february|march|april|may|june|july|august|september|october|november|december";
String yearnumber="("+onetwo+")("+digit+")("+digit+")("+digit+")";
String monthnumber=" 0|1|2|3|4|5|6|7|8|9|10|11|12";
String separation=".|/|-";
String hyphenorwhitespace="-| ";
String season="summer|winter|spring|autumn|fall";
String earlymidlate="early|mid|late";
String beginmiddleend="start|beginning|middle|end";
String yearnumberbc="("+digit+")(("+digit+")(("+digit+")(("+digit+"))?)?)?";
String now="today|now|right now|this day|this day";
String timehours="("+digit+")|0("+digit+")|1("+ceroonetwo+")";
String timetotal="("+timehours+")(.|:)?(("+cerofive+")("+digit+"))?";
String ampm="a.m|p.m";
String holiday="christmas( day)?|easter|new year'?s|may day|boxing day|independence day|labor day|memorial day|veteran'?s day|thanksgiving";
String alphaNumber="one|two|three|four|five|six|seven|eight|nine|ten|eleven|twelve|thirteen|fourteen|fifteen|sixteen|seventeen|eighteen|nineteen|twenty|twenty-one|twenty-two|twenty-three|twenty-four|twenty-five|twenty-six|twenty-seven|twenty-eight|twenty-nine|thirty|thirty-one|thirty-two|thirty-three|thirty-four|thirty-five|thirty-six|thirty-seven|thirty-eight|thirty-nine|forty|forty-one|forty-two|forty-three|forty-four|forty-five|forty-six|forty-seven|forty-eight|forty-nine|fifty|fifty-one|fifty-two|fifty-three|fifty-four|fifty-five|fifty-six|fifty-seven|fifty-eight|fifty-nine|sixty|sixty-one|sixty-two|sixty-three|sixty-four|sixty-five|sixty-six|sixty-seven|sixty-eight|sixty-nine|seventy|seventy-one|seventy-two|seventy-three|seventy-four|seventy-five|seventy-six|seventy-seven|seventy-eight|seventy-nine|eighty|eighty-one|eighty-two|eighty-three|eighty-four|eighty-five|eighty-six|eighty-seven|eighty-eight|eighty-nine|ninety|ninety-one|ninety-two|ninety-three|ninety-four|ninety-five|ninety-six|ninety-seven|ninety-eight|ninety-nine|(one )?hundred;";
String dayweekmonthyear="days?|weeks?|months?|years?";
String later="later|after|earlier|before";
String dayName="monday|tuesday|wednesday|thursday|friday|saturday|sunday";




counter++;englishDateRegexMap.put(counter, String.format("\\b("+daynumber+") ("+monthname+")((,)? ("+yearnumber+"))?\\b"));
       patterns.add(Pattern.compile(englishDateRegexMap.get(counter)));
counter++;englishDateRegexMap.put(counter, String.format("\\b("+monthname+") ("+daynumber+")(st|nd|rd|th)?(,)?( ("+yearnumber+"))?\\b"));
       patterns.add(Pattern.compile(englishDateRegexMap.get(counter)));
counter++;englishDateRegexMap.put(counter, String.format("\\b("+yearnumber+")\\b"));
       patterns.add(Pattern.compile(englishDateRegexMap.get(counter)));
counter++;englishDateRegexMap.put(counter, String.format("\\b("+daynumber+")("+separation+")("+monthnumber+")(("+separation+")("+yearnumber+"))?\\b"));
       patterns.add(Pattern.compile(englishDateRegexMap.get(counter)));
counter++;englishDateRegexMap.put(counter, String.format("\\b("+yearnumber+")s\\b"));
       patterns.add(Pattern.compile(englishDateRegexMap.get(counter)));
counter++;englishDateRegexMap.put(counter, String.format("\\b("+earlymidlate+")("+hyphenorwhitespace+")("+yearnumber+")\\b"));
       patterns.add(Pattern.compile(englishDateRegexMap.get(counter)));
counter++;englishDateRegexMap.put(counter, String.format("\\b("+earlymidlate+")("+hyphenorwhitespace+")("+monthname+")( ("+yearnumber+"))?\\b"));
       patterns.add(Pattern.compile(englishDateRegexMap.get(counter)));
counter++;englishDateRegexMap.put(counter, String.format("\\b("+season+")( ("+yearnumber+"))?\\b"));
       patterns.add(Pattern.compile(englishDateRegexMap.get(counter)));
counter++;englishDateRegexMap.put(counter, String.format("\\b("+season+") of ("+yearnumber+")\\b"));
       patterns.add(Pattern.compile(englishDateRegexMap.get(counter)));
counter++;englishDateRegexMap.put(counter, String.format("\\b("+monthname+") (the )?("+daynumber+")(st|nd|rd|th)?(,)?( ("+yearnumber+"))?\\b"));
       patterns.add(Pattern.compile(englishDateRegexMap.get(counter)));
counter++;englishDateRegexMap.put(counter, String.format("\\b("+beginmiddleend+") of ("+yearnumber+")\\b"));
       patterns.add(Pattern.compile(englishDateRegexMap.get(counter)));
counter++;englishDateRegexMap.put(counter, String.format("\\b("+beginmiddleend+") of ("+monthname+")( ("+yearnumber+"))?\\b"));
       patterns.add(Pattern.compile(englishDateRegexMap.get(counter)));
counter++;englishDateRegexMap.put(counter, String.format("\\b("+yearnumberbc+") b(.)?c(.)?\\b"));
       patterns.add(Pattern.compile(englishDateRegexMap.get(counter)));
counter++;englishDateRegexMap.put(counter, String.format("\\b("+now+")\\b"));
       patterns.add(Pattern.compile(englishDateRegexMap.get(counter)));
counter++;englishDateRegexMap.put(counter, String.format("\\b("+timetotal+") ("+ampm+")\\b"));
       patterns.add(Pattern.compile(englishDateRegexMap.get(counter)));
counter++;englishDateRegexMap.put(counter, String.format("\\b("+holiday+")( ("+yearnumber+"))?\\b"));
       patterns.add(Pattern.compile(englishDateRegexMap.get(counter)));
counter++;englishDateRegexMap.put(counter, String.format("\\b("+alphaNumber+") ("+dayweekmonthyear+") ("+later+")\\b"));
       patterns.add(Pattern.compile(englishDateRegexMap.get(counter)));
counter++;englishDateRegexMap.put(counter, String.format("\\b("+dayName+")\\b"));
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
         RegexNameFinder timeFinder= AutomaticEnglishDateRules.initEnglishDateFinder();
         String input = "tuesday";
         RegexFinder rf = new RegexFinder();
         List<Span> timeSpans = rf.filterFind(timeFinder, input);
         for (Span s : timeSpans){
          System.out.println("DEBUGGING:" + s.getStart() + "=--="+s.getEnd());
          System.out.println("DEBUGGING span:" + input.substring(s.getStart(), s.getEnd()));
         LinkedList<String> normalizedStartAndEnd = normalizeEnglishDate(input.substring(s.getStart(), s.getEnd()));;
               System.out.println("DEBUGGING norm:" + normalizedStartAndEnd);;
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
           
//we need a variable for duration, we need to say if we split by blanks or by characters
    if (key == 1){
       int dayNumber = 1;
       int monthNumber = 1;
       int yearNumber = DateCommons.getYearFromAnchorDate();
     String[] parts = foundDate.split("\\s");
         dayNumber = Integer.parseInt(parts[0].replaceAll("\\p{P}", ""));;
        monthNumber = englishMonthName2Number.get(parts[1].toLowerCase().replaceAll("\\p{P}", ""));
        yearNumber = DateCommons.getYearFromAnchorDate();
    if (parts.length > 2){
     yearNumber = Integer.parseInt(parts[2].replaceAll("\\D", ""));
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
       int yearNumber = DateCommons.getYearFromAnchorDate();
     String[] parts = foundDate.split("\\s");
         if (parts[1].matches("\\d{1,2}(st|nd|rd|th)?(,)?")){
              dayNumber = Integer.parseInt(parts[1].replaceAll("(st|nd|rd|th)", "").replaceAll(",",""));
              };
        monthNumber = englishMonthName2Number.get(parts[0].toLowerCase().replaceAll("\\p{P}", ""));
        yearNumber = DateCommons.getYearFromAnchorDate();
    if (parts.length > 2){
     yearNumber = Integer.parseInt(parts[2].replaceAll("\\D", ""));
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
       int yearNumber = DateCommons.getYearFromAnchorDate();
     String[] parts = foundDate.split("\\s");
         yearNumber = DateCommons.getYearFromAnchorDate();
    if (parts.length > 0){
     yearNumber = Integer.parseInt(parts[0].replaceAll("\\D", ""));
    }
    else{
     yearNumber = DateCommons.getYearFromAnchorDate();
    }
                 cal.set(yearNumber, monthNumber-1, dayNumber, 0, 0, 0);
            normalizedStartDate = cal.getTime();
            normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, 365, normalizedStartDate);
            dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
            dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
            DateCommons.updateAnchorDate(normalizedStartDate);
                       }
    if (key == 4){
       int dayNumber = 1;
       int monthNumber = 1;
       int yearNumber = DateCommons.getYearFromAnchorDate();
     String[] parts = foundDate.split("\\D");
         dayNumber = Integer.parseInt(parts[0].replaceAll("\\p{P}", ""));;
        monthNumber = Integer.parseInt(parts[1]);
        yearNumber = DateCommons.getYearFromAnchorDate();
    if (parts.length > 2){
     yearNumber = Integer.parseInt(parts[2].replaceAll("\\D", ""));
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
    if (key == 5){
       int dayNumber = 1;
       int monthNumber = 1;
       int yearNumber = DateCommons.getYearFromAnchorDate();
     String[] parts = foundDate.split("\\s");
         yearNumber = DateCommons.getYearFromAnchorDate();
    if (parts.length > 0){
     yearNumber = Integer.parseInt(parts[0].replaceAll("\\D", ""));
    }
    else{
     yearNumber = DateCommons.getYearFromAnchorDate();
    }
                 cal.set(yearNumber, monthNumber-1, dayNumber, 0, 0, 0);
            normalizedStartDate = cal.getTime();
            normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, 3652, normalizedStartDate);
            dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
            dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
            DateCommons.updateAnchorDate(normalizedStartDate);
                       }
    if (key == 6){
       int dayNumber = 1;
       int monthNumber = 1;
       int yearNumber = DateCommons.getYearFromAnchorDate();
     String[] parts = foundDate.split("\\s|-");
           if (parts[0].toLowerCase().matches("early")){
          monthNumber = 1;
         }
         else if (parts[0].toLowerCase().matches("mid")){
          monthNumber = 6;
         }
         else if (parts[0].toLowerCase().matches("late")){
          monthNumber = 10;
         }
        yearNumber = DateCommons.getYearFromAnchorDate();
    if (parts.length > 1){
     yearNumber = Integer.parseInt(parts[1].replaceAll("\\D", ""));
    }
    else{
     yearNumber = DateCommons.getYearFromAnchorDate();
    }
                 cal.set(yearNumber, monthNumber-1, dayNumber, 0, 0, 0);
            normalizedStartDate = cal.getTime();
            normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, 60, normalizedStartDate);
            dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
            dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
            DateCommons.updateAnchorDate(normalizedStartDate);
                       }
    if (key == 7){
       int dayNumber = 1;
       int monthNumber = 1;
       int yearNumber = DateCommons.getYearFromAnchorDate();
     String[] parts = foundDate.split("\\s|-");
               if (parts[0].toLowerCase().matches("early")){
              dayNumber = 1;
             }
             else if (parts[0].toLowerCase().matches("mid")){
              dayNumber = 10;
             }
             else if (parts[0].toLowerCase().matches("late")){
              dayNumber = 20;
             }
        monthNumber = englishMonthName2Number.get(parts[1].toLowerCase().replaceAll("\\p{P}", ""));
        yearNumber = DateCommons.getYearFromAnchorDate();
    if (parts.length > 2){
     yearNumber = Integer.parseInt(parts[2].replaceAll("\\D", ""));
    }
    else{
     yearNumber = DateCommons.getYearFromAnchorDate();
    }
                 cal.set(yearNumber, monthNumber-1, dayNumber, 0, 0, 0);
            normalizedStartDate = cal.getTime();
            normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, 10, normalizedStartDate);
            dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
            dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
            DateCommons.updateAnchorDate(normalizedStartDate);
                       }
    if (key == 8){
       int dayNumber = 1;
       int monthNumber = 1;
       int yearNumber = DateCommons.getYearFromAnchorDate();
     String[] parts = foundDate.split("\\s");
            String season = parts[0];
              if (season.equalsIgnoreCase("spring")){
                    monthNumber = 3;
                    dayNumber = 31;
                   }
                   else if (season.equalsIgnoreCase("summer")){
                   monthNumber = 6;
                   dayNumber = 31;
       
                   }
                   else if (season.equalsIgnoreCase("(autumn|fall)")){
                   monthNumber = 9;
                   dayNumber = 30;
       
                   }
                   else if (season.equalsIgnoreCase("winter")){
                   monthNumber = 12;
                   dayNumber = 28;
       
                    if (((GregorianCalendar) cal).isLeapYear(yearNumber)){
                    dayNumber = 29;
                    }
                   }
                   if (season.equalsIgnoreCase("winter")){
                    cal.set(yearNumber-1, monthNumber-1, dayNumber,0,0,0);
                   }
                   else{
                    cal.set(yearNumber, monthNumber-1, dayNumber,0,0,0);
                   }
                      yearNumber = DateCommons.getYearFromAnchorDate();
    if (parts.length > 1){
     yearNumber = Integer.parseInt(parts[1].replaceAll("\\D", ""));
    }
    else{
     yearNumber = DateCommons.getYearFromAnchorDate();
    }
                 cal.set(yearNumber, monthNumber-1, dayNumber, 0, 0, 0);
            normalizedStartDate = cal.getTime();
            normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, 60, normalizedStartDate);
            dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
            dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
            DateCommons.updateAnchorDate(normalizedStartDate);
                       }
    if (key == 9){
       int dayNumber = 1;
       int monthNumber = 1;
       int yearNumber = DateCommons.getYearFromAnchorDate();
     String[] parts = foundDate.split("\\s");
            String season = parts[0];
              if (season.equalsIgnoreCase("spring")){
                    monthNumber = 3;
                    dayNumber = 31;
                   }
                   else if (season.equalsIgnoreCase("summer")){
                   monthNumber = 6;
                   dayNumber = 31;
       
                   }
                   else if (season.equalsIgnoreCase("(autumn|fall)")){
                   monthNumber = 9;
                   dayNumber = 30;
       
                   }
                   else if (season.equalsIgnoreCase("winter")){
                   monthNumber = 12;
                   dayNumber = 28;
       
                    if (((GregorianCalendar) cal).isLeapYear(yearNumber)){
                    dayNumber = 29;
                    }
                   }
                   if (season.equalsIgnoreCase("winter")){
                    cal.set(yearNumber-1, monthNumber-1, dayNumber,0,0,0);
                   }
                   else{
                    cal.set(yearNumber, monthNumber-1, dayNumber,0,0,0);
                   }
                      yearNumber = DateCommons.getYearFromAnchorDate();
    if (parts.length > 2){
     yearNumber = Integer.parseInt(parts[2].replaceAll("\\D", ""));
    }
    else{
     yearNumber = DateCommons.getYearFromAnchorDate();
    }
                 cal.set(yearNumber, monthNumber-1, dayNumber, 0, 0, 0);
            normalizedStartDate = cal.getTime();
            normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, 60, normalizedStartDate);
            dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
            dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
            DateCommons.updateAnchorDate(normalizedStartDate);
                         }
    if (key == 10){
       int dayNumber = 1;
       int monthNumber = 1;
       int yearNumber = DateCommons.getYearFromAnchorDate();
     String[] parts = foundDate.split("\\s");
         if (parts[2].matches("\\d{1,2}(st|nd|rd|th)?(,)?")){
              dayNumber = Integer.parseInt(parts[2].replaceAll("(st|nd|rd|th)", "").replaceAll(",",""));
              };
        monthNumber = englishMonthName2Number.get(parts[0].toLowerCase().replaceAll("\\p{P}", ""));
        yearNumber = DateCommons.getYearFromAnchorDate();
    if (parts.length > 3){
     yearNumber = Integer.parseInt(parts[3].replaceAll("\\D", ""));
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
    if (key == 11){
       int dayNumber = 1;
       int monthNumber = 1;
       int yearNumber = DateCommons.getYearFromAnchorDate();
     String[] parts = foundDate.split("\\s");
         if (parts[0].matches("(?i)(start|beginning)")){
             monthNumber = 1;
            }
            else if (parts[0].matches("(?i)middle")){
             monthNumber = 5;
            }
            else if (parts[0].matches("(?i)end")){
             monthNumber = 10;
            }        yearNumber = DateCommons.getYearFromAnchorDate();
    if (parts.length > 2){
     yearNumber = Integer.parseInt(parts[2].replaceAll("\\D", ""));
    }
    else{
     yearNumber = DateCommons.getYearFromAnchorDate();
    }
                 cal.set(yearNumber, monthNumber-1, dayNumber, 0, 0, 0);
            normalizedStartDate = cal.getTime();
            normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, 91, normalizedStartDate);
            dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
            dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
            DateCommons.updateAnchorDate(normalizedStartDate);
                       }
    if (key == 12){
       int dayNumber = 1;
       int monthNumber = 1;
       int yearNumber = DateCommons.getYearFromAnchorDate();
     String[] parts = foundDate.split("\\s");
             if (parts[0].matches("(?i)(start|beginning)")){
                 dayNumber = 1;
                }
                else if (parts[0].matches("(?i)middle")){
                 dayNumber = 10;
                }
                else if (parts[0].matches("(?i)end")){
                 dayNumber = DateCommons.getLastDayOfMonth(monthNumber, yearNumber) - 10;
                }
        monthNumber = englishMonthName2Number.get(parts[2].toLowerCase().replaceAll("\\p{P}", ""));
        yearNumber = DateCommons.getYearFromAnchorDate();
    if (parts.length > 3){
     yearNumber = Integer.parseInt(parts[3].replaceAll("\\D", ""));
    }
    else{
     yearNumber = DateCommons.getYearFromAnchorDate();
    }
                 cal.set(yearNumber, monthNumber-1, dayNumber, 0, 0, 0);
            normalizedStartDate = cal.getTime();
            normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, 10, normalizedStartDate);
            dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
            dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
            DateCommons.updateAnchorDate(normalizedStartDate);
                       }
    if (key == 13){
       int dayNumber = 1;
       int monthNumber = 1;
       int yearNumber = DateCommons.getYearFromAnchorDate();
     String[] parts = foundDate.split("\\s");
         String yearNumberBeforeChrist = parts[0];
         int yearSubtract = cal.get(Calendar.YEAR) + Integer.parseInt(yearNumberBeforeChrist);
       cal.add(Calendar.YEAR, -yearSubtract+1);
         yearNumber = cal.get(Calendar.YEAR);
                      cal.set(yearNumber, monthNumber-1, dayNumber, 0, 0, 0);
            normalizedStartDate = cal.getTime();
            normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, 365, normalizedStartDate);
            dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
            dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
            DateCommons.updateAnchorDate(normalizedStartDate);
                       }
    if (key == 14){
       int dayNumber = 1;
       int monthNumber = 1;
       int yearNumber = DateCommons.getYearFromAnchorDate();
     String[] parts = foundDate.split("\\s");
             yearNumber = DateCommons.getYearFromAnchorDate();
           monthNumber = DateCommons.getMonthFromAnchorDate();
           dayNumber = DateCommons.getDayFromAnchorDate();
                             cal.set(yearNumber, monthNumber-1, dayNumber, 0, 0, 0);
            normalizedStartDate = cal.getTime();
            normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, 1, normalizedStartDate);
            dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
            dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
            DateCommons.updateAnchorDate(normalizedStartDate);
                       }
    if (key == 15){
       int dayNumber = 1;
       int monthNumber = 1;
       int yearNumber = DateCommons.getYearFromAnchorDate();
     String[] parts = foundDate.split("\\s|:|\\.");
             yearNumber = DateCommons.getYearFromAnchorDate();
           monthNumber = DateCommons.getMonthFromAnchorDate();
           dayNumber = DateCommons.getDayFromAnchorDate();
                        int hour = 0;
       int minute = 0;
       if(parts[0].matches("([0-9]|0[0-9]|1[0-2])")){
             hour = Integer.parseInt(parts[0]);
            }
       if (parts[2].matches("p")|parts[1].matches("p"))
{             hour = hour+12;
            }
        if(parts[1].matches("[0-5][0-9]")){
             minute = Integer.parseInt(parts[1]);
            }
       else{
             //do nothing
            }
           cal.set(yearNumber,  monthNumber, dayNumber, hour, minute, 0);
               normalizedStartDate = cal.getTime();
               normalizedEndDate = DateCommons.increaseCalendar(Calendar.MINUTE, 1, normalizedStartDate);
               dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
               dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
               DateCommons.updateAnchorDate(normalizedStartDate);
                           }
    if (key == 16){
       int dayNumber = 1;
       int monthNumber = 1;
       int yearNumber = DateCommons.getYearFromAnchorDate();
     String[] parts = foundDate.split("\\s");
        if (parts[0].toLowerCase().matches("christmas")){
             monthNumber = 12;
             dayNumber = 25;
            }
            else if (parts[0].toLowerCase().matches("boxing")){
             monthNumber = 12;
             dayNumber = 26;
            }            else if (parts[0].toLowerCase().matches("thanksgiving")){
             monthNumber =  07;
             dayNumber = 04;
            }            else if (parts[0].toLowerCase().matches("veteran'?s")){
             monthNumber = 11 ;
             dayNumber = 11;
            }            else if (parts[0].toLowerCase().matches("new")){
             monthNumber = 1;
             dayNumber = 1;
            }            else if (parts[0].toLowerCase().matches("may")){
             monthNumber = 5;
             dayNumber = 1;
            }            else if (parts[0].toLowerCase().matches("labor")){
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
                         if (parts[parts.length-1].matches("^\\d{4}$")){
                    yearNumber = Integer.parseInt(parts[parts.length-1]);
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
    if (key == 17){
       int dayNumber = 1;
       int monthNumber = 1;
       int yearNumber = DateCommons.getYearFromAnchorDate();
     String[] parts = foundDate.split("\\s");
            int direction = 0;
                if (parts[2].toLowerCase().matches("(later|after)")){
                 direction = 1;
                }
                else if (parts[2].toLowerCase().matches("(earlier|before)")){
                 direction = -1;
                }
                   int increment = convertEnglishAlphaNumber(parts[0].toLowerCase()) * direction;
                yearNumber = DateCommons.getYearFromAnchorDate();
                monthNumber = DateCommons.getMonthFromAnchorDate();
                if (monthNumber == 0){
                 monthNumber += 1;
                }
                dayNumber = DateCommons.getDayFromAnchorDate();
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
    if (key == 18){
       int dayNumber = 1;
       int monthNumber = 1;
       int yearNumber = DateCommons.getYearFromAnchorDate();
     String[] parts = foundDate.split("\\s");
             dayNumber = DateCommons.getDayFromAnchorDate();
                 monthNumber = DateCommons.getMonthFromAnchorDate();
                 yearNumber = DateCommons.getYearFromAnchorDate();
                 cal.set(yearNumber,  monthNumber, dayNumber,0,0,0);
                 int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
                 int daysOfIncrease = 0;
                 int x = englishDayName2Integer.get(foundDate.trim().toLowerCase());
                 if (x > dayOfWeek){
                  // it is in the rest of the week
                  daysOfIncrease = x - dayOfWeek;
                 }
                 else if (x == dayOfWeek){
                  // do nothing, it is the current day
                  daysOfIncrease = 0;
                 }
                 else{ // else x smaller than currentDayOfWeek and the day is in the next week
                  daysOfIncrease = 7 - dayOfWeek + x;
                 }
                 normalizedStartDate = DateCommons.increaseCalendar(Calendar.DATE, daysOfIncrease, cal.getTime());
                 normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, 1, normalizedStartDate);
            
                 dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
                 dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
                 DateCommons.updateAnchorDate(normalizedStartDate);
                                }

               }
            }
            
              return dates;}
            
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
            static HashMap<String, Integer> englishDayName2Integer = new HashMap<String, Integer>(){{              put("sunday", 1);              put("monday", 2);              put("tuesday", 3);              put("thursday", 4);              put("wednesday", 5);              put("friday", 6);              put("saturday", 7);             }};      }           
