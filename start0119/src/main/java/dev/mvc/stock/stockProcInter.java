package dev.mvc.stock;

import java.util.List;

public interface stockProcInter {

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
	
}
