package concurseiro.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Assunto {

    private String id = UUID.randomUUID().toString();
    private String nome;
    private String materiaNome;

}
