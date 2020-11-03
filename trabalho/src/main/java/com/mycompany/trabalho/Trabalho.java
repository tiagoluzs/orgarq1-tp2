/**
 * Trabalho de Arquitetura e Organização de Computadores 1
 * Grupo: Tiago Luz e Arthur Ávila
 */

package com.mycompany.trabalho;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Classe principal do projeto
 * recebe os argumentos da linha de comando e chama as classes de acordo com a operação
*/
public class Trabalho {
    public static void main(String args[]) {
        
        // exibe erro de chamada e texto de ajuda
        if(args.length == 0) {
            printHelp(true);
            System.exit(-1);
        }
        
        // se não for ajuda e não houverem 3 parametros, exibe erro de chamada e texto de ajuda
        if(!args[0].equals("ajuda") && args.length != 3) {
            printHelp(true);
            System.exit(-1);
        }
        
        // exibe erro de chamada e texto de ajuda
        if(!args[0].equals("ajuda") && !args[0].equals("monta") && !args[0].equals("desmonta")) {
            printHelp(true);
            System.exit(-1);
        }
        
        // chama operações de acordo com chamada do usuário
        String operacao = args[0];
        switch(operacao) {
            case "ajuda" -> printHelp(false);
            case "monta" -> monta(args[1],args[2]);
            case "desmonta" -> desmonta(args[1],args[2]);
        }
    }
    
    /**
     * Chama rotina de montagem de instruções assembly -> hexa
     * @param origem arquivo de leitura das instruções
     * @param destino arquivo de destino das instruções ou -stdout para escrever na saída padrão do console
     */
    private static void monta(String origem, String destino) {
        String content = null; 
        try {
            // lê contéudo do arquivo de entrada
            content = readFile(origem);
        } catch(FileNotFoundException e) {
            System.out.println("Arquivo de origem inexistente.");
            System.exit(-1);
        } catch(IOException e) {
            System.out.println("Arquivo de origem não pode ser lido.");
            System.exit(-1);
        }
        
        // chama classe de montagem
        Montagem montagem = new Montagem(content);
        String contentMontado = montagem.toString();
        
        // retorna erro se não montar nenhuma instrução
        if(contentMontado == null || contentMontado.trim().length() == 0) {
            System.out.println("Montagem retornou uma saída vazia.");
            System.exit(-1);
        } 
        
        // executa saúda de acordo com a definição do usuário (arquivo ou stdout)
        executaSaida(contentMontado, destino);
    }
    
    private static void desmonta(String origem, String destino) {
        String content = null; 
        try {
            // lê contéudo do arquivo de entrada
            content = readFile(origem);
        } catch(FileNotFoundException e) {
            System.out.println("Arquivo de origem inexistente.");
            System.exit(-1);
        } catch(IOException e) {
            System.out.println("Arquivo de origem não pode ser lido.");
            System.exit(-1);
        }
        
        // chama classe de desmontagem
        Desmontagem desmontagem = new Desmontagem(content);
        String contentDesmontado = desmontagem.toString();
        
        if(contentDesmontado == null || contentDesmontado.trim().length() == 0) {
            System.out.println("Desmontagem retornou uma saída vazia.");
            System.exit(-1);
        } 
        
        // executa saúda de acordo com a definição do usuário (arquivo ou stdout)
        executaSaida(contentDesmontado, destino);
        
    }
    
    /**
     * 
     * @param lê conteúdo de um arquivo informado no parâmetro
     * @return retorna string com o conteúdo do arquivo
     * @throws FileNotFoundException 
     * @throws IOException 
     */
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
    
    /**
     * escreve ajuda quando ocorre algum erro. 
     */
    public static void printHelp() {
        printHelp(true);
    }
    
    
    /**
     * escreve ajuda
     * @param erro se true informa que houve um erro de chamada dos parâmetros
     */
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
        System.out.println("Para executar os arquivos de exemplo (dentro da pasta release):");
        System.out.println("java -jar trabalho.jar monta ../trabalho/exemplo_entrada_assembly.asm -stdout");
        System.out.println("java -jar trabalho.jar desmonta ../trabalho/exemplo_desmontagem.asm -stdout");
    }

    /**
     * executa saúda de acordo com a definição do usuário
     * @param content conteúdo gerado pela montagem ou desmontagem (string)
     * @param destino destino do conteúdo (arquivo ou stdout)
     */
    private static void executaSaida(String content, String destino) {
        content = content.trim();
        // se for -stdout, escreve direto no console e encerra.
        if(destino.equals("-stdout")) {
            System.out.println(content);
            System.exit(0);
        } else {
            // se for arquivo, como boa prática coloca uma quebra de linha no final
            content = content + "\n";
            // cria arquivo e escreve conteúdo no mesmo.
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
