package com.unir.papertales.orders.controller;

import com.unir.papertales.orders.controller.model.GetUsersResponseDto;
import com.unir.papertales.orders.controller.model.UserDto;
import com.unir.papertales.orders.controller.model.WriteUserRequestDto;
import com.unir.papertales.orders.service.CreateUserService;
import com.unir.papertales.orders.service.DeleteUserService;
import com.unir.papertales.orders.service.GetUsersService;
import com.unir.papertales.orders.service.ModifyUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class UsersController {

    private final GetUsersService getUsersService;
    private final CreateUserService createUserService;
    private final ModifyUserService modifyUserService;
    private final DeleteUserService deleteUserService;

    /** Lista todos los usuarios */
    @GetMapping("/users")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<GetUsersResponseDto> getUsers() {
        return ResponseEntity.ok(getUsersService.getUsers());
    }

    /** Obtiene un usuario por ID */
    @GetMapping("/users/{userId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<UserDto> getUser(@PathVariable Long userId) {
        return ResponseEntity.ok(getUsersService.getUser(userId));
    }

    /** Crea un usuario */
    @PostMapping("/users")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<UserDto> createUser(@RequestBody WriteUserRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createUserService.createUser(request));
    }

    /** Reemplaza completamente un usuario */
    @PutMapping("/users/{userId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable Long userId,
            @RequestBody WriteUserRequestDto request) {
        return ResponseEntity.ok(modifyUserService.updateUser(userId, request));
    }

    /** Actualización parcial de un usuario (JSON Merge Patch) */
    @PatchMapping("/users/{userId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<UserDto> patchUser(
            @PathVariable Long userId,
            @RequestBody String jsonPart) {
        return ResponseEntity.ok(modifyUserService.patchUser(userId, jsonPart));
    }

    /** Elimina un usuario */
    @DeleteMapping("/users/{userId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        deleteUserService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
