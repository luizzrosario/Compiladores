import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Automato {
    // inicio no estado 0
    private static int estado = 0;

    public static void main(String[] args) {
        // convertendo o arquivo para uma array de char
        String codigo = fileToString("../CodigosAutomato/entrada.txt");

        // um for each para percorrer o array
        for (char c : codigo.toCharArray()) {
            // printar o c a ser analisado
            System.out.printf("%c", c);
            // basicamente o loop consiste em verificar o estado atual (em int) e o char
            // atual
            if (estado == 0) {
                if (Character.isDigit(c)) { // se for digito [0-9]
                    estado = 1;
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
    }

    // Função para retornar o token
    /*
     * 0 = TK_ERRO
     * 1 = TK_INT
     * 2 = TK_FLOAT
     */
    public static void retornaToken(int x) {
        switch (x) {
            case 0:
                estado = 0;
                System.out.println(" <- TK_ERRO");
                break;
            case 1:
                estado = 0;
                System.out.println(" <- TK_INT");
                break;
            case 2:
                estado = 0;
                System.out.println(" <- TK_FLOAT");
                break;
        }
    }

    // Função para converter o arquivo em uma string (pega da internet)
    public static String fileToString(String filePath) {
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }
}
