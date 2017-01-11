package mom.trd.opentheso.SelectedBeans;

import com.zaxxer.hikari.HikariDataSource;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.xml.parsers.ParserConfigurationException;
import mom.trd.opentheso.bdd.datas.Languages_iso639;
import mom.trd.opentheso.bdd.datas.Term;
import mom.trd.opentheso.bdd.helper.AlignmentHelper;
import mom.trd.opentheso.bdd.helper.ConceptHelper;
import mom.trd.opentheso.bdd.helper.Connexion;
import mom.trd.opentheso.bdd.helper.GpsHelper;
import mom.trd.opentheso.bdd.helper.LanguageHelper;
import mom.trd.opentheso.bdd.helper.TermHelper;
import mom.trd.opentheso.bdd.helper.UserHelper;
import mom.trd.opentheso.bdd.helper.nodes.NodeAlignment;
import mom.trd.opentheso.bdd.helper.nodes.NodeLang;
import mom.trd.opentheso.bdd.helper.nodes.NodePreference;
import mom.trd.opentheso.core.alignment.AlignementSource;
import mom.trd.opentheso.core.alignment.GpsQuery;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.MapModel;
import org.xml.sax.SAXException;

@ManagedBean(name = "gps", eager = true)
@SessionScoped
public class GpsBeans {

    private MapModel geoModel;

    public double latitud;
    public double longitud;

    private ArrayList<AlignementSource> listeAlignementSources;
    private ArrayList<AlignementSource> alignementSources;
    private ArrayList<NodeAlignment> listAlignValues;
    private ArrayList<Languages_iso639> listLanguesInTheso;

    private NodeAlignment alignment_choisi;

    private AlignementSource alignementPreferences;
    NodePreference nodePreference;

    private String selectedAlignement;
    private String centerGeoMap = "41.850033, -87.6500523";
    private String coordennees = null;
    private String codeid;

    private boolean integrerTraduction = true;
    private boolean reemplacerTraduction = true;
    private boolean alignementAutomatique = true;

    // variables pour le lot
    private ArrayList<String> listOfChildrenInConcept;

    private NodeAlignment nodeAli;

    private String id_concept;
    private String id_theso;
    private String id_langue;
    private String nomduterm;
    private String message = "";
    private String erreur = "";
    private String uriSelection = "";

    private int position = 0;
    private int alignement_id_type;

    private boolean fin = false;
    private boolean first = true;
    private boolean last = false;
    private boolean mettreAJour = false;
    //////
    @ManagedProperty(value = "#{poolConnexion}")
    private Connexion connect;
    @ManagedProperty(value = "#{user1}")
    private CurrentUser theUser;
    @ManagedProperty(value = "#{selectedTerme}")
    private SelectedTerme selectedTerme;
    @ManagedProperty(value = "#{langueBean}")
    private LanguageBean langueBean;

    @PostConstruct
    public void init() {
        geoModel = new DefaultMapModel();
    }

    /*
    public void onGeocode(GeocodeEvent event) {
        boolean premiere = true;
        List<GeocodeResult> results = event.getResults();
        geoModel = new DefaultMapModel();
        if (results != null && !results.isEmpty()) {
            LatLng center = results.get(0).getLatLng();
            centerGeoMap = center.getLat() + "," + center.getLng();

            for (int i = 0; i < results.size(); i++) {
                GeocodeResult result = results.get(i);
                geoModel.addOverlay(new Marker(result.getLatLng(), result.getAddress()));
                if (premiere) {
                    latitud = result.getLatLng().getLat();
                    longitud = result.getLatLng().getLng();
                    premiere = false;
                }
            }
        }
    }
     */
    public boolean addCoordinates(String idConcept, String idTheso) {
        GpsHelper gpshelper = new GpsHelper();
        gpshelper.insertCoordonees(connect.getPoolConnexion(), idConcept, idTheso, latitud, longitud);

        return true;
    }

