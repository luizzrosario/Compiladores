public class Simbolo {
    // Atributos para representar um símbolo
    String token; // Token associado ao símbolo
    String lexema; // Lexema (nome) do símbolo
    String tipoDado; // Tipo de dado do símbolo (NUMERO ou CADEIA)
    Object valor; // Valor atribuído ao símbolo

    // Construtor para criar um novo símbolo
    public Simbolo(String token, String lexema, String tipoDado, Object valor) {
        // Inicializar os atributos com os valores passados como parâmetro
        this.token = token;
        this.lexema = lexema;
        this.tipoDado = tipoDado;
        // Verificar se o valor é nulo
        if (valor == null) {
            // Se o tipo de dado for NUMERO, atribuir o valor 0
            if (this.tipoDado.equals("NUMERO")) {
                this.valor = 0;
            }
            // Se o tipo de dado for CADEIA, atribuir uma cadeia vazia
            else if (this.tipoDado.equals("CADEIA")) {
                this.valor = "";
            }
        }
        // Se o valor não for nulo, atribuir o valor passado
        else {
            this.valor = valor;
        }
    }
}
