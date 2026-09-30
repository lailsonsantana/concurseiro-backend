package concurseiro.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document("materia")
public class Materia {

    @Id
    private String id;
    private String nome;
    private List<Assunto> assuntos = new ArrayList<>();

    public Materia() {}

    public Materia(String nome) { this.nome = nome; }

    public Assunto adicionarAssunto(String nomeAssunto) {
        Assunto a = new Assunto();
        a.setNome(nomeAssunto);
        a.setMateriaNome(this.nome);
        assuntos.add(a);
        return a;
    }

    public String getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public List<Assunto> getAssuntos() { return assuntos; }
}