    public boolean doAll(String idC, String id_Theso,
            int id_user, String langEnCour, String idTerm) throws ParserConfigurationException {

        boolean status = false;
        boolean found = false;
        if (codeid != null) {
            for (NodeAlignment na : listAlignValues) {
                if (na.getIdUrl() == null ? codeid == null : na.getIdUrl().equals(codeid) && !found) {
                    alignment_choisi = na;
                    found = true;
                }
            }
            if (found) {
                latitud = alignment_choisi.getLat();
                longitud = alignment_choisi.getLng();
                addCoordinates(idC, id_Theso);
                if (nodePreference.isGps_alignementautomatique()) {
                    AlignmentHelper alignmentHelper = new AlignmentHelper();
                    if (alignmentHelper.addNewAlignment(connect.getPoolConnexion(), id_user, alignment_choisi.getName(), alignementPreferences.getSource(),
                            alignment_choisi.getIdUrl(), 1, idC, id_Theso, nodePreference.getGps_id_source())) {
                        if (nodePreference.isGps_integrertraduction()) {
                            LanguageHelper languageHelper = new LanguageHelper();
                            listLanguesInTheso = new ArrayList<>();
                            listLanguesInTheso = languageHelper.getLanguagesOfThesaurus(connect.getPoolConnexion(), id_Theso);
                            //theso.    languesTheso
                            for (NodeLang languesOfGps : alignment_choisi.getAlltraductions()) {
                                for (Languages_iso639 langueTheso : listLanguesInTheso) {
                                    if (langueTheso.getId_iso639_1().equals(languesOfGps.getCode())) {
                                        Term term = new Term();
                                        term.setLexical_value(languesOfGps.getValue());
                                        term.setId_term(idTerm);
                                        term.setId_thesaurus(id_Theso);
                                        term.setLang(languesOfGps.getCode());
                                        if (nodePreference.isGps_reemplacertraduction()) {
                                            if (!new TermHelper().updateTermTraduction(connect.getPoolConnexion(), term, id_user)) {
                                                return false;
                                            }
                                        } else if (!new TermHelper().isExitsTraduction(connect.getPoolConnexion(), term)) {
                                            if (!new TermHelper().updateTermTraduction(connect.getPoolConnexion(), term, id_user)) {
                                                return false;
                                            }
                                        }
                                    }

                                }

                            }
                        }
                    }
                    nextPosition();
                }
            }
            selectedTerme.majLangueConcept();
            selectedTerme.setAlign(new AlignmentHelper().getAllAlignmentOfConcept(connect.getPoolConnexion(), idC, id_Theso));

            initcoordonees();
        }
        return status;
    }

    private void initcoordonees() {
        latitud = 0.0;
        longitud = 0.0;
    }

    public void reinitBoolean() {
        integrerTraduction = true;
        reemplacerTraduction = true;
        alignementAutomatique = true;
    }

