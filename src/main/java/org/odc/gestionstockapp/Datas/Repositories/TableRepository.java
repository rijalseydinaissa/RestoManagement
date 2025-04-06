package org.odc.gestionstockapp.Datas.Repositories;

import org.odc.gestionstockapp.Datas.Entities.TableEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TableRepository extends JpaRepository<TableEntity, Integer> {
    List<TableEntity> findByOccupeeFalse(); // Pour récupérer les tables disponibles

    Object countByOccupeeTrue();

    Object countByOccupeeFalse();
}
