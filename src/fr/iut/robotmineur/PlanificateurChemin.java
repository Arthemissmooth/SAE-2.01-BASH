package fr.iut.robotmineur;

import java.util.ArrayList;
import java.util.List;

public class PlanificateurChemin {

    public List<Position> calculerChemin(Monde monde, Position depart, Position arrivee) {

        List<CaseChemin> ouverts = new ArrayList<>();
        List<CaseChemin> fermes = new ArrayList<>();

        CaseChemin caseDepart = new CaseChemin(depart);
        caseDepart.setG(0);
        caseDepart.setH(calculerHeuristique(depart, arrivee));

        ouverts.add(caseDepart);

        while (!ouverts.isEmpty()) {

            CaseChemin courant = trouverMeilleureCase(ouverts);

            //Si la case courante est l'arrivée, on a trouvé le chemin.
            if (memePosition(courant.getPosition(), arrivee)) {
                return reconstruireChemin(courant);  //On remonte les parents pour le reconstruire et on retourne le résultat.
            }

            ouverts.remove(courant);
            fermes.add(courant);

            for (Position voisin : getVoisins(courant.getPosition())) {

                if (!monde.positionValide(voisin)) {//Si le voisin est en dehors de la grille 10x10, on l'ignore.
                    continue;
                }

            // Si le voisin est une case eau ou occupée par un autre robot, on l'ignore
                if (!positionAccessible(monde, voisin, arrivee)) {
                    continue;
                }

                if (contientPosition(fermes, voisin)) {
                    continue;
                }

                int nouveauG = courant.getG() + 1;

                CaseChemin caseVoisine = trouverCase(ouverts, voisin);

                //Si le voisin n'est pas encore dans la liste ouverte, on le crée avec son g, h, et son parent, et on l'ajoute
                if (caseVoisine == null) {
                    caseVoisine = new CaseChemin(voisin);
                    caseVoisine.setParent(courant);
                    caseVoisine.setG(nouveauG);
                    caseVoisine.setH(calculerHeuristique(voisin, arrivee));
                    ouverts.add(caseVoisine);
                } else if (nouveauG < caseVoisine.getG()) {
                    caseVoisine.setParent(courant);
                    caseVoisine.setG(nouveauG);
                }
            }
        }

        return new ArrayList<>();
    }

    private int calculerHeuristique(Position a, Position b) {
        return Math.abs(a.getLigne() - b.getLigne())
                + Math.abs(a.getColonne() - b.getColonne());
    }

    private CaseChemin trouverMeilleureCase(List<CaseChemin> cases) {
        CaseChemin meilleure = cases.get(0);

        for (CaseChemin caseChemin : cases) {
            if (caseChemin.getF() < meilleure.getF()) {
                meilleure = caseChemin;
            }
        }

        return meilleure;
    }

    private List<Position> getVoisins(Position position) {

        List<Position> voisins = new ArrayList<>();

        voisins.add(position.deplacer(Direction.NORD));
        voisins.add(position.deplacer(Direction.SUD));
        voisins.add(position.deplacer(Direction.EST));
        voisins.add(position.deplacer(Direction.OUEST));
        return voisins;
    }

    private boolean positionAccessible(Monde monde, Position position, Position arrivee) {
        Secteur secteur = monde.getSecteur(position);

        if (secteur.estEau()) {
            return false;
        }

        if (secteur.getRobot() != null && !memePosition(position, arrivee)) {
            return false;
        }
        return true;
    }

    private boolean contientPosition(List<CaseChemin> liste, Position position) {
        return trouverCase(liste, position) != null;
    }

    private CaseChemin trouverCase(List<CaseChemin> liste, Position position) {
        for (CaseChemin caseChemin : liste) {
            if (memePosition(caseChemin.getPosition(), position)) {
                return caseChemin;
            }
        }
        return null;
    }

    private boolean memePosition(Position a, Position b) {
        return a.getLigne() == b.getLigne()
                && a.getColonne() == b.getColonne();
    }

//On part de l'arrivée et on remonte de parent en parent jusqu'au départ.
// Le chemin.add(0,...) insère chaque position au début de la liste
    private List<Position> reconstruireChemin(CaseChemin arrivee) {
        List<Position> chemin = new ArrayList<>();

        CaseChemin courant = arrivee;

        while (courant != null) {
            chemin.add(0, courant.getPosition());
            courant = courant.getParent();
        }
        return chemin;
    }
}