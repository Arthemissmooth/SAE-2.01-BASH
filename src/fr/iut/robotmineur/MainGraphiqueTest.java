package fr.iut.robotmineur;

import javax.swing.SwingUtilities;

public class MainGraphiqueTest {

    public static void main(String[] args) {

        Monde monde = MondTest.creerMondTestRobotsOr();



        SwingUtilities.invokeLater(() -> {
            FenetreSimulationAutomatique fenetre =
                    new FenetreSimulationAutomatique(monde);


            fenetre.setVisible(true);
        });
    }
}