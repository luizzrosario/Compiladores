import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Automato {
    // inicio no estado 0
    private static int estado = 0;
    private static int currentLine = 1;
    private static int currentColumn = 1;
    private static String lexema = "";
    private static boolean tokenUnico = false;

    public static void main(String[] args) {

        // convertendo o arquivo para uma array de char
        String codigo = fileToString("../CodigosAutomato/entrada.txt");

        System.out.println(
                "+-----+-----+-----------------+-----------------------+\n| LIN | COL |     TOKEN       |        LEXEMA         |\n+-----+-----+-----------------+-----------------------+");
        // um for each para percorrer o array
        for (char c : codigo.toCharArray()) {
            if (c != '\n') {
                lexema += c;
            }
            // printar o c a ser analisado
            // System.out.printf("%c", c);
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
                    estado = 0;
                } else {
                    retornaToken(0); // retorno do token de erro (caso 0)
                }
            } else if (estado == 1) {
                if (Character.isDigit(c)) {
                    estado = 2;
                } else if (c == 'x') {
                    estado = 11;
                } else if (c == '.') {
                    estado = 5;
                } else {
                    retornaToken(1); // retorno de token de inteiro (caso 1)
                }
            } else if (estado == 2) {
                if (Character.isDigit(c)) {
                    estado = 3;
                } else if (c == '.') {
                    estado = 5;
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
                    retornaToken(0); // aqui é a filtragem para mais que 4 dígitos antes do ponto
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
                    retornaToken(0); // aqui rejeita caso termine só com um '-' sem número
                }
            } else if (estado == 9) {
                if (Character.isDigit(c)) {
                    estado = 5;
                } else {
                    retornaToken(0); // rejeita só o '.'
                }
            } else if (estado == 10) {
                if (c == 'x') {
                    estado = 11;
                } else {
                    retornaToken(0);
                }
            } else if (estado == 11) {
                if (Character.isDigit(c) || (c >= 'A' && c <= 'F')) {
                    estado = 11;
                } else {
                    retornaToken(3);
                }
            } else if (estado == 12) {
                if (Character.isAlphabetic(c) && Character.isLowerCase(c)) {
                    switch (lexema) {
                        case "rotina":
                            retornaToken(5);
                            break;
                        case "fim_rotina":
                        case "se":
                        case "senao":
                        case "imprima":
                        case "leia":
                        case "para":
                        case "enquanto":

                        default:
                            estado = 12;
                            break;
                    }
                } else {
                    retornaToken(0);
                }

            } else if (estado == 13) {
                if (Character.isAlphabetic(c)) {
                    if (Character.isUpperCase(c)) {
                        estado = 14;
                    } else {
                        estado = 12;
                    }
                }
            } else if (estado == 14) {
                if (!Character.isAlphabetic(c)) {
                    retornaToken(4);
                } else if (Character.isAlphabetic(c) && Character.isLowerCase(c)) {
                    estado = 15;
                } else {
                    retornaToken(0);
                }
            } else if (estado == 15) {
                if (!Character.isAlphabetic(c)) {
                    retornaToken(4);
                } else if (Character.isAlphabetic(c) && Character.isUpperCase(c)) {
                    estado = 14;
                } else {
                    retornaToken(0);
                }
            }
            if (c == '\n') {
                currentLine++;
                currentColumn = 1;
            } else {
                currentColumn++;
            }
            // System.out.println("->" + estado); // verificar os estados na hora de debugar
        }

        // Verifica o ultimo estado (no caso quando o loop termina, pode ocorrer de não
        // ter retornado o token)
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
    }

    /*
     * Função para retornar o token
     * 0 = TK_ERRO
     * 1 = TK_INT
     * 2 = TK_FLOAT
     * 3 = TK_HEX
     * 4 = TK_ID
     * 5 = TK_PALAVRA
     */
    public static void retornaToken(int x) {
        retornaToken(x, currentLine, currentColumn);
    }

    public static void retornaToken(int x, int line, int column) {

        estado = 0;
        String tokenName = "";
        switch (x) {
            case 0:
                tokenName = "TK_ERRO";
                break;
            case 1:
                tokenName = "TK_INT";
                break;
            case 2:
                tokenName = "TK_FLOAT";
                break;
            case 3:
                tokenName = "TK_HEX";
                break;
            case 4:
                tokenName = "TK_ID";
                break;
            case 5:
                tokenName = "TK_PALAVRA";
                break;
        }
        if (tokenUnico) {
            lexema = "";
        }
        System.out.printf("| %-3d | %-3d | %-15s | %-21s |\n+-----+-----+-----------------+-----------------------+\n",
                line, column, tokenName, lexema);
        lexema = "";
        tokenUnico = false;
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
}
