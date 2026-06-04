package fr.iut.robotmineur;

public class MondTest {

    public static Monde creerMondTestRobotsOr() {

        Monde monde = new Monde();

        Mine mineOr = new Mine(
                1,
                new Position(9, 0),
                TypeMinerai.OR,
                30,
                30
        );

        Entrepot entrepotOr = new Entrepot(
                1,
                new Position(9, 9),
                TypeMinerai.OR
        );

        Robot robotProche = new RobotOr(
                1,
                "proche",
                new Position(8, 0),
                5,
                2
        );

        Robot robotLoin = new RobotOr(
                2,
                " ",
                new Position(9,1 ),
                5,
                2
        );

        monde.ajouterMine(mineOr);
        monde.ajouterEntrepot(entrepotOr);
        monde.ajouterRobot(robotProche);
        monde.ajouterRobot(robotLoin);

        return monde;
    }
}
