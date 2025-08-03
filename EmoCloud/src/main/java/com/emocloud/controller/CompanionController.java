package com.emocloud.controller;

import com.emocloud.dto.EmotionCompanionDto.DialogueDTO;
import com.emocloud.dto.EmotionCompanionDto.MessageDTO;
import com.emocloud.dto.EmotionCompanionDto.SceneModeDTO;
import com.emocloud.service.companion.DialogueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/companion")
public class CompanionController {

    @Autowired
    private DialogueService dialogueService;

    // === Dialogue 相关接口 ===
    @PostMapping("/dialogues")
    public ResponseEntity<DialogueDTO> createDialogue(@RequestBody DialogueDTO dialogueDTO) {
        DialogueDTO createdDialogue = dialogueService.createDialogue(dialogueDTO);
        return new ResponseEntity<>(createdDialogue, HttpStatus.CREATED);
    }

    @GetMapping("/dialogues/{dialogueId}")
    public ResponseEntity<DialogueDTO> getDialogueById(@PathVariable Long dialogueId) {
        Optional<DialogueDTO> dialogue = dialogueService.getDialogueById(dialogueId);
        return dialogue.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/dialogues/{dialogueId}")
    public ResponseEntity<DialogueDTO> updateDialogue(@PathVariable Long dialogueId, @RequestBody DialogueDTO dialogueDTO) {
        try {
            DialogueDTO updatedDialogue = dialogueService.updateDialogue(dialogueId, dialogueDTO);
            return new ResponseEntity<>(updatedDialogue, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/dialogues/{dialogueId}")
    public ResponseEntity<Void> closeDialogue(@PathVariable Long dialogueId) {
        dialogueService.closeDialogue(dialogueId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/users/{userId}/dialogues")
    public ResponseEntity<Page<DialogueDTO>> getDialoguesByUser(@PathVariable Long userId, Pageable pageable) {
        Page<DialogueDTO> dialogues = dialogueService.getDialoguesByUser(userId, pageable);
        return new ResponseEntity<>(dialogues, HttpStatus.OK);
    }

    // === Message 相关接口 ===
    @PostMapping("/messages/with-response")
    public ResponseEntity<MessageDTO> sendMessageAndGetResponse(@RequestBody MessageDTO messageDTO) {
        System.out.println("接收到的 dialogueId: " + messageDTO.getDialogueId());

        MessageDTO aiResponse = dialogueService.sendMessageAndGetResponse(messageDTO);
        return new ResponseEntity<>(aiResponse, HttpStatus.CREATED);
    }
    @PostMapping("/messages")
    public ResponseEntity<MessageDTO> sendMessage(@RequestBody MessageDTO messageDTO) {
        MessageDTO sentMessage = dialogueService.sendMessage(messageDTO);
        return new ResponseEntity<>(sentMessage, HttpStatus.CREATED);
    }

    @GetMapping("/messages/{messageId}")
    public ResponseEntity<MessageDTO> getMessageById(@PathVariable Long messageId) {
        Optional<MessageDTO> message = dialogueService.getMessageById(messageId);
        return message.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/dialogues/{dialogueId}/messages")
    public ResponseEntity<Page<MessageDTO>> getMessagesByDialogue(@PathVariable Long dialogueId, Pageable pageable) {
        Page<MessageDTO> messages = dialogueService.getMessagesByDialogue(dialogueId, pageable);
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }

    // === SceneMode 相关接口 ===
    @PutMapping("/dialogues/{dialogueId}/scene-mode")
    public ResponseEntity<DialogueDTO> updateSceneMode(@PathVariable Long dialogueId, @RequestBody SceneModeDTO sceneModeDTO) {
        try {
            DialogueDTO updatedDialogue = dialogueService.updateSceneMode(dialogueId, sceneModeDTO);
            return new ResponseEntity<>(updatedDialogue, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 总结对话内容
     * @param dialogueId 对话ID
     * @return 对话总结
     */
    //这个功能还没加上
    @GetMapping("/dialogues/{dialogueId}/summary")
    public ResponseEntity<String> summarizeDialogue(@PathVariable Long dialogueId) {
        String summary = dialogueService.summarizeDialogue(dialogueId);
        return new ResponseEntity<>(summary, HttpStatus.OK);
    }

}