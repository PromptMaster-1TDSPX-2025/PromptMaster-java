package br.com.fiap.dao;

import br.com.fiap.beans.LoginUsuario;
import br.com.fiap.excessoes.DaoException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LoginUsuarioDAO {

    public void inserirLogin(Connection conn, LoginUsuario loginUsuario) {

        String sql = "INSERT INTO TB_LOGIN_USUARIO (id_usuario, login, senha) VALUES (?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, loginUsuario.getUsuario().getId());
            ps.setString(2, loginUsuario.getLogin());
            ps.setString(3, loginUsuario.getSenha());

            ps.executeUpdate();

            System.out.println("Login inserido com sucesso!");

        } catch (SQLException e) {
            throw new DaoException("Erro ao inserir Login de Usuário.", e);
        }
    }
}