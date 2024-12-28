package org.odc.gestionstockapp.Services.Implementation;

import org.odc.gestionstockapp.Datas.Entities.CommandeEntity;
import org.odc.gestionstockapp.Datas.Entities.CommandeProduit;
import org.odc.gestionstockapp.Datas.Entities.ProduitEntity;
import org.odc.gestionstockapp.Datas.Repositories.CommandeProduitRepository;
import org.odc.gestionstockapp.Datas.Repositories.CommandeRepository;
import org.odc.gestionstockapp.Datas.Repositories.ProduitRepository;
import org.odc.gestionstockapp.Services.Interfaces.CrudService;
import org.odc.gestionstockapp.Web.Dtos.CommandeDto;
import org.odc.gestionstockapp.Web.Dtos.CommandeProduitDto;
import org.odc.gestionstockapp.Web.Mappers.CommandeMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommandeService implements CrudService<CommandeEntity, CommandeDto, CommandeProduitDto> {
    private final CommandeRepository commandeRepository;
    private CommandeMapper commandeMapper;
    private ProduitService produceService;
    private final ProduitRepository produitRepository;
    private final CommandeProduitRepository commandeProduitRepository;

    public CommandeService(CommandeRepository commandeRepository, CommandeMapper commandeMapper,
                           CommandeProduitRepository commandeProduitRepository, ProduitRepository produitRepository,ProduitService produceService) {
        this.commandeRepository = commandeRepository;
        this.commandeMapper = commandeMapper;
        this.commandeProduitRepository = commandeProduitRepository;
        this.produitRepository = produitRepository;
    }

    @Override
    @Transactional
    public CommandeEntity create(CommandeDto t) {
        CommandeEntity commandeEntity = commandeMapper.toEntity(t);
        commandeEntity.setDate(LocalDate.now());

        double montantTotal = 0;

        // Vérifier et traiter chaque produit
        List<CommandeProduit> commandeProduits = t.getProduits().stream().map(produitDto -> {
            // Récupérer le produit
            ProduitEntity produit = produitRepository.findById(produitDto.getProduitId())
                    .orElseThrow(() -> new IllegalArgumentException("Produit introuvable"));

            // Vérifier la quantité
            if (produitDto.getQuantite() <= 0) {
                throw new IllegalArgumentException("La quantité doit être supérieure à 0 pour le produit " + produit.getNom());
            }

            if (produitDto.getQuantite() > produit.getQuantite()) {
                throw new IllegalArgumentException(
                        String.format("Stock insuffisant pour le produit %s. Stock disponible : %d, Quantité demandée : %d",
                                produit.getNom(), produit.getQuantite(), produitDto.getQuantite())
                );
            }
            // Mettre à jour le stock
            produit.setQuantite(produit.getQuantite() - produitDto.getQuantite());
            produceService.updateProduitStatus(produit);
            produitRepository.save(produit);

            // Créer la ligne de commande
            CommandeProduit commandeProduit = new CommandeProduit();
            commandeProduit.setCommande(commandeEntity);
            commandeProduit.setProduit(produit);
            commandeProduit.setQuantite(produitDto.getQuantite());

            return commandeProduit;
        }).collect(Collectors.toList());

        // Calculer le montant total
        montantTotal = commandeProduits.stream()
                .mapToDouble(cp -> cp.getProduit().getPrix() * cp.getQuantite())
                .sum();

        // Associer les produits à la commande
        commandeEntity.setCommandeProduits(commandeProduits);
        commandeEntity.setMontantTotal(montantTotal);

        // Sauvegarder la commande
        return commandeRepository.save(commandeEntity);
    }

    @Override
    public CommandeEntity update(CommandeProduitDto t) {
        return null;
    }

    @Override
    public void delete(int id) {commandeRepository.deleteById(id);}

    @Override
    public List<CommandeEntity> findAll() {
        return commandeRepository.findAll();
    }

    @Override
    public CommandeEntity findById(int id) {
        return commandeRepository.findById(id).orElse(null);
    }

    public List<CommandeDto> searchCommande(String client, LocalDate date) {
        List<CommandeEntity> commandes;
        if (client != null && date != null) {
            commandes = commandeRepository.findByClientContainingIgnoreCaseAndDate(client, date);
        } else if (client != null) {
            commandes = commandeRepository.findByClientContainingIgnoreCase(client);
        } else if (date != null) {
            commandes = commandeRepository.findByDate(date);
        } else {
            commandes = commandeRepository.findAll();
        }
        return commandes.stream().map(commandeMapper::toDto).collect(Collectors.toList());
    }
}