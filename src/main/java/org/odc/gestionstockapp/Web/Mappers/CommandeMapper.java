package org.odc.gestionstockapp.Web.Mappers;

import org.mapstruct.Mapper;
import org.odc.gestionstockapp.Datas.Entities.CommandeEntity;
import org.odc.gestionstockapp.Datas.Entities.CommandeProduit;
import org.odc.gestionstockapp.Web.Dtos.CommandeDto;
import org.odc.gestionstockapp.Web.Dtos.CommandeProduitDto;

@Mapper(componentModel = "spring")
public interface CommandeMapper {
    // Map CommandeDto vers CommandeEntity
    CommandeEntity toEntity(CommandeDto commandeDto);

    CommandeProduitDto toDto (CommandeProduit commandeProduit);
    // Map CommandeEntity vers CommandeDto
    CommandeDto toDto(CommandeEntity commandeEntity);
}
