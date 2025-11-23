package br.com.fiap.bo;

import br.com.fiap.beans.Feedback;
import br.com.fiap.beans.TentativasPrompt;
import br.com.fiap.conexoes.ConnectionManager;
import br.com.fiap.dao.TentativasPromptDAO;
import br.com.fiap.dao.TrilhasAtivasUsuarioDAO;
import br.com.fiap.excessoes.DaoException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * Classe responsável por orquestrar a lógica de negócio relacionada a tentativas de prompt.
 * Delega responsabilidades específicas para outras classes BO especializadas.
 */
@ApplicationScoped
public class TentativasPromptBO {

    @Inject
    private FeedbackBO feedbackBO;
    
    @Inject
    private ProgressoBO progressoBO;
    
    @Inject
    private VidasBO vidasBO;
    
    @Inject
    private OfensivaBO ofensivaBO;
    
    @Inject
    private ValidacaoBO validacaoBO;
    
    private TentativasPromptDAO tentativasPromptDAO;
    private TrilhasAtivasUsuarioDAO trilhasAtivasUsuarioDAO;

    public TentativasPromptBO() {
        tentativasPromptDAO = new TentativasPromptDAO();
        trilhasAtivasUsuarioDAO = new TrilhasAtivasUsuarioDAO();
    }

    /**
     * Orquestra a transação completa: Insere a prompt, gera feedback pela IA, salva o feedback (com a nota) e retorna o texto do feedback.
     * @param prompt O objeto TentativasPrompt a ser salvo.
     * @return O texto do feedback gerado pela IA.
     * @throws Exception Propagada pelo Resource.
     */
    public String gerarFeedbackDoPrompt(TentativasPrompt prompt) throws Exception {
        Connection conn = null;
        LocalDateTime dataPrompt = LocalDateTime.now();

        try {
            conn = new ConnectionManager().conexao();
            conn.setAutoCommit(false); // Início da transação

            int idUsuario = prompt.getUsuario().getId();
            int idLicao = prompt.getLicoes().getId();

            // INSERIR O PROMPT
            tentativasPromptDAO.inserirPrompt(conn, prompt);
            int idPromptGerado = prompt.getId();

            // GERAR E SALVAR O FEEDBACK (delega para FeedbackBO)
            Feedback feedback = feedbackBO.gerarESalvarFeedback(conn, prompt, idPromptGerado);
            int nota = feedback.getNota();
            String feedbackTexto = feedback.getFeedback();

            // PROCESSAR PROGRESSO (XP e Nível) - delega para ProgressoBO
            progressoBO.processarXpENivel(conn, idUsuario, idLicao, nota);

            // DEDUZIR VIDA SE NOTA INSUFICIENTE - delega para VidasBO
            if (nota < ProgressoBO.getNotaMinimaParaConclusao()) {
                vidasBO.deduzirVida(conn, idUsuario);
            }

            // PROCESSAR OFENSIVA SE NOTA SUFICIENTE - delega para OfensivaBO
            if (nota >= ProgressoBO.getNotaMinimaParaConclusao()) {
                ofensivaBO.processarOfensiva(conn, idUsuario, dataPrompt);
            }

            // ATUALIZAR STATUS DA LIÇÃO - delega para ProgressoBO
            progressoBO.atualizarStatusLicao(conn, idUsuario, idLicao, nota);

            // FINALIZAÇÃO DA TRANSAÇÃO
            conn.commit();

            return "NOTA:" + nota + "|FEEDBACK:" + feedbackTexto;

        } catch (DaoException | IllegalArgumentException e) {
            // --- Rollback (Se algo falhou) ---
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Erro ao tentar rollback: " + ex.getMessage());
                }
            }


            if (e instanceof IllegalArgumentException) {
                System.err.println("Erro na transação (VALIDAÇÃO/PRE-REQ): " + e.getMessage());
                throw e;
            }

            System.err.println("Erro na transação de prompt/feedback: " + e.getMessage());
            throw new Exception("Erro no banco de dados durante a geração/salvamento do feedback.", e);

        } catch (ClassNotFoundException e) {
            System.err.println("Driver JDBC não encontrado: " + e.getMessage());
            throw new Exception("Erro de configuração do sistema.", e);
        } catch (SQLException e) {
            // Rollback para erros de SQL remanescentes
            if (conn != null) { try { conn.rollback(); } catch (SQLException ex) { /* log */ } }
            throw new Exception("Erro de SQL não tratado.", e);
        } catch (Exception e) {
            // Rollback para qualquer outro erro inesperado
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { /* log */ }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    System.err.println("Erro ao fechar conexão: " + ex.getMessage());
                }
            }
        }
    }

    /**
     * Valida se o usuário tem vidas restantes antes de permitir o prompt.
     * Delega para VidasBO.
     * @param idUsuario ID do usuário
     * @throws IllegalArgumentException Se vidas == 0
     * @throws Exception Se houver erro na validação
     */
    public void validarVidas(int idUsuario) throws Exception {
        vidasBO.validarVidas(idUsuario);
    }

    /**
     * Valida se o usuário tem acesso à lição (se concluiu a anterior).
     * Delega para ValidacaoBO.
     * @param idUsuario ID do usuário
     * @param idLicao ID da lição
     * @throws IllegalArgumentException Se o acesso for negado
     * @throws Exception Se houver erro na validação
     */
    public void validarAcessoLicao(int idUsuario, int idLicao) throws Exception {
        validacaoBO.validarAcessoLicao(idUsuario, idLicao);
    }


    public void ativarTrilha(int idUsuario, int idTrilha) throws Exception {
        Connection conn = null;
        try {
            conn = new ConnectionManager().conexao();
            trilhasAtivasUsuarioDAO.inserirTrilhaAtiva(conn, idUsuario, idTrilha);
        } catch (DaoException e) {
            throw e;
        } catch (Exception e) {
            throw new Exception("Erro ao tentar ativar a trilha.", e);
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException ex) { System.err.println("Erro ao fechar conexão após ativação: " + ex.getMessage()); }
            }
        }
    }


    /**
     * Valida se a trilha associada à lição está ativa para o usuário.
     * Delega para ValidacaoBO.
     * @param idUsuario ID do usuário
     * @param idLicao ID da lição
     * @throws IllegalArgumentException Se a trilha não estiver ativa
     * @throws Exception Se houver erro na validação
     */
    public void validarTrilhaAtiva(int idUsuario, int idLicao) throws Exception {
        validacaoBO.validarTrilhaAtiva(idUsuario, idLicao);
    }
}