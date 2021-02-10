/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.methods.overlap.incboard;

import incboard.api.DataItemInterface;
import incboard.api.DataModelInterface;
import incboard.api.IncBoard;
import incboard.api.MoveEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Essa classe segue o modelo proposto pela API do método IncBoard.
 * Disponível em: https://github.com/robertodepinho/incBoard-API
 * @author wilson
 */
public class IncBoardExecutor implements DataModelInterface {
    
    private List<PointItem> items = new ArrayList<>();
    private int minRow = Integer.MAX_VALUE;
    private int maxRow = Integer.MIN_VALUE;
    private int minCol = Integer.MAX_VALUE;
    private int maxCol = Integer.MIN_VALUE;
    private int count = 0;
    private final int STEP = 1;
    
    @Override
    public void moveReceived(MoveEvent event) {
        count++;
        
        if( count % STEP != 0 ) 
            return;
        
        DataItemInterface moved = event.movedItem();
        
        if( moved.getRow() < minRow ) 
            minRow = moved.getRow();
        if( moved.getRow() > maxRow ) 
            maxRow = moved.getRow();       
        if( moved.getCol() < minCol ) 
            minCol = moved.getCol();
        if( moved.getCol() > maxCol ) 
            maxCol = moved.getCol();    
    }

    public List<PointItem> getItems() {
        return items;
    }

    public int getMinRow() {
        return minRow;
    }

    public int getMaxRow() {
        return maxRow;
    }

    public int getMinCol() {
        return minCol;
    }

    public int getMaxCol() {
        return maxCol;
    }

    public int getSTEP() {
        return STEP;
    }
    
    public void apply(ArrayList<PointItem> rects) {
        IncBoard engine = new IncBoard(this, false);
        for( PointItem rect : rects ) {
            engine.add(rect);
            items.add(rect);
        }
    }
    
    
}
