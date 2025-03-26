package org.odc.gestionstockapp.Web.Controllers.Implementation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.odc.gestionstockapp.Datas.Entities.CommandeEntity;
import org.odc.gestionstockapp.Datas.Enums.StatutCommande;
//import org.odc.gestionstockapp.Exceptions.AuthorizationException;
//import org.odc.gestionstockapp.Exceptions.CommandeException;
import org.odc.gestionstockapp.Services.Implementation.CommandeService;
import org.odc.gestionstockapp.Web.Controllers.Interface.CrudController;
import org.odc.gestionstockapp.Web.Dtos.CommandeDto;
import org.odc.gestionstockapp.Web.Dtos.CommandeProduitDto;
import org.odc.gestionstockapp.Web.Mappers.CommandeMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/commandes")  // Préfixe API pour clarifier
@Tag(name = "Commandes", description = "API pour la gestion des commandes du restaurant")
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
    @Operation(summary = "Créer une nouvelle commande", description = "Accessible aux serveurs et administrateurs uniquement")
    @PreAuthorize("hasAnyRole('SERVEUR', 'ADMIN')")
    public CommandeEntity create(@RequestBody CommandeDto commandeDto) {
        return commandeService.create(commandeDto);
    }

    @Override
    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour une commande existante", description = "Un serveur ne peut modifier que ses propres commandes en attente")
    @PreAuthorize("hasAnyRole('SERVEUR', 'ADMIN')")
    public CommandeEntity update(@PathVariable int id, @RequestBody CommandeProduitDto commandeProduitDto) {
        return commandeService.update(commandeProduitDto);
    }

    @PutMapping("/{id}/complet")
    @Operation(summary = "Mettre à jour une commande complète", description = "Permet de modifier plusieurs aspects d'une commande")
    @PreAuthorize("hasAnyRole('SERVEUR', 'ADMIN')")
    public ResponseEntity<CommandeEntity> updateCommande(@PathVariable int id, @RequestBody CommandeDto commandeDto) {
        try {
            CommandeEntity updatedCommande = commandeService.updateCommande(id, commandeDto);
            return ResponseEntity.ok(updatedCommande);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @Override
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une commande", description = "Seules les commandes en attente peuvent être supprimées")
    @PreAuthorize("hasAnyRole('SERVEUR', 'ADMIN')")
    public void delete(@PathVariable int id) {
        commandeService.delete(id);
    }

    @Override
    @GetMapping("/{id}")
    @Operation(summary = "Obtenir une commande par son ID", description = "Filtré selon le rôle de l'utilisateur")
    @PreAuthorize("hasAnyRole('SERVEUR', 'ADMIN', 'CUISINIER')")
    public CommandeEntity getById(@PathVariable int id) {
        return commandeService.findById(id);
    }

    @Override
    @GetMapping
    @Operation(summary = "Obtenir toutes les commandes", description = "Filtré selon le rôle de l'utilisateur")
    @PreAuthorize("hasAnyRole('SERVEUR', 'ADMIN', 'CUISINIER')")
    public List<CommandeEntity> getAll() {
        return commandeService.findAll();
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher des commandes", description = "Filtré selon le rôle de l'utilisateur")
    @PreAuthorize("hasAnyRole('SERVEUR', 'ADMIN', 'CUISINIER')")
    public List<CommandeDto> searchCommande(
            @RequestParam(required = false) String client,
            @RequestParam(required = false) LocalDate date) {
        return commandeService.searchCommande(client, date);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Mettre à jour le statut d'une commande", description = "Les droits dépendent du rôle et du statut actuel")
    @PreAuthorize("hasAnyRole('SERVEUR', 'ADMIN', 'CUISINIER')")
    public ResponseEntity<CommandeEntity> updateStatus(
            @PathVariable int id,
            @RequestBody Map<String, String> statusUpdate) {
        try {
            CommandeEntity updatedCommande = commandeService.updateStatus(id, statusUpdate.get("status"));
            return ResponseEntity.ok(updatedCommande);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @PatchMapping("/{id}/payer")
    @Operation(summary = "Marquer une commande comme payée", description = "Seules les commandes servies peuvent être payées")
    @PreAuthorize("hasAnyRole('SERVEUR', 'ADMIN')")
    public ResponseEntity<CommandeEntity> payerCommande(@PathVariable int id) {
        try {
            CommandeEntity commandePayee = commandeService.payerCommande(id);
            return ResponseEntity.ok(commandePayee);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @GetMapping("/statistiques/jour")
    @Operation(summary = "Obtenir les statistiques du jour", description = "Accessible uniquement aux administrateurs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getStatistiquesJour() {
        try {
            Map<String, Object> statistiques = commandeService.getStatistiquesJour();
            return ResponseEntity.ok(statistiques);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }
}