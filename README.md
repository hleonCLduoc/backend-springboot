# Ferretería Los Maestros · API

Backend Java 17 y Spring Boot con API REST, JPA, PostgreSQL, Spring Security, JWT y Swagger.

## Configuración

Variables de entorno: `DB_URL`, `DB_USER`, `DB_PASSWORD` y `JWT_SECRET`. El perfil `dev` usa además `ADMIN_PASSWORD` para crear el administrador inicial si no existe. El puerto se toma de `PORT` y por defecto es 8081. No guardar credenciales en Git.

Swagger: `/swagger-ui/index.html`.

## Cuentas

- `POST /api/v1/auth/registrar`: registro público con nombre, apellido, email, RUT y password. El servidor asigna USER, cuenta activa y $100.000 de cupo; ignora cualquier intento de asignar rol o cupo en este registro.
- `POST /api/v1/auth/iniciar-sesion`: devuelve usuario y JWT.
- `GET /api/v1/usuarios/mi-cuenta`: perfil actualizado de la sesión autenticada.
- `GET /api/v1/usuarios` y `GET /api/v1/usuarios/{id}`: consulta administrativa.
- `POST /api/v1/usuarios`: ADMIN crea un cliente con nombre, apellido, email, RUT, password, lineaCredito y activo.
- `PUT /api/v1/usuarios/{id}`: ADMIN edita los mismos datos; password es opcional. El rol no se modifica por esta vía.
- `DELETE /api/v1/usuarios/{id}`: baja lógica. Se conserva la fila para no destruir referencias e historial. Las cuentas eliminadas no se listan ni pueden autenticarse.

RUT normalizado y validado por dígito verificador, correo/RUT únicos y contraseña BCrypt. Las cuentas ADMIN no se pueden editar ni eliminar mediante el CRUD de clientes. Desactivar, eliminar o cambiar correo/contraseña revoca los tokens anteriores; reactivar una cuenta exige un nuevo login.

El cupo no puede ser negativo, superar $100.000.000 ni ser menor al saldo registrado en el servidor. No se permite eliminar una cuenta con saldo pendiente. Los movimientos de compra del frontend aún son simulados y no alimentan el saldo del servidor: falta esa integración para una cuenta corriente compartida entre navegadores.

## Despliegue

El Dockerfile construye y ejecuta el JAR. Render recibe las variables de entorno. Al iniciar con `spring.jpa.hibernate.ddl-auto=update`, Hibernate añade los campos nuevos a usuarios. Los registros existentes reciben cupo 100000, saldo 0, activo true, eliminado false y versión de sesión 0. Los RUT anteriores quedan vacíos hasta que se completen; no se inventan identificadores.

Publicar primero el backend y comprobar `/v3/api-docs`; después publicar el frontend conectado a la API. Los tokens antiguos sin identificador y versión de sesión se rechazan: hay que iniciar sesión de nuevo.

## Pruebas

```powershell
.\mvnw.cmd test
```

Las pruebas usan bases H2 aisladas, sin credenciales ni conexión a Supabase. Cubren registro sin elevación de privilegios, duplicados y validación de RUT, permisos, edición sin cambiar contraseña, cupos, bloqueo/reactivación, revocación de JWT, baja lógica y actualización del esquema anterior preservando datos.

## Catálogo y contacto

El catálogo GET es público. POST, PUT, DELETE e importación de productos requieren ADMIN. Los precios y stock deben ser enteros no negativos. `POST /api/v1/productos/importar` recibe una lista de hasta 500 artículos y agrega solo códigos que no existan. Marca y subcategoría preservan los datos del catálogo original.

El contacto se envía sin sesión con POST /api/v1/mensajes; solo ADMIN puede consultar sus mensajes con GET.

En docs están la colección Postman, el recorrido de verificación y la guía de presentación EP3. Las pruebas de Java comprueban además catálogo público, CRUD y validación de productos, importación sin sobrescritura, permisos de contacto y preflight CORS de Vercel.
