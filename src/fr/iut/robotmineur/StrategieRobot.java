package fr.iut.robotmineur;

public class StrategieRobot {


    public Position trouverObjectif(Robot robot, Monde monde) {

        // 1. Robot plein : déposer immédiatement
        if (robot.estPlein()) {
            Entrepot entrepot = trouverEntrepot(robot, monde);
            if (entrepot != null) {
                return entrepot.getPosition();
            }
            return null;
        }

        Mine mine = trouverMine(robot, monde);

        // 2. Aucune mine disponible : si le robot porte quelque chose, déposer
        if (mine == null) {
            if (!robot.estVide()) {
                Entrepot entrepot = trouverEntrepot(robot, monde);
                if (entrepot != null) {
                    return entrepot.getPosition();
                }
            }
            return null;
        }

        // 3. Robot partiellement chargé : comparer le coût "miner d'abord" vs "déposer d'abord"
        if (!robot.estVide()) {
            Entrepot entrepot = trouverEntrepot(robot, monde);
            if (entrepot != null) {
                int coutDeposerDabord = calculerDistance(robot.getPosition(), entrepot.getPosition());
                int coutMinerDabord   = calculerDistance(robot.getPosition(), mine.getPosition());

                // Si déposer est plus court ou aussi court, on dépose d'abord
                if (coutDeposerDabord <= coutMinerDabord) {
                    return entrepot.getPosition();
                }
            }
        }

        // 4. Aller miner
        return mine.getPosition();
    }

    public Mine trouverMine(Robot robot, Monde monde) {

        Mine meilleureMine = null;
        int meilleurScore  = Integer.MAX_VALUE;

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

            int distanceMineEntrepot = (entrepot != null)
                    ? calculerDistance(mine.getPosition(), entrepot.getPosition())
                    : 0;

            // Bonus : favoriser les mines avec beaucoup de minerai restant
            int ressourcesRestantes = mine.getQuantiteActuelle();
            int score = distanceRobotMine + distanceMineEntrepot - (ressourcesRestantes / 10);

            if (score < meilleurScore) {
                meilleurScore  = score;
                meilleureMine  = mine;
            }
        }

        return meilleureMine;
    }

    public Entrepot trouverEntrepot(Robot robot, Monde monde) {

        Entrepot meilleur      = null;
        int      distanceMin   = Integer.MAX_VALUE;

        for (Entrepot entrepot : monde.getEntrepots()) {

            if (entrepot.getTypeMinerai() != robot.getTypeMinerai()) {
                continue;
            }

            // S'il y a plusieurs entrepôts du même type, prendre le plus proche
            int distance = calculerDistance(robot.getPosition(), entrepot.getPosition());
            if (distance < distanceMin) {
                distanceMin = distance;
                meilleur    = entrepot;
            }
        }

        return meilleur;
    }

    private int calculerDistance(Position a, Position b) {
        return Math.abs(a.getLigne() - b.getLigne())
                + Math.abs(a.getColonne() - b.getColonne());
    }
}