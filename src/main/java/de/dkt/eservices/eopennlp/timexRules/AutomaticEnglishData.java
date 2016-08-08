 package de.dkt.eservices.eopennlp.timexRules;
        import java.util.HashMap;
       
     public class AutomaticEnglishData {
            

  static HashMap<String, String> season2Dates = new HashMap<String, String>(){{
  put("spring","0331");put("winter","1228");put("summer","0631");put("fall","0930");  }};
  
  static HashMap<String, Integer> monthName2Number = new HashMap<String, Integer>(){{
  put("january",1);put("february",2);put("march",3);put("april",4);put("may",5);put("june",6);put("july",7);put("august",8);put("september",9);put("october",10);put("november",11);put("december",12);  }};
  
  static HashMap<String, Integer> dayName2Integer = new HashMap<String, Integer>(){{
  put("sunday",1);put("monday",2);put("tuesday",3);put("thursday",4);put("wednesday",5);put("friday",6);put("saturday",7);  }};
  
  static HashMap<String, Integer> early2Month = new HashMap<String, Integer>(){{
  put("early",1);put("mid",6);put("late",10);  }};
  
  static HashMap<String, Integer> early2Day = new HashMap<String, Integer>(){{
  put("early",1);put("mid",10);put("late",20);  }};
  
  static HashMap<String, Integer> start2Month = new HashMap<String, Integer>(){{
  put("start",1);put("beginning",1);put("middle",5);put("end",10);  }};
  
  static HashMap<String, Integer> start2Day = new HashMap<String, Integer>(){{
  put("start",1);put("beginning",1);put("middle",10);put("end",20);  }};
  
  static HashMap<String, String> fixedHoliday2Dates = new HashMap<String, String>(){{
  put("christmas","1225"); put("thanksgiving","0704"); put("boxing","2612"); put("veteran'?s","1111");put("new","0101");put("may","0501");  }};
  
  static HashMap<String, Integer> alpha2Number = new HashMap<String, Integer>(){{
  put("one",1);put("two",2);put("three",3);put("four",4);put("five",5);put("six",6);put("seven",7);put("eight",8);put("nine",9);put("ten",10);put("eleven",11);put("twelve",12);put("thirteen",13);put("fourteen",14);put("fifteen",15);put("sixteen",16);put("seventeen",17);put("eighteen",18);put("nineteen",19);put("twenty",20);put("twenty-one",21);put("twenty-two",22);put("twenty-three",23);put("twenty-four",24);put("twenty-five",25);put("twenty-six",26);put("twenty-seven",27);put("twenty-eight",28);put("twenty-nine",29);put("thirty",30);put("thirty-one",31);put("thirty-two",32);put("thirty-three",33);put("thirty-four",34);put("thirty-five",35);put("thirty-six",36);put("thirty-seven",37);put("thirty-eight",38);put("thirty-nine",39);put("forty",40);put("forty-one",41);put("forty-two",42);put("forty-three",43);put("forty-four",44);put("forty-five",45);put("forty-six",46);put("forty-seven",47);put("forty-eight",48);put("forty-nine",49);put("fifty",50);put("fifty-one",51);put("fifty-two",52);put("fifty-three",53);put("fifty-four",54);put("fifty-five",55);put("fifty-six",56);put("fifty-seven",57);put("fifty-eight",58);put("fifty-nine",59);put("sixty",60);put("sixty-one",61);put("sixty-two",62);put("sixty-three",63);put("sixty-four",64);put("sixty-five",65);put("sixty-six",66);put("sixty-seven",67);put("sixty-eight",68);put("sixty-nine",69);put("seventy",70);put("seventy-one",71);put("seventy-two",72);put("seventy-three",73);put("seventy-four",74);put("seventy-five",75);put("seventy-six",76);put("seventy-seven",77);put("seventy-eight",78);put("seventy-nine",79);put("eighty",80);put("eighty-one",81);put("eighty-two",82);put("eighty-three",83);put("eighty-four",84);put("eighty-five",85);put("eighty-six",86);put("eighty-seven",87);put("eighty-eight",88);put("eighty-nine",89);put("ninety",90);put("ninety-one",91);put("ninety-two",92);put("ninety-three",93);put("ninety-four",94);put("ninety-five",95);put("ninety-six",96);put("ninety-seven",97);put("ninety-eight",98);put("ninety-nine",99);  }};
  

                    }
        
