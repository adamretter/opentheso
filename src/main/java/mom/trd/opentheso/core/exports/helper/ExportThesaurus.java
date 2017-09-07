/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.core.exports.helper;

import com.zaxxer.hikari.HikariDataSource;
import java.util.ArrayList;
import mom.trd.opentheso.bdd.helper.GroupHelper;
import mom.trd.opentheso.core.exports.altlabel.WriteAltLabel;
import mom.trd.opentheso.core.exports.tabulate.ThesaurusDatas;


/**
 * Cette classe permet d'exporter toutes les données d'un thésaurus
 * et les préparer pour un export spécifique (Skos, tabulé ....)
 *
 * @author miled.rousset
 */
public class ExportThesaurus {

    private ThesaurusDatas thesaurusDatas;
    
    public ExportThesaurus() {
    }
    
    public boolean exportAllDatas(HikariDataSource ds, String idThesaurus){
        this.thesaurusDatas = new ThesaurusDatas();
        if(!thesaurusDatas.exportAllDatas(ds, idThesaurus)){
            return false;
        }
        return true;
    }

    public ThesaurusDatas getThesaurusDatas() {
        return thesaurusDatas;
    }
    
    public StringBuilder exportAltLabel(HikariDataSource ds, 
            String idTheso, String idLang) {
        GroupHelper groupHelper = new GroupHelper();
        WriteAltLabel writeAltLabel = new WriteAltLabel();
        String GroupLabel;
        
        ArrayList <String> GroupLists = groupHelper.getListIdOfGroup(ds, idTheso);//getAllBottomGroup(ds, idTheso);
        if(GroupLists == null) return null;
        if(GroupLists.isEmpty()) return new StringBuilder("");
        
        for (String GroupList : GroupLists) {
            GroupLabel = groupHelper.getLexicalValueOfGroup(ds, GroupList, idTheso, idLang);
            writeAltLabel.setGroup(GroupList, GroupLabel);
            writeAltLabel.setHeader();
            writeAltLabel.AddAltLabelByGroup(ds, idTheso, GroupList, idLang);
        }
        return writeAltLabel.getAllAltLabels();
    }
    
    
}
