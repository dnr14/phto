package dev.mvc.contents;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	/**
	 * 페이징 총 페이지
	 * @return
	 */
	public int pagingCount(HashMap<String,Object> map);
	
	/**
	 * 게시판 상세불러오기
	 * @param contentsNo
	 * @return
	 */
	public ContentsVO read(int contentsNo);
	public int update(Map<String, Object> map);	
	public int delete(int contentNo);
}
