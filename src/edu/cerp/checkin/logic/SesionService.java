package edu.cerp.checkin.logic;

import edu.cerp.checkin.model.Inscripcion;
import edu.cerp.checkin.persistencia.ArchivoManager;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/** Lógica mínima en memoria (sin validaciones complejas). */
public class SesionService {
    private final List<Inscripcion> inscripciones = new ArrayList<>();
    private final ArchivoManager archivoManager;

    public SesionService() {
        this.archivoManager = new ArchivoManager();
        // Cargar datos existentes al iniciar
        this.inscripciones.addAll(archivoManager.cargarInscripciones());
    }

    public void registrar(String nombre, String documento, String curso) {
        if (nombre == null || nombre.isBlank()) nombre = "(sin nombre)";
        if (documento == null) documento = "";
        if (curso == null || curso.isBlank()) curso = "Prog 1";
        
        Inscripcion nuevaInscripcion = new Inscripcion(
            nombre.trim(), 
            documento.trim(), 
            curso.trim(), 
            LocalDateTime.now()
        );
        inscripciones.add(nuevaInscripcion);
        
        // Guardar automáticamente después de cada registro
        archivoManager.guardarInscripciones(inscripciones);
        
        System.out.println("✓ Inscripción registrada y guardada: " + nombre.trim());
    }

    public List<Inscripcion> listar() { 
        return Collections.unmodifiableList(inscripciones); 
    }

    public List<Inscripcion> buscar(String q) {
        if (q == null || q.isBlank()) return listar();
        String s = q.toLowerCase();
        return inscripciones.stream()
                .filter(i -> i.getNombre().toLowerCase().contains(s) || i.getDocumento().toLowerCase().contains(s))
                .collect(Collectors.toList());
    }

    public String resumen() {
        Map<String, Long> porCurso = inscripciones.stream()
                .collect(Collectors.groupingBy(Inscripcion::getCurso, LinkedHashMap::new, Collectors.counting()));
        StringBuilder sb = new StringBuilder();
        sb.append("Total: ").append(inscripciones.size()).append("\nPor curso:\n");
        for (var e : porCurso.entrySet()) sb.append(" - ").append(e.getKey()).append(": ").append(e.getValue()).append("\n");
        return sb.toString();
    }

    /** Datos de prueba para arrancar la app (solo si no hay datos existentes) */
    public void cargarDatosDemo() {
        // Solo cargar demo si no hay inscripciones guardadas
        if (inscripciones.isEmpty()) {
            registrar("Ana Pérez", "51234567", "Prog 2");
            registrar("Luis Gómez", "49887766", "Prog 1");
            registrar("Camila Díaz", "53422110", "Base de Datos");
            System.out.println("✓ Datos demo cargados (sistema nuevo)");
        } else {
            System.out.println("✓ Se encontraron " + inscripciones.size() + " inscripciones existentes");
        }
    }
}