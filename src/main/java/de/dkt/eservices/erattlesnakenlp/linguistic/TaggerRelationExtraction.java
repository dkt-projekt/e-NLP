package de.dkt.eservices.erattlesnakenlp.linguistic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import de.dkt.common.filemanagement.FileFactory;
import de.dkt.common.niftools.ITSRDF;
import de.dkt.common.niftools.NIF;
import de.dkt.common.niftools.NIFReader;
import eu.freme.common.conversion.rdf.RDFConstants.RDFSerialization;

public class TaggerRelationExtraction {

	public static void main(String[] args) throws Exception {
		String folder = "/Users/jumo04/Downloads/tests/";
		String subfolder = "english/";
		int windowsSize = 20;
		String debugFilename = "debug_ws"+windowsSize+".txt";
		String outputSubfolder = "ws"+windowsSize+"/";
		File f = new File(folder+subfolder);

		BufferedWriter bw = FileFactory.generateOrCreateBufferedWriterInstance(folder+debugFilename, "utf-8", false);
		String[] files = f.list();
		String content = "";
		for (String sf : files) {
			if(!sf.startsWith(".")){
				content = "";
				BufferedReader br = FileFactory.generateBufferedReaderInstance(folder+subfolder+sf, "utf-8");
				String line = br.readLine();
				while(line!=null){
					content = content + line;
					line = br.readLine();
				}
				
				Model model = NIFReader.extractModelFromFormatString(content, RDFSerialization.TURTLE);
				//Get the relations from the nif model.
				List<SpanRelation> relations = EntitiesRelationExtraction.extractRelationsListNIFString(content,windowsSize);
				Model m2 = EntitiesRelationExtraction.extractRelationsNIFString(content,windowsSize);

				BufferedWriter bw1 = FileFactory.generateOrCreateBufferedWriterInstance(folder+outputSubfolder+sf, "utf-8", false);
				bw1.write(NIFReader.model2String(m2, "TTL")+"\n");
				bw1.close();

			    bw.write(folder+subfolder+sf+"\n\n");
//			    System.out.println(NIFReader.model2String(model, "TTL"));
			    for (SpanRelation sp : relations) {
					bw.write(extractTextFromNIFEntity(model, sp.getSubject()));
					bw.write("\t-->\t");
					bw.write(sp.getAction());
					bw.write("\t-->\t");
					bw.write(extractTextFromNIFEntity(model, sp.getObject()));
					bw.write("\n");
					System.out.print(extractTextFromNIFEntity(model, sp.getSubject()));
					System.out.print("\t-->\t");
					System.out.print(sp.getAction());
					System.out.print("\t-->\t");
					System.out.print(extractTextFromNIFEntity(model, sp.getObject()));
					System.out.print("\n");
				}
				bw.write("\n\n\n\n");
				br.close();
			}
		}
		bw.close();
	}
	
	private static String extractTextFromNIFEntity(Model nifModel, String uri) {
		Resource r = nifModel.getResource(uri);
		StmtIterator iter2 = r.listProperties();
		String altUri = "";
		String text = "";
		while (iter2.hasNext()) {
			Statement st2 = iter2.next();
			String predicate =st2.getPredicate().getURI();
			if(predicate.equalsIgnoreCase(NIF.anchorOf.getURI())){
				text = st2.getObject().asLiteral().getString();
			}
			else if(predicate.equalsIgnoreCase(ITSRDF.taIdentRef.getURI())){
				altUri = st2.getObject().asResource().getURI();
			}
			else{
			}
		}
		return text+"("+altUri+")";
	}
}
