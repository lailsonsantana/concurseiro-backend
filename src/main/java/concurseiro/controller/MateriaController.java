package concurseiro.controller;

import concurseiro.domain.Materia;
import concurseiro.dto.AssuntoRequest;
import concurseiro.dto.MateriaRequest;
import concurseiro.repository.MateriaRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/materias")
public class MateriaController {

    private final MateriaRepository repo;

    public MateriaController(MateriaRepository repo) { this.repo = repo; }

    @GetMapping
    public List<Materia> listar() { return repo.findAll(); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Materia criar(@Valid @RequestBody MateriaRequest req) {
        Materia m = new Materia(req.nome());
        if (req.assuntos() != null) req.assuntos().forEach(m::adicionarAssunto);
        return repo.save(m);
    }

    @PostMapping("/{id}/assuntos")
    public ResponseEntity<Materia> adicionarAssunto(@PathVariable String id,
                                                    @Valid @RequestBody AssuntoRequest req) {
        return repo.findById(id).map(m -> {
            m.adicionarAssunto(req.nome());
            return ResponseEntity.ok(repo.save(m));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable String id) { repo.deleteById(id); }
}
