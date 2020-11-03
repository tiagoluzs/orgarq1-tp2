package com.mycompany.trabalho;

import java.util.HashMap;

public class Desmontagem {
    String content;
    String contentDesmontado;
    ParseHelper parseHelper;
    public Desmontagem(){
    }
    public Desmontagem(String content) {
        this.content = content;
        run();
    }
    public void run() {
        parseHelper = ParseHelper.getInstance();
        String linhas[] = parseHelper.breakLines(content);
        StringBuilder sb = new StringBuilder(".text\n");
        HashMap<Integer,String> labels = new HashMap<>();
        int inicio = Integer.parseInt("400000",16); // inicio do programa 0x00400000
        for(int i = 0; i < linhas.length; i++) {
            String linha = linhas[i].trim();
            String binario = parseHelper.hexToBin(linha);
            Instrucao instrucao = desmontaDeBinario(binario);            
            if(instrucao == null) {
                System.out.println("Instrução inválida na linha " + (i + 1));
                System.exit(-1);
            }
            String parametros = "";
            String rs, rt, rd;
            int offset, imediato;
            switch(instrucao.opcode) {
                case 0: 
                    switch(instrucao.func) {
                        case 8: // jr
                            parametros = instrucao.rs;
                        break;
                        case 26: // div
                            parametros = instrucao.rs + "," + instrucao.rt;
                            break;
                        case 39: // nor
                            parametros = instrucao.rd + "," + instrucao.rs + "," + instrucao.rt;
                            break;
                        case 0: // sll
                            parametros = instrucao.rd + "," + instrucao.rt + "," + instrucao.shamt;
                            break;
                        default:
                            System.out.println("O código de função da instrução informado é inválido ou não foi implementado. (opcode: "+instrucao.opcode+" func: "+instrucao.func+") na linha " + (i+1));
                            System.exit(-1);
                    }
                    break;
                case 3: // jal
                    parametros = verificaLabel(labels, i+1, instrucao.offset);
                    break;
                case 4: // beq
                    parametros = instrucao.rs + "," + instrucao.rt + "," + verificaLabel(labels, i+1, instrucao.offset);   
                    break;
                case 35: // lw
                    parametros = instrucao.rt + "," + instrucao.offset + "("+instrucao.rs+")";
                    break;
                case 6: // blez
                    parametros = instrucao.rs + "," + verificaLabel(labels, i+1, instrucao.offset);   
                    break;
                case 13: // ori
                    parametros = instrucao.rt + "," + instrucao.rs + "," + instrucao.imm;
                    break;
                case 10: // slti
                    parametros = instrucao.rt + "," + instrucao.rs + "," + instrucao.imm;
                    break;
                default:
                    System.out.println("O opcode da instrução informado é inválido ou não foi implementado. (opcode: "+instrucao.opcode+") na linha " + (i+1));
                    System.exit(-1);
            }
            
            sb.append(instrucao.cmd).append(" ").append(parametros).append("\n");
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
        this.contentDesmontado = sb2.toString().trim();
    }
    
    @Override
    public String toString() {
        return this.contentDesmontado;
    }

    private String verificaLabel(HashMap<Integer,String> labels,int linhaAtual, int offset) {
        int destino = linhaAtual + offset;
        if(labels.containsKey(destino)) {
            return labels.get(destino);
        } else {
            labels.put(destino, "label"+destino);
            return "label"+destino;
        }
    }
    
    private String getInstrucao(int opcode, int func) {
        if(opcode == 0) {
            return switch (func) {
                case 39 -> "nor";
                case 8 -> "jr";
                case 0 -> "sll";
                case 26 -> "div";
                default -> null;
            };
        } else {
            return switch (opcode) {
                case 3 -> "jal";
                case 10 -> "slti";
                case 35 -> "lw";
                case 4 -> "beq";
                case 6 -> "blez";
                case 13 -> "ori";
                default -> null;
            };
        }
    }
    private Instrucao desmontaDeBinario(String binario) {
        Instrucao instrucao = new Instrucao();
        instrucao.opcode = parseHelper.binToInt(binario.substring(0, 6)); 
        instrucao.func = parseHelper.binToInt(binario.substring(26, 32));
        instrucao.cmd = getInstrucao(instrucao.opcode, instrucao.func);
        if(instrucao.cmd == null) {
            return null;
        }
        instrucao.rs = parseHelper.translateRegister(Integer.parseInt(binario.substring(6,11),2));
        instrucao.rt = parseHelper.translateRegister(Integer.parseInt(binario.substring(11,16),2));
        instrucao.rd = parseHelper.translateRegister(Integer.parseInt(binario.substring(16,21),2));        
        int inicio = Integer.parseInt("400000",16); // inicio do programa 0x00400000
        if(instrucao.opcode == 3) { // jal
            instrucao.offset = (Integer.parseInt(binario.substring(7)+"00",2) - inicio) / 4;
        } else {
            instrucao.offset = parseHelper.trataOffset(binario.substring(16,32));
        }
        instrucao.imm = parseHelper.trataOffset(binario.substring(16,32));
        instrucao.shamt = parseHelper.trataOffset(binario.substring(21,26));
        return instrucao;
    }
}
