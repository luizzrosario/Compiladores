import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        // Ler o programa do arquivo "codigo.txt"
        List<String> programa = lerArquivo("exemplo1.txt");
        // Se o programa foi lido com sucesso
        if (programa != null) {
            // Criar um objeto Parser
            Parser parser = new Parser();
            // Processar cada linha do programa
            for (int i = 0; i < programa.size(); i++) {
                // Obter a linha atual
                String linha = programa.get(i).trim();
                // Se a linha não estiver vazia
                if (!linha.isEmpty()) {
                    // Processar a linha pelo parser
                    parser.processarLinha(linha, i + 1);
                }
            }
        }
    }

    // Método para ler o conteúdo de um arquivo
    static List<String> lerArquivo(String caminhoArq) {
        try {
            // Ler todas as linhas do arquivo
            return Files.readAllLines(Paths.get(caminhoArq), StandardCharsets.UTF_8);
        } catch (IOException e) {
            // Se o arquivo não puder ser lido, exibir uma mensagem de erro
            System.out.println("Arquivo não encontrado: " + caminhoArq);
            return null;
        }
    }
}
