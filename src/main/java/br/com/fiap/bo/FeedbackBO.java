package br.com.fiap.bo;

import br.com.fiap.api.GeminiApi;
import br.com.fiap.beans.Feedback;
import br.com.fiap.beans.Licoes;
import br.com.fiap.beans.TentativasPrompt;
import br.com.fiap.dao.FeedbackDAO;
import br.com.fiap.dao.LicoesDAO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.sql.Connection;

/**
 * Classe responsável pela lógica de negócio relacionada a feedback.
 * Inclui parsing de resposta da API e geração de feedback.
 */
@ApplicationScoped
public class FeedbackBO {

    @Inject
    private GeminiApi geminiApi;
    
    private FeedbackDAO feedbackDAO;
    private LicoesDAO licoesDAO;

    public FeedbackBO() {
        feedbackDAO = new FeedbackDAO();
        licoesDAO = new LicoesDAO();
    }

    /**
     * Processa o feedback completo da API, extraindo nota e texto.
     * @param feedbackCompletoDaApi Resposta completa da API no formato "NOTA:X|FEEDBACK:texto"
     * @return Objeto Feedback com nota e texto processados
     */
    public Feedback processarFeedbackDaApi(String feedbackCompletoDaApi) {
        int nota = parseNota(feedbackCompletoDaApi);
        String feedbackTexto = parseFeedbackText(feedbackCompletoDaApi);
        return new Feedback(0, nota, feedbackTexto); // id será definido depois
    }

    /**
     * Gera feedback usando a API Gemini e salva no banco.
     * @param conn Conexão ativa com transação iniciada
     * @param prompt Prompt do usuário
     * @param idPromptGerado ID do prompt já inserido
     * @return Objeto Feedback salvo com ID
     * @throws Exception Se houver erro na geração ou salvamento
     */
    public Feedback gerarESalvarFeedback(Connection conn, TentativasPrompt prompt, int idPromptGerado) throws Exception {
        int idLicao = prompt.getLicoes().getId();
        
        // Buscar o contexto da lição
        Licoes licao = licoesDAO.buscarInstrucoesPorId(conn, idLicao);
        
        if (licao == null) {
            throw new IllegalArgumentException("Lição com ID " + idLicao + " não encontrada para gerar feedback.");
        }

        String conteudoTeorico = licao.getConteudoTeorico();
        String instrucaoExercicio = licao.getInstrucaoExercicio();
        String criteriosAvaliacao = licao.getCriteriosAvaliacao();
        String promptUsuario = prompt.getPromptUsuario();

        // Chamar a API Gemini
        String feedbackCompletoDaApi = geminiApi.gerarFeedback(
            promptUsuario, 
            conteudoTeorico, 
            instrucaoExercicio, 
            criteriosAvaliacao
        );

        // Processar a resposta da API
        Feedback feedback = processarFeedbackDaApi(feedbackCompletoDaApi);
        feedback.setIdTentativaPrompt(idPromptGerado);

        // Salvar o feedback
        feedbackDAO.inserirFeedback(conn, feedback);

        return feedback;
    }

    /**
     * Método auxiliar para parsear a nota da resposta delimitada
     */
    private int parseNota(String feedbackCompleto) {
        try {
            String trimmedFeedback = feedbackCompleto.trim();

            if (trimmedFeedback.startsWith("NOTA:")) {
                String[] parts = trimmedFeedback.split("\\|");
                String scorePart = parts[0].replace("NOTA:", "").trim();
                return Integer.parseInt(scorePart);
            }
            return 0;
        } catch (Exception e) {
            System.err.println("Erro ao parsear a nota da API: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Método auxiliar para extrair o texto do feedback da resposta delimitada
     */
    private String parseFeedbackText(String feedbackCompleto) {
        try {
            if (feedbackCompleto.contains("|FEEDBACK:")) {
                return feedbackCompleto.substring(
                    feedbackCompleto.indexOf("|FEEDBACK:") + "|FEEDBACK:".length()
                ).trim();
            }
            return feedbackCompleto;
        } catch (Exception e) {
            System.err.println("Erro ao extrair texto do feedback: " + e.getMessage());
            return "Falha na leitura do feedback completo.";
        }
    }
}

