package mom.trd.opentheso.SelectedBeans;

import com.zaxxer.hikari.HikariDataSource;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import mom.trd.opentheso.bdd.datas.Concept;
import mom.trd.opentheso.bdd.helper.Connexion;
import mom.trd.opentheso.bdd.datas.Term;
import mom.trd.opentheso.bdd.helper.ConceptHelper;
import mom.trd.opentheso.bdd.helper.FacetHelper;
import mom.trd.opentheso.bdd.helper.GroupHelper;
import mom.trd.opentheso.bdd.helper.OrphanHelper;
import mom.trd.opentheso.bdd.helper.RelationsHelper;
import mom.trd.opentheso.bdd.helper.TermHelper;
import mom.trd.opentheso.bdd.helper.nodes.NodeAutoCompletion;
import mom.trd.opentheso.bdd.helper.nodes.NodeBT;
import mom.trd.opentheso.bdd.helper.nodes.NodeNT;
import mom.trd.opentheso.bdd.helper.nodes.concept.NodeConceptExport;
import mom.trd.opentheso.core.exports.old.WriteFileSKOS;
import org.primefaces.event.SelectEvent;

@ManagedBean(name = "autoComp", eager = true)
@SessionScoped

public class AutoCompletBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private NodeAutoCompletion selectedAtt;
    private String idOld;
    private String facetEdit;

    @ManagedProperty(value = "#{theso}")
    private SelectedThesaurus theso;

    @ManagedProperty(value = "#{treeBean}")
    private TreeBean tree;

    @ManagedProperty(value = "#{poolConnexion}")
    private Connexion connect;

    @ManagedProperty(value = "#{selectedTerme}")
    private SelectedTerme terme;

    @ManagedProperty(value = "#{selectedCandidat}")
    private SelectedCandidat candidat;

    @ManagedProperty(value = "#{langueBean}")
    private LanguageBean langueBean;

    public List<NodeAutoCompletion> completTerm(String query) {
        selectedAtt = new NodeAutoCompletion();
        List<NodeAutoCompletion> liste = new ArrayList<>();
        if (theso.getThesaurus().getId_thesaurus() != null && theso.getThesaurus().getLanguage() != null) {
            liste = new TermHelper().getAutoCompletionTerm(connect.getPoolConnexion(), theso.getThesaurus().getId_thesaurus(),
                    theso.getThesaurus().getLanguage(), query);
        }
        return liste;
    }
    
    public void onItemSelect(SelectEvent event) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Item Selected", event.getObject().toString()));
    }
    
    
    public List<NodeAutoCompletion> completSearchTerm(String query) {
        selectedAtt = new NodeAutoCompletion();
        List<NodeAutoCompletion> liste = new ArrayList<>();
        if (theso.getThesaurus().getId_thesaurus() != null && theso.getThesaurus().getLanguage() != null) {
            liste = new TermHelper().getAutoCompletionTerm(connect.getPoolConnexion(), theso.getThesaurus().getId_thesaurus(),
                    theso.getThesaurus().getLanguage(), query);
        }
        return liste;
    }    

    public List<NodeAutoCompletion> completGroup(String query) {
        selectedAtt = new NodeAutoCompletion();
        List<NodeAutoCompletion> liste = new ArrayList<>();
        if (theso.getThesaurus().getId_thesaurus() != null && theso.getThesaurus().getLanguage() != null) {
            liste = new GroupHelper().getAutoCompletionGroup(connect.getPoolConnexion(), theso.getThesaurus().getId_thesaurus(),
                    theso.getThesaurus().getLanguage(), query);
        }
        return liste;
    }

    public List<NodeAutoCompletion> completNvxCandidat(String query) {
        selectedAtt = new NodeAutoCompletion();
        List<NodeAutoCompletion> liste = new TermHelper().getAutoCompletionTerm(connect.getPoolConnexion(), candidat.getIdTheso(), candidat.getLangueTheso(), query);
        return liste;
    }

    /**
     * Ajoute un terme associé
     */
    public void newTAsso() {
        if (selectedAtt == null || selectedAtt.getIdConcept().equals("")) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("autoComp.error1")));
        } else {
            Term laValeur = terme.getTerme(selectedAtt.getIdConcept());
            if (laValeur == null) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("autoComp.error2")));
            } else {
                terme.creerTermeAsso(selectedAtt.getIdConcept());
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", laValeur.getLexical_value() + " " + langueBean.getMsg("autoComp.info1")));
            }
            selectedAtt = new NodeAutoCompletion();
        }
    }

    /**
     * Ajoute un terme générique
     */
    public void newTGene() {
        if (selectedAtt == null || selectedAtt.getIdConcept().equals("")) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("autoComp.error1")));
        } else {
            Term laValeur = terme.getTerme(selectedAtt.getIdConcept());
            if (laValeur == null) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("autoComp.error2")));
            } else {
                terme.creerTermeGene(selectedAtt.getIdConcept());
                tree.reInit();
                tree.reExpand();
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", laValeur.getLexical_value() + " " + langueBean.getMsg("autoComp.info1")));
            }
            selectedAtt = new NodeAutoCompletion();
        }
    }

    /**
     * Cette fonction permet d'ajouter une relation TG à un concept Le TG existe
     * déjà dans le thésaurus, donc c'est une relation à créer
     *
     * @return
     */
    public boolean addTGene() {

        // selectedAtt.getIdConcept() est le terme TG à ajouter
        // terme.getIdC() est le terme séléctionné dans l'arbre
        // terme.getIdTheso() est l'id du thésaurus
        if (selectedAtt == null || selectedAtt.getIdConcept().equals("")) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("autoComp.error1")));
            return false;
        }
        if (selectedAtt.getIdConcept().equals(terme.getIdC())) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("autoComp.impossible")));
            return false;
        }

        // addTermeGene(idNT, idBT)
        if (!terme.addTermeGene(terme.getIdC(), selectedAtt.getIdConcept())) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("autoComp.error2")));
            return false;
        }

        tree.reInit();
        tree.reExpand();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", selectedAtt.getTermLexicalValue() + " " + langueBean.getMsg("autoComp.info1")));
        selectedAtt = new NodeAutoCompletion();
        return true;
    }

    /**
     * Ajoute un terme spécifique
     */
    public void newTSpe() {
        if (selectedAtt == null || selectedAtt.getIdConcept().equals("")) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("autoComp.error1")));
            return;
        }

        Term laValeur = terme.getTerme(selectedAtt.getIdConcept());

        if (laValeur == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("autoComp.error2")));
        } else {
            terme.creerTermeSpe(selectedAtt.getIdConcept());
            tree.reInit();
            tree.reExpand();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", laValeur.getLexical_value() + " " + langueBean.getMsg("autoComp.info1")));
        }
        selectedAtt = new NodeAutoCompletion();

    }

    /**
     * Ajoute une facette
     */
    public void newFacette() {
        if (selectedAtt == null || selectedAtt.getIdConcept().equals("")) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("autoComp.error1")));
        } else if (facetEdit.trim().equals("")) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("autoComp.error3")));
        } else {
            theso.creerFacette(selectedAtt.getIdConcept(), facetEdit);
            facetEdit = "";
        }
    }

    /**
     * Déplace la branche d'un arbre de idOld à l'id du selected att déplacement
     * de la branche d'un concept vers un autre concept (pas d'un domaine)
     *
     * @return
     */
    public boolean moveBranche() {
        if (selectedAtt == null || selectedAtt.getIdConcept().equals("")) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("autoComp.error1")));
            return false;
        }
        ConceptHelper ch = new ConceptHelper();
        HikariDataSource ds = connect.getPoolConnexion();

        ArrayList<String> domsOld = ch.getListGroupIdOfConcept(ds, idOld, terme.getIdTheso());
        ArrayList<String> domsNew = ch.getListGroupIdOfConcept(ds, selectedAtt.getIdConcept(), terme.getIdTheso());
        // pas de changement de domaine.
        if (!domsOld.equals(domsNew)) {
            // fonction pour nettoyer les anciens domaines et remplacer par le nouveau
            // présente un bug à corriger
            recursiveMoveBranche(ds, terme.getIdC(), idOld, domsOld, domsNew);
        }
        /*    else 
         if(!selectedAtt.getIdGroup().equalsIgnoreCase(terme.getIdDomaine())) {
         recursiveMoveBranche(ds, terme.getIdC(), idOld, domsOld, domsNew);
         }
         */
        if (!new ConceptHelper().moveBranch(ds,
                terme.getIdC(), idOld, selectedAtt.getIdConcept(), terme.getIdTheso(), terme.getUser().getUser().getId())) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("error") + " :", langueBean.getMsg("error")));
            return false;
        }

        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("autoComp.info2")));

        tree.reInit();
        tree.reExpand();
        terme.getVue().setMoveBranch(0);
        return true;
    }

    /**
     * Déplace la branche du thésaurus, d'un domaine à un concept
     *
     * @return
     */
    public boolean moveBrancheFromDomain() {
        // idOld = MT actuel, c'est le domaine 
        // selectedAtt.getIdConcept = l'id du concept de destination
        // terme.getIdC = le concept sélectionné
        // List selectedNode (c'est le noeud complet sélectionné)

        if (selectedAtt == null || selectedAtt.getIdConcept().equals("")) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("autoComp.error1")));
            return false;
        }

        try {
            Connection conn = connect.getPoolConnexion().getConnection();
            conn.setAutoCommit(false);
            ConceptHelper ch = new ConceptHelper();
            ArrayList<String> domsOld = ch.getListGroupIdOfConcept(connect.getPoolConnexion(), terme.getIdC(), terme.getIdTheso());
            ArrayList<String> domsNew = ch.getListGroupIdOfConcept(connect.getPoolConnexion(), selectedAtt.getIdConcept(), terme.getIdTheso());
            
            // on vérifie s'il on change de domaine, si oui, on supprime l'ancien domaine, et on affecte le nouveau domaine à la branche.
            if (!domsOld.equals(domsNew)) {
                // fonction pour nettoyer les anciens domaines et remplacer par le nouveau
                for (String domsOld1 : domsOld) {
                    if (!deleteAllDomainOfBranch(conn, terme.getIdC(), domsOld1, terme.getIdTheso())) {
                        conn.rollback();
                        conn.close();
                        return false;
                    }
                }
                for (String domsNew1 : domsNew) {
                    // on ajoute le nouveau domaine à la branche
                    if (!setDomainToBranch(conn, terme.getIdC(), domsNew1, terme.getIdTheso())) {
                        conn.rollback();
                        conn.close();
                        return false;
                    }
                }

            }

            for (String domsNew1 : domsNew) {
                // on déplace la branche au nouveau concept puis création de TG-TS (on ajoute la relation BT du concept, puis on supprime  
                // au concept la relation TT
                if (!new ConceptHelper().moveBranchFromMT(conn, terme.getIdC(),
                        selectedAtt.getIdConcept(),
                        domsNew1,
                        terme.getIdTheso(),
                        terme.getUser().getUser().getId())) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("error") + " :", langueBean.getMsg("error")));
                    conn.rollback();
                    conn.close();
                    return false;
                }
            }

            // On distingue la branche si elle vient des orphelins ou non.
            if (terme.getIdDomaine().trim().equalsIgnoreCase("orphan")) {    
                if (!new OrphanHelper().deleteOrphan(conn, terme.getIdC(), terme.getIdTheso())) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("error") + " :", langueBean.getMsg("error")));
                    conn.rollback();
                    conn.close();
                    return false;
                }
            }
            conn.commit();
            conn.close();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("autoComp.info2")));

            tree.reInit();
            tree.reExpand();
            terme.getVue().setMoveBranchFromMT(0);
            terme.getVue().setMoveBranchFromOrphin(0);
            selectedAtt = new NodeAutoCompletion();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(AutoCompletBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     * Déplace la branche d'un arbre de idConcept à un Group ou domaine
     *
     * @return
     */
    public boolean moveBrancheToDomain() {
        // idOld = TG actuel
        // selectedAtt.getIdGroup() = l'id  domaine de destination
        // terme.getIdC = le concept sélectionné
        try {
            Connection conn = connect.getPoolConnexion().getConnection();
            conn.setAutoCommit(false);
            /*
             On distingue la branche si elle vient des orphelins ou non.
             */
            if (terme.getIdDomaine().trim().equalsIgnoreCase("orphan")) {
                if (selectedAtt == null || selectedAtt.getIdGroup().equals("")) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("autoComp.error1")));
                    return false;
                }
                ConceptHelper ch = new ConceptHelper();

                ArrayList<String> domsOld = ch.getListGroupIdOfConcept(connect.getPoolConnexion(), terme.getIdC(), terme.getIdTheso());

                // on vérifie s'il on change de domaine, si oui, on supprime l'ancien domaine, et on affecte le nouveau domaine à la branche.
                if (!domsOld.contains(selectedAtt.getIdGroup())) {
                    // fonction pour nettoyer les anciens domaines et remplacer par le nouveau
                    for (String domsOld1 : domsOld) {
                        if (!deleteAllDomainOfBranch(conn, terme.getIdC(), domsOld1, terme.getIdTheso())) {
                            conn.rollback();
                            conn.close();
                            return false;
                        }
                    }
                    // on ajoute le nouveau domaine à la branche
                    if (!setDomainToBranch(conn, terme.getIdC(), selectedAtt.getIdGroup(), terme.getIdTheso())) {
                        conn.rollback();
                        conn.close();
                        return false;
                    }
                }

                // on déplace la branche au domaine (on coupe les relations BT du concept, puis on afecte 
                // au concept la relation TT
                if (!ch.moveBranchToMT(conn, terme.getIdC(),
                        idOld, selectedAtt.getIdGroup(), terme.getIdTheso(),
                        terme.getUser().getUser().getId())) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("error") + " :", langueBean.getMsg("error")));
                    conn.rollback();
                    conn.close();
                    return false;
                }
                if (!new OrphanHelper().deleteOrphan(conn, terme.getIdC(), terme.getIdTheso())) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("error") + " :", langueBean.getMsg("error")));
                    conn.rollback();
                    conn.close();
                    return false;
                }
            } else {

                if (selectedAtt == null || selectedAtt.getIdGroup().equals("")) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("autoComp.error1")));
                    return false;
                }
                ConceptHelper ch = new ConceptHelper();

                ArrayList<String> domsOld = ch.getListGroupIdOfConcept(connect.getPoolConnexion(), idOld, terme.getIdTheso());

                // on vérifie s'il on change de domaine, si oui, on supprime l'ancien domaine, et on affecte le nouveau domaine à la branche.
                if (!domsOld.contains(selectedAtt.getIdGroup())) {
                    // fonction pour nettoyer les anciens domaines et remplacer par le nouveau
                    for (String domsOld1 : domsOld) {
                        if (!deleteAllDomainOfBranch(conn, terme.getIdC(), domsOld1, terme.getIdTheso())) {
                            conn.rollback();
                            conn.close();
                            return false;
                        }
                    }
                    // on ajoute le nouveau domaine à la branche
                    if (!setDomainToBranch(conn, terme.getIdC(), selectedAtt.getIdGroup(), terme.getIdTheso())) {
                        conn.rollback();
                        conn.close();
                        return false;
                    }
                }

                // on déplace la branche au domaine (on coupe les relations BT du concept, puis on afecte 
                // au concept la relation TT
                if (!new ConceptHelper().moveBranchToMT(conn, terme.getIdC(),
                        idOld, selectedAtt.getIdGroup(), terme.getIdTheso(),
                        terme.getUser().getUser().getId())) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("error") + " :", langueBean.getMsg("error")));
                    conn.rollback();
                    conn.close();
                    return false;
                }
            }
            conn.commit();
            conn.close();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("autoComp.info2")));

            tree.reInit();
            tree.reExpand();
            terme.getVue().setMoveBranchToMT(0);
            terme.getVue().setMoveBranchFromOrphinToMT(0);
            selectedAtt = new NodeAutoCompletion();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(AutoCompletBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     * Fonction qui permet de supprimer un domaine de la branche donnée avec un
     * concept de tête un domaine et thesaurus
     *
     * @param conn
     * @param idConceptDeTete
     * @param idGroup
     * @param idTheso
     * @return
     */
    private boolean deleteAllDomainOfBranch(Connection conn, String idConceptDeTete,
            String idGroup, String idTheso) {

        ConceptHelper conceptHelper = new ConceptHelper();
        RelationsHelper relationsHelper = new RelationsHelper();

        ArrayList<String> listIdsOfConceptChildren
                = conceptHelper.getListChildrenOfConcept(connect.getPoolConnexion(),
                        idConceptDeTete, idTheso);

        if (!relationsHelper.deleteRelationMT(conn, idConceptDeTete, idGroup, idTheso)) {
            return false;
        }

        for (String listIdsOfConceptChildren1 : listIdsOfConceptChildren) {
      //      if(!relationsHelper.deleteRelationMT(conn, listIdsOfConceptChildren1, idGroup, idTheso))
            //          return false;
            deleteAllDomainOfBranch(conn, listIdsOfConceptChildren1,
                    idGroup, idTheso);
        }
        return true;
    }

    /**
     * Fonction qui permet de supprimer un domaine de la branche donnée avec un
     * concept de tête un domaine et thesaurus
     *
     * @param conn
     * @param idConceptDeTete
     * @param idGroup
     * @param idTheso
     * @return
     */
    private boolean setDomainToBranch(Connection conn, String idConceptDeTete,
            String idGroup, String idTheso) {

        ConceptHelper conceptHelper = new ConceptHelper();
        RelationsHelper relationsHelper = new RelationsHelper();

        ArrayList<String> listIdsOfConceptChildren
                = conceptHelper.getListChildrenOfConcept(connect.getPoolConnexion(),
                        idConceptDeTete, idTheso);

        if (!relationsHelper.setRelationMT(conn, idConceptDeTete, idGroup, idTheso)) {
            return false;
        }

        for (String listIdsOfConceptChildren1 : listIdsOfConceptChildren) {
            if (!relationsHelper.setRelationMT(conn, listIdsOfConceptChildren1, idGroup, idTheso)) {
                return false;
            }
            setDomainToBranch(conn, listIdsOfConceptChildren1,
                    idGroup, idTheso);
        }
        return true;
    }

    private void recursiveMoveBranche(HikariDataSource ds, String idC, String idOld, ArrayList<String> domsOld, ArrayList<String> domsNew) {
        if (new ConceptHelper().haveChildren(ds, terme.getIdTheso(), idC)) {
            List<NodeNT> children = new RelationsHelper().getListNT(ds, idC, terme.getIdTheso(), terme.getIdlangue());
            for (NodeNT nnt : children) {
                recursiveMoveBranche(ds, nnt.getIdConcept(), idC, domsOld, domsNew);
            }
        }
        ConceptHelper ch = new ConceptHelper();
        ArrayList<NodeBT> tempBT = new RelationsHelper().getListBT(ds, idC, terme.getIdTheso(), terme.getIdlangue());
        ArrayList<String> idParents = new ArrayList<>();
        for (NodeBT bt : tempBT) {
            idParents.add(bt.getIdConcept());
        }
        ArrayList<String> domParent = ch.getListGroupIdParentOfConceptOtherThan(ds, idParents, terme.getIdTheso(), idOld);
        for (String domNew : domsNew) {
            if (!domParent.contains(domNew)) {
                Concept c = ch.getThisConcept(ds, idC, terme.getIdTheso());
                c.setIdGroup(domNew);
                try {
                    ch.addNewGroupOfConcept(ds.getConnection(), c, terme.getUser().getUser().getId());
                } catch (SQLException ex) {
                    Logger.getLogger(AutoCompletBean.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        for (String domOld : domsOld) {
            if (!domParent.contains(domOld) && !domsNew.contains(domOld)) {
                ch.deleteGroupOfConcept(ds, idC, domOld, terme.getIdTheso(), terme.getUser().getUser().getId());
            }
        }
    }

    public NodeAutoCompletion getSelectedAtt() {
        return selectedAtt;
    }

    public void setSelectedAtt(NodeAutoCompletion selectedAtt) {
        this.selectedAtt = selectedAtt;
    }

    public Connexion getConnect() {
        return connect;
    }

    public void setConnect(Connexion connect) {
        this.connect = connect;
    }

    public SelectedTerme getTerme() {
        return terme;
    }

    public void setTerme(SelectedTerme terme) {
        this.terme = terme;
    }

    public TreeBean getTree() {
        return tree;
    }

    public void setTree(TreeBean tree) {
        this.tree = tree;
    }

    public SelectedCandidat getCandidat() {
        return candidat;
    }

    public void setCandidat(SelectedCandidat candidat) {
        this.candidat = candidat;
    }

    public LanguageBean getLangueBean() {
        return langueBean;
    }

    public void setLangueBean(LanguageBean langueBean) {
        this.langueBean = langueBean;
    }

    public String getIdOld() {
        return idOld;
    }

    public void setIdOld(String idOld) {
        this.idOld = idOld;
    }

    public String getFacetEdit() {
        return facetEdit;
    }

    public void setFacetEdit(String facetEdit) {
        this.facetEdit = facetEdit;
    }

    public SelectedThesaurus getTheso() {
        return theso;
    }

    public void setTheso(SelectedThesaurus theso) {
        this.theso = theso;
    }

}
