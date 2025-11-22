package br.com.fiap.bo;

import br.com.fiap.beans.LoginUsuario;
import br.com.fiap.beans.Usuario;
import br.com.fiap.dao.LoginUsuarioDAO;
import br.com.fiap.dao.UsuarioDAO;
import br.com.fiap.conexoes.ConnectionManager;
import br.com.fiap.excessoes.DaoException;
import br.com.fiap.dao.VidasUsuarioDAO;
import br.com.fiap.dao.OfensivaUsuarioDAO;

import java.sql.Connection;
import java.sql.SQLException;

public class UsuarioBO {

    private UsuarioDAO usuarioDAO;
    private LoginUsuarioDAO loginDAO;
    private VidasUsuarioDAO vidasUsuarioDAO;
    private OfensivaUsuarioDAO ofensivaUsuarioDAO;

    public UsuarioBO() {
        usuarioDAO = new UsuarioDAO();
        loginDAO = new LoginUsuarioDAO();
        vidasUsuarioDAO = new VidasUsuarioDAO();
        ofensivaUsuarioDAO = new OfensivaUsuarioDAO();
    }

    /**
     * Gerencia a transação de cadastro, aplicando regras de negócio e coordenando os DAOs.
     * Inclui a inicialização dos registros de Vidas e Ofensiva.
     * @param loginUsuario Contém os dados do novo usuário e as credenciais de login.
     * @return O objeto Usuario cadastrado, incluindo o ID gerado.
     * @throws Exception Propaga erros para a camada Resource.
     */
    public Usuario cadastrarUsuarioCompleto(LoginUsuario loginUsuario) throws Exception {

        // --- Setar valores iniciais ---
        Usuario novoUsuario = loginUsuario.getUsuario();
        if (novoUsuario == null) {
            throw new IllegalArgumentException("Objeto Usuario não pode ser nulo.");
        }

        novoUsuario.setTotalXp(0); // Novo usuário começa com 0 XP
        novoUsuario.setNivel(1);   // Novo usuário começa no Nível 1

        Connection conn = null;
        try {
            // --- Início da Transação ---
            conn = new ConnectionManager().conexao();
            conn.setAutoCommit(false); // Desliga o auto-commit para gerenciar a transação

            // Insere na TB_USUARIO e recupera o ID gerado
            usuarioDAO.inserirUsuario(conn, novoUsuario);
            int idNovoUsuario = novoUsuario.getId();

            // Insere na TB_LOGIN_USUARIO
            loginDAO.inserirLogin(conn, loginUsuario);

            // Insere registro inicial na TB_VIDAS_USUARIO
            vidasUsuarioDAO.inserirRegistroInicial(conn, idNovoUsuario);

            // Insere registro inicial na TB_OFENSIVA_USUARIO (0 dias)
            ofensivaUsuarioDAO.inserirRegistroInicial(conn, idNovoUsuario);

            // --- Commit (Se tudo deu certo) ---
            conn.commit();

            return novoUsuario; // Retorna o usuário completo

        } catch (DaoException | SQLException e) {
            // --- Rollback (Se algo falhou) ---
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Erro ao tentar rollback: " + ex.getMessage());
                }
            }
            // Lança a exceção para a camada superior (Resource)
            throw e;
        } finally {
            // --- Fechamento da Conexão ---
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    System.err.println("Erro ao fechar conexão: " + ex.getMessage());
                }
            }
        }
    }

    public int buscarVidasAtualizadas(int idUsuario) throws Exception {
        Connection conn = null;
        try {
            // Reabre uma conexão separada para esta operação de leitura
            conn = new ConnectionManager().conexao();

            // O DAO gerencia a lógica de recarga e retorna o valor atualizado
            int vidas = vidasUsuarioDAO.getVidasAtualizadas(conn, idUsuario);
            return vidas;
        } catch (DaoException e) {
            System.err.println("Erro de DAO ao buscar vidas: " + e.getMessage());
            throw new Exception("Erro de persistência ao buscar vidas.", e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    System.err.println("Erro ao fechar conexão após consulta de vidas: " + ex.getMessage());
                }
            }
        }
    }

    /**
     * Realiza o login verificando credenciais e retornando o usuário completo.
     */
    public Usuario realizarLogin(String login, String senha) throws Exception {
        Connection conn = null;
        try {
            conn = new ConnectionManager().conexao();

            // Verifica se login/senha batem
            int idUsuario = loginDAO.verificarCredenciais(conn, login, senha);

            if (idUsuario == -1) {
                return null; // Login falhou
            }

            // Se deu certo, busca os dados completos do usuário (XP, Nível, Nome)
            return usuarioDAO.buscarUsuarioPorId(conn, idUsuario);

        } catch (Exception e) {
            throw new Exception("Erro interno ao realizar login.", e);
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

}