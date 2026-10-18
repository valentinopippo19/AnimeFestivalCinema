# Pruebas de reglas de negocio

La entrega incluye `tests/ReglasNegocioTest.java`, una suite ejecutable sin librerías externas.

## Ejecutar

```text
javac -d build $(find src -name '*.java')
javac -cp build -d build tests/ReglasNegocioTest.java
java -cp build ReglasNegocioTest
```

En Windows PowerShell, una alternativa es compilar los `.java` del proyecto con IntelliJ y luego ejecutar la clase `ReglasNegocioTest` desde el IDE.

La suite cubre:

- validación de email;
- evaluación duplicada de un jurado;
- superposición de funciones considerando duración;
- función fuera del período de la edición;
- venta de una función ya finalizada;
- doble venta de una misma butaca.
