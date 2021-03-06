package dev.mvc.member;

import java.util.HashMap;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.mvc.tool.MailService;
import dev.mvc.tool.Sha256;

@Controller
@RequestMapping(value = "/member")
public class memberCnt {

	@Autowired
	@Qualifier("GoogleMail")
	private MailService googleMail;

	@Autowired
	@Qualifier("memberProc")
	memberProcInter memberProc; // 다형성 memberProcInter memberProc = new memberProc

	public memberCnt() {
		System.out.println("memberCnt ==>  memberCnt 의존성 생성");
	}

	/**
	 * 회원가입 페이지
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
	 * 회원가입 agreement
	 * 
	 * @return
	 */
	@GetMapping("/agreement")
	public ModelAndView agreement() {
		return new ModelAndView("/member/agreement");
	}

	/**
	 * 회원가입 및 유효성 검사
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
			memberProc.register(memberCreateRequest); // 유효성 검사
		} catch (AlreadyExistingEmailException e) {
			errors.rejectValue("email", "duplicate", e.getMessage());
		} catch (AlreadyExistingIdException e) {
			errors.rejectValue("id", "duplicate",  e.getMessage());
		} finally {
			if (errors.hasErrors()) {
				return new ModelAndView("member/create");
			}
		}
		// 난수로 만든 key 전송
		googleMail.sendMemberMail(memberCreateRequest.getId(), memberCreateRequest.getEmail(),
				memberCreateRequest.getKey(), requset);

		// 회원가입 성공
		return new ModelAndView("member/authority").addObject("id", memberCreateRequest.getId()).addObject("email",
				memberCreateRequest.getEmail());
	}

	/**
	 * 로그인 페이지
	 * 
	 * @return
	 */
	@GetMapping("/login")
	public ModelAndView loginView(@CookieValue(value = "REMEMBER", required = false) Cookie cookie) {
		memberLoginCheck memberLoginCheck = new memberLoginCheck();

		if (cookie != null) {
			JSONObject jsonObject = new JSONObject(cookie.getValue());
			memberLoginCheck.setId(jsonObject.get("id").toString());
			memberLoginCheck.setPwd(jsonObject.get("pwd").toString());
			memberLoginCheck.setCookieCheck(true);
		}

		return new ModelAndView("member/login").addObject("memberLoginCheck", memberLoginCheck);
	}

