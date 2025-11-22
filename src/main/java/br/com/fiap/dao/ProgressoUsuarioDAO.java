package br.com.fiap.dao;

import br.com.fiap.excessoes.DaoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProgressoUsuarioDAO {


    // Busca o status da lição anterior (Lição de número N-1)
    private static final String SQL_BUSCAR_STATUS_ANTERIOR =
            "SELECT T2.status_licao FROM TB_LICOES T1 " +
                    "JOIN TB_PROGRESSO_USUARIO T2 ON T1.id_licao = T2.id_licao " +
                    "WHERE T1.numero_licao = ? - 1 " + // Lição anterior
                    "AND T2.id_usuario = ?";

    private static final String SQL_ATUALIZAR_STATUS =
            "MERGE INTO TB_PROGRESSO_USUARIO T " +
                    "USING (SELECT ? AS id_usuario, ? AS id_licao, ? AS status_licao FROM DUAL) S " +
                    "ON (T.id_usuario = S.id_usuario AND T.id_licao = S.id_licao) " +
                    "WHEN MATCHED THEN UPDATE SET T.status_licao = S.status_licao " +
                    "WHEN NOT MATCHED THEN INSERT (id_usuario, id_licao, status_licao) " +
                    "VALUES (S.id_usuario, S.id_licao, S.status_licao)";

    private static final String SQL_BUSCAR_STATUS =
            "SELECT status_licao FROM TB_PROGRESSO_USUARIO WHERE id_usuario = ? AND id_licao = ?";

    /**
     * Verifica se a lição imediatamente anterior (N-1) foi concluída.
     * ATENÇÃO: Assume que a lição anterior está na mesma trilha (melhor feito no BO/Service).
     * @param conn A conexão com o banco.
     * @param idUsuario O ID do usuário.
     * @param numeroLicaoAtual O número da lição que o usuário tenta acessar.
     * @return true se a lição anterior foi 'CONCLUIDA', false caso contrário.
     * @throws DaoException Se ocorrer um erro de banco de dados.
     */
    public boolean liçaoAnteriorConcluida(Connection conn, int idUsuario, int numeroLicaoAtual) {
        if (numeroLicaoAtual == 1) {
            return true; // Lição 1 não tem pré-requisito
        }

        try (PreparedStatement ps = conn.prepareStatement(SQL_BUSCAR_STATUS_ANTERIOR)) {

            ps.setInt(1, numeroLicaoAtual);
            ps.setInt(2, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String status = rs.getString("status_licao");
                    return "CONCLUIDA".equalsIgnoreCase(status);
                }
                return false; // Não existe registro da lição anterior, logo, não foi concluída
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao verificar lição anterior.", e);
        }
    }


    /**
     * Busca o status atual da lição para um usuário.
     * @param conn A conexão com o banco.
     * @param idUsuario O ID do usuário.
     * @param idLicao O ID da lição.
     * @return O status da lição ('CONCLUIDA', 'REVISAO') ou null se não houver registro.
     * @throws DaoException Se ocorrer um erro de banco de dados.
     */
    public String buscarStatusLicao(Connection conn, int idUsuario, int idLicao) {

        try (PreparedStatement ps = conn.prepareStatement(SQL_BUSCAR_STATUS)) {

            ps.setInt(1, idUsuario);
            ps.setInt(2, idLicao);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("status_licao");
                }
            }
            return null;

        } catch (SQLException e) {
            throw new DaoException("Erro ao buscar o status de progresso do usuário.", e);
        }
    }


    /**
     * Atualiza o status da lição para um usuário. Se a combinação (usuario, licao) não existe, insere.
     * @param conn A conexão com o banco.
     * @param idUsuario O ID do usuário.
     * @param idLicao O ID da lição.
     * @param status O novo status ('CONCLUIDA' ou 'REVISAO').
     * @throws DaoException Se ocorrer um erro de banco de dados.
     */
    public void atualizarStatusLicao(Connection conn, int idUsuario, int idLicao, String status) {

        try (PreparedStatement ps = conn.prepareStatement(SQL_ATUALIZAR_STATUS)) {

            // Parâmetros para a instrução MERGE
            ps.setInt(1, idUsuario);
            ps.setInt(2, idLicao);
            ps.setString(3, status);

            ps.executeUpdate();

            System.out.println("Progresso do usuário " + idUsuario + " na lição " + idLicao + " atualizado para: " + status);

        } catch (SQLException e) {
            throw new DaoException("Erro ao atualizar o status de progresso do usuário.", e);
        }
    }
}