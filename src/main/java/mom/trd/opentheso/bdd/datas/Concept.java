package mom.trd.opentheso.bdd.datas;

import java.util.Date;

public class Concept {

    private String idConcept;
    private String idThesaurus;
    private String idArk = "";
    private String idHandle = "";
    private Date created;
    private Date modified;
    private String status;
    private String notation;
    private boolean topConcept;
    private String idGroup;
    private String userName;

    public Concept(String idConcept, String status, String notation, String idThesaurus, String idGroup,
            boolean topConcept) {
        super();
        this.idConcept = idConcept;
        this.status = status;
        this.notation = notation;
        this.idThesaurus = idThesaurus;
        this.idGroup = idGroup;
        this.topConcept = topConcept;
        this.notation = "";
    }

    public Concept() {
        super();
    }

    public String getIdConcept() {
        return idConcept;
    }

    public void setIdConcept(String idConcept) {
        this.idConcept = idConcept;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
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

    public boolean isTopConcept() {
        return topConcept;
    }

    public void setTopConcept(boolean topConcept) {
        this.topConcept = topConcept;
    }

    public String getIdThesaurus() {
        return idThesaurus;
    }

    public void setIdThesaurus(String idThesaurus) {
        this.idThesaurus = idThesaurus;
    }

    public String getIdGroup() {
        return idGroup;
    }

    public void setIdGroup(String idGroup) {
        this.idGroup = idGroup;
    }

    public String getIdArk() {
        return idArk;
    }

    public void setIdArk(String idArk) {
        this.idArk = idArk;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getIdHandle() {
        return idHandle;
    }

    public void setIdHandle(String idHandle) {
        this.idHandle = idHandle;
    }

}
