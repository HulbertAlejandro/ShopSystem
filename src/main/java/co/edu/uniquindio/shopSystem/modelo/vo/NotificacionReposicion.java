package co.edu.uniquindio.shopSystem.modelo.vo;

import co.edu.uniquindio.shopSystem.modelo.documentos.Producto;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class NotificacionReposicion {

    private Producto producto;
    private int stockActual;
    private int stockMinimo;
    private LocalDateTime fechaNotificacion;
    private boolean atendida; // Indica si ya se procesó la notificación

    public NotificacionReposicion(Producto producto, int stockActual, int stockMinimo) {
        this.producto = producto;
        this.stockActual = stockActual;
        this.stockMinimo = stockMinimo;
        this.fechaNotificacion = LocalDateTime.now();
        this.atendida = false;
    }

    public boolean necesitaReposicion() {
        return stockActual < stockMinimo;
    }
}
