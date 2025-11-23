package br.com.fiap.resource;

import br.com.fiap.beans.LicaoDetalhada;
import br.com.fiap.beans.Trilhas;
import br.com.fiap.bo.TentativasPromptBO;
import br.com.fiap.bo.TrilhasBO;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

/**
 * Endpoint para gerenciar ações de Trilha (Ativação).
 */
@Path("/trilhas")
@Produces(MediaType.APPLICATION_JSON)
public class TrilhasResource {

    @Inject
    private TentativasPromptBO tentativasPromptBO;

    @Inject
    private TrilhasBO trilhasBO;

    /**
     * Endpoint para listar todas as trilhas disponíveis.
     * URL: GET /trilhas
     */
    @GET
    public Response listarTrilhas() {
        try {
            List<Trilhas> trilhas = trilhasBO.listarTrilhas();
            return Response.ok(trilhas).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao listar trilhas.").build();
        }
    }

    /**
     * Endpoint para ativar uma trilha para um usuário.
     * URL: /trilhas/{idUsuario}/ativar/{idTrilha}
     */
    @POST
    @Path("/{idUsuario}/ativar/{idTrilha}")
    public Response ativarTrilha(@PathParam("idUsuario") int idUsuario, @PathParam("idTrilha") int idTrilha) {

        // Validação básica se os IDs são válidos (não zero ou negativos)
        if (idUsuario <= 0 || idTrilha <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("IDs de usuário e trilha são obrigatórios e devem ser positivos.").build();
        }

        try {
            // Chama a lógica de negócio para inserir o registro em TB_TRILHAS_ATIVAS_USUARIO
            tentativasPromptBO.ativarTrilha(idUsuario, idTrilha);

            // Retorno de sucesso (200 OK)
            return Response.ok("Trilha ativada com sucesso.").build();

        } catch (IllegalArgumentException e) {
            // Captura erros específicos (se o BO detectar algum problema lógico)
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage()).build();

        } catch (Exception e) {
            System.err.println("Erro inesperado ao ativar trilha: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro interno ao processar a ativação da trilha.").build();
        }
    }

    /**
     * Retorna lista de IDs das trilhas que o usuário já ativou.
     * URL: GET /trilhas/ativas/{idUsuario}
     */
    @GET
    @Path("/ativas/{idUsuario}")
    public Response buscarTrilhasAtivasUsuario(@PathParam("idUsuario") int idUsuario) {
        try {
            List<Integer> idsAtivos = trilhasBO.listarIdsTrilhasAtivas(idUsuario);
            return Response.ok(idsAtivos).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Lista as lições de uma trilha com o status calculado para o usuário.
     * URL: GET /trilhas/{idTrilha}/licoes/{idUsuario}
     */
    @GET
    @Path("/{idTrilha}/licoes/{idUsuario}")
    public Response listarLicoesDaTrilha(@PathParam("idTrilha") int idTrilha, @PathParam("idUsuario") int idUsuario) {
        try {
            if (idTrilha <= 0 || idUsuario <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("IDs inválidos.").build();
            }

            List<LicaoDetalhada> licoes = trilhasBO.obterLicoesComStatus(idTrilha, idUsuario);
            return Response.ok(licoes).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao buscar lições da trilha.").build();
        }
    }

}