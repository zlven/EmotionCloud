package com.emocloud.service.pet;

import com.emocloud.dto.PetDTO;
import com.emocloud.model.pet.Pet;
import org.springframework.stereotype.Service;

/**
 * 宠物模块服务接口，定义了与宠物交互的功能，如玩耍和抚摸。
 */
@Service
public interface PetModuleService {
    /**
     * 与宠物玩耍，更新宠物的状态
     * @param petId 宠物ID
     * @return 更新后的宠物信息 DTO
     */
    PetDTO playWithPet(Long petId);

    /**
     * 抚摸宠物，更新宠物的状态
     * @param petId 宠物ID
     * @return 更新后的宠物信息 DTO
     */
    PetDTO strokePet(Long petId);

    /**
     * 将 Pet 实体转换为 PetDTO
     * @param pet 宠物实体
     * @return 宠物信息 DTO
     */
    PetDTO convertToDTO(Pet pet);

    /**
     * 将 PetDTO 转换为 Pet 实体
     * @param petDTO 宠物信息 DTO
     * @return 宠物实体
     */
    Pet convertToEntity(PetDTO petDTO);
}