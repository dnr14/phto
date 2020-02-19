package dev.mvc.contents;

import java.util.List;

import dev.mvc.stock.stockVO;

public interface ContentsDAOInter {

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
	 * ����¡ �� ������
	 * @return
	 */
	public int pagingCount();

}
