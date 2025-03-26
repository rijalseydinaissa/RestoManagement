package org.odc.gestionstockapp.Datas.Repositories;

import org.odc.gestionstockapp.Datas.Entities.CommandeProduit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CommandeProduitRepository extends JpaRepository<CommandeProduit,Integer> {

    @Query("SELECT cp.produit, SUM(cp.quantite) as totalQuantite FROM CommandeProduit cp " +
            "WHERE cp.commande.date BETWEEN :dateDebut AND :dateFin " +
            "GROUP BY cp.produit ORDER BY totalQuantite DESC")
    List<Object[]> findMostOrderedProductsForDay(
            @Param("dateDebut") LocalDateTime dateDebut,
            @Param("dateFin") LocalDateTime dateFin);
}