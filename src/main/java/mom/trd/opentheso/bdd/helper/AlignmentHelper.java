package mom.trd.opentheso.bdd.helper;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import mom.trd.opentheso.bdd.helper.nodes.NodeAlignment;
import mom.trd.opentheso.bdd.tools.StringPlus;
import mom.trd.opentheso.core.alignment.AlignementPreferences;
import mom.trd.opentheso.core.alignment.AlignementSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AlignmentHelper {

    private final Log log = LogFactory.getLog(ThesaurusHelper.class);
    private String message = "";

    public AlignmentHelper() {

    }

    /**
     * Permet de savoir si le concept 'id_concept' a déjà une alignement ou pas
     *
     * @param ds
     * @param id_alignement_source
     * @param id_Theso
     * @param id_Concept
     * @return
     */
    public boolean isExistsAlignement(HikariDataSource ds,
            int id_alignement_source, String id_Theso, String id_Concept, int alignement_id_type) {
        boolean status = false;
        Connection conn;
        Statement stmt;
        ResultSet rs;
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT internal_id_concept from alignement"
                            + " where internal_id_concept = '" + id_Concept + "'"
                            + " and internal_id_thesaurus = '" + id_Theso + "'"
                            + " and id_alignement_source = '" + id_alignement_source + "'"
                            + " and alignement_id_type = '" + alignement_id_type + "'";
                    rs = stmt.executeQuery(query);
                    if (rs.next()) {
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
            log.error("Error while search alignement with target : " + sqle);
        }
        return status;
    }

    /**
     * Permet de savoir si on besoin faire une update ou un insert dans la BDD
     *
     * @param ds
     * @param author
     * @param conceptTarget
     * @param thesaurusTarget
     * @param uriTarget
     * @param idTypeAlignment
     * @param idConcept
     * @param idThesaurus
     * @param id_alignement_source parametre que on prende de la BDD, si c'est 0
     * c'est alignement manuel
     * @return
     */
    public boolean addNewAlignment(HikariDataSource ds,
            int author,
            String conceptTarget, String thesaurusTarget,
            String uriTarget, int idTypeAlignment,
            String idConcept, String idThesaurus, int id_alignement_source) {
        boolean status = false;
        if (!isExistsAlignement(ds, id_alignement_source, idThesaurus, idConcept, idTypeAlignment)) {
            message = "<br>";//"Cet alignement n'exite pas, création en cours <br>";
            status = addNewAlignement2(ds, author, conceptTarget, thesaurusTarget, uriTarget, idTypeAlignment,
                    idConcept, idThesaurus, id_alignement_source);
            if (!status) {
                return false;
            }
            message += "<br> New alignment created ... ok";
        } else {
            // message = "Cette alignement exits, updating en cours";
            status = updateAlignment(ds, idTypeAlignment, conceptTarget, thesaurusTarget, uriTarget, idTypeAlignment,
                    idConcept, idThesaurus, id_alignement_source);
            if (!status) {
                return false;
            }
            message += "<br> Alignment updated ... Ok";
        }

        return status;
    }

    /**
     * Cette fonction permet d'ajouter un nouvel alignement sur un thésaurus
     * distant pour ce concept
     *
     * @param ds
     * @param author
     * @param conceptTarget
     * @param thesaurusTarget
     * @param uriTarget
     * @param idTypeAlignment
     * @param idConcept
     * @param idThesaurus
     * @return
     */
    private boolean addNewAlignement2(HikariDataSource ds,
            int author,
            String conceptTarget, String thesaurusTarget,
            String uriTarget, int idTypeAlignment,
            String idConcept, String idThesaurus, int id_alignement_source) {
        Connection conn;
        Statement stmt;
        boolean status = false;
        conceptTarget = new StringPlus().convertString(conceptTarget);
        uriTarget = new StringPlus().convertString(uriTarget);

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "Insert into alignement "
                            + "(author, concept_target, thesaurus_target,"
                            + " uri_target, alignement_id_type,"
                            + " internal_id_thesaurus, internal_id_concept,"
                            + " id_alignement_source)"
                            + " values ("
                            + author
                            + ",'" + conceptTarget + "'"
                            + ",'" + thesaurusTarget + "'"
                            + ",'" + uriTarget + "'"
                            + "," + idTypeAlignment
                            + ",'" + idThesaurus + "'"
                            + ",'" + idConcept + "',"
                            + id_alignement_source + " )";

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
            log.error("Error while adding external alignement with target : " + uriTarget, sqle);
        }
        return status;
    }

    /**
     * Cette fonction permet d'ajouter un nouvel alignement sur un thésaurus
     * distant pour ce concept
     *
     * @param ds
     * @param nodeAlignment
     *
     * @return
     */
    public boolean addNewAlignment(HikariDataSource ds,
            NodeAlignment nodeAlignment) {

        Connection conn;
        Statement stmt;

        nodeAlignment.setConcept_target(new StringPlus().convertString(nodeAlignment.getConcept_target()));
        nodeAlignment.setUri_target(new StringPlus().convertString(nodeAlignment.getUri_target()));

        boolean status = false;
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "Insert into alignement "
                            + "(author, concept_target, thesaurus_target,"
                            + " uri_target, alignement_id_type,"
                            + " internal_id_thesaurus, internal_id_concept)"
                            + " values ("
                            + nodeAlignment.getId_author()
                            + ",'" + nodeAlignment.getConcept_target() + "'"
                            + ",'" + nodeAlignment.getThesaurus_target() + "'"
                            + ",'" + nodeAlignment.getUri_target() + "'"
                            + "," + nodeAlignment.getAlignement_id_type()
                            + ",'" + nodeAlignment.getInternal_id_thesaurus() + "'"
                            + ",'" + nodeAlignment.getInternal_id_concept() + "')";

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
            log.error("Error while adding external alignement with target : " + nodeAlignment.getUri_target(), sqle);
        }
        return status;
    }

    /**
     * Cette focntion permet de supprimer un alignement
     *
     * @param ds
     * @param idAlignment
     * @param idThesaurus
     * @return
     */
    public boolean deleteAlignment(HikariDataSource ds,
            int idAlignment, String idThesaurus) {

        Connection conn;
        Statement stmt;
        boolean status = false;

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "delete from alignement "
                            + " where id = " + idAlignment
                            + " and internal_id_thesaurus = '" + idThesaurus + "'";

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
            log.error("Error while deleting alignment from thesaurus with idAlignment : " + idAlignment, sqle);
        }
        return status;
    }

    /**
     * Cette focntion permet de supprimer tous les aligenements d'un concept
     *
     * @param conn
     * @param idConcept
     * @param idThesaurus
     * @return
     */
    public boolean deleteAlignmentOfConcept(Connection conn,
            String idConcept, String idThesaurus) {

        Statement stmt;
        boolean status = false;

        try {
            // Get connection from pool
            try {
                stmt = conn.createStatement();
                try {
                    String query = "delete from alignement "
                            + " where internal_id_concept = " + idConcept
                            + " and internal_id_thesaurus = '" + idThesaurus + "'";

                    stmt.executeUpdate(query);
                    status = true;

                } finally {
                    stmt.close();
                }
            } finally {
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while deleting alignment from thesaurus with idConcept : " + idConcept, sqle);
        }
        return status;
    }

    /**
     * Cette fonction permet de mettre à jour un Terme à la table Term, en
     * paramètre un objet Classe Term
     *
     * @param ds
     * @param idAlignment
     * @param conceptTarget
     * @param thesaurusTarget
     * @param idConcept
     * @param idTypeAlignment
     * @param uriTarget
     * @param idThesaurus
     * @param id_alignement_source parametre que on prende de la BDD, si c'est 0
     * c'est alignement manuel
     * @return
     */
    public boolean updateAlignment(HikariDataSource ds,
            int idAlignment,
            String conceptTarget, String thesaurusTarget,
            String uriTarget, int idTypeAlignment,
            String idConcept, String idThesaurus, int id_alignement_source) {

        Connection conn;
        Statement stmt;
        boolean status = false;
        uriTarget = new StringPlus().convertString(uriTarget);
        conceptTarget = new StringPlus().convertString(conceptTarget);
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "UPDATE alignement set"
                            + " concept_target = '" + conceptTarget + "',"
                            + " modified = current_date,"
                            + " thesaurus_target = '" + thesaurusTarget + "',"
                            + " uri_target = '" + uriTarget + "',"
                            + " alignement_id_type = " + idTypeAlignment
                            + " WHERE internal_id_thesaurus = '" + idThesaurus + "'"
                            + " AND internal_id_concept = '" + idConcept + "'"
                            + " AND id_alignement_source = " + id_alignement_source
                            + " AND alignement_id_type = " + idTypeAlignment;
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
            log.error("Error while updating Alignment : " + idAlignment, sqle);
        }
        return status;
    }

    /**
     * Cette fonction permet de retourner la liste des alignements pour un
     * concept
     *
     * @param ds
     * @param idConcept
     * @param idThesaurus
     * @return Objet class
     */
    public ArrayList<NodeAlignment> getAllAlignmentOfConcept(HikariDataSource ds,
            String idConcept, String idThesaurus) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        ArrayList<NodeAlignment> nodeAlignmentList = null;

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT id, created, modified,"
                            + " author, thesaurus_target, concept_target,"
                            + " uri_target, alignement_id_type, internal_id_thesaurus,"
                            + " internal_id_concept FROM alignement"
                            + " where internal_id_concept = '" + idConcept + "'"
                            + " and internal_id_thesaurus ='" + idThesaurus + "'";

                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    nodeAlignmentList = new ArrayList<>();
                    while (resultSet.next()) {
                        NodeAlignment nodeAlignment = new NodeAlignment();
                        nodeAlignment.setId_alignement(resultSet.getInt("id"));
                        nodeAlignment.setCreated(resultSet.getDate("created"));
                        nodeAlignment.setModified(resultSet.getDate("modified"));
                        nodeAlignment.setId_author(resultSet.getInt("author"));
                        nodeAlignment.setThesaurus_target(resultSet.getString("thesaurus_target"));
                        nodeAlignment.setConcept_target(resultSet.getString("concept_target"));
                        nodeAlignment.setUri_target(resultSet.getString("uri_target").trim());
                        nodeAlignment.setAlignement_id_type(resultSet.getInt("alignement_id_type"));
                        nodeAlignment.setInternal_id_thesaurus(resultSet.getString("internal_id_thesaurus"));
                        nodeAlignment.setInternal_id_concept(resultSet.getString("internal_id_concept"));

                        nodeAlignmentList.add(nodeAlignment);
                    }

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting All list of alignment of Concept  : " + idConcept, sqle);
        }
        return nodeAlignmentList;
    }

    /**
     * Retourne la liste des types d'alignements sous forme de MAP (id + Nom)
     *
     * @param ds
     * @return
     */
    public HashMap<String, String> getAlignmentType(HikariDataSource ds) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        HashMap<String, String> map = new HashMap<>();

        try {
            // Get connection from pool--- aqui salta para el install
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "select id, label_skos from alignement_type";

                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    while (resultSet.next()) {
                        map.put(
                                String.valueOf(resultSet.getInt("id")),
                                resultSet.getString("label_skos"));
                    }
                    resultSet.close();

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting Map of Type of Alignment : " + map.toString(), sqle);
        }
        return map;
    }

    /**
     * cette fonction permet de récupérer les informations de la table des
     * sources d'alignement
     *
     * @param ds
     * @param id_theso
     * @return
     */
    public ArrayList<AlignementSource> getAlignementSource(HikariDataSource ds, String id_theso) {
        ArrayList<AlignementSource> alignementSources = new ArrayList<>();

        Connection conn;
        Statement stmt;
        ResultSet resultSet;

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "select "
                            + " alignement_source.source, alignement_source.requete,"
                            + " alignement_source.type_rqt, alignement_source.alignement_format,"
                            + " alignement_source.id from alignement_source, thesaurus_alignement_source"
                            + " WHERE thesaurus_alignement_source.id_alignement_source = alignement_source.id"
                            + " AND thesaurus_alignement_source.id_thesaurus = '" + id_theso + "'"
                            + " AND gps = false";
                    resultSet = stmt.executeQuery(query);
                    while (resultSet.next()) {
                        AlignementSource alignementSource = new AlignementSource();
                        alignementSource.setSource(resultSet.getString("source"));
                        alignementSource.setRequete(resultSet.getString("requete"));
                        alignementSource.setTypeRequete(resultSet.getString("type_rqt"));
                        alignementSource.setAlignement_format(resultSet.getString("alignement_format"));
                        alignementSource.setId(resultSet.getInt("id"));
                        alignementSources.add(alignementSource);
                    }
                    resultSet.close();
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting colection of Type of Alignment : ", sqle);
        }
        return alignementSources;
    }

    /**
     * cette fonction permet de récupérer les informations de la table des
     * sources d'alignement
     *
     * @param ds
     * @param id_alignement_source
     * @return
     */
    public List<String> getSelectedAlignementOfThisTheso(HikariDataSource ds, int id_alignement_source) {
        List<String> listAlignementSourceSelected = null;

        Connection conn;
        Statement stmt;
        ResultSet resultSet;

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "select "
                            + " id_thesaurus from thesaurus_alignement_source"
                            + " WHERE id_alignement_source = " + id_alignement_source;
                    resultSet = stmt.executeQuery(query);
                    listAlignementSourceSelected = new ArrayList<>();
                    while (resultSet.next()) {
                        listAlignementSourceSelected.add(resultSet.getString("id_thesaurus"));

                    }
                    resultSet.close();
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting colection of Type of Alignment : ", sqle);
        }
        return listAlignementSourceSelected;
    }

    public ArrayList<AlignementSource> getAlignementSourceSAdmin(HikariDataSource ds) {
        ArrayList<AlignementSource> alignementSources = new ArrayList<>();

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "select  * from alignement_source";
                    resultSet = stmt.executeQuery(query);
                    while (resultSet.next()) {
                        AlignementSource alignementSource = new AlignementSource();
                        alignementSource.setSource(resultSet.getString("source"));
                        alignementSource.setRequete(resultSet.getString("requete"));
                        alignementSource.setTypeRequete(resultSet.getString("type_rqt"));
                        alignementSource.setAlignement_format(resultSet.getString("alignement_format"));
                        alignementSource.setId(resultSet.getInt("id"));
                        alignementSource.setDescription((resultSet.getString("description")));
                        alignementSources.add(alignementSource);
                    }
                    resultSet.close();
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting colection of Type of Alignment : ", sqle);
        }
        return alignementSources;
    }

    public ArrayList<String> typesRequetes(HikariDataSource ds,
            String cherche) throws SQLException {
        ArrayList<String> les_types = null;
        Statement stmt;
        ResultSet resultSet;
        Connection conn;

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    stmt = conn.createStatement();
                    String query = "select e.enumlabel\n"
                            + "from pg_type t, pg_enum e\n"
                            + "where t.oid = e.enumtypid\n"
                            + "and t.typname = '" + cherche + "'";
                    resultSet = stmt.executeQuery(query);
                    les_types = new ArrayList<>();
                    while (resultSet.next()) {
                        les_types.add(resultSet.getString("enumlabel"));
                    }

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while Types : " + cherche, sqle);
        }
        return les_types;
    }

    public boolean injenctdansBDAlignement(HikariDataSource ds, List<String> listThesos, AlignementSource alig,
            int id_user, String currentIdTheso) {
        int id_alignement;
        boolean status = false;

        try {
            Connection conn = ds.getConnection();
            conn.setAutoCommit(false);

            if (!insertAlignementSource(conn, alig, id_user)) {
                conn.rollback();
                conn.close();
                return false;
            }
            for (String listTheso : listThesos) {
                id_alignement = getId_Alignement(conn, alig.getSource());
                if (!insertSourceAlignementToTheso(conn, listTheso, id_alignement)) {
                    conn.rollback();
                    conn.close();
                    return false;
                }
            }
            status = true;
            conn.commit();
            if (!conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(AlignmentHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return status;
    }

    public boolean isThesoEnCours(String idTheso, String currentIdTheso) {
        boolean status = false;
        if (idTheso.equals(currentIdTheso)) {
            status = true;
        }
        return status;
    }

    public int getId_Alignement(Connection conn, String source) {
        int id_alignement = 0;
        Statement stmt;
        ResultSet rs;
        try {
            // Get connection from pool
            try {
                stmt = conn.createStatement();
                try {
                    stmt = conn.createStatement();

                    String query = "Select id from alignement_source "
                            + " where source = '" + source + "'";
                    rs = stmt.executeQuery(query);
                    if (rs.next()) {
                        id_alignement = rs.getInt("id");
                    }
                } finally {
                    stmt.close();
                }
            } finally {
                //conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while insert new Alignement : ", sqle);
        }
        return id_alignement;
    }

    private boolean insertAlignementSource(Connection conn, AlignementSource alig, int id_user) {
        Statement stmt;
        boolean status = false;
        try {
            // Get connection from pool
            try {
                stmt = conn.createStatement();
                try {
                    stmt = conn.createStatement();

                    String query = "Insert into alignement_source"
                            + "(source,requete,type_rqt,"
                            + "alignement_format, id_user, description) values('"
                            + alig.getSource() + "','"
                            + alig.getRequete() + "','"
                            + alig.getTypeRequete() + "','"
                            + alig.getAlignement_format() + "','"
                            + id_user + "','"
                            + alig.getDescription() + "');";
                    stmt.execute(query);
                    status = true;

                } finally {
                    stmt.close();
                }
            } finally {
                //conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while insert new Alignement : ", sqle);
        }
        return status;
    }

    /**
     * Permet de faire un update d'un alignement_source
     *
     * @param ds
     * @param alig
     * @param id
     */
    public void update_alignementSource(HikariDataSource ds, AlignementSource alig, int id) {
        Statement stmt;
        Connection conn;

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    stmt = conn.createStatement();

                    String query = "update alignement_source set "
                            + "source ='" + alig.getSource()
                            + "', requete ='" + alig.getRequete()
                            + "', type_rqt ='" + alig.getTypeRequete()
                            + "', alignement_format='" + alig.getAlignement_format()
                            + "', description ='" + alig.getDescription()
                            + "' where id =" + id;
                    stmt.execute(query);

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while insert new Alignement : ", sqle);
        }

    }

    /**
     * permet d'ajouter une source d'alignement à un ou plusieurs thésaurus on
     * supprime d'abord les anciennes valeurs, puis on ajoute les nouvelles
     *
     * @param ds
     * @param authorizedThesaurus
     * @param listThesos
     * @param idAlignement
     * @return
     */
    public boolean addSourceAlignementToTheso(HikariDataSource ds,
            ArrayList<Map.Entry<String, String>> authorizedThesaurus,
            List<String> listThesos, int idAlignement) {
        boolean status = false;
        try {
            Connection conn = ds.getConnection();
            conn.setAutoCommit(false);

            // suppression des anciennes relations
            for (Map.Entry<String, String> auEntry : authorizedThesaurus) {
                if (!deleteSourceAlignementFromTheso(conn,
                        auEntry.getValue(), idAlignement)) {
                    conn.rollback();
                    conn.close();
                    return false;
                }
            }

            for (String listTheso : listThesos) {
                if (!insertSourceAlignementToTheso(conn, listTheso, idAlignement)) {
                    conn.rollback();
                    conn.close();
                    return false;
                }
            }
            conn.commit();
            conn.close();
            status = true;

        } catch (SQLException ex) {
            Logger.getLogger(AlignmentHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return status;
    }

    /**
     * Permet d'effacer le alignement "idAlignement" du theso "idTheso"
     *
     * @param conn
     * @param idTheso
     * @param idAlignement
     * @return
     */
    private boolean deleteSourceAlignementFromTheso(Connection conn, String idTheso, int idAlignement) {
        Statement stmt;
        boolean status = false;
        try {
            // Get connection from pool
            try {
                stmt = conn.createStatement();
                try {
                    stmt = conn.createStatement();

                    String query = "delete from thesaurus_alignement_source"
                            + " where id_alignement_source = " + idAlignement
                            + " and id_thesaurus = '" + idTheso + "'";
                    stmt.executeUpdate(query);
                    status = true;

                } finally {
                    stmt.close();
                }
            } finally {
                //   conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while insert new Alignement to theasurus : " + idTheso + " id_alignement : " + idAlignement, sqle);
        }
        return status;
    }

    /**
     * Permet de ajouté un alignement a un theso
     *
     * @param conn
     * @param idTheso
     * @param idAlignement
     * @return
     */
    private boolean insertSourceAlignementToTheso(Connection conn, String idTheso, int idAlignement) {
        Statement stmt;
        boolean status = false;
        try {
            // Get connection from pool
            try {
                stmt = conn.createStatement();
                try {
                    stmt = conn.createStatement();

                    String query = "Insert into thesaurus_alignement_source"
                            + "(id_thesaurus, id_alignement_source) values("
                            + "'" + idTheso + "',"
                            + idAlignement + ")";
                    stmt.executeUpdate(query);
                    status = true;

                } finally {
                    stmt.close();
                }
            } finally {
                //   conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while insert new Alignement to theasurus : " + idTheso + " id_alignement : " + idAlignement, sqle);
        }
        return status;
    }

    /**
     * permet d'effacer une Alignement
     *
     * @param ds
     * @param id
     */
    public void efaceAligSour(HikariDataSource ds, int id) {
        Statement stmt;
        Connection conn;

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    stmt = conn.createStatement();

                    String query = "delete from alignement_source"
                            + " where id =" + id;
                    stmt.executeUpdate(query);

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while delete Alignement : ", sqle);
        }
    }

    /**
     * permet de savoir le group du concept idconcept
     *
     * @param ds
     * @param id_Theso
     * @param id_concept
     * @return
     */
    public String getGroupOfConcept(HikariDataSource ds, String id_Theso, String id_concept) {
        String group = "";
        Statement stmt;
        Connection conn;
        ResultSet rs;
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    stmt = conn.createStatement();

                    String query = "select  id_group from concept"
                            + " where id_thesaurus = '" + id_Theso
                            + "' and id_concept = '" + id_concept + "'";
                    rs = stmt.executeQuery(query);
                    if (rs.next()) {
                        group = rs.getString("id_group");
                    }
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while get it back group : ", sqle);
        }
        return group;
    }

    /**
     * permet de savoir si le concept idconcept a des enfants o pas
     *
     * @param ds
     * @param idtheso
     * @param idconcept
     * @return
     */
    public boolean isHaveChildren(HikariDataSource ds, String idtheso, String idconcept) {
        Statement stmt;
        Connection conn;
        ResultSet rs;
        boolean status = false;
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    stmt = conn.createStatement();

                    String query = "select id_concept2 from hierarchical_relationship "
                            + "where id_thesaurus = '" + idtheso
                            + "' and id_concept1='" + idconcept
                            + "' and role ='NT'";
                    rs = stmt.executeQuery(query);
                    if (rs.next()) {
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
            log.error("Error while search children", sqle);
        }
        return status;
    }

    public boolean isAlignedWithThisSource(HikariDataSource ds, String id_Concept,
            String id_Theso, int id_alignement_source) {
        Statement stmt;
        Connection conn;
        ResultSet rs;
        boolean status = false;
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    stmt = conn.createStatement();

                    String query = "select id from alignement "
                            + "where internal_id_thesaurus = '" + id_Theso
                            + "' and internal_id_concept='" + id_Concept
                            + "' and id_alignement_source =" + id_alignement_source;
                    rs = stmt.executeQuery(query);
                    if (rs.next()) {
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
            log.error("Error while see it's alignée", sqle);
        }
        return status;
    }

    public AlignementPreferences getListPreferencesAlignement(HikariDataSource ds, 
            String id_theso, int id_user, String id_concept_depart, int id_alignement_source) {
        AlignementPreferences alignementPreferences = new AlignementPreferences();
        Statement stmt;
        Connection conn;
        ResultSet rs;

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    stmt = conn.createStatement();
                    String query = "select * from alignement_preferences where id_thesaurus = '" + id_theso
                            + "' and id_user = " + id_user
                            + " and id_concept_depart ='"+id_concept_depart+"'"
                            + " and  id_alignement_source="+ id_alignement_source;
                    rs = stmt.executeQuery(query);
                    if (rs.next()) {
                        alignementPreferences.setId_concetp_depart(rs.getString("id_concept_depart"));
                        alignementPreferences.setId_concept_tratees(rs.getString("id_concept_tratees"));
                    }
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while get alignement's preferences", sqle);
        }
        return alignementPreferences;
    }

    public boolean validate_Preferences(HikariDataSource ds, String id_theso, int id_user,
            String id_concept_depart, ArrayList<String>listConceptTratees, int id_alignement_source ) {
        if (!existPreferences(ds, id_theso, id_user, id_concept_depart, id_alignement_source)) {
            if (!insert_validate_Preferences(ds, id_theso, id_user, id_concept_depart, listConceptTratees, id_alignement_source)) {
                return false;
            }
        } else if (!enregistreProgres(ds, id_theso, id_concept_depart, id_user, listConceptTratees, id_alignement_source))  {
            return false;
        }
        return true;
    }

    private boolean existPreferences(HikariDataSource ds, String id_theso, int id_user, String id_concept_depart, int id_alignement_source) {
        Statement stmt;
        Connection conn;
        ResultSet rs;
        boolean status = false;
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    stmt = conn.createStatement();

                    String query = "SELECT id from alignement_preferences"
                            + " where id_thesaurus ='" + id_theso + "'"
                            + " and id_user= " + id_user
                            + " and id_concept_depart ='"+id_concept_depart+"'"
                            + " and id_alignement_source ="+ id_alignement_source;
                    rs = stmt.executeQuery(query);
                    if (rs.next()) {
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
            log.error("Error while see it's exist", sqle);
        }
        return status;
    }

    private boolean insert_validate_Preferences(HikariDataSource ds, String id_theso, int id_user,
            String id_concept_depart, ArrayList<String> listConceptTratees, int id_alignement_source) {

        Statement stmt;
        Connection conn;
        String conceptstratees = "";
        for (String id : listConceptTratees) {
            conceptstratees += id+"#";
        }
        boolean status = false;
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    stmt = conn.createStatement();

                    String query = "insert into alignement_preferences "
                            + " (id_thesaurus, id_user, id_concept_depart, id_concept_tratees, id_alignement_source )"
                            + " values('" + id_theso + "'," + id_user + ",'" + id_concept_depart + "',"
                            + " '"+conceptstratees +"',"+ id_alignement_source+")";
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
            log.error("Error while see it's alignée", sqle);
        }
        return status;
    }

    public boolean enregistreProgres(HikariDataSource ds, String id_theso, String id_concept,
            int id_user, ArrayList<String> listConceptTratees, int id_alignement_source) {
        Statement stmt;
        Connection conn;
        String conceptstratees = "";
        for (String id : listConceptTratees) {
            conceptstratees += id+"#";
        }
        boolean status = false;
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    stmt = conn.createStatement();

                    String query = "Update alignement_preferences"
                            + " SET id_concept_tratees ='" + conceptstratees + "'"
                            + " where id_thesaurus ='" + id_theso + "'"
                            + " and id_user =" + id_user
                            + " and id_concept_depart ='" + id_concept + "'"
                            + " and  id_alignement_source ="+ id_alignement_source;
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
            log.error("Error while save progress", sqle);
        }
        return status;
    }

    /**
     * public boolean update_IdConceptInTable(HikariDataSource ds, String
     * id_theso, int id_user) { Statement stmt; Connection conn; ResultSet rs;
     * boolean status = false; try { // Get connection from pool conn =
     * ds.getConnection(); try { stmt = conn.createStatement(); try { stmt =
     * conn.createStatement();
     *
     * String query = "Update alignement_preferences set " +" id_concept = '0'"
     * + " where id_thesaurus = '" + id_theso + "'" + " and id_user = " +
     * id_user; stmt.executeUpdate(query); status = true; } finally {
     * stmt.close(); } } finally { conn.close(); } } catch (SQLException sqle) {
     * // Log exception log.error("Error while reinit id_concept", sqle); }
     * return status;
    }
     */
    public String getListIdCTrates(HikariDataSource ds, String id_theso, int id_user) {
        String trates = "";
        Statement stmt;
        Connection conn;
        ResultSet rs;
        boolean status = false;
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    stmt = conn.createStatement();

                    String query = "SELECT id_concept_tratees from alignement_preferences"
                            + " where id_thesaurus = '" + id_theso + "'"
                            + " and id_user = " + id_user;
                    rs = stmt.executeQuery(query);
                    if (rs.next()) {
                        trates = rs.getString("id_concept_tratees");
                    }
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while reinit id_concept", sqle);
        }
        return trates;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
