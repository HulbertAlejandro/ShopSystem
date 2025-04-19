package co.edu.uniquindio.shopSystem.repositorios;

import co.edu.uniquindio.shopSystem.modelo.documentos.Cuenta;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la gestión de cuentas de usuario en MongoDB.
 * Proporciona métodos personalizados para operaciones CRUD y consultas específicas.
 *
 * @author TuNombre
 * @version 1.0
 */
@Repository
public interface CuentaRepo extends MongoRepository<Cuenta, String> {

    /**
     * Busca una cuenta por número de cédula asociado al usuario
     * @param cedula Número de identificación del usuario (campo anidado en 'usuario.cedula')
     * @return Optional con la cuenta encontrada o vacío si no existe
     * @apiNote Realiza búsqueda exacta en el campo anidado de la entidad Usuario
     */
    @Query("{ 'usuario.cedula' : ?0 }")
    Optional<Cuenta> buscarCuentaPorCedula(String cedula);

    /**
     * Busca una cuenta por dirección de correo electrónico registrada
     * @param correo Dirección de email exacta a buscar
     * @return Optional con la cuenta correspondiente al email
     * @implNote Campo email debe ser único en la colección
     */
    @Query("{ 'email' : ?0 }")
    Optional<Cuenta> buscarCuentaPorCorreo(String correo);

    /**
     * Busca una cuenta por su ID único en formato ObjectId nativo de MongoDB
     * @param id Identificador único en formato ObjectId
     * @return Optional con la cuenta encontrada
     * @see ObjectId
     */
    @Query("{ '_id' : ?0 }")
    Optional<Cuenta> findById(ObjectId id);

    /**
     * Obtiene todos los correos electrónicos registrados en el sistema
     * @return Lista de strings con solo los campos email
     * @apiNote Usa proyección MongoDB para optimizar el rendimiento
     */
    @Query(value = "{}", fields = "{ 'email' : 1 }")
    List<String> obtenerTodosLosCorreos();

    /**
     * Obtiene todas las cuentas con rol de CLIENTE
     * @return Lista completa de cuentas de clientes (incluyendo inactivos)
     * @implNote Para filtrar por estado usar obtenerClientesActivos()
     */
    @Query(value = "{ 'rol' : 'CLIENTE' }")
    List<Cuenta> obtenerClientes();

    /**
     * Obtiene las cuentas de clientes con estado ACTIVO
     * @return Lista de cuentas activas de clientes
     * @attention Considerar combinar con filtro de rol para mayor precisión
     */
    @Query(value = "{ 'estadoCuenta' : 'ACTIVO' }")
    List<Cuenta> obtenerClientesActivos();
}