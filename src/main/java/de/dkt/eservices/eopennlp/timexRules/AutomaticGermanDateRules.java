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
     
     public class AutomaticGermanDateRules {
     static HashMap<Integer, String> dateRegexMap = new HashMap<Integer, String>();
          public static RegexNameFinder initDateFinder(){
       int counter = 0;
       List<Pattern> patterns = new LinkedList<Pattern>();
       
       


String digit="0|1|2|3|4|5|6|7|8|9";
String ceroone="0|1";
String cerofive="0|1|2|3|4|5";
String onetwo="1|2";
String ceroonetwo="0|1|2";
String cerotofour="0|1|2|3|4";
String daynumber="(0)?("+digit+")|1("+digit+")|2("+digit+")|3("+ceroone+")";
String monthname="januar|februar|märz|april|mai|juni|juli|august|september|oktober|november|dezember";
String yearnumber="("+onetwo+")("+digit+")("+digit+")("+digit+")";
String monthnumber="0|1|2|3|4|5|6|7|8|9|10|11|12";
String separation="\\.|/|\\-";
String hyphenorwhitespace="\\-| ";
String season="sommer|winter|frühling|herbst";
String earlymidlate="anfang|mitte|ende";
String beginmiddleend="anfang|mitte|ende";
String yearnumberbc="("+digit+")(("+digit+")(("+digit+")(("+digit+"))?)?)?";
String now="heute|jetzt|gegenwärtig|am selben tag";
String timehours="("+digit+")|0("+digit+")|1("+digit+")|2("+cerotofour+")";
String timetotal="("+timehours+")(\\.|:)?(("+cerofive+")("+digit+"))?";
String ampm="uhr";
String holiday="weihnachten|erster weihnacht(stag)?|ostern|silvester|neujahr|maifeiertag|tag der deutschen einheit|tag der arbeit";
String alphaNumber="null|eins|zwei|drei|vier|fünf|sechs|sieben|acht|neun|zehn|elf|zwölf|dreizehn|vierzehn|fünfzehn|sechzehn|siebzehn|achtzehn|neunzehn|zwanzig|einundzwanzig|zweiundzwanzig|dreiundzwanzig|vierundzwanzig|fünfundzwanzig|sechsundzwanzig|siebenundzwanzig|achtundzwanzig|neunundzwanzig|dreiβig|einunddreiβig|zweiundreißig|dreiunddreißig|vierunddreißig|fünfunddreißig|sechsunddreißig|siebenunddreißig|achtunddreißig|neununddreißig|vierzig|einundvierzig|zweiundvierzig|dreiundvierzig|vierundvierzig|fünfundvierzig|sechsundvierzig|siebenundvierzig|achtundvierzig|neunundvierzig|fünfzig|einundfünfzig|zweiundfünfzig|dreiundfünfzig|vierundfünfzig|fünfundfünfzig|sechsundfünfzig|siebenundfünfzig|achtundfünfzig|neunundfünfzig|sechzig|einundsechzig|zweiundsechzig|dreiundsechzig|vierundsechzig|fünfundsechzig|sechsundsechzig|siebenundsechzig|achtundsechzig|neunundsechzig|siebzig|einundsiebzig|zweiundsiebzig|dreiundsiebzig|vierundsiebzig|fünfundsiebzig|sechsundsiebzig|siebenundsiebzig|achtundsiebzig|neunundsiebzig|achtzig|einundachtzig|zweiundachtzig|dreiundachtzig|vierundachtzig|fünfundachtzig|sechsundachtzig|siebenundachtzig|achtundachtzig|neunundachtzig|neunzig|einundneunzig|zweiundneunzig|dreiundneunzig|vierundneunzig|fünfundneunzig|sechsundneunzig|siebenundneunzig|achtundneunzig|neunundneunzig|hundert";
String later="später|früher|zuvor|eher";
String dayweekmonthyear="tage?|wochen?|monate?|jahre?|tagen|monaten|jahren";
String dayName="montag|dienstag|mittwoch|donnerstag|freitag|samstag|sonntag";
String dayPart="mittag|abend|nacht";
String yesterday="gestern";
String tomorrow="morgen";
String monthRomanNumber="I|II|III|IV|V|VI|VII|VII|IX|X|XI|XII";
String nineteenHundred_twoDigitYearNumber="("+digit+")("+digit+")";
String dies="dieser|dieses|diese|diesen|diesem";
String next="nächste|nächster|nächstes|nächsten";
String sincebefore="seit|vor";
String day="tage?";
String end="jahresende|monatsende";
String maxfourdigitnumber="("+digit+")(("+digit+"))?(("+digit+"))?(("+digit+"))?";
String alphaNumOrNum="("+alphaNumber+")|("+maxfourdigitnumber+")";
String currency="\\$|€";
String separation2="\\.|,";







dateRegexMap.put(1, String.format("(?i)\\b("+daynumber+")\\. ("+monthname+")( ("+yearnumber+"))?\\b"));
       patterns.add(Pattern.compile(dateRegexMap.get(1)));
dateRegexMap.put(2, String.format("(?i)\\b("+daynumber+")(te|ten|ter)? ("+monthname+")( ("+yearnumber+"))?\\b"));
       patterns.add(Pattern.compile(dateRegexMap.get(2)));
dateRegexMap.put(3, String.format("(?i)\\b("+yearnumber+")\\b"));
       patterns.add(Pattern.compile(dateRegexMap.get(3)));
dateRegexMap.put(4, String.format("(?i)\\b("+daynumber+")("+separation+")("+monthnumber+")(("+separation+")("+yearnumber+"))?\\b"));
       patterns.add(Pattern.compile(dateRegexMap.get(4)));
dateRegexMap.put(5, String.format("(?i)\\b("+yearnumber+")er\\b"));
       patterns.add(Pattern.compile(dateRegexMap.get(5)));
dateRegexMap.put(6, String.format("(?i)\\b("+earlymidlate+") ("+yearnumber+")\\b"));
       patterns.add(Pattern.compile(dateRegexMap.get(6)));
dateRegexMap.put(7, String.format("(?i)\\b("+earlymidlate+") ("+monthname+")( ("+yearnumber+"))?\\b"));
       patterns.add(Pattern.compile(dateRegexMap.get(7)));
dateRegexMap.put(8, String.format("(?i)\\b("+season+")( ("+yearnumber+"))?\\b"));
       patterns.add(Pattern.compile(dateRegexMap.get(8)));

dateRegexMap.put(13, String.format("(?i)\\b("+yearnumberbc+") v(\\. )?Chr(.)?\\b"));
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
dateRegexMap.put(19, String.format("(?i)\\b("+monthname+")(,)? ("+yearnumber+")\\b"));
       patterns.add(Pattern.compile(dateRegexMap.get(19)));
dateRegexMap.put(20, String.format("(?i)\\b("+monthnumber+")("+separation+")("+yearnumber+")\\b"));
       patterns.add(Pattern.compile(dateRegexMap.get(20)));
dateRegexMap.put(21, String.format("(?i)\\b("+dayPart+")\\b"));
       patterns.add(Pattern.compile(dateRegexMap.get(21)));
dateRegexMap.put(22, String.format("(?i)\\b("+monthname+")\\b"));
       patterns.add(Pattern.compile(dateRegexMap.get(22)));
dateRegexMap.put(23, String.format("(?i)\\b("+yesterday+")\\b"));
       patterns.add(Pattern.compile(dateRegexMap.get(23)));
dateRegexMap.put(24, String.format("(?i)\\b("+tomorrow+")\\b"));
       patterns.add(Pattern.compile(dateRegexMap.get(24)));
dateRegexMap.put(25, String.format("(?i)\\b("+daynumber+")("+separation+")("+monthRomanNumber+")("+separation+")("+nineteenHundred_twoDigitYearNumber+")\\b"));
       patterns.add(Pattern.compile(dateRegexMap.get(25)));
dateRegexMap.put(26, String.format("(?i)\\b("+dies+") ("+dayweekmonthyear+")\\b"));
       patterns.add(Pattern.compile(dateRegexMap.get(26)));
dateRegexMap.put(27, String.format("(?i)\\b("+next+") ("+dayweekmonthyear+")\\b"));
       patterns.add(Pattern.compile(dateRegexMap.get(27)));
dateRegexMap.put(28, String.format("(?i)\\bnach ("+alphaNumber+") ("+dayweekmonthyear+")\\b"));
       patterns.add(Pattern.compile(dateRegexMap.get(28)));
dateRegexMap.put(29, String.format("(?i)\\b("+sincebefore+") ("+alphaNumOrNum+") ("+dayweekmonthyear+")\\b"));
       patterns.add(Pattern.compile(dateRegexMap.get(29)));
dateRegexMap.put(30, String.format("(?i)\\b("+day+")\\b"));
       patterns.add(Pattern.compile(dateRegexMap.get(30)));
dateRegexMap.put(31, String.format("(?i)\\b("+alphaNumber+") ("+day+") vor ("+end+")\\b"));
       patterns.add(Pattern.compile(dateRegexMap.get(31)));

dateRegexMap.put(32, String.format("("+currency+")\\d{1,9}(?:("+separation2+")\\d{3})*(?:("+separation2+")\\d{2})"));
            patterns.add(Pattern.compile(dateRegexMap.get(32)));
dateRegexMap.put(33, String.format("(?i)\\b\\d{1,100} ("+yearnumber+")\\b"));
       patterns.add(Pattern.compile(dateRegexMap.get(33)));
dateRegexMap.put(34, String.format("(?i)\\b\\d{1,100}("+separation+")("+yearnumber+")\\b"));
       patterns.add(Pattern.compile(dateRegexMap.get(34)));
dateRegexMap.put(35, String.format("(?i)\\b("+yearnumber+") \\d{1,100}\\b"));
       patterns.add(Pattern.compile(dateRegexMap.get(35)));
dateRegexMap.put(36, String.format("(?i)\\b("+yearnumber+")("+separation+")\\d{1,100}\\b"));
       patterns.add(Pattern.compile(dateRegexMap.get(36)));
dateRegexMap.put(37, String.format("(?i)\\b("+daynumber+")("+separation+")("+monthnumber+")("+separation+")\\d{5,100}\\b"));
       patterns.add(Pattern.compile(dateRegexMap.get(37)));
dateRegexMap.put(38, String.format("(?i)\\b\\d{1,100}("+separation+")("+daynumber+")("+separation+")("+monthnumber+")\\b"));
       patterns.add(Pattern.compile(dateRegexMap.get(38)));
dateRegexMap.put(39, String.format("(?i)\\b\\d{1,100}("+separation+")("+daynumber+")("+separation+")("+monthnumber+")("+separation+")("+yearnumber+")\\b"));
       patterns.add(Pattern.compile(dateRegexMap.get(39)));
dateRegexMap.put(40, String.format("(?i)\\b("+daynumber+")("+separation+")("+monthnumber+")("+separation+")("+yearnumber+")("+separation+")\\d{1,100}\\b"));
       patterns.add(Pattern.compile(dateRegexMap.get(40)));
dateRegexMap.put(41, String.format("\\d{1,9}(?:("+separation2+")\\d{3})*(?:("+separation2+")\\d{2})("+currency+")"));
            patterns.add(Pattern.compile(dateRegexMap.get(41)));






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
         RegexNameFinder timeFinder= AutomaticGermanDateRules.initDateFinder();
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
        monthNumber = AutomaticGermanData.monthName2Number.get(parts[1].toLowerCase().replaceAll("\\p{P}", ""));
        yearNumber = DateCommons.getYearFromAnchorDate();
    if (parts.length > 2){
     yearNumber = Integer.parseInt(parts[2].replaceAll("\\D", ""));
    }
    else{
     yearNumber = DateCommons.getYearFromAnchorDate();
    }
                 cal.set(yearNumber, monthNumber, dayNumber, 0, 0, 0);
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
         if (parts[0].matches("\\d{1,2}(te|ten|ter)?(,)?")){
              dayNumber = Integer.parseInt(parts[0].replaceAll("\\D", ""));
              };
        monthNumber = AutomaticGermanData.monthName2Number.get(parts[1].toLowerCase().replaceAll("\\p{P}", ""));
        yearNumber = DateCommons.getYearFromAnchorDate();
    if (parts.length > 2){
     yearNumber = Integer.parseInt(parts[2].replaceAll("\\D", ""));
    }
    else{
     yearNumber = DateCommons.getYearFromAnchorDate();
    }
                 cal.set(yearNumber, monthNumber, dayNumber, 0, 0, 0);
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
                      cal.set(yearNumber, monthNumber, dayNumber, 0, 0, 0);
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
        yearNumber = DateCommons.getYearFromAnchorDate();
    if (parts.length > 2){
     yearNumber = Integer.parseInt(parts[2].replaceAll("\\D", ""));
    }
    else{
     yearNumber = DateCommons.getYearFromAnchorDate();
    }
                 cal.set(yearNumber, monthNumber, dayNumber, 0, 0, 0);
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
                 cal.set(yearNumber, monthNumber, dayNumber, 0, 0, 0);
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
        if(AutomaticGermanData.early2Month.containsKey(a)){
                  monthNumber = AutomaticGermanData.early2Month.get(a)-1;
                 }
       
               yearNumber = DateCommons.getYearFromAnchorDate();
    if (parts.length > 1){
     yearNumber = Integer.parseInt(parts[1].replaceAll("\\D", ""));
    }
    else{
     yearNumber = DateCommons.getYearFromAnchorDate();
    }
                 cal.set(yearNumber, monthNumber, dayNumber, 0, 0, 0);
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
                   if(AutomaticGermanData.early2Day.containsKey(a)){
                             dayNumber = AutomaticGermanData.early2Day.get(a);
                            }
                  
         monthNumber = AutomaticGermanData.monthName2Number.get(parts[1].toLowerCase().replaceAll("\\p{P}", ""));
        yearNumber = DateCommons.getYearFromAnchorDate();
    if (parts.length > 2){
     yearNumber = Integer.parseInt(parts[2].replaceAll("\\D", ""));
    }
    else{
     yearNumber = DateCommons.getYearFromAnchorDate();
    }
                 cal.set(yearNumber, monthNumber, dayNumber, 0, 0, 0);
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
       
       if(AutomaticGermanData.season2Dates.containsKey(a)){
           String stringDates = AutomaticGermanData.season2Dates.get(a);
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
                 cal.set(yearNumber, monthNumber, dayNumber, 0, 0, 0);
            normalizedStartDate = cal.getTime();
            normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, 60, normalizedStartDate);
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
                           cal.set(yearNumber, monthNumber, dayNumber, 0, 0, 0);
                 normalizedStartDate = cal.getTime();
                 normalizedEndDate = DateCommons.increaseCalendar(Calendar.YEAR, 1, normalizedStartDate);
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
                             cal.set(yearNumber, monthNumber, dayNumber, 0, 0, 0);
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
                                         if(parts[0].matches("([0-9]|0[0-9]|1[0-9]|2[0-4])")){
                  hour = Integer.parseInt(parts[0]);
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
        if (parts[parts.length-1].matches("^\\d{4}$")){
                    yearNumber = Integer.parseInt(parts[parts.length-1]);
                   }
                   else{
                    yearNumber = DateCommons.getYearFromAnchorDate();
                   }
                    String a = parts[0].toLowerCase();
       
       if(AutomaticGermanData.fixedHoliday2Dates.containsKey(a)){
           String stringDates = AutomaticGermanData.fixedHoliday2Dates.get(a);
           monthNumber = Integer.parseInt(stringDates.substring(0, 2))-1;
           dayNumber = Integer.parseInt(stringDates.substring(2, 4));
          }
       
                                else if (parts[0].toLowerCase().matches("ostern")){
                  Calendar loCal = Calendar.getInstance();
                  loCal.setTime(DateCommons.getEasterDate(yearNumber));
                  monthNumber = loCal.get(Calendar.MONTH);
                  dayNumber = loCal.get(Calendar.DATE);
                 }
            else if (parts[0].toLowerCase().matches("erster")){
                  monthNumber = 11;
                  dayNumber = 26;
                 }
                 else if (parts.length>2 && parts[2].toLowerCase().matches("arbeit")){
                  monthNumber = 4;
                  dayNumber = 1;
                 }
            else if (parts.length>2 && parts[2].toLowerCase().matches("deutschen")){
                             monthNumber = 9;
                             dayNumber = 3            ;
                            }
                                                    cal.set(yearNumber, monthNumber, dayNumber, 0, 0, 0);
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
                if (parts[2].toLowerCase().matches(AutomaticGermanData.beforeAfter.get(1))){
                 direction = 1;
                }
                else if (parts[2].toLowerCase().matches(AutomaticGermanData.beforeAfter.get(2))){
                 direction = -1;
                }
                   int increment = AutomaticGermanData.alpha2Number.get(parts[0]) * direction;
                yearNumber = DateCommons.getYearFromAnchorDate();
                monthNumber = DateCommons.getMonthFromAnchorDate();
                if (monthNumber == 0){
                 monthNumber += 1;
                }
                dayNumber = DateCommons.getDayFromAnchorDate();
                cal.set(yearNumber,  monthNumber, dayNumber,0,0,0);
                Date anchor = cal.getTime();
                //final String day = "(?i)days?";
                //final String week = "(?i)weeks?";
                //final String month = "(?i)months?";
                //final String year = "(?i)years?";
                if (parts[1].toLowerCase().matches("(?i)tage?")){
                 normalizedStartDate = DateCommons.increaseCalendar(Calendar.DATE, increment, anchor);
                 normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, 1, normalizedStartDate);
                }
                else if (parts[1].toLowerCase().matches("(?i)wochen?")){
                 normalizedStartDate = DateCommons.increaseCalendar(Calendar.DATE, increment * 7, anchor);
                 normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, 1 * 7, normalizedStartDate);
                }
                else if (parts[1].toLowerCase().matches("(?i)monate?")){
                 normalizedStartDate = DateCommons.increaseCalendar(Calendar.MONTH, increment, anchor);
                 normalizedEndDate = DateCommons.increaseCalendar(Calendar.MONTH, 1, normalizedStartDate);
                }
                else if (parts[1].toLowerCase().matches("(?i)jahre?")){
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
                 int x = AutomaticGermanData.dayName2Integer.get(foundDate.trim().toLowerCase());
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
     String[] parts = foundDate.split("\\s");
        monthNumber = AutomaticGermanData.monthName2Number.get(parts[0].toLowerCase().replaceAll("\\p{P}", ""));
       yearNumber = DateCommons.getYearFromAnchorDate();
    if (parts.length > 1){
     yearNumber = Integer.parseInt(parts[1].replaceAll("\\D", ""));
    }
    else{
     yearNumber = DateCommons.getYearFromAnchorDate();
    }
                cal.set(yearNumber, monthNumber, dayNumber, 0, 0, 0);
            normalizedStartDate = cal.getTime();
            normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, 30, normalizedStartDate);
            dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
            dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
            DateCommons.updateAnchorDate(normalizedStartDate);
                       }
    if (key == 20){
       int dayNumber = 1;
       int monthNumber = 0;
       int yearNumber = DateCommons.getYearFromAnchorDate();
     String[] parts = foundDate.split("\\.|/|-");
        monthNumber = Integer.parseInt(parts[0]);
       yearNumber = DateCommons.getYearFromAnchorDate();
    if (parts.length > 1){
     yearNumber = Integer.parseInt(parts[1].replaceAll("\\D", ""));
    }
    else{
     yearNumber = DateCommons.getYearFromAnchorDate();
    }
                cal.set(yearNumber, monthNumber, dayNumber, 0, 0, 0);
            normalizedStartDate = cal.getTime();
            normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, 30, normalizedStartDate);
            dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
            dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
            DateCommons.updateAnchorDate(normalizedStartDate);
                       }
    if (key == 21){
       int dayNumber = 1;
       int monthNumber = 0;
       int yearNumber = DateCommons.getYearFromAnchorDate();
     String[] parts = foundDate.split("\\s");
            yearNumber = DateCommons.getYearFromAnchorDate();
           monthNumber = DateCommons.getMonthFromAnchorDate();
           dayNumber = DateCommons.getDayFromAnchorDate();
        int hour = 0;
           int minute = 0;
                               String a = parts[0].toLowerCase();
                    if(AutomaticGermanData.dayPart2Hour.containsKey(a)){
                              hour = AutomaticGermanData.dayPart2Hour.get(a);
                             }
                   
                             cal.set(yearNumber,  monthNumber, dayNumber, hour, minute, 0);
               normalizedStartDate = cal.getTime();
               normalizedEndDate = DateCommons.increaseCalendar(Calendar.MINUTE, 180, normalizedStartDate);
               dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
               dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
               DateCommons.updateAnchorDate(normalizedStartDate);
                           }
    if (key == 22){
       int dayNumber = 1;
       int monthNumber = 0;
       int yearNumber = DateCommons.getYearFromAnchorDate();
     String[] parts = foundDate.split("\\s");
        monthNumber = AutomaticGermanData.monthName2Number.get(parts[0].toLowerCase().replaceAll("\\p{P}", ""));
            cal.set(yearNumber, monthNumber, dayNumber, 0, 0, 0);
            normalizedStartDate = cal.getTime();
            normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, 30, normalizedStartDate);
            dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
            dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
            DateCommons.updateAnchorDate(normalizedStartDate);
                       }
    if (key == 23){
       int dayNumber = 1;
       int monthNumber = 0;
       int yearNumber = DateCommons.getYearFromAnchorDate();
     String[] parts = foundDate.split("\\s");
                      yearNumber = DateCommons.getYearFromAnchorDate();
                     monthNumber = DateCommons.getMonthFromAnchorDate();
                     dayNumber = DateCommons.getDayFromAnchorDate()-1;
            int hour = 0;
                      int minute = 0;
                                                cal.set(yearNumber, monthNumber, dayNumber, 0, 0, 0);
            normalizedStartDate = cal.getTime();
            normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, 1, normalizedStartDate);
            dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
            dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
            DateCommons.updateAnchorDate(normalizedStartDate);
                       }
    if (key == 24){
       int dayNumber = 1;
       int monthNumber = 0;
       int yearNumber = DateCommons.getYearFromAnchorDate();
     String[] parts = foundDate.split("\\s");
                          yearNumber = DateCommons.getYearFromAnchorDate();
                         monthNumber = DateCommons.getMonthFromAnchorDate();
                         dayNumber = DateCommons.getDayFromAnchorDate()+1;
                int hour = 0;
                          int minute = 0;
                                                        cal.set(yearNumber, monthNumber, dayNumber, 0, 0, 0);
            normalizedStartDate = cal.getTime();
            normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, 1, normalizedStartDate);
            dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
            dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
            DateCommons.updateAnchorDate(normalizedStartDate);
                       }
    if (key == 25){
       int dayNumber = 1;
       int monthNumber = 0;
       int yearNumber = DateCommons.getYearFromAnchorDate();
     String[] parts = foundDate.split("\\.|/|-");
        dayNumber = Integer.parseInt(parts[0].replaceAll("\\p{P}", ""));;
        String a = parts[1].toLowerCase();
        if(AutomaticGermanData.romanMonth2Number.containsKey(a)){
                  monthNumber = AutomaticGermanData.romanMonth2Number.get(a)-1;
                 }
       
                   yearNumber = DateCommons.getYearFromAnchorDate();
         if (parts.length > 2){
          yearNumber = 1900+Integer.parseInt(parts[2].replaceAll("\\D", ""));
         }
         else{
          yearNumber = DateCommons.getYearFromAnchorDate();
         }
                     cal.set(yearNumber, monthNumber, dayNumber, 0, 0, 0);
            normalizedStartDate = cal.getTime();
            normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, 30, normalizedStartDate);
            dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
            dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
            DateCommons.updateAnchorDate(normalizedStartDate);
                       }
    if (key == 26){
       int dayNumber = 1;
       int monthNumber = 0;
       int yearNumber = DateCommons.getYearFromAnchorDate();
     String[] parts = foundDate.split("\\s");
        dayNumber = DateCommons.getDayFromAnchorDate();
            monthNumber = DateCommons.getMonthFromAnchorDate();
            yearNumber = DateCommons.getYearFromAnchorDate();
       
            if(parts[1].matches("(?i)jahre?")){
       
             cal.set(yearNumber, 0, 1, 0, 0, 0);
             normalizedStartDate = cal.getTime();
             cal.set(yearNumber+1,  0, 1, 0, 0, 0);
             normalizedEndDate = cal.getTime();
       
            }
            if(parts[1].matches("(?i)monate?")){
       
             cal.set(yearNumber, monthNumber, 1, 0, 0, 0);
       

             normalizedStartDate = cal.getTime();
             normalizedEndDate = cal.getTime();
             normalizedEndDate = DateCommons.increaseCalendar(Calendar.MONTH, 1, normalizedStartDate);
       
            }
       if(parts[1].matches("(?i)wochen?")){
       
             cal.set(yearNumber, monthNumber, dayNumber, 0, 0, 0);
             int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
             int x = 7 - dayOfWeek;
       
             cal.set(yearNumber, monthNumber, dayNumber, 0, 0, 0);
             normalizedStartDate = cal.getTime();
             normalizedStartDate = DateCommons.increaseCalendar(Calendar.DATE, -dayOfWeek, normalizedStartDate);
             cal.set(yearNumber, monthNumber, dayNumber, 0, 0, 0);
             normalizedEndDate = cal.getTime();
             normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, x, normalizedEndDate);
       
            }if(parts[1].matches("(?i)tage?")){
       
             cal.set(yearNumber, monthNumber, dayNumber, 0, 0, 0);
             normalizedStartDate = cal.getTime();
             normalizedEndDate = cal.getTime();
             normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, 1, normalizedStartDate);
            }
            dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
            dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
            DateCommons.updateAnchorDate(normalizedStartDate);
            DateCommons.updateAnchorDate(normalizedEndDate);
           
       
                                    }
    if (key == 27){
       int dayNumber = 1;
       int monthNumber = 0;
       int yearNumber = DateCommons.getYearFromAnchorDate();
     String[] parts = foundDate.split("\\s");
             dayNumber = DateCommons.getDayFromAnchorDate();
                 monthNumber = DateCommons.getMonthFromAnchorDate();
                 yearNumber = DateCommons.getYearFromAnchorDate();
            
                 if(parts[1].matches("(?i)jahre?")){
            
                  cal.set(yearNumber+1, 0, 1, 0, 0, 0);
                  normalizedStartDate = cal.getTime();
                  cal.set(yearNumber+2,  0, 1, 0, 0, 0);
                  normalizedEndDate = cal.getTime();
            
                 }
                 if(parts[1].matches("(?i)monate?")){
            
                  cal.set(yearNumber, monthNumber+1, 1, 0, 0, 0);
            

                  normalizedStartDate = cal.getTime();
                  normalizedEndDate = cal.getTime();
                  normalizedEndDate = DateCommons.increaseCalendar(Calendar.MONTH, 1, normalizedStartDate);
            
                 }
                 if(parts[1].matches("(?i)wochen?")){
            
                  cal.set(yearNumber, monthNumber, dayNumber, 0, 0, 0);
                  int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
                  int x = 7 - dayOfWeek;
            
                  cal.set(yearNumber, monthNumber, dayNumber, 0, 0, 0);
                  normalizedStartDate = cal.getTime();
                  normalizedStartDate = DateCommons.increaseCalendar(Calendar.DATE, -dayOfWeek+7, normalizedStartDate);
                  cal.set(yearNumber, monthNumber, dayNumber, 0, 0, 0);
                  normalizedEndDate = cal.getTime();
                  normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, x+7, normalizedEndDate);
            
                 }if(parts[1].matches("(?i)tage?")){
            
                  cal.set(yearNumber, monthNumber, dayNumber+1, 0, 0, 0);
                  normalizedStartDate = cal.getTime();
                  normalizedEndDate = cal.getTime();
                  normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, 1, normalizedStartDate);
                 }
                 dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
                 dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
                 DateCommons.updateAnchorDate(normalizedStartDate);
                 DateCommons.updateAnchorDate(normalizedEndDate);
                
            
                                         }
    if (key == 28){
       int dayNumber = 1;
       int monthNumber = 0;
       int yearNumber = DateCommons.getYearFromAnchorDate();
     String[] parts = foundDate.split("\\s");
        
            int number = AutomaticGermanData.alpha2Number.get((parts[1]));
            dayNumber = DateCommons.getDayFromAnchorDate();
            monthNumber = DateCommons.getMonthFromAnchorDate();
            yearNumber = DateCommons.getYearFromAnchorDate();
       
            cal.set(yearNumber,  monthNumber, dayNumber,0,0,0);
            final String day = "(?i)tage?|tagen";
            final String week = "(?i)wochen?";
            final String month = "(?i)monate?|monaten";
            final String year = "(?i)jahre?|jahren";
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
    if (key == 29){
       int dayNumber = 1;
       int monthNumber = 0;
       int yearNumber = DateCommons.getYearFromAnchorDate();
     String[] parts = foundDate.split("\\s");
         int number = 0;
    if(parts[1].matches("[1-9][0-9]?[0-9]?[0-9]?")){
     number = Integer.parseInt(parts[1]);
    }else{
     number = AutomaticGermanData.alpha2Number.get(parts[1]);
     }
       
    monthNumber = DateCommons.getMonthFromAnchorDate();
   yearNumber = DateCommons.getYearFromAnchorDate();
    dayNumber = DateCommons.getDayFromAnchorDate();
       
    cal.set(yearNumber, monthNumber, dayNumber, 0,0,0);
    Date current = cal.getTime();
       
    if (parts[0].matches("(i)?vor")){
     if (parts[2].matches("Jahren")){
       
      cal.set(yearNumber-number, monthNumber, dayNumber, 0, 0, 0);
      normalizedStartDate = cal.getTime();
      normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, 1, normalizedStartDate);
     }
     if (parts[2].matches("Monaten")){
       
      cal.set(yearNumber, monthNumber-number, dayNumber, 0, 0, 0);
      normalizedStartDate = cal.getTime();
      normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, 1, normalizedStartDate);
     }
     if (parts[2].matches("Tagen")){
       
      cal.set(yearNumber, monthNumber, dayNumber-number, 0, 0, 0);
      normalizedStartDate = cal.getTime();
      normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, 1, normalizedStartDate);
     }
       
    }
    else if (parts[0].matches("seit")){
       
     cal.set(yearNumber, monthNumber, dayNumber, 0, 0, 0);
     normalizedEndDate = cal.getTime();
       
     if (parts[2].matches("Jahren")){
      cal.set(yearNumber-number, monthNumber, dayNumber, 0, 0, 0);
      normalizedStartDate = cal.getTime();
     }
     if (parts[2].matches("Monaten")){
      cal.set(yearNumber, monthNumber-number, dayNumber, 0, 0, 0);
      normalizedStartDate = cal.getTime();
     }
     if (parts[2].matches("Tagen")){
      cal.set(yearNumber, monthNumber, dayNumber-number, 0, 0, 0);
      normalizedStartDate = cal.getTime();
     }
       
    }
    dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
    dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
    DateCommons.updateAnchorDate(normalizedStartDate);
    DateCommons.updateAnchorDate(normalizedEndDate);
                                   }
    if (key == 30){
       int dayNumber = 1;
       int monthNumber = 0;
       int yearNumber = DateCommons.getYearFromAnchorDate();
     String[] parts = foundDate.split("\\s");
            yearNumber = DateCommons.getYearFromAnchorDate();
           monthNumber = DateCommons.getMonthFromAnchorDate();
           dayNumber = DateCommons.getDayFromAnchorDate();
        int hour = 0;
           int minute = 0;
                            cal.set(yearNumber, monthNumber, dayNumber, 0, 0, 0);
            normalizedStartDate = cal.getTime();
            normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, 1, normalizedStartDate);
            dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
            dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
            DateCommons.updateAnchorDate(normalizedStartDate);
                       }
    if (key == 31){
       int dayNumber = 1;
       int monthNumber = 0;
       int yearNumber = DateCommons.getYearFromAnchorDate();
     String[] parts = foundDate.split("\\s");
            yearNumber = DateCommons.getYearFromAnchorDate();
           monthNumber = DateCommons.getMonthFromAnchorDate();
           dayNumber = DateCommons.getDayFromAnchorDate();
        int hour = 0;
           int minute = 0;
                       int numberOfDays = AutomaticGermanData.alpha2Number.get(parts[0]);
       
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

               }
            }
            
              return dates;}
            
                              }           
