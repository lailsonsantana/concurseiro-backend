package concurseiro.domain;

import java.util.List;

public record DiaEstudo(int dia, List<Assunto> estudo, List<Assunto> revisao) {}
