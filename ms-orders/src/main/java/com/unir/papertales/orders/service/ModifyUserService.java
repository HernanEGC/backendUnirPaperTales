package com.unir.papertales.orders.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.unir.papertales.orders.controller.model.UserDto;
import com.unir.papertales.orders.controller.model.WriteUserRequestDto;
import com.unir.papertales.orders.exception.UserNotFoundException;
import com.unir.papertales.orders.repository.UserJpaRepository;
import com.unir.papertales.orders.repository.model.User;
import com.unir.papertales.orders.utils.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class ModifyUserService {

    private final UserJpaRepository userJpaRepository;
    private final ObjectMapper objectMapper;
    private final UserMapper userMapper;

    @Transactional
    public UserDto updateUser(Long userId, WriteUserRequestDto request) {
        validateRequired(request);
        User existing = userJpaRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        if (!existing.getEmail().equals(request.getEmail())
                && userJpaRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use: " + request.getEmail());
        }
        existing.setName(request.getName());
        existing.setEmail(request.getEmail());
        existing.setPassword(request.getPassword());
        existing.setAvatar(request.getAvatar());

        return userMapper.asUserDto(userJpaRepository.save(existing));
    }

    @Transactional
    public UserDto patchUser(Long userId, String jsonPart) {
        User existing = userJpaRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        try {
            JsonNode patch = objectMapper.readTree(jsonPart);
            rejectPatchField(patch, "id");
            validatePatchFieldNotNull(patch, "name", "User name cannot be null");
            validatePatchFieldNotNull(patch, "email", "User email cannot be null");
            validatePatchFieldNotNull(patch, "password", "User password cannot be null");

            WriteUserRequestDto base = WriteUserRequestDto.builder()
                    .name(existing.getName())
                    .email(existing.getEmail())
                    .password(existing.getPassword())
                    .avatar(existing.getAvatar())
                    .build();

            JsonNode actualUser = objectMapper.valueToTree(base);
            JsonMergePatch mergePatch = JsonMergePatch.fromJson(patch);
            JsonNode patchedNode = mergePatch.apply(actualUser);
            WriteUserRequestDto patched = objectMapper.treeToValue(patchedNode, WriteUserRequestDto.class);

            if (patch.has("email")
                    && patched.getEmail() != null
                    && !patched.getEmail().equals(existing.getEmail())
                    && userJpaRepository.existsByEmail(patched.getEmail())) {
                throw new IllegalArgumentException("Email already in use: " + patched.getEmail());
            }

            existing.setName(patched.getName());
            existing.setEmail(patched.getEmail());
            existing.setPassword(patched.getPassword());
            existing.setAvatar(patched.getAvatar());

            User saved = userJpaRepository.save(existing);
            return userMapper.asUserDto(saved);
        } catch (JsonProcessingException | JsonPatchException e) {
            throw new IllegalArgumentException("Invalid JSON merge patch", e);
        }
    }

    private void rejectPatchField(JsonNode patch, String field) {
        if (patch != null && patch.has(field)) {
            throw new IllegalArgumentException("Field '" + field + "' cannot be patched");
        }
    }

    private void validatePatchFieldNotNull(JsonNode patch, String field, String message) {
        if (patch != null && patch.has(field) && patch.get(field).isNull()) {
            throw new IllegalArgumentException(message);
        }
    }

    private void validateRequired(WriteUserRequestDto request) {
        if (request == null) {
            throw new IllegalArgumentException("User payload is required");
        }
        if (!StringUtils.hasText(request.getName())) {
            throw new IllegalArgumentException("User name is required");
        }
        if (!StringUtils.hasText(request.getEmail())) {
            throw new IllegalArgumentException("User email is required");
        }
        if (!StringUtils.hasText(request.getPassword())) {
            throw new IllegalArgumentException("User password is required");
        }
    }
}
