package utils;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class ReportMailer {

    public static final String fromEmail = "vamshi.qa6@gmail.com"; // Replace with your email
    public static final String password = "fmpl qtni nsnq sfvl";        // App-specific password
    public static final String toEmail = "vamshi.qa6@gmail.com";

    public static void sendSummaryReport() {
        String summaryPath = "allure-report/widgets/summary.json";

        try {
            Path path = Paths.get(summaryPath);

            if (!Files.exists(path)) {
                System.out.println("⚠️ summary.json not found. Skipping email sending.");
                return;
            }

            String summaryContent = Files.readString(path).trim();

            if (!summaryContent.startsWith("{")) {
                System.out.println("⚠️ Invalid summary.json content. Skipping email.");
                return;
            }

            JSONObject root = new JSONObject(summaryContent);

            int total = 0, passed = 0, failed = 0, skipped = 0;
            String title = "✅ Allure Test Execution Summary";

            // ✅ Try items[0].statistic first
            JSONObject statBlock = null;
            JSONArray items = root.optJSONArray("items");
            if (items != null && items.length() > 0) {
                statBlock = items.getJSONObject(0).optJSONObject("statistic");
            }

            // 🛡 Fallback to top-level "statistic"
            if (statBlock == null) {
                statBlock = root.optJSONObject("statistic");
            }

            if (statBlock != null) {
                total = statBlock.optInt("total", 0);
                passed = statBlock.optInt("passed", 0);
                failed = statBlock.optInt("failed", 0);
                skipped = statBlock.optInt("skipped", 0);
            }

            if (failed > 0) {
                title = "❌ Test Execution: Some Tests Failed";
            }

            // 📬 Prepare email content
            StringBuilder body = new StringBuilder();
            body.append("<h2>").append(title).append("</h2>")
                .append("<table border='1' cellpadding='10' cellspacing='0' style='border-collapse: collapse; text-align: center;'>")
                .append("<tr style='background-color:#f2f2f2;'>")
                .append("<th>Total</th><th style='color:green;'>Passed</th><th style='color:red;'>Failed</th><th style='color:orange;'>Skipped</th>")
                .append("</tr>")
                .append("<tr>")
                .append("<td>").append(total).append("</td>")
                .append("<td style='color:green;'>").append(passed).append("</td>")
                .append("<td style='color:red;'>").append(failed).append("</td>")
                .append("<td style='color:orange;'>").append(skipped).append("</td>")
                .append("</tr>")
                .append("</table><br>");

            body.append("<p><a href='file:///")
                .append(Paths.get("allure-report/index.html").toAbsolutePath().toString().replace("\\", "/"))
                .append("' target='_blank'>🔍 View Full Report</a></p>")
                .append("<p>Regards,<br><b>Automation Team</b></p>");

            // ✉️ Send mail
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");

            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(fromEmail, password);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(title);
            message.setContent(body.toString(), "text/html");

            Transport.send(message);
            System.out.println("[EMAIL] ✅ Summary email sent successfully!");

        } catch (Exception e) {
            System.err.println("❌ Email failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

}



//*****************************************************
//package utils;
//
//import jakarta.activation.DataHandler;
//import jakarta.activation.DataSource;
//import jakarta.activation.FileDataSource;
//import jakarta.mail.*;
//import jakarta.mail.internet.*;
//
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.nio.file.*;
//import java.util.Properties;
//import java.util.zip.ZipEntry;
//import java.util.zip.ZipOutputStream;
//
//public class ReportMailer {
//
//    public static void sendReport() {
//        final String fromEmail = "vamshi.qa6@gmail.com"; // Replace with your email
//        final String password = "****";     // Use App Password
//        final String toEmail = "vamshi.qa6@gmail.com";   // Recipient email
//
//        try {
//            // Step 1: Zip the allure-report folder
//            String zipFilePath = "./AllureReport.zip";
//            Path sourceDir = Paths.get("allure-report");
//            zipFolder(sourceDir, Paths.get(zipFilePath));
//
//            // Step 2: Setup mail properties
//            Properties props = new Properties();
//            props.put("mail.smtp.host", "smtp.gmail.com");
//            props.put("mail.smtp.port", "587");
//            props.put("mail.smtp.auth", "true");
//            props.put("mail.smtp.starttls.enable", "true");
//
//            Session session = Session.getInstance(props, new Authenticator() {
//                protected PasswordAuthentication getPasswordAuthentication() {
//                    return new PasswordAuthentication(fromEmail, password);
//                }
//            });
//
//            // Step 3: Compose the message
//            Message message = new MimeMessage(session);
//            message.setFrom(new InternetAddress(fromEmail));
//            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
//            message.setSubject("✅ Allure Test Report - Playwright Java");
//
//            BodyPart messageBodyPart = new MimeBodyPart();
//            messageBodyPart.setText("Hi Team,\n\nPlease find the attached Allure Test Report.\n\nRegards,\nAutomation Team");
//
//            Multipart multipart = new MimeMultipart();
//            multipart.addBodyPart(messageBodyPart);
//
//            // Attachment part
//            MimeBodyPart attachmentPart = new MimeBodyPart();
//            DataSource source = new FileDataSource(zipFilePath);
//            attachmentPart.setDataHandler(new DataHandler(source));
//            attachmentPart.setFileName("AllureReport.zip");
//            multipart.addBodyPart(attachmentPart);
//
//            message.setContent(multipart);
//
//            // Step 4: Send the message
//            Transport.send(message);
//            System.out.println("[EMAIL] Allure report sent successfully!");
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    // Utility: Zip the full allure-report folder
//    private static void zipFolder(Path sourceFolderPath, Path zipPath) throws IOException {
//        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipPath.toFile()))) {
//            Files.walk(sourceFolderPath).filter(path -> !Files.isDirectory(path)).forEach(path -> {
//                ZipEntry zipEntry = new ZipEntry(sourceFolderPath.relativize(path).toString());
//                try {
//                    zos.putNextEntry(zipEntry);
//                    Files.copy(path, zos);
//                    zos.closeEntry();
//                } catch (IOException e) {
//                    System.err.println("❌ Could not zip: " + path + " - " + e.getMessage());
//                }
//            });
//        }
//    }
//}
