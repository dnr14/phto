package dev.mvc.member;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class RegisterRequestValidator implements Validator {

	private static final String emailRegExp = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	private static final String phoneNumberExp = "^01[017]-\\d{3,4}-\\d{4}";

	private Matcher matcher;
	
	public RegisterRequestValidator() {
		System.out.println("ȸ�� ���� �˻� ���� ===>");
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return memberCreateRequest.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		memberCreateRequest mcr = (memberCreateRequest) target;

		if (mcr.getEmail() == null || mcr.getEmail().trim().isEmpty()) {
			errors.rejectValue("email", "required", "�ʼ� ���� �Դϴ�.");
		} else {
			matcher = Pattern.compile(emailRegExp).matcher(mcr.getEmail());
			if (!matcher.matches()) {
				errors.rejectValue("email", "bad", "�ùٸ��� �ʴ� �����Դϴ�.");
			}
				
			matcher = Pattern.compile(phoneNumberExp).matcher(mcr.getPhone());
			if (!matcher.matches()) {
				errors.rejectValue("phone", "bad", "�ùٸ��� �ʴ� �����Դϴ�.");
			}
		}

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "id", "required", "�ʼ� �����Դϴ�.");
		ValidationUtils.rejectIfEmpty(errors, "pwd", "required", "�ʼ� �����Դϴ�.");
		ValidationUtils.rejectIfEmpty(errors, "phone", "required", "�ʼ� �����Դϴ�.");
		ValidationUtils.rejectIfEmpty(errors, "zipcode", "required", "�ʼ� �����Դϴ�.");
		ValidationUtils.rejectIfEmpty(errors, "address1", "required", "�ʼ� �����Դϴ�.");
		ValidationUtils.rejectIfEmpty(errors, "address2", "required", "�ʼ� �����Դϴ�.");

		if (!mcr.getPwd().isEmpty()) {
			if (mcr.getPwd_check().isEmpty()) {
				ValidationUtils.rejectIfEmpty(errors, "pwd_check", "required", "�ʼ� �����Դϴ�.");
			} else if (!mcr.isPwCheck()) {
				errors.rejectValue("pwd_check", "nomatch", "��й�ȣ�� ��ġ���� �ʽ��ϴ�.");
			}
		} else {
			if (mcr.getPwd_check().isEmpty()) {
				ValidationUtils.rejectIfEmpty(errors, "pwd_check", "required", "�ʼ� �����Դϴ�.");
			}
		}
	}

}
