package de.dkt.eservices.erattlesnakenlp.modules;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.xml.namespace.NamespaceContext;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.query.ParameterizedSparqlString;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import de.dkt.common.niftools.DBO;
import de.dkt.common.niftools.GEO;
import de.dkt.common.niftools.ITSRDF;
import de.dkt.common.niftools.NIF;
import de.dkt.common.niftools.NIFReader;
import de.dkt.common.niftools.NIFWriter;
import de.dkt.eservices.eopennlp.modules.NameFinder;

public class Sparqler {
	/*
	 * Guess I could rename this class now, since it not only does the sparql stuff, but also handles some more NIF things (e.g. putting in/calculating means etc.)
	 */

	static Logger logger = Logger.getLogger(Sparqler.class);
	//public static List<String> georssPoints = new ArrayList<String>();
	private List<Double> longitudes = new ArrayList<Double>();
	private List<Double> latitudes = new ArrayList<Double>();
	
	public static String getDBPediaLabelForLanguage(String URI, String targetLang){
		
		String targetLabel = null;
		
		ParameterizedSparqlString sQuery = new ParameterizedSparqlString(
				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n" +
				"SELECT ?label\n" +
				"WHERE {\n" +
				"?uri rdfs:label ?label\n" +
				"}"
		);
		sQuery.setIri("uri", URI);
		String service = "http://dbpedia.org/sparql";
		//System.out.println("dEBUGGINg\n" + sQuery);
		ResultSet res = null;
		QueryExecution exec = QueryExecutionFactory.sparqlService(service, sQuery.asQuery());
		exec.setTimeout(3000, TimeUnit.MILLISECONDS);
		
		try{
			res = exec.execSelect();
			while (res.hasNext()) {
				QuerySolution qs = res.next();
				RDFNode uri = qs.get("label");
				String labelLanguage = uri.toString().substring(uri.toString().lastIndexOf("@")+1);
				if (labelLanguage.equalsIgnoreCase(targetLang)){
					targetLabel = uri.toString().substring(0, uri.toString().lastIndexOf("@"));
				}
				
			}
			
		}catch(Exception e){
			logger.info("Unable to retrieve label for URI: " + URI + " in language: " + targetLang + ". Skipping.");
	}
		
		return targetLabel;
	}
	
