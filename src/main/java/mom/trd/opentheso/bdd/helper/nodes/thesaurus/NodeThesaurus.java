package mom.trd.opentheso.bdd.helper.nodes.thesaurus;

import java.util.ArrayList;
import mom.trd.opentheso.bdd.datas.Thesaurus;

/**
 * Cette classe permet de regrouper le thésaurus avec ses traductions
 *
 * @author miled.rousset
 */
public class NodeThesaurus {

    private String idThesaurus;
    private String idArk;
    
    private ArrayList<Thesaurus> listThesaurusTraduction = new ArrayList<>();

    public NodeThesaurus() {
        super();
    }

    public ArrayList<Thesaurus> getListThesaurusTraduction() {
        return listThesaurusTraduction;
    }

    public void setListThesaurusTraduction(ArrayList<Thesaurus> listThesaurusTraduction) {
        this.listThesaurusTraduction = listThesaurusTraduction;
    }

    public String getIdThesaurus() {
        return idThesaurus;
    }

    public void setIdThesaurus(String idThesaurus) {
        this.idThesaurus = idThesaurus;
    }

    public String getIdArk() {
        return idArk;
    }

    public void setIdArk(String idArk) {
        this.idArk = idArk;
    }

    
}
