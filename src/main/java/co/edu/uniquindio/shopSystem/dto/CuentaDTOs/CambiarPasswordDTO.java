package co.edu.uniquindio.shopSystem.dto.CuentaDTOs;

public record CambiarPasswordDTO(
        String codigoVerificacion,
        String passwordNueva,
        String correo
) {
}