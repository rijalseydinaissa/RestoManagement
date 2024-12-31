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
    private String client;
    private int nombreProduits;
    List<CommandeProduitDto> produits;
}
