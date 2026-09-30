package concurseiro.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

/**
 * Documento único (id fixo) com as duas filas e a configuração.
 * A ordem das listas É a ordem da fila.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("cronograma")
public class Cronograma {

    public static final String ID_UNICO = "principal";

    @Id
    private String id = ID_UNICO;

    private List<Assunto> assuntosEstudar = new ArrayList<>();
    private List<Assunto> assuntosRevisao = new ArrayList<>();

    private int quantAulasEstudarNoDia = 2;
    private int quantAulasRevisaoNoDia = 1;
    private int quantDias = 5;
    private int assuntosPorMateria = 2;   // quantos assuntos seguidos de cada matéria por rodada

    public int quantAulasDia() {
        return quantAulasEstudarNoDia + quantAulasRevisaoNoDia;
    }

    // ---------- montagem das filas ----------

    /** Monta a fila de estudo na ordem inicial (blocos da mesma matéria). Não mexe na revisão. */
    public void definirAssuntosEstudar(List<Materia> materias) {
        assuntosEstudar.clear();
        assuntosEstudar.addAll(ordemInicial(materias));
    }

    /** Volta ao cronograma inicial: zera a revisão e refaz a fila de estudo. */
    public void reiniciar(List<Materia> materias) {
        assuntosRevisao.clear();
        definirAssuntosEstudar(materias);
    }

    /**
     * Sincroniza com o cadastro sem perder a ordem atual: assuntos novos entram no fim da fila de
     * estudo, assuntos excluídos saem das duas filas e nomes alterados são atualizados.
     */
    public void sincronizar(List<Materia> materias) {
        Map<String, Assunto> atuais = new HashMap<>();
        materias.forEach(m -> m.getAssuntos().forEach(a -> atuais.put(a.getId(), a)));

        atualizar(assuntosEstudar, atuais);
        atualizar(assuntosRevisao, atuais);

        Set<String> naFila = new HashSet<>();
        assuntosEstudar.forEach(a -> naFila.add(a.getId()));
        for (Assunto a : ordemInicial(materias)) {
            if (!naFila.contains(a.getId())) assuntosEstudar.add(a);
        }
    }

    // ---------- visão ----------

    /** Só monta a visão; não altera as filas. */
    public List<DiaEstudo> definirCronograma() {
        Iterator<Assunto> est = assuntosEstudar.iterator();
        Iterator<Assunto> rev = assuntosRevisao.iterator();
        List<DiaEstudo> dias = new ArrayList<>();
        for (int d = 1; d <= quantDias; d++) {
            dias.add(new DiaEstudo(d,
                    pegar(est, quantAulasEstudarNoDia),
                    pegar(rev, quantAulasRevisaoNoDia)));
        }
        return dias;
    }

    // ---------- ações ----------

    /** Estudou completo: vai para o fim da fila de estudo e entra no fim da revisão (se ainda não estiver). */
    public void marcarAssuntoEstudado(List<String> ids) {
        List<Assunto> estudados = remover(assuntosEstudar, ids);
        assuntosEstudar.addAll(estudados);

        Set<String> jaNaRevisao = new HashSet<>();
        assuntosRevisao.forEach(a -> jaNaRevisao.add(a.getId()));
        for (Assunto a : estudados) {
            if (!jaNaRevisao.contains(a.getId())) assuntosRevisao.add(a);
        }
    }

    /** Estudo incompleto: continua na fila de estudo (topo ou fim). */
    public void marcarAssuntoIncompleto(List<String> ids, Posicao pos) {
        colocar(assuntosEstudar, remover(assuntosEstudar, ids), pos);
    }

    /** Revisou: vai para o fim da fila de revisão. */
    public void marcarAssuntoRevisado(List<String> ids) {
        assuntosRevisao.addAll(remover(assuntosRevisao, ids));
    }

    /** Tirar da revisão e reposicionar na fila de estudo (sem duplicar). */
    public void voltarParaEstudo(List<String> ids, Posicao pos) {
        List<Assunto> itens = remover(assuntosRevisao, ids);
        remover(assuntosEstudar, ids);
        colocar(assuntosEstudar, itens, pos);
    }

    // ---------- auxiliares ----------

    /** Blocos de 'assuntosPorMateria' assuntos de cada matéria, rodada após rodada. */
    private List<Assunto> ordemInicial(List<Materia> materias) {
        int bloco = Math.max(1, assuntosPorMateria);
        int max = materias.stream().mapToInt(m -> m.getAssuntos().size()).max().orElse(0);
        List<Assunto> resultado = new ArrayList<>();
        for (int inicio = 0; inicio < max; inicio += bloco) {
            for (Materia m : materias) {
                List<Assunto> as = m.getAssuntos();
                for (int i = inicio; i < Math.min(inicio + bloco, as.size()); i++) {
                    resultado.add(as.get(i));
                }
            }
        }
        return resultado;
    }

    private void atualizar(List<Assunto> fila, Map<String, Assunto> atuais) {
        List<Assunto> nova = new ArrayList<>();
        for (Assunto a : fila) {
            Assunto atual = atuais.get(a.getId());
            if (atual != null) nova.add(atual);
        }
        fila.clear();
        fila.addAll(nova);
    }

    private List<Assunto> pegar(Iterator<Assunto> it, int qtd) {
        List<Assunto> r = new ArrayList<>();
        while (r.size() < qtd && it.hasNext()) r.add(it.next());
        return r;
    }

    /** Remove da fila os itens com os ids informados, preservando a ordem em que estavam. */
    private List<Assunto> remover(List<Assunto> fila, List<String> ids) {
        Set<String> alvo = new HashSet<>(ids);
        List<Assunto> removidos = new ArrayList<>();
        for (Iterator<Assunto> it = fila.iterator(); it.hasNext(); ) {
            Assunto a = it.next();
            if (alvo.contains(a.getId())) {
                removidos.add(a);
                it.remove();
            }
        }
        return removidos;
    }

    private void colocar(List<Assunto> fila, List<Assunto> itens, Posicao pos) {
        if (pos == Posicao.TOPO) fila.addAll(0, itens);
        else fila.addAll(itens);
    }


}
