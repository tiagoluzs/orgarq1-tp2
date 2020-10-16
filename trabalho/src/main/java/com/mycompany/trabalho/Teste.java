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
public class Teste {
    
    

    public static void main(String a[]) {
       Montagem m = new Montagem();
       
       int inicio = Integer.parseInt("400000",16);
       
       int opcode = 2;
       int destino = inicio + 3*4;
       
        System.out.println(m.montaInstrucaoJ(opcode, destino));
        
        String teste = "1234567890abcdefghijklmnopqrstuv";
        System.out.println(teste);
        
        System.out.println(teste.substring(4, 30));
    }
}
