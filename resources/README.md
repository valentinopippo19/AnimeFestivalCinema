# Anime Festival Cinema — Proyecto Final POO Java

Versión web del proyecto de gestión de festival de cine, adaptada **exclusivamente a películas de anime**.

## Qué cambió

- La ejecución ya no abre la aplicación Swing como pantalla principal.
- `app.Main` levanta un servidor HTTP local en `http://127.0.0.1:8765/` y abre el navegador automáticamente cuando el sistema operativo lo permite.
- Cartelera visual con posters individuales de anime.
- Funciones internacionales con **día, horario, ciudad, país y sala**.
- Reserva de butaca mediante un mapa de asientos.
- Validación de butaca ocupada y capacidad de sala.
- Creación de cuenta e inicio/cierre de sesión.
- Recomendador tipo IA local para sugerir anime por puntuación.
- Video de apertura seleccionable desde el navegador.
- Video de salida seleccionable al abandonar el festival.
- Diseño responsive tipo plataforma comercial de cine, con navegación por secciones, cards, filtros y modales.

## Cómo ejecutar

### IntelliJ IDEA

1. Abrir el proyecto como proyecto Java.
2. Ejecutar `src/app/Main.java`.
3. Se iniciará el servidor web.
4. Se abrirá `http://127.0.0.1:8765/` en el navegador.
5. Si el navegador no se abre automáticamente, copiar esa dirección manualmente.

### Consola

```bash
javac -encoding UTF-8 -d out $(find src -name '*.java')
java -cp out app.Main
```

En Windows PowerShell se puede compilar con IntelliJ o ejecutar una compilación equivalente con `Get-ChildItem`.

## Videos de apertura y salida

La interfaz permite elegir archivos de video locales:

- Al entrar: botón de selección en la pantalla de apertura.
- Al salir: botón `Salir del festival`, que solicita un video de salida y lo reproduce antes de abandonar el sitio.

Los videos se reproducen del lado del navegador y no se suben a ningún servidor externo.

## Arquitectura

Se conserva la separación de responsabilidades de POO:

- `modelo/`: entidades del dominio.
- `servicios/`: reglas de negocio.
- `persistencia/`: persistencia existente del TP original.
- `seguridad/`: cuentas, autenticación, repository y sesiones.
- `ia/`: estrategia de recomendación.
- `patrones/`: eventos de reserva y Observer.
- `web/`: servidor HTTP y controlador web.
- `web/index.html`, `styles.css`, `app.js`: frontend.

### Patrones incorporados

Se incorporan patrones **cuando tienen una responsabilidad real** en el diseño:

- MVC: separación entre dominio, servicios y presentación web.
- Repository: `CuentaRepository`.
- Singleton: `SesionManager`.
- Strategy: `Recomendador` / `RecomendadorAnime`.
- Observer: `ReservaEventBus` / `ReservaObserver` / `ReservaLogger`.
- Facade / Service Layer: `CuentaServicio` y servicios de dominio.
- Factory/Seeder: `AnimeFestivalSeeder` centraliza la construcción del catálogo demo.

No es conveniente forzar literalmente todos los patrones de diseño conocidos: hacerlo produciría acoplamiento artificial y código innecesario. La implementación utiliza los patrones que resuelven problemas concretos del sistema.

## IA

La sección `IA · Recomendador Anime` utiliza una estrategia local de ranking por puntuaciones del jurado. Está preparada para reemplazar `RecomendadorAnime` por una implementación de ML o una API de IA sin modificar la interfaz web.

## Datos demo

El catálogo inicial contiene:

- Your Name
- Weathering With You
- Suzume
- Spirited Away
- A Silent Voice
- Perfect Blue
- Akira
- Demon Slayer: Mugen Train

Las funciones se distribuyen en ciudades de Japón, España, Francia, Argentina, Corea del Sur, Estados Unidos, Reino Unido y Chile.

## Nota sobre el TP original

Los paneles Swing, clases de persistencia y entidades originales se conservan para no destruir el trabajo previo. El nuevo `Main` utiliza la aplicación web como interfaz principal.
