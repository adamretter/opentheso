/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.bdd.helper.nodes;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import mom.trd.opentheso.bdd.helper.UserHelper;

/**
 *
 * @author Quincy
 */
public class PreferencesHelper {

    public NodePreference getThesaurusPreference(HikariDataSource ds, String idThesaurus) {
        NodePreference np = null;
        Connection conn;
        Statement stmt;
        ResultSet resultSet;

        try {
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT * FROM preferences where id_thesaurus = '" + idThesaurus + "'";
                    resultSet = stmt.executeQuery(query);

                    if (resultSet.next()) {
                        np = new NodePreference();
                        np.setAlertCdt(resultSet.getBoolean("alert_cdt"));
                        np.setNbAlertCdt(resultSet.getInt("nb_alert_cdt"));
                        np.setSourceLang(resultSet.getString("source_lang"));
                        np.setIdentifierType(resultSet.getInt("identifierType"));
                        np.setUseArk(resultSet.getBoolean("useArk"));
                        np.setServeurArk(resultSet.getString("serverArk"));
                        np.setPathImage(resultSet.getString("pathImage"));
                        np.setDossierResize(resultSet.getString("dossierResize"));
                        np.setProtcolMail(resultSet.getString("protocolMail"));
                        np.setHostMail(resultSet.getString("hostMail"));
                        np.setPortMail(resultSet.getInt("portMail"));
                        np.setAuthMail(resultSet.getBoolean("authMail"));
                        np.setMailForm(resultSet.getString("mailFrom"));
                        np.setTransportMail(resultSet.getString("transportMail"));
                        np.setBddActive(resultSet.getBoolean("bddActive"));
                        np.setBddUseId(resultSet.getBoolean("bddUseId"));
                        np.setUrlBdd(resultSet.getString("urlBdd"));
                        np.setZ3950acif(resultSet.getBoolean("z3950actif"));
                        np.setCollectionAdresse(resultSet.getString("collectionAdresse"));
                        np.setNoticeUrl(resultSet.getString("noticeUrl"));
                        np.setUrlEncode(resultSet.getString("urlEncode"));
                        np.setPathNotice1(resultSet.getString("pathNotice1"));
                        np.setPathNotice2(resultSet.getString("pathNotice2"));
                        np.setCheminSite(resultSet.getString("cheminSite"));

                    }

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }

        } catch (SQLException ex) {
            Logger.getLogger(UserHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return np;
    }
    
    

    /**
     * Cette fonction permet de mettre à jour les préférences d'un thésaurus
     *
     * @param ds
     * @param idThesaurus
     * @param workLanguage
     * @param nb_alert_cdt
     * @param alert_cdt
     */
    public void updatePreferences(HikariDataSource ds, String idThesaurus,
            String workLanguage, int nb_alert_cdt,
            boolean alert_cdt) {
        Connection conn;
        Statement stmt;
        try {
            // Get connection from pool
            conn = ds.getConnection();

            try {
                stmt = conn.createStatement();
                try {
                    String query = "UPDATE preferences set id_thesaurus  = '" + idThesaurus + "',"
                            + " source_lang = '" + workLanguage + "',"
                            + " nb_alert_cdt = " + nb_alert_cdt
                            + " alert_cdt = " + alert_cdt
                            + " WHERE id_thesaurus = '" + idThesaurus + "'";
                    stmt.executeUpdate(query);
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Cette fonction permet d'initialiser les préférences d'un thésaurus
     *
     * @param ds
     * @param idThesaurus
     * @param workLanguage
     * @param nb_alert_cdt
     * @param alert_cdt
     */
    public void initPreferences(HikariDataSource ds, String idThesaurus,
            String workLanguage, int nb_alert_cdt,
            boolean alert_cdt) {
        Connection conn;
        Statement stmt;
        try {
            // Get connection from pool
            conn = ds.getConnection();

            try {
                stmt = conn.createStatement();
                try {
                    String query = "insert into preferences "
                            + "(id_thesaurus,source_lang,nb_alert_cdt, alert_cdt)"
                            + " values"
                            + " ('" + idThesaurus + "',"
                            + "'" + workLanguage + "',"
                            + nb_alert_cdt
                            + "," + alert_cdt + ")";
                    stmt.executeUpdate(query);
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Permet de mettre à jour la préférence d'un thésaurus
     *
     * @param ds
     * @param np
     * @param idThesaurus
     * @return
     */
    public boolean updatePreferenceUser(HikariDataSource ds, NodePreference np, String idThesaurus) {
        Connection conn;
        Statement stmt;

        boolean status = false;
        try {
            conn = ds.getConnection();

            try {
                stmt = conn.createStatement();
                try {
                    String query = "update preferences set source_lang='" + np.getSourceLang()
                            + "', nb_alert_cdt=" + np.getNbAlertCdt() + ", alert_cdt=" + np.isAlertCdt() + " where"
                            + " id_thesaurus = '" + idThesaurus + "'";
                    stmt.executeUpdate(query);
                    status = true;

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return status;
    }

    /**
     * Permet de mettre à jour toutes les préférence
     *
     * @param ds
     * @param np
     * @param idThesaurus
     * @return
     */
    public boolean updateAllPreferenceUser(HikariDataSource ds, NodePreference np, String idThesaurus) {
        Connection conn;
        Statement stmt;
        boolean status = false;

        try {
            conn = ds.getConnection();

            try {
                stmt = conn.createStatement();
                try {
                    String query = "update preferences set "
                            + "source_lang='" + np.getSourceLang() + "'"
                            + ", nb_alert_cdt='" + np.getNbAlertCdt() + "'"
                            + ", alert_cdt='" + np.isAlertCdt() + "'"
                            + ", \"identifierType\"='" + np.getIdentifierType() + "'"
                            + ", \"useArk\"='"+np.isUseArk() + "'"
                            + ", \"serverArk\"='"+np.getServeurArk() + "'"
                            + ", \"pathImage\"='"+np.getPathImage() + "'"
                            + ", \"dossierResize\"='"+np.getDossierResize() + "'"
                            + ", \"protocolMail\"='"+np.getProtcolMail() + "'"
                            + ", \"hostMail\"='"+np.getHostMail() + "'"
                            + ", \"portMail\"='"+np.getPortMail() + "'"
                            + ", \"authMail\"='"+np.isAuthMail() + "'"
                            + ", \"mailFrom\"='"+np.getMailForm() + "'"
                            + ", \"transportMail\"='"+np.getTransportMail() + "'"
                            + ", \"bddActive\"='"+np.isBddActive() + "'"
                            + ", \"bddUseId\"='"+np.isBddUseId() + "'"
                            + ", \"urlBdd\"='"+np.getUrlBdd() + "'"
                            + ", z3950actif='"+np.getZ3950acif() + "'"
                            + ", \"collectionAdresse\"='"+np.getCollectionAdresse() + "'"
                            + ", \"noticeUrl\"='"+ np.getNoticeUrl()+ "'"
                            + ", \"urlEncode\"='"+np.getUrlEncode() + "'"
                            + ", \"pathNotice1\"='"+np.getPathNotice1() + "'"
                            + ", \"pathNotice2\"='"+np.getPathNotice2() + "'"
                            + ", \"cheminSite\"='"+np.getCheminSite()+"'"
                            + " WHERE"
                            + " id_thesaurus = '" + idThesaurus + "'";
                    stmt.executeUpdate(query);
                    status = true;

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserHelper.class.getName()).log(Level.SEVERE, null, ex);
        }

        return status;
    }
    
    

}
