package dev.mvc.admin;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/admin")
public class AdminCont {

  @Autowired
  @Qualifier("AdminProc")
  private AdminProcInter adminProc;

  @Autowired
  private JavaMailSender mailSender;

  /**
   * admin �α��� ȭ��
   * 
   * @param adminLoginCheck
   * @return
   */
  @GetMapping("/login")
  public ModelAndView AdminForm() {
    return new ModelAndView("admin/login").addObject("adminLoginCheck", new AdminLoginCheck());
  }

  /**
   * admin �α��� ó��
   * 
   * @param adminLoginCheck
   * @return
   */
  @PostMapping("/login")
  public String AdminLoginProc(AdminLoginCheck adminLoginCheck, Errors errors, HttpSession session) {

    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "id", "requierd", "������ ���̵� �ʼ� �Դϴ�.");
    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "pwd", "requierd", "�н������  �ʼ� �Դϴ�.");

    if (errors.hasErrors()) {
      return "admin/login";
    } else {
      String admin_id = adminLoginCheck.getId();
      String admin_pwd = adminLoginCheck.getPwd();

      boolean sw = adminProc.login(admin_id, admin_pwd);

      if (sw == true) {
        session.setAttribute("admin_id", admin_id);
        session.setAttribute("admin_pwd", admin_pwd);
        session.setMaxInactiveInterval(60 * 30);
      } else {
        return "admin/login_fail";
      }

      return "index";
    }
  }

  /**
   * admin �α׾ƿ�
   * 
   * @param session
   * @return
   */
  @GetMapping("/logOut")
  public String AdminLogOut(HttpSession session) {
    session.invalidate();
    return "index";
  }

  @GetMapping("/mail")
  public ModelAndView sendMail() {
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

    return new ModelAndView("redirect:/");
  }

}
