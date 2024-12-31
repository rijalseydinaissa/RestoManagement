package org.odc.gestionstockapp.Web.Controllers.Implementation;

import org.odc.gestionstockapp.Datas.Entities.ProduitEntity;
import org.odc.gestionstockapp.Datas.Enums.Categorie;
import org.odc.gestionstockapp.Datas.Repositories.ProduitRepository;
import org.odc.gestionstockapp.Services.Implementation.CloudinaryService;
import org.odc.gestionstockapp.Services.Implementation.ProduitService;
import org.odc.gestionstockapp.Web.Controllers.Interface.CrudController;
import org.odc.gestionstockapp.Web.Dtos.ProduitDto;
import org.odc.gestionstockapp.Web.Dtos.ProduitDtoUpdate;
import org.odc.gestionstockapp.Web.Mappers.ProduitMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/produits")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {
        RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
        RequestMethod.DELETE, RequestMethod.OPTIONS
})
public class ProduitController implements CrudController<ProduitEntity, ProduitDto, ProduitDtoUpdate> {

    private final ProduitService produitService;
    private final ProduitMapper produitMapper;

    @Autowired
    public ProduitController(ProduitService produitService, ProduitMapper produitMapper) {
        this.produitService = produitService;
        this.produitMapper = produitMapper;
    }
    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    private ProduitRepository produitRepository;

    @Override
    @PostMapping
    public ProduitEntity create(@RequestBody ProduitDto produitDto) {
        return produitService.create(produitDto);
    }
    @PostMapping("/{id}/image")
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    public ResponseEntity<Map<String, String>> uploadImage(@PathVariable int id, @RequestParam("image") MultipartFile file) {
        try {
            ProduitEntity produit = produitRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Produit non trouvé"));

            String imageUrl = cloudinaryService.uploadImage(file);
            produit.setImage(imageUrl);
            produitRepository.save(produit);

            Map<String, String> response = new HashMap<>();
            response.put("url", imageUrl);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }

    @Override
    @PutMapping("/{id}")
    public ProduitEntity update(@PathVariable int id, @RequestBody ProduitDtoUpdate produitDtoUpdate) {
        return produitService.update(produitDtoUpdate);
    }

    @Override
    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        produitService.delete(id);
    }

    @Override
    @GetMapping("/{id}")
    public ProduitEntity getById(@PathVariable int id) {
        return produitService.findById(id);
    }

    @Override
    @GetMapping
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
