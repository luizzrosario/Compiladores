import java.util.*;

public class TabelaSimbolos {
    // Mapa para armazenar os símbolos, onde a chave é o lexema e o valor é o
    // símbolo
    private Map<String, Simbolo> tabela;

    // Construtor para criar uma nova tabela de símbolos vazia
    public TabelaSimbolos() {
        tabela = new HashMap<>();
    }

    // Método para adicionar um símbolo à tabela de símbolos
    public void adicionarSimbolo(Simbolo simbolo) {
        tabela.put(simbolo.lexema, simbolo); // Adiciona o símbolo ao mapa usando o lexema como chave
    }

    // Método para obter um símbolo pelo seu lexema
    public Simbolo obterSimbolo(String lexema) {
        return tabela.get(lexema); // Retorna o símbolo correspondente ao lexema, ou null se não encontrado
    }

    // Método para verificar se a tabela de símbolos contém um símbolo com o lexema
    // especificado
    public boolean contemSimbolo(String lexema) {
        return tabela.containsKey(lexema); // Retorna true se a tabela contém o lexema, false caso contrário
    }
}
