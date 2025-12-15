package com.security;

import com.password4j.Argon2Function;
import com.password4j.Password;
import com.password4j.types.Argon2;

public class PasswordHasher {

    private static final Argon2Function ARGON2 = Argon2Function.getInstance(
            65536,      // memory (64MB)
            5,          // iterations
            2,          // parallelism
            16,         // output length
            Argon2.ID   // correct enum
    );

    public static String hash(String password) {
        return Password.hash(password)
                .addRandomSalt()
                .withArgon2()
                .getResult();
    }

    public static boolean verify(String password, String storedHash) {
        String hashed = PasswordHasher.hash(password);
        System.out.println("HASH = " + hashed);
        boolean result = Password.check(password,hashed).withArgon2();
        System.out.println("Verify result = " + result);
        return Password.check(password, storedHash)
                .withArgon2();
    }
}