	/**
	 * 로그인 및 유효성 체크
	 * 
	 * @param mlc
	 * @param errors
	 * @return
	 */
	@PostMapping("/login")
	public ModelAndView loginProc(memberLoginCheck memberLoginCheck, Errors errors, HttpSession session,
			HttpServletResponse response) {

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "id", "required", "아이디를 입력하세요.");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "pwd", "required", "비밀번호를 입력하세요.");

		if (errors.hasErrors()) {
			return new ModelAndView("member/login");
		}

		memberVO vo = memberProc.loginCheck(memberLoginCheck);

		if (vo != null) {
			if (!memberLoginCheck.isPwdCheck(vo.getPwd())) {
				errors.rejectValue("pwd", "bad", "패스워드가 틀립니다.");
			} else {
				// 로그인 성공
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
			errors.rejectValue("id", "bad", "없는 회원 입니다.");
		}
		return new ModelAndView("member/login");
	}

	/**
	 * 회원 로그아웃
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
	 * 회원아이디 찾기 페이지
	 * 
	 * @return
	 */
	@GetMapping("/IdFind")
	public ModelAndView IdFindFrom() {
		return new ModelAndView("member/IdFind").addObject("memberIdPwdFind", new memberIdPwdFind());
	}

	/**
	 * 회원아이디 찾기 폼
	 * 
	 * @param memberIdPwdFind
	 * @param errors
	 * @return
	 */
	@PostMapping("/IdFind")
	public ModelAndView IdFindProc(memberIdPwdFind memberIdPwdFind, Errors errors) {
		ModelAndView mav = new ModelAndView();
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "required", "이메일은 필수 입니다.");

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
	 * 비밀번호 찾기
	 * 
	 * @return
	 */
	@GetMapping("/PwdFind")
	public ModelAndView PwdFindFrom() {
		return new ModelAndView("member/PwdFind").addObject("memberIdPwdFind", new memberIdPwdFind());
	}

	/**
	 * 비밀번호 찾기 Proc
	 * 
	 * @param memberIdPwdFind
	 * @param errors
	 * @return
	 */
	@PostMapping("/PwdFind")
	public ModelAndView PwdFindProc(memberIdPwdFind memberIdPwdFind, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "id", "required", "아이디는 필수 입니다.");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "required", "이메일은 필수 입니다.");

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
	 * 이메일 인증
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
				mav.setViewName("member/createResult"); // 인증 성공
			} else {
				mav.setViewName("member/createFail"); // 인증 실패
			}
		} else {
			mav.setViewName("member/createFail"); // 인증 실패
		}
		mav.addObject("id", id);
		return mav;
	}

	/**
	 * 회원 수정 화면
	 * @return
	 */
	@GetMapping("/update")
	public ModelAndView memberUpdateForm(HttpSession session) {
		ModelAndView mav = new ModelAndView();
		String id = session.getAttribute("id").toString();
		memberVO vo = memberProc.memberSelect(id); // 회원정보 가져오기

		mav.addObject("memberVO", vo);
		mav.addObject("memberCreateRequest", new memberCreateRequest());
		mav.setViewName("member/update");
		return mav;
	}

	@PostMapping("/update")
	public ModelAndView memberUpdateProc(memberCreateRequest memberCreateRequest, Errors errors) {
		ModelAndView mav = new ModelAndView();

		new RegisterRequestValidator().validate(memberCreateRequest, errors);

		if (errors.hasErrors()) {
			mav.setViewName("member/update");
			mav.addObject("memberCreateRequest", memberCreateRequest);
			mav.addObject("memberVO", memberCreateRequest);
		} else {
			memberProc.memberUpdate(memberCreateRequest);// 회원정보 수정
			mav.setViewName("redirect:/member/update");// 회원정보 조회
		}
		return mav;
	}

	/**
	 *  회원 비밀번호 수정
	 * @param beforePwd 이전 비밀번호
	 * @param afterPwd 변경 비밀번호
	 * @param afterPwd_Check
	 * @param session
	 * @return
	 */
	@PostMapping("/pwdUpdate")
	@ResponseBody
	public String pwdUpdateAjax(@RequestParam("beforePwd") String beforePwd, @RequestParam("afterPwd") String afterPwd,
			@RequestParam("afterPwd_Check") String afterPwd_Check, HttpSession session) {

		String id = session.getAttribute("id").toString();
		memberVO vo = memberProc.memberSelect(id);

		JSONObject json = new JSONObject();

		// 이전 비밀번호 빈값이나 null
		if (beforePwd.trim().isEmpty() || beforePwd == null) {
			return json.put("result", "beforePwd_empty").toString();
		}

		if (afterPwd.trim().isEmpty() || afterPwd == null) {
			return json.put("result", "afterPwd_empty").toString();
		}

		if (afterPwd_Check.trim().isEmpty() || afterPwd_Check == null) {
			return json.put("result", "afterPwd_Check_empty").toString();
		}

		// 비밀번호가 같다면
		if (Sha256.encoding(beforePwd).equals(vo.getPwd())) {
			if (!afterPwd.equals(afterPwd_Check)) {
				// 
				json.put("result", "afterPwd_mismatch");
				return json.toString();
			}
			
			// 비밀번호 변경
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("id", id);
			map.put("afterPwd", Sha256.encoding(afterPwd));
			memberProc.memberPwdUpdate(map);
			
			json.put("result", "pwd_update");

		} else {
			json.put("result", "beforePwd_mismatch");
		}
		return json.toString();
	}

	/**
	 * 회원삭제 화면
	 * @return
	 */
	@GetMapping("/delete")
	public ModelAndView memberDeleteForm() {
		return new ModelAndView("member/delete");
	}
	/**
	 * 회원삭제 
	 * @param session
	 * @param pwd
	 * @return
	 */
	@PostMapping("/delete")
	@ResponseBody
	public String memberDeleteAjax(HttpSession session, @RequestParam("pwd") String pwd) {
		String user_id = session.getAttribute("id").toString();
		String encoding_pwd = Sha256.encoding(pwd);
		boolean sw = 	memberProc.memberSelect(user_id).getPwd().equals(encoding_pwd);
		JSONObject json = new JSONObject();
		
		//비밀번호 공백
		if(pwd.trim().isEmpty() || pwd == null) {
			return json.put("result", "pwd_null").toString();
		}
		//비밀번호 일치 
		if(sw) {
			int count = memberProc.memberDelete(user_id);
			if(count == 1) {
				// 삭제 성공
				json.put("result", "delete_success");
			}else {
				json.put("result", "delete_fail");
				// 삭제 실패
			}
		//비밀번호 불일치 
		}else {
			json.put("result", "pwd_mismatch");
		}
		return json.toString();
	}

}
