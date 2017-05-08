package de.dkt.eservices.eheideltime;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.hp.hpl.jena.rdf.model.Model;

import de.dkt.common.niftools.NIFReader;
import de.dkt.common.niftools.NIFWriter;
import de.unihd.dbs.heideltime.standalone.DocumentType;
import de.unihd.dbs.heideltime.standalone.HeidelTimeStandalone;
import de.unihd.dbs.heideltime.standalone.OutputType;
import de.unihd.dbs.heideltime.standalone.POSTagger;
import de.unihd.dbs.heideltime.standalone.exceptions.DocumentCreationTimeMissingException;
import de.unihd.dbs.uima.annotator.heideltime.resources.Language;
import de.unihd.dbs.uima.types.heideltime.Timex3;
import de.unihd.dbs.uima.types.heideltime.Timex3Interval;
import eu.freme.common.exception.ExternalServiceFailedException;

/**
 * @author Julian Moreno Schneider julian.moreno_schneider@dfki.de, Peter Bourgonje peter.bourgonje@dfki.de
 *
 * The whole documentation about openNLP examples can be found in https://opennlp.apache.org/documentation/1.6.0/manual/opennlp.html
 *
 */

@Component
public class EHeideltimeService {

	Logger logger = Logger.getLogger(EHeideltimeService.class);
	
	private static final String CONFIG_PROPS = "config.props"; 

	private static final String RESOURCE_NAME = "/opt/heideltime-kit/conf/" + CONFIG_PROPS; 
//	private static final String RESOURCE_NAME = "C:\\Users\\pebo01\\Desktop\\heideltime-kit\\conf\\" + CONFIG_PROPS;
	
	private String configPath; 

	private HashMap<String, HeidelTimeStandalone> heidels; 

	private Set<Interval> intervals = new TreeSet<>(); 
	private Set<Date> dates = new TreeSet<>(); 
	private Date normDate; 

	public EHeideltimeService() {
		try{
	//		InputStream in = EHeideltimeService.class.getResourceAsStream(RESOURCE_NAME); 
			InputStream in = new FileInputStream(RESOURCE_NAME);
			String tempDir = System.getProperty("java.io.tmpdir"); 
			configPath = Paths.get(tempDir, CONFIG_PROPS).toString(); 
	
			// File f = new File(configPath); 
			// if (!f.exists()) { 
			OutputStream out = new FileOutputStream(configPath); 
			try { 
				IOUtils.copy(in, out); 
			} finally { 
				in.close(); 
				out.close(); 
			}
			
			heidels = new HashMap<String, HeidelTimeStandalone>();
			heidels.put("en", new HeidelTimeStandalone(Language.ENGLISH, DocumentType.NEWS, 
					OutputType.TIMEML, configPath, POSTagger.TREETAGGER, 
					true));
			heidels.put("de", new HeidelTimeStandalone(Language.GERMAN, DocumentType.NEWS, 
					OutputType.TIMEML, configPath, POSTagger.TREETAGGER, 
					true));
			heidels.put("es", new HeidelTimeStandalone(Language.SPANISH, DocumentType.NEWS, 
					OutputType.TIMEML, configPath, POSTagger.TREETAGGER, 
					true));
			heidels.put("fr", new HeidelTimeStandalone(Language.FRENCH, DocumentType.NEWS, 
					OutputType.TIMEML, configPath, POSTagger.TREETAGGER, 
					true));
		}
		catch(Exception e){
			e.printStackTrace();
		}
	} 

	@PostConstruct
	public void initializeModels(){
	}
	
	public Model annotateTime(Model nifModel, String languageParam) throws DocumentCreationTimeMissingException, ParseException {
		try {
			String document = NIFReader.extractIsString(nifModel);
			Model auxModel = annotateTimeInText(document, languageParam, NIFReader.extractDocumentURI(nifModel)); 
			nifModel.add(auxModel);
			return nifModel; 
    	} catch (ExternalServiceFailedException e2) {
        	logger.error(e2.getMessage());
    		throw e2;
    	}
	}

	public Model annotateTimeInText(String document, String languageParam, String prefix) throws DocumentCreationTimeMissingException, ParseException {
		Calendar cal = Calendar.getInstance(); 
		Date documentCreationTime = cal.getTime(); 
		Model nifOutModel = doProcess(document, documentCreationTime, languageParam, prefix); 
		if (nifOutModel!=null && dates.size() > 2) { 
			nifOutModel = doProcess(document, normDate,languageParam,prefix); 
		} 
		return nifOutModel;
	}

