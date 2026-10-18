# Cobertura de ADOO y Patrones de Diseño — Anime Festival Cinema

## Objetivo

La entrega incorpora los contenidos de la materia en el código del sistema, relacionando cada tema con una responsabilidad concreta.

## Cobertura

| Unidad / contenido | Evidencia en el proyecto |
|---|---|
| Introducción a ADOO / POO | `src/modelo/` con encapsulamiento, herencia y polimorfismo |
| Modelo de dominio | `Festival`, `Edicion`, `Seccion`, `Pelicula`, `Funcion`, `Entrada`, `Abono`, `Espectador`, etc. |
| Casos de uso / realización | `FestivalServicio`, `PeliculaServicio`, `EntradaServicio`, `CuentaServicio` |
| GRASP | Controller, Information Expert, Creator, Polymorphism, Low Coupling, High Cohesion, Indirection, Pure Fabrication, Protected Variations |
| SOLID / code smells | separación de servicios, abstracciones, Strategy, Decorator, Factory, DAO y Repository |
| MVC | `web/` como View/Controller, `modelo/` como Model, `servicios/` como capa de aplicación |
| Strategy | `PrecioStrategy` y `Recomendador` |
| Adapter | `RecomendadorAdapter` |
| Composite | `ElementoCartelera`, `PeliculaHoja`, `CarteleraComposite` |
| Decorator | `PrecioDecorator`, `DescuentoTarifaDecorator`, `DescuentoAnticipadoDecorator` |
| Facade | `AnimeFestivalFacade` |
| State | `EstadoFuncion`, `FuncionDisponible`, `FuncionAgotada`, `FuncionFinalizada` |
| Observer | `ReservaEventBus`, `ReservaObserver`, `ReservaLogger`, `EventoReserva` |
| Singleton | `SesionManager` |
| Factory | `EntradaFactory`, `AbonoFactory` |
| DAO | `PeliculaDAO`, `PeliculaDAOArchivo`, `PeliculaDAOMemoria` |
| Repository | `PeliculaRepository`, `CuentaRepository` |
| Table Data Gateway | `PeliculaTableDataGateway` |
| Variantes de acceso a datos | memoria vs. archivo |
| IA aplicada a OO | `RecomendadorAnime` + Strategy + Adapter |

## Relación entre patrones

### Precio de entradas

```text
EntradaServicio
    |
    v
PrecioStrategy
    |
    +--> PrecioBaseStrategy
    |
    +--> DescuentoTarifaDecorator
             |
             +--> DescuentoAnticipadoDecorator
```

Esto permite agregar nuevas reglas comerciales sin modificar el algoritmo base.

### Recomendaciones

```text
WebServer
   |
   v
Recomendador
   |
   +--> RecomendadorAnime
   |
   +--> RecomendadorAdapter --> RecomendadorExterno
```

El controlador depende de la abstracción y el Adapter permite integrar una API incompatible.

### Persistencia

```text
PeliculaRepository
       |
       v
    PeliculaDAO
       |
       +--> PeliculaDAOMemoria
       |
       +--> PeliculaDAOArchivo
                    |
                    v
        PeliculaTableDataGateway
                    |
                    v
                 archivo
```

La variante en memoria facilita pruebas; la variante de archivo permite persistencia física.

### Estado de una función

```text
                 +-------------------+
                 |    Funcion        |
                 +-------------------+
                         |
                         v
                  EstadoFuncion
                  /     |      \
                 /      |       \
       Disponible    Agotada   Finalizada
```

El cliente pregunta `funcion.puedeReservar()` y no necesita conocer qué estado concreto está activo.

### Cartelera como Composite

```text
CarteleraComposite: Edición
        |
        +--- CarteleraComposite: Sección
        |          |
        |          +--- PeliculaHoja
        |          +--- PeliculaHoja
        |
        +--- CarteleraComposite: Sección
                   |
                   +--- PeliculaHoja
```

## GRASP aplicado

- **Controller:** `WebServer` y los servicios coordinan casos de uso.
- **Information Expert:** las entidades calculan la información que poseen.
- **Creator:** las factories y agregados crean objetos relacionados.
- **Polymorphism:** funciones, entradas, abonos y estados implementan comportamiento específico.
- **Low Coupling:** se utilizan interfaces para evitar dependencias innecesarias.
- **High Cohesion:** cada paquete tiene una responsabilidad delimitada.
- **Indirection:** Repository, DAO, Facade y Adapter desacoplan componentes.
- **Pure Fabrication:** servicios y componentes de persistencia encapsulan responsabilidades no propias del dominio.
- **Protected Variations:** las interfaces protegen frente a cambios de algoritmos, almacenamiento y proveedores.

## SOLID aplicado

- **S:** una responsabilidad principal por clase/componente.
- **O:** nuevas estrategias, estados, descuentos y DAOs se incorporan sin modificar los clientes.
- **L:** las implementaciones respetan sus contratos.
- **I:** interfaces pequeñas y específicas.
- **D:** los consumidores dependen de abstracciones.

## MVC

- **Model:** `modelo/`.
- **View:** `src/web/index.html`, `styles.css`, `app.js`.
- **Controller:** `WebServer`.
- **Application layer:** `servicios/`.

## Validación

El proyecto compila con JDK 21 y se verificó el arranque del servidor y los endpoints `/api/movies` y `/api/festival`.

## Mejoras de reglas de negocio y robustez incorporadas

La versión actual agrega validaciones en el dominio para evitar inconsistencias que podían quedar ocultas detrás de la interfaz web:

- Una función no puede programarse en una fecha y hora ya pasadas.
- Una función debe pertenecer a una edición y su fecha debe estar dentro del período de esa edición.
- La superposición de funciones presenciales se calcula utilizando el intervalo completo de duración de la película, no solo la hora de inicio.
- Un jurado no puede evaluar dos veces la misma película.
- Una entrada no puede venderse/reservarse si la función ya finalizó.
- Las salas validan capacidad y las personas validan formato básico de email.
- Se mantienen las validaciones de capacidad y de butaca ocupada.

## Persistencia completa del estado del festival

Además de los DAO de películas, la aplicación incorpora `PersistenciaFestivalCompleta`, que guarda un snapshot del dominio en `anime-festival.data` al cerrar la aplicación y lo restaura al iniciar.

El snapshot conserva:

- festival y ediciones;
- secciones y premios;
- películas y reparto;
- directores, actores y jurados;
- evaluaciones y votos públicos;
- salas;
- funciones presenciales y streaming;
- espectadores;
- entradas;
- abonos y reservas;
- contador de códigos.

Las cuentas de usuario se conservan mediante `CuentaRepositoryArchivo` en `anime-festival-cuentas.data`; se almacena el hash SHA-256 de la contraseña y no la contraseña en texto plano.

La persistencia no modifica la interfaz web ni las rutas HTTP: únicamente permite continuar la sesión de datos del dominio después de reiniciar el servidor.

## Pruebas de reglas de negocio

`tests/ReglasNegocioTest.java` contiene seis pruebas ejecutables sin dependencias externas para verificar email inválido, evaluaciones duplicadas, superposición por duración, fechas fuera de edición, venta de funciones finalizadas y doble reserva de butaca.
