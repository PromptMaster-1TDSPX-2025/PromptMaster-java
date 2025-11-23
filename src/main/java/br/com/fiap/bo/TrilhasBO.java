package br.com.fiap.bo;

import br.com.fiap.beans.Trilhas;
import br.com.fiap.conexoes.ConnectionManager;
import br.com.fiap.dao.TrilhasAtivasUsuarioDAO;
import br.com.fiap.dao.TrilhasDAO;
import jakarta.enterprise.context.ApplicationScoped;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@ApplicationScoped
public class TrilhasBO {

    private TrilhasDAO trilhasDAO;
    private TrilhasAtivasUsuarioDAO trilhasAtivasUsuarioDAO;

    public TrilhasBO() {
        this.trilhasDAO = new TrilhasDAO();
        this.trilhasAtivasUsuarioDAO = new TrilhasAtivasUsuarioDAO();
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

    /**
     * Busca a lista de IDs de trilhas ativas para um usuário específico.
     */
    public List<Integer> listarIdsTrilhasAtivas(int idUsuario) throws Exception {
        Connection conn = null;
        try {
            conn = new ConnectionManager().conexao();
            return trilhasAtivasUsuarioDAO.buscarIdsTrilhasAtivas(conn, idUsuario);
        } catch (Exception e) {
            throw new Exception("Erro ao buscar trilhas ativas.", e);
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

}