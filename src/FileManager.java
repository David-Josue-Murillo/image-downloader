import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Gestor de archivos y directorios para la aplicación de descarga de imágenes.
 *
 * Esta clase centraliza las operaciones relacionadas con el sistema de archivos,
 * incluyendo la creación de directorios y validación de rutas.
 */
public class FileManager {
    private String directorio;

    public FileManager(String directorio) {
        if (directorio == null || directorio.trim().isEmpty()) {
            throw new IllegalArgumentException("El directorio no puede ser null o vacío");
        }
        this.directorio = directorio.trim();
    }

    /**
     * Crea el directorio configurado si no existe.
     *
     * Este método es idempotente: si el directorio ya existe, simplemente
     * devuelve su ruta sin realizar cambios.
     */
    public Path crearDirectorio() throws IOException {
        Path path = Paths.get(directorio);

        try {
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            throw new IOException("No se pudo crear el directorio: " + directorio, e);
        }

        // Devuelve la ruta del directorio
        return path;
    }
}
