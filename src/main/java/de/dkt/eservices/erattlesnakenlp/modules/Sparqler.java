package de.dkt.eservices.erattlesnakenlp.modules;

import java.util.ArrayList;
import java.util.List;

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

public class Sparqler {
	/*
	 * Guess I could rename this class now, since it not only does the sparql stuff, but also handles some more NIF things (e.g. putting in/calculating means etc.)
	 */

	//public static List<String> georssPoints = new ArrayList<String>();
	public static List<Double> longitudes = new ArrayList<Double>();
	public static List<Double> latitudes = new ArrayList<Double>();
	
	public static String getDBPediaURI(String label, String language, String service, String defaultGraph){
		
		String URI = null;
		// may want to keep some database/structure somewhere to store queries like these, instead of having them hardcoded
		// NOTE that this method just returns all URIs found (given base URI). May want to do some kind of disambiguation at some point.
		ParameterizedSparqlString sQuery = new ParameterizedSparqlString(
				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n" +
				"SELECT ?uri WHERE {\n" +
					"?uri rdfs:label ?label@" + language + ".\n" + // this is stupid, but couldn't figure out how to properly add this (e.g. setLiteral surrounds it with quotes, setIri with hooks...
				"}"
				);
		sQuery.setLiteral("label", label);
		
		QueryExecution exec = QueryExecutionFactory.sparqlService(service, sQuery.asQuery(), defaultGraph);
		ResultSet res = exec.execSelect();
		
		ArrayList<String> resList = new ArrayList<String>();
		while (res.hasNext()){
			QuerySolution qs = res.next();
			RDFNode uri = qs.get("uri");
			resList.add(uri.toString());
		}
		// this is a really very naive way of disambiguating uri based on the idea that the shortest URL is the most popular/general. When we get to the point where we want to disambiguate intelligently, this is (currently) the place to do it...
		if (resList.size() > 0){
			URI = resList.get(0);
			for (String uri: resList){
				if (uri.length() < URI.length()){
					URI = uri;
				}
			}
		}
		return URI;
	}
	/*
	public static void getInfo(){
		  try{
		   // Construct data
			  String sQuery = 
					  "PREFIX gn: <http://www.geonames.org/ontology#>\n" +
							  "						  PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> \n" +
							  "						  PREFIX spatial: <http://jena.apache.org/spatial#> \n" +
							  "						  PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#>\n" +
							  "						   \n" +
							  "\n" +
							  "						  SELECT ?link ?name ?lat ?lon\n" +
							  "						  WHERE  {  \n" +
							  "						     ?link gn:name ?name .  \n" +
							  "						     ?link geo:lat ?lat .\n" +
							  "						     ?link geo:long ?lon\n" +
							  "						  FILTER (?link = <http://sws.geonames.org/4951257/>)\n" +
							  "						  }\n";
			  
		      String data = URLEncoder.encode("query", "UTF-8") + "=" + URLEncoder.encode(sQuery, "UTF-8");
		      //data += "&" + URLEncoder.encode("key2", "UTF-8") + "=" + URLEncoder.encode("value2", "UTF-8");

		      // Send data
		      //URL url = new URL("http://hostname:80/cgi");
		      
		      URL url = new URL("http://www.lotico.com:3030/lotico/sparql");
		      URLConnection conn = url.openConnection();
		      conn.setDoOutput(true);
		      OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		      wr.write(data);
		      wr.flush();

		      // Get the response
		      BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		      String line;
		      String result = "";
		      while ((line = rd.readLine()) != null) {
		    	  System.out.println(line);
		       result += line;
		      }
		      wr.close();
		      rd.close();
		      
		      JSONObject obj = new JSONObject(result);
		      JSONObject obj2 = obj.getJSONObject("results");
		      JSONObject obj3 = obj2.getJSONObject("bindings");
		    
		    		  
		      
		      //System.out.println(result);
		      
		  } catch (Exception e) {
		   e.printStackTrace();
		  }
		 }
	
	*/
	
	public static void queryDBPedia(Model nifModel, String docURI, int nifStartIndex, int nifEndIndex, String infoURL, Property nifProp, String sparqlService){

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
					infoToAdd = p[0];
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
		}
	}


	public static void addGeoStats(Model nifModel, String textToProcess, String string) {

		Double avgLatitude = calculateAverage(latitudes);
		Double stdDevLatitude = calculateStandardDeviation(latitudes, avgLatitude);
		Double avgLongitude = calculateAverage(longitudes);
		Double stdDevLongitude = calculateStandardDeviation(longitudes, avgLongitude);
		Sparqler.latitudes.clear();
		Sparqler.longitudes.clear();
		String docURI = NIFReader.extractDocumentURI(nifModel);
		NIFWriter.addGeoStats(nifModel, textToProcess, avgLatitude,  avgLongitude,  stdDevLatitude,  stdDevLongitude, docURI);

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

		getDBPediaURI("Berlin", "en", "http://dbpedia.org/sparql", "http://dbpedia.org");		
		getDBPediaURI("Berlin", "de", "http://de.dbpedia.org/sparql", "http://de.dbpedia.org");
	}

}
