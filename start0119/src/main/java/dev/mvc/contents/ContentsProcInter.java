package dev.mvc.contents;

import java.util.HashMap;
import java.util.List;

import dev.mvc.stock.stockVO;

public interface ContentsProcInter {

	/**
	 * ī�װ� ajax
	 * @return
	 */
	public List<stockVO> CateGroupAjax(int categrpNo);
	
	/**
	 * ���� ����
	 * @param ContentsCreateDto
	 * @return
	 */
	public int create(ContentsCreateDto ContentsCreateDto);
	
	/**
	 * ������ ���� ��ȣ ��������
	 * @return
	 */
	public int cotentsNoSelect();
	
	/**
	 * ���� ���ε�
	 * @param map
	 * @return
	 */
	public int create(HashMap<String,Object> map);
	
	/**
	 * �Խñ� ��������
	 * @return
	 */
	public List<ContentsVO> list();
	
}
