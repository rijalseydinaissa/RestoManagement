package org.odc.gestionstockapp.Datas.Entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "facture")
public class FactureEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "contenu_pdf")
    private byte[] contenuPdf;

    @Column(name = "commande_id")
    private Long commandeId;

    @Column(name = "date_generation")
    private LocalDateTime dateGeneration;

    private String urlTelechargement;
}
