# Étape 1 : Utilisation de l'image officielle de Java 21
FROM openjdk:21-jdk-slim

# Étape 2 : Définition du répertoire de travail
WORKDIR /app

# Étape 3 : Copier d'abord uniquement les fichiers nécessaires pour Maven
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Étape 4 : Télécharger les dépendances
RUN chmod +x ./mvnw
RUN ./mvnw dependency:go-offline

# Étape 5 : Copier le reste des sources
COPY src ./src

# Étape 6 : Construire l'application
RUN ./mvnw clean package -DskipTests

# Étape 7 : Exposer le port 8081
EXPOSE 8081

# Étape 8 : Commande pour exécuter l'application
CMD ["java", "-jar", "target/gestionstockapp-0.0.1-SNAPSHOT.jar"]