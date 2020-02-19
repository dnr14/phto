package dev.mvc.contents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import dev.mvc.cateGroup.cateGroupProcInter;
import dev.mvc.tool.PageMaker;
import dev.mvc.tool.Tool;
import dev.mvc.tool.Upload;

@Controller
@RequestMapping("/contents")
public class ContentsCont {

	// �⺻ �����ڷ� ����
	@Autowired
	@Qualifier("ContentsProc") // id������ ã�Ƽ� ����
	private ContentsProcInter ContentsProc;

	@Autowired
	private cateGroupProcInter cateGroupProc;

	/**
	 * ��ǰ ��� ȭ��
	 * 
	 * @return
	 */
	@GetMapping("/create")
	public ModelAndView creatForm() {
		return new ModelAndView("contents/create").addObject("ContentsCreateDto", new ContentsCreateDto())
				.addObject("cate", cateGroupProc.stockCateGroup());
	}

	/**
	 * ��ǰ ��� Proc
	 * 
	 * @param ContentsCreateDto
	 * @param errors
	 * @return
	 */
	@PostMapping("/create")
	public ModelAndView createProc(@ModelAttribute("ContentsCreateDto") ContentsCreateDto ContentsCreateDto,
			Errors errors, HttpServletRequest request) {

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "title", "required", "������ �ʼ� �Դϴ�.");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "keyword", "required", "keyword�� �ʼ� �Դϴ�.");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "contents", "required", "������ �ʼ� �Դϴ�.");

		if (ContentsCreateDto.getCategrpNo() == 0) {
			errors.rejectValue("categrpNo", "error", "ī�װ��� �����ϼ���.");
		}
		if (ContentsCreateDto.getStockNo() == 0) {
			errors.rejectValue("stockNo", "error", "��ǰ�� �����ϼ���.");
		}
		if (ContentsCreateDto.getDeliveryCharge() < 0 || ContentsCreateDto.getDeliveryCharge() > 9999) {
			errors.rejectValue("deliveryCharge", "error", "��ۺ�� 0�� ���� 9999�� ���� �����մϴ�.");
		}
		if (errors.hasErrors()) {
			return new ModelAndView("contents/create").addObject("cate", cateGroupProc.stockCateGroup());
		}

		// ���� ����
		int count = ContentsProc.create(ContentsCreateDto);

		if (count == 1) {
			int contentsNo = ContentsProc.cotentsNoSelect();

			// ī�װ� cnt ����
			cateGroupProc.cateGroupCntUp(ContentsCreateDto.getCategrpNo());

			String upDir = Tool.getRealPath(request, "/contents/storage");

			List<MultipartFile> fnamesMF = ContentsCreateDto.getFnamesMF();

			// �̹����� ������
			if (fnamesMF.get(0).getSize() > 0) {
				String fname = "";
				String fupname = "";
				long fsize = 0;

				int fnamesMF_count = fnamesMF.size(); // ���� ���� ����

				if (fnamesMF_count > 0) {

					HashMap<String, Object> map = new HashMap<String, Object>();
					for (MultipartFile multipartFile : fnamesMF) { // ���� ����
						fsize = multipartFile.getSize();
						if (fsize > 0) {
							fname = multipartFile.getOriginalFilename();
							
							if(!Tool.isImage(fname)) {
								return new ModelAndView("contents/contentsFail").addObject("error", "Ȯ���� ���� jpg, jpeg, png, gif�� ����");
							}
							// ���ε� �� ���� �̸� ����
							fupname = Upload.saveFileSpring(multipartFile, upDir);

							map.put("contentsNo", contentsNo);
							map.put("fname", fname);
							map.put("fupname", fupname);
							map.put("fsize", fsize);

							ContentsProc.create(map);
						}

					}
				}

			}

			MultipartFile fthum = ContentsCreateDto.getFthum();
			if (fthum.getSize() > 0) {
				
				if(!Tool.isImage(fthum.getOriginalFilename())) {
					return new ModelAndView("contents/contentsFail").addObject("error", "Ȯ���� ���� jpg, jpeg, png, gif�� ����");
				}

				String fupname = "";
				fupname = Upload.saveFileSpring(fthum, upDir); // ���� ����
				String thum_image = Tool.preview(upDir, fupname, 200, 300);// ����� ����

				if (thum_image != null) {
					HashMap<String, Object> map = new HashMap<String, Object>();

					Tool.deleteFile(upDir, fupname);
					String fname = fthum.getOriginalFilename();
					long fsize = fthum.getSize();
					
					map.put("contentsNo", contentsNo);
					map.put("fname", fname);
					map.put("fsize", fsize);
					map.put("thumb", thum_image);
					
					ContentsProc.create(map);
				}
			}

		} else {
			// ���� ����
			return new ModelAndView("contents/contentsFail").addObject("error", "�� ��� ����");
		}

		return new ModelAndView("redirect:/contents/list");
	}

	/**
	 * ī�װ� ȣ��
	 * 
	 * @param categrpNo
	 * @return
	 */
	@GetMapping(value = "/CateGroupAjax", produces = "text/plain;charset=UTF-8")
	@ResponseBody
	public String CateGroupAjax(int categrpNo) {
		JSONObject json = new JSONObject();
		json.put("cate", ContentsProc.CateGroupAjax(categrpNo));
		return json.toString();
	}
	
	/**
	 * �Խñ� �ѷ��ֱ�
	 * @return
	 */
	@GetMapping("/list")
	public ModelAndView list(
			@RequestParam(value="pagenum", defaultValue = "1")  int pagenum
			) {
		
		PageMaker pagemaker = new PageMaker();
		
		// ��ü �Խñ� ������ �����Ѵ�.
		pagemaker.setTotalcount(ContentsProc.pagingCount());
		// ���� �������� ������ ��ü�� �����Ѵ�.
		pagemaker.setPagenum(pagenum);
		// ���� ������ ��� ������� ���� ������ ��ȣ�� ���ؼ� ����
		pagemaker.setCurrentblock(pagenum);
		// ������ ��� ��ȣ�� ��ü �Խñ� ���� ���ؼ� ���Ѵ�.
		pagemaker.setLastblock(pagemaker.getTotalcount());
		
		pagemaker.setStartPageNum(pagenum);
		pagemaker.setEndPageNum(pagenum);
		
		pagemaker.setStartPage(pagemaker.getCurrentblock());
		pagemaker.setEndPage(pagemaker.getLastblock(), pagemaker.getCurrentblock());

		pagemaker.prevnext(pagenum);
		
		List<ContentsVO> list = new ArrayList<ContentsVO>();
		HashMap<String,Object> map = new HashMap<String,Object>();
		
		map.put("startPageNum", pagemaker.getStartPageNum());
		map.put("endPageNum", pagemaker.getEndPageNum());
		
		list = ContentsProc.list(map);
		
		for(ContentsVO vo : list) {
			System.out.println(vo.toString());
		}
		
		return new ModelAndView("contents/list")
				.addObject("list", list)
				.addObject("page", pagemaker);
	}
	

}
