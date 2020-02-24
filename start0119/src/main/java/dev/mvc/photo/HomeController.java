package dev.mvc.photo;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import dev.mvc.contents.ContentsProcInter;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
	@Autowired @Qualifier("ContentsProc")	
	ContentsProcInter contentsProc;
	
	public HomeController() {
		System.out.println("HomeController �� ���� =>");
	}
	
	// dispachservlet url-pattern�� /�� �س����� 
	// defaultservlet�� url-pattern�� �⺻ /�� �Ǿ��ִ�.
	// ��� ��û�� ��Ĺ�� ���� �޴´�.
	// /�� ���� ��û�� ĳġ�Ͽ� 
	//
	 /*   <welcome-file-list>
	    <welcome-file>index.html</welcome-file>
	    <welcome-file>index.htm</welcome-file>
	    <welcome-file>index.jsp</welcome-file>
	</welcome-file-list>*/
	// welcome�� ��ϵ� ������ �ִ��� ã�´� ���� ��ã���� 
	// ��Ĺ�� dispachservlet���� ������ �Ѱܼ� dispachservlet��  url-pattern�� �´�
	// requestMapping�� ã�´�.
	@RequestMapping(value="/" , method=RequestMethod.GET)
	public String home(Model model) {
		model.addAttribute("list", contentsProc.index_list());
		return "home";
	}
	
}
