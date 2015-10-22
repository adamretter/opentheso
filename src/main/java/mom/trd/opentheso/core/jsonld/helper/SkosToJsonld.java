/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.core.jsonld.helper;
import java.util.ArrayList;
import skos.SKOSConceptScheme;
import skos.SKOSDate;
import skos.SKOSDocumentation;
import skos.SKOSLabel;
import skos.SKOSMapping;
import skos.SKOSProperty;
import skos.SKOSRelation;
import skos.SKOSResource;
import skos.SKOSTopConcept;
import skos.SKOSXmlDocument;

/**
 *
 * @author miled.rousset
 */
public class SkosToJsonld {

    private static final String nameSpaceSkosCore = "http://www.w3.org/2004/02/skos/core#";
    private static final String nameSpaceDcTerms = "http://purl.org/dc/terms/";
    
    private StringBuffer jsonLd; 
    
    public SkosToJsonld() {
    }

    /**
     * à compléter plus tard pour produire du JsonLd sous forme de
     * Classe JsonLdDocument
     * @param skosDocument
     * @return 
     */
/*    private JsonLdDocument setJsonLdDocument(SKOSXmlDocument skosDocument) {
        JsonLdDocument jsonLdDocument = new JsonLdDocument();
        ArrayList<JsonConcept> jsonConcepts = new ArrayList<>();
        
        // boucle pour tous les concepts
        for (SKOSResource skosConcept : skosDocument.getResourcesList()) {
            startConcept();
            // add this concept to Json
            JsonConcept jsonConcept = new JsonConcept();
            jsonConcept.setId(skosConcept.getUri());
            jsonConcept.setNameSpace(nameSpaceSkosCore + "Concept");
            jsonConcepts.add(jsonConcept);
            

        }
        
        
        
        return jsonLdDocument;
    }*/

   /**
     *  Cette fonction permet de transformer un document Skos de type ConceptScheme en JsonLd
     * @param skosDocument
     * @return StringBuffer
     */
    public StringBuffer getJsonLdConceptScheme(SKOSXmlDocument skosDocument) {
        
        if(skosDocument == null) return null;
        jsonLd = new StringBuffer();
        
        startJson();
        startConcept();
        // boucle pour les Groupes (ConceptScheme)
        SKOSConceptScheme sKOSConceptScheme = skosDocument.getConceptScheme();
        if(sKOSConceptScheme == null) return null;
        
        // add this uri to Json
        addIdConcept(sKOSConceptScheme.getUri());
        
        // add labels
        if(!sKOSConceptScheme.getSkosLabels().isEmpty()) {
            addLabels(sKOSConceptScheme.getSkosLabels());
        }
        
        if(!sKOSConceptScheme.getTopConceptsList().isEmpty()){
            addElementInScheme(sKOSConceptScheme.getTopConceptsList(),
                    "hasTopConcept");
        }
        
        endLastConcept();
        endJson();
        
        return jsonLd;
    }
        
    /**
     *  Cette fonction permet de transformer un document Skos en JsonLd
     * @param skosDocument
     * @return StringBuffer
     */
    public StringBuffer getJsonLdDocument(SKOSXmlDocument skosDocument) {
        
        if(skosDocument == null) return null;
        jsonLd = new StringBuffer();
        
        startJson();
        
        boolean first = true;
        

        // boucle pour tous les concepts
        for (SKOSResource skosConcept : skosDocument.getResourcesList()) {
            if(!first) {
                endConcept();
            }
            startConcept();

            // add this concept to Json
            addIdConcept(skosConcept.getUri());

            // add dates
            if(!skosConcept.getDateList().isEmpty()) {
                addDates(skosConcept.getDateList());
            }
            // add documentations
            if(!skosConcept.getDocumentationsList().isEmpty()){
                addDocumentations(skosConcept.getDocumentationsList());
            }             
            
            // add labels
            if(!skosConcept.getLabelsList().isEmpty()) {
                addLabels(skosConcept.getLabelsList());
            }
            
            // add relations
            if(!skosConcept.getRelationsList().isEmpty()){
                addRelations(skosConcept.getRelationsList());
            }
            
            // add mappings
            if(!skosConcept.getMappings().isEmpty()){
                addMappings(skosConcept.getMappings());
            }
        //    endConcept();
         //   endElement();
            first = false;
        }
        endLastConcept();
        endJson();
        
        return jsonLd;
    }
 
    private void addIdConcept(String uri){
        String conceptId;
        conceptId = "      \"@id\": \"" + uri + "\",\n";
        conceptId += "      \"@type\": \"" + nameSpaceSkosCore + "Concept" + "\"";
        
        jsonLd.append(conceptId);
    }
    
    /**
     * Ajout des dates à Json
     * @param dates 
     */
    private void addDates(ArrayList<SKOSDate> dates) {
        String created;
        String modified;
        
        for (SKOSDate date : dates) {
            switch (date.getProperty()) {
                case SKOSProperty.created:
                    endElement();
                    created = "      \"" + nameSpaceDcTerms + "created" + "\": ";
                    created += "\"" + date.getDate() + "\"";
                    jsonLd.append(created);
                    break;
                case SKOSProperty.modified:
                    endElement();
                    modified = "      \"" + nameSpaceDcTerms + "modified" + "\": ";
                    modified += "\"" + date.getDate() + "\"";
                    jsonLd.append(modified);
                    break;
                default:
                    break;
            }
        }
    }
    
