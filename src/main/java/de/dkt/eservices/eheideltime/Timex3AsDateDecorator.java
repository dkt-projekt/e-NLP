package de.dkt.eservices.eheideltime;

import java.text.ParseException; 
import java.text.SimpleDateFormat; 
import java.util.Date; 

import de.unihd.dbs.uima.types.heideltime.Timex3; 

public class Timex3AsDateDecorator { 

	private Timex3 timex3; 

	private static final String TIME_PATTERN = "yyyy-MM-dd'T'HH:mm"; 

	private static final String DATE_PATTERN = "yyyy-MM-dd"; 

	private static final String DATE_TYPE = "DATE"; 

	private static final String TIME_TYPE = "TIME"; 

	public Timex3AsDateDecorator(Timex3 timex3) { 
		this.timex3 = timex3; 
	} 

	public Date getValueAsDate() throws ParseException { 

		String source = timex3.getTimexValue(); 
		SimpleDateFormat df = new SimpleDateFormat(); 

		if (DATE_TYPE.equals(timex3.getTimexType())) { 
			df.applyPattern(DATE_PATTERN); 
			return df.parse(source); 
		} else if (TIME_TYPE.equals(timex3.getTimexType())) { 
			df.applyPattern(TIME_PATTERN); 
			return df.parse(source); 
		} else { 
			throw new ParseException( 
					"Only DATE and TIME TIMEX3 types are supported", -1); 
		} 
	} 

	@Override 
	public String toString() { 
		return "Timex3AsDateDecorator [value=" + timex3.getTimexValue() + "]"; 
	} 

}