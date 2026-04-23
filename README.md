# ToDo Pro

Una aplicación web moderna y rápida para gestionar tus tareas del día a día. Construida con Spring Boot, cuenta con un sistema de autenticación seguro, diseño adaptable a móviles y la posibilidad de ordenar tus tareas arrastrándolas con el ratón o el dedo.

## Funcionalidades
* **Autenticación Dual:** Inicia sesión cómodamente usando tu correo electrónico o tu nombre de usuario.
* **Seguridad Avanzada:** Contraseñas encriptadas y sistema de recuperación de contraseña vía email con tokens temporales.
* **Gestión de Tareas:** Añade, edita, elimina y marca tareas como completadas.
* **Drag & Drop:** Ordena tus tareas arrastrándolas visualmente de forma intuitiva.
* **Panel de Administración (RBAC):** Acceso exclusivo para administradores para supervisar y gestionar las tareas de todos los usuarios registrados.
* **Diseño Responsivo:** Interfaz moderna (UI/UX) 100% compatible con dispositivos móviles.

## ¿Qué necesitas para ejecutarlo?
* **Java 17** o superior.
* **MySQL** (Puedes usar XAMPP, WAMP o similar).
* Un editor de código (IntelliJ IDEA, Eclipse o VS Code).

---

## Pasos para arrancar el proyecto

### 1. Preparar la Base de Datos
No tienes que escribir código SQL, el programa crea las tablas por ti gracias a Hibernate. Solo debes hacer lo siguiente:
* Abre tu gestor de base de datos o phpMyAdmin (normalmente en `http://localhost/phpmyadmin`).
* Crea una base de datos nueva llamada exactamente: `todo_app_db`.

### 2. Configurar tus credenciales
Abre el proyecto en tu editor y busca el archivo `src/main/resources/application.properties`. Revisa que tu usuario y contraseña de MySQL sean correctos:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/todo_app_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=root    <-- Pon aquí tu usuario (por defecto suele ser root)
spring.datasource.password=        <-- Pon aquí tu contraseña (por defecto suele estar vacío en XAMPP)
```
*(Nota: Para que funcione la recuperación de contraseña, asegúrate de configurar también las variables de correo de Gmail en este archivo).*

### 3. Ejecutar la aplicación
* Busca el archivo principal llamado `TodoAppApplication.java`.
* Haz clic derecho sobre él y selecciona **Run**.
* Cuando la consola indique que el servidor ha arrancado, abre tu navegador web y visita: `http://localhost:8080`

---

## Cómo activar el Modo Administrador

Por defecto, todos los usuarios que se registran en la aplicación tienen el rol básico (`USER`). Para poder acceder al panel de gestión y supervisar las tareas, debes ascender una cuenta de forma manual:

1. Inicia la aplicación y **regístrate** creando una cuenta normal a través del formulario web.
2. Abre tu gestor de base de datos (ej. phpMyAdmin).
3. Entra en la tabla `user` y busca la fila correspondiente al usuario que acabas de crear.
4. Edita el valor de la columna `role` y cámbialo de `USER` a **`ADMIN`** (es importante que sea todo en mayúsculas).
5. Vuelve a la aplicación web, cierra sesión si estabas logueado, y **vuelve a iniciar sesión** con esa cuenta.
6. ¡Listo! Verás una alerta destacada en tu pantalla principal con el botón para acceder al panel de gestión de usuarios.