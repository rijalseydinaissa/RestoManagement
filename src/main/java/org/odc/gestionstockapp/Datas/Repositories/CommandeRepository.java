package org.odc.gestionstockapp.Datas.Repositories;

import org.odc.gestionstockapp.Datas.Entities.CommandeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CommandeRepository extends JpaRepository <CommandeEntity,Integer> {
    List<CommandeEntity> findByClientContainingIgnoreCase(String client);
    List<CommandeEntity> findByDate(LocalDate date);
    List<CommandeEntity> findByClientContainingIgnoreCaseAndDate(String client, LocalDate date);
}
