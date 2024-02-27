package com.example.market.handler.login;


import com.example.market.exception.DuplicateMemberException;
import com.example.market.exception.NotFoundMemberException;
import com.example.market.security.dto.ErrorDto;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@ControllerAdvice
public class RestResponseExceptionHandler extends ResponseEntityExceptionHandler {

    //회원 가입 시 중복 회원 정보가 있을 때
    @ResponseStatus(CONFLICT)
    @ExceptionHandler(value = {DuplicateMemberException.class})
    @ResponseBody
    protected ErrorDto conflict(RuntimeException ex, WebRequest request) {
        return new ErrorDto(CONFLICT.value(), ex.getMessage());
    }

    // 권한이 없는 계정으로 접근하였을 때 mvc 레벨
    @ResponseStatus(FORBIDDEN)
    @ExceptionHandler(value = {NotFoundMemberException.class, AccessDeniedException.class})
    @ResponseBody
    protected ErrorDto forbidden(RuntimeException ex, WebRequest request) {
        String additionalMsg = "접근 권한이 없습니다. 계정을 확인해주세요.";
        return new ErrorDto(FORBIDDEN.value(), ex.getMessage(), additionalMsg);
    }
}
