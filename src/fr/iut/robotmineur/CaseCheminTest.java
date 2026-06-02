package fr.iut.robotmineur;


import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;


class CaseCheminTest {


    @Test
    void testCalculF() {
        CaseChemin caseChemin = new CaseChemin(new Position(0, 0));


        caseChemin.setG(3);
        caseChemin.setH(4);


        assertEquals(7, caseChemin.getF());
    }


    @Test
    void testParent() {
        CaseChemin depart = new CaseChemin(new Position(0, 0));
        CaseChemin suivante = new CaseChemin(new Position(1, 0));


        suivante.setParent(depart);


        assertEquals(depart, suivante.getParent());
    }
}
