/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.SelectedBeans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import mom.trd.opentheso.bdd.helper.AlignmentHelper;
import mom.trd.opentheso.bdd.helper.ConceptHelper;
import mom.trd.opentheso.bdd.helper.Connexion;
import mom.trd.opentheso.bdd.helper.TermHelper;
import mom.trd.opentheso.bdd.helper.nodes.NodeAlignment;
import mom.trd.opentheso.core.alignment.AlignementPreferences;
import sun.util.calendar.CalendarUtils;

/**
 *
 * @author antonio.perez
 */
@ManagedBean(name = "alignementparlot", eager = true)
@SessionScoped

public class AlignementParLotBean {

    private static  int optionAllBranch = 0;
    private static  int optionNonAligned = 1;
    private static  int optionWorkFlow = 2;    
    
    
    private AlignementPreferences alignementPreferences;
    private ArrayList<String> listOfChildrenInConcept;
    private ArrayList<String> listConceptTrates = new ArrayList<>();
    private NodeAlignment nodeAli;

    private int alignement_id_type;
    private int position = 0;

    private boolean mettreAJour = true;
    private boolean fin = false;
    private boolean first = true;
    private boolean last = false;
    private boolean addDefinition = false;

    private String uriSelection = null;
    private String nomduterm;
    private String id_concept;
    private String id_theso;
    private String erreur = "";
    private String message = "";
    private String id_term;
    private String id_concept_depart;
    private String uri_manual;

    /////////////Preferences*//////////////
    private int optionOfAlignement = -1;
    private ArrayList<Integer> option;
    private ArrayList<String> optionValue;    
    private Map<Integer,String> options;
    

    @ManagedProperty(value = "#{poolConnexion}")
    private Connexion connect;
    @ManagedProperty(value = "#{selectedTerme}")
    private SelectedTerme selectedTerme;
    @ManagedProperty(value = "#{langueBean}")
    private LanguageBean langueBean;

    /**
     * Permet de savoir combien d'enfants a le concept selectionnée
     *
     * @param id_Theso
     * @param id_Concept
     */
    public void getListChildren(String id_Theso, String id_Concept) {
        reinitTotal();
        initOption();
        id_concept_depart = id_Concept;
        id_concept = id_Concept;
        id_theso = id_Theso;
        AlignmentHelper alignmentHelper = new AlignmentHelper();
        ConceptHelper conceptHelper = new ConceptHelper();
        listOfChildrenInConcept = new ArrayList<>();
        listOfChildrenInConcept = conceptHelper.getIdsOfBranch(
                connect.getPoolConnexion(), id_concept, id_Theso, listOfChildrenInConcept);

        if (listOfChildrenInConcept.isEmpty() || listOfChildrenInConcept.size() == 1) {
            last = true;
        }
        nomduterm = selectedTerme.nom;
    }

    private void initOption() {
    
        options = new LinkedHashMap<>();
        options.put(optionAllBranch, langueBean.getMsg("alig.TTB"));
        options.put(optionNonAligned, langueBean.getMsg("alig.nonA"));
        options.put(optionWorkFlow, langueBean.getMsg("alig.workF"));
    }

    /**
     * Cette fonction permet de passer au concept suivant. et fait l'apelation a
     * la funtion pour créer l'alignement (la funtion apelé est dans
     * selecteTerme
     */
    public void nextPosition() {

        if (fin) {
            return;
        }
        erreur = "";
        AlignmentHelper alignmentHelper = new AlignmentHelper();
        ConceptHelper conceptHelper = new ConceptHelper();
        listConceptTrates.add(id_concept);

        if (optionNonAligned == optionOfAlignement) {
            position++;
            if (position < listOfChildrenInConcept.size()) {
                id_concept = listOfChildrenInConcept.get(position);
            }
            comprobationFin();
            while (alignmentHelper.dejaAligneParAvecCetteAlignement(connect.getPoolConnexion(),
                    id_concept, id_theso, alignement_id_type)) {
                position++;
                if (position < listOfChildrenInConcept.size()) {
                    id_concept = listOfChildrenInConcept.get(position);
                }
                comprobationFin();
            }
            nomduterm = conceptHelper.getLexicalValueOfConcept(connect.getPoolConnexion(), id_concept,
                    selectedTerme.getIdTheso(), selectedTerme.getIdlangue());
        }
        if ((optionAllBranch == optionOfAlignement) || (optionWorkFlow == optionOfAlignement)) {
            position++;
            comprobationFin();
            if (position < listOfChildrenInConcept.size()) {
                id_concept = listOfChildrenInConcept.get(position);
            } else {
                return;
            }

            nomduterm = conceptHelper.getLexicalValueOfConcept(connect.getPoolConnexion(), id_concept,
                    selectedTerme.getIdTheso(), selectedTerme.getIdlangue());

        }
        selectedTerme.creerAlignAuto(id_concept, nomduterm);

    }

