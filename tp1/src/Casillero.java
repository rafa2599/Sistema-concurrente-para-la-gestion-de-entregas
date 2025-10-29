public class Casillero {
        private EstadoCasillero estado;
        int Contador;

        Casillero(){
            estado   = EstadoCasillero.VACIO;
            Contador = 0;
        }

        public boolean estaVacio(){
            return estado == EstadoCasillero.VACIO; 
        }

        public boolean estaFueraServicio(){
            return estado == EstadoCasillero.FUERA_DE_SERVICIO ;
        }
        
        public void ocupar(){
            estado = EstadoCasillero.OCUPADO;
            Contador++;
        }

        public void desocupar(){
            estado = EstadoCasillero.VACIO;
        }

        public void setFueraServicio(){
            estado = EstadoCasillero.FUERA_DE_SERVICIO;
        }
        public EstadoCasillero getEstado() {
            return estado;
        }
}
