package com.gestionStock.ui.generic;

import com.gestionStock.dao.GenericDao;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public final class FabriqueTableau {

    private FabriqueTableau() {
    }

    public static class Colonne<T> {
        private final String nom;
        private final Function<T, Object> extracteurValeur;

        public Colonne(String nom, Function<T, Object> extracteurValeur) {
            this.nom = nom;
            this.extracteurValeur = extracteurValeur;
        }

        public static <T> Colonne<T> depuisGetter(String nomGetter, Function<T, Object> extracteurValeur) {
            return new Colonne<>(formaterNomGetter(nomGetter), extracteurValeur);
        }

        private static String formaterNomGetter(String nomGetter) {
            String nom = nomGetter;

            if (nom.startsWith("get") && nom.length() > 3) {
                nom = nom.substring(3);
            } else if (nom.startsWith("is") && nom.length() > 2) {
                nom = nom.substring(2);
            }

            nom = nom.replaceAll("([a-z])([A-Z])", "$1 $2");

            if (nom.equalsIgnoreCase("id")) {
                return "ID";
            }
            return nom;
        }

        public String getNom() {
            return nom;
        }

        public Object getValeur(T objetLigne) {
            return extracteurValeur.apply(objetLigne);
        }
    }
    
    public static <T> JTable creerTableau(List<T> donnees, List<Colonne<T>> colonnes) {
        AbstractTableModel modele = new AbstractTableModel() {
            @Override
            public int getRowCount() {
                return donnees.size();
            }

            @Override
            public int getColumnCount() {
                return colonnes.size();
            }

            @Override
            public String getColumnName(int indexColonne) {
                return colonnes.get(indexColonne).getNom();
            }

            @Override
            public Object getValueAt(int indexLigne, int indexColonne) {
                T objetLigne = donnees.get(indexLigne);
                Colonne<T> colonne = colonnes.get(indexColonne);
                return colonne.getValeur(objetLigne);
            }
        };
        return new JTable(modele);
    }

    public static <T> List<T> recupererDonneesDepuisDao(Class<T> classeModele) throws Exception {
        GenericDao<T> dao = new GenericDao<>(classeModele);
        return dao.findAll();
    }

    public static <T> List<Colonne<T>> recupererColonnesDepuisGetters(Class<T> classeModele, String... nomsGetters) {
        List<Colonne<T>> colonnes = new ArrayList<>();
        Set<String> gettersDemandes = new LinkedHashSet<>(Arrays.asList(nomsGetters));

        if (gettersDemandes.isEmpty()) {
            for (Method methode : classeModele.getMethods()) {
                if (estGetterValide(methode)) {
                    String nomGetter = methode.getName();
                    colonnes.add(creerColonneDepuisGetter(nomGetter));
                }
            }
            return colonnes;
        }

        for (String nomGetter : gettersDemandes) {
            colonnes.add(creerColonneDepuisGetter(nomGetter));
        }

        return colonnes;
    }

    public static <T> JTable creerDepuisDao(Class<T> classeModele, String... nomsGetters) throws Exception {
        List<T> donnees = recupererDonneesDepuisDao(classeModele);
        List<Colonne<T>> colonnes = recupererColonnesDepuisGetters(classeModele, nomsGetters);
        return creerTableau(donnees, colonnes);
    }

    public static <T> JTable creerDepuisDonneesEtGetters(List<T> donnees, Class<T> classeModele, String... nomsGetters) {
        List<Colonne<T>> colonnes = recupererColonnesDepuisGetters(classeModele, nomsGetters);
        return creerTableau(donnees, colonnes);
    }

    private static <T, D extends Comparable<? super D>> void trierParDateHeure(
            List<T> donnees,
            Function<T, D> extracteurDateHeure,
            boolean ordreCroissant
    ) {
        Comparator<T> comparateur = Comparator.comparing(
                extracteurDateHeure,
                Comparator.nullsLast(Comparator.naturalOrder())
        );

        if (!ordreCroissant) {
            comparateur = comparateur.reversed();
        }

        donnees.sort(comparateur);
    }

    public static <T, D extends Comparable<? super D>> JTable creerDepuisDaoTrieDateHeure(
            Class<T> classeModele,
            Function<T, D> extracteurDateHeure,
            boolean ordreCroissant,
            String... nomsGetters
    ) throws Exception {
        List<T> donnees = recupererDonneesDepuisDao(classeModele);
        trierParDateHeure(donnees, extracteurDateHeure, ordreCroissant);
        List<Colonne<T>> colonnes = recupererColonnesDepuisGetters(classeModele, nomsGetters);
        return creerTableau(donnees, colonnes);
    }

    private static <T> boolean estGetterValide(Method methode) {
        String nom = methode.getName();
        if (!Modifier.isPublic(methode.getModifiers())) {
            return false;
        }
        if (methode.getParameterCount() != 0) {
            return false;
        }
        if (methode.getReturnType() == void.class) {
            return false;
        }
        if ("getClass".equals(nom)) {
            return false;
        }
        return (nom.startsWith("get") && nom.length() > 3) || (nom.startsWith("is") && nom.length() > 2);
    }

    private static <T> Colonne<T> creerColonneDepuisGetter(String nomGetter) {
        return Colonne.depuisGetter(nomGetter, objetLigne -> {
            try {
                Method methode = objetLigne.getClass().getMethod(nomGetter);
                return methode.invoke(objetLigne);
            } catch (Exception e) {
                throw new IllegalStateException("Impossible d'appeler " + nomGetter, e);
            }
        });
    }
}
