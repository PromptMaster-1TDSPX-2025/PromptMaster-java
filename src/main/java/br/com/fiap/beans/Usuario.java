package br.com.fiap.beans;

public class Usuario {

    private int id;
    private String nome;
    private int idade;
    private int totalXp;
    private int nivel;

    public Usuario(int id, String nome, int idade, int totalXp, int nivel) {
        this.id = id;
        this.nome = nome;
        this.idade = idade;
        this.totalXp = totalXp;
        this.nivel = nivel;
    }

    public Usuario() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getIdade() {
        return idade;
    }

    public void setIdade(int idade) {
        this.idade = idade;
    }

    public int getTotalXp() {
        return totalXp;
    }

    public void setTotalXp(int totalXp) {
        this.totalXp = totalXp;
    }

    public int getNivel() {
        return nivel;
    }

    public void setNivel(int nivel) {
        this.nivel = nivel;
    }


}