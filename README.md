# Anime Festival Cinema — Proyecto Final PROCESO DE DESARROLLO DE SOFTWARE

Versión web de un sistema de gestión de un Festival Internacional de Cine, adaptado exclusivamente a películas de anime.

La aplicación permite gestionar el festival y sus ediciones, consultar la cartelera, programar funciones presenciales y de streaming, reservar entradas, administrar cuentas de espectadores, consultar evaluaciones del jurado y obtener recomendaciones de películas.

El proyecto está desarrollado en Java y cuenta con una interfaz web basada en HTML, CSS y JavaScript, manteniendo la separación de responsabilidades mediante programación orientada a objetos, capas de servicios y patrones de diseño.

## Tabla de contenidos

- [Características principales](#características-principales)
- [Qué cambió respecto del proyecto original](#qué-cambió-respecto-del-proyecto-original)
- [Requisitos](#requisitos)
- [Cómo ejecutar](#cómo-ejecutar)
- [Videos de apertura y salida](#videos-de-apertura-y-salida)
- [Funcionalidades](#funcionalidades)
- [Arquitectura](#arquitectura)
- [Patrones de diseño](#patrones-de-diseño)
- [Recomendador de anime](#recomendador-de-anime)
- [Datos de demostración](#datos-de-demostración)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Persistencia y decisiones de diseño](#persistencia-y-decisiones-de-diseño)
- [Nota sobre el TP original](#nota-sobre-el-tp-original)
- [Videos del festival](#videos-del-festival)
- [Películas y recursos visuales](#películas-y-recursos-visuales)
- [Trailers](#trailers-de-peliculas)
- [Waifu](#Waifu-del-repositorio-Kurumi-Tokisaki)
- [Clases](#Diagramas-de-clases)

## Características principales

- Interfaz web responsive con estética de plataforma comercial de cine.
- Cartelera visual con pósteres JPG individuales de películas de anime.
- Fichas de películas con título, descripción, género, duración, director, elenco y valoración.
- Búsqueda y filtrado de películas.
- Programación internacional con día, horario, ciudad, país y sala.
- Funciones presenciales con selección visual de butacas.
- Funciones de streaming con generación de códigos de acceso únicos.
- Validación de disponibilidad de butacas y capacidad de las salas.
- Registro de usuarios e inicio y cierre de sesión.
- Historial de entradas y reservas.
- Estadísticas del festival y visualización del porcentaje de ocupación.
- Gestión de ediciones, secciones y premios.
- Jurados y evaluaciones con puntuaciones de 0 a 10.
- Cálculo de promedios y determinación de ganadores por sección.
- Recomendador local de anime basado en las puntuaciones disponibles.
- Videos de apertura y cierre integrados en la experiencia web.

## Qué cambió respecto del proyecto original

La versión actual utiliza una interfaz web como punto de entrada principal, en lugar de abrir la aplicación Swing como pantalla inicial.

Los principales cambios son:

- `app.Main` inicia un servidor HTTP local en `http://127.0.0.1:8765/`.
- El sistema intenta abrir automáticamente el navegador cuando el entorno lo permite.
- La cartelera presenta pósteres individuales y fichas descriptivas de anime.
- Las funciones incluyen información de fecha, horario, ciudad, país y sala.
- Las reservas presenciales permiten seleccionar butacas desde un mapa visual.
- Las funciones de streaming contemplan códigos de acceso únicos.
- Se incorporan pantallas para crear cuentas, iniciar sesión y consultar el historial.
- Se incluye un recomendador local de películas.
- Se incorporan videos de apertura y salida.
- La interfaz utiliza navegación por secciones, tarjetas, filtros, modales y componentes responsive.
- Se conserva la organización del dominio y la lógica de negocio en componentes Java separados de la presentación.

## Modificaciones y agregados realizados para ADOO

Esta versión incorpora al proyecto los contenidos de diseño orientado a objetos y patrones de diseño trabajados en la materia. Los cambios no se limitan a documentación: se agregaron clases, interfaces y colaboraciones concretas dentro del código para que los patrones puedan identificarse y explicarse en una defensa oral.

### 1. GRASP

Se reorganizaron responsabilidades para aplicar los principales patrones GRASP vistos en la materia:

- **Controller:** `WebServer` y las clases de servicio coordinan las operaciones correspondientes a los casos de uso.
- **Information Expert:** las entidades del dominio conservan y calculan información que conocen directamente.
- **Creator:** las factories y objetos agregadores se encargan de crear objetos relacionados.
- **Polymorphism:** entradas, abonos, funciones y estados utilizan comportamiento polimórfico mediante herencia o interfaces.
- **Low Coupling:** se introdujeron abstracciones para reducir dependencias directas entre componentes.
- **High Cohesion:** se separaron responsabilidades entre dominio, servicios, persistencia, seguridad, patrones y presentación.
- **Indirection:** Repository, DAO, Facade y Adapter funcionan como intermediarios para desacoplar componentes.
- **Pure Fabrication:** servicios y componentes de persistencia encapsulan responsabilidades que no corresponden directamente a una entidad del dominio.
- **Protected Variations:** las interfaces protegen a los clientes frente a cambios de algoritmos, persistencia o proveedores externos.

### 2. SOLID y reducción de code smells

Se agregaron abstracciones y separaciones de responsabilidades para aplicar SOLID y evitar acoplamiento innecesario:

- **SRP:** responsabilidades separadas entre entidades, servicios, persistencia, factories, estados, estrategias y presentación.
- **OCP:** Strategy, State, Decorator, Factory y DAO permiten extender comportamientos sin modificar los consumidores principales.
- **LSP:** las implementaciones concretas respetan los contratos de sus abstracciones.
- **ISP:** se utilizan interfaces específicas como `PrecioStrategy`, `EstadoFuncion`, `ReservaObserver` y `PeliculaDAO`.
- **DIP:** los consumidores trabajan contra abstracciones en lugar de depender directamente de implementaciones concretas.

También se evitó concentrar demasiadas responsabilidades en una única clase, reduciendo duplicación y dependencias rígidas.

### 3. MVC

Se documentó y reforzó la separación MVC:

- **Model:** `src/modelo/` contiene las entidades del dominio.
- **View:** `src/web/index.html`, `styles.css` y `app.js` contienen la presentación e interacción web.
- **Controller:** `WebServer` recibe solicitudes HTTP y coordina las operaciones.
- **Application layer:** `src/servicios/` concentra los casos de uso y reglas de aplicación.

### 4. Strategy

Se agregaron estrategias intercambiables para representar algoritmos que pueden variar:

- `PrecioStrategy`: abstracción para el cálculo de precios.
- `PrecioBaseStrategy`: cálculo del precio base.
- `Recomendador`: abstracción del algoritmo de recomendación.
- `RecomendadorAnime`: implementación de recomendación local basada en los datos disponibles.

El objetivo es poder incorporar otra política de precios o recomendación sin modificar los clientes que consumen la estrategia.

### 5. Adapter

Se incorporó una integración desacoplada para un recomendador externo:

- `RecomendadorExterno`: representa una interfaz/proveedor con un contrato diferente.
- `RecomendadorAdapter`: adapta ese contrato al de `Recomendador`.

De esta forma, el resto de la aplicación puede trabajar con la abstracción interna sin conocer los detalles del proveedor externo.

### 6. Composite

Se incorporó una estructura jerárquica para representar la cartelera:

- `ElementoCartelera`: componente común.
- `PeliculaHoja`: elemento hoja de la estructura.
- `CarteleraComposite`: compuesto que puede contener otros elementos de cartelera.

La estructura permite tratar películas individuales y agrupaciones de cartelera de manera uniforme.

### 7. Decorator

Se incorporó composición dinámica de reglas de precio:

- `PrecioDecorator`: decorador base.
- `DescuentoTarifaDecorator`: agrega una política de descuento por tarifa.
- `DescuentoAnticipadoDecorator`: agrega descuento por compra anticipada.

Los descuentos pueden combinarse sin modificar el cálculo base ni crear una subclase para cada combinación posible.

### 8. Facade

Se agregó `AnimeFestivalFacade` como punto de acceso simplificado a operaciones del sistema. La Fachada reduce la cantidad de detalles que necesita conocer la capa cliente y concentra la coordinación de operaciones relacionadas.

### 9. State

Se incorporó el patrón State para modelar el estado de una función:

- `EstadoFuncion`: interfaz/abstracción del estado.
- `FuncionDisponible`: permite operaciones de reserva.
- `FuncionAgotada`: representa una función sin disponibilidad.
- `FuncionFinalizada`: representa una función que ya terminó.

La clase `Funcion` delega el comportamiento dependiente del estado al objeto de estado activo, evitando condicionales rígidos basados en un único atributo de estado.

### 10. Observer

Se agregó un mecanismo de eventos para notificar operaciones de reserva:

- `EventoReserva`: representa el evento generado.
- `ReservaObserver`: contrato para los observadores.
- `ReservaEventBus`: administra la publicación y suscripción de eventos.
- `ReservaLogger`: observador concreto que registra los eventos.

Esto desacopla la generación de una reserva de las acciones que quieran reaccionar ante ella.

### 11. Singleton

Se incorporó `SesionManager` como instancia compartida para centralizar la gestión de sesión de usuario. El patrón evita que diferentes componentes creen gestores de sesión independientes cuando se necesita un único punto de acceso.

### 12. Factory

Se agregaron dos factories para centralizar la creación de objetos complejos:

- `EntradaFactory`: creación de entradas presenciales y de streaming.
- `AbonoFactory`: creación de las variantes de abono.

Esto evita distribuir lógica de construcción por diferentes clientes y facilita incorporar nuevas variantes.

### 13. DAO

Se incorporó una capa DAO para aislar el acceso a datos de películas:

- `PeliculaDAO`: contrato de acceso.
- `PeliculaDAOMemoria`: implementación en memoria.
- `PeliculaDAOArchivo`: implementación sobre archivo.

La capa de negocio no necesita conocer los detalles de almacenamiento.

### 14. Repository

Se agregaron repositorios para trabajar con colecciones/agregados desde la lógica de aplicación:

- `PeliculaRepository`: acceso de alto nivel a películas.
- `CuentaRepository`: acceso de alto nivel a cuentas.
- `CuentaRepositoryMemoria`: implementación en memoria para cuentas.

El Repository oculta la forma concreta en que se obtienen o almacenan las entidades.

### 15. Table Data Gateway

Se incorporó `PeliculaTableDataGateway` como variante de acceso a datos orientada a las operaciones de almacenamiento de películas. Se relaciona con la implementación DAO para separar las operaciones sobre los datos de la lógica de negocio.

### 16. Variantes de persistencia

Se agregaron/organizaron variantes de acceso a datos para mostrar la diferencia entre almacenamiento en memoria y almacenamiento en archivo. Esto permite cambiar la estrategia de persistencia sin modificar los consumidores de las abstracciones.

### 17. Persistencia y excepciones

Se mantuvieron separadas las responsabilidades de persistencia y se incorporaron excepciones específicas para representar errores del dominio y de la aplicación, entre ellas errores de capacidad, butaca ocupada, función no disponible, persistencia, entidad inexistente y votos inválidos.

### 18. IA aplicada al diseño OO

El recomendador de anime se organizó como una funcionalidad desacoplada mediante Strategy. `RecomendadorAnime` permite implementar recomendaciones locales a partir de puntuaciones y datos disponibles, mientras que `RecomendadorAdapter` deja preparada la integración con un proveedor externo sin acoplar la aplicación a su contrato.

La documentación aclara que el recomendador actual es un mecanismo basado en reglas/puntuaciones y no implica el entrenamiento de un modelo de machine learning.

### 19. Seeder y datos de demostración

Se mantuvo y organizó la inicialización de datos mediante `AnimeFestivalSeeder` e `InicializadorDatos`, separando la carga de datos de demostración del resto de la lógica de negocio.

### 20. Documentación adicional

Se agregó el archivo `PATRONES_ADOO.md`, que contiene:

- tabla de correspondencia entre contenidos de la materia y clases del proyecto;
- explicación de GRASP y SOLID aplicados;
- explicación de MVC;
- diagramas textuales de Strategy, Adapter, Composite, Decorator, State, Observer y persistencia;
- relación entre Repository, DAO y Table Data Gateway;
- explicación de la integración de IA con diseño OO;
- nota de validación de compilación y ejecución.

### Resumen de clases nuevas relacionadas con ADOO

```text
src/patrones/
├── AbonoFactory.java
├── AnimeFestivalFacade.java
├── CarteleraComposite.java
├── DescuentoAnticipadoDecorator.java
├── DescuentoTarifaDecorator.java
├── ElementoCartelera.java
├── EntradaFactory.java
├── EstadoFuncion.java
├── EventoReserva.java
├── FuncionAgotada.java
├── FuncionDisponible.java
├── FuncionFinalizada.java
├── PeliculaHoja.java
├── PrecioBaseStrategy.java
├── PrecioDecorator.java
├── PrecioStrategy.java
├── RecomendadorAdapter.java
├── RecomendadorExterno.java
├── ReservaEventBus.java
├── ReservaLogger.java
└── ReservaObserver.java

src/persistencia/
├── PeliculaDAO.java
├── PeliculaDAOArchivo.java
├── PeliculaDAOMemoria.java
├── PeliculaRepository.java
└── PeliculaTableDataGateway.java

src/seguridad/
├── CuentaRepository.java
├── CuentaRepositoryMemoria.java
└── SesionManager.java
```

Estos agregados se integran con las clases existentes del dominio y los servicios para que los patrones tengan una responsabilidad concreta dentro del sistema.

## Requisitos

Para ejecutar el proyecto se necesita:

- Java Development Kit (JDK) 21 o superior.
- `javac` y `java` disponibles en el `PATH`, o un entorno de desarrollo configurado.
- IntelliJ IDEA u otro entorno compatible con proyectos Java.
- Un puerto local disponible para iniciar el servidor HTTP.

La aplicación utiliza el puerto `8765` de forma predeterminada.

## Cómo ejecutar

### IntelliJ IDEA

1. Abrir el proyecto como un proyecto Java.
2. Configurar un JDK 21 o superior.
3. Verificar que el directorio `src` esté reconocido como fuente del proyecto.
4. Abrir `src/app/Main.java`.
5. Ejecutar la clase `app.Main`.
6. Esperar a que se inicie el servidor web.
7. Abrir `http://127.0.0.1:8765/` en el navegador si no se abre automáticamente.

### Consola en Linux o macOS

Desde la raíz del repositorio, compilar los archivos Java:

```bash
javac -encoding UTF-8 -d out $(find src -name '*.java')
```

Después, ejecutar la aplicación:

```bash
java -cp out app.Main
```

### Windows PowerShell

Desde la raíz del proyecto, ejecutar:

```powershell
$sources = Get-ChildItem src -Recurse -Filter *.java |
    ForEach-Object { $_.FullName }

javac -encoding UTF-8 -d out $sources

java -cp out app.Main
```

También se puede utilizar `resources/run.bat` desde una terminal compatible, si el script está presente y configurado correctamente.

### Acceso a la aplicación

La dirección local es:

**http://127.0.0.1:8765/**

El servidor debe permanecer ejecutándose mientras se utiliza la aplicación. Para detenerlo, finalizar el proceso desde la terminal o el entorno de desarrollo.

## Videos de apertura y salida

El proyecto contempla dos videos para acompañar la entrada y la salida del festival:

- **Video de apertura:** `src/web/assets/video/entrada.mp4`
- **Video de salida:** `src/web/assets/video/salida.mp4`

Los archivos se sirven desde el propio proyecto mediante las rutas configuradas en el servidor web.

### Video de apertura

Al ingresar al sitio se muestra una pantalla de introducción a pantalla completa. El usuario puede entrar al festival o saltar la introducción.

La reproducción intenta habilitar el audio, pero los navegadores pueden bloquear la reproducción automática con sonido. En ese caso, el usuario debe poder activar el sonido mediante una interacción manual.

### Video de salida

El cierre presenta una pantalla final con opciones para abandonar la experiencia o interrumpir la reproducción.

Los videos se reproducen desde el navegador utilizando recursos del propio proyecto; no requieren subir los archivos a un servicio externo.

> **Nota:** la reproducción automática con sonido depende de las políticas del navegador. Para garantizar el comportamiento esperado, las rutas, los controles y la función de activación del audio deben coincidir con la implementación actual de `app.js` y `WebServer.java`.

## Funcionalidades

### 1. Cartelera oficial

La cartelera muestra las películas de anime disponibles en el catálogo.

Cada ficha puede incluir:

- Póster JPG.
- Título.
- Descripción.
- Género.
- Duración.
- Director.
- Elenco.
- Promedio de evaluación.

La interfaz permite buscar películas mediante un campo de texto y actualizar los resultados sin abandonar la página.

### 2. Funciones internacionales

El sistema contempla funciones presenciales y de streaming.

La información de programación puede incluir:

- Película asociada.
- Fecha.
- Horario.
- Ciudad.
- País.
- Sala o modalidad de exhibición.
- Capacidad disponible.

La interfaz permite filtrar las funciones según la película seleccionada.

### 3. Reservas presenciales

Las funciones presenciales cuentan con un mapa visual de butacas.

El sistema contempla:

- Identificación de butacas disponibles.
- Identificación de butacas ocupadas.
- Selección visual de asientos.
- Validación de disponibilidad.
- Control de capacidad de la sala.
- Confirmación de la reserva.

La validación definitiva debe realizarse en el backend para impedir que una misma butaca se reserve más de una vez ante solicitudes concurrentes.

### 4. Entradas de streaming

Las funciones virtuales permiten reservar entradas sin seleccionar una butaca física.

Al confirmar una entrada, el sistema genera un código de acceso único, según la lógica implementada en el servicio correspondiente.

La disponibilidad de la función debe respetar su capacidad virtual configurada.

### 5. Registro y autenticación

La aplicación incluye formularios para crear cuentas e iniciar sesión.

También contempla el cierre de sesión y la actualización de los datos de usuario mostrados en la interfaz.

Las operaciones protegidas deben validar la sesión en el backend, independientemente de los controles visuales del navegador.

### 6. Historial de entradas

La sección de cuenta permite consultar el historial de entradas asociadas al usuario autenticado, incluyendo la información que proporcione el servicio de historial.

### 7. Festival, ediciones, secciones y premios

El dominio contempla un festival organizado en ediciones con información temporal y geográfica.

Las secciones previstas son:

- Competencia Oficial.
- Cine Independiente.
- Cortometraje.
- Nuevos Talentos.

Cada sección puede asociarse con un premio. Las evaluaciones permiten calcular promedios y determinar las películas ganadoras de acuerdo con las reglas del sistema.

### 8. Jurados y evaluaciones

El sistema contempla jurados que califican películas mediante puntuaciones de 0 a 10.

Las evaluaciones se utilizan para calcular promedios y determinar ganadores por sección.

### 9. Estadísticas y ocupación

La interfaz incluye un área de estadísticas y componentes visuales para representar información del festival, como el porcentaje de ocupación de las funciones.

Los valores mostrados dependen de los datos devueltos por el backend.

## Arquitectura

El sistema conserva una separación de responsabilidades basada en programación orientada a objetos.

### Modelo de dominio

El paquete `modelo/` contiene las entidades que representan los conceptos del festival y sus relaciones.

### Servicios

El paquete `servicios/` concentra las operaciones y reglas de negocio.

Entre las responsabilidades contempladas se encuentran la gestión del festival, películas, funciones, entradas y cuentas.

### Persistencia

El paquete `persistencia/` agrupa los mecanismos de almacenamiento que utiliza la implementación original y que se conservan en el proyecto.

### Seguridad

El paquete `seguridad/` contiene los componentes relacionados con cuentas, autenticación, repositorios y sesiones.

### Recomendación

El paquete `ia/` contiene la estrategia local de recomendación de películas.

### Patrones

El paquete `patrones/` agrupa los componentes relacionados con la publicación y observación de eventos, según la implementación existente.

### Servidor web

El paquete `web/` contiene el servidor HTTP y los recursos de presentación.

Los principales archivos del frontend son:

- `index.html`: estructura de la página.
- `styles.css`: estilos y diseño responsive.
- `app.js`: interacciones, renderizado y comunicación con el backend.

La arquitectura permite mantener la presentación web separada de las entidades y las reglas de negocio.

## Patrones de diseño

La entrega final incorpora los patrones y mecanismos de diseño correspondientes a los contenidos trabajados en la materia. Cada patrón se aplica a una responsabilidad concreta del sistema.

| Patrón / concepto | Implementación | Aplicación en el sistema |
|---|---|---|
| MVC | `modelo/`, `web/`, `WebServer` | Separación de modelo, presentación y coordinación HTTP |
| Strategy | `PrecioStrategy`, `PrecioBaseStrategy`, `Recomendador`, `RecomendadorAnime` | Algoritmos de precio y recomendación intercambiables |
| Adapter | `RecomendadorAdapter`, `RecomendadorExterno` | Adaptación de un proveedor externo al contrato interno |
| Composite | `ElementoCartelera`, `PeliculaHoja`, `CarteleraComposite` | Cartelera jerárquica tratada uniformemente |
| Decorator | `PrecioDecorator`, `DescuentoTarifaDecorator`, `DescuentoAnticipadoDecorator` | Composición de descuentos y reglas de precio |
| Facade | `AnimeFestivalFacade` | Acceso simplificado a operaciones del festival |
| State | `EstadoFuncion`, `FuncionDisponible`, `FuncionAgotada`, `FuncionFinalizada` | Comportamiento de una función según su estado |
| Observer | `ReservaEventBus`, `ReservaObserver`, `ReservaLogger`, `EventoReserva` | Notificación desacoplada de eventos de reserva |
| Singleton | `SesionManager` | Gestión centralizada de sesión |
| Factory | `EntradaFactory`, `AbonoFactory` | Creación centralizada de entradas y abonos |
| DAO | `PeliculaDAO`, `PeliculaDAOArchivo`, `PeliculaDAOMemoria` | Abstracción del acceso a datos |
| Repository | `PeliculaRepository`, `CuentaRepository` | Acceso de alto nivel a entidades/agregados |
| Table Data Gateway | `PeliculaTableDataGateway` | Encapsulación de operaciones sobre datos de películas |
| Service Layer | `FestivalServicio`, `PeliculaServicio`, `EntradaServicio`, `CuentaServicio` | Casos de uso y reglas de aplicación |
| GRASP | Distribuido entre dominio, servicios, patrones y persistencia | Asignación de responsabilidades y reducción del acoplamiento |
| SOLID | Aplicado transversalmente | Cohesión, extensibilidad, contratos y dependencias sobre abstracciones |

Para una explicación detallada de cada patrón, sus participantes y sus relaciones, consultar `PATRONES_ADOO.md`.

### Relaciones principales

**Precio:** `PrecioStrategy` define el cálculo y los Decorator agregan descuentos sin modificar la estrategia base.

**Recomendación:** `Recomendador` permite usar `RecomendadorAnime` o adaptar un proveedor externo mediante `RecomendadorAdapter`.

**Persistencia:** `PeliculaRepository` utiliza la abstracción `PeliculaDAO`, que puede resolverse mediante memoria o archivo y relacionarse con `PeliculaTableDataGateway`.

**Estado:** `Funcion` delega operaciones dependientes del estado a `EstadoFuncion` y sus implementaciones concretas.

**Eventos:** una reserva puede generar `EventoReserva`, publicado por `ReservaEventBus` y recibido por implementaciones de `ReservaObserver`, como `ReservaLogger`.

**Creación:** `EntradaFactory` y `AbonoFactory` centralizan la construcción de objetos y reducen la dependencia de los clientes respecto de las clases concretas.

## Recomendador de anime

La sección **IA · Recomendador Anime** utiliza una estrategia local de ranking basada en la información disponible en el catálogo y las puntuaciones del jurado.

El recomendador permite presentar sugerencias sin depender necesariamente de un servicio externo.

Esta implementación debe entenderse como un mecanismo de recomendación basado en reglas y puntuaciones; no implica por sí misma el entrenamiento o la ejecución de un modelo de aprendizaje automático.

La separación mediante Strategy permite reemplazar o ampliar el algoritmo de recomendación sin tener que modificar toda la interfaz web.

## Datos de demostración

El catálogo inicial contempla las siguientes películas:

- Your Name.
- Weathering With You.
- Suzume.
- Spirited Away.
- A Silent Voice.
- Perfect Blue.
- Akira.
- Demon Slayer: Mugen Train.

Las funciones de demostración se distribuyen en ciudades de distintos países, entre ellos:

- Japón.
- España.
- Francia.
- Argentina.
- Corea del Sur.
- Estados Unidos.
- Reino Unido.
- Chile.

La cantidad de registros, las fechas y la disponibilidad efectiva dependen de la configuración de inicialización del proyecto.

## Estructura del proyecto

La organización general del repositorio es la siguiente:

```text
src/
├── app/
│   └── Main.java
├── modelo/
├── servicios/
├── persistencia/
├── excepciones/
├── seguridad/
├── ia/
├── patrones/
└── web/
    ├── WebServer.java
    ├── index.html
    ├── styles.css
    ├── app.js
    └── assets/
        ├── posters/
        └── video/
            ├── entrada.mp4
            └── salida.mp4
```

Esta estructura es ilustrativa y debe coincidir con los directorios y archivos que efectivamente se encuentran en el repositorio.

## Persistencia y decisiones de diseño

El proyecto conserva los mecanismos de persistencia y las entidades de la implementación original siempre que corresponda.

Las responsabilidades de almacenamiento se mantienen separadas de las operaciones de negocio y de la interfaz web.

### Datos persistidos

Los archivos y mecanismos concretos dependen de la implementación existente en el paquete `persistencia/`.

Antes de entregar, conviene verificar qué entidades se guardan efectivamente, cuáles se inicializan automáticamente y cuáles se conservan solamente durante la ejecución actual.

### Butacas

Las butacas pueden representarse mediante números enteros comprendidos entre 1 y la capacidad de la sala, evitando crear objetos independientes cuando no resulta necesario para el alcance del proyecto.

### Ganadores

Las evaluaciones permiten calcular promedios y determinar ganadores por sección según las reglas definidas en los servicios del dominio.

### Validaciones

Las reglas de negocio deben centralizarse en los servicios, incluyendo las validaciones de disponibilidad, capacidad, fechas y operaciones que puedan producir inconsistencias.

### Manejo de errores

El paquete `excepciones/` permite organizar errores específicos del dominio y de la aplicación, manteniendo el tratamiento de errores separado de la presentación.

## Nota sobre el TP original

El proyecto conserva las entidades, los servicios, los mecanismos de persistencia y los componentes de la aplicación original que siguen siendo compatibles con la entrega web.

Los paneles Swing y las clases anteriores pueden permanecer en el repositorio como parte del trabajo previo, pero la clase `app.Main` utiliza la aplicación web como interfaz principal de esta versión.

La conservación de las clases originales permite mantener la evolución del trabajo práctico sin tener que eliminar todo el código desarrollado en etapas anteriores.

## Videos del festival

### Entrada

[Ver recurso de introducción](https://github.com/user-attachments/assets/8b33e4b1-c7a2-4954-807a-2b5c3d9e5fa4)

### Salida

[Ver recurso de cierre](https://github.com/user-attachments/assets/8b5fd872-92ba-4076-999f-3ded585ec7d1)

## Waifu del repositorio-Kurumi Tokisaki

<img width="735" height="441" alt="Kurumi" src="https://github.com/user-attachments/assets/d4be520c-0176-4214-a6d1-e75e5e72e64e" />

https://github.com/user-attachments/assets/bae990d2-32e5-46b0-a16e-62190382dbfc

## Películas y recursos visuales

Las siguientes imágenes se incluyen como referencias visuales de las películas y del catálogo de anime.

![Madoka Magica: Walpurgisnacht Rising](https://github.com/user-attachments/assets/18fbe98c-d974-4a0a-9169-65dc26901b1f)

![Referencia visual de anime](https://github.com/user-attachments/assets/fe6322da-475b-411b-9551-9f83e77e2664)

![Seishun Buta Yarou wa Yumemiru Shoujo no Yume wo Minai](https://github.com/user-attachments/assets/702f7e83-19f5-4b40-9b6c-dc5243ca6d81)

![Referencia visual de anime](https://github.com/user-attachments/assets/efc2f46b-4308-4653-bea7-a00f85c3fa35)

![Pretty Guardian Sailor Moon Cosmos](https://github.com/user-attachments/assets/ea7fe6b8-f147-4b26-9fcc-9bd2fd6618e3)

## Trailers de peliculas

https://github.com/user-attachments/assets/a01ef482-6b27-49d5-ac08-6e7535675ad5

https://github.com/user-attachments/assets/9970fb0a-447c-4d37-9a72-7f52bb172c4d

https://github.com/user-attachments/assets/17216215-d7f3-4ca5-a7dd-d1a3c2c14f0a

https://github.com/user-attachments/assets/e8aca9d7-8602-4e54-a45a-0909cc34e1f9

https://github.com/user-attachments/assets/9d1c68c1-12b9-4f0c-b00b-f6b52cd9f5ec

https://github.com/user-attachments/assets/7fc29e3c-82f6-4932-8142-085056a917be

## Diagramas de clases

<img width="1536" height="1024" alt="Diagrama UML del Sistema de Películas" src="https://github.com/user-attachments/assets/ab835597-38fd-49a2-bdb7-552a7d6582df" />

<img width="1536" height="1024" alt="Diagrama UML del Festival de Anime" src="https://github.com/user-attachments/assets/3a0bfd77-83ce-4325-9b05-15bf4d95dc99" />

**Anime Festival Cinema — Proyecto Final de PROCESO DE DESARROLLO DE SOFTWARE en Java.**

## Mejoras incorporadas en la versión final

La aplicación web mantiene su interfaz de Anime Festival Cinema y suma validaciones de dominio para fechas, duración y superposición de funciones, pertenencia a ediciones, evaluaciones duplicadas, venta de funciones finalizadas, capacidad y formato de email.

También incorpora persistencia completa del estado del festival mediante `PersistenciaFestivalCompleta` y persistencia de cuentas mediante `CuentaRepositoryArchivo`. El estado se restaura automáticamente al iniciar y se guarda al cerrar el servidor, sin modificar el panel web.

Las pruebas de reglas de negocio se encuentran en `tests/ReglasNegocioTest.java`.
