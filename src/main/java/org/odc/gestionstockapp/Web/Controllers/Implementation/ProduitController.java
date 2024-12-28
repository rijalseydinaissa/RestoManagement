package org.odc.gestionstockapp.Web.Controllers.Implementation;

import org.odc.gestionstockapp.Datas.Entities.ProduitEntity;
import org.odc.gestionstockapp.Datas.Enums.Categorie;
import org.odc.gestionstockapp.Services.Implementation.ProduitService;
import org.odc.gestionstockapp.Web.Controllers.Interface.CrudController;
import org.odc.gestionstockapp.Web.Dtos.ProduitDto;
import org.odc.gestionstockapp.Web.Dtos.ProduitDtoUpdate;
import org.odc.gestionstockapp.Web.Mappers.ProduitMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class ProduitController implements CrudController<ProduitEntity, ProduitDto, ProduitDtoUpdate> {
    @Autowired
    private ProduitService produitService;
    private ProduitMapper produitMapper;
    @Override
    @PostMapping("/produits")
    public ProduitEntity create(@RequestBody ProduitDto produitDto) { return produitService.create(produitDto);}

    @Override
    @PutMapping("/produits/{id}")
    public ProduitEntity update(@PathVariable int id,@RequestBody ProduitDtoUpdate produitDtoUpdate) {return produitService.update(produitDtoUpdate);}

    @Override
    @DeleteMapping("/produits{id}")
    public void delete(@PathVariable int id) {produitService.delete(id);}

    @Override
    @GetMapping("/produits/{id}")
    public ProduitEntity getById(@PathVariable int id) {
        return produitService.findById(id);
    }

    @Override
    @GetMapping("/produits")
    public List<ProduitEntity> getAll() {
        return produitService.findAll();
    }
    @GetMapping("/search")
    public List<ProduitDto> searchProduits(
            @RequestParam(required = false) String nom,
            @RequestParam(required = false) Categorie categorie) {
        return produitService.searchProduit(nom, categorie);
    }
}
