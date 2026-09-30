package concurseiro.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

/** assuntos é opcional: permite cadastrar a matéria já com a lista do edital. */
public record MateriaRequest(@NotBlank String nome, List<String> assuntos) {}
