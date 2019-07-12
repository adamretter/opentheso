/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.core.exports.helper;

import com.zaxxer.hikari.HikariDataSource;
import java.util.ArrayList;
import java.util.List;
import mom.trd.opentheso.bdd.datas.Concept;
import mom.trd.opentheso.bdd.datas.Term;
import mom.trd.opentheso.bdd.helper.AlignmentHelper;
import mom.trd.opentheso.bdd.helper.ConceptHelper;
import mom.trd.opentheso.bdd.helper.NoteHelper;
import mom.trd.opentheso.bdd.helper.TermHelper;
import mom.trd.opentheso.bdd.helper.nodes.NodeAlignmentSmall;
import mom.trd.opentheso.bdd.helper.nodes.NodePreference;
import mom.trd.opentheso.bdd.helper.nodes.group.NodeGroup;
import mom.trd.opentheso.bdd.helper.nodes.notes.NodeNote;

/**
 *
 * @author Miled
 */
public class WriteIdentifier {

    private StringBuilder stringBuilder;
    private StringBuffer message;
    private int countTreated;
    private int totalCount;

    public WriteIdentifier() {
        stringBuilder = new StringBuilder();
        message = new StringBuffer();
        countTreated = 0;
        totalCount = 0;
    }

    public void setHeader() {
        stringBuilder.append("URI");
        stringBuilder.append("\t");
        stringBuilder.append("conceptId");
        stringBuilder.append("\t");
        stringBuilder.append("arkId");
        stringBuilder.append("\t");
        stringBuilder.append("handleId");
        stringBuilder.append("\t");
        stringBuilder.append("prefLabel");
        stringBuilder.append("\t");
//        stringBuilder.append("altLabel");
//        stringBuilder.append("\t");
        stringBuilder.append("definition");
        stringBuilder.append("\t");
        stringBuilder.append("alignment");
        stringBuilder.append("\n");
    }

    /**
     * permet d'ajouter une ligne CSV d'informations d'un concept
     *
     * @param ds
     * @param idTheso
     * @param idLang
     * @param selectedGroups
     * @param nodePreference
     */
    public void AppendConcept(HikariDataSource ds,
            String idTheso, String idLang,
            List<NodeGroup> selectedGroups,
            NodePreference nodePreference) {

        ArrayList<String> listIdConcept;
        ConceptHelper conceptHelper = new ConceptHelper();
        TermHelper termHelper = new TermHelper();
        NoteHelper noteHelper = new NoteHelper();
        AlignmentHelper alignmentHelper = new AlignmentHelper();

        Concept concept;
        Term term;
        String idTerme;
        ArrayList<NodeNote> nodeNote;
        ArrayList<NodeAlignmentSmall> nodeAlignments;
        boolean passed = false;
        boolean notePassed = false;
        String note;

        for (NodeGroup selectedGroup : selectedGroups) {
            listIdConcept = conceptHelper.getAllIdConceptOfThesaurusByGroup(ds, idTheso, selectedGroup.getConceptGroup().getIdgroup());
            totalCount = totalCount + listIdConcept.size();
        }
        for (NodeGroup selectedGroup : selectedGroups) {
            listIdConcept = conceptHelper.getAllIdConceptOfThesaurusByGroup(ds, idTheso, selectedGroup.getConceptGroup().getIdgroup());
            for (String idConcept : listIdConcept) {
                concept = conceptHelper.getThisConcept(ds, idConcept, idTheso);
                if (concept == null) {
                    message.append("concept null :");
                    message.append(idConcept);
                    message.append("\n");
                } else {
                    // Uri
                    stringBuilder.append(getInternalUri(
                            nodePreference.getCheminSite(), idConcept, idTheso));
                    stringBuilder.append("\t");

                    // Identifiants
                    stringBuilder.append(concept.getIdConcept());
                    stringBuilder.append("\t");

                    stringBuilder.append(concept.getIdArk());
                    stringBuilder.append("\t");

                    stringBuilder.append(concept.getIdHandle());
                    stringBuilder.append("\t");

                    // Label
                    term = termHelper.getThisTerm(ds, idConcept, idTheso, idLang);
                    if (term != null) {
                        note = term.getLexical_value().replace('\r', ' ');
                        note = note.replace('\n', ' ');
                        note = note.replace('\t', ' ');
                        note = note.replace('\"', ' ');
                        stringBuilder.append(note);
                        stringBuilder.append("\t");
                    } else {
                        message.append("terme null :");
                        message.append(idConcept);
                        message.append("\n");                  
                        stringBuilder.append(" ");
                        stringBuilder.append("\t");
                    }

                    // Definition
                    idTerme = termHelper.getIdTermOfConcept(ds, idConcept, idTheso);
                    if(idTerme == null) {
                        message.append("idTerm null pour le concept :");
                        message.append(idConcept);
                        message.append("\n");
                    }
                    nodeNote = noteHelper.getListNotesTerm(ds, idTerme, idTheso, idLang);
                    if (nodeNote != null) {
                        for (NodeNote nodeNote1 : nodeNote) {
                            if (nodeNote1.getNotetypecode().equalsIgnoreCase("definition")) {
                                note = nodeNote1.getLexicalvalue().replace('\r', ' ');
                                note = note.replace('\n', ' ');
                                note = note.replace('\t', ' ');
                                note = note.replace('\"', ' ');
                                if (notePassed) {
                                    stringBuilder.append(" ## ");
                                }
                                stringBuilder.append(note);
                                passed = true;
                                notePassed = true;
                            }
                        }
                    }
                    if (!passed) {
                        stringBuilder.append(" ");                    
                        stringBuilder.append("\t");
                    } else {
                        stringBuilder.append("\t");
                    } 
                    passed = false;
                    notePassed = false;
                    // alignements
                    nodeAlignments = alignmentHelper.getAllAlignmentOfConceptNew(ds, idConcept, idTheso);
                    if (nodeAlignments != null) {
                        for (NodeAlignmentSmall nodeAlignment : nodeAlignments) {
                            note = nodeAlignment.getUri_target().replace('\r', ' ');
                            note = note.replace('\n', ' ');
                            note = note.replace('\t', ' ');
                            note = note.replace('\"', ' ');
                            if (notePassed) {
                                stringBuilder.append(" ## ");
                            }
                            stringBuilder.append(note);
                            passed = true;
                            notePassed = true;
                        }
                    }
                    if (!passed) {
                        stringBuilder.append(" ");                    
                    }
                    passed = false;
                    notePassed = false;
                    stringBuilder.append("\n");
                    countTreated = countTreated + 1;
                }
            }
        }
        message.append("total général = ");
        message.append(totalCount);
        stringBuilder.append("\n");
        message.append("total traité = ");
        message.append(countTreated);        
        System.out.println("message = " + message.toString());
    }
    

    private String getInternalUri(String urlPath, String idConcept, String idtheso) {
        String uri;
        uri = urlPath + "?idc=" + idConcept + "&idt=" + idtheso;
        return uri;
    }

    public StringBuilder getAllIdentifiers() {
        return stringBuilder;
    }

    public StringBuffer getMessage() {
        return message;
    }

    public void setMessage(StringBuffer message) {
        this.message = message;
    }

    public int getCountTreated() {
        return countTreated;
    }

    public void setCountTreated(int countTreated) {
        this.countTreated = countTreated;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

}
