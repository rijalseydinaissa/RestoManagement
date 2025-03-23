package org.odc.gestionstockapp.Web.Dtos;

import java.time.LocalDate;
import java.util.Date;

import lombok.Data;
import org.odc.gestionstockapp.Datas.Entities.CommandeProduit;
import org.odc.gestionstockapp.Datas.Enums.StatutCommande;
import java.util.List;

@Data
public class CommandeDto {
    private LocalDate date;
    private StatutCommande status;
    private String numeroTable;
    private int nombreProduits;
    private int tableId;
    private Long serveurId;  // ID du serveur qui crée la commande
    private boolean payee;
    List<CommandeProduitDto> produits;
}
