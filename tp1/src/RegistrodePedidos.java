import java.util.ArrayList;
import java.util.Random;

/**
 * Clase RegistrodePedidos que gestiona listas compartidas de pedidos en preparación y en tránsito.
 * Utiliza sincronización con objetos independientes para proteger el acceso concurrente a cada lista.
 */
public class RegistrodePedidos {
    private ArrayList<Pedido> listaPreparacion;
    private ArrayList<Pedido> listaTransito;
    private ArrayList<Pedido> listaEntregados;
    private ArrayList<Pedido> listaFallidos;
    private ArrayList<Pedido> listaVerificados;

    private final Object lockPreparacion = new Object();
    private final Object lockTransito    = new Object();
    private final Object lockEntregados  = new Object();
    private final Object lockFallidos    = new Object();
    private final Object lockVerificados = new Object();

    /**
     * Constructor que inicializa las listas y los objetos de sincronización.
     */
    public RegistrodePedidos() {
        listaPreparacion = new ArrayList<>();
        listaTransito    = new ArrayList<>();
        listaEntregados  = new ArrayList<>();
        listaFallidos    = new ArrayList<>();
        listaVerificados = new ArrayList<>();
    }

    /**
     * Agrega un pedido a la lista de preparación.
     * @param pedido el pedido que será agregado.
     */
    public void addListaPreparacion(Pedido pedido) {
        synchronized (lockPreparacion) {
            listaPreparacion.add(pedido);
            lockPreparacion.notifyAll();
        }
    }

    /**
     * Obtiene y remueve el último pedido de la lista de preparación.
     * Espera si la lista está vacía.
     * @return el pedido listo para ser despachado.
     */
    public Pedido getListaPreparacion() {
        synchronized (lockPreparacion) {
            while (listaPreparacion.isEmpty()) {
                try {
                    lockPreparacion.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted while waiting for pedidos en preparación", e);
                }
            }
            int indice = new Random().nextInt(listaPreparacion.size());
            return listaPreparacion.remove(indice);
        }
    }

    /**
     * Agrega un pedido a la lista de tránsito.
     * @param pedido el pedido que ha sido despachado.
     */
    public void addListaTransito(Pedido pedido) {
        synchronized (lockTransito) {
            listaTransito.add(pedido);
            lockTransito.notifyAll();
        }
    }

    /**
     * Obtiene y remueve el último pedido de la lista de tránsito.
     * Espera si la lista está vacía.
     * @return el pedido listo para ser entregado.
     */
    public Pedido getListaTransito() {
        synchronized (lockTransito) {
            while (listaTransito.isEmpty()) {
                try {
                    lockTransito.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted while waiting for pedidos en tránsito", e);
                }
            }
            int indice = new Random().nextInt(listaTransito.size());
            return listaTransito.remove(indice);
        }
    }

    /**
     * Agrega un pedido a la lista de entregados.
     * @param pedido el pedido que ha sido entregado.
     */
    public void addListaEntregados(Pedido pedido) {
        synchronized (lockEntregados) {
            listaEntregados.add(pedido);
            lockEntregados.notifyAll();
        }
    }

    /**
     * Obtiene y remueve el último pedido de la lista de entregados.
     * Espera si la lista está vacía.
     * @return el pedido entregado más reciente.
     */
    public Pedido getListaEntregados() {
        synchronized (lockEntregados) {
            while (listaEntregados.isEmpty()) {
                try {
                    lockEntregados.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted while waiting for pedidos entregados", e);
                }
            }
            int indice = new Random().nextInt(listaEntregados.size());
            return listaEntregados.remove(indice);
        }
    }

    /**
     * Agrega un pedido a la lista de fallidos.
     * @param pedido el pedido que ha fallado.
     */
    public void addListaFallidos(Pedido pedido) {
        synchronized (lockFallidos) {
            listaFallidos.add(pedido);
        }
    }

    /**
     * Agrega un pedido a la lista de verificados.
     * @param pedido el pedido que ha sido verificado.
     */
    public void addListaVerificados(Pedido pedido) {
        synchronized (lockVerificados) {
            listaVerificados.add(pedido);
        }
    }

    /**
     * Imprime la cantidad de pedidos en cada estado.
     */
    public void print() {
        synchronized (lockTransito) {
            System.out.printf("\nCantidad pedidos en tránsito: %d\n", sizeListaTransito());
        }
        synchronized (lockFallidos) {
            System.out.printf("Cantidad pedidos fallidos: %d\n", sizeListaFallidos());
        }
        synchronized (lockEntregados) {
            System.out.printf("Cantidad pedidos entregados: %d\n", sizeListaEntregados());
        }
        synchronized (lockVerificados) {
            System.out.printf("Cantidad pedidos verificados: %d\n", sizeListaVerificados());
        }
    }

      /**
     * Retorna el tamaño de la lista de pedidos en tránsito, descontando los pedidos de tipo "poison".
     * @return cantidad de pedidos despachados (excluyendo poisons).
     */
    public int sizeListaTransito() {
        synchronized (lockTransito) {
            int total = listaTransito.size();
            int poisonCount = 0;
            for (Pedido p : listaTransito) {
                if (p.pedidoPoison()) {           
                    poisonCount++;
                }
            }
            return total - poisonCount;
        }
    }

    /**
     * Retorna el tamaño de la lista de pedidos en preparación.
     * @return cantidad de pedidos en preparación.
     */
    public int sizeListaPreparacion() {
        synchronized (lockPreparacion) {
            return listaPreparacion.size();
        }
    }

    /**
     * Retorna el tamaño de la lista de pedidos fallidos.
     * @return cantidad de pedidos fallidos.
     */
    public int sizeListaFallidos() {
        synchronized (lockFallidos) {
            return listaFallidos.size();
        }
    }

    /**
     * Retorna el tamaño de la lista de pedidos entregados.
     * @return cantidad de pedidos entregados.
     */
    public int sizeListaEntregados() {
        synchronized (lockEntregados) {
            int total = listaEntregados.size();
            int poisonCount = 0;
            for (Pedido p : listaEntregados) {
                if (p.pedidoPoison()) {           
                    poisonCount++;
                }
            }
            return total - poisonCount;
        }
    }

    /**
     * Retorna el tamaño de la lista de pedidos entregados.
     * @return cantidad de pedidos entregados.
     */
    public int sizeListaVerificados() {
        synchronized (lockEntregados) {
            return listaVerificados.size();
        }
    }
    
}
