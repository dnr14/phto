package dev.mvc.cateGroup;

import java.util.List;

public interface cateGroupDAOInter {
	
	/**
	 * ī�װ� ����
	 * @param create
	 * @return
	 */
	public int create(cateGroupCreateRequest cgc);
	
	/**
	 * ī�װ� ���
	 * @return
	 */
	public List<cateGroupVO> list();
	
	/**
	 * ī�װ� ��� ����
	 * @return
	 */
	public int cateGroupCount();
	
	/**
	 *  ī�װ� ����
	 * @return
	 */
	public int cateGroupDelete(int categrpno);
	
	/**
	 *  ī�װ� ���̵� ���
	 * @return
	 */
	public List<cateGroupVO> cateGroupSideList();
	
	/**
	 * ī�װ� ������Ʈ
	 * @return
	 */
	public  cateGroupVO cateGroupUpdateForm(cateGroupVO vo);
	
	/**
	 * ī�װ� ���� Proc
	 * @param vo
	 * @return
	 */
	public int cateGroupUpdateProc(cateGroupVO vo);
	
	/**
	 * ī�װ� �׺�� ���
	 * @return
	 */
	public List<String> cateGroupTopList();
	
	/**
	 * ��� ��� ī�װ� ȣ��
	 * @return
	 */
	public List<cateGroupVO> stockCateGroup();
	
}
