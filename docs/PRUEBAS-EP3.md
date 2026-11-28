# Verificación y entrega EP3

## Pruebas automáticas

Ejecutar `.\mvnw.cmd test` con Java 17. Las bases de pruebas son H2 aisladas; no usan Supabase. Cubren autorización, JWT revocado, CRUD de clientes, persistencia de campos nuevos sobre el esquema anterior, validación y CRUD de productos, importación sin sobrescritura, contacto y CORS.

En React ejecutar `npm run lint`, `npm test` y `npm run build`. Las pruebas de interfaz usan respuestas HTTP controladas; la colección Postman y el recorrido manual siguiente comprueban el servicio publicado.

## Colección Postman

Importar `Ferreteria_EP3.postman_collection.json`. En Variables completar `adminPassword` solo en el valor local; no guardar secretos en el repositorio ni exportarlos con la entrega. `baseUrl` apunta al backend de Render y puede cambiarse por localhost:8081.

Ejecutar la colección en orden. El primer request genera correo, RUT válido y código de producto para esta ejecución. Los login guardan los tokens. Al finalizar se elimina el producto y se da de baja al cliente temporal. El mensaje de contacto de prueba se conserva.

## Recorrido manual integrado

1. Abrir Vercel y consultar Catálogo sin iniciar sesión. Debe cargar los productos de la API.
2. Ingresar como ADMIN, abrir Productos y usar Cargar catálogo inicial. Se importan los 85 productos del proyecto original y se conservan los códigos existentes.
3. Crear un producto con código nuevo, verificarlo en Catálogo, editar precio y stock, comprobar los cambios y eliminarlo.
4. Registrar un cliente nuevo. Debe recibir rol USER, cupo de 100000 y estado activo.
5. Ingresar con el cliente, recargar y comprobar que conserva sesión. La ruta de administración debe estar restringida.
6. En ADMIN, consultar el mismo cliente, editar su cupo y datos, y desactivarlo. Las solicitudes con su token anterior deben responder 401. React cerrará la sesión al comprobar el perfil o recibir un 401 autenticado.
7. Reactivar y volver a ingresar. El token anterior debe seguir rechazado.
8. Enviar un mensaje desde Nosotros/Contáctanos sin sesión y consultarlo en ADMIN/Mensajes. USER no puede consultar la lista.
9. Comprobar que productos/clientes/mensajes se conservan después de reiniciar el backend.
10. Abrir Swagger, ingresar, usar Authorize y consultar rutas protegidas.

## Alcance que se debe explicar

La autenticación, clientes, RUT, cupos, catálogo y contacto usan Spring Boot y PostgreSQL. Las compras, reservas, pago simulado y abonos siguen siendo simulados en el navegador. No se debe presentar su deuda o inventario reservado como una contabilidad global; esa integración requiere endpoints de operaciones comerciales y está fuera de los CRUD cerrados aquí.

## Entregables

- Repositorio público frontend: https://github.com/hleonCLduoc/Ferreteria_Los_Maestros
- Repositorio público backend: https://github.com/hleonCLduoc/backend-springboot
- Frontend publicado: https://ferreterialosmaestros.vercel.app
- Swagger publicado: https://ferreteria-backend-wlil.onrender.com/swagger-ui/index.html
- ZIP de fuentes de cada proyecto, sin node_modules, target, .git, .idea ni archivos de entorno privados.
- Colección Postman y evidencia del recorrido real. La existencia del archivo no equivale a una ejecución aprobada; registrar los resultados al ejecutarla.
