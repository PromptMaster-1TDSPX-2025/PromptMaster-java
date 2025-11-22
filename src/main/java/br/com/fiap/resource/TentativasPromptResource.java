package br.com.fiap.resource;

import br.com.fiap.beans.TentativasPrompt;
import br.com.fiap.bo.TentativasPromptBO;
import br.com.fiap.excessoes.DaoException;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.sql.SQLException;

@Path("/prompt")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TentativasPromptResource {

    @Inject
    private TentativasPromptBO tentativasPromptBO;

    /**
     * Endpoint para submeter a resposta de um aluno e gerar feedback.
     */
    @POST
    @Path("/feedback")
    public Response submeterParaFeedback(TentativasPrompt tentativaPrompt) {

        // Validação de dados de entrada
        if (tentativaPrompt == null ||
                tentativaPrompt.getPromptUsuario() == null || tentativaPrompt.getPromptUsuario().trim().isEmpty() ||
                tentativaPrompt.getUsuario() == null || tentativaPrompt.getUsuario().getId() == 0 ||
                tentativaPrompt.getLicoes() == null || tentativaPrompt.getLicoes().getId() == 0)
        {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Dados de submissão incompletos. O prompt, o ID do usuário e o ID da lição são obrigatórios.").build();
        }

        int idUsuario = tentativaPrompt.getUsuario().getId();
        int idLicao = tentativaPrompt.getLicoes().getId();

        try {
            // PRÉ-VALIDAÇÕES (Garantindo que o usuário pode fazer a lição)
            // Estas chamadas abrem e fecham a conexão internamente no BO.
            tentativasPromptBO.validarTrilhaAtiva(idUsuario, idLicao);
            tentativasPromptBO.validarAcessoLicao(idUsuario, idLicao);
            tentativasPromptBO.validarVidas(idUsuario); // O erro de "vidas esgotadas" é bloqueado aqui.

            // Chama a lógica de negócio que executa a transação completa
            String feedbackTexto = tentativasPromptBO.gerarFeedbackDoPrompt(tentativaPrompt);

            // Retorno de sucesso (201 Created)
            return Response.status(Response.Status.CREATED)
                    .entity(feedbackTexto)
                    .build();

        } catch (IllegalArgumentException e) {
            // Captura erros de validação de negócio (Acesso negado, Sem vidas, Lição não existe)
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage()).build();

        } catch (DaoException e) {
            Throwable cause = e.getCause();

            if (cause instanceof SQLException) {
                SQLException sqlEx = (SQLException) cause;
                System.err.println("Erro de SQL na submissão: " + sqlEx.getMessage());
                // Se o erro for violação de Foreign Key (código 2291 no Oracle)
                if (sqlEx.getErrorCode() == 2291) {
                    return Response.status(Response.Status.NOT_FOUND) // 404 NOT FOUND
                            .entity("O ID do Usuário ou da Lição não existe no sistema.").build();
                }
            }
            // Erros gerais de persistência (500)
            System.err.println("Erro na camada de persistência durante a submissão: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro interno do servidor ao processar a submissão.").build();

        } catch (Exception e) {
            // Captura erros da API Gemini ou outros erros inesperados (500)
            System.err.println("Erro inesperado durante a geração de feedback: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro interno inesperado na geração de feedback.").build();
        }
    }
}