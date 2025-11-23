package br.com.fiap.bo;

import br.com.fiap.beans.Trilhas;
import br.com.fiap.conexoes.ConnectionManager;
import br.com.fiap.dao.TrilhasDAO;
import jakarta.enterprise.context.ApplicationScoped;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@ApplicationScoped
public class TrilhasBO {

    private TrilhasDAO trilhasDAO;

    public TrilhasBO() {
        this.trilhasDAO = new TrilhasDAO();
    }

    public List<Trilhas> listarTrilhas() throws Exception {
        Connection conn = null;
        try {
            conn = new ConnectionManager().conexao();
            return trilhasDAO.listarTodas(conn);
        } catch (Exception e) {
            throw new Exception("Erro ao buscar lista de trilhas.", e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}