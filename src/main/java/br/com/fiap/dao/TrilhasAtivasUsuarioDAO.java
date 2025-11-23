package br.com.fiap.dao;

import br.com.fiap.excessoes.DaoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TrilhasAtivasUsuarioDAO {

    // Status da trilha ativa
    private static final String STATUS_ATIVA = "ATIVA";

    // SQL para inserir o registro de ativação da trilha
    private static final String SQL_INSERIR_ATIVA =
            "INSERT INTO TB_TRILHAS_ATIVAS_USUARIO (id_usuario, id_trilha, data_inicio_trilha, status) VALUES (?, ?, ?, ?)";

    // SQL para verificar se a trilha está ATIVA
    private static final String SQL_ESTA_ATIVA =
            "SELECT COUNT(1) FROM TB_TRILHAS_ATIVAS_USUARIO WHERE id_usuario = ? AND id_trilha = ? AND status = ?";

    /**
     * Insere um novo registro de trilha ativa para o usuário.
     * @param conn A conexão com o banco.
     * @param idUsuario ID do usuário.
     * @param idTrilha ID da trilha a ser ativada.
     * @throws DaoException Se a trilha já estiver ativa (violação de UK) ou outro erro de banco.
     */
    public void inserirTrilhaAtiva(Connection conn, int idUsuario, int idTrilha) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_INSERIR_ATIVA)) {

            ps.setInt(1, idUsuario);
            ps.setInt(2, idTrilha);
            ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(4, STATUS_ATIVA);

            ps.executeUpdate();

            System.out.println("Trilha " + idTrilha + " ativada para o usuário " + idUsuario);

        } catch (SQLException e) {
            if (e.getErrorCode() == 1) {
                System.err.println("Trilha já estava ativa (ID Usuário: " + idUsuario + ", ID Trilha: " + idTrilha + ").");
            } else {
                throw new DaoException("Erro ao inserir/ativar a trilha do usuário.", e);
            }
        }
    }

    /**
     * Verifica se a trilha está ativa para o usuário.
     * @param conn A conexão com o banco.
     * @param idUsuario ID do usuário.
     * @param idTrilha ID da trilha a ser verificada.
     * @return true se a trilha estiver com o status 'ATIVA', false caso contrário.
     * @throws DaoException Em caso de erro de banco de dados.
     */
    public boolean isTrilhaAtiva(Connection conn, int idUsuario, int idTrilha) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_ESTA_ATIVA)) {

            ps.setInt(1, idUsuario);
            ps.setInt(2, idTrilha);
            ps.setString(3, STATUS_ATIVA);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
                return false;
            }

        } catch (SQLException e) {
            throw new DaoException("Erro ao verificar se a trilha está ativa.", e);
        }
    }


    /**
     * Retorna uma lista com os IDs das trilhas que o usuário já ativou.
     */
    public List<Integer> buscarIdsTrilhasAtivas(Connection conn, int idUsuario) {

        String sql = "SELECT id_trilha FROM TB_TRILHAS_ATIVAS_USUARIO WHERE id_usuario = ? AND status = 'ATIVA'";

        List<Integer> ids = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt("id_trilha"));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao buscar trilhas ativas do usuário.", e);
        }
        return ids;
    }

}