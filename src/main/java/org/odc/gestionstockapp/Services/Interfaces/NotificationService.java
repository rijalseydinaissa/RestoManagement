package org.odc.gestionstockapp.Services.Interfaces;

import org.odc.gestionstockapp.Datas.Entities.UserEntity;

public interface NotificationService {
    void notifyCuisinier(UserEntity cuisinier, String message, Long commandeId);
    void notifyServeur(UserEntity serveur, String message, Long commandeId);
}