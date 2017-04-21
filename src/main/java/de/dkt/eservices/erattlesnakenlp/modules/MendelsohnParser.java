package de.dkt.eservices.erattlesnakenlp.modules;

import java.io.File;
import java.io.FileInputStream;
import info.debatty.java.stringsimilarity.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.dkt.common.filemanagement.FileFactory;

@Component
public class MendelsohnParser {

	private static String metaDataDir = "MendelsohnMetaData";
	public static HashMap<byte[], HashMap<String, String>> hm;	
	
	
	public String getAuthor(byte[] key){
		HashMap<String, String> im = hm.get(key);
		return im.get("author");
	}
	
	public String getDate(byte[] key){
		HashMap<String, String> im = hm.get(key);
		return im.get("date");
	}
	
	public String getLocation(byte[] key){
		HashMap<String, String> im = hm.get(key);
		return im.get("location");
	}
	
	public byte[] getHashKey(String s){
		
		byte[] ba = null;
		
		try {
			File modelFile = FileFactory.generateOrCreateFileInstance(metaDataDir + File.separator + "metadata.ser");
			FileInputStream fis = new FileInputStream(modelFile);
			ObjectInputStream ois = new ObjectInputStream(fis);
			hm = (HashMap<byte[], HashMap<String, String>>)ois.readObject();
		    ois.close();
		    fis.close();
		    
			// first check if the identical one is perhaps in the hash (EDIT: not sure how md5 is implemented, and if the byte array will ever be exactly the same, hence commented out the bit below
//		    MessageDigest md = MessageDigest.getInstance("MD5");
//			byte[] md5key = md.digest(s.getBytes());
//		    if (hm.containsKey(md5key)){
//		    	//System.out.println("DEBUGGING: Exact letter in map!!!!");
//		    	ba = md5key;
//		    }
//		    else{ // get the one with the closest edit distance to the current one
		    	byte[] j = null;
		    	double smallestDistance = 1.0;
		    	for (byte[] k : hm.keySet()){
		    		HashMap<String, String> im = hm.get(k);
		    		String content = im.get("content");
		    		NormalizedLevenshtein l = new NormalizedLevenshtein();
		            double distance = l.distance(content, s);
		             // the higher the value for distance, the more distance.... (no shit...)
		    		if (distance < smallestDistance ){
		    			smallestDistance = distance;
		    			j = k;
		    		}
		    	}
		    	ba = j;
//		    }
		    
			
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ba;
	}
	
	
	public void serializeMetaData(String xmlFile) throws ParserConfigurationException, SAXException, IOException, NoSuchAlgorithmException{
		
		
		HashMap<byte[], HashMap<String, String>> hm = new HashMap<byte[], HashMap<String, String>>();
		File inputFile = new File(xmlFile);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(inputFile);
		doc.getDocumentElement().normalize();
		NodeList rows = doc.getElementsByTagName("row");
		for (int temp = 0; temp < rows.getLength(); temp++) {
			Node rowNode = rows.item(temp);
			NodeList fieldNodes = rowNode.getChildNodes();
			String author = null;
			String location = null;
			String date = null;
			String content = null;
			for (int j = 0; j < fieldNodes.getLength(); j++){
				Node fieldNode = fieldNodes.item(j);
				if (fieldNode.hasAttributes()){
					if (fieldNode.getAttributes().item(0).toString().equalsIgnoreCase("name=\"verfasser\"")){
						author = fieldNode.getTextContent();
					}
					else if (fieldNode.getAttributes().item(0).toString().equalsIgnoreCase("name=\"kalliope_ort\"")){
						location = fieldNode.getTextContent();
					}
					else if (fieldNode.getAttributes().item(0).toString().equalsIgnoreCase("name=\"kalliope_datum\"")){
						date = fieldNode.getTextContent();
					}
					else if (fieldNode.getAttributes().item(0).toString().equalsIgnoreCase("name=\"handschrift_ausgabe\"")){
						content = fieldNode.getTextContent();
					}
				}
			}
//			System.out.println("Author:" + author);
//			System.out.println("location:" + location);
//			System.out.println("date:" + date);
//			System.out.println("content:" + content);
			if (!content.equals("")){
				MessageDigest md = MessageDigest.getInstance("MD5"); // md5 hashing to get key which is a bit shorter than the letter itself
				byte[] md5key = md.digest(content.getBytes());
				HashMap<String, String> im = new HashMap<String, String>();
				im.put("author", author);
				im.put("location", location);
				im.put("date", date);
				im.put("content", content);
				hm.put(md5key,  im);
			}
		}
		
		// write hashmap to file
		File modelFile = FileFactory.generateOrCreateFileInstance(metaDataDir + File.separator + "metadata.ser");
		FileOutputStream fos = new FileOutputStream(modelFile);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(hm);
		oos.flush();
		oos.close();
		fos.close();

		System.out.println("INFO: Successfully serialized metadata hashmap.");

	}
	
	
	public static void main(String[] args){
		
		MendelsohnParser mp = new MendelsohnParser();
			
		//mp.serializeMetaData("C:\\Users\\pebo01\\Desktop\\mendelsohn_metadata.xml"); // only needs to be done once...
		String testLetter = "\n" +
				//"[Kolorierte Farbzeichnung]\n" +
				"[Kolorierte Farbzeichn]\n" +
				"\n" +
				"\n" +
				"\n" +
				"All our love\n" +
				"and dearest\n" +
				"wishes to all\n" +
				"of you\n" +
				"/\n" +
				"Louise+Eric Mendelsohn\n" +
				"\n" +
				"\n";
		
		byte[] key = mp.getHashKey(testLetter);
		HashMap<String, String> im = hm.get(key);
		System.out.println("author:" + im.get("author"));
		System.out.println("date:" + im.get("date"));
		System.out.println("location:" + im.get("location"));
		System.out.println("content:" + im.get("content"));
		
	
		
	}

}
