package com.mycompany.trabalho;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Trabalho {
    public static void main(String args[]) {
        
        if(args.length == 0) {
            printHelp(true);
            System.exit(-1);
        }
        
        if(!args[0].equals("ajuda") && args.length != 3) {
            printHelp(true);
            System.exit(-1);
        }
        
        if(!args[0].equals("ajuda") && !args[0].equals("monta") && !args[0].equals("desmonta")) {
            printHelp(true);
            System.exit(-1);
        }
        
        String operacao = args[0];
        
        switch(operacao) {
            case "ajuda" -> printHelp(false);
            case "monta" -> monta(args[1],args[2]);
            case "desmonta" -> desmonta(args[1],args[2]);
        }
    }
    
    private static void monta(String origem, String destino) {
        String content = null; 
        try {
            content = readFile(origem);
        } catch(FileNotFoundException e) {
            System.out.println("Arquivo de origem inexistente.");
            System.exit(-1);
        } catch(IOException e) {
            System.out.println("Arquivo de origem não pode ser lido.");
            System.exit(-1);
        }
        
        Montagem montagem = new Montagem(content);
        String contentMontado = montagem.toString();
        
        if(contentMontado == null || contentMontado.trim().length() == 0) {
            System.out.println("Montagem retornou uma saída vazia.");
            System.exit(-1);
        } 
        
        executaSaida(contentMontado, destino);
    }
    
    private static void desmonta(String origem, String destino) {
        String content = null; 
        try {
            content = readFile(origem);
        } catch(FileNotFoundException e) {
            System.out.println("Arquivo de origem inexistente.");
            System.exit(-1);
        } catch(IOException e) {
            System.out.println("Arquivo de origem não pode ser lido.");
            System.exit(-1);
        }
        
        Desmontagem desmontagem = new Desmontagem(content);
        String contentDesmontado = desmontagem.toString();
        
        if(contentDesmontado == null || contentDesmontado.trim().length() == 0) {
            System.out.println("Desmontagem retornou uma saída vazia.");
            System.exit(-1);
        } 
        
        executaSaida(contentDesmontado, destino);
        
    }
    
    private static String readFile(String file) throws FileNotFoundException, IOException {
        File f = new File(file);
        FileReader fr = new FileReader(f);
        int i;
        StringBuilder sb = new StringBuilder();
        while((i=fr.read())!=-1) {
            sb.append((char)i);
        }
        return sb.toString();
    }
    
    public static void printHelp() {
        printHelp(true);
    }
    
    public static void printHelp(boolean erro) {
        System.out.println("\n\nTrabalho de Arquitetura e Organização de Computadores 1");
        System.out.println("=======================================================\n");        
        System.out.println("Grupo: Tiago Luz e Arthur Ávila\n");
        if(erro)
            System.out.println("ERRO: Parâmetros informados incorretos. \n");
        System.out.println("Exemplos de execução:\n");
        System.out.println("java -jar trabalho.jar ajuda");
        System.out.println("java -jar trabalho.jar monta arquivo_entrada.asm arquivo_saida.asm");
        System.out.println("java -jar trabalho.jar monta arquivo_entrada.asm -stdout");
        System.out.println("java -jar trabalho.jar desmonta arquivo_entrada.asm arquivo_saida.asm");
        System.out.println("java -jar trabalho.jar desmonta arquivo_entrada.asm -stdout");
        System.out.println("\n\n");
    }

    private static void executaSaida(String content, String destino) {
        if(destino.equals("-stdout")) {
            System.out.println(content);
            System.exit(0);
        } else {
            try {
                File f = new File(destino);
                FileWriter fw = new FileWriter(f);
                fw.write(content);
                fw.close();
            } catch (IOException ex) {
                System.out.println("Não foi possível escrever no arquivo de saída.");
                System.exit(-1);
            }
            
        }
    }
}
