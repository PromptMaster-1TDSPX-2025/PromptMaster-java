package br.com.fiap.beans;

public class ProgressaoNivel {

    private int nivel;
    private int xpNecessario;
    private String tituloNivel;

    public ProgressaoNivel(int nivel, int xpNecessario, String tituloNivel) {
        this.nivel = nivel;
        this.xpNecessario = xpNecessario;
        this.tituloNivel = tituloNivel;
    }

    public ProgressaoNivel() {}

    public int getNivel() {
        return nivel;
    }

    public void setNivel(int nivel) {
        this.nivel = nivel;
    }

    public int getXpNecessario() {
        return xpNecessario;
    }

    public void setXpNecessario(int xpNecessario) {
        this.xpNecessario = xpNecessario;
    }

    public String getTituloNivel() {
        return tituloNivel;
    }

    public void setTituloNivel(String tituloNivel) {
        this.tituloNivel = tituloNivel;
    }
}
