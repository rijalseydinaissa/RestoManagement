package org.odc.gestionstockapp.Datas.Enums;

public enum StatutCommande {
    EN_ATTENTE,      // Créée par le serveur
    EN_PREPARATION,  // En cours de préparation par le cuisinier
    PRET,            // Plats préparés, prêts à être servis
    SERVI,          // Plats servis au client
    PAYEE,            // Commande payée et terminée
    ANNULEE
}
