package dev.mvc.member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import dev.mvc.tool.Sha256;

@Component("memberProc")
public class memberProc implements memberProcInter {

	@Autowired
	private memberDAOInter memberDAO;

	@Override
	public void register(memberCreateRequest memberCreateRequest) throws Exception {
		int IdCount = selectById(memberCreateRequest.getId());
		if (IdCount == 1) {
			throw new AlreadyExistingIdException(memberCreateRequest.getId() + " �ߺ��� ���̵� �Դϴ�.");
		}
		int EmailCount = selectByEamil(memberCreateRequest.getEmail());
		if (EmailCount == 1) {
			throw new AlreadyExistingEmailException(memberCreateRequest.getEmail() + " �ߺ��� �̸��� �Դϴ�.");
		}

		Sha256 sha256 = new Sha256();
		
		String key = sha256.getKey(false, 20);// ���� ��ȣȭ ����
		String encodingPwd = Sha256.encoding(memberCreateRequest.getPwd());// ��ȣȭ�� pwd
		
		memberCreateRequest.setKey(key); 
		memberCreateRequest.setPwd(encodingPwd);
		
		create(memberCreateRequest); // ȸ������
	}

	@Override
	public int selectByEamil(String email) {
		return memberDAO.selectByEamil(email);
	}

	@Override
	public int selectById(String id) {
		return memberDAO.selectById(id);
	}

	@Override
	public int create(memberCreateRequest mcr) {
		return memberDAO.create(mcr);
	}

	@Override
	public memberVO loginCheck(memberLoginCheck mlc) {
		return memberDAO.loginCheck(mlc);
	}

	@Override
	public int IdFindCount(String email) {
		return memberDAO.IdFindCount(email);
	}

	@Override
	public String IdFind(String email) {
		return memberDAO.IdFind(email);
	}

	@Override
	public int PwdFindCount(memberIdPwdFind memberIdPwdFind) {
		return memberDAO.PwdFindCount(memberIdPwdFind);
	}

	@Override
	public String PwdFind(memberIdPwdFind memberIdPwdFind) {
		return memberDAO.PwdFind(memberIdPwdFind);
	}

	@Override
	public String selectByKey(String id) {
		return memberDAO.selectByKey(id);
	}

	@Override
	public int updateKey(String id) {
		return memberDAO.updateKey(id);
	}

}
