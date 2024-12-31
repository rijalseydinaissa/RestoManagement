package org.odc.gestionstockapp.Datas.Entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.odc.gestionstockapp.Datas.Enums.StatutCommande;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommandeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String client; // Nom du client qui passe la commande

    private LocalDate date;

    private double montantTotal;
    @Column(name = "nombre_produits")
    private Integer nombreProduits = 0;


    @Enumerated(EnumType.STRING)
    private StatutCommande status=StatutCommande.NONREGLE;

    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<CommandeProduit> commandeProduits;
}
