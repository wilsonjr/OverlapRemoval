/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.projection.spacereduction;

import java.util.Objects;

/**
 *
 * @author Windows
 * @param <S>
 * @param <T>
 */
public class SeamPair<S, T> {
    
    private S first;
    private T second;
    
    public SeamPair(S first, T second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + Objects.hashCode(this.first);
        hash = 89 * hash + Objects.hashCode(this.second);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if( this == obj )
            return true;
        if( obj == null )
            return false;
        if( getClass() != obj.getClass() )
            return false;
        final SeamPair<?, ?> other = (SeamPair<?, ?>) obj;
        return Objects.equals(this.first, other.first);
    }

    public S getFirst() {
        return first;
    }

    public void setFirst(S first) {
        this.first = first;
    }

    public T getSecond() {
        return second;
    }

    public void setSecond(T second) {
        this.second = second;
    }
    
    
    
    
    
    
    
    
    
}
