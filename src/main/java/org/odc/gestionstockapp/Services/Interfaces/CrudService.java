package org.odc.gestionstockapp.Services.Interfaces;

import org.odc.gestionstockapp.Datas.Entities.CommandeEntity;
import org.odc.gestionstockapp.Web.Dtos.CommandeProduitDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CrudService<T,Dto,DtoUpdate> {
    T create(Dto t);
    T update(DtoUpdate t);
    void delete(int id);
    List<T> findAll();
    T findById(int id);
}