	/**
	 *  
	 * @param document 
	 * @return true if the given document has been successfully processed, i.e. 
	 *         a date interval or a single date has been found. 
	 * @throws DocumentCreationTimeMissingException 
	 * @throws ParseException 
	 */ 
	private Model doProcess(String document, Date documentCreationTime, String languageParam, String prefix) throws DocumentCreationTimeMissingException, ParseException { 
		dates.clear(); 
		normDate = null; 
		intervals.clear(); 

		boolean success = false; 
		Model nifModel = NIFWriter.initializeOutputModel();
//		NIFWriter.addInitialString(nifModel, document, "http://dkt.dfki.de/heideltime/101");
		NIFWriter.addInitialString(nifModel, document, prefix);

		NIFTimeResultFormatter rf = new NIFTimeResultFormatter();
		String result = heidels.get(languageParam).process(document + ".", documentCreationTime, rf); 

		Map<Integer, Timex3Interval> timex3Intervals = rf.getIntervals(); 
		Map<Integer, Timex3> timexes = rf.getTimexes(); 

		if (!timex3Intervals.isEmpty()) { 
			for (Timex3Interval interval : timex3Intervals.values()) { 
				Timex3IntervalAsDateDecorator decorated = new Timex3IntervalAsDateDecorator( 
						interval); 
				intervals.add(new Interval(decorated.getEarliestBegin(), 
						decorated.getLatestEnd()));
				
//				System.out.println("-----");
//				System.out.println(interval.getTimexValueEB());
//				System.out.println(interval.getTimexValueLE());
//				System.out.println(interval.getBegin());
//				System.out.println(interval.getEnd());
//				System.out.println(interval.getBeginTimex());
//				System.out.println(interval.getEndTimex());
//				System.out.println(interval.getTimexValue());
//				System.out.println(interval.getCoveredText());
//				System.out.println("-----");
				NIFWriter.addTemporalEntityFromTimeML(nifModel, 
						interval.getBegin(), 
						interval.getEnd(),
						interval.getCoveredText(), 
						interval.getTimexValueEB()+"_"+interval.getTimexValueLE());
				
			} 
			success = true; 
		} 

//		if (!timexes.isEmpty()) { 
//			for (Iterator<Timex3> iterator = timexes.values().iterator(); iterator 
//					.hasNext();) { 
//				Timex3 timex = iterator.next(); 
//				Timex3AsDateDecorator decorated = new Timex3AsDateDecorator( 
//						timex); 
////				dates.add(decorated.getValueAsDate()); 
////				if (!iterator.hasNext()) { 
////					normDate = decorated.getValueAsDate(); 
////				} 
//
//				normDate = timex.getTimexValue();
//				//TODO Include all the timexes information into NIF.
//
//			} 
//			success = true; 
//		} 
		if(success){
//			System.out.println(NIFReader.model2String(nifModel, RDFSerialization.TURTLE));
			return nifModel;
		}
		return null; 
	} 

//	public Set<Interval> getIntervals() { 
//		return this.intervals; 
//	} 
//
//	public Set<Date> getDates() { 
//		return this.dates; 
//	} 
//
//	public <T> T getLastElement(final Collection<T> c) { 
//		final Iterator<T> itr = c.iterator(); 
//		T lastElement = itr.next(); 
//		while (itr.hasNext()) { 
//			lastElement = itr.next(); 
//		} 
//		return lastElement; 
//	} 

//	/**
//	 * Checks if two date objects are on the same day ignoring time. 
//	 *  
//	 * @param date1 
//	 * @param date2 
//	 * @return true if they represent the same day 
//	 * @throws java.lang.IllegalArgumentException 
//	 *             if either date is null 
//	 */ 
//	private boolean isSameDay(Date date1, Date date2) { 
//		if (date1 == null || date2 == null) { 
//			throw new IllegalArgumentException("The date must not be null"); 
//		} 
//		Calendar cal1 = Calendar.getInstance(); 
//		cal1.setTime(date1); 
//		Calendar cal2 = Calendar.getInstance(); 
//		cal2.setTime(date2); 
//		return isSameDay(cal1, cal2); 
//	} 

//	/**
//	 * Checks if two calendar objects are on the same day ignoring time. 
//	 *  
//	 * @param cal1 
//	 * @param cal2 
//	 * @return true if they represent the same day 
//	 * @throws java.lang.IllegalArgumentException 
//	 *             if either calendar is null 
//	 * @see org.apache.commons.lang.time.DateUtils 
//	 */ 
//	private boolean isSameDay(Calendar cal1, Calendar cal2) { 
//		if (cal1 == null || cal2 == null) { 
//			throw new IllegalArgumentException("The date must not be null"); 
//		} 
//		return (/*
//		 * cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) && 
//		 */cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1 
//		 .get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)); 
//	} 

	/**
	 * A time interval represents a period of time between two instants. 
	 */ 
	public class Interval implements Comparable<Interval> { 
		private Date startTime; 
		private Date stopTime; 