    public void validateParamretagesGps(String id_Theso, String id_lang) {
        boolean status = true;
        if (selectedAlignement != null) {
            for (AlignementSource alignementSource : alignementSources) {
                if (alignementSource.getSource() == null ? selectedAlignement == null : alignementSource.getSource().equals(selectedAlignement)) {
                    alignementPreferences = alignementSource;
                }
            }
            GpsHelper gpsHelper = new GpsHelper();
            if (!gpsHelper.updateTablePreferences(connect.getPoolConnexion(), id_Theso,
                    integrerTraduction, reemplacerTraduction, alignementAutomatique, alignementPreferences.getId())) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", "Ne peux pas faire uptdate de preferences"));
                //message error
                status = false;
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", "Vous besoin selectionée une source"));
        }
        if (status) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", " validation de parametrisage fait!"));
        }

    }

    public void creerAlignAuto(String idC, String idTheso, String nom, String idlangue) throws ParserConfigurationException, SAXException {

        UserHelper userHelper = new UserHelper();
        nodePreference = userHelper.getThesaurusPreference(connect.getPoolConnexion(), idTheso);
        if (nodePreference != null) {

            GpsQuery gpsQuery = new GpsQuery();
            GpsHelper gpsHelper = new GpsHelper();

            alignementPreferences = gpsHelper.find_alignement_gps(connect.getPoolConnexion(), nodePreference.getGps_id_source());
            listAlignValues = new ArrayList<>();

            if ("REST".equalsIgnoreCase(alignementPreferences.getTypeRequete())) {
                if ("xml".equals(alignementPreferences.getAlignement_format())) {

                    listAlignValues = gpsQuery.queryGps2(idC, idTheso, nom.trim(), idlangue, alignementPreferences.getRequete());
                }
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", "Ne peux pas recuperer le preferences"));
            //message ne peux pas recuperer le preferences
        }
    }

    public void setListeAlignementSources(String idTheso) {
        int role = theUser.getUser().getIdRole();
        if (role == 1 || role == 2) {
            AlignmentHelper alignmentHelper = new AlignmentHelper();
            listeAlignementSources = alignmentHelper.getAlignementSourceSAdmin(connect.getPoolConnexion());
        }
    }

    // test Géonames
/*        public void test() {
        try {
            WebService.setUserName("demo"); // add your username here
            
            ToponymSearchCriteria searchCriteria = new ToponymSearchCriteria();
            searchCriteria.setQ("zurich");
            ToponymSearchResult searchResult = WebService.search(searchCriteria);
            for (Toponym toponym : searchResult.getToponyms()) {
                System.out.println(toponym.getName()+" "+ toponym.getCountryName());
            }
        } catch (Exception ex) {
            Logger.getLogger(GpsHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
     */
    /////////////////*******************/////////////////////
    /////////////////*******************/////////////////////
    /////////////////   GPS PAR LOT     /////////////////////
    /////////////////*******************/////////////////////
    /////////////////*******************/////////////////////
    /**
     * Permet de savoir combien d'enfants a le concept selectionnée
     *
     * @param id_Theso
     * @param id_Concept
     */
    public void getListChildren(String id_Theso, String id_Concept, String id_lang) {
        reinitTotal();
        id_concept = id_Concept;
        id_theso = id_Theso;
        id_langue = id_lang;
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

    /**
     * reinicialitation du variables
     */
    public void reinitTotal() {
        listOfChildrenInConcept = null;
        nomduterm = "";
        last = false;
        fin = false;
        position = 0;
        first = true;
        last = false;
    }

    /**
     * cherche l'alignement que on a selectionée dans l'arrayList d'alignements
     * et ce fait l'apelation a la funtion pour ajouter l'alignement
     *
     * public void addAlignement() {
     *
     * AlignmentHelper alignmentHelper = new AlignmentHelper(); if
     * (!alignmentHelper.dejaAligneParAvecCetteAlignement(connect.getPoolConnexion(),
     * id_concept, id_theso, alignementPreferences.getId()) || mettreAJour) { if
     * (uriSelection.isEmpty()) { erreur = "no selection d'alignement"; message
     * = ""; } else { for (NodeAlignment nodeAlignment :
     * selectedTerme.getListAlignValues()) { if
     * (nodeAlignment.getUri_target().equals(uriSelection)) { nodeAli =
     * nodeAlignment; message = "l'alignement va se faire <br>";
     * doAll(id_concept, id_theso, position, erreur, id_theso) message +=
     * selectedTerme.getMessageAlig(); nodeAli = null; nextPosition();
     * uriSelection = null; message += "<br>C'est fini pour cetter concept";
     * return; } } } } else { nextPosition(); } }
     */
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
        if (!mettreAJour) {
            position++;
            if (position < listOfChildrenInConcept.size()) {
                id_concept = listOfChildrenInConcept.get(position);
            }
            comprobationFin();
            nomduterm = conceptHelper.getLexicalValueOfConcept(connect.getPoolConnexion(), id_concept,
                    selectedTerme.getIdTheso(), selectedTerme.getIdlangue());
        } else {
            if (position < listOfChildrenInConcept.size()) {
                id_concept = listOfChildrenInConcept.get(position);
            }
            comprobationFin();
            nomduterm = conceptHelper.getLexicalValueOfConcept(connect.getPoolConnexion(), id_concept,
                    selectedTerme.getIdTheso(), selectedTerme.getIdlangue());
            position++;
        }
        try {
            creerAlignAuto(id_concept, id_theso, nomduterm, id_langue);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(GpsBeans.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(GpsBeans.class.getName()).log(Level.SEVERE, null, ex);
        }

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
     * Permet de savoir le premiere element que on besoin montrer
     *
     * @param id_Concept
     * @param idTheso
     * @param id_lang
     */
    public void getPreliereElement(String id_Concept, String idTheso, String id_lang) throws ParserConfigurationException, SAXException {
        AlignmentHelper alignmentHelper = new AlignmentHelper();
        UserHelper userHelper = new UserHelper();
        ConceptHelper conceptHelper = new ConceptHelper();
        GpsHelper gpsHelper = new GpsHelper();
        nodePreference = userHelper.getThesaurusPreference(connect.getPoolConnexion(), idTheso);
        alignementPreferences = gpsHelper.find_alignement_gps(connect.getPoolConnexion(), nodePreference.getGps_id_source());

        if (!mettreAJour) {
            //nextPosition();
            //listOfChildrenInConcept = chercherSansAligne();
            if (listOfChildrenInConcept != null) {
                id_concept = listOfChildrenInConcept.get(0);
                nomduterm = conceptHelper.getLexicalValueOfConcept(connect.getPoolConnexion(), listOfChildrenInConcept.get(0),
                        selectedTerme.getIdTheso(), selectedTerme.getIdlangue());
                creerAlignAuto(listOfChildrenInConcept.get(0), idTheso, nomduterm, id_lang);
            }
        } else {
            nomduterm = conceptHelper.getLexicalValueOfConcept(connect.getPoolConnexion(), id_concept,
                    selectedTerme.getIdTheso(), selectedTerme.getIdlangue());
            creerAlignAuto(id_concept, idTheso, nomduterm, id_lang);
        }
    }

    public void doForLot(int id_user) {
        ConceptHelper conceptHelper = new ConceptHelper();
        String id_term = conceptHelper.getLexicalValueOfConcept(connect.getPoolConnexion(),
                id_concept, id_theso, id_langue);
        try {
            doAll(id_concept, id_theso, id_user, id_langue, id_term);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(GpsBeans.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private ArrayList<String> chercherSansAligne() {
        ArrayList<String> listOfChildrenInConceptSansAligne = new ArrayList<>();
        AlignmentHelper alignmentHelper = new AlignmentHelper();
        for (String iterator : listOfChildrenInConcept) {
            if (!alignmentHelper.dejaAligneParAvecCetteAlignement(connect.getPoolConnexion(), iterator, id_theso, nodePreference.getGps_id_source())) {
                listOfChildrenInConceptSansAligne.add(iterator);
            }
        }
        return listOfChildrenInConceptSansAligne;
    }

    ///////////////GET & SET////////////////////////////
    public MapModel getGeoModel() {
        return geoModel;
    }

    public void setGeoModel(MapModel geoModel) {
        this.geoModel = geoModel;
    }

    public String getCenterGeoMap() {
        return centerGeoMap;
    }

    public void setCenterGeoMap(String centerGeoMap) {
        this.centerGeoMap = centerGeoMap;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public Connexion getConnect() {
        return connect;
    }

    public void setConnect(Connexion connect) {
        this.connect = connect;
    }

    public String getCoordennees() {
        return coordennees;
    }

    public void setCoordennees(String coordennees) {
        this.coordennees = coordennees;
    }

    public ArrayList<AlignementSource> getAlignementSources() {
        return alignementSources;
    }

    public void setAlignementSources(ArrayList<AlignementSource> alignementSources) {
        this.alignementSources = alignementSources;
    }

    public ArrayList<NodeAlignment> getListAlignValues() {
        return listAlignValues;
    }

    public void setListAlignValues(ArrayList<NodeAlignment> listAlignValues) {
        this.listAlignValues = listAlignValues;
    }

    public String getSelectedAlignement() {
        return selectedAlignement;
    }

    public void setSelectedAlignement(String selectedAlignement) {
        this.selectedAlignement = selectedAlignement;
    }

    public ArrayList<AlignementSource> getListeAlignementSources() {
        return listeAlignementSources;
    }

    public void setListeAlignementSources() {
        GpsHelper gpsHelper = new GpsHelper();
        alignementSources = gpsHelper.getAlignementSource(connect.getPoolConnexion());
        if (!alignementSources.isEmpty()) {
            selectedAlignement = alignementSources.get(0).getSource();
        }
    }

    public CurrentUser getTheUser() {
        return theUser;
    }

    public void setTheUser(CurrentUser theUser) {
        this.theUser = theUser;
    }

    public NodeAlignment getAlignment_choisi() {
        return alignment_choisi;
    }

    public void setAlignment_choisi(NodeAlignment alignment_choisi) {
        this.alignment_choisi = alignment_choisi;
    }

    public String getCodeid() {
        return codeid;
    }

    public void setCodeid(String codeid) {
        this.codeid = codeid;
    }

    public boolean isIntegrerTraduction() {
        return integrerTraduction;
    }

    public void setIntegrerTraduction(boolean integrerTraduction) {
        this.integrerTraduction = integrerTraduction;
    }

    public boolean isReemplacerTraduction() {
        return reemplacerTraduction;
    }

    public void setReemplacerTraduction(boolean reemplacerTraduction) {
        this.reemplacerTraduction = reemplacerTraduction;
    }

    public boolean isAlignementAutomatique() {
        return alignementAutomatique;
    }

    public void setAlignementAutomatique(boolean alignementAutomatique) {
        this.alignementAutomatique = alignementAutomatique;
    }

    public AlignementSource getAlignementPreferences() {
        return alignementPreferences;
    }

    public void setAlignementPreferences(AlignementSource alignementPreferences) {
        this.alignementPreferences = alignementPreferences;
    }

    public ArrayList<Languages_iso639> getListLanguesInTheso() {
        return listLanguesInTheso;
    }

    public void setListLanguesInTheso(ArrayList<Languages_iso639> listLanguesInTheso) {
        this.listLanguesInTheso = listLanguesInTheso;
    }

    public SelectedTerme getSelectedTerme() {
        return selectedTerme;
    }

    public void setSelectedTerme(SelectedTerme selectedTerme) {
        this.selectedTerme = selectedTerme;
    }

    public LanguageBean getLangueBean() {
        return langueBean;
    }

    public void setLangueBean(LanguageBean langueBean) {
        this.langueBean = langueBean;
    }

    public String getId_concept() {
        return id_concept;
    }

    public void setId_concept(String id_concept) {
        this.id_concept = id_concept;
    }

    public String getId_theso() {
        return id_theso;
    }

    public void setId_theso(String id_theso) {
        this.id_theso = id_theso;
    }

    public String getNomduterm() {
        return nomduterm;
    }

    public void setNomduterm(String nomduterm) {
        this.nomduterm = nomduterm;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    public boolean isMettreAJour() {
        return mettreAJour;
    }

    public void setMettreAJour(boolean mettreAJour) {
        this.mettreAJour = mettreAJour;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErreur() {
        return erreur;
    }

    public void setErreur(String erreur) {
        this.erreur = erreur;
    }

    public ArrayList<String> getListOfChildrenInConcept() {
        return listOfChildrenInConcept;
    }

    public void setListOfChildrenInConcept(ArrayList<String> listOfChildrenInConcept) {
        this.listOfChildrenInConcept = listOfChildrenInConcept;
    }

    public String getUriSelection() {
        return uriSelection;
    }

    public void setUriSelection(String uriSelection) {
        this.uriSelection = uriSelection;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isFin() {
        return fin;
    }

    public void setFin(boolean fin) {
        this.fin = fin;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public String getId_langue() {
        return id_langue;
    }

    public void setId_langue(String id_langue) {
        this.id_langue = id_langue;
    }

    public int getAlignement_id_type() {
        return alignement_id_type;
    }

    public void setAlignement_id_type(int alignement_id_type) {
        this.alignement_id_type = alignement_id_type;
    }
}
