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
import java.util.logging.Level;
import java.util.logging.Logger;
import mom.trd.opentheso.bdd.helper.nodes.NodeUser;
import mom.trd.opentheso.bdd.helper.nodes.candidat.NodeCandidatValue;
import mom.trd.opentheso.bdd.helper.nodes.candidat.NodeMessageAdmin;
import mom.trd.opentheso.bdd.helper.nodes.candidat.NodeProposition;
import mom.trd.opentheso.bdd.helper.nodes.candidat.NodeTraductionCandidat;
import mom.trd.opentheso.bdd.tools.StringPlus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author miled.rousset
 */
public class CandidateHelper {

    private final Log log = LogFactory.getLog(ThesaurusHelper.class);

    public CandidateHelper() {
    }

     /**
     * Cette fonction permet d'ajouter un group (MT, domaine etc..) avec le
     * libellé
     *
     * @param conn
     * @param lexical_value
     * @param idLang
     * @param idThesaurus
     * @param contributor
     * @param note
     * @param idParentConcept
     * @param idGroup
     * @return null si le term existe ou si erreur, sinon le numero de Concept
     */
    public String addCandidat_rollBack(Connection conn,
            String lexical_value,
            String idLang, String idThesaurus,  
            int contributor, String note,
            String idParentConcept, String idGroup) {

        try {
            conn.setAutoCommit(false);
            
            CandidateHelper candidateHelper = new CandidateHelper();
            // controle si le term existe avant de rajouter un concept
            if (candidateHelper.isCandidatExist_rollBack(conn, lexical_value, idThesaurus, idLang)) {
                conn.rollback();
                conn.close();
                return null;
            }
            
            String idConceptCandidat = addConceptCandidat_rollback(conn, idThesaurus);
            if (idConceptCandidat == null) {
                conn.rollback();
                conn.close();
                return null;
            }
            
            String idTermCandidat = candidateHelper.addTermCandidat_RollBack(conn, lexical_value, idLang, idThesaurus, contributor);
            if (idTermCandidat == null) {
                conn.rollback();
                conn.close();
                return null;
            }
            
            if(!addRelationConceptTermCandidat_RollBack(conn, idConceptCandidat,
                    idTermCandidat, idThesaurus)) {
                conn.rollback();
                conn.close();
                return null;
            }
            
            if(!candidateHelper.addPropositionCandidat_RollBack(conn,
                    idConceptCandidat, contributor, idThesaurus,
                    note, idParentConcept, idGroup)){
                conn.rollback();
                conn.close();
                return null;
            }
            
            return idConceptCandidat;
        } catch (SQLException ex) {
            Logger.getLogger(CandidateHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }  
    
    
     /**
     * Cette fonction permet d'ajouter un group (MT, domaine etc..) avec le
     * libellé
     *
     * @param ds
     * @param lexical_value
     * @param idLang
     * @param idThesaurus
     * @param contributor
     * @param note
     * @param idParentConcept
     * @param idGroup
     * @return null si le term existe ou si erreur, sinon le numero de Concept
     */
    public String addCandidat(HikariDataSource ds,
            String lexical_value,
            String idLang, String idThesaurus,  
            int contributor, String note,
            String idParentConcept, String idGroup) {

        CandidateHelper candidateHelper = new CandidateHelper();
        // controle si le term existe avant de rajouter un concept
        if (candidateHelper.isCandidatExist(ds, lexical_value, idThesaurus, idLang)) {
            return null;
        }

        String idConceptCandidat = addConceptCandidat(ds, idThesaurus);
        if (idConceptCandidat == null) {
            return null;
        }

        String idTermCandidat = candidateHelper.addTermCandidat(ds, lexical_value, idLang, idThesaurus, contributor);
        if (idTermCandidat == null) {
            return null;
        }
        
        if(!addRelationConceptTermCandidat(ds, idConceptCandidat,
                idTermCandidat, idThesaurus))
            return null;
        
        candidateHelper.addPropositionCandidat(ds, idConceptCandidat, contributor, idThesaurus, note, idParentConcept, idGroup);

        return idConceptCandidat;
    }  

    /**
     * Cette fonction permet d'ajouter une relation entre 
     * Concept_candidat et terme_candidat
     *
     * @param conn
     * @param idConceptCandidat
     * @param idTermCandidat
     * @param idThesaurus
     * @return booelean
     */
    public boolean addRelationConceptTermCandidat_RollBack(Connection conn,
                String idConceptCandidat,
                String idTermCandidat, String idThesaurus) {

        Statement stmt;
        boolean status = false;

        try {
            try {
                stmt = conn.createStatement();
                try {
                    String query = "Insert into concept_term_candidat"
                            + "(id_concept, id_term, id_thesaurus)"
                            + " values ("
                            + "'" + idConceptCandidat + "'"
                            + ",'" + idTermCandidat + "'"
                            + ",'" + idThesaurus + "')";

                    stmt.executeUpdate(query);
                    status = true;

                } finally {
                    stmt.close();
                }
            } finally {
            //    conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while adding Relation Candidat Term : "
                    + idConceptCandidat, sqle);
        }
        return status;
    }     
    
    /**
     * Cette fonction permet d'ajouter une relation entre 
     * Concept_candidat et terme_candidat
     *
     * @param ds
     * @param idConceptCandidat
     * @param idTermCandidat
     * @param idThesaurus
     * @return booelean
     */
    public boolean addRelationConceptTermCandidat(HikariDataSource ds,
                String idConceptCandidat,
                String idTermCandidat, String idThesaurus) {

        Connection conn;
        Statement stmt;
        boolean status = false;

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "Insert into concept_term_candidat"
                            + "(id_concept, id_term, id_thesaurus)"
                            + " values ("
                            + "'" + idConceptCandidat + "'"
                            + ",'" + idTermCandidat + "'"
                            + ",'" + idThesaurus + "')";

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
            log.error("Error while adding Relation Candidat Term : "
                    + idConceptCandidat, sqle);
        }
        return status;
    }        

    /**
     * Cette fonction permet d'ajouter un Concept à la table Concept, en
     * paramètre un objet Classe Concept
     *
     * @param conn
     * @param idThesaurus
     * @return idConceptCandidat 
     */
    public String addConceptCandidat_rollback(Connection conn,
            String idThesaurus) {

        String idConcept = null;
        Statement stmt;
        ResultSet resultSet;

        try {
            try {
                stmt = conn.createStatement();
                try {
                    String query = "select max(id) from concept_candidat";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    resultSet.next();
                    int idNumerique = resultSet.getInt(1);
                    idConcept = "CA_" + (++idNumerique);
                    while (isCandidatExist(conn,  idConcept, idThesaurus)) {                        
                         idConcept = "CA_" + (++idNumerique);
                    }
                    /**
                     * Ajout des informations dans la table Concept_candidat
                     */
                    query = "Insert into concept_candidat "
                            + "(id_concept, id_thesaurus)"
                            + " values ("
                            + "'" + idConcept + "'"
                            + ",'" + idThesaurus + "')";

                    stmt.executeUpdate(query);

                } finally {
                    stmt.close();
                }
            } finally {
            //    conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while adding Concept_candidat : " + idConcept, sqle);
            idConcept = null;
        }
        return idConcept;
    }    
    
    /**
     * Cette fonction permet d'ajouter un Concept à la table Concept, en
     * paramètre un objet Classe Concept
     *
     * @param ds
     * @param idThesaurus
     * @return idConceptCandidat 
     */
    public String addConceptCandidat(HikariDataSource ds,
            String idThesaurus) {

        String idConcept = null;
        Connection conn;
        Statement stmt;
        ResultSet resultSet;

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "select max(id) from concept_candidat";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    resultSet.next();
                    int idNumerique = resultSet.getInt(1);
                    idConcept = "CA_" + (++idNumerique);
                    while (isCandidatExist(ds.getConnection(),  idConcept, idThesaurus)) {                        
                         idConcept = "CA_" + (++idNumerique);
                    }

                    /**
                     * Ajout des informations dans la table Concept_candidat
                     */
                    query = "Insert into concept_candidat "
                            + "(id_concept, id_thesaurus)"
                            + " values ("
                            + "'" + idConcept + "'"
                            + ",'" + idThesaurus + "')";

                    stmt.executeUpdate(query);

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while adding Concept*_candidat : " + idConcept, sqle);
        }
        return idConcept;
    }
    
    /**
     * Cette fonction permet de supprimer un ConceptCandidat avec toutes les relations
     *
     * @param ds
     * @param idConceptCandidat
     * @param idThesaurus
     * @return boolean 
     */
    public boolean deleteConceptCandidat(HikariDataSource ds,
            String idConceptCandidat,
            String idThesaurus) {
        
        CandidateHelper candidateHelper = new CandidateHelper();
        if(!candidateHelper.deleteTermsCandidatsOfConcept(ds, idConceptCandidat, idThesaurus))
            return false;
        return deleteThisConceptCandidat(ds, idConceptCandidat, idThesaurus);
    }       
    
    /**
     * Cette fonction permet de récupérer un Concept par son id et son thésaurus
     * sous forme de classe Concept (sans les relations) ni le Terme
     *
     * @param ds
     * @param idConcept
     * @param idThesaurus
     * @return Objet class Concept
     */
    public boolean deleteThisConceptCandidat(HikariDataSource ds,
            String idConcept, String idThesaurus) {

        Connection conn;
        Statement stmt;
        boolean status = false;
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "delete from concept_candidat where"
                            + " id_thesaurus = '" + idThesaurus + "'"
                            + " and id_concept = '" + idConcept + "'";
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
            log.error("Error while deleting this Concept_candidat : " + idConcept, sqle);
        }
        return status;
    }        

