package concurseiro.controller;


import concurseiro.domain.DiaEstudo;
import concurseiro.dto.ConfiguracaoRequest;
import concurseiro.dto.IdsPosicaoRequest;
import concurseiro.dto.IdsRequest;
import concurseiro.service.CronogramaService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cronograma")
public class CronogramaController {

    private final CronogramaService service;

    public CronogramaController(CronogramaService service) { this.service = service; }

    @GetMapping
    public List<DiaEstudo> consultar() { return service.consultar(); }

    @PostMapping("/sincronizar")
    public List<DiaEstudo> sincronizar() { return service.sincronizar(); }

    @PostMapping("/reiniciar")
    public List<DiaEstudo> reiniciar() { return service.reiniciar(); }

    @PostMapping("/estudado")
    public List<DiaEstudo> estudado(@Valid @RequestBody IdsRequest req) {
        return service.marcarEstudado(req.ids());
    }

    @PostMapping("/estudo-incompleto")
    public List<DiaEstudo> incompleto(@Valid @RequestBody IdsPosicaoRequest req) {
        return service.marcarIncompleto(req.ids(), req.posicao());
    }

    @PostMapping("/revisado")
    public List<DiaEstudo> revisado(@Valid @RequestBody IdsRequest req) {
        return service.marcarRevisado(req.ids());
    }

    @PostMapping("/voltar-para-estudo")
    public List<DiaEstudo> voltar(@Valid @RequestBody IdsPosicaoRequest req) {
        return service.voltarParaEstudo(req.ids(), req.posicao());
    }

    @PutMapping("/configuracao")
    public List<DiaEstudo> configurar(@Valid @RequestBody ConfiguracaoRequest req) {
        return service.configurar(req.quantAulasEstudarNoDia(), req.quantAulasRevisaoNoDia(),
                req.quantDias(), req.assuntosPorMateria());
    }
}
