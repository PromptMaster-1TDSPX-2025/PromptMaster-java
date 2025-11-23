package br.com.fiap.resource;

import br.com.fiap.beans.Licoes;
import br.com.fiap.bo.LicoesBO;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/licoes")
@Produces(MediaType.APPLICATION_JSON)
public class LicoesResource {

    @Inject
    private LicoesBO licoesBO;

    /**
     * Retorna os detalhes completos de uma lição (Teoria, Instruções).
     * URL: GET /trilhas/licoes/{idLicao}
     */
    @GET
    @Path("/{idLicao}")
    public Response buscarLicaoCompleta(@PathParam("idLicao") int idLicao) {
        try {
            if (idLicao <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("ID da lição inválido.").build();
            }

            Licoes licao = licoesBO.buscarLicaoPorId(idLicao);

            if (licao == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Lição não encontrada.").build();
            }

            return Response.ok(licao).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao buscar detalhes da lição.").build();
        }
    }

}
