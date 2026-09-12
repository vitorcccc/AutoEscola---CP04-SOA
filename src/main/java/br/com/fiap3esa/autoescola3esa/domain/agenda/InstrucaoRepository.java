package br.com.fiap3esa.autoescola3esa.domain.agenda;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface InstrucaoRepository extends JpaRepository<Instrucao, Long> {
    boolean existByIdAndDataHora(Long idInstrutor, LocalDateTime dataHora);

    boolean existsByIdAndDataHoraBetween(Long idAluno, LocalDateTime inicio, LocalDateTime fim);
}