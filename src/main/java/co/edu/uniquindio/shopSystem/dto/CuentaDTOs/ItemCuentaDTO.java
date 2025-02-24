package co.edu.uniquindio.shopSystem.dto.CuentaDTOs;

import co.edu.uniquindio.shopSystem.modelo.enums.EstadoCuenta;
import co.edu.uniquindio.shopSystem.modelo.enums.Rol;
import org.bson.types.ObjectId;

public record ItemCuentaDTO(
        ObjectId id,
        String nombre,
        String email,
        String telefono,
        EstadoCuenta estadoCuenta,
        Rol rol
) {
}
