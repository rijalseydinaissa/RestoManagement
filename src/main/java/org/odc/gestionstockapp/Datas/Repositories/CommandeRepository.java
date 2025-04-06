package org.odc.gestionstockapp.Datas.Repositories;

import org.odc.gestionstockapp.Datas.Entities.CommandeEntity;
import org.odc.gestionstockapp.Datas.Entities.UserEntity;
import org.odc.gestionstockapp.Datas.Enums.StatutCommande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CommandeRepository extends JpaRepository<CommandeEntity, Integer> {
    @Query("SELECT c FROM CommandeEntity c WHERE c.status = :status1 OR (c.cuisinier = :cuisinier AND c.status = :status2)")
    List<CommandeEntity> findByStatusOrCuisinierAndStatus(
            @Param("status1") StatutCommande status1,
            @Param("cuisinier") UserEntity cuisinier,
            @Param("status2") StatutCommande status2);
    // Recherche par date
    @Query("SELECT c FROM CommandeEntity c WHERE DATE(c.date) = :date")
    List<CommandeEntity> findByDate(@Param("date") LocalDate date);

    // Recherche par serveur
    List<CommandeEntity> findByServeurId(Long serveurId);

    @Query("SELECT c FROM CommandeEntity c WHERE c.serveur.id = :serveurId AND DATE(c.date) = :date")
    List<CommandeEntity> findByServeurIdAndDate(@Param("serveurId") Long serveurId, @Param("date") LocalDate date);

    // Recherche par statut
    List<CommandeEntity> findByStatus(StatutCommande status);

    @Query("SELECT c FROM CommandeEntity c WHERE c.status = :status AND DATE(c.date) = :date")
    List<CommandeEntity> findByStatusAndDate(
            @Param("status") StatutCommande status,
            @Param("date") LocalDate date);

    // Statistiques
    long countByDateBetween(LocalDateTime debut, LocalDateTime fin);

    @Query("SELECT SUM(c.montantTotal) FROM CommandeEntity c WHERE c.date BETWEEN :debut AND :fin AND c.estPaye = :estPaye")
    double sumMontantTotalByDateBetweenAndEstPaye(
            @Param("debut") LocalDateTime debut,
            @Param("fin") LocalDateTime fin,
            @Param("estPaye") boolean estPaye);
}
