package br.com.fiap3esa.autoescola3esa.domain.agenda;

import jakarta.validation.constraints.NotNull;

public record DadosCancelamentoInstrucao(
        @NotNull
        MotivoCancelamento motivo) {
}
