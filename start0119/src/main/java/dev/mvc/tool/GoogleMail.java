package dev.mvc.tool;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

@Service("GoogleMail")
public class GoogleMail implements MailService{

  @Autowired
  private JavaMailSender mailSender;
  
  @Override
  public void sendMail() {
    System.out.println(mailSender.toString());

    final MimeMessagePreparator message = new MimeMessagePreparator() {
      @Override
      public void prepare(MimeMessage mimeMessage) throws Exception {
        final MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        
        helper.setFrom("dnr0145@gmail.com"); // �����»�� �����ϸ� �����۵��� ����
        helper.setTo("dnr14@naver.com"); // �޴»�� �̸���
        helper.setSubject("�׽�Ʈ"); // ���������� ������ �����ϴ�
        helper.setText("<h1>�ȳ�</h1>",true); // ���� ����
      }

    };
    mailSender.send(message);
  }

}
