import java.util.Date;
import java.util.Random;

public class VerificacionFinal implements Runnable {
    private RegistrodePedidos registropedidos;
    private boolean finalizacion = false;

    public VerificacionFinal(RegistrodePedidos registropedidos) {
        this.registropedidos = registropedidos;
    }
    
    public void VerificarPedido() {
        Pedido pedido = registropedidos.getListaEntregados();
        if (pedido.pedidoPoison()) {
            registropedidos.addListaEntregados(pedido);
            finalizacion = true;
            return;
        }

        Random rnd = new Random();

        if (rnd.nextInt(100) < 5) { // 5% de fallas
            pedido.setFallido();
            registropedidos.addListaFallidos(pedido);
            log("PEDIDO_FALLIDO", pedido);
        } else {
            registropedidos.addListaVerificados(pedido);
            log("PEDIDO_VERIFICADO", pedido);
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
     * Ejecuta el hilo de verificacion hasta completar la cantidad total de pedidos.
     */
    @Override
    public void run() {
        while (!finalizacion){
            VerificarPedido();
            try {
                Random rnd = new Random();
                Thread.sleep(rnd.nextInt(60, 120));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        log("VERIFICACION_FINALIZACION", null);
        
    }
}