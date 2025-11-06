/**
 * Representa el resultado de una operación de descarga de imagen.
 *
 * Esta clase encapsula toda la información sobre el resultado de una descarga,
 * incluyendo el estado de éxito, mensajes descriptivos y la ruta del archivo.
 *
 */
public class DownloadResult {
    private boolean exito;
    private String mensaje;
    private String rutaArchivo;

    public DownloadResult(boolean exito, String mensaje, String rutaArchivo) {
        this.exito = exito;
        this.mensaje = mensaje;
        this.rutaArchivo = rutaArchivo;
    }

    public boolean esExito() {
        return exito;
    }

    public String getMensaje() {
        return mensaje;
    }

    public String getRutaArchivo() {
        return rutaArchivo;
    }
}
