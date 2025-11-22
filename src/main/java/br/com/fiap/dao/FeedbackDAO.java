package br.com.fiap.dao;

import br.com.fiap.beans.Feedback;
import br.com.fiap.excessoes.DaoException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FeedbackDAO {

    /**
     * Insere um novo registro de feedback na tabela TB_FEEDBACK.
     * @param conn A conexão com o banco (gerenciada pelo BO).
     * @param feedback O objeto Feedback a ser salvo.
     * @throws DaoException Se ocorrer um erro de banco de dados.
     */
    public void inserirFeedback(Connection conn, Feedback feedback) {

        String sql = "INSERT INTO TB_FEEDBACK (id_tentativa_prompt, nota, feedback) VALUES (?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {


            ps.setInt(1, feedback.getId());

            ps.setInt(2, feedback.getNota());

            ps.setString(3, feedback.getFeedback());

            ps.executeUpdate();

            System.out.println("Feedback inserido com sucesso ");

        } catch (SQLException e) {
            throw new DaoException("Erro ao inserir Feedback na TB_FEEDBACK.", e);
        }
    }
}