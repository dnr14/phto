package dev.mvc.contents;

import java.util.HashMap;
import java.util.List;

public interface ContentsFileDAOInter {

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