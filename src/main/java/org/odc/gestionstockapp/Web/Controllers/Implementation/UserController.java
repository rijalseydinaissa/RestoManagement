package org.odc.gestionstockapp.Web.Controllers.Implementation;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.odc.gestionstockapp.Datas.Entities.UserEntity;
import org.odc.gestionstockapp.Services.Implementation.UserService;
import org.odc.gestionstockapp.Web.Controllers.Interface.CrudController;
import org.odc.gestionstockapp.Web.Dtos.UserDto;
import org.odc.gestionstockapp.Web.Dtos.UserDtoUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "API pour la gestion des utilisateurs")
public class UserController implements CrudController<UserEntity, UserDto, UserDtoUpdate> {
    @Autowired
    private UserService userService;

    @PostMapping
    @Override
    public UserEntity create(@RequestBody UserDto userDto) {
        return userService.create(userDto);
    }

    @PutMapping("/{id}")
    @Override
    public UserEntity update(@PathVariable int id, @RequestBody UserDtoUpdate userDtoUpdate) {
        return userService.update(userDtoUpdate);
    }

    @DeleteMapping("/{id}")
    @Override
    public void delete(@PathVariable int id) {
        userService.delete(id);
    }


    @GetMapping("/{id}")
    @Override
    public UserEntity getById(@PathVariable int id) {
        return userService.findById(id);
    }

    @GetMapping
    @Override
    public List<UserEntity> getAll() {
        return userService.findAll();
    }
}
