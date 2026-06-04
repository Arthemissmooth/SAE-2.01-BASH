package fr.iut.robotmineur;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Monde {

    private Secteur[][] secteurs;
    private List<Robot> robots;
    private List<Mine> mines;
    private List<Entrepot> entrepots;
    private int tourActuel;

    public Monde() {
        this.secteurs = new Secteur[10][10];
        this.robots = new ArrayList<>();
        this.mines = new ArrayList<>();
        this.entrepots = new ArrayList<>();
        this.tourActuel = 1;

        initialiserSecteurs();
    }

    private void initialiserSecteurs() {
        for (int ligne = 0; ligne < 10; ligne++) {
            for (int colonne = 0; colonne < 10; colonne++) {
                Position position = new Position(ligne, colonne);
                secteurs[ligne][colonne] = new Secteur(position, TypeSecteur.TERRAIN);
            }
        }
    }

    public void initialiserAleatoirement(String nomRobotOr, String nomRobotNickel) {
        Random random = new Random();

        int nbEau = random.nextInt(11);

        for (int i = 0; i < nbEau; i++) {
            ajouterEau(positionLibrePourEau(random));
        }

        int idMine = 1;

        int nbMinesOr = 1 + random.nextInt(2);
        int nbMinesNickel = 1 + random.nextInt(2);

        for (int i = 0; i < nbMinesOr; i++) {
            int quantite = 50 + random.nextInt(51);

            Mine mine = new Mine(
                    idMine,
                    positionLibrePourMineOuEntrepot(random),
                    TypeMinerai.OR,
                    quantite,
                    quantite
            );

            ajouterMine(mine);
            idMine++;
        }

        for (int i = 0; i < nbMinesNickel; i++) {
            int quantite = 50 + random.nextInt(51);

            Mine mine = new Mine(
                    idMine,
                    positionLibrePourMineOuEntrepot(random),
                    TypeMinerai.NICKEL,
                    quantite,
                    quantite
            );

            ajouterMine(mine);
            idMine++;
        }

        ajouterEntrepot(new Entrepot(
                1,
                positionLibrePourMineOuEntrepot(random),
                TypeMinerai.OR
        ));

        ajouterEntrepot(new Entrepot(
                2,
                positionLibrePourMineOuEntrepot(random),
                TypeMinerai.NICKEL
        ));

        Robot robotOr = new RobotOr(
                1,
                nomRobotOr,
                positionLibrePourRobot(random),
                5 + random.nextInt(5),
                1 + random.nextInt(3)
        );

        Robot robotNickel = new RobotNickel(
                2,
                nomRobotNickel,
                positionLibrePourRobot(random),
                5 + random.nextInt(5),
                1 + random.nextInt(3)
        );

        ajouterRobot(robotOr);
        ajouterRobot(robotNickel);

        int nbRobots = 2 + random.nextInt(2);

        if (nbRobots == 3) {

            int typeRobot = random.nextInt(2);

            if (typeRobot == 0) {
                Robot robotOr2 = new RobotOr(
                        3,
                        nomRobotOr + " 2",
                        positionLibrePourRobot(random),
                        5 + random.nextInt(5),
                        1 + random.nextInt(3)
                );

                ajouterRobot(robotOr2);
            } else {
                Robot robotNickel2 = new RobotNickel(
                        3,
                        nomRobotNickel + " 2",
                        positionLibrePourRobot(random),
                        5 + random.nextInt(5),
                        1 + random.nextInt(3)
                );

                ajouterRobot(robotNickel2);
            }
        }
    }

    private Position positionLibrePourEau(Random random) {
        Position position;

        do {
            position = new Position(random.nextInt(10), random.nextInt(10));
        } while (!positionDisponiblePourEau(position));

        return position;
    }

    private Position positionLibrePourMineOuEntrepot(Random random) {
        Position position;

        do {
            position = new Position(random.nextInt(10), random.nextInt(10));
        } while (!positionDisponiblePourMineOuEntrepot(position));

        return position;
    }

    private Position positionLibrePourRobot(Random random) {
        Position position;

        do {
            position = new Position(random.nextInt(10), random.nextInt(10));
        } while (!positionDisponiblePourRobot(position));

        return position;
    }

    public Secteur getSecteur(Position position) {
        return secteurs[position.getLigne()][position.getColonne()];
    }

    public boolean positionValide(Position position) {
        return position.getLigne() >= 0
                && position.getLigne() < 10
                && position.getColonne() >= 0
                && position.getColonne() < 10;
    }

    public boolean positionDisponiblePourEau(Position position) {
        return positionValide(position)
                && getSecteur(position).estVideTotalement();
    }

    public boolean positionDisponiblePourMineOuEntrepot(Position position) {
        return positionValide(position)
                && getSecteur(position).estLibrePourMineOuEntrepot();
    }

    public boolean positionDisponiblePourRobot(Position position) {
        return positionValide(position)
                && getSecteur(position).estLibrePourRobot();
    }

    public void ajouterEau(Position position) {
        if (!positionDisponiblePourEau(position)) {
            throw new IllegalArgumentException("Position invalide ou déjà occupée pour l'eau.");
        }

        secteurs[position.getLigne()][position.getColonne()] =
                new Secteur(position, TypeSecteur.EAU);
    }

    public void ajouterMine(Mine mine) {
        if (!positionDisponiblePourMineOuEntrepot(mine.getPosition())) {
            throw new IllegalArgumentException("Position invalide ou déjà occupée pour la mine.");
        }

        mines.add(mine);
        getSecteur(mine.getPosition()).placerMine(mine);
    }

    public void ajouterEntrepot(Entrepot entrepot) {
        if (!positionDisponiblePourMineOuEntrepot(entrepot.getPosition())) {
            throw new IllegalArgumentException("Position invalide ou déjà occupée pour l'entrepôt.");
        }

        entrepots.add(entrepot);
        getSecteur(entrepot.getPosition()).placerEntrepot(entrepot);
    }

    public void ajouterRobot(Robot robot) {
        if (!positionDisponiblePourRobot(robot.getPosition())) {
            throw new IllegalArgumentException("Position invalide, eau ou robot déjà présent.");
        }

        robots.add(robot);
        getSecteur(robot.getPosition()).placerRobot(robot);
    }

    public boolean deplacerRobot(Robot robot, Direction direction) {
        Position anciennePosition = robot.getPosition();
        Position nouvellePosition = anciennePosition.deplacer(direction);

        if (!positionDisponiblePourRobot(nouvellePosition)) {
            return false;
        }

        getSecteur(anciennePosition).retirerRobot();
        robot.setPosition(nouvellePosition);
        getSecteur(nouvellePosition).placerRobot(robot);

        return true;
    }

    public void tourSuivant() {
        tourActuel++;
    }

    public List<Robot> getRobots() {
        return robots;
    }

    public List<Mine> getMines() {
        return mines;
    }

    public List<Entrepot> getEntrepots() {
        return entrepots;
    }

    public int getTourActuel() {
        return tourActuel;
    }
}