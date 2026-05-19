```text
Table

Article :
    id
    libelle

Methode :
    id
    libelle
        - FIFO
        - LIFO
        - CUMP

Mouvement :
    id
    article_id
    methode_id
    date_mouvement
    type_mouvement
        - ENTREE
        - SORTIE

    qte
    PU
    valeur

    stock_apres
    valeur_stock_apres
    CUMP_apres

Mouvement_source :
    id

    mouvement_sortie_id
    mouvement_entree_id

    qte_utilisee
    PU_utilise
    valeur_utilisee



Metier (Application generalisee)

    formule de mouvement pour entree d'article

        date_mouvement = input

        qte = input

        PU = input

        valeur = qte * PU

        stock_apres = dernier_stock + qte

        valeur_stock_apres = derniere_valeur_stock + valeur

        CUMP_apres = valeur_stock_apres / stock_apres

        type_mouvement = ENTREE


    formule de mouvement pour sortie d'article

        date_mouvement = input

        qte = input

        PU = calcule automatiquement selon la methode FIFO / LIFO / CUMP

        valeur = qte * PU

        stock_apres = dernier_stock - qte

        valeur_stock_apres = derniere_valeur_stock - valeur

        CUMP_apres = dernier_CUMP_de_l_article

        type_mouvement = SORTIE


        puis insertion dans Mouvement_source pour indiquer de quelles entrees provient la sortie


        Exemple :

            Entree 1 : 5 BEX a 1000

            Entree 2 : 10 BEX a 1500

            Sortie : 8 BEX


        Mouvement_source :

            mouvement_sortie_id = 3
            mouvement_entree_id = 1
            qte_utilisee = 5

            mouvement_sortie_id = 3
            mouvement_entree_id = 2
            qte_utilisee = 3


        Donc :

            la sortie de 8 BEX provient :
                - de 5 BEX de l'entree 1
                - de 3 BEX de l'entree 2



UI


    Fenetre de saisie d'article / produit :

        champs :

            libelle produit

            methode :
                - FIFO
                - LIFO
                - CUMP



    Fenetre de saisie de mouvement de stock :

        champs :

            produit

            type_mouvement

            qte

            prix_unitaire


        regles :

            si ENTREE :
                prix_unitaire saisi manuellement

            si SORTIE :
                prix_unitaire calcule automatiquement
                selon la methode choisie



    Fenetre Etat de stock global (date)

        objectif :

            afficher le stock global
            a une date donnee


        colonnes :

            produit

            quantite en stock

            valeur du stock

            CUMP

            methode

            date dernier mouvement


        exemple :

            BEX        8       12000      1500
            Visigasy   4       6000       1500
            THB        10      20000      2000


        option :

            filtrage par date :

                date_mouvement <= date choisie



    Fenetre Etat de stock detail

        s'affiche apres un click
        sur une ligne du stock global


        objectif :

            expliquer comment le stock
            a ete obtenu


        colonnes :

            date

            type mouvement

            quantite

            PU

            valeur

            stock apres

            valeur stock apres

            CUMP apres

            sources utilisees


        exemple :

            Produit :
                BEX

            Entree 1 :
                +5 BEX a 1000

            Entree 2 :
                +10 BEX a 1500

            Sortie :
                -8 BEX


            Sources utilisees :

                - 5 depuis entree 1
                - 3 depuis entree 2


            Resultat :

                Stock final BEX = 7
```




NB : tout les fonction doivent etre generaliser , utilisable pour tout les objet , on doit donc utiliser le genericiter 