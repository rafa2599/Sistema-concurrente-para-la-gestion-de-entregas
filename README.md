# Sistema concurrente para la gestión de entregas

En este informe se busca diseñar, implementar y analizar un sistema concurrente
para la gestión de entregas de una empresa de logística que realiza envíos de
productos comprados en línea. Este sistema cuenta con cuatro etapas
principales: preparación de pedido, despacho de pedido, entrega al cliente y
verificación final.

Dentro de los desafíos para realizar el programa se encontraba el de modelar un
entorno donde múltiples hilos pudieran ejecutarse de forma simultánea, evitando
problemas de concurrencia, como deadlocks, inconsistencias en los recursos
compartidos, etc.

En este trabajo no solo se buscó cumplir con el funcionamiento del programa sino
que también se buscó entender la importancia del diseño concurrente en los tiempos
de ejecución y en el rendimiento. Por ello se realizan registros y el análisis de
resultados para entender el sistema bajo distintas condiciones.