    /**
     * Permet de savoir si c'est le fin de l'Arraylist et sortir du dialog
     */
    private void comprobationFin() {

        if (position == listOfChildrenInConcept.size() - 1) {
            last = true;
        }
        if (position == listOfChildrenInConcept.size()) {
            fin = true;
        }

    }

    /**
     * reinicialitation du variables
     */
    public void reinitTotal() {
        listOfChildrenInConcept = null;
        nomduterm = "";
        position = 0;
        fin = false;
        first = true;
        last = false;
        erreur = "";
        message = "";
        optionOfAlignement = -1;
        listConceptTrates.clear();
    }

    /**
     * cherche l'alignement que on a selectionée dans l'arrayList d'alignements
     * et ce fait l'apelation a la funtion pour ajouter l'alignement
     */
    public void addAlignement() {
        erreur = "";
        AlignmentHelper alignmentHelper = new AlignmentHelper();
        TermHelper termHelper = new TermHelper();
            if (uriSelection.isEmpty() && uri_manual.isEmpty()) {
                erreur = "no selected alignment";
                message = "";
            } else {
                if(!uri_manual.trim().isEmpty())
                {
                    nodeAli = new NodeAlignment();
                    nodeAli.setAlignement_id_type(alignement_id_type);
                    nodeAli.setUri_target(uri_manual);
                    nodeAli.setInternal_id_concept(id_concept);
                    nodeAli.setThesaurus_target(selectedTerme.alignementSource.getSource());
                    nodeAli.setConcept_target(nomduterm);
                    id_term = termHelper.getIdTermOfConcept(connect.getPoolConnexion(), id_concept, id_theso);
                    selectedTerme.ajouterAlignAutoByLot(nodeAli, addDefinition, id_term);
                    message += selectedTerme.getMessageAlig();
                    addDefinition = false;
                    nodeAli = null;
                    message += "<br>Concept aligné ...";
                    uriSelection = null;
                    uri_manual = null;
                    nextPosition();
                    return;
                }else
                {
                    for (NodeAlignment nodeAlignment : selectedTerme.getListAlignValues()) {
                        if (nodeAlignment.getUri_target().equals(uriSelection)) {
                            message = "";
                            nodeAli = nodeAlignment;
                            nodeAli.setAlignement_id_type(alignement_id_type);
                            // message = "l'alignement va se faire <br>";
                            id_term = termHelper.getIdTermOfConcept(connect.getPoolConnexion(), id_concept, id_theso);
                            selectedTerme.ajouterAlignAutoByLot(nodeAli, addDefinition, id_term);
                            message += selectedTerme.getMessageAlig();
                            addDefinition = false;
                            nodeAli = null;
                            message += "<br>Concept aligné ...";
                            uriSelection = null;
                            nextPosition();
                            return;
                        }
                    }
                }
        } 
    }

