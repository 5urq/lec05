package com.team02.lec05.service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class AsyncCount58 {
  private int customerCount = 1;
  private int sellerCount = 1;
  private final Logger logger = LoggerFactory.getLogger(AsyncCount58.class);

  @Async
  public void count(SseEmitter emitter, String role) throws IOException {
    logger.info("count start for role: " + role);
    try {
      while (true) {
        int count = 0;
        if ("CUSTOMER".equals(role)) {
          count = customerCount++;
        } else if ("SELLER".equals(role)) {
          count = sellerCount++;
        }
        logger.info("send id:" + role + ", data:" + count);
        emitter.send(SseEmitter.event().id(role).data(count));
        TimeUnit.SECONDS.sleep(1);
      }
    } catch (InterruptedException e) {
      logger.warn("Exception:" + e.getClass().getName() + ":" + e.getMessage());
    }
  }
}
