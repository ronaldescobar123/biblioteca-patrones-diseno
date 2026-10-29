# Sistema de Prestamos de Biblioteca con Patrones de Diseño

Materia: Arquitectura de Software II-2026

Autor: Ronald Escobar Vargas

## Descripcion

Sistema sencillo de prestamos de libros de una biblioteca, hecho en Java y
ejecutado por consola. Presta libros a estudiantes y docentes, avisa de cada
operacion y muestra un reporte de los prestamos activos.

Usa tres patrones de diseño: Builder (creacional), Decorator (estructural) y
Observer (comportamiento).

## Patrones usados

- Builder (creacional): la clase LibroBuilder crea objetos Libro paso a paso,
  sin usar un constructor largo ni valores null.
- Decorator (estructural): la clase PrestamoConExtension envuelve a un Prestamo
  y le suma dias de plazo sin crear una subclase nueva.
- Observer (comportamiento): la clase Biblioteca avisa a una lista de
  Notificador cada vez que hay un prestamo o una devolucion.

## Estructura del codigo

Todo esta en el archivo Main.java:

- Libro y LibroBuilder
- Prestamo, PrestamoEstudiante, PrestamoDocente y PrestamoConExtension
- Notificador, NotificadorConsola y NotificadorContador
- Reporte y ReporteConsola
- Biblioteca
- Main (clase de prueba con datos fijos)

## Requisitos

Tener instalado el JDK de Java (version 11 o superior).

## Como ejecutar

Desde la carpeta del proyecto:

    javac Main.java
    java Main

## Resultado esperado

El programa muestra los libros creados, los avisos de cada prestamo y
devolucion, el reporte de prestamos activos y el total de avisos enviados.
