# ToDo Pro

Una aplicación web moderna y rápida para gestionar tus tareas del día a día. Construida con Spring Boot, cuenta con un sistema de autenticación seguro, diseño adaptable a móviles y una interfaz refinada orientada a la productividad profesional.

## Funcionalidades de Usuario
* **Autenticación Dual:** Inicio de sesión mediante correo electrónico o nombre de usuario.
* **Seguridad Avanzada:** Contraseñas encriptadas y sistema de recuperación vía email con tokens temporales y plantillas HTML profesionales.
* **Gestión de Tareas:** Añadir, editar (incluyendo descripción y categorías), eliminar y marcar tareas como completadas.
* **Categorización y Fechas Límite:** Organización por etiquetas (Trabajo, Clase, Ocio, Hogar, Importante) y asignación de fechas de vencimiento.
* **Alertas Visuales:** Indicadores de estado para tareas retrasadas o próximas a vencer.
* **Filtrado y Búsqueda:** Buscador en tiempo real y filtros avanzados por estado, categoría y orden cronológico.
* **Ordenación Dinámica:** Sistema Drag & Drop para organizar tareas manualmente mediante arrastre.
* **Gestión de Perfil:** Panel de ajustes para actualizar el nombre de usuario, cambiar la contraseña o eliminar la cuenta de forma permanente.
* **Exportación de Datos:** Descarga de la lista de tareas en formato CSV compatible con Excel.
* **Modo Oscuro:** Soporte nativo para tema claro y oscuro con persistencia de preferencia en el navegador.

## Funcionalidades de Administración (RBAC)
* **Panel de Control:** Acceso exclusivo para usuarios con rol ADMIN.
* **Estadísticas en Tiempo Real:** Visualización del total de usuarios registrados y tareas creadas durante el día actual.
* **Gestión de Usuarios:** Listado paginado de todos los usuarios del sistema y supervisión de sus respectivas tareas.

## Requisitos del Sistema
* **Java 17** o superior.
* **MySQL** (XAMPP, WAMP o servidor dedicado).
* **Maven** (incluido en el wrapper del proyecto).

---

## Instrucciones de Configuración

### 1. Base de Datos
La aplicación utiliza Hibernate para la creación automática del esquema.
* Acceda a su gestor de base de datos (ej. phpMyAdmin).
* Cree una base de datos nueva denominada: `todo_app_db`.

### 2. Configuración de Credenciales
Localice el archivo `src/main/resources/application.properties` y ajuste los parámetros de conexión:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/todo_app_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=

# Configuración de Servidor de Correo (Ejemplo Gmail)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=su_correo@gmail.com
spring.mail.password=su_contraseña_de_aplicacion
app.mail.from=noreply@todopro.com
```

### 3. Ejecución
* Ejecute la clase principal `Application.java` desde su IDE o mediante terminal usando `./mvnw spring-boot:run`.
* Una vez iniciado el servidor, acceda a: `http://localhost:8080`

---

## Activación del Rol de Administrador

Por defecto, los nuevos registros obtienen el rol `USER`. Para habilitar las funciones de administración:

1. Regístrese como un usuario convencional.
2. Acceda a la tabla `users` en su base de datos.
3. Localice su registro y cambie el valor de la columna `role` a `ADMIN` (en mayúsculas).
4. Reinicie su sesión en la aplicación para aplicar los cambios.
5. Aparecerá un acceso directo al "Panel de Administración" en su menú principal.