    /**
     * Cette fonction permet de rajouter un term_candidat 
     * 
     * @param conn
     * @param lexical_value
     * @param idLang
     * @param idThesaurus
     * @param contributor
     * @return  idConceptCandidat
     */
    public String addTermCandidat_RollBack(Connection conn,
            String lexical_value,
            String idLang, String idThesaurus,  
            int contributor) {

        Statement stmt;
        ResultSet resultSet;
        String idTerm = null;
        lexical_value = new StringPlus().convertString(lexical_value);
        try {
            try {
                stmt = conn.createStatement();
                try {
                    String query = "select max(id) from term_candidat";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    resultSet.next();
                    int idNumerique = resultSet.getInt(1);
                    idTerm = "TC_" + (++idNumerique);

                    /**
                     * Ajout des informations dans la table Concept
                     */
                    query = "Insert into term_candidat "
                            + "(id_term, lexical_value, lang, "
                            + "id_thesaurus, contributor)"
                            + " values ("
                            + "'" + idTerm + "'"
                            + ",'" + lexical_value + "'"
                            + ",'" + idLang + "'"
                            + ",'" + idThesaurus + "'"
                            + "," + contributor + ")";

                    stmt.executeUpdate(query);

                } finally {
                    stmt.close();
                }
            } finally {
            //    conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while adding Term_candidat  : " + idTerm, sqle);
            idTerm = null;
        }
        return idTerm;
    }     
    
    /**
     * Cette fonction permet de rajouter un term_candidat 
     * 
     * @param ds
     * @param lexical_value
     * @param idLang
     * @param idThesaurus
     * @param contributor
     * @return  idConceptCandidat
     */
    public String addTermCandidat(HikariDataSource ds,
            String lexical_value,
            String idLang, String idThesaurus,  
            int contributor) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        String idTerm = null;
        lexical_value = new StringPlus().convertString(lexical_value);
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "select max(id) from term_candidat";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    resultSet.next();
                    int idNumerique = resultSet.getInt(1);
                    idTerm = "TC_" + (++idNumerique);

                    /**
                     * Ajout des informations dans la table Concept
                     */
                    query = "Insert into term_candidat "
                            + "(id_term, lexical_value, lang, "
                            + "id_thesaurus, contributor)"
                            + " values ("
                            + "'" + idTerm + "'"
                            + ",'" + lexical_value + "'"
                            + ",'" + idLang + "'"
                            + ",'" + idThesaurus + "'"
                            + "," + contributor + ")";

                    stmt.executeUpdate(query);

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while adding Term_candidat  : " + idTerm, sqle);
        }
        return idTerm;
    } 
    
        /**
     * Cette fonction permet d'ajouter un message de justification sur un candidat
     * refusé 
     * 
     * @param ds
     * @param idConceptCandidat
     * @param message
     * @param adminId
     * @param idThesaurus
     * @return  boolean
     */
    public boolean addAdminMessage(HikariDataSource ds,
            String idConceptCandidat,
            String idThesaurus,
            int adminId,
            String message) {

        Connection conn;
        Statement stmt;
        boolean status = false;
        message = new StringPlus().convertString(message);
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "Update concept_candidat set"
                            + " modified = current_date,"
                            + " admin_id = " + adminId + ","
                            + " admin_message = '" + message + "'"
                            + " where id_concept = '" + idConceptCandidat + "'"
                            + " and id_thesaurus = '" + idThesaurus + "'";

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
            log.error("Error while adding Admin Message of candidat  : " + idConceptCandidat, sqle);
        }
        return status;
    } 
    
    /**
     * Cette fonction permet de retourner le nombre de candidats d'un concept
     *
     * @param ds
     * @param idConcept
     * @param idThesaurus
     * @return Objet class NodeMessageAdmin
     */
    public NodeMessageAdmin getMessageAdmin(HikariDataSource ds,
            String idThesaurus, String idConcept) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        NodeMessageAdmin nodeMessageAdmin = null;

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT users.username, users.id_user,"
                            + " concept_candidat.admin_message"
                            + " FROM concept_candidat, users WHERE"
                            + " concept_candidat.admin_id = users.id_user"
                            + " and concept_candidat.id_concept = '" + idConcept + "'"
                            + " and concept_candidat.id_thesaurus = '" + idThesaurus + "'";

                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    if (resultSet.next()) {
                        nodeMessageAdmin = new NodeMessageAdmin();
                        nodeMessageAdmin.setId_user(resultSet.getInt("id_user"));
                        nodeMessageAdmin.setUser(resultSet.getString("username"));
                        nodeMessageAdmin.setMessage(resultSet.getString("admin_message"));
                    }

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting Admin Message of candidat : " + idConcept, sqle);
        }
        return nodeMessageAdmin;
    }    
    

    /**
     * Cette fonction permet de rajouter un term_candidat 
     * 
     * @param ds
     * @param status
     * @param idConceptCandidat
     * @param idThesaurus
     * @return  boolean
     */
    public boolean updateCandidatStatus(HikariDataSource ds,
            String status,
            String idThesaurus,
            String idConceptCandidat) {

        Connection conn;
        Statement stmt;

        boolean etat = false;
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "Update concept_candidat set"
                            + " status = '" + status + "',"
                            + " modified = current_date"
                            + " where id_concept = '" + idConceptCandidat + "'"
                            + " and id_thesaurus = '" + idThesaurus + "'";

                    stmt.executeUpdate(query);
                    updateDateOfCandidat(conn, idConceptCandidat, idThesaurus);
                    etat = true;

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while updating Status of candidat  : " + idConceptCandidat, sqle);
        }
        return etat;
    }
    
    /**
     * Cette fonction permet de mettre à jour le commentaire d'un candidat,
     * le niveau et le groupe, cette modification est autorisée par propriétaire. 
     * 
     * @param ds
     * @param idCandidat
     * @param idUser
     * @param idThesaurus
     * @param note
     * @param idConceptParent
     * @param idGroup
     * @return  boolean
     */
    public boolean updatePropositionCandidat(HikariDataSource ds,
            String idCandidat,
            int idUser,
            String idThesaurus,
            String note,
            String idConceptParent,
            String idGroup) {

        note = new StringPlus().convertString(note);
        Connection conn;
        Statement stmt;

        boolean etat = false;
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "Update proposition set"
                            + " note = '" + note + "',"
                            + " concept_parent = '" + idConceptParent + "',"
                            + " id_group = '" + idGroup + "',"
                            + " modified = current_date"
                            + " where id_concept = '" + idCandidat + "'"
                            + " and id_thesaurus = '" + idThesaurus + "'"
                            + " and id_user = " + idUser;

                    stmt.executeUpdate(query);
                    updateDateOfCandidat(conn, idCandidat, idThesaurus);
                    etat = true;

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while updating proposition of candidat  : " + idCandidat, sqle);
        }
        return etat;
    }    
    
    /**
     * Cette fonction permet de mettre à jour le nom d'un candidat qui vient d'être déposé
     * 
     * @param ds
     * @param idCandidat
     * @param idThesaurus
     * @param value
     * @return  boolean
     */
    public boolean updateMotCandidat(HikariDataSource ds,
            String idCandidat,
            String idThesaurus,
            String value) {

        Connection conn;
        Statement stmt;

        boolean etat = false;
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                String idTermCdt = getIdTermOfConceptCandidat(ds, idCandidat, idThesaurus);
                try {
                    String query = "Update term_candidat set"
                            + " lexical_value = '" + value + "',"
                            + " modified = current_date"
                            + " where id_term = '" + idTermCdt + "'"
                            + " and id_thesaurus = '" + idThesaurus + "'";

                    stmt.executeUpdate(query);
                    updateDateOfCandidat(conn, idCandidat, idThesaurus);
                    etat = true;

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while updating proposition of candidat  : " + idCandidat, sqle);
        }
        return etat;
    }  
    
    /**
     * Cette fonction permet de mettre à jour le status d'un candidat
     * 
     * @param ds
     * @param idConceptCandidat
     * @param idThesaurus
     * @return  idTermCandidat
     */
    public String getIdTermOfConceptCandidat(HikariDataSource ds,
            String idConceptCandidat, String idThesaurus) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        String idTermCandidat = null;
        
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT id_term"
                            + " FROM concept_term_candidat"
                            + " WHERE id_thesaurus = '" + idThesaurus + "'"
                            + " and id_concept = '" + idConceptCandidat + "'";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    if(resultSet.next()) {
                        idTermCandidat = resultSet.getString("id_term");
                    }
                    else
                        return null;

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting idTermCandidat of idConceptCandidat : " + idConceptCandidat, sqle);
        }
        return idTermCandidat;
    }       
    
     /**
     * Cette fonction permet de retourner l'Id du candidat d'après son nom
     * 
     * @param ds
     * @param title
     * @param idThesaurus
     * @return  idTermCandidat
     */
    public String getIdCandidatFromTitle(HikariDataSource ds,
            String title, String idThesaurus) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        String idTermCandidat = null;
        
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT concept_candidat.id_concept"
                            + " FROM concept_candidat, concept_term_candidat, term_candidat"
                            + " WHERE"
                            + " concept_candidat.id_concept = concept_term_candidat.id_concept"
                            + " AND"
                            + " concept_term_candidat.id_thesaurus = term_candidat.id_thesaurus"
                            + " AND"
                            + " term_candidat.id_term = concept_term_candidat.id_term"
                            + " AND"
                            + " term_candidat.id_thesaurus = '" + idThesaurus + "'"
                            + " and"
                            + " term_candidat.lexical_value = '" + title + "'";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    if(resultSet.next()) {
                        idTermCandidat = resultSet.getString("id_concept");
                    }
                    else
                        return null;

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting idCandidat from candidat value : " + title, sqle);
        }
        return idTermCandidat;
    }   
    
    /**
     * Cette fonction permet de supprimer un term_candidat 
     * 
     * @param ds
     * @param idConceptCandidat
     * @param idLang
     * @param idThesaurus
     * @param contributor
     * @return  boolean
     */
    public boolean deleteTraductionTermCandidat(HikariDataSource ds,
            String idConceptCandidat,
            String idLang, String idThesaurus,  
            int contributor) {

        Connection conn;
        Statement stmt;

        String idTermCandidat;
        boolean status = false;
        
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    idTermCandidat = getIdTermOfConceptCandidat(ds, idConceptCandidat, idThesaurus);
                    if(idTermCandidat == null) return false;
                    
                    String query = "delete from term_candidat where"
                            + " id_thesaurus = '" + idThesaurus + "'"
                            + " and id_term = '" + idTermCandidat + "'"
                            + " and lang = '" + idLang + "'"
                            + " and contributor = '" + contributor + "'" 
                             ;
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
            log.error("Error while deleting Term_candidat of conceptCandidat : " + idConceptCandidat, sqle);
        }
        return status;
    }    

    /**
     * Cette fonction permet de supprimer un term_candidat 
     * 
     * @param ds
     * @param idConceptCandidat
     * @param idThesaurus
     * @return  boolean
     */
    public boolean deleteTermsCandidatsOfConcept(HikariDataSource ds,
            String idConceptCandidat, String idThesaurus) {

        Connection conn;
        Statement stmt;

        String idTermCandidat = null;
        boolean status = false;
        
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    idTermCandidat = getIdTermOfConceptCandidat(ds, idConceptCandidat, idThesaurus);
                    if(idTermCandidat == null) return false;
                    
                    String query = "delete from term_candidat where"
                            + " id_thesaurus = '" + idThesaurus + "'"
                            + " and id_term = '" + idTermCandidat + "'";
                    stmt.executeUpdate(query);
                    
                    query = "delete from concept_term_candidat where"
                            + " id_thesaurus = '" + idThesaurus + "'"
                            + " and id_concept = '" + idConceptCandidat + "'";
                    stmt.executeUpdate(query);
                    
                    query = "delete from proposition where"
                            + " id_thesaurus = '" + idThesaurus + "'"
                            + " and id_concept = '" + idConceptCandidat + "'";
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
            log.error("Error while deleting Term_candidat of conceptCandidat : " + idConceptCandidat, sqle);
        }
        return status;
    }     
    
    /**
     * Cette fonction permet de rajouter une traduction pour un term_candidat 
     * 
     * @param ds
     * @param idConcept
     * @param lexical_value
     * @param idLang
     * @param idThesaurus
     * @param contributor
     * @return  idConceptCandidat
     */
    public boolean addTermCandidatTraduction(HikariDataSource ds,
            String idConcept,
            String lexical_value,
            String idLang, String idThesaurus,  
            int contributor) {

        Connection conn;
        Statement stmt;
        boolean status = false;
        String idTermCandidat = null;
        lexical_value = new StringPlus().convertString(lexical_value);
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    idTermCandidat = getIdTermOfConceptCandidat(ds, idConcept, idThesaurus);
                    if(idTermCandidat == null) return false;
                    
                    String query = "Insert into term_candidat "
                            + "(id_term, lexical_value, lang, "
                            + "id_thesaurus, contributor)"
                            + " values ("
                            + "'" + idTermCandidat + "'"
                            + ",'" + lexical_value + "'"
                            + ",'" + idLang + "'"
                            + ",'" + idThesaurus + "'"
                            + "," + contributor + ")";

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
            log.error("Error while adding Traduction of Term_candidat  : " + idTermCandidat, sqle);
        }
        return status;
    }      

    /**
     * Cette fonction permet de rajouter une proposition de candidat 
     * dans la table propositon
     * 
     * @param conn
     * @param idConcept
     * @param idUser
     * @param idThesaurus
     * @param note 
     * @param idConceptParent 
     * @param idGroup 
     * @return  idConceptCandidat
     */
    public boolean addPropositionCandidat_RollBack(Connection conn,
            String idConcept,
            int idUser, String idThesaurus,  
            String note,
            String idConceptParent, String idGroup) {

        note = new StringPlus().convertString(note);
        Statement stmt;
        boolean status = false;
        
        try {
            try {
                stmt = conn.createStatement();
                try {
                    String query = "Insert into proposition "
                            + "(id_concept, id_user,"
                            + " id_thesaurus, note, concept_parent,"
                            + " id_group)"
                            + " values ("
                            + "'" + idConcept + "'"
                            + "," + idUser + ""
                            + ",'" + idThesaurus + "'"
                            + ",'" + note + "'"
                            + ",'" + idConceptParent + "'"
                            + ",'" + idGroup + "')";

                    stmt.executeUpdate(query);
                    updateDateOfCandidat(conn, idConcept, idThesaurus);
                    status = true;

                } finally {
                    stmt.close();
                }
            } finally {
            //    conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while adding Proposition Candidat  : " + idConcept, sqle);
        }
        return status;
    }
    
    /**
     * cette fonction permet de mettre à jour la date de modification du candidat
     * @return 
     */
    private boolean updateDateOfCandidat(Connection conn,
            String idConcept,
            String idThesaurus) {

        Statement stmt;
        try {
            try {
                stmt = conn.createStatement();
                try {
                    String query = "Update concept_candidat set"
                            + " modified = now()"
                            + " where id_concept = '" + idConcept + "'"
                            + " and id_thesaurus = '" + idThesaurus + "'";
                    
                    stmt.executeUpdate(query);
                    return true;

                } finally {
                    stmt.close();
                }
            } finally {
            //    conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while uddating date of Candidat : " + idConcept);
        }
        return false;
    }
    
    /**
     * Cette fonction permet de rajouter une proposition de candidat 
     * dans la table propositon
     * 
     * @param ds
     * @param idConcept
     * @param idUser
     * @param idThesaurus
     * @param note 
     * @param idConceptParent 
     * @param idGroup 
     * @return  idConceptCandidat
     */
    public boolean addPropositionCandidat(HikariDataSource ds,
            String idConcept,
            int idUser, String idThesaurus,  
            String note,
            String idConceptParent, String idGroup) {

        note = new StringPlus().convertString(note);
        Connection conn;
        Statement stmt;
        boolean status = false;
        
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "Insert into proposition "
                            + "(id_concept, id_user,"
                            + " id_thesaurus, note, concept_parent,"
                            + " id_group)"
                            + " values ("
                            + "'" + idConcept + "'"
                            + "," + idUser + ""
                            + ",'" + idThesaurus + "'"
                            + ",'" + note + "'"
                            + ",'" + idConceptParent + "'"
                            + ",'" + idGroup + "')";

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
            log.error("Error while adding Proposition Candidat  : " + idConcept, sqle);
        }
        return status;
    }  
    
    
    /**
     * Cette fonction permet de supprimer une proposition de candidat 
     * dans la table propositon
     * 
     * @param ds
     * @param idConcept
     * @param idUser
     * @param idThesaurus

     * @return  idConceptCandidat
     */
    public boolean deletePropositionCandidat(HikariDataSource ds,
            String idConcept,
            int idUser, String idThesaurus) {

        Connection conn;
        Statement stmt;
        boolean status = false;
        
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "delete from proposition where"
                            + " id_concept ='" + idConcept + "'"
                            + " and id_user =" + idUser + ""
                            + " and id_thesaurus = '" + idThesaurus + "'";

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
            log.error("Error while deleting Proposition candidat  : " + idConcept, sqle);
        }
        return status;
    }      

    /**
     * Permet de retourner une ArrayList de NodeConceptCandidat par
     * thésaurus
     * Si le Candidat n'est pas traduit dans la langue en cours, on récupère
     * l'identifiant pour l'afficher à la place
     *
     * @param ds le pool de connexion
     * @param idConcept
     * @param idThesaurus
     * @param idUser
     * @return Objet Class ArrayList NodeProposition
     */
    public NodeProposition getNodePropositionOfUser(HikariDataSource ds,
            String idConcept, String idThesaurus, int idUser) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        
        NodeProposition nodeProposition = null;
        
        
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT proposition.id_user,"
                            + " users.username, proposition.note,"
                            + " proposition.created,"
                            + " proposition.modified,"
                            + " proposition.concept_parent,"
                            + " proposition.id_group"
                            + " FROM proposition, users WHERE "
                            + " proposition.id_user = users.id_user"
                            + " and proposition.id_concept = '" + idConcept + "'"
                            + " and proposition.id_thesaurus = '" + idThesaurus + "'"
                            + " and proposition.id_user = " + idUser;
                            
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    if (resultSet != null) {
                        if(resultSet.next()) {
                            nodeProposition = new NodeProposition();
                            nodeProposition.setId_user(resultSet.getInt("id_user"));
                            nodeProposition.setUser(resultSet.getString("username"));
                            nodeProposition.setNote(resultSet.getString("note"));
                            nodeProposition.setCreated(resultSet.getDate("created"));
                            nodeProposition.setModified(resultSet.getDate("modified"));
                            nodeProposition.setIdConceptParent(resultSet.getString("concept_parent"));
                            nodeProposition.setIdGroup(resultSet.getString("id_group"));
                        }
                    }

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting List of node Proposition Candidats of Concept Candidat : " + idConcept, sqle);
        }
        return nodeProposition;
    } 
    
    /**
     * Permet de retourner une ArrayList de NodeUser par
     * thésaurus et Concept 
     * c'est la liste des personnes qui ont déposé ce candidat
     *
     * @param ds le pool de connexion
     * @param idConcept
     * @param idThesaurus
     * @return Objet Class ArrayList NodeUSer
     */
    public ArrayList<NodeUser> getListUsersOfCandidat(HikariDataSource ds,
            String idConcept, String idThesaurus) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        
        ArrayList <NodeUser> nodeUserList = null;
        
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT users.username, users.id_user,"
                            + " proposition.modified"
                            + " FROM proposition, users WHERE"
                            + " proposition.id_user = users.id_user"
                            + " and proposition.id_concept = '" + idConcept + "'"
                            + " and proposition.id_thesaurus = '" + idThesaurus + "'"
                            + " order By proposition.modified DESC;";
                            
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    if (resultSet != null) {
                        nodeUserList = new ArrayList<> ();
                        while(resultSet.next()) {
                            NodeUser nodeUser = new NodeUser();
                            nodeUser.setId(resultSet.getInt("id_user"));
                            nodeUser.setName(resultSet.getString("username"));
                            nodeUserList.add(nodeUser);
                        }
                    }

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting List of nodeUsersCandidat of ConceptCandidat : " + idConcept, sqle);
        }
        return nodeUserList;
    }     

    /**
     * Permet de retourner une ArrayList de nodeTraductionCandidat par
     * thésaurus
     *
     * @param ds le pool de connexion
     * @param idConcept
     * @param idThesaurus
     * @param idLang
     * @return Objet Class ArrayList nodeTraductionCandidat
     */
    public ArrayList<NodeTraductionCandidat> getNodeTraductionCandidat(HikariDataSource ds,
            String idConcept, String idThesaurus, String idLang) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        ArrayList <NodeTraductionCandidat> nodeTraductionCandidatList = null; 
        
        String idTermCandidat = null;

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    idTermCandidat = getIdTermOfConceptCandidat(ds, idConcept, idThesaurus);
                    String query = "SELECT term_candidat.lexical_value, term_candidat.lang,"
                            + " users.username, users.id_user"
                            + " FROM users, term_candidat WHERE"
                            + " term_candidat.contributor = users.id_user"
                            + " and term_candidat.lang != '" + idLang + "'"
                            + " and term_candidat.id_thesaurus = '" + idThesaurus + "'"
                            + " and term_candidat.id_term = '" + idTermCandidat + "'"
                            + " order by users.username ASC";

                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    if (resultSet != null) {
                         nodeTraductionCandidatList = new ArrayList <>();
                        while(resultSet.next()) {
                           NodeTraductionCandidat nodeTraductionCandidat = new NodeTraductionCandidat();
                           nodeTraductionCandidat.setIdLang(resultSet.getString("lang"));
                           nodeTraductionCandidat.setTitle(resultSet.getString("lexical_value"));
                           nodeTraductionCandidat.setUseId(resultSet.getInt("id_user"));
                           nodeTraductionCandidat.setUser(resultSet.getString("username"));
                           nodeTraductionCandidatList.add(nodeTraductionCandidat);
                        }
                    }

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting Traductions of Candidat : " + idConcept, sqle);
        }
        return nodeTraductionCandidatList;
 
    }      
    
    
    /**
     * Permet de retourner une ArrayList de NodeConceptCandidat par
     * thésaurus, c'est la liste des candidats en attente (status = a)
     * Si le Candidat n'est pas traduit dans la langue en cours, on récupère
     * l'identifiant pour l'afficher à la place
     *
     * @param ds le pool de connexion
     * @param idThesaurus
     * @param idLang
     * @return Objet Class ArrayList NodeCandidatValue
     */
    public ArrayList<NodeCandidatValue> getListCandidatsWaiting(HikariDataSource ds,
            String idThesaurus, String idLang) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        ArrayList <NodeCandidatValue> nodeCandidatLists = null;
        ArrayList tabIdConcept = new ArrayList();
        
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "select id_concept from concept_candidat where id_thesaurus = '" + idThesaurus +"'"
                            + " and status ='a' order by modified DESC";
                            
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    while(resultSet.next()) {
                        tabIdConcept.add(resultSet.getString("id_concept"));
                    }
                    nodeCandidatLists = new ArrayList<>();
                    for (Object tabIdConcept1 : tabIdConcept) {
                        NodeCandidatValue nodeCandidatValue;
                        nodeCandidatValue = getThisCandidat(ds, tabIdConcept1.toString(), idThesaurus, idLang);
                        if(nodeCandidatValue == null) return null;
                        nodeCandidatValue.setEtat("a");
                        nodeCandidatValue.setNbProp(getNbPropCandidat(ds,idThesaurus,tabIdConcept1.toString()));
                        nodeCandidatLists.add(nodeCandidatValue);
                    }

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting List Group or Domain of thesaurus : " + idThesaurus, sqle);
        }
        return nodeCandidatLists;
    }
    /**
     * Permet de retourner une ArrayList de NodeConceptCandidat par
     * thésaurus et par id_user c'est la liste des candidats en attente (status = a)
     * Si le Candidat n'est pas traduit dans la langue en cours, on récupère
     * l'identifiant pour l'afficher à la place
     * @param ds
     * @param idThesaurus
     * @param idLang
     * @param id_user
     * @return 
     */
    public ArrayList<NodeCandidatValue> getListMyCandidatsWait(HikariDataSource ds,
            String idThesaurus, String idLang, Integer id_user)
    {
        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        ArrayList <NodeCandidatValue> nodeCandidatLists = null;
        ArrayList tabIdConcept = new ArrayList();
        
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "select concept_candidat.id_concept from concept_candidat, proposition"
                            + " where concept_candidat.id_concept = proposition.id_concept and"
                            + " concept_candidat.id_thesaurus= proposition.id_thesaurus"
                            + " and proposition.id_user ="+id_user+" and proposition.id_thesaurus ='"+idThesaurus
                            + "' and concept_candidat.status='a'";
                            
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    while(resultSet.next()) {
                        tabIdConcept.add(resultSet.getString("id_concept"));
                    }
                    nodeCandidatLists = new ArrayList<>();
                    for (Object tabIdConcept1 : tabIdConcept) {
                        NodeCandidatValue nodeCandidatValue;
                        nodeCandidatValue = getThisCandidat(ds, tabIdConcept1.toString(), idThesaurus, idLang);
                        if(nodeCandidatValue == null) return null;
                        nodeCandidatValue.setEtat("a");
                        nodeCandidatValue.setNbProp(getNbPropCandidat(ds,idThesaurus,tabIdConcept1.toString()));
                        nodeCandidatLists.add(nodeCandidatValue);
                    }

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting List Group or Domain of thesaurus : " + idThesaurus, sqle);
        }
        return nodeCandidatLists;
        
    }
    
    /**
     * Permet de retourner une ArrayList de NodeConceptCandidat par
     * thésaurus, c'est la liste des candidats archivés
     * tous les status sauf a (a=attente, v=validé,i=insérré,r=refusé)
     * Si le Candidat n'est pas traduit dans la langue en cours, on récupère
     * l'identifiant pour l'afficher à la place
     *
     * @param ds le pool de connexion
     * @param idThesaurus
     * @param idLang
     * @return Objet Class ArrayList NodeCandidatValue
     */
    public ArrayList<NodeCandidatValue> getListCandidatsArchives(HikariDataSource ds,
            String idThesaurus, String idLang) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        ArrayList <NodeCandidatValue> nodeCandidatLists = null;
        ArrayList tabIdConcept = new ArrayList();
        ArrayList tabStatus = new ArrayList();
        NodeCandidatValue nodeCandidatValue = new NodeCandidatValue();
        
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "select id_concept, status from concept_candidat where id_thesaurus = '" + idThesaurus +"' and status != 'a' "
                            + " order by modified DESC";
                            
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    while(resultSet.next()) {
                        tabIdConcept.add(resultSet.getString("id_concept"));
                        tabStatus.add(resultSet.getString("status"));
                    }
                    nodeCandidatLists = new ArrayList<>();

                    int i = 0;
                    for (Object tabIdConcept1 : tabIdConcept) {
                        nodeCandidatValue = getThisCandidat(ds, tabIdConcept1.toString(), idThesaurus, idLang);
                        if(nodeCandidatValue == null) return null;
                        nodeCandidatValue.setEtat(tabStatus.get(i++).toString());
                        nodeCandidatValue.setNbProp(getNbPropCandidat(ds, idThesaurus, tabIdConcept1.toString()));
                        nodeCandidatLists.add(nodeCandidatValue);
                    }

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting List Group or Domain of thesaurus : " + idThesaurus, sqle);
        }
        return nodeCandidatLists;
    }
    
    /**
     * Permet de retourner une ArrayList de NodeConceptCandidat par
     * thésaurus, c'est la liste des candidats validé mais pas encore insérré dans les thésaurus (status = v)
     * Si le Candidat n'est pas traduit dans la langue en cours, on récupère
     * l'identifiant pour l'afficher à la place
     *
     * @param ds le pool de connexion
     * @param idThesaurus
     * @param idLang
     * @return Objet Class ArrayList NodeCandidatValue
     */
    public ArrayList<NodeCandidatValue> getListCandidatsValidated(HikariDataSource ds,
            String idThesaurus, String idLang) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        ArrayList <NodeCandidatValue> nodeCandidatLists = null;
        ArrayList tabIdConcept = new ArrayList();
        
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "select id_concept from concept_candidat where id_thesaurus = '" + idThesaurus +"' and status = 'v' "
                            + "order by modified DESC";
                            
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    while(resultSet.next()) {
                        tabIdConcept.add(resultSet.getString("id_concept"));
                    }
                    nodeCandidatLists = new ArrayList<>();
                    for (Object tabIdConcept1 : tabIdConcept) {
                        NodeCandidatValue nodeCandidatValue;
                        nodeCandidatValue = getThisCandidat(ds, tabIdConcept1.toString(), idThesaurus, idLang);
                        if(nodeCandidatValue == null) return null;
                        nodeCandidatValue.setEtat("v");
                        nodeCandidatValue.setNbProp(getNbPropCandidat(ds, idThesaurus, tabIdConcept1.toString()));
                        nodeCandidatLists.add(nodeCandidatValue);
                    }
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting List Group or Domain of thesaurus : " + idThesaurus, sqle);
        }
        return nodeCandidatLists;
    }    
    
    
    /**
     * $$$$$$$ deprecated $$$$$$$
     * Cette fonction permet de récupérer la liste des candidats
     * 
     * @param ds
     * @param idConcept
     * @param idThesaurus
     * @param idLang
     * @return Objet class NodeCandidatValue
     */
    public NodeCandidatValue getThisCandidatList(HikariDataSource ds,
            String idConcept, String idThesaurus, String idLang) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        NodeCandidatValue nodeCandidatList = null;
                
        if(isTraductionExistOfCandidat(ds, idConcept, idThesaurus, idLang)) {
            try {
            // Get connection from pool
            conn = ds.getConnection();
                try {
                    stmt = conn.createStatement();
                    try {
                        String query = "SELECT DISTINCT term_candidat.lexical_value,"
                                + " concept_candidat.status FROM"
                                + " term_candidat, concept_term_candidat, concept_candidat" 
                                + " WHERE concept_term_candidat.id_term = term_candidat.id_term"
                                + " and concept_term_candidat.id_concept = concept_candidat.id_concept"
                                + " and concept_term_candidat.id_concept ='" + idConcept +"'"
                                + " and term_candidat.lang = '" + idLang + "'"
                                + " and term_candidat.id_thesaurus = '" + idThesaurus + "'"
                                + " order by lexical_value DESC";

                        stmt.executeQuery(query);
                        resultSet = stmt.getResultSet();
                        if (resultSet != null) {
                            
                            while(resultSet.next()) {
                                nodeCandidatList = new NodeCandidatValue();
                                nodeCandidatList.setValue(resultSet.getString("lexical_value"));
                                nodeCandidatList.setIdConcept(idConcept);
                                nodeCandidatList.setEtat(resultSet.getString("status"));
                                nodeCandidatList.setNbProp(getNbPropCandidat(ds,idThesaurus,idConcept));
                            }
                        }

                    } finally {
                        stmt.close();
                    }
                } finally {
                    conn.close();
                }
            } catch (SQLException sqle) {
                // Log exception
                log.error("Error while getting Concept : " + idConcept, sqle);
            }
        }
        else {
            try {
            // Get connection from pool
                conn = ds.getConnection();
                try {
                    stmt = conn.createStatement();
                    try {
                        String query = "SELECT concept_candidat.id_concept,"
                                + " concept_candidat.status FROM"
                                + " concept_candidat" 
                                + " WHERE concept_candidat.id_concept ='" + idConcept +"'"
                                + " and concept_candidat.id_thesaurus = '" + idThesaurus + "'";

                        stmt.executeQuery(query);
                        resultSet = stmt.getResultSet();
                        if (resultSet != null) {
                            while(resultSet.next()) {
                                nodeCandidatList = new NodeCandidatValue();
                                nodeCandidatList.setValue("");
                                nodeCandidatList.setIdConcept(idConcept);
                                nodeCandidatList.setEtat(resultSet.getString("status"));
                                nodeCandidatList.setNbProp(getNbPropCandidat(ds,idThesaurus,idConcept));
                            }
                        }

                    } finally {
                        stmt.close();
                    }
                } finally {
                    conn.close();
                }
            } catch (SQLException sqle) {
                // Log exception
                log.error("Error while getting Concept : " + idConcept, sqle);
            }
            
        }
        return nodeCandidatList;
    }
    
