package dev.mvc.tool;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import dev.mvc.member.memberIdPwdFind;

@Service("GoogleMail")
public class GoogleMail implements MailService {

	@Autowired
	private JavaMailSender mailSender;

	@Override
	public void sendMail(String Id, String Email, String Pwd) {

		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");

			String html = "<h1>��й�ȣ ã�� ����Դϴ�.</h1>";
			html += "<ul><li>" + Id + "</li></ul>";
			html += "<ul><li>" + Email + "</li></ul>";
			html += "<ul><li>" + Pwd + "</li></ul>";

			messageHelper.setFrom("dnr145@gmail.com"); // �����»�� �����ϸ� �����۵��� ����
			messageHelper.setTo(Email); // �޴»�� �̸���
			messageHelper.setSubject(Id + "�� ��й�ȣ ã��"); // ���������� ������ �����ϴ�
			messageHelper.setText(html, true); // ���� ����
			mailSender.send(message);

		} catch (Exception e) {
			System.out.println(e);
		}
	}

}
