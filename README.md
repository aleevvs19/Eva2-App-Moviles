# MPAZ PRO 1.º — EVA 2

## Descripción del proyecto

Este proyecto corresponde a la Evaluación 2 de Desarrollo de Aplicaciones Móviles

Desarrollamos una aplicación en Android Studio llamada **MPAZ PRO 1.º**, basada en el caso de la Escuela Escritora Marcela Paz. Su objetivo es apoyar el aprendizaje de los estudiantes de primero básico mediante actividades educativas

La aplicación cuenta con tres perfiles: Estudiante, Docente y Jefatura UTP, cada uno con diferentes funciones

## Tecnologías utilizadas

Para desarrollar la aplicación utilizamos:

- **Kotlin:** para programar la aplicación
- **Jetpack Compose:** para crear las pantallas
- **Material Design 3:** para los botones, formularios y otros componentes
- **NavController:** para navegar entre las pantallas
- **SQLite con Room:** para registrar usuarios e iniciar sesión sin internet
- **ViewModel:** para manejar la información y las funciones de la aplicación
- **SharedPreferences:** para guardar información local de prueba

## Funcionalidades de la aplicación

### Estudiante

El estudiante puede ingresar a las cuatro asignaturas: Lenguaje, Matemática, Ciencias Naturales e Historia

Dentro de ellas puede realizar actividades con preguntas, seleccionar respuestas y revisar sus resultados. También puede consultar su progreso y acceder a una prueba final de demostración

### Docente

El docente puede seleccionar asignaturas, configurar fechas de disponibilidad y establecer la cantidad máxima de intentos

También cuenta con una pantalla de informe pedagógico, donde puede ingresar observaciones de demostración

### Jefatura UTP

UTP puede crear y editar contenidos educativos, enviarlos a revisión, aprobarlos, rechazarlos y publicarlos

Además, cuenta con una bitácora que muestra los cambios realizados

## Inicio de sesión y registro

Implementamos un sistema de registro e inicio de sesión utilizando **SQLite mediante Room**

Los estudiantes pueden crear una cuenta con su nombre, correo y contraseña. Los usuarios quedan guardados localmente, por lo que pueden iniciar sesión aunque no tengan conexión a internet

También tenemos usuarios de prueba para los tres perfiles

## Navegación y formularios

Utilizamos **NavController** para conectar las diferentes pantallas y permitir que el usuario navegue por la aplicación

Los formularios de Docente y UTP permiten ingresar información y muestran el mensaje **«Para una futura actualización»**,

Por ahora, estas funciones trabajan con datos locales de demostración y no están conectadas a una base de datos académica externa

## Requisitos de la segunda entrega

Para esta evaluación trabajamos principalmente en:

1. Implementar las pantallas correspondientes a los tres perfiles
2. Permitir la navegación entre las diferentes pantallas
3. Incorporar los formularios necesarios y el mensaje solicitado
4. Implementar registro e inicio de sesión con SQLite
5. Preparar un video de máximo 90 segundos mostrando el funcionamiento de la aplicación

## Cómo ejecutar el proyecto

1. Descargar o clonar el repositorio
2. Abrir el proyecto `Evaluacion2` en Android Studio
3. Esperar que Gradle termine de sincronizar
4. Iniciar el emulador
5. Ejecutar la aplicación
6. Seleccionar uno de los tres perfiles

## Usuarios de prueba

| Perfil | Correo | Contraseña |
|---|---|---|
| Estudiante | estudiante01@mpazpro.test | 1234 |
| Docente | docente01@mpazpro.test | 1234 |
| UTP | utp01@mpazpro.test | 1234 |

## Estado del proyecto

Para esta segunda entrega tenemos implementadas las pantallas de los tres perfiles, la navegación, los formularios y el registro e inicio de sesión con SQLite

También agregamos actividades interactivas, resultados, progreso y funciones de administración de contenidos

Algunas funciones, como las pruebas finales y los informes pedagógicos, todavía están en modo demostración y se podrán completar en futuras actualizaciones

---

**Desarrollo de Aplicaciones Móviles — Duoc UC**

**Proyecto: MPAZ PRO 1.º — Evaluación 2**
