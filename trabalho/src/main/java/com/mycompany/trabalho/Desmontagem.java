/**
 * Trabalho de Arquitetura e Organização de Computadores 1
 * Grupo: Tiago Luz e Arthur Ávila
 */
package com.mycompany.trabalho;

import java.util.HashMap;

public class Desmontagem {
    String content;
    String contentDesmontado;
    ParseHelper parseHelper;
    public Desmontagem(){
    }
    
    /**
     * Efetua desmontagem das instruções HEXA
     * @param content string com conteúdo do arquivo 
     */
    public Desmontagem(String content) {
        this.content = content;
        run();
    }
    
    /**
     * roda a desmontagem
     */
    public void run() {
        parseHelper = ParseHelper.getInstance();
        
        // quebra linhas 
        String linhas[] = content.split("\n");
        
        // inicia o conteúdo final já com o label .text
        StringBuilder sb = new StringBuilder(".text\n");
        
        // inicia o dicionário de labels
        HashMap<Integer,String> labels = new HashMap<>();
        
        // início do endereço das instruções 
        int inicio = Integer.parseInt("400000",16); // inicio do programa 0x00400000
        
        // varre as instruções hexadecimal
        for(int i = 0; i < linhas.length; i++) {
            String linha = linhas[i].trim();
            
            // converte a linha hexa para binário onde cada caractere hexa 
            // é convertido para 4 caracteres 0 ou 1. 
            String binario = parseHelper.hexToBin(linha);
            
            // quebra a instrução em binário nos campos necessários para a desmontagem
            // maide detalhes no método
            Instrucao instrucao = desmontaDeBinario(binario);  
            
            // se a instrução retornar nula é porque está inválida
            if(instrucao == null) {
                System.out.println("Instrução inválida na linha " + (i + 1));
                System.exit(-1);
            }
            String parametros = "";
            String rs, rt, rd;
            int offset, imediato;
            
            // monta os parâmetros de cada instrução de acordo com o caso específico
            // o objeto instrução neste ponto já contém os campos da instrução parseados. 
            
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
        
        // adiciona labels de acordo com o que consta no dicionário labels
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

    /**
     * verifica se o label existe considerando a linha atual e o offset da instrução
     * se não existir, cria uma, adiciona o dicionário e retorna a mesma
     * @param labels
     * @param linhaAtual
     * @param offset
     * @return 
     */
    private String verificaLabel(HashMap<Integer,String> labels,int linhaAtual, int offset) {
        int destino = linhaAtual + offset;
        if(labels.containsKey(destino)) {
            return labels.get(destino);
        } else {
            labels.put(destino, "label"+destino);
            return "label"+destino;
        }
    }
    
    /**
     * traduz opcode + func para a instrução assembly
     * @param opcode inteiro
     * @param func inteiro
     * @return 
     */
    private String getInstrucao(int opcode, int func) {
        if(opcode == 0) {
            switch (func) {
                case 39: 
                    return "nor";
                case 8 : 
                    return  "jr";
                case 0 : 
                    return  "sll";
                case 26 : 
                    return  "div";
                default : 
                    return  null;
            }
            
        } else {
            switch (opcode) {
                case 3 : 
                    return  "jal";
                case 10 : 
                    return  "slti";
                case 35 : 
                    return "lw";
                case 4 : 
                    return "beq";
                case 6 : 
                    return  "blez";
                case 13 : 
                    return  "ori";
                default : 
                    return  null;
            }
        }
    }
    /**
     * Recebe uma string de um número binário da instrução e quebra os campos 
     * para reconhecimento e tradução da mesma para assembly
     * @param binario recebe um binário
     * @return retorna objeto instrução com campos preenchidos de acordo com o tipo de instrução
     */
    private Instrucao desmontaDeBinario(String binario) {
        Instrucao instrucao = new Instrucao();
        instrucao.opcode = parseHelper.binToInt(binario.substring(0, 6)); 
        instrucao.func = parseHelper.binToInt(binario.substring(26, 32));
        instrucao.cmd = getInstrucao(instrucao.opcode, instrucao.func);
        if(instrucao.cmd == null) {
            return null;
        }
        // busca registradores e já faz a tradução
        instrucao.rs = parseHelper.translateRegister(Integer.parseInt(binario.substring(6,11),2));
        instrucao.rt = parseHelper.translateRegister(Integer.parseInt(binario.substring(11,16),2));
        instrucao.rd = parseHelper.translateRegister(Integer.parseInt(binario.substring(16,21),2));        
        
        int inicio = Integer.parseInt("400000",16); // inicio do programa 0x00400000
        if(instrucao.opcode == 3) { // se for jal, trata de acordo
            instrucao.offset = (Integer.parseInt(binario.substring(7)+"00",2) - inicio) / 4;
        } else { // senão, trata normal 
            instrucao.offset = parseHelper.trataOffset(binario.substring(16,32));
        }
        
        // faz a tradução do imediato e do shamt 
        instrucao.imm = parseHelper.trataOffset(binario.substring(16,32));
        instrucao.shamt = parseHelper.trataOffset(binario.substring(21,26));
        
        return instrucao;
    }
}
