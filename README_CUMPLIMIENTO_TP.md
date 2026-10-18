# Cumplimiento del TP - Anime Festival Cinema

La entrega mantiene **dos interfaces** sobre el mismo dominio y servicios:

- **Java Swing (interfaz oficial del TP):** `app.Main` inicia la aplicación de escritorio.
- **Panel web (interfaz existente conservada):** `app.WebMain` inicia el panel web en `http://127.0.0.1:8765/`. También se puede ejecutar `app.Main --web`.

## Requisitos técnicos incorporados

- Java JDK 17 y aplicación de escritorio Swing.
- Ventana principal con menús `Archivo`, `Festival`, `Películas`, `Funciones`, `Entradas`, `Evaluaciones` y `Ayuda`.
- Persistencia en archivos de texto plano `.txt` para salas, directores, actores, jurados, espectadores, películas y evaluaciones.
- Excepciones de negocio no checked mediante `FestivalException` y derivadas.
- Excepción checked `PersistenciaException` para errores de archivo.
- Guardado desde `Archivo -> Guardar datos` en segundo plano para no bloquear Swing.
- Confirmación al cerrar y guardado automático del snapshot completo.
- Se agregaron paneles Swing de secciones/premios, abonos y voto público.
- Se conserva el panel web y la lógica existente.
- UML disponible también en `uml/FestivalCine.mdj`.
