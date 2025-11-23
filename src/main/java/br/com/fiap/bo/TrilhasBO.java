package br.com.fiap.bo;

import br.com.fiap.beans.LicaoDetalhada;
import br.com.fiap.beans.Licoes;
import br.com.fiap.beans.Trilhas;
import br.com.fiap.conexoes.ConnectionManager;
import br.com.fiap.dao.LicoesDAO;
import br.com.fiap.dao.ProgressoUsuarioDAO;
import br.com.fiap.dao.TrilhasAtivasUsuarioDAO;
import br.com.fiap.dao.TrilhasDAO;
import jakarta.enterprise.context.ApplicationScoped;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class TrilhasBO {

    private TrilhasDAO trilhasDAO;
    private TrilhasAtivasUsuarioDAO trilhasAtivasUsuarioDAO;
    private LicoesDAO licoesDAO;
    private ProgressoUsuarioDAO progressoUsuarioDAO;

    public TrilhasBO() {
        this.trilhasDAO = new TrilhasDAO();
        this.trilhasAtivasUsuarioDAO = new TrilhasAtivasUsuarioDAO();
        this.licoesDAO = new LicoesDAO();
        this.progressoUsuarioDAO = new ProgressoUsuarioDAO();
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


    /**
     * Retorna a lista de lições de uma trilha com o status calculado para o usuário (Visualização do Mapa).
     */
    public List<LicaoDetalhada> obterLicoesComStatus(int idTrilha, int idUsuario) throws Exception {
        Connection conn = null;
        List<LicaoDetalhada> resultado = new ArrayList<>();

        try {
            conn = new ConnectionManager().conexao();

            // Pega todas as lições da trilha ordenadas
            List<Licoes> todasLicoes = licoesDAO.listarPorTrilha(conn, idTrilha);

            // A primeira lição está sempre liberada se a trilha estiver ativa
            boolean anteriorConcluida = true;

            for (Licoes licao : todasLicoes) {
                LicaoDetalhada dto = new LicaoDetalhada();
                dto.setId(licao.getId());
                dto.setNumeroLicao(licao.getNumeroLicao());
                dto.setTitulo(licao.getTitulo());

                // Verifica status dessa lição específica no banco
                String statusBanco = progressoUsuarioDAO.buscarStatusLicao(conn, idUsuario, licao.getId());
                boolean estaConcluida = "CONCLUIDA".equalsIgnoreCase(statusBanco);

                // Define o status visual para o Front
                if (estaConcluida) {
                    dto.setStatus("completed");
                    anteriorConcluida = true; // Libera a próxima no loop
                } else if (anteriorConcluida) {
                    dto.setStatus("current"); // É a vez dessa lição
                    anteriorConcluida = false; // As próximas estarão bloqueadas
                } else {
                    dto.setStatus("locked");
                }

                resultado.add(dto);
            }

            return resultado;

        } catch (Exception e) {
            throw new Exception("Erro ao processar status das lições.", e);
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }


}