package org.odc.gestionstockapp.Web.Controllers.Interface;

import java.util.List;

public interface CrudController<T,Dto,DtoUpdate> {
   T create(Dto dto);
   T update(int id ,DtoUpdate dtoUpdate);
   void delete(int id);
   T getById(int id);
   List<T> getAll();
}
