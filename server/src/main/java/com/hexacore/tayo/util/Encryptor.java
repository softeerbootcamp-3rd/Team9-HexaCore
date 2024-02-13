package com.hexacore.tayo.util;

import org.mindrot.jbcrypt.BCrypt;

public class Encryptor {

    // 비밀번호 암호화
    public static String encryptPwd(String plainPwd) {
        String salt = BCrypt.gensalt();
        return BCrypt.hashpw(plainPwd, salt);
    }
}
