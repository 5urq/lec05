package com.team02.lec05.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.team02.lec05.model.Fruit;
import com.team02.lec05.model.FruitMapper;

@Service
public class AsyncShopService57 {
  private final Logger logger = LoggerFactory.getLogger(AsyncShopService57.class);

  @Autowired
  FruitMapper fMapper;

  // 接続されているすべてのSseEmitterをスレッドセーフに管理
  private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();

  /**
   * DBに登録されたすべての果物のレコードを取得する
   *
   * @return 果物リスト
   */
  public ArrayList<Fruit> syncShowFruitsList() {
    return fMapper.selectAllFruit();
  }

  /**
   * 指定したIDの果物を取得・削除し、全クライアントに最新の果物一覧を非同期プッシュする
   *
   * @param id 果物ID
   * @return 購入した果物
   */
  @Transactional
  public Fruit syncBuyFruits(int id) {
    Fruit fruit = fMapper.selectById(id);
    fMapper.deleteById(id);

    // 削除後の最新フルーツリストを取得
    ArrayList<Fruit> updatedFruits = fMapper.selectAllFruit();

    // 接続されているすべてのクライアントに最新リストをプッシュ
    this.sendFruitsToAll(updatedFruits);

    return fruit;
  }

  /**
   * SseEmitterを受け取り、クライアント一覧に登録する
   *
   * @param emitter SseEmitter
   */
  @Async
  public void asyncShowFruitsList(SseEmitter emitter) {
    logger.info("asyncShowFruitsList start");
    this.emitters.add(emitter);

    // 接続完了またはタイムアウト時にリストから除去
    emitter.onCompletion(() -> {
      logger.info("SseEmitter completed");
      this.emitters.remove(emitter);
    });
    emitter.onTimeout(() -> {
      logger.info("SseEmitter timeout");
      this.emitters.remove(emitter);
    });
    emitter.onError((e) -> {
      logger.info("SseEmitter error: " + e.getMessage());
      this.emitters.remove(emitter);
    });

    try {
      // 接続直後に現在の果物一覧を一度送信
      ArrayList<Fruit> fruits = fMapper.selectAllFruit();
      emitter.send(fruits);
    } catch (IOException e) {
      logger.warn("Exception in initial send: " + e.getMessage());
      emitter.complete();
      this.emitters.remove(emitter);
    }
  }

  /**
   * すべてのSseEmitterに最新の果物リストを送信する
   *
   * @param fruits 果物リスト
   */
  public void sendFruitsToAll(ArrayList<Fruit> fruits) {
    logger.info("sendFruitsToAll called, clients: " + this.emitters.size());
    ArrayList<SseEmitter> deadEmitters = new ArrayList<>();
    for (SseEmitter emitter : this.emitters) {
      try {
        emitter.send(fruits);
      } catch (Exception e) {
        logger.warn("Exception sending to emitter: " + e.getMessage());
        deadEmitters.add(emitter);
      }
    }
    this.emitters.removeAll(deadEmitters);
  }
}
