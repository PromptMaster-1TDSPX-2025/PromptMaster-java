package br.com.fiap.dao;

import br.com.fiap.beans.TentativasPrompt;
import br.com.fiap.excessoes.DaoException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TentativasPromptDAO {

    /**
     * Insere uma nova tentativa de prompt na tabela TB_TENTATIVAS_PROMPT e recupera o ID gerado.
     * @param conn A conexão com o banco (gerenciada pelo BO).
     * @param prompt O objeto TentativasPrompt a ser salvo.
     * @throws DaoException Se ocorrer um erro de banco de dados.
     */
    public void inserirPrompt(Connection conn, TentativasPrompt prompt) {

        String sql = "INSERT INTO TB_TENTATIVAS_PROMPT (id_usuario, id_licao, prompt_usuario) VALUES (?, ?, ?)";
        String[] colunasRetorno = { "ID_TENTATIVA_PROMPT" }; // Para recuperar o ID gerado

        try (PreparedStatement ps = conn.prepareStatement(sql, colunasRetorno)) {


            ps.setInt(1, prompt.getUsuario().getId());
            ps.setInt(2, prompt.getLicoes().getId());
            ps.setString(3, prompt.getPromptUsuario());

            int linhasAfetadas = ps.executeUpdate();

            if (linhasAfetadas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        prompt.setId(rs.getInt(1)); // Seta o ID gerado no bean para uso no FeedbackDAO
                    }
                }
            }

        } catch (SQLException e) {
            throw new DaoException("Erro ao inserir prompt.", e);
        }
    }
}