    private void addLabels(ArrayList<SKOSLabel> sKOSLabels) {
        ArrayList <SKOSLabel> prefLabel = new ArrayList<>();
        ArrayList <SKOSLabel> altLabel = new ArrayList<>();        
        
        for (SKOSLabel sKOSLabel : sKOSLabels) {
            switch (sKOSLabel.getProperty()) {
                case SKOSProperty.prefLabel:
                    prefLabel.add(sKOSLabel);
                    break;
               case SKOSProperty.altLabel:
                    altLabel.add(sKOSLabel);
                    break;    
                default:
                    break;
            }
        }

        // prefLabls
        String prefLabelString;
        if(!prefLabel.isEmpty()) {
            if(prefLabel.size() > 1) {
                endElement();
                prefLabelString = "      \"" + nameSpaceSkosCore + "prefLabel" + "\": [\n";
                boolean first = true;
                for (SKOSLabel prefLabel1 : prefLabel) {
                    if(!first)
                        prefLabelString += ",\n"; 
                    prefLabelString += "        {\n";
                    prefLabelString += "          \"@language\": \"" + prefLabel1.getLanguage() + "\",\n";
                    prefLabelString += "          \"@value\": \"" + prefLabel1.getLabel() + "\"\n";
                    prefLabelString += "        }";
                    first = false;
                }
                prefLabelString += "\n      ]";
            } 
            else {
                endElement();
                prefLabelString = "      \"" + nameSpaceSkosCore + "prefLabel" + "\": {\n";
                for (SKOSLabel prefLabel1 : prefLabel) {
                    prefLabelString += "        \"@language\": \"" + prefLabel1.getLanguage() + "\",\n";
                    prefLabelString += "        \"@value\": \"" + prefLabel1.getLabel() + "\"\n";            
                    prefLabelString += "      }";
                }
            }
            jsonLd.append(prefLabelString);
        }
        
        // altLables
        String altLabelString; 
        if(!altLabel.isEmpty()) {
            if(altLabel.size() > 1) {
                endElement();
                altLabelString = "      \"" + nameSpaceSkosCore + "altLabel" + "\": [\n";
                boolean first = true;
                for (SKOSLabel altLabel1 : altLabel) {
                    if(!first)
                        altLabelString += ",\n"; 
                    altLabelString += "        {\n";
                    altLabelString += "          \"@language\": \"" + altLabel1.getLanguage() + "\",\n";
                    altLabelString += "          \"@value\": \"" + altLabel1.getLabel() + "\"\n";
                    altLabelString += "        }";
                    first = false;
                }
                altLabelString += "\n      ]";
            } 
            else {
                endElement();
                altLabelString = "      \"" + nameSpaceSkosCore + "altLabel" + "\": {\n";
                for (SKOSLabel altLabel1 : altLabel) {
                    altLabelString += "        \"@language\": \"" + altLabel1.getLanguage() + "\",\n";
                    altLabelString += "        \"@value\": \"" + altLabel1.getLabel() + "\"\n";            
                    altLabelString += "      }";
                }
            }
            jsonLd.append(altLabelString);
        }
    }
    
    private void addRelations(ArrayList<SKOSRelation> relations) {
        ArrayList <SKOSRelation> narrower = new ArrayList<>();
        ArrayList <SKOSRelation> related = new ArrayList<>();
        ArrayList <SKOSRelation> broader = new ArrayList<>();
        ArrayList <SKOSRelation> inScheme = new ArrayList<>();
        
        for (SKOSRelation relation : relations) {
            switch (relation.getProperty()) {
                case SKOSProperty.broader:
                    broader.add(relation);
                    break;                  
                case SKOSProperty.narrower:
                    narrower.add(relation);
                    break;
                case SKOSProperty.related:
                    related.add(relation);
                    break;
                case SKOSProperty.ConceptScheme:
                    inScheme.add(relation);
                    break;                      
                default:
                    break;
            }
        }
        
        // broader
        if(!broader.isEmpty()) {
            addElementRelation(broader, "broader");
        }
        
        // narrower
        if(!narrower.isEmpty()) {
            addElementRelation(narrower, "narrower");
        }
        
        // related
        if(!related.isEmpty()) {
            addElementRelation(related, "related");
        }
        
        // ConceptScheme
        if(!inScheme.isEmpty()) {
            addElementRelation(inScheme, "inScheme");
        }
    }
    
    private void addDocumentations(ArrayList<SKOSDocumentation> documentations) {
        for (SKOSDocumentation documentation : documentations) {
            switch (documentation.getProperty()) {
                case SKOSProperty.scopeNote:
                    endElement();
                    addElementDocumentation(documentation, "scopeNote");
                    break;
                case SKOSProperty.historyNote:
                    endElement();
                    addElementDocumentation(documentation, "historyNote");
                    break;
                case SKOSProperty.definition:
                    endElement();
                    addElementDocumentation(documentation, "definition");
                    break;
                case SKOSProperty.editorialNote:
                    endElement();
                    addElementDocumentation(documentation, "editorialNote");
                    break;                    
                default:
                    break;
            }
        }
    }    
    
