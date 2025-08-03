package com.emocloud.repository.pet;

import com.emocloud.model.pet.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// PetRepository 用于操作 Pet 实体的数据库交互
@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {
    /**
     * 根据宠物名称查找宠物
     * @param petname 宠物名称
     * @return 匹配的宠物列表
     */
    List<Pet> findByPetname(String petname);

    /**
     * 根据主人 ID 查找宠物
     * @param UserId 主人的 ID
     * @return 匹配的宠物列表
     */
    List<Pet> findByUserId(Long UserId);

    /**
     * 根据 ID 查找宠物
     * @param id 宠物的 ID
     * @return 匹配的宠物，如果未找到则返回 Optional.empty()
     */
    Optional<Pet> findById(Long id);
}