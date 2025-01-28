package org.odc.gestionstockapp.Services.Implementation;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.layout.borders.Border;
import org.odc.gestionstockapp.Datas.Entities.CommandeEntity;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import org.odc.gestionstockapp.Datas.Entities.CommandeProduit;
import org.odc.gestionstockapp.Datas.Entities.FactureEntity;
import org.odc.gestionstockapp.Datas.Repositories.CommandeRepository;
import org.odc.gestionstockapp.Datas.Repositories.FactureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;


@Service
public class FactureService {
    @Autowired
    private FactureRepository factureRepository;

    @Autowired
    private CommandeRepository commandeRepository;

    @Value("${app.base-url}")
    private String baseUrl;

    public String genererEtSauvegarderFacture(Long commandeId) {
        CommandeEntity commande = commandeRepository.findById(Math.toIntExact(commandeId))
                .orElseThrow(() -> new RuntimeException("Commande non trouvée"));

        byte[] pdfContent = genererContenuPDF(commande);

        FactureEntity facture = new FactureEntity();
        facture.setCommandeId((long) commande.getId());
        facture.setContenuPdf(pdfContent);
        facture.setDateGeneration(LocalDateTime.now());

        String urlTelechargement = UUID.randomUUID().toString();
        facture.setUrlTelechargement(urlTelechargement);

        factureRepository.save(facture);

        return baseUrl + "/telecharger-facture/" + urlTelechargement;
    }

    private byte[] genererContenuPDF(CommandeEntity commande) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (PdfWriter writer = new PdfWriter(baos);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            // Couleurs personnalisées
            Color bleuPrincipal = new DeviceRgb(44, 62, 80);
            Color vertAccent = new DeviceRgb(39, 174, 96);

            // Titre de la facture
            Paragraph titre = new Paragraph("FACTURE")
                    .setFontSize(24)
                    .setFontColor(bleuPrincipal)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(titre);

            // Tableau des informations
            Table infoTable = new Table(2).useAllAvailableWidth();
            infoTable.addCell(createInfoCell("Numéro de commande:"));
            infoTable.addCell(createInfoCell(String.valueOf(commande.getId())));
            infoTable.addCell(createInfoCell("Date:"));
            infoTable.addCell(createInfoCell(LocalDate.now().toString()));
            document.add(infoTable);

            // Tableau des articles
            Table tableArticles = new Table(4).useAllAvailableWidth();
            String[] headers = {"Produit", "Quantité", "Prix Unitaire", "Total"};
            for (String header : headers) {
                tableArticles.addHeaderCell(createHeaderCell(header, bleuPrincipal));
            }

            // Remplir les lignes d'articles
            for (CommandeProduit article : commande.getCommandeProduits()) {
                tableArticles.addCell(createCell(article.getProduit().getNom()));
                tableArticles.addCell(createCell(String.valueOf(article.getQuantite())));
                tableArticles.addCell(createCell(String.format("%.2f Fcfa", article.getProduit().getPrix())));
                tableArticles.addCell(createCell(String.format("%.2f Fcfa", article.getQuantite() * article.getProduit().getPrix())));
            }
            document.add(tableArticles);

            // Total
            Paragraph total = new Paragraph("Total: " + String.format("%.2f Fcfa", commande.getMontantTotal()))
                    .setFontSize(16)
                    .setFontColor(vertAccent)
                    .setTextAlignment(TextAlignment.RIGHT);
            document.add(total);

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération de la facture", e);
        }

        return baos.toByteArray();
    }

    // Méthodes utilitaires pour créer des cellules stylisées
    private Cell createHeaderCell(String text, Color backgroundColor) {
        return new Cell()
                .add(new Paragraph(text))
                .setBackgroundColor(backgroundColor)
                .setFontColor(ColorConstants.WHITE)
                .setTextAlignment(TextAlignment.CENTER);
    }

    private Cell createCell(String text) {
        return new Cell()
                .add(new Paragraph(text))
                .setTextAlignment(TextAlignment.CENTER);
    }

    private Cell createInfoCell(String text) {
        return new Cell()
                .add(new Paragraph(text))
                .setBorder(Border.NO_BORDER);
    }
}