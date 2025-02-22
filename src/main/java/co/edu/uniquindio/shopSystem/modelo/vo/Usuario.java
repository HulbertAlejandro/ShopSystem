package co.edu.uniquindio.shopSystem.modelo.vo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;

import java.util.List;

@Document("usuarios")
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Usuario {

    @Id
    @EqualsAndHashCode.Include
    private String codigo;

    private String cedula;
    private String nombre;
    private String email;
    private List<String> telefono;

}
