package edu.cerp.checkin.persistencia;

import edu.cerp.checkin.model.Inscripcion;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ArchivoManager {
    private static final String ARCHIVO = "data/inscripciones.csv";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void guardarInscripciones(List<Inscripcion> inscripciones) {
        try {
            // Crear directorio si no existe
            new File("data").mkdirs();
            
            try (PrintWriter writer = new PrintWriter(new FileWriter(ARCHIVO))) {
                // Escribir encabezado
                writer.println("nombre|documento|curso|fechaHora");
                
                // Escribir datos
                for (Inscripcion inscripcion : inscripciones) {
                    writer.printf("%s|%s|%s|%s%n",
                        escapeCSV(inscripcion.getNombre()),
                        escapeCSV(inscripcion.getDocumento()),
                        escapeCSV(inscripcion.getCurso()),
                        inscripcion.getFechaHora().format(formatter));
                }
            }
        } catch (IOException e) {
            System.err.println("Error al guardar inscripciones: " + e.getMessage());
        }
    }

    public List<Inscripcion> cargarInscripciones() {
        List<Inscripcion> inscripciones = new ArrayList<>();
        File archivo = new File(ARCHIVO);
        
        if (!archivo.exists()) {
            return inscripciones; // Retorna lista vacía si no existe el archivo
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(ARCHIVO))) {
            String linea;
            boolean primeraLinea = true;
            
            while ((linea = reader.readLine()) != null) {
                if (primeraLinea) {
                    primeraLinea = false;
                    continue; // Saltar encabezado
                }
                
                String[] partes = linea.split("\\|", -1); // -1 para mantener campos vacíos
                if (partes.length == 4) {
                    String nombre = unescapeCSV(partes[0]);
                    String documento = unescapeCSV(partes[1]);
                    String curso = unescapeCSV(partes[2]);
                    LocalDateTime fechaHora = LocalDateTime.parse(partes[3], formatter);
                    
                    Inscripcion inscripcion = new Inscripcion(nombre, documento, curso, fechaHora);
                    inscripciones.add(inscripcion);
                }
            }
        } catch (IOException e) {
            System.err.println("Error al cargar inscripciones: " + e.getMessage());
        }
        
        return inscripciones;
    }

    private String escapeCSV(String campo) {
        if (campo == null) return "";
        // Escapar pipes y saltos de línea
        return campo.replace("|", "\\|").replace("\n", "\\n").replace("\r", "\\r");
    }

    private String unescapeCSV(String campo) {
        if (campo == null) return "";
        // Deshacer escape
        return campo.replace("\\|", "|").replace("\\n", "\n").replace("\\r", "\r");
    }
}
