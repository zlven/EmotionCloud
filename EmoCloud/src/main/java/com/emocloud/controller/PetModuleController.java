package com.emocloud.controller;

import com.emocloud.dto.PetDTO;
import com.emocloud.service.pet.PetModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 宠物模块控制器，处理与宠物模块相关的 HTTP 请求
 */
@RestController
@RequestMapping("/pet-module")
public class PetModuleController {

    @Autowired
    private PetModuleService petModuleService;

    /**
     * 与宠物玩耍
     * @param id 宠物 ID
     * @return 与宠物玩耍后的宠物信息
     */
    @PostMapping("/play/{id}")
    public PetDTO playWithPet(@PathVariable Long id) {
        return petModuleService.playWithPet(id);
    }

    /**
     * 抚摸宠物
     * @param id 宠物 ID
     * @return 抚摸宠物后的宠物信息
     */
    @PostMapping("/stroke/{id}")
    public PetDTO strokePet(@PathVariable Long id) {
        return petModuleService.strokePet(id);
    }
}