package org.odc.gestionstockapp.Datas.Repositories;

import org.odc.gestionstockapp.Datas.Entities.CommandeProduit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface CommandeProduitRepository extends JpaRepository<CommandeProduit,Integer> {
    List<Object[]> findMostOrderedProductsForDay(LocalDateTime localDateTime, LocalDateTime localDateTime1);
}
