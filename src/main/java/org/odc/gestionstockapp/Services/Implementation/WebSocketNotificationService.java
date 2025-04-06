package org.odc.gestionstockapp.Services.Implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.odc.gestionstockapp.Datas.Entities.NotificationEntity;
import org.odc.gestionstockapp.Datas.Entities.UserEntity;
import org.odc.gestionstockapp.Datas.Repositories.NotificationRepository;
import org.odc.gestionstockapp.Services.Interfaces.NotificationService;
import org.odc.gestionstockapp.Web.Dtos.NotificationDto;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketNotificationService implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final JavaMailSender mailSender;

    @Override
    public void notifyCuisinier(UserEntity cuisinier, String message, Long commandeId) {
        NotificationEntity notification = createAndSaveNotification(cuisinier, message, commandeId);
        sendWebSocketNotification(cuisinier.getEmail(), convertToDto(notification));
        sendEmailNotification(cuisinier.getEmail(), "Nouvelle commande à préparer", message);
    }

    @Override
    public void notifyServeur(UserEntity serveur, String message, Long commandeId) {
        NotificationEntity notification = createAndSaveNotification(serveur, message, commandeId);
        sendWebSocketNotification(serveur.getEmail(), convertToDto(notification));
        sendEmailNotification(serveur.getEmail(), "Commande prête à servir", message);
    }

    @Async
    public void sendEmailNotification(String to, String subject, String text) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("Restaurant App - " + subject);
            helper.setText(createEmailContent(text), true); // true = HTML

            mailSender.send(message);
            log.info("Email envoyé à {}", to);
        } catch (MessagingException e) {
            log.error("Échec d'envoi d'email à {}: {}", to, e.getMessage());
        }
    }

    private String createEmailContent(String message) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head><style>body{font-family:Arial,sans-serif}</style></head>" +
                "<body>" +
                "<h2>Notification du Restaurant</h2>" +
                "<p>" + message + "</p>" +
                "<footer><small>© 2023 Votre Restaurant</small></footer>" +
                "</body></html>";
    }

    private void sendWebSocketNotification(String email, NotificationDto notification) {
        messagingTemplate.convertAndSendToUser(
                email,
                "/queue/notifications",
                notification
        );
        log.debug("Notification WS envoyée à {}", email);
    }

    private NotificationEntity createAndSaveNotification(UserEntity user, String message, Long commandeId) {
        NotificationEntity notification = NotificationEntity.builder()
                .user(user)
                .message(message)
                .commandeId(commandeId)
                .dateCreation(LocalDateTime.now())
                .lue(false)
                .build();
        return notificationRepository.save(notification);
    }

    private NotificationDto convertToDto(NotificationEntity entity) {
        return NotificationDto.builder()
                .id(entity.getId())
                .message(entity.getMessage())
                .commandeId(entity.getCommandeId())
                .dateCreation(entity.getDateCreation())
                .lue(entity.isLue())
                .build();
    }
}