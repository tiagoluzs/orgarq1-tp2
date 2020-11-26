/**
 * Trabalho de Arquitetura e Organização de Computadores 1
 * Grupo: Tiago Luz e Arthur Ávila
 */
package com.mycompany.trabalho;

import java.util.HashMap;
/**
 * classe com métodos auxiliares para parsing dos arquivos
 * @author tiagoluz
 */
public class ParseHelper {
    
    // dicionário registrador->indice
    static final HashMap<String,Integer> regTable;
    
    // dicionário indice->registrador
    static final HashMap<Integer,String> regTableInd;
    
    // Singleton Design Pattern
    private static ParseHelper instance;
    
    private ParseHelper() {
        
    }
    
    // popula dicionários
    static {
        regTable = new HashMap<>();
        regTableInd = new HashMap<>();
        regTable.put("$zero", 0);
        regTable.put("$at", 1);
        regTable.put("$v0", 2);
        regTable.put("$v1", 3);
        regTable.put("$a0", 4);
        regTable.put("$a1", 5);
        regTable.put("$a2", 6);
        regTable.put("$a3", 7);
        regTable.put("$t0", 8);
        regTable.put("$t1", 9);
        regTable.put("$t2", 10);
        regTable.put("$t3", 11);
        regTable.put("$t4", 12);
        regTable.put("$t5", 13);
        regTable.put("$t6", 14);
        regTable.put("$t7", 15);
        regTable.put("$s0", 16);
        regTable.put("$s1", 17);
        regTable.put("$s2", 18);
        regTable.put("$s3", 19);
        regTable.put("$s4", 20);
        regTable.put("$s5", 21);
        regTable.put("$s6", 22);
        regTable.put("$s7", 23);
        regTable.put("$t8", 24);
        regTable.put("$t9", 25);
        regTable.put("$k0", 26);
        regTable.put("$k1", 27);
        regTable.put("$gp", 28);
        regTable.put("$sp", 29);
        regTable.put("$fp", 30);
        regTable.put("$ra", 31);
        regTableInd.put(0,"$zero");
        regTableInd.put(1,"$at");
        regTableInd.put(2,"$v0");
        regTableInd.put(3,"$v1");
        regTableInd.put(4,"$a0");
        regTableInd.put(5,"$a1");
        regTableInd.put(6,"$a2");
        regTableInd.put(7,"$a3");
        regTableInd.put(8,"$t0");
        regTableInd.put(9,"$t1");
        regTableInd.put(10,"$t2");
        regTableInd.put(11,"$t3");
        regTableInd.put(12,"$t4");
        regTableInd.put(13,"$t5");
        regTableInd.put(14,"$t6");
        regTableInd.put(15,"$t7");
        regTableInd.put(16,"$s0");
        regTableInd.put(17,"$s1");
        regTableInd.put(18,"$s2");
        regTableInd.put(19,"$s3");
        regTableInd.put(20,"$s4");
        regTableInd.put(21,"$s5");
        regTableInd.put(22,"$s6");
        regTableInd.put(23,"$s7");
        regTableInd.put(24,"$t8");
        regTableInd.put(25,"$t9");
        regTableInd.put(26,"$k0");
        regTableInd.put(27,"$k1");
        regTableInd.put(28,"$gp");
        regTableInd.put(29,"$sp");
        regTableInd.put(30,"$fp");
        regTableInd.put(31,"$ra");
    }
    
    // chamada singleton para esta classe
    public static ParseHelper getInstance() {
        if(instance == null) {
            instance = new ParseHelper();
        }
        return instance;
    }
    
    // traduz nome do registrador para indice
    public int translateRegister(String reg) {
        return (int)regTable.get(reg.trim());
    }
    
    // traduz indice para nome do registrador
    public String translateRegister(int ind) {
        return regTableInd.get(ind).toString();
    }
    
    // preenche caracteres à esquerda
    private String padLeft(String inputString, int length, char pad) {
        if (inputString.length() >= length) {
            return inputString.substring(inputString.length() - length);
        }
        StringBuilder sb = new StringBuilder();
        while (sb.length() < length - inputString.length()) {
            sb.append(pad);
        }
        sb.append(inputString);
        return sb.toString();
    }
    
    // wraper para padLeft
    public String padLeftZeros(String inputString, int length) {
        return this.padLeft(inputString, length, '0');
    }
    
    // wraper para padLeft
    public String padLeftOnes(String inputString, int length) {
        return this.padLeft(inputString, length, '1');
    }
    
    /**
     * converte inteiro para binário complemento de 2
     * @param val inteiro signed
     * @return 
     */
    public String intToBin(int val) {
        return Integer.toBinaryString(val);
    }
    
    /**
     * converte binário para hexadecimal e retorna no formato 0xA0
     * @param val binario no formato 00101001
     * @return 
     */
    public String binToHex(String val) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < val.length(); i+=4) {
            String chunk = val.substring(i,i+4);
            int h = Integer.parseInt(chunk, 2);
            sb.append(Integer.toHexString(h));
        }
        return "0x"+sb.toString();
    }
    
    /**
     * converte binário para inteiro unsigned
     * @param string do binário no formato 00010101
     * @return 
     */
    int binToInt(String val) {
        return Integer.parseInt(val,2);
    }
     
    /**
     * Converte hexa para binário 
     * @param val string do numero hexadecimal. aceita com ou sem prefixo 0x. 
     * @return 
     */
    String hexToBin(String val) {
        StringBuilder sb = new StringBuilder();
        if(val.startsWith("0x")) {
            val = val.substring(2);
        }
        for(int i = 0; i < val.length(); i++) {
            int h = Integer.parseInt(String.valueOf(val.charAt(i)), 16);
            sb.append(padLeftZeros(intToBin(h), 4));
        }
        return sb.toString();
    }

    /**
     * converte o binário para inteiro já tratando se for complemento de 2 positivo
     * ou negativo. 
     * @param binario
     * @return 
     */
    
    int trataOffset(String binario) {
        int i = Integer.parseInt(binario,2);
        if(binario.startsWith("1")) {
            return i - 65535;
        } else {
            return i;
        }
    }
}