/**
     * Cette fonction permet de récupérer un candidat 
     * avec sa traduction, sinon, son identifiant
     * 
     * @param ds
     * @param idCandidat
     * @param idThesaurus
     * @param idLang
     * @return Objet class NodeCandidatValue
     */
    public NodeCandidatValue getThisCandidat(HikariDataSource ds,
            String idCandidat, String idThesaurus, String idLang) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        NodeCandidatValue nodeCandidatList = null;
                
//        if(isTraductionExistOfCandidat(ds, idConcept, idThesaurus, idLang)) {
            try {
            // Get connection from pool
            conn = ds.getConnection();
                try {
                    stmt = conn.createStatement();
                    try {
                        String query = "SELECT term_candidat.lexical_value" +
                            " FROM concept_term_candidat, term_candidat" +
                            " WHERE concept_term_candidat.id_term = term_candidat.id_term" +
                            " AND concept_term_candidat.id_concept = '" + idCandidat + "'" +
                            " AND term_candidat.lang = '" + idLang + "'" + 
                            " AND term_candidat.id_thesaurus = '" + idThesaurus + "'";
                                
                        stmt.executeQuery(query);
                        resultSet = stmt.getResultSet();
                            
                        if(resultSet.next()) {
                            nodeCandidatList = new NodeCandidatValue();
                            nodeCandidatList.setValue(resultSet.getString("lexical_value").trim());
                            nodeCandidatList.setIdConcept(idCandidat);
                        }
                        else {
                            nodeCandidatList = new NodeCandidatValue();
                            nodeCandidatList.setValue("");
                            nodeCandidatList.setIdConcept(idCandidat);
                        }
                            

                    } finally {
                        stmt.close();
                    }
                } finally {
                    conn.close();
                }
            } catch (SQLException sqle) {
                // Log exception
                log.error("Error while getting Concept : " + idCandidat, sqle);
            }