    /**
     * Permet de savoir le premiere element que on besoin montrer
     *
     * @param id_Concept
     * @param id_theso
     * @param id_user
     */
    public void getPreliereElement(String id_Concept, String id_theso, int id_user) {
        if (!"".equals(selectedTerme.selectedAlignement)) {
            getPreferenceAlignement(id_theso, id_user);
            AlignmentHelper alignmentHelper = new AlignmentHelper();
            ConceptHelper conceptHelper = new ConceptHelper();
            if (optionOfAlignement != -1) {
                if (optionNonAligned==optionOfAlignement) {//reprise de non alignes
                    isNonAligne(id_Concept, id_theso, id_user);
                }
                if (optionAllBranch == optionOfAlignement ) {//Mise a jour = true
                    ismiseAJour();
                }
                if (optionWorkFlow == optionOfAlignement) {//reprise de la suite
                    if(alignementPreferences.getId_concept_tratees() == null)
                    {
                        ismiseAJour();
                    }else
                        isSuite();
                }
            } else {
                erreur += "besoin choissi une option";
            }
        } else {
            erreur += "besoin choissi une source";
        }
    }

    public void isSuite() {
        String dejaTratees[];
        boolean trouve = false;
        boolean sort = false;
        ConceptHelper conceptHelper = new ConceptHelper();

        dejaTratees = (alignementPreferences.getId_concept_tratees()).split("#");
        ArrayList<String> conceptFait = new ArrayList<>();
        conceptFait.addAll(Arrays.asList(dejaTratees));
        listConceptTrates = conceptFait;

        if (!conceptFait.isEmpty()) {
            for (String traite : conceptFait) {
                if (listOfChildrenInConcept.contains(traite)) {
                    listOfChildrenInConcept.remove(traite);
                }
            }
        }
        if (listOfChildrenInConcept.isEmpty()) {
            last = true;
            fin = true;
            message = "tout la branche traités";
            return;
        }
        comprobationFin();
        id_concept = listOfChildrenInConcept.get(0);
        nomduterm = conceptHelper.getLexicalValueOfConcept(connect.getPoolConnexion(), listOfChildrenInConcept.get(0),
                selectedTerme.getIdTheso(), selectedTerme.getIdlangue());
        selectedTerme.creerAlignAuto(listOfChildrenInConcept.get(0), nomduterm);
    }

    /**
     * Permet d'avoir le premier element si la option c'est non aligne
     *
     * @param id_Concept
     * @param id_theso
     * @param id_user
     */
    public void isNonAligne(String id_Concept, String id_theso, int id_user) {
        ConceptHelper conceptHelper = new ConceptHelper();
        AlignmentHelper alignmentHelper = new AlignmentHelper();
        if (!alignmentHelper.dejaAligneParAvecCetteAlignement(connect.getPoolConnexion(), id_Concept, id_theso,
                selectedTerme.alignementSource.getId())) {//si n'est pas aligne
            id_concept = listOfChildrenInConcept.get(0);
            nomduterm = conceptHelper.getLexicalValueOfConcept(connect.getPoolConnexion(), listOfChildrenInConcept.get(0),
                    selectedTerme.getIdTheso(), selectedTerme.getIdlangue());
            selectedTerme.creerAlignAuto(listOfChildrenInConcept.get(0), nomduterm);
        } else {//si il est déjà aligne
            nextPosition();
        }
    }

    /**
     * c'est va a faire tout les concept
     */
    public void ismiseAJour() {
        ConceptHelper conceptHelper = new ConceptHelper();
        nomduterm = conceptHelper.getLexicalValueOfConcept(connect.getPoolConnexion(), id_concept,
                selectedTerme.getIdTheso(), selectedTerme.getIdlangue());
        selectedTerme.creerAlignAuto(id_concept, nomduterm);
    }

    //private ArrayList<String> remiseSuite() {
    //}
    /**
     * Recuperation des preferences pour alignement
     *
     * @param idTheso
     * @param id_user
     */
    public void getPreferenceAlignement(String idTheso, int id_user) {
        alignementPreferences = new AlignementPreferences();
        AlignmentHelper alignmentHelper = new AlignmentHelper();
        alignementPreferences = alignmentHelper.getListPreferencesAlignement(
                connect.getPoolConnexion(), idTheso, id_user, id_concept_depart, selectedTerme.alignementSource.getId());
    }

    public void getConceptTratees(String idTheso, int id_user) {
        AlignmentHelper alignmentHelper = new AlignmentHelper();
    }

