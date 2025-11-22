package br.com.fiap.beans;

import java.time.LocalDateTime;

public class VidasUsuario {

    private int idUsuario;
    private int vidasAtuais;
    private LocalDateTime ultimaPerda;

    // Construtores
    public VidasUsuario() {}

    public VidasUsuario(int idUsuario, int vidasAtuais, LocalDateTime ultimaPerda) {
        this.idUsuario = idUsuario;
        this.vidasAtuais = vidasAtuais;
        this.ultimaPerda = ultimaPerda;
    }

    // Getters e Setters
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public int getVidasAtuais() { return vidasAtuais; }
    public void setVidasAtuais(int vidasAtuais) { this.vidasAtuais = vidasAtuais; }

    public LocalDateTime getUltimaPerda() { return ultimaPerda; }
    public void setUltimaPerda(LocalDateTime ultimaPerda) { this.ultimaPerda = ultimaPerda; }
}