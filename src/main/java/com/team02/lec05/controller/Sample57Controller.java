package com.team02.lec05.controller;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.team02.lec05.model.Fruit;
import com.team02.lec05.service.AsyncShopService57;

/**
 * /sample5へのリクエスト（step7, step8, step9）を扱うコントローラ
 */
@Controller
@RequestMapping("/sample5")
public class Sample57Controller {
  private final Logger logger = LoggerFactory.getLogger(Sample57Controller.class);

  @Autowired
  AsyncShopService57 shopService;

  /**
   * sample57.htmlを表示する処理
   *
   * @param model ModelMap
   * @return sample57.html
   */
  @GetMapping("step7")
  public String sample57(ModelMap model) {
    logger.info("sample57 called");
    ArrayList<Fruit> fruits = shopService.syncShowFruitsList();
    model.addAttribute("fruits", fruits);
    return "sample57.html";
  }

  /**
   * 購入処理
   *
   * @param id 果物ID
   * @param model ModelMap
   * @return sample57.html
   */
  @GetMapping("step8")
  public String sample58(@RequestParam Integer id, ModelMap model) {
    logger.info("sample58 called for id: " + id);
    Fruit fruit = shopService.syncBuyFruits(id);
    model.addAttribute("fruit", fruit);

    ArrayList<Fruit> fruits = shopService.syncShowFruitsList();
    model.addAttribute("fruits", fruits);
    return "sample57.html";
  }

  /**
   * SSEプッシュ用エンドポイント
   *
   * @return SseEmitter
   */
  @GetMapping("step9")
  public SseEmitter sample59() {
    logger.info("sample59 called (SSE connect)");
    final SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
    shopService.asyncShowFruitsList(emitter);
    return emitter;
  }
}
