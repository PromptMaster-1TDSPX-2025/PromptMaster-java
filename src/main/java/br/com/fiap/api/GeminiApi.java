package br.com.fiap.api;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class GeminiApi {

    @Inject
    private Client client;

    private static final String MODELO = "gemini-2.5-flash";


    /**
     * Gera feedback construtivo e encorajador para uma submissão de prompt,
     * usando o contexto específico da lição fornecido. Retorna uma string delimitada
     * contendo a NOTA (0-100) e o TEXTO do feedback.
     * @param promptUsuario A submissão do aluno para o exercício.
     * @param conteudoTeorico O conteúdo teórico da lição (para contexto da IA).
     * @param instrucaoExercicio As instruções detalhadas do exercício (para contexto da IA).
     * @param criteriosAvaliacao OS CRITÉRIOS DE SUCESSO DA LIÇÃO (para a IA se basear na nota).
     * @return Uma String no formato: "NOTA:[0-100]|FEEDBACK:[Texto completo do feedback]".
     */
    public String gerarFeedback(String promptUsuario, String conteudoTeorico, String instrucaoExercicio, String criteriosAvaliacao) {

        // --- PROMPT DE ENGENHARIA FIXO (Define Papel, Formato e Requisito de Nota) ---
        String instrucaoBase =
                // 1. Define o Papel, Tom e Objetivo
                "Você é um tutor educacional educacional e especialista em Prompt Engineering. Seu objetivo é avaliar a submissão do aluno de forma construtiva e encorajadora. Use a língua portuguesa." +

                        // 2. Requisito de Nota
                        "\n\n**REQUISITO DE SAÍDA CRÍTICO:** Ao final da avaliação, você deve atribuir uma nota inteira de 0 a 100 para o prompt do aluno com base na correta aplicação dos CRITÉRIOS DE AVALIAÇÃO fornecidos." +

                        // 3. Define o Formato de Saída
                        "\n\n**TAREFA:** Avalie a submissão do aluno estritamente com base no CONTEÚDO, INSTRUÇÕES e nos CRITÉRIOS DE AVALIAÇÃO. Retorne a nota e o feedback no FORMATO ESPECÍFICO abaixo." +
                        "\n\n**FORMATO DE FEEDBACK (Parte do Texto CLOB):**" +
                        "\n\n**👏 O que foi bem feito:**" +
                        "\n[Liste os pontos fortes da submissão com base nos critérios de avaliação. Elogie o esforço.]" +
                        "\n\n**🚀 Pontos para melhorar:**" +
                        "\n[Liste os critérios que faltaram ou que precisam de mais clareza. Dê sugestões para aprimorar, focando no conteúdo teórico ensinado.]" +

                        // 4. Formato de Retorno Final (Delimitador para parsing)
                        "\n\n--- FIM DO FEEDBACK ---" +
                        "\n\n**FORMATO DE RETORNO FINAL (Obrigatório, APENAS ESTA LINHA):**" +
                        "\nNOTA:[score]|FEEDBACK:[Texto completo do feedback, incluindo as seções 👏 e 🚀]";


        // --- MONTAGEM DINÂMICA DO PROMPT ---
        String instrucaoCompleta =
                instrucaoBase +

                        "\n\n**CONTEÚDO TEÓRICO DA LIÇÃO:**" +
                        "\n" + conteudoTeorico +

                        "\n\n**INSTRUÇÕES DO EXERCÍCIO (CENÁRIO E TAREFA):**" +
                        "\n" + instrucaoExercicio +

                        "\n\n**CRITÉRIOS DE AVALIAÇÃO (Validação Interna):**" +
                        "\n" + criteriosAvaliacao +

                        "\n\n--- PROMPT DO ALUNO PARA AVALIAÇÃO ---" +
                        "\n" + promptUsuario +
                        "\n--- FIM DO PROMPT DO ALUNO ---";


        try {

            GenerateContentResponse response = this.client.models.generateContent(
                    MODELO,
                    instrucaoCompleta,
                    null
            );

            return response.text();

        } catch (Exception e) {
            System.err.println("Ocorreu um erro ao chamar a API Gemini para gerar feedback:");
            e.printStackTrace();
            return "NOTA:0|FEEDBACK:Erro ao gerar feedback: Não foi possível conectar com a API Gemini.";
        }
    }

}