package edu.cerp.checkin;

import edu.cerp.checkin.console.MainConsole;
import edu.cerp.checkin.logic.SesionService;
import edu.cerp.checkin.ui.CheckInGUI;

public class App {
    public static void main(String[] args) {
        //para usar gui=true, para usar consola = false
        boolean usarGui = true;
        
        // Verificar argumentos para GUI
        for (String arg : args) {
            if ("--gui".equalsIgnoreCase(arg)) {
                usarGui = true;
                break;
            }
        }

        SesionService service = new SesionService();
        service.cargarDatosDemo(); // Solo carga demo si no hay datos existentes

        if (usarGui) {
            CheckInGUI.show(service);
        } else {
            MainConsole.run(service);
        }
    }
}