    /**
     * permet d'ecrire dans la bdd les concept que sont déjà tratées
     *
     * @param id_user
     * @return
     */
    public boolean enregister_Des_Progres(int id_user) {
        AlignmentHelper alignmentHelper = new AlignmentHelper();
        if (optionWorkFlow == optionOfAlignement) {
            if (!alignmentHelper.validate_Preferences(connect.getPoolConnexion(), id_theso, id_user, 
                    id_concept_depart, listConceptTrates, selectedTerme.alignementSource.getId())) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", "Ne peux pas faire uptdate de preferences"));
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, langueBean.getMsg("alig.ok") + " :", ""));
            }
        }
        return true;
    }

    ///////////////GET & SET////////////////
    public Connexion getConnect() {
        return connect;
    }

    public void setConnect(Connexion connect) {
        this.connect = connect;
    }

    public ArrayList<String> getListOfChildrenInConcept() {
        return listOfChildrenInConcept;
    }

    public void setListOfChildrenInConcept(ArrayList<String> listOfChildrenInConcept) {
        this.listOfChildrenInConcept = listOfChildrenInConcept;
    }

    public String getNomduterm() {
        return nomduterm;
    }

    public void setNomduterm(String nomduterm) {
        this.nomduterm = nomduterm;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public SelectedTerme getSelectedTerme() {
        return selectedTerme;
    }

    public void setSelectedTerme(SelectedTerme selectedTerme) {
        this.selectedTerme = selectedTerme;
    }

    public boolean isFin() {
        return fin;
    }

    public void setFin(boolean fin) {
        this.fin = fin;
    }

    public NodeAlignment getNodeAli() {
        return nodeAli;
    }

    public void setNodeAli(NodeAlignment nodeAli) {
        this.nodeAli = nodeAli;
    }

    public String getUriSelection() {
        return uriSelection;
    }

    public void setUriSelection(String uriSelection) {
        this.uriSelection = uriSelection;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public int getAlignement_id_type() {
        return alignement_id_type;
    }

    public void setAlignement_id_type(int alignement_id_type) {
        this.alignement_id_type = alignement_id_type;
    }

    public String getErreur() {
        return erreur;
    }

    public void setErreur(String erreur) {
        this.erreur = erreur;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getId_concept() {
        return id_concept;
    }

    public void setId_concept(String id_concept) {
        this.id_concept = id_concept;
    }

    public boolean isMettreAJour() {
        return mettreAJour;
    }

    public void setMettreAJour(boolean mettreAJour) {
        this.mettreAJour = mettreAJour;
    }

    public boolean isAddDefinition() {
        return addDefinition;
    }

    public void setAddDefinition(boolean addDefinition) {
        this.addDefinition = addDefinition;
    }

    public LanguageBean getLangueBean() {
        return langueBean;
    }

    public void setLangueBean(LanguageBean langueBean) {
        this.langueBean = langueBean;
    }

    public String getId_term() {
        return id_term;
    }

    public void setId_term(String id_term) {
        this.id_term = id_term;
    }

    public ArrayList<String> getListConceptTrates() {
        return listConceptTrates;
    }

    public void setListConceptTrates(ArrayList<String> listConceptTrates) {
        this.listConceptTrates = listConceptTrates;
    }

    public int getOptionOfAlignement() {
        return optionOfAlignement;
    }

    public void setOptionOfAlignement(int optionOfAlignement) {
        this.optionOfAlignement = optionOfAlignement;
    }

    public ArrayList<Integer> getOption() {
        return option;
    }

    public void setOption(ArrayList<Integer> option) {
        this.option = option;
    }

    public ArrayList<String> getOptionValue() {
        return optionValue;
    }

    public void setOptionValue(ArrayList<String> optionValue) {
        this.optionValue = optionValue;
    }

    public Map<Integer, String> getOptions() {
        return options;
    }

    public void setOptions(Map<Integer, String> options) {
        this.options = options;
    }

    public String getUri_manual() {
        return uri_manual;
    }

    public void setUri_manual(String uri_manual) {
        this.uri_manual = uri_manual;
    }


}
