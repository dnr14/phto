package dev.mvc.cateGroup;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping(value="/cateGroup")
public class cateGroupCnt {

	@Qualifier("cateGroupProc")
	@Autowired
	cateGroupProcInter cateGroupProc;

	public cateGroupCnt() {
		System.out.println("cateGroupCnt ==>  cateGroupCnt ������ ����");
	}

	@GetMapping("/create")
	public ModelAndView cateGroupView() {

		ModelAndView model = new ModelAndView("categroup/cateGroupView");
		model.addObject("cateGroupCreateRequest", new cateGroupCreateRequest());
		model.addObject("cateGroupList", cateGroupProc.list());
		return model;
	}

	/**
	 * ī�װ� ����
	 * @param cgc
	 * @param errors
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/create")
	public ModelAndView cateGroupView(cateGroupCreateRequest cgc, Errors errors) throws Exception {
		int cateGroupCount = cateGroupProc.cateGroupCount();
		String View = "redirect:/cateGroup/create.do";

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "categrpName", "required", "ī�װ� �̸��� �ʼ� �Դϴ�.");

		if (cgc.getCategrpSeqno() > 5) {
			errors.rejectValue("categrpSeqno", "bad", "5 �̻��� �Է� �Ұ��� �մϴ�.");
		}

		if (cateGroupCount == 5) {
			return new ModelAndView("categroup/cateGroupView").addObject("Excess", "ī�װ��� 5�� �̻� ����� �Ұ��� �մϴ�.")
					.addObject("cateGroupList", cateGroupProc.list());
		}

		if (errors.hasErrors()) {
			return new ModelAndView("categroup/cateGroupView").addObject("cateGroupList", cateGroupProc.list());
		}

		cateGroupProc.create(cgc);
		return new ModelAndView(View);
	}

	/**
	 * ī�װ� ����
	 * 
	 * @param map
	 * @return
	 */
	@PostMapping(value = "/cateGroupDelete", produces = "text/plain;charset=UTF-8")
	@ResponseBody
	public String cateGroupDelete(@RequestBody HashMap<String, ArrayList<Integer>> map) {
		for (int categrpNo : map.get("array")) {
			cateGroupProc.cateGroupDelete(categrpNo);
		}
		JSONObject jsonobejct = new JSONObject();
		jsonobejct.put("message", "��� ���� �Ǿ����ϴ�.");

		return jsonobejct.toString();
	}

	/**
	 * ī�װ� ���̵� �޴� ���
	 * 
	 * @return
	 */
	@RequestMapping(value = "/cateGroupSideList", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView cateGroupSideList() {
		return new ModelAndView("menu/cateGroupList").addObject("cateGroupSideList", cateGroupProc.cateGroupSideList());
	}

	/**
	 * ī�װ� ���� ���� ��������
	 * 
	 * @param VO
	 * @return
	 */
	@ResponseBody
	@PostMapping(value = "/cateGroupUpdate", produces = "text/plain;charset=UTF-8")
	public String cateGroupUpdate(cateGroupVO vo) {
		JSONObject jsonobject = new JSONObject();
		String string = "";
		try {
			string = new ObjectMapper().writeValueAsString(cateGroupProc.cateGroupUpdateForm(vo));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		jsonobject.put("cateGroupVO", string);
		return jsonobject.toString();
	}
	
	
	/**
	 * ī�װ� ���� ����
	 * @param vo
	 * @return count 1 �̻� ���� count 0 ���� 
	 */
	@ResponseBody
	@PostMapping(value="/cateGroupUpdateProc", produces="text/plain;charset=UTF-8")
	public String cateGroupUpdateProc(cateGroupVO vo) {
		System.out.println(vo.toString());
		int count = cateGroupProc.cateGroupUpdateProc(vo);
		System.out.println(count);
		JSONObject jsonobject = new JSONObject();
		jsonobject.put("count", count);
		return jsonobject.toString();
	}
	
	/**
	 * �׺�� ���
	 * @return
	 */
	@ResponseBody
	@GetMapping(value="/cateGroupTopList", produces="text/plain;charset=UTF-8")
	public String cateGroupTopList() {
		return new JSONObject().put("list", cateGroupProc.cateGroupTopList())
				.toString();
	}

}
