package org.odc.gestionstockapp.Datas.Repositories;

import org.odc.gestionstockapp.Datas.Entities.UserEntity;
import org.odc.gestionstockapp.Datas.Enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity , Integer> {
    Optional<UserEntity> findByEmail(String email);

    List<UserEntity> findByRole(Role role);


    //Optional<UserEntity> findByUsername(String username);
}
