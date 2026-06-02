package fr.iut.robotmineur;

public class
MainAutomatique {

    public static void main(String[] args) {

        Monde monde = new Monde();

        monde.initialiserAleatoirement("RobotOr", "RobotNickel");

        AffichageConsole affichage = new AffichageConsole();
        SimulationAutomatique simulation = new SimulationAutomatique(monde);

        for (int i = 0; i < 30; i++) {

            affichage.afficherMonde(monde);

            simulation.jouerTour();

            System.out.println("Appuyez sur Entrée pour continuer...");
            new java.util.Scanner(System.in).nextLine();
        }

        System.out.println("Fin de la simulation automatique.");
    }
}
