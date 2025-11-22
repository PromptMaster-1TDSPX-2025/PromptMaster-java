package br.com.fiap.dao;

import br.com.fiap.beans.Licoes;
import br.com.fiap.excessoes.DaoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LicoesDAO {


    private static final String SQL_BUSCAR_DADOS_PROGRESSAO =
            "SELECT id_trilha, numero_licao FROM TB_LICOES WHERE id_licao = ?";


    private static final String SQL_BUSCAR_INSTRUCOES =
            "SELECT id_licao, titulo_licao, conteudo_teorico_licao, instrucao_exercicio, criterios_avaliacao_licao FROM TB_LICOES WHERE id_licao = ?";

    /**
     * Busca informações de progressão (ID Trilha, Número Lição) pelo ID da lição.
     * @param conn A conexão com o banco
     * @param idLicao O ID da lição.
     * @return Objeto Licoes contendo ID e número da lição/trilha, ou null se não encontrado.
     * @throws DaoException Se ocorrer um erro de banco de dados.
     */
    public Licoes buscarDadosProgressao(Connection conn, int idLicao) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_BUSCAR_DADOS_PROGRESSAO)) {
            ps.setInt(1, idLicao);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Licoes licao = new Licoes();

                    licao.setId(idLicao);
                    licao.setNumeroLicao(rs.getInt("NUMERO_LICAO"));

                    return licao;
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao buscar dados de progressão da lição.", e);
        }
    }


    /**
     * Busca o conteúdo teórico e a instrução do exercício de uma lição pelo ID (MANTIDO).
     * @param idLicao O ID da lição.
     * @param conn A conexão com o banco
     * @return O objeto Licoes contendo os detalhes, ou null se não encontrado.
     * @throws DaoException Se ocorrer um erro de banco de dados.
     */
    public Licoes buscarInstrucoesPorId(Connection conn, int idLicao) {

        String sql = SQL_BUSCAR_INSTRUCOES;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idLicao);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Licoes licao = new Licoes();
                    licao.setId(rs.getInt("ID_LICAO"));
                    licao.setTitulo(rs.getString("TITULO_LICAO"));

                    licao.setConteudoTeorico(rs.getString("CONTEUDO_TEORICO_LICAO"));
                    licao.setInstrucaoExercicio(rs.getString("INSTRUCAO_EXERCICIO"));
                    licao.setCriteriosAvaliacao(rs.getString("CRITERIOS_AVALIACAO_LICAO"));

                    return licao;
                }
                return null; // Lição não encontrada
            }

        } catch (SQLException e) {
            throw new DaoException("Erro ao buscar lição por ID.", e);
        }
    }

    private static final String SQL_BUSCAR_ID_TRILHA =
            "SELECT id_trilha FROM TB_LICOES WHERE id_licao = ?";

    public int buscarIdTrilha(Connection conn, int idLicao) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_BUSCAR_ID_TRILHA)) {
            ps.setInt(1, idLicao);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_trilha");
                }
                return 0; // 0 se não for encontrado
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao buscar ID da trilha da lição.", e);
        }
    }
}