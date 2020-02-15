package dev.mvc.admin;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
  
  public AdminCont() {
    System.out.println("AdminCont ==>  AdminCont ������ ����");
  }

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

      return "redirect:/";
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
    return "redirect:/";
  }


}
