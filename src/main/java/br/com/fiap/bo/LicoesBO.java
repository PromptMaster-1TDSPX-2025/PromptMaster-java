package br.com.fiap.bo;

import br.com.fiap.beans.Licoes;
import br.com.fiap.conexoes.ConnectionManager;
import br.com.fiap.dao.LicoesDAO;

import java.sql.Connection;
import java.sql.SQLException;

public class LicoesBO {

    private LicoesDAO licoesDAO;

    public LicoesBO() {
        this.licoesDAO = new LicoesDAO();
    }

    /**
     * Busca os detalhes completos de uma lição (Teoria, Instruções, etc).
     */
    public Licoes buscarLicaoPorId(int idLicao) throws Exception {
        Connection conn = null;
        try {
            conn = new ConnectionManager().conexao();
            return licoesDAO.buscarInstrucoesPorId(conn, idLicao);
        } catch (Exception e) {
            throw new Exception("Erro ao buscar detalhes da lição.", e);
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

}
