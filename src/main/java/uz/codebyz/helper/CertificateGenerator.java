//package uz.codebyz.helper;
//
//import org.apache.pdfbox.pdmodel.PDDocument;
//import org.apache.pdfbox.pdmodel.PDPage;
//import org.apache.pdfbox.pdmodel.PDPageContentStream;
//import org.apache.pdfbox.pdmodel.common.PDRectangle;
//import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
//import uz.codebyz.onlinecoursebackend.certificate.ccertificateDto.SkillDto;
//
//import javax.imageio.ImageIO;
//import java.awt.*;
//import java.awt.font.FontRenderContext;
//import java.awt.geom.Rectangle2D;
//import java.awt.geom.RoundRectangle2D;
//import java.awt.image.BufferedImage;
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//import java.util.List;
//import java.util.Locale;
//import java.util.UUID;
//
//public class CertificateGenerator {
//
//    // ============================================================
//    //  FORMAT ENUM (FULL SUPPORT)
//    // ============================================================
//
//    public enum CertificateType {
//
//        PNG("png"), JPG("jpg"), JPEG("jpeg"),
//        GIF("gif"), BMP("bmp"),
//        TIF("tif"), TIFF("tiff"),
//        PDF("pdf");
//
//        private final String ext;
//
//        CertificateType(String ext) {
//            this.ext = ext;
//        }
//
//        public String ext() { return ext; }
//
//        public static CertificateType detect(Path outputPath) {
//            String fn = outputPath.toString().toLowerCase();
//            for (CertificateType t : values()) {
//                if (fn.endsWith("." + t.ext)) return t;
//            }
//            throw new RuntimeException("Unsupported file format: " + fn);
//        }
//    }
//
//
//    // ============================================================
//    //  PUBLIC API — FORMAT AUTO DETECT
//    // ============================================================
//
//    public static String generateCertificate(
//            String fullName,
//            String courseName,
//            LocalDate date,
//            List<SkillDto> skills,
//            Path outputPath,
//            String verifyUrl,
//            CertificateType type,
//            UUID certificateUnique
//    ) throws Exception {
//        String certificateId = certificateUnique.toString();
//        // asosiy generatsiya
//        generateCertificateWithType(
//                fullName,
//                courseName,
//                date,
//                skills,
//                outputPath,
//                type,
//                certificateId,
//                verifyUrl
//        );
//
//        // ✔ serverga saqlangan real fayl yo‘li qaytadi
//        return outputPath.toString();
//    }
//
//
//
//    // ============================================================
//    //  PUBLIC API (WITH TYPE)
//    // ============================================================
//
//    public static void generateCertificateWithType(
//            String fullName,
//            String courseName,
//            LocalDate date,
//            List<SkillDto> skills,
//            Path outputPath,
//            CertificateType type,
//            String certificateId,
//            String verifyUrl
//    ) throws Exception {
//
//        int width = 1600;
//
//        int count = skills == null ? 0 : skills.size();
//        int rows = (count == 0) ? 0 : (int) Math.ceil(count / (double) getItemsPerRow(count));
//
//        int gapY = 200;
//        int baseHeight = 1200;
//        int skillBlock = rows * gapY + 250;
//        int height = baseHeight + skillBlock;
//
//        // CANVAS
//        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
//        Graphics2D g = img.createGraphics();
//        enableAA(g);
//
//        paintWhiteBackground(g, width, height);
//        drawRealCertificateBorder(g, width, height);
//
//        int y = drawHeader(g, width);
//        y = drawTitleBlock(g, width, y, fullName, courseName, certificateId);
//        y += 80;
//        y = drawSkillSection(g, width, y, skills);
//
//        drawFooter(g, width, height, date, verifyUrl);
//        g.dispose();
//
//        // Papka yo'q bo'lsa — yaratamiz
//        if (!Files.exists(outputPath.getParent())) {
//            Files.createDirectories(outputPath.getParent());
//        }
//
//        // FAYLNI SAQLASH — formatga qarab
//        if (type == CertificateType.PDF) {
//            // PDF sifatida
//            saveAsPdf(img, outputPath);
//        } else {
//            // JPG/PNG/Webp...
//            ImageIO.write(img, type.ext(), outputPath.toFile());
//        }
//    }
//
//
//
//    // ============================================================
//    //  SAVE AS PDF (PDFBOX)
//    // ============================================================
//
//    private static void saveAsPdf(BufferedImage img, Path output) throws IOException {
//
//        try (PDDocument doc = new PDDocument()) {
//
//            PDPage page = new PDPage(new PDRectangle(img.getWidth(), img.getHeight()));
//            doc.addPage(page);
//
//            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
//
//                var pdImage = LosslessFactory.createFromImage(doc, img);
//
//                cs.drawImage(
//                        pdImage,
//                        0,
//                        0,
//                        img.getWidth(),
//                        img.getHeight()
//                );
//            }
//
//            doc.save(output.toFile());
//        }
//    }
//
//
//    // ==================================================================
//    //  ASL KOD – SENING HAMMA FUNKSIYALARING QOLDIRILGAN HOLICHA
//    // ==================================================================
//
//    private static void paintWhiteBackground(Graphics2D g, int width, int height) {
//        g.setColor(Color.WHITE);
//        g.fillRect(0, 0, width, height);
//    }
//
//    private static void drawRealCertificateBorder(Graphics2D g, int width, int height) {
//
//        g.setColor(new Color(252, 250, 245));
//        g.fillRect(0, 0, width, height);
//
//        g.setStroke(new BasicStroke(10f));
//        g.setColor(new Color(34, 74, 154));
//        g.drawRoundRect(20, 20, width - 40, height - 40, 35, 35);
//
//        g.setStroke(new BasicStroke(5f));
//        g.setColor(new Color(22, 54, 124));
//        g.drawRoundRect(45, 45, width - 90, height - 90, 25, 25);
//
//        g.setStroke(new BasicStroke(2f));
//        g.setColor(new Color(140, 140, 180));
//        for (int i = 0; i < 40; i++) {
//            int offset = 70 + (i * 8);
//            g.drawRoundRect(offset, offset, width - offset * 2, height - offset * 2, 15, 15);
//        }
//
//        g.setColor(Color.WHITE);
//        g.fillRoundRect(90, 90, width - 180, height - 180, 20, 20);
//    }
//
//    private static int drawHeader(Graphics2D g, int width) {
//
//        int logoSize = 150;
//        int logoX = width / 2 - 400;
//        int logoY = 100;
//
//        drawRoundedLogo(g, logoX, logoY, logoSize);
//
//        String text = "CODEBYZ PLATFORM";
//
//        g.setFont(new Font("Arial", Font.BOLD, 48));
//        g.setColor(new Color(33, 33, 33));
//
//        FontMetrics fm = g.getFontMetrics();
//        int tw = fm.stringWidth(text);
//        int tx = width / 2 - tw / 2;
//        int ty = logoY + logoSize / 2 + fm.getAscent() / 2;
//
//        g.drawString(text, tx + 200, ty);
//
//        return logoY + logoSize + 40;
//    }
//
//    private static void drawRoundedLogo(Graphics2D g, int x, int y, int size) {
//        try {
//            File f = new File("uploads/logo/codebyz.png");
//            BufferedImage logo = ImageIO.read(f);
//
//            RoundRectangle2D bg = new RoundRectangle2D.Double(x, y, size, size, 35, 35);
//            g.setColor(new Color(245, 245, 245));
//            g.fill(bg);
//
//            g.setColor(new Color(100, 149, 237));
//            g.setStroke(new BasicStroke(3f));
//            g.draw(bg);
//
//            double scale = (double) (size - 20) / Math.max(logo.getWidth(), logo.getHeight());
//            int w = (int) (logo.getWidth() * scale);
//            int h = (int) (logo.getHeight() * scale);
//
//            int lx = x + (size - w) / 2;
//            int ly = y + (size - h) / 2;
//
//            g.drawImage(logo.getScaledInstance(w, h, Image.SCALE_SMOOTH), lx, ly, null);
//
//        } catch (Exception e) {
//            g.setColor(Color.BLACK);
//            g.drawString("LOGO", x, y + size / 2);
//        }
//    }
//
//
//    private static int drawTitleBlock(Graphics2D g, int width, int startY,
//                                      String fullName, String courseName, String certificateId) {
//
//        int y = startY + 40;
//
//        g.setFont(new Font("Georgia", Font.BOLD, 72));
//        g.setColor(new Color(33, 33, 33));
//        center(g, "SERTIFIKAT", width, y);
//
//        g.setStroke(new BasicStroke(4f));
//        g.setColor(new Color(100, 149, 237));
//
//        int lineW = 600;
//        g.drawLine(width / 2 - lineW / 2, y + 15, width / 2 + lineW / 2, y + 15);
//
//        y += 90;
//
//        g.setFont(new Font("Arial", Font.PLAIN, 26));
//        g.setColor(new Color(90, 90, 90));
//        center(g,
//                "Ushbu sertifikat quyidagi shaxs kursni muvaffaqiyatli tamomlaganligini tasdiqlaydi:",
//                width, y);
//
//        y += 80;
//
//        g.setFont(new Font("Times New Roman", Font.BOLD, 52));
//        g.setColor(new Color(30, 30, 30));
//        center(g, fullName.toUpperCase(Locale.ROOT), width, y);
//
//        y += 60;
//
//        g.setFont(new Font("Arial", Font.BOLD, 30));
//        g.setColor(new Color(54, 120, 196));
//        center(g, courseName, width, y);
//
//        y += 50;
//
//        g.setFont(new Font("Arial", Font.PLAIN, 20));
//        g.setColor(new Color(140, 140, 140));
//        center(g, "S/R: " + certificateId, width, y);
//
//        return y;
//    }
//
//
//    private static int drawSkillSection(Graphics2D g, int width, int startY, List<SkillDto> skills) {
//
//        g.setFont(new Font("Arial", Font.BOLD, 36));
//        g.setColor(new Color(54, 120, 196));
//        center(g, "O'ZLASHTIRGAN TEXNOLOGIYALAR:", width, startY);
//
//        int y = startY + 80;
//
//        if (skills == null || skills.isEmpty())
//            return y + 30;
//
//        return drawSkillGrid(g, width, y, skills);
//    }
//
//
//    private static int drawSkillGrid(Graphics2D g, int width, int y, List<SkillDto> skills) {
//
//        int count = skills.size();
//        int perRow = getItemsPerRow(count);
//
//        int cardW = 150;
//        int cardH = 150;
//        int gapX = 80;
//        int gapY = 200;
//
//        int totalW = perRow * cardW + (perRow - 1) * gapX;
//        int startX = (width - totalW) / 2;
//
//        int lastY = y;
//
//        for (int i = 0; i < count; i++) {
//
//            int col = i % perRow;
//            int row = i / perRow;
//
//            int x = startX + col * (cardW + gapX);
//            int yy = y + row * gapY;
//
//            lastY = yy;
//
//            drawSkillCard(g, skills.get(i), x, yy, cardW, cardH);
//        }
//
//        return lastY + gapY;
//    }
//
//
//    private static void drawSkillCard(Graphics2D g, SkillDto dto, int x, int y, int w, int h) {
//
//        RoundRectangle2D rect = new RoundRectangle2D.Double(x, y, w, h, 28, 28);
//
//        g.setColor(new Color(250, 250, 250));
//        g.fill(rect);
//
//        g.setColor(new Color(200, 200, 200));
//        g.setStroke(new BasicStroke(3f));
//        g.draw(rect);
//
//        drawSkillIcon(g, dto, x, y, w, h);
//
//        g.setFont(new Font("Arial", Font.BOLD, 20));
//        g.setColor(new Color(33, 33, 33));
//        center(g, dto.getName(), x, y + h + 30, w);
//    }
//
//
//    private static void drawSkillIcon(Graphics2D g, SkillDto dto, int x, int y, int w, int h) {
//        try {
//
//            File f = new File(dto.getIconUrl());
//            BufferedImage icon = ImageIO.read(f);
//
//            int iw = icon.getWidth();
//            int ih = icon.getHeight();
//
//            double scale = (double) (w - 40) / Math.max(iw, ih);
//
//            int rw = (int) (iw * scale);
//            int rh = (int) (ih * scale);
//
//            int dx = x + (w - rw) / 2;
//            int dy = y + (h - rh) / 2;
//
//            g.drawImage(icon.getScaledInstance(rw, rh, Image.SCALE_SMOOTH), dx, dy, null);
//
//        } catch (Exception e) {
//            g.setColor(Color.GRAY);
//            g.drawString("ICON", x + w / 2 - 20, y + h / 2);
//        }
//    }
//
//
//    private static void drawSignatureImage(Graphics2D g, int x, int y) {
//        try {
//            File f = new File("uploads/signature.png");
//            BufferedImage sig = ImageIO.read(f);
//
//            if (sig != null) {
//
//                int targetH = 80;
//                double scale = (double) targetH / sig.getHeight();
//
//                int w = (int) (sig.getWidth() * scale);
//                int h = targetH;
//
//                g.drawImage(
//                        sig.getScaledInstance(w, h, Image.SCALE_SMOOTH),
//                        x, y - h,
//                        null
//                );
//            }
//
//        } catch (Exception e) {
//            g.setColor(Color.GRAY);
//            g.drawString("SIGNATURE", x, y);
//        }
//    }
//
//
//    private static void drawFooter(Graphics2D g, int width, int height,
//                                   LocalDate date, String verifyUrl) {
//
//        int y = height - 120;
//
//        g.setFont(new Font("Arial", Font.PLAIN, 24));
//        g.setColor(new Color(90, 90, 90));
//        g.drawString("SANA:", 120, y);
//
//        g.setFont(new Font("Arial", Font.BOLD, 26));
//        g.drawString(date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")), 200, y);
//
//        String label = "IMZO";
//        g.setFont(new Font("Arial", Font.PLAIN, 24));
//        int lw = g.getFontMetrics().stringWidth(label);
//
//        int xRight = width - 500;
//        g.drawString(label, xRight, y);
//
//        g.setColor(new Color(160, 160, 160));
//        g.drawLine(xRight + lw + 20, y - 10, width - 120, y - 10);
//
//        drawSignatureImage(g, xRight + lw + 20, y - 10);
//
//        g.setFont(new Font("Arial", Font.BOLD, 22));
//        g.setColor(new Color(54, 120, 196));
//        g.drawString("CodeByZ Platform", xRight + lw + 20, y + 35);
//
//        if (verifyUrl != null) {
//            g.setFont(new Font("Arial", Font.PLAIN, 16));
//            g.setColor(new Color(120, 120, 120));
//            center(g, "Tasdiqlash uchun: " + verifyUrl, width, height - 40);
//        }
//    }
//
//
//    private static void enableAA(Graphics2D g) {
//        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
//    }
//
//    private static void center(Graphics2D g, String text, int width, int y) {
//        FontRenderContext frc = g.getFontRenderContext();
//        Rectangle2D rect = g.getFont().getStringBounds(text, frc);
//        int tw = (int) rect.getWidth();
//        int x = (width - tw) / 2;
//        g.drawString(text, x, y);
//    }
//
//    private static void center(Graphics2D g, String text, int x, int y, int width) {
//        FontRenderContext frc = g.getFontRenderContext();
//        Rectangle2D rect = g.getFont().getStringBounds(text, frc);
//        int tw = (int) rect.getWidth();
//        int xx = x + (width - tw) / 2;
//        g.drawString(text, xx, y);
//    }
//
//    private static int getItemsPerRow(int count) {
//        if (count <= 1) return 1;
//        if (count <= 4) return 2;
//        if (count <= 8) return 3;
//        return 4;
//    }
//
//    private static String generateCertificateId() {
//        String random = UUID.randomUUID().toString().substring(0, 6).toUpperCase(Locale.ROOT);
//        return "CBZ-" + random;
//    }
//}
