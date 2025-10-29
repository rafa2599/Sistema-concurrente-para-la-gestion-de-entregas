import java.util.Date;
import java.util.Random;

public class Entrega implements Runnable {
    private RegistrodePedidos Registropedidos;
    private boolean finalizacion = false;

    /**
     * Constructor.
     * @param sistema instancia del sistema de almacenamiento.
     * @param pedidos referencia al registro compartido de pedidos.
     */
    public Entrega( RegistrodePedidos pedidos) {
        this.Registropedidos = pedidos;
    }

    /**
     * Entrega un pedido, liberando el casillero o marcándolo como fuera de servicio en caso de error.
     */
    public void entregaPedido() {
        Pedido pedido = Registropedidos.getListaTransito();
        if (pedido.pedidoPoison()) {
            Registropedidos.addListaTransito(pedido);
            finalizacion = true;
            return;
        }

        Random rnd = new Random();

        if (rnd.nextInt(100) < 10) { // 10% de fallas
            pedido.setFallido();
            Registropedidos.addListaFallidos(pedido);
            log("PEDIDO_FALLIDO", pedido);
        } else {
            Registropedidos.addListaEntregados(pedido);
            log("PEDIDO_ENTREGADO", pedido);
        }

    }


    /**
     * Registra en consola información sobre acciones realizadas en el sistema.
     * @param accion Descripción de la acción.
     * @param pedido Pedido involucrado (puede ser null).
     */
    private void log(String accion, Pedido pedido) {
        String msg = String.format("%1$tF %1$tT.%1$tL [%2$s] %3$s %4$s",
                new Date(),
                Thread.currentThread().getName(),
                accion,
                (pedido != null ? pedido : ""));
        System.out.println(msg);
    }

 
    /**
     * Ejecuta el hilo de despacho hasta completar la cantidad total de pedidos.
     */
    @Override
    public void run() {
        while (!finalizacion){
            entregaPedido();
            try {
                Random rnd = new Random();
                Thread.sleep(rnd.nextInt(90, 180));
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

        Registropedidos.addListaEntregados(pedidoPoison);
        log("ENTREGA_FINALIZACION", null);
    }

}
