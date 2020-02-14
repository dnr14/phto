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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.mvc.tool.MailService;

@Controller
@RequestMapping(value = "/member")
public class memberCnt {

	@Autowired
	@Qualifier("GoogleMail")
	private MailService googleMail;

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
	public ModelAndView createForm(HttpServletRequest request,
			@CookieValue(value = "create_agree", required = false) Cookie cookie) throws Exception {

		String agree = "N";

		if (cookie != null) {
			agree = cookie.getValue();
		}

		if (agree.equals("Y")) {
			return new ModelAndView("member/create").addObject("memberCreateRequest", new memberCreateRequest());
		} else {
			return new ModelAndView("/member/agreement");
		}
	}

	/**
	 * ȸ������ agreement
	 * 
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
	public ModelAndView create(memberCreateRequest memberCreateRequest, Errors errors, HttpServletRequest requset)
			throws Exception {
		new RegisterRequestValidator().validate(memberCreateRequest, errors);
		if (errors.hasErrors()) {
			return new ModelAndView("member/create");
		}

		try {
			memberProc.register(memberCreateRequest); // ��ȿ�� �˻�
		} catch (AlreadyExistingEmailException e) {
			errors.rejectValue("email", "duplicate", "�̹� ���Ե� �̸����Դϴ�.");
		} catch (AlreadyExistingIdException e) {
			errors.rejectValue("id", "duplicate", "�̹� ���Ե� ���̵��Դϴ�.");
		} finally {
			if (errors.hasErrors()) {
				return new ModelAndView("member/create");
			}
		}
		// ������ ���� key ����
		googleMail.sendMemberMail(memberCreateRequest.getId(), memberCreateRequest.getEmail(),
				memberCreateRequest.getKey(), requset);

		// ȸ������ ����
		return new ModelAndView("member/authority").addObject("id", memberCreateRequest.getId()).addObject("email",
				memberCreateRequest.getEmail());
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
	public ModelAndView loginProc(memberLoginCheck memberLoginCheck, Errors errors, HttpSession session,
			HttpServletResponse response) {

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "id", "required", "���̵� �Է��ϼ���.");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "pwd", "required", "��й�ȣ�� �Է��ϼ���.");

		if (errors.hasErrors()) {
			return new ModelAndView("member/login");
		}

		memberVO vo = memberProc.loginCheck(memberLoginCheck);

		if (vo != null) {
			if (!memberLoginCheck.isPwdCheck(vo.getPwd())) {
				errors.rejectValue("pwd", "bad", "�н����尡 Ʋ���ϴ�.");
			} else {
				// �α��� ����
				session.setAttribute("id", vo.getId());
				session.setAttribute("key", vo.getKey());

				try {
					String value = new ObjectMapper().writeValueAsString(memberLoginCheck);

					Cookie cookie = new Cookie("REMEMBER", value);
					cookie.setPath("/");
					if (memberLoginCheck.getCookieCheck()) {
						cookie.setMaxAge(60 * 60 * 24);
					} else {
						cookie.setMaxAge(0);
					}

					response.addCookie(cookie);
					return new ModelAndView("redirect:/");

				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
			}
		} else {
			errors.rejectValue("id", "bad", "���� ȸ�� �Դϴ�.");
		}
		return new ModelAndView("member/login");
	}

	/**
	 * ȸ�� �α׾ƿ�
	 * 
	 * @param session
	 * @return
	 */
	@GetMapping("/logOut")
	public ModelAndView logOut(HttpSession session) {
		System.out.println(session.getAttribute("id"));
		session.invalidate();
		return new ModelAndView("redirect:/");
	}

	/**
	 * ȸ�����̵� ã�� ������
	 * 
	 * @return
	 */
	@GetMapping("/IdFind")
	public ModelAndView IdFindFrom() {
		return new ModelAndView("member/IdFind").addObject("memberIdPwdFind", new memberIdPwdFind());
	}

	/**
	 * ȸ�����̵� ã�� ��
	 * 
	 * @param memberIdPwdFind
	 * @param errors
	 * @return
	 */
	@PostMapping("/IdFind")
	public ModelAndView IdFindProc(memberIdPwdFind memberIdPwdFind, Errors errors) {
		ModelAndView mav = new ModelAndView();
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "required", "�̸����� �ʼ� �Դϴ�.");

		if (errors.hasErrors()) {
			mav.setViewName("member/IdFind");
			return mav;
		}

		String email = memberIdPwdFind.getEmail();
		int count = memberProc.IdFindCount(email);

		if (!errors.hasErrors()) {
			if (count == 1) {
				String getId = memberProc.IdFind(email);
				mav.setViewName("member/IdPwdResult");
				mav.addObject("Id", getId);
				mav.addObject("Email", email);
				mav.addObject("find", "id");
			} else {
				mav.setViewName("member/IdPwdResult");
				mav.addObject("find", "id");
			}
		} else {
			mav.setViewName("member/IdFind");
		}
		return mav;
	}

	/**
	 * ��й�ȣ ã��
	 * 
	 * @return
	 */
	@GetMapping("/PwdFind")
	public ModelAndView PwdFindFrom() {
		return new ModelAndView("member/PwdFind").addObject("memberIdPwdFind", new memberIdPwdFind());
	}

	/**
	 * ��й�ȣ ã�� Proc
	 * 
	 * @param memberIdPwdFind
	 * @param errors
	 * @return
	 */
	@PostMapping("/PwdFind")
	public ModelAndView PwdFindProc(memberIdPwdFind memberIdPwdFind, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "id", "required", "���̵�� �ʼ� �Դϴ�.");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "required", "�̸����� �ʼ� �Դϴ�.");

		if (errors.hasErrors()) {
			return new ModelAndView("member/PwdFind");
		} else {
			ModelAndView mav = new ModelAndView();
			int count = memberProc.PwdFindCount(memberIdPwdFind);
			String id = memberIdPwdFind.getId();
			String email = memberIdPwdFind.getEmail();
			String pwd = memberProc.PwdFind(memberIdPwdFind);
			
			if (count == 1) {
				googleMail.sendMail(id, email, pwd);
				mav.addObject("Id", memberIdPwdFind.getId());
				mav.addObject("Email", memberIdPwdFind.getEmail());
			} 
			
			mav.setViewName("member/IdPwdResult");
			mav.addObject("find", "pwd");
			return mav;
		}
	}

	/**
	 * �̸��� ����
	 * 
	 * @param id
	 * @param key
	 * @return
	 */
	@GetMapping("/key_alter")
	public ModelAndView key_alter(@RequestParam("user_id") String id, @RequestParam("user_key") String key) {

		ModelAndView mav = new ModelAndView();

		String getKey = memberProc.selectByKey(id);

		if (key.equals(getKey)) {
			int count = memberProc.updateKey(id);
			if (count == 1) {
				mav.setViewName("member/createResult"); // ���� ����
			} else {
				mav.setViewName("member/createFail"); // ���� ����
			}
		} else {
			mav.setViewName("member/createFail"); // ���� ����
		}
		mav.addObject("id", id);
		return mav;
	}

}
