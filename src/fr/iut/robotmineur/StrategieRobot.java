package fr.iut.robotmineur;

public class StrategieRobot {

    public Position trouverObjectif(Robot robot, Monde monde) {

        Mine mine = trouverMine(robot, monde);

        if (robot.estPlein()) {
            Entrepot entrepot = trouverEntrepot(robot, monde);
            if (entrepot != null) {
                return entrepot.getPosition();
            }
        }

        if (mine != null) {
            return mine.getPosition();
        }

        if (!robot.estVide()) {
            Entrepot entrepot = trouverEntrepot(robot, monde);
            if (entrepot != null) {
                return entrepot.getPosition();
            }
        }

        return null;
    }

    public Mine trouverMine(Robot robot, Monde monde) {

        Mine meilleureMine = null;
        int meilleurScore = Integer.MAX_VALUE;

        Entrepot entrepot = trouverEntrepot(robot, monde);

        for (Mine mine : monde.getMines()) {

            if (mine.getTypeMinerai() != robot.getTypeMinerai()) {
                continue;
            }

            if (mine.estVide()) {
                continue;
            }

            int distanceRobotMine = calculerDistance(
                    robot.getPosition(),
                    mine.getPosition()
            );

            int distanceMineEntrepot = 0;

            if (entrepot != null) {
                distanceMineEntrepot = calculerDistance(
                        mine.getPosition(),
                        entrepot.getPosition()
                );
            }

            int score = distanceRobotMine + distanceMineEntrepot;

            if (score < meilleurScore) {
                meilleurScore = score;
                meilleureMine = mine;
            }
        }

        return meilleureMine;
    }


    public Entrepot trouverEntrepot(Robot robot, Monde monde) {

        for (Entrepot entrepot : monde.getEntrepots()) {
            if (entrepot.getTypeMinerai() == robot.getTypeMinerai()) {
                return entrepot;
            }
        }

        return null;
    }

    private int calculerDistance(Position a, Position b) {
        return Math.abs(a.getLigne() - b.getLigne())
                + Math.abs(a.getColonne() - b.getColonne());
    }
}


