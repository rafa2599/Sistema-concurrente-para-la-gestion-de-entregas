import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Clase principal que inicia el sistema de almacenamiento y gestión de pedidos.
 * 
 * Se crean hilos concurrentes para preparar y despachar pedidos, los cuales
 * interactúan mediante estructuras sincronizadas (casilleros y registros de pedidos).
 * El estado del sistema se registra periódicamente en un archivo de salida.
 *
 * @author 
 */
public class Main {
  /**
   * Punto de entrada del programa. Inicializa el sistema, lanza hilos productores
   * y consumidores, y registra el estado del sistema hasta que todos los hilos finalicen.
   *
   * @param args Argumentos de línea de comandos (no utilizados).
   */
  public static void main(String[] args) {
      long startTime = System.currentTimeMillis();

      SistemaAlmacenamiento sistemaAlmacenamiento = new SistemaAlmacenamiento(500);
      RegistrodePedidos registrodePedidos = new RegistrodePedidos();
      Preparacion preparacion = new Preparacion(sistemaAlmacenamiento, registrodePedidos);
      Despacho despacho = new Despacho(sistemaAlmacenamiento, registrodePedidos);
      Entrega entrega = new Entrega(registrodePedidos);
      VerificacionFinal verificacion = new VerificacionFinal(registrodePedidos);

      Thread[] hilos = {
          new Thread(preparacion),
          new Thread(despacho),
          new Thread(verificacion),
          new Thread(preparacion),
          new Thread(despacho),
          new Thread(entrega),
          new Thread(entrega),
          new Thread(verificacion),
          new Thread(preparacion),
          new Thread(entrega),
      };

      for (Thread t : hilos) {
          t.start();
      }

      BufferedWriter writer = null;
      try {
          writer = new BufferedWriter(new FileWriter("registro.txt"));
      } catch (IOException e) {
          e.printStackTrace();
      }

      boolean algunoVivo = true;

      while (algunoVivo) {
          algunoVivo = false;
          for (Thread thread : hilos) {
              if (thread.isAlive()) {
                  algunoVivo = true;
              }
          }

          Registro(startTime, sistemaAlmacenamiento, registrodePedidos, writer, false);
          try {
              Thread.sleep(200);
          } catch (InterruptedException e) {
              e.printStackTrace();
          }
      }

      Registro(startTime, sistemaAlmacenamiento, registrodePedidos, writer, true);
      despacho.print();
  }

  /**
   * Registra información del estado actual del sistema en un archivo de texto.
   * 
   * @param startTime Tiempo de inicio del programa en milisegundos.
   * @param sistema Referencia al sistema de almacenamiento.
   * @param registro Registro de pedidos en transcurso y completados.
   * @param writer Objeto de escritura a archivo.
   * @param lineaFinal Si es true, imprime la línea de cierre con casilleros fallidos.
   */
  public static void Registro(long startTime, SistemaAlmacenamiento sistema,
                               RegistrodePedidos registro, BufferedWriter writer,
                               boolean lineaFinal) {
      long programTime = System.currentTimeMillis() - startTime;
      String prefix = String.format("[%03d ms] ", programTime);

      try {
        String linea = prefix +
        "Preparación: " + registro.sizeListaPreparacion() + " | " +
        "Tránsito: " + registro.sizeListaTransito() + " | " +
        "Entregados: " + registro.sizeListaEntregados() + " | " +
        "Fallidos: " + registro.sizeListaFallidos() + " | " +
        "Verificados: " + registro.sizeListaVerificados();
    
          writer.write(linea);
          writer.newLine();
          writer.flush();
      } catch (IOException e) {
          e.printStackTrace();
      }

      if (lineaFinal) {
          try {
              writer.newLine();
              String linea = prefix + " Casilleros fallidos: " + sistema.getCasillerosFallidos() + " | " +
              " Casilleros funcionales " + sistema.getCasillerosFuncionales() ;
              writer.write(linea);
              writer.newLine();
              ArrayList<Casillero> lista = sistema.getMatrizCasilleros();
              for (int i = 0; i < 10; i++) {
                StringBuilder row = new StringBuilder();
                for (int j = 0; j < 20; j++) { 
                    int index = i * 20 + j;
                    if (lista.get(index).estaFueraServicio()) {
                        row.append(String.format("%4s", "#"));
                    } else{
                        row.append(String.format("%4d", lista.get(index).Contador));
                    }  
                }
                writer.write(row.toString());
                writer.newLine();
            }
            writer.flush();
          } catch (IOException e) {
              e.printStackTrace();
          }
      }
  }
}
