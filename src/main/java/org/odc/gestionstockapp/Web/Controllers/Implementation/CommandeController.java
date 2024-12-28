package org.odc.gestionstockapp.Web.Controllers.Implementation;


import org.odc.gestionstockapp.Datas.Entities.CommandeEntity;
import org.odc.gestionstockapp.Services.Implementation.CommandeService;
import org.odc.gestionstockapp.Web.Controllers.Interface.CrudController;
import org.odc.gestionstockapp.Web.Dtos.CommandeDto;
import org.odc.gestionstockapp.Web.Dtos.CommandeProduitDto;
import org.odc.gestionstockapp.Web.Mappers.CommandeMapper;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class CommandeController implements CrudController<CommandeEntity, CommandeDto, CommandeProduitDto> {
    private final CommandeService commandeService;
    private final CommandeMapper commandeMapper;
    public CommandeController(CommandeService commandeService, CommandeMapper commandeMapper) {
        this.commandeService = commandeService;
        this.commandeMapper  = commandeMapper;
    }
    @Override
    @PostMapping("/commandes")
    public CommandeEntity create(@RequestBody CommandeDto commandeDto) {return commandeService.create(commandeDto);}

    @Override
    @PostMapping("/updates")
    public CommandeEntity update(@PathVariable int id, @RequestBody CommandeProduitDto commandeProduitDto) {
        return commandeService.update(commandeProduitDto);
    }

    @Override
    @DeleteMapping("/commandes{id}")
    public void delete(@PathVariable int id) {commandeService.delete(id);}

    @Override
    @GetMapping("/commandes/{id}")
    public CommandeEntity getById(@PathVariable int id) {return commandeService.findById(id);}

    @Override
    @GetMapping("/commandes")
    public List<CommandeEntity> getAll() {return commandeService.findAll();}
    @GetMapping("/commandes/search")
    public List<CommandeDto> searchCommande(@PathVariable String client, @PathVariable LocalDate date) {return commandeService.searchCommande(client, date);}

}
