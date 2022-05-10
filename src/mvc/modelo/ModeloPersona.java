package mvc.modelo;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ModeloPersona extends Persona {

    ConexionPG con = new ConexionPG();

    public ModeloPersona() {
    }

    public ModeloPersona(String idPersona, String nombre, String apellido, Date fechaNacimiento, int edad, String telefono, String sexo, Double sueldo, int cupo, byte[] foto) {
        super(idPersona, nombre, apellido, fechaNacimiento, edad, telefono, sexo, sueldo, cupo, foto);
    }

    public ArrayList<Persona> listaPersonas(String aguja) {
        int edad;

        try {
            String sql = "select * from persona WHERE ";
            sql += " UPPER(nombres) like UPPER('%" + aguja + "%') OR";
            sql += " UPPER(apellidos) like UPPER('%" + aguja + "%') OR";
            sql += " UPPER(telefono) like UPPER('%" + aguja + "%') OR";
            sql += " UPPER(sexo) like UPPER('%" + aguja + "%') OR";
            // sql += " sueldo = " + Double.parseDouble(aguja) + " OR";
            // sql += " cupo =" + aguja + " OR";
            sql += " UPPER(idpersona) like UPPER('%" + aguja + "%') ";
            ResultSet rs = con.consulta(sql);
            ArrayList<Persona> lp = new ArrayList<Persona>();
            while (rs.next()) {
                Persona per = new Persona();
                per.setIdPersona(rs.getString("idpersona"));//Nombre de la columna de la base de dato.
                per.setNombre(rs.getString("nombres"));//Nombre de la columna de la base de dato.
                per.setApellido(rs.getString("apellidos"));//Nombre de la columna de la base de dato.
                edad = calcularEdad(rs.getString("fechanacimiento"));
                per.setEdad(edad);
                per.setTelefono(rs.getString("telefono"));
                per.setSexo(rs.getString("sexo"));
                per.setSueldo(rs.getDouble("sueldo"));
                per.setCupo(rs.getInt("cupo"));
                per.setFoto(rs.getBytes("foto"));
                lp.add(per);
            }
            rs.close();
            return lp;
        } catch (SQLException ex) {
            Logger.getLogger(ModeloPersona.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }

    public boolean grabar(File ruta) throws SQLException {

        byte[] foto;
        try {
            byte[] icono = new byte[(int) ruta.length()];
            InputStream input = new FileInputStream(ruta);
            input.read(icono);
            foto = icono;
        } catch (IOException ex) {
            foto = null;
        }
        String sql1 = "INSERT INTO persona(idpersona,nombres,apellidos,fechanacimiento,telefono,sexo,sueldo,cupo,foto) "
                + "VALUES (?,?,?,?,?,?,?,?,?);";
        PreparedStatement ps = null;

        ps = con.getCon().prepareStatement(sql1);
        ps.setString(1, getIdPersona());
        ps.setString(2, getNombre());
        ps.setString(3, getApellido());
        ps.setDate(4, getFechaNacimiento());
        ps.setString(5, getTelefono());
        ps.setString(6, getSexo());
        ps.setDouble(7, getSueldo());
        ps.setInt(8, getCupo());
        ps.setBytes(9, foto);
        boolean ejecutar = false;

        if (ps.executeUpdate() == 1) {
            ejecutar = true;
        }

        /* String sql;
        sql = "INSERT INTO persona(idpersona,nombres,apellidos,fechanacimiento,telefono,sexo,sueldo,cupo,foto) ";
        sql += " VALUES ('" + getIdPersona() + "','" + getNombre() + "','" + getApellido()
                + "','" + getFechaNacimiento() + "','" + getTelefono() + "','" + getSexo()
                + "','" + getSueldo() + "','" + getCupo() + "','" + foto64 + "')";
        boolean hola = con.accion(sql);
        System.out.println(hola);*/
        return ejecutar;
    }

    public boolean editar(String id, File ruta) throws SQLException {

        byte[] foto;
        try {
            byte[] icono = new byte[(int) ruta.length()];
            InputStream input = new FileInputStream(ruta);
            input.read(icono);
            foto = icono;
        } catch (IOException ex) {
            foto = null;
        }
       String sql;
        sql = "UPDATE persona SET  nombres=?,apellidos=?,fechanacimiento=?,telefono=?,sexo=?,sueldo=?"
                + ",cupo=?,foto=? where idpersona = '" + id + "';";
        
          PreparedStatement ps = null;

        ps = con.getCon().prepareStatement(sql);
        ps.setString(1, getNombre());
        ps.setString(2, getApellido());
        ps.setDate(3, getFechaNacimiento());
        ps.setString(4, getTelefono());
        ps.setString(5, getSexo());
        ps.setDouble(6, getSueldo());
        ps.setInt(7, getCupo());
        ps.setBytes(8, foto);
        boolean ejecutar = false;

        if (ps.executeUpdate() == 1) {
            ejecutar = true;
        }

        return ejecutar;

        /*String sql;
        sql = "UPDATE persona SET nombres='" + getNombre() + "',apellidos='" + getApellido()
                + "',fechanacimiento='" + getFechaNacimiento() + "',telefono='" + getTelefono()
                + "',sexo='" + getSexo() + "',sueldo=" + getSueldo() + ",cupo=" + getCupo() + ",foto= '" + Arrays.toString(foto) + "'"
                + " WHERE idpersona = '" + id + "';";*/
    }

    public boolean eliminar(String id) {
        String sql;
        sql = "DELETE from persona where idpersona='" + id + "'";
        return con.accion(sql);
    }

    public Date listarFechaNac(String id) throws SQLException {
        String sql;
        Date fecha = null;
        sql = "SELECT fechanacimiento from persona where idpersona= '" + id + "'";
        ResultSet rs = con.consulta(sql);
        while (rs.next()) {
            fecha = rs.getDate("fechanacimiento");
        }
        return fecha;
    }

    public int calcularEdad(String fecha) {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate fechaNaci = LocalDate.parse(fecha, formato);
        LocalDate fechaActual = LocalDate.now();
        Period periodo = Period.between(fechaNaci, fechaActual);
        return periodo.getYears();
    }

    /*private BufferedImage imgBimage(Image img) {
        //Compruebo que no ya un buferrimage
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }
        BufferedImage bi = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D bGR = bi.createGraphics();
        bGR.drawImage(img, 0, 0, null);
        bGR.dispose();
        return bi;
    }*/

 /* private Image obtenerImagen(byte[] bytes) throws IOException {

        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        Iterator it = ImageIO.getImageReadersByFormatName("png");
        ImageReader reader = (ImageReader) it.next();
        Object source = bis;
        ImageInputStream iis = ImageIO.createImageInputStream(source);
        reader.setInput(iis, true);
        ImageReadParam param = reader.getDefaultReadParam();
        param.setSourceSubsampling(1, 1, 0, 0);
        return reader.read(0, param);

    }*/
}
