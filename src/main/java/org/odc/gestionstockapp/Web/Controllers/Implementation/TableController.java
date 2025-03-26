package org.odc.gestionstockapp.Web.Controllers.Implementation;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.odc.gestionstockapp.Datas.Entities.TableEntity;
import org.odc.gestionstockapp.Services.Implementation.ProduitService;
import org.odc.gestionstockapp.Services.Implementation.TableService;
import org.odc.gestionstockapp.Web.Dtos.TableDto;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tables")

@Tag(name = "Produits", description = "API pour la gestion des tables")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {
        RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
        RequestMethod.DELETE, RequestMethod.OPTIONS
})
public class TableController {

    private final ProduitService produitService;
    private final TableService tableService;

    public TableController(ProduitService produitService, TableService tableService) {
        this.produitService = produitService;
        this.tableService = tableService;
    }

    @GetMapping
    public List<TableEntity> getAllTables() {
        return tableService.findAll();
    }

    @GetMapping("/available")
    public List<TableEntity> getAvailableTables() {
        return tableService.getAvailableTables();
    }

    @PostMapping
    public TableEntity create(@RequestBody TableDto tableDto) {
        return tableService.create(tableDto);
    }

    @PatchMapping("/{id}/status")
    public TableEntity updateTableStatus(@PathVariable int id,
                                         @RequestBody Map<String, Boolean> requestBody) {
        return tableService.updateTable(id, requestBody.get("occupee"));
    }
}