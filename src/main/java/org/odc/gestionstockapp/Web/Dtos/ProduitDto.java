package org.odc.gestionstockapp.Web.Dtos;

import lombok.Data;
import org.odc.gestionstockapp.Datas.Enums.Categorie;
import org.odc.gestionstockapp.Datas.Enums.StatutProduit;

@Data
public class ProduitDto {
    private int id;
    private String nom;
    private String image;
    private String description;
    private Integer quantite;
    private Double prix;
    private StatutProduit statut;
    private Categorie categorie;
}
