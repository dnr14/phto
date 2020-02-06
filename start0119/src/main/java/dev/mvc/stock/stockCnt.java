package dev.mvc.stock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import dev.mvc.cateGroup.cateGroupProcInter;

@Controller()
@RequestMapping("/stock")
public class stockCnt {
	
	@Autowired
	@Qualifier("stockProc")
	stockProcInter stockProc;
	
	@Autowired
	@Qualifier("cateGroupProc")
	cateGroupProcInter cateGroupProc;
	
	public stockCnt() {
		System.out.println("stockCnt ==>  stockCnt ������ ����");
	}
	
	@GetMapping("/create")
	public ModelAndView stockView() {
		return new ModelAndView("/stock/stockView")
				.addObject("stockCateGroup", cateGroupProc.stockCateGroup())
				.addObject("stockCreateRequest", new stockCreateRequest());
	}
	
	@PostMapping("/create")
	public ModelAndView createProc(stockCreateRequest scr ,Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "stockName", "required", "��� �̸��� �ʼ� �Դϴ�.");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "manager", "required", "����� �̸��� �ʼ� �Դϴ�.");
		
		if(scr.getStockCnt()  > 99) {
			errors.rejectValue("stockCnt", "bad", "��� ������ 99������ ��� �����մϴ�.");
		}else if(scr.getStockCnt() == 0){
			errors.rejectValue("stockCnt", "bad", "��� ������ 0���̻� ��� �����մϴ�.");
		}
		
		if(scr.getStockPrice() == 0) {
			errors.rejectValue("stockPrice", "bad", "��� ������ 0���̻� ��� �����մϴ�.");
		}
		
		if(errors.hasErrors()) {
			return new ModelAndView("/stock/stockView")
					.addObject("stockCateGroup", cateGroupProc.stockCateGroup());
		}
		
		// ��� ���
		stockProc.create(scr);
		return new ModelAndView("redirect:/stock/create");
	}
	
}
