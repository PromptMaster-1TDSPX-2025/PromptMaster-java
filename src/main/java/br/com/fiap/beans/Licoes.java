package br.com.fiap.beans;

public class Licoes {

    private int id;
    private Trilhas trilhas;
    private int numeroLicao;
    private String titulo;
    private String conteudoTeorico;
    private String instrucaoExercicio;
    private String criteriosAvaliacao;

    public Licoes(int id, Trilhas trilhas, int numeroLicao, String titulo, String conteudoTeorico, String instrucaoExercicio, String criteriosAvaliacao) {
        this.id = id;
        this.trilhas = trilhas;
        this.numeroLicao = numeroLicao;
        this.titulo = titulo;
        this.conteudoTeorico = conteudoTeorico;
        this.instrucaoExercicio = instrucaoExercicio;
        this.criteriosAvaliacao = criteriosAvaliacao;
    }

    public Licoes() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Trilhas getTrilhas() {
        return trilhas;
    }

    public void setTrilhas(Trilhas trilhas) {
        this.trilhas = trilhas;
    }

    public int getNumeroLicao() {
        return numeroLicao;
    }

    public void setNumeroLicao(int numeroLicao) {
        this.numeroLicao = numeroLicao;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getConteudoTeorico() {
        return conteudoTeorico;
    }

    public void setConteudoTeorico(String conteudoTeorico) {
        this.conteudoTeorico = conteudoTeorico;
    }

    public String getInstrucaoExercicio() {
        return instrucaoExercicio;
    }

    public void setInstrucaoExercicio(String instrucaoExercicio) {
        this.instrucaoExercicio = instrucaoExercicio;
    }

    public String getCriteriosAvaliacao() {
        return criteriosAvaliacao;
    }

    public void setCriteriosAvaliacao(String criteriosAvaliacao) {
        this.criteriosAvaliacao = criteriosAvaliacao;
    }
}