	public static String getDBPediaURI(String label, String language, String service, String defaultGraph){
		
		String URI = null;
		// may want to keep some database/structure somewhere to store queries like these, instead of having them hardcoded
		// NOTE that this method just returns all URIs found (given base URI). May want to do some kind of disambiguation at some point.
		ParameterizedSparqlString sQuery = new ParameterizedSparqlString(
				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n" +
				"SELECT ?uri WHERE {\n" +
					"?uri rdfs:label ?label@" + language + ".\n" + // this is stupid, but couldn't figure out how to properly add this (e.g. setLiteral surrounds it with quotes, setIri with hooks...
					//"FILTER NOT EXISTS { ?uri rdf:type rdf:Property } \n" +
				"}"
				);
		sQuery.setLiteral("label", label);
		//System.out.println("DEBUGGING QUERY:" + sQuery.toString());
		ResultSet res = null;
		QueryExecution exec = QueryExecutionFactory.sparqlService(service, sQuery.asQuery(), defaultGraph);
		exec.setTimeout(3000, TimeUnit.MILLISECONDS);
		try{
			res = exec.execSelect();
			ArrayList<String> resList = new ArrayList<String>();
			while (res.hasNext()) {
				QuerySolution qs = res.next();
				RDFNode uri = qs.get("uri");
				resList.add(uri.toString());
			}
			// this is a really very naive way of disambiguating uri based on
			// the idea that the shortest URL is the most popular/general. When
			// we get to the point where we want to disambiguate intelligently,
			// this is (currently) the place to do it...
			if (resList.size() > 0) {
				URI = resList.get(0);
				for (String uri : resList) {
					//System.out.println("URI loop:" + uri);
					if (uri.length() < URI.length()) {
						URI = uri;
					}
				}
			}

		}catch(Exception e){
			logger.info("Unable to retrieve DBPedia URI for: " + label + ". Skipping.");
		}

		return URI;
	}

	
	public void queryDBPedia(Model nifModel, String docURI, int nifStartIndex, int nifEndIndex, String infoURL, Property nifProp, String sparqlService){

		ParameterizedSparqlString sQuery = new ParameterizedSparqlString( "" +
				"select ?uri ?info where {\n" +
				" ?uri ?infoURL ?info.\n" +
				" FILTER (?uri = ?dbpediaURI)" +
				"}" );

		String entityUri = new StringBuilder().append(docURI).append("#char=").append(nifStartIndex).append(',').append(nifEndIndex).toString();
		Resource r = nifModel.getResource(entityUri);
		Statement st2 = r.getProperty(ITSRDF.taIdentRef);
		if (st2.getObject() != null){
			String dbPediaURI = st2.getObject().toString();
			sQuery.setIri("dbpediaURI", dbPediaURI);
			sQuery.setIri("infoURL", infoURL);
			QueryExecution exec = QueryExecutionFactory.sparqlService(sparqlService, sQuery.asQuery());
			//System.out.println("DEBUGGING final sparql query:" + sQuery.toString());
			exec.setTimeout(3000, TimeUnit.MILLISECONDS);
			try{
				ResultSet res = exec.execSelect();
				while (res.hasNext()){
					QuerySolution qs = res.next();
					RDFNode n = qs.get("info");
					// this is where some content-specific stuff comes in, because of format differences. And because of possible average lists (georssPoints for ex.)
					String infoToAdd = null;
					XSDDatatype dataType = null;
					if (nifProp == GEO.latitude){
						String[] p = n.toString().split("\\^\\^");
						infoToAdd = p[0].split("\\s")[0];
						//infoToAdd = p[0];
						//dataType = XSDDatatype.XSDstring;
						dataType = XSDDatatype.XSDdouble;
						//georssPoints.add(infoToAdd);
						latitudes.add(Double.parseDouble(infoToAdd));
					}
					else if (nifProp == GEO.longitude){
						String[] p = n.toString().split("\\^\\^");
						//infoToAdd = p[0];
						//infoToAdd = p[1].replaceAll("\\s", "_");
						infoToAdd = p[0].split("\\s")[1];
						dataType = XSDDatatype.XSDdouble;
						longitudes.add(Double.parseDouble(infoToAdd));
					}
					else if (nifProp == DBO.birthDate || nifProp == DBO.deathDate){
						String[] p = n.toString().split("\\^\\^");
						infoToAdd = p[0]; // TODO: there is a bug here resulting in an invalid date value (e.g. containing hyphen where it should not). Difficult to debug though, perhaps temporarily increase timeout setting to test?)
						dataType = XSDDatatype.XSDdate;
					}
					else if(nifProp == NIF.orgType){
						String[] p = n.toString().split("\\^\\^");
						infoToAdd = p[0];
						dataType = XSDDatatype.XSDstring;
					}
					if (infoToAdd != null){
						// this feels really ugly, must be a better way of getting indices
						int beginIndex = Integer.parseInt(r.getProperty(NIF.beginIndex).getObject().toString().split("\\^\\^")[0]);
						int endIndex = Integer.parseInt(r.getProperty(NIF.endIndex).getObject().toString().split("\\^\\^")[0]);
						docURI = NIFReader.extractDocumentURI(nifModel);
						NIFWriter.addPrefixToModel(nifModel, "dbo", DBO.uri);
						NIFWriter.addEntityProperty(nifModel, beginIndex, endIndex, docURI, infoToAdd, nifProp, dataType);
					}
					break; // just taking the first result here, not caring about any others in case of multiple results

				}
			}catch(Exception e){
				logger.info("Unable to retrieve " + infoURL + " for: " + dbPediaURI+ ". Skipping.");
			}
		}
	}


	public void addGeoStats(Model nifModel, String textToProcess, String string) {

		Double avgLatitude = calculateAverage(latitudes);
		Double stdDevLatitude = calculateStandardDeviation(latitudes, avgLatitude);
		Double avgLongitude = calculateAverage(longitudes);
		Double stdDevLongitude = calculateStandardDeviation(longitudes, avgLongitude);
		latitudes.clear();
		longitudes.clear();
		String docURI = NIFReader.extractDocumentURI(nifModel);
		NIFWriter.addGeoStats(nifModel, textToProcess, avgLatitude,  avgLongitude,  stdDevLatitude,  stdDevLongitude, docURI);

	}

	public boolean hasCoordinates() {
		return latitudes.size() > 0 || longitudes.size() > 0;
	}

	private static Double calculateAverage(List <Double> l) {
		Double sum = 0.0;
		if(!l.isEmpty()) {
			for (Double d : l) {
				sum += d;
			}
			return sum / l.size();
		}
		return sum;
	}

	private static Double calculateStandardDeviation(List<Double> l, Double avg){
		Double totalSquareDiff = 0.0;
		for (Double d : l){
			Double diff = d - avg;
			totalSquareDiff += Math.pow(diff, 2);
		}
		Double msd = totalSquareDiff / l.size();
		Double stdDev = Math.sqrt(msd);
		return stdDev;
	}

	public static void main(String[] args) throws Exception {

		String uri = getDBPediaURI("Hollande", "en", "http://dbpedia.org/sparql", "http://dbpedia.org");
		System.out.println("uri:" + uri);
		//uri = getDBPediaURI("Berlin", "de", "http://de.dbpedia.org/sparql", "http://de.dbpedia.org");
		//System.out.println("uri:" + uri);
		//String targetLabel = getDBPediaLabelForLanguage("http://dbpedia.org/resource/Antwerp", "ar");
		//System.out.println(targetLabel);
		
	}

}
