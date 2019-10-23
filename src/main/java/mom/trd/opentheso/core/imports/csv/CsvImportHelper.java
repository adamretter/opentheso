/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.core.imports.csv;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import mom.trd.opentheso.SelectedBeans.rdf4jFileBean;
import mom.trd.opentheso.bdd.datas.Concept;
import mom.trd.opentheso.bdd.datas.ConceptGroupLabel;
import mom.trd.opentheso.bdd.datas.Term;
import mom.trd.opentheso.bdd.datas.Thesaurus;
import mom.trd.opentheso.bdd.helper.AlignmentHelper;
import mom.trd.opentheso.bdd.helper.ConceptHelper;
import mom.trd.opentheso.bdd.helper.GpsHelper;
import mom.trd.opentheso.bdd.helper.GroupHelper;
import mom.trd.opentheso.bdd.helper.NoteHelper;
import mom.trd.opentheso.bdd.helper.RelationsHelper;
import mom.trd.opentheso.bdd.helper.TermHelper;
import mom.trd.opentheso.bdd.helper.ThesaurusHelper;
import mom.trd.opentheso.bdd.helper.UserHelper2;
import mom.trd.opentheso.bdd.helper.nodes.NodeAlignment;
import mom.trd.opentheso.bdd.helper.nodes.NodePreference;

/**
 *
 * @author miled.rousset
 */
public class CsvImportHelper {

    private String message = "";
    private NodePreference nodePreference;

    private String langueSource;
    private String formatDate;
    private String adressSite;
    private int idUser;
    private int idGroupUser;

    private String idDefaultGroup;

    public CsvImportHelper(NodePreference nodePreference) {
        this.nodePreference = nodePreference;
    }

    /**
     * initialisation des paramètres d'import
     *
     * @param formatDate
     * @param idGroupUser
     * @param idUser
     * @param langueSource
     * @return
     */
    public boolean setInfos(
            String formatDate, int idUser,
            int idGroupUser,
            String langueSource) {
        this.formatDate = formatDate;
        this.idUser = idUser;
        this.idGroupUser = idGroupUser;
        this.langueSource = langueSource;
        return true;
    }

    public String getMessage() {
        return message;
    }

    public void addSingleConcept(
            HikariDataSource ds,
            String idTheso,
            String idConceptPere,
            String idGroup,
            int idUser,
            CsvReadHelper.ConceptObject conceptObject) {

        boolean first = true;
        String idConcept = null;
        String idTerm = null;

        // ajout du concept
        ConceptHelper conceptHelper = new ConceptHelper();
        conceptHelper.setNodePreference(nodePreference);
        Concept concept = new Concept();
        TermHelper termHelper = new TermHelper();

        // On vérifie si le conceptPere est un Groupe, alors il faut ajouter un TopTerm, sinon, c'est un concept avec des reraltions
        if (idConceptPere == null) {
            concept.setTopConcept(true);
        } else {
            concept.setTopConcept(false);
        }

        concept.setIdGroup(idGroup);
        concept.setIdThesaurus(idTheso);
        concept.setStatus("");
        concept.setNotation("");
        concept.setIdConcept(conceptObject.getIdConcept());

        Term term = new Term();
        term.setId_thesaurus(idTheso);

        // ajout des PrefLabel
        for (CsvReadHelper.Label prefLabel : conceptObject.getPrefLabels()) {
            if (first) {
                term.setLang(prefLabel.getLang());
                term.setLexical_value(prefLabel.getLabel());
                term.setSource("");
                term.setStatus("");
                idConcept = conceptHelper.addConcept(ds, idConceptPere, "NT", concept, term, idUser);
                if (idConcept == null) {
                    message = message + "\n" + "erreur dans l'intégration du concept " + prefLabel.getLabel();
                }
                conceptObject.setIdConcept(idConcept);
                idTerm = termHelper.getIdTermOfConcept(ds, idConcept, idTheso);
                if (idTerm == null) {
                    message = message + "\n" + "erreur dans l'intégration du concept " + prefLabel.getLabel();
                }
                conceptObject.setIdTerm(idTerm);
                first = false;
            } // ajout des traductions
            else {
                if (idConcept != null) {
                    term.setId_thesaurus(idTheso);
                    term.setLang(prefLabel.getLang());
                    term.setLexical_value(prefLabel.getLabel());
                    term.setId_term(idTerm);
                    term.setContributor(idUser);
                    term.setCreator(idUser);
                    term.setSource("");
                    term.setStatus("");
                    if (!conceptHelper.addConceptTraduction(ds, term, idUser)) {
                        message = message + "\n" + "erreur dans l'intégration du terme " + prefLabel.getLabel();
                    }
                }
            }
        }

        // synonymes et cachés
        addAltLabels(ds, idTheso, conceptObject);

        // notes
        addNotes(ds, idTheso, conceptObject);

        // relations
        addRelations(ds, idTheso, conceptObject);
        
        // alignements
        addAlignments(ds, idTheso, conceptObject);
        
        // géolocalisation
        addGeoLocalisation(ds, idTheso, conceptObject);
    }

