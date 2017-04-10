package de.dkt.eservices.eheideltime;

import java.text.DateFormat; 
import java.text.ParseException; 
import java.text.SimpleDateFormat; 
import java.util.Date; 

import de.unihd.dbs.uima.types.heideltime.Timex3Interval; 

public class Timex3IntervalAsDateDecorator { 

	private Timex3Interval interval; 

	private static final String PATTERN = "yyyy-MM-dd'T'HH:mm:ss"; 

	private DateFormat df = new SimpleDateFormat(PATTERN); 

	public Timex3IntervalAsDateDecorator(Timex3Interval interval) { 
		this.interval = interval; 
	} 

	// earliestBegin 
	public Date getEarliestBegin() throws ParseException { 
		String source = interval.getTimexValueEB(); 
		return df.parse(source); 
	} 

	// latestBegin 
	public Date getLatestBegin() throws ParseException { 
		String source = interval.getTimexValueLB(); 
		return df.parse(source); 
	} 

	// earliestEnd 
	public Date getEarliestEnd() throws ParseException { 
		String source = interval.getTimexValueEE(); 
		return df.parse(source); 
	} 

	// latestEnd 
	public Date getLatestEnd() throws ParseException { 
		String source = interval.getTimexValueLE(); 
		return df.parse(source); 
	} 

	@Override 
	public String toString() { 
		return "Timex3IntervalAsDateDecorator [earliestBegin=" 
				+ interval.getTimexValueEB() + ", latestBegin=" 
				+ interval.getTimexValueLB() + ", earliestEnd=" 
				+ interval.getTimexValueEE() + ", latestEnd=" 
				+ interval.getTimexValueLE() + "]"; 
	} 

}