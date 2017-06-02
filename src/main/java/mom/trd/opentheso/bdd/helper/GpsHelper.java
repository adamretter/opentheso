/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.bdd.helper;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import mom.trd.opentheso.bdd.helper.nodes.NodeGps;
import mom.trd.opentheso.core.alignment.AlignementSource;
import mom.trd.opentheso.core.alignment.GpsPreferences;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author antonio.perez
 */
public class GpsHelper {

    private final Log log = LogFactory.getLog(ThesaurusHelper.class);

    public GpsHelper() {
    }

    public boolean insertCoordonees(HikariDataSource ds, String idC, String idTheso, double lat, double lon) {
        if (isCoordoneesExist(ds, idC, idTheso)) {
            if (!updateCoordonees(ds, idC, idTheso, lat, lon)) {
                return false;
            }
        } else {
            if (!insertGpsCoordinate(ds, idC, idTheso, lat, lon)) {
                return false;
            }
            if (!updateConcept(ds, idC, idTheso)) {
                return false;
            }
        }
        return true;
    }

    private boolean insertGpsCoordinate(HikariDataSource ds, String idC, String idTheso, double lat, double lon) {
        Connection conn;
        Statement stmt;
        boolean status = false;
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "Insert into gps values("
                            + "'" + idC + "'"
                            + ",'" + idTheso + "'"
                            + "," + lat
                            + "," + lon + ")";
                    stmt.executeUpdate(query);
                    status = true;
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while Add coordonnes : " + idC, sqle);
        }
        return status;
    }

    private boolean updateConcept(HikariDataSource ds, String idC, String idTheso) {
        Connection conn;
        Statement stmt;
        boolean status = false;
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "update concept set gps = true where"
                            + " id_concept ='" + idC + "'"
                            + " and id_thesaurus ='" + idTheso + "'";
                    stmt.executeUpdate(query);
                    status = true;
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while Add coordonnes : " + idC, sqle);
        }
        return status;
    }

    private boolean updateCoordonees(HikariDataSource ds, String idC, String idTheso, double lat, double lon) {
        Connection conn;
        Statement stmt;
        boolean status = false;
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "update gps set latitude = " + lat
                            + ", longitude = " + lon
                            + " where id_concept = '" + idC
                            + "' and id_theso = '" + idTheso + "'";
                    stmt.executeUpdate(query);
                    status = true;
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }

        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while update coordonnes : " + idC, sqle);

        }
        return status;
    }

    private boolean isCoordoneesExist(HikariDataSource ds, String idC, String idTheso) {
        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        boolean existe = false;
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "select id_concept from gps where "
                            + "id_concept ='" + idC + "'"
                            + " and id_theso = '" + idTheso + "'";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    if (resultSet.next()) {
                        existe = true;
                    }
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while Add coordonnes : " + idC, sqle);
        }
        return existe;
    }

    public boolean isHaveCoordinate(HikariDataSource ds, String id_concept, String id_theso) {
        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        boolean existe = false;
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "select gps from concept where "
                            + "id_concept ='" + id_concept + "'"
                            + " and id_thesaurus = '" + id_theso + "'";
                    resultSet = stmt.executeQuery(query);
                    if (resultSet.next()) {
                        existe = resultSet.getBoolean("gps");
                    }
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while Add coordonnes : " + id_concept, sqle);
        }
        return existe;
    }

    public NodeGps getCoordinate(HikariDataSource ds, String id_concept, String id_theso) {
        NodeGps coordonnees = null;
        if (isHaveCoordinate(ds, id_concept, id_theso)) {
            Connection conn;
            Statement stmt;
            ResultSet resultSet;

            try {
                // Get connection from pool
                conn = ds.getConnection();
                try {
                    stmt = conn.createStatement();
                    try {
                        String query = "select latitude, longitude from gps"
                                + " where id_concept ='" + id_concept + "'"
                                + " and id_theso = '" + id_theso + "'";
                        resultSet = stmt.executeQuery(query);
                        if (resultSet.next()) {
                            coordonnees = new NodeGps();
                            coordonnees.setLatitude(resultSet.getDouble("latitude"));
                            coordonnees.setLongitude(resultSet.getDouble("longitude"));
                        }
                    } finally {
                        stmt.close();
                    }
                } finally {
                    conn.close();
                }
            } catch (SQLException sqle) {
                // Log exception
                log.error("Error while Add coordonnes : " + id_concept, sqle);
            }
        }

        return coordonnees;
    }
    
    /**
     * fonction qui permet de supprimer les coordonnées GPS d'un Concept
     * @param ds
     * @param id_concept
     * @param id_theso
     * @return 
     */
    public boolean deleteCoordinate(HikariDataSource ds, String id_concept, String id_theso) {
            Connection conn;
            Statement stmt;
            boolean status = false; 
            try {
                // Get connection from pool
                conn = ds.getConnection();
                try {
                    stmt = conn.createStatement();
                    try {
                        String query = "delete from gps"
                                + " where id_concept ='" + id_concept + "'"
                                + " and id_theso = '" + id_theso + "'";
                        stmt.executeUpdate(query);
                        status = true;
                    } finally {
                        stmt.close();
                    }
                } finally {
                    conn.close();
                }
            } catch (SQLException sqle) {
                // Log exception
                log.error("Error while deleting coordinate GPS of Concept : " + id_concept, sqle);
            }
        return status;
    }

    public ArrayList<AlignementSource> getAlignementSource(HikariDataSource ds) {
        ArrayList<AlignementSource> aligSource = null;
        AlignementSource alig = null;

        Connection conn;
        Statement stmt;
        ResultSet resultSet;

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "select * from alignement_source"
                            + " where gps = true";
                    resultSet = stmt.executeQuery(query);
                    aligSource = new ArrayList<>();
                    while (resultSet.next()) {
                        alig = new AlignementSource();
                        alig.setSource(resultSet.getString("source"));
                        alig.setRequete(resultSet.getString("requete"));
                        alig.setTypeRequete(resultSet.getString("type_rqt"));
                        alig.setAlignement_format(resultSet.getString("alignement_format"));
                        alig.setId(resultSet.getInt("id"));
                        alig.setDescription(resultSet.getString("description"));
                        aligSource.add(alig);
                    }

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while Add coordonnes : ", sqle);
        }
        return aligSource;
    }

    public boolean garderPreferences(HikariDataSource ds, String id_Theso,
            boolean integrerTraduction, boolean reemplacerTraduction, boolean alignementAutomatique, Integer id_gps_source, int id_user) {
        if (!existsPreferences(ds, id_Theso, id_gps_source, id_user)) {
            if (!insertPreferences(ds, id_Theso, integrerTraduction, reemplacerTraduction, alignementAutomatique, id_gps_source, id_user)) {
                return false;
            }
            return true;
        } else if (!updateTablePreferences(ds, id_Theso, integrerTraduction, reemplacerTraduction, alignementAutomatique, id_gps_source, id_user)) {
            return false;
        }
        return true;
    }

    public boolean existsPreferences(HikariDataSource ds, String id_Theso,
            Integer id_gps_source, int id_user) {
        boolean status = false;
        Connection conn;
        Statement stmt;
        ResultSet resultSet;

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT id from gps_preferences"
                            + " where id_thesaurus ='" + id_Theso
                            + "' and id_user = " + id_user
                            + " and id_alignement_source ='" + id_gps_source + "'";
                    resultSet = stmt.executeQuery(query);
                    if (resultSet.next()) {
                        status = true;
                    }
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while search preferences Gps : ", sqle);
        }

        return status;
    }

    public boolean insertPreferences(HikariDataSource ds, String id_Theso,
            boolean integrerTraduction, boolean reemplacerTraduction, boolean alignementAutomatique, int id_gps_source, int id_user) {
        boolean status = false;
        Connection conn;
        Statement stmt;
        ResultSet resultSet;

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "INSERT INTO gps_preferences"
                            + "(id_thesaurus, id_user, id_alignement_source, gps_integrertraduction,"
                            + " gps_reemplacertraduction, gps_alignementautomatique)"
                            + " values('"
                            + id_Theso
                            + "',"
                            + id_user
                            + ","
                            + id_gps_source
                            + ",'"
                            + integrerTraduction
                            + "','"
                            + reemplacerTraduction
                            + "','"
                            + alignementAutomatique + "')";

                    stmt.execute(query);
                    status = true;
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while add gps preferences : ", sqle);
        }

        return status;
    }

    public boolean updateTablePreferences(HikariDataSource ds, String id_Theso,
            boolean integrerTraduction, boolean reemplacerTraduction, boolean alignementAutomatique, Integer id_gps_source, int id_user) {
        boolean status = false;
        Connection conn;
        Statement stmt;
        ResultSet resultSet;

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "Update gps_preferences set gps_integrertraduction ='" + integrerTraduction
                            + "', gps_reemplacertraduction = '" + reemplacerTraduction
                            + "', gps_alignementautomatique ='" + alignementAutomatique
                            + " ' where id_thesaurus ='" + id_Theso + "'"
                            + "  and id_user =" + id_user
                            + " and id_alignement_source =" + id_gps_source;
                    stmt.executeUpdate(query);
                    status = true;
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while update gps preferences : ", sqle);
        }

        return status;
    }

    public AlignementSource find_alignement_gps(HikariDataSource ds, Integer idsource) {
        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        AlignementSource alig = new AlignementSource();;
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "select * from alignement_source"
                            + " where id = " + idsource;
                    resultSet = stmt.executeQuery(query);
                    if (resultSet.next()) {
                        alig.setSource(resultSet.getString("source"));
                        alig.setRequete(resultSet.getString("requete"));
                        alig.setTypeRequete(resultSet.getString("type_rqt"));
                        alig.setAlignement_format(resultSet.getString("alignement_format"));
                        alig.setId(resultSet.getInt("id"));

                    }
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while Add coordonnes : ", sqle);
        }
        return alig;
    }
    
    public GpsPreferences getGpsPreferences(HikariDataSource ds, String id_theso, int iduser, int id_source)
    {
        GpsPreferences gpsPreferences = new GpsPreferences();
        Connection conn;
        Statement stmt;
        ResultSet resultSet;

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "select * from gps_preferences"+
                            " where id_thesaurus = '" + id_theso +
                            "' and id_user =" + iduser +
                            " and id_alignement_source =" + id_source;
                    resultSet = stmt.executeQuery(query);
                    if (resultSet.next()) {
                        gpsPreferences.setGps_alignementautomatique(resultSet.getBoolean("gps_alignementautomatique"));
                        gpsPreferences.setGps_integrertraduction(resultSet.getBoolean("gps_integrertraduction"));
                        gpsPreferences.setGps_reemplacertraduction(resultSet.getBoolean("gps_reemplacertraduction"));
                        gpsPreferences.setId_user(resultSet.getInt("id_user"));
                        gpsPreferences.setId_alignement_source(resultSet.getInt("id_alignement_source"));
                    }
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while Add coordonnes : ", sqle);
        }
        return gpsPreferences;
    }
}
