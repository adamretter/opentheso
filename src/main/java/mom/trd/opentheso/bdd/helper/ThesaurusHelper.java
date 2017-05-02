/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.bdd.helper;

import com.zaxxer.hikari.HikariDataSource;
import fr.mom.arkeo.soap.DcElement;
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
import mom.trd.opentheso.bdd.datas.Languages_iso639;
import mom.trd.opentheso.bdd.datas.Thesaurus;
import mom.trd.opentheso.bdd.helper.nodes.NodeLang;
import mom.trd.opentheso.bdd.helper.nodes.thesaurus.NodeThesaurus;
import mom.trd.opentheso.bdd.tools.FileUtilities;
import mom.trd.opentheso.bdd.tools.StringPlus;
import mom.trd.opentheso.ws.ark.Ark_Client;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author miled.rousset
 */
public class ThesaurusHelper {

    private final Log log = LogFactory.getLog(ThesaurusHelper.class);
    
    private String identifierType = "1";

    public String getIdentifierType() {
        return identifierType;
    }

    public void setIdentifierType(String identifierType) {
        this.identifierType = identifierType;
    }



    /**
     * Permet de créer un nouveau Thésaurus. Retourne l'identifiant du thésaurus
     * ou null
     *
     * @param ds le pool de connexion
     * @param thesaurus
     * @param urlSite
     * @param isArkActive
     * @return String Id du thésaurus rajouté
     */
    public String addThesaurus(HikariDataSource ds, Thesaurus thesaurus,
            String urlSite, boolean isArkActive) {

        String idThesaurus = null;//"TH";//"ark:/66666/srvq9a5Ll41sk";
        Connection conn;
        Statement stmt;
        ResultSet resultSet;

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "select max(id) from thesaurus";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    resultSet.next();
                    int idNumeriqueThesaurus = resultSet.getInt(1);
                    idThesaurus = "" + ++idNumeriqueThesaurus;

                    /**
                     * récupération du code Ark via WebServices
                     *
                     */
                    String idArk = "";

                    if (isArkActive) {
                        ArrayList<DcElement> dcElementsList = new ArrayList<>();
                        Ark_Client ark_Client = new Ark_Client();
                        idArk = ark_Client.getArkId(
                                new FileUtilities().getDate(),
                                urlSite + "?idt=" + idThesaurus,
                                "", "", dcElementsList, "pcrt"); // pcrt : p= pactols, crt=code DCMI pour collection
                    }

                    query = "Insert into thesaurus (id_thesaurus,"
                            + " id_ark, created, modified)"
                            + " values ("
                            + "'" + idThesaurus + "'"
                            + ",'" + idArk + "'"
                            + "," + "current_date,"
                            + "current_date)";

                    stmt.executeUpdate(query);
                    thesaurus.setId_thesaurus(idThesaurus);
                 /*   if(thesaurus.getTitle().isEmpty()){
                        thesaurus.setTitle("Theso_" + idThesaurus);
                    }
                    addThesaurusTraduction(ds, thesaurus);*/

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while adding Thesaurus : " + idThesaurus, sqle);
            idThesaurus = null;
        }
        return idThesaurus;
    }
    
    /**
     * Permet de créer un nouveau Thésaurus. Retourne l'identifiant du thésaurus
     * ou null
     *
     * @param conn
     * @param urlSite
     * @param isArkActive
     * @return String Id du thésaurus rajouté
     */
    public String addThesaurusRollBack(Connection conn,
            String urlSite, boolean isArkActive) {

        String idThesaurus = null;
        String idArk = "";
        Statement stmt;
        ResultSet resultSet;

        try {

            try {
                stmt = conn.createStatement();
                try {
                    String query;
                    if(identifierType.equalsIgnoreCase("1")) { // identifiants types alphanumérique
                        ToolsHelper toolsHelper = new ToolsHelper();
                        idThesaurus = toolsHelper.getNewId(10);
                        while (isThesaurusExiste(conn, idThesaurus)) {
                            idThesaurus = toolsHelper.getNewId(10);
                        }
                    }
                    else {
                        query = "select max(id) from thesaurus";
                        stmt.executeQuery(query);
                        resultSet = stmt.getResultSet();
                        resultSet.next();
                        int idNumeriqueThesaurus = resultSet.getInt(1);
                        idThesaurus = "" + ++idNumeriqueThesaurus;
                        while (isThesaurusExiste(conn, idThesaurus)) {
                            idThesaurus = "" + ++idNumeriqueThesaurus;
                        }
                    }
                    /**
                     * récupération du code Ark via WebServices
                     *
                     */
                    if (isArkActive) {
                        ArrayList<DcElement> dcElementsList = new ArrayList<>();
                        Ark_Client ark_Client = new Ark_Client();
                        idArk = ark_Client.getArkId(
                                new FileUtilities().getDate(),
                                urlSite + "?idt=" + idThesaurus,
                                "", "", dcElementsList, "pcrt"); // pcrt : p= pactols, crt=code DCMI pour collection
                    }

                    query = "Insert into thesaurus (id_thesaurus,"
                            + " id_ark, created, modified)"
                            + " values ("
                            + "'" + idThesaurus + "'"
                            + ",'" + idArk + "'"
                            + "," + "current_date,"
                            + "current_date)";

                    stmt.executeUpdate(query);
                 //   thesaurus.setId_thesaurus(idThesaurus);
                   /* if(thesaurus.getTitle().isEmpty()) {
                        thesaurus.setTitle("theso_" + idThesaurus);
                    }
                    if(!addThesaurusTraductionRollBack(conn, thesaurus)) {
                        stmt.close();
                        return null;
                    }*/
                       

                } finally {
                    stmt.close();
                }
            } finally {
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while adding Thesaurus : " + idThesaurus, sqle);
            idThesaurus = null;
        }
        return idThesaurus;
    }
    
    /**
     * Permet de rajouter une traduction à un Thésaurus existant suivant un l'id
     * du thésaurus et la langue retourne yes or No si l'opération a réussie ou
     * non
     *
     * @param conn
     * @param thesaurus la classe Thesaurus
     * @return boolean
     */
    public boolean addThesaurusTraductionRollBack(Connection conn,
            Thesaurus thesaurus) {

        Statement stmt;
        boolean status = false;
        thesaurus = addQuotes(thesaurus);

        try {
            try {
                stmt = conn.createStatement();
                try {
                    String query = "Insert into thesaurus_label ("
                            + " id_thesaurus,"
                            + " contributor, coverage,"
                            + " creator, created, modified, description,"
                            + " format,lang, publisher, relation,"
                            + " rights, source, subject, title,"
                            + " type)"
                            + " values ("
                            + "'" + thesaurus.getId_thesaurus() + "'"
                            + ",'" + thesaurus.getContributor() + "'"
                            + ",'" + thesaurus.getCoverage() + "'"
                            + ",'" + thesaurus.getCreator() + "'"
                            + ",current_date"
                            + ",current_date"
                            + ",'" + thesaurus.getDescription() + "'"
                            + ",'" + thesaurus.getFormat() + "'"
                            + ",'" + thesaurus.getLanguage().trim() + "'"
                            + ",'" + thesaurus.getPublisher() + "'"
                            + ",'" + thesaurus.getRelation() + "'"
                            + ",'" + thesaurus.getRights() + "'"
                            + ",'" + thesaurus.getSource() + "'"
                            + ",'" + thesaurus.getSubject() + "'"
                            + ",'" + thesaurus.getTitle() + "'"
                            + ",'" + thesaurus.getType() + "')";

                    stmt.executeUpdate(query);
                    status = true;

                } finally {
                    stmt.close();
                }
            } finally {
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while adding Traduction Thesaurus : " + thesaurus.getTitle(), sqle);
        }
        return status;
    }    

    /**
     * Cette focntion permet de nettoyer un thésaurus 
     * @param conn
     * @param idTheso
     * @return 
     */
    public boolean reorganizingTheso(Connection conn, String idTheso) {
        Statement stmt;
        boolean status = false;

        try {
            try {
                stmt = conn.createStatement();
                try {
                    String query = "delete from term where id_term = ''"
                            + " and id_thesaurus = '" + idTheso + "'";
                    stmt.executeUpdate(query);

                    query = "delete from concept_group_label where idgroup = ''"
                            + " and idthesaurus = '" + idTheso + "'";
                    stmt.executeUpdate(query);
                    
                    query = "UPDATE concept_group SET notation = '' WHERE notation ilike 'null'";
                    stmt.executeUpdate(query);
                    
                    query = "UPDATE concept_group SET idtypecode = 'MT' WHERE idtypecode ilike 'null'";
                    stmt.executeUpdate(query);
                    
                    query = "UPDATE concept SET notation = '' WHERE notation ilike 'null'";
                    stmt.executeUpdate(query);
                    
                    status = true;

                } finally {
                    stmt.close();
                }
            } finally {
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while reorganizing theso : " + idTheso, sqle);
        }
        return status;        
    }
    
    /**
     * Permet de rajouter une traduction à un Thésaurus existant suivant un l'id
     * du thésaurus et la langue retourne yes or No si l'opération a réussie ou
     * non
     *
     * @param ds le pool de connexion
     * @param thesaurus la classe Thesaurus
     * @return boolean
     */
    public boolean addThesaurusTraduction(HikariDataSource ds,
            Thesaurus thesaurus) {

        Connection conn;
        Statement stmt;
        boolean status = false;
        thesaurus = addQuotes(thesaurus);

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "Insert into thesaurus_label ("
                            + " id_thesaurus,"
                            + " contributor, coverage,"
                            + " creator, created, modified, description,"
                            + " format,lang, publisher, relation,"
                            + " rights, source, subject, title,"
                            + " type)"
                            + " values ("
                            + "'" + thesaurus.getId_thesaurus() + "'"
                            + ",'" + thesaurus.getContributor() + "'"
                            + ",'" + thesaurus.getCoverage() + "'"
                            + ",'" + thesaurus.getCreator() + "'"
                            + ",current_date"
                            + ",current_date"
                            + ",'" + thesaurus.getDescription() + "'"
                            + ",'" + thesaurus.getFormat() + "'"
                            + ",'" + thesaurus.getLanguage().trim() + "'"
                            + ",'" + thesaurus.getPublisher() + "'"
                            + ",'" + thesaurus.getRelation() + "'"
                            + ",'" + thesaurus.getRights() + "'"
                            + ",'" + thesaurus.getSource() + "'"
                            + ",'" + thesaurus.getSubject() + "'"
                            + ",'" + thesaurus.getTitle() + "'"
                            + ",'" + thesaurus.getType() + "')";

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
            log.error("Error while adding Traduction Thesaurus : " + thesaurus.getTitle(), sqle);
        }
        return status;
    }

    /**
     * Permet de retourner un thésaurus par identifiant et par langue / ou null
     * si rien cette fonction ne retourne pas les détails et les traductions
     *
     * @param ds le pool de connexion
     * @param idThesaurus
     * @param idLang
     * @return Objet Class Thesaurus
     */
    public Thesaurus getThisThesaurus(HikariDataSource ds, String idThesaurus, String idLang) {
        idLang = idLang.trim();
        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        Thesaurus thesaurus = null;
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "select * from thesaurus_label where id_thesaurus = '"
                            + idThesaurus + "' and lang = '"
                            + idLang + "'";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    if (resultSet.next()) {
                        if (resultSet.getString("lang") != null) {
                            thesaurus = new Thesaurus();
                            thesaurus.setId_thesaurus(idThesaurus);
                            thesaurus.setContributor(resultSet.getString("contributor"));
                            thesaurus.setCoverage(resultSet.getString("coverage"));
                            thesaurus.setCreator(resultSet.getString("creator"));
                            thesaurus.setCreated(resultSet.getDate("created"));
                            thesaurus.setModified(resultSet.getDate("modified"));
                            thesaurus.setDescription(resultSet.getString("description"));
                            thesaurus.setFormat(resultSet.getString("format"));
                            thesaurus.setLanguage(resultSet.getString("lang"));
                            thesaurus.setPublisher(resultSet.getString("publisher"));
                            thesaurus.setRelation(resultSet.getString("relation"));
                            thesaurus.setRights(resultSet.getString("rights"));
                            thesaurus.setSource(resultSet.getString("source"));
                            thesaurus.setSubject(resultSet.getString("subject"));
                            thesaurus.setTitle(resultSet.getString("title"));
                            thesaurus.setType(resultSet.getString("type"));
                        }
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
            log.error("Error while getting This Thesaurus : " + idThesaurus, sqle);
        }
        return thesaurus;
    }

    /**
     * Permet de retourner un thésaurus par identifiant sous forme de
     * NodeThesaurus avec les traductions
     *
     * @param ds le pool de connexion
     * @param idThesaurus
     * @return Objet Class Thesaurus
     */
    public NodeThesaurus getNodeThesaurus(HikariDataSource ds, String idThesaurus) {

        ArrayList<Languages_iso639> listLangTheso = getLanguagesOfThesaurus(ds, idThesaurus);

        NodeThesaurus nodeThesaurus = new NodeThesaurus();

        ArrayList<Thesaurus> thesaurusTraductionsList = new ArrayList<>();

        for (int i = 0; i < listLangTheso.size(); i++) {
            Thesaurus thesaurus = getThisThesaurus(ds, idThesaurus, listLangTheso.get(i).getId_iso639_1());
            if (thesaurus != null) {
                thesaurusTraductionsList.add(thesaurus);
            }
        }
        nodeThesaurus.setIdThesaurus(idThesaurus);
        nodeThesaurus.setListThesaurusTraduction(thesaurusTraductionsList);
        return nodeThesaurus;
    }

    /**
     * Retourne la liste des langues sous forme de MAP (nom + id) si le
     * thesaurus n'existe pas dans la langue demandée, on récupère seulement son
     * id
     *
     * @param ds
     * @param idLang
     * @return
     */
    public Map getListThesaurus(HikariDataSource ds, String idLang) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        Map map = new HashMap();
        ArrayList tabIdThesaurus = new ArrayList();

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "select DISTINCT id_thesaurus from thesaurus";

                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    if (resultSet != null) {
                        while (resultSet.next()) {
                            tabIdThesaurus.add(resultSet.getString("id_thesaurus"));
                        }
                        for (Object tabIdThesauru : tabIdThesaurus) {
                            query = "select title from thesaurus_label where"
                                    + " id_thesaurus = '" + tabIdThesauru + "'" + " and lang = '" + idLang + "'";
                            stmt.executeQuery(query);
                            resultSet = stmt.getResultSet();
                            if (resultSet != null) {
                                resultSet.next();
                                if (resultSet.getRow() == 0) {
                                    map.put("(" + tabIdThesauru + ")", tabIdThesauru);
                                } else {
                                    map.put(resultSet.getString("title") + "(" + tabIdThesauru + ")", tabIdThesauru);
                                }

                            } else {
                            }
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
            log.error("Error while getting Map of thesaurus : " + map.toString(), sqle);
        }
        return map;
    }

    /**
     * Retourne la liste des Ids des thésaurus existants 
     *
     * @param ds
     * @return
     */
    public List getAllIdOfThesaurus(HikariDataSource ds) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        List tabIdThesaurus = new ArrayList();

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "select id_thesaurus from thesaurus";

                    resultSet = stmt.executeQuery(query);
                    while (resultSet.next()) {
                        tabIdThesaurus.add(resultSet.getString("id_thesaurus"));
                    }

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting All ids of thesaurus : ", sqle);
        }
        return tabIdThesaurus;
    }    
    
    /**
     * retourne la liste des thésaurus  d'un utilisateur
     * @param ds
     * @param idUser
     * @param idLang
     * @return 
     */
    public Map getListThesaurusOfUser(HikariDataSource ds, int idUser, String idLang) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        Map map = new HashMap();
        ArrayList tabIdThesaurus = new ArrayList();

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT DISTINCT user_role.id_thesaurus FROM"
                            + " user_role WHERE"
                            + " id_user = " + idUser;

                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    if (resultSet != null) {
                        while (resultSet.next()) {
                            if(!resultSet.getString("id_thesaurus").isEmpty())
                                tabIdThesaurus.add(resultSet.getString("id_thesaurus"));
                        }
                        for (Object tabIdThesauru : tabIdThesaurus) {
                            query = "select title from thesaurus_label where"
                                    + " id_thesaurus = '" + tabIdThesauru + "'" + " and lang = '" + idLang + "'";
                            stmt.executeQuery(query);
                            resultSet = stmt.getResultSet();
                            if (resultSet != null) {
                                resultSet.next();
                                if (resultSet.getRow() == 0) {
                                    map.put("(" + tabIdThesauru + ")", tabIdThesauru);
                                } else {
                                    map.put(resultSet.getString("title") + "(" + tabIdThesauru + ")", tabIdThesauru);
                                }

                            } else {
                            }
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
            log.error("Error while getting Map of thesaurus : " + map.toString(), sqle);
        }
        return map;
    }    
    
    /**
     * Retourne la liste des traductions d'un thesaurus sous forme de MAP (lang
     * + title)
     *
     * @param ds
     * @param idThesaurus
     * @return
     */
    public Map getMapTraduction(HikariDataSource ds, String idThesaurus) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        Map map = new HashMap();
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "select lang, title from thesaurus_label"
                            + " where id_thesaurus = '" + idThesaurus + "'";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    if (resultSet != null) {
                        while (resultSet.next()) {
                            map.put(
                                    resultSet.getString("lang"),
                                    resultSet.getString("title")
                            );
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
            log.error("Error while getting Map of thesaurus : " + map.toString(), sqle);
        }
        return map;
    }
    
    /**
     * Cette fonction permet de récupérer l'identifiant Ark
     * sinon renvoie un une chaine vide
     *
     * @param ds
     * @param idThesaurus
     * @return String idArk
     */
    public String getIdArkOfThesaurus(HikariDataSource ds, String idThesaurus) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        String ark = "";
        try {
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "select id_ark from thesaurus where"
                            + " id_thesaurus = '" + idThesaurus + "'";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                        
                    if (resultSet.next()) {
                        ark = resultSet.getString("id_ark");
                    }

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting idArk of Thesaurus : " + idThesaurus, sqle);
        }
        return ark;
    }        

    /**
     * Retourne la liste des traductions d'un thesaurus sous forme de ArrayList
     * d'Objet Languages_iso639
     *
     * @param ds
     * @param idThesaurus
     * @return
     */
    public ArrayList<Languages_iso639> getLanguagesOfThesaurus(HikariDataSource ds, String idThesaurus) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        ArrayList<Languages_iso639> lang = null;
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT DISTINCT"
                            + " languages_iso639.iso639_1, "
                            + " languages_iso639.iso639_2, "
                            + " languages_iso639.english_name, "
                            + " languages_iso639.french_name, "
                            + " thesaurus_label.lang"
                            + " FROM "
                            + " thesaurus_label,"
                            + " languages_iso639"
                            + " WHERE"
                            + " thesaurus_label.lang = languages_iso639.iso639_1 AND"
                            + " thesaurus_label.lang = languages_iso639.iso639_1;";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    if (resultSet != null) {
                        lang = new ArrayList<>();
                        while (resultSet.next()) {
                            Languages_iso639 languages_iso639 = new Languages_iso639();
                            languages_iso639.setId_iso639_1(resultSet.getString("iso639_1"));
                            languages_iso639.setId_iso639_2(resultSet.getString("iso639_2"));
                            languages_iso639.setFrench_name(resultSet.getString("french_name"));
                            languages_iso639.setFrench_name(resultSet.getString("english_name"));

                            lang.add(languages_iso639);
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
            log.error("Error while getting List Language of thesaurus : " + idThesaurus, sqle);
        }
        return lang;
    }

    /**
     * Cette fonction permet de retourner toutes les langues utilisées par les
     * Concepts d'un thésaurus (sous forme de NodeLang, un objet complet)
     *
     * @param ds
     * @param idThesaurus
     * @return Objet class NodeConceptTree
     */
    public ArrayList<NodeLang> getAllUsedLanguagesOfThesaurusNode(HikariDataSource ds,
            String idThesaurus) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        ArrayList<NodeLang> nodeLangs = new ArrayList<>();

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT thesaurus_label.lang, languages_iso639.french_name"
                            + " FROM thesaurus_label, languages_iso639"
                            + " WHERE thesaurus_label.lang = languages_iso639.iso639_1"
                            + " and thesaurus_label.id_thesaurus = '" + idThesaurus + "'"
                            + " order by languages_iso639.french_name";

                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    int i=0;
                    while (resultSet.next()) {
                        NodeLang nodeLang = new NodeLang();
                        nodeLang.setId("" + i);
                        nodeLang.setCode(resultSet.getString("lang"));
                        nodeLang.setValue(resultSet.getString("french_name"));
                        nodeLangs.add(nodeLang);
                        i++;
                    }
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting All Used languages of Concepts of thesaurus  : " + idThesaurus, sqle);
        }
        return nodeLangs;
    }    
    
    
    /**
     * Cette fonction permet de retourner toutes les langues utilisées par les
     * Concepts d'un thésaurus  !!! seulement les code iso des langues 
     *
     * @param ds
     * @param idThesaurus
     * @return Objet class NodeConceptTree
     */
    public ArrayList<String> getAllUsedLanguagesOfThesaurus(HikariDataSource ds,
            String idThesaurus) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        ArrayList<String> tabIdLang = new ArrayList<>();

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "select distinct lang from term where id_thesaurus = '" + idThesaurus + "'";

                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    while (resultSet.next()) {
                        tabIdLang.add(resultSet.getString("lang"));
                    }
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting All Used languages of Concepts of thesaurus  : " + idThesaurus, sqle);
        }
        return tabIdLang;
    }

    /**
     * Cette fonction permet de savoir si le terme existe ou non
     *
     * @param ds
     * @param idThesaurus
     * @param idLang
     * @return boolean
     */
    public boolean isLanguageExistOfThesaurus(HikariDataSource ds,
            String idThesaurus, String idLang) {

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
                    String query = "select id_thesaurus from thesaurus_label where "
                            + " id_thesaurus ='" + idThesaurus + "'"
                            + " and lang = '" + idLang + "'";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();

                    resultSet.next();
                    if (resultSet.getRow() == 0) {
                        existe = false;
                    } else {
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
            log.error("Error while asking if Language exist of Thesaurus : " + idThesaurus, sqle);
        }
        return existe;
    }
    
    /**
     * Cette fonction permet de savoir si le thesaurus existe ou non
     *
     * @param ds
     * @param idThesaurus
     * @return boolean
     */
    public boolean isThesaurusExiste(HikariDataSource ds, String idThesaurus) {

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
                    String query = "select id_thesaurus from thesaurus where "
                            + " id_thesaurus = '" + idThesaurus + "'";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    if (resultSet.next()) {
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
            log.error("Error while asking if thesaurus exist : " + idThesaurus, sqle);
        }
        return existe;
    }
    
    /**
     * Cette fonction permet de savoir si le thesaurus existe ou non
     *
     * @param conn
     * @param idThesaurus
     * @return boolean
     */
    public boolean isThesaurusExiste(Connection conn, String idThesaurus) {

        Statement stmt;
        ResultSet resultSet;
        boolean existe = false;

        try {
            try {
                stmt = conn.createStatement();
                try {
                    String query = "select id_thesaurus from thesaurus where "
                            + " id_thesaurus = '" + idThesaurus + "'";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    if (resultSet.next()) {
                        existe = resultSet.getRow() != 0;
                    }

                } finally {
                    stmt.close();
                }
            } finally {
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while asking if thesaurus exist : " + idThesaurus, sqle);
        }
        return existe;
    }    
    

    /**
     * Cette fonction permet d'ajouter des cotes pour passer des données en JDBC
     *
     * @return
     */
    private Thesaurus addQuotes(Thesaurus thesaurus) {

        thesaurus.setContributor(new StringPlus().convertString(thesaurus.getContributor()));
        thesaurus.setCoverage(new StringPlus().convertString(thesaurus.getCoverage()));
        thesaurus.setDescription(new StringPlus().convertString(thesaurus.getDescription()));
        thesaurus.setFormat(new StringPlus().convertString(thesaurus.getFormat()));
        thesaurus.setPublisher(new StringPlus().convertString(thesaurus.getPublisher()));
        thesaurus.setRelation(new StringPlus().convertString(thesaurus.getRelation()));
        thesaurus.setRights(new StringPlus().convertString(thesaurus.getRights()));
        thesaurus.setSource(new StringPlus().convertString(thesaurus.getSource()));
        thesaurus.setSubject(new StringPlus().convertString(thesaurus.getSubject()));
        thesaurus.setTitle(new StringPlus().convertString(thesaurus.getTitle()));
        thesaurus.setType(new StringPlus().convertString(thesaurus.getType()));

        return thesaurus;
    }

    /**
     * Permet de mettre à jour un thésaurus suivant un identifiant et une langue
     * donnés
     *
     * @param ds
     * @param thesaurus
     * @return true or false
     */
    public boolean UpdateThesaurus(HikariDataSource ds, Thesaurus thesaurus) {

        Connection conn;
        Statement stmt;
        boolean status = false;

        thesaurus = addQuotes(thesaurus);

        /**
         * On met à jour tous les chmamps saufs l'idThesaurus, la date de
         * creation en utilisant et la langue
         */
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "UPDATE thesaurus_label "
                            + "set contributor='" + thesaurus.getContributor() + "',"
                            + " coverage='" + thesaurus.getCoverage() + "',"
                            + " creator='" + thesaurus.getCreator() + "',"
                            + " modified = current_date,"
                            + " description='" + thesaurus.getDescription() + "',"
                            + " format='" + thesaurus.getFormat() + "',"
                            + " publisher='" + thesaurus.getPublisher() + "',"
                            + " relation='" + thesaurus.getRelation() + "',"
                            + " rights='" + thesaurus.getRights() + "',"
                            + " source='" + thesaurus.getSource() + "',"
                            + " subject='" + thesaurus.getSubject() + "',"
                            + " title='" + thesaurus.getTitle() + "',"
                            + " type='" + thesaurus.getType() + "'"
                            + " WHERE lang='" + thesaurus.getLanguage() + "'"
                            + " AND id_thesaurus='" + thesaurus.getId_thesaurus() + "'";

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
            log.error("Error while updating thesausurs : " + thesaurus.getTitle() + " lang = " + thesaurus.getLanguage(), sqle);
        }
        return status;

    }

    /**
     * Permet de supprimer un thésaurus
     *
     * @param ds
     * @param idThesaurus
     * @return
     */
    public boolean deleteThesaurus(HikariDataSource ds, String idThesaurus) {
        StringPlus text = new StringPlus();
        idThesaurus = text.convertString(idThesaurus);
        Statement stmt;
        Connection conn;
        boolean state = false;

        try {
            conn = ds.getConnection();

            try {
                stmt = conn.createStatement();

                try {
                    String query = "delete from thesaurus where id_thesaurus = '" + idThesaurus + "';"
                            + "delete from thesaurus_label where id_thesaurus = '" + idThesaurus + "';"
                            + "delete from thesaurus_array where id_thesaurus = '" + idThesaurus + "';"
                            + "delete from node_label where id_thesaurus = '" + idThesaurus + "';"
                            + "delete from thesaurus_array_concept where id_thesaurus = '" + idThesaurus + "';"
                            + "delete from concept where id_thesaurus = '" + idThesaurus + "';"
                            + "delete from concept_historique where id_thesaurus = '" + idThesaurus + "';"
                            + "delete from images where id_thesaurus = '" + idThesaurus + "';"
                            + "delete from preferred_term where id_thesaurus = '" + idThesaurus + "';"
                            + "delete from non_preferred_term where id_thesaurus = '" + idThesaurus + "';"
                            + "delete from non_preferred_term_historique where id_thesaurus = '" + idThesaurus + "';"
                            + "delete from term where id_thesaurus = '" + idThesaurus + "';"
                            + "delete from term_historique where id_thesaurus = '" + idThesaurus + "';"
                            + "delete from concept_group where idthesaurus = '" + idThesaurus + "';"
                            + "delete from concept_group_historique where idthesaurus = '" + idThesaurus + "';"
                            + "delete from concept_group_label where idthesaurus = '" + idThesaurus + "';"
                            + "delete from concept_group_label_historique where idthesaurus = '" + idThesaurus + "';"
                            + "delete from note where id_thesaurus = '" + idThesaurus + "';"
                            + "delete from note_historique where id_thesaurus = '" + idThesaurus + "';"
                            + "delete from permuted where id_thesaurus = '" + idThesaurus + "';"
                            + "delete from hierarchical_relationship where id_thesaurus = '" + idThesaurus + "';"
                            + "delete from hierarchical_relationship_historique where id_thesaurus = '" + idThesaurus + "';"
                            + "delete from concept_candidat where id_thesaurus = '" + idThesaurus + "';"
                            + "delete from concept_term_candidat where id_thesaurus = '" + idThesaurus + "';"
                            + "delete from term_candidat where id_thesaurus = '" + idThesaurus + "';"
                            + "delete from alignement where internal_id_thesaurus = '" + idThesaurus + "';"
                            + "delete from concept_orphan where id_thesaurus = '" + idThesaurus + "';"
                            + "delete from proposition where id_thesaurus = '" + idThesaurus + "';"
                            + "delete from concept_fusion where id_thesaurus = '" + idThesaurus + "';"
                            + "delete from gps where id_theso = '" + idThesaurus + "';"
                            + "delete from thesaurus_alignement_source where id_thesaurus = '" + idThesaurus + "';"
                            + "delete from concept_group_concept where idthesaurus = '" + idThesaurus + "';"
                            + "delete from relation_group where id_thesaurus = '" + idThesaurus + "';"
                            
                            ;

                    stmt.executeUpdate(query);
                    state = true;
                } catch (SQLException e) {
                    Logger.getLogger(ThesaurusHelper.class.getName()).log(Level.SEVERE, null, e);
                } finally {
                    stmt.close();
                }

            } catch (SQLException e) {
                Logger.getLogger(ThesaurusHelper.class.getName()).log(Level.SEVERE, null, e);
            } finally {
                conn.close();
            }

        } catch (SQLException ex) {
            Logger.getLogger(ThesaurusHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return state;
    }

    /**
     * Permet de supprimer une traduction d'un thésaurus
     *
     * @param ds
     * @param idThesaurus
     * @param id_lang
     * @return
     */
    public boolean deleteThesaurusTraduction(HikariDataSource ds, String idThesaurus, String id_lang) {
        StringPlus text = new StringPlus();
        idThesaurus = text.convertString(idThesaurus);
        Statement stmt = null;
        Connection conn = null;
        boolean state = false;

        try {
            conn = ds.getConnection();

            try {
                stmt = conn.createStatement();

                try {
                    String query = "delete from thesaurus_label where id_thesaurus = '"
                            + idThesaurus + "'"
                            + " and lang = '"
                            + id_lang + "'";
                    stmt.executeUpdate(query);
                    state = true;
                } catch (SQLException e) {
                    Logger.getLogger(ThesaurusHelper.class.getName()).log(Level.SEVERE, null, e);
                } finally {
                    stmt.close();
                }

            } catch (SQLException e) {
                Logger.getLogger(ThesaurusHelper.class.getName()).log(Level.SEVERE, null, e);
            } finally {
                conn.close();
            }

        } catch (SQLException ex) {
            Logger.getLogger(ThesaurusHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return state;
    }

}
