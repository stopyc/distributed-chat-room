package org.example.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;

/**
 * 当缓存读写异常时,忽略异常
 * @author YC104
 */
@Slf4j
public class IgnoreExceptionCacheErrorHandler implements CacheErrorHandler {


	@Override
	public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
		log.error(exception.getMessage(), exception);
	}

	@Override
	public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
		log.error(exception.getMessage(), exception);
	}

	@Override
	public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
		log.error(exception.getMessage(), exception);
	}

	@Override
	public void handleCacheClearError(RuntimeException exception, Cache cache) {
		log.error(exception.getMessage(), exception);
	}
}
