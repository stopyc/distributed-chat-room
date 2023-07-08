package org.example.handler;

import lombok.extern.slf4j.Slf4j;
import org.example.pojo.vo.ResultVO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.UnsupportedGrantTypeException;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;

import static org.example.constant.ResultEnum.*;

/**
 * @program: cloud
 * @description: 认证异常翻译器(自定义异常处理)
 * @author: stop.yc
 * @create: 2023-03-22 16:04
 **/
@Slf4j
public class OAuthServerWebResponseExceptionTranslator implements WebResponseExceptionTranslator {
    @Override
    public ResponseEntity<ResultVO> translate(Exception e) throws Exception {
        ResultVO resultVO = doTranslateHandler(e);
        return new ResponseEntity<>(resultVO, HttpStatus.UNAUTHORIZED);
    }

    private ResultVO doTranslateHandler(Exception e) {

        log.error("翻译器错误",e);

        ResultVO result = ResultVO.fail(UNAUTHORIZED, e.getMessage());

        System.out.println(e.getClass());

        if (e instanceof UnsupportedGrantTypeException) {
            result = ResultVO.fail(AUTHENTICATION_METHOD_ERROR);
        } else if (e instanceof InvalidGrantException) {
            result = ResultVO.fail(USERNAME_OR_PASSWORD_ERROR);
        }

        return result;
    }
}
