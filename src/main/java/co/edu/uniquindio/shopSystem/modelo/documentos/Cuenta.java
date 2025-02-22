package co.edu.uniquindio.shopSystem.modelo.documentos;

import co.edu.uniquindio.shopSystem.modelo.vo.CodigoValidacion;
import co.edu.uniquindio.shopSystem.modelo.enums.EstadoCuenta;
import co.edu.uniquindio.shopSystem.modelo.enums.Rol;
import co.edu.uniquindio.shopSystem.modelo.vo.Usuario;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document("cuentas")
@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
public class Cuenta{

    private String id;
    private Usuario usuario;
    private String email;
    private String password;
    private Rol rol;
    private EstadoCuenta estadoCuenta;
    private LocalDateTime fechaRegistro;
    private CodigoValidacion codigoValidacionRegistro;
    private CodigoValidacion codigoValidacionPassword;
}