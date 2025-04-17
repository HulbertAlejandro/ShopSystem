package co.edu.uniquindio.shopSystem.controllers;

import co.edu.uniquindio.shopSystem.servicios.implementaciones.CloudinaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/auth/images")
public class ImageController {

    private final CloudinaryService cloudinaryService;

    public ImageController(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }

    @PostMapping("/subir-imagen")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            if (file == null) {
                return ResponseEntity.badRequest().body("El archivo es nulo.");
            }
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("No se ha recibido ningún archivo.");
            }

            System.out.println("Nombre del archivo: " + file.getOriginalFilename());
            System.out.println("Tipo de archivo: " + file.getContentType());
            System.out.println("Tamaño del archivo: " + file.getSize() + " bytes");

            String imageUrl = cloudinaryService.uploadImage(file);
            return ResponseEntity.ok(imageUrl);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error al subir la imagen");
        }
    }


}
