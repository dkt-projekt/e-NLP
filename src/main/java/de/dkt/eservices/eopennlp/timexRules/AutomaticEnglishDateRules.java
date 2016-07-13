
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
     public class AutomaticEnglishDateRules {
     static HashMap<Integer, String> englishDateRegexMap = new HashMap<Integer, String>();
     public static RegexNameFinder initEnglishDateFinder(){
       int counter = 0;
       List<Pattern> patterns = new LinkedList<Pattern>();



String digit=" 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 ";
String ceroone=" 0 | 1 ";
String onetwo=" 1 | 2 ";
String daynumber=" (0)? "+digit+" | 1 "+digit+" | 2 "+digit+" | 3 "+ceroone+" ";
String monthname=" januar | february | march | april | may | june | july | august | september | october | november | december ";
String yearnumber=" "+onetwo+" "+digit+" "+digit+" "+digit+" ";

counter++;englishDateRegexMap.put(1, String.format(" "+daynumber+" "+monthname+" ( (,)? "+yearnumber+")?"));
       patterns.add(Pattern.compile(englishDateRegexMap.get(counter)));


 RegexNameFinder rnf = new RegexNameFinder((Pattern[])patterns.toArray(), null);
 return rnf;
 }
 }
 
