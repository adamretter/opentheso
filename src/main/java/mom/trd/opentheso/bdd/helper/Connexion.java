package mom.trd.opentheso.bdd.helper;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;


@ManagedBean (name = "poolConnexion", eager = true)
@ApplicationScoped
public class Connexion implements Serializable {
    
    private static final long serialVersionUID = 1L;

//    private static final Log log = LogFactory.getLog("opentheso.log");
    
    private HikariDataSource poolConnexion = null;
    
    public ResourceBundle getBundlePool(){
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            ResourceBundle bundlePool = context.getApplication().getResourceBundle(context, "conHikari");
            return bundlePool;
        } catch (Exception e) {
            System.err.println(e.toString());
        }
        return null;
    }

    public Connexion() {
        this.poolConnexion = openConnexionPool();
    }

    public HikariDataSource getPoolConnexion() {
        return poolConnexion;
    }

    public void setPoolConnexion(HikariDataSource poolConnexion) {
        this.poolConnexion = poolConnexion;
    }

    private HikariDataSource openConnexionPool() {
        HikariConfig config = new HikariConfig();
        ResourceBundle resourceBundle = getBundlePool();
        if(resourceBundle == null){
            // retour pour installation
            return null;
        }
        config.setMinimumIdle(Integer.parseInt(resourceBundle.getString("minimumIdle")));
        config.setMaximumPoolSize(Integer.parseInt(resourceBundle.getString("setMaximumPoolSize")));
        config.setAutoCommit(true);
    //   config.setIdleTimeout(Integer.parseInt(getBundlePool().getString("idleTimeout")));
    //   config.setConnectionTimeout(Integer.parseInt(getBundlePool().getString("connectionTimeout")));
    //  config.setJdbc4ConnectionTest(false);
        config.setConnectionTestQuery(resourceBundle.getString("connectionTestQuery"));
        config.setDataSourceClassName(resourceBundle.getString("dataSourceClassName"));
        
        config.addDataSourceProperty("user", resourceBundle.getString("dataSource.user"));
        config.addDataSourceProperty("password", resourceBundle.getString("dataSource.password"));
        config.addDataSourceProperty("databaseName", resourceBundle.getString("dataSource.databaseName"));
        config.addDataSourceProperty("serverName", resourceBundle.getString("dataSource.serverName"));
        config.addDataSourceProperty("portNumber", resourceBundle.getString("dataSource.serverPort"));
        
        HikariDataSource poolConnexion1 = new HikariDataSource(config);
        try {
            poolConnexion1.getConnection();
        } catch (SQLException ex) {
            Logger.getLogger(Connexion.class.getName()).log(Level.SEVERE, null, ex);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_FATAL, ex.getClass().getName(), ex.getMessage()); 
            FacesContext.getCurrentInstance().addMessage(null, message);
            poolConnexion1.close();
            return null;
        }
        return poolConnexion1;
     }

    public void closeConnexion() {
        if(poolConnexion != null)
            poolConnexion.close();
    }

    void close() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
