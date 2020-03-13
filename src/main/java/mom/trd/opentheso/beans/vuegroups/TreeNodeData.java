/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.beans.vuegroups;

import java.io.Serializable;

/**
 *
 * @author miledrousset
 */
public class TreeNodeData implements Serializable {
    private String nodeId;
    private String name;
    private String notation;
    
    private boolean isGroup;
    private boolean isSubGroup;
    private boolean isConcept;
    
    private String nodeType;

    public TreeNodeData(String nodeId, String name, String notation,
            boolean isGroup, boolean isSubGroup,
            boolean isConcept, String nodeType) {
        this.nodeId = nodeId;
        this.name = name;
        this.notation = notation;
        this.isGroup = isGroup;
        this.isSubGroup = isSubGroup;
        this.isConcept = isConcept;
        this.nodeType = nodeType;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNotation() {
        return notation;
    }

    public void setNotation(String notation) {
        this.notation = notation;
    }

    public boolean isIsGroup() {
        return isGroup;
    }

    public void setIsGroup(boolean isGroup) {
        this.isGroup = isGroup;
    }

    public boolean isIsSubGroup() {
        return isSubGroup;
    }

    public void setIsSubGroup(boolean isSubGroup) {
        this.isSubGroup = isSubGroup;
    }

    public boolean isIsConcept() {
        return isConcept;
    }

    public void setIsConcept(boolean isConcept) {
        this.isConcept = isConcept;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }
 
 
    @Override
    public String toString() {
        return name;
    }

}
