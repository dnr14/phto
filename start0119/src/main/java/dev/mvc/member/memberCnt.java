package dev.mvc.member;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping(value="/member")
public class memberCnt {

	@Autowired
	@Qualifier("memberProc")
	memberProcInter memberProc; // ������ memberProcInter memberProc = new memberProc

	public memberCnt() {
		System.out.println("memberCnt ==>  memberCnt ������ ����");
	}

	/**
	 * ȸ������ ������
	 * 
	 * @return
	 */
	@GetMapping("/create")
	public ModelAndView createView(
			HttpServletRequest request,
			@CookieValue(value = "create_agree", required = false) Cookie cookie) throws Exception {

		String agree = "N";
		
		if(cookie !=null) {
			agree = cookie.getValue();
		}

		if (agree.equals("Y")) {
			return new ModelAndView("member/create").
					addObject("memberCreateRequest", new memberCreateRequest());
		} else {
			return new ModelAndView("/member/agreement");
		}
	}
	
	/**
	 * ȸ������ agreement
	 * @return
	 */
	@GetMapping("/agreement")
	public ModelAndView agreement() {
		return new ModelAndView("/member/agreement");
	}

	/**
	 * ȸ������ �� ��ȿ�� �˻�
	 * 
	 * @param mcr
	 * @param errors
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/create")
	public ModelAndView create(memberCreateRequest mcr, Errors errors) throws Exception {
		new RegisterRequestValidator().validate(mcr, errors);
		if (errors.hasErrors()) {
			return new ModelAndView("member/create");
		}

		try {
			memberProc.register(mcr);
		} catch (AlreadyExistingEmailException e) {
			errors.rejectValue("email", "duplicate", "�̹� ���Ե� �̸����Դϴ�.");
		} catch (AlreadyExistingIdException e) {
			errors.rejectValue("id", "duplicate", "�̹� ���Ե� ���̵��Դϴ�.");
		} finally {
			if (errors.hasErrors()) {
				return new ModelAndView("member/create");
			}
		}
		// ȸ������ ����
		return new ModelAndView("member/createResult");
	}

	/**
	 * �α��� ������
	 * 
	 * @return
	 */
	@GetMapping("/login")
	public ModelAndView loginView(@CookieValue(value = "REMEMBER", required = false) Cookie cookie) {
		memberLoginCheck mlc = new memberLoginCheck();

		if (cookie != null) {
			JSONObject jsonObject = new JSONObject(cookie.getValue());
			mlc.setId(jsonObject.get("id").toString());
			mlc.setPwd(jsonObject.get("pwd").toString());
			mlc.setCookieCheck(true);
		}

		return new ModelAndView("member/login").addObject("memberLoginCheck", mlc);
	}

	/**
	 * �α��� �� ��ȿ�� üũ
	 * 
	 * @param mlc
	 * @param errors
	 * @return
	 */
	@PostMapping("/login")
	public ModelAndView loginProc(memberLoginCheck mlc, Errors errors, HttpSession session,
			HttpServletResponse response) {

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "id", "required", "���̵� �Է��ϼ���.");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "pwd", "required", "��й�ȣ�� �Է��ϼ���.");

		if (errors.hasErrors()) {
			return new ModelAndView("member/login");
		}

		memberVO vo = memberProc.loginCheck(mlc);

		if (vo != null) {
			if (!mlc.isPwdCheck(vo.getPwd())) {
				errors.rejectValue("pwd", "bad", "�н����尡 Ʋ���ϴ�.");
			} else {
				// �α��� ����
				session.setAttribute("id", vo.getId());

				String value = "";
				try {
					value = new ObjectMapper().writeValueAsString(mlc);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
				Cookie cookie = new Cookie("REMEMBER", value);
				cookie.setPath("/");
				if (mlc.getCookieCheck()) {
					cookie.setMaxAge(60 * 60 * 24);
				} else {
					cookie.setMaxAge(0);
				}

				response.addCookie(cookie);
				return new ModelAndView("redirect:/");
			}
		} else {
			errors.rejectValue("id", "bad", "���� ȸ�� �Դϴ�.");
		}
		return new ModelAndView("member/login");
	}
	
	@GetMapping("/logOut")
	public ModelAndView logOut(HttpSession session) {
		System.out.println(session.getAttribute("id"));
		session.invalidate();
		return new ModelAndView("redirect:/");
	}
	
	
}
