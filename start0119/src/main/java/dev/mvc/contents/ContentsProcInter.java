package dev.mvc.contents;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	public List<ContentsVO> list(HashMap<String,Object> map );
	/**
	 * ����¡ �� ������
	 * @return
	 */
	public int pagingCount(HashMap<String, Object> map);
	
	/**
	 * �Խ��� �󼼺ҷ�����
	 * @param contentsNo
	 * @return
	 */
	public ContentsVO read(int contentsNo);
	
	public List<ContentsVO> contentsImageLoad(int contentsNo);
	public List<ContentsVO> imagesAllLoad(int contentsNo);
	public int imageDelete(HashMap<String,Object> map);
	public int update(Map<String, Object> map);
	public int delete(int contentNo);

	
}
