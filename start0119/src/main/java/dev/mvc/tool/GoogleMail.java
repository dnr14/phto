package dev.mvc.tool;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service("GoogleMail")
public class GoogleMail implements MailService {

	@Autowired
	private JavaMailSender mailSender;

	/**
	 * ��й�ȣ ã�� �̸��� �߼�
	 */
	@Override
	public void sendMail(String Id, String Email, String Pwd) {

		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");

			String html = "<h1>��й�ȣ ã�� ����Դϴ�.</h1>";
			html += "<ul style='list-style:none;'>";
			html += "<li> ���̵� : " + Id + "</li>";
			html += "<li> �̸��� : " + Email + "</li>";
			html += "<li> ��й�ȣ : " + Pwd + "</li>";
			html += "</ul>";

			messageHelper.setFrom("dnr145@gmail.com"); // �����»�� �����ϸ� �����۵��� ����
			messageHelper.setTo(Email); // �޴»�� �̸���
			messageHelper.setSubject(Id + "�� ��й�ȣ ã��"); // ���������� ������ �����ϴ�
			messageHelper.setText(html, true); // ���� ����
			mailSender.send(message);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ȸ������ �̸��� ����
	 */
	@Override
	public void sendMemberMail(String Id, String Email, String key, HttpServletRequest request) {

		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");

			String html = "<h2>�ȳ��ϼ��� ������ǰ : ) ������~ �Դϴ�!</h2><br><br>";
			html += "<h3>" + Id + "��</h3>" + "<p>�����ϱ� ��ư�� �����ø� �α����� �Ͻ� �� �ֽ��ϴ� : ";
			html += "<a href='http://localhost:9090" + request.getContextPath() + "/member/key_alter?user_id=" + Id
					+ "&user_key=" + key + "'>�����ϱ�</a></p>";

			messageHelper.setFrom("dnr145@gmail.com"); // �����»�� �����ϸ� �����۵��� ����
			messageHelper.setTo(Email); // �޴»�� �̸���
			messageHelper.setSubject("[��������] ������ǰ : )"+Id+"���� ���������Դϴ�");
			messageHelper.setText(html, true); // ���� ����
			mailSender.send(message);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
