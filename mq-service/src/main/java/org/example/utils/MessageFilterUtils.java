package org.example.utils;

import org.example.util.RedisUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.example.constant.RedisKey.PREFIX_BAD_LANGUAGE;


/**
 * @program: chat-room
 * @description: 消息敏感词过滤器
 * @author: stop.yc
 * @create: 2023-04-21 14:56
 **/
@Component
public class MessageFilterUtils {

    @Resource
    private RedisUtils redisUtils;

    private List<String> keyWords;


    /**
     * 敏感词过滤
     * @param text :原始文本
     * @return :过滤后的字符串
     */
    public String filter(String text) {

        Set<String> keywords = getKeyWords();

        String key = "";

        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                text = text.replace(keyword, getChar(keyword));
            }
        }

        return text;
    }

    private String getChar(String keyword) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < keyword.length(); i++) {
            str.append("*");
        }
        return str.toString();
    }

    public Set<String> getKeyWords() {
        Set<Object> cacheSet = redisUtils.getCacheSet(PREFIX_BAD_LANGUAGE);
        return cacheSet.stream()
                .map(Object::toString)
                .collect(Collectors.toSet());
    }
}
