package fr.iut.robotmineur;

public class MainAutomatique {

    public static void main(String[] args) {

        Monde monde = new Monde();

        monde.initialiserAleatoirement("RobotOr", "RobotNickel");

        AffichageConsole affichage = new AffichageConsole();
        SimulationAutomatique simulation = new SimulationAutomatique(monde);

        while (!simulation.estTerminee()) {

            affichage.afficherMonde(monde);

            simulation.jouerTour();

            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        affichage.afficherMonde(monde);

        System.out.println();
        System.out.println("Simulation terminée !");
        System.out.println("Toutes les mines sont vides.");
    }
}
