package br.com.fiap.beans;

public class ProgressoUsuario {

    private Usuario usuario;
    private Licoes licoes;
    private String statusLicao;

    public ProgressoUsuario(Usuario usuario, Licoes licoes, String statusLicao) {
        this.usuario = usuario;
        this.licoes = licoes;
        this.statusLicao = statusLicao;
    }

    public ProgressoUsuario() {}

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

    public String getStatusLicao() {
        return statusLicao;
    }

    public void setStatusLicao(String statusLicao) {
        this.statusLicao = statusLicao;
    }
}
