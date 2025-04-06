package org.odc.gestionstockapp.Datas.Entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.odc.gestionstockapp.Datas.Enums.StatutCommande;
import org.odc.gestionstockapp.Datas.Entities.UserEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommandeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // Suppression du champ client qui est remplacé par la table

    @Column(nullable = false)
    private LocalDate date;

    private double montantTotal;

    @Column(name = "nombre_produits")
    private Integer nombreProduits = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutCommande status = StatutCommande.EN_ATTENTE;  // Statut modifié selon vos exigences

    @Column(name = "est_paye")
    private boolean estPaye = false;  // Nouveau champ pour suivre le paiement
// URL de la facture générée

    @ManyToOne
    @JoinColumn(name = "serveur_id", nullable = true)
    private UserEntity serveur;  // Serveur qui a créé la commande

    @ManyToOne
    @JoinColumn(name = "cuisinier_id")
    private UserEntity cuisinier;  // Cuisinier qui prend en charge la commande

    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<CommandeProduit> commandeProduits;

    @ManyToOne
    @JoinColumn(name = "table_id", nullable = false)
    private TableEntity table;// Estimation du temps de préparation en minutes

    @Column(name = "date_paiement")
    private LocalDateTime datePaiement;  // Date à laquelle la commande a été payée


    // Méthode utilitaire pour vérifier si une commande peut être modifiée
    @Transient
    public boolean peutEtreModifiee() {
        return !estPaye && status != StatutCommande.SERVI;
    }
}