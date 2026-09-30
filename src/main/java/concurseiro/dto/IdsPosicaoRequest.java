package concurseiro.dto;

import concurseiro.domain.Posicao;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record IdsPosicaoRequest(@NotEmpty List<String> ids, @NotNull Posicao posicao) {}
