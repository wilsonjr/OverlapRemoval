/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.overlayanalisys.definition;

import br.com.metodos.utils.Retangulo;
import java.util.ArrayList;

/**
 *
 * @author wilson
 */
public interface Metric {

    abstract double execute(ArrayList<Retangulo> pts1, ArrayList<Retangulo> pts2);
}
