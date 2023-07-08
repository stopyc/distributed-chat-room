package org.example.handler;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.example.pojo.vo.ResultVO;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.example.constant.ResultEnum.FORBIDDEN;

@Component
@Slf4j
public class MyExtendAccessDeniedHandler implements AccessDeniedHandler {


	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
		
		// 自定义返回格式内容
		ResultVO baseResult = ResultVO.fail(FORBIDDEN);

		log.error("权限不足: {}",accessDeniedException.getMessage());

		response.setStatus(HttpStatus.OK.value());
		response.setCharacterEncoding("utf-8");
		response.setHeader("Content-Type", "application/json;charset=UTF-8");
		// 权限不足403
		response.setStatus(HttpStatus.FORBIDDEN.value());
		//response.getWriter().write(new ObjectMapper().writeValueAsString(baseResult));
		response.getWriter().write(JSONObject.toJSONString(baseResult));

	}
}
