package org.odc.gestionstockapp.Datas.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.odc.gestionstockapp.Datas.Enums.Categorie;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.odc.gestionstockapp.Datas.Enums.StatutProduit;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProduitEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String image;

    private String nom;

    private Integer quantite;

    private String description;

    private Double prix;

    @Enumerated(EnumType.STRING)
    private Categorie categorie;

    @Enumerated(EnumType.STRING)
    private StatutProduit statut;

    @OneToMany(mappedBy = "produit", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // Évite les cycles de sérialisation lorsque les produits sont affichés
    private List<CommandeProduit> commandeProduits;
}

