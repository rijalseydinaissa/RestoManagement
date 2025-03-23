package org.odc.gestionstockapp.Web.Controllers.Implementation;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import org.odc.gestionstockapp.Datas.Entities.FactureEntity;
import org.odc.gestionstockapp.Services.Implementation.FactureService;
import org.odc.gestionstockapp.Datas.Repositories.FactureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/factures")
@Tag(name = "Facture", description = "API pour la gestion des factures")

@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {
        RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
        RequestMethod.DELETE, RequestMethod.OPTIONS
})
public class FactureController {
    @Autowired
    private FactureService factureService;

    @Autowired
    private FactureRepository factureRepository;
    // Génération de facture - uniquement serveur (pour ses commandes) et admin
    @GetMapping("/generer/{commandeId}")
    public ResponseEntity<String> genererFacture(@PathVariable Long commandeId) {
        try {
            String lienTelechargement = factureService.genererEtSauvegarderFacture(commandeId);
            return ResponseEntity.ok(lienTelechargement);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la génération de la facture : " + e.getMessage());
        }
    }


    @CrossOrigin(origins = "https://angular-front-app-mu.vercel.app/", allowedHeaders = "*", exposedHeaders = "Content-Disposition")
    @GetMapping("/telecharger-facture/{urlUnique}")
    @Transactional // Ajoutez cette annotation
    public ResponseEntity<byte[]> telechargerFacture(@PathVariable String urlUnique) {
        try {
            FactureEntity facture = factureRepository.findByUrlTelechargement(urlUnique)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Facture non trouvée"));

            // S'assurer que le contenu PDF est chargé dans la transaction
            byte[] contenuPdf = facture.getContenuPdf();

            if (contenuPdf == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Contenu PDF non trouvé");
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=facture.pdf")
                    .body(contenuPdf);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erreur lors du téléchargement de la facture: " + e.getMessage());
        }
    }

}