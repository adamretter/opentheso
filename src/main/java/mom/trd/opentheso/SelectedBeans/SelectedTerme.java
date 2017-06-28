package mom.trd.opentheso.SelectedBeans;

import com.k_int.IR.IRQuery;
import com.k_int.IR.QueryModels.PrefixString;
import com.k_int.IR.SearchException;
import com.k_int.IR.SearchTask;
import com.k_int.IR.Searchable;
import com.k_int.IR.TimeoutExceededException;
import com.k_int.hss.HeterogeneousSetOfSearchable;
import mom.trd.opentheso.bdd.tools.AsciiUtils;
//import com.k_int.IR.IRQuery;
//import com.k_int.IR.QueryModels.PrefixString;
//import com.k_int.IR.SearchException;
//import com.k_int.IR.SearchTask;
//import com.k_int.IR.Searchable;
//import com.k_int.IR.TimeoutExceededException;
//import com.k_int.hss.HeterogeneousSetOfSearchable;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import mom.trd.opentheso.core.alignment.AlignmentQuery;
import mom.trd.opentheso.bdd.helper.Connexion;
import mom.trd.opentheso.bdd.datas.Concept;
import mom.trd.opentheso.bdd.datas.ConceptGroupLabel;
import mom.trd.opentheso.bdd.datas.HierarchicalRelationship;
import mom.trd.opentheso.bdd.datas.Term;
import mom.trd.opentheso.bdd.helper.AlignmentHelper;
import mom.trd.opentheso.bdd.helper.GroupHelper;
import mom.trd.opentheso.bdd.helper.ConceptHelper;
import mom.trd.opentheso.bdd.helper.StatisticHelper;
import mom.trd.opentheso.bdd.helper.FacetHelper;
import mom.trd.opentheso.bdd.helper.ImagesHelper;
import mom.trd.opentheso.bdd.helper.NoteHelper;
import mom.trd.opentheso.bdd.helper.OrphanHelper;
import mom.trd.opentheso.bdd.helper.RelationsHelper;
import mom.trd.opentheso.bdd.helper.TermHelper;
import mom.trd.opentheso.bdd.helper.UserHelper;
import mom.trd.opentheso.bdd.helper.GpsHelper;
import mom.trd.opentheso.bdd.helper.nodes.NodeAlignment;
import mom.trd.opentheso.bdd.helper.nodes.NodeAutoCompletion;
import mom.trd.opentheso.bdd.helper.nodes.NodeBT;
import mom.trd.opentheso.bdd.helper.nodes.NodeEM;
import mom.trd.opentheso.bdd.helper.nodes.NodeFacet;
import mom.trd.opentheso.bdd.helper.nodes.NodeFusion;
import mom.trd.opentheso.bdd.helper.nodes.NodeGps;
import mom.trd.opentheso.bdd.helper.nodes.NodeImage;
import mom.trd.opentheso.bdd.helper.nodes.NodeNT;
import mom.trd.opentheso.bdd.helper.nodes.NodePermute;
import mom.trd.opentheso.bdd.helper.nodes.NodeRT;
import mom.trd.opentheso.bdd.helper.nodes.concept.NodeConceptTree;
import mom.trd.opentheso.bdd.helper.nodes.group.NodeGroup;
import mom.trd.opentheso.bdd.helper.nodes.group.NodeGroupTraductions;
import mom.trd.opentheso.bdd.helper.nodes.notes.NodeNote;
import mom.trd.opentheso.bdd.helper.nodes.search.NodeSearch;
import mom.trd.opentheso.bdd.helper.nodes.term.NodeTermTraduction;
import mom.trd.opentheso.bdd.tools.StringPlus;
import mom.trd.opentheso.core.alignment.AlignementSource;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import org.primefaces.model.map.MapModel;

@ManagedBean(name = "selectedTerme", eager = true)
@SessionScoped

public class SelectedTerme implements Serializable {

    private static final long serialVersionUID = 1L;

    public String nom;
    private ArrayList<Entry<String, String>> termeGenerique = new ArrayList<>();
    private ArrayList<Entry<String, String>> termesSpecifique = new ArrayList<>();
    private ArrayList<Entry<String, String>> termesAssocies = new ArrayList<>();
    private ArrayList<String> termesSynonymesE = new ArrayList<>();
    private ArrayList<String> termesSynonymesP = new ArrayList<>();
    private ArrayList<NodeImage> images = new ArrayList<>();
    private ArrayList<NodeAlignment> align = new ArrayList<>();
    private ArrayList<NodeNote> listnotes = new ArrayList<>();

    private TreeNode root;
    private TreeNode selectedNode;
    private NodeAutoCompletion selectedTermComp;
    private boolean allLangue = false;

    private ArrayList<Entry<String, String>> arrayFacette;

    private ArrayList<Entry<String, String>> langues = new ArrayList<>();
    private String note;
    private String definition;
    private String noteApplication;
    private String noteHistorique;
    private String noteEditoriale;

    private ArrayList<NodeNote> nodeNoteTermList;
    private ArrayList<NodeNote> nodeNoteConceptList;

    private String idT;
    private String idC;
    private String status;
    private String notation;
    private String idTheso;
    private String idlangue;
    private String idDomaine;
    private String typeDomaine; // MT, C, Gr ...
    private String idTopConcept;
    private int nbNotices;
    private String urlNotice;
    private String microTheso;
    private String dateC;
    private String dateM;
    private int type; // 1 = domaine/Group, 2 = TT (top Term), 3 = Concept/term  
    private int tree; // 0 pour newtreeBean, 2 pour underTree
    private String idArk;
    private int contributor;
    private int creator;

    private String oldValue;
    private String langEnTraduction;
    private String valueOfTraductionToModify;
    private String valueOfSynonymesToModify;
    private String valueEdit;
    private String valueEdit2;
    private String langueEdit;
    private String nomEdit;
    private String statutEdit;
    private String linkEdit;
    private ArrayList<NodeRT> conceptFusionNodeRT;
    private ArrayList<NodeAlignment> conceptFusionAlign;
    private String conceptFusionId;
    private String choixdesactive;
    //Alignement auto
    private boolean wiki;
    private boolean dbp;
    private boolean agrovoc;
    private boolean gemet;
    private boolean opentheso;
    private String linkOT;
    private String idOT;
    private ArrayList<NodeAlignment> listAlignValues;
    public String selectedAlignement;

    private ArrayList<AlignementSource> alignementSources;
    public AlignementSource alignementSource;
    private NodeSearch nodeSe;
    private NodePermute nodePe;

    private String identifierType = "2";
    public String icon = "+";

    public String messageAlig = "";

    // Variables resourcesBundle
    String cheminNotice1;
    String cheminNotice2;
    private boolean arkActive;
    private String serverAdress;
    private boolean z3950_actif;
    private boolean bdd_active;
    private boolean bdd_useId;

    NodeGps coordonnees;

    private String totalConceptOfBranch;
    private String totalNoticesOfBranch;

    //maps
    private String latitudLongitud = null;
    private MapModel simpleModel = null;

    @ManagedProperty(value = "#{vue}")
    private Vue vue;

    @ManagedProperty(value = "#{user1}")
    private CurrentUser user;

    @ManagedProperty(value = "#{langueBean}")
    private LanguageBean langueBean;

    @ManagedProperty(value = "#{conceptbean}")
    private ConceptBean conceptbean;

    @ManagedProperty(value = "#{poolConnexion}")
    private Connexion connect;

    /**
     * *************************************** INITIALISATION
     * ****************************************
     */
    @PostConstruct
    public void initTerme() {
        if (user == null || user.getNodePreference() == null) {
            return;
        }

        majPref();

        user.setIdTheso(idTheso);
        Integer temp = user.getNodePreference().getIdentifierType();
        identifierType = temp.toString();//bundlePref.getString("identifierType");
        totalConceptOfBranch = "";
        totalNoticesOfBranch = "";

    }

    private void majPref() {
        nodeSe = new NodeSearch();
        images = new ArrayList<>();
        //ResourceBundle bundlePref = getBundlePref();
        cheminNotice1 = user.getNodePreference().getPathNotice1();//bundlePref.getString("pathNotice1");
        cheminNotice2 = user.getNodePreference().getPathNotice2();//bundlePref.getString("pathNotice2");

        arkActive = user.getNodePreference().isUseArk();//bundlePref.getString("useArk").equals("true");

        root = (TreeNode) new DefaultTreeNode("Root", null);
        serverAdress = user.getNodePreference().getCheminSite();//bundlePref.getString("cheminSite");

        z3950_actif = user.getNodePreference().getZ3950acif();//bundlePref.getString("z3950.actif").equals("true");
        bdd_active = user.getNodePreference().isBddActive();//bundlePref.getString("bdd.active").equals("true");
        bdd_useId = user.getNodePreference().isBddUseId();//bundlePref.getString("bdd.useId").equals("true");
    }

    /**
     * Récupération des préférences
     *
     * @return la ressourceBundle des préférences
     */
    private ResourceBundle getBundlePref() {
        FacesContext context = FacesContext.getCurrentInstance();
        ResourceBundle bundlePref = context.getApplication().getResourceBundle(context, "pref");
        return bundlePref;
    }

    /**
     * Vide toutes les informations du terme
     */
    public void reInitTerme() {
        root = (TreeNode) new DefaultTreeNode("Root", null);
        nom = "";
        nomEdit = "";
        termeGenerique = new ArrayList<>();
        termesSpecifique = new ArrayList<>();
        termesAssocies = new ArrayList<>();
        termesSynonymesE = new ArrayList<>();
        termesSynonymesP = new ArrayList<>();
        langues = new ArrayList<>();
        images = new ArrayList<>();
        align = new ArrayList<>();
        note = "";
        definition = "";
        noteApplication = "";
        noteHistorique = "";
        noteEditoriale = "";
        idT = "";
        idC = null;
        status = "";
        notation = "";
        idTheso = "";
        idlangue = "";
        idDomaine = "";
        idTopConcept = "";
        nbNotices = 0;
        urlNotice = "";
        microTheso = "";
        dateC = "";
        dateM = "";
        idArk = "";
        type = 1;

        valueEdit = "";
        langueEdit = "";
        statutEdit = "";

        nodeSe = new NodeSearch();
    }

    /**
     * *************************************** MISE A JOUR
     * ****************************************
     */
    /**
     * Met à jour le terme à la sélection d'un node de l'arbre (traitement
     * différent si Domaine/TopConcept/Concept)
     *
     * @param sN
     */
    public void majTerme(MyTreeNode sN) {

        // contrôler si la connexion est toujour valide 
        // connect.
        reInitTerme();
        majPref();

        idC = sN.getIdMot();
        idTheso = sN.getIdTheso();
        idlangue = sN.getLangue();
        idDomaine = sN.getIdDomaine();
        idTopConcept = sN.getIdTopConcept();
        type = sN.getTypeMot();
        status = "";
        notation = "";
        latitudLongitud = null;
        simpleModel = null;
        totalConceptOfBranch = "";
        totalNoticesOfBranch = "";
        majTAsso();

        GroupHelper groupHelper = new GroupHelper();
        // 1 = domaine/Group, 2 = TT (top Term), 3 = Concept/term 

        if (groupHelper.isIdOfGroup(connect.getPoolConnexion(), idC, idTheso)) {
            microTheso = new GroupHelper().getLexicalValueOfGroup(connect.getPoolConnexion(), idDomaine, idTheso, idlangue);

            NodeGroup ncg = new GroupHelper().getThisConceptGroup(connect.getPoolConnexion(), idC, idTheso, idlangue);
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            nom = ncg.getLexicalValue();
            idArk = ncg.getConceptGroup().getIdARk();
            nomEdit = nom;
            idT = "";
            if (ncg.getCreated() != null && ncg.getModified() != null) {
                dateC = dateFormat.format(ncg.getCreated());
                dateM = dateFormat.format(ncg.getModified());
            }

            majGroupTGen();
            majLangueGroup();
            majTSpeGroup();
            majNotes();

        } else {
            Concept concept = new ConceptHelper().getThisConcept(connect.getPoolConnexion(), idC, idTheso);
            if (concept == null) {
                return;
            }
            status = concept.getStatus();
            notation = concept.getNotation();

            if (!idDomaine.equals("Orphan")) {
                majGroup();
                majTGen();
            } else {
                microTheso = idDomaine;
            }
            Term t = new TermHelper().getThisTerm(connect.getPoolConnexion(), idC, idTheso, idlangue);
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            nom = t.getLexical_value();
            nomEdit = nom;
            idT = t.getId_term();
            dateC = dateFormat.format(t.getCreated());
            dateM = dateFormat.format(t.getModified());

            creator = t.getCreator();
            contributor = t.getContributor();

            images = new ImagesHelper().getImage(connect.getPoolConnexion(), idC, idTheso, user.getUser().getId());
            majNotes();
            majLangueConcept();
            majSyno();
            updateGps();
            majTSpeConcept();
            align = new AlignmentHelper().getAllAlignmentOfConcept(connect.getPoolConnexion(), idC, idTheso);
            //ResourceBundle bundlePref = getBundlePref();
            if (user.getNodePreference().isZ3950actif()) {
                majNoticeZ3950();
            }
            if (bdd_active) {
                majNoticeBdd();
            }

            idArk = new ConceptHelper().getIdArkOfConcept(connect.getPoolConnexion(), idC, idTheso);

            reInitFacette();
            initTree();
        }
    }