    /**
     * permet d'intégrer le thésaurs dans la base de données (d'après un objet
     * lu d'un fichier CSV)
     *
     * @param ds
     * @param fileBean
     * @param thesoName
     * @param conceptObject
     * @param langs
     * @return
     */
    public boolean addTheso(HikariDataSource ds,
            rdf4jFileBean fileBean,
            String thesoName,
            ArrayList<CsvReadHelper.ConceptObject> conceptObject,
            ArrayList<String> langs) {
        // création du thésaurus
        String idTheso = createTheso(ds, thesoName);
        if (idTheso == null) {
            return false;
        }
        addLangsToThesaurus(ds, idTheso, thesoName, langs);
        
        GroupHelper groupHelper = new GroupHelper();
        if (!groupHelper.addGroupDefault(ds, langueSource, idTheso)) {
            return false;
        }
        idDefaultGroup = "orphans";
        for (CsvReadHelper.ConceptObject conceptObject1 : conceptObject) {
            fileBean.setAbs_progress(fileBean.getAbs_progress() + 1);
            fileBean.setProgress(fileBean.getAbs_progress() / fileBean.getTotal() * 100);

            switch (conceptObject1.getType().trim().toLowerCase()) {
                case "skos:concept":
                    // ajout de concept
                    if (!addConcept(ds, idTheso, conceptObject1)) {
                        return false;
                    }
                    break;
    /*           case "":
                    // ajout de concept
                    if (!addConcept(ds, idTheso, conceptObject1)) {
                        return false;
                    }
                    break;*/
                case "skos:collection":
                    // ajout de groupe
                    if (!addGroup(ds, idTheso, conceptObject1)) {
                        return false;
                    }
                    break;
                default:
                    break;
            }
        }
        return true;
    }

