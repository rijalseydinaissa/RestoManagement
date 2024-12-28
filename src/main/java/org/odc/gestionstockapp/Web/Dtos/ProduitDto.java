package org.odc.gestionstockapp.Web.Dtos;

import lombok.Data;
import org.odc.gestionstockapp.Datas.Enums.Categorie;

@Data
public class ProduitDto {
    private int id;
    private String nom;
    private String image;
    private Integer quantite;
    private Double prix;
    private String statut;
    private Categorie categorie;
}
