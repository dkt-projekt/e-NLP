package de.dkt.eservices.erattlesnakenlp;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.dkt.common.filemanagement.FileFactory;

public class TaggerRelationCleaning {

	public static void main(String[] args) throws Exception{
		String folder = "/Users/jumo04/Downloads/tests/";
		int windowsSize = 20;
		String debugFilename = "debug_ws"+windowsSize+".txt";

		HashMap<String,HashMap<String,Integer>> relations = new HashMap<String,HashMap<String,Integer>>();
		BufferedReader br = FileFactory.generateBufferedReaderInstance(folder+debugFilename, "utf-8");
		String line = br.readLine();
		while(line!=null){
			if(line.contains("-->")){
//				System.out.println(line);
				String sub = line.substring(0, line.indexOf('\t'));
				String obj = line.substring(line.lastIndexOf("-->")+3).trim();
				if(relations.containsKey(sub)){
					if(relations.get(sub).containsKey(obj)){
						relations.get(sub).put(obj,relations.get(sub).get(obj)+1);
					}
					else{
						relations.get(sub).put(obj,new Integer(1));
					}
				}
				else{
					HashMap<String,Integer> list = new HashMap<String,Integer>();
					list.put(obj,new Integer(1));
					relations.put(sub, list);
				}
				if(relations.containsKey(obj)){
					if(relations.get(obj).containsKey(sub)){
						relations.get(obj).put(sub,relations.get(obj).get(sub)+1);
					}
					else{
						relations.get(obj).put(sub,new Integer(1));
					}
				}
				else{
					HashMap<String,Integer> list = new HashMap<String,Integer>();
					list.put(sub,new Integer(1));
					relations.put(obj, list);
				}
			}
			line = br.readLine();
		}
		br.close();
		
		String father = "Eric(http://d-nb.info/gnd/11858071X)";
		HashMap<String,Integer> uris = relations.get(father);
		Set<String> keys = uris.keySet();
		List<String> usedKeys = new LinkedList<String>();
		usedKeys.add(father);
		for (String k : keys) {
			if(!k.equalsIgnoreCase(father)){
				if(uris.get(k)>10){
//					usedKeys.add(k);
					String[] parts = k.split("\\(");
					System.out.println("<li><a href=\""+parts[1].replace(")", " ").trim()+"\">"+parts[0]+"</a>");
	//				System.out.println(k + "\t\t"+uris.get(k));
					System.out.println("<ul>");
	
					HashMap<String,Integer> uris2 = relations.get(k.trim());
					Set<String> keys2 = uris2.keySet();
					List<String> usedKeys2 = new LinkedList<String>();
					for (String sw : usedKeys) {
//						usedKeys2.add(new String(sw));
					}
					for (String k2 : keys2) {
						if(!k2.equalsIgnoreCase(k) && !k2.equalsIgnoreCase(father)){
							if(uris2.get(k2)>10){
//								usedKeys2.add(k2);
								String[] parts2 = k2.split("\\(");
								System.out.println("\t<li><a href=\""+parts2[1].replace(")", " ").trim()+"\">"+parts2[0]+"</a>");
		//						System.out.println(k + "\t\t"+uris.get(k));
								System.out.println("\t<ul>");
		
								HashMap<String,Integer> uris3 = relations.get(k2.trim());
								Set<String> keys3 = uris3.keySet();
								List<String> usedKeys3 = new LinkedList<String>();
								for (String sw : usedKeys2) {
									usedKeys3.add(new String(sw));
								}
								for (String k3 : keys3) {
									if(!k3.equalsIgnoreCase(k) && !k3.equalsIgnoreCase(k2) && !k3.equalsIgnoreCase(father)){
										if(uris3.get(k3)>10){
//											usedKeys3.add(k3);
											String[] parts3 = k3.split("\\(");
											System.out.println("\t\t<li><a href=\""+parts3[1].replace(")", " ").trim()+"\">"+parts3[0]+"</a>");
//											System.out.println("\t\t<ul>");
//			
//											HashMap<String,Integer> uris4 = relations.get(k3.trim());
//											Set<String> keys4 = uris4.keySet();
//											List<String> usedKeys4 = new LinkedList<String>();
//											for (String sw : usedKeys3) {
//												usedKeys4.add(new String(sw));
//											}
//											for (String k4 : keys4) {
//												if(!usedKeys3.contains(k3)){
//													if(uris4.get(k4)>20){
//	//													usedKeys4.add(k4);
//														String[] parts4 = k4.split("\\(");
//														System.out.println("\t\t\t<li><a href=\""+parts4[1].replace(")", " ").trim()+"\">"+parts4[0]+"</a></li>");
//													}
//												}
//											}
//			
//											System.out.println("\t\t</ul>");
											System.out.println("\t\t</li>");
										}
									}
								}
								System.out.println("\t</ul>");
								System.out.println("\t</li>");
							}
						}
					}
					System.out.println("</ul>");
					System.out.println("</li>");
				}
			}
		}
		
		
	}
	
}
