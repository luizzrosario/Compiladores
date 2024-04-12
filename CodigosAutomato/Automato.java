import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Automato {
    private static int estado = 0;

    public static void main(String[] args) {
        String codigo = fileToString("../CodigosAutomato/codigo.txt");

        for (char c : codigo.toCharArray()) {
            System.out.printf("%c", c);
            if (estado == 0) {
                if (Character.isDigit(c)) {
                    estado = 1;
                } else if (c == '.') {
                    estado = 9;
                } else if (c == ' ') {
                    estado = 0;
                }
                else {
                    retornaToken(0);
                }
            } else if (estado == 1) {
                if (Character.isDigit(c)) {
                    estado = 2;
                } else if (c == '.') {
                    estado = 5;
                } else {
                    retornaToken(1);
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
                    estado = 4;
                } else if (c == '.') {
                    retornaToken(0);
                } else {
                    retornaToken(1);
                }
            } else if (estado == 5) {
                if (Character.isDigit(c)) {
                    estado = 5;
                } else if (c == 'e') {
                    estado = 6;
                } else {
                    retornaToken(2);
                }
            } else if (estado == 6) {
                if (Character.isDigit(c)) {
                    estado = 7;
                } else if (c == '-') {
                    estado = 8;
                } else {
                    retornaToken(0);
                }
            } else if (estado == 7) {
                if (Character.isDigit(c)) {
                    estado = 7;
                } 
                else {
                    retornaToken(2);
                }
            } else if (estado == 8) {
                if (Character.isDigit(c)) {
                    estado = 7;
                } else {
                    retornaToken(0);
                }
            } else if (estado == 9) {
                if (Character.isDigit(c)) {
                    estado = 5;
                } else {
                    retornaToken(0);
                }
            }
            //System.out.println("->" + estado);
        }
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
