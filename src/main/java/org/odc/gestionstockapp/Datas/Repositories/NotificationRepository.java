package org.odc.gestionstockapp.Datas.Repositories;

import org.odc.gestionstockapp.Datas.Entities.NotificationEntity;
import org.odc.gestionstockapp.Datas.Entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
    List<NotificationEntity> findByUserAndLueOrderByDateCreationDesc(UserEntity user, boolean lue);
    long countByUserAndLue(UserEntity user, boolean lue);
    List<NotificationEntity> findByUserOrderByDateCreationDesc(UserEntity currentUser);
}