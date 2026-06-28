package controlador;

import vista.MenuPrincipalVista;
import vista.BrilloContrasteVista;
import vista.HistogramaVista;
import vista.EspaciosColorVista;
import vista.UmbralesVista;
import vista.AritmeticaLogicaVista;
import vista.HistoOperacionesVista;
import vista.EcualizacionPDFVista;
import vista.ConvolucionVista;
import vista.FiltrosNoLinealesVista;
import vista.MorfologiaBinariaVista;
import vista.MorfologiaGrisVista;
import vista.FourierVista;
import vista.RuidoInteractivoVista;

import modelo.BrilloContrasteModelo;
import modelo.HistogramaModelo;
import modelo.EspaciosColorModelo;
import modelo.UmbralesModelo;
import modelo.AritmeticaLogicaModelo;
import modelo.HistoOperacionesModelo;
import modelo.EcualizacionPDFModelo;
import modelo.ConvolucionModelo;
import modelo.FiltrosNoLinealesModelo;
import modelo.MorfologiaBinariaModelo;
import modelo.MorfologiaGrisModelo;
import modelo.FourierModelo;
import modelo.RuidoInteractivoModelo;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MenuPrincipalControlador {
    private final MenuPrincipalVista menu;

    public MenuPrincipalControlador(MenuPrincipalVista menu) {
        this.menu = menu;
        init();
    }

    private void init() {
        menu.botones[0].addActionListener(e -> abrir(new BrilloContrasteVista(),
            () -> new BrilloContrasteControlador(new BrilloContrasteModelo(), (BrilloContrasteVista) getLastOpened())));

        menu.botones[1].addActionListener(e -> abrir(new HistogramaVista(),
            () -> new HistogramaControlador(new HistogramaModelo(), (HistogramaVista) getLastOpened())));

        menu.botones[2].addActionListener(e -> abrir(new EspaciosColorVista(),
            () -> new EspaciosColorControlador(new EspaciosColorModelo(), (EspaciosColorVista) getLastOpened())));

        menu.botones[3].addActionListener(e -> abrir(new UmbralesVista(),
            () -> new UmbralesControlador(new UmbralesModelo(), (UmbralesVista) getLastOpened())));

        menu.botones[4].addActionListener(e -> abrir(new AritmeticaLogicaVista(),
            () -> new AritmeticaLogicaControlador(new AritmeticaLogicaModelo(), (AritmeticaLogicaVista) getLastOpened())));

        menu.botones[5].addActionListener(e -> abrir(new HistoOperacionesVista(),
            () -> new HistoOperacionesControlador(new HistoOperacionesModelo(), (HistoOperacionesVista) getLastOpened())));

        menu.botones[6].addActionListener(e -> abrir(new EcualizacionPDFVista(),
            () -> new EcualizacionPDFControlador(new EcualizacionPDFModelo(), (EcualizacionPDFVista) getLastOpened())));

        menu.botones[7].addActionListener(e -> abrir(new ConvolucionVista(),
            () -> new ConvolucionControlador(new ConvolucionModelo(), (ConvolucionVista) getLastOpened())));

        menu.botones[8].addActionListener(e -> abrir(new FiltrosNoLinealesVista(),
            () -> new FiltrosNoLinealesControlador(new FiltrosNoLinealesModelo(), (FiltrosNoLinealesVista) getLastOpened())));

        menu.botones[9].addActionListener(e -> abrir(new MorfologiaBinariaVista(),
            () -> new MorfologiaBinariaControlador(new MorfologiaBinariaModelo(), (MorfologiaBinariaVista) getLastOpened())));

        menu.botones[10].addActionListener(e -> abrir(new MorfologiaGrisVista(),
            () -> new MorfologiaGrisControlador(new MorfologiaGrisModelo(), (MorfologiaGrisVista) getLastOpened())));

        menu.botones[11].addActionListener(e -> abrir(new FourierVista(),
            () -> new FourierControlador(new FourierModelo(), (FourierVista) getLastOpened())));

        menu.botones[12].addActionListener(e -> abrir(new RuidoInteractivoVista(),
            () -> new RuidoInteractivoControlador(new RuidoInteractivoModelo(), (RuidoInteractivoVista) getLastOpened())));

        menu.btnSalir.addActionListener(e -> System.exit(0));
    }

    private JFrame lastOpened;

    private JFrame getLastOpened() {
        return lastOpened;
    }

    private void abrir(JFrame ventana, Runnable initController) {
        lastOpened = ventana;
        ventana.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                menu.setVisible(true);
            }
        });
        menu.setVisible(false);
        ventana.setVisible(true);
        initController.run();
    }
}
