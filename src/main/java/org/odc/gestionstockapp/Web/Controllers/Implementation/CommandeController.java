package org.odc.gestionstockapp.Web.Controllers.Implementation;


import org.odc.gestionstockapp.Datas.Entities.CommandeEntity;
import org.odc.gestionstockapp.Datas.Enums.StatutCommande;
import org.odc.gestionstockapp.Services.Implementation.CommandeService;
import org.odc.gestionstockapp.Web.Controllers.Interface.CrudController;
import org.odc.gestionstockapp.Web.Dtos.CommandeDto;
import org.odc.gestionstockapp.Web.Dtos.CommandeProduitDto;
import org.odc.gestionstockapp.Web.Mappers.CommandeMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/commandes")  // Ajout du mapping de base
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {
        RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
        RequestMethod.DELETE, RequestMethod.OPTIONS, RequestMethod.PATCH
})
public class CommandeController implements CrudController<CommandeEntity, CommandeDto, CommandeProduitDto> {
    private final CommandeService commandeService;
    private final CommandeMapper commandeMapper;

    public CommandeController(CommandeService commandeService, CommandeMapper commandeMapper) {
        this.commandeService = commandeService;
        this.commandeMapper = commandeMapper;
    }

    @Override
    @PostMapping
    public CommandeEntity create(@RequestBody CommandeDto commandeDto) {
        return commandeService.create(commandeDto);
    }

    @Override
    @PostMapping("/{id}")
    public CommandeEntity update(@PathVariable int id, @RequestBody CommandeProduitDto commandeProduitDto) {
        return commandeService.update(commandeProduitDto);
    }

    @Override
    @DeleteMapping("/{id}")  // Correction de la syntaxe
    public void delete(@PathVariable int id) {
        commandeService.delete(id);
    }

    @Override
    @GetMapping("/{id}")
    public CommandeEntity getById(@PathVariable int id) {
        return commandeService.findById(id);
    }

    @Override
    @GetMapping
    public List<CommandeEntity> getAll() {
        return commandeService.findAll();
    }

    @GetMapping("/search")  // Déplacé après la méthode
    public List<CommandeDto> searchCommande(
            @RequestParam(required = false) String client,
            @RequestParam(required = false) LocalDate date) {
        return commandeService.searchCommande(client, date);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<CommandeEntity> updateStatus(
            @PathVariable int id,
            @RequestBody Map<String, String> statusUpdate) {
        try {
            System.out.println("Reçu requête PATCH pour commande ID: " + id); // Log pour debug
            System.out.println("Nouveau statut: " + statusUpdate.get("status")); // Log pour debug

            StatutCommande status = StatutCommande.valueOf(statusUpdate.get("status"));
            CommandeEntity updatedCommande = commandeService.updateStatus(id, status.name());
            return ResponseEntity.ok(updatedCommande);
        } catch (IllegalArgumentException e) {
            System.err.println("Erreur lors de la mise à jour: " + e.getMessage()); // Log pour debug
            return ResponseEntity.badRequest().build();
        }
    }
}