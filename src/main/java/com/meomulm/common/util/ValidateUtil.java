package com.meomulm.common.util;

import com.meomulm.common.exception.BadRequestException;
import com.meomulm.common.exception.NotFoundException;
import org.springframework.stereotype.Component;

@Component
public class ValidateUtil {

    public void validateEmail(String email) {
        String regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

        if (email.isEmpty()) {
            throw new BadRequestException("이메일은 필수 입력 사항입니다.");
        }

        if(!email.matches(regexp)) {
            throw new BadRequestException("이메일 형식이 올바르지 않습니다.");
        }
    }

    public void validatePassword(String password) {
        String regexp = "^[A-Za-z0-9!@#$%^&*(),.?\":{}|<>_\\-+=\\[\\]\\\\/~`]{8,16}$";

        if (password == null || password.isEmpty()) {
            throw new BadRequestException("비밀번호는 필수 입력 사항입니다.");
        }

        if (!password.matches(regexp)) {
            throw new BadRequestException("비밀번호 형식이 올바르지 않습니다.");
        }
    }

    public void validateName(String name) {
        String regexp = "^[a-zA-Z가-힣]+$";

        if (name == null || name.isEmpty()) {
            throw new BadRequestException("이름은 필수 입력 사항입니다.");
        }

        if(name.length() < 2) {
            throw new BadRequestException("이름은 최소 2글자 이상이여야 합니다.");
        }

        if(!name.matches(regexp)){
            throw new BadRequestException("이름 형식이 올바르지 않습니다.");
        }
    }

    public void validatePhone(String phone) {
        String regexp = "^[0-9-]+$";

        if (phone == null || phone.isEmpty()) {
            throw new BadRequestException("전화번호는 필수 입력 사항입니다.");
        }

        if(phone.length() < 10 || phone.length() > 14) {
            throw new BadRequestException("전화번호는 최소 10자 최대 13자까지 가능합니다.");
        }

        if(!phone.matches(regexp)) {
            throw new BadRequestException("전화번호 형식이 올바르지 않습니다.");
        }
    }

    public void validateBirth(String birth) {
        String regexp = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$";
        if (birth == null || birth.isEmpty()) {
            throw new BadRequestException("생년월일은 필수 입력 사항입니다.");
        }

        if(!birth.matches(regexp)) {
            throw new BadRequestException("생년월일 형식이 올바르지 않습니다.(- 입력 필수)");
        }
    }
}
