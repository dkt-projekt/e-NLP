package de.dkt.eservices.eopennlp.modules;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import de.dkt.common.niftools.DBO;
import de.dkt.common.niftools.DKTNIF;
import de.dkt.common.niftools.NIFReader;
import de.dkt.common.niftools.NIFWriter;
import de.dkt.eservices.erattlesnakenlp.modules.Sparqler;
//import de.dkt.eservices.esesame.modules.SesameStorage;
import eu.freme.common.exception.BadRequestException;
import eu.freme.common.exception.ExternalServiceFailedException;
import opennlp.tools.dictionary.Dictionary;
import opennlp.tools.namefind.DictionaryNameFinder;
import opennlp.tools.util.Span;
import opennlp.tools.util.StringList;

/**
 * @author Julian Moreno Schneider julian.moreno_schneider@dfki.de
 *
 */
public class DictionaryNameF {

	//public static String modelsDirectory = "trainedModels/";
	public static String dictionariesDirectory = "trainedModels" + File.separator + "dict" + File.separator;

	public static String[] readLines(String filename) throws IOException {
        FileReader fileReader = new FileReader(filename);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        List<String> lines = new ArrayList<String>();
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            lines.add(line);
        }
        bufferedReader.close();
        return lines.toArray(new String[lines.size()]);
    }
	
	
	
	
	public static Model detectEntitiesNIF(Model nifModel, ArrayList<String> dictionaries, String sentModel) throws ExternalServiceFailedException {
	
		for (String dictionary : dictionaries){
			try {
				Dictionary dct = new Dictionary();
				HashMap<String, String> dictHash = new HashMap<String, String>();
				ClassPathResource cprDictModel = new ClassPathResource(dictionariesDirectory + dictionary);
				InputStream dictionaryModelStream = new FileInputStream(cprDictModel.getFile());
				BufferedReader br = new BufferedReader(new InputStreamReader(dictionaryModelStream, "utf-8"));
				String line = br.readLine();
				while (line != null){
					String parts[] = line.split("\t");
					String[] tokens = parts[0].split(" ");
					dct.put(new StringList(tokens));
					dictHash.put(parts[0].trim().toLowerCase(), parts[1]);
					line = br.readLine();
				}
				br.close();
				String[] temp = dictionary.split("_");
				String dictionaryAnnotationType = temp[temp.length-1];
				DictionaryNameFinder nameFinder = new DictionaryNameFinder(dct, dictionaryAnnotationType);
				String content = NIFReader.extractIsString(nifModel);
				Span[] sentenceSpans = SentenceDetector.detectSentenceSpans(content, sentModel);
				for (Span ss : sentenceSpans){
					String sentence = content.substring(ss.getStart(), ss.getEnd());
					Span tokenSpans[] = Tokenizer.simpleTokenizeIndices(sentence);
					String[] tokens = Span.spansToStrings(tokenSpans, sentence);
					Span[] nameSpans = nameFinder.find(tokens); 
					for (Span ns : nameSpans){
						int nameStartIndex = 0;
						int nameEndIndex = 0;
						for (int i = 0; i <= tokenSpans.length ; i++){
							if (i == ns.getStart()){
								nameStartIndex = tokenSpans[i].getStart() + ss.getStart();
							}
							else if (i == ns.getEnd()){
								nameEndIndex = tokenSpans[i-1].getEnd() + ss.getStart();
							}
						}
						String foundName = content.substring(nameStartIndex, nameEndIndex);
						String uri = dictHash.get(foundName.toLowerCase());
						

						//List<String> entURIs = new LinkedList<String>();
						//entURIs.add(uri);
						String entityType = null;
						// TODO: put in docs for dictionary upload that it HAS TO BE one the the following three:
						if (dictionaryAnnotationType.equalsIgnoreCase("PER")){
							entityType = DBO.person.toString();
						}
						else if (dictionaryAnnotationType.equalsIgnoreCase("LOC")){
							entityType = DBO.location.toString();
						}
						else if (dictionaryAnnotationType.equalsIgnoreCase("ORG")){
							entityType = DBO.organisation.toString();
						}
						else{
							// added because we want to allow the flexibility to add any types in dictionary uploading
							entityType = DKTNIF.property(dictionaryAnnotationType).toString();
						}
						//NIFWriter.addAnnotationEntities(nifModel, nameStartIndex, nameEndIndex, foundName, uri, DFKINIF.resource(dictionaryAnnotationType).toString());
						NIFWriter.addAnnotationEntities(nifModel, nameStartIndex, nameEndIndex, foundName, uri, entityType);
						
						/*
						 * This part is just for temporary purposes (paper ACL). Improve this!
						 */
						
//						if (dictionary.equals("clean_mendelsohn_LOC")){
//							if (!(uri == null)){
//								String geoId = uri.substring(uri.lastIndexOf("/")+1);
//								String sparqlQuery = 
//									"PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos/>\n" +
//									//"PREFIX sws: <http://sws.geonames.org/>\n" +
//									"SELECT ?lat ?long WHERE { \n" +
//									"?geoNameId geo:lat ?lat . \n" +
//									"?geoNameId geo:long ?long . \n" +
//									"FILTER (?geoNameId = <http://sws.geonames.org/" + geoId + "/>) \n" +
//									"}";	
//								SesameStorage.setStorageDirectory("C:\\Users\\pebo01\\workspace\\e-Sesame\\target\\test-classes\\ontologies\\");
//								List<BindingSet> sets = SesameStorage.retrieveTQRTripletsFromSPARQL("geoFinal", sparqlQuery);
//								
//								String lat = null;
//								String lon = null;
//								for (BindingSet bs : sets) {
//									lat = bs.getValue("lat").toString().replaceAll("\"", "");
//									lon = bs.getValue("long").toString().replaceAll("\"", "");
//								}
//								if (!(lat == null) && !(lon == null)){
//									Sparqler.latitudes.add(Double.parseDouble(lat));
//									Sparqler.longitudes.add(Double.parseDouble(lon));
//									NIFWriter.addPrefixToModel(nifModel, "geo", GEO.uri);
//									NIFWriter.addEntityProperty(nifModel, nameStartIndex, nameEndIndex, NIFReader.extractDocumentURI(nifModel), lat, GEO.latitude, XSDDatatype.XSDdouble);
//									NIFWriter.addEntityProperty(nifModel, nameStartIndex, nameEndIndex, NIFReader.extractDocumentURI(nifModel), lon, GEO.longitude, XSDDatatype.XSDdouble);
//								}
//							}
//						}
						

						
					}
					
						
				}
//				// if there was a location in there, add document stats for geopoints
//				if (Sparqler.latitudes.size() > 0 || Sparqler.longitudes.size() > 0){
//					Sparqler.addGeoStats(nifModel, content, NIFReader.extractDocumentURI(nifModel));
//				}

	
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				throw new ExternalServiceFailedException("ERROR while analyzing text. Model not found in:" + dictionariesDirectory + dictionary);
			} catch (IOException e) {
				e.printStackTrace();
				throw new ExternalServiceFailedException("ERROR while analyzing text...");
			}
		}
		//HttpHeaders responseHeaders = new HttpHeaders();
        //responseHeaders.add("Content-Type", "RDF/XML");
        //String nifString = NIFReader.model2String(nifModel, "TTL"); // TODO: probably don't want to have this hardcoded in here (as in many other places)
       	//return new ResponseEntity<String>(nifString, responseHeaders, HttpStatus.OK);
		
		return nifModel;
		
	}
		/**
	 * 
	 * @param inputDictionaryData Stream of training data
	 * @param dictionaryName Name to be assigned to the model
	 * @return true if the model has been successfully trained
	 */
	public static String createDictionaryFromString(String dictionaryDataType, String inputData, String sDictionary) throws BadRequestException, ExternalServiceFailedException {
		try{
			HashMap<String, String> hash = new HashMap<String, String>();
			
			ClassPathResource cprOutputDictionary = new ClassPathResource(dictionariesDirectory);
			File outputDictionaryFile = new File(cprOutputDictionary.getFile(), sDictionary);
			//if(outputDictionaryFile.exists()){
				//throw new ExternalServiceFailedException("This dictionary already exists. Change the name to create another dictionary.");
			//}
			//if(!outputDictionaryFile.createNewFile()){
				//throw new ExternalServiceFailedException("ERROR creating the dictionary output file.");
			//}
			
			
			if(dictionaryDataType.equalsIgnoreCase("RDF/XML") || dictionaryDataType.equalsIgnoreCase("N-TRIPLES") || dictionaryDataType.equalsIgnoreCase("Turtle")){
				Model model = ModelFactory.createDefaultModel(); 
		        model.read(new ByteArrayInputStream(inputData.getBytes()), dictionaryDataType);
		        
		        StmtIterator iter = model.listStatements();
		        while (iter.hasNext()) {
		            Statement stmt      = iter.nextStatement();  // get next statement
		            Resource  subject   = stmt.getSubject();     // get the subject
		            Property  predicate = stmt.getPredicate();   // get the predicate
		            RDFNode   object    = stmt.getObject();      // get the object
					hash.put(object.toString(), subject.toString());
		        } 
			}
			else if (dictionaryDataType.equalsIgnoreCase("tsv")){
				String sentences[] = inputData.split("\n");
				for (int i = 0; i < sentences.length; i++) {
					String parts[] = sentences[i].split("\t");
					hash.put(parts[0], parts[1]);
					
				}
			}
			else{
				throw new ExternalServiceFailedException("Format '" + dictionaryDataType + "' not supported.");
			}
			
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputDictionaryFile),"utf-8"));
			Set<Entry<String,String>> elements = hash.entrySet();
			for (Entry<String, String> entry : elements) {
				
				bw.write(entry.getKey() + "\t" + entry.getValue() + "\n");
				
			}
			bw.close();
			return sDictionary;//"The dictionary " + dictionariesDirectory + sDictionary + " has been succesfully created.";
		}
		catch(FileNotFoundException e){
			throw new BadRequestException(e.getMessage());
		}
		catch(IOException e){
			throw new ExternalServiceFailedException(e.getMessage());
		}
	}

	public static void main(String[] args) throws Exception {
//		DictionaryNameF.detectEntitiesInFile("C:\\Users\\jmschnei\\Desktop\\dkt-test\\NER-de-test.opennlp2CaseSMALL.train", 
//				"C:\\Users\\jmschnei\\Desktop\\dkt-test\\NER-de-dict-SELF.txt", "LOC", "ner", "de-token.bin", "de-sent.bin", "de", null);
		
		//System.out.println(DictionaryNameF.detectEntitiesNIF("I was living in Berlin after Muenchen but before Frankfurt.", "locations-de.txt", "LOCATION2", "en-token.bin", "en-sent.bin"));
		String testDictMendelsohn = 
							"Mendelsohn	http://d-nb.info/gnd/11858071X\n" +
							"Luise Mendelsohn	http://d-nb.info/gnd/128830751";
		//createDictionaryFromString("tsv", testDict, "mendelsohnTest");
		//System.out.println("created:" + "mendelsohnTest");
		
		Model nifModel = ModelFactory.createDefaultModel();
		String docURI = "http://dkt.dfki.de/examples/";
		NIFWriter.addInitialString(nifModel, "Luise Mendelsohn acted in The Matrix Reloaded", docURI);
		ArrayList<String> dictionaries = new ArrayList<String>();
		dictionaries.add("mendelsohnTest");
		nifModel = detectEntitiesNIF(nifModel, dictionaries, "en-sent.bin");
		System.out.println(NIFReader.model2String(nifModel, "turtle"));
		//System.out.println(detectEntitiesNIF("This is a story about Erich Mendelsohn. It is very short. It also mentions Luise Mendelsohn. The end.", "mendelsohnTest", "PERSON", "en-token.bin", "en-sent.bin"));
		
	}
}
