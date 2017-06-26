package de.dkt.eservices.execution;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.l3s.boilerpipe.document.TextDocument;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import de.l3s.boilerpipe.sax.BoilerpipeHTMLContentHandler;
import de.l3s.boilerpipe.sax.BoilerpipeSAXInput;
import de.l3s.boilerpipe.sax.HTMLDocument;
import de.l3s.boilerpipe.sax.HTMLFetcher;

public class WikipediaNewsCrawler {

	
	public static void main(String[] args) throws Exception {
		String uri = "https://en.wikipedia.org/wiki/List_of_international_presidential_trips_made_by_Barack_Obama";
        URL oracle = new URL(uri);
        BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));
        String inputLine;
        int counter = 1;
        while ((inputLine = in.readLine()) != null){
        	if(inputLine.contains("<li id=\"cite_note")){
            	if(counter>-1){
//	                System.out.println(inputLine);
	                try{
	                    // Create a DOM builder and parse the fragment.
	                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	                    Document d = factory.newDocumentBuilder().parse(new InputSource(new StringReader(inputLine)));
	                    NodeList list = d.getElementsByTagName("a");
	                    boolean found=false;
	                    String auxUri="";
	                    for (int i = 0; i < list.getLength() && !found; i++) {
							Node node = list.item(i);
							
							 NamedNodeMap map = node.getAttributes();
							 Node href = map.getNamedItem("href");
	//						 System.out.println("\t"+href.getTextContent());
							 if(href.getTextContent().startsWith("http")){
								 found=true;
								 auxUri = href.getTextContent();
							 }
						}
	                    
	                    System.out.println("\t"+auxUri);
	                    try{
	                    	final HTMLDocument htmlDoc = HTMLFetcher.fetch(new URL(auxUri));
	                    	final TextDocument doc = new BoilerpipeSAXInput(htmlDoc.toInputSource()).getTextDocument();
	                    	String title = doc.getTitle();

	                    	String content = ArticleExtractor.INSTANCE.getText(doc);
	                    	String ss = IOUtils.toString(htmlDoc.toInputSource().getByteStream());
	                    	
	                    	if(!content.equalsIgnoreCase("")){
		                    	FileOutputStream fos = new FileOutputStream("src/main/resources/files/news2/txt/"+counter+".txt");
		                    	IOUtils.write(content, fos,"utf-8");
		                    	fos.close();
		                    	FileOutputStream fos2 = new FileOutputStream("src/main/resources/files/news2/html/"+counter+".html");
		                    	IOUtils.write(ss, fos2,"utf-8");
		                    	fos2.close();
	                    	}
//	                    	System.out.println("------------------------");
//	                    	System.out.println("------------------------");
//	                    	System.out.println("------------------------");
//	                    	System.out.println(ss);
//	                    	System.out.println("------------------------");
//	                    	System.out.println(content);
//	                    	System.out.println("------------------------");

//	                    	final BoilerpipeExtractor extractor = CommonExtractors.KEEP_EVERYTHING_EXTRACTOR;

//	                    	org.jsoup.nodes.Document doc = Jsoup.connect(auxUri).get();
//	//                		System.out.println(doc.title());
//	                    	String data = doc.text();
//	                    	String data2 = doc.getElementsByTag("body").text();
//	                		System.out.println("\t"+data);
//	                		System.out.println("\t"+data2);
//	 //               		System.out.println(doc.getElementsByTag("body").text());
//	                    	FileOutputStream fos = new FileOutputStream("src/main/resources/files/"+counter+".txt");
//	                    	IOUtils.write(data, fos,"utf-8");
//	                    	fos.close();
//	                    	FileOutputStream fos2 = new FileOutputStream("src/main/resources/files/"+counter+"_body.txt");
//	                    	IOUtils.write(data2, fos2,"utf-8");
//	                    	fos2.close();
	                    }
	                    catch(Exception e){
	                    	System.out.println("\tTHERE IS NO WEBSITE");
	                    }
	                }
	                catch (SAXException e){
	                }
	                catch (ParserConfigurationException e){
	                }
	                catch (IOException e){
	                }
	        	}
            	counter++;
        	}        	
        }
        in.close();

	}
}
