package com.team02.lec05.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.team02.lec05.service.AsyncCount58;

/**
 * /sample58へのリクエストを扱うクラス
 */
@Controller
@RequestMapping("/sample58")
public class Sample58Controller {

  private final Logger logger = LoggerFactory.getLogger(Sample58Controller.class);

  @Autowired
  private AsyncCount58 ac58;

  /**
   * ロールごとに数字をカウントアップしながらpushしつづけるメソッド
   *
   * @param auth 認証情報
   * @return SseEmitter
   */
  @GetMapping("step1")
  public SseEmitter pushCount(Authentication auth) {
    logger.info("pushCount");

    final SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

    String role = "";
    if (auth != null) {
      for (GrantedAuthority authority : auth.getAuthorities()) {
        String authName = authority.getAuthority();
        if (authName.contains("CUSTOMER")) {
          role = "CUSTOMER";
          break;
        } else if (authName.contains("SELLER")) {
          role = "SELLER";
          break;
        }
      }
    }

    try {
      this.ac58.count(emitter, role);
    } catch (IOException e) {
      logger.warn("Exception:" + e.getClass().getName() + ":" + e.getMessage());
      emitter.complete();
    }
    return emitter;
  }

}
