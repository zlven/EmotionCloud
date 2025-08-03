package com.emocloud.service.pet;

import com.emocloud.dto.PetDTO;
import com.emocloud.model.pet.Pet;
import com.emocloud.repository.pet.PetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PetModuleServiceImpl implements PetModuleService {

    @Autowired
    private PetRepository petRepository;

    @Override
    public PetDTO playWithPet(Long petId) {
        Optional<Pet> petOptional = petRepository.findById(petId);
        if (petOptional.isPresent()) {
            Pet pet = petOptional.get();
            // 假设玩耍能让宠物情绪值增加10，但不超过100
            int newEmotion = Math.min(pet.getEmotion() + 10, 100);
            pet.setEmotion(newEmotion);
            // 假设玩耍会让宠物饥饿值增加5，但不超过100
            if (pet.getHungry() != null) {
                pet.setHungry(Math.min(pet.getHungry() + 5, 100));
            }
            Pet savedPet = petRepository.save(pet);
            return convertToDTO(savedPet);
        }
        return null;
    }

    @Override
    public PetDTO strokePet(Long petId) {
        Optional<Pet> petOptional = petRepository.findById(petId);
        if (petOptional.isPresent()) {
            Pet pet = petOptional.get();
            // 假设抚摸能让宠物情绪值增加5，但不超过100
            int newEmotion = Math.min(pet.getEmotion() + 5, 100);
            pet.setEmotion(newEmotion);
            Pet savedPet = petRepository.save(pet);
            return convertToDTO(savedPet);
        }
        return null;
    }

    @Override
    public PetDTO convertToDTO(Pet pet) {
        PetDTO dto = new PetDTO();
        dto.setId(pet.getId());
        dto.setPetname(pet.getPetname());
        dto.setEmotion(pet.getEmotion());
        dto.setHungry(pet.getHungry());
        dto.setVariety(pet.getVariety());
        if (pet.getUser() != null) {
            dto.setUserId(pet.getUser().getId());
        }
        return dto;
    }

    @Override
    public Pet convertToEntity(PetDTO petDTO) {
        Pet pet = new Pet();
        pet.setId(petDTO.getId());
        pet.setPetname(petDTO.getPetname());
        pet.setEmotion(petDTO.getEmotion());
        pet.setHungry(petDTO.getHungry());
        pet.setVariety(petDTO.getVariety());
        // 这里需要根据 userId 查找 User 实体，暂时先不做关联，可根据实际需求补充
        return pet;
    }
}