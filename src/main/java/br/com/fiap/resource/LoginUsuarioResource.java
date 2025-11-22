package br.com.fiap.resource;

import br.com.fiap.beans.LoginUsuario;
import br.com.fiap.beans.Usuario;
import br.com.fiap.bo.UsuarioBO;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/login")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LoginUsuarioResource {

    UsuarioBO usuarioBO = new UsuarioBO();

    @POST
    @Path("/autenticar")
    public Response login(LoginUsuario credenciais) {
        try {
            // Validação básica
            if (credenciais == null || credenciais.getLogin() == null || credenciais.getSenha() == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Login e senha são obrigatórios.").build();
            }

            Usuario usuarioLogado = usuarioBO.realizarLogin(credenciais.getLogin(), credenciais.getSenha());

            if (usuarioLogado != null) {
                return Response.ok(usuarioLogado).build(); // Retorna 200 e o JSON do usuário
            } else {
                return Response.status(Response.Status.UNAUTHORIZED) // Retorna 401
                        .entity("Email ou senha inválidos.").build();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao processar o login.").build();
        }
    }
}
