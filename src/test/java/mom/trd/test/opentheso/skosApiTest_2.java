/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mom.trd.test.opentheso;


import org.semanticweb.skos.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.semanticweb.skos.SKOSUntypedLiteral;
import org.semanticweb.skosapibinding.SKOSManager;




import java.net.URI;


/**
 *
 * @author miled.rousset
 */
public class skosApiTest_2 {
    
    public skosApiTest_2() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
   
    @Test
    public void testReadSkos() {

        // récupération d'un identifiant unique
    /*    ToolsHelper toolsHelper = new ToolsHelper();
        ConceptHelper conceptHelper = new ConceptHelper();
        String id = toolsHelper.getNewId(10);
        for (int i = 0; i < 10; i++) {
            id = toolsHelper.getNewId(10);
        }
      */  
        try {

            SKOSManager man = new SKOSManager();
//
            SKOSDataset dataSet = man.loadDatasetFromPhysicalURI(URI.create("file:/Users/Miled/Desktop/rameau17.xml"));


            //////

            // print out all concepts;
            for (SKOSConcept concept : dataSet.getSKOSConcepts()) {
                System.out.println("Concept: " + concept.getURI());
                // get the narrower concepts
                for (SKOSEntity narrowerConcepts : concept.getSKOSRelatedEntitiesByProperty(dataSet, man.getSKOSDataFactory().getSKOSNarrowerProperty())) {
                    System.err.println("\t hasNarrower: " + narrowerConcepts.getURI());
                }
                // get the broader concepts
                for (SKOSEntity broaderConcepts : concept.getSKOSRelatedEntitiesByProperty(dataSet, man.getSKOSDataFactory().getSKOSBroaderProperty())) {
                    System.err.println("\t hasBroader: " + broaderConcepts.getURI());
                }
                
                    for (SKOSAnnotation anno : concept.getSKOSAnnotations(dataSet)) {
                        System.err.print("\t\tAnnotation: " + anno.getURI() + "-> ");
                        if (anno.isAnnotationByConstant()) {
                            if (anno.getAnnotationValueAsConstant().isTyped()) {
                                SKOSTypedLiteral con = anno.getAnnotationValueAsConstant().getAsSKOSTypedLiteral();
                                System.err.print(con.getLiteral() + " Type: " + con.getDataType().getURI());
                            }
                            else {
                                SKOSUntypedLiteral con = anno.getAnnotationValueAsConstant().getAsSKOSUntypedLiteral();
                                System.err.print(con.getLiteral());
                                if (con.hasLang()) {
                                    System.err.print("@" + con.getLang());
                                }
                            }
                            System.err.println("");
                        }
                        else {
                            System.err.println(anno.getAnnotationValue().getURI().toString());
                        }
                    }                

            }



            System.out.println("");
            System.out.println("---------------------");
            System.out.println("");
            System.out.println("Ontology loaded!");

            for (SKOSConceptScheme scheme : dataSet.getSKOSConceptSchemes()) {

                System.out.println("ConceptScheme: " + scheme.getURI());

                // i can get all the concepts from this scheme
                for (SKOSConcept conceptsInScheme : dataSet.getSKOSConcepts()) {

                    System.err.println("\tConcepts: " + conceptsInScheme.getURI());

                    for (SKOSAnnotation anno : conceptsInScheme.getSKOSAnnotations(dataSet)) {
                        System.err.print("\t\tAnnotation: " + anno.getURI() + "-> ");
                        if (anno.isAnnotationByConstant()) {
                            if (anno.getAnnotationValueAsConstant().isTyped()) {
                                SKOSTypedLiteral con = anno.getAnnotationValueAsConstant().getAsSKOSTypedLiteral();
                                System.err.print(con.getLiteral() + " Type: " + con.getDataType().getURI());
                            }
                            else {
                                SKOSUntypedLiteral con = anno.getAnnotationValueAsConstant().getAsSKOSUntypedLiteral();
                                System.err.print(con.getLiteral());
                                if (con.hasLang()) {
                                    System.err.print("@" + con.getLang());
                                }
                            }
                            System.err.println("");
                        }
                        else {
                            System.err.println(anno.getAnnotationValue().getURI().toString());
                        }
                    }

                }

            }
             for (SKOSConceptScheme scheme : dataSet.getSKOSConceptSchemes()) {
                 
             }

//
//                for (SKOSConcept con : scheme.getTopConcepts(vocab)) {
//
//                    System.out.println("Top Concept: " + con.getURI());
//
//                }
//
//                int counter = 0;
//
//                for (SKOSConcept con : scheme.getConceptsInScheme(vocab)) {
//                    counter++;
//                    System.out.println("Concept: " + con.getURI().getFragment());
//
//                    for (OWLUntypedConstant type : con.getSKOSPrefLabel(vocab)) {
//                        System.out.println("PrefLabel: " + type.getLiteral() + " lang: " + type.getLang());
//                    }
//
//                    for (OWLUntypedConstant type : con.getSKOSAltLabel(vocab)) {
//                        System.out.println("AltLabel: " + type.getLiteral() + " lang: " + type.getLang());
//                    }
//
//                    for (SKOSConcept concepts : con.getSKOSBroaderConcepts(vocab)) {
//                        System.out.println("\tHas Broader: " + concepts.getURI().getFragment());
//                    }
//
//                }
//                System.out.println("Count: " + counter);
//            }
//
//
//            // see if we can get and find an entity
//
//            SKOSEntity entity = vocab.getSKOSEntity("conker");
//            System.out.println("Entity lookup for Nose " + entity.asSKOSConcept().getAsOWLIndividual().getURI());
//
////            vocab.getAssertions();
////            vocab.conatinsSKOSConcept();
////            vocab.getSKOSObjectRelationAssertions();
////            vocab.getSKOSBroaderAssertions();
////            vocab.getSKOSNarrowerAssertions();
////            vocab.getSKOSRelatedAssertions();
//
//
        } catch (SKOSCreationException e) {
            e.printStackTrace();
        }
//

    }



    public void dumpHierarchy() {

    }
    
}
