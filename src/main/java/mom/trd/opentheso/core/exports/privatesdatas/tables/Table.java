/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.core.exports.privatesdatas.tables;

import java.util.ArrayList;
import mom.trd.opentheso.core.exports.privatesdatas.LineOfData;

/**
 *
 * @author antonio.perez
 */
public class Table {
    
    private ArrayList<LineOfData> lineOfDatas;
    
    public ArrayList<LineOfData> getLineOfDatas() {
        
        return lineOfDatas;
    }

    public void setLineOfDatas(ArrayList<LineOfData> lineOfDatas) {
        this.lineOfDatas = lineOfDatas;
    }
    
}
