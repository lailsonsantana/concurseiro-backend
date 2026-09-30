package concurseiro.repository;

import concurseiro.domain.Cronograma;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CronogramaRepository extends MongoRepository<Cronograma, String> {
}
