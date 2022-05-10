package mvc.controlador;

import java.awt.Dimension;
import mvc.modelo.ModeloPersona;
import mvc.vista.Ventana_Principal;
import mvc.vista.VistaPersona;

public class Control_Principal {

    private Ventana_Principal vista;
    private ControlPersona mode;

    public Control_Principal(Ventana_Principal vista) {
        this.vista = vista;
        vista.setVisible(true);
        System.out.println("1");
        
    }

    public void inicioControl() {
        vista.getBtn_mantenimiento().addActionListener(l -> mantenimientoPersonas());
        vista.getSub_mantenimiento().addActionListener(l -> mantenimientoPersonas());
        vista.getBtn_generarReporte().addActionListener(l-> imprimir());            
    }
    
    public void mantenimientoPersonas() {

        ModeloPersona m = new ModeloPersona();
        VistaPersona v = new VistaPersona();
        vista.getDsk_escritorio().add(v);
        Dimension desktopSize = vista.getDsk_escritorio().getSize();
        Dimension FrameSize = v.getSize();
        v.setLocation((desktopSize.width - FrameSize.width) / 2, (desktopSize.height - FrameSize.height) / 2);
        ControlPersona c = new ControlPersona(m, v);
        c.iniciaControl();
        
    }

    public void imprimir() {

        ModeloPersona m = new ModeloPersona();
        VistaPersona v = new VistaPersona();
        ControlPersona c = new ControlPersona(m, v);
        c.imopoo();     
    }

}
