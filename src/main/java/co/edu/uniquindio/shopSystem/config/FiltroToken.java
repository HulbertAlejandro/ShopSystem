package co.edu.uniquindio.shopSystem.config;

import co.edu.uniquindio.shopSystem.dto.TokenDTOs.MensajeDTO;
import co.edu.uniquindio.shopSystem.modelo.enums.Rol;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

/**
 * Filtro de seguridad para validación de tokens JWT y control de acceso basado en roles.
 * Se ejecuta en cada solicitud para proteger los endpoints de la API.
 */
@Component
@RequiredArgsConstructor
public class FiltroToken extends OncePerRequestFilter {


    private final JWTUtils jwtUtils;

    /**
     * Filtra cada solicitud HTTP para validar autenticación y autorización
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {


        // Configuración de cabeceras para CORS
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.addHeader("Access-Control-Allow-Headers", "Origin, Accept, Content-Type, Authorization");


        if (request.getMethod().equals("OPTIONS")) {
            response.setStatus(HttpServletResponse.SC_OK);
        }else {


            //Obtener la URI de la petición que se está realizando
            String requestURI = request.getRequestURI();


            //Se obtiene el token de la petición del encabezado del mensaje HTTP
            String token = getToken(request);
            boolean error = true;


            try {

                //Si la petición es para la ruta /api/cliente se verifica que el token exista y que el rol sea CLIENTE
                if (requestURI.startsWith("/api/cliente")) {
                    error = validarToken(token, Rol.CLIENTE);
                }else {
                    error = false;
                }


                //Agregar la validación para las peticiones que sean de los administradores


                //Si hay un error se crea una respuesta con el mensaje del error
                if(error){
                    crearRespuestaError("No tiene permisos para acceder a este recurso", HttpServletResponse.SC_FORBIDDEN, response);
                }


            } catch (MalformedJwtException | SignatureException e) {
                crearRespuestaError("El token es incorrecto", HttpServletResponse.SC_INTERNAL_SERVER_ERROR, response);
            } catch (ExpiredJwtException e) {
                crearRespuestaError("El token está vencido", HttpServletResponse.SC_INTERNAL_SERVER_ERROR, response);
            } catch (Exception e) {
                crearRespuestaError(e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR, response);
            }


            //Si no hay errores se continúa con la petición
            if (!error) {
                filterChain.doFilter(request, response);
            }
        }


    }

    /**
     * Obtiene el token JWT del encabezado de autorización
     * @param req Objeto HttpServletRequest
     * @return Token JWT (sin el prefijo "Bearer") o null si no existe
     * @implNote Espera el formato: "Bearer <token>"
     */
    private String getToken(HttpServletRequest req) {
        String header = req.getHeader("Authorization");
        return header != null && header.startsWith("Bearer ") ? header.replace("Bearer ", "") : null;
    }

    /**
     * Crea una respuesta de error estandarizada en formato JSON
     * @param mensaje Descripción del error
     * @param codigoError Código HTTP de error
     * @param response Objeto HttpServletResponse para escribir la respuesta
     * @throws IOException Si falla la escritura en la respuesta
     */
    private void crearRespuestaError(String mensaje, int codigoError, HttpServletResponse response) throws IOException {
        MensajeDTO<String> dto = new MensajeDTO<>(true, mensaje);


        response.setContentType("application/json");
        response.setStatus(codigoError);
        response.getWriter().write(new ObjectMapper().writeValueAsString(dto));
        response.getWriter().flush();
        response.getWriter().close();
    }

    /**
     * Valida un token JWT contra un rol específico
     * @param token Token JWT a validar
     * @param rol Rol requerido para el acceso
     * @return true si hay error de validación, false si es válido
     * @throws ExpiredJwtException Si el token está expirado
     * @throws MalformedJwtException Si el token está mal formado
     * @throws SignatureException Si la firma es inválida
     * @throws IllegalArgumentException Si el token es nulo
     */
    private boolean validarToken(String token, Rol rol){
        boolean error = true;
        if (token != null) {
            Jws<Claims> jws = jwtUtils.parseJwt(token);
            if (Rol.valueOf(jws.getPayload().get("rol").toString()) == rol) {
                error = false;
            }
        }
        return error;
    }

}
