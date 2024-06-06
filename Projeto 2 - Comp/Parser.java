import java.util.*;

public class Parser {
    // Lista para manter o histórico de tabelas de símbolos
    static List<TabelaSimbolos> pilhaTabelas = new ArrayList<>();

    // Método para processar uma linha do código
    public void processarLinha(String linha, int numeroLinha) {
        // Remover espaços em branco no início e no final da linha
        linha = linha.trim();

        // Verificar se a linha é um novo bloco
        if (linha.startsWith("BLOCO")) {
            // Adicionar uma nova tabela de símbolos à pilha
            pilhaTabelas.add(new TabelaSimbolos());
        }
        // Verificar se a linha é o fim de um bloco
        else if (linha.startsWith("FIM")) {
            // Se houver tabelas de símbolos na pilha
            if (!pilhaTabelas.isEmpty()) {
                // Remover a tabela de símbolos mais recente da pilha
                pilhaTabelas.remove(pilhaTabelas.size() - 1);
            }
        }
        // Verificar se a linha declara variáveis NUMERO ou CADEIA
        else if (linha.startsWith("NUMERO") || linha.startsWith("CADEIA")) {
            // Dividir a linha em duas partes: tipo de dado e declarações de variáveis
            String[] partes = linha.split(" ", 2);
            String tipoDado = partes[0];
            String[] declaracoes = partes[1].split(",");
            // Iterar sobre cada declaração de variável na linha
            for (String declaracao : declaracoes) {
                Simbolo simbolo;
                // Verificar se a declaração contém um valor atribuído
                if (declaracao.contains("=")) {
                    String[] lados = declaracao.split("=");
                    String lexema = lados[0].trim();
                    String valorStr = lados[1].trim();
                    Object valor;
                    // Determinar o tipo do valor com base no tipo de dado declarado
                    if (tipoDado.equals("NUMERO")) {
                        valor = valorStr.contains(".") ? Double.parseDouble(valorStr) : Integer.parseInt(valorStr);
                    } else {
                        valor = valorStr.replace("\"", "");
                    }
                    // Criar um novo símbolo com o lexema, tipo de dado e valor
                    simbolo = new Simbolo("tk_identificador", lexema, tipoDado, valor);
                }
                // Se não houver valor atribuído à variável
                else {
                    String lexema = declaracao.trim();
                    // Criar um novo símbolo com o lexema e tipo de dado, valor é nulo
                    simbolo = new Simbolo("tk_identificador", lexema, tipoDado, null);
                }
                // Adicionar o símbolo à tabela de símbolos atual
                pilhaTabelas.get(pilhaTabelas.size() - 1).adicionarSimbolo(simbolo);
            }
        }
        // Verificar se a linha é um comando de impressão (PRINT)
        else if (linha.startsWith("PRINT")) {
            // Obter o lexema da variável a ser impressa
            String lexema = linha.split(" ")[1];
            Simbolo simbolo = null;
            // Iterar sobre as tabelas de símbolos na pilha
            for (int i = pilhaTabelas.size() - 1; i >= 0; i--) {
                // Verificar se a variável está definida em alguma tabela de símbolos
                if (pilhaTabelas.get(i).contemSimbolo(lexema)) {
                    // Obter o símbolo correspondente à variável
                    simbolo = pilhaTabelas.get(i).obterSimbolo(lexema);
                    break;
                }
            }
            // Se o símbolo for encontrado
            if (simbolo != null) {
                // Determinar o valor a ser impresso com base no tipo de dado
                String valor = simbolo.tipoDado.equals("CADEIA") ? "\"" + simbolo.valor + "\""
                        : simbolo.valor.toString();
                // Imprimir o valor
                System.out.println(valor);
            }
            // Se a variável não estiver definida
            else {
                // Imprimir uma mensagem de erro
                String erroMsg = "Erro linha " + numeroLinha + " - Variável não declarada";
                System.out.println(erroMsg);
            }
        }
        // Verificar se a linha contém uma atribuição de valor a uma variável
        else if (linha.contains("=")) {
            String[] lados = linha.split("=");
            String lexema = lados[0].trim();
            String valorStr = lados[1].trim();
            Simbolo simbolo = null;
            // Iterar sobre as tabelas de símbolos na pilha
            for (int i = pilhaTabelas.size() - 1; i >= 0; i--) {
                // Verificar se a variável está definida em alguma tabela de símbolos
                if (pilhaTabelas.get(i).contemSimbolo(lexema)) {
                    // Obter o símbolo correspondente à variável
                    simbolo = pilhaTabelas.get(i).obterSimbolo(lexema);
                    break;
                }
            }
            // Se o símbolo for encontrado
            if (simbolo != null) {
                // Verificar o tipo de dado da variável
                if (simbolo.tipoDado.equals("NUMERO")) {
                    // Verificar se o valor atribuído é numérico
                    if (valorStr.matches("-?\\d+(\\.\\d+)?")) {
                        simbolo.valor = valorStr.contains(".") ? Double.parseDouble(valorStr)
                                : Integer.parseInt(valorStr);
                    }
                    // Se o valor não for numérico
                    else if (valorStr.startsWith("\"") && valorStr.endsWith("\"")) {
                        // Imprimir uma mensagem de erro
                        String erroMsg = "Erro linha " + numeroLinha + ", tipos não compatíveis";
                        System.out.println(erroMsg);
                    }
                    // Se o valor for uma variável
                    else {
                        Simbolo simboloOrigem = null;
                        // Iterar sobre as tabelas de símbolos na pilha
                        for (int i = pilhaTabelas.size() - 1; i >= 0; i--) {
                            // Verificar se a variável está definida em alguma tabela de símbolos
                            if (pilhaTabelas.get(i).contemSimbolo(valorStr)) {
                                // Obter o símbolo correspondente à variável
                                simboloOrigem = pilhaTabelas.get(i).obterSimbolo(valorStr);
                                break;
                            }
                        }
                        // Se a variável de origem for encontrada
                        if (simboloOrigem != null) {
                            // Verificar se a variável de origem é do tipo NUMERO
                            if (simboloOrigem.tipoDado.equals("NUMERO")) {
                                // Atribuir o valor da variável de origem à variável atual
                                simbolo.valor = simboloOrigem.valor;
                            }
                            // Se a variável de origem não for do tipo NUMERO
                            else {
                                // Imprimir uma mensagem de erro
                                String erroMsg = "Erro linha " + numeroLinha + ", tipos não compatíveis";
                                System.out.println(erroMsg);
                            }
                        }
                        // Se a variável de origem não estiver definida
                        else {
                            // Imprimir uma mensagem de erro
                            String erroMsg = "Erro linha " + numeroLinha + " - Variável não declarada";
                            System.out.println(erroMsg);
                        }
                    }
                }
                // Verificar se o tipo de dado da variável é CADEIA
                else if (simbolo.tipoDado.equals("CADEIA")) {
                    // Verificar se o valor atribuído é uma cadeia de caracteres
                    if (valorStr.startsWith("\"") && valorStr.endsWith("\"")) {
                        // Remover as aspas da cadeia de caracteres
                        simbolo.valor = valorStr.replace("\"", "");
                    }
                    // Se o valor não for uma cadeia de caracteres
                    else if (valorStr.matches("-?\\d+(\\.\\d+)?")) {
                        // Imprimir uma mensagem de erro
                        String erroMsg = "Erro linha " + numeroLinha + ", tipos não compatíveis";
                        System.out.println(erroMsg);
                    }
                    // Se o valor for uma variável
                    else {
                        Simbolo simboloOrigem = null;
                        // Iterar sobre as tabelas de símbolos na pilha
                        for (int i = pilhaTabelas.size() - 1; i >= 0; i--) {
                            // Verificar se a variável está definida em alguma tabela de símbolos
                            if (pilhaTabelas.get(i).contemSimbolo(valorStr)) {
                                // Obter o símbolo correspondente à variável
                                simboloOrigem = pilhaTabelas.get(i).obterSimbolo(valorStr);
                                break;
                            }
                        }
                        // Se a variável de origem for encontrada
                        if (simboloOrigem != null) {
                            // Verificar se a variável de origem é do tipo CADEIA
                            if (simboloOrigem.tipoDado.equals("CADEIA")) {
                                // Atribuir o valor da variável de origem à variável atual
                                simbolo.valor = simboloOrigem.valor;
                            }
                            // Se a variável de origem não for do tipo CADEIA
                            else {
                                // Imprimir uma mensagem de erro
                                String erroMsg = "Erro linha " + numeroLinha + ", tipos não compatíveis";
                                System.out.println(erroMsg);
                            }
                        }
                        // Se a variável de origem não estiver definida
                        else {
                            // Imprimir uma mensagem de erro
                            String erroMsg = "Erro linha " + numeroLinha + " - Variável não declarada";
                            System.out.println(erroMsg);
                        }
                    }
                }
            }
            // Se a variável não estiver definida
            else {
                // Imprimir uma mensagem de erro
                String erroMsg = "Erro linha " + numeroLinha + " - Variável não declarada";
                System.out.println(erroMsg);
            }
        }
    }
}
