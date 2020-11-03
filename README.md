**Como compilar o projeto SEM o maven:**

    ~# javac -d release trabalho/src/main/java/com/mycompany/trabalho/*.java
    ~# cd release
    ~# jar cfe trabalho.jar com.mycompany.trabalho.Trabalho *

O jar deve ter sido criado dentro da pasta release.  

**Para executar a montagem e escrever na saída padrão (STDOUT):**

    ~# java -jar monta <ARQUIVO_COM_INSTRUCOES_ASSEMBLY.asm> -stdout

**Para executar a montagem e escrever a saída em um arquivo:**

    ~# java -jar monta <ARQUIVO_COM_INSTRUCOES_ASSEMBLY.asm> <ARQUIVO_SAIDA.asm>

**Para executar a desmontagem e escrever na saída padrão (STDOUT):**

    ~# java -jar desmonta <ARQUIVO_COM_INSTRUCOES_HEXA.asm> -stdout  

**Para executar a montagem e escrever a saída em um arquivo:**

    ~# java -jar desmonta <ARQUIVO_COM_INSTRUCOES_HEXA.asm> <ARQUIVO_SAIDA.asm>


**Para executar os arquivos de exemplo dados pelo enunciado do trabalho:**

    ~# cd release
    ~# java -jar trabalho.jar monta ../trabalho/exemplo_entrada_assembly.asm -stdout
    ~# java -jar trabalho.jar desmonta ../trabalho/exemplo_desmontagem.asm -stdout



