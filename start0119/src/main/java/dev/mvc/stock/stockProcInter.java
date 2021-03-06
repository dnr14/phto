package dev.mvc.stock;

import java.util.HashMap;
import java.util.List;

public interface stockProcInter {

	/**
	 * 재고 등록
	 * @param request
	 * @return
	 */
	public int create(stockCreateRequest request);
	
	
	/**
	 * 재고 가져오기
	 * @return
	 */
	public List<stockVO> selectStock(HashMap<String, Object> map);

	
	/**
	 * 재고 삭제
	 * @param stockNo
	 * @return
	 */
	public int delete(int stockNo);
	
	/**
	 * 재고 수정 불러오기
	 * @param stockNo
	 * @return
	 */
	public stockVO update(int stockNo);
	
	/**
	 * 재고 수정
	 * @param stockVO
	 * @return
	 */
	public int updateProc(stockVO stockVO);
	public int selectStockCount();

	
}
