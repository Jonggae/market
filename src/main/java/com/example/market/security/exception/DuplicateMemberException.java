package com.example.market.security.exception;
/*
* 이미 존재하는 ~~  예외처리
*/
public class DuplicateMemberException extends RuntimeException {
    public DuplicateMemberException() {
        super();
    }
    public DuplicateMemberException(String message, Throwable cause) {
        super(message, cause);
    }
    public DuplicateMemberException(String message) {
        super(message);
    }
    public DuplicateMemberException(Throwable cause) {
        super(cause);
    }
}
