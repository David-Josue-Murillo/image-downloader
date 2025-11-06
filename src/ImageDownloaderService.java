import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

// Este servicio es el responsable de descargar imágenes desde una URL y almacenarlas en disco.
public class ImageDownloaderService {
    private FileManager fileManager;

    public ImageDownloaderService(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    /*
     * Descarga una imagen desde la URL indicada y la guarda en el directorio proporcionado
     * o en el directorio configurado por el FileManager si `directorio` es nulo/ vacío.
     */
    public DownloadResult download(String urlString, String directorio) {
        try {
            // Determinar el directorio destino: usar el parámetro si se proporcionó,
            // en caso contrario delegar en el FileManager inyectado.
            final Path destinoDirectorio;
            if (directorio != null && !directorio.trim().isEmpty()) {
                destinoDirectorio = Paths.get(directorio);
                if (!Files.exists(destinoDirectorio)) {
                    Files.createDirectories(destinoDirectorio);
                }
            } else {
                destinoDirectorio = fileManager.crearDirectorio();
            }

            // Construir URL y abrir conexión HTTP
            URL url = new URL(urlString);
            HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
            conexion.setInstanceFollowRedirects(true); // seguir redirecciones si las hay
            conexion.setRequestMethod("GET");
            conexion.setConnectTimeout(10_000); // 10s timeout conexión
            conexion.setReadTimeout(5_000); // 5s timeout lectura
            conexion.setRequestProperty("User-Agent", "Java ImageDownloaderGUI");

            // Comprobar código de respuesta HTTP
            int codigoRespuesta = conexion.getResponseCode();
            if (codigoRespuesta != HttpURLConnection.HTTP_OK) {
                return new DownloadResult(false, "Error: código de respuesta HTTP " + codigoRespuesta, null);
            }

            // Verificar que el contenido es una imagen (image/*)
            String tipoContenido = conexion.getContentType();
            if (tipoContenido == null || !tipoContenido.startsWith("image/")) {
                return new DownloadResult(false,
                        "La URL no apunta a una imagen válida. Content-Type: " + tipoContenido,
                        null);
            }

            // Generar nombre de archivo de forma segura
            String nombreArchivo = generarNombreArchivo(urlString, tipoContenido);
            Path rutaDestino = destinoDirectorio.resolve(nombreArchivo);

            // Leer los bytes desde la conexión y escribirlos en el archivo destino
            try (BufferedInputStream entrada = new BufferedInputStream(conexion.getInputStream());
                 BufferedOutputStream salida = new BufferedOutputStream(new FileOutputStream(rutaDestino.toFile()))) {

                final byte[] buffer = new byte[8 * 1024]; // buffer de 8KB
                int bytesLeidos;
                while ((bytesLeidos = entrada.read(buffer)) != -1) {
                    salida.write(buffer, 0, bytesLeidos);
                }
                salida.flush();
            }

            // Éxito: devolver ruta del archivo
            return new DownloadResult(true, "Imagen descargada exitosamente en: " + rutaDestino.toString(), rutaDestino.toString());

        } catch (MalformedURLException e) {
            return new DownloadResult(false, "URL mal formada: " + e.getMessage(), null);
        } catch (IOException e) {
            // En caso de cualquier excepción de E/S, devolver mensaje con la causa
            return new DownloadResult(false, "Error de red durante la descarga: " + e.getMessage(), null);
        }
    }

    /**
     * Genera un nombre de archivo a partir de la URL y el tipo de contenido.
     *
     * Si la URL contiene un nombre válido (por ejemplo example.jpg) se usa tal cual.
     * En caso contrario se genera un nombre por defecto con la extensión derivada
     * del Content-Type (por ejemplo image/png -> png).
     */
    private String generarNombreArchivo(String urlString, String tipoContenido) {
        // Intentar extraer el segmento final de la URL después del último '/'
        String nombreDesdeURL = urlString.substring(Math.max(0, urlString.lastIndexOf('/') + 1));

        // Si no se obtiene un nombre válido (vacío o sin extensión), construir uno por defecto
        if (nombreDesdeURL.isEmpty() || !nombreDesdeURL.contains(".")) {
            String extension = "img"; // extension genérico
            if (tipoContenido != null && tipoContenido.contains("/")) {
                extension = tipoContenido.substring(tipoContenido.indexOf('/') + 1);
            }
            return "imagen_descargada." + extension;
        }

        return nombreDesdeURL;
    }
}
