package dev.mvc.member;

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
	
	
}
