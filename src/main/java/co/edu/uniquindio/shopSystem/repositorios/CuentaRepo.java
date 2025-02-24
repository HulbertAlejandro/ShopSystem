package co.edu.uniquindio.shopSystem.repositorios;

import co.edu.uniquindio.shopSystem.modelo.documentos.Cuenta;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CuentaRepo extends MongoRepository<Cuenta, String> {

    @Query("{ 'usuario.cedula' : ?0 }")
    Optional<Cuenta> buscarCuentaPorCedula(String cedula);

    @Query("{ 'email' : ?0 }")
    Optional<Cuenta> buscarCuentaPorCorreo(String correo);

    @Query("{ '_id' : ?0 }")
    Optional<Cuenta> findById(ObjectId cedula);

    @Query(value = "{}", fields = "{ 'email' : 1 }")
    List<String> obtenerTodosLosCorreos();

}