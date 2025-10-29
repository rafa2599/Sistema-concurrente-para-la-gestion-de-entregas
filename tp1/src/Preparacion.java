import java.util.Random;

/**
 * Clase Preparacion que simula el rol de productor.
 * Se encarga de generar pedidos y ocupar casilleros en el sistema.
 */
public class Preparacion implements Runnable {
    private SistemaAlmacenamiento sistema;
    private RegistrodePedidos Registropedidos;
    private int pedidosCompletados;

    /**
     * Constructor.
     * @param sistema instancia del sistema de almacenamiento.
     * @param pedidos referencia al registro compartido de pedidos.
     */
    public Preparacion(SistemaAlmacenamiento sistema, RegistrodePedidos pedidos) {
        this.sistema = sistema;
        this.Registropedidos = pedidos;
        this.pedidosCompletados = 0;
    }

    /**
     * Solicita un casillero disponible y registra el pedido en preparación.
     */
    public void prepararPedido() {
        Pedido pedido = sistema.ocuparCasillero();
        Registropedidos.addListaPreparacion(pedido); 
    }

    /**
     * Método sincronizado para incrementar y devolver el número de pedido procesado.
     * @return número del siguiente pedido a procesar.
     */
    public synchronized int siguientePedido() {
        return pedidosCompletados++;
    }

    /**
     * Ejecuta el hilo de preparación hasta completar la cantidad total de pedidos.
     */
    @Override
    public void run() {
        while (siguientePedido() < sistema.getTotalPedidos()) {
            prepararPedido();
            try {
                Random rnd = new Random();
                Thread.sleep(rnd.nextInt(90, 180));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        } 
    }

