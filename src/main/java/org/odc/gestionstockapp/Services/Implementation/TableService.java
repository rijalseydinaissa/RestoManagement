package org.odc.gestionstockapp.Services.Implementation;

import org.odc.gestionstockapp.Datas.Entities.TableEntity;
import org.odc.gestionstockapp.Datas.Repositories.TableRepository;
import org.odc.gestionstockapp.Services.Interfaces.CrudService;
import org.odc.gestionstockapp.Web.Dtos.TableDto;
import org.odc.gestionstockapp.Web.Dtos.TableDtoUpdate;
import org.odc.gestionstockapp.Web.Mappers.ProduitMapper;
import org.odc.gestionstockapp.Web.Mappers.TableMapper;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class TableService implements CrudService <TableEntity, TableDto, TableDtoUpdate> {

    private final TableRepository tableRepository; // Ajout du repository des tables
    private TableMapper tableMapper ;

    public TableService(TableRepository tableRepository,
                          TableMapper tabletMapper) {
        this.tableRepository = tableRepository;
        this.tableMapper = tableMapper;
    }

    @Override
    public TableEntity create(TableDto t) {
            TableEntity tableEntity =tableMapper.toEntity(t);
        return tableRepository.save(tableEntity);
    }

    @Override
    public TableEntity update(TableDtoUpdate t) {
        return null;
    }

    public TableEntity updateTable(int id ,boolean occuppe) {
        TableEntity tableEntity = tableRepository.findById(id).orElseThrow(() -> new RuntimeException("Table non trouvée"));
        tableEntity.setOccupee(occuppe);
        return tableRepository.save(tableEntity) ;
    }
    public List<TableEntity> getAvailableTables() {
        return tableRepository.findByOccupeeFalse();
    }

    @Override
    public void delete(int id) {
        tableRepository.deleteById(id);
    }

    @Override
    public List<TableEntity> findAll() {
        return tableRepository.findAll();
    }

    @Override
    public TableEntity findById(int id) {
        return tableRepository.findById(id).orElse(null);
    }
}
