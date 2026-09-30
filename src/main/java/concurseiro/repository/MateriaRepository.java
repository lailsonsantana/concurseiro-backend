package concurseiro.repository;

import concurseiro.domain.Materia;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MateriaRepository extends MongoRepository<Materia, String> {
}
