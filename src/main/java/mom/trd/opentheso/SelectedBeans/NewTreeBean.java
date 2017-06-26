/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.SelectedBeans;

import com.zaxxer.hikari.HikariDataSource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import mom.trd.opentheso.bdd.datas.HierarchicalRelationship;
import mom.trd.opentheso.bdd.helper.AlignmentHelper;
import mom.trd.opentheso.bdd.helper.ConceptHelper;
import mom.trd.opentheso.bdd.helper.Connexion;
import mom.trd.opentheso.bdd.helper.GroupHelper;
import mom.trd.opentheso.bdd.helper.nodes.NodeAlignment;
import mom.trd.opentheso.bdd.helper.nodes.NodeAutoCompletion;
import mom.trd.opentheso.bdd.helper.nodes.NodeRT;
import mom.trd.opentheso.bdd.helper.nodes.concept.NodeConceptTree;
import mom.trd.opentheso.bdd.helper.nodes.group.NodeGroup;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 *
 * @author Quincy
 */
@ManagedBean(name = "newtreeBean", eager = true)
@SessionScoped

public class NewTreeBean implements Serializable {

    @ManagedProperty(value = "#{poolConnexion}")
    private Connexion connect;

    @ManagedProperty(value = "#{selectedTerme}")
    private SelectedTerme selectedTerme;

    @ManagedProperty(value = "#{vue}")
    private Vue vue;

    @ManagedProperty(value = "#{langueBean}")
    private LanguageBean langueBean;

    @ManagedProperty(value = "#{conceptbean}")
    private ConceptBean conceptbean;

    private TreeNode root;
    private TreeNode selectedNode;
    private ArrayList<TreeNode> selectedNodes;
    private String idThesoSelected;
    private String defaultLanguage;

    public NewTreeBean() {
        root = (TreeNode) new DefaultTreeNode("Root", null);
        selectedNodes = new ArrayList<>();
    }

    private boolean createValid = false;

    /**
     *
     * @param idTheso
     * @param langue
     */
    private String getTypeOfGroup(String typeCode) {
        String type;
        switch (typeCode) {

            case "G":
                type = "group";
                break;
            case "C":
                type = "collection";
                break;
            case "T":
                type = "thème";
                break;
            case "MT":
            default:
                type = "microTheso";
                break;
        }
        return type;
    }

    private String getTypeOfSubGroup(String typeCode) {
        String type;
        switch (typeCode) {

            case "G":
                type = "subGroup";
                break;
            case "C":
                type = "subCollection";
                break;
            case "T":
                type = "subThème";
                break;
            case "MT":
            default:
                type = "subMicroTheso";
                break;
        }
        return type;
    }

    /**
     * Pour détecter les agents d'indexation
     *
     * @return
     */
    public String getBrowserName() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        String userAgent = externalContext.getRequestHeaderMap().get("User-Agent");

