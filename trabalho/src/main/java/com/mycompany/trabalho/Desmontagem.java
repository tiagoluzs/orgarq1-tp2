/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.trabalho;

/**
 *
 * @author tiagoluz
 */
public class Desmontagem {
    
    String content;
    String contentDesmontado;
    
    public Desmontagem(){}
    
    public Desmontagem(String content) {
        this.content = content;
        run();
    }
    
    public void run() {
        this.contentDesmontado = this.content;
    }
    
    

     @Override
    public String toString() {
        return this.contentDesmontado;
    }
}
