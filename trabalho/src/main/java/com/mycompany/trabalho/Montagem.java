/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.trabalho;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author tiagoluz
 */
class Montagem {

    String content;
    String contentMontado;
    
    public Montagem() {}
    
    public Montagem(String content) {
        this.content = content;
        run();
    }
    
    public void run() {
        ParseHelper helper = new ParseHelper();
        String linhas[] = helper.breakLines(content);
        HashMap labels = new HashMap();
        StringBuilder sb = new StringBuilder();
        
        ArrayList<String> linhasValidas = new ArrayList<String>();
        int contadorLinha = 0;
        
        for(int i = 0; i < linhas.length; i++) {
            String linha = linhas[i].trim();
            
            if(linha.length() == 0) {
                continue;
            }
            
            // se for .text, remove string e segue
            if(isInicioText(linha)) {
                linha = linha.substring(".text".length()).trim();
            }
            
            if(linha.length() == 0) {
                continue;
            }
            
            if(isLabel(linha)) {
                String label = linha.substring(0, linha.indexOf(":"));
                linha = linha.substring(linha.indexOf(":")+1).trim();
                labels.put(label, contadorLinha);
            }
            
            linhasValidas.add(linha);
            
            contadorLinha++;
        }
        
        
        
        
        System.out.println("LABELS:");
        System.out.println("========");
        
        for(Object a : labels.keySet()) {
            System.out.println(a.toString() + " => " + labels.get(a));
        }
        
        System.out.println("Montagem:");
        System.out.println("========");
        
        contadorLinha = 0;
        for(String linha : linhasValidas) {
            System.out.print(contadorLinha + " " + linha);
            String l = montaLinha(linha,labels);
            
            System.out.println(" => "+ l);
            sb.append(l);
            
            sb.append("\n");
            
            contadorLinha++;
        }
        
        this.contentMontado = sb.toString();
    }
    
    @Override
    public String toString() {
        return this.contentMontado;
    }

    public String montaLinha(String linha, HashMap labels) {
        String saida = null;
        linha = linha.trim();
        ParseHelper ph = new ParseHelper();
        
        // inicio do programa 0x00400000
        int inicio = Integer.parseInt("400000",16);
        
        if(linha.startsWith("jal")) {
            // opcode dec 3
            // opcode hex 03
            // TIPO J
            int opcode = 3;
            int func = 0;
            String destino = linha.substring(4).trim();
            int n = (int)labels.get(destino);
            int enderecoDestino = inicio + (n*4);
            saida = montaInstrucaoJ(opcode,enderecoDestino);
            
        } else if(linha.startsWith("jr")) {
            // opcode dec 0
            // opcode hex 00
            // func dec 8
            int opcode = 0;
            int func = 8;
            
            String destino = linha.substring(3).trim();
            
            int rs = ph.translateRegister(destino);     // primeiro operando
            int rt = 0;
            int rd = 0;     // destino 
            int shamt = 0;  // 
            
            saida = montaInstrucaoR(opcode, rs, rt, rd, shamt, func);
            
            
        } else if(linha.startsWith("sll")) {
            // opcode dec 0
            // opcode hex 00
            // func dec 0
            int opcode = 0;
            int funct = 0;
            
            String destino = linha.substring(3).trim();
            String p[] = destino.split(",");
            
            int rs = 0;
            int rt = ph.translateRegister(p[1]);     
            int rd = ph.translateRegister(p[0]);     
            int shamt = Integer.parseInt(p[2].trim());  // 
            
            
            saida = montaInstrucaoR(opcode, rs, rt, rd, shamt, funct);
            
        } else if(linha.startsWith("slti")) {
            // opcode dec 0
            // opcode hex 00
            // func dec 0
            int opcode = 10;
            
            String destino = linha.substring(4).trim();
            String p[] = destino.split(",");
            
            int rs = ph.translateRegister(p[1]);     
            int rt = ph.translateRegister(p[0]);     
            int shamt = Integer.parseInt(p[2].trim());  // 
            
            saida = montaInstrucaoI(opcode, rs, rt, shamt);
        } else if(linha.startsWith("div")) {
            // opcode dec 0
            // opcode hex 00
            // func dec 26
            int opcode = 0;
            int funct = 26;
            
            String destino = linha.substring(4).trim();
            
            String p[] = destino.split(",");
            
            int rs = ph.translateRegister(p[0]);     // primeiro operando
            int rt = ph.translateRegister(p[1]);     // segundo operando
            int rd = 0;     // destino 
            int shamt = 0;  // 
            
            saida = montaInstrucaoR(opcode, rs, rt, rd, shamt, funct);
            
        } else if(linha.startsWith("lw")) {
            
            // opcode dec 35
            // opcode hex 23
            // func dec 0
            int opcode = 35;
            int funct = 0;
            
            String destino = linha.substring(3).trim();
            
            String p[] = destino.split(",");
            
            Pattern pattern = Pattern.compile("([0-9]+)\\((\\$[a-z0-9]{2,4})\\)");
            Matcher matcher = pattern.matcher(p[1].trim());
            matcher.find();
            
            int offset = Integer.parseInt(matcher.group(1));
                    
            int rt = ph.translateRegister(p[0]);     // segundo operando
            int rd = 0;     // destino 
            int rs = ph.translateRegister(matcher.group(2).trim());     // primeiro operando
            int shamt = 0;  // 
            
            saida = montaInstrucaoL(opcode, rs, rt, offset);
            
        } else if(linha.startsWith("beq")) {
            
        } else if(linha.startsWith("blez")) {
            
        } else if(linha.startsWith("ori")) {
            
        } else if(linha.startsWith("nor")) {
            
        } 
        
        return saida;
    }