        if (userAgent.toLowerCase().contains("slurp")) {
            return "agent";
        }
        if (userAgent.toLowerCase().contains("msnbot")) {
            return "agent";
        }
        if (userAgent.toLowerCase().contains("googlebot")) {
            return "agent";
        }
        return "notagent";
    }

    public void initTree(String idTheso, String langue) {

        //      idThesoSelected = idTheso;
        //      defaultLanguage = langue;
        root = (TreeNode) new DefaultTreeNode("Root", null);

        if (connect.getPoolConnexion() == null) {
            System.err.println("Opentheso n'a pas pu se connecter à la base de données");
            return;
        }
        List<NodeGroup> racineNode = new GroupHelper().getListRootConceptGroup(connect.getPoolConnexion(), idTheso, langue);
        Collections.sort(racineNode);

        // Les premiers noeuds de l'arbre sont de type Groupe (isGroup = true)
        for (NodeGroup nodegroup : racineNode) {

            String typeCode = nodegroup.getConceptGroup().getIdtypecode();
            String type = getTypeOfGroup(typeCode);

            if (nodegroup.getLexicalValue().trim().isEmpty()) {
                TreeNode dynamicTreeNode = (TreeNode) new MyTreeNode(1, nodegroup.getConceptGroup().getIdgroup(),
                        nodegroup.getConceptGroup().getIdthesaurus(),
                        nodegroup.getIdLang(), nodegroup.getConceptGroup().getIdgroup(),
                        nodegroup.getConceptGroup().getIdtypecode(),
                        null,
                        type, nodegroup.getConceptGroup().getIdgroup(), root);
                ((MyTreeNode) dynamicTreeNode).setIsGroup(true);
                new DefaultTreeNode("facette", dynamicTreeNode);
            } else {
                TreeNode dynamicTreeNode = (TreeNode) new MyTreeNode(1, nodegroup.getConceptGroup().getIdgroup(),
                        nodegroup.getConceptGroup().getIdthesaurus(),
                        nodegroup.getIdLang(), nodegroup.getConceptGroup().getIdgroup(),
                        nodegroup.getConceptGroup().getIdtypecode(),
                        null,
                        type, nodegroup.getLexicalValue(), root);
                ((MyTreeNode) dynamicTreeNode).setIsGroup(true);
                new DefaultTreeNode("facette", dynamicTreeNode);
            }

        }
        if (idTheso != null) {
            //loadOrphan(idTheso, langue);
        }

    }

    public void majSearchPermute() {
        selectedTerme.majSearchPermute();
        reInit();
        reExpand();
    }

    public void majSearch() {
        selectedTerme.majSearch();
        vue.setOnglet(1);
        reInit();
        reExpand();

    }

    /**
     * cette fonction permet de déplier le noeud sélectionné "event" est de type
     * TreeNode, donc on a toutes les informations sur le noeud sélectionné
     *
     * @param event
     */
    public void onNodeExpand(NodeExpandEvent event) {

        if (!event.getTreeNode().getType().equals("orphan")) {
            ArrayList<NodeConceptTree> listeSubGroup = new ArrayList<>();
            ArrayList<NodeConceptTree> listeConcept;
            ConceptHelper conceptHelper = new ConceptHelper();
            GroupHelper groupHelper = new GroupHelper();
            int type = 3;

            //<Retirer noeuds fictifs>
            if (event.getTreeNode().getChildCount() == 1) {
                event.getTreeNode().getChildren().remove(0);
            }

            MyTreeNode myTreeNode = (MyTreeNode) event.getTreeNode();

            // id du concept ou group sélectionné qu'il faut déployer
            String idSelectedNode = myTreeNode.getIdMot();

            if (groupHelper.isIdOfGroup(connect.getPoolConnexion(), idSelectedNode, myTreeNode.getIdTheso())) {
                // if (myTreeNode.isIsGroup() || myTreeNode.isIsSubGroup()) { //pour détecter les noeuds type Group/collecton/MT/Thèmes ...
                myTreeNode.setTypeMot(1);//pour group ?
                //      myTreeNode.setIsGroup(true);

                // on récupère la liste des sous_groupes (s'il y en a)
                listeSubGroup = groupHelper.getRelationGroupOf(connect.getPoolConnexion(), idSelectedNode, myTreeNode.getIdTheso(), myTreeNode.getLangue());

                if (listeSubGroup == null) {
                    listeSubGroup = new ArrayList<>();
                }
                // pour récupérer les concepts mélangés avec les Sous_Groupes
                listeConcept = conceptHelper.getListTopConcepts(connect.getPoolConnexion(), idSelectedNode, myTreeNode.getIdTheso(), myTreeNode.getLangue());

            } else {
                listeConcept = conceptHelper.getListConcepts(connect.getPoolConnexion(), idSelectedNode, myTreeNode.getIdTheso(), myTreeNode.getLangue());
            }

            TreeNode treeNode;
            // 1 = domaine/Group, 2 = TT (top Term), 3 = Concept/term 
            // myTreeNode.isIsGroup() myTreeNode.isIsSubGroup()
            // 
            String value = "";
            String idTC = "";
            String icon;
            /**
             * Ajout des Groupes (MT, C, G, T ..)
             */
            for (NodeConceptTree nodeConceptTreeGroup : listeSubGroup) {

                value = nodeConceptTreeGroup.getTitle();
                if (groupHelper.haveSubGroup(connect.getPoolConnexion(), nodeConceptTreeGroup.getIdThesaurus(), nodeConceptTreeGroup.getIdConcept())
                        || nodeConceptTreeGroup.isHaveChildren()) {

                    icon = getTypeOfSubGroup(myTreeNode.getTypeDomaine());

                    treeNode = new MyTreeNode(1, nodeConceptTreeGroup.getIdConcept(), ((MyTreeNode) event.getTreeNode()).getIdTheso(),
                            ((MyTreeNode) event.getTreeNode()).getLangue(), ((MyTreeNode) event.getTreeNode()).getIdDomaine(),
                            ((MyTreeNode) event.getTreeNode()).getTypeDomaine(),
                            idTC, icon, value, event.getTreeNode());
                    ((MyTreeNode) treeNode).setIsSubGroup(true);
                    new DefaultTreeNode("fake", treeNode);
                }
            }

            // Ajout dans l'arbre des concepts
            for (NodeConceptTree nodeConceptTree : listeConcept) {

                if (conceptHelper.haveChildren(connect.getPoolConnexion(), nodeConceptTree.getIdThesaurus(), nodeConceptTree.getIdConcept())
                        || nodeConceptTree.isHaveChildren()) {
                    icon = "dossier";

                    if (nodeConceptTree.isIsTopTerm()) { //Création de topConcepts
                        if (nodeConceptTree.getTitle().trim().isEmpty()) {
                            value = nodeConceptTree.getIdConcept();
                        } else {
                            value = nodeConceptTree.getTitle();
                        }
                        idTC = value;
                    } else { //Création de concepts
                        idTC = ((MyTreeNode) event.getTreeNode()).getIdTopConcept();
                        if (nodeConceptTree.getTitle().trim().isEmpty()) {
                            value = nodeConceptTree.getIdConcept();
                        } else {
                            value = nodeConceptTree.getTitle();
                        }
                    }
                    if (nodeConceptTree.getStatusConcept() != null) {
                        if (nodeConceptTree.getStatusConcept().equals("hidden")) {
                            icon = "hidden";
                        }
                    }
                    treeNode = new MyTreeNode(type, nodeConceptTree.getIdConcept(), ((MyTreeNode) event.getTreeNode()).getIdTheso(),
                            ((MyTreeNode) event.getTreeNode()).getLangue(), ((MyTreeNode) event.getTreeNode()).getIdDomaine(),
                            ((MyTreeNode) event.getTreeNode()).getTypeDomaine(),
                            idTC, icon, value, event.getTreeNode());
                    new DefaultTreeNode("fake", treeNode);
                } else {
                    icon = "fichier";
                    // if (type == 2) { //Création des topConcepts
                    if (nodeConceptTree.isIsTopTerm()) { // cas de TT
                        //type=2;
                        idTC = nodeConceptTree.getIdConcept();
                        if (nodeConceptTree.getTitle().trim().isEmpty()) {
                            value = nodeConceptTree.getIdConcept();
                        } else {
                            value = nodeConceptTree.getTitle();
                        }

                    } else { //Création de concepts
                        //type=3;
                        idTC = ((MyTreeNode) event.getTreeNode()).getIdTopConcept();
                        if (nodeConceptTree.getTitle().trim().isEmpty()) {
                            value = nodeConceptTree.getIdConcept();
                        } else {
                            value = nodeConceptTree.getTitle();
                        }
                    }
                    if (nodeConceptTree.getStatusConcept().equals("hidden")) {
                        icon = "hidden";
                    }
                    new MyTreeNode(type, nodeConceptTree.getIdConcept(), ((MyTreeNode) event.getTreeNode()).getIdTheso(),
                            ((MyTreeNode) event.getTreeNode()).getLangue(), ((MyTreeNode) event.getTreeNode()).getIdDomaine(),
                            ((MyTreeNode) event.getTreeNode()).getTypeDomaine(),
                            idTC, icon, value, event.getTreeNode());
                }
//*/
            }
        }

    }

    /**
     *
     * @param event
     */
    public void onNodeSelect(NodeSelectEvent event) {

        //if (((MyTreeNode) event.getTreeNode()).getIdDomaine() != null) {
        selectedTerme.majTerme((MyTreeNode) selectedNode);
        //}
        vue.setOnglet(0);
        selectedTerme.setTree(0);

    }

    /**
     * Permet de mettre à jour l'arbre et le terme à la sélection d'un index
     * rapide par autocomplétion
     */
    public void majIndexRapidSearch() {
        selectedTerme.majIndexRapidSearch(idThesoSelected, defaultLanguage);
        reInit();
        reExpand();
    }

    public void changeTerme(String id, int type) {

        selectedNode.setSelected(false);

        for (TreeNode node : selectedNodes) {
            node.setSelected(false);
        }

        String idTC;
        if (type == 2) { //On vient d'un domaine
            idTC = id;
        } else {
            idTC = selectedTerme.getIdTopConcept();
        }
        if (type == 0) {
            boolean temp = new ConceptHelper().getThisConcept(connect.getPoolConnexion(), id, selectedTerme.getIdTheso()).isTopConcept();
            if (temp) {
                type = 2;
            } else {
                type = 3;
            }
        }

        MyTreeNode mTN = new MyTreeNode(type, id, selectedTerme.getIdTheso(),
                selectedTerme.getIdlangue(), selectedTerme.getIdDomaine(), selectedTerme.getTypeDomaine(), idTC, null, null, null);
        selectedTerme.majTerme(mTN);

        reExpand();
        vue.setOnglet(0);
    }

    public void reExpand() {
        if (selectedNode == null) {
            //      selectedNode = new MyTreeNode(0, "", "", "", "", "", "domaine", "", root);
        }
        //    selectedNode.setSelected(false);
        for (TreeNode tn : selectedNodes) {
            tn.setSelected(false);
        }
        selectedNodes = new ArrayList<>();
        ArrayList<String> first = new ArrayList<>();
        first.add(selectedTerme.getIdC());
        ArrayList<ArrayList<String>> paths = new ArrayList<>();
        paths = new ConceptHelper().getPathOfConcept(connect.getPoolConnexion(), selectedTerme.getIdC(), selectedTerme.getIdTheso(), first, paths);
        reExpandTree(paths, selectedTerme.getIdTheso(), selectedTerme.getIdlangue());
    }

    /**
     * Expansion automatique de la racine
     *
     * @param listeId
     * @param idTheso
     * @param langue
     */
    private void reExpandTree(ArrayList<ArrayList<String>> listeId, String idTheso, String langue) {
        //  if(selectedNodes.isEmpty()){
        if (root.getChildCount() == 0) {
            // On recrÃ©e la racine
            List<NodeGroup> racineNode = new GroupHelper().getListRootConceptGroup(connect.getPoolConnexion(), idTheso, langue);
            Collections.sort(racineNode);

            for (NodeGroup nodegroup : racineNode) {
                TreeNode dynamicTreeNode;

                String typeCode = nodegroup.getConceptGroup().getIdtypecode();
                String type = getTypeOfGroup(typeCode);

                if (nodegroup.getLexicalValue().trim().isEmpty()) {
                    dynamicTreeNode = (TreeNode) new MyTreeNode(1, nodegroup.getConceptGroup().getIdgroup(), nodegroup.getConceptGroup().getIdthesaurus(),
                            nodegroup.getIdLang(), nodegroup.getConceptGroup().getIdgroup(), nodegroup.getConceptGroup().getIdtypecode(), null,
                            type, nodegroup.getConceptGroup().getIdgroup(), root);
                    ((MyTreeNode) dynamicTreeNode).setIsGroup(true);
                } else {
                    dynamicTreeNode = (TreeNode) new MyTreeNode(1, nodegroup.getConceptGroup().getIdgroup(), nodegroup.getConceptGroup().getIdthesaurus(),
                            nodegroup.getIdLang(), nodegroup.getConceptGroup().getIdgroup(),
                            nodegroup.getConceptGroup().getIdtypecode(), null,
                            type, nodegroup.getLexicalValue(), root);
                    ((MyTreeNode) dynamicTreeNode).setIsGroup(true);
                }

                new DefaultTreeNode("fake", dynamicTreeNode);
                for (ArrayList<String> tabId : listeId) {
                    // Si c'est le chemin, on Ã©tend
                    if (tabId.size() > 1 && tabId.get(0) != null) {
                        if (tabId.get(0).equals(nodegroup.getConceptGroup().getIdgroup())) {
                            reExpandChild(tabId, (MyTreeNode) dynamicTreeNode, 1);
                        }
                    } else {
                        if (tabId.get(1).equals(nodegroup.getConceptGroup().getIdgroup())) {
                            dynamicTreeNode.setSelected(true);
                            selectedNode = dynamicTreeNode;
                            selectedNodes.add(dynamicTreeNode);
                        } else {
                            dynamicTreeNode.setSelected(false);
                        }
                    }
                }
            }
            //loadOrphan(idTheso, langue);
            for (TreeNode tn : root.getChildren()) {
                if (tn.getType().equals("orphan")) {
                    for (TreeNode tn2 : tn.getChildren()) {
                        for (ArrayList<String> tabId : listeId) {
                            if (tabId.size() == 2 && tabId.get(1).equals(((MyTreeNode) tn2).getIdMot())) {
                                tn2.setSelected(true);
                                selectedNode = tn2;
                                selectedNodes.add(tn2);

                            } else {
                                if (tabId.get(1).equals(((MyTreeNode) tn2).getIdMot())) {
                                    reExpandChild(tabId, (MyTreeNode) tn2, 2);
                                }
                            }
                        }
                    }
                }
            }
        } else {
            List<TreeNode> racineNode = root.getChildren();
            for (TreeNode dynamicTreeNode : racineNode) {
                if (!dynamicTreeNode.getType().equals("orphan")) {
                    for (ArrayList<String> tabId : listeId) {
                        // Si c'est le , on Ã©tend
                        if (tabId.size() > 1 && tabId.get(0) != null) {
                            if (tabId.get(0).equals(((MyTreeNode) dynamicTreeNode).getIdDomaine())) {
                                reExpandChild(tabId, (MyTreeNode) dynamicTreeNode, 1);
                            }
                        } else {
                            if (tabId.get(1).equals(((MyTreeNode) dynamicTreeNode).getIdDomaine())) {
                                dynamicTreeNode.setSelected(true);
                                selectedNode = dynamicTreeNode;
                                selectedNodes.add(dynamicTreeNode);
                            } else {
                                dynamicTreeNode.setSelected(false);
                            }
                        }
                    }
                } else {
                    for (TreeNode tn2 : dynamicTreeNode.getChildren()) {
                        for (ArrayList<String> tabId : listeId) {
                            if (tabId.size() == 2 && tabId.get(1).equals(((MyTreeNode) tn2).getIdMot())) {
                                tn2.setSelected(true);
                                selectedNode = tn2;
                                selectedNodes.add(tn2);

                            } else {
                                if (tabId.get(1).equals(((MyTreeNode) tn2).getIdMot())) {
                                    reExpandChild(tabId, (MyTreeNode) tn2, 2);
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    private void reExpandChild(ArrayList<String> listeId, TreeNode node, int cpt) {
        if (!node.isExpanded()) {
            ArrayList<NodeConceptTree> listeConcept;
            ArrayList<NodeConceptTree> listeSubGroup = new ArrayList<>();
            ConceptHelper conceptHelper = new ConceptHelper();
            GroupHelper groupHelper = new GroupHelper();
            int type = 3;

            if (node.getChildCount() == 1) {
                node.getChildren().remove(0);
            }

            MyTreeNode myTreeNode = (MyTreeNode) node;
            String idConcept = myTreeNode.getIdMot();
            if (groupHelper.isIdOfGroup(connect.getPoolConnexion(), idConcept, myTreeNode.getIdTheso())) {

                myTreeNode.setTypeMot(1);//pour group ?
                myTreeNode.setIsGroup(true);

                listeSubGroup = groupHelper.getRelationGroupOf(connect.getPoolConnexion(), idConcept, myTreeNode.getIdTheso(), myTreeNode.getLangue());

                if (listeSubGroup == null) {
                    listeSubGroup = new ArrayList<>();
                }

                // pour récupérer les concepts mélangés avec les Sous_Groupes
                listeConcept = conceptHelper.getListTopConcepts(connect.getPoolConnexion(), idConcept, myTreeNode.getIdTheso(), myTreeNode.getLangue());

            } else {
                listeConcept = conceptHelper.getListConcepts(connect.getPoolConnexion(), idConcept, myTreeNode.getIdTheso(), myTreeNode.getLangue());
            }

            TreeNode treeNode = null;
            String value = "";
            String idTC = "";
            String icon;
            /**
             * Ajout des Groupes (MT, C, G, T ..)
             */
            for (NodeConceptTree nodeConceptTreeGroup : listeSubGroup) {

                value = nodeConceptTreeGroup.getTitle();
                if (groupHelper.haveSubGroup(connect.getPoolConnexion(), nodeConceptTreeGroup.getIdThesaurus(), nodeConceptTreeGroup.getIdConcept())
                        || nodeConceptTreeGroup.isHaveChildren()) {

                    icon = getTypeOfSubGroup(myTreeNode.getTypeDomaine());

                    treeNode = new MyTreeNode(1, nodeConceptTreeGroup.getIdConcept(), myTreeNode.getIdTheso(),
                            myTreeNode.getLangue(), myTreeNode.getIdDomaine(),
                            myTreeNode.getTypeDomaine(),
                            idTC, icon, value, myTreeNode);
                    ((MyTreeNode) treeNode).setIsSubGroup(true);
                    new DefaultTreeNode("fake", treeNode);
                    if (listeId.get(cpt).equals(((MyTreeNode) treeNode).getIdMot())) {
                        if (cpt + 1 < listeId.size()) {
                            treeNode.setSelected(false);
                            reExpandChild(listeId, treeNode, cpt + 1);
                        } else {
                            treeNode.setSelected(true);
                            selectedNode = treeNode;
                            selectedNodes.add(treeNode);
                        }
                    }

                }
            }

            // Ajout dans l'arbre
            for (NodeConceptTree nodeConceptTree : listeConcept) {

                if (conceptHelper.haveChildren(connect.getPoolConnexion(), nodeConceptTree.getIdThesaurus(), nodeConceptTree.getIdConcept())
                        || nodeConceptTree.isHaveChildren()) {
                    icon = "dossier";
                    if (nodeConceptTree.isIsGroup()) {

                        icon = "domaine";
                        //String type = getTypeOfGroup(typeCode);

                    } else if (nodeConceptTree.isIsSubGroup()) {
                        icon = getTypeOfSubGroup(myTreeNode.getTypeDomaine());
                    }

                    if (type == 2) { //CrÃ©ation de topConcepts
                        if (nodeConceptTree.getTitle().trim().isEmpty()) {
                            value = nodeConceptTree.getIdConcept();
                        } else {
                            value = nodeConceptTree.getTitle();
                        }
                        idTC = value;
                    } else { //CrÃ©ation de concepts
                        idTC = ((MyTreeNode) node).getIdTopConcept();
                        if (nodeConceptTree.getTitle().trim().isEmpty()) {
                            value = nodeConceptTree.getIdConcept();
                        } else {
                            value = nodeConceptTree.getTitle();
                        }
                    }
                    if (nodeConceptTree.getStatusConcept() != null) {
                        if (nodeConceptTree.getStatusConcept().equals("hidden")) {
                            icon = "hidden";
                        }
                    }
                    treeNode = new MyTreeNode(type, nodeConceptTree.getIdConcept(), ((MyTreeNode) node).getIdTheso(),
                            ((MyTreeNode) node).getLangue(), ((MyTreeNode) node).getIdDomaine(),
                            ((MyTreeNode) node).getTypeDomaine(),
                            idTC, icon, value, node);

                    /*  if (nodeConceptTree.isIsGroup()) {
                        ((MyTreeNode)tn).setIsGroup(true);
                        ((MyTreeNode)tn).setIsSubGroup(false);
                    } else if (nodeConceptTree.isIsSubGroup()) {
                        ((MyTreeNode)tn).setIsGroup(false);
                        ((MyTreeNode)tn).setIsSubGroup(true);
                    }*/
                    new DefaultTreeNode("fake", treeNode);

                    if (listeId.get(cpt).equals(((MyTreeNode) treeNode).getIdMot())) {
                        if (cpt + 1 < listeId.size()) {
                            treeNode.setSelected(false);
                            reExpandChild(listeId, treeNode, cpt + 1);
                        } else {
                            treeNode.setSelected(true);
                            selectedNode = treeNode;
                            selectedNodes.add(treeNode);
                        }
                    }
                } else {
                    icon = "fichier";
                    if (type == 2) { //CrÃ©ation de topConcepts
                        idTC = nodeConceptTree.getIdConcept();
                        if (nodeConceptTree.getTitle().trim().isEmpty()) {
                            value = nodeConceptTree.getIdConcept();
                        } else {
                            value = nodeConceptTree.getTitle();
                        }

                    } else { //CrÃ©ation de concepts
                        idTC = ((MyTreeNode) node).getIdTopConcept();
                        if (nodeConceptTree.getTitle().trim().isEmpty()) {
                            value = nodeConceptTree.getIdConcept();
                        } else {
                            value = nodeConceptTree.getTitle();
                        }
                    }
                    if (nodeConceptTree.getStatusConcept().equals("hidden")) {
                        icon = "hidden";
                    }
                    treeNode = new MyTreeNode(type, nodeConceptTree.getIdConcept(), ((MyTreeNode) node).getIdTheso(),
                            ((MyTreeNode) node).getLangue(), ((MyTreeNode) node).getIdDomaine(),
                            ((MyTreeNode) node).getTypeDomaine(),
                            idTC, icon, value, node);

                    if (listeId.get(cpt).equals(((MyTreeNode) treeNode).getIdMot())) {
                        treeNode.setSelected(true);
                        selectedNode = treeNode;
                        selectedNodes.add(treeNode);
                    } else {
                        treeNode.setSelected(false);
                    }

                }

//*/
            }
            node.setExpanded(true);
        } else {
            List<TreeNode> children = node.getChildren();
            for (TreeNode mtn : children) {
                if (listeId.get(cpt).equals(((MyTreeNode) mtn).getIdMot())) {
                    if (cpt + 1 < listeId.size()) {
                        mtn.setSelected(false);
                        reExpandChild(listeId, mtn, cpt + 1);
                    } else {
                        mtn.setSelected(true);
                        selectedNode = mtn;
                        selectedNodes.add(mtn);
                    }
                }
            }
        }
    }

    public void reInit() {
        root = (TreeNode) new DefaultTreeNode("Root", null);
    }

    public void newTSpe() {
        createValid = false;
        selectedTerme.setValueEdit(selectedTerme.getSelectedTermComp().getTermLexicalValue());
        if (selectedTerme.getValueEdit().trim().equals("")) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("tree.error1")));
            return;
        }

        String valueEdit = selectedTerme.getValueEdit().trim();

        // vérification si c'est le même nom, on fait rien
        if (valueEdit.equalsIgnoreCase(selectedTerme.getNom())) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("autoComp.impossible")));
            return;
        }
        String idTerm;
        String idConceptLocal;
        // vérification si le term à ajouter existe déjà 
        if ((idTerm = selectedTerme.isTermExist(valueEdit)) != null) {
            idConceptLocal = selectedTerme.getIdConceptOf(idTerm);
            // on vérifie si c'est autorisé de créer une relation ici
            selectedTerme.isCreateAuthorizedForTS(idConceptLocal);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("sTerme.error6")));
            return;
        }

        if (!selectedTerme.creerTermeSpe(((MyTreeNode) selectedNode))) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("error")));
            return;
        } else {
            reInit();
            reExpand();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", valueEdit + " " + langueBean.getMsg("tree.info1")));
        }
        selectedTerme.setSelectedTermComp(new NodeAutoCompletion());
        createValid = true;
    }

    /**
     * ************************** ACTIONS SELECTEDTERME
     * ***************************
     */
    /**
     * Supprime le groupe sélectionné
     */
    public void delGroup() {
        new GroupHelper().deleteConceptGroup(connect.getPoolConnexion(), selectedTerme.getIdC(), selectedTerme.getIdTheso(), selectedTerme.getUser().getUser().getId());
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("tree.info7")));
        reInit();
        initTree(selectedTerme.getIdTheso(), selectedTerme.getIdlangue());
        selectedTerme.reInitTerme();
    }

    /**
     * Cette fonction permet de supprimer le ou les orphelins (une branche à
     * partir du concept orphelin sélectionné
     *
     * @return
     */
    public boolean delOrphans() {
        if (!deleteOrphanBranch(connect.getPoolConnexion(),
                selectedTerme.getIdC(), selectedTerme.getIdTheso(), selectedTerme.getUser().getUser().getId())) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("error") + " :", langueBean.getMsg("error")));
            return false;
        }
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("tree.info8")));
        conceptbean.setDeleteBranchOrphan(0);
        reInit();
        initTree(selectedTerme.getIdTheso(), selectedTerme.getIdlangue());
        selectedTerme.reInitTerme();
        return true;
    }

    /**
     * Fonction recursive qui permet de supprimer une branche d'orphelins un
     * concept de tête et thesaurus. La suppression est descendante qui ne
     * supprime pas les autres branches remontantes.
     *
     * @param conn
     * @param idConcept
     * @param idTheso
     * @return
     */
    private boolean deleteOrphanBranch(HikariDataSource ds, String idConcept, String idTheso, int idUser) {

        ConceptHelper conceptHelper = new ConceptHelper();

        ArrayList<String> listIdsOfConceptChildren
                = conceptHelper.getListChildrenOfConcept(ds,
                        idConcept, idTheso);

        if (!conceptHelper.deleteConceptForced(ds, idConcept, idTheso, idUser)) {
            return false;
        }

        for (String listIdsOfConceptChildren1 : listIdsOfConceptChildren) {
            //     if(!conceptHelper.deleteConceptForced(ds, listIdsOfConceptChildren1, idTheso, idUser))
            //        return false;
            deleteOrphanBranch(ds, listIdsOfConceptChildren1, idTheso, idUser);
        }
        return true;
    }

    /**
     * Change le nom du terme courant avec mise à jour dans l'arbre Choix du
     * type de d'objet sélectionné (Group, sousGroup, Concept)
     *
     */
    public void editNomT() {
        if (selectedTerme == null) {
            return;
        }
        if (selectedTerme.getSelectedTermComp() == null) {
            return;
        }
        // si c'est la même valeur, on fait rien
        String valueEdit = selectedTerme.getSelectedTermComp().getTermLexicalValue().trim();
        if (selectedTerme.getNom().trim().equals(valueEdit)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("tree.error2")));
            selectedTerme.setNomEdit(selectedTerme.getNom());
            return;
        }

        String idTerm;
        String idConceptLocal;
        MyTreeNode myTreeNode = (MyTreeNode) selectedNode;

        // vérification si le Groupe à ajouter existe déjà 
        if (new GroupHelper().isDomainExist(connect.getPoolConnexion(), valueEdit, myTreeNode.getIdTheso(), myTreeNode.getLangue())) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("sTerme.error6")));
            return;
        }

        selectedTerme.setNomEdit(selectedTerme.getSelectedTermComp().getTermLexicalValue());

        // saisie d'une valeur vide
        if (selectedTerme.getNomEdit().trim().equals("")) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("tree.error1")));
            selectedTerme.setNomEdit(selectedTerme.getNom());
            return;
        }
        /// cas d'un Group
        if (myTreeNode.isIsGroup()) {
            /*
                    if (selectedTerme.getNom() == null || selectedTerme.getNom().equals("")) {
                        selectedTerme.editTerme(3);
                    } else {
                        selectedTerme.editTerme(4);
                    }*/

            if (!selectedTerme.editGroupName(myTreeNode.getIdTheso(),
                    myTreeNode.getIdMot(), myTreeNode.getLangue(),
                    valueEdit)) {
                //erreur à traiter
            }
            myTreeNode.setData(valueEdit);
            selectedTerme.setNom(valueEdit);
            selectedTerme.setSelectedTermComp(new NodeAutoCompletion());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", valueEdit + " " + langueBean.getMsg("tree.info2")));
            return;

        }

        /// cas d'un sousGroup
        if (((MyTreeNode) selectedNode).isIsSubGroup()) {
            if (!selectedTerme.editGroupName(myTreeNode.getIdTheso(),
                    myTreeNode.getIdMot(), myTreeNode.getLangue(),
                    valueEdit)) {
                //erreur à traiter
            }
            myTreeNode.setData(valueEdit);
            selectedTerme.setNom(valueEdit);
            selectedTerme.setSelectedTermComp(new NodeAutoCompletion());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", valueEdit + " " + langueBean.getMsg("tree.info2")));
            return;
        }

        /// cas d'un Concept
        String temp = selectedTerme.getNomEdit();
        if (selectedTerme.getIdT() != null && !selectedTerme.getIdT().equals("")) {
            selectedTerme.editTerme(1);
        } else {
            // le terme n'existe pas encore
            if (!selectedTerme.editTerme(2)) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("error")));
                selectedTerme.setNomEdit(selectedTerme.getNom());
                return;
            }
        }

        if (selectedNode != null) {
            //((MyTreeNode) selectedNode).setData(temp + " (Id_" + selectedTerme.getIdC() + ")");
            ((MyTreeNode) selectedNode).setData(temp);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", temp + " " + langueBean.getMsg("tree.info2")));
            selectedTerme.setNomEdit(selectedTerme.getNom());
        }

        selectedTerme.setSelectedTermComp(new NodeAutoCompletion());
    }

    /**
     * Supprime la relation hiÃ©rarchique qui lie le terme courant au terme dont
     * l'id est passÃ© en paramÃ¨tre puis met l'arbre Ã  jour. Si type vaut 0,
     * le terme courant est le fils, si type vaut 1, le terme courant est le
     * pÃ¨re.
     *
     * @param id
     * @param type
     */
    public void suppRel(String id, int type) {
        if (type == 0) {
            // type 0 = suppression de la relation gÃ©nÃ©rique 
            if (!selectedTerme.delGene(id)) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("error")));
                return;
            }

        } else {
            // type 1 = suppression de la relation spÃ©cifique
            if (!selectedTerme.delSpe(id)) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("error")));
                return;
            }
        }

        reInit();
        reExpand();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("tree.info3")));
    }

    public boolean desactivateConcept() {
        if (!selectedTerme.deprecateConcept()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("tree.error3")));
            return false;
        }
        reInit();
        reExpand();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("tree.info4")));
        vue.setAddTInfo(0);
        return true;

    }

    public boolean getConceptForFusion() {
        if (selectedTerme.getSelectedTermComp() == null || !selectedTerme.loadConceptFusion()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("tree.error5")));
            vue.setAddTInfo(0);
            return false;
        }
        vue.setAddTInfo(3);
        return true;
    }

    public void initConceptFusion() {
        selectedTerme.initConceptFusion();
    }

    /**
     * Fusionne les concepts avec mise à  jour dans l'abre
     */
    public void fusionConcept() {
        if (selectedTerme.getConceptFusionId().equals(selectedTerme.getIdC())) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error"), langueBean.getMsg("error")));
            selectedTerme.setConceptFusionId(null);
            selectedTerme.setConceptFusionAlign(null);
            selectedTerme.setConceptFusionNodeRT(null);
        } else {
            int idUser = selectedTerme.getUser().getUser().getId();
            for (NodeRT rt : selectedTerme.getConceptFusionNodeRT()) {
                HierarchicalRelationship hr = new HierarchicalRelationship(rt.getIdConcept(), selectedTerme.getConceptFusionId(), selectedTerme.getIdTheso(), "RT");
                new ConceptHelper().addAssociativeRelation(connect.getPoolConnexion(), hr, idUser);
            }
            for (NodeAlignment na : selectedTerme.getConceptFusionAlign()) {
                new AlignmentHelper().addNewAlignment(connect.getPoolConnexion(),
                        idUser, na.getConcept_target(), na.getThesaurus_target(), na.getUri_target(), na.getAlignement_id_type(),
                        selectedTerme.getConceptFusionId(), selectedTerme.getIdTheso(), 0);
            }
            new ConceptHelper().addConceptFusion(connect.getPoolConnexion(), selectedTerme.getConceptFusionId(), selectedTerme.getIdC(), selectedTerme.getIdTheso(), idUser);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("tree.info6")));
            reInit();
            reExpand();

        }
        selectedTerme.setSelectedTermComp(new NodeAutoCompletion());
        vue.setAddTInfo(0);
    }

    /*       if (selectedTerme.getChoixdesactive().equals("0")) {
            if (!selectedTerme.delConcept()) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("tree.error3")));
                return false;
            }
            reInit();
            reExpand();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("tree.info4")));
            vue.setAddTInfo(0);
            return true;
        } else if (selectedTerme.getChoixdesactive().equals("1")) {
            if (selectedTerme.getSelectedTermComp() == null || !selectedTerme.loadConceptFusion()) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("tree.error5")));
                vue.setAddTInfo(0);
                return false;
            }
            vue.setAddTInfo(3);
            return true;
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("tree.error6")));
            vue.setAddTInfo(0);
            return false;
        }*/
    public boolean reactivConcept() {
        if (!selectedTerme.reactivConcept()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("tree.error4")));
            return false;
        }
        reInit();
        reExpand();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("tree.info5")));
        return true;
    }

    /**
     * *
     * Nouvelles fonctions par Miled Rousset
     */
    /**
     * Permet de modifier la valeur de la notation d'un concept
     */
    public void editNotation() {
        if (selectedTerme == null) {
            return;
        }
        if (selectedTerme.getIdT() != null && !selectedTerme.getIdT().equals("")) {

            if (!selectedTerme.updateNotation()) {
                return;
            }
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("tree.info2")));
        }
    }

    /**
     * Fin des nouvelles fonctions
     */
    public Connexion getConnect() {
        return connect;
    }

    public void setConnect(Connexion connect) {
        this.connect = connect;
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

    public ArrayList<TreeNode> getSelectedNodes() {
        return selectedNodes;
    }

    public void setSelectedNodes(ArrayList<TreeNode> selectedNodes) {
        this.selectedNodes = selectedNodes;
    }

    public SelectedTerme getSelectedTerme() {
        return selectedTerme;
    }

    public void setSelectedTerme(SelectedTerme selectedTerme) {
        this.selectedTerme = selectedTerme;
    }

    public Vue getVue() {
        return vue;
    }

    public void setVue(Vue vue) {
        this.vue = vue;
    }

    public LanguageBean getLangueBean() {
        return langueBean;
    }

    public void setLangueBean(LanguageBean langueBean) {
        this.langueBean = langueBean;
    }

    public boolean isCreateValid() {
        return createValid;
    }

    public void setCreateValid(boolean createValid) {
        this.createValid = createValid;
    }

    public String getIdThesoSelected() {
        return idThesoSelected;
    }

    public void setIdThesoSelected(String idThesoSelected) {
        this.idThesoSelected = idThesoSelected;
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public void setDefaultLanguage(String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

    public ConceptBean getConceptbean() {
        return conceptbean;
    }

    public void setConceptbean(ConceptBean conceptbean) {
        this.conceptbean = conceptbean;
    }

}
