package org.odc.gestionstockapp.Web.Dtos;

import lombok.Data;

@Data
public class ProduitDtoUpdate {
    private int id;
    private String nom;
    private String image;
    private Integer quantite;
    private Double prix;
    private Double prixAchat;
    private String statut;
}
