    package org.odc.gestionstockapp.Services.Implementation;

import org.odc.gestionstockapp.Datas.Entities.ProduitEntity;
import org.odc.gestionstockapp.Datas.Enums.Categorie;
import org.odc.gestionstockapp.Datas.Enums.StatutProduit;
import org.odc.gestionstockapp.Datas.Repositories.ProduitRepository;
import org.odc.gestionstockapp.Datas.Repositories.UserRepository;
import org.odc.gestionstockapp.Services.Interfaces.CrudService;
import org.odc.gestionstockapp.Web.Dtos.ProduitDto;
import org.odc.gestionstockapp.Web.Dtos.ProduitDtoUpdate;
import org.odc.gestionstockapp.Web.Mappers.ProduitMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
@Service
public class ProduitService implements CrudService<ProduitEntity,ProduitDto,ProduitDtoUpdate> {

    private final ProduitRepository produitRepository;
    private final ProduitMapper produitMapper;

    public ProduitService(ProduitRepository produitRepository, ProduitMapper produitMapper) {
        this.produitRepository = produitRepository;
        this.produitMapper = produitMapper;
    }

    @Override
    public ProduitEntity create(ProduitDto t) {
        ProduitEntity produitEntity = produitMapper.toEntity(t);
        // Par défaut, le statut est SUFFISANT à la création
        produitEntity.setStatut(StatutProduit.SUFFISANT);
        return produitRepository.save(produitEntity);
    }

    // Méthode utilitaire pour mettre à jour le statut
    void updateProduitStatus(ProduitEntity produit) {
        if (produit.getQuantite() == 0) {
            produit.setStatut(StatutProduit.INSUFFISANT);
        } else {
            produit.setStatut(StatutProduit.SUFFISANT);
        }
    }


    @Override
    public ProduitEntity update(ProduitDtoUpdate t) {
        return null;
    }

    @Override
    public void delete(int id) {
    produitRepository.deleteById(id);
    }

    @Override
    public List<ProduitEntity> findAll() {
        return produitRepository.findAll();
    }

    @Override
    public ProduitEntity findById(int id) {
        return produitRepository.findById(id).orElse(null);
    }

    public List<ProduitDto> searchProduit(String nom, Categorie categorie) {
        List<ProduitEntity> produits;
        if (nom != null && categorie != null) {
            produits = produitRepository.findByNomContainingIgnoreCaseAndCategorie(nom, categorie);
        } else if (nom != null) {
            produits = produitRepository.findByNomContainingIgnoreCase(nom);
        } else if (categorie != null) {
            produits = produitRepository.findByCategorie(categorie);
        } else {
            produits = produitRepository.findAll();
        }
        return produits.stream()
                .map(produitMapper::toDto)
                .toList();
    }

}
