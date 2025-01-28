package org.odc.gestionstockapp.Datas.Repositories;

import org.odc.gestionstockapp.Datas.Entities.FactureEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FactureRepository extends JpaRepository<FactureEntity,Integer> {
    Optional<FactureEntity> findByCommandeId(Long commandeId);
    Optional<FactureEntity> findByUrlTelechargement(String urlTelechargement);
}
