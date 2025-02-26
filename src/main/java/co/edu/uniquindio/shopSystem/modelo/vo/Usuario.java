package co.edu.uniquindio.shopSystem.modelo.vo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;

import java.util.List;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("usuarios")
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Usuario {

    @Id
    @EqualsAndHashCode.Include
    private String codigo;

    private String telefono;
    private String cedula;
    private String nombre;
    private String direccion;

}