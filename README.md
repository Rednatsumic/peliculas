# Películas

UI cinematográfica con carrusel y backend mínimo (Spring Boot) para cuentas/planes.


## Demo en vivo

[![Ver el sitio](https://img.shields.io/badge/VER_SITIO-GitHub_Pages-red?style=for-the-badge)](https://rednatsumic.github.io/peliculas/)

Enlace directo: https://rednatsumic.github.io/peliculas/


## Notas
- Este enlace publica solo el frontend (GitHub Pages). Las rutas del backend (/login, /register, /app/**) requieren desplegar el servidor aparte.
- Si algún asset no cargara, refresca con Ctrl+F5; el workflow copia `src/main/resources/static/`.


## Ejecutar local
- Frontend: abre `src/main/resources/static/index.html` con Live Server.
- Backend: `./gradlew bootRun` (Java 17). App en http://localhost:8080# Películas - notas rápidas de desarrollo

Este README explica por qué los estilos podían no cargarse y cómo probar la interfaz rápidamente usando Live Server.

Por qué pudiera parecer "básico" o sin estilos:

- El HTML usa rutas absolutas como `/css/styles.css` y `/images/...`. Si el servidor está sirviendo desde otra ruta o el archivo no existe en `static/css`, el navegador no encontrará el CSS y aplicará estilos por defecto.
- Si el archivo estaba vacío o no contenía reglas para las clases usadas en el `index.html`, los elementos se ven con estilos por defecto del navegador.
- Caché del navegador puede servir una versión vieja.

Qué se hizo para arreglarlo:

- Se creó y pobló `src/main/resources/static/css/styles.css` con variables CSS, reglas base y estilos para las clases que usa `templates/index.html`.
- Se añadió `.vscode/settings.json` para configurar Live Server (root -> `src/main/resources/static`).
- Se copió `src/main/resources/templates/index.html` a `src/main/resources/static/index.html` para permitir abrir la página con Live Server sin tocar las plantillas usadas por Spring Boot.

Cómo ver la página con Live Server (rápido):

1. Abre la carpeta `peliculas` en VS Code.
2. Instala la extensión Live Server si no la tienes.
3. Abre `src/main/resources/static/index.html`, clic derecho → "Open with Live Server".
4. Live Server usará la carpeta `src/main/resources/static` como raíz y la URL será `http://127.0.0.1:5500/index.html`.

Cómo probar usando la app (Spring Boot):

1. Limpiar y compilar:

```powershell
.\gradlew.bat clean build
```

2. Ejecutar la app:

```powershell
.\gradlew.bat bootRun
```

3. Abrir `http://localhost:8080/` y usar DevTools → Network para confirmar que `/css/styles.css` devuelve el archivo correcto.

Notas de limpieza:

- He eliminado versiones antiguas y copias de estilos que podrían crear confusión (style_old, backups). Si quieres que restaure alguna copia, dímelo.

Si quieres más cambios visuales (hero cinematográfico, tipografías, animaciones del carousel), dime cuál prefieres y lo implemento.
# Películas - UI estática

Página de inicio estática añadida al proyecto Spring Boot.

Archivos añadidos:
- `src/main/java/io/github/Rednatsumic/peliculas/controller/MainController.java` - Controlador que sirve `/` e `/index`.
- `src/main/resources/templates/index.html` - Plantilla Thymeleaf (estática) para la página de inicio.
- `src/main/resources/static/css/styles.css` - Estilos para la página.

Cómo ejecutar:

1. En Windows PowerShell, desde la carpeta `peliculas` ejecuta:

   .\gradlew.bat bootRun

2. Abre http://localhost:8080 en tu navegador.

Notas:
- La plantilla es una versión estática basada en el PDF que proporcionaste. Podemos iterar sobre el diseño, añadir imágenes y componentes interactivos según prefieras.
- Si quieres usar Thymeleaf para contenido dinámico, dime qué datos quieres mostrar y los enlazo al controlador.

---

## Guía rápida de interfaz (ES)

Esta sección resume cómo alternar entre las dos variantes de carrusel, ajustar parámetros y editar el tráiler e imágenes. Todo el código tiene comentarios en español para facilitar el mantenimiento.

### Vista previa con Live Server
- Raíz a servir: `src/main/resources/static`
- Archivo de entrada: `index.html`
- Rutas relativas ya listas: `css/styles.css`, `js/*.js`, `images/*`.

### Carrusel: dos variantes
Hay dos implementaciones, puedes elegir según prioridad de UX:

- Scroll‑snap (ACTUAL)
   - Script: `js/carousel.snap.js`
   - Ventajas: desplazamiento táctil nativo, menos JS, comportamiento fluido.

- Coverflow (alternativa)
   - Script: `js/carousel.coverflow.js`
   - Ventajas: efecto de enfoque con escala/desenfoque y sensación 3D.

Cómo alternar entre modos:
1. Abre `src/main/resources/static/index.html`.
2. Cambia el script del carrusel:
    - Scroll‑snap: `<script src="js/carousel.snap.js"></script>`
    - Coverflow: `<script src="js/carousel.coverflow.js"></script>`

### Parámetros que puedes ajustar
- `K` (clones por lado): típicamente `1..3`.
- Autoplay: intervalo por defecto `3500ms`. Puedes cambiarlo o desactivarlo comentando el `setInterval`.
- Dots: se generan dinámicamente según cantidad de slides reales.

Dónde tocar:
- Scroll‑snap: ver sección “Ajustes” en `js/carousel.snap.js`.
- Coverflow: ver encabezado y creación de clones en `js/carousel.coverflow.js`.

### Tráiler (modal)
- Controlador: `js/trailer.js`.
- Cambiar video: actualiza `data-video` en los elementos del HTML (botón “Ver tráiler” y botón de play en el póster).
- Cierre: overlay, botón ✕ o tecla ESC. Al cerrar se limpia el `src` del iframe para detener la reproducción.

### Hero e imágenes
- Fondo del hero: capas con gradientes + `image-set` (`hero.webp`/`hero.jpg`) y placeholders `hero-dark.svg`/`hero-bg.svg`.
- Reemplaza `images/movie-*.svg` por afiches reales (`.webp`/`.jpg`). Mantén proporciones similares.

### Sugerencias de rendimiento
- Si notas tirones, reduce la intensidad de `box-shadow` en `.slide.active` y/o disminuye `gap` en `.carousel-track`.
