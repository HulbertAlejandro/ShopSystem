package co.edu.uniquindio.shopSystem.servicios.implementaciones;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * Servicio que gestiona la carga de imágenes a Cloudinary.
 * Utiliza las credenciales configuradas en el archivo de propiedades de la aplicación
 * para autenticar las operaciones de carga.
 */
@Service
@Transactional
public class CloudinaryService {

    private final Cloudinary cloudinary;

    /**
     * Constructor que inicializa la instancia de Cloudinary con las credenciales
     * necesarias proporcionadas desde las propiedades de la aplicación.
     *
     * @param cloudName nombre del cloud en Cloudinary
     * @param apiKey clave API de Cloudinary
     * @param apiSecret secreto API de Cloudinary
     */
    public CloudinaryService(@Value("${cloudinary.cloud-name}") String cloudName,
                             @Value("${cloudinary.api-key}") String apiKey,
                             @Value("${cloudinary.api-secret}") String apiSecret) {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        ));
    }

    /**
     * Carga una imagen a Cloudinary y retorna la URL pública de la imagen.
     *
     * @param file archivo de imagen que se desea cargar
     * @return URL pública de la imagen cargada
     * @throws IOException si ocurre un error durante la carga del archivo
     */
    public String uploadImage(MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        return uploadResult.get("url").toString(); // Retorna la URL de la imagen
    }
}
