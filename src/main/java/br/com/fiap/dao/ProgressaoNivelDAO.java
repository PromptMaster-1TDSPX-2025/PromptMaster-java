package br.com.fiap.dao;

import br.com.fiap.excessoes.DaoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProgressaoNivelDAO {

    // Busca o maior nível onde o XP necessário é menor ou igual ao XP do usuário
    private static final String SQL_BUSCAR_MAX_NIVEL =
            "SELECT MAX(nivel) AS nivel_elegivel FROM TB_PROGRESSAO_NIVEL WHERE xp_necessario <= ?";

    /**
     * Busca o nível mais alto que o usuário pode atingir com o total de XP fornecido.
     * @param conn A conexão com o banco.
     * @param totalXp O total de XP atual do usuário.
     * @return O nível máximo elegível, ou 1 (nível inicial) se nenhum nível for encontrado.
     * @throws DaoException Se ocorrer um erro de banco de dados.
     */
    public int buscarNivelPorXp(Connection conn, int totalXp) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_BUSCAR_MAX_NIVEL)) {

            ps.setInt(1, totalXp);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Garante que retorna no mínimo o Nível 1
                    int nivel = rs.getInt("nivel_elegivel");
                    return (nivel > 0) ? nivel : 1;
                }
            }
            return 1;

        } catch (SQLException e) {
            throw new DaoException("Erro ao buscar nível por XP.", e);
        }
    }
}