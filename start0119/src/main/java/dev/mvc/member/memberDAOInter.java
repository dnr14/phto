package dev.mvc.member;

import java.util.HashMap;
import java.util.List;

import dev.mvc.tool.UserPageMaker;

public interface memberDAOInter {

	/**
	 * ���̵� ����
	 * @param mVO
	 * @return ���� 1 ���� 0
	 */
	public int create(memberCreateRequest mcr);
	/**
	 * �̸��� �ߺ� Ȯ��
	 * @param email
	 * @return �ߺ� 1 ���ߺ� 0
	 */
	public int selectByEamil(String email);
	
	/**
	 * ���̵� �ߺ� Ȯ��
	 * @param id
	 * @return �ߺ� 1 ���ߺ� 0
	 */
	public int selectById(String id);

	/**
	 * �α��� 
	 * @param mlc
	 * @return ���� null
	 */
	public memberVO loginCheck(memberLoginCheck mlc);
	
	/**
	 * Id ����
	 * @param email
	 * @return
	 */
	public int IdFindCount(String email);
	
	/**
	 * id ã��
	 * @param email
	 * @return
	 */
	public String IdFind(String email);
	
	/**
	 * ��й�ȣ ����
	 * @param memberIdPwdFind
	 * @return
	 */
	public int PwdFindCount(memberIdPwdFind memberIdPwdFind);
	
	/**
	 * ��й�ȣ ã��
	 * @param memberIdPwdFind
	 * @return
	 */
	public String PwdFind(memberIdPwdFind memberIdPwdFind);
	
	/**
	 * key ��������
	 * @return
	 */
	public String selectByKey(String id); 
	
	/**
	 * key update
	 * @param id
	 * @return
	 */
	public int updateKey(String id);
	
	/**
	 * ȸ������ ��ȸ
	 * @param id
	 * @return
	 */
	public memberVO memberSelect(String id);
	
	/**
	 * ȸ������ ����
	 * @param memberCreateRequest
	 * @return
	 */
	public int memberUpdate(memberCreateRequest memberCreateRequest);
	
	
	/**
	 *  ��й�ȣ ����
	 * @return
	 */
	public int memberPwdUpdate(HashMap<String,Object> map);
	
	
	/**
	 * ȸ�� ����
	 * @param id
	 * @return
	 */
	public int memberDelete(String id);
	
	public List<memberVO> userAll(UserPageMaker userPageMaker);
	public int userTotal();
	
}
