package mvc;

import mvc.controlador.ControlPersona;
import mvc.controlador.Control_Principal;
import mvc.modelo.ModeloPersona;
import mvc.vista.Ventana_Principal;
import mvc.vista.VistaPersona;

public class Mvc {

    public static void main(String[] args) {
      
        Ventana_Principal vista = new Ventana_Principal();      
        Control_Principal control = new Control_Principal(vista);
        control.inicioControl();

    }

}
