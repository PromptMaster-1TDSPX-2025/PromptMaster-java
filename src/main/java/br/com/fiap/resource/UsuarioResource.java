package br.com.fiap.resource;

import br.com.fiap.beans.LoginUsuario;
import br.com.fiap.beans.Usuario;
import br.com.fiap.bo.UsuarioBO;
import br.com.fiap.excessoes.DaoException;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.sql.SQLException;

@Path("/usuarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UsuarioResource {

    private UsuarioBO usuarioBO = new UsuarioBO();

    /**
     * Endpoint para cadastro de um novo usuário.
     */
    @POST
    @Path("/cadastro")
    public Response cadastrar(LoginUsuario loginUsuario) {

        // --- Validação dos dados de entrada ---
        if (loginUsuario == null ||
                loginUsuario.getLogin() == null || loginUsuario.getLogin().trim().isEmpty() ||
                loginUsuario.getSenha() == null || loginUsuario.getSenha().trim().isEmpty() ||
                loginUsuario.getUsuario() == null || loginUsuario.getUsuario().getNome() == null ||
                loginUsuario.getUsuario().getNome().trim().isEmpty())
        {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Dados de cadastro incompletos. Nome, Login e Senha são obrigatórios.").build();
        }

        try {
            // O BO gerencia a transação, a conexão e retorna o objeto Usuario com o ID gerado.
            Usuario novoUsuario = usuarioBO.cadastrarUsuarioCompleto(loginUsuario);

            // --- Retorno de sucesso (201 Created) ---
            return Response.status(Response.Status.CREATED)
                    .entity(novoUsuario) // Retorna o objeto completo com o ID
                    .build();

        } catch (IllegalArgumentException e) {
            // Captura erros de validação de negócio.
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage()).build();

        } catch (DaoException e) {
            Throwable cause = e.getCause();

            // Tenta verificar se a causa é uma violação de restrição única (código Oracle 1)
            if (cause instanceof SQLException) {
                SQLException sqlEx = (SQLException) cause;
                if (sqlEx.getErrorCode() == 1) {
                    System.err.println("Violação de restrição UNIQUE: " + sqlEx.getMessage());
                    return Response.status(Response.Status.CONFLICT) // 409 CONFLICT
                            .entity("O login ou nome de usuário já está em uso. Escolha outros dados.").build();
                }
            }

            // Para outros erros de persistência não mapeados
            System.err.println("Erro na camada de persistência: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR) // 500
                    .entity("Erro interno do servidor ao processar o cadastro.").build();

        } catch (Exception e) {
            System.err.println("Erro inesperado durante o cadastro: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro interno inesperado. Tente novamente mais tarde.").build();
        }
    }

    /**
     * ENDPOINT: Consulta o status atualizado das vidas do usuário (com recarga automática).
     * URL: GET /usuarios/{idUsuario}/vidas
     */
    @GET
    @Path("/{idUsuario}/vidas")
    public Response buscarVidas(@PathParam("idUsuario") int idUsuario) {

        if (idUsuario <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("ID do usuário inválido.").build();
        }

        try {
            // Chama o método do BO para aplicar a lógica de recarga e obter o valor atual
            int vidas = usuarioBO.buscarVidasAtualizadas(idUsuario);

            // Retorna a resposta no formato JSON
            return Response.ok()
                    .entity("{\"vidas_atuais\": " + vidas + "}")
                    .build();

        } catch (Exception e) {
            // Captura qualquer erro de conexão ou DAO propagado pelo BO
            System.err.println("Erro ao buscar vidas: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro interno ao buscar o status das vidas.").build();
        }
    }
}