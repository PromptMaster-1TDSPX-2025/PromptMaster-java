package br.com.fiap.dao;

import br.com.fiap.beans.Trilhas;
import br.com.fiap.excessoes.DaoException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TrilhasDAO {

    /**
     * Lista todas as trilhas disponíveis no sistema.
     */
    public List<Trilhas> listarTodas(Connection conn) {

        String sql = "SELECT id_trilha, nome_trilha, descricao_trilha FROM TB_TRILHAS ORDER BY id_trilha ASC";

        List<Trilhas> lista = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Trilhas trilha = new Trilhas();
                trilha.setId(rs.getInt("id_trilha"));
                trilha.setNome(rs.getString("nome_trilha"));
                trilha.setDescricao(rs.getString("descricao_trilha"));

                lista.add(trilha);
            }

        } catch (SQLException e) {
            throw new DaoException("Erro ao listar trilhas.", e);
        }

        return lista;
    }
}