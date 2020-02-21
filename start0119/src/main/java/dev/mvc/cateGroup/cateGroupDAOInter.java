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
	public List<cateGroupVO> cateGroupTopList();
	
	/**
	 * ��� ��� ī�װ� ȣ��
	 * @return
	 */
	public List<cateGroupVO> stockCateGroup();
	
	/**
	 * ī�װ� cnt up
	 * @param categrpno
	 * @return
	 */
	public int cateGroupCntUp(int categrpno);
	
	/**
	 * ī�װ� �̸� ȣ��
	 * @param categrpNo
	 * @return
	 */
	public String select(String categrpNo);
	
	public int cateGroupCntDown(int contentsno);
}
