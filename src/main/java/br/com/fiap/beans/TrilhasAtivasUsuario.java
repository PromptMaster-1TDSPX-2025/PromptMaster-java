package br.com.fiap.beans;

import java.time.LocalDateTime;

public class TrilhasAtivasUsuario {

    private Usuario usuario;
    private Trilhas trilhas;
    private LocalDateTime dataInicio;
    private String status;

    public TrilhasAtivasUsuario(Usuario usuario, Trilhas trilhas, LocalDateTime dataInicio, String status) {
        this.usuario = usuario;
        this.trilhas = trilhas;
        this.dataInicio = dataInicio;
        this.status = status;
    }

    public TrilhasAtivasUsuario() {}

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Trilhas getTrilhas() {
        return trilhas;
    }

    public void setTrilhas(Trilhas trilhas) {
        this.trilhas = trilhas;
    }

    public LocalDateTime getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDateTime dataInicio) {
        this.dataInicio = dataInicio;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