/*        }
        else {
            try {
            // Get connection from pool
                conn = ds.getConnection();
                try {
                    stmt = conn.createStatement();
                    try {
                        String query = "SELECT concept_candidat.id_concept,"
                                + " concept_candidat.status FROM"
                                + " concept_candidat" 
                                + " WHERE concept_candidat.id_concept ='" + idConcept +"'"
                                + " and concept_candidat.id_thesaurus = '" + idThesaurus + "'";

                        stmt.executeQuery(query);
                        resultSet = stmt.getResultSet();
                        if (resultSet != null) {
                            while(resultSet.next()) {
                                nodeCandidatList = new NodeCandidatValue();
                                nodeCandidatList.setValue("");
                                nodeCandidatList.setIdConcept(idConcept);
                                nodeCandidatList.setEtat(resultSet.getString("status"));
                                nodeCandidatList.setNbProp(getNbPropCandidat(ds,idThesaurus,idConcept));
                            }
                        }

                    } finally {
                        stmt.close();
                    }
                } finally {
                    conn.close();
                }
            } catch (SQLException sqle) {
                // Log exception
                log.error("Error while getting Concept : " + idConcept, sqle);
            }
            
        }*/
        return nodeCandidatList;
    }    
    
   
    /**
     * Cette fonction permet de retourner le nombre de candidats d'un concept
     *
     * @param ds
     * @param idConcept
     * @param idThesaurus
     * @return Objet class NodeConceptTree
     */
    public int getNbPropCandidat(HikariDataSource ds,
            String idThesaurus, String idConcept) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        int count = 0;
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "select count(id_concept) from proposition where"
                            + " id_concept = '" + idConcept + "'"
                            + " AND id_thesaurus = '" + idThesaurus + "'";

                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    if (resultSet != null) {
                        resultSet.next();
                        if (resultSet.getInt(1) != 0) {
                            count = resultSet.getInt(1);
                        }
                    }

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting count of candidat of Concept : " + idConcept, sqle);
        }
        return count;
    }

    /**
     * Cette fonction permet de savoir si le terme existe ou non
     * @param ds
     * @param idConcept
     * @param idThesaurus
     * @param idLang
     * @return Objet class NodeConceptTree
     */

    public boolean isTraductionExistOfCandidat(HikariDataSource ds,
            String idConcept, String idThesaurus, String idLang) {

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
                    String query = "select term_candidat.id_term from term_candidat, concept_term_candidat"
                            + " where term_candidat.id_term = concept_term_candidat.id_term and"
                            + " concept_term_candidat.id_concept = '" + idConcept +"'"
                            + " and term_candidat.lang = '" + idLang + "'"
                            + " and term_candidat.id_thesaurus = '" + idThesaurus + "'";

                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    if (resultSet != null) {
                        resultSet.next();
                        if (resultSet.getRow() == 0) {
                            existe = false;
                        } else {
                            existe = true;
                        }
                    }

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while asking if Traduction of Candidat exist : " + idConcept, sqle);
        }
        return existe;
    }

    /**
     * Cette fonction permet de savoir si le Candidat existe ou non
     * @param conn
     * @param title
     * @param idThesaurus
     * @param idLang
     * @return boolean
     */
    public boolean isCandidatExist_rollBack(Connection conn,
            String title, String idThesaurus, String idLang) {

        Statement stmt;
        ResultSet resultSet;
        boolean existe = false; 
        StringPlus stringPlus = new StringPlus();
        title = stringPlus.addQuotes(title);
        try {
            try {
                stmt = conn.createStatement();
                try {
                    String query = "select id_term from term_candidat where "
                            + "unaccent_string(lexical_value) ilike "
                            + "unaccent_string('" + title
                            + "')  and lang = '" + idLang
                            + "' and id_thesaurus = '" + idThesaurus
                            + "'";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    if (resultSet != null) {
                        resultSet.next();
                        if (resultSet.getRow() == 0) {
                            existe = false;
                        } else {
                            existe = true;
                        }
                    }

                } finally {
                    stmt.close();
                }
            } finally {
            //    conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while asking if Title of Candidat exist : " + title, sqle);
        }
        return existe;
    }
    
    
   /**
     * Cette fonction permet de savoir si le Candidat existe ou non
     * @param ds
     * @param idCandidat
     * @param idThesaurus
     * @param idUser
     * @return boolean
     */

    public boolean setStatusCandidatToInserted(HikariDataSource ds,
            String idCandidat, String idThesaurus, int idUser) {

        Statement stmt;

        try {
            try {
                Connection conn = ds.getConnection();
                stmt = conn.createStatement();
                try {
                    String query = "Update concept_candidat set"
                            + " status = 'i'"
                            + " where id_concept = '" + idCandidat + "'"
                            + " and id_thesaurus = '" + idThesaurus + "'";
                    
                    stmt.executeUpdate(query);
                    updateDateOfCandidat(conn, idCandidat, idThesaurus);
                    return true;

                } finally {
                    stmt.close();
                }
            } finally {
            //    conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while asking if Title of Candidat exist : " + sqle);
        }
        return false;
    }    
   
    
        /**
     * Cette fonction permet de savoir si le Candidat existe ou non
     * @param ds
     * @param title
     * @param idThesaurus
     * @param idLang
     * @return boolean
     */
    public boolean isCandidatExist(HikariDataSource ds,
            String title, String idThesaurus, String idLang) {

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
                    String query = "select id_term from term_candidat where "
                            + "unaccent_string(lexical_value) ilike "
                            + "unaccent_string('" + title
                            + "')  and lang = '" + idLang
                            + "' and id_thesaurus = '" + idThesaurus
                            + "'";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    if(resultSet.next()) {
                        existe = resultSet.getRow() != 0;
                    }

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while asking if Title of Candidat exist : " + title, sqle);
        }
        return existe;
    }
    
    /**
     * Cette fonction permet de savoir si l'id du Candidat existe, si oui, on l'incrémente
     * @param conn
     * @param idCandidat
     * @param idThesaurus
     * @return boolean
     */
    public boolean isCandidatExist(Connection conn,
            String idCandidat, String idThesaurus) {

        Statement stmt;
        ResultSet resultSet;
        boolean existe = false; 

        try {

            try {
                stmt = conn.createStatement();
                try {
                    String query = "select id_concept from concept_candidat where "
                            + "id_concept = '" + idCandidat + "'"
                            + " and id_thesaurus = '" + idThesaurus + "'";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    if(resultSet.next()) {
                        existe = resultSet.getRow() != 0;
                    }

                } finally {
                    stmt.close();
                }
            } finally {

            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while asking if id of Candidat exist : " + idCandidat, sqle);
        }
        return existe;
    }    
    
    
}
