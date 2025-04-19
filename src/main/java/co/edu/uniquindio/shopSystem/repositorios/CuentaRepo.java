package co.edu.uniquindio.shopSystem.repositorios;

import co.edu.uniquindio.shopSystem.modelo.documentos.Cuenta;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la gestión de cuentas de usuarios en MongoDB.
 * Provee consultas personalizadas para diferentes necesidades de acceso a datos.
 */
@Repository
public interface CuentaRepo extends MongoRepository<Cuenta, String> {

    /**
     * Busca una cuenta por número de cédula asociado al usuario
     * @param cedula Número de cédula a buscar en el campo 'usuario.cedula'
     * @return Optional con la cuenta encontrada o vacío si no existe
     */
    @Query("{ 'usuario.cedula' : ?0 }")
    Optional<Cuenta> buscarCuentaPorCedula(String cedula);

    /**
     * Busca una cuenta por dirección de correo electrónico
     * @param correo Email exacto a buscar en el sistema
     * @return Optional con la cuenta correspondiente al email
     */
    @Query("{ 'email' : ?0 }")
    Optional<Cuenta> buscarCuentaPorCorreo(String correo);

    /**
     * Busca una cuenta por su ID único en formato ObjectId de MongoDB
     * @param cedula ObjectId de la cuenta (conversión automática String/ObjectId)
     * @return Optional con la cuenta encontrada
     */
    @Query("{ '_id' : ?0 }")
    Optional<Cuenta> findById(ObjectId cedula);

    /**
     * Obtiene todos los correos electrónicos registrados en el sistema
     * @return Lista de strings con solo los campos email de todas las cuentas
     */
    @Query(value = "{}", fields = "{ 'email' : 1 }")
    List<String> obtenerTodosLosCorreos();

    /**
     * Obtiene todas las cuentas con rol de CLIENTE
     * @return Lista completa de cuentas de clientes
     */
    @Query(value = "{ 'rol' : 'CLIENTE' }")
    List<Cuenta> obtenerClientes();

    /**
     * Obtiene los clientes con estado de cuenta ACTIVO
     * @return Lista de cuentas activas de clientes
     */
    @Query(value = "{ 'estadoCuenta' : 'ACTIVO' }")
    List<Cuenta> obtenerClientesActivos();
}