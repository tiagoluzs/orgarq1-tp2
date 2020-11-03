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
public class Desmontagem {
    
    String content;
    String contentDesmontado;
    ParseHelper parseHelper;
    
    public Desmontagem(){}
    
    public Desmontagem(String content) {
        this.content = content;
        run();
    }
    
    
    
    public void run() {
        parseHelper = ParseHelper.getInstance();
        String linhas[] = parseHelper.breakLines(content);
        StringBuilder sb = new StringBuilder(".text\n");
        
        HashMap labels = new HashMap();
        
        int inicio = Integer.parseInt("400000",16); // inicio do programa 0x00400000
        
        for(int i = 0; i < linhas.length; i++) {
            String linha = linhas[i].trim();
            String binario = parseHelper.hexToBin(linha);
            int opcode = parseHelper.binToInt(binario.substring(0, 6)); 
            int func = parseHelper.binToInt(binario.substring(26, 32)); 
            String instrucao = getInstrucao(opcode, func);
            if(instrucao == null) {
                System.out.println("Instrução inválida na linha " + (i + 1));
                System.exit(-1);
            }
            sb.append(instrucao);
            sb.append(" ");
            String parametros = "";
            String rs, rt, rd;
            int offset, imediato;
            
            switch(opcode) {
                case 0: 
                    switch(func) {
                        case 8: // jr
                            rs = parseHelper.translateRegister(Integer.parseInt(binario.substring(6,11),2));
                            parametros = rs;
                        break;
                        case 26: // div
                            rs = parseHelper.translateRegister(Integer.parseInt(binario.substring(6,11),2));
                            rt = parseHelper.translateRegister(Integer.parseInt(binario.substring(11,16),2));
                            parametros = rs + "," + rt;
                            break;
                        case 39: // nor
                            rs = parseHelper.translateRegister(Integer.parseInt(binario.substring(6,11),2));
                            rt = parseHelper.translateRegister(Integer.parseInt(binario.substring(11,16),2));
                            rd = parseHelper.translateRegister(Integer.parseInt(binario.substring(16,21),2));
                            parametros = rd + "," + rs + "," + rt;
                            break;
                        case 0: // sll
                            rt = parseHelper.translateRegister(Integer.parseInt(binario.substring(11,16),2));
                            rd = parseHelper.translateRegister(Integer.parseInt(binario.substring(16,21),2));
                            int shamt = parseHelper.trataOffset(binario.substring(21,26));
                            parametros = rd + "," + rt + "," + shamt;
                            break;
                        default:
                            System.out.println("O código de função da instrução informado é inválido ou não foi implementado. (opcode: "+opcode+" func: "+func+") na linha " + (i+1));
                            System.exit(-1);
                    }
                    break;
                case 3: // jal
                    offset = (Integer.parseInt(binario.substring(7)+"00",2) - inicio) / 4;
                    parametros = verificaLabel(labels, i+1, offset);
                    break;
                case 4: // beq
                    rs = parseHelper.translateRegister(Integer.parseInt(binario.substring(6,11),2));
                    rt = parseHelper.translateRegister(Integer.parseInt(binario.substring(11,16),2));
                    offset = parseHelper.trataOffset(binario.substring(16,32));
                    parametros = rs + "," + rt + "," + verificaLabel(labels, i+1, offset);   
                    break;
                case 35: // lw
                    rs = parseHelper.translateRegister(Integer.parseInt(binario.substring(6,11),2));
                    rt = parseHelper.translateRegister(Integer.parseInt(binario.substring(11,16),2));
                    offset = parseHelper.trataOffset(binario.substring(16,32));
                    parametros = rt + "," + offset + "("+rs+")";
                    break;
                case 6: // blez
                    rs = parseHelper.translateRegister(Integer.parseInt(binario.substring(6,11),2));
                    rt = parseHelper.translateRegister(Integer.parseInt(binario.substring(11,16),2));
                    offset = parseHelper.trataOffset(binario.substring(16,32));
                    parametros = rt + "," + verificaLabel(labels, i+1, offset);   
                    break;
                case 13: // ori
                    rs = parseHelper.translateRegister(Integer.parseInt(binario.substring(6,11),2));
                    rt = parseHelper.translateRegister(Integer.parseInt(binario.substring(11,16),2));
                    imediato = parseHelper.trataOffset(binario.substring(16,32));
                    parametros = rt + "," + rs + "," + imediato;
                    break;
                case 10: // slti
                    rs = parseHelper.translateRegister(Integer.parseInt(binario.substring(6,11),2));
                    rt = parseHelper.translateRegister(Integer.parseInt(binario.substring(11,16),2));
                    imediato = parseHelper.trataOffset(binario.substring(16,32));
                    parametros = rt + "," + rs + "," + imediato;
                    break;
                default:
                    System.out.println("O opcode da instrução informado é inválido ou não foi implementado. (opcode: "+opcode+") na linha " + (i+1));
                    System.exit(-1);
            }
            
            sb.append(parametros);
            sb.append("\n");
        }
        
        // adiciona labels
        StringBuilder sb2 = new StringBuilder();
        String ls[] = sb.toString().split("\n");
        for(int i = 0; i < ls.length; i++) {
            if(labels.containsKey(i)) {
                ls[i] = labels.get(i)+":"+ls[i];
            }
            sb2.append(ls[i]);
            sb2.append("\n");
        }
        
        this.contentDesmontado = sb2.toString();
    }
    
    @Override
    public String toString() {
        return this.contentDesmontado;
    }

    private String getInstrucao(int opcode, int func) {
        if(opcode == 0) {
            switch(func) {
                case 39:
                    return "nor";
                case 8:
                    return "jr";
                case 0:
                    return "sll";
                case 26: 
                    return "div";
                default:
                    return null;
            }
        } else {
            switch(opcode) {
                case 3:
                    return "jal";
                case 10:
                    return "slti";
                case 35:
                    return  "lw";
                case 4:
                    return "beq";
                case 6:
                    return "blez";
                case 13: 
                    return "ori";
                default:
                    return null;
            }
        }
    }

    private String verificaLabel(HashMap labels,int linhaAtual, int offset) {
        int destino = linhaAtual + offset;
        if(labels.containsKey(destino)) {
            return labels.get(destino).toString();
        } else {
            labels.put(destino, "label"+destino);
            return "label"+destino;
        }
    }
}
