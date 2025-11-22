package br.com.fiap.dao;

import br.com.fiap.beans.OfensivaUsuario;
import br.com.fiap.excessoes.DaoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class OfensivaUsuarioDAO {

    private static final String SQL_ATUALIZAR_OFENSIVA =
            "MERGE INTO TB_OFENSIVA_USUARIO T " +
                    "USING (SELECT ? AS id_usuario, ? AS dias_ofensiva, ? AS ultima_data_conclusao FROM DUAL) S " +
                    "ON (T.id_usuario = S.id_usuario) " +
                    "WHEN MATCHED THEN UPDATE SET T.dias_ofensiva = S.dias_ofensiva, T.ultima_data_conclusao = S.ultima_data_conclusao " +
                    "WHEN NOT MATCHED THEN INSERT (id_usuario, dias_ofensiva, ultima_data_conclusao) " +
                    "VALUES (S.id_usuario, S.dias_ofensiva, S.ultima_data_conclusao)";

    private static final String SQL_BUSCAR_OFENSIVA =
            "SELECT dias_ofensiva, ultima_data_conclusao FROM TB_OFENSIVA_USUARIO WHERE id_usuario = ?";

    /**
     * Busca os dados de ofensiva do usuário.
     * @param conn A conexão com o banco.
     * @param idUsuario O ID do usuário.
     * @return O objeto OfensivaUsuario populado, ou null se não houver registro.
     * @throws DaoException Se ocorrer um erro de banco de dados.
     */
    public OfensivaUsuario buscarOfensivaPorIdUsuario(Connection conn, int idUsuario) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_BUSCAR_OFENSIVA)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    OfensivaUsuario ofensiva = new OfensivaUsuario();

                    Timestamp ultimaDataTS = rs.getTimestamp("ultima_data_conclusao");

                    if (ultimaDataTS != null) {
                        // Converte Timestamp do JDBC para LocalDateTime do bean
                        ofensiva.setUltimaDataConclusao(ultimaDataTS.toLocalDateTime());
                    }

                    ofensiva.setDiasOfensiva(rs.getInt("dias_ofensiva"));
                    return ofensiva;
                }
            }
            return null; // Retorna null se não houver registro (primeira vez)
        } catch (SQLException e) {
            throw new DaoException("Erro ao buscar dados de ofensiva do usuário.", e);
        }
    }

    /**
     * Atualiza o registro de ofensiva (ou insere, se for a primeira vez).
     * @param conn A conexão com o banco.
     * @param idUsuario O ID do usuário.
     * @param novoContador O novo valor para dias_ofensiva.
     * @param dataAtual A data e hora atual.
     * @throws DaoException Se ocorrer um erro de banco de dados.
     */
    public void atualizarOfensiva(Connection conn, int idUsuario, int novoContador, LocalDateTime dataAtual) {
        String sql = SQL_ATUALIZAR_OFENSIVA;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.setInt(2, novoContador);
            // Converte LocalDateTime para Timestamp para salvar no banco
            ps.setTimestamp(3, Timestamp.valueOf(dataAtual));

            ps.executeUpdate();

            System.out.println("Ofensiva do usuário " + idUsuario + " atualizada para: " + novoContador);
        } catch (SQLException e) {
            throw new DaoException("Erro ao atualizar a ofensiva do usuário.", e);
        }
    }

    // SQL para inserir o registro inicial com 0 dias
    private static final String SQL_INSERIR_INICIAL =
            "INSERT INTO TB_OFENSIVA_USUARIO (id_usuario, dias_ofensiva, ultima_data_conclusao) VALUES (?, 0, NULL)";

    /**
     * Insere o registro inicial de ofensiva (0 dias) para um novo usuário.
     */
    public void inserirRegistroInicial(Connection conn, int idUsuario) throws DaoException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_INSERIR_INICIAL)) {
            ps.setInt(1, idUsuario);
            ps.executeUpdate();
            System.out.println("Registro inicial de ofensiva criado para o usuário: " + idUsuario);
        } catch (SQLException e) {
            throw new DaoException("Erro ao inserir registro inicial de ofensiva do usuário.", e);
        }
    }

}