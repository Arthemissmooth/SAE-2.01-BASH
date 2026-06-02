package fr.iut.robotmineur;


import javax.swing.SwingUtilities;
import java.util.Scanner;


public class MainGraphiqueAutomatique {


    public static void main(String[] args) {


        Scanner scanner = new Scanner(System.in);


        Monde monde = new Monde();


        System.out.println("Bienvenue dans la simulation graphique automatique");


        System.out.println("Nom du robot OR :");
        String nomRobotOr = scanner.nextLine();


        System.out.println("Nom du robot NICKEL :");
        String nomRobotNickel = scanner.nextLine();


        monde.initialiserAleatoirement(nomRobotOr, nomRobotNickel);


        SwingUtilities.invokeLater(() -> {
            FenetreSimulationAutomatique fenetre = new FenetreSimulationAutomatique(monde);
            fenetre.setVisible(true);
        });
    }
}
