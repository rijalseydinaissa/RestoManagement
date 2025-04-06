package org.odc.gestionstockapp.Services.Implementation;

import org.odc.gestionstockapp.Datas.Entities.*;
import org.odc.gestionstockapp.Datas.Enums.Role;
import org.odc.gestionstockapp.Datas.Enums.StatutCommande;
import org.odc.gestionstockapp.Datas.Repositories.*;
//import org.odc.gestionstockapp.Exceptions.AuthorizationDeniedException;
//import org.odc.gestionstockapp.Exceptions.AuthorizationDeniedException;
import org.odc.gestionstockapp.Services.Interfaces.CrudService;
import org.odc.gestionstockapp.Services.Implementation.FactureService;

import org.odc.gestionstockapp.Web.Dtos.CommandeDto;
import org.odc.gestionstockapp.Web.Dtos.CommandeProduitDto;
import org.odc.gestionstockapp.Web.Mappers.CommandeMapper;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommandeService implements CrudService<CommandeEntity, CommandeDto, CommandeProduitDto> {
    private final CommandeRepository commandeRepository;
    private final CommandeMapper commandeMapper;
    private final ProduitService produitService;
    private final ProduitRepository produitRepository;
    private final CommandeProduitRepository commandeProduitRepository;
    private final TableRepository tableRepository;
    private final UserRepository userRepository;
    private final FactureService factureService;


    public CommandeService(CommandeRepository commandeRepository, CommandeMapper commandeMapper,
                           CommandeProduitRepository commandeProduitRepository, TableRepository tableRepository,
                           ProduitRepository produitRepository, ProduitService produitService,
                           UserRepository userRepository, FactureService factureService) {
        this.commandeRepository = commandeRepository;
        this.commandeMapper = commandeMapper;
        this.commandeProduitRepository = commandeProduitRepository;
        this.produitRepository = produitRepository;
        this.tableRepository = tableRepository;
        this.produitService = produitService;
        this.userRepository = userRepository;
        this.factureService = factureService;
    }

    @Override
    @Transactional
    public CommandeEntity create(CommandeDto commandeDto) {
        // Récupérer l'utilisateur authentifié (serveur)
        UserEntity currentUser = getCurrentUser();
        // Vérifier que l'utilisateur est bien un serveur
        if (!currentUser.getRole().equals(Role.SERVEUR) && !currentUser.getRole().equals(Role.ADMIN)) {
            throw new AuthorizationDeniedException("Seuls les serveurs et administrateurs peuvent créer des commandes");
        }
        CommandeEntity commandeEntity = commandeMapper.toEntity(commandeDto);
        commandeEntity.setDate(LocalDate.now());
        commandeEntity.setServeur(currentUser);
        commandeEntity.setStatus(StatutCommande.EN_ATTENTE);
        // Récupérer la table
        TableEntity table = tableRepository.findById(commandeDto.getTableId())
                .orElseThrow(() -> new IllegalArgumentException("Table introuvable"));
        commandeEntity.setTable(table);
        // Vérifier et traiter chaque produit
        List<CommandeProduit> commandeProduits = commandeDto.getProduits().stream().map(produitDto -> {
            // Récupérer le produit
            ProduitEntity produit = produitRepository.findById(produitDto.getProduitId())
                    .orElseThrow(() -> new IllegalArgumentException("Produit introuvable"));
            //Vérifier que le produit est disponible
            if (!produit.isDisponible()) {
            throw new IllegalArgumentException("Le produit " + produit.getNom() + " n'est pas disponible");
            }
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
            produitService.updateProduitStatus(produit);
            produitRepository.save(produit);
            // Créer la ligne de commande
            CommandeProduit commandeProduit = new CommandeProduit();
            commandeProduit.setCommande(commandeEntity);
            commandeProduit.setProduit(produit);
            commandeProduit.setQuantite(produitDto.getQuantite());
            return commandeProduit;
        }).collect(Collectors.toList());
        // Calculer le montant total
        double montantTotal = commandeProduits.stream()
                .mapToDouble(cp -> cp.getProduit().getPrix() * cp.getQuantite())
                .sum();

        // Associer les produits à la commande
        commandeEntity.setCommandeProduits(commandeProduits);
        commandeEntity.setMontantTotal(montantTotal);
        commandeEntity.setNombreProduits(commandeProduits.size());

        // Sauvegarder la commande
        return commandeRepository.save(commandeEntity);
    }

    @Override
    @Transactional
    public CommandeEntity update(CommandeProduitDto commandeProduitDto) {
        // Cette méthode devrait être adaptée pour modifier une commande existante
        // Si non implémentée, vous pouvez lancer une exception
        throw new UnsupportedOperationException("Méthode non implémentée");
    }

    @Transactional
    public CommandeEntity updateCommande(int commandeId, CommandeDto commandeDto) {
        UserEntity currentUser = getCurrentUser();
        CommandeEntity existingCommande = commandeRepository.findById(commandeId)
                .orElseThrow(() -> new IllegalArgumentException("Commande introuvable"));

        // Vérifier que l'utilisateur a le droit de modifier cette commande
        if (currentUser.getRole() == Role.SERVEUR && existingCommande.getServeur().getId() != currentUser.getId()) {
            throw new AuthorizationDeniedException ("Vous ne pouvez modifier que vos propres commandes");
        }

        // Vérifier que la commande peut être modifiée
        if (!existingCommande.peutEtreModifiee()) {
            throw new AuthorizationDeniedException("Cette commande ne peut plus être modifiée (déjà servie ou payée)");
        }

        // Mise à jour des champs (à adapter selon vos besoins)
        if (commandeDto.getTableId() != 0) {
            TableEntity table = tableRepository.findById(commandeDto.getTableId())
                    .orElseThrow(() -> new IllegalArgumentException("Table introuvable"));
            existingCommande.setTable(table);
        }

        // Mettre à jour les produits si besoin...

        return commandeRepository.save(existingCommande);
    }

    @Override
    @Transactional
    public void delete(int id) {
        UserEntity currentUser = getCurrentUser();
        CommandeEntity commande = commandeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Commande introuvable"));

        // Vérifier que l'utilisateur a le droit de supprimer cette commande
        if (currentUser.getRole() == Role.SERVEUR && !(commande.getServeur().getId() ==currentUser.getId())) {
            throw new AuthorizationDeniedException("Vous ne pouvez supprimer que vos propres commandes");
        }

        // Vérifier que la commande peut être supprimée
        if (!commande.getStatus().equals(StatutCommande.EN_ATTENTE) && !commande.getStatus().equals(StatutCommande.ANNULEE)) {
            throw new AuthorizationDeniedException("Seules les commandes en attente ou annulee peuvent être supprimées");
        }

        // Remettre les produits en stock
        for (CommandeProduit cp : commande.getCommandeProduits()) {
            ProduitEntity produit = cp.getProduit();
            produit.setQuantite(produit.getQuantite() + cp.getQuantite());
            produitService.updateProduitStatus(produit);
            produitRepository.save(produit);
        }

        commandeRepository.deleteById(id);
    }

    @Override
    public List<CommandeEntity> findAll() {
        UserEntity currentUser = getCurrentUser();

        if (currentUser.getRole() == Role.ADMIN) {
            return commandeRepository.findAll();
        } else if (currentUser.getRole() == Role.SERVEUR) {
            return commandeRepository.findByServeurId((long) currentUser.getId());
        } else if (currentUser.getRole() == Role.CUISINIER) {
            // Cuisinier voit les commandes EN_ATTENTE et celles qu'il prépare
            return commandeRepository.findByStatusOrCuisinierAndStatus(
                    StatutCommande.EN_ATTENTE,
                    currentUser,
                    StatutCommande.EN_PREPARATION
            );
        }
        return List.of();
    }

    @Override
    public CommandeEntity findById(int id) {
        UserEntity currentUser = getCurrentUser();
        CommandeEntity commande = commandeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Commande introuvable"));

        // Vérifier que l'utilisateur a le droit de voir cette commande
        if (currentUser.getRole() == Role.SERVEUR && !(commande.getServeur().getId()==currentUser.getId())) {
            throw new AuthorizationDeniedException("Vous ne pouvez voir que vos propres commandes");
        } else if (currentUser.getRole() == Role.CUISINIER && commande.getStatus() != StatutCommande.EN_PREPARATION) {
            throw new AuthorizationDeniedException("Un cuisinier ne peut voir que les commandes en préparation");
        }

        return commande;
    }

    @Transactional
    public CommandeEntity updateStatus(int id, String status) {
        UserEntity currentUser = getCurrentUser();
        CommandeEntity commande = commandeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Commande introuvable"));
        StatutCommande newStatus = StatutCommande.valueOf(status);
        StatutCommande currentStatus = commande.getStatus();
        // Vérification des permissions par rôle
        switch (currentUser.getRole()) {
            case CUISINIER:
                handleCuisinierStatusUpdate(commande, currentUser, currentStatus, newStatus);
                break;
            case SERVEUR:
                handleServeurStatusUpdate(commande, currentUser, currentStatus, newStatus);
                break;
            case ADMIN:
                // L'admin peut tout faire, pas de restrictions
                break;
            default:
                throw new AuthorizationDeniedException("Rôle non autorisé pour cette action");
        }
        // Validation des transitions de statut
        validateStatusTransition(currentStatus, newStatus);
        if(newStatus==StatutCommande.PRET){
            commande.setEstPaye(true);
            commande.setDatePaiement(LocalDateTime.now());
        }
        // Mise à jour du statut
        commande.setStatus(newStatus);
        return commandeRepository.save(commande);
    }

    private void handleCuisinierStatusUpdate(CommandeEntity commande, UserEntity cuisinier,
                                             StatutCommande currentStatus, StatutCommande newStatus) {
        // Un cuisinier peut :
        // 1. Prendre une commande EN_ATTENTE (→ EN_PREPARATION)
        // 2. Marquer sa commande comme PRETE (EN_PREPARATION → PRET)
        if (newStatus == StatutCommande.EN_PREPARATION && currentStatus == StatutCommande.EN_ATTENTE) {
            commande.setCuisinier(cuisinier); // Attribution automatique
        }
        else if (newStatus == StatutCommande.PRET && currentStatus == StatutCommande.EN_PREPARATION) {
            // Vérifier que le cuisinier est bien celui qui a pris la commande
            if (!cuisinier.equals(commande.getCuisinier())) {
                throw new AuthorizationDeniedException("Vous ne pouvez pas modifier une commande que vous n'avez pas prise en charge");
            }
        }
        else {
            throw new AuthorizationDeniedException(
                    String.format("Transition non autorisée: %s → %s", currentStatus, newStatus));
        }
    }

    private void handleServeurStatusUpdate(CommandeEntity commande, UserEntity serveur,
                                           StatutCommande currentStatus, StatutCommande newStatus) {
        // Un serveur ne peut modifier que ses propres commandes
        if (!serveur.equals(commande.getServeur())) {
            throw new AuthorizationDeniedException("Vous ne pouvez modifier que vos propres commandes");
        }
        // Un serveur peut seulement :
        // - Marquer comme SERVI (PRET → SERVI)
        // - Annuler (EN_ATTENTE → ANNULEE)
        if (!(newStatus == StatutCommande.SERVI && currentStatus == StatutCommande.PRET) &&
                !(newStatus == StatutCommande.ANNULEE && currentStatus == StatutCommande.EN_ATTENTE)) {
            throw new AuthorizationDeniedException(
                    String.format("Transition non autorisée: %s → %s", currentStatus, newStatus));
        }
    }
    private void validateStatusTransition(StatutCommande current, StatutCommande newStatus) {
        // Validations globales (applicables à tous les rôles)
        if (current == StatutCommande.SERVI || current == StatutCommande.PAYEE) {
            throw new IllegalStateException("Une commande servie ou payée ne peut plus être modifiée");
        }
        if (current == StatutCommande.ANNULEE) {
            throw new IllegalStateException("Une commande annulée ne peut plus être modifiée");
        }
    }

    @Transactional
    public CommandeEntity payerCommande(int id) {
        UserEntity currentUser = getCurrentUser();
        CommandeEntity commande = commandeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Commande introuvable"));

        // Vérifier que l'utilisateur a le droit de payer cette commande
        if (currentUser.getRole() == Role.SERVEUR && !(commande.getServeur().getId() ==currentUser.getId())) {
            throw new AuthorizationDeniedException("Vous ne pouvez payer que vos propres commandes");
        }

        // Vérifier que la commande peut être payée
        if (commande.getStatus() != StatutCommande.SERVI) {
            throw new AuthorizationDeniedException("Seules les commandes servies peuvent être payées");
        }

        if (commande.isEstPaye()) {
            throw new AuthorizationDeniedException("Cette commande a déjà été payée");
        }

        // Marquer comme payée
        commande.setEstPaye(true);
        commande.setDatePaiement(LocalDateTime.now());

        // Générer la facture PDF
        String factureUrl = factureService.genererEtSauvegarderFacture((long) commande.getId());
        //commande.setFactureUrl(factureUrl);
        return commandeRepository.save(commande);
    }

    public List<CommandeDto> searchCommande(String client, LocalDate date) {
        UserEntity currentUser = getCurrentUser();
        List<CommandeEntity> commandes;

        // Adapter la recherche selon le rôle
        if (currentUser.getRole() == Role.ADMIN) {
            // L'admin peut tout chercher
            if (date != null) {
                commandes = commandeRepository.findByDate(date);
            } else {
                commandes = commandeRepository.findAll();
            }
        } else if (currentUser.getRole() == Role.SERVEUR) {
            // Le serveur ne peut chercher que dans ses commandes
            if (date != null) {
                commandes = commandeRepository.findByServeurIdAndDate((long) currentUser.getId(), date);
            } else {
                commandes = commandeRepository.findByServeurId((long) currentUser.getId());
            }
        } else if (currentUser.getRole() == Role.CUISINIER) {
            // Le cuisinier ne peut chercher que dans les commandes en préparation
            if (date != null) {
                commandes = commandeRepository.findByStatusAndDate(StatutCommande.EN_PREPARATION, date);
            } else {
                commandes = commandeRepository.findByStatus(StatutCommande.EN_PREPARATION);
            }
        } else {
            commandes = List.of();
        }

        return commandes.stream()
                .map(commandeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public CommandeEntity annulerCommande(int id) {
        UserEntity currentUser = getCurrentUser();
        CommandeEntity commande = commandeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Commande introuvable"));
        // Vérifier que l'utilisateur a le droit d'annuler cette commande
        if (currentUser.getRole() == Role.SERVEUR && commande.getServeur().getId() != currentUser.getId()) {
            throw new AuthorizationDeniedException("Vous ne pouvez annuler que vos propres commandes");
        }
        // Vérifier que la commande peut être annulée (seulement si elle est en attente ou en préparation)
        if (commande.getStatus() != StatutCommande.EN_ATTENTE &&
                commande.getStatus() != StatutCommande.EN_PREPARATION) {
            throw new AuthorizationDeniedException("Seules les commandes en attente ou en préparation peuvent être annulées");
        }
        // Si la commande est déjà payée, elle ne peut pas être annulée
        if (commande.isEstPaye()) {
            throw new AuthorizationDeniedException("Une commande déjà payée ne peut pas être annulée");
        }
        // Remettre les produits en stock
        for (CommandeProduit cp : commande.getCommandeProduits()) {
            ProduitEntity produit = cp.getProduit();
            produit.setQuantite(produit.getQuantite() + cp.getQuantite());
            produitService.updateProduitStatus(produit);
            produitRepository.save(produit);
        }
        // Marquer la commande comme annulée
        commande.setStatus(StatutCommande.ANNULEE);
        // Libérer la table si elle était occupée
        TableEntity table = commande.getTable();
        if (table != null) {
            table.setOccupee(false);
            tableRepository.save(table);
        }
        return commandeRepository.save(commande);
    }

    // Stats pour le dashboard admin
    public Map<String, Object> getStatistiquesJour() {
        // Vérifier que l'utilisateur est admin
        UserEntity currentUser = getCurrentUser();
        if (currentUser.getRole() != Role.ADMIN) {
            throw new AuthorizationDeniedException("Seuls les administrateurs peuvent accéder aux statistiques");
        }

        LocalDate today = LocalDate.now();

        // Nombre de commandes du jour
        long nombreCommandes = commandeRepository.countByDateBetween(
                today.atStartOfDay(),
                today.plusDays(1).atStartOfDay()
        );

        // Total des ventes en cours
        double totalVentes = commandeRepository.sumMontantTotalByDateBetweenAndEstPaye(
                today.atStartOfDay(),
                today.plusDays(1).atStartOfDay(),
                true
        );

        // Plat le plus commandé du jour
        List<Object[]> platsPlusCommandes = commandeProduitRepository.findMostOrderedProductsForDay(
                today.atStartOfDay(),
                today.plusDays(1).atStartOfDay()
        );

        return Map.of(
                "nombreCommandes", nombreCommandes,
                "totalVentes", totalVentes,
                "platsPlusCommandes", platsPlusCommandes
        );
    }

    // Helper pour récupérer l'utilisateur courant
    private UserEntity getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalStateException("Utilisateur non authentifié"));
    }
}