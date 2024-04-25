import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Stack;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Automato {
    // objeto para o erro: descrição, linha e coluna
    static class ErroInfo {
        String erro;
        int linha;
        int coluna;

        public ErroInfo(String erro) {
            this.erro = erro;
            this.linha = currentLine;
            this.coluna = currentColumn;
        }
    }

    // inicio no estado 0
    private static int estado = 0;
    private static int currentLine = 1;
    private static int currentColumn = 1;
    private static String lexema = "";
    // boolean para os tokens que não precisam de retorno de lexema
    private static boolean tokenUnico = false;
    // pilha para verificar se os parenteses estão balanceados (completamente
    // desnecessário.)
    private static Stack<Character> pilhaParenteses = new Stack<>();
    private static int i;
    // Lista para a contagem de tokens
    private static String[] nomesTokens = { "TK_ERRO", "TK_INT", "TK_FLOAT", "TK_HEX", "TK_ID", "TK_ROTINA",
            "TK_FIM_ROTINA", "TK_SE", "TK_SENAO", "TK_IMPRIMA", "TK_LEIA", "TK_PARA", "TK_ENQUANTO", "TK_DATA",
            "TK_CADEIA", "TK_COMENTARIO", "TK_ABRE_PAREN", "TK_FECHA_PAREN", "TK_DOIS_PONTOS", "TK_MENOR", "TK_MAIOR",
            "TK_MENOR_IGUAL", "TK_MAIOR_IGUAL", "TK_IGUAL", "TK_DIFERENTE", "TK_ATRIBUICAO", "TK_MAIS", "TK_MENOS",
            "TK_MULTIPLICACAO", "TK_DIVISAO", "TK_NEGAR", "TK_E", "TK_OU" };
    private static int[] contagemTokens = new int[nomesTokens.length];
    static List<ErroInfo> erros = new ArrayList<>();
    static boolean comentario = false;

    public static void main(String[] args) {
        /*
         * 
         * AQUI PARA MODIFICAR O ARQUIVO DE ENTRADA, BASTA MUDAR O CAMINHO DO ARQUIVO
         * 
         */

        // convertendo o arquivo para uma array de char
        String codigo = fileToString("entrada3.txt");
        System.out.println(
                "+-----+-----+-----------------+-----------------------+\n| LIN | COL |     TOKEN       |        LEXEMA         |\n+-----+-----+-----------------+-----------------------+");
        // loop para percorrer o código
        for (i = 0; i < codigo.length(); i++) {
            // indica onde está o char atual
            char c = codigo.charAt(i);
            // junta os chars analisados para formar o lexema
            if (c != '\n') {
                lexema += c;
            }
            // basicamente o loop consiste em verificar o estado atual (em int) e o char
            // atual
            if (estado == 0) {
                if (Character.isDigit(c)) { // se for digito [0-9]
                    estado = 1;
                } else if (Character.isAlphabetic(c)) {
                    if (Character.isUpperCase(c)) {
                        estado = 10;
                    } else {
                        estado = 13;
                    }
                } else if (c == '.') {
                    estado = 9;
                } else if (c == ' ') { // reconhecer o espaço para não printar o TK_ERRO (Ainda não tratado)
                    lexema = "";
                } else if (c == '"') {
                    estado = 30;
                } else if (c == '#') {
                    estado = 31;
                } else if (c == '<') {
                    estado = 32;
                } else if (c == '(') {
                    pilhaParenteses.push(c);
                    retornaToken(16);
                } else if (c == ')') {
                    if (pilhaParenteses.isEmpty() || pilhaParenteses.pop() != '(') {
                        // erros.add(new ErroInfo("sem parentesis de abertura"));
                        retornaToken(0); // erro de parêntesis
                    } else if (c == ')') {
                        retornaToken(17);
                    }
                } else if (c == ':') {
                    retornaToken(18);
                } else if (c == '+') {
                    retornaToken(26);
                } else if (c == '-') {
                    retornaToken(27);
                } else if (c == '*') {
                    retornaToken(28);
                } else if (c == '%') {
                    retornaToken(29);
                } else if (c == '~') {
                    retornaToken(30);
                } else if (c == '&') {
                    retornaToken(31);
                } else if (c == '|') {
                    retornaToken(32);
                } else if (c == '=') {
                    retornaToken(23);
                } else if (c == '>') {
                    estado = 38;
                } else {
                    // erros.add(new ErroInfo("caractere inválido"));
                    retornaToken(0);
                    lexema = "";
                }
            } else if (estado == 1) {
                if (Character.isDigit(c)) {
                    estado = 2;
                } else if (c == 'x') {
                    estado = 11;
                } else if (c == '.') {
                    estado = 5;
                } else if (Character.isAlphabetic(c)) {
                    erros.add(new ErroInfo("caractere inválido"));
                    retornaToken(0);
                } else {
                    retornaToken(1); // retorno de token de inteiro (caso 1)
                }
            } else if (estado == 2) {
                if (Character.isDigit(c)) {
                    estado = 3;
                } else if (c == '.') {
                    estado = 5;
                } else if (c == '/') {
                    estado = 16;
                } else if (c == '_') {
                    estado = 17;
                } else {
                    retornaToken(1);
                }
            } else if (estado == 3) {
                if (Character.isDigit(c)) {
                    estado = 4;
                } else if (c == '.') {
                    estado = 5;
                } else {
                    retornaToken(1);
                }
            } else if (estado == 4) {
                if (Character.isDigit(c)) {
                    estado = 4; // loop para reconhecer mais de um digito no inteiro
                } else if (c == '.') {
                    erros.add(new ErroInfo("4 dígitos antes do ponto"));
                    retornaToken(0); // aqui é a filtragem para mais que 4 dígitos antes do ponto
                } else if (Character.isAlphabetic(c)) {
                    erros.add(new ErroInfo("letra no meio do número"));
                    retornaToken(0); // rejeita caso tenha letra no meio do número
                } else {
                    retornaToken(1);
                }
            } else if (estado == 5) {
                if (Character.isDigit(c)) {
                    estado = 5; // loop para reconhecer mais de um digito no float após o ponto
                } else if (c == 'e') { // reconhecer o 'e' para o float com elevado
                    estado = 6;
                } else {
                    retornaToken(2);
                }
            } else if (estado == 6) {
                if (Character.isDigit(c)) {
                    estado = 7;
                } else if (c == '-') { // reconhecer o '-' para o elevado negativo
                    estado = 8;
                } else {
                    erros.add(new ErroInfo("sem número após o 'e'	"));
                    retornaToken(0); // rejeita caso seja só um 'e' sem número
                }
            } else if (estado == 7) {
                if (Character.isDigit(c)) {
                    estado = 7; // loop para reconhecer mais de um digito no elevado
                } else {
                    retornaToken(2);
                }
            } else if (estado == 8) {
                if (Character.isDigit(c)) {
                    estado = 7;
                } else {
                    erros.add(new ErroInfo("- sem número após o 'e'"));
                    retornaToken(0); // aqui rejeita caso termine só com um '-' sem número
                }
            } else if (estado == 9) {
                if (Character.isDigit(c)) {
                    estado = 5;
                } else {
                    erros.add(new ErroInfo("sem número após o ponto	"));
                    retornaToken(0); // rejeita só o '.'
                }
            } else if (estado == 10) {
                if (c == 'x') {
                    estado = 11;
                } else {
                    erros.add(new ErroInfo(""));
                    retornaToken(0);
                }
            } else if (estado == 11) {
                if (Character.isDigit(c) || (c >= 'A' && c <= 'F')) {
                    estado = 11;
                } else {
                    retornaToken(3);
                }
            } else if (estado == 12) {
                lexema = lexema.replace(" ", "");
                if (lexema.equals("rotina") || lexema.equals("fim_rotina") || lexema.equals("se")
                        || lexema.equals("senao") || lexema.equals("imprima") || lexema.equals("leia")
                        || lexema.equals("para") || lexema.equals("enquanto")) {
                    tokenUnico = true;
                }
                if (lexema.equals("se")) {
                    retornaToken(7);
                } else if ((Character.isAlphabetic(c) && Character.isLowerCase(c) || c == '_')) {
                    switch (lexema) {
                        case "rotina":
                            retornaToken(5);
                            break;
                        case "fim_rotina":
                            retornaToken(6);
                            break;
                        case "senao":
                            retornaToken(8);
                            break;
                        case "imprima":
                            retornaToken(9);
                            break;
                        case "leia":
                            retornaToken(10);
                            break;
                        case "para":
                            retornaToken(11);
                            break;
                        case "enquanto":
                            retornaToken(12);
                            break;
                    }
                } else {
                    erros.add(new ErroInfo("palavra inválida"));
                    retornaToken(0);
                }

            } else if (estado == 13) {
                if (lexema.equals("se")) {
                    estado = 12;
                } else if (Character.isAlphabetic(c)) {
                    if (Character.isUpperCase(c)) {
                        estado = 14;
                    } else {
                        estado = 12;
                    }
                } else {
                    erros.add(new ErroInfo("caractere inválido"));
                    retornaToken(0);
                }
            } else if (estado == 14) {
                if (!Character.isAlphabetic(c)) {
                    retornaToken(4);
                } else if (Character.isAlphabetic(c) && Character.isLowerCase(c)) {
                    estado = 15;
                } else {
                    erros.add(new ErroInfo(""));
                    retornaToken(0);
                }
            } else if (estado == 15) {
                if (!Character.isAlphabetic(c)) {
                    retornaToken(4);
                } else if (Character.isAlphabetic(c) && Character.isUpperCase(c)) {
                    estado = 14;
                } else {
                    erros.add(new ErroInfo(""));
                    retornaToken(0);
                }
            } else if (estado == 16) {
                if (Character.isDigit(c)) {
                    estado = 18;
                } else {
                    erros.add(new ErroInfo("data inválida"));
                    retornaToken(0);
                }
            } else if (estado == 18) {
                if (Character.isDigit(c)) {
                    estado = 20;
                } else {
                    erros.add(new ErroInfo("data inválida"));
                    retornaToken(0);
                }
            } else if (estado == 20) {
                if (c == '/') {
                    estado = 22;
                } else {
                    erros.add(new ErroInfo("data inválida"));
                    retornaToken(0);
                }
            } else if (estado == 22) {
                if (Character.isDigit(c)) {
                    estado = 24;
                } else {
                    erros.add(new ErroInfo("data inválida"));
                    retornaToken(0);
                }
            } else if (estado == 24) {
                if (Character.isDigit(c)) {
                    estado = 26;
                } else {
                    erros.add(new ErroInfo("data inválida"));
                    retornaToken(0);
                }
            } else if (estado == 26) {
                if (Character.isDigit(c)) {
                    estado = 28;
                } else {
                    retornaToken(13);
                }
            } else if (estado == 28) {
                if (Character.isDigit(c)) {
                    retornaToken(13);
                } else {
                    erros.add(new ErroInfo("data inválida"));
                    retornaToken(0);
                }
            } else if (estado == 17) {
                if (Character.isDigit(c)) {
                    estado = 19;
                } else {
                    erros.add(new ErroInfo("data inválida"));
                    retornaToken(0);
                }
            } else if (estado == 19) {
                if (Character.isDigit(c)) {
                    estado = 21;
                } else {
                    erros.add(new ErroInfo("data inválida"));
                    retornaToken(0);
                }
            } else if (estado == 21) {
                if (c == '_') {
                    estado = 23;
                } else {
                    erros.add(new ErroInfo("data inválida"));
                    retornaToken(0);
                }
            } else if (estado == 23) {
                if (Character.isDigit(c)) {
                    estado = 25;
                } else {
                    erros.add(new ErroInfo("data inválida"));
                    retornaToken(0);
                }
            } else if (estado == 25) {
                if (Character.isDigit(c)) {
                    estado = 27;
                } else {
                    erros.add(new ErroInfo("data inválida"));
                    retornaToken(0);
                }
            } else if (estado == 27) {
                if (Character.isDigit(c)) {
                    estado = 29;
                } else {
                    erros.add(new ErroInfo("data inválida"));
                    retornaToken(0);
                }
            } else if (estado == 29) {
                if (Character.isDigit(c)) {
                    retornaToken(13);
                } else {
                    erros.add(new ErroInfo("data inválida"));
                    retornaToken(0);
                }
            } else if (estado == 30) {
                if (c == '"') {
                    retornaToken(14);
                } else if (c == '\n') {
                    erros.add(new ErroInfo("cadeia não fechada"));
                    retornaToken(0);
                } else {
                    estado = 30;
                }
            } else if (estado == 31) {
                if (c == '\n') {
                    retornaToken(15);
                } else {
                    estado = 31;
                }
            } else if (estado == 32) {
                if (c == '<') {
                    estado = 33;
                } else if (c == '=') {
                    estado = 37;
                } else if (c == '>') {
                    retornaToken(24);
                } else {
                    retornaToken(19);
                }
            } else if (estado == 33) {
                if (c == '<') {
                    erros.add(new ErroInfo("comentário não fechado"));
                    estado = 34;
                } else {
                    erros.add(new ErroInfo("caractere inválido"));
                    retornaToken(0);
                }
            } else if (estado == 34) {
                if (c == '>') {
                    estado = 35;
                } else {
                    estado = 34;
                }
            } else if (estado == 35) {
                if (c == '>') {
                    estado = 36;
                } else {
                    estado = 34;
                }
            } else if (estado == 36) {
                erros.remove(erros.size() - 1);
                if (c == '>') {
                    retornaToken(15);
                } else {
                    estado = 34;
                }
            } else if (estado == 37) {
                if (c == '=') {
                    retornaToken(25);
                } else {
                    retornaToken(21);
                }
            } else if (estado == 38) {
                if (c == '=') {
                    retornaToken(22);
                } else {
                    retornaToken(20);
                }
            } else {
                erros.add(new ErroInfo("estado inválido"));
                retornaToken(0);
            }
            // responsáveis pela contagem de linhas e colunas
            if (c == '\n') {
                currentLine++;
                currentColumn = 1;
            } else {
                currentColumn++;
            }

            // System.out.println(lexema);
            // System.out.println("\n->" + estado); // verificar os estados na hora de
            // debugar
        }

        // Verifica o ultimo estado (no caso quando o loop termina, pode ocorrer de não
        // ter terminado)

        if (estado == 4 || estado == 3 || estado == 2 || estado == 1) {
            retornaToken(1);
        }
        if (estado == 6 || estado == 5 || estado == 9 || estado == 7) {
            retornaToken(2);
        }
        if (estado == 8) {
            retornaToken(0);
        }
        if (estado == 10 || estado == 41 || estado == 11 || estado == 21) {
            retornaToken(3);
        }
        if (estado == 12 || estado == 15 || estado == 14) {
            retornaToken(4);
        }

        // Chamada de função para imprimir a tabela com a contagem de tokens
        imprimirTabela();

        // Imprime o código
        System.out.println("\n" + codigo);

        // Imprime os erros
        for (ErroInfo erro : erros) {
            System.out.printf("Erro: %s\nLinha: %d\nColuna: %d\n\n", erro.erro, erro.linha, erro.coluna);
        }
    }

    /*
     * Função para retornar o token
     * 0 = TK_ERRO (testes)
     * 1 = TK_INT
     * 2 = TK_FLOAT
     * 3 = TK_HEX
     * 4 = TK_ID
     * 5 = TK_ROTINA
     * 6 = TK_FIM_ROTINA
     * 7 = TK_SE
     * 8 = TK_SENAO
     * 9 = TK_IMPRIMA
     * 10 = TK_LEIA
     * 11 = TK_PARA
     * 12 = TK_ENQUANTO
     * 13 = TK_DATA
     * 14 = TK_CADEIA
     * 15 = TK_COMENTARIO (testes)
     * 16 = TK_ABRE_PARENTESIS
     * 17 = TK_FECHA_PARENTESIS
     * 18 = TK_DOIS_PONTOS
     * 19 = TK_MENOR
     * 20 = TK_MAIOR
     * 21 = TK_MENOR_IGUAL
     * 22 = TK_MAIOR_IGUAL
     * 23 = TK_IGUAL
     * 24 = TK_DIFERENTE
     * 25 = TK_ATRIBUICAO
     * 26 = TK_MAIS
     * 27 = TK_MENOS
     * 28 = TK_MULTIPLICACAO
     * 29 = TK_DIVISAO
     * 30 = TK_NEGAR
     * 31 = TK_E
     * 32 = TK_OU
     */

    public static void retornaToken(int x) {
        retornaToken(x, currentLine, currentColumn);
    }

    public static void retornaToken(int x, int line, int column) {
        estado = 0;
        String tokenName = "";
        switch (x) {
            case 0:
                // tokenName = "TK_ERRO";
                // break;
                lexema = "";
                return;
            case 1:
                i--;
                lexema = lexema.substring(0, lexema.length() - 1);
                tokenName = "TK_INT";
                break;
            case 2:
                i--;
                lexema = lexema.substring(0, lexema.length() - 1);
                tokenName = "TK_FLOAT";
                break;
            case 3:
                i--;
                lexema = lexema.substring(0, lexema.length() - 1);
                tokenName = "TK_HEX";
                break;
            case 4:
                i--;
                lexema = lexema.substring(0, lexema.length() - 1);
                tokenName = "TK_ID";
                break;
            case 5:
                tokenName = "TK_ROTINA";
                break;
            case 6:
                tokenName = "TK_FIM_ROTINA";
                break;
            case 7:
                tokenName = "TK_SE";
                break;
            case 8:
                tokenName = "TK_SENAO";
                break;
            case 9:
                tokenName = "TK_IMPRIMA";
                break;
            case 10:
                tokenName = "TK_LEIA";
                break;
            case 11:
                tokenName = "TK_PARA";
                break;
            case 12:
                tokenName = "TK_ENQUANTO";
                break;
            case 13:
                tokenName = "TK_DATA";
                break;
            case 14:
                tokenName = "TK_CADEIA";
                break;
            case 15:
                // tokenName = "TK_COMENTARIO";
                // break;
                return;
            case 16:
                tokenUnico = true;
                tokenName = "TK_ABRE_PAREN";
                break;
            case 17:
                tokenUnico = true;
                tokenName = "TK_FECHA_PAREN";
                break;
            case 18:
                tokenUnico = true;
                tokenName = "TK_DOIS_PONTOS";
                break;
            case 19:
                tokenUnico = true;
                tokenName = "TK_MENOR";
                break;
            case 20:
                tokenUnico = true;
                tokenName = "TK_MAIOR";
                break;
            case 21:
                tokenUnico = true;
                tokenName = "TK_MENOR_IGUAL";
                break;
            case 22:
                tokenUnico = true;
                tokenName = "TK_MAIOR_IGUAL";
                break;
            case 23:
                tokenUnico = true;
                tokenName = "TK_IGUAL";
                break;
            case 24:
                tokenUnico = true;
                tokenName = "TK_DIFERENTE";
                break;
            case 25:
                tokenUnico = true;
                tokenName = "TK_ATRIBUICAO";
                break;
            case 26:
                tokenUnico = true;
                tokenName = "TK_MAIS";
                break;
            case 27:
                tokenUnico = true;
                tokenName = "TK_MENOS";
                break;
            case 28:
                tokenUnico = true;
                tokenName = "TK_MULTIPLICACAO";
                break;
            case 29:
                tokenUnico = true;
                tokenName = "TK_DIVISAO";
                break;
            case 30:
                tokenUnico = true;
                tokenName = "TK_NEGAR";
                break;
            case 31:
                tokenUnico = true;
                tokenName = "TK_E";
                break;
            case 32:
                tokenUnico = true;
                tokenName = "TK_OU";
                break;
        }
        if (tokenUnico) {
            lexema = "";
        }

        System.out.printf("| %-3d | %-3d | %-15s | %-21s |\n+-----+-----+-----------------+-----------------------+\n",
                line, column, tokenName, lexema);
        lexema = "";
        tokenUnico = false;

        // Incrementando o contador do token correspondente
        if (x >= 0 && x < contagemTokens.length) {
            contagemTokens[x]++;
        }
    }

    // Função para converter o arquivo em uma string (pega da internet)
    public static String fileToString(String filePath) {
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    // Função para imprimir a tabela com o contador de tokens
    public static void imprimirTabela() {
        // Imprimindo o cabeçalho da tabela
        System.out.println("+-----------------+-------------------+");
        System.out.println("|     Token       |     Contagem      |");
        System.out.println("+-----------------+-------------------+");

        // Criar uma cópia dos arrays para ordenação
        String[] nomesTokensOrdenados = Arrays.copyOf(nomesTokens, nomesTokens.length);
        int[] contagemTokensOrdenados = Arrays.copyOf(contagemTokens, contagemTokens.length);

        // Ordenar os arrays baseados na contagem de tokens
        for (int i = 0; i < contagemTokensOrdenados.length - 1; i++) {
            for (int j = i + 1; j < contagemTokensOrdenados.length; j++) {
                if (contagemTokensOrdenados[i] < contagemTokensOrdenados[j]) {
                    // Trocar as contagens
                    int tempContagem = contagemTokensOrdenados[i];
                    contagemTokensOrdenados[i] = contagemTokensOrdenados[j];
                    contagemTokensOrdenados[j] = tempContagem;

                    // Trocar os tokens correspondentes
                    String tempToken = nomesTokensOrdenados[i];
                    nomesTokensOrdenados[i] = nomesTokensOrdenados[j];
                    nomesTokensOrdenados[j] = tempToken;
                }
            }
        }

        // Imprimir os tokens ordenados por contagem
        for (int j = 0; j < nomesTokensOrdenados.length; j++) {
            int contagem = contagemTokensOrdenados[j];
            if (contagem > 0) {
                System.out.printf("| %-15s | %-17d |\n+-----------------+-------------------+\n",
                        nomesTokensOrdenados[j], contagem);
            }
        }

    }

}
