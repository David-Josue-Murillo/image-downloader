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
            Path destinoDirectorio = determinarDirectorioDestino(directorio);

            // Construir URL y abrir conexión HTTP
            URL url = new URL(urlString);
            HttpURLConnection conexion = crearConexion(url);

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

            // Guardar el contenido de la conexión en el archivo destino (se extrae a función)
            guardarContenidoDesdeConexion(conexion, rutaDestino);

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
            String extension = "img"; // extension genérico (fallback)
            if (tipoContenido != null && tipoContenido.contains("/")) {
                extension = tipoContenido.substring(tipoContenido.indexOf('/') + 1);
            }
            return "img_." + extension;
        }

        return nombreDesdeURL;
    }

    /**
     * Crea y configura una conexión HTTP (GET) para la URL proporcionada.
     *
     * Este método encapsula la configuración de timeouts, encabezados y
     * comportamiento de redirección para evitar duplicación en el método
     * principal de descarga.
     */
    private HttpURLConnection crearConexion(URL url) {
        try {
            HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
            conexion.setInstanceFollowRedirects(true); // seguir redirecciones si las hay
            conexion.setRequestMethod("GET");
            conexion.setConnectTimeout(10_000);
            conexion.setReadTimeout(5_000);
            conexion.setRequestProperty("User-Agent", "Java ImageDownloaderGUI");
            return conexion;
        } catch (IOException e) {
            throw new RuntimeException("Error al crear la conexión HTTP: " + e.getMessage(), e);
        }
    }

    /**
     * Crea o obtiene el directorio destino donde se guardará la imagen.
     * Si se proporciona `directorio`, se asegura que exista (creándolo si hace falta).
     * Si no se proporciona, delega en el FileManager.
     */
    private Path determinarDirectorioDestino(String directorio) throws IOException {
        if (directorio != null && !directorio.trim().isEmpty()) {
            Path destino = Paths.get(directorio);
            if (!Files.exists(destino)) {
                Files.createDirectories(destino);
            }
            return destino;
        } else {
            return fileManager.crearDirectorio();
        }
    }

    /**
     * Lee el InputStream de la conexión y escribe su contenido en `rutaDestino`.
     */
    private void guardarContenidoDesdeConexion(HttpURLConnection conexion, Path rutaDestino) throws IOException {
        try (BufferedInputStream entrada = new BufferedInputStream(conexion.getInputStream());
             BufferedOutputStream salida = new BufferedOutputStream(new FileOutputStream(rutaDestino.toFile()))) {

            final byte[] buffer = new byte[8 * 1024]; // buffer de 8KB
            int bytesLeidos;
            while ((bytesLeidos = entrada.read(buffer)) != -1) {
                salida.write(buffer, 0, bytesLeidos);
            }
            salida.flush();
        } catch (IOException e) {
            throw new IOException("Error al guardar el archivo en disco: " + rutaDestino.toString(), e);
        }
    }
}
