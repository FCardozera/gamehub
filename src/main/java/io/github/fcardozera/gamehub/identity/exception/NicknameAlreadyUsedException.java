package io.github.fcardozera.gamehub.identity.exception;

public class NicknameAlreadyUsedException extends RuntimeException {

    public NicknameAlreadyUsedException(String nickname) {
        super("Nickname already registered: " + nickname);
    }
}