    private void majNoticeZ3950() {
        //ResourceBundle bundlePref = getBundlePref();
        if (user.getNodePreference().isZ3950actif()) {
            Properties p = new Properties();
            p.put("CollectionDataSourceClassName", "com.k_int.util.Repository.XMLDataSource");
            p.put("RepositoryDataSourceURL", "file:" + cheminNotice1);
            p.put("XSLConverterConfiguratorClassName", "com.k_int.IR.Syntaxes.Conversion.XMLConfigurator");
            p.put("ConvertorConfigFile", cheminNotice2);
            Searchable federated_search_proxy = new HeterogeneousSetOfSearchable();
            federated_search_proxy.init(p);
            try {
                IRQuery e = new IRQuery();
                //   e.collections = new Vector<String>();
                e.collections.add(user.getNodePreference().getCollectionAdresse());
                e.hints.put("default_element_set_name", "f");
                e.hints.put("small_set_setname", "f");
                e.hints.put("record_syntax", "unimarc");
                e.query = new PrefixString((new StringBuilder("@attrset bib-1 @attr 1=Koha-Auth-Number \"")).append(AsciiUtils.convertNonAscii("" + idC)).append("\"").toString());
                SearchTask st = federated_search_proxy.createTask(e, null);
                st.evaluate(5000);
                nbNotices = st.getTaskResultSet().getFragmentCount();
                for (NodeFusion nf : getFusions()) {
                    if (nf.getIdConcept1().equals(idC)) {
                        String idTe = new TermHelper().getIdTermOfConcept(connect.getPoolConnexion(), nf.getIdConcept2(), idTheso);
                        nbNotices += getNotice(idTe);
                    }
                }

            } catch (TimeoutExceededException | SearchException srch_e) {
                srch_e.printStackTrace();
            }
            urlNotice = user.getNodePreference().getNoticeUrl();
            try {
                //String url_notices = "http://catalogue.frantiq.fr/cgi-bin/koha/opac-search.pl?idx=su%2Cwrdl&q=terme&idx=kw&idx=kw&sort_by=relevance&do=OK";
                urlNotice = urlNotice.replace("terme", URLEncoder.encode("" + idC, user.getNodePreference().getUrlEncode()));
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(SelectedTerme.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public String urlEncodeUtf_8(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(SelectedTerme.class.getName()).log(Level.SEVERE, null, ex);
        }
        return value;
    }

    private void majNoticeBdd() {
        //ResourceBundle bundlePref = getBundlePref();
        nbNotices = 0; //st.getTaskResultSet().getFragmentCount();
        urlNotice = user.getNodePreference().getUrlBdd();
        if (bdd_useId) {
            urlNotice = urlNotice.replace("terme", idC);
        } else {
            urlNotice = urlNotice.replace("terme", nom);
        }
        // try {

        //  urlNotice = URLEncoder.encode(urlNotice);
        // } catch (UnsupportedEncodingException ex) {
        //     Logger.getLogger(SelectedTerme.class.getName()).log(Level.SEVERE, null, ex);
        // }
    }

    private void majTAsso() {
        termesAssocies = new ArrayList<>();
        ArrayList<NodeRT> tempRT = new RelationsHelper().getListRT(connect.getPoolConnexion(), idC, idTheso, idlangue);
        HashMap<String, String> tempMap = new HashMap<>();
        for (NodeRT nrt : tempRT) {
            if (nrt.getStatus().equals("hidden")) {
                tempMap.put(nrt.getIdConcept(), "<del>" + nrt.getTitle() + " (" + nrt.getRole() + ")" + "</del>");
            } else {
                tempMap.put(nrt.getIdConcept(), nrt.getTitle() + " (" + nrt.getRole() + ")");
            }
        }
        termesAssocies.addAll(tempMap.entrySet());
    }

    private void majLangueGroup() {
        langues = new ArrayList<>();
        ArrayList<NodeGroupTraductions> tempNGT = new GroupHelper().getGroupTraduction(connect.getPoolConnexion(), idC, idTheso, idlangue);
        HashMap<String, String> tempMapL = new HashMap<>();
        for (NodeGroupTraductions ngt : tempNGT) {
            tempMapL.put(ngt.getIdLang(), ngt.getTitle());
        }
        langues.addAll(tempMapL.entrySet());
    }

    private void majTSpeGroup() {
        termesSpecifique = new ArrayList<>();
        ConceptHelper ch = new ConceptHelper();
        GroupHelper gh = new GroupHelper();
        ArrayList<NodeConceptTree> tempNT = ch.getListTopConcepts(connect.getPoolConnexion(), idC, idTheso, idlangue);
        for (NodeConceptTree nct : tempNT) {
            HashMap<String, String> tempMap1 = new HashMap<>();
            tempMap1.put(nct.getIdConcept(), nct.getTitle());
            termesSpecifique.addAll(tempMap1.entrySet());
        }
        /*for(String tGroup : ch.getListGroupChildIdOfGroup(connect.getPoolConnexion(), idC, idTheso)){
            
            String value  = gh.getLexicalValueOfGroup(connect.getPoolConnexion(), tGroup, idTheso, idlangue);
            HashMap<String, String> tempMap1 = new HashMap<>();
            tempMap1.put(tGroup, value);
            termesSpecifique.addAll(tempMap1.entrySet());
        }*/

    }

    private void majGroup() {
        ArrayList<String> idGroup = new ConceptHelper().getListGroupIdOfConcept(connect.getPoolConnexion(), idC, idTheso);
        microTheso = new GroupHelper().getLexicalValueOfGroup(connect.getPoolConnexion(), idGroup.get(0), idTheso, idlangue);
        if (idGroup.size() > 1) {
            for (int i = 1; i < idGroup.size(); i++) {
                microTheso += ", " + new GroupHelper().getLexicalValueOfGroup(connect.getPoolConnexion(), idGroup.get(i), idTheso, idlangue);
            }
        }
    }

    private void initNotes() {
        noteEditoriale = "";
        definition = "";
        noteHistorique = "";
        noteApplication = "";
        note = "";
    }

    private void majNotes() {
        // NodeNote contient la note avec le type de note, il faut filtrer pour trouver la bonne note
        // For Concept : customnote ; scopeNote ; historyNote
        // For Term : definition; editorialNote; historyNote; 
        initNotes();
        nodeNoteTermList = new NoteHelper().getListNotesTerm(connect.getPoolConnexion(), idT, idTheso, idlangue);
        for (NodeNote nodeNoteList1 : nodeNoteTermList) {
            if (nodeNoteList1 != null) {
                // cas d'une noteEditoriale
                if (nodeNoteList1.getNotetypecode().equalsIgnoreCase("editorialNote")) {
                    if (nodeNoteList1.getLexicalvalue() != null) {
                        noteEditoriale = nodeNoteList1.getLexicalvalue();
                    }
                }
                // cas de definitionNote
                if (nodeNoteList1.getNotetypecode().equalsIgnoreCase("definition")) {
                    if (nodeNoteList1.getLexicalvalue() != null) {
                        definition = nodeNoteList1.getLexicalvalue();
                    }
                }
                // cas de HistoryNote
                if (nodeNoteList1.getNotetypecode().equalsIgnoreCase("historyNote")) {
                    if (nodeNoteList1.getLexicalvalue() != null) {
                        noteHistorique = nodeNoteList1.getLexicalvalue();
                    }
                }
            }
        }
        nodeNoteConceptList = new NoteHelper().getListNotesConcept(connect.getPoolConnexion(), idC, idTheso, idlangue);
        for (NodeNote nodeNoteList1 : nodeNoteConceptList) {
            if (nodeNoteList1 != null) {
                // cas de Note d'application
                if (nodeNoteList1.getNotetypecode().equalsIgnoreCase("scopeNote")) {
                    if (nodeNoteList1.getLexicalvalue() != null) {
                        noteApplication = nodeNoteList1.getLexicalvalue();
                    }
                }
                // cas de HistoryNote
                if (nodeNoteList1.getNotetypecode().equalsIgnoreCase("historyNote")) {
                    if (nodeNoteList1.getLexicalvalue() != null) {
                        noteHistorique = nodeNoteList1.getLexicalvalue();
                    }
                }
                // cas de Note
                if (nodeNoteList1.getNotetypecode().equalsIgnoreCase("note")) {
                    if (nodeNoteList1.getLexicalvalue() != null) {
                        note = nodeNoteList1.getLexicalvalue();
                    }
                }
            }
        }
    }

    public void majLangueConcept() {
        langues = new ArrayList<>();
        ArrayList<NodeTermTraduction> tempNTT = new TermHelper().getTraductionsOfConcept(connect.getPoolConnexion(), idC, idTheso, idlangue);
        HashMap<String, String> tempMapL = new HashMap<>();
        for (NodeTermTraduction ntt : tempNTT) {
            tempMapL.put(ntt.getLang(), ntt.getLexicalValue());
        }
        langues.addAll(tempMapL.entrySet());
    }

    public void updateGps() {
        GpsBeans gps = new GpsBeans();
        coordonnees = new NodeGps();
        GpsHelper gpsHelper = new GpsHelper();
        coordonnees = gpsHelper.getCoordinate(connect.getPoolConnexion(), idC, idTheso);
        if (coordonnees == null) {
            latitudLongitud = null;
            gps.latitud = 0.0;
            gps.longitud = 0.0;
            return;
        }
        gps.latitud = coordonnees.getLatitude();
        gps.longitud = coordonnees.getLongitude();
        gps.reinitBoolean();
        latitudLongitud = "" + coordonnees.getLatitude() + "," + coordonnees.getLongitude();

    }

    private void majSyno() {
        termesSynonymesE = new ArrayList<>();
        termesSynonymesP = new ArrayList<>();
        ArrayList<NodeEM> tempEM = new TermHelper().getNonPreferredTerms(connect.getPoolConnexion(), idT, idTheso, idlangue);
        for (NodeEM nem : tempEM) {
            if (nem.getStatus().equalsIgnoreCase("USE")) {
                termesSynonymesE.add(nem.getLexical_value());
            } else {
                termesSynonymesP.add(nem.getLexical_value());
            }
        }
    }

    private void majTSpeConcept() {
        termesSpecifique = new ArrayList<>();
        ArrayList<NodeNT> tempNT = new RelationsHelper().getListNT(connect.getPoolConnexion(), idC, idTheso, idlangue);
        writeTermesSpecifique(tempNT);
    }

    private void writeTermesSpecifique(ArrayList<NodeNT> tempNT) {
        for (NodeNT nnt : tempNT) {
            HashMap<String, String> tempMap1 = new HashMap<>();
            if (nnt.getStatus().equals("hidden")) {
                tempMap1.put(nnt.getIdConcept(), "<del>" + nnt.getTitle() + " (" +nnt.getRole() +")" + "</del>");
            } else {
                tempMap1.put(nnt.getIdConcept(), nnt.getTitle() + " (" +nnt.getRole() +")");
            }
            termesSpecifique.addAll(tempMap1.entrySet());
        }
    }

    public void majTSpeConceptOrder() {
        termesSpecifique = new ArrayList<>();
        ArrayList<NodeNT> tempNT = new RelationsHelper().getListNTOrderByDate(connect.getPoolConnexion(), idC, idTheso, idlangue);
        writeTermesSpecifique(tempNT);
    }

    private void majTGen() {
        termeGenerique = new ArrayList<>();
        /*// On ajoute le domaine
        ArrayList<String> listIdGroup = new ConceptHelper().getListGroupIdOfConcept(connect.getPoolConnexion(), idC, idTheso);
        HashMap<String, String> tempMap = new HashMap<>();
        for (String group : listIdGroup) {
            tempMap.put(group, new GroupHelper().getLexicalValueOfGroup(connect.getPoolConnexion(), group, idTheso, idlangue));
        }
        termeGenerique.addAll(tempMap.entrySet());*/

        ArrayList<NodeBT> tempBT = new RelationsHelper().getListBT(connect.getPoolConnexion(), idC, idTheso, idlangue);
        for (NodeBT nbt : tempBT) {
            HashMap<String, String> tempMap2 = new HashMap<>();
            if (nbt.getStatus().equals("hidden")) {
                tempMap2.put(nbt.getIdConcept(), "<del>" + nbt.getTitle() + " (" + nbt.getRole() +")" + "</del>");
            } else {
                tempMap2.put(nbt.getIdConcept(), nbt.getTitle() + " (" + nbt.getRole() +")");
            }
            termeGenerique.addAll(tempMap2.entrySet());
        }
    }

    private void majGroupTGen() {
        termeGenerique = new ArrayList<>();
        // On ajoute le domaine
        ArrayList<String> listIdGroup = new ConceptHelper().getListGroupParentIdOfGroup(connect.getPoolConnexion(), idC, idTheso);
        HashMap<String, String> tempMap = new HashMap<>();
        /*for (String group : listIdGroup) {
            tempMap.put(group, new GroupHelper().getLexicalValueOfGroup(connect.getPoolConnexion(), group, idTheso, idlangue));
        }
        termeGenerique.addAll(tempMap.entrySet());*/

        ArrayList<NodeBT> tempBT = new RelationsHelper().getListBT(connect.getPoolConnexion(), idC, idTheso, idlangue);
        for (NodeBT nbt : tempBT) {
            HashMap<String, String> tempMap2 = new HashMap<>();
            if (nbt.getStatus().equals("hidden")) {
                tempMap2.put(nbt.getIdConcept(), "<del>" + nbt.getTitle() + " (" + nbt.getRole() +")" + "</del>");
            } else {
                tempMap2.put(nbt.getIdConcept(), nbt.getTitle() + " (" + nbt.getRole() +")");
            }
            termeGenerique.addAll(tempMap2.entrySet());
        }
    }

    public void majSearch() {
        if (nodeSe.isTopConcept()) {
            type = 2;
        } else {
            type = 3;
        }
        MyTreeNode mtn = new MyTreeNode(type, nodeSe.getIdConcept(), nodeSe.getIdThesaurus(),
                nodeSe.getIdLang(), nodeSe.getIdGroup(), nodeSe.getTypeGroup(), null, null, nodeSe.getLexical_value(), null);
        majTerme(mtn);
    }

    public void majIndexRapidSearch(String idThesaurus, String idLangue) {
        if (nodeSe == null) {
            return;
        }

        //cas du premier chargement du thésaurus 
        if (nodeSe.getIdConcept() == null) {
            idTheso = idThesaurus;
        }
        if (idlangue.isEmpty()) {
            idlangue = idLangue;
        }
        if (nodeSe.isTopConcept()) {
            type = 2;
        } else {
            type = 3;
        }
        String idConcept = getSelectedTermComp().getIdConcept();
        MyTreeNode mtn = new MyTreeNode(type,
                idConcept, idTheso, idlangue, idDomaine,
                typeDomaine, null, null, getSelectedTermComp(), null);
        //     nodeSe.getIdConcept(), nodeSe.getIdThesaurus(), nodeSe.getIdLang(), nodeSe.getIdGroup(), null, null, nodeSe.getLexical_value(), null);
        majTerme(mtn);
    }

    public void majSearchPermute() {
        if (new ConceptHelper().isTopConcept(connect.getPoolConnexion(), nodePe.getIdConcept(), idTheso, nodePe.getIdGroup())) {
            type = 2;
        } else {
            type = 3;
        }
        String value = (nodePe.getFirstColumn() + " " + nodePe.getSearchedValue() + " " + nodePe.getLastColumn()).trim();
        MyTreeNode mtn = new MyTreeNode(type, nodePe.getIdConcept(), nodePe.getIdThesaurus(), nodePe.getIdLang(), nodePe.getIdGroup(),
                "", null, null, value, null);
        majTerme(mtn);
    }

    /**
     * cette fonction permet de savoir si un term existe à l'identique, si oui,
     * on a son identifiant, sinon, un null
     *
     * @param term
     * @return null or idTerm
     */
    public String isTermExist(String term) {
        TermHelper termHelper = new TermHelper();
        return termHelper.isTermEqualTo(connect.getPoolConnexion(), term, idTheso, idlangue);
    }

    public String getIdConceptOf(String idTerm) {
        ConceptHelper conceptHelper = new ConceptHelper();
        return conceptHelper.getIdConceptOfTerm(connect.getPoolConnexion(), idTerm, idTheso);
    }

    public boolean isCreateAuthorizedForTS(String idConceptNew) {
        RelationsHelper relationsHelper = new RelationsHelper();

        return false;
    }

    /**
     * *************************************** CREATION
     * ****************************************
     */
    /**
     * Crée un nouveau terme spécifique au terme sélectionné
     *
     * @param selecedTerm
     * @return true or false
     */
    public boolean creerTermeSpe(MyTreeNode selecedTerm) {
        ConceptHelper instance = new ConceptHelper();
        instance.setIdentifierType(identifierType);
        // 1 = domaine/Group, 2 = TT (top Term), 3 = Concept/term  
        if (selecedTerm.isIsSubGroup() || selecedTerm.isIsGroup()) {
            // ici c'est le cas d'un Group ou Sous Group, on crée un TT Top Terme
            Concept concept = new Concept();
            concept.setIdGroup(selecedTerm.getIdMot());
            concept.setIdThesaurus(idTheso);
            concept.setStatus("D");
            concept.setNotation("");

            Term terme = new Term();
            terme.setId_thesaurus(idTheso);
            terme.setLang(idlangue);
            terme.setLexical_value(valueEdit);
            terme.setSource("");
            terme.setStatus("");

            if (instance.addTopConcept(connect.getPoolConnexion(), idTheso, concept, terme, serverAdress, arkActive, user.getUser().getId()) == null) {
                return false;
            }
            instance.insertID_grouptoPermuted(connect.getPoolConnexion(), concept.getIdThesaurus(), concept.getIdConcept());
            ConceptHelper ch = new ConceptHelper();

            ArrayList<NodeConceptTree> tempNT = ch.getListTopConcepts(connect.getPoolConnexion(), idC, idTheso, idlangue);
            termesSpecifique = new ArrayList<>();
            HashMap<String, String> tempMap = new HashMap<>();
            for (NodeConceptTree nct : tempNT) {
                tempMap.put(nct.getIdConcept(), nct.getTitle()  );
            }
            termesSpecifique.addAll(tempMap.entrySet());

        } else {

            Concept concept = new Concept();
            concept.setIdGroup(selecedTerm.getIdDomaine());
            concept.setIdThesaurus(idTheso);
            concept.setStatus("D");
            concept.setNotation("");

            Term terme = new Term();
            terme.setId_thesaurus(idTheso);
            terme.setLang(idlangue);
            terme.setLexical_value(valueEdit);
            terme.setSource("");
            terme.setStatus("");

            //String idTC = idTopConcept;
            String idP = idC;

            if (instance.addConcept(connect.getPoolConnexion(), idP, concept, terme, serverAdress, arkActive, user.getUser().getId()) == null) {
                return false;
            }
            instance.insertID_grouptoPermuted(connect.getPoolConnexion(), concept.getIdThesaurus(), concept.getIdConcept());
            concept.getUserName();
            ArrayList<NodeNT> tempNT = new RelationsHelper().getListNT(connect.getPoolConnexion(), idC, idTheso, idlangue);
            termesSpecifique = new ArrayList<>();
            HashMap<String, String> tempMap = new HashMap<>();
            for (NodeNT nnt : tempNT) {
                tempMap.put(nnt.getIdConcept(), nnt.getTitle()  + " (" +nnt.getRole() +")");
            }
            termesSpecifique.addAll(tempMap.entrySet());
        }
        vue.setAddTSpe(false);
        valueEdit = "";
        return true;
    }

    /**
     * relation special type NTG NTP NTI ...
     *
     * @param selecedTerm
     * @return
     */
    public boolean creerSpecialTermeSpe(MyTreeNode selecedTerm,String BTname, String NTname) {
        ConceptHelper instance = new ConceptHelper();
        instance.setIdentifierType(identifierType);

        Concept concept = new Concept();
        concept.setIdGroup(selecedTerm.getIdDomaine());
        concept.setIdThesaurus(idTheso);
        concept.setStatus("D");
        concept.setNotation("");

        Term terme = new Term();
        terme.setId_thesaurus(idTheso);
        terme.setLang(idlangue);
        terme.setLexical_value(valueEdit);
        terme.setSource("");
        terme.setStatus("");

        //String idTC = idTopConcept;
        String idP = idC;
             
        if (instance.addConceptSpecial(connect.getPoolConnexion(), idP, concept, terme,BTname,NTname, serverAdress, arkActive, user.getUser().getId()) == null) {
            return false;
        }
        instance.insertID_grouptoPermuted(connect.getPoolConnexion(), concept.getIdThesaurus(), concept.getIdConcept());
        concept.getUserName();
        ArrayList<NodeNT> tempNT = new RelationsHelper().getListNT(connect.getPoolConnexion(), idC, idTheso, idlangue);
        termesSpecifique = new ArrayList<>();
        HashMap<String, String> tempMap = new HashMap<>();
        for (NodeNT nnt : tempNT) {
            tempMap.put(nnt.getIdConcept(), nnt.getTitle() + " (" +nnt.getRole() +")");
        }
        termesSpecifique.addAll(tempMap.entrySet());

        vue.setAddTSpe(false);
        valueEdit = "";
        return true;
    }

    public void creerTermeSyno() {
        if (valueEdit == null || valueEdit.trim().equals("")) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("sTerme.error1")));
        } else if (idT != null && !idT.equals("")) {
            if (new TermHelper().isTermExist(connect.getPoolConnexion(), valueEdit, idTheso, idlangue)) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("sTerme.error6")));
                return;
            }
            String leNom = valueEdit;
            Term temp = new Term();
            temp.setId_term(idT);
            temp.setId_thesaurus(idTheso);
            temp.setLang(idlangue);
            temp.setLexical_value(valueEdit);
            if (statutEdit.equalsIgnoreCase("Hidden")) {
                temp.setHidden(true);
            } else {
                temp.setHidden(false);
            }
            temp.setStatus(statutEdit);
            temp.setSource(String.valueOf(user.getUser().getName()));
            if (!new TermHelper().addNonPreferredTerm(connect.getPoolConnexion(), temp, user.getUser().getId())) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("error")));
                return;
            }

            ArrayList<NodeEM> tempEM = new TermHelper().getNonPreferredTerms(connect.getPoolConnexion(), idT, idTheso, idlangue);
            termesSynonymesE = new ArrayList<>();
            termesSynonymesP = new ArrayList<>();
            latitudLongitud = null;
            for (NodeEM nem : tempEM) {
                if (nem.getStatus().equalsIgnoreCase("USE")) {
                    termesSynonymesE.add(nem.getLexical_value());
                }
                if (nem.getStatus().equalsIgnoreCase("Hidden")) {
                    termesSynonymesP.add(nem.getLexical_value());
                }
                if (nem.getLexical_value().contains("WKT:")) {
                    latitudLongitud = nem.getLexical_value().substring(nem.getLexical_value().indexOf(":") + 1,
                            nem.getLexical_value().length()).trim();

                }
            }
            valueEdit = "";
            nomEdit = "";
            vue.setAddTSyno(0);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", leNom + " " + langueBean.getMsg("sTerme.info1")));
        } else {
            vue.setAddTSyno(0);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("sTerme.error2")));
        }
    }

    /**
     * Ajoute une relation terme associé au concept courant
     *
     * @param idC2 le concept associé
     */
    public void creerTermeAsso(String idC2) {
        HierarchicalRelationship hr = new HierarchicalRelationship();
        hr.setIdConcept1(idC);
        hr.setIdConcept2(idC2);
        hr.setIdThesaurus(idTheso);
        hr.setRole("RT");
        new ConceptHelper().addAssociativeRelation(connect.getPoolConnexion(), hr, user.getUser().getId());
        ArrayList<NodeRT> tempRT = new RelationsHelper().getListRT(connect.getPoolConnexion(), idC, idTheso, idlangue);
        termesAssocies = new ArrayList<>();
        HashMap<String, String> tempMap = new HashMap<>();
        for (NodeRT nrt : tempRT) {
            tempMap.put(nrt.getIdConcept(), nrt.getTitle() + " (" + nrt.getRole() + ")");
        }
        termesAssocies.addAll(tempMap.entrySet());
        vue.setAddTAsso(0);
    }
    public void creerTermeAsso(String idC2,String role) {
        HierarchicalRelationship hr = new HierarchicalRelationship();
        hr.setIdConcept1(idC);
        hr.setIdConcept2(idC2);
        hr.setIdThesaurus(idTheso);
        hr.setRole(role);
        new ConceptHelper().addAssociativeRelation(connect.getPoolConnexion(), hr, user.getUser().getId());
        ArrayList<NodeRT> tempRT = new RelationsHelper().getListRT(connect.getPoolConnexion(), idC, idTheso, idlangue);
        termesAssocies = new ArrayList<>();
        HashMap<String, String> tempMap = new HashMap<>();
        for (NodeRT nrt : tempRT) {
            tempMap.put(nrt.getIdConcept(), nrt.getTitle() + " (" + nrt.getRole() + ")");
        }
        termesAssocies.addAll(tempMap.entrySet());
        vue.setAddTAsso(0);
    }

    /**
     * Cette fonction permet d'ajouter un groupe à une branche
     *
     * @param newGroup
     * @param idConcept
     * @return
     */
    public boolean addBranchGroup(ArrayList<String> newGroup, String idConcept) {
        try {
            Connection conn = connect.getPoolConnexion().getConnection();
            conn.setAutoCommit(false);

            if (new ConceptHelper().haveChildren(connect.getPoolConnexion(), idTheso, idConcept)) {
                List<NodeNT> children = new RelationsHelper().getListNT(connect.getPoolConnexion(), idConcept, idTheso, "");
                for (NodeNT nnt : children) {
                    if (!addBranchGroup(newGroup, nnt.getIdConcept())) {
                        conn.rollback();
                        conn.close();
                        return false;
                    }
                }
            }
            for (String s : newGroup) {
                if (!new ConceptHelper().haveThisGroup(connect.getPoolConnexion(), idConcept, s, idTheso)) {
                    Concept c = new Concept();
                    c.setIdConcept(idConcept);
                    c.setIdGroup(s);
                    c.setIdThesaurus(idTheso);
                    c.setTopConcept(false);
                    c.setStatus("D");
                    if (!new ConceptHelper().addNewGroupOfConcept(conn, c, user.getUser().getId())) {
                        conn.rollback();
                        conn.close();
                        return false;
                    }
                }
            }
            conn.commit();
            conn.close();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(SelectedTerme.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     * Fonction pour trouver le nombre des concepts dans une branche
     *
     * @return
     */
    public String getTheNbConceptOfBranch() {
        if (totalConceptOfBranch.isEmpty()) {
            StatisticHelper statisticHelper = new StatisticHelper();
            int tot = statisticHelper.getConceptCountOfBranch(connect.getPoolConnexion(),
                    idC, idTheso);
            totalConceptOfBranch = "" + tot;
        }
        return totalConceptOfBranch;
    }

    public String getNbNoticesOfBranch() {
        //ResourceBundle bundlePref = getBundlePref();
        int total = 0;
        if (totalNoticesOfBranch.isEmpty()) {
            if (z3950_actif) {
                ConceptHelper conceptHelper = new ConceptHelper();
                ArrayList<String> lisIds = new ArrayList();
                lisIds = conceptHelper.getIdsOfBranch(connect.getPoolConnexion(), idC, idTheso, lisIds);

                Properties p = new Properties();
                p.put("CollectionDataSourceClassName", "com.k_int.util.Repository.XMLDataSource");
                p.put("RepositoryDataSourceURL", "file:" + cheminNotice1);
                p.put("XSLConverterConfiguratorClassName", "com.k_int.IR.Syntaxes.Conversion.XMLConfigurator");
                p.put("ConvertorConfigFile", cheminNotice2);
                Searchable federated_search_proxy = new HeterogeneousSetOfSearchable();
                federated_search_proxy.init(p);
                try {
                    IRQuery e = new IRQuery();
                    //   e.collections = new Vector<String>();
                    e.collections.add(user.getNodePreference().getCollectionAdresse());
                    e.hints.put("default_element_set_name", "f");
                    e.hints.put("small_set_setname", "f");
                    e.hints.put("record_syntax", "unimarc");
                    for (String idConcept : lisIds) {
                        e.query = new PrefixString((new StringBuilder("@attrset bib-1 @attr 1=Koha-Auth-Number \"")).append(AsciiUtils.convertNonAscii("" + idConcept)).append("\"").toString());
                        SearchTask st = federated_search_proxy.createTask(e, null);
                        st.evaluate(5000);
                        total = total + st.getTaskResultSet().getFragmentCount();
                    }

                } catch (TimeoutExceededException | SearchException srch_e) {
                    srch_e.printStackTrace();
                }
                totalNoticesOfBranch = "" + total;
            }
        }
        return totalNoticesOfBranch;
    }

    /**
     * $$$$$ deprecated $$$$$$ Ajoute une relation terme générique au concept
     * courant
     *
     * @param idCBT
     * @return true or false
     */
    public boolean creerTermeGene(String idCBT) {
        if (termeGenerique.isEmpty()) {
            try {
                // Le concept était orphelin
                Connection conn = connect.getPoolConnexion().getConnection();
                conn.setAutoCommit(false);

                ArrayList<String> newGroup = new ConceptHelper().getListGroupIdOfConcept(connect.getPoolConnexion(), idCBT, idTheso);
                for (String s : newGroup) {
                    Concept c = new Concept();
                    c.setIdConcept(idC);
                    c.setIdGroup(s);
                    c.setIdThesaurus(idTheso);
                    c.setStatus("D");
                    if (!new ConceptHelper().insertConceptInTableRollBack(
                            conn, c, urlNotice, arkActive, user.getUser().getId())) {
                        conn.rollback();
                        conn.close();
                        return false;
                    };
                }
                if (!new OrphanHelper().deleteOrphan(conn, idC, idTheso)) {
                    conn.rollback();
                    conn.close();
                    return false;
                }
                conn.commit();
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(SelectedTerme.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }

        } else {
            // On ajoute les nouveaux domaines s'il y en a
            ArrayList<String> groupBT = new ConceptHelper().getListGroupIdOfConcept(connect.getPoolConnexion(), idCBT, idTheso);
            ArrayList<String> groupCurrent = new ConceptHelper().getListGroupIdOfConcept(connect.getPoolConnexion(), idC, idTheso);
            ArrayList<String> newGroup = new ArrayList<>();
            for (String s : groupBT) {
                if (!groupCurrent.contains(s)) {
                    newGroup.add(s);
                }
            }
            if (!addBranchGroup(newGroup, idC)) {
                return false;
            }
        }

        try {
            Connection conn = connect.getPoolConnexion().getConnection();
            conn.setAutoCommit(false);
            //On crée les relations
            if (!new RelationsHelper().addRelationBT(conn, idC, idTheso, idCBT, user.getUser().getId())) {
                conn.rollback();
                conn.close();
                return false;
            }
            conn.commit();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(SelectedTerme.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        termeGenerique = new ArrayList<>();
        majTGen();
        vue.setAddTGen(0);
        return true;
    }

    /**
     * Ajoute une relation terme générique au concept courant
     *
     * @param idNT
     * @param idBT
     * @return true or false
     */
    public boolean addTermeGene(String idNT, String idBT) {
        try {
            Connection conn = connect.getPoolConnexion().getConnection();
            conn.setAutoCommit(false);
            if (termeGenerique.isEmpty()) {
                // c'était un orphelin
                if (!new OrphanHelper().deleteOrphan(conn, idNT, idTheso)) {
                    conn.rollback();
                    conn.close();
                    return false;
                }
            }

            //On ajoute la realtion BT au concept
            if (!new RelationsHelper().addRelationBT(conn, idNT, idTheso, idBT, user.getUser().getId())) {
                conn.rollback();
                conn.close();
                return false;
            }
            conn.commit();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(SelectedTerme.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        termeGenerique = new ArrayList<>();
        majTGen();
        vue.setAddTGen(0);
        return true;
    }

    /**
     * Ajoute une relation terme générique au concept courant
     *
     * @param idConcept
     * @param idGroup
     * @param idNewConceptBT
     * @return true or false
     */
    public boolean addTermeGeneOtherGroup(String idConcept, String idGroup, String idNewConceptBT) {

        ConceptHelper conceptHelper = new ConceptHelper();
        RelationsHelper relationsHelper = new RelationsHelper();
        GroupHelper groupHelper = new GroupHelper();

        String idNewGroup = conceptHelper.getGroupIdOfConcept(connect.getPoolConnexion(), idNewConceptBT, idTheso);
        if (idNewGroup == null) {
            return false;
        }

        try {
            Connection conn = connect.getPoolConnexion().getConnection();
            conn.setAutoCommit(false);
            if (termeGenerique.isEmpty()) {
                // c'était un orphelin
                if (!new OrphanHelper().deleteOrphan(conn, idConcept, idTheso)) {
                    conn.rollback();
                    conn.close();
                    return false;
                }
            }

            //On ajoute la realtion BT au concept
            if (!relationsHelper.addRelationBT(conn, idConcept, idTheso, idNewConceptBT, user.getUser().getId())) {
                conn.rollback();
                conn.close();
                return false;
            }

            // on récupère les Ids des concepts à modifier 
            ArrayList<String> lisIds = new ArrayList<>();
            lisIds = conceptHelper.getIdsOfBranch(connect.getPoolConnexion(), idConcept, idTheso, lisIds);

            // on ajoute le nouveau domaine à la branche
            if (!groupHelper.addDomainToBranch(conn, lisIds, idNewGroup, idTheso, user.getUser().getId())) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("error") + " :", langueBean.getMsg("error")));
                conn.rollback();
                conn.close();
                return false;
            }

            conn.commit();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(SelectedTerme.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        termeGenerique = new ArrayList<>();
        majTGen();
        return true;
    }

    /**
     * Corrigé par M.R. Ajoute une relation terme spécifique au concept courant
     *
     * @param idCNT
     * @return true or false
     */
    public boolean creerTermeSpe(String idCNT) {
        ConceptHelper conceptHelper = new ConceptHelper();
        conceptHelper.setIdentifierType(identifierType);

        if (new OrphanHelper().isOrphan(connect.getPoolConnexion(), idCNT, idTheso)) {
            try {
                Connection conn = connect.getPoolConnexion().getConnection();
                conn.setAutoCommit(false);

                ArrayList<String> newGroup = conceptHelper.getListGroupIdOfConcept(connect.getPoolConnexion(), idC, idTheso);
                for (String s : newGroup) {
                    Concept c = new Concept();
                    c.setIdConcept(idCNT);
                    c.setIdGroup(s);
                    c.setIdThesaurus(idTheso);
                    c.setStatus("D");

                    String idConcept = conceptHelper.addConceptInTable(conn, c, user.getUser().getId());
                    // si ça se passe mal, on ajoute rien;
                    if (idConcept == null) {
                        conn.rollback();
                        conn.close();
                        return false;
                    }

                }
                if (!new OrphanHelper().deleteOrphan(conn, idCNT, idTheso)) {
                    conn.rollback();
                    conn.close();
                    return false;
                }
                //On crée les relations
                if (!new RelationsHelper().addRelationBT(conn, idCNT, idTheso, idC, user.getUser().getId())) {
                    conn.rollback();
                    conn.close();
                    return false;
                }

                conn.commit();
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(SelectedTerme.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        } else if (type == 1) {
            try {
                Connection conn = connect.getPoolConnexion().getConnection();
                conn.setAutoCommit(false);

                if (new ConceptHelper().haveThisGroup(connect.getPoolConnexion(), idCNT, idC, idTheso)) {

                    if (!new RelationsHelper().setRelationTopConcept(conn, idCNT, idTheso, idC, true, user.getUser().getId())) {
                        conn.rollback();
                        conn.close();
                        return false;
                    }
                } else {
                    ArrayList<String> newGroup = new ArrayList<>();
                    newGroup.add(idC);
                    if (!addBranchGroup(newGroup, idCNT)) {
                        conn.rollback();
                        conn.close();
                        return false;
                    }
                    if (!new RelationsHelper().setRelationTopConcept(conn, idCNT, idTheso, idC, true, user.getUser().getId())) {
                        conn.rollback();
                        conn.close();
                        return false;
                    }
                }
                conn.commit();
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(SelectedTerme.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {

            try {
                Connection conn = connect.getPoolConnexion().getConnection();
                conn.setAutoCommit(false);
                // On ajoute les nouveaux domaines s'il y en a
                ArrayList<String> groupNT = conceptHelper.getListGroupIdOfConcept(connect.getPoolConnexion(), idCNT, idTheso);
                ArrayList<String> groupCurrent = conceptHelper.getListGroupIdOfConcept(connect.getPoolConnexion(), idC, idTheso);
                ArrayList<String> newGroup = new ArrayList<>();
                for (String s : groupCurrent) {
                    if (!groupNT.contains(s)) {
                        newGroup.add(s);
                    }
                }
                if (!addBranchGroup(newGroup, idCNT)) {
                    conn.rollback();
                    conn.close();
                    return false;
                }

                //On crée les relations
                if (!new RelationsHelper().addRelationBT(conn, idCNT, idTheso, idC, user.getUser().getId())) {
                    conn.rollback();
                    conn.close();
                    return false;
                }
                conn.commit();
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(SelectedTerme.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }
        ArrayList<NodeNT> tempNT = new RelationsHelper().getListNT(connect.getPoolConnexion(), idC, idTheso, idlangue);
        termesSpecifique = new ArrayList<>();
        HashMap<String, String> tempMap = new HashMap<>();
        for (NodeNT nnt : tempNT) {
            tempMap.put(nnt.getIdConcept(), nnt.getTitle() + " (" +nnt.getRole() +")");
        }
        termesSpecifique.addAll(tempMap.entrySet());
        vue.setAddTSpe(false);
        return true;
    }

    /**
     * Ajoute une traduction au terme courant et met l'affichage à jour
     */
    public void creerTradterme() {
        if (valueEdit == null || valueEdit.trim().equals("")) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("sTerme.error3")));
            return;
        }
        boolean tradExist = false;

        // c'est le cas ou le concept n'a pas de traduction dans la langue en cours, il faut le mettre a jour dans l'arbre
        boolean newTraduction = false;

        for (Entry<String, String> e : langues) {
            if (e.getKey().equals(langueEdit)) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("sTerme.error4")));
                tradExist = true;
                break;
            }
        }
        ConceptHelper ch = new ConceptHelper();
        TermHelper termHelper = new TermHelper();
        if (idT.isEmpty()) {
            newTraduction = true;
            String tmp = termHelper.getIdTermOfConcept(connect.getPoolConnexion(), idC, idTheso);
            if (tmp != null) {
                idT = tmp;
            }
        }
        // traduction du domaine
        if (type == 1 && !tradExist) {
            ConceptGroupLabel cgl = new ConceptGroupLabel();
            cgl.setLexicalvalue(valueEdit);
            cgl.setIdgroup(idDomaine);
            cgl.setIdthesaurus(idTheso);
            cgl.setLang(langueEdit);

            GroupHelper cgh = new GroupHelper();
            if (cgh.isDomainExist(connect.getPoolConnexion(),
                    cgl.getLexicalvalue(),
                    cgl.getIdthesaurus(), cgl.getLang())) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("sTerme.error4")));
                return;
            }
            if (!cgh.addGroupTraduction(connect.getPoolConnexion(), cgl, user.getUser().getId())) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("Error")));
                return;
            }

            ArrayList<NodeGroupTraductions> tempNGT = new GroupHelper().getGroupTraduction(connect.getPoolConnexion(), idDomaine, idTheso, idlangue);
            langues = new ArrayList<>();
            HashMap<String, String> tempMapL = new HashMap<>();
            for (NodeGroupTraductions ngt : tempNGT) {
                tempMapL.put(ngt.getIdLang(), ngt.getTitle());
            }
            if (newTraduction) {
                nom = cgh.getLexicalValueOfGroup(connect.getPoolConnexion(), idDomaine, idTheso, idlangue);
            }
            langues.addAll(tempMapL.entrySet());

            // traduction du TT
        } else if (type == 2 && !tradExist) {
            Term terme = new Term();
            terme.setId_thesaurus(idTheso);
            terme.setLang(langueEdit);
            terme.setLexical_value(valueEdit);
            terme.setId_term(idT);
            terme.setContributor(user.getUser().getId());
            terme.setCreator(user.getUser().getId());
            terme.setSource("");
            terme.setStatus("");
            if (termHelper.isTermExist(connect.getPoolConnexion(),
                    terme.getLexical_value(),
                    terme.getId_thesaurus(), terme.getLang())) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("sTerme.error4")));
                return;
            }

            if (!ch.addTopConceptTraduction(connect.getPoolConnexion(), terme, user.getUser().getId())) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("Error")));
                return;
            }

            ArrayList<NodeTermTraduction> tempNTT = termHelper.getTraductionsOfConcept(connect.getPoolConnexion(), idC, idTheso, idlangue);
            langues = new ArrayList<>();
            HashMap<String, String> tempMapL = new HashMap<>();
            for (NodeTermTraduction ntt : tempNTT) {
                tempMapL.put(ntt.getLang(), ntt.getLexicalValue());
            }
            if (newTraduction) {
                nom = termHelper.getThisTerm(connect.getPoolConnexion(), idC, idTheso, idlangue).getLexical_value();
            }
            langues.addAll(tempMapL.entrySet());

            // traduction des concepts
        } else if (type == 3 && !tradExist) {
            Term terme = new Term();
            terme.setId_thesaurus(idTheso);
            terme.setLang(langueEdit);
            terme.setLexical_value(valueEdit);
            terme.setId_term(idT);
            terme.setContributor(user.getUser().getId());
            terme.setCreator(user.getUser().getId());
            terme.setSource("");
            terme.setStatus("");
            if (termHelper.isTermExist(connect.getPoolConnexion(),
                    terme.getLexical_value(),
                    terme.getId_thesaurus(), terme.getLang())) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("sTerme.error4")));
                return;
            }

            if (!ch.addConceptTraduction(connect.getPoolConnexion(), terme, user.getUser().getId())) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("Error")));
                return;
            }

            ArrayList<NodeTermTraduction> tempNTT = termHelper.getTraductionsOfConcept(connect.getPoolConnexion(), idC, idTheso, idlangue);
            langues = new ArrayList<>();
            HashMap<String, String> tempMapL = new HashMap<>();
            for (NodeTermTraduction ntt : tempNTT) {
                tempMapL.put(ntt.getLang(), ntt.getLexicalValue());
            }
            langues.addAll(tempMapL.entrySet());
            if (newTraduction) {
                nom = termHelper.getThisTerm(connect.getPoolConnexion(), idC, idTheso, idlangue).getLexical_value();
            }
        }

        langueEdit = "";
        valueEdit = "";
        if (!tradExist) {
            vue.setAddTrad(0);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("sTerme.info2")));
        }
    }

    public void creerAlign() {
        if (valueEdit == null || valueEdit2 == null || linkEdit == null || /*valueEdit.equals("") || valueEdit2.equals("") ||*/ linkEdit.equals("")) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("sTerme.error5")));
        } else {
            new AlignmentHelper().addNewAlignment(connect.getPoolConnexion(),
                    user.getUser().getId(), valueEdit2.trim(), valueEdit.trim(),
                    linkEdit.trim(), Integer.parseInt(statutEdit), idC, idTheso, 0);
            valueEdit = "";
            valueEdit2 = "";
            linkEdit = "";
            statutEdit = "";
            align = new AlignmentHelper().getAllAlignmentOfConcept(connect.getPoolConnexion(), idC, idTheso);
            vue.setAddAlign(0);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("sTerme.info9")));
        }
    }

    /**
     * Cette fonction permet de récupérer la liste des alignements par rapport à
     * une source
     *
     * @param idConcept
     * @param lexicalValue
     */
    public void creerAlignAuto(String idConcept, String lexicalValue) {
        if (selectedAlignement == null) {
            return;
        }
        AlignmentQuery alignmentQuery = new AlignmentQuery();

        listAlignValues = new ArrayList<>();

        for (AlignementSource alignementSource1 : alignementSources) {
            // on se positionne sur la source sélectionnée 
            if (selectedAlignement.equalsIgnoreCase(alignementSource1.getSource())) {
                // on trouve le type de filtre à appliquer
                alignementSource = alignementSource1;

                if ("REST".equalsIgnoreCase(alignementSource1.getTypeRequete())) {
                    // si type opentheso / skos
                    // action skos
                    if ("skos".equals(alignementSource1.getAlignement_format())) {
                        listAlignValues = alignmentQuery.queryOpentheso(idConcept, idTheso, lexicalValue.trim(), idlangue, alignementSource1.getRequete());
                    }
                    // action xml (wikipédia)
                    if ("xml".equals(alignementSource1.getAlignement_format())) {
                        //ici il faut appeler le filtre de Wikipédia 
                        listAlignValues = alignmentQuery.queryWikipedia(idConcept, idTheso, lexicalValue.trim(), idlangue, alignementSource1.getRequete());
                    }
                }
                if ("SPARQL".equalsIgnoreCase(alignementSource1.getTypeRequete())) {
                    // action SKOS (BNF)
                    if ("skos".equals(alignementSource1.getAlignement_format())) {
                        //ici il faut appeler le filtre de Wikipédia 
                        listAlignValues = alignmentQuery.queryBNF(idConcept, idTheso, lexicalValue.trim(), idlangue, alignementSource1.getRequete());
                    }
                    // action SKOS (BNF)
                    if ("skos".equals(alignementSource1.getAlignement_format())) {
                        //ici il faut appeler le filtre de Wikipédia 
                        //         listAlignValues = alignmentQuery.queryDBPedia(idC, idTheso, nom.trim(), idlangue);
                    }

                }
                //si type Json
                // action jason

                //
            }
        }
        /*
        if ("DBPedia".equals(selectedAlignement)) {
            listAlignValues.addAll(new AlignmentQuery().query(connect.getPoolConnexion(), "DBP", idC, idTheso, nom, idlangue, null));
            dbp = false;
        }
        if ("bnf".equals(selectedAlignement)) {
            listAlignValues.addAll(new AlignmentQuery().query(connect.getPoolConnexion(), "bnf", idC, idTheso, nom, idlangue, null));
            dbp = false;
        }  
        /*
        if (wiki) {
            listAlignValues.addAll(new AlignmentQuery().query("WIKI", idC, idTheso, nom, idlangue, null));
            wiki = false;
        }
        if (agrovoc) {
            listAlignValues.addAll(new AlignmentQuery().query("AGROVOC", idC, idTheso, nom, idlangue, null));
            agrovoc = false;
        }
        if (gemet) {
            listAlignValues.addAll(new AlignmentQuery().query("GEMET", idC, idTheso, nom, idlangue, null));
            gemet = false;
        }*/
 /*     if (opentheso) {
            String lien;
            if (!linkOT.trim().equals("") && !idOT.trim().equals("")) {
                if (linkOT.lastIndexOf("/") == linkOT.length() - 1) {
                    lien = linkOT.trim() + "webresources/rest/skos/concept/value=" + nom.replaceAll(" ", "_") + "&lang=" + idlangue + "&th=" + idOT;
                } else {
                    lien = linkOT.trim() + "/webresources/rest/skos/concept/value=" + nom.replaceAll(" ", "_") + "&lang=" + idlangue + "&th=" + idOT;
                }
                //listAlignTemp.addAll(new AlignmentQuery().query("OPENT", idC, idTheso, nom, idlangue, lien));
            }
            opentheso = false;
        }
         */
    }

    public void ajouterAlignAuto() {
        if (listAlignValues.isEmpty()) {
            return;
        }
        for (NodeAlignment na : listAlignValues) {
            if (na.isSave()) {
                new AlignmentHelper().addNewAlignment(connect.getPoolConnexion(), user.getUser().getId(), na.getConcept_target(), na.getThesaurus_target(),
                        na.getUri_target(), na.getAlignement_id_type(), idC, idTheso, alignementSource.getId());
            }
        }
        align = new AlignmentHelper().getAllAlignmentOfConcept(connect.getPoolConnexion(), idC, idTheso);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("sTerme.info11")));
        vue.setAddAlign(0);
    }

    /**
     * Permet de creer une alignement, cette funtion s'utilise pour l'alignement
     * par lot l'apelation de la funtion c'est de AlignementParLotBean
     *
     * @param nodeAlignment
     * @return
     */
    public boolean ajouterAlignAutoByLot(NodeAlignment nodeAlignment, boolean addDefinition, String id_term) {
        AlignmentHelper alignmentHelper = new AlignmentHelper();

        NoteHelper noteHelper = new NoteHelper();
        if (!alignmentHelper.addNewAlignment(connect.getPoolConnexion(), user.getUser().getId(), nodeAlignment.getConcept_target(),
                nodeAlignment.getThesaurus_target(), nodeAlignment.getUri_target(),
                nodeAlignment.getAlignement_id_type(), nodeAlignment.getInternal_id_concept(), idTheso, alignementSource.getId())) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("Notation Error BDD")));
            return false;
        }
        if (addDefinition) {
            StringPlus stringPlus = new StringPlus();
            String dejaBonString = stringPlus.clearAngles(nodeAlignment.getDef_target());
            if (!noteHelper.addTermNote(connect.getPoolConnexion(), id_term, idlangue, idTheso, dejaBonString, "definition", user.getUser().getId())) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("Notation Error BDD")));
                return false;
            }
        }
        messageAlig = alignmentHelper.getMessage();
        return true;
    }

    /**
     * *************************************** EDITION
     * ****************************************
     */
    /**
     * Permet de modifier le nom d'un Group ou sousGroup
     *
     * @param idTheso
     * @param idGroup
     * @param idLangue
     * @param value
     * @return #MR
     */
    public boolean editGroupName(String idTheso, String idGroup,
            String idLangue, String value) {

        GroupHelper groupHelper = new GroupHelper();
        ConceptGroupLabel conceptGroupLabel = new ConceptGroupLabel();
        conceptGroupLabel.setIdthesaurus(idTheso);
        conceptGroupLabel.setIdgroup(idGroup);
        conceptGroupLabel.setLang(idLangue);
        conceptGroupLabel.setLexicalvalue(value);
        return groupHelper.updateConceptGroupLabel(connect.getPoolConnexion(), conceptGroupLabel, user.getUser().getId());
    }

    /**
     * Modifie le nom du terme selectionné
     *
     * @param cas
     * @return
     */
    public boolean editTerme(int cas) {
        if (cas == 1) {
            Term t = new Term();
            t.setLexical_value(nomEdit);
            t.setId_term(idT);
            t.setId_thesaurus(idTheso);
            t.setLang(idlangue);
            new TermHelper().updateTermTraduction(connect.getPoolConnexion(), t, user.getUser().getId());
        } else if (cas == 2) {
            //le terme n'existe pas, il faut le créer
            String idTerme = new TermHelper().getIdTermOfConcept(connect.getPoolConnexion(), idC, idTheso);
            if (idTerme == null) {
                return false;
            }
            Term termTemp = new Term();
            termTemp.setId_concept(idC);
            termTemp.setId_term(idTerme);
            termTemp.setId_thesaurus(idTheso);
            termTemp.setLang(idlangue);
            termTemp.setLexical_value(nomEdit);
            termTemp.setSource(String.valueOf(user.getUser().getId()));
            new TermHelper().addTraduction(connect.getPoolConnexion(), termTemp, user.getUser().getId());
        } else if (cas == 3) {
            ConceptGroupLabel cgl = new ConceptGroupLabel();
            cgl.setLexicalvalue(nomEdit);
            cgl.setIdgroup(idDomaine);
            cgl.setIdthesaurus(idTheso);
            cgl.setLang(idlangue);
            new GroupHelper().addGroupTraduction(connect.getPoolConnexion(), cgl, user.getUser().getId());
        } else if (cas == 4) {
            ConceptGroupLabel cgl = new ConceptGroupLabel();
            cgl.setLexicalvalue(nomEdit);
            cgl.setIdgroup(idDomaine);
            cgl.setIdthesaurus(idTheso);
            cgl.setLang(idlangue);
            new GroupHelper().updateConceptGroupLabel(connect.getPoolConnexion(), cgl, user.getUser().getId());
        }
        vue.setAddTInfo(0);
        nom = nomEdit;
        nomEdit = "";
        return true;
    }

    /**
     * Crée ou modifie la définition du terme courant
     */
    public void editDef() {
        int idUser = user.getUser().getId();
        if (definition.isEmpty()) {
            deleteThisNoteOfConcept("note");
            return;
        }
        if (new NoteHelper().isNoteExistOfTerm(connect.getPoolConnexion(), idT, idTheso, idlangue, "definition")) {
            new NoteHelper().updateTermNote(connect.getPoolConnexion(), idT, idlangue, idTheso, definition, "definition", idUser);
        } else {
            new NoteHelper().addTermNote(connect.getPoolConnexion(), idT, idlangue, idTheso, definition, "definition", idUser);
        }
        majNotes();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("sTerme.info3")));
    }

    /**
     * Cette fontion permet de modifié l'information du note
     *
     */
    public void editNote() {
        int idUser = user.getUser().getId();
        if (note.isEmpty()) {
            deleteThisNoteOfConcept("note");
            return;
        }
        if (new NoteHelper().isNoteExistOfConcept(connect.getPoolConnexion(), idC, idTheso, idlangue, "note")) {
            new NoteHelper().updateConceptNote(connect.getPoolConnexion(), idC, idlangue, idTheso, note, "note", idUser);
        } else {
            new NoteHelper().addConceptNote(connect.getPoolConnexion(), idC, idlangue, idTheso, note, "note", idUser);
        }
        majNotes();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("sTerme.info12")));
    }

    /**
     * Cette fontion permet d'avoir l'information que se besoin du term qu'on va
     * changer
     *
     * @param value
     * @param key
     */
    public void traductionEnCours(String value, String key) {
        valueOfTraductionToModify = value;
        langEnTraduction = key;

    }

    /**
     * Cette fontion permet d'avoir l'information que se besoin du Synonyme
     *
     * @param value
     */
    public void changeSynonymeEnCour(String value) {
        oldValue = value;
        valueOfSynonymesToModify = "";
    }

    public void nouveauSynonymeEnCour(String value) {
        valueOfSynonymesToModify = value;
    }

    public void modifierSynonyme() {

        int idUser = user.getUser().getId();
        Term term = new Term();
        term.setLexical_value(valueOfSynonymesToModify);
        term.setId_term(idT);
        term.setId_thesaurus(idTheso);
        term.setLang(idlangue);
        if (!new TermHelper().updateTermSynonyme(connect.getPoolConnexion(), oldValue, term, idUser)) {
            //ca va pas
        }
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("sTerme.modifySyn")));
        majSyno();
    }

    /**
     * Cette fontion permet de modifier une Traduction
     *
     */
    public void modifierTraduction() {

        int idUser = user.getUser().getId();
        Term term = new Term();
        term.setLexical_value(valueOfTraductionToModify);
        term.setId_term(idT);
        term.setId_thesaurus(idTheso);
        term.setLang(langEnTraduction);

        if (!new TermHelper().updateTermTraduction(connect.getPoolConnexion(), term, idUser)) {
            //erreur
        }
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("sTerme.modifyLang")));
        majLangueConcept();
    }

    /**
     * Cette fonction permet de supprimer une note suivant son type
     *
     * @param noteTypeCode
     */
    public void deleteThisNoteOfConcept(String noteTypeCode) {
        int idUser = user.getUser().getId();
        new NoteHelper().deletethisNoteOfConcept(connect.getPoolConnexion(), idC, idTheso, idlangue, noteTypeCode);

        majNotes();

        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("sTerme.info13")));
    }

    /**
     * Cette fonction permet de supprimer une note suivant son type
     *
     * @param noteTypeCode
     */
    public void deleteThisNoteOfTerm(String noteTypeCode) {
        int idUser = user.getUser().getId();
        new NoteHelper().deleteThisNoteOfTerm(connect.getPoolConnexion(), idT, idTheso, idlangue, noteTypeCode);

        majNotes();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("sTerme.info13")));
    }

    public void editNoteApp() {
        int idUser = user.getUser().getId();
        if (noteApplication.isEmpty()) {
            deleteThisNoteOfConcept("note");
            return;
        }
        if (new NoteHelper().isNoteExistOfConcept(connect.getPoolConnexion(), idC, idTheso, idlangue, "scopeNote")) {
            new NoteHelper().updateConceptNote(connect.getPoolConnexion(), idC, idlangue, idTheso, noteApplication, "scopeNote", idUser);
        } else {
            new NoteHelper().addConceptNote(connect.getPoolConnexion(), idC, idlangue, idTheso, noteApplication, "scopeNote", idUser);
        }
        majNotes();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("sTerme.info12")));
    }

    /**
     * Crée ou modifie la note historique du terme courant
     */
    public void editNoteHisto() {
        int idUser = user.getUser().getId();
        if (noteHistorique.isEmpty()) {
            deleteThisNoteOfConcept("note");
            return;
        }
        if (new NoteHelper().isNoteExistOfTerm(connect.getPoolConnexion(), idT, idTheso, idlangue, "historyNote")) {
            new NoteHelper().updateTermNote(connect.getPoolConnexion(), idT, idlangue, idTheso, noteHistorique, "historyNote", idUser);
        } else {
            new NoteHelper().addTermNote(connect.getPoolConnexion(), idT, idlangue, idTheso, noteHistorique, "historyNote", idUser);
        }
        majNotes();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("sTerme.info12")));
    }

    /**
     * Crée ou modifie la note éditoriale du terme courant
     */
    public void editNoteEdit() {

        int idUser = user.getUser().getId();
        if (noteEditoriale.isEmpty()) {
            deleteThisNoteOfConcept("note");
            return;
        }
        if (new NoteHelper().isNoteExistOfTerm(connect.getPoolConnexion(), idT, idTheso, idlangue, "editorialNote")) {
            new NoteHelper().updateTermNote(connect.getPoolConnexion(), idT, idlangue, idTheso, noteEditoriale, "editorialNote", idUser);
        } else {
            new NoteHelper().addTermNote(connect.getPoolConnexion(), idT, idlangue, idTheso, noteEditoriale, "editorialNote", idUser);
        }
        majNotes();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("sTerme.info5")));
    }

    public void insertInFacet() {
        new FacetHelper().addConceptToFacet(connect.getPoolConnexion(), Integer.parseInt(valueEdit), idTheso, idC);
        valueEdit = "";
        reInitFacette();
        initTree();
        vue.setAddFacette(false);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("sTerme.info10")));
    }

    public void delFromFacet() {
        new FacetHelper().deleteConceptFromFacet(connect.getPoolConnexion(), Integer.parseInt(valueEdit), idC, idTheso);
        valueEdit = "";
        reInitFacette();
        initTree();
        vue.setAddFacette(false);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("sTerme.info11")));
    }

    /**
     * *************************************** SUPPRESSION
     * ****************************************
     */
    /**
     * Supprime le terme synonyme dont la valeur est passée en paramètre
     *
     * @param value
     * @param status
     */
    public void delSyno(String value, String status) {
        new TermHelper().deleteNonPreferedTerm(connect.getPoolConnexion(), idT, idlangue, value, idTheso, status, user.getUser().getId());
        majSyno();
        vue.setAddTSyno(0);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", value + " " + langueBean.getMsg("sTerme.info6")));
    }

    /**
     * Supprime la traduction du terme pour la langue passée en paramètre
     *
     * @param lang
     */
    public void delTrad(String lang) {
        new TermHelper().deleteTraductionOfTerm(connect.getPoolConnexion(), idT, lang, idTheso, user.getUser().getId());
        majLangueConcept();
        majNotes();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("sTerme.info10")));
    }

    /**
     * Supprime la relation d'association qui lie le terme courant au terme dont
     * l'id est passé en paramètre.
     *
     * @param id
     */
    public void delAsso(String id) {
        if (!new RelationsHelper().deleteRelationRT(connect.getPoolConnexion(), idC, idTheso, id, user.getUser().getId())) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("sTerme.info7")));
            return;
        }
        majTAsso();
        vue.setAddTAsso(0);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("sTerme.info7")));
    }

    /**
     * (ne marche pas encore !!! en cours) cette fonction permet de supprimer un
     * groupe d'une branche
     *
     * @param delGroup
     * @param idConcept
     * @return
     */
    public boolean delBranchGroup(ArrayList<String> delGroup, String idConcept) {
        if (new ConceptHelper().haveChildren(connect.getPoolConnexion(), idTheso, idConcept)) {
            List<NodeNT> children = new RelationsHelper().getListNT(connect.getPoolConnexion(), idConcept, idTheso, "");
            for (NodeNT nnt : children) {
                delBranchGroup(delGroup, nnt.getIdConcept());
            }
        }
        List<NodeBT> parents = new RelationsHelper().getListBT(connect.getPoolConnexion(), idConcept, idTheso, "");
        int cpt = 0;
        for (String s : delGroup) {
            for (NodeBT nbt : parents) {
                if (new ConceptHelper().haveThisGroup(connect.getPoolConnexion(), nbt.getIdConcept(), s, idTheso)) {
                    cpt++;
                }
            }
            if (new ConceptHelper().isTopConcept(connect.getPoolConnexion(), idConcept, idTheso, s)) {
                cpt++;
            }
            if (cpt <= 1) {
                new ConceptHelper().deleteGroupOfConcept(connect.getPoolConnexion(), idConcept, s, idTheso, user.getUser().getId());
            }
            cpt = 0;
        }
        return true;
    }

    /**
     * Supprime la relation hiérarchique qui lie le terme courant à son père
     *
     * @param id l'identifiant du père
     * @return true or false
     */
    public boolean delGene(String id) {
        // id est l'identifiant du concept à qui on doit supprimer la relation BT
        boolean TGisDomaine = false;
        if (idDomaine.equals(id)) {
            // cas où le générique est un domaine // donc le concept est un TT
            TGisDomaine = true;
        }
        // premier cas, si la branche n'a qu'un BT, alors elle devient orpheline
        if (termeGenerique.size() == 1) {
            // Le concept devient orphelin
            try {
                Connection conn = connect.getPoolConnexion().getConnection();
                conn.setAutoCommit(false);

                /*   if (!new ConceptHelper().deleteConceptFromTable(conn, idC, idTheso, user.getUser().getId())) {
                    conn.rollback();
                    conn.close();
                    return false;
                }*/
                if (!new OrphanHelper().addNewOrphan(conn, idC, idTheso)) {
                    conn.rollback();
                    conn.close();
                    return false;
                }

                if (new GroupHelper().isIdOfGroup(connect.getPoolConnexion(), id, idTheso)) {
                    if (!new RelationsHelper().setRelationTopConcept(conn, idC, idTheso, id, false, user.getUser().getId())) {
                        conn.rollback();
                        conn.close();
                        return false;
                    }
                } else // on coupe la branche de son BT
                if (!new RelationsHelper().deleteRelationBT(conn, idC, idTheso, id, user.getUser().getId())) {
                    conn.rollback();
                    conn.close();
                    return false;
                }
                conn.commit();
                conn.close();

                termeGenerique = new ArrayList<>();
                vue.setAddTGen(0);
                return true;

            } catch (SQLException ex) {
                Logger.getLogger(SelectedTerme.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }

        // deuxième cas où la branche a plusieurs termes générique
        if (termeGenerique.size() > 1) {
            // si la branche est dans le même domaine, alors on supprime seulement la relation BT

            try {
                Connection conn = connect.getPoolConnexion().getConnection();
                conn.setAutoCommit(false);

                if (TGisDomaine) {
                    if (!new RelationsHelper().deleteRelationTT(conn, idC,
                            idDomaine, idTheso, user.getUser().getId())) {
                        conn.rollback();
                        conn.close();
                        return false;
                    }
                } else if (!new RelationsHelper().deleteRelationBT(conn, idC, idTheso, id, user.getUser().getId())) {
                    conn.rollback();
                    conn.close();
                    return false;
                }
                conn.commit();
                conn.close();

                termeGenerique = new ArrayList<>();
                majTGen();
                vue.setAddTGen(0);
                return true;

            } catch (SQLException ex) {
                Logger.getLogger(SelectedTerme.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }

        // troisième cas, si la branche est à deplacer dans un nouveau domaine, alors il faut lui rajouter ce nouveau domaine.
        return false;
    }

    // ancien code qui ne marche pas
    /*   
     if(termeGenerique.size() == 1) { // Le concept devient orphelin
     new ConceptHelper().deleteConceptFromTable(connect.getPoolConnexion(), idC, idTheso);
     new OrphanHelper().addNewOrphan(connect.getPoolConnexion(), idC, idTheso);
            
     if(new GroupHelper().isIdOfGroup(connect.getPoolConnexion(), id, idTheso)) {
     new RelationsHelper().setRelationTopConcept(connect.getPoolConnexion(), idC, idTheso, id, false);
     } else {
     new RelationsHelper().deleteRelationBT(connect.getPoolConnexion(), idC, idTheso, id);
     }
            
     termeGenerique = new ArrayList<>();
     }  else {
     // On supprime l'ancien domaines si besoin
     ArrayList<String> groupBT;
     if(type == 2) {
     groupBT = new ArrayList<>();
     groupBT.add(id);
     } else {
     groupBT = new ConceptHelper().getListGroupIdOfConcept(connect.getPoolConnexion(), id, idTheso);
     }
     ArrayList<String> groupCurrent = new ConceptHelper().getListGroupIdOfConcept(connect.getPoolConnexion(), idC, idTheso);
     ArrayList<String> delGroup = new ArrayList<>();
     for(String s : groupBT) {
     if(groupCurrent.contains(s)) {
     delGroup.add(s);
     }
     }
     delBranchGroup(delGroup, idC);
            
     if(new GroupHelper().isIdOfGroup(connect.getPoolConnexion(), id, idTheso)) {
     new RelationsHelper().setRelationTopConcept(connect.getPoolConnexion(), idC, idTheso, id, false);
     } else {
     new RelationsHelper().deleteRelationBT(connect.getPoolConnexion(), idC, idTheso, id);
     }
            
     termeGenerique = new ArrayList<>();
     majTGen();
     }  
     vue.setAddTGen(false);
        
     }*/
    /**
     * Supprime les relations qui lient le concept au Concept père
     *
     * @param id l'identifiant du fils
     * @return true or false
     */
    public boolean delSpe(String id) {
        // On regarde si le fils devient orphelin
        ArrayList<Entry<String, String>> sonFathers = new ArrayList<>();
        // On ajoute le domaine
        ArrayList<String> listIdGroup = new ConceptHelper().getListGroupParentIdOfConcept(connect.getPoolConnexion(), id, idTheso);
        HashMap<String, String> tempMap1 = new HashMap<>();
        for (String group : listIdGroup) {
            tempMap1.put(group, new GroupHelper().getLexicalValueOfGroup(connect.getPoolConnexion(), group, idTheso, idlangue));
        }
        sonFathers.addAll(tempMap1.entrySet());

        ArrayList<NodeBT> tempBT = new RelationsHelper().getListBT(connect.getPoolConnexion(), id, idTheso, idlangue);
        HashMap<String, String> tempMap2 = new HashMap<>();
        for (NodeBT nbt : tempBT) {
            tempMap2.put(nbt.getIdConcept(), nbt.getTitle());
        }
        sonFathers.addAll(tempMap2.entrySet());

        if (sonFathers.size() <= 1) {
            try {
                Connection conn = connect.getPoolConnexion().getConnection();
                conn.setAutoCommit(false);
                if (!new ConceptHelper().deleteConceptFromTable(conn, id, idTheso, user.getUser().getId())) {
                    conn.rollback();
                    conn.close();
                    return false;
                }
                if (!new RelationsHelper().deleteRelationBT(conn, id, idTheso, idC, user.getUser().getId())) {
                    conn.rollback();
                    conn.close();
                    return false;
                }
                if (!new OrphanHelper().addNewOrphan(conn, id, idTheso)) {
                    conn.rollback();
                    conn.close();
                    return false;
                }
                conn.commit();
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(SelectedTerme.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        } else {
            try {
                Connection conn = connect.getPoolConnexion().getConnection();
                conn.setAutoCommit(false);
                // On supprime l'ancien domaines si besoin
                ArrayList<String> groupCurrent;
                if (type == 1) {
                    groupCurrent = new ArrayList<>();
                    groupCurrent.add(idC);
                } else {
                    groupCurrent = new ConceptHelper().getListGroupIdOfConcept(connect.getPoolConnexion(), idC, idTheso);
                }
                ArrayList<String> groupNT = new ConceptHelper().getListGroupIdOfConcept(connect.getPoolConnexion(), id, idTheso);
                ArrayList<String> delGroup = new ArrayList<>();
                for (String s : groupCurrent) {
                    if (groupNT.contains(s)) {
                        delGroup.add(s);
                    }
                }
                if (!delBranchGroup(delGroup, id)) {
                    conn.rollback();
                    conn.close();
                    return false;
                }

                if (type == 1) {
                    if (!new RelationsHelper().setRelationTopConcept(conn, id, idTheso, idC, false, user.getUser().getId())) {
                        conn.rollback();
                        conn.close();
                        return false;
                    }
                } else if (!new RelationsHelper().deleteRelationBT(conn, id, idTheso, idC, user.getUser().getId())) {
                    conn.rollback();
                    conn.close();
                    return false;
                }
                conn.commit();
                conn.close();

                ArrayList<NodeNT> tempNT = new RelationsHelper().getListNT(connect.getPoolConnexion(), idC, idTheso, idlangue);
                termesSpecifique = new ArrayList<>();
                HashMap<String, String> tempMap3 = new HashMap<>();
                for (NodeNT nnt : tempNT) {
                    tempMap3.put(nnt.getIdConcept(), nnt.getTitle() + " (" +nnt.getRole() +")");
                }
                termesSpecifique.addAll(tempMap3.entrySet());
            } catch (SQLException ex) {
                Logger.getLogger(SelectedTerme.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }
        return true;
    }

    public void delImage(String nomImage) {
        new ImagesHelper().deleteImage(connect.getPoolConnexion(), idC, idTheso, nomImage);
        images = new ArrayList<>();
        images = new ImagesHelper().getImage(connect.getPoolConnexion(), idC, idTheso, user.getUser().getId());
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("sTerme.info8")));
    }

    /**
     * Suppression d'un Concept
     *
     * @return
     */
    public boolean deprecateConcept() {
        if (!new ConceptHelper().desactiveConcept(connect.getPoolConnexion(),
                idC, idTheso, user.getUser().getId())) {
            return false;
        }
        status = "hidden";
        return true;
    }

    /**
     * Réactivation d'un Concept
     *
     * @return
     */
    public boolean reactivConcept() {
        if (!new ConceptHelper().reactiveConcept(connect.getPoolConnexion(),
                idC, idTheso, user.getUser().getId())) {
            return false;
        }
        status = "D";
        return true;
    }

    public boolean canBeDel() {
        return (!status.equals("hidden") && (type != 1) && (user.getUser().getName() != null) && (idC != null) && (user.haveRights(1)));
    }

    public boolean canBeDelGroup() {
        boolean test;
        test = new GroupHelper().isEmptyDomain(connect.getPoolConnexion(), idDomaine, idTheso);
        //   boolean test =  ((termesSpecifique.isEmpty()) && (type == 1) && (user.getUser().getName() != null) && (idC != null) && (user.haveRights(1)));
        return test;
    }

    public void delAlign(int id) {
        new AlignmentHelper().deleteAlignment(connect.getPoolConnexion(), id, idTheso);
        align = new AlignmentHelper().getAllAlignmentOfConcept(connect.getPoolConnexion(), idC, idTheso);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("sTerme.info12")));
    }

    public void delFuAlign(NodeAlignment a) {
        conceptFusionAlign.remove(a);
    }

    public void delRTFusion(NodeRT rt) {
        conceptFusionNodeRT.remove(rt);
    }

    /**
     * *************************************** AUTRE
     * ****************************************
     */
    /**
     * Ajoute les informations demandées par l'utilisateur sur le concept
     * sélectionné puis fusionne le concept courant avec.
     *
     * @return
     */
    public boolean loadConceptFusion() {
        if (!new ConceptHelper().isIdExiste(connect.getPoolConnexion(), selectedTermComp.getIdConcept(), idTheso)) {
            //if (new ConceptHelper().getThisConcept(connect.getPoolConnexion(), selectedTermComp.getIdConcept(), idTheso) == null) {
            return false;
        }
        conceptFusionId = selectedTermComp.getIdConcept();
        conceptFusionNodeRT = new RelationsHelper().getListRT(connect.getPoolConnexion(), idC, idTheso, idlangue);
        conceptFusionAlign = new AlignmentHelper().getAllAlignmentOfConcept(connect.getPoolConnexion(), idC, idTheso);
        vue.setAddTInfo(3);
        return true;
    }

    /**
     * initialisation pour la fusion des concepts.
     *
     * @return
     */
    public boolean initConceptFusion() {
        conceptFusionId = "";
        conceptFusionNodeRT = new ArrayList<>();
        conceptFusionAlign = new ArrayList<>();
        selectedTermComp = new NodeAutoCompletion();

        return true;
    }

    /**
     * Permet de voir les nouvelles notes et changer l'icon pour pouvoir voir
     * touts les notes dans les autres langues;
     */
    public void valide() {
        if (allLangue == true) {
            allLangue = false;
            majNotes();
            icon = "+";
        } else {
            allLangue = true;

            majNotes2();
            icon = "-";
        }
    }

    /**
     * Renvoie le type du concept générique
     *
     * @return 1 si le concept courant test un topConcept, 0 sinon
     */
    public int getTypeSup() {
        if (type == 2) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * Renvoie le type du concept spécifique
     *
     * @return 2 si le concept courant test un domaine, 3 sinon
     */
    public int getTypeInf() {
        if (type == 1) {
            return 2;
        } else {
            return 3;
        }
    }

    public Term getTerme(String id) {
        TermHelper th = new TermHelper();
        Term t = th.getThisTerm(connect.getPoolConnexion(), id, idTheso, idlangue);
        return t;
    }

    public String getLienTerme() {
        return "?idc=" + idC + "&amp;idt=" + idTheso;
    }

    /**
     * ************************** ACTIONS TREE ***************************
     */
    /**
     * Vide l'arbre
     */
    public void reInitFacette() {
        root = (TreeNode) new DefaultTreeNode("Root", null);
    }

    /**
     * Récupère les concept pour remplir la racine de l'arbre des facette
     */
    public void initTree() {
        List<Integer> idFacettes = new FacetHelper().getIdFacetOfConcept(connect.getPoolConnexion(), idC, idTheso);
        ArrayList<NodeConceptTree> racineNode = new ArrayList<>();
        for (Integer i : idFacettes) {
            racineNode.add(new FacetHelper().getConceptOnFacet(connect.getPoolConnexion(), i, idTheso, idlangue));
        }

        for (NodeConceptTree n : racineNode) {
            TreeNode dynamicTreeNode;
            if (n.getTitle().trim().isEmpty()) {
                dynamicTreeNode = (TreeNode) new MyTreeNode(1, n.getIdConcept(), idTheso, idlangue, "", "", "", "dossier", n.getIdConcept(), root);
            } else {
                dynamicTreeNode = (TreeNode) new MyTreeNode(1, n.getIdConcept(), idTheso, idlangue, "", "", "", "dossier", n.getTitle(), root);
            }

            DefaultTreeNode defaultTreeNode = new DefaultTreeNode("fake", dynamicTreeNode);
            defaultTreeNode.setExpanded(true);
        }
    }

    /**
     * Expension de l'abre sur un seul noeud (ouvert manuellement)
     *
     * @param event Le noeud ouvert
     */
    public void onNodeExpand(NodeExpandEvent event) {
        String theso = ((MyTreeNode) event.getTreeNode()).getIdTheso();
        String lang = ((MyTreeNode) event.getTreeNode()).getLangue();

        // Récupération des facettes
        ArrayList<Integer> listIdFacet = new FacetHelper().getIdFacetUnderConcept(connect.getPoolConnexion(), ((MyTreeNode) event.getTreeNode()).getIdMot(), theso);
        ArrayList<NodeFacet> listFacet = new ArrayList<>();
        for (Integer id : listIdFacet) {
            NodeFacet nf = new FacetHelper().getThisFacet(connect.getPoolConnexion(), id, theso, lang);
            listFacet.add(nf);
        }

        //<Retirer noeuds fictifs>
        if (event.getTreeNode().getChildCount() == 1) {
            event.getTreeNode().getChildren().remove(0);
        }

        // Récupération et insertion des facettes avec leurs concepts
        for (NodeFacet nf : listFacet) {
            String valeur = nf.getLexicalValue() + "(" + String.valueOf(nf.getIdFacet()) + ")";
            new MyTreeNode(1, String.valueOf(nf.getIdFacet()), theso, lang, "", "", "", "facette", valeur, event.getTreeNode());

            ArrayList<String> listIdC = new FacetHelper().getIdConceptsOfFacet(connect.getPoolConnexion(), nf.getIdFacet(), theso);

            ArrayList<NodeConceptTree> liste = new ArrayList<>();
            for (String id : listIdC) {
                String value = new TermHelper().getThisTerm(connect.getPoolConnexion(), id, theso, lang).getLexical_value();
                NodeConceptTree nct = new NodeConceptTree();
                nct.setHaveChildren(false);
                nct.setIdConcept(id);
                nct.setIdLang(lang);
                nct.setIdThesaurus(theso);
                nct.setTitle(value);
                liste.add(nct);
            }

            // Ajout dans l'arbre
            for (NodeConceptTree nct : liste) {
                new MyTreeNode(3, nct.getIdConcept(), theso, lang, "", "", "", "fichier", nct.getTitle() + "(" + nct.getIdConcept() + ")", event.getTreeNode());
            }
        }
    }

    /**
     * Affiche les informations du terme correspondant au noeud selectionné dans
     * l'arbre
     *
     * @param event le noeud selectionné
     */
    public void onNodeSelect(NodeSelectEvent event) {
        if (!event.getTreeNode().getType().equals("facette")) {
            MyTreeNode temp = (MyTreeNode) selectedNode;
            temp.setTypeMot(3);
            majTerme(temp);
            setTree(2);
        } else {
            reInitTerme();
        }
    }

    /**
     * *
     * Nouvelles Fonctions par Miled Rousset
     */
    /**
     * permet de mettre à jour la notation d'un concept
     *
     * @return
     */
    public boolean updateNotation() {

        boolean status1 = false;
        ConceptHelper conceptHelper = new ConceptHelper();
        Connection conn;
        try {
            conn = connect.getPoolConnexion().getConnection();
            if (conceptHelper.isNotationExist(conn, idTheso, notation)) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("index.notationError")));
                status1 = false;
                conn.close();
            }

            conn.setAutoCommit(false);
            if (!conceptHelper.updateNotation(conn, idC, idTheso, notation)) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("Notation Error BDD")));
                status1 = false;
                conn.rollback();
                conn.close();
            } else {
                status1 = true;
                conn.commit();
                conn.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(SelectedTerme.class.getName()).log(Level.SEVERE, null, ex);
        }
        return status1;
    }

    /**
     * *
     * Fin des nouvelles fonctions
     */
    /**
     * ************************************ GETTERS SETTERS
     * *************************************
     */
    /**
     * Retourne le nombre de notice pour un terme passé en paramètre
     *
     * @param idTe
     */
    private int getNotice(String idTe) {
        //ResourceBundle bundlePref = getBundlePref();
        int nbNoticesT = 0;
        if (user.getNodePreference().getZ3950acif()) {
            Properties p = new Properties();
            p.put("CollectionDataSourceClassName", "com.k_int.util.Repository.XMLDataSource");
            p.put("RepositoryDataSourceURL", "file:" + cheminNotice1);
            p.put("XSLConverterConfiguratorClassName", "com.k_int.IR.Syntaxes.Conversion.XMLConfigurator");
            p.put("ConvertorConfigFile", cheminNotice2);
            Searchable federated_search_proxy = new HeterogeneousSetOfSearchable();
            federated_search_proxy.init(p);
            try {
                IRQuery e = new IRQuery();
                //   e.collections = new Vector<String>();
                e.collections.add(user.getNodePreference().getCollectionAdresse());
                e.hints.put("default_element_set_name", "f");
                e.hints.put("small_set_setname", "f");
                e.hints.put("record_syntax", "unimarc");
                e.query = new PrefixString((new StringBuilder("@attrset bib-1 @attr 1=Koha-Auth-Number \"")).append(AsciiUtils.convertNonAscii("" + idTe)).append("\"").toString());
                SearchTask st = federated_search_proxy.createTask(e, null);
                st.evaluate(5000);
                nbNoticesT = st.getTaskResultSet().getFragmentCount();
            } catch (TimeoutExceededException | SearchException srch_e) {
                srch_e.printStackTrace();
            }
        }
        return nbNoticesT;
    }

    /**
     * Retourne la liste des fusions du concept (qu'il soit le concept déprécié
     * ou qu'il obtienne les attributs d'une fusion)
     *
     * @return
     */
    public ArrayList<NodeFusion> getFusions() {
        ArrayList<NodeFusion> temp = new ConceptHelper().getConceptFusion(connect.getPoolConnexion(), idC, idlangue, idTheso);
        return new ConceptHelper().getConceptFusion(connect.getPoolConnexion(), idC, idlangue, idTheso);
    }

    /**
     * Retourne les types d'alignements présents pour ce terme
     *
     * @return
     */
    public ArrayList<Entry<String, String>> getALignType() {

        ArrayList<Entry<String, String>> types = new ArrayList<>();
        ArrayList<Entry<String, String>> temp = new ArrayList<>();
        //if(connect.getPoolConnexion()==null)return null;
        HashMap<String, String> map = new AlignmentHelper().getAlignmentType(connect.getPoolConnexion());
        temp.addAll(map.entrySet());
        for (Entry<String, String> e : temp) {
            for (NodeAlignment na : align) {
                if (e.getKey().equals(String.valueOf(na.getAlignement_id_type()))) {
                    types.add(e);
                    break;
                }
            }
        }

        return types;
    }

    public void majNotes2() {
        // NodeNote contient la note avec le type de note, il faut filtrer pour trouver la bonne note
        // For Concept : customnote ; scopeNote ; historyNote
        // For Term : definition; editorialNote; historyNote;
        if (allLangue) {
            initNotes();
            nodeNoteTermList = new NoteHelper().getListNotesTerm2(connect.getPoolConnexion(), idT, idTheso);
            nodeNoteConceptList = new NoteHelper().getListNotesConcept2(connect.getPoolConnexion(), idC, idTheso);
        } else {
            majNotes();
        }
    }

    /**
     * Retourne tous les types d'alignements présents
     *
     * @return
     */
    public ArrayList<Entry<String, String>> getAllALignType() {
        ArrayList<Entry<String, String>> types = new ArrayList<>();
        if (connect.getPoolConnexion() == null) {
            return types;
        }
        HashMap<String, String> map = new AlignmentHelper().getAlignmentType(connect.getPoolConnexion());
        types.addAll(map.entrySet());
        return types;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public ArrayList<Entry<String, String>> getTermeGenerique() {
        return termeGenerique;
    }

    public void setTermeGenerique(ArrayList<Entry<String, String>> termeGenerique) {
        this.termeGenerique = termeGenerique;
    }

    public ArrayList<Entry<String, String>> getTermesSpecifique() {
        return termesSpecifique;
    }

    public void setTermesSpecifique(ArrayList<Entry<String, String>> termesSpecifique) {
        this.termesSpecifique = termesSpecifique;
    }

    public ArrayList<Entry<String, String>> getTermesAssocies() {
        return termesAssocies;
    }

    public void setTermesAssocies(ArrayList<Entry<String, String>> termesAssocies) {
        this.termesAssocies = termesAssocies;
    }

    public ArrayList<String> getTermesSynonymesE() {
        return termesSynonymesE;
    }

    public void setTermesSynonymesE(ArrayList<String> termesSynonymesE) {
        this.termesSynonymesE = termesSynonymesE;
    }

    public ArrayList<String> getTermesSynonymesP() {
        return termesSynonymesP;
    }

    public void setTermesSynonymesP(ArrayList<String> termesSynonymesP) {
        this.termesSynonymesP = termesSynonymesP;
    }

    public ArrayList<Entry<String, String>> getLangues() {
        return langues;
    }

    public void setLangues(ArrayList<Entry<String, String>> langues) {
        this.langues = langues;
    }

    public String getIdT() {
        return idT;
    }

    public void setIdT(String idT) {
        this.idT = idT;
    }

    public String getIdC() {
        return idC;
    }

    public void setIdC(String idC) {
        this.idC = idC;
    }

    public int getNbNotices() {
        return nbNotices;
    }

    public void setNbNotices(int nbNotices) {
        this.nbNotices = nbNotices;
    }

    public String getMicroTheso() {
        return microTheso;
    }

    public void setMicroTheso(String microTheso) {
        this.microTheso = microTheso;
    }

    public String getDateC() {
        return dateC;
    }

    public void setDateC(String dateC) {
        this.dateC = dateC;
    }

    public String getDateM() {
        return dateM;
    }

    public void setDateM(String dateM) {
        this.dateM = dateM;
    }

    public String getIdTheso() {
        return idTheso;
    }

    public void setIdTheso(String idTheso) {
        this.idTheso = idTheso;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Vue getVue() {
        return vue;
    }

    public void setVue(Vue vue) {
        this.vue = vue;
    }

    public ConceptBean getConceptbean() {
        return conceptbean;
    }

    public void setConceptbean(ConceptBean conceptbean) {
        this.conceptbean = conceptbean;
    }

    public String getValueEdit() {
        return valueEdit;
    }

    public void setValueEdit(String valueEdit) {
        this.valueEdit = valueEdit;
    }

    public String getIdlangue() {
        return idlangue;
    }

    public void setIdlangue(String idlangue) {
        this.idlangue = idlangue;
    }

    public Connexion getConnect() {
        return connect;
    }

    public void setConnect(Connexion connect) {
        this.connect = connect;
    }

    public String getLangueEdit() {
        return langueEdit;
    }

    public void setLangueEdit(String langueEdit) {
        this.langueEdit = langueEdit.trim();
    }

    public String getIdDomaine() {
        return idDomaine;
    }

    public void setIdDomaine(String idDomaine) {
        this.idDomaine = idDomaine;
    }

    public String getIdTopConcept() {
        return idTopConcept;
    }

    public void setIdTopConcept(String idTopConcept) {
        this.idTopConcept = idTopConcept;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getNoteApplication() {
        return noteApplication;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setNoteApplication(String noteApplication) {
        this.noteApplication = noteApplication;
    }

    public String getNoteHistorique() {
        return noteHistorique;
    }

    public void setNoteHistorique(String noteHistorique) {
        this.noteHistorique = noteHistorique;
    }

    public String getNoteEditoriale() {
        return noteEditoriale;
    }

    public void setNoteEditoriale(String noteEditoriale) {
        this.noteEditoriale = noteEditoriale;
    }

    public String getNomEdit() {
        return nomEdit;
    }

    public void setNomEdit(String nomEdit) {
        this.nomEdit = nomEdit;
    }

    public CurrentUser getUser() {
        return user;
    }

    public void setUser(CurrentUser user) {
        this.user = user;
    }

    public String getStatutEdit() {
        return statutEdit;
    }

    public void setStatutEdit(String statutEdit) {
        this.statutEdit = statutEdit;
    }

    public NodeSearch getNodeSe() {
        return nodeSe;
    }

    public void setNodeSe(NodeSearch nodeSe) {
        this.nodeSe = nodeSe;
    }

    public int getTree() {
        return tree;
    }

    public void setTree(int tree) {
        this.tree = tree;
    }

    public ArrayList<NodeImage> getImages() {
        return images;
    }

    public void setImages(ArrayList<NodeImage> images) {
        this.images = images;
    }

    public String getUrlNotice() {
        return urlNotice;
    }

    public void setUrlNotice(String urlNotice) {
        this.urlNotice = urlNotice;
    }

    public LanguageBean getLangueBean() {
        return langueBean;
    }

    public void setLangueBean(LanguageBean langueBean) {
        this.langueBean = langueBean;
    }

    public NodePermute getNodePe() {
        return nodePe;
    }

    public void setNodePe(NodePermute nodePe) {
        this.nodePe = nodePe;
    }

    public TreeNode getRoot() {
        return root;
    }

    public void setRoot(TreeNode root) {
        this.root = root;
    }

    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }

    public ArrayList<NodeAlignment> getAlign() {
        return align;
    }

    public void setAlign(ArrayList<NodeAlignment> align) {
        this.align = align;
    }

    public String getLinkEdit() {
        return linkEdit;
    }

    public void setLinkEdit(String linkEdit) {
        this.linkEdit = linkEdit;
    }

    public String getValueEdit2() {
        return valueEdit2;
    }

    public void setValueEdit2(String valueEdit2) {
        this.valueEdit2 = valueEdit2;
    }

    public String getIdArk() {
        return idArk;
    }

    public void setIdArk(String idArk) {
        this.idArk = idArk;
    }

    public ArrayList<Entry<String, String>> getArrayFacette() {
        if (connect.getPoolConnexion() == null) {
            return null;
        }
        ArrayList<Integer> temp = new FacetHelper().getIdFacetOfConcept(connect.getPoolConnexion(), idC, idTheso);
        Map<String, String> mapTemp = new HashMap<>();
        for (Integer i : temp) {
            NodeFacet nf = new FacetHelper().getThisFacet(connect.getPoolConnexion(), i, idTheso, idlangue);
            String value = new TermHelper().getThisTerm(connect.getPoolConnexion(), nf.getIdConceptParent(), idTheso, idlangue).getLexical_value();
            mapTemp.put(String.valueOf(nf.getIdFacet()), nf.getLexicalValue() + " (" + value + ")");
        }
        arrayFacette = new ArrayList<>(mapTemp.entrySet());

        return arrayFacette;
    }

    public ArrayList<Entry<String, String>> getArrayFacetteInclure() {
        if (connect.getPoolConnexion() == null) {
            return null;
        }
        ArrayList<NodeFacet> temp = new FacetHelper().getAllFacetsOfThesaurus(connect.getPoolConnexion(), idTheso, idlangue);
        ArrayList<Integer> temp2 = new FacetHelper().getIdFacetOfConcept(connect.getPoolConnexion(), idC, idTheso);
        Map<String, String> mapTemp = new HashMap<>();
        for (NodeFacet nf : temp) {
            if (!temp2.contains(nf.getIdFacet())) {
                String value = new TermHelper().getThisTerm(connect.getPoolConnexion(), nf.getIdConceptParent(), idTheso, idlangue).getLexical_value();
                mapTemp.put(String.valueOf(nf.getIdFacet()), nf.getLexicalValue() + " (" + value + ")");
            }
        }
        arrayFacette = new ArrayList<>(mapTemp.entrySet());
        return arrayFacette;
    }

    public void setArrayFacette(ArrayList<Entry<String, String>> arrayFacette) {
        this.arrayFacette = arrayFacette;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotation() {
        return notation;
    }

    public void setNotation(String notation) {
        this.notation = notation;
    }

    public NodeAutoCompletion getSelectedTermComp() {
        return selectedTermComp;
    }

    public void setSelectedTermComp(NodeAutoCompletion selectedTermComp) {
        this.selectedTermComp = selectedTermComp;
    }

    public ArrayList<NodeRT> getConceptFusionNodeRT() {
        return conceptFusionNodeRT;
    }

    public void setConceptFusionNodeRT(ArrayList<NodeRT> conceptFusionNodeRT) {
        this.conceptFusionNodeRT = conceptFusionNodeRT;
    }

    public ArrayList<NodeAlignment> getConceptFusionAlign() {
        return conceptFusionAlign;
    }

    public void setConceptFusionAlign(ArrayList<NodeAlignment> conceptFusionAlign) {
        this.conceptFusionAlign = conceptFusionAlign;
    }

    public String getConceptFusionId() {
        return conceptFusionId;
    }

    public void setConceptFusionId(String conceptFusionId) {
        this.conceptFusionId = conceptFusionId;
    }

    public String getChoixdesactive() {
        return choixdesactive;
    }

    public void setChoixdesactive(String choixdesactive) {
        this.choixdesactive = choixdesactive;
    }

    public boolean isDbp() {
        return dbp;
    }

    public void setDbp(boolean dbp) {
        this.dbp = dbp;
    }

    public boolean isWiki() {
        return wiki;
    }

    public void setWiki(boolean wiki) {
        this.wiki = wiki;
    }

    public boolean isAgrovoc() {
        return agrovoc;
    }

    public void setAgrovoc(boolean agrovoc) {
        this.agrovoc = agrovoc;
    }

    public boolean isGemet() {
        return gemet;
    }

    public void setGemet(boolean gemet) {
        this.gemet = gemet;
    }

    public boolean isOpentheso() {
        return opentheso;
    }

    public void setOpentheso(boolean opentheso) {
        this.opentheso = opentheso;
    }

    public String getLinkOT() {
        return linkOT;
    }

    public void setLinkOT(String linkOT) {
        this.linkOT = linkOT;
    }

    public String getIdOT() {
        return idOT;
    }

    public void setIdOT(String idOT) {
        this.idOT = idOT;
    }

    public ArrayList<NodeAlignment> getListAlignValues() {
        return listAlignValues;
    }

    public void setListAlignValues(ArrayList<NodeAlignment> listAlignValues) {
        this.listAlignValues = listAlignValues;
    }

    public int getContributor() {
        return contributor;
    }

    public void setContributor(int contributor) {
        this.contributor = contributor;
    }

    public int getCreator() {
        return creator;
    }

    public String getCreatorName() {
        UserHelper userHelper = new UserHelper();
        return userHelper.getNameUser(connect.getPoolConnexion(), creator);
    }

    public String getContributorName() {
        UserHelper userHelper = new UserHelper();
        return userHelper.getNameUser(connect.getPoolConnexion(), contributor);
    }

    public void setCreator(int creator) {
        this.creator = creator;
    }

    public String getValueOfTraductionToModify() {
        return valueOfTraductionToModify;
    }

    public void setValueOfTraductionToModify(String valueOfTraductionToModify) {
        this.valueOfTraductionToModify = valueOfTraductionToModify;
    }

    public String getValueOfSynonymesToModify() {
        return valueOfSynonymesToModify;
    }

    public void setValueOfSynonymesToModify(String valueOfSynonymesToModify) {
        this.valueOfSynonymesToModify = valueOfSynonymesToModify;
    }

    public String getLangEnTraduction() {
        return langEnTraduction;
    }

    public void setLangEnTraduction(String langEnTraduction) {
        this.langEnTraduction = langEnTraduction;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public ArrayList<NodeNote> getListnotes() {
        return listnotes;
    }

    public void setListnotes(ArrayList<NodeNote> listnotes) {
        this.listnotes = listnotes;
    }

    public ArrayList<NodeNote> getNodeNoteTermList() {
        return nodeNoteTermList;
    }

    public void setNodeNoteTermList(ArrayList<NodeNote> nodeNoteTermList) {
        this.nodeNoteTermList = nodeNoteTermList;
    }

    public ArrayList<NodeNote> getNodeNoteConceptList() {
        return nodeNoteConceptList;
    }

    public void setNodeNoteConceptList(ArrayList<NodeNote> nodeNoteConceptList) {
        this.nodeNoteConceptList = nodeNoteConceptList;
    }

    public boolean isAllLangue() {
        return allLangue;
    }

    public void setAllLangue(boolean allLangue) {
        this.allLangue = allLangue;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public ArrayList<AlignementSource> getAlignementSources() {
        return alignementSources;
    }

    /*   public void setAlignementSources(ArrayList<AlignementSource> alignementSources) {
        this.alignementSources = alignementSources;
    }
     */
    public void setAlignementSources() {
        AlignmentHelper alignmentHelper = new AlignmentHelper();
        alignementSources = alignmentHelper.getAlignementSource(connect.getPoolConnexion(), idTheso);
        //    return alignementSources;
    }

    public String getSelectedAlignement() {
        return selectedAlignement;
    }

    public void setSelectedAlignement(String selectedAlignement) {
        this.selectedAlignement = selectedAlignement;
        for (AlignementSource alignementSource1 : alignementSources) {
            // on se positionne sur la source sélectionnée 
            if (selectedAlignement.equalsIgnoreCase(alignementSource1.getSource())) {
                // on trouve le type de filtre à appliquer
                alignementSource = alignementSource1;
            }
        }
    }

    public AlignementSource getAlignementSource() {
        return alignementSource;
    }

    public void setAlignementSource(AlignementSource alignementSource) {
        this.alignementSource = alignementSource;
    }

    public String getLatitudLongitud() {
        return latitudLongitud;
    }

    public void setLatitudLongitud(String latitudLongitud) {
        this.latitudLongitud = latitudLongitud;
    }

    public String getMessageAlig() {
        return messageAlig;
    }

    public void setMessageAlig(String messageAlig) {
        this.messageAlig = messageAlig;
    }

    public boolean isZ3950_actif() {
        return z3950_actif;
    }

    public void setZ3950_actif(boolean z3950_actif) {
        this.z3950_actif = z3950_actif;
    }

    public boolean isBdd_active() {
        return bdd_active;
    }

    public void setBdd_active(boolean bdd_active) {
        this.bdd_active = bdd_active;
    }

    public String getTotalConceptOfBranch() {
        if (totalConceptOfBranch.isEmpty()) {
            return langueBean.getMsg("index.totalOfConcepts");
        } else {
            return totalConceptOfBranch;
        }
    }

    public void setTotalConceptOfBranch(String totalConceptOfBranch) {
        this.totalConceptOfBranch = totalConceptOfBranch;
    }

    public String getTotalNoticesOfBranch() {
        if (totalNoticesOfBranch.isEmpty()) {
            return langueBean.getMsg("index.totalOfNotices");
        } else {
            return totalNoticesOfBranch;
        }
    }

    public void setTotalNoticesOfBranch(String totalNoticesOfBranch) {
        this.totalNoticesOfBranch = totalNoticesOfBranch;
    }

    public String getTypeDomaine() {
        return typeDomaine;
    }

    public void setTypeDomaine(String typeDomaine) {
        this.typeDomaine = typeDomaine;
    }

}
