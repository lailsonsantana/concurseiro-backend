package concurseiro.service;

import concurseiro.domain.Cronograma;
import concurseiro.domain.DiaEstudo;
import concurseiro.domain.Posicao;
import concurseiro.repository.CronogramaRepository;
import concurseiro.repository.MateriaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Consumer;

@Service
@AllArgsConstructor
public class CronogramaService {

    private final CronogramaRepository cronogramaRepo;
    private final MateriaRepository materiaRepo;


    public List<DiaEstudo> consultar() {
        return carregar().definirCronograma();
    }

    /** Inclui assuntos novos no fim da fila e remove os excluídos, mantendo a ordem atual. */
    public List<DiaEstudo> sincronizar() {
        return executar(c -> c.sincronizar(materiaRepo.findAll()));
    }

    /** Volta ao cronograma inicial: zera a revisão e refaz a fila de estudo. */
    public List<DiaEstudo> reiniciar() {
        return executar(c -> c.reiniciar(materiaRepo.findAll()));
    }

    public List<DiaEstudo> marcarEstudado(List<String> ids) {
        return executar(c -> c.marcarAssuntoEstudado(ids));
    }

    public List<DiaEstudo> marcarIncompleto(List<String> ids, Posicao pos) {
        return executar(c -> c.marcarAssuntoIncompleto(ids, pos));
    }

    public List<DiaEstudo> marcarRevisado(List<String> ids) {
        return executar(c -> c.marcarAssuntoRevisado(ids));
    }

    public List<DiaEstudo> voltarParaEstudo(List<String> ids, Posicao pos) {
        return executar(c -> c.voltarParaEstudo(ids, pos));
    }

    public List<DiaEstudo> configurar(int estudo, int revisao, int dias, int assuntosPorMateria) {
        return executar(c -> {
            c.setQuantAulasEstudarNoDia(estudo);
            c.setQuantAulasRevisaoNoDia(revisao);
            c.setQuantDias(dias);
            c.setAssuntosPorMateria(assuntosPorMateria);
        });
    }

    // carrega -> aplica ação -> salva -> devolve o cronograma já recalculado
    private List<DiaEstudo> executar(Consumer<Cronograma> acao) {
        Cronograma c = carregar();
        acao.accept(c);
        cronogramaRepo.save(c);
        return c.definirCronograma();
    }

    private Cronograma carregar() {
        return cronogramaRepo.findById(Cronograma.ID_UNICO).orElseGet(Cronograma::new);
    }
}
