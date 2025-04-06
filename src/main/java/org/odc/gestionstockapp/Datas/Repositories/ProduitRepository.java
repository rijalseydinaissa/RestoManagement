package org.odc.gestionstockapp.Datas.Repositories;

import org.odc.gestionstockapp.Datas.Entities.ProduitEntity;
import org.odc.gestionstockapp.Datas.Enums.Categorie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProduitRepository extends JpaRepository<ProduitEntity,Integer> {
    List<ProduitEntity> findByNomContainingIgnoreCase(String nom);
    List<ProduitEntity>findByCategorie(Categorie categorie);
    List<ProduitEntity>findByNomContainingIgnoreCaseAndCategorie(String nom , Categorie categorie);

    Object findByQuantiteLessThan(int i);
}
