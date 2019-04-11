package mom.trd.opentheso.bdd.helper.nodes;


public class NodePreference {
    private String sourceLang;
    private int identifierType;
    private String preferredName;
    
    // paramètres Ark
    private boolean useArk;
    private String serveurArk;
    private String idNaan;
    private String prefixArk;
    private String userArk;
    private String passArk;
    
    
    // paramètres Handle
    private boolean useHandle;
    private String userHandle;
    private String passHandle;
    private String pathKeyHandle;
    private String pathCertHandle;
    private String urlApiHandle;
    private String prefixIdHandle;
    private String privatePrefixHandle;    
    
    
    
    private String pathImage;
    private String dossierResize;
    
    private boolean bddActive;
    private boolean bddUseId;
    private String urlBdd;
    private String urlCounterBdd;    
    
    private boolean z3950actif;
    private String collectionAdresse;
    private String noticeUrl;
    private String urlEncode;
    private String pathNotice1;
    private String pathNotice2;
    private String cheminSite;
    private boolean webservices;

    public String getSourceLang() {
        return sourceLang;
    }

    public void setSourceLang(String sourceLang) {
        this.sourceLang = sourceLang;
    }

    public int getIdentifierType() {
        return identifierType;
    }

    public void setIdentifierType(int identifierType) {
        this.identifierType = identifierType;
    }

    public boolean isUseArk() {
        return useArk;
    }

    public void setUseArk(boolean useArk) {
        this.useArk = useArk;
    }

    public String getServeurArk() {
        return serveurArk;
    }

    public void setServeurArk(String serveurArk) {
        this.serveurArk = serveurArk;
    }

    public String getIdNaan() {
        return idNaan;
    }

    public void setIdNaan(String idNaan) {
        this.idNaan = idNaan;
    }

    public String getPrefixArk() {
        return prefixArk;
    }

    public void setPrefixArk(String prefixArk) {
        this.prefixArk = prefixArk;
    }

    public String getUserArk() {
        return userArk;
    }

    public void setUserArk(String userArk) {
        this.userArk = userArk;
    }

    public String getPassArk() {
        return passArk;
    }

    public void setPassArk(String passArk) {
        this.passArk = passArk;
    }

    public boolean isUseHandle() {
        return useHandle;
    }

    public void setUseHandle(boolean useHandle) {
        this.useHandle = useHandle;
    }

    public String getUserHandle() {
        return userHandle;
    }

    public void setUserHandle(String userHandle) {
        this.userHandle = userHandle;
    }

    public String getPassHandle() {
        return passHandle;
    }

    public void setPassHandle(String passHandle) {
        this.passHandle = passHandle;
    }

    public String getPathKeyHandle() {
        return pathKeyHandle;
    }

    public void setPathKeyHandle(String pathKeyHandle) {
        this.pathKeyHandle = pathKeyHandle;
    }

    public String getPathCertHandle() {
        return pathCertHandle;
    }

    public void setPathCertHandle(String pathCertHandle) {
        this.pathCertHandle = pathCertHandle;
    }

    public String getUrlApiHandle() {
        return urlApiHandle;
    }

    public void setUrlApiHandle(String urlApiHandle) {
        this.urlApiHandle = urlApiHandle;
    }

    public String getPrefixIdHandle() {
        return prefixIdHandle;
    }

    public void setPrefixIdHandle(String prefixIdHandle) {
        this.prefixIdHandle = prefixIdHandle;
    }

    public String getPrivatePrefixHandle() {
        return privatePrefixHandle;
    }

    public void setPrivatePrefixHandle(String privatePrefixHandle) {
        this.privatePrefixHandle = privatePrefixHandle;
    }



    public String getPathImage() {
        return pathImage;
    }

    public void setPathImage(String pathImage) {
        this.pathImage = pathImage;
    }

    public String getDossierResize() {
        return dossierResize;
    }

    public void setDossierResize(String dossierResize) {
        this.dossierResize = dossierResize;
    }

    public boolean isBddActive() {
        return bddActive;
    }

    public void setBddActive(boolean bddActive) {
        this.bddActive = bddActive;
    }

    public boolean isBddUseId() {
        return bddUseId;
    }

    public void setBddUseId(boolean bddUseId) {
        this.bddUseId = bddUseId;
    }

    public String getUrlBdd() {
        return urlBdd;
    }

    public void setUrlBdd(String urlBdd) {
        this.urlBdd = urlBdd;
    }

    public String getUrlCounterBdd() {
        return urlCounterBdd;
    }

    public void setUrlCounterBdd(String urlCounterBdd) {
        this.urlCounterBdd = urlCounterBdd;
    }

    public boolean isZ3950actif() {
        return z3950actif;
    }

    public void setZ3950actif(boolean z3950actif) {
        this.z3950actif = z3950actif;
    }

    public String getCollectionAdresse() {
        return collectionAdresse;
    }

    public void setCollectionAdresse(String collectionAdresse) {
        this.collectionAdresse = collectionAdresse;
    }

    public String getNoticeUrl() {
        return noticeUrl;
    }

    public void setNoticeUrl(String noticeUrl) {
        this.noticeUrl = noticeUrl;
    }

    public String getUrlEncode() {
        return urlEncode;
    }

    public void setUrlEncode(String urlEncode) {
        this.urlEncode = urlEncode;
    }

    public String getPathNotice1() {
        return pathNotice1;
    }

    public void setPathNotice1(String pathNotice1) {
        this.pathNotice1 = pathNotice1;
    }

    public String getPathNotice2() {
        return pathNotice2;
    }

    public void setPathNotice2(String pathNotice2) {
        this.pathNotice2 = pathNotice2;
    }

    public String getCheminSite() {
        return cheminSite;
    }

    public void setCheminSite(String cheminSite) {
        this.cheminSite = cheminSite;
    }

    public boolean isWebservices() {
        return webservices;
    }

    public void setWebservices(boolean webservices) {
        this.webservices = webservices;
    }

    public String getPreferredName() {
        return preferredName;
    }

    public void setPreferredName(String preferredName) {
        this.preferredName = preferredName;
    }
    
    
}
