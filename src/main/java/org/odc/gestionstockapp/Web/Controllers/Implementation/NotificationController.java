package org.odc.gestionstockapp.Web.Controllers.Implementation;

import lombok.RequiredArgsConstructor;
import org.odc.gestionstockapp.Datas.Entities.NotificationEntity;
import org.odc.gestionstockapp.Datas.Entities.UserEntity;
import org.odc.gestionstockapp.Datas.Repositories.NotificationRepository;
import org.odc.gestionstockapp.Datas.Repositories.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<NotificationEntity>> getNotifications(@RequestParam(defaultValue = "false") boolean includeRead) {
        UserEntity currentUser = getCurrentUser();
        List<NotificationEntity> notifications;

        if (includeRead) {
            notifications = notificationRepository.findByUserOrderByDateCreationDesc(currentUser);
        } else {
            notifications = notificationRepository.findByUserAndLueOrderByDateCreationDesc(currentUser, false);
        }

        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getUnreadCount() {
        UserEntity currentUser = getCurrentUser();
        long count = notificationRepository.countByUserAndLue(currentUser, false);
        return ResponseEntity.ok(Map.of("count", count));
    }

    @PatchMapping("/{id}/mark-read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        UserEntity currentUser = getCurrentUser();
        NotificationEntity notification = notificationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notification introuvable"));

        // Vérifier que la notification appartient à l'utilisateur courant
        if (notification.getUser().getId() == (currentUser.getId())) {
            return ResponseEntity.status(403).build();
        }

        notification.setLue(true);
        notificationRepository.save(notification);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/mark-all-read")
    public ResponseEntity<Void> markAllAsRead() {
        UserEntity currentUser = getCurrentUser();
        List<NotificationEntity> unreadNotifications =
                notificationRepository.findByUserAndLueOrderByDateCreationDesc(currentUser, false);

        for (NotificationEntity notification : unreadNotifications) {
            notification.setLue(true);
        }

        notificationRepository.saveAll(unreadNotifications);
        return ResponseEntity.ok().build();
    }

    private UserEntity getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalStateException("Utilisateur non authentifié"));
    }
}