/**
 * Trabalho de Arquitetura e Organização de Computadores 1
 * Grupo: Tiago Luz e Arthur Ávila
 */
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
        // classe helper para parsing
        parseHelper = ParseHelper.getInstance();
        
        // quebra as linhas do conteúdo em um array
        String linhas[] = content.split("\n");
        
        // cria um dicionário para eventuais lebels do assembly
        // nomeDaLabel->linha 
        // será preenchido no início de cada linha no final do método
        labels = new HashMap<>();
        
        // stringbuilder onde as instruções serão montadas 
        StringBuilder sb = new StringBuilder();
        
        // lista das linhas válidas
        // aka: removidas linhas vazias, .text, .global, etc.
        ArrayList<String> linhasValidas = new ArrayList<String>();
        int contadorLinha = 0;
        for(int i = 0; i < linhas.length; i++) {
            // tira quebras e espaços desnecessários
            String linha = linhas[i].trim();
            
            // remove linha vazia
            if(linha.length() == 0) {
                continue;
            }
            
            // se for .text, remove string e segue
            if(isInicioText(linha)) {
                linha = linha.substring(".text".length()).trim();
            }
            
            // se alinha estiver vazia neste ponto, remove
            if(linha.length() == 0) {
                continue;
            }
            
            // se for um label, registra no dicionário o nome do label
            // e a linha que se encontra e já remove do código
            // O dicionário será usado depois para vazer a ligação das labels
            // e as respectivas linhas
            if(isLabel(linha)) {
                String label = linha.substring(0, linha.indexOf(":"));
                linha = linha.substring(linha.indexOf(":")+1).trim();
                // cria entrada no dicionário para a nova label
                labels.put(label, contadorLinha);
            } 
            
            linhasValidas.add(linha);
            
            contadorLinha++;
        }
        contadorLinha = 0;
        
        // varre as linhas válidas e mota a instrução de cada uma delas
        // no método montaLinha()
        for(String linha : linhasValidas) {
            sb.append(montaLinha(linha, contadorLinha));
            sb.append("\n");
            contadorLinha++;
        }
        this.contentMontado = sb.toString();
    }
    
    @Override
    public String toString() {
        return this.contentMontado;
    }
    
    /**
     * retorna o número do label ou zero se o label não existir no dicionário
     * @param destino nome do lebel
     * @return número da linha
     */
    int getLabelNum(String destino) {
        Object o = labels.get(destino);
        if(o == null) {
            return 0;
        } else {
            return (int)o;
        }
    }
    
    /**
     * montagem da linha de instrução
     * @param linha instrução
     * @param numeroLinha número da linha no arquivo (entre linhas válidas)
     * @return 
     */
    public String montaLinha(String linha, int numeroLinha) {
        String saida = null;
        linha = linha.trim();        
        int inicio = Integer.parseInt("400000",16); // inicio do programa 0x00400000
        
        // pega a instrução da linha
        String instrucao = linha.substring(0,linha.indexOf(" "));
        int opcode, func, funct, rs, rt, rd, shamt, offset,n,imediato;
        String p[];
        String parametros = linha.substring(instrucao.length()+1).trim();
        
        // para cada instrução chama a montagem correspondente
        // de acordo com o tipo de instrução
        switch(instrucao) {
            case "jal":
                opcode = 3;
                func = 0;
                n = getLabelNum(parametros);
                int enderecoDestino = inicio + (n*4);
                saida = montaInstrucaoJ(opcode,enderecoDestino);
            break;
            case "jr":
                opcode = 0;
                func = 8;
                // traduz o nome do registrador para o indice
                rs = parseHelper.translateRegister(parametros);
                rt = 0;
                rd = 0;
                shamt = 0;
                saida = montaInstrucaoR(opcode, rs, rt, rd, shamt, func);
                break;
            case "sll":
                opcode = 0;
                funct = 0;
                p = parametros.split(","); // quebra parâmetros por ,
                rs = 0;
                
                // traduz o nome dos registradores para os respectivos índices
                rt = parseHelper.translateRegister(p[1]);     
                rd = parseHelper.translateRegister(p[0]);     
                shamt = Integer.parseInt(p[2].trim());
                saida = montaInstrucaoR(opcode, rs, rt, rd, shamt, funct);
                break;
            case "slti":
                opcode = 10;
                p = parametros.split(",");// quebra parâmetros por ,
                
                // traduz o nome dos registradores para os respectivos índices
                rs = parseHelper.translateRegister(p[1]);     
                rt = parseHelper.translateRegister(p[0]);     
                shamt = Integer.parseInt(p[2].trim());
                saida = montaInstrucaoI(opcode, rs, rt, shamt);
                break;
            case "div":
                opcode = 0;
                funct = 26;
                p = parametros.split(",");// quebra parâmetros por ,
                
                // traduz o nome dos registradores para os respectivos índices
                rs = parseHelper.translateRegister(p[0]);
                rt = parseHelper.translateRegister(p[1]);
                rd = 0;
                shamt = 0;
                saida = montaInstrucaoR(opcode, rs, rt, rd, shamt, funct);
                break;
            case "lw":
                opcode = 35;
                funct = 0;
                p= parametros.split(",");// quebra parâmetros por ,
                Pattern pattern = Pattern.compile("([0-9]+)\\((\\$[a-z0-9]{2,4})\\)");
                Matcher matcher = pattern.matcher(p[1].trim());
                matcher.find();
                
                offset = Integer.parseInt(matcher.group(1));
                
                // traduz o nome dos registradores para os respectivos índices
                rt = parseHelper.translateRegister(p[0]);
                rs = parseHelper.translateRegister(matcher.group(2).trim());
                saida = montaInstrucaoL(opcode, rs, rt, offset);
                break;
            case "beq":
                opcode = 4;
                func = 0;
                Pattern pattern2 = Pattern.compile("(\\$[a-z0-9]+)\\ *,\\ *(\\$[a-z0-9]+)\\ *,\\ *([a-z0-9]*)");
                Matcher matcher2 = pattern2.matcher(parametros);
                matcher2.find();
                
                // traduz o nome dos registradores para os respectivos índices
                rt = parseHelper.translateRegister(matcher2.group(2));
                rs = parseHelper.translateRegister(matcher2.group(1));     
                n = (int)labels.get(matcher2.group(3));
                offset = n - (numeroLinha + 1);
                saida = montaInstrucaoL(opcode, rs, rt, offset);
                break;
            case "blez":
                opcode = 6;
                func = 0;
                
                // regex dos parâmetros paseados nos grupos para registrador, label
                Pattern pattern3 = Pattern.compile("(\\$[a-z0-9]+)\\ *,\\ *([a-z0-9]*)");
                Matcher matcher3 = pattern3.matcher(parametros);
                matcher3.find();
                rt = 0;
                // traduz o nome do registrador para o índice
                rs = parseHelper.translateRegister(matcher3.group(1));            
                n = (int)labels.get(matcher3.group(2));
                offset = n - (numeroLinha + 1);
                saida = montaInstrucaoL(opcode, rs, rt, offset);
                break;
            case "ori":
                opcode = 13;
                p = parametros.split(",");// quebra parâmetros por ,
                
                // traduz o nome dos registradores para os respectivos índices
                rs = parseHelper.translateRegister(p[1]);    
                rt = parseHelper.translateRegister(p[0]);
                imediato = Integer.parseInt(p[2].trim());
                saida = montaInstrucaoI(opcode, rs, rt, imediato);
                break;
            case "nor":
                opcode = 0;
                func = 39;
                p = parametros.split(",");// quebra parâmetros por ,
                
                // traduz o nome dos registradores para os respectivos índices
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
    
    /**
     * verifica se é uma linha com label
     * @param linha
     * @return 
     */
    private boolean isLabel(String linha) {
        // verifica se tem ou é um label
        return linha.matches("^.+:.*$");
    }

    /**
     * verifica se é a linha do .text
     * @param linha
     * @return 
     */
    private boolean isInicioText(String linha) {
        return linha.startsWith(".text");
    }

    /**
     * monta a linha das instruções do tipo I
     * @param opcode opcode da operação
     * @param rs indice do registrador
     * @param rt indice do registrador 
     * @param imediato inteiro do imediato
     * @return 
     */
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
    
    /**
     * monta a linha das instruções do tipo J (jal)
     * @param opcode códido da operação
     * @param destino endereço de destino da instrução, partindo de 0x00400000
     * @return 
     */
    public String montaInstrucaoJ(int opcode, int destino) {
        StringBuilder binario = new StringBuilder();
        binario.append(parseHelper.padLeftZeros(parseHelper.intToBin(opcode), 6));
        // tira os primeiros 4 dígitos e os últimos dois dígitos pois são irrelevantes
        binario.append(parseHelper.padLeftZeros(parseHelper.intToBin(destino),32).substring(4, 30));
        String bin = binario.toString();
        return parseHelper.binToHex(bin);
    }
    
    /**
     * monta a linha das instruções do tipo R 
     * @param opcode opcode da operação
     * @param rs indice do registrador rs
     * @param rt indice do registrador rt
     * @param rd indice do registrador rd
     * @param shamt inteiro do shamt 
     * @param func func da operação quando opcode = 0
     * @return 
     */
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

    /**
     * 
     * @param opcode opcode da operação
     * @param rs indice do registrador rs
     * @param rt indice do registrador rt
     * @param offset int do offset 
     * @return 
     */
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
