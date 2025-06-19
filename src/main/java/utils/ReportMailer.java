package utils;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class ReportMailer {

    public static void sendReport() {
        final String fromEmail = "vamshi.qa6@gmail.com"; // Replace with your email
        final String password = "fmpl qtni nsnq sfvl";     // App-specific password
        final String toEmail = "vamshi.qa6@gmail.com";

        
        try {
            // 1. Load summary.json
            String summaryContent = Files.readString(Paths.get("allure-report/widgets/summary.json"));
            JSONObject summaryStats = new JSONObject(summaryContent).getJSONObject("statistic");

            int total = summaryStats.getInt("total");
            int passed = summaryStats.getInt("passed");
            int failed = summaryStats.getInt("failed");
            int skipped = summaryStats.getInt("skipped");

            // 2. Create styled HTML body with table layout
            StringBuilder body = new StringBuilder();
            body.append("<h2 style='color:green;'>✅ Test Execution Summary</h2>")
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

            // 3. Add report link (update if hosted remotely)
            body.append("<p><a href='file:///")
                .append(Paths.get("allure-report/index.html").toAbsolutePath().toString().replace("\\", "/"))
                .append("' target='_blank'>🔍 View Full Report</a></p>");

            // 4. Footer
            body.append("<p>Regards,<br><b>Automation Team</b></p>");

            // 5. Mail setup
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

            // 6. Send email
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("✅ Allure Test Execution Summary");
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