    private void addMappings(ArrayList<SKOSMapping> mappings) {
        ArrayList <SKOSMapping> closeMatch = new ArrayList<>();
        ArrayList <SKOSMapping> exactMatch = new ArrayList<>();        
        
        for (SKOSMapping mapping : mappings) {
            switch (mapping.getProperty()) {
                case SKOSProperty.closeMatch:
                    closeMatch.add(mapping);
                    break;
                case SKOSProperty.exactMatch:
                    exactMatch.add(mapping);
                    break;                    
                default:
                    break;
            }
        }

        // closeMatch
        if(!closeMatch.isEmpty()) {
            addElementMapping(closeMatch, "closeMatch");
        } 
        
        // exactMatch
        if(!exactMatch.isEmpty()) {
            addElementMapping(exactMatch, "exactMatch");
        } 
    }
    
    private void addElementRelation(ArrayList <SKOSRelation> skosRelation, String nameSpace) {
        String element;
        if(skosRelation.size() > 1) {
            endElement();

            element = "      \"" + nameSpaceSkosCore + nameSpace + "\": [\n";
            boolean first = true; 
            for (SKOSRelation skosRelation1 : skosRelation) {
                if(!first)
                    element += "        },\n";
                element += "        {\n";
                element += "          \"@id\": \"" + skosRelation1.getTargetUri() + "\"\n";
                first = false;
            }
            element += "        }\n";
            element += "      ]";
        } 
        else {
            endElement();
            element = "      \"" + nameSpaceSkosCore + nameSpace + "\": {\n";
            for (SKOSRelation skosRelation1 : skosRelation) {
                element += "        \"@id\": \"" + skosRelation1.getTargetUri() + "\"\n";
                element += "      }";
            }
        }
        jsonLd.append(element);
    }
    
    private void addElementInScheme(ArrayList <SKOSTopConcept> sKOSTopConcepts, String nameSpace) {
        String element;
        if(sKOSTopConcepts.size() > 1) {
            endElement();

            element = "      \"" + nameSpaceSkosCore + nameSpace + "\": [\n";
            boolean first = true; 
            for (SKOSTopConcept sKOSTopConcept : sKOSTopConcepts) {
                if(!first)
                    element += "        },\n";
                element += "        {\n";
                element += "          \"@id\": \"" + sKOSTopConcept.getTopConcept() + "\"\n";
                first = false;
            }
            element += "        }\n";
            element += "      ]";
        } 
        else {
            endElement();
            element = "      \"" + nameSpaceSkosCore + nameSpace + "\": {\n";
            for (SKOSTopConcept sKOSTopConcept : sKOSTopConcepts) {
                element += "        \"@id\": \"" + sKOSTopConcept.getTopConcept() + "\"\n";
                element += "      }";
            }
        }
        jsonLd.append(element);
    }    
    
    private void addElementMapping(ArrayList <SKOSMapping> skosRelation, String nameSpace) {
        String element;
        if(skosRelation.size() > 1) {
            boolean first = true;
            endElement();
            element = "      \"" + nameSpaceSkosCore + nameSpace + "\": [\n";
            for (SKOSMapping skosRelation1 : skosRelation) {
                if(!first)
                    element += "        },\n";
                element += "        {\n";
                element += "          \"@id\": \"" + skosRelation1.getTargetUri() + "\"\n";
                first = false;
            }
            element += "        }\n";
            element += "      ]";
        } 
        else {
            endElement();
            element = "      \"" + nameSpaceSkosCore + nameSpace + "\": {\n";
            for (SKOSMapping skosRelation1 : skosRelation) {
                element += "        \"@id\": \"" + skosRelation1.getTargetUri() + "\"\n";
                element += "      }";
            }
        }
        jsonLd.append(element);
    }
    
    private void addElementDocumentation(SKOSDocumentation sKOSDocumentation, String nameSpace) {
        String element = "      \"" + nameSpaceSkosCore + nameSpace + "\": {\n";
        element += "        \"@language\": \"" + sKOSDocumentation.getLanguage() + "\",\n";
        element += "        \"@value\": \"" + sKOSDocumentation.getText() + "\"\n";        
        element += "      }";
        jsonLd.append(element);
    }
    
    private void startConcept() {
        jsonLd.append("    {\n");
    }

    private void endConcept() {
        jsonLd.append("\n    },\n");
    }
    
    private void endElement() {
        jsonLd.append(",\n");
    }
    
    private void endLastConcept() {
        jsonLd.append("\n    }\n");
    }
    
    private void startJson() {
        jsonLd.append("{\n");
        jsonLd.append("  \"@graph\":  [\n");
    }
    
    private void endJson() {
        jsonLd.append("  ]\n");
        jsonLd.append("}\n");
    }    
}
