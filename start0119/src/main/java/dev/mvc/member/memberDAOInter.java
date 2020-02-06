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
	
	
	
	
}
