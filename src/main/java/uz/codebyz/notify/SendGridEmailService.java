package uz.codebyz.notify;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class SendGridEmailService implements EmailService {

    private final SendGridProperties props;
    private final Logger log = LoggerFactory.getLogger(SendGridEmailService.class);

    public SendGridEmailService(SendGridProperties props) {
        this.props = props;
    }

    @Override
    public void sendVerificationCode(String toEmail, String subject, String code, String purpose) {

        if (!props.isEnabled()) return;

        String html =
                "<!DOCTYPE html>" +
                        "<html lang='tr'>" +
                        "<head>" +
                        "  <meta charset='UTF-8'>" +
                        "  <meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                        "  <title>CodeByZ Doğrulama</title>" +
                        "</head>" +

                        "<body style='margin:0;padding:0;" +
                        "background:#f2f5f9;" +   // 🔥 SOFT PROFESSIONAL BACKGROUND
                        "font-family:Segoe UI,Roboto,Arial,sans-serif;'>" +

                        "<table width='100%' cellpadding='0' cellspacing='0'>" +
                        " <tr>" +
                        "  <td align='center' style='padding:40px 16px;'>" +

                        "   <div style='max-width:520px;width:100%;" +
                        "        background:#0f1424;" +
                        "        border-radius:18px;" +
                        "        overflow:hidden;" +
                        "        box-shadow:0 16px 40px rgba(15,20,36,.35);'>" +

                        "    <!-- HEADER -->" +
                        "    <div style='background:linear-gradient(135deg,#00f5c4,#00a8ff);" +
                        "                padding:26px;text-align:center;'>" +
                        "      <div style='font-size:26px;font-weight:800;color:#041317;'>&lt;CodeByZ/&gt;</div>" +
                        "    </div>" +

                        "    <!-- CONTENT -->" +
                        "    <div style='padding:26px;color:#e6e9f0;'>" +
                        "      <h2 style='margin:0 0 10px;font-size:20px;color:#00f5c4;'>Giriş Kodu</h2>" +
                        "      <p style='margin:0 0 20px;color:#b6bfd3;font-size:14px;'>" +
                        purpose + " işlemi için doğrulama kodunuz:" +
                        "      </p>" +

                        "      <div style='background:#0b2f2a;border:1px solid #00f5c4;" +
                        "                  color:#00f5c4;font-size:30px;font-weight:700;" +
                        "                  letter-spacing:10px;text-align:center;" +
                        "                  padding:18px 0;border-radius:12px;margin-bottom:22px;'>" +
                        code +
                        "      </div>" +

                        "      <div style='background:#10162a;padding:14px 16px;border-radius:10px;" +
                        "                  color:#9aa3b2;font-size:13px;border-left:4px solid #00a8ff;'>" +
                        "        Bu isteği siz yapmadıysanız, bu e-postayı güvenle yok sayabilirsiniz." +
                        "      </div>" +
                        "    </div>" +

                        "    <!-- FOOTER -->" +
                        "    <div style='padding:14px;color:#7f8899;font-size:12px;" +
                        "                border-top:1px solid #1b2240;text-align:center;'>" +
                        "      © 2025 CodeByZ • Tüm hakları saklıdır" +
                        "    </div>" +

                        "   </div>" +
                        "  </td>" +
                        " </tr>" +
                        "</table>" +

                        "</body>" +
                        "</html>";

        Email from = new Email(props.getFromEmail(), props.getFromName());
        Email to = new Email(toEmail);
        Content content = new Content("text/html", html);
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(props.getApiKey());
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            sg.api(request);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

}
