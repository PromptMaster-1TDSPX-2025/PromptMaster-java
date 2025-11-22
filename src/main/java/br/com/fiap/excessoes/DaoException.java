package br.com.fiap.excessoes;

public class DaoException extends RuntimeException {

    // Construtor que recebe a mensagem de erro
    public DaoException(String message) {
        super(message);
    }

    // Construtor que recebe a mensagem e a exceção original (SQLException)
    public DaoException(String message, Throwable cause) {
        super(message, cause);
    }
}