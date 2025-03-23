package org.odc.gestionstockapp.Services.Implementation;

import org.odc.gestionstockapp.Datas.Entities.UserEntity;
import org.odc.gestionstockapp.Datas.Repositories.UserRepository;
import org.odc.gestionstockapp.Services.Interfaces.CrudService;
import org.odc.gestionstockapp.Web.Dtos.UserDto;
import org.odc.gestionstockapp.Web.Dtos.UserDtoUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.List;
@Service
public class UserService implements CrudService<UserEntity, UserDto, UserDtoUpdate> {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public UserEntity create(UserDto t) {
        return userRepository.save(
                UserEntity.builder()
                        .nom(t.getNom())
                        .prenom(t.getPrenom())
                        .email(t.getEmail())
                        .password(passwordEncoder.encode(t.getPassword()))
                        .role(t.getRole())
                .build());
    }

    @Override
    public UserEntity update(UserDtoUpdate t) {
        return null;
    }

    @Override
    public void delete(int id) {
        userRepository.deleteById(id);
    }

    @Override
    public List<UserEntity> findAll() {
        return userRepository.findAll();
    }

    @Override
    public UserEntity findById(int id) {
        return userRepository.findById(id).orElse(null);
    }
}
