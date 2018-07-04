package mom.trd.opentheso.bdd.helper.nodes;

import java.io.Serializable;

public class NodeUser2 implements Serializable {
    private int idUser;
    private String name;
    private String mail;
    private boolean isActive;
    private boolean isAlertMail;
    private boolean isSuperAdmin = false;
    private boolean passtomodify;

    
    public NodeUser2() {
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public boolean isIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public boolean isIsAlertMail() {
        return isAlertMail;
    }

    public void setIsAlertMail(boolean isAlertMail) {
        this.isAlertMail = isAlertMail;
    }

    public boolean isIsSuperAdmin() {
        return isSuperAdmin;
    }

    public void setIsSuperAdmin(boolean isSuperAdmin) {
        this.isSuperAdmin = isSuperAdmin;
    }

    public boolean isPasstomodify() {
        return passtomodify;
    }

    public void setPasstomodify(boolean passtomodify) {
        this.passtomodify = passtomodify;
    }
    
}
