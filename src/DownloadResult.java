/**
 * Representa el resultado de una operación de descarga de imagen.
 *
 * Esta clase encapsula toda la información sobre el resultado de una descarga,
 * incluyendo el estado de éxito, mensajes descriptivos y la ruta del archivo.
 *
 */
public class DownloadResult {
    private boolean success;
    private String message;
    private String filePath;

    public DownloadResult(boolean success, String message, String filePath) {
        this.success = success;
        this.message = message;
        this.filePath = filePath;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getFilePath() {
        return filePath;
    }
}
