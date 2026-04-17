ToDo Pro
Una aplicación web moderna y rápida para gestionar tus tareas del día a día. Cuenta con registro seguro, diseño adaptable a móviles y la posibilidad de ordenar tus tareas arrastrándolas con el ratón o el dedo.

Funcionalidades
Crear una cuenta de usuario de forma segura.

Añadir, editar y eliminar tareas.

Marcar tareas como completadas.

Drag & Drop: Ordenar tareas arrastrándolas.

Diseño moderno y 100% compatible con móviles.

¿Qué necesitas para ejecutarlo?
Java 17 o superior.

MySQL (Puedes usar XAMPP, WAMP o similar).

Un editor de código (IntelliJ IDEA, Eclipse o VS Code).

Pasos para arrancar el proyecto
1. Preparar la Base de Datos
No tienes que escribir código SQL, el programa crea las tablas solo. Solo haz lo siguiente:

Abre tu phpMyAdmin (normalmente en http://localhost/phpmyadmin).

Crea una base de datos nueva llamada exactamente: todo_app_db.

2. Configurar tus contraseñas
Abre el proyecto en tu editor y busca el archivo src/main/resources/application.properties. Revisa que tu usuario y contraseña de MySQL sean correctos:

spring.datasource.url=jdbc:mysql://localhost:3306/todo_app_db?useSSL=false&serverTimezone=UTC

spring.datasource.username=root    <-- Pon aquí tu usuario (por defecto suele ser root)

spring.datasource.password=        <-- Pon aquí tu contraseña (por defecto suele estar vacío en XAMPP)


Busca el archivo principal llamado TodoAppApplication.java, haz clic derecho sobre él y dale a Run (Ejecutar).

Cuando la consola termine de cargar, abre tu navegador y entra en:
--> http://localhost:8080
