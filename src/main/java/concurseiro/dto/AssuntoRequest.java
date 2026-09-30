package concurseiro.dto;

import jakarta.validation.constraints.NotBlank;

public record AssuntoRequest(@NotBlank String nome) {}
