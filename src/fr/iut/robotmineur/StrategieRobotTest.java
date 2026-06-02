package fr.iut.robotmineur;


import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;


class StrategieRobotTest {

    @Test
    void testTrouverEntrepotDuBonType() {
        Monde monde = new Monde();
        Robot robot = new RobotOr(
                1,
                "Robot OR",
                new Position(0, 0),
                5,
                2
        );
        Entrepot entrepotOr = new Entrepot(
                1,
                new Position(2, 2),
                TypeMinerai.OR
        );

        monde.ajouterRobot(robot);
        monde.ajouterEntrepot(entrepotOr);

        StrategieRobot strategie = new StrategieRobot();

        Entrepot resultat = strategie.trouverEntrepot(robot, monde);

        assertEquals(entrepotOr, resultat);
    }


    @Test
    void testTrouverMineLaPlusProcheDuBonType() {
        Monde monde = new Monde();


        Robot robot = new RobotOr(
                1,
                "Robot OR",
                new Position(0, 0),
                5,
                2
        );


        Mine mineOrLoin = new Mine(
                1,
                new Position(8, 8),
                TypeMinerai.OR,
                80,
                80
        );


        Mine mineOrProche = new Mine(
                2,
                new Position(1, 0),
                TypeMinerai.OR,
                80,
                80
        );


        Mine mineNickelProche = new Mine(
                3,
                new Position(0, 1),
                TypeMinerai.NICKEL,
                80,
                80
        );


        monde.ajouterRobot(robot);
        monde.ajouterMine(mineOrLoin);
        monde.ajouterMine(mineOrProche);
        monde.ajouterMine(mineNickelProche);


        StrategieRobot strategie = new StrategieRobot();


        Mine resultat = strategie.trouverMine(robot, monde);


        assertEquals(mineOrProche, resultat);
    }
}
