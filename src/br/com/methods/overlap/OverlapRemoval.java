/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.methods.overlap;

import br.com.methods.utils.OverlapRect;
import java.util.ArrayList;

/**
 *
 * @author wilson
 */
public interface OverlapRemoval {
    public ArrayList<OverlapRect> apply(ArrayList<OverlapRect> rects);
}
