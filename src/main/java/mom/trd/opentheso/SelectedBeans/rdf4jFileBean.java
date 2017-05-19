/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.SelectedBeans;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.PhaseId;
import mom.trd.opentheso.bdd.helper.Connexion;
import mom.trd.opentheso.core.imports.rdf4j.ReadRdf4j;
import mom.trd.opentheso.core.imports.rdf4j.helper.ImportRdf4jHelper;
import mom.trd.opentheso.skosapi.SKOSXmlDocument;
import org.primefaces.event.FileUploadEvent;

/**
 *
 * @author Quincy
 */

@ManagedBean(name = "rdf4jFileBean")
@ViewScoped
public class rdf4jFileBean implements Serializable {
    
    @ManagedProperty(value = "#{poolConnexion}")
    private Connexion connect;
    private double progress=0;
    private double progress_abs=0;
    /*
    @ManagedProperty(value = "#{langueBean}")
    private LanguageBean langueBean;    
    */
    private String formatDate;
    private String uri;
    private double total;
    
    private boolean uploadEnable = true;
    private boolean BDDinsertEnable = false;
    
    private SKOSXmlDocument sKOSXmlDocument;

    
    
    public void chargeSkos(FileUploadEvent event) {
        progress =0;
        
        if (!PhaseId.INVOKE_APPLICATION.equals(event.getPhaseId())) {
            event.setPhaseId(PhaseId.INVOKE_APPLICATION);
            event.queue();
        } else {
            InputStream is = null;

            try {
                is = event.getFile().getInputstream();
            } catch (IOException ex) {
                Logger.getLogger(FileBean.class.getName()).log(Level.SEVERE, null, ex);
            }
            ReadRdf4j readRdf4j = new ReadRdf4j(is,0);
            progress =100;
            sKOSXmlDocument = readRdf4j.getsKOSXmlDocument();
            total = sKOSXmlDocument.getConceptList().size() + sKOSXmlDocument.getGroupList().size() + 1  ;
            uri = sKOSXmlDocument.getTitle();    
            uploadEnable = false;
            BDDinsertEnable = true;

            
        }      
    }
    
    public void chargeJsonLd(FileUploadEvent event) {
        progress =0;
        
        if (!PhaseId.INVOKE_APPLICATION.equals(event.getPhaseId())) {
            event.setPhaseId(PhaseId.INVOKE_APPLICATION);
            event.queue();
        } else {
            InputStream is = null;

            try {
                is = event.getFile().getInputstream();
            } catch (IOException ex) {
                Logger.getLogger(FileBean.class.getName()).log(Level.SEVERE, null, ex);
            }
            ReadRdf4j readRdf4j = new ReadRdf4j(is,1);
            progress =100;
            sKOSXmlDocument = readRdf4j.getsKOSXmlDocument();
            total = sKOSXmlDocument.getConceptList().size() + sKOSXmlDocument.getGroupList().size() + 1  ;
            uri = sKOSXmlDocument.getTitle();    
            uploadEnable = false;
            BDDinsertEnable = true;

            
        }      
    }
    
    public void chargeTurtle(FileUploadEvent event) {
        progress =0;
        
        if (!PhaseId.INVOKE_APPLICATION.equals(event.getPhaseId())) {
            event.setPhaseId(PhaseId.INVOKE_APPLICATION);
            event.queue();
        } else {
            InputStream is = null;

            try {
                is = event.getFile().getInputstream();
            } catch (IOException ex) {
                Logger.getLogger(FileBean.class.getName()).log(Level.SEVERE, null, ex);
            }
            ReadRdf4j readRdf4j = new ReadRdf4j(is,2);
            progress =100;
            sKOSXmlDocument = readRdf4j.getsKOSXmlDocument();
            total = sKOSXmlDocument.getConceptList().size() + sKOSXmlDocument.getGroupList().size() + 1  ;
            uri = sKOSXmlDocument.getTitle();    
            uploadEnable = false;
            BDDinsertEnable = true;

            
        }      
    }
    
    public void insertBDD(int idUser, int idRole) {
        
        progress =0;
        progress_abs=0;            
        ImportRdf4jHelper importRdf4jHelper = new ImportRdf4jHelper();
        importRdf4jHelper.setInfos(connect.getPoolConnexion(), formatDate, uploadEnable, "adresse",idUser, idRole, /*langueBean.getIdLangue()*/"fr");
        importRdf4jHelper.setRdf4jThesaurus(sKOSXmlDocument);
        importRdf4jHelper.addThesaurus();
        progress_abs++;
        progress =  progress_abs/total *100;
        importRdf4jHelper.addGroups(this);
        importRdf4jHelper.addConcepts(this);
        uploadEnable = true;
        BDDinsertEnable = false;
        uri = null;
        total =0;
    }

    public String getFormatDate() {
        return formatDate;
    }

    public void setFormatDate(String formatDate) {
        this.formatDate = formatDate;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public boolean isUploadEnable() {
        return uploadEnable;
    }

    public void setUploadEnable(boolean uploadEnable) {
        this.uploadEnable = uploadEnable;
    }

    public boolean isBDDinsertEnable() {
        return BDDinsertEnable;
    }

    public void setBDDinsertEnable(boolean BDDinsertEnable) {
        this.BDDinsertEnable = BDDinsertEnable;
    }

    public Connexion getConnect() {
        return connect;
    }

    public void setConnect(Connexion connect) {
        this.connect = connect;
    }

    public SKOSXmlDocument getsKOSXmlDocument() {
        return sKOSXmlDocument;
    }

    public void setsKOSXmlDocument(SKOSXmlDocument sKOSXmlDocument) {
        this.sKOSXmlDocument = sKOSXmlDocument;
    }

    public int getProgress() {
        return (int)progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    public double getAbs_progress() {
        return progress_abs;
    }

    public void setAbs_progress(double abs_progress) {
        this.progress_abs = abs_progress;
    }
    
    
    
    
    
    
    
    
    
}
