package concurseiro.dto;

import jakarta.validation.constraints.Min;

public record ConfiguracaoRequest(
        @Min(0) int quantAulasEstudarNoDia,
        @Min(0) int quantAulasRevisaoNoDia,
        @Min(1) int quantDias,
        @Min(1) int assuntosPorMateria) {}