		public Interval() { 
		} 

		public Interval(Date startTime, Date stopTime) { 
			setStartTime(startTime); 
			setStopTime(stopTime); 
		} 

		public Date getStartTime() { 
			return startTime; 
		} 

		public Date getStopTime() { 
			return stopTime; 
		} 

		public void setStartTime(Date startTime) { 
			this.startTime = startTime; 
		} 

		public void setStopTime(Date stopTime) { 
			this.stopTime = stopTime; 
		} 

		@Override 
		public int compareTo(Interval o) { 
			long thisTime = startTime.getTime() + stopTime.getTime(); 
			long anotherTime = o.startTime.getTime() + o.stopTime.getTime(); 
			return (thisTime < anotherTime ? -1 : (thisTime == anotherTime ? 0 
					: 1)); 
		} 

		@Override 
		public int hashCode() { 
			final int prime = 31; 
			int result = 1; 
			result = prime * result 
					+ ((startTime == null) ? 0 : startTime.hashCode()); 
			result = prime * result 
					+ ((stopTime == null) ? 0 : stopTime.hashCode()); 
			return result; 
		} 

		@Override 
		public boolean equals(Object obj) { 
			if (this == obj) 
				return true; 
			if (obj == null) 
				return false; 
			if (getClass() != obj.getClass()) 
				return false; 
			Interval other = (Interval) obj; 
			if (startTime == null) { 
				if (other.startTime != null) 
					return false; 
			} else if (!startTime.equals(other.startTime)) 
				return false; 
			if (stopTime == null) { 
				if (other.stopTime != null) 
					return false; 
			} else if (!stopTime.equals(other.stopTime)) 
				return false; 
			return true; 
		} 

	} 

	public static void main(String[] args) throws DocumentCreationTimeMissingException, ParseException, IOException, Exception { 
		String[] documents = new String[] { 
				"Du 21 au 30 janvier 2015", 
				// "Tous les samedis et vendredis de 20h à 22h00.", Not yet 
				// managed 
				"mer 28 jan 15 à 20:00.", 
				"lun 27 avr 15, 20:00", 
				"sam 3 jan", 
				"Du 11 novembre 2014 au 15 janvier 2015", 
				"Le 16 janvier 2015", 
				"mercredi 1er samedi 4 et dimanche 5 avril à 10h30 et 16h45", 
				"Le mercredi 4 et le samedi 7 et le dimanche 8 mars" /*
				 * à 10h30 
				 * et 
				 * 16h45" 
				 */, 
				 "mercredi 4 et samedi 7 et dimanche 8 mars à 10h30 et 16h45", 
				 "Le mercredi 4 et le dimanche 8 mars", 
				 "Le mercredi 4", 
				 "21.03", 
				 "le lundi, mardi et jeudi", 
				 "mercredi 1er avril à 10h30 et 16h45", 
				 "lundi 20, mardi 21 mercredi 22 jeudi 23 vendredi 24 samedi 25 et dimanche 26 avril à 10h30 et 16h45", 
				 "9-10-11 AVRIL 15", "27.03.15", "DU 05/05/2015 AU 20/12/2015", 
				 "le 8/11/2015 à 19:30", "20 nov. à 20h00", 

				 // "Tous les jours du lundi 13 au dimanche 19 avril", 
		}; 

		Map<String, EHeideltimeService> results = new HashMap<>(); 

		for (String document : documents) { 
			EHeideltimeService dateTimeProcessor = new EHeideltimeService(); 
			dateTimeProcessor.annotateTimeInText(document.toLowerCase(), "fr", null); // TODO: thinking 
			// about the case 
			results.put(document, dateTimeProcessor); 
		} 

//		// Display results 
//		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.FULL, 
//				DateFormat.SHORT, Locale.FRENCH); 
//		Set<Entry<String, EHeideltimeService>> entries = results.entrySet(); 
//
//		for (Entry<String, EHeideltimeService> entry : entries) { 
//			System.out.println("--------------------------------------"); 
//			System.out.println("Source = \"" + entry.getKey() + "\""); 
//			EHeideltimeService dtp = entry.getValue(); 
//
//			Set<Interval> intervals = dtp.getIntervals(); 
//			if (!intervals.isEmpty()) { 
//				System.out.println("-- Intervals found --"); 
//				for (Interval interval : intervals) { 
//					System.out.println("Du " 
//							+ df.format(interval.getStartTime()) + " au " 
//							+ df.format(interval.getStopTime())); 
//				} 
//			} 
//
//			Set<Date> dates = dtp.getDates(); 
//			if (!dates.isEmpty()) { 
//				System.out.println("-- Dates found --"); 
//				for (Date date : dates) { 
//					System.out.println(df.format(date)); 
//				} 
//			} 
//
//		} 
	} 
}
