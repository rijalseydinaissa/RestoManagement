package org.odc.gestionstockapp.Web.Dtos;

import lombok.Data;

@Data
public class CommandeProduitDto {
    private ProduitDto produit;
    private int produitId;
    private int quantite;
}