    /**
     * Cette fonction permet de créer un thésaurus avec ses traductions (Import)
     * elle retourne l'identifiant du thésaurus, sinon Null
     *
     * @return
     */
    private String createTheso(HikariDataSource ds, String thesoName) {
        try {
            Thesaurus thesaurus = new Thesaurus();

            thesaurus.setCreator("");
            thesaurus.setContributor("");

            ThesaurusHelper thesaurusHelper = new ThesaurusHelper();
            thesaurusHelper.setIdentifierType("2");
            Connection conn = ds.getConnection();
            conn.setAutoCommit(false);
            String idTheso1;
            if (thesaurus.getLanguage() == null) {
                thesaurus.setLanguage(langueSource);
            }
            if ((idTheso1 = thesaurusHelper.addThesaurusRollBack(conn, "", false)) == null) {
                conn.rollback();
                conn.close();
                return null;
            }

            thesaurus.setId_thesaurus(idTheso1);

            if (thesoName.isEmpty()) {
                thesoName = "theso_" + idTheso1;
            }
            thesaurus.setTitle(thesoName);

            if (!thesaurusHelper.addThesaurusTraductionRollBack(conn, thesaurus)) {
                conn.rollback();
                conn.close();
                return null;
            }

            // ajouter le thésaurus dans le group de l'utilisateur
            if (idGroupUser != -1) { // si le groupeUser = - 1, c'est le cas d'un SuperAdmin, alors on n'intègre pas le thésaurus dans un groupUser
                UserHelper2 userHelper = new UserHelper2();
                if (!userHelper.addThesoToGroup(conn, thesaurus.getId_thesaurus(),
                        idGroupUser)) {
                    conn.rollback();
                    conn.close();
                    return null;
                }
            }
            conn.commit();
            conn.close();

            return idTheso1;
        } catch (SQLException ex) {
            Logger.getLogger(CsvImportHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * permet d'ajouter les langues détectées au thésaurus
     *
     * @param ds
     * @param idThesaurus
     * @param langs
     */
    private void addLangsToThesaurus(HikariDataSource ds,
            String idThesaurus,
            String name,
            ArrayList<String> langs) {

        ThesaurusHelper thesaurusHelper = new ThesaurusHelper();
        if (name.isEmpty()) {
            name = "theso_" + idThesaurus;
        }

        for (int i = 0; i < langs.size(); i++) {
            if (!thesaurusHelper.isLanguageExistOfThesaurus(ds, idThesaurus, langs.get(i).trim())) {
                Thesaurus thesaurus1 = new Thesaurus();
                thesaurus1.setId_thesaurus(idThesaurus);
                thesaurus1.setContributor("");
                thesaurus1.setCoverage("");
                thesaurus1.setCreator("");
                thesaurus1.setDescription("");
                thesaurus1.setFormat("");
                thesaurus1.setLanguage(langs.get(i));
                thesaurus1.setPublisher("");
                thesaurus1.setRelation("");
                thesaurus1.setRights("");
                thesaurus1.setSource("");
                thesaurus1.setSubject("");
                thesaurus1.setTitle(name);
                thesaurus1.setType("");
                thesaurusHelper.addThesaurusTraduction(ds, thesaurus1);
            }
        }
    }

    /**
     * Permet d'ajouter les groupes/collections...
     *
     * @param ds
     * @param idTheso
     * @param conceptObject
     * @return
     */
    private boolean addGroup(HikariDataSource ds,
            String idTheso,
            CsvReadHelper.ConceptObject conceptObject) {
        // récupération des groups ou domaine
        GroupHelper groupHelper = new GroupHelper();

        String idGroup = conceptObject.getIdConcept();
        if (idGroup == null || idGroup.isEmpty()) {
            message = message + "\n" + "Identifiant Groupe manquant";
            return false;
        }
        groupHelper.insertGroup(ds, idGroup,
                idTheso, "C",
                conceptObject.getNotation(),
                "", false, idUser);

        ConceptGroupLabel conceptGroupLabel = new ConceptGroupLabel();
        for (CsvReadHelper.Label label : conceptObject.getPrefLabels()) {
            // ajouter les traductions des Groupes
            conceptGroupLabel.setIdgroup(idGroup);
            conceptGroupLabel.setIdthesaurus(idTheso);
            conceptGroupLabel.setLang(label.getLang());
            conceptGroupLabel.setLexicalvalue(label.getLabel());
            groupHelper.addGroupTraduction(ds, conceptGroupLabel, idUser);
        }

        return true;
    }

    private boolean addConcept(HikariDataSource ds,
            String idTheso,
            CsvReadHelper.ConceptObject conceptObject) {

        conceptObject.setIdTerm(conceptObject.getIdConcept());
        
        if (!addPrefLabel(ds, idTheso, conceptObject)) {
            return false;
        }
        // synonymes et cachés
        if (!addAltLabels(ds, idTheso, conceptObject)) {
            return false;
        }
        // notes
        if (!addNotes(ds, idTheso, conceptObject)) {
            return false;
        }
        // relations
        if (!addRelations(ds, idTheso, conceptObject)) {
            return false;
        }
        // alignements
        if (!addAlignments(ds, idTheso, conceptObject)) {
            return false;
        }
        // géolocalisation
        if (!addGeoLocalisation(ds, idTheso, conceptObject)) {
            return false;
        }
        // Membres ou appartenance aux groupes
        if (!addMembers(ds, idTheso, conceptObject)) {
            return false;
        }

        return true;
    }

    private boolean addPrefLabel(
            HikariDataSource ds,
            String idTheso,
            CsvReadHelper.ConceptObject conceptObject) {

        if (conceptObject.getIdConcept()== null || conceptObject.getIdConcept().isEmpty()) {
            message = message + "\n" + "concept sans identifiant";
            return false;
        }

        // ajout du concept
        ConceptHelper conceptHelper = new ConceptHelper();
        TermHelper termHelper = new TermHelper();

        conceptHelper.setNodePreference(nodePreference);
        Concept concept = new Concept();

        // On vérifie si le concept a des BT (termes génériques), alors il faut ajouter un TopTerm, sinon, c'est un concept avec des rerlations
        if (conceptObject.getBroaders().isEmpty()) {
            concept.setTopConcept(true);
        } else {
            concept.setTopConcept(false);
        }

        concept.setIdThesaurus(idTheso);
        concept.setStatus("");
        concept.setNotation(conceptObject.getNotation());
        concept.setIdConcept(conceptObject.getIdConcept());

        // ajout du concept
        if (!conceptHelper.insertConceptInTable(ds, concept, idUser)) {
            message = message + "\n" + "erreur dans l'intégration du concept " + conceptObject.getIdConcept();
        }

        Term term = new Term();
        term.setId_thesaurus(idTheso);
        term.setId_term(conceptObject.getIdConcept());
        term.setContributor(idUser);
        term.setCreator(idUser);
        term.setSource("");
        term.setStatus("");
        Connection conn = null;
        try {
            conn = ds.getConnection();
            conn.setAutoCommit(false);
            // ajout de la relation entre le concept et le terme
            if (!termHelper.addLinkTerm(conn, term, conceptObject.getIdConcept(), idUser)) {
                message = message + "\n" + "erreur dans l'intégration du concept " + conceptObject.getIdConcept();
                conn.rollback();
                conn.close();
                return false;
            }
            conn.commit();
            // ajout des PrefLabel
            for (CsvReadHelper.Label prefLabel : conceptObject.getPrefLabels()) {
                // ajout des traductions
                term.setId_thesaurus(idTheso);
                term.setLang(prefLabel.getLang());
                term.setLexical_value(prefLabel.getLabel());
                term.setId_term(conceptObject.getIdConcept());
                term.setContributor(idUser);
                term.setCreator(idUser);
                term.setSource("");
                term.setStatus("");
                if(!termHelper.addTermTraduction(conn, term, idUser)) {
                    conn.rollback();
                    conn.close();
                    message = message + "\n" + "erreur dans l'intégration du terme " + prefLabel.getLabel();
                    return false;
                }
                conn.commit();
            /*if (!conceptHelper.addConceptTraduction(ds, term, idUser)) {
                    message = message + "\n" + "erreur dans l'intégration du terme " + prefLabel.getLabel();
                }*/
            }
            conn.close();
            
        } catch (SQLException ex) {
            Logger.getLogger(CsvImportHelper.class.getName()).log(Level.SEVERE, null, ex);
            if(conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex1) {
                    Logger.getLogger(CsvImportHelper.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
            return false;
        }
       
        return true;
    }

    /**
     * Intègre les synonymes et synonymes cachés
     *
     * @param ds
     * @param idTheso
     * @param conceptObject
     * @return
     */
    private boolean addAltLabels(
            HikariDataSource ds,
            String idTheso,
            CsvReadHelper.ConceptObject conceptObject) {
        Term term = new Term();
        TermHelper termHelper = new TermHelper();
        for (CsvReadHelper.Label altLabel : conceptObject.getAltLabels()) {
            term.setId_term(conceptObject.getIdTerm());
            term.setId_thesaurus(idTheso);
            term.setLang(altLabel.getLang());
            term.setLexical_value(altLabel.getLabel());
            term.setHidden(false);
            term.setStatus("USE");
            term.setSource("");

            if (!termHelper.addNonPreferredTerm(ds,
                    term, idUser)) {
                message = message + "\n" + "erreur dans l'intégration du synonyme : " + altLabel.getLabel();
            }
        }
        for (CsvReadHelper.Label altLabel : conceptObject.getHiddenLabels()) {
            term.setId_term(conceptObject.getIdTerm());
            term.setId_thesaurus(idTheso);
            term.setLang(altLabel.getLang());
            term.setLexical_value(altLabel.getLabel());
            term.setHidden(true);
            term.setStatus("Hiddden");
            term.setSource("");

            if (!termHelper.addNonPreferredTerm(ds,
                    term, idUser)) {
                message = message + "\n" + "erreur dans l'intégration du synonyme : " + altLabel.getLabel();
            }
        }
        return true;
    }

    /**
     * Intègre les notes
     *
     * @param ds
     * @param idTheso
     * @param conceptObject
     * @return
     */
    private boolean addNotes(
            HikariDataSource ds,
            String idTheso,
            CsvReadHelper.ConceptObject conceptObject) {
        NoteHelper noteHelper = new NoteHelper();
        for (CsvReadHelper.Label note : conceptObject.getDefinitions()) {
            noteHelper.addTermNote(ds, conceptObject.getIdTerm(),
                    note.getLang(),
                    idTheso,
                    note.getLabel(),
                    "definition", idUser);
        }
        for (CsvReadHelper.Label note : conceptObject.getChangeNotes()) {
            noteHelper.addTermNote(ds, conceptObject.getIdTerm(),
                    note.getLang(),
                    idTheso,
                    note.getLabel(),
                    "changeNote", idUser);
        }
        for (CsvReadHelper.Label note : conceptObject.getEditorialNotes()) {
            noteHelper.addTermNote(ds, conceptObject.getIdTerm(),
                    note.getLang(),
                    idTheso,
                    note.getLabel(),
                    "editorialNote", idUser);
        }
        for (CsvReadHelper.Label note : conceptObject.getHistoryNotes()) {
            noteHelper.addTermNote(ds, conceptObject.getIdTerm(),
                    note.getLang(),
                    idTheso,
                    note.getLabel(),
                    "historyNote", idUser);
        }
        for (CsvReadHelper.Label note : conceptObject.getScopeNotes()) {
            noteHelper.addConceptNote(ds, conceptObject.getIdConcept(),
                    note.getLang(),
                    idTheso,
                    note.getLabel(),
                    "scopeNote", idUser);
        }
        for (CsvReadHelper.Label note : conceptObject.getExamples()) {
            noteHelper.addTermNote(ds, conceptObject.getIdTerm(),
                    note.getLang(),
                    idTheso,
                    note.getLabel(),
                    "example", idUser);
        }
        return true;
    }

    private boolean addRelations(
            HikariDataSource ds,
            String idTheso,
            CsvReadHelper.ConceptObject conceptObject) {

        RelationsHelper relationsHelper = new RelationsHelper();

        for (String idConcept2 : conceptObject.getBroaders()) {
            if (!relationsHelper.insertHierarchicalRelation(ds,
                    conceptObject.getIdConcept(),
                    idTheso,
                    "BT",
                    idConcept2)) {
                message = message + "\n" + "erreur dans de la relation BT: " + conceptObject.getIdConcept();
            }
            // pour créer la relation réciproque si elle n'existe pas
            if (!relationsHelper.insertHierarchicalRelation(ds,
                    idConcept2,
                    idTheso,
                    "NT",
                    conceptObject.getIdConcept())) {
                message = message + "\n" + "erreur dans de la relation BT: " + conceptObject.getIdConcept();
            }
        }
        for (String idConcept2 : conceptObject.getNarrowers()) {
            if (!relationsHelper.insertHierarchicalRelation(ds,
                    conceptObject.getIdConcept(),
                    idTheso,
                    "NT",
                    idConcept2)) {
                message = message + "\n" + "erreur dans de la relation NT: " + conceptObject.getIdConcept();
            }
            // pour créer la relation réciproque si elle n'existe pas
            if (!relationsHelper.insertHierarchicalRelation(ds,
                    idConcept2,
                    idTheso,
                    "BT",
                    conceptObject.getIdConcept())) {
                message = message + "\n" + "erreur dans de la relation NT: " + conceptObject.getIdConcept();
            }
        }
        for (String idConcept2 : conceptObject.getRelateds()) {
            if (!relationsHelper.insertHierarchicalRelation(ds,
                    conceptObject.getIdConcept(),
                    idTheso,
                    "RT",
                    idConcept2)) {
                message = message + "\n" + "erreur dans de la relation RT: " + conceptObject.getIdConcept();
            }
//            // pour créer la relation réciproque si elle n'existe pas
            if (!relationsHelper.insertHierarchicalRelation(ds,
                    idConcept2,
                    idTheso,
                    "RT",
                    conceptObject.getIdConcept())) {
                message = message + "\n" + "erreur dans de la relation RT: " + conceptObject.getIdConcept();
            }
        }
        return true;
    }

    private boolean addAlignments(
            HikariDataSource ds,
            String idTheso,
            CsvReadHelper.ConceptObject conceptObject) {

        AlignmentHelper alignmentHelper = new AlignmentHelper();
        NodeAlignment nodeAlignment = new NodeAlignment();
        nodeAlignment.setId_author(idUser);
        nodeAlignment.setConcept_target("");
        nodeAlignment.setThesaurus_target("");
        nodeAlignment.setInternal_id_concept(conceptObject.getIdConcept());
        nodeAlignment.setInternal_id_thesaurus(idTheso);

//        exactMatch   = 1;
//        closeMatch   = 2;
//        broadMatch   = 3;
//        relatedMatch = 4;        
//        narrowMatch  = 5;
        for (String uri : conceptObject.getExactMatchs()) {
            nodeAlignment.setUri_target(uri);
            nodeAlignment.setAlignement_id_type(1);
            if (!alignmentHelper.addNewAlignment(ds, nodeAlignment)) {
                message = message + "\n" + "erreur dans l'ajout de l'alignement : " + conceptObject.getIdConcept();
            }
        }
        for (String uri : conceptObject.getCloseMatchs()) {
            nodeAlignment.setUri_target(uri);
            nodeAlignment.setAlignement_id_type(2);
            if (!alignmentHelper.addNewAlignment(ds, nodeAlignment)) {
                message = message + "\n" + "erreur dans l'ajout de l'alignement : " + conceptObject.getIdConcept();
            }
        }
        for (String uri : conceptObject.getBroadMatchs()) {
            nodeAlignment.setUri_target(uri);
            nodeAlignment.setAlignement_id_type(3);
            if (!alignmentHelper.addNewAlignment(ds, nodeAlignment)) {
                message = message + "\n" + "erreur dans l'ajout de l'alignement : " + conceptObject.getIdConcept();
            }
        }
        for (String uri : conceptObject.getRelatedMatchs()) {
            nodeAlignment.setUri_target(uri);
            nodeAlignment.setAlignement_id_type(4);
            if (!alignmentHelper.addNewAlignment(ds, nodeAlignment)) {
                message = message + "\n" + "erreur dans l'ajout de l'alignement : " + conceptObject.getIdConcept();
            }
        }
        for (String uri : conceptObject.getNarrowMatchs()) {
            nodeAlignment.setUri_target(uri);
            nodeAlignment.setAlignement_id_type(5);
            if (!alignmentHelper.addNewAlignment(ds, nodeAlignment)) {
                message = message + "\n" + "erreur dans l'ajout de l'alignement : " + conceptObject.getIdConcept();
            }
        }

        return true;
    }

    private boolean addGeoLocalisation(
            HikariDataSource ds,
            String idTheso,
            CsvReadHelper.ConceptObject conceptObject) {

        Double latitude;
        Double longitude;

        if (conceptObject.getLatitude() == null || conceptObject.getLatitude().isEmpty()) {
            return true;
        }
        if (conceptObject.getLongitude() == null || conceptObject.getLongitude().isEmpty()) {
            return true;
        }
        try {
            latitude = Double.parseDouble(conceptObject.getLatitude());
            longitude = Double.parseDouble(conceptObject.getLongitude());
        } catch (Exception e) {
            return true;
        }
        GpsHelper gpsHelper = new GpsHelper();
        gpsHelper.insertCoordonees(ds, conceptObject.getIdConcept(),
                idTheso,
                latitude, longitude);
        return true;
    }

    private boolean addMembers(
            HikariDataSource ds,
            String idTheso,
            CsvReadHelper.ConceptObject conceptObject) {

        GroupHelper groupHelper = new GroupHelper();
        if (conceptObject.getMembers().isEmpty()) {
            // ajout dans le groupe par defaut (NoGroup)
            groupHelper.addConceptGroupConcept(ds, idDefaultGroup, conceptObject.getIdConcept(), idTheso);
        } else {
            for (String member : conceptObject.getMembers()) {
                groupHelper.addConceptGroupConcept(ds, member.trim(), conceptObject.getIdConcept(), idTheso);
            }
        }
        return true;
    }
 }
