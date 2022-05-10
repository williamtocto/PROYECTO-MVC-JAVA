package mvc.controlador;

import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import mvc.modelo.ModeloPersona;
import mvc.modelo.Persona;
import mvc.vista.VistaPersona;
import javax.imageio.ImageIO;
import mvc.modelo.ConexionPG;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

public class ControlPersona {

    private ModeloPersona modelo;
    private VistaPersona vista;
    int n;
    String ruta = "";
    int fila = -1;
    ModeloPersona persona = new ModeloPersona();

   String idpersona, nombre, apellido, telefono, sueldo, sexo, cupo;

    public ControlPersona(ModeloPersona modelo, VistaPersona vista) {
        this.modelo = modelo;
        this.vista = vista;
        //Inicializaciones
        vista.setTitle("CRUD PERSONAS");
        vista.setVisible(true);
        cargaLista("");
    }

    public void iniciaControl() {
        KeyListener kl = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                //     throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void keyPressed(KeyEvent e) {
                //     throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void keyReleased(KeyEvent e) {
                //    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                cargaLista(vista.getTxtBuscar().getText());
            }
        };
        //Controlar los eventos de la vista
        vista.getBtnRefrescar().addActionListener(l -> cargaLista(""));
        vista.getBtnCrear().addActionListener(l -> {

            try {
                cargarDialogo(1);
            } catch (SQLException ex) {
                Logger.getLogger(ControlPersona.class.getName()).log(Level.SEVERE, null, ex);
            }

        });
        vista.getBtnEditar().addActionListener(l -> {

            try {
                cargarDialogo(2);
            } catch (SQLException ex) {
                Logger.getLogger(ControlPersona.class.getName()).log(Level.SEVERE, null, ex);
            }

        });
        vista.getBtnEliminar().addActionListener(l -> Eliminar());
        vista.getBtnExaminar().addActionListener(l -> examinaFoto());
        vista.getBtnAceptar().addActionListener(l -> {
            try {
                DefinirMetodo(n);
            } catch (SQLException ex) {
                Logger.getLogger(ControlPersona.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        //Controlador Buscar
        vista.getTxtBuscar().addKeyListener(kl);
        vista.getBtn_salir().addActionListener(l -> salir());
        vista.getBtnImprimir().addActionListener(l -> insertarTitulo());
        vista.getBtn_aceptar_titulo().addActionListener(l -> imprimirPersonas());
        vista.getBtn_cancelarTitulo().addActionListener(l -> vista.getDialogo_titulo().dispose());
    }

    public void salir() {
        this.vista.setVisible(false);
        this.vista.dispose();
    }

    public void insertarTitulo() {
        vista.getTxt_nombre_titulo().setText("");
        vista.getDialogo_titulo().setLocationRelativeTo(vista);
        vista.getDialogo_titulo().setSize(300, 270);
        vista.getDialogo_titulo().setVisible(true);
    }
    public void imopoo() {
        vista.getDialogo_titulo().dispose();
        ConexionPG con = new ConexionPG();
        try {
            JasperReport reporte = null;
            String ruta = "src\\mvc\\vista\\reportes\\ReportesPersona.jasper";
            reporte = (JasperReport) JRLoader.loadObjectFromFile(ruta);
            Map<String, Object> parametro = new HashMap<String, Object>();
            parametro.put("aguja", "%" + vista.getTxtBuscar().getText() + "%");
            parametro.put("titulo","REPORTE GENERAL DE PERSONAS");
            JasperPrint jp = JasperFillManager.fillReport(reporte, parametro, con.getCon());
            JasperViewer jv = new JasperViewer(jp, false);
            jv.setVisible(true);
        } catch (JRException ex) {
            Logger.getLogger(ControlPersona.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    private void imprimirPersonas() {

        vista.getDialogo_titulo().dispose();
        ConexionPG con = new ConexionPG();
        try {
            JasperReport reporte = null;
            String ruta = "src\\mvc\\vista\\reportes\\ReportesPersona.jasper";
            reporte = (JasperReport) JRLoader.loadObjectFromFile(ruta);
            Map<String, Object> parametro = new HashMap<String, Object>();
            parametro.put("aguja", "%" + vista.getTxtBuscar().getText() + "%");
            parametro.put("titulo", vista.getTxt_nombre_titulo().getText());
            JasperPrint jp = JasperFillManager.fillReport(reporte, parametro, con.getCon());
            JasperViewer jv = new JasperViewer(jp, false);
            jv.setVisible(true);
        } catch (JRException ex) {
            Logger.getLogger(ControlPersona.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void DefinirMetodo(int n) throws SQLException {

        if (n == 1) {
            fila = vista.getTblPersonas().getSelectedRow();
            grabarPersona();
        } else if (n == 2) {
            fila = vista.getTblPersonas().getSelectedRow();
            editar();
        }
    }
    private void tablaMouseClicked(java.awt.event.MouseEvent evt) {
        
    }
    private void cargarDialogo(int origen) throws SQLException {
        vista.getLblFoto().setIcon(null);
        vista.getDlgPersona().setSize(600, 600);
        vista.getDlgPersona().setLocationRelativeTo(vista);
        fila = vista.getTblPersonas().getSelectedRow();
        if (origen == 1) {
            vista.getDlgPersona().setTitle("Crear Persona");
            n = 1;
            vista.getDlgPersona().setVisible(true);
        } else {
            if (fila == -1) {
                JOptionPane.showMessageDialog(vista, "SELECCIONE UN DATO DE LA TABLA", "WILLIAM TOCTO", 2);
            } else {
                cargarDatos();
                vista.getDlgPersona().setTitle("Editar Persona");
                n = 2;
                vista.getDlgPersona().setVisible(true);
            }

        }

    }

    private void cargaLista(String aguja) {

        vista.getTblPersonas().setDefaultRenderer(Object.class, new Render());
        vista.getTblPersonas().setRowHeight(100);
        DefaultTableCellRenderer render = new DefaultTableCellRenderer();
        DefaultTableModel tblModel; //Estructura JTbable
        tblModel = (DefaultTableModel) vista.getTblPersonas().getModel();
        tblModel.setNumRows(0);
        ArrayList<Persona> lista = modelo.listaPersonas(aguja);
        int ncols = tblModel.getColumnCount();
        for (int i = 0; i < lista.size(); i++) {
            Persona p = new Persona();
            tblModel.addRow(new Object[ncols]);
            vista.getTblPersonas().setValueAt(lista.get(i).getIdPersona(), i, 0);
            vista.getTblPersonas().setValueAt(lista.get(i).getNombre(), i, 1);
            vista.getTblPersonas().setValueAt(lista.get(i).getApellido(), i, 2);
            vista.getTblPersonas().setValueAt(lista.get(i).getEdad(), i, 3);
            vista.getTblPersonas().setValueAt(lista.get(i).getTelefono(), i, 4);
            vista.getTblPersonas().setValueAt(lista.get(i).getSexo(), i, 5);
            vista.getTblPersonas().setValueAt(lista.get(i).getSueldo(), i, 6);
            vista.getTblPersonas().setValueAt(lista.get(i).getCupo(), i, 7);
            try {
                byte[] bi = lista.get(i).getFoto();
                BufferedImage image = null;
                InputStream in = new ByteArrayInputStream(bi);
                image = ImageIO.read(in);
                ImageIcon imgi = new ImageIcon(image.getScaledInstance(100, 100, Image.SCALE_SMOOTH));
                vista.getTblPersonas().setValueAt(new JLabel(imgi), i, 8);

            } catch (Exception ex) {
                vista.getTblPersonas().setValueAt(new JLabel("SIN IMAGEN"), i, 8);
            }
        }
    }

    private void examinaFoto() {
        JFileChooser jfc = new JFileChooser();
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            ruta = jfc.getSelectedFile().getAbsolutePath();
            System.out.println(ruta);
            try {
                Image miImagen = ImageIO.read(jfc.getSelectedFile()).getScaledInstance(
                        vista.getLblFoto().getWidth(),
                        vista.getLblFoto().getHeight(),
                        Image.SCALE_DEFAULT);
                Icon icon = new ImageIcon(miImagen);
                vista.getLblFoto().setIcon(icon);
                vista.getLblFoto().updateUI();
            } catch (IOException ex) {
                Logger.getLogger(ControlPersona.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void grabarPersona() throws SQLException {
        Datos();
        Instant instant = vista.getDtcFechaNacimiento().getDate().toInstant();
        ZoneId zid = ZoneId.of("America/Guayaquil");
        ZonedDateTime zdt = ZonedDateTime.ofInstant(instant, zid);
        Date fecha = Date.valueOf(zdt.toLocalDate());
        persona.setIdPersona(idpersona);
        persona.setNombre(nombre);
        persona.setApellido(apellido);
        persona.setFechaNacimiento(fecha);
        persona.setTelefono(telefono);
        persona.setSueldo(Double.parseDouble(sueldo));
        persona.setSexo(sexo);
        persona.setCupo(Integer.parseInt(cupo));
        File rutafile = new File(ruta);
        if (persona.grabar(rutafile)) {
            JOptionPane.showMessageDialog(vista, "Persona Creada Satisfactoriamente", "WILLIAM TOCTO", 1);
            vista.getDlgPersona().dispose();
            cargaLista("");
        } else {
            JOptionPane.showMessageDialog(vista, "ERROR");
        }
    }

    public void editar() throws SQLException {

        Datos();
        Instant instant = vista.getDtcFechaNacimiento().getDate().toInstant();
        ZoneId zid = ZoneId.of("America/Guayaquil");
        ZonedDateTime zdt = ZonedDateTime.ofInstant(instant, zid);
        Date fecha = Date.valueOf(zdt.toLocalDate());
        persona.setIdPersona(idpersona);
        persona.setNombre(nombre);
        persona.setApellido(apellido);
        persona.setFechaNacimiento(fecha);
        persona.setTelefono(telefono);
        persona.setSueldo(Double.parseDouble(sueldo));
        persona.setSexo(sexo);
        persona.setCupo(Integer.parseInt(cupo));
        File ruta1 = new File(ruta);
        if (persona.editar(idpersona, ruta1)) {
            JOptionPane.showMessageDialog(vista, "PERSONA EDITADA CORRECTAMENTE", "WILLIAM TOCTO", 1);
            vista.getDlgPersona().dispose();
            cargaLista("");
        } else {
            JOptionPane.showMessageDialog(vista, "ERROR");
        }

    }

    private void Eliminar() {
        fila = vista.getTblPersonas().getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(vista, "PRIMERO SELECCIONE UNA PERSONA", "WILLIAM TOCTO", 2);
        } else {
            int op = op = JOptionPane.showOptionDialog(null, "ESTA SEGIRO QUE DESEA ELIMNAR ESTA PERSONA", "WILLIAM TOCTO",
                    JOptionPane.YES_NO_CANCEL_OPTION, 2, null, new Object[]{"SI", "NO",}, null);
            if (op == 0) {

                String id = String.valueOf(vista.getTblPersonas().getValueAt(fila, 0));
                if (persona.eliminar(id)) {
                    cargaLista("");
                    JOptionPane.showMessageDialog(vista, "PERSONA ELIMINADA SATISFACTORIAMNETE", "WILLIAM TOCTO", 1);
                    vista.getDlgPersona().setVisible(false);
                } else {
                    JOptionPane.showMessageDialog(vista, "ERROR");
                }

            } else {
                JOptionPane.showMessageDialog(vista, "ACCION CANCELADA", "WILLIAM TOCTO", 1);
            }
        }

    }

    public void Datos() {
        fila = vista.getTblPersonas().getSelectedRow();
        idpersona = vista.getTxtID().getText();
        nombre = vista.getTxtNombres().getText();
        apellido = vista.getTxtApellidos().getText();
        telefono = vista.getTxt_telefono().getText();
        sueldo = vista.getTxt_sueldo1().getText();
        sexo = vista.getTxt_sexo().getText();
        cupo = vista.getTxt_cupo().getText();
    }

    public void cargarDatos() throws SQLException {
        
        fila = vista.getTblPersonas().getSelectedRow();
        vista.getTxtID().setText(String.valueOf(vista.getTblPersonas().getValueAt(fila, 0)));
        vista.getTxtNombres().setText(String.valueOf(vista.getTblPersonas().getValueAt(fila, 1)));
        vista.getTxtApellidos().setText(String.valueOf(vista.getTblPersonas().getValueAt(fila, 2)));
        vista.getDtcFechaNacimiento().setDate(persona.listarFechaNac(String.valueOf(vista.getTblPersonas().getValueAt(fila, 0))));
        vista.getTxt_telefono().setText(String.valueOf(vista.getTblPersonas().getValueAt(fila, 4)));
        vista.getTxt_sexo().setText(String.valueOf(vista.getTblPersonas().getValueAt(fila, 5)));
        vista.getTxt_sueldo1().setText(String.valueOf(vista.getTblPersonas().getValueAt(fila, 6)));
        vista.getTxt_cupo().setText(String.valueOf(vista.getTblPersonas().getValueAt(fila, 7)));
    }

}
