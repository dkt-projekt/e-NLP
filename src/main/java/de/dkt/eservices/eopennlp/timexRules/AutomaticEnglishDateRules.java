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
     static HashMap<Integer, String> dateRegexMap = new HashMap<Integer, String>();
          public static RegexNameFinder initDateFinder(){
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
String monthnumber="(0)?("+digit+")|1("+ceroonetwo+")";
String separation="\\.|/|\\-";
String hyphenorwhitespace="\\-| ";
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
String currency="\\$|€";
String separation2="\\.|,";




dateRegexMap.put(1, String.format("(?i)\\b("+daynumber+") ("+monthname+")((,)? ("+yearnumber+"))?\\b"));
       patterns.add(Pattern.compile(dateRegexMap.get(1)));
dateRegexMap.put(2, String.format("(?i)\\b("+monthname+") ("+daynumber+")(st|nd|rd|th)?(,)?( ("+yearnumber+"))?\\b"));
       patterns.add(Pattern.compile(dateRegexMap.get(2)));
dateRegexMap.put(3, String.format("(?i)\\b("+yearnumber+")\\b"));
       patterns.add(Pattern.compile(dateRegexMap.get(3)));
dateRegexMap.put(4, String.format("(?i)\\b("+daynumber+")("+separation+")("+monthnumber+")\\b"));
       patterns.add(Pattern.compile(dateRegexMap.get(4)));
dateRegexMap.put(5, String.format("(?i)\\b("+yearnumber+")s\\b"));
       patterns.add(Pattern.compile(dateRegexMap.get(5)));
dateRegexMap.put(6, String.format("(?i)\\b("+earlymidlate+")("+hyphenorwhitespace+")("+yearnumber+")\\b"));
       patterns.add(Pattern.compile(dateRegexMap.get(6)));
dateRegexMap.put(7, String.format("(?i)\\b("+earlymidlate+")("+hyphenorwhitespace+")("+monthname+")( ("+yearnumber+"))?\\b"));
       patterns.add(Pattern.compile(dateRegexMap.get(7)));
dateRegexMap.put(8, String.format("(?i)\\b("+season+")( ("+yearnumber+"))?\\b"));
       patterns.add(Pattern.compile(dateRegexMap.get(8)));
dateRegexMap.put(9, String.format("(?i)\\b("+season+") of ("+yearnumber+")\\b"));
       patterns.add(Pattern.compile(dateRegexMap.get(9)));
dateRegexMap.put(10, String.format("(?i)\\b("+monthname+") the ("+daynumber+")(st|nd|rd|th)?(,)?( ("+yearnumber+"))?\\b"));
       patterns.add(Pattern.compile(dateRegexMap.get(10)));
dateRegexMap.put(11, String.format("(?i)\\b("+beginmiddleend+") of ("+yearnumber+")\\b"));
       patterns.add(Pattern.compile(dateRegexMap.get(11)));
dateRegexMap.put(12, String.format("(?i)\\b("+beginmiddleend+") of ("+monthname+")( ("+yearnumber+"))?\\b"));
       patterns.add(Pattern.compile(dateRegexMap.get(12)));
dateRegexMap.put(13, String.format("(?i)\\b("+yearnumberbc+") b(.)?c(.)?\\b"));
       patterns.add(Pattern.compile(dateRegexMap.get(13)));
dateRegexMap.put(14, String.format("(?i)\\b("+now+")\\b"));
       patterns.add(Pattern.compile(dateRegexMap.get(14)));
dateRegexMap.put(15, String.format("(?i)\\b("+timetotal+") ("+ampm+")\\b"));
       patterns.add(Pattern.compile(dateRegexMap.get(15)));
dateRegexMap.put(16, String.format("(?i)\\b("+holiday+")( ("+yearnumber+"))?\\b"));
       patterns.add(Pattern.compile(dateRegexMap.get(16)));
dateRegexMap.put(17, String.format("(?i)\\b("+alphaNumber+") ("+dayweekmonthyear+") ("+later+")\\b"));
       patterns.add(Pattern.compile(dateRegexMap.get(17)));
dateRegexMap.put(18, String.format("(?i)\\b("+dayName+")\\b"));
       patterns.add(Pattern.compile(dateRegexMap.get(18)));
dateRegexMap.put(19, String.format("(?i)\\b("+daynumber+")("+separation+")("+monthnumber+")("+separation+")("+yearnumber+")\\b"));
       patterns.add(Pattern.compile(dateRegexMap.get(19)));
dateRegexMap.put(20, String.format("(?i)\\b("+monthname+")\\b"));
       patterns.add(Pattern.compile(dateRegexMap.get(20)));
dateRegexMap.put(21, String.format("(?i)\\b("+monthname+") ("+yearnumber+")\\b"));
       patterns.add(Pattern.compile(dateRegexMap.get(21)));
dateRegexMap.put(22, String.format("("+currency+")\\d{1,9}(?:("+separation2+")\\d{3})*(?:("+separation2+")\\d{2})"));
            patterns.add(Pattern.compile(dateRegexMap.get(22)));
dateRegexMap.put(23, String.format("(?i)\\b\\d{1,100} ("+yearnumber+")\\b"));
       patterns.add(Pattern.compile(dateRegexMap.get(23)));
dateRegexMap.put(24, String.format("(?i)\\b\\d{1,100}("+separation+")("+yearnumber+")\\b"));
       patterns.add(Pattern.compile(dateRegexMap.get(24)));
dateRegexMap.put(25, String.format("(?i)\\b("+yearnumber+") \\d{1,100}\\b"));
       patterns.add(Pattern.compile(dateRegexMap.get(25)));
dateRegexMap.put(26, String.format("(?i)\\b("+yearnumber+")("+separation+")\\d{1,100}\\b"));
       patterns.add(Pattern.compile(dateRegexMap.get(26)));
dateRegexMap.put(27, String.format("(?i)\\b("+daynumber+")("+separation+")("+monthnumber+")("+separation+")\\d{5,100}\\b"));
       patterns.add(Pattern.compile(dateRegexMap.get(27)));
dateRegexMap.put(28, String.format("(?i)\\b\\d{1,100}("+separation+")("+daynumber+")("+separation+")("+monthnumber+")\\b"));
       patterns.add(Pattern.compile(dateRegexMap.get(28)));
dateRegexMap.put(29, String.format("(?i)\\b\\d{1,100}("+separation+")("+daynumber+")("+separation+")("+monthnumber+")("+separation+")("+yearnumber+")\\b"));
       patterns.add(Pattern.compile(dateRegexMap.get(29)));
dateRegexMap.put(30, String.format("(?i)\\b("+daynumber+")("+separation+")("+monthnumber+")("+separation+")("+yearnumber+")("+separation+")\\d{1,100}\\b"));
       patterns.add(Pattern.compile(dateRegexMap.get(30)));


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
         RegexNameFinder timeFinder= AutomaticEnglishDateRules.initDateFinder();
         String input = "october 6 1990";
         RegexFinder rf = new RegexFinder();
         List<Span> timeSpans = rf.filterFind(timeFinder, input);
         for (Span s : timeSpans){
          System.out.println("DEBUGGING:" + s.getStart() + "=--="+s.getEnd());
          System.out.println("DEBUGGING span:" + input.substring(s.getStart(), s.getEnd()));
         LinkedList<String> normalizedStartAndEnd = normalizeDate(input.substring(s.getStart(), s.getEnd()));;
               System.out.println("DEBUGGING norm:" + normalizedStartAndEnd);;
         }
       }
  

            public static LinkedList<String> normalizeDate(String foundDate) {
               // this is directly coupled to the dateRegexMap thing. If that changes, this needs to be checked too
              Date normalizedStartDate = new Date();
              Date normalizedEndDate = new Date();
              Calendar cal = Calendar.getInstance();
            
              Iterator it = dateRegexMap.entrySet().iterator();
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
       int monthNumber = 0;
       int yearNumber = DateCommons.getYearFromAnchorDate();
     String[] parts = foundDate.split("\\s");
         dayNumber = Integer.parseInt(parts[0].replaceAll("\\p{P}", ""));;
        monthNumber = AutomaticEnglishData.monthName2Number.get(parts[1].toLowerCase().replaceAll("\\p{P}", ""));
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
       int monthNumber = 0;
       int yearNumber = DateCommons.getYearFromAnchorDate();
     String[] parts = foundDate.split("\\s");
         if (parts[1].matches("\\d{1,2}(st|nd|rd|th)?(,)?")){
              dayNumber = Integer.parseInt(parts[1].replaceAll("(st|nd|rd|th)", "").replaceAll(",",""));
              };
        monthNumber = AutomaticEnglishData.monthName2Number.get(parts[0].toLowerCase().replaceAll("\\p{P}", ""));
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
       int monthNumber = 0;
       int yearNumber = DateCommons.getYearFromAnchorDate();
     String[] parts = foundDate.split("\\s");
         yearNumber = DateCommons.getYearFromAnchorDate();
    if (parts.length > 0){
     yearNumber = Integer.parseInt(parts[0].replaceAll("\\D", ""));
    }
    else{
     yearNumber = DateCommons.getYearFromAnchorDate();
    }
             if(monthNumber>0){
                  cal.set(yearNumber, monthNumber-1, dayNumber, 0, 0, 0);
        }
        else{
                  cal.set(yearNumber, monthNumber, dayNumber, 0, 0, 0);
        }
            normalizedStartDate = cal.getTime();
            normalizedEndDate = DateCommons.increaseCalendar(Calendar.YEAR, 1, normalizedStartDate);
            dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
            dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
            DateCommons.updateAnchorDate(normalizedStartDate);
                       }
    if (key == 4){
       int dayNumber = 1;
       int monthNumber = 0;
       int yearNumber = DateCommons.getYearFromAnchorDate();
     String[] parts = foundDate.split("\\D");
         dayNumber = Integer.parseInt(parts[0].replaceAll("\\p{P}", ""));;
        monthNumber = Integer.parseInt(parts[1]);
             cal.set(yearNumber, monthNumber-1, dayNumber, 0, 0, 0);
            normalizedStartDate = cal.getTime();
            normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, 1, normalizedStartDate);
            dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
            dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
            DateCommons.updateAnchorDate(normalizedStartDate);
                       }
    if (key == 5){
       int dayNumber = 1;
       int monthNumber = 0;
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
       int monthNumber = 0;
       int yearNumber = DateCommons.getYearFromAnchorDate();
     String[] parts = foundDate.split("\\s|-");
          String a = parts[0].toLowerCase();
        if(AutomaticEnglishData.early2Month.containsKey(a)){
                  monthNumber = AutomaticEnglishData.early2Month.get(a)-1;
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
       int monthNumber = 0;
       int yearNumber = DateCommons.getYearFromAnchorDate();
     String[] parts = foundDate.split("\\s|-");
               String a = parts[0].toLowerCase();
                   if(AutomaticEnglishData.early2Day.containsKey(a)){
                             dayNumber = AutomaticEnglishData.early2Day.get(a);
                            }
                  
         monthNumber = AutomaticEnglishData.monthName2Number.get(parts[1].toLowerCase().replaceAll("\\p{P}", ""));
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
       int monthNumber = 0;
       int yearNumber = DateCommons.getYearFromAnchorDate();
     String[] parts = foundDate.split("\\s");
            String a = parts[0].toLowerCase();
       
       if(AutomaticEnglishData.season2Dates.containsKey(a)){
           String stringDates = AutomaticEnglishData.season2Dates.get(a);
           monthNumber = Integer.parseInt(stringDates.substring(0, 2))-1;
           dayNumber = Integer.parseInt(stringDates.substring(2, 4));
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
       int monthNumber = 0;
       int yearNumber = DateCommons.getYearFromAnchorDate();
     String[] parts = foundDate.split("\\s");
            String a = parts[0].toLowerCase();
       
       if(AutomaticEnglishData.season2Dates.containsKey(a)){
           String stringDates = AutomaticEnglishData.season2Dates.get(a);
           monthNumber = Integer.parseInt(stringDates.substring(0, 2))-1;
           dayNumber = Integer.parseInt(stringDates.substring(2, 4));
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
       int monthNumber = 0;
       int yearNumber = DateCommons.getYearFromAnchorDate();
     String[] parts = foundDate.split("\\s");
         if (parts[1].matches("the")){;
            dayNumber = Integer.parseInt(parts[1].replaceAll("(st|nd|rd|th)", "").replaceAll(",",""));
                           }
                           else{
                               dayNumber = Integer.parseInt(parts[(1)].replaceAll("(st|nd|rd|th)", "").replaceAll(",",""));
                               };
        monthNumber = AutomaticEnglishData.monthName2Number.get(parts[0].toLowerCase().replaceAll("\\p{P}", ""));
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
       int monthNumber = 0;
       int yearNumber = DateCommons.getYearFromAnchorDate();
     String[] parts = foundDate.split("\\s");
          String a = parts[0].toLowerCase();
        if(AutomaticEnglishData.start2Month.containsKey(a)){
                  monthNumber = AutomaticEnglishData.start2Month.get(a)-1;
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
            normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, 91, normalizedStartDate);
            dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
            dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
            DateCommons.updateAnchorDate(normalizedStartDate);
                       }
    if (key == 12){
       int dayNumber = 1;
       int monthNumber = 0;
       int yearNumber = DateCommons.getYearFromAnchorDate();
     String[] parts = foundDate.split("\\s");
               String a = parts[0].toLowerCase();
                   if(AutomaticEnglishData.start2Day.containsKey(a)){
                             dayNumber = AutomaticEnglishData.start2Day.get(a);
                            }
                  
         monthNumber = AutomaticEnglishData.monthName2Number.get(parts[2].toLowerCase().replaceAll("\\p{P}", ""));
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
       int monthNumber = 0;
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
       int monthNumber = 0;
       int yearNumber = DateCommons.getYearFromAnchorDate();
     String[] parts = foundDate.split("\\s");
             yearNumber = DateCommons.getYearFromAnchorDate();
           monthNumber = DateCommons.getMonthFromAnchorDate();
           dayNumber = DateCommons.getDayFromAnchorDate();
        int hour = 0;
           int minute = 0;
                             cal.set(yearNumber, monthNumber-1, dayNumber, 0, 0, 0);
            normalizedStartDate = cal.getTime();
            normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, 1, normalizedStartDate);
            dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
            dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
            DateCommons.updateAnchorDate(normalizedStartDate);
                       }
    if (key == 15){
       int dayNumber = 1;
       int monthNumber = 0;
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
{                  hour = hour+12;
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
       int monthNumber = 0;
       int yearNumber = DateCommons.getYearFromAnchorDate();
     String[] parts = foundDate.split("\\s");
           String a = parts[0].toLowerCase();
       
       if(AutomaticEnglishData.fixedHoliday2Dates.containsKey(a)){
           String stringDates = AutomaticEnglishData.fixedHoliday2Dates.get(a);
           monthNumber = Integer.parseInt(stringDates.substring(0, 2))-1;
           dayNumber = Integer.parseInt(stringDates.substring(2, 4));
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
       int monthNumber = 0;
       int yearNumber = DateCommons.getYearFromAnchorDate();
     String[] parts = foundDate.split("\\s");
            int direction = 0;
                if (parts[2].toLowerCase().matches("(later|after)")){
                 direction = 1;
                }
                else if (parts[2].toLowerCase().matches("(earlier|before)")){
                 direction = -1;
                }
                   int increment = AutomaticEnglishData.alpha2Number.get(parts[0]) * direction;
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
       int monthNumber = 0;
       int yearNumber = DateCommons.getYearFromAnchorDate();
     String[] parts = foundDate.split("\\s");
             dayNumber = DateCommons.getDayFromAnchorDate();
                 monthNumber = DateCommons.getMonthFromAnchorDate();
                 yearNumber = DateCommons.getYearFromAnchorDate();
                 cal.set(yearNumber,  monthNumber, dayNumber,0,0,0);
                 int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
                 int daysOfIncrease = 0;
                 int x = AutomaticEnglishData.dayName2Integer.get(foundDate.trim().toLowerCase());
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
    if (key == 19){
       int dayNumber = 1;
       int monthNumber = 0;
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
    if (key == 20){
       int dayNumber = 1;
       int monthNumber = 0;
       int yearNumber = DateCommons.getYearFromAnchorDate();
     String[] parts = foundDate.split("\\s");
         monthNumber = AutomaticEnglishData.monthName2Number.get(parts[0].toLowerCase().replaceAll("\\p{P}", ""));
             cal.set(yearNumber, monthNumber-1, dayNumber, 0, 0, 0);
            normalizedStartDate = cal.getTime();
            normalizedEndDate = DateCommons.increaseCalendar(Calendar.MONTH, 1, normalizedStartDate);
            dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
            dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
            DateCommons.updateAnchorDate(normalizedStartDate);
                       }
    if (key == 21){
       int dayNumber = 1;
       int monthNumber = 0;
       int yearNumber = DateCommons.getYearFromAnchorDate();
     String[] parts = foundDate.split("\\s");
         monthNumber = AutomaticEnglishData.monthName2Number.get(parts[0].toLowerCase().replaceAll("\\p{P}", ""));
        yearNumber = DateCommons.getYearFromAnchorDate();
    if (parts.length > 1){
     yearNumber = Integer.parseInt(parts[1].replaceAll("\\D", ""));
    }
    else{
     yearNumber = DateCommons.getYearFromAnchorDate();
    }
                 cal.set(yearNumber, monthNumber-1, dayNumber, 0, 0, 0);
            normalizedStartDate = cal.getTime();
            normalizedEndDate = DateCommons.increaseCalendar(Calendar.MONTH, 1, normalizedStartDate);
            dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
            dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
            DateCommons.updateAnchorDate(normalizedStartDate);
                       }

               }
            }
            
              return dates;}
            
                              }           
