package br.com.fiap.beans;

public class LoginUsuario {

    private Usuario usuario;
    private String login;
    private String senha;

    public LoginUsuario(Usuario usuario, String login, String senha) {
        this.usuario = usuario;
        this.login = login;
        this.senha = senha;
    }

    public LoginUsuario(){}

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
