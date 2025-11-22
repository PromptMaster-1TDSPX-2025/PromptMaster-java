package br.com.fiap.beans;

public class Feedback {

    private int id;
    private int nota;
    private String feedback;

    public Feedback(int id, int nota, String feedback) {
        this.id = id;
        this.nota = nota;
        this.feedback = feedback;
    }

    public Feedback() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