    private boolean isLabel(String linha) {
        // verifica se tem ou é um label
        return linha.matches("^.+:.*$");
    }

    private boolean isInicioText(String linha) {
        return linha.startsWith(".text");
    }

    public String montaInstrucaoI(int opcode, int rs, int rt, int imediato) {
        StringBuilder binario = new StringBuilder();
        
        ParseHelper ph = new ParseHelper();
        
        // opcode 31-26 6 bits
        binario.append(ph.padLeftZeros(ph.intToBin(opcode), 6));
        
        // rs 25-21 5 bits
        binario.append(ph.padLeftZeros(ph.intToBin(rs), 5));
        
        // rt 20-16 5 bits
        binario.append(ph.padLeftZeros(ph.intToBin(rt), 5));
        
        // imediato 15-0 16 bits
        binario.append(ph.padLeftZeros(ph.intToBin(imediato), 16));
        
        
        String bin = binario.toString();
        
        return ph.binToHex(bin);
    }

    public String montaInstrucaoJ(int opcode, int destino) {
        StringBuilder binario = new StringBuilder();
        
        ParseHelper ph = new ParseHelper();
        
        // opcode
        binario.append(ph.padLeftZeros(ph.intToBin(opcode), 6));
        
        // destino
        binario.append(ph.padLeftZeros(ph.intToBin(destino),32).substring(4, 30));
        
        String bin = binario.toString();
        
        return ph.binToHex(bin);
    }
    
    private String montaInstrucaoR(int opcode, int rs, int rt, int rd, int shamt, int func) {
        StringBuilder binario = new StringBuilder();
        
        ParseHelper ph = new ParseHelper();
        
        // opcode 31-26 6 bits
        binario.append(ph.padLeftZeros(ph.intToBin(opcode), 6));
        
        // rs 25-21 5 bits
        binario.append(ph.padLeftZeros(ph.intToBin(rs), 5));
        
        // rt 20-16 5 bits
        binario.append(ph.padLeftZeros(ph.intToBin(rt), 5));
        
        // rd 15-11 5 bits
        binario.append(ph.padLeftZeros(ph.intToBin(rd), 5));
        
        // shampt 10-6 5 bits
        binario.append(ph.padLeftZeros(ph.intToBin(shamt), 5));
        
        // func 5-0 6 bits
        binario.append(ph.padLeftZeros(ph.intToBin(func), 6));
        
        String bin = binario.toString();
        
        return ph.binToHex(bin);
    }

    private String montaInstrucaoL(int opcode, int rs, int rt, int offset) {
         StringBuilder binario = new StringBuilder();
        
        ParseHelper ph = new ParseHelper();
        
        // opcode
        binario.append(ph.padLeftZeros(ph.intToBin(opcode), 6));
        
        binario.append(ph.padLeftZeros(ph.intToBin(rs), 5));
        
        binario.append(ph.padLeftZeros(ph.intToBin(rt), 5));
        
        // destino
        binario.append(ph.padLeftZeros(ph.intToBin(offset),16));
        
        String bin = binario.toString();
        
        return ph.binToHex(bin);
    }
    
    
}
