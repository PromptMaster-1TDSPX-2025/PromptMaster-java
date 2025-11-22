package br.com.fiap.beans;

public class TentativasPrompt {

    private int id;
    private Usuario usuario;
    private Licoes licoes;
    private String promptUsuario;

    public TentativasPrompt(int id, Usuario usuario, Licoes licoes, String promptUsuario) {
        this.id = id;
        this.usuario = usuario;
        this.licoes = licoes;
        this.promptUsuario = promptUsuario;
    }

    public TentativasPrompt() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Licoes getLicoes() {
        return licoes;
    }

    public void setLicoes(Licoes licoes) {
        this.licoes = licoes;
    }

    public String getPromptUsuario() {
        return promptUsuario;
    }

    public void setPromptUsuario(String promptUsuario) {
        this.promptUsuario = promptUsuario;
    }
}
