Ejercicio 2
-----------

En este ejercicio se trata de descargar una imagen en el contexto de una app de Android. Para ello usaremos AsyncTask para descargar una imagen en segundo plano.

El interfaz de la Activity contiene un botón de descarga y un objeto ImageView que mostrará la imagen una vez descargada. Además la app nos debe indicar el porcentaje de descarga de la imagen mediante un objeto ProgressBar.

La URL se leerá de un TextView, que contendrá una URL por defecto pero que podrá cambiarse.

Si la URL no contiene una imagen debe indicarse mediante un mensaje de error.
