package br.com.fiap.dao;

import br.com.fiap.beans.Usuario;
import br.com.fiap.excessoes.DaoException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {

    public void inserirUsuario(Connection conn, Usuario usuario) {

        String sql = "INSERT INTO TB_USUARIO (nome_usuario, idade_usuario, total_xp_usuario, nivel_usuario) VALUES (?, ?, ?, ?)";

        String[] colunasRetorno = { "ID_USUARIO" };

        try (PreparedStatement ps = conn.prepareStatement(sql, colunasRetorno)) {

            ps.setString(1, usuario.getNome());
            ps.setInt(2, usuario.getIdade());
            ps.setInt(3, usuario.getTotalXp());
            ps.setInt(4, usuario.getNivel());

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                usuario.setId(rs.getInt(1));
            }

            System.out.println("Usuario inserido com sucesso!");

        } catch (SQLException e) {
            throw new DaoException("Erro ao inserir Usuário.", e);
        }
    }

    // MÉTODO EXISTENTE: Incrementa o total de XP
    public void incrementarXp(Connection conn, int idUsuario, int xpParaAdicionar) {
        String sql = "UPDATE TB_USUARIO SET total_xp_usuario = total_xp_usuario + ? WHERE id_usuario = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, xpParaAdicionar);
            ps.setInt(2, idUsuario);

            ps.executeUpdate();

            System.out.println("XP de " + xpParaAdicionar + " adicionado ao usuário " + idUsuario);
        } catch (SQLException e) {
            throw new DaoException("Erro ao incrementar XP do usuário.", e);
        }
    }

    // Busca o Total XP de um usuário
    public int buscarTotalXp(Connection conn, int idUsuario) {
        String sql = "SELECT total_xp_usuario FROM TB_USUARIO WHERE id_usuario = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total_xp_usuario");
                }
            }
            return 0; // Retorna 0 se o usuário não for encontrado ou XP for nulo

        } catch (SQLException e) {
            throw new DaoException("Erro ao buscar total XP do usuário.", e);
        }
    }

    // Busca o Nível Atual de um usuário
    public int buscarNivelAtual(Connection conn, int idUsuario) {
        String sql = "SELECT nivel_usuario FROM TB_USUARIO WHERE id_usuario = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("nivel_usuario");
                }
            }
            return 1; // Nível inicial

        } catch (SQLException e) {
            throw new DaoException("Erro ao buscar nível do usuário.", e);
        }
    }

    // Atualiza o Nível de um usuário
    public void atualizarNivel(Connection conn, int idUsuario, int novoNivel) {
        String sql = "UPDATE TB_USUARIO SET nivel_usuario = ? WHERE id_usuario = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, novoNivel);
            ps.setInt(2, idUsuario);

            ps.executeUpdate();

            System.out.println("Nível do usuário " + idUsuario + " atualizado para: " + novoNivel);
        } catch (SQLException e) {
            throw new DaoException("Erro ao atualizar o nível do usuário.", e);
        }
    }
}