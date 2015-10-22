package mom.trd.opentheso.bdd.helper;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import mom.trd.opentheso.bdd.helper.nodes.NodeEM;
import mom.trd.opentheso.bdd.helper.nodes.NodePermute;
import mom.trd.opentheso.bdd.helper.nodes.NodeTab2Levels;
import mom.trd.opentheso.bdd.helper.nodes.search.NodeSearch;
import mom.trd.opentheso.bdd.helper.nodes.term.NodeTermTraduction;
import mom.trd.opentheso.bdd.tools.StringPlus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SearchHelper {

    private final Log log = LogFactory.getLog(ThesaurusHelper.class);

    public SearchHelper() {

    }

    /**
     * Cette fonction permet de faire une recherche par value sur les termes
     * Préférés et les synonymes
     *
     * @param ds
     * @param value
     * @param idLang
     * @param idThesaurus
     * @return
     */
    public ArrayList<NodeSearch> searchTerm(HikariDataSource ds,
            String value, String idLang, String idThesaurus) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        ArrayList<NodeSearch> nodeSearchList = null;
        value = new StringPlus().convertString(value);
        
        String values [] = value.trim().split(" ");
        String query;
        String temp = "";
        try {
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    if(values.length != 0) {
                        // cas où on cherche des mots combinés exemple :
                        // and unaccent_string(term.lexical_value) ilike unaccent_string('%saint%')
                        // and unaccent_string(term.lexical_value) ilike unaccent_string('%rhone%')
                        
                        query = "SELECT term.lexical_value, preferred_term.id_concept,"
                                + " preferred_term.id_term, term.lang, term.id_thesaurus,"
                                + " concept.id_group, concept.top_concept"
                                + " FROM term, preferred_term, concept WHERE"
                                + " concept.id_concept = preferred_term.id_concept AND"
                                + " concept.id_thesaurus = preferred_term.id_thesaurus AND"
                                + " preferred_term.id_term = term.id_term AND"
                                + " preferred_term.id_thesaurus = term.id_thesaurus";
                        for (String value1 : values) {
                            temp = temp + 
                                    " and unaccent_string(term.lexical_value) ilike" +
                                    " unaccent_string('%" + value1 + "%')";

                        }
                        query = query + temp +
                                " and term.id_thesaurus = '" + idThesaurus + "'"
                                + " and term.lang = '" + idLang + "'"
                                + " order by lexical_value ASC LIMIT 100";

                    }
                    else {
                        query = "SELECT term.lexical_value, preferred_term.id_concept,"
                            + " preferred_term.id_term, term.lang, term.id_thesaurus,"
                            + " concept.id_group, concept.top_concept"
                            + " FROM term, preferred_term, concept WHERE"
                            + " concept.id_concept = preferred_term.id_concept AND"
                            + " concept.id_thesaurus = preferred_term.id_thesaurus AND"
                            + " preferred_term.id_term = term.id_term AND"
                            + " preferred_term.id_thesaurus = term.id_thesaurus"
                            + " and"
                            + " unaccent_string(term.lexical_value) ilike"
                            + " unaccent_string('" + value + "%')"
                            + " and term.id_thesaurus = '" + idThesaurus + "'"
                            + " and term.lang = '" + idLang + "'"
                            + " order by lexical_value ASC LIMIT 100";
                    }
                    
                    resultSet = stmt.executeQuery(query);
                    nodeSearchList = new ArrayList<>();
                    while (resultSet.next()) {
                        NodeSearch nodeSearch = new NodeSearch();
                        nodeSearch.setLexical_value(resultSet.getString("lexical_value"));
                        nodeSearch.setIdConcept(resultSet.getString("id_concept"));
                        nodeSearch.setIdTerm(resultSet.getString("id_term"));
                        nodeSearch.setIdGroup(resultSet.getString("id_group"));
                        nodeSearch.setIdLang(idLang);
                        nodeSearch.setIdThesaurus(idThesaurus);
                        nodeSearch.setTopConcept(resultSet.getBoolean("top_concept"));
                        nodeSearch.setPreferredLabel(true);

                        //cas où le terme recherché est égal au terme retrouvé, on le place en premier
                        if (value.trim().equalsIgnoreCase(nodeSearch.getLexical_value().trim())) {
                            nodeSearchList.add(0, nodeSearch);
                        } else {
                            nodeSearchList.add(nodeSearch);
                        }
                    }

                    temp = "";
                    /**
                     * recherche de Synonymes
                     */
                    if(values.length != 0) {
                        // cas où on cherche des mots combinés exemple :
                        // and unaccent_string(term.lexical_value) ilike unaccent_string('%saint%')
                        // and unaccent_string(term.lexical_value) ilike unaccent_string('%rhone%')
                        
                        query = "SELECT non_preferred_term.id_term, "
                            + " non_preferred_term.lexical_value, "
                            + " concept.id_group, preferred_term.id_concept,"
                            + " concept.top_concept"
                            + " FROM non_preferred_term, preferred_term,"
                            + " concept WHERE "
                            + " preferred_term.id_term = non_preferred_term.id_term AND"
                            + " preferred_term.id_thesaurus = non_preferred_term.id_thesaurus AND"
                            + " concept.id_concept = preferred_term.id_concept AND"
                            + " concept.id_thesaurus = preferred_term.id_thesaurus";
                        

                        for (String value1 : values) {
                            temp = temp + 
                                    " and unaccent_string(non_preferred_term.lexical_value) ilike" +
                                    " unaccent_string('%" + value1 + "%')";
                        }
                        query = query + temp +
                            " and non_preferred_term.id_thesaurus = '" + idThesaurus + "'"
                            + " and non_preferred_term.lang = '" + idLang + "'"
                            + " order by lexical_value ASC LIMIT 100";
                    }
                    else {
                        query = "SELECT non_preferred_term.id_term, "
                            + " non_preferred_term.lexical_value, "
                            + " concept.id_group, preferred_term.id_concept,"
                            + " concept.top_concept"
                            + " FROM non_preferred_term, preferred_term,"
                            + " concept WHERE "
                            + " preferred_term.id_term = non_preferred_term.id_term AND"
                            + " preferred_term.id_thesaurus = non_preferred_term.id_thesaurus AND"
                            + " concept.id_concept = preferred_term.id_concept AND"
                            + " concept.id_thesaurus = preferred_term.id_thesaurus"
                            + " and"
                            + " unaccent_string(non_preferred_term.lexical_value) ilike"
                            + " unaccent_string('" + value + "%')"
                            + " and non_preferred_term.id_thesaurus = '" + idThesaurus + "'"
                            + " and non_preferred_term.lang = '" + idLang + "'"
                            + " order by lexical_value ASC LIMIT 100";
                    }

                    resultSet = stmt.executeQuery(query);

                    while (resultSet.next()) {
                        NodeSearch nodeSearch = new NodeSearch();
                        nodeSearch.setLexical_value(resultSet.getString("lexical_value"));
                        nodeSearch.setIdConcept(resultSet.getString("id_concept"));
                        nodeSearch.setIdTerm(resultSet.getString("id_term"));
                        nodeSearch.setIdGroup(resultSet.getString("id_group"));
                        nodeSearch.setIdLang(idLang);
                        nodeSearch.setIdThesaurus(idThesaurus);
                        nodeSearch.setTopConcept(resultSet.getBoolean("top_concept"));
                        nodeSearch.setPreferredLabel(false);

                        //cas où le terme recherché est égal au terme retrouvé, on le place en premier
                        if (value.trim().equalsIgnoreCase(nodeSearch.getLexical_value().trim())) {
                            nodeSearchList.add(0, nodeSearch);
                        } else {
                            nodeSearchList.add(nodeSearch);
                        }
                    }

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }

        } catch (SQLException ex) {
            Logger.getLogger(SearchHelper.class.getName()).log(Level.SEVERE, null, ex);
        }

        return nodeSearchList;
    }

    /**
     * Cette fonction permet de faire une recherche par value en filtran par
     * Group pour les termes préférés et synonymes
     *
     * @param ds
     * @param value
     * @param idLang
     * @param idThesaurus
     * @param idGroup
     * @return
     */
    public ArrayList<NodeSearch> searchTerm(HikariDataSource ds,
            String value, String idLang, String idThesaurus, String idGroup) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        ArrayList<NodeSearch> nodeSearchList = null;

        value = new StringPlus().convertString(value);
        try {
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT term.lexical_value, preferred_term.id_concept,"
                            + " preferred_term.id_term, term.lang, term.id_thesaurus,"
                            + " concept.id_group, concept.top_concept"
                            + " FROM term, preferred_term, concept WHERE"
                            + " concept.id_concept = preferred_term.id_concept AND"
                            + " concept.id_thesaurus = preferred_term.id_thesaurus AND"
                            + " preferred_term.id_term = term.id_term AND"
                            + " preferred_term.id_thesaurus = term.id_thesaurus"
                            + " and"
                            + " unaccent_string(term.lexical_value) ilike"
                            + " unaccent_string('" + value + "%')"
                            + " and term.id_thesaurus = '" + idThesaurus + "'"
                            + " and term.lang = '" + idLang + "'"
                            + " and concept.id_group = '" + idGroup + "'"
                            + " order by lexical_value ASC LIMIT 100";

                    resultSet = stmt.executeQuery(query);
                    nodeSearchList = new ArrayList<>();
                    while (resultSet.next()) {
                        NodeSearch nodeSearch = new NodeSearch();
                        nodeSearch.setLexical_value(resultSet.getString("lexical_value"));
                        nodeSearch.setIdConcept(resultSet.getString("id_concept"));
                        nodeSearch.setIdTerm(resultSet.getString("id_term"));
                        nodeSearch.setIdGroup(resultSet.getString("id_group"));
                        nodeSearch.setIdLang(idLang);
                        nodeSearch.setIdThesaurus(idThesaurus);
                        nodeSearch.setTopConcept(resultSet.getBoolean("top_concept"));
                        nodeSearch.setPreferredLabel(true);

                        //cas où le terme recherché est égal au terme retrouvé, on le place en premier
                        if (value.trim().equalsIgnoreCase(nodeSearch.getLexical_value().trim())) {
                            nodeSearchList.add(0, nodeSearch);
                        } else {
                            nodeSearchList.add(nodeSearch);
                        }
                    }

                    /**
                     * recherche de Synonymes
                     */
                    query = "SELECT non_preferred_term.id_term, "
                            + " non_preferred_term.lexical_value, "
                            + " concept.id_group, preferred_term.id_concept,"
                            + " concept.top_concept"
                            + " FROM non_preferred_term, preferred_term,"
                            + " concept WHERE "
                            + " preferred_term.id_term = non_preferred_term.id_term AND"
                            + " preferred_term.id_thesaurus = non_preferred_term.id_thesaurus AND"
                            + " concept.id_concept = preferred_term.id_concept AND"
                            + " concept.id_thesaurus = preferred_term.id_thesaurus"
                            + " and"
                            + " unaccent_string(non_preferred_term.lexical_value) ilike"
                            + " unaccent_string('" + value + "%')"
                            + " and non_preferred_term.id_thesaurus = '" + idThesaurus + "'"
                            + " and non_preferred_term.lang = '" + idLang + "'"
                            + " and concept.id_group = '" + idGroup + "'"
                            + " order by lexical_value ASC LIMIT 100";

                    resultSet = stmt.executeQuery(query);

                    while (resultSet.next()) {
                        NodeSearch nodeSearch = new NodeSearch();
                        nodeSearch.setLexical_value(resultSet.getString("lexical_value"));
                        nodeSearch.setIdConcept(resultSet.getString("id_concept"));
                        nodeSearch.setIdTerm(resultSet.getString("id_term"));
                        nodeSearch.setIdGroup(resultSet.getString("id_group"));
                        nodeSearch.setIdLang(idLang);
                        nodeSearch.setIdThesaurus(idThesaurus);
                        nodeSearch.setTopConcept(resultSet.getBoolean("top_concept"));
                        nodeSearch.setPreferredLabel(false);

                        //cas où le terme recherché est égal au terme retrouvé, on le place en premier
                        if (value.trim().equalsIgnoreCase(nodeSearch.getLexical_value().trim())) {
                            nodeSearchList.add(0, nodeSearch);
                        } else {
                            nodeSearchList.add(nodeSearch);
                        }
                    }

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }

        } catch (SQLException ex) {
            Logger.getLogger(SearchHelper.class.getName()).log(Level.SEVERE, null, ex);
        }

        return nodeSearchList;
    }

    /**
     * Cette fonction permet de faire une recherche par value sur les termes
     * préférés
     *
     * @param ds
     * @param value
     * @param idLang
     * @param idThesaurus
     * @return
     */
    public ArrayList<NodeSearch> searchPreferredTerm(HikariDataSource ds,
            String value, String idLang, String idThesaurus) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        ArrayList<NodeSearch> nodeSearchList = null;

        value = new StringPlus().convertString(value);
        try {
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT term.lexical_value, preferred_term.id_concept,"
                            + " preferred_term.id_term, term.lang, term.id_thesaurus,"
                            + " concept.id_group, concept.top_concept"
                            + " FROM term, preferred_term, concept WHERE"
                            + " concept.id_concept = preferred_term.id_concept AND"
                            + " concept.id_thesaurus = preferred_term.id_thesaurus AND"
                            + " preferred_term.id_term = term.id_term AND"
                            + " preferred_term.id_thesaurus = term.id_thesaurus"
                            + " and"
                            + " unaccent_string(term.lexical_value) ilike"
                            + " unaccent_string('" + value + "%')"
                            + " and term.id_thesaurus = '" + idThesaurus + "'"
                            + " and term.lang = '" + idLang + "'"
                            + " order by lexical_value ASC LIMIT 100";

                    resultSet = stmt.executeQuery(query);
                    nodeSearchList = new ArrayList<>();
                    while (resultSet.next()) {
                        NodeSearch nodeSearch = new NodeSearch();
                        nodeSearch.setLexical_value(resultSet.getString("lexical_value"));
                        nodeSearch.setIdConcept(resultSet.getString("id_concept"));
                        nodeSearch.setIdTerm(resultSet.getString("id_term"));
                        nodeSearch.setIdGroup(resultSet.getString("id_group"));
                        nodeSearch.setIdLang(idLang);
                        nodeSearch.setIdThesaurus(idThesaurus);
                        nodeSearch.setTopConcept(resultSet.getBoolean("top_concept"));
                        nodeSearch.setPreferredLabel(true);

                        //cas où le terme recherché est égal au terme retrouvé, on le place en premier
                        if (value.trim().equalsIgnoreCase(nodeSearch.getLexical_value().trim())) {
                            nodeSearchList.add(0, nodeSearch);
                        } else {
                            nodeSearchList.add(nodeSearch);
                        }
                    }
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }

        } catch (SQLException ex) {
            Logger.getLogger(SearchHelper.class.getName()).log(Level.SEVERE, null, ex);
        }

        return nodeSearchList;
    }

    /**
     * Cette fonction permet de faire une recherche par idConcept
     * ici on indique la langue 
     *
     * @param ds
     * @param id
     * @param idThesaurus
     * @param idLang
     * @return
     */
    public ArrayList<NodeSearch> searchIdConcept(HikariDataSource ds,
            String id, String idThesaurus, String idLang) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        ArrayList<NodeSearch> nodeSearchList = null;

        try {
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT term.lexical_value,"
                            + " preferred_term.id_term, term.lang, term.id_thesaurus,"
                            + " concept.id_group, concept.top_concept"
                            + " FROM term, preferred_term, concept WHERE"
                            + " concept.id_concept = preferred_term.id_concept AND"
                            + " concept.id_thesaurus = preferred_term.id_thesaurus AND"
                            + " preferred_term.id_term = term.id_term AND"
                            + " preferred_term.id_thesaurus = term.id_thesaurus"
                            + " and concept.id_concept = '" + id + "'"
                            + " and term.id_thesaurus = '" + idThesaurus + "'"
                            + " and term.lang = '" + idLang + "'"
                            + " order by lexical_value ASC LIMIT 100";

                    resultSet = stmt.executeQuery(query);
                    nodeSearchList = new ArrayList<>();
                    while (resultSet.next()) {
                        NodeSearch nodeSearch = new NodeSearch();
                        nodeSearch.setLexical_value(resultSet.getString("lexical_value"));
                        nodeSearch.setIdConcept(id);
                        nodeSearch.setIdTerm(resultSet.getString("id_term"));
                        nodeSearch.setIdGroup(resultSet.getString("id_group"));
                        nodeSearch.setIdLang(idLang);
                        nodeSearch.setIdThesaurus(idThesaurus);
                        nodeSearch.setTopConcept(resultSet.getBoolean("top_concept"));
                        nodeSearch.setPreferredLabel(true);

                        nodeSearchList.add(nodeSearch);
                    }
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }

        } catch (SQLException ex) {
            Logger.getLogger(SearchHelper.class.getName()).log(Level.SEVERE, null, ex);
        }

        return nodeSearchList;
    }
    
    /**
     * Cette fonction permet de faire une recherche par idConcept sans tenir compte de la langue 
     * on retourne un tableau du Concept dans toutes les langues disponibles
     * 
     * @param ds
     * @param id
     * @param idThesaurus
     * @return
     */
    public ArrayList<NodeSearch> searchIdConcept(HikariDataSource ds,
            String id, String idThesaurus) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        ArrayList<NodeSearch> nodeSearchList = null;

        try {
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT term.lexical_value,"
                            + " preferred_term.id_term, term.lang, term.id_thesaurus,"
                            + " concept.id_group, concept.top_concept"
                            + " FROM term, preferred_term, concept WHERE"
                            + " concept.id_concept = preferred_term.id_concept AND"
                            + " concept.id_thesaurus = preferred_term.id_thesaurus AND"
                            + " preferred_term.id_term = term.id_term AND"
                            + " preferred_term.id_thesaurus = term.id_thesaurus"
                            + " and concept.id_concept = '" + id + "'"
                            + " and term.id_thesaurus = '" + idThesaurus + "'"
                            + " order by lexical_value ASC";

                    resultSet = stmt.executeQuery(query);
                    nodeSearchList = new ArrayList<>();
                    while (resultSet.next()) {
                        NodeSearch nodeSearch = new NodeSearch();
                        nodeSearch.setLexical_value(resultSet.getString("lexical_value"));
                        nodeSearch.setIdConcept(id);
                        nodeSearch.setIdTerm(resultSet.getString("id_term"));
                        nodeSearch.setIdGroup(resultSet.getString("id_group"));
                        nodeSearch.setIdLang(resultSet.getString("lang"));
                        nodeSearch.setIdThesaurus(idThesaurus);
                        nodeSearch.setTopConcept(resultSet.getBoolean("top_concept"));
                        nodeSearch.setPreferredLabel(true);

                        nodeSearchList.add(nodeSearch);
                    }
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }

        } catch (SQLException ex) {
            Logger.getLogger(SearchHelper.class.getName()).log(Level.SEVERE, null, ex);
        }

        return nodeSearchList;
    }

    /**
     * Cette fonction permet de faire une recherche par idTerme
     *
     * @param ds
     * @param id
     * @param idThesaurus
     * @param idLang
     * @return
     */
    public ArrayList<NodeSearch> searchIdTerm(HikariDataSource ds,
            String id, String idThesaurus, String idLang) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        ArrayList<NodeSearch> nodeSearchList = null;

        try {
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT term.lexical_value, preferred_term.id_concept,"
                            + " term.lang, term.id_thesaurus,"
                            + " concept.id_group, concept.top_concept"
                            + " FROM term, preferred_term, concept WHERE"
                            + " concept.id_concept = preferred_term.id_concept AND"
                            + " concept.id_thesaurus = preferred_term.id_thesaurus AND"
                            + " preferred_term.id_term = term.id_term AND"
                            + " preferred_term.id_thesaurus = term.id_thesaurus"
                            + " and term.id_term = '" + id + "'"
                            + " and term.id_thesaurus = '" + idThesaurus + "'"
                            + " and term.lang = '" + idLang + "'"
                            + " order by lexical_value ASC LIMIT 100";

                    resultSet = stmt.executeQuery(query);
                    nodeSearchList = new ArrayList<>();
                    while (resultSet.next()) {
                        NodeSearch nodeSearch = new NodeSearch();
                        nodeSearch.setLexical_value(resultSet.getString("lexical_value"));
                        nodeSearch.setIdConcept(resultSet.getString("id_concept"));
                        nodeSearch.setIdTerm(id);
                        nodeSearch.setIdGroup(resultSet.getString("id_group"));
                        nodeSearch.setIdLang(idLang);
                        nodeSearch.setIdThesaurus(idThesaurus);
                        nodeSearch.setTopConcept(resultSet.getBoolean("top_concept"));
                        nodeSearch.setPreferredLabel(true);

                        nodeSearchList.add(nodeSearch);
                    }
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(SearchHelper.class.getName()).log(Level.SEVERE, null, ex);
        }

        return nodeSearchList;
    }

    /**
     * Cette fonction permet de faire une recherche par value en filtran par
     * Group pour les termes préférés uniquement
     *
     * @param ds
     * @param value
     * @param idLang
     * @param idThesaurus
     * @param idGroup
     * @return
     */
    public ArrayList<NodeSearch> searchPreferredTerm(HikariDataSource ds,
            String value, String idLang, String idThesaurus, String idGroup) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        ArrayList<NodeSearch> nodeSearchList = null;
        value = new StringPlus().convertString(value);
        try {
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT term.lexical_value, preferred_term.id_concept,"
                            + " preferred_term.id_term, term.lang, term.id_thesaurus,"
                            + " concept.id_group, concept.top_concept"
                            + " FROM term, preferred_term, concept WHERE"
                            + " concept.id_concept = preferred_term.id_concept AND"
                            + " concept.id_thesaurus = preferred_term.id_thesaurus AND"
                            + " preferred_term.id_term = term.id_term AND"
                            + " preferred_term.id_thesaurus = term.id_thesaurus"
                            + " and"
                            + " unaccent_string(term.lexical_value) ilike"
                            + " unaccent_string('" + value + "%')"
                            + " and term.id_thesaurus = '" + idThesaurus + "'"
                            + " and term.lang = '" + idLang + "'"
                            + " and concept.id_group = '" + idGroup + "'"
                            + " order by lexical_value ASC LIMIT 100";

                    resultSet = stmt.executeQuery(query);
                    nodeSearchList = new ArrayList<>();
                    while (resultSet.next()) {
                        NodeSearch nodeSearch = new NodeSearch();
                        nodeSearch.setLexical_value(resultSet.getString("lexical_value"));
                        nodeSearch.setIdConcept(resultSet.getString("id_concept"));
                        nodeSearch.setIdTerm(resultSet.getString("id_term"));
                        nodeSearch.setIdGroup(resultSet.getString("id_group"));
                        nodeSearch.setIdLang(idLang);
                        nodeSearch.setIdThesaurus(idThesaurus);
                        nodeSearch.setTopConcept(resultSet.getBoolean("top_concept"));

                        //cas où le terme recherché est égal au terme retrouvé, on le place en premier
                        if (value.trim().equalsIgnoreCase(nodeSearch.getLexical_value().trim())) {
                            nodeSearchList.add(0, nodeSearch);
                        } else {
                            nodeSearchList.add(nodeSearch);
                        }
                    }
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }

        } catch (SQLException ex) {
            Logger.getLogger(SearchHelper.class.getName()).log(Level.SEVERE, null, ex);
        }

        return nodeSearchList;
    }

    /**
     * Cette fonction permet de faire une recherche par value en filtran par
     * Group
     *
     * @param ds
     * @param value
     * @param idLang
     * @param idThesaurus
     * @return
     */
    public ArrayList<NodeSearch> searchNonPreferedTerm(HikariDataSource ds,
            String value, String idLang, String idThesaurus) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        ArrayList<NodeSearch> nodeSearchList = null;
        value = new StringPlus().convertString(value);
        try {
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT non_preferred_term.id_term, "
                            + " non_preferred_term.lexical_value, "
                            + " concept.id_group, preferred_term.id_concept,"
                            + " concept.top_concept"
                            + " FROM non_preferred_term, preferred_term,"
                            + " concept WHERE "
                            + " preferred_term.id_term = non_preferred_term.id_term AND"
                            + " preferred_term.id_thesaurus = non_preferred_term.id_thesaurus AND"
                            + " concept.id_concept = preferred_term.id_concept AND"
                            + " concept.id_thesaurus = preferred_term.id_thesaurus"
                            + " and"
                            + " unaccent_string(non_preferred_term.lexical_value) ilike"
                            + " unaccent_string('" + value + "%')"
                            + " and non_preferred_term.id_thesaurus = '" + idThesaurus + "'"
                            + " and non_preferred_term.lang = '" + idLang + "'"
                            + " order by lexical_value ASC LIMIT 100";

                    resultSet = stmt.executeQuery(query);
                    nodeSearchList = new ArrayList<>();
                    while (resultSet.next()) {
                        NodeSearch nodeSearch = new NodeSearch();
                        nodeSearch.setLexical_value(resultSet.getString("lexical_value"));
                        nodeSearch.setIdConcept(resultSet.getString("id_concept"));
                        nodeSearch.setIdTerm(resultSet.getString("id_term"));
                        nodeSearch.setIdGroup(resultSet.getString("id_group"));
                        nodeSearch.setIdLang(idLang);
                        nodeSearch.setIdThesaurus(idThesaurus);
                        nodeSearch.setTopConcept(resultSet.getBoolean("top_concept"));
                        nodeSearch.setPreferredLabel(false);

                        //cas où le terme recherché est égal au terme retrouvé, on le place en premier
                        if (value.trim().equalsIgnoreCase(nodeSearch.getLexical_value().trim())) {
                            nodeSearchList.add(0, nodeSearch);
                        } else {
                            nodeSearchList.add(nodeSearch);
                        }
                    }
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }

        } catch (SQLException ex) {
            Logger.getLogger(SearchHelper.class.getName()).log(Level.SEVERE, null, ex);
        }

        return nodeSearchList;
    }

    /**
     * Cette fonction permet de faire une recherche par value en filtran par
     * Group
     *
     * @param ds
     * @param value
     * @param idLang
     * @param idThesaurus
     * @param idGroup
     * @return
     */
    public ArrayList<NodeSearch> searchNonPreferedTerm(HikariDataSource ds,
            String value, String idLang, String idThesaurus, String idGroup) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        ArrayList<NodeSearch> nodeSearchList = null;
        value = new StringPlus().convertString(value);
        try {
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT non_preferred_term.id_term, "
                            + " non_preferred_term.lexical_value, "
                            + " concept.id_group, preferred_term.id_concept,"
                            + " concept.top_concept"
                            + " FROM non_preferred_term, preferred_term,"
                            + " concept WHERE "
                            + " preferred_term.id_term = non_preferred_term.id_term AND"
                            + " preferred_term.id_thesaurus = non_preferred_term.id_thesaurus AND"
                            + " concept.id_concept = preferred_term.id_concept AND"
                            + " concept.id_thesaurus = preferred_term.id_thesaurus"
                            + " and"
                            + " unaccent_string(non_preferred_term.lexical_value) ilike"
                            + " unaccent_string('" + value + "%')"
                            + " and non_preferred_term.id_thesaurus = '" + idThesaurus + "'"
                            + " and non_preferred_term.lang = '" + idLang + "'"
                            + " and concept.id_group = '" + idGroup + "'"
                            + " order by lexical_value ASC LIMIT 100";

                    resultSet = stmt.executeQuery(query);
                    nodeSearchList = new ArrayList<>();
                    while (resultSet.next()) {
                        NodeSearch nodeSearch = new NodeSearch();
                        nodeSearch.setLexical_value(resultSet.getString("lexical_value"));
                        nodeSearch.setIdConcept(resultSet.getString("id_concept"));
                        nodeSearch.setIdTerm(resultSet.getString("id_term"));
                        nodeSearch.setIdGroup(resultSet.getString("id_group"));
                        nodeSearch.setIdLang(idLang);
                        nodeSearch.setIdThesaurus(idThesaurus);
                        nodeSearch.setTopConcept(resultSet.getBoolean("top_concept"));
                        nodeSearch.setPreferredLabel(false);

                        //cas où le terme recherché est égal au terme retrouvé, on le place en premier
                        if (value.trim().equalsIgnoreCase(nodeSearch.getLexical_value().trim())) {
                            nodeSearchList.add(0, nodeSearch);
                        } else {
                            nodeSearchList.add(nodeSearch);
                        }
                    }
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }

        } catch (SQLException ex) {
            Logger.getLogger(SearchHelper.class.getName()).log(Level.SEVERE, null, ex);
        }

        return nodeSearchList;
    }

    /**
     * FONCTION CAROLE Cette fonction permet de faire une recherche permutée par
     * value
     *
     * @param ds
     * @param value
     * @param idLang
     * @param idThesaurus
     * @return
     */
    /*public ArrayList<NodePermute> searchTermPermute(HikariDataSource ds, 
     String value, String idLang, String idThesaurus) {

     Connection conn;
     Statement stmt;
     ResultSet resultSet;
     ArrayList <NodePermute> nodeSearchList = new ArrayList<>();
        
     ArrayList<Entry<String, String>> listId = searchIdPermute(ds, value, idLang, idThesaurus);
        
     try {
     conn = ds.getConnection();
     stmt = conn.createStatement();
     for(Entry<String, String> e : listId) {
     String query = "SELECT * FROM permute WHERE"
     + " and id_concept = '" + e.getKey() + "'";
            
     resultSet = stmt.executeQuery(query);
                
     resultSet.next();
     NodePermute np = new NodePermute();
     np.setIdConcept(e.getKey());
     np.setIdGroup(resultSet.getString("id_group"));
     np.setIdLang(idLang);
     np.setIndexOfValue(Integer.parseInt(e.getValue()));
     if(Integer.parseInt(resultSet.getString("ord")) < np.getIndexOfValue()) {
     np.setFirstColumn(resultSet.getString("lexical_value"));
     } else {
     np.setSearchedValue(resultSet.getString("lexical_value"));
     }
                
     while (resultSet.next()) {
     if(Integer.parseInt(resultSet.getString("ord")) < np.getIndexOfValue()) {
     np.setFirstColumn(np.getFirstColumn() + resultSet.getString("lexical_value"));
     } else if(Integer.parseInt(resultSet.getString("ord")) == np.getIndexOfValue()) {
     np.setSearchedValue(resultSet.getString("lexical_value"));
     } else {
     np.setLastColumn(np.getLastColumn() + resultSet.getString("lexical_value"));
     }
     }
     nodeSearchList.add(np);
     }
            
     } catch (SQLException ex) {
     Logger.getLogger(SearchHelper.class.getName()).log(Level.SEVERE, null, ex);
     }
        
     return nodeSearchList;
     }*/
    /**
     * FONCTION CAROLE Cette fonction permet récupérer la liste des identifiants
     * pour une recherche permutée avec leur numéro (ordre)
     *
     * @param ds
     * @param value
     * @param idLang
     * @param idThesaurus
     * @return
     */
    /*public ArrayList<Entry<String, String>> searchIdPermute(HikariDataSource ds, 
     String value, String idLang, String idThesaurus) {

     Connection conn;
     Statement stmt;
     ResultSet resultSet;
     ArrayList <Entry<String, String>> idList = null;
        
     try {
     conn = ds.getConnection();
     stmt = conn.createStatement();
     String query = "SELECT id_concept, ord FROM permuted WHERE"
     + " unaccent_string(term.lexical_value) ilike"
     + " unaccent_string('" + value + "%')"
     + " and id_thesaurus = '" + idThesaurus + "'"
     + " and id_lang = '" + idLang + "'"
     + " order by lexical_value ASC LIMIT 100";
            
     resultSet = stmt.executeQuery(query);
     idList = new ArrayList<>();
     Map temp = new HashMap();
     while (resultSet.next()) {
     temp.put(resultSet.getString("id_concept"), resultSet.getString("ord"));
     }
     idList.addAll(temp.entrySet());
            
     } catch (SQLException ex) {
     Logger.getLogger(SearchHelper.class.getName()).log(Level.SEVERE, null, ex);
     }
        
     return idList;
     }*/
    /*
     * Recherche Permutée
     */
    /**
     * Cette fonction permet de récupérer une liste des concepts sous forme
     * permutée : la première collonne est la première valeur non recherchée ou
     * vide, la deuxième colonne est la valeur recherchée et la troisième
     * colonne contient le reste du concept
     *
     * Retourne les synonymes aussi
     *
     * @param ds
     * @param idThesaurus
     * @param idLang
     * @param value
     * @return ArrayList de NodePermute
     */
    public ArrayList<NodePermute> getListPermute(HikariDataSource ds,
            String idThesaurus, String idLang, String value) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        ArrayList<NodePermute> nodePermuteList = new ArrayList<>();
        value = new StringPlus().convertString(value);

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT ord, id_concept, "
                            + " id_group, id_lang,"
                            + " lexical_value, ispreferredterm, original_value"
                            + " FROM permuted WHERE"
                            + " id_lang = '" + idLang + "'"
                            + " and id_thesaurus = '" + idThesaurus + "'"
                            + " and unaccent_string(lexical_value) ilike"
                            + " unaccent_string('" + value + "%')"
                            + " order by lexical_value ASC LIMIT 200";

                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();

                    while (resultSet.next()) {
                        NodePermute nodePermute = new NodePermute();
                        nodePermute.setIdThesaurus(idThesaurus);
                        nodePermute.setIdConcept(resultSet.getString("id_concept"));
                        nodePermute.setIdLang(resultSet.getString("id_lang"));
                        nodePermute.setIdGroup(resultSet.getString("id_group"));
                        nodePermute.setSearchedValue(resultSet.getString("lexical_value"));
                        nodePermute.setIndexOfValue(resultSet.getInt("ord"));
                        nodePermute.setIsPreferredTerm(resultSet.getBoolean("ispreferredterm"));

                        //cas où le terme recherché est égal au terme retrouvé, on le place en premier
                        if (value.trim().equalsIgnoreCase(resultSet.getString("original_value").trim())) {
                            nodePermuteList.add(0, nodePermute);
                        } else {
                            nodePermuteList.add(nodePermute);
                        }
                    }
                    if (!nodePermuteList.isEmpty()) {
                        nodePermuteList = addColumnsToPermute(ds, idThesaurus, nodePermuteList);
                    }

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting List Permute of Value : " + value, sqle);
        }

        return nodePermuteList;
    }

    /**
     * Cette fonction permet de récupérer une liste des concepts sous forme
     * permutée par domaine
     *
     * renvoie les synonymes aussi
     *
     * @param ds
     * @param idThesaurus
     * @param idLang
     * @param value
     * @param idGroup
     * @return ArrayList de NodePermute
     */
    public ArrayList<NodePermute> getListPermute(HikariDataSource ds,
            String idThesaurus, String idLang, String value, String idGroup) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        ArrayList<NodePermute> nodePermuteList = null;
        value = new StringPlus().convertString(value);

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT ord, id_concept, "
                            + " id_lang, lexical_value, ispreferredterm, original_value"
                            + " FROM permuted WHERE"
                            + " id_lang = '" + idLang + "'"
                            + " and id_group = '" + idGroup + "'"
                            + " and id_thesaurus = '" + idThesaurus + "'"
                            + " and unaccent_string(lexical_value) ilike"
                            + " unaccent_string('" + value + "%')"
                            + " order by lexical_value ASC LIMIT 200";

                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    nodePermuteList = new ArrayList<>();
                    while (resultSet.next()) {
                        NodePermute nodePermute = new NodePermute();
                        nodePermute.setIdThesaurus(idThesaurus);
                        nodePermute.setIdConcept(resultSet.getString("id_concept"));
                        nodePermute.setIdLang(resultSet.getString("id_lang"));
                        nodePermute.setIdGroup(idGroup);
                        nodePermute.setSearchedValue(resultSet.getString("lexical_value"));
                        nodePermute.setIndexOfValue(resultSet.getInt("ord"));
                        nodePermute.setIsPreferredTerm(resultSet.getBoolean("ispreferredterm"));

                        //cas où le terme recherché est égal au terme retrouvé, on le place en premier
                        if (value.trim().equalsIgnoreCase(resultSet.getString("original_value").trim())) {
                            nodePermuteList.add(0, nodePermute);
                        } else {
                            nodePermuteList.add(nodePermute);
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
            log.error("Error while getting List Permute of Value by Group : " + value, sqle);
        }
        if (nodePermuteList != null) {
            nodePermuteList = addColumnsToPermute(ds, idThesaurus, nodePermuteList);
        }

        return nodePermuteList;
    }

    /**
     * Cette fonction permet de récupérer une liste des concepts sous forme
     * permutée par domaine
     *
     * @param ds
     * @param idThesaurus
     * @param idLang
     * @param value
     * @param idGroup
     * @return ArrayList de NodePermute
     */
    public ArrayList<NodePermute> getListPermuteNonPreferredTerm(HikariDataSource ds,
            String idThesaurus, String idLang, String value, String idGroup) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        ArrayList<NodePermute> nodePermuteList = null;
        value = new StringPlus().convertString(value);

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT DISTINCT ord, id_concept, "
                            + " id_lang, lexical_value"
                            + " FROM permuted WHERE"
                            + " id_lang = '" + idLang + "'"
                            + " and id_group = '" + idGroup + "'"
                            + " and id_thesaurus = '" + idThesaurus + "'"
                            + " and ispreferredterm = " + false
                            + " and unaccent_string(lexical_value) ilike"
                            + " unaccent_string('" + value + "%')"
                            + " order by lexical_value ASC LIMIT 200";

                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    nodePermuteList = new ArrayList<>();
                    while (resultSet.next()) {
                        NodePermute nodePermute = new NodePermute();
                        nodePermute.setIdThesaurus(idThesaurus);
                        nodePermute.setIdConcept(resultSet.getString("id_concept"));
                        nodePermute.setIdLang(resultSet.getString("id_lang"));
                        nodePermute.setIdGroup(idGroup);
                        nodePermute.setSearchedValue(resultSet.getString("lexical_value"));
                        nodePermute.setIndexOfValue(resultSet.getInt("ord"));
                        nodePermute.setIsPreferredTerm(false);
                        //cas où le terme recherché est égal au terme retrouvé, on le place en premier
                        if (value.trim().equalsIgnoreCase(nodePermute.getSearchedValue().trim())) {
                            nodePermuteList.add(0, nodePermute);
                        } else {
                            nodePermuteList.add(nodePermute);
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
            log.error("Error while getting List Permute of Value : " + value, sqle);
        }
        if (nodePermuteList != null) {
            nodePermuteList = addColumnsToPermute(ds, idThesaurus, nodePermuteList);
        }

        return nodePermuteList;
    }

    /**
     * Cette fonction permet de récupérer une liste des concepts Synonymes sous
     * forme permutée
     *
     * @param ds
     * @param idThesaurus
     * @param idLang
     * @param value
     * @return ArrayList de NodePermute
     */
    public ArrayList<NodePermute> getListPermuteNonPreferredTerm(HikariDataSource ds,
            String idThesaurus, String idLang, String value) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        ArrayList<NodePermute> nodePermuteList = null;
        value = new StringPlus().convertString(value);

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT DISTINCT ord, id_concept, "
                            + " id_lang, lexical_value, id_group"
                            + " FROM permuted WHERE"
                            + " id_lang = '" + idLang + "'"
                            + " and id_thesaurus = '" + idThesaurus + "'"
                            + " and ispreferredterm = " + false
                            + " and unaccent_string(lexical_value) ilike"
                            + " unaccent_string('" + value + "%')"
                            + " order by lexical_value ASC LIMIT 200";

                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    nodePermuteList = new ArrayList<>();
                    while (resultSet.next()) {
                        NodePermute nodePermute = new NodePermute();
                        nodePermute.setIdThesaurus(idThesaurus);
                        nodePermute.setIdConcept(resultSet.getString("id_concept"));
                        nodePermute.setIdLang(resultSet.getString("id_lang"));
                        nodePermute.setSearchedValue(resultSet.getString("lexical_value"));
                        nodePermute.setIndexOfValue(resultSet.getInt("ord"));
                        nodePermute.setIdGroup(resultSet.getString("id_group"));
                        nodePermute.setIsPreferredTerm(false);
                        //cas où le terme recherché est égal au terme retrouvé, on le place en premier
                        if (value.trim().equalsIgnoreCase(nodePermute.getSearchedValue().trim())) {
                            nodePermuteList.add(0, nodePermute);
                        } else {
                            nodePermuteList.add(nodePermute);
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
            log.error("Error while getting List Permute of Value : " + value, sqle);
        }
        if (nodePermuteList != null) {
            nodePermuteList = addColumnsToPermute(ds, idThesaurus, nodePermuteList);
        }
        return nodePermuteList;
    }

    /**
     * Cette fonction permet de récupérer la valeur d'origine du concept
     * réorganisé dans le bon ordre pour construire un tableau de 3 colonnes : 1
     * - le premier est le contenu avant la valeur recherchée 2 - le deuxième
     * contient le mot recherché 3 - le troisième contient le reste de la valeur
     * exp : saint clair du rhone si on cherche (clair) 1 = saint 2 = clair 3 =
     * du rhone
     *
     * @param ds
     * @param idThesaurus
     * @param idConcept
     * @param idLang
     * @param isPreferredTerm
     * @return ArrayList de String (les valeurs dans l'ordre)
     */
    public ArrayList<String> getThisConceptPermute(HikariDataSource ds,
            String idThesaurus, String idConcept, String idLang, boolean isPreferredTerm) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        ArrayList<String> tabValues = null;

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT ord, lexical_value"
                            + " FROM permuted WHERE"
                            + " id_lang = '" + idLang + "'"
                            + " and id_thesaurus = '" + idThesaurus + "'"
                            + " and id_concept = '" + idConcept + "'"
                            + " and ispreferredterm = " + isPreferredTerm
                            + " order by ord ASC";

                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    tabValues = new ArrayList<>();
                    while (resultSet.next()) {
                        tabValues.add(resultSet.getString("lexical_value"));
                    }

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting the value Permute of Concept : " + idConcept, sqle);
        }
        return tabValues;
    }

    /**
     * Cette fonction permet de compléter la colomne de gauche et la colonne de
     * droite avec les valeurs d'un concept Exp : Rhone Alpes Auvergne = colonne
     * de gauche = Rhone mot recherché = Alpes colonne de droite = Auvergne
     *
     * @param ds
     * @param idThesaurus
     * @param nodePermuteList
     * @return ArrayList de NodePermute
     */
    public ArrayList<NodePermute> addColumnsToPermute(HikariDataSource ds,
            String idThesaurus, ArrayList<NodePermute> nodePermuteList) {

        ArrayList<String> tabValues;
        String firstColumn = "";
        String lastColumn = "";

        for (NodePermute nodePermute : nodePermuteList) {
            tabValues = getThisConceptPermute(ds, idThesaurus,
                    nodePermute.getIdConcept(),
                    nodePermute.getIdLang(),
                    nodePermute.isIsPreferredTerm());
            if (tabValues != null) {
                for (int i = 0; i < nodePermute.getIndexOfValue() - 1; i++) {
                    firstColumn = firstColumn + " " + tabValues.get(i);
                }
                for (int i = nodePermute.getIndexOfValue(); i < tabValues.size(); i++) {
                    lastColumn = lastColumn + " " + tabValues.get(i);
                }
                nodePermute.setFirstColumn(firstColumn.trim());
                nodePermute.setLastColumn(lastColumn.trim());

            }
            firstColumn = "";
            lastColumn = "";

        }

        return nodePermuteList;
    }

    /**
     * Cette fonction permet de générer la table Permuted d'après les tables
     * PreferredTerm et NonPreferredTerm
     *
     * @param ds
     * @param idThesaurus
     * @return ArrayList de NodePermute
     */
    public boolean generatePermutedTable(HikariDataSource ds,
            String idThesaurus) {

        ConceptHelper conceptHelper = new ConceptHelper();
        TermHelper termHelper = new TermHelper();
        ArrayList<NodeTermTraduction> nodeTermTraductionList;
        ArrayList<NodeEM> nodeEMList;

        // suppression des données de la table Permuted pour un thésaurus
        termHelper.deletePermutedTable(ds, idThesaurus);

        // Génération des Termes Préférés (PreferredTerm)
        ArrayList<String> tabIdConcept = conceptHelper.getAllIdConceptOfThesaurus(ds, idThesaurus);

        for (String idConcept : tabIdConcept) {
            nodeTermTraductionList = termHelper.getAllTraductionsOfConcept(ds, idConcept, idThesaurus);
            for (NodeTermTraduction nodeTermTraduction : nodeTermTraductionList) {
                // cette fonction permet de remplir la table Permutée
                termHelper.splitConceptForPermute(ds, idConcept,
                        new ConceptHelper().getGroupIdOfConcept(ds, idConcept, idThesaurus),
                        idThesaurus,
                        nodeTermTraduction.getLang(),
                        nodeTermTraduction.getLexicalValue());
            }
        }

        // Génération des Termes Synonymes (NonPreferredTerm)
        ArrayList<NodeTab2Levels> tabIdNonPreferredTerm = termHelper.getAllIdOfNonPreferredTerms(ds, idThesaurus);

        for (NodeTab2Levels nodeTab2Levels : tabIdNonPreferredTerm) {
            nodeEMList = termHelper.getAllNonPreferredTerms(ds, nodeTab2Levels.getIdTerm(), idThesaurus);
            for (NodeEM nodeEM : nodeEMList) {
                // cette fonction permet de remplir la table Permutée
                termHelper.splitConceptForNonPermuted(ds,
                        nodeTab2Levels.getIdConcept(),
                        new ConceptHelper().getGroupIdOfConcept(ds, nodeTab2Levels.getIdConcept(), idThesaurus),
                        idThesaurus,
                        nodeEM.getLang(),
                        nodeEM.getLexical_value());
            }
        }

        return true;
    }
}
