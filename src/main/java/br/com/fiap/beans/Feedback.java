package br.com.fiap.beans;

public class Feedback {

    private int idTentativaPrompt;
    private int nota;
    private String feedback;

    public Feedback(int idTentativaPrompt, int nota, String feedback) {
        this.idTentativaPrompt = idTentativaPrompt;
        this.nota = nota;
        this.feedback = feedback;
    }

    public Feedback() {}
    

    public int getIdTentativaPrompt() {
        return idTentativaPrompt;
    }

    public void setIdTentativaPrompt(int idTentativaPrompt) {
        this.idTentativaPrompt = idTentativaPrompt;
    }

    public int getNota() {
        return nota;
    }

    public void setNota(int nota) {
        this.nota = nota;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}
