package br.com.fiap.bo;

import br.com.fiap.beans.Licoes;
import br.com.fiap.dao.LicoesDAO;
import br.com.fiap.dao.ProgressoUsuarioDAO;
import br.com.fiap.dao.TrilhasAtivasUsuarioDAO;
import br.com.fiap.conexoes.ConnectionManager;
import jakarta.enterprise.context.ApplicationScoped;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Classe responsável pelas validações de negócio relacionadas a tentativas de prompt.
 * Inclui validação de trilha ativa, acesso à lição e sequência de lições.
 */
@ApplicationScoped
public class ValidacaoBO {

    private LicoesDAO licoesDAO;
    private ProgressoUsuarioDAO progressoUsuarioDAO;
    private TrilhasAtivasUsuarioDAO trilhasAtivasUsuarioDAO;

    public ValidacaoBO() {
        licoesDAO = new LicoesDAO();
        progressoUsuarioDAO = new ProgressoUsuarioDAO();
        trilhasAtivasUsuarioDAO = new TrilhasAtivasUsuarioDAO();
    }

    /**
     * Valida se a trilha associada à lição está ativa para o usuário.
     * @param idUsuario ID do usuário
     * @param idLicao ID da lição
     * @throws IllegalArgumentException Se a trilha não estiver ativa
     * @throws Exception Se houver erro na validação
     */
    public void validarTrilhaAtiva(int idUsuario, int idLicao) throws Exception {
        Connection conn = null;
        try {
            conn = new ConnectionManager().conexao();

            int idTrilha = licoesDAO.buscarIdTrilha(conn, idLicao);

            if (idTrilha == 0) {
                throw new IllegalArgumentException("Lição (ID " + idLicao + ") não encontrada ou sem trilha associada.");
            }

            boolean trilhaAtiva = trilhasAtivasUsuarioDAO.isTrilhaAtiva(conn, idUsuario, idTrilha);

            if (!trilhaAtiva) {
                throw new IllegalArgumentException("Acesso negado. A trilha deve ser ativada antes de submeter a lição.");
            }

        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    System.err.println("Erro ao fechar conexão após validação de trilha: " + ex.getMessage());
                }
            }
        }
    }

    /**
     * Valida se o usuário tem acesso à lição (se concluiu a anterior).
     * @param idUsuario ID do usuário
     * @param idLicao ID da lição
     * @throws IllegalArgumentException Se o acesso for negado
     * @throws Exception Se houver erro na validação
     */
    public void validarAcessoLicao(int idUsuario, int idLicao) throws Exception {
        Connection conn = null;
        try {
            conn = new ConnectionManager().conexao();

            Licoes licaoAtual = licoesDAO.buscarDadosProgressao(conn, idLicao);

            if (licaoAtual == null) {
                throw new IllegalArgumentException("Lição com ID " + idLicao + " não existe no sistema.");
            }

            int numeroLicaoAtual = licaoAtual.getNumeroLicao();

            if (numeroLicaoAtual == 1) {
                return; // Primeira lição sempre acessível
            }

            boolean anteriorConcluida = progressoUsuarioDAO.liçaoAnteriorConcluida(conn, idUsuario, numeroLicaoAtual);

            if (!anteriorConcluida) {
                throw new IllegalArgumentException("Acesso negado. Conclua a lição número " + (numeroLicaoAtual - 1) + " primeiro.");
            }

        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    System.err.println("Erro ao fechar conexão após validação de acesso: " + ex.getMessage());
                }
            }
        }
    }
}

