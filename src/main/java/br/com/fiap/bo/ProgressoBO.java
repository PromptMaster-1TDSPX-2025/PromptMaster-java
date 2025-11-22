package br.com.fiap.bo;

import br.com.fiap.dao.ProgressoUsuarioDAO;
import br.com.fiap.dao.ProgressaoNivelDAO;
import br.com.fiap.dao.UsuarioDAO;
import jakarta.enterprise.context.ApplicationScoped;

import java.sql.Connection;

/**
 * Classe responsável pela lógica de negócio relacionada a progresso do usuário.
 * Inclui XP, nível e status de lições.
 */
@ApplicationScoped
public class ProgressoBO {

    private static final int NOTA_MINIMA_PARA_CONCLUSAO = 60;

    private ProgressoUsuarioDAO progressoUsuarioDAO;
    private UsuarioDAO usuarioDAO;
    private ProgressaoNivelDAO progressaoNivelDAO;

    public ProgressoBO() {
        progressoUsuarioDAO = new ProgressoUsuarioDAO();
        usuarioDAO = new UsuarioDAO();
        progressaoNivelDAO = new ProgressaoNivelDAO();
    }

    /**
     * Processa o ganho de XP e atualização de nível baseado na nota.
     * @param conn Conexão ativa com transação iniciada
     * @param idUsuario ID do usuário
     * @param idLicao ID da lição
     * @param nota Nota obtida na tentativa
     * @return XP ganho nesta tentativa
     * @throws Exception Se houver erro no processamento
     */
    public int processarXpENivel(Connection conn, int idUsuario, int idLicao, int nota) throws Exception {
        String statusAtual = progressoUsuarioDAO.buscarStatusLicao(conn, idUsuario, idLicao);
        int nivelAtual = usuarioDAO.buscarNivelAtual(conn, idUsuario);

        boolean primeiraConclusaoComSucesso =
                (statusAtual == null || !"CONCLUIDA".equals(statusAtual)) &&
                        nota >= NOTA_MINIMA_PARA_CONCLUSAO;

        int xpGanho = 0;
        if (primeiraConclusaoComSucesso) {
            xpGanho = nota;
            usuarioDAO.incrementarXp(conn, idUsuario, xpGanho);
        }

        // Lógica de Nível
        if (xpGanho > 0) {
            int novoTotalXp = usuarioDAO.buscarTotalXp(conn, idUsuario);
            int novoNivelElegivel = progressaoNivelDAO.buscarNivelPorXp(conn, novoTotalXp);

            if (novoNivelElegivel > nivelAtual) {
                usuarioDAO.atualizarNivel(conn, idUsuario, novoNivelElegivel);
            }
        }

        return xpGanho;
    }

    /**
     * Atualiza o status da lição baseado na nota obtida.
     * @param conn Conexão ativa com transação iniciada
     * @param idUsuario ID do usuário
     * @param idLicao ID da lição
     * @param nota Nota obtida na tentativa
     * @throws Exception Se houver erro na atualização
     */
    public void atualizarStatusLicao(Connection conn, int idUsuario, int idLicao, int nota) throws Exception {
        String statusAtual = progressoUsuarioDAO.buscarStatusLicao(conn, idUsuario, idLicao);
        String novoStatus;

        if ("CONCLUIDA".equals(statusAtual)) {
            novoStatus = "CONCLUIDA";
        } else if (nota >= NOTA_MINIMA_PARA_CONCLUSAO) {
            novoStatus = "CONCLUIDA";
        } else {
            novoStatus = "REVISAO";
        }

        // Atualizar o status de progresso
        if (statusAtual == null || !statusAtual.equals(novoStatus)) {
            progressoUsuarioDAO.atualizarStatusLicao(conn, idUsuario, idLicao, novoStatus);
        }
    }

    /**
     * Retorna a nota mínima necessária para conclusão de uma lição.
     */
    public static int getNotaMinimaParaConclusao() {
        return NOTA_MINIMA_PARA_CONCLUSAO;
    }
}

