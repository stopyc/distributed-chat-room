package org.example.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import feign.Feign;
import org.example.pojo.dto.ResultDTO;
import org.example.pojo.exception.FeignException;

import java.util.Map;

import static org.example.constant.ResultEnum.REQUEST_SUCCESS;

/**
 * @program: chat-room
 * @description: 结果集处理
 * @author: stop.yc
 * @create: 2023-04-24 11:11
 **/
public class ResultUtils {
    public static boolean checkIfSuccess(ResultDTO resultDTO) {
        if (resultDTO.getCode() != REQUEST_SUCCESS.getCode()) {
            throw new FeignException(resultDTO.getMsg());
        }
        return true;
    }


    public static boolean checkIfFail(ResultDTO resultDTO) {

        if (resultDTO.getCode() != REQUEST_SUCCESS.getCode()) {
            throw new FeignException(resultDTO.getMsg());
        }

        return false;
    }

    public static <T> T getResultDtoData(ResultDTO resultDTO, Class<T> classType) {

        if (checkIfFail(resultDTO)) {
            throw new FeignException(resultDTO.getMsg());
        }

        if (resultDTO.getData() == null) {
            return null;
        }

        return BeanUtil.mapToBean((Map<?, ?>) resultDTO.getData(), classType, false, CopyOptions.create());
    }

}
