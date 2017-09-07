/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package export;

import com.zaxxer.hikari.HikariDataSource;
import connexion.ConnexionTest;
import java.util.ArrayList;
import mom.trd.opentheso.bdd.helper.CandidateHelper;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author miled.rousset
 */
public class ExportCandidats {

    public ExportCandidats() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void exportCandidatsToCSV() {
        ConnexionTest connexionTest = new ConnexionTest();
        HikariDataSource conn = connexionTest.getConnexionPool();

        String idTheso = "TH_1";
        String idLang = "fr";

        ArrayList<String> tabIdCandidats;
        boolean passed = false;
        StringBuilder file = new StringBuilder();
        
        CandidateHelper candidateHelper = new CandidateHelper();
        tabIdCandidats = candidateHelper.getAllCandidatId(conn, idTheso);
        
        if(tabIdCandidats != null){
            if(!tabIdCandidats.isEmpty()) {
                file.append("id_candidat");
                file.append("\t");
                file.append("titre");
                file.append("\t");
                file.append("langue");
                file.append("\t");
                file.append("notes contributeurs");
                file.append("\t");
                file.append("status");
                file.append("\t");
                file.append("message de l'administrateur");
                file.append("\t");
                file.append("id_concept");
                file.append("\t");                
                file.append("date création");
                file.append("\t");
                file.append("date modification");
                file.append("\n");
                for (String tabIdCandidat : tabIdCandidats) {
                    
                    file.append(tabIdCandidat);
                    file.append("\t");
                    file.append(" ");
                    file.append("\t");
                    file.append(" ");
                    file.append("\t");
                    file.append(" ");
                    file.append("\t");
                    file.append(" ");
                    file.append("\t");
                    file.append(" ");
                    file.append("\t");
                    file.append(" ");
                    file.append("\t");
                    file.append(" ");
                    file.append("\t");
                    file.append(" ");
                    file.append("\n");                    
                }
            }
        }
        System.out.println(file.toString());
        conn.close();
    }
}
