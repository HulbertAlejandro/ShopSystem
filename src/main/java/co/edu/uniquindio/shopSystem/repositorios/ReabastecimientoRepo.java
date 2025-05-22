package co.edu.uniquindio.shopSystem.repositorios;

import co.edu.uniquindio.shopSystem.modelo.documentos.Reabastecimiento;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReabastecimientoRepo extends MongoRepository<Reabastecimiento, String> {
}
