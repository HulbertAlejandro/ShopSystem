package co.edu.uniquindio.shopSystem.controllers;

import co.edu.uniquindio.shopSystem.modelo.documentos.Producto;
import co.edu.uniquindio.shopSystem.modelo.documentos.Pedido;
import co.edu.uniquindio.shopSystem.modelo.documentos.Cuenta;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.*;

@RestController
@RequestMapping("/admin")
public class BackupController {

    @Autowired
    private MongoRepository<Producto, String> productoRepo;
    @Autowired
    private MongoRepository<Pedido, String> pedidoRepo;
    @Autowired
    private MongoRepository<Cuenta, String> cuentaRepo;

    @GetMapping("/backup")
    public ResponseEntity<InputStreamResource> exportarBackup() {
        try {
            Map<String, Object> backup = new HashMap<>();
            backup.put("productos", productoRepo.findAll());
            backup.put("pedidos", pedidoRepo.findAll());
            backup.put("cuentas", cuentaRepo.findAll());

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            new ObjectMapper().writeValue(out, backup);

            ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=backup_shopsystem.json");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new InputStreamResource(in));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }
}
