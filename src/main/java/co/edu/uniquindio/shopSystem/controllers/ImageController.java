package co.edu.uniquindio.shopSystem.controllers;

import co.edu.uniquindio.shopSystem.servicios.implementaciones.CloudinaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Controlador para la gestión de imágenes mediante Cloudinary.
 * Proporciona endpoints para la subida de archivos multimedia.
 */
@RestController
@RequestMapping("/api/auth/images")
public class ImageController {

    private final CloudinaryService cloudinaryService;

    /**
     * Constructor para inyección de dependencias
     * @param cloudinaryService Servicio de Cloudinary configurado
     */
    public ImageController(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }

    /**
     * Sube una imagen al servicio Cloudinary
     * @param file Archivo de imagen a subir (Formatos soportados: JPEG, PNG, GIF)
     * @return URL pública de la imagen subida
     * @apiNote Límite de tamaño: 10MB
     * @throws IOException Si ocurre error en la comunicación con Cloudinary

     * Ejemplo de solicitud:
     * POST /api/auth/images/subir-imagen
     * Content-Type: multipart/form-data

     * Ejemplo de respuesta exitosa:
     * HTTP 200 OK
     * "https://res.cloudinary.com/.../image.jpg"

     * Posibles errores:
     * - 400: Archivo vacío o inválido
     * - 500: Error en el servidor de imágenes
     */
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
