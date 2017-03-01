package de.dkt.eservices.eopennlp.timexRules;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.dkt.common.filemanagement.FileFactory;

public class DateCommons {
	
	static Calendar cal = Calendar.getInstance();
	static SimpleDateFormat fullDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
	//static String anchorDate = fullDateFormat.format(cal.getTime()); // This way it should be the current date, and be overwritten with file timestamp is available. Note that at the moment this is completely linear; whenever a date is found, the anchorDate is updated. 
	static Date anchorDate = cal.getTime();

	public static final Date getEasterDate(int year) {
		   
		  Date easter = new Date();
			
		  if (year <= 1582) {
			  cal.set(year, 4, 15);
			  easter =  cal.getTime();//);Integer.toString(year) + "0415000000"; // it's not correct before 1583. But if it's that long ago, I do not care about being a few days off. Just want something that always returns a normalized date.
		  }
		  int golden, century, x, z, d, epact, n;
		  
		  golden = (year % 19) + 1; /* E1: metonic cycle */
		  century = (year / 100) + 1; /* E2: e.g. 1984 was in 20th C */
		  x = (3 * century / 4) - 12; /* E3: leap year correction */
		  z = ((8 * century + 5) / 25) - 5; /* E3: sync with moon's orbit */
		  d = (5 * year / 4) - x - 10;
		  epact = (11 * golden + 20 + z - x) % 30; /* E5: epact */
		  if ((epact == 25 && golden > 11) || epact == 24) epact++;
		  n = 44 - epact;
		  n += 30 * (n < 21 ? 1 : 0); /* E6: */
		  n += 7 - ((d + n) % 7);
		  
		  
		  if (n > 31) {
			easter = new GregorianCalendar(year, 4-1, n-31).getTime();
		  }else{
			  easter = new GregorianCalendar(year, 3-1, n).getTime();
		  }
		  return easter;
		}
			  

		public static final Date getAscensionDate(int year){
			
			Date ascension = new Date();
			Date easter = getEasterDate(year);
			cal.setTime(easter);
			cal.add(Calendar.DATE, 39);
			ascension = cal.getTime();
			
			return ascension;
				
		}
		
		public static final Date getPentecostDate(int year){
			
			Date pentecost = new Date();
			Date easter = getEasterDate(year);
			cal.setTime(easter);
			cal.add(Calendar.DATE, 49);
			pentecost = cal.getTime();
			
			return pentecost;
			
		}
		

		public static void setInitialAnchorDate(){
			
			try {
		        Date date = new Date();
		        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
		        String dateCreated = df.format(date);
		        anchorDate = df.parse(dateCreated);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	

		

		public static void setInitialAnchorDate(Path filePath){

			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
			
			try {
				BasicFileAttributes attr = Files.readAttributes(filePath, BasicFileAttributes.class);
		        FileTime date = attr.creationTime();
		        String dateCreated = sdf.format(date.toMillis());
		        anchorDate = sdf.parse(dateCreated);
		        
		        
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		

		public static void updateAnchorDate(Date newAnchorDate){
			
			anchorDate = newAnchorDate;
			
		}
		
		public static int getYearFromAnchorDate(){
			cal.setTime(anchorDate);
			return cal.get(Calendar.YEAR);
		}
		public static int getMonthFromAnchorDate(){
			cal.setTime(anchorDate);
			return cal.get(Calendar.MONTH);
		}
		public static int getDayFromAnchorDate(){
			cal.setTime(anchorDate);
			return cal.get(Calendar.DATE);
		}

		
		public static int getMonthNumber(String str, HashMap<String, Integer> monthHashMap) {
			
			Iterator it = monthHashMap.entrySet().iterator();
			int i = 0;
			while (it.hasNext()){
				Map.Entry pair = (Map.Entry)it.next();
				String name = (String) pair.getKey();
				int number = (Integer) pair.getValue();
				if (str.contains(name)){
					i = number;
				}
			}
			
			return i;
		}
		
		
		public static int getLastDayOfMonth(int month, int year){
			
			int lastDay = 0;
			if (month  == 1){
				lastDay = 31;
			}
			else if (month == 2){
				if (year % 4 == 0){
					lastDay = 29;
				}
				else{
					lastDay = 28;
				}
			}
			else if (month == 3){
				lastDay = 31;
			}
			else if (month == 4){
				lastDay = 30;
			}
			else if (month == 5){
				lastDay = 31;
			}
			else if (month == 6){
				lastDay = 30;
			}
			else if (month == 7){
				lastDay = 31;
			}
			else if (month == 8){
				lastDay = 31;
			}
			else if (month == 9){
				lastDay = 30;
			}
			else if (month == 10){
				lastDay = 31;
			}
			else if (month == 11){
				lastDay = 30;
			}
			else if (month == 12){
				lastDay = 31;
			}
			
			return lastDay;
		}
		
		
		public static Date normParseDate(String formatted){
		
			Date d = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
			try {
				d = sdf.parse(formatted);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return d;
		}
		
		public static Date increaseCalendar(int unit, int amount, Date startDate){
			cal.setTime(startDate);
			cal.add(unit, amount);
			return cal.getTime();
		}
		
		public static int getFirstSpecificDayOfMonth(int year, int month, String day) {
			LocalDate date = LocalDate.of(year,month, 1);
			int dayNumber = 0;
		    for(int i=1;i<date.lengthOfMonth();i++){
		        if(day.equalsIgnoreCase(date.getDayOfWeek().toString())){
		        	dayNumber = i;
		            break;
		        }else{
		            date = date.plusDays(1);
		        }
		    }
		    return dayNumber;
		}
	
		public static int getLastSpecificDayOfMonth(int year, int month, String day){
			int lastDayOfMonth = getLastDayOfMonth(month, year);
			LocalDate date = LocalDate.of(year, month, lastDayOfMonth);
			int dayNumber = 0;
			for (int i = lastDayOfMonth;i>0;i--){
				if (day.equalsIgnoreCase(date.getDayOfWeek().toString())){
					dayNumber = i;
					break;
				}
				else{
					date = date.minusDays(1);
				}
			}
			return dayNumber;
		}


		public static List<Date> convertStringsToDates(List<String> dateList) {

			List<Date> dl = new ArrayList<Date>();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			for (String s : dateList){
				Date d = new Date();
				try{
					d = sdf.parse(s);
				} catch (ParseException e){
					e.printStackTrace();
				}
				dl.add(d);
			}
			return dl;
		}
}
