package br.com.fiap.beans;

import java.time.LocalDateTime;

public class OfensivaUsuario {

    private Usuario usuario;
    private LocalDateTime ultimaDataConclusao;
    private int diasOfensiva;

    public OfensivaUsuario(Usuario usuario, LocalDateTime ultimaDataConclusao, int diasOfensiva) {
        this.usuario = usuario;
        this.ultimaDataConclusao = ultimaDataConclusao;
        this.diasOfensiva = diasOfensiva;
    }

    public OfensivaUsuario(){}

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public LocalDateTime getUltimaDataConclusao() {
        return ultimaDataConclusao;
    }

    public void setUltimaDataConclusao(LocalDateTime ultimaDataConclusao) {
        this.ultimaDataConclusao = ultimaDataConclusao;
    }

    public int getDiasOfensiva() {
        return diasOfensiva;
    }

    public void setDiasOfensiva(int diasOfensiva) {
        this.diasOfensiva = diasOfensiva;
    }
}
