package co.edu.uniquindio.shopSystem.dto.CarritoDTOs;

import co.edu.uniquindio.shopSystem.modelo.vo.DetalleCarrito;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CarritoListDTO {
    private ObjectId id;
    private LocalDateTime fecha;
    private List<DetalleCarrito> items;
}
