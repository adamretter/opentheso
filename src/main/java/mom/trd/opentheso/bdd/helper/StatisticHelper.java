package mom.trd.opentheso.bdd.helper;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import mom.trd.opentheso.bdd.helper.nodes.statistic.NodeStatConcept;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class StatisticHelper {
    
    private final Log log = LogFactory.getLog(ThesaurusHelper.class);
    private int nombreConcept = 0;
    
    public StatisticHelper() {
        
    }

    public int getNombreConcept() {
        return nombreConcept;
    }

    public void setNombreConcept(int nombreConcept) {
        this.nombreConcept = nombreConcept;
    }
    
    
    
    /**
     * Fonction recursive qui permet de retrouver le nombre de concepts dans la branche + le concept lui même
     * @param ds
     * @param idConcept
     * @param idTheso
     * @return 
     */
    public int getConceptCountOfBranch(HikariDataSource ds, String idConcept,
            String idTheso) {
        ConceptHelper conceptHelper = new ConceptHelper();

        ArrayList <String> listIdsOfConceptChildren = conceptHelper.getListChildrenOfConcept(ds,
                        idConcept, idTheso);

        
        int compteur = getChildrenCountOfConcept(ds, idConcept, idTheso);
        if(compteur != -1) {
            nombreConcept = nombreConcept + compteur;
        }

        for (String listIdsOfConceptChildren1 : listIdsOfConceptChildren) {
       //     if(!conceptHelper.deleteConceptForced(ds, listIdsOfConceptChildren1, idTheso, idUser))
        //        return false;
            getConceptCountOfBranch(ds, listIdsOfConceptChildren1, idTheso);
        }
        return nombreConcept + 1;
    }   
    
    
    /**
     * Cette fonction permet de récupérer le nombre des concepts suivant l'id du
     * Concept-Père et le thésaurus
     *
     * @param ds
     * @param idConcept
     * @param idThesaurus
     * @return Objet Array String
     */
    public int getChildrenCountOfConcept(HikariDataSource ds,
            String idConcept, String idThesaurus) {

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
                    String query = "select count(id_concept2) from hierarchical_relationship"
                            + " where id_thesaurus = '" + idThesaurus + "'"
                            + " and id_concept1 = '" + idConcept + "'"
                            + " and role = '" + "NT" + "'";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    if(resultSet.next()) {
                        count = resultSet.getInt(1);
                    }
                    return count;
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting count of childs for Concept : " + idConcept, sqle);
        }
        return -1;
    }    
    
    public int getNbCpt(HikariDataSource ds, String idThesaurus) {
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
                    String query = "SELECT count(id_concept) FROM concept WHERE"
                            + " id_thesaurus = '" + idThesaurus + "'";

                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    if (resultSet != null) {
                        resultSet.next();
                        count = resultSet.getInt(1);
                    }

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting count of concept of thesaurus : " + idThesaurus, sqle);
        }
        return count;
    }
    
    public int getNbDescOfGroup(HikariDataSource ds, String idThesaurus, String idGroup) {
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
                    String query = "select count(id_concept) from concept left join concept_group_concept" +
                        " on id_concept = idconcept and id_thesaurus = idthesaurus" +
                        " where id_thesaurus = '" +idThesaurus + "'" +
                        " and idgroup = '" + idGroup + "'";
                    
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    if (resultSet != null) {
                        resultSet.next();
                        count = resultSet.getInt(1);
                    }

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting count of decriptor of group : " + idGroup, sqle);
        }
        return count;
    }
    
    public int getNbNonDescOfGroup(HikariDataSource ds, String idThesaurus, String idGroup, String langue) {
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
                   //modification query SQL #JM
                   String query="SELECT count(non_preferred_term.id_term) FROM non_preferred_term "
                            + "INNER JOIN (SELECT DISTINCT preferred_term.id_concept,"
                            + "preferred_term.id_term FROM preferred_term "
                            + "WHERE preferred_term.id_concept IN "
                            + "(SELECT DISTINCT idconcept FROM concept_group_concept "
                            + "WHERE idgroup ='"+idGroup+"'  AND idthesaurus = '"+idThesaurus+"')) "
                            + "as Tabl ON Tabl.id_term=non_preferred_term.id_term "
                            + "WHERE non_preferred_term.lang='"+langue+"'";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    if (resultSet != null) {
                        resultSet.next();
                        count = resultSet.getInt(1);
                    }

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting count of non-decriptor of group : " + idGroup, sqle);
        }
        return count;
    }
    
    public int getNbTradOfGroup(HikariDataSource ds, String idThesaurus, String idGroup, String langue) {
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
                    //modification de la requête SQL #jm
                    String query="SELECT count(distinct term.id_term) FROM term INNER JOIN "
                            + "(SELECT preferred_term.id_concept,"
                            + "preferred_term.id_term FROM preferred_term "
                            + "WHERE preferred_term.id_concept IN "
                            + "(SELECT idconcept FROM concept_group_concept "
                            + "WHERE idgroup='"+idGroup+"' AND idthesaurus='"+idThesaurus+"'))"
                            + " as Tabl ON Tabl.id_term=term.id_term WHERE term.lang='"+langue+"' AND id_thesaurus='"+idThesaurus+"'";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    if (resultSet != null) {
                        resultSet.next();
                        count = resultSet.getInt(1);
                    }

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting count of traduction of group : " + idGroup, sqle);
        }
        return count;
    }
    
    public int getNbDefinitionNoteOfGroup(HikariDataSource ds, String idThesaurus, String langue, String idGroup) {
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
                    String query = "SELECT count(DISTINCT note.id) FROM concept, note WHERE"
                            + " concept.id_concept = note.id_concept"
                            + " and concept.id_thesaurus = '" + idThesaurus + "'"
                            + " and concept.id_concept IN (SELECT idconcept FROM concept_group_concept WHERE idgroup = '"+ idGroup + "')"
                            + " and note.lang = '" + langue + "'";

                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    if (resultSet != null) {
                        resultSet.next();
                        count = resultSet.getInt(1);
                    }

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting count of definition note of group : " + idGroup, sqle);
        }
        return count;
    }
    
    /**
     * permet de retourner les stats d'un thésaurus pour les concepts modifiés 
     * pas de définition de dates mais une limite de nombre de résultat
     * @param ds
     * @param idThesaurus
     * @param langue
     * @param limit
     * @return 
     * #MR
     */
    public ArrayList<NodeStatConcept> getStatConcept(HikariDataSource ds,
            String idThesaurus, String langue, int limit) {
        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        ArrayList<NodeStatConcept> list = new ArrayList<>();

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT term.lexical_value," +
                        " concept_group_concept.idgroup," +
                        " concept_group_concept.idconcept, term.created, term.modified" +
                        " FROM concept_group_concept," +
                        " preferred_term, term WHERE" +
                        " preferred_term.id_thesaurus = term.id_thesaurus AND" +
                        " preferred_term.id_term = term.id_term AND" +
                        " preferred_term.id_thesaurus = concept_group_concept.idthesaurus AND" +
                        " preferred_term.id_concept = concept_group_concept.idconcept AND" +
                        " term.id_thesaurus = '" + idThesaurus + "' AND" +
                        " term.lang = '" + langue + "'" +
                        " order by term.created DESC" +
                        " LIMIT "+limit;
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    while (resultSet.next()) {
                        NodeStatConcept nsc = new NodeStatConcept();
                        nsc.setDateCreat(resultSet.getDate("created"));
                        nsc.setDateEdit(resultSet.getDate("modified"));
                        String temp = new GroupHelper().getThisConceptGroup(ds, resultSet.getString("idgroup"), idThesaurus, langue).getLexicalValue();
                        nsc.setGroup(temp + "(" + resultSet.getString("idgroup") + ")");
                        nsc.setIdConcept(resultSet.getString("idconcept"));
                        nsc.setValue(resultSet.getString("lexical_value"));
                        list.add(nsc);
                    }
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting List statistic of Concept in thesaurus : " + idThesaurus, sqle);
        }
        return list;
    }
    
    public ArrayList<NodeStatConcept> getStatConceptCreat(HikariDataSource ds, String begin, String end, String idThesaurus, String langue,int limit) {
        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        ArrayList<NodeStatConcept> list = new ArrayList<>();

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT term.lexical_value," +
                        " concept_group_concept.idgroup," +
                        " concept_group_concept.idconcept, term.created, term.modified" +
                        " FROM concept_group_concept," +
                        " preferred_term, term WHERE" +
                        " preferred_term.id_thesaurus = term.id_thesaurus AND" +
                        " preferred_term.id_term = term.id_term AND" +
                        " preferred_term.id_thesaurus = concept_group_concept.idthesaurus AND" +
                        " preferred_term.id_concept = concept_group_concept.idconcept AND" +
                        " term.id_thesaurus = '" + idThesaurus + "' AND" +
                        " term.created <= '" + end + "'" +
                        " AND term.created >= '" + begin + "'" +
                        " AND term.lang = '" + langue + "'" +
                        " order by term.created DESC" +
                        " LIMIT "+limit;

                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    while (resultSet.next()) {
                        NodeStatConcept nsc = new NodeStatConcept();
                        nsc.setDateCreat(resultSet.getDate("created"));
                        nsc.setDateEdit(resultSet.getDate("modified"));
                        String temp = new GroupHelper().getThisConceptGroup(ds, resultSet.getString("idgroup"), idThesaurus, langue).getLexicalValue();
                        nsc.setGroup(temp + "(" + resultSet.getString("idgroup") + ")");
                        nsc.setIdConcept(resultSet.getString("idconcept"));
                        nsc.setValue(resultSet.getString("lexical_value"));
                        list.add(nsc);
                    }
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting List statistic of Concept in thesaurus : " + idThesaurus, sqle);
        }
        return list;
    }
    
    public ArrayList<NodeStatConcept> getStatConceptEdit(HikariDataSource ds, String begin, String end, String idThesaurus, String langue,int limit) {
        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        ArrayList<NodeStatConcept> list = new ArrayList<>();

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT term.lexical_value," +
                        " concept_group_concept.idgroup," +
                        " concept_group_concept.idconcept, term.created, term.modified" +
                        " FROM concept_group_concept," +
                        " preferred_term, term WHERE" +
                        " preferred_term.id_thesaurus = term.id_thesaurus AND" +
                        " preferred_term.id_term = term.id_term AND" +
                        " preferred_term.id_thesaurus = concept_group_concept.idthesaurus AND" +
                        " preferred_term.id_concept = concept_group_concept.idconcept AND" +
                        " term.id_thesaurus = '" + idThesaurus + "' AND" +
                        " term.modified <= '" + end + "'" +
                        " AND term.modified >= '" + begin + "'" +
                        " AND term.lang = '" + langue + "'" +
                        " order by term.created DESC" +
                        " LIMIT "+limit;
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    while (resultSet.next()) {
                        NodeStatConcept nsc = new NodeStatConcept();
                        nsc.setDateCreat(resultSet.getDate("created"));
                        nsc.setDateEdit(resultSet.getDate("modified"));
                        String temp = new GroupHelper().getThisConceptGroup(ds, resultSet.getString("idgroup"), idThesaurus, langue).getLexicalValue();
                        nsc.setGroup(temp + "(" + resultSet.getString("idgroup") + ")");
                        nsc.setIdConcept(resultSet.getString("idconcept"));
                        nsc.setValue(resultSet.getString("lexical_value"));
                        list.add(nsc);
                    }
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting List statistic of Concept in thesaurus : " + idThesaurus, sqle);
        }
        return list;
    }
    /**
     * #JM
     * méthode pour récupérer les concepts selon un groupe et des dates 
     * la colonne modified ou created et intérrogée selon le paramètre column
     * @param ds
     * @param begin
     * @param end
     * @param column
     * @param idThesaurus
     * @param langue
     * @param selectedGroup
     * @param limit
     * @return 
     */
    public ArrayList<NodeStatConcept> getStatConceptByGroupAndDate(HikariDataSource ds, String begin, String end, String column, String idThesaurus, String langue, String selectedGroup,int limit) {
        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        ArrayList<NodeStatConcept> list = new ArrayList<>();

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT term.lexical_value," +
                        " concept_group_concept.idgroup," +
                        " concept_group_concept.idconcept, term.created, term.modified" +
                        " FROM concept_group_concept," +
                        " preferred_term, term WHERE" +
                        " preferred_term.id_thesaurus = term.id_thesaurus AND" +
                        " preferred_term.id_term = term.id_term AND" +
                        " preferred_term.id_thesaurus = concept_group_concept.idthesaurus AND" +
                        " preferred_term.id_concept = concept_group_concept.idconcept AND" +
                        " term.id_thesaurus = '" + idThesaurus + "' AND" +
                        " idgroup = '" + selectedGroup + "' AND" +
                        " term.created <= '" + end + "'" +
                        " AND term.created >= '" + begin + "'" +
                        " AND term.lang = '" + langue + "'" +
                        " order by term.created DESC" +
                        " LIMIT "+limit;                    
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                     while (resultSet.next()) {
                        NodeStatConcept nsc = new NodeStatConcept();
                        nsc.setDateCreat(resultSet.getDate("created"));
                        nsc.setDateEdit(resultSet.getDate("modified"));
                        String temp = new GroupHelper().getThisConceptGroup(ds, resultSet.getString("idgroup"), idThesaurus, langue).getLexicalValue();
                        nsc.setGroup(temp + "(" + resultSet.getString("idgroup") + ")");
                        nsc.setIdConcept(resultSet.getString("idconcept"));
                        nsc.setValue(resultSet.getString("lexical_value"));
                        list.add(nsc);
                    }
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting List statistic of Concept in thesaurus : " + idThesaurus, sqle);
        }
        return list;
    }

   
}
