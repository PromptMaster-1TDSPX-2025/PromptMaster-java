package br.com.fiap.bo;

import br.com.fiap.beans.OfensivaUsuario;
import br.com.fiap.dao.OfensivaUsuarioDAO;
import jakarta.enterprise.context.ApplicationScoped;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Classe responsável pela lógica de negócio relacionada a ofensiva do usuário.
 * Gerencia a sequência de dias consecutivos de conclusão de lições.
 */
@ApplicationScoped
public class OfensivaBO {

    private OfensivaUsuarioDAO ofensivaUsuarioDAO;

    public OfensivaBO() {
        ofensivaUsuarioDAO = new OfensivaUsuarioDAO();
    }

    /**
     * Processa a atualização da ofensiva do usuário baseado na data de conclusão.
     * @param conn Conexão ativa com transação iniciada
     * @param idUsuario ID do usuário
     * @param dataConclusao Data/hora da conclusão da lição
     * @throws Exception Se houver erro no processamento
     */
    public void processarOfensiva(Connection conn, int idUsuario, LocalDateTime dataConclusao) throws Exception {
        OfensivaUsuario dadosOfensiva = ofensivaUsuarioDAO.buscarOfensivaPorIdUsuario(conn, idUsuario);

        int ofensivaAtual = (dadosOfensiva != null) ? dadosOfensiva.getDiasOfensiva() : 0;
        LocalDateTime ultimaData = (dadosOfensiva != null) ? dadosOfensiva.getUltimaDataConclusao() : null;

        int novaOfensiva = calcularNovaOfensiva(ofensivaAtual, ultimaData, dataConclusao);

        if (novaOfensiva != ofensivaAtual || ultimaData == null) {
            ofensivaUsuarioDAO.atualizarOfensiva(conn, idUsuario, novaOfensiva, dataConclusao);
        }
    }

    /**
     * Calcula a nova ofensiva baseado na última data de conclusão e data atual.
     * @param ofensivaAtual Ofensiva atual do usuário
     * @param ultimaData Última data de conclusão (pode ser null)
     * @param dataConclusao Data atual de conclusão
     * @return Nova ofensiva calculada
     */
    private int calcularNovaOfensiva(int ofensivaAtual, LocalDateTime ultimaData, LocalDateTime dataConclusao) {
        LocalDate hoje = dataConclusao.toLocalDate();

        if (ultimaData == null) {
            return 1; // Primeira conclusão
        }

        LocalDate ultimoDia = ultimaData.toLocalDate();
        long diferencaDias = ChronoUnit.DAYS.between(ultimoDia, hoje);

        if (diferencaDias == 0) {
            // Mesmo dia - mantém a ofensiva atual
            return ofensivaAtual;
        } else if (diferencaDias == 1) {
            // Dia consecutivo - incrementa
            return ofensivaAtual + 1;
        } else {
            // Mais de um dia de diferença - reinicia
            return 1;
        }
    }
}

