package org.odc.gestionstockapp.Web.Mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.odc.gestionstockapp.Datas.Entities.ProduitEntity;
import org.odc.gestionstockapp.Web.Dtos.ProduitDto;
import org.odc.gestionstockapp.Web.Dtos.ProduitDtoUpdate;

@Mapper(componentModel = "spring")
public interface ProduitMapper {
    // Map ProduitDto vers ProduitEntity
    ProduitEntity toEntity(ProduitDto produitDto);

    // Map ProduitEntity vers ProduitDto
    ProduitDto toDto(ProduitEntity produitEntity);

    // Map ProduitDtoUpdate vers ProduitEntity
    ProduitEntity toEntity(ProduitDtoUpdate produitDtoUpdate);
}
