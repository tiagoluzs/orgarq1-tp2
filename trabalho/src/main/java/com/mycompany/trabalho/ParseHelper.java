/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.trabalho;

import java.util.HashMap;

/**
 *
 * @author tiagoluz
 */
public class ParseHelper {
    
    static HashMap regTable;
    
    private static ParseHelper instance;
    
    private ParseHelper() {
        
    }
    
    static {
        regTable = new HashMap();
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
    }
    
    public static ParseHelper getInstance() {
        if(instance == null) {
            instance = new ParseHelper();
        }
        return instance;
    }
        
    public String[] breakLines(String content) {
        return content.split("\n");
    }
    
    
    public int translateRegister(String reg) {
        return (int)regTable.get(reg.trim());
    }
    
    public String padLeft(String inputString, int length, char pad) {
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
    
    public String padLeftZeros(String inputString, int length) {
        return this.padLeft(inputString, length, '0');
    }
    
    public String padLeftOnes(String inputString, int length) {
        return this.padLeft(inputString, length, '1');
    }
    
    public String intToBin(int val) {
        return Integer.toBinaryString(val);
    }
    
    public String binToHex(String val) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < val.length(); i+=4) {
            String chunk = val.substring(i,i+4);
            int h = Integer.parseInt(chunk, 2);
            sb.append(Integer.toHexString(h));
        }
        return "0x"+sb.toString();
    }
}
