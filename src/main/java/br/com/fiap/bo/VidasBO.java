package br.com.fiap.bo;

import br.com.fiap.dao.VidasUsuarioDAO;
import br.com.fiap.conexoes.ConnectionManager;
import jakarta.enterprise.context.ApplicationScoped;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Classe responsável pela lógica de negócio relacionada a vidas do usuário.
 * Inclui validação e dedução de vidas.
 */
@ApplicationScoped
public class VidasBO {

    private VidasUsuarioDAO vidasUsuarioDAO;

    public VidasBO() {
        vidasUsuarioDAO = new VidasUsuarioDAO();
    }

    /**
     * Valida se o usuário tem vidas restantes antes de permitir o prompt.
     * @param idUsuario ID do usuário
     * @throws IllegalArgumentException Se vidas == 0
     * @throws Exception Se houver erro na validação
     */
    public void validarVidas(int idUsuario) throws Exception {
        Connection conn = null;
        try {
            conn = new ConnectionManager().conexao();

            int vidas = vidasUsuarioDAO.getVidasAtualizadas(conn, idUsuario);

            if (vidas <= 0) {
                throw new IllegalArgumentException("Você não tem vidas restantes. Aguarde a recarga para continuar a lição.");
            }
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    System.err.println("Erro ao fechar conexão após validação de vidas: " + ex.getMessage());
                }
            }
        }
    }

    /**
     * Deduz uma vida do usuário quando a nota é menor que a mínima.
     * @param conn Conexão ativa com transação iniciada
     * @param idUsuario ID do usuário
     * @throws Exception Se houver erro na dedução
     */
    public void deduzirVida(Connection conn, int idUsuario) throws Exception {
        vidasUsuarioDAO.deduzirVida(conn, idUsuario);
    }
}

