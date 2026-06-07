package fr.iut.robotmineur;

import java.util.List;


public class SimulationAutomatique {

    private static final int TAILLE_GRILLE = 10;
    private Monde monde;
    private PlanificateurChemin planificateurChemin;
    private StrategieRobot strategieRobot;


    public SimulationAutomatique(Monde monde) {
        this.monde = monde;
        this.planificateurChemin = new PlanificateurChemin();
        this.strategieRobot = new StrategieRobot();
    }
    public void jouerTour() {
        if (estTerminee()) {
            return;
        }

        for (Robot robot : monde.getRobots()) {
            jouerRobot(robot);
        }


        monde.tourSuivant();
    }

    private void jouerRobot(Robot robot) {
        Secteur secteurActuel = monde.getSecteur(robot.getPosition());


        // 1. Sur une mine et pas plein → récolter
        if (secteurActuel.getMine() != null && !robot.estPlein()) {
            boolean recolte = robot.recolter(secteurActuel.getMine());
            if (recolte) {
                return;
            }
        }


        // 2. Sur un entrepôt et pas vide → déposer, puis s'éloigner
        if (secteurActuel.getEntrepot() != null && !robot.estVide()) {
            boolean depot = robot.deposer(secteurActuel.getEntrepot());


            if (depot) {
                eloignerDuDepot(robot, secteurActuel.getEntrepot().getPosition());
                return;
            }
        }


        // 3. Chercher le prochain objectif via la stratégie
        Position objectif = strategieRobot.trouverObjectif(robot, monde);
        if (objectif == null) {
            return;
        }


        // 4. Calculer le chemin et avancer d'un pas
        avancerVers(robot, objectif);
    }

    private void avancerVers(Robot robot, Position cible) {
        List<Position> chemin = planificateurChemin.calculerChemin(
                monde,
                robot.getPosition(),
                cible
        );


        if (chemin.size() < 2) {
            return;
        }

        Position prochainePosition = chemin.get(1);
        Direction direction = trouverDirection(robot.getPosition(), prochainePosition);


        if (direction != null) {
            monde.deplacerRobot(robot, direction);
        }
    }

    private void eloignerDuDepot(Robot robot, Position posDepot) {
        Position sortie = trouverCaseEloignee(robot.getPosition(), posDepot);


        if (sortie != null) {
            Direction direction = trouverDirection(robot.getPosition(), sortie);
            if (direction != null) {
                monde.deplacerRobot(robot, direction);
            }
        }
    }
    private Position trouverCaseEloignee(Position posRobot, Position posDepot) {
        Position meilleureCible = null;
        int distanceMax = -1;


        for (int ligne = 0; ligne < TAILLE_GRILLE; ligne++) {
            for (int col = 0; col < TAILLE_GRILLE; col++) {
                Position candidate = new Position(ligne, col);


                // Ignorer la case actuelle du robot
                if (candidate.equals(posRobot)) {
                    continue;
                }


                // Utiliser la méthode de Monde pour vérifier l'accessibilité
                if (!monde.positionDisponiblePourRobot(candidate)) {
                    continue;
                }


                int distance = distanceManhattan(candidate, posDepot);
                if (distance > distanceMax) {
                    distanceMax = distance;
                    meilleureCible = candidate;
                }
            }
        }


        if (meilleureCible == null) {
            return null;
        }


        // On ne se déplace que d'un pas : on retourne la case suivante du chemin
        List<Position> chemin = planificateurChemin.calculerChemin(monde, posRobot, meilleureCible);
        if (chemin.size() >= 2) {
            return chemin.get(1);
        }


        return null;
    }


    public boolean estTerminee() {
        for (Mine mine : monde.getMines()) {
            if (!mine.estVide()) {
                return false;
            }
        }


        for (Robot robot : monde.getRobots()) {
            if (!robot.estVide()) {
                return false;
            }
        }


        return true;
    }

    private int distanceManhattan(Position a, Position b) {
        return Math.abs(a.getLigne() - b.getLigne())
                + Math.abs(a.getColonne() - b.getColonne());
    }

    private Direction trouverDirection(Position depart, Position arrivee) {
        if (arrivee.getLigne() == depart.getLigne() - 1) return Direction.NORD;
        if (arrivee.getLigne() == depart.getLigne() + 1) return Direction.SUD;
        if (arrivee.getColonne() == depart.getColonne() + 1) return Direction.EST;
        if (arrivee.getColonne() == depart.getColonne() - 1) return Direction.OUEST;
        return null;
    }
}
