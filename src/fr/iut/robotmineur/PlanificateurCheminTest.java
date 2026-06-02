package fr.iut.robotmineur;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlanificateurCheminTest {

    @Test
    void testCheminSimpleSansObstacle() {
        Monde monde = new Monde();
        PlanificateurChemin planificateur = new PlanificateurChemin();

        Position depart = new Position(0, 0);
        Position arrivee = new Position(0, 3);

        List<Position> chemin = planificateur.calculerChemin(
                monde,
                depart,
                arrivee
        );

        assertFalse(chemin.isEmpty());
        assertEquals(0, chemin.get(0).getLigne());
        assertEquals(0, chemin.get(0).getColonne());

        Position dernierePosition = chemin.get(chemin.size() - 1);

        assertEquals(0, dernierePosition.getLigne());
        assertEquals(3, dernierePosition.getColonne());
    }
}