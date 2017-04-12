/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.SelectedBeans;

import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import mom.trd.opentheso.bdd.helper.Connexion;
import mom.trd.opentheso.bdd.helper.GroupHelper;
import mom.trd.opentheso.bdd.helper.ThesaurusHelper;
import mom.trd.opentheso.bdd.helper.nodes.NodeLang;
import mom.trd.opentheso.bdd.helper.nodes.group.NodeGroup;

/**
 *
 * @author miled.rousset
 */
@ManagedBean(name = "skosExportBean")
@ViewScoped

public class skosExportBean {

    
    @ManagedProperty(value = "#{langueBean}")
    private LanguageBean langueBean;    
    @ManagedProperty(value = "#{poolConnexion}")
    private Connexion connect;    
    

    private String idTheso;
    private List<NodeLang> selectedLanguages;
    
    
    private List<NodeLang> languagesOfTheso;
    private List<NodeGroup> groupList;
    private List<NodeGroup> selectedGroups;    
    
    
    
    
    /**
     * Creates a new instance of skosExportBean
     */
    public skosExportBean() {
        
    }

    public void init(String idTheso, String idLang){
                
        this.idTheso = idTheso;
        ThesaurusHelper thesaurusHelper = new ThesaurusHelper();
        languagesOfTheso = thesaurusHelper.getAllUsedLanguagesOfThesaurusNode(connect.getPoolConnexion(), idTheso);
        
        GroupHelper groupHelper = new GroupHelper();
        groupList = groupHelper.getListConceptGroup(connect.getPoolConnexion(), idTheso, idLang);
       selectedLanguages = new ArrayList<>();
       selectedGroups = new ArrayList<>();
        
        selectedLanguages = new ArrayList<>();
        for (NodeLang nodeLang : languagesOfTheso) {
            selectedLanguages.add(nodeLang);
        }
        
        selectedGroups = new ArrayList<>();
        for (NodeGroup nodeGroup : groupList) {
            selectedGroups.add(nodeGroup);
        }

        
             
    }

    public LanguageBean getLangueBean() {
        return langueBean;
    }

    public void setLangueBean(LanguageBean langueBean) {
        this.langueBean = langueBean;
    }

    public Connexion getConnect() {
        return connect;
    }

    public void setConnect(Connexion connect) {
        this.connect = connect;
    }

    public String getIdTheso() {
        return idTheso;
    }

    public void setIdTheso(String idTheso) {
        this.idTheso = idTheso;
    }

    public List<NodeLang> getSelectedLanguages() {
        return selectedLanguages;
    }

    public void setSelectedLanguages(List<NodeLang> selectedLanguages) {
        this.selectedLanguages = selectedLanguages;
    }

    public List<NodeLang> getLanguagesOfTheso() {
        return languagesOfTheso;
    }

    public void setLanguagesOfTheso(List<NodeLang> languagesOfTheso) {
        this.languagesOfTheso = languagesOfTheso;
    }

    public List<NodeGroup> getGroupList() {
        return groupList;
    }

    public void setGroupList(List<NodeGroup> groupList) {
        this.groupList = groupList;
    }

    public List<NodeGroup> getSelectedGroups() {
        return selectedGroups;
    }

    public void setSelectedGroups(List<NodeGroup> selectedGroups) {
        this.selectedGroups = selectedGroups;
    }



   
    
}
