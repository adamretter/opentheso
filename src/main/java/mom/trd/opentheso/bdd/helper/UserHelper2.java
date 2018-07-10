package mom.trd.opentheso.bdd.helper;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import java.util.logging.Level;
import java.util.logging.Logger;

import mom.trd.opentheso.bdd.helper.nodes.NodeUser2;
import mom.trd.opentheso.bdd.helper.nodes.NodeUserGroupThesaurus;
import mom.trd.opentheso.bdd.helper.nodes.NodeUserRole;
import mom.trd.opentheso.bdd.helper.nodes.NodeUserRoleGroup;

public class UserHelper2 {

    public UserHelper2() {
    }
    
 //// restructuration de la classe User le 05/04/2018 //////    
    
 ////////////////////////////////////////////////////////////////////
 ////////////////////////////////////////////////////////////////////
 ////////////////// Nouvelles fontions #MR//////////////////////////////
 ////////////////////////////////////////////////////////////////////
 ////////////////////////////////////////////////////////////////////    

    /**
     * Cette fonction permet d'ajouter un nouvel utilisateur sans le role
     *
     * @param ds
     * @param userName
     * @param mail
     * @param password
     * @param isSuperAdmin
     * @param alertMail
     * @return
     * #MR
     */
    public boolean addUser(HikariDataSource ds,
            String userName, String mail,
            String password, boolean isSuperAdmin,
            boolean alertMail) {
        boolean active = true;
        boolean passtomodify = false;
        
        boolean status = false;
        Statement stmt;
        try {
            Connection conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "Insert into users"
                            + "(username,password,active,mail,"
                            + "passtomodify,alertmail,issuperadmin)"
                            + " values ("
                            + "'" + userName + "'"
                            + ", '" + password + "'"
                            + "," + active
                            + ", '" + mail + "'"
                            + ", " + passtomodify                              
                            + ", " + alertMail 
                            + ", " + isSuperAdmin                           
                            +")";

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
     * Permet de récupérer l'Id de l'utilisateur
     * si l'ulisateur existe ou si le mot de passe est faux, on retourne (-1)
     * sinon, on retourne l'ID de l'utilisateur de retourner l'identifiant
     *
     * @param ds
     * @param login
     * @param pwd
     * @return
     */
    public int getIdUser(HikariDataSource ds, String login, String pwd) {
        int idUser = -1;
        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        try { 
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT id_user FROM users WHERE username ilike '" +
                            login + "' AND password='" + pwd +"'";
                    resultSet = stmt.executeQuery(query);
                    if (resultSet.next()) {
                        idUser = resultSet.getInt("id_user");
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
        return idUser;
    }    
    
    /**
     * cette fonction permet de retourner les informations de l'utilisateur
     *
     * @param ds
     * @param idUser
     * @return
     * #MR
     */
    public NodeUser2 getUser(HikariDataSource ds, int idUser) {
        NodeUser2 nodeUser = null;
        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        try {
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT" +
                                    "  users.id_user," +
                                    "  users.username," +
                                    "  users.active," +
                                    "  users.mail," +
                                    "  users.passtomodify," +
                                    "  users.alertmail," +
                                    "  users.issuperadmin" +
                                    " FROM users" +
                                    " WHERE " +
                                    " users.id_user = " + idUser;
                    resultSet = stmt.executeQuery(query);
                    if(resultSet.next()) {
                        nodeUser = new NodeUser2();
                        nodeUser.setIdUser(idUser);
                        nodeUser.setName(resultSet.getString("username"));
                        nodeUser.setIsActive(resultSet.getBoolean("active"));                        
                        nodeUser.setMail(resultSet.getString("mail"));
                        nodeUser.setIsAlertMail(resultSet.getBoolean("alertmail"));
                        nodeUser.setPasstomodify(resultSet.getBoolean("passtomodify"));
                        nodeUser.setIsSuperAdmin(resultSet.getBoolean("issuperadmin"));
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
        return nodeUser;
    }
    
    /**
     * cette fonction permet de retourner le nom d'un groupe
     * 
     *
     * @param ds
     * @param idGroup
     * @return
     */
    public String getGroupName(
            HikariDataSource ds, int idGroup) {
            
        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        String groupLabel = null;
        try {
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT" +
                                    "  user_group_label.label_group" +
                                    " FROM" +
                                    "  user_group_label" +
                                    " WHERE" +
                                    "  user_group_label.id_group = " + idGroup;
                    resultSet = stmt.executeQuery(query);
                    if(resultSet.next()) {
                        groupLabel = resultSet.getString("label_group");
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
        return groupLabel;
    }    
        
    /**
     * cette fonction permet de retourner la liste des groupes d'un utilisateur
     * 
     *
     * @param ds
     * @param idUser
     * @return
     */
    public Map<String, String> getGroupsOfUser(
            HikariDataSource ds, int idUser) {
        HashMap<String, String> listGroup = new LinkedHashMap();
            
        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        try {
            conn = ds.getConnection();

            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT" +
                                    "  user_group_label.id_group," +
                                    "  user_group_label.label_group" +
                                    " FROM" +
                                    "  user_role_group," +
                                    "  user_group_label" +
                                    " WHERE" +
                                    "  user_role_group.id_group = user_group_label.id_group AND" +
                                    "  user_role_group.id_user = " + idUser + " order by label_group";
                    resultSet = stmt.executeQuery(query);
                    while (resultSet.next()) {
                        listGroup.put(""+resultSet.getInt("id_group"), resultSet.getString("label_group"));
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
        return listGroup;
    }
    
    /**
     * cette fonction permet de retourner tous les groupes existant
     * c'est pour le SuperAdmin
     * 
     *
     * @param ds
     * @return
     */
    public Map<String, String> getAllGroups(
            HikariDataSource ds) {
        HashMap<String, String> sortedHashMap = new LinkedHashMap();
        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        try {
            conn = ds.getConnection();

            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT  user_group_label.id_group,  user_group_label.label_group FROM user_group_label order by label_group";
                    resultSet = stmt.executeQuery(query);
                    while (resultSet.next()) {
                        sortedHashMap.put(""+resultSet.getInt("id_group"), resultSet.getString("label_group"));
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
        return sortedHashMap;
    }
    
    /**
     * permet de retourner la liste des thésaurus pour un groupe pour un affichage IHM
     * @param ds
     * @param idGroup
     * @return 
     */
    public Map <String, String> getThesaurusLabelsOfGroup(HikariDataSource ds, int idGroup) {
        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        Map <String, String> listThesos = null;
        try {
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT distinct" +
                                    "  thesaurus_label.title," +
                                    "  thesaurus_label.id_thesaurus" +
                                    " FROM " +
                                    "  user_group_thesaurus," +
                                    "  thesaurus_label" +
                                    " WHERE " +
                                    "  user_group_thesaurus.id_thesaurus = thesaurus_label.id_thesaurus AND" +
                                    "  user_group_thesaurus.id_group = " + idGroup;
                    resultSet = stmt.executeQuery(query);
                    listThesos = new HashMap<>();
                    while(resultSet.next()) {
                        listThesos.put(resultSet.getString("id_thesaurus"), resultSet.getString("title"));
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
        return listThesos;        
    }
    
    
    /**
     * cette fonction permet de retourner la liste des thésaurus pour un utilisateur
     * l'utilisateur peut faire partie de plusieurs groupes, donc on retourne la liste
     * de tous ces thésaurus dans différents groupes
     *
     * @param ds
     * @param idUser
     * @return
     * #MR
     */
    public List<String> getThesaurusOfUser(HikariDataSource ds, int idUser) {
        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        List<String> nodeUserGroupThesauruses = new ArrayList<>();
        try {
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT distinct" +
                                    " user_group_thesaurus.id_thesaurus" +
                                    " FROM " +
                                    "  user_group_thesaurus," +
                                    "  user_role_group" +
                                    " WHERE " +
                                    "  user_role_group.id_group = user_group_thesaurus.id_group AND" +
                                    "  user_role_group.id_user = " + idUser +
                                    " order by id_thesaurus";
                    resultSet = stmt.executeQuery(query);

                    while(resultSet.next()) {
                        nodeUserGroupThesauruses.add(resultSet.getString("id_thesaurus"));
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
        return nodeUserGroupThesauruses;
    }
    
    /**
     * cette fonction permet de retourner la liste des thésaurus et les groupes correspondants 
     *
     * @param ds
     * @param idLangSource
     * @return
     * #MR
     */
    public ArrayList<NodeUserGroupThesaurus> getAllGroupTheso(HikariDataSource ds, String idLangSource) {
        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        ArrayList<NodeUserGroupThesaurus> nodeUserGroupThesauruses = new ArrayList<>();
        try {
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT " +
                                        "  user_group_label.label_group," +
                                        "  thesaurus_label.title," +
                                        "  user_group_thesaurus.id_thesaurus," +
                                        "  user_group_thesaurus.id_group" +
                                        " FROM " +
                                        "  thesaurus_label, " +
                                        "  user_group_thesaurus, " +
                                        "  user_group_label" +
                                        " WHERE " +
                                        "  user_group_thesaurus.id_group = user_group_label.id_group AND" +
                                        "  user_group_thesaurus.id_thesaurus = thesaurus_label.id_thesaurus AND" +
                                        "  thesaurus_label.lang = '" + idLangSource +"'" +
                                        " ORDER BY LOWER(thesaurus_label.title)";
                    resultSet = stmt.executeQuery(query);

                    while(resultSet.next()) {
                        NodeUserGroupThesaurus nodeUserGroupThesaurus = new NodeUserGroupThesaurus();
                        nodeUserGroupThesaurus.setIdThesaurus(resultSet.getString("id_thesaurus"));
                        nodeUserGroupThesaurus.setThesaurusName(resultSet.getString("title"));
                        nodeUserGroupThesaurus.setIdGroup(resultSet.getInt("id_group"));
                        nodeUserGroupThesaurus.setGroupName(resultSet.getString("label_group"));
                        
                        nodeUserGroupThesauruses.add(nodeUserGroupThesaurus);
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
        return nodeUserGroupThesauruses;
    }
    
    /**
     * cette fonction permet de retourner la liste des thésaurus qui n'apprtiennent à aucun groupe 
     *
     * @param ds
     * @param idLangSource
     * @return
     * #MR
     */
    public ArrayList<NodeUserGroupThesaurus> getAllThesoWithoutGroup(HikariDataSource ds, String idLangSource) {
        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        
        /// fonction prête à intégrer        
        ArrayList<NodeUserGroupThesaurus> nodeUserGroupThesauruses = new ArrayList<>();
        try {
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT thesaurus_label.id_thesaurus, thesaurus_label.title " +
                            " FROM  thesaurus_label" +
                            " WHERE thesaurus_label.lang = '" + idLangSource + "'" +
                            " and thesaurus_label.id_thesaurus not in "
                            + "(select id_thesaurus from  user_group_thesaurus)"
                            + " ORDER BY LOWER(thesaurus_label.title)";
                    resultSet = stmt.executeQuery(query);

                    while(resultSet.next()) {
                        NodeUserGroupThesaurus nodeUserGroupThesaurus = new NodeUserGroupThesaurus();
                        nodeUserGroupThesaurus.setIdThesaurus(resultSet.getString("id_thesaurus"));
                        nodeUserGroupThesaurus.setThesaurusName(resultSet.getString("title"));
                        nodeUserGroupThesaurus.setIdGroup(-1);
                        nodeUserGroupThesaurus.setGroupName("");
                        
                        nodeUserGroupThesauruses.add(nodeUserGroupThesaurus);
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
        return nodeUserGroupThesauruses;
    }      
    
    /**
     * permet de créer un groupe ou projet pour regrouper les utilisateurs et les thésaurus
     * @param ds
     * @param userGroupName
     * @return 
     */
    public boolean createUserGroup(HikariDataSource ds,
            String userGroupName) {
        Statement stmt;
        boolean status = false;
        try {
            Connection conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "Insert into user_group_label"
                            + "(label_group)"
                            + " values ('"
                            + userGroupName + "')";

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
     * permet de savoir si le groupe ou projet existe déja  
     * @param ds
     * @param userGroupName
     * @return 
     */
    public boolean isUserGroupExist(HikariDataSource ds, String userGroupName) {
        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        boolean existe = false;
        try {
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT id_group FROM user_group_label WHERE label_group ilike '" + userGroupName + "'";
                    resultSet = stmt.executeQuery(query);
                    if (resultSet.next()) {
                        existe = true;
                    }
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }

        } catch (SQLException ex) {
            Logger.getLogger(UserHelper2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return existe;
    }
    
    /**
     * pemret de mettre à jour le role d'un utilisateur et quelques préférences
     * module de modification de l'utilisateur
     * @param conn
     * @param idUser
     * @param idRole
     * @param isIsActive
     * @param isIsAlertMail
     * @param isSuperAdmin
     * @return 
     */
    public boolean updateUser(Connection conn, 
                    int idUser,
                    int idRole,
                    boolean isIsActive,
                    boolean isIsAlertMail,
                    boolean isSuperAdmin
                    ){
        Statement stmt;
        boolean status = false;
        try {
            try {
                stmt = conn.createStatement();
                try {
                    String query = "UPDATE users set alertmail = " + isIsAlertMail +
                            ", active = " + isIsActive + 
                            ", issuperadmin = " + isSuperAdmin +
                            " WHERE id_user = " + idUser;
                    stmt.executeUpdate(query);
                    if(!isSuperAdmin) {
                        query = "UPDATE user_role_group set id_role = " + idRole +
                                " WHERE id_user = " + idUser;
                        stmt.executeUpdate(query);
                    }                  
                    status = true;
                } finally {
                    stmt.close();
                }
            } finally {
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserHelper2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return status;
    }
    
    public boolean setIsSuperAdmin(Connection conn, 
                    int idUser, boolean isSuperAdmin) {
        Statement stmt;
        boolean status = false;
        try {
            try {
                stmt = conn.createStatement();
                try {
                    String query = "UPDATE users set " +
                            " issuperadmin = " + isSuperAdmin +
                            " WHERE id_user = " + idUser;
                    stmt.executeUpdate(query);
                    status = true;
                } finally {
                    stmt.close();
                }
            } finally {
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserHelper2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return status;        
    }
    
    /**
     * permet de supprimer les roles d'un utilisateur sur les groupes
     * cas où l'utilisateur passe en superAdmin, plus besoin de rôles
     * @param conn
     * @param idUser
     * @return 
     */
    public boolean deleteRolesOfUser(Connection conn, int idUser) {
        Statement stmt;
        boolean status = false;

        try {
            try {
                stmt = conn.createStatement();
                try {
                    String query = "delete from user_role_group where"
                            + " id_user =" + idUser;
                    stmt.executeUpdate(query);
                    status = true;
                } finally {
                    stmt.close();
                }
            } finally {
            }
        } catch (SQLException sqle) {
            Logger.getLogger(UserHelper2.class.getName()).log(Level.SEVERE, null, sqle);
        }
        return status;
    }
    
    /**
     * cette fonction permet de retourner les utilisateurs avec role SuperAdmin
     * 
     *
     * @param ds
     * @return
     */
    public ArrayList<NodeUserRole> getListOfSuperAdmin(
                HikariDataSource ds){

        ArrayList<NodeUserRole> nodeUserRoles = null;
            
        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        try {
            conn = ds.getConnection();

            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT " +
                                    "  users.id_user," +
                                    "  users.username," +
                                    " users.active" +
                                    " FROM " +
                                    "  users" +
                                    " WHERE" +
                                    "  users.issuperadmin = true" +
                                    " ORDER BY" +
                                    "  LOWER(users.username)";
                    resultSet = stmt.executeQuery(query);
                    nodeUserRoles = new ArrayList<>();
                    while(resultSet.next()) {
                        NodeUserRole nodeUserRole = new NodeUserRole();
                        nodeUserRole.setIdUser(resultSet.getInt("id_user"));
                        nodeUserRole.setUserName(resultSet.getString("username"));
                        nodeUserRole.setIdRole(1);
                        nodeUserRole.setRoleName("SuperAdmin");
                        nodeUserRole.setIsActive(resultSet.getBoolean("active"));
                        nodeUserRoles.add(nodeUserRole);
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
        return nodeUserRoles;
    }    
    
    /**
     * pemret de modifier le role de l'utilisateur sur un groupe
     * @param ds
     * @param idUser
     * @param idRole
     * @param idGroup
     * @return 
     */
    public boolean updateUserRoleOnGroup(HikariDataSource ds,
            int idUser, int idRole, int idGroup){
        boolean status = false;
        boolean isSuperAdmin = false;
        
        if(idRole == 1) 
            isSuperAdmin = true;
        
        try {
            Connection conn = ds.getConnection();
            conn.setAutoCommit(false);
            
            /// si le role devient SuperAdmin, alors on supprime les roles sur les groupes
            if(isSuperAdmin) {
                if(!deleteRoleOnGroup(conn, idUser, idGroup)) {
                    conn.rollback();
                    conn.close();
                    return false;
                }
            } else {
                if(!updateUserRoleOnGroupRollBack(conn,
                            idUser, idRole, idGroup)) {
                    conn.rollback();
                    conn.close();
                    return false;
                }
            }
            if(!setIsSuperAdmin(conn, idUser, isSuperAdmin)) {
                conn.rollback();
                conn.close();
                return false;
            }            
            conn.commit();
            conn.close();
            status = true;
        } catch (SQLException ex) {
            Logger.getLogger(UserHelper2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return status;        
    }
    
    private boolean updateUserRoleOnGroupRollBack(Connection conn,
            int idUser, int idRole, int idGroup){
        Statement stmt;
        boolean status = false;
        try {
            try {
                stmt = conn.createStatement();
                try {
                    String query = "UPDATE user_role_group set id_role = " +
                            idRole +
                            " WHERE id_user = " + idUser +
                            " and id_group = " + idGroup;
                    stmt.executeUpdate(query);
                    status = true;
                } finally {
                    stmt.close();
                }
            } finally {
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserHelper2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return status;
    }    
    
    
    /**
     * permet de modifier le role de l'utilisateur sur un groupe 
     * si l'utilisateur change de role (SuperAdmin -> en Admin, on  met 
     * à jour son status dans la table Users (isSuperadmin = false)
     * 
     * @param ds
     * @param idUser
     * @param idRole
     * @param idGroup
     * @return 
     */
    public boolean addUserRoleOnGroup(HikariDataSource ds,
            int idUser, int idRole, int idGroup){
        boolean status = false;
        boolean isSuperAdmin = false;
        
        if(idRole == 1) 
            isSuperAdmin = true;
        try {
            Connection conn = ds.getConnection();
            conn.setAutoCommit(false);
            
            /// si le role devient SuperAdmin, alors on supprime les roles sur les groupes
            if(isSuperAdmin) {
                if(!deleteRoleOnGroup(conn, idUser, idGroup)) {
                    conn.rollback();
                    conn.close();
                    return false;
                }
            } else {
                if(!addUserRoleOnGroupRollBack(conn,
                            idUser, idRole, idGroup)) {
                    conn.rollback();
                    conn.close();
                    return false;
                }
            }
            if(!setIsSuperAdmin(conn, idUser, isSuperAdmin)) {
                conn.rollback();
                conn.close();
                return false;
            }            
            conn.commit();
            conn.close();
            status = true;
        } catch (SQLException ex) {
            Logger.getLogger(UserHelper2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return status;
    }
    
    private boolean addUserRoleOnGroupRollBack(Connection conn,
            int idUser, int idRole, int idGroup){
        Statement stmt;
        boolean status = false;
        try {
            try {
                stmt = conn.createStatement();
                try {
                    String query = "insert into user_role_group (id_user, id_role, id_group)" +
                            " values(" +
                            idUser + "," +
                            idRole + "," +
                            idGroup + ")";
                    stmt.executeUpdate(query);
                    status = true;
                } finally {
                    stmt.close();
                }
            } finally {
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserHelper2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return status;
    }
    
    /**
     * Permet de passer un thésaurus d'un groupe à un autre
     * @param ds
     * @param idTheso
     * @param oldGroup
     * @param newGroup
     * @return 
     * #MR
     */
    public boolean moveThesoToGroup(HikariDataSource ds,
            String idTheso, int oldGroup, int newGroup){
        boolean status = false;
        try {
            Connection conn = ds.getConnection();
            conn.setAutoCommit(false);
            
            if(!deleteThesoFromGroup(conn, idTheso/*, oldGroup*/)) {
                conn.rollback();
                conn.close();
                return false;                
            }
            if(!moveThesoToGroupRollBack(conn, idTheso, newGroup)) {
                conn.rollback();
                conn.close();
                return false;
            }
            conn.commit();
            conn.close();
            status = true;
        } catch (SQLException ex) {
            Logger.getLogger(UserHelper2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return status;
    }    
    private boolean moveThesoToGroupRollBack(Connection conn,
            String idTheso, int newGroup){
        Statement stmt;
        boolean status = false;
        try {
            try {
                stmt = conn.createStatement();
                try {
                    String query = "insert into user_group_thesaurus(id_group, id_thesaurus)" +
                            " values('" +
                            newGroup + "'," +
                            idTheso + ")";
                    stmt.executeUpdate(query);
                    status = true;
                } finally {
                    stmt.close();
                }
            } finally {
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserHelper2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return status;        
    }
    
    /**
     * permet de supprimer l'appartenance d'un thesaurus à un groupe/projet
     * @param conn
     * @param idTheso
     * @return 
     * #MR
     */
    public boolean deleteThesoFromGroup(Connection conn, 
            String idTheso) {
        Statement stmt;
        boolean status = false;
        try {
            try {
                stmt = conn.createStatement();
                try {
                    String query = "delete from user_group_thesaurus where"
                            + " id_thesaurus ='" + idTheso + "'";
                    stmt.executeUpdate(query);
                    status = true;                    
                } finally {
                    stmt.close();
                }
            } finally {
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return status;        
    }
    
        
     /**
     * permet d'ajouter un thesaurus à un groupe/projet 
     * @param conn
     * @param idTheso
     * @param idGroup
     * @return 
     * #MR
     */
    public boolean addThesoToGroup(Connection conn, 
            String idTheso, int idGroup) {
        Statement stmt;
        boolean status = false;
        try {
            try {
                stmt = conn.createStatement();
                try {
                    String query = "insert into user_group_thesaurus"
                            + " (id_group, id_thesaurus) values ("
                            + idGroup + ",'" + idTheso + "')";
                    stmt.executeUpdate(query);
                    status = true;                    
                } finally {
                    stmt.close();
                }
            } finally {
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return status;
    }
       
    
    
    
    
    
    
    
    
    
    
    
    
    
    /**
     * permet de supprimer cet utilisateur 
     * @param ds
     * @param idUser 
     * @return  
     */
    public boolean deleteUser(HikariDataSource ds, int idUser) {
        Connection conn;
        Statement stmt;
        boolean status = false;
        try {
            conn = ds.getConnection();
            conn.setAutoCommit(false);
            try {
                stmt = conn.createStatement();
                try {
                    String query = "delete from users where"
                            + " id_user =" + idUser;
                    stmt.executeUpdate(query);
                    query = "delete from user_role_group where"
                            + " id_user = "+idUser;
                    stmt.executeUpdate(query);
                    query = "update users_historique "
                            + " set delete = '" + new ToolsHelper().getDate()
                            + " ' where id_user = '"+idUser+"'";
                    stmt.executeUpdate(query);
                    status = true;
                    conn.commit();
                    conn.close();
                } finally {
                    stmt.close();
                }
            } finally {
                if(status == false) {
                    conn.rollback();
                }
                conn.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return status;
    }
    


    
    /**
     * cette fonction permet de retourner la liste des groupes - roles
     * pour un un utilisateur
     *
     * @param ds
     * @param idUser
     * @return
     * #MR
     */
    public ArrayList<NodeUserRoleGroup> getUserRoleGroup(HikariDataSource ds, int idUser) {
        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        ArrayList<NodeUserRoleGroup> nodeUserRoleGroups = new ArrayList<>();
        try {
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT " +
                                    "  user_role_group.id_role," +
                                    "  roles.name," +
                                    "  user_role_group.id_group," +
                                    "  user_group_label.label_group" +
                                    " FROM user_role_group, roles, user_group_label" +
                                    " WHERE" +
                                    "  user_role_group.id_role = roles.id AND" +
                                    "  user_group_label.id_group = user_role_group.id_group AND" +
                                    "  user_role_group.id_user =" + idUser ;
                    resultSet = stmt.executeQuery(query);
                    while(resultSet.next()) {
                        NodeUserRoleGroup nodeUserRoleGroup = new NodeUserRoleGroup();
                        nodeUserRoleGroup.setIdRole(resultSet.getInt("id_role"));
                        nodeUserRoleGroup.setRoleName(resultSet.getString("name"));
                        nodeUserRoleGroup.setIdGroup(resultSet.getInt("id_group"));
                        nodeUserRoleGroup.setGroupName(resultSet.getString("label_group"));
                     //   nodeUserRoleGroup.setNodeUserGroupThesauruses(getUserThesaurusOfGroup(ds, resultSet.getInt("id_group")));
                        
                        if(resultSet.getInt("id_role") == 2)
                            nodeUserRoleGroup.setIsAdmin(true);
                        if(resultSet.getInt("id_role") == 3)
                            nodeUserRoleGroup.setIsManager(true);
                        if(resultSet.getInt("id_role") == 4)
                            nodeUserRoleGroup.setIsContributor(true);
                        
                        nodeUserRoleGroups.add(nodeUserRoleGroup);
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
        return nodeUserRoleGroups;
    } 
    

    
    /**
     * permet de retourner le groupe à lequel le thesaurus appartient
     * @param ds
     * @param idTheso
     * @return 
     */
    public int getGroupOfThisTheso(HikariDataSource ds, String idTheso) {
        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        int idGroup = -1;
        try {
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT user_group_thesaurus.id_group" +
                                    " FROM user_group_thesaurus" +
                                    " WHERE" +
                                    " user_group_thesaurus.id_thesaurus = '" + idTheso +"'";
                    resultSet = stmt.executeQuery(query);
                    if(resultSet.next()) {
                        idGroup = resultSet.getInt("id_group");
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
        return idGroup;        
    }
           
    /**
     * cette fonction permet de retourner la liste des utilisateurs qui
     * n'appartiennent à aucun groupe donc aucun role défini
     * 
     * @param ds
     * @return
     */
    public ArrayList<NodeUserRole> getUsersWithoutGroup(HikariDataSource ds) {
        
        ArrayList<NodeUserRole> listUser = new ArrayList<>();
            
        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        try {
            conn = ds.getConnection();

            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT users.id_user, users.username, users.active"
                            + " FROM users where users.issuperadmin!=true and users.id_user not in "
                            + "(select distinct id_user from user_role_group order by id_user)"
                            + " ORDER BY LOWER(users.username)";
                    resultSet = stmt.executeQuery(query);
                    while (resultSet.next()) {
                        NodeUserRole nodeUserRole = new NodeUserRole();
                        nodeUserRole.setIdUser(resultSet.getInt("id_user"));
                        nodeUserRole.setUserName(resultSet.getString("username"));
                        nodeUserRole.setIsActive(resultSet.getBoolean("active"));                        
                        nodeUserRole.setIdRole(-1);
                        nodeUserRole.setRoleName("");
                        listUser.add(nodeUserRole);
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
        return listUser;
    }
    
    /**
     * cette fonction permet de retourner un objet vide de type NodeUserRoleGroup pour un utilisateur
     * qui n'a aucun role encore sur aucun groupe
     * 
     *
     * @param ds
     * @param idUser
     * @return
     */
    public NodeUserRoleGroup getUserRoleWithoutGroup(
                HikariDataSource ds){

     //   NodeUserRoleGroup nodeUserRoleGroup = null;
        NodeUserRoleGroup nodeUserRoleGroup = new NodeUserRoleGroup();
        nodeUserRoleGroup.setIdRole(-1);
        nodeUserRoleGroup.setRoleName("");
        nodeUserRoleGroup.setGroupName("");                        
        nodeUserRoleGroup.setIdGroup(-1);
        nodeUserRoleGroup.setIsAdmin(false);
        nodeUserRoleGroup.setIsManager(false);
        nodeUserRoleGroup.setIsContributor(false);
           
        
        /*
        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        try {
            conn = ds.getConnection();

            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT \n" +
                                    "  roles.id, \n" +
                                    "  roles.name, \n" +
                                    "  user_group_label.label_group, \n" +
                                    "  user_group_label.id_group\n" +
                                    " FROM \n" +
                                    "  user_role_group, \n" +
                                    "  roles, \n" +
                                    "  user_group_label\n" +
                                    " WHERE \n" +
                                    "  user_role_group.id_role = roles.id AND\n" +
                                    "  user_group_label.id_group = user_role_group.id_group AND\n" +
                                    "  user_role_group.id_user = " + idUser;
                    resultSet = stmt.executeQuery(query);

                    if(resultSet.next()) {
                        nodeUserRoleGroup = new NodeUserRoleGroup();
                        nodeUserRoleGroup.setIdRole(resultSet.getInt("id"));
                        nodeUserRoleGroup.setRoleName(resultSet.getString("name"));
                        nodeUserRoleGroup.setGroupName(resultSet.getString("label_group"));                        
                        nodeUserRoleGroup.setIdGroup(resultSet.getInt("id_group"));
                        if(resultSet.getInt("id") == 2) 
                            nodeUserRoleGroup.setIsAdmin(true);
                        if(resultSet.getInt("id") == 3) 
                            nodeUserRoleGroup.setIsManager(true);
                        if(resultSet.getInt("id") == 4) 
                            nodeUserRoleGroup.setIsContributor(true);                        
                    }
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserHelper.class.getName()).log(Level.SEVERE, null, ex);
        }*/
        return nodeUserRoleGroup;
    }      
   

    
    /**
     * cette fonction permet de retourner la liste des utilisateurs pour un groupe
     * avec un role égale ou inférieur au role de l'utilisateur en cours 
     * 
     *
     * @param ds
     * @param idGroup
     * @param idRole
     * @return
     */
    public ArrayList<NodeUserRole> getUsersRolesByGroup(HikariDataSource ds,
           int idGroup, int idRole) {
        
        ArrayList<NodeUserRole> listUser = new ArrayList<>();
            
        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        try {
            conn = ds.getConnection();

            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT" +
                                    "  users.id_user," +
                                    "  users.username," +
                                    "  users.active," +
                                    "  roles.name," +
                                    "  roles.id" +
                                    " FROM " +
                                    "  user_role_group," +
                                    "  users," +
                                    "  roles" +
                                    " WHERE" +
                                    "  user_role_group.id_role = roles.id AND" +
                                    "  users.id_user = user_role_group.id_user AND" +
                                    "  user_role_group.id_group =" + idGroup + 
                                    " and users.issuperadmin != " + true +
                                    " and user_role_group.id_role >= " + idRole +
                                    " ORDER BY LOWER(users.username)";
                    resultSet = stmt.executeQuery(query);
                    while (resultSet.next()) {
                        NodeUserRole nodeUserRole = new NodeUserRole();
                        nodeUserRole.setIdUser(resultSet.getInt("id_user"));
                        nodeUserRole.setUserName(resultSet.getString("username"));
                        nodeUserRole.setIsActive(resultSet.getBoolean("active"));                        
                        nodeUserRole.setIdRole(resultSet.getInt("id"));
                        nodeUserRole.setRoleName(resultSet.getString("name"));
                        listUser.add(nodeUserRole);
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
        return listUser;
    }
    
    /**
     * cette fonction permet de retourner le role de l'utilisateur sur ce groupe
     * 
     *
     * @param ds
     * @param idUser
     * @param idGroup
     * @return
     */
    public NodeUserRoleGroup getUserRoleOnThisGroup(
                HikariDataSource ds, int idUser, int idGroup){

        NodeUserRoleGroup nodeUserRoleGroup = null;
            
        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        try {
            conn = ds.getConnection();

            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT \n" +
                                    "  roles.id, \n" +
                                    "  roles.name, \n" +
                                    "  user_group_label.label_group, \n" +
                                    "  user_group_label.id_group\n" +
                                    " FROM \n" +
                                    "  user_role_group, \n" +
                                    "  roles, \n" +
                                    "  user_group_label\n" +
                                    " WHERE \n" +
                                    "  user_role_group.id_role = roles.id AND\n" +
                                    "  user_group_label.id_group = user_role_group.id_group AND\n" +
                                    "  user_role_group.id_user = " + idUser + " AND \n" +
                                    "  user_role_group.id_group = " + idGroup;
                    resultSet = stmt.executeQuery(query);

                    if(resultSet.next()) {
                        nodeUserRoleGroup = new NodeUserRoleGroup();
                        nodeUserRoleGroup.setIdRole(resultSet.getInt("id"));
                        nodeUserRoleGroup.setRoleName(resultSet.getString("name"));
                        nodeUserRoleGroup.setGroupName(resultSet.getString("label_group"));                        
                        nodeUserRoleGroup.setIdGroup(resultSet.getInt("id_group"));
                        if(resultSet.getInt("id") == 2) 
                            nodeUserRoleGroup.setIsAdmin(true);
                        if(resultSet.getInt("id") == 3) 
                            nodeUserRoleGroup.setIsManager(true);
                        if(resultSet.getInt("id") == 4) 
                            nodeUserRoleGroup.setIsContributor(true);                        
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
        return nodeUserRoleGroup;
    }    
    
    /**
     * cette fonction permet de retourner le role du superAdmin 
     * 
     *
     * @param ds
     * @return
     */
    public NodeUserRoleGroup getUserRoleForSuperAdmin(
                HikariDataSource ds){

        NodeUserRoleGroup nodeUserRoleGroup = null;
            
        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        try {
            conn = ds.getConnection();

            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT " +
                                    "  roles.id, " +
                                    "  roles.name " +
                                    " FROM " +
                                    "  public.roles " +
                                    " WHERE " +
                                    "  roles.id = 1";
                    resultSet = stmt.executeQuery(query);

                    if(resultSet.next()) {
                        nodeUserRoleGroup = new NodeUserRoleGroup();
                        nodeUserRoleGroup.setIdRole(resultSet.getInt("id"));
                        nodeUserRoleGroup.setRoleName(resultSet.getString("name"));
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
        return nodeUserRoleGroup;
    }    
    
    /**
     * permet de supprimer le role d'un utilisateur sur ce groupe
     * @param conn
     * @param idUser
     * @param idGroup
     * @return 
     */
    public boolean deleteRoleOnGroup(Connection conn,
            int idUser, int idGroup) {
        Statement stmt;
        boolean status = false;

        try {
            try {
                stmt = conn.createStatement();
                try {
                    String query = "delete from user_role_group where"
                            + " id_user =" + idUser +
                            " and id_group = " + idGroup;
                    stmt.executeUpdate(query);
                    status = true;
                } finally {
                    stmt.close();
                }
            } finally {
            }
        } catch (SQLException sqle) {
            Logger.getLogger(UserHelper2.class.getName()).log(Level.SEVERE, null, sqle);
        }
        return status;        
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    /**
     * cette fonction permet de retourner le role de l'utilisateur sur ce groupe
     * 
     *
     * @param ds
     * @param idUser
     * @param idGroup
     * @return
     */
    public NodeUserRoleGroup getUserRoleOnThisGroupForSuperAdmin(
                HikariDataSource ds, int idUser, int idGroup){

        NodeUserRoleGroup nodeUserRoleGroup = null;
            
        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        try {
            conn = ds.getConnection();

            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT \n" +
                                    "  roles.id, \n" +
                                    "  roles.name, \n" +
                                    "  user_group_label.id_group, \n" +
                                    "  user_group_label.label_group\n" +
                                    " FROM \n" +
                                    "  public.roles, \n" +
                                    "  public.user_group_label\n" +
                                    " WHERE \n" +
                                    "  roles.id = "+ idUser + " AND \n" +
                                    "  user_group_label.id_group = " + idGroup;
                    resultSet = stmt.executeQuery(query);

                    if(resultSet.next()) {
                        nodeUserRoleGroup = new NodeUserRoleGroup();
                        nodeUserRoleGroup.setIdRole(resultSet.getInt("id"));
                        nodeUserRoleGroup.setRoleName(resultSet.getString("name"));
                        nodeUserRoleGroup.setGroupName(resultSet.getString("label_group"));                        
                        nodeUserRoleGroup.setIdGroup(resultSet.getInt("id_group"));
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
        return nodeUserRoleGroup;
    }
    
    
    
    
    
    
    
    
    
    

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

    public void updateMail(HikariDataSource ds, int idUser, String newMail) {
        Connection conn;
        Statement stmt;
        try {
            // Get connection from pool
            conn = ds.getConnection();

            try {
                stmt = conn.createStatement();
                try {
                    String query = "UPDATE users set mail = '" + newMail
                            + "' WHERE id_user = " + idUser;
                    stmt.executeUpdate(query);
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserHelper2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * permet de mette à jour le status des alertes mail pour l'utilisateur
     * @param conn
     * @param idUser
     * @param alertMail 
     * @return 
     * #MR
     */
    public boolean setAlertMailForUser(Connection conn, int idUser, boolean alertMail) {
        Statement stmt;
        boolean status = false;
        try {
            try {
                stmt = conn.createStatement();
                try {
                    String query = "UPDATE users set alertmail = " + alertMail
                            + " WHERE id_user = " + idUser;
                    stmt.executeUpdate(query);
                    status = true;
                } finally {
                    stmt.close();
                }
            } finally {
               // conn.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserHelper2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return status;
    }    
    
    
    /**
     * Cette fonction permet de savoir si le Pseudo existe
     *
     * @param ds
     * @param pseudo
     * @return
     */
    public boolean isPseudoExist(HikariDataSource ds, String pseudo) {
        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        boolean existe = false;
        try {
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT id_user FROM users WHERE username='" + pseudo + "'";
                    resultSet = stmt.executeQuery(query);
                    //resultSet.first();
                    //resultSet.next();
                    if (resultSet.next()) {
                        existe = true;
                    }
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }

        } catch (SQLException ex) {
            Logger.getLogger(UserHelper2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return existe;
    }
    
    public boolean isMailExist(HikariDataSource ds, String mail) {
        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        try {
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT mail FROM users WHERE mail='" + mail + "'";
                    resultSet = stmt.executeQuery(query);
                    resultSet.next();
                    if (resultSet.getRow() != 0) {
                        return true;
                    }
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }

        } catch (SQLException ex) {
            Logger.getLogger(UserHelper2.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    } 
    
    /**
     * Permet de savoir si l'utilisateur a un droit d'admin sur le groupe
     * @param ds
     * @param idUser
     * @param idGroup
     * @return 
     */
    public boolean isAdminOnThisGroup(HikariDataSource ds,
            int idUser, int idGroup) {
        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        try {
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT " +
                                    "  user_role_group.id_user" +
                                    " FROM " +
                                    "  user_role_group" +
                                    " WHERE " +
                                    "  user_role_group.id_group = " + idGroup +
                                    " AND user_role_group.id_user = " + idUser + 
                                    " AND user_role_group.id_role < 3";
                    resultSet = stmt.executeQuery(query);
                    resultSet.next();
                    if (resultSet.getRow() != 0) {
                        return true;
                    }
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }

        } catch (SQLException ex) {
            Logger.getLogger(UserHelper2.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;        
    }
    
    /**
     * Cette fonction permet de retourner la liste des roles autorisés pour un
     * utilisateur (c'est la liste qu'un utilisateur a le droit d'attribué à un
     * nouvel utilisateur)
     *
     * @param ds
     * @param idRoleFrom
     * @return
     */
    public ArrayList<Map.Entry<String, String>> getAuthorizedRoles(HikariDataSource ds,
            int idRoleFrom) {
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
                    String query = "select id, name from roles "
                            + " where id >= " + idRoleFrom;

                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    if (resultSet != null) {
                        while (resultSet.next()) {
                            map.put(resultSet.getString("id"), resultSet.getString("name"));
                        }
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
        ArrayList<Map.Entry<String, String>> listeRoles = new ArrayList<>(map.entrySet());
        return listeRoles;
    }    
    
    
    
    
 ////////////////////////////////////////////////////////////////////
 ////////////////////////////////////////////////////////////////////
 //////// fin des nouvelles fontions ////////////////////////////////
 ////////////////////////////////////////////////////////////////////
 ////////////////////////////////////////////////////////////////////    
    
 
    
}
