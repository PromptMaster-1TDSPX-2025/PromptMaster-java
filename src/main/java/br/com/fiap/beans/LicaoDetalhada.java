package br.com.fiap.beans;

/**
 *  DTO para o Front
 */

public class LicaoDetalhada {
    private int id;
    private int numeroLicao;
    private String titulo;
    private String status;

    public LicaoDetalhada() {}

    public LicaoDetalhada(int id, int numeroLicao, String titulo, String status) {
        this.id = id;
        this.numeroLicao = numeroLicao;
        this.titulo = titulo;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getNumeroLicao() { return numeroLicao; }
    public void setNumeroLicao(int numeroLicao) { this.numeroLicao = numeroLicao; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}