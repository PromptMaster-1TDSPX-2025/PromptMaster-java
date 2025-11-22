package br.com.fiap.dao;

import br.com.fiap.excessoes.DaoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class VidasUsuarioDAO {

    private static final int MAX_VIDAS = 5;
    // Define a regra de recarga: 1 hora em minutos
    private static final int RECHARGE_TIME_MINUTES = 60;

    // MERGE para criar o registro inicial ou apenas obter o valor
    private static final String SQL_MERGE_OU_SELECT =
            "MERGE INTO TB_VIDAS_USUARIO T " +
                    "USING (SELECT ? AS id_usuario FROM DUAL) S " +
                    "ON (T.id_usuario = S.id_usuario) " +
                    "WHEN NOT MATCHED THEN INSERT (id_usuario) VALUES (S.id_usuario)";

    // Select para obter o status atual (após o merge, se necessário)
    private static final String SQL_SELECT_STATUS =
            "SELECT vidas_atuais, ultima_perda FROM TB_VIDAS_USUARIO WHERE id_usuario = ?";

    // UPDATE para recarga de vidas
    private static final String SQL_UPDATE_RECARGA =
            "UPDATE TB_VIDAS_USUARIO SET vidas_atuais = ?, ultima_perda = ? WHERE id_usuario = ?";

    // UPDATE para dedução de vidas (quando falha uma lição)
    private static final String SQL_DEDUZIR_VIDA =
            "UPDATE TB_VIDAS_USUARIO " +
                    "SET vidas_atuais = vidas_atuais - 1, " +
                    "    ultima_perda = CASE " +
                    "                       WHEN vidas_atuais = " + MAX_VIDAS + " THEN SYSTIMESTAMP " +
                    "                       ELSE ultima_perda " +
                    "                   END " +
                    "WHERE id_usuario = ? AND vidas_atuais > 0";

    /**
     * Obtém o número de vidas do usuário, aplicando a regra de recarga baseada no tempo.
     * @param conn Conexão transacional.
     * @param idUsuario ID do usuário.
     * @return O número de vidas atualizado.
     */
    public int getVidasAtualizadas(Connection conn, int idUsuario) throws DaoException {

        try (PreparedStatement psMerge = conn.prepareStatement(SQL_MERGE_OU_SELECT)) {
            psMerge.setInt(1, idUsuario);
            psMerge.executeUpdate();

        } catch (SQLException e) {
            throw new DaoException("Erro ao garantir o registro de vidas do usuário.", e);
        }

        // Obtém o status atualizado, aplicando a recarga de tempo
        try (PreparedStatement ps = conn.prepareStatement(SQL_SELECT_STATUS)) {
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int vidasAtuais = rs.getInt("vidas_atuais");
                // Garante que o timestamp não é nulo antes de chamar toLocalDateTime()
                Timestamp ultimaPerdaTS = rs.getTimestamp("ultima_perda");

                // Se ultimaPerdaTS for nulo, a recarga não é necessária (usuário acabou de ser criado e já tem 5 vidas, ou nunca perdeu)
                if (vidasAtuais < MAX_VIDAS && ultimaPerdaTS != null) {
                    return recarregarVidasInterno(conn, idUsuario, vidasAtuais, ultimaPerdaTS.toLocalDateTime());
                }
                return vidasAtuais;
            }
            // Se o SELECT falhar após o MERGE, algo está errado, retorna 0 para segurança.
            return 0;
        } catch (SQLException e) {
            throw new DaoException("Erro ao buscar vidas do usuário.", e);
        }
    }

    /**
     * Lógica de recarga: calcula quantas vidas devem ser recuperadas.
     */
    private int recarregarVidasInterno(Connection conn, int idUsuario, int vidasAtuais, LocalDateTime ultimaPerda)
            throws DaoException, SQLException {

        long minutosPassados = ChronoUnit.MINUTES.between(ultimaPerda, LocalDateTime.now());

        // Número de vidas que deveriam ter sido recarregadas
        long vidasRecarregadas = minutosPassados / RECHARGE_TIME_MINUTES;

        int novaVidas = (int) Math.min(vidasAtuais + vidasRecarregadas, MAX_VIDAS);

        if (novaVidas > vidasAtuais) {

            // A nova data de perda deve ser a hora atual MENOS o tempo
            // já decorrido no NOVO ciclo de recarga (o resto da divisão).
            long minutosRestantesDoCiclo = minutosPassados % RECHARGE_TIME_MINUTES;

            LocalDateTime novaUltimaPerda = LocalDateTime.now().minusMinutes(
                    minutosRestantesDoCiclo
            );

            try (PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_RECARGA)) {
                ps.setInt(1, novaVidas);
                ps.setTimestamp(2, Timestamp.valueOf(novaUltimaPerda));
                ps.setInt(3, idUsuario);
                ps.executeUpdate();
                return novaVidas;
            }
        }

        return vidasAtuais;
    }

    /**
     * Deduz uma vida após a falha na lição.
     */
    public void deduzirVida(Connection conn, int idUsuario) throws DaoException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_DEDUZIR_VIDA)) {
            ps.setInt(1, idUsuario);
            ps.executeUpdate();
            System.out.println("Vida deduzida para o usuário: " + idUsuario);
        } catch (SQLException e) {
            throw new DaoException("Erro ao deduzir vida do usuário.", e);
        }
    }


    private static final String SQL_INSERIR_INICIAL =
            "INSERT INTO TB_VIDAS_USUARIO (id_usuario, vidas_atuais) VALUES (?, 5)";

    /**
     * Insere o registro inicial de vidas (5) para um novo usuário.
     */
    public void inserirRegistroInicial(Connection conn, int idUsuario) throws DaoException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_INSERIR_INICIAL)) {
            ps.setInt(1, idUsuario);
            ps.executeUpdate();
            System.out.println("Registro inicial de vidas criado para o usuário: " + idUsuario);
        } catch (SQLException e) {
            throw new DaoException("Erro ao inserir registro inicial de vidas do usuário.", e);
        }
    }

}