# Guía de presentación EP3

## 1. Backend y lógica del negocio

Mostrar models, repositories, services y controllers. Una entidad representa datos, JpaRepository accede a PostgreSQL, el servicio aplica reglas y el controlador recibe HTTP. Ejemplos: RUT válido/único, cupo inicial impuesto por el servidor, cupo no inferior al saldo registrado y cuenta desactivada sin acceso.

## 2. API REST y Swagger

Explicar GET, POST, PUT y DELETE con productos. Usamos /api/v1 para versionar. Mostrar 201 al crear, 204 al eliminar, 400 para datos inválidos, 404 para inexistentes y 409 para duplicados. Abrir Swagger y Authorize.

## 3. Integración

React llama mediante fetch a la API en Render; Spring Boot consulta PostgreSQL en Supabase mediante JPA/JDBC. Mostrar services/api.js y ContextoProductos. El backend devuelve precio numérico; React lo presenta como moneda y conserva el mismo diseño. Las credenciales de PostgreSQL no se envían al navegador.

## 4. JWT y roles

El login verifica BCrypt y devuelve un token firmado con usuario, identificador, versión y vencimiento. El frontend lo envía como Authorization: Bearer. JwtAuthenticationFilter valida firma/expiración y carga el usuario y rol de la base de datos. USER consulta; ADMIN modifica. No autenticar produce 401; no tener permisos produce 403. Desactivar o cambiar credenciales revoca las sesiones anteriores.

## 5. Sesión de React

Mantener sesión usa localStorage; sin esa opción se usa sessionStorage. La recarga restaura los datos y consulta mi-cuenta. El token vence, se elimina al salir y un 401 autenticado cierra sesión. Decodificar el JWT para comprobar la fecha en React no sustituye su validación por el backend.

## 6. Restricciones de interfaz

Mi cuenta muestra el panel según el rol. Crear, editar y eliminar clientes/productos y consultar mensajes pertenecen a ADMIN. Esconder botones no basta: SecurityConfig aplica las mismas restricciones aunque se use Postman. CORS permite el origen de Vercel/local; no reemplaza JWT ni permisos.

## Demostración

Abrir Render antes de presentar para esperar su arranque. Mostrar catálogo API, registro/login de USER, F5, bloqueo de administración, login ADMIN, cambios visibles en catálogo/clientes y un mensaje de contacto. Tener lista la colección Postman. Explicar honestamente qué operaciones comerciales siguen simuladas.
