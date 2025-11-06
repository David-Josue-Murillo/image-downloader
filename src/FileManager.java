import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileManager {
    private String directorio;

    public FileManager(String directorio) {
        this.directorio = directorio;
    }

    public Path crearDirectorio() throws IOException {
        Path path = Paths.get(directorio);

        // Si el directorio no existe, lo crea
        if(!Files.exists(path)) {
            Files.createDirectories(path);
        }

        // Devuelve la ruta del directorio
        return path;
    }
}
