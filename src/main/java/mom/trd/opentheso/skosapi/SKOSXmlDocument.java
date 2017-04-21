package mom.trd.opentheso.skosapi;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
/**
 * <p>SKOSXmlDocument est la classe qui permet de construire un document XML au format
 * SKOS (voir le site du W3C http://www.w3.org/TR/2005/WD-swbp-skos-core-guide-20051102).</p>
 * <p>Un objet SKOSXmlDocument est composé de :
 * <ul>
 * <li>Un ConceptScheme contenant les topConcepts</li>
 * <li>Une liste de ressources</li>
 * </ul>
 * </p>
 * 
 * @author Djamel Ferhod
 *
 */
public class SKOSXmlDocument {
	 
	private SKOSResource conceptScheme;
        private ArrayList<SKOSResource> groupList;
        private ArrayList<SKOSResource> conceptList;
        private String title;
	/**
	 * Constructeur qui permet de créer un nouveau SKOSXmlDocument
	 */

     public SKOSXmlDocument(){
         groupList = new ArrayList<>();
         conceptList = new ArrayList<>();
         
     }

    public void addGroup(SKOSResource r) {
        groupList.add(r);
    }
     
     public void addconcept(SKOSResource r){
         conceptList.add(r);
     } 

    public ArrayList<SKOSResource> getGroupList() {
        return groupList;
    }

    public ArrayList<SKOSResource> getConceptList() {
        return conceptList;
    }
     
     

    public SKOSResource getConceptScheme() {
        return conceptScheme;
    }

    public void setConceptScheme(SKOSResource conceptScheme) {
        this.conceptScheme = conceptScheme;
    }
	
	/**
	 * Affecte un SKOSConceptScheme
	 * @param conceptScheme le conceptScheme à affecter
	 */
	
	
	
	


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
	
	/**
	 * Ajoute une ressource à la liste des SKOSResource (@see {@link #resourcesList})
	 * @param resource la ressource à ajouter
	 */

	
	/**
	 * Transforme l'objet SKOSXmlDocument sous forme de String au format XML
	 * @return un document XML au modèle SKOS
	 */
    /*
	public String toString(){
		String xmlDocument = new String();
		Iterator<SKOSResource> it = resourcesList.iterator();
		xmlDocument = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
		xmlDocument += "<rdf:RDF\n";
		xmlDocument += "    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n";
		xmlDocument += "    xmlns:skos=\"http://www.w3.org/2004/02/skos/core#\">\n";
		xmlDocument += "    "+conceptScheme.toString();
		
		while(it.hasNext()){
			xmlDocument += "    "+it.next().toString();
		}
		xmlDocument += "</rdf:RDF>";
		return xmlDocument;
	}
	*/
	public String openingBlock(){
		String xml = new String();
		xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
		xml += "<rdf:RDF\n";
		xml += "    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n";
		xml += "    xmlns:skos=\"http://www.w3.org/2004/02/skos/core#\">\n";
		return xml;
	}
	
	public String closingBlock(){
		String xml = new String();
		xml += "</rdf:RDF>";
		return xml;
	}
	
	/**
	 * Ecrit le document XML dans un fichier encodé en UTF-8
	 * @param fileName le nom du fichier
	 * @return <code>true</code> si le fichier est correctement enregistré sur le disque ;<br/>
	 *         <code>false</code> sinon.
	 */
	public boolean write(String fileName){
		File file = new File(fileName);
		Writer writeFile = null;
		try{
			if(file.exists()) file.delete();
			file.createNewFile();
			writeFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),"UTF-8"));
			writeFile.write(this.toString());
			writeFile.close();
			return true;
		}
		catch(IOException e){
			System.out.println(e.getMessage());
			return false;
		}
	}
	
}
