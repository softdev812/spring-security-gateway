package com.ptc.gateway.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "ptc-redis")
public interface RedisFeignClient {

    @PostMapping("set")
    void set(@RequestParam("key") String key, @RequestParam("value") String value, @RequestParam("expire") long expire);

    @GetMapping("get")
    public Object get(@RequestParam("key") String key);

    @PostMapping("expire")
    boolean expire(@RequestParam("key") String key, @RequestParam("timeout") long timeout);
}
