 package de.dkt.eservices.eopennlp.timexRules;
        import java.util.HashMap;
       
     public class AutomaticGermanData {
            

  static HashMap<String, String> season2Dates = new HashMap<String, String>(){{
  put("frühling","0331");put("winter","1228");put("sommer","0631");put("herbst","0930");  }};
  
  static HashMap<String, Integer> monthName2Number = new HashMap<String, Integer>(){{
  put("januar",0);put("februar",1);put("märz",2);put("april",3);put("mai",4);put("juni",5);put("juli",6);put("august",7);put("september",8);put("oktober",9);put("november",10);put("dezember",11);  }};
  
  static HashMap<String, Integer> dayName2Integer = new HashMap<String, Integer>(){{
  put("sonntag",1);put("montag",2);put("dienstag",3);put("mittwoch",4);put("donnerstag",5);put("freitag",6);put("samstag",7);  }};
  
  static HashMap<String, Integer> early2Month = new HashMap<String, Integer>(){{
  put("anfang",1);put("mitte",6);put("ende",10);  }};
  
  static HashMap<String, Integer> early2Day = new HashMap<String, Integer>(){{
  put("anfang",1);put("mitte",10);put("ende",20);  }};
  
  static HashMap<String, Integer> start2Month = new HashMap<String, Integer>(){{
  put("anfang",1);put("mitte",5);put("ende",10);  }};
  
  static HashMap<String, Integer> start2Day = new HashMap<String, Integer>(){{
  put("anfang",1);put("mitte",10);put("ende",20);  }};
  
  static HashMap<String, String> fixedHoliday2Dates = new HashMap<String, String>(){{
  put("weihnachten","1225");put("erster weihnachtstag","2612");put("neujahr","0101");put("silvester","1231");put("maifeiertag","0501");put("tag der arbeit","0105");put("tag der deutschen einheit","0310");  }};
  
  static HashMap<String, Integer> alpha2Number = new HashMap<String, Integer>(){{
  put("null",0);put("eins",1);put("ein",1);put("eine",1);put("einer",1);put("zwei",2);put("drei",3);put("vier",4);put("fünf",5);put("sechs",6);put("sieben",7);put("acht",8);put("neun",9);put("zehn",10);put("elf",11);put("zwölf",12);put("dreizehn",13);put("vierzehn",14);put("fünfzehn",15);put("sechzehn",16);put("siebzehn",17);put("achtzehn",18);put("neunzehn",19);put("zwanzig",20);put("einundzwanzig",21);put("zweiundzwanzig",22);put("dreiundzwanzig",23);put("vierundzwanzig",24);put("fünfundzwanzig",25);put("sechsundzwanzig",26);put("siebenundzwanzig",27);put("achtundzwanzig",28);put("neunundzwanzig",29);put("dreiβig",30);put("einunddreiβig",31);put("zweiundreißig",32);put("dreiunddreißig",33);put("vierunddreißig",34);put("fünfunddreißig",35);put("sechsunddreißig",36);put("siebenunddreißig",37);put("achtunddreißig",38);put("neununddreißig",39);put("vierzig",40);put("einundvierzig",41);put("zweiundvierzig",42);put("dreiundvierzig",43);put("vierundvierzig",44);put("fünfundvierzig",45);put("sechsundvierzig",46);put("siebenundvierzig",47);put("achtundvierzig",48);put("neunundvierzig",49);put("fünfzig",50);put("einundfünfzig",51);put("zweiundfünfzig",52);put("dreiundfünfzig",53);put("vierundfünfzig",54);put("fünfundfünfzig",55);put("sechsundfünfzig",56);put("siebenundfünfzig",57);put("achtundfünfzig",58);put("neunundfünfzig",59);put("sechzig",60);put("einundsechzig",61);put("zweiundsechzig",62);put("dreiundsechzig",63);put("vierundsechzig",64);put("fünfundsechzig",65);put("sechsundsechzig",66);put("siebenundsechzig",67);put("achtundsechzig",68);put("neunundsechzig",69);put("siebzig",70);put("einundsiebzig",71);put("zweiundsiebzig",72);put("dreiundsiebzig",73);put("vierundsiebzig",74);put("fünfundsiebzig",75);put("sechsundsiebzig",76);put("siebenundsiebzig",77);put("achtundsiebzig",78);put("neunundsiebzig",79);put("achtzig",80);put("einundachtzig",81);put("zweiundachtzig",82);put("dreiundachtzig",83);put("vierundachtzig",84);put("fünfundachtzig",85);put("sechsundachtzig",86);put("siebenundachtzig",87);put("achtundachtzig",88);put("neunundachtzig",89);put("neunzig",90);put("einundneunzig",91);put("zweiundneunzig",92);put("dreiundneunzig",93);put("vierundneunzig",94);put("fünfundneunzig",95);put("sechsundneunzig",96);put("siebenundneunzig",97);put("achtundneunzig",98);put("neunundneunzig",99);put("hundert",100);  }};
  
  static HashMap<String, Integer> dayPart2Hour = new HashMap<String, Integer>(){{
  put("morgen",6);put("mittag",12);put("abend",18);put("nacht",23);  }};
  
  static HashMap<String, String> dayWeekMonthYear2Variable = new HashMap<String, String>(){{
  put("day","dayNumber");put("week","dayNumber");put("month","monthNumber");put("year","yearNumber");  }};
  
  static HashMap<String, Integer> romanMonth2Number = new HashMap<String, Integer>(){{
  put("i", 1);put("ii", 2);put("iii", 3);put("iv", 4);put("v", 5);put("vi", 6);put("vii", 7);put("viii", 8);put("ix", 9);put("x", 10);put("xi", 11);put("xii", 12);  }};
  
  static HashMap<String, Integer> thisNext2Number = new HashMap<String, Integer>(){{
  put("nächste",1);put("nächster",1);put("nächstes",1);put("nächsten",1);put("dieser",0);put("dieses",0);put("diese",0);put("diesen",0);put("diesem",0);  }};
  
  static HashMap<Integer, String> beforeAfter = new HashMap<Integer, String>(){{
  put(1,"später");put(2,"früher|zuvor|eher");  }};
  

                    }
        
