package dev.mvc.contents;

import java.util.List;

import dev.mvc.stock.stockVO;

public interface ContentsDAOInter {

	/**
	 * 카테고리 ajax
	 * @return
	 */
	public List<stockVO> CateGroupAjax(int categrpNo);
	
	/**
	 * 내용 생성
	 * @param ContentsCreateDto
	 * @return
	 */
	public int create(ContentsCreateDto ContentsCreateDto);

	/**
	 * 생성된 내용 번호 가져오기
	 * @return
	 */
	public int cotentsNoSelect();
	

}
