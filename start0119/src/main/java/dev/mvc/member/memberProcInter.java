package dev.mvc.member;

public interface memberProcInter {

	/**
	 * ���̵� ����
	 * @param mVO
	 * @return ���� 1 ���� 0
	 */
	public int create(memberCreateRequest mcr);
	
	/**
	 * ��ȿ�� �˻�
	 * @param mcr
	 * @throws Exception
	 */
	void register(memberCreateRequest mcr) throws Exception;
	
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
