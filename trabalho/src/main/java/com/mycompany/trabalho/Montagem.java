package com.mycompany.trabalho;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Montagem {

    String content;
    String contentMontado;
    ParseHelper parseHelper;
    HashMap<String,Integer> labels;
    
    public Montagem() {}
    
    public Montagem(String content) {
        this.content = content;
        run();
    }
    
    public void run() {
        parseHelper = ParseHelper.getInstance();
        String linhas[] = parseHelper.breakLines(content);
        labels = new HashMap<>();
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
        contadorLinha = 0;
        for(String linha : linhasValidas) {
            String l = montaLinha(linha, contadorLinha);
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
    
    int getLabelNum(String destino) {
        Object o = labels.get(destino);
        if(o == null) {
            return 0;
        } else {
            return (int)o;
        }
    }

    public String montaLinha(String linha, int numeroLinha) {
        String saida = null;
        linha = linha.trim();        
        int inicio = Integer.parseInt("400000",16); // inicio do programa 0x00400000
        String instrucao = linha.substring(0,linha.indexOf(" "));
        int opcode, func, funct, rs, rt, rd, shamt, offset,n,imediato;
        String destino;
        String p[];
        switch(instrucao) {
            case "jal":
                opcode = 3;
                func = 0;
                destino = linha.substring(4).trim();
                n = getLabelNum(destino);
                int enderecoDestino = inicio + (n*4);
                saida = montaInstrucaoJ(opcode,enderecoDestino);
            break;
            case "jr":
                opcode = 0;
                func = 8;
                destino = linha.substring(3).trim();
                rs = parseHelper.translateRegister(destino);
                rt = 0;
                rd = 0;
                shamt = 0;
                saida = montaInstrucaoR(opcode, rs, rt, rd, shamt, func);
                break;
            case "sll":
                opcode = 0;
                funct = 0;
                destino = linha.substring(3).trim();
                p = destino.split(",");
                rs = 0;
                rt = parseHelper.translateRegister(p[1]);     
                rd = parseHelper.translateRegister(p[0]);     
                shamt = Integer.parseInt(p[2].trim());
                saida = montaInstrucaoR(opcode, rs, rt, rd, shamt, funct);
                break;
            case "slti":
                opcode = 10;
                destino = linha.substring(4).trim();
                p = destino.split(",");
                rs = parseHelper.translateRegister(p[1]);     
                rt = parseHelper.translateRegister(p[0]);     
                shamt = Integer.parseInt(p[2].trim());
                saida = montaInstrucaoI(opcode, rs, rt, shamt);
                break;
            case "div":
                opcode = 0;
                funct = 26;
                destino = linha.substring(4).trim();           
                p = destino.split(",");
                rs = parseHelper.translateRegister(p[0]);
                rt = parseHelper.translateRegister(p[1]);
                rd = 0;
                shamt = 0;
                saida = montaInstrucaoR(opcode, rs, rt, rd, shamt, funct);
                break;
            case "lw":
                opcode = 35;
                funct = 0;
                destino = linha.substring(3).trim();
                p= destino.split(",");
                Pattern pattern = Pattern.compile("([0-9]+)\\((\\$[a-z0-9]{2,4})\\)");
                Matcher matcher = pattern.matcher(p[1].trim());
                matcher.find();
                offset = Integer.parseInt(matcher.group(1));
                rt = parseHelper.translateRegister(p[0]);
                rs = parseHelper.translateRegister(matcher.group(2).trim());
                saida = montaInstrucaoL(opcode, rs, rt, offset);
                break;
            case "beq":
                opcode = 4;
                func = 0;
                destino = linha.substring(4).trim();
                Pattern pattern2 = Pattern.compile("(\\$[a-z0-9]+)\\ *,\\ *(\\$[a-z0-9]+)\\ *,\\ *([a-z0-9]*)");
                Matcher matcher2 = pattern2.matcher(destino);
                matcher2.find();
                rt = parseHelper.translateRegister(matcher2.group(2));
                rs = parseHelper.translateRegister(matcher2.group(1));     
                n = (int)labels.get(matcher2.group(3));
                offset = n - (numeroLinha + 1);
                saida = montaInstrucaoL(opcode, rs, rt, offset);
                break;
            case "blez":
                opcode = 6;
                func = 0;
                destino = linha.substring(4).trim();
                Pattern pattern3 = Pattern.compile("(\\$[a-z0-9]+)\\ *,\\ *([a-z0-9]*)");
                Matcher matcher3 = pattern3.matcher(destino);
                matcher3.find();
                rt = 0;
                rs = parseHelper.translateRegister(matcher3.group(1));            
                n = (int)labels.get(matcher3.group(2));
                offset = n - (numeroLinha + 1);
                saida = montaInstrucaoL(opcode, rs, rt, offset);
                break;
            case "ori":
                opcode = 13;
                destino = linha.substring(4).trim();
                p = destino.split(",");
                rs = parseHelper.translateRegister(p[1]);     
                rt = parseHelper.translateRegister(p[0]);     
                imediato = Integer.parseInt(p[2].trim());
                saida = montaInstrucaoI(opcode, rs, rt, imediato);
                break;
            case "nor":
                opcode = 0;
                func = 39;
                destino = linha.substring(4).trim();
                p = destino.split(",");
                rs = parseHelper.translateRegister(p[1]);     
                rt = parseHelper.translateRegister(p[2]);     
                rd = parseHelper.translateRegister(p[0]);     
                shamt = 0; 
                saida = montaInstrucaoR(opcode, rs, rt, rd, shamt, func);
                break;
            default:
                System.out.println("Instrução inválida ou não implementada: " + instrucao);
                System.exit(-1);
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
        // opcode 31-26 6 bits
        binario.append(parseHelper.padLeftZeros(parseHelper.intToBin(opcode), 6));
        // rs 25-21 5 bits
        binario.append(parseHelper.padLeftZeros(parseHelper.intToBin(rs), 5));        
        // rt 20-16 5 bits
        binario.append(parseHelper.padLeftZeros(parseHelper.intToBin(rt), 5));
        // imediato 15-0 16 bits
        binario.append(parseHelper.padLeftZeros(parseHelper.intToBin(imediato), 16));
        String bin = binario.toString();        
        return parseHelper.binToHex(bin);
    }

    public String montaInstrucaoJ(int opcode, int destino) {
        StringBuilder binario = new StringBuilder();
        binario.append(parseHelper.padLeftZeros(parseHelper.intToBin(opcode), 6));
        binario.append(parseHelper.padLeftZeros(parseHelper.intToBin(destino),32).substring(4, 30));
        String bin = binario.toString();
        return parseHelper.binToHex(bin);
    }
    
    private String montaInstrucaoR(int opcode, int rs, int rt, int rd, int shamt, int func) {
        StringBuilder binario = new StringBuilder();
        // opcode 31-26 6 bits
        binario.append(parseHelper.padLeftZeros(parseHelper.intToBin(opcode), 6));
        // rs 25-21 5 bits
        binario.append(parseHelper.padLeftZeros(parseHelper.intToBin(rs), 5));      
        // rt 20-16 5 bits
        binario.append(parseHelper.padLeftZeros(parseHelper.intToBin(rt), 5));
        // rd 15-11 5 bits
        binario.append(parseHelper.padLeftZeros(parseHelper.intToBin(rd), 5));
        // shampt 10-6 5 bits
        binario.append(parseHelper.padLeftZeros(parseHelper.intToBin(shamt), 5));
        // func 5-0 6 bits
        binario.append(parseHelper.padLeftZeros(parseHelper.intToBin(func), 6));
        String bin = binario.toString();
        return parseHelper.binToHex(bin);
    }

    private String montaInstrucaoL(int opcode, int rs, int rt, int offset) {
        StringBuilder binario = new StringBuilder();
        binario.append(parseHelper.padLeftZeros(parseHelper.intToBin(opcode), 6));
        binario.append(parseHelper.padLeftZeros(parseHelper.intToBin(rs), 5));
        binario.append(parseHelper.padLeftZeros(parseHelper.intToBin(rt), 5));
        binario.append(parseHelper.padLeftZeros(parseHelper.intToBin(offset),16));
        String bin = binario.toString();
        return parseHelper.binToHex(bin);
    }
    
    
}
