# Práctica 3: Implementación de Chat Concurrente


---

## 1. Cliente.java

* **Paquete:** `concurrentChat`
* **Función:** Es el punto de entrada para el usuario. Se encarga de iniciar la conexión con el servidor y lanzar los hilos de E/S (`ReadHandler` y `WriteHandler`) una vez que la conexión es exitosa.
* **Modificaciones y Mejoras:**
    * **Conexión Dinámica:** En lugar de tener la IP y el Puerto "quemados" (hardcoded), el sistema solicita activamente al usuario que introduzca la **IP** y el **Puerto** del servidor al que desea conectarse.
    * **Comando de Arranque:** Se implementó un comando de inicio `start-conection`. El cliente no intentará conectarse hasta que el usuario escriba este comando.
    * **Refactorización Estructural:**
        * La lógica de `main` se simplificó, cambiando la estructura de `switch` por un `if-else` más directo.
        * La lógica para solicitar datos y crear el `Socket` se encapsuló en un método separado (`establecerConexion()`) para un código más limpio.
    * **Manejo de Recursos:** Se utiliza `try-with-resources` para el `Scanner` que lee los datos de conexión, asegurando que se cierre correctamente.

---

## 2. ClientHandler.java

* **Paquete:** `Procesadores` (Se movió a un paquete distinto de `Handlers` para separar responsabilidades).
* **Función:** Es la clase clave del lado del servidor. Cada instancia de `ClientHandler` es ejecutada en su propio hilo por el `ExecutorService` del servidor. Gestiona la lógica de un cliente individual, su estado (como el `nombreDeUsuario`) y procesa todos sus comandos.
* **Modificaciones y Mejoras Significativas:**
    * **Interfaz de Comandos:** Implementa un procesador de comandos que responde a las siguientes acciones:
        * `/change-userName [nuevoNombre]`: Actualiza el alias del usuario y notifica a los demás.
        * `/send-msg [usuario] [mensaje]`: Envía un mensaje privado a un usuario específico.
        * `/global-msg [mensaje]`: Envía un mensaje a todos los demás clientes conectados.
        * `/exit`: Desconecta al cliente.
    * **Refactorización Funcional (Java Streams):**
        * A diferencia de un bucle `for` con banderas booleanas, la lógica para `/send-msg` se implementó usando `Streams` y `Optional`. Esto permite una búsqueda de usuario más limpia y moderna (`.stream().filter(...).findFirst()`).
        * La lógica de `/global-msg` y la notificación de cambio de nombre también usan `Streams` (`.stream().filter().forEach()`) para difundir el mensaje a los clientes relevantes.
    * **Manejo de E/S:** Se configuró el `PrintWriter` para usar `autoFlush=true`, eliminando la necesidad de llamar a `.flush()` manualmente después de cada mensaje.

---

## 3. ReadHandler.java

* **Paquete:** `Handlers`
* **Función:** Hilo dedicado exclusivamente a la **lectura**. Escucha de forma pasiva y continua cualquier mensaje que el servidor envíe al cliente y lo imprime en la consola del usuario.

---

## 4. WriteHandler.java

* **Paquete:** `Handlers`
* **Función:** Hilo dedicado exclusivamente a la **escritura**. Lee la entrada del usuario desde la consola (`System.in`) y la envía directamente al servidor a través del `Socket`.
* **Modificaciones:**
    * Maneja el comando `/exit` del lado del cliente, permitiendo al usuario terminar la conexión de forma controlada.

---

## 5. ChatServer.java 

* **Función:** El servidor principal. Escucha en un puerto específico por conexiones entrantes.
* **Mejoras (Basadas en la lógica de `ClientHandler`):**
    * **Pool de Hilos:** Utiliza un `ExecutorService` (ej. `newCachedThreadPool`) para gestionar a los clientes. En lugar de crear un `new Thread()` manualmente, delega la gestión de hilos al *pool* por cada cliente que se conecta.
    * **Colección Segura:** Mantiene una colección `Set<ClientHandler>` que es *thread-safe* (segura para hilos), como `ConcurrentHashMap.newKeySet()`.
    * **Inyección de Dependencia:** Al crear un nuevo `ClientHandler`, el servidor le "inyecta" (pasa) la lista completa de clientes (`Set<ClientHandler>`) para que este pueda realizar las acciones de mensajes globales y privados.

---
