package dev.mvc.stock;

import java.util.List;

public interface stockDAOInter {
	
	/**
	 * ��� ���
	 * @param request
	 * @return
	 */
	public int create(stockCreateRequest request);

	/**
	 * ��� ��������
	 * @return
	 */
	public List<stockVO> selectStock();
	
	/**
	 * ��� ����
	 * @param stockNo
	 * @return
	 */
	public int delete(int stockNo);
	
	/**
	 * ��� ���� �ҷ�����
	 * @param stockNo
	 * @return
	 */
	public stockVO update(int stockNo);
	
	/**
	 * ��� ����
	 * @param stockVO
	 * @return
	 */
	public int updateProc(stockVO stockVO);
	
}
