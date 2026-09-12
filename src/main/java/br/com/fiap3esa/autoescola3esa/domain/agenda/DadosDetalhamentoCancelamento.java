package br.com.fiap3esa.autoescola3esa.domain.agenda;

import java.time.LocalDateTime;

public record DadosDetalhamentoCancelamento(
        Long id,
        String nomeAluno,
        String nomeInstrutor,
        LocalDateTime dataHora,
        boolean cancelada,
        MotivoCancelamento motivoCancelamento) {
    public DadosDetalhamentoCancelamento(Instrucao instrucao) {
        this(
                instrucao.getId(),
                instrucao.getAluno().getNome(),
                instrucao.getInstrutor().getNome(),
                instrucao.getDataHora(),
                instrucao.isCancelada(),
                instrucao.getMotivoCancelamento()
        );
    }
}
