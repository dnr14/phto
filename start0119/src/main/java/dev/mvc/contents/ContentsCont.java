package dev.mvc.contents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
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
import dev.mvc.cateGroup.cateGroupVO;
import dev.mvc.stock.stockVO;
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

							if (!Tool.isImage(fname)) {
								return new ModelAndView("contents/contentsFail").addObject("error",
										"Ȯ���� ���� jpg, jpeg, png, gif�� ����");
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

				if (!Tool.isImage(fthum.getOriginalFilename())) {
					return new ModelAndView("contents/contentsFail").addObject("error",
							"Ȯ���� ���� jpg, jpeg, png, gif�� ����");
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
	 * �Խ��� �ѷ��ֱ�
	 * 
	 * @param pagenum   ������ ȭ�� ��ȣ
	 * @param categrpno ������ ī�װ� ��ȣ default 0 ���� 0�̸� ��ü ��� Mybatis�� ���� ������ ó��
	 * @return �˻��� ȭ�� ����¡�Ǿ �����ش�.
	 */
	@GetMapping("/list")
	public ModelAndView list(@RequestParam(value = "pagenum", defaultValue = "1") int pagenum,
			@RequestParam(value = "categrpno", defaultValue = "0") String categrpno,
			@RequestParam(value = "title", defaultValue = "") String title) {

		HashMap<String, Object> categrpSearch = new HashMap<String, Object>();
		categrpSearch.put("categrpno", categrpno);
		categrpSearch.put("title", title);

		int totalcount = ContentsProc.pagingCount(categrpSearch);
		if (totalcount != 0) {
			PageMaker pagemaker = new PageMaker();
			pagemaker.setTotalcount(totalcount); // �� ������ ����

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
			HashMap<String, Object> map = new HashMap<String, Object>();

			map.put("startPageNum", pagemaker.getStartPageNum());
			map.put("endPageNum", pagemaker.getEndPageNum());
			map.put("categrpno", categrpno);
			map.put("title", title);

			list = ContentsProc.list(map);
			String categrpName = cateGroupProc.select(categrpno);

			return new ModelAndView("contents/list").addObject("list", list).addObject("page", pagemaker)
					.addObject("categrpno", categrpno).addObject("title", title).addObject("categrpName", categrpName);
		} else {
			// ȭ�鿡 �ѷ��� �Խñ��� 0���϶�
			return new ModelAndView("contents/list");
		}
	}

	/**
	 * �Խ��� �б�
	 * 
	 * @return
	 */
	@GetMapping("/read")
	public ModelAndView read(int contentsNo) {

		ModelAndView mav = new ModelAndView("/contents/read");
		ContentsVO contentsVO = ContentsProc.read(contentsNo);
		mav.addObject("contentsVO", contentsVO);

		return mav;
	}

	/**
	 * �������� �̹��� �������� Ajax
	 * 
	 * @param contentsNo
	 * @return
	 */
	@GetMapping("read_file")
	public @ResponseBody String readFileAjax(int contentsNo) {
		JSONObject json = new JSONObject();
		List<ContentsVO> list = ContentsProc.contentsImageLoad(contentsNo);
		json.put("file_read", list);
		return json.toString();
	}

	/**
	 * �̹��� ����
	 * 
	 * @param contentsNo
	 * @return
	 */
	@GetMapping("/images_update")
	public ModelAndView images_update(int contentsNo) {
		ModelAndView mav = new ModelAndView("contents/images_update");
		List<ContentsVO> list = ContentsProc.imagesAllLoad(contentsNo);
		mav.addObject("image_list", list);
		mav.addObject("contentsNo", contentsNo);
		return mav;
	}

	/**
	 * �̹��� ����
	 * 
	 * @param image
	 * @param contentsNo
	 * @return
	 */
	@GetMapping("/images_delete")
	public @ResponseBody String images_deleteAjax(@RequestParam("fupname") String image,
			@RequestParam("contentsNo") int contentsNo) {
		JSONObject json = new JSONObject();

		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("fupname", image);
		map.put("contentsno", contentsNo);
		ContentsProc.imageDelete(map);

		return json.toString();
	}

	/**
	 * �̹��� ���� ����
	 * 
	 * @param contentsCreateDto
	 * @param contentsNo
	 * @param request
	 * @return
	 */
	@PostMapping("/file_update")
	public ModelAndView file_update(ContentsCreateDto contentsCreateDto, int contentsNo, HttpServletRequest request) {
		ModelAndView mav = new ModelAndView("redirect:/contents/read?contentsNo=" + contentsNo);
		String upDir = Tool.getRealPath(request, "/contents/storage");
		HashMap<String, Object> map = new HashMap<String, Object>();

		MultipartFile fthum = contentsCreateDto.getFthum();
		boolean isEmpty = fthum.getOriginalFilename().isEmpty();
		// ����� �����

		if (!isEmpty) {
			List<ContentsVO> list = ContentsProc.imagesAllLoad(contentsNo);
			if (!list.isEmpty()) {
				for (ContentsVO vo : list) {
					if (vo.getThumb() != null) {
						Tool.deleteFile(upDir, vo.getThumb());
						map.put("thumb", vo.getThumb());
						map.put("contentsno", vo.getContentsNo());
						ContentsProc.imageDelete(map);
					}
				}
				System.out.println("���� ����");
			}
			String fupname = Upload.saveFileSpring(fthum, upDir);
			if (!fupname.isEmpty()) {
				long fsize = fthum.getSize();
				String fname = fthum.getOriginalFilename();
				String thumb = Tool.preview(upDir, fupname, 200, 300);
				Tool.deleteFile(upDir, fupname);

				map.put("fsize", fsize);
				map.put("fname", fname);
				map.put("thumb", thumb);
				map.put("contentsno", contentsNo);

				ContentsProc.create(map);
			}
		}

		List<MultipartFile> fnamesMF = contentsCreateDto.getFnamesMF();
		isEmpty = fnamesMF.get(0).isEmpty();
		if (!isEmpty) {
			List<ContentsVO> list = ContentsProc.imagesAllLoad(contentsNo);
			if (list.isEmpty()) {
				for (ContentsVO vo : list) {
					boolean pass = Tool.deleteFile(upDir, vo.getFupname()); // ���� ���� ����
					if (pass) {
						map.put("fupname", vo.getFupname());
						map.put("contentsno", contentsNo);
						ContentsProc.imageDelete(map);// DB���� ����
					}
				}
			}

			for (MultipartFile file : fnamesMF) {
				String fupname = Upload.saveFileSpring(file, upDir); // ���� ��������
				long fszie = file.getSize();
				String fname = file.getOriginalFilename();

				map.put("fupname", fupname);
				map.put("fsize", fszie);
				map.put("fname", fname);
				map.put("contentsNo", contentsNo);
				ContentsProc.create(map);// DB�� ����
			}
		}
		return mav;
	}

	/**
	 * �� ����
	 * 
	 * @return
	 */
	@GetMapping("/update")
	public ModelAndView update(int contentsNo) {
		ModelAndView mav = new ModelAndView("contents/update");
		ContentsVO vo = ContentsProc.read(contentsNo);
		List<cateGroupVO> list = cateGroupProc.stockCateGroup();

		mav.addObject("contentsVO", vo);
		mav.addObject("cate", list);
		return mav;
	}

	/**
	 * ���� proc
	 * 
	 * @param map
	 * @return
	 */
	@PostMapping("/update")
	public ModelAndView updateProc(@RequestParam Map<String, Object> map) {
		String contentsNo = map.get("contentsNo").toString();
		ModelAndView mav = new ModelAndView("redirect:/contents/read?contentsNo=" + contentsNo);
		int count = ContentsProc.update(map);
		if (count > 0) {
			System.out.println("����");
		}
		return mav;
	}

	/**
	 * ��� ��� ��������
	 * 
	 * @param categrpNo
	 * @return
	 */
	@GetMapping(value = "/repository_select", produces = "text/plain;charset=UTF-8")
	public @ResponseBody String repository_select(int categrpNo) {
		JSONObject json = new JSONObject();
		List<stockVO> list = ContentsProc.CateGroupAjax(categrpNo);
		json.put("repositoy_select", list);
		return json.toString();
	}

	@PostMapping("/delete")
	public @ResponseBody String deleteAjax(int contentsNo) {
		System.out.println(contentsNo);
		HashMap<String, Object> map = new HashMap<>();

		ContentsProc.imagesAllLoad(contentsNo).stream().forEach(c -> {
			if (c.getThumb() != null) {
				// �����
				String thumb = c.getThumb();
				boolean pass = Tool.deleteFile("contents/storge", thumb);
				if (pass) {
					map.put("contentsno", contentsNo);
					map.put("thumb", thumb);
					ContentsProc.imageDelete(map);
				}
			} else if (c.getFupname() != null) {
				// �̹���
				String fupname = c.getFupname();
				boolean pass = Tool.deleteFile("contents/storge", fupname);
				if (pass) {
					map.put("contentsno", contentsNo);
					map.put("fupname", fupname);
					ContentsProc.imageDelete(map);
				}
			}
		});
		
		int categrpNo = ContentsProc.read(contentsNo).getCategrpNo();
		int count = ContentsProc.delete(contentsNo);
		
		if(count > 0) {
		}
		// ���� ��� ����
		// DB���� fileDB => contentsDB ������
		// ī�װ� -1

		JSONObject json = new JSONObject();
		return json.toString();
	}

}
