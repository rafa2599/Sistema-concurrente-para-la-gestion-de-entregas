import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Clase Despacho que simula el rol de consumidor.
 * Se encarga de despachar pedidos, liberando casilleros o marcándolos como fallidos.
 */
public class Despacho implements Runnable {
    private SistemaAlmacenamiento sistema;
    private RegistrodePedidos Registropedidos;
    private AtomicInteger pedidosCompletados = new AtomicInteger(0);

    /**
     * Constructor.
     * @param sistema instancia del sistema de almacenamiento.
     * @param pedidos referencia al registro compartido de pedidos.
     */
    public Despacho(SistemaAlmacenamiento sistema, RegistrodePedidos pedidos) {
        this.sistema = sistema;
        this.Registropedidos = pedidos;
    }

    /**
     * Despacha un pedido, liberando el casillero o marcándolo como fuera de servicio en caso de error.
     */
    public void despacharPedido() {
        Pedido pedido = Registropedidos.getListaPreparacion();
        Random rnd = new Random();

        if (rnd.nextInt(100) < 15) { // 15% de fallas
            sistema.setCasilleroFueraServicio(pedido);
            pedido.setFallido();
            Registropedidos.addListaFallidos(pedido);
        } else {
            sistema.desocuparCasillero(pedido);
            Registropedidos.addListaTransito(pedido);
        }
    }

    /**
     * Método sincronizado para incrementar y devolver el número de pedido procesado.
     * @return número del siguiente pedido a procesar o el total si ya se completó.
     */
    public int siguientePedido() {
        int pedido;
        if (pedidosCompletados.get() < sistema.getTotalPedidos()) {
            pedido = pedidosCompletados.getAndIncrement();
            return pedido;
        } else {
            return sistema.getTotalPedidos(); // o -1 si querés evitar que se repita
        }
    }

     /**
     * Imprime estadísticas y registros de pedidos.
     */
    public void print() {
        System.out.printf("\nCantidad pedidos preparados  %d\n", pedidosCompletados.get());
        Registropedidos.print();
    }

    /**
     * Ejecuta el hilo de despacho hasta completar la cantidad total de pedidos.
     */
    @Override
    public void run() {
        while (siguientePedido() < sistema.getTotalPedidos()) {
            despacharPedido();
            try {
                Random rnd = new Random();
                Thread.sleep(rnd.nextInt(60, 120));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Pedido pedidoPoison = new Pedido(null, -1);
        pedidoPoison.setPoisonPill();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Registropedidos.addListaTransito(pedidoPoison);
    }
}