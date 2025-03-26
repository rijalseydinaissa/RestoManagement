package org.odc.gestionstockapp.Web.Mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.odc.gestionstockapp.Datas.Entities.TableEntity;
import org.odc.gestionstockapp.Web.Dtos.TableDto;
import org.odc.gestionstockapp.Web.Dtos.TableDtoUpdate;


@Mapper(componentModel = "spring")
public interface TableMapper {
    // Map TableDto vers TableEntity
    TableEntity toEntity(TableDto tableDto);
    // Map TableEntity vers TableDto
    TableDto toDto(TableEntity tableEntity);
    // Map TableDtoUpdate vers TableEntity
    TableEntity toEntity(TableDtoUpdate tableDtoUpdate);
}

