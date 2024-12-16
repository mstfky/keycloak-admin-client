package com.teksi.keycloak.admin;

import com.teksi.keycloak.admin.service.KeyCloakClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.idm.UserSessionRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/keycloak")
public class AdminClientController {

    private final KeyCloakClientService keyCloakClientService;

    @Autowired
    public AdminClientController(KeyCloakClientService keyCloakClientService) {
        this.keyCloakClientService = keyCloakClientService;
    }

    @Operation(summary = "Create a new realm", description = "Creates a new realm in Keycloak with the given name.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Realm created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid realm name"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PostMapping("/realms")
    public ResponseEntity<String> createRealm(@RequestParam String realmName) {
        keyCloakClientService.createRealm(realmName);
        return ResponseEntity.ok("OK");
    }

    @Operation(summary = "Update a realm", description = "Updates an existing realm in Keycloak with the provided attributes.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Realm updated successfully"),
            @ApiResponse(responseCode = "404", description = "Realm not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PutMapping("/realms/{realmName}")
    public ResponseEntity<String> updateRealm(@PathVariable String realmName, @RequestBody Map<String, Object> updates) {
        keyCloakClientService.updateRealm(realmName, updates);
        return ResponseEntity.ok("OK");
    }

    @Operation(summary = "Delete a realm", description = "Deletes a specified realm from Keycloak.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Realm deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Realm not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @DeleteMapping("/realms/{realmName}")
    public ResponseEntity<String> deleteRealm(@PathVariable String realmName) {
        keyCloakClientService.deleteRealm(realmName);
        return ResponseEntity.ok("OK");
    }

    @Operation(summary = "Create a role", description = "Creates a new role in Keycloak with the given name.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Role created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid role name"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PostMapping("/roles")
    public ResponseEntity<String> createRole(@RequestParam String roleName) {
        keyCloakClientService.createRole(roleName);
        return ResponseEntity.ok("OK");
    }

    @Operation(summary = "Update a role", description = "Updates a role with the given name and attributes.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Role updated successfully"),
            @ApiResponse(responseCode = "404", description = "Role not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PutMapping("/roles/{roleName}")
    public ResponseEntity<String> updateRole(@PathVariable String roleName, @RequestBody Map<String, Object> updates) {
        keyCloakClientService.updateRole(roleName, updates);
        return ResponseEntity.ok("OK");
    }

    @Operation(summary = "Delete a role", description = "Deletes a role in Keycloak.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Role deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Role not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @DeleteMapping("/roles/{roleName}")
    public ResponseEntity<String> deleteRole(@PathVariable String roleName) {
        keyCloakClientService.removeRole(roleName);
        return ResponseEntity.ok("OK");
    }

    @Operation(summary = "List roles", description = "Fetches a list of all roles in Keycloak.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Roles fetched successfully"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping("/roles")
    public ResponseEntity<List<RoleRepresentation>> listRoles() {
        List<RoleRepresentation> roles = keyCloakClientService.listRoles();
        return ResponseEntity.ok(roles);
    }

    @Operation(summary = "Add composite role", description = "Adds composite roles to a parent role.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Composite roles added successfully"),
            @ApiResponse(responseCode = "404", description = "Parent role not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PostMapping("/roles/composites/{parentRole}")
    public ResponseEntity<String> addCompositeRole(@PathVariable String parentRole, @RequestBody List<String> childRoles) {
        keyCloakClientService.addCompositeRole(parentRole, childRoles);
        return ResponseEntity.ok("OK");
    }

    @Operation(summary = "Remove composite role", description = "Removes composite roles from a parent role.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Composite roles removed successfully"),
            @ApiResponse(responseCode = "404", description = "Parent role not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @DeleteMapping("/roles/composites/{parentRole}")
    public ResponseEntity<String> removeCompositeRole(@PathVariable String parentRole, @RequestBody List<String> childRoles) {
        keyCloakClientService.removeCompositeRole(parentRole, childRoles);
        return ResponseEntity.ok("OK");
    }

    @Operation(summary = "Grant a role to a user", description = "Assigns a role to a user in Keycloak.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Role granted successfully"),
            @ApiResponse(responseCode = "404", description = "User or role not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PostMapping("/roles/grant/{userId}/{roleName}")
    public ResponseEntity<String> grantRoleToUser(@PathVariable String userId, @PathVariable String roleName) {
        keyCloakClientService.grantRoleToUser(userId, roleName);
        return ResponseEntity.ok("OK");
    }

    @Operation(summary = "Revoke a role from a user", description = "Revokes a specific role from a user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Role revoked successfully"),
            @ApiResponse(responseCode = "404", description = "User or role not found")
    })
    @DeleteMapping("/roles/revoke/{userId}/{roleName}")
    public ResponseEntity<String> revokeRoleFromUser(@PathVariable String userId, @PathVariable String roleName) {
        keyCloakClientService.revokeRoleFromUser(userId, roleName);
        return ResponseEntity.ok("OK");
    }

    @Operation(
            summary = "Revoke all roles from a user",
            description = "Revokes all roles assigned to a specific user in Keycloak"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "All roles revoked successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/roles/revoke/{userId}")
    public ResponseEntity<String> revokeAllRolesFromUser(@PathVariable String userId) {
        keyCloakClientService.revokeAllRolesFromUser(userId);
        return ResponseEntity.ok("OK");
    }

    @Operation(
            summary = "Revoke all roles",
            description = "Revokes all roles from all users in Keycloak"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "All roles revoked successfully"),
            @ApiResponse(responseCode = "500", description = "Server error during the revocation process")
    })
    @DeleteMapping("/roles/revoke")
    public ResponseEntity<String> revokeAllRoles() {
        keyCloakClientService.revokeAllRoles();
        return ResponseEntity.ok("OK");
    }

    @Operation(
            summary = "Create a group",
            description = "Creates a new group in Keycloak with the specified name"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Group created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input provided"),
            @ApiResponse(responseCode = "500", description = "Server error during group creation")
    })
    @PostMapping("/groups")
    public ResponseEntity<String> createGroup(@RequestParam String groupName) {
        keyCloakClientService.createGroup(groupName);
        return ResponseEntity.ok("OK");
    }


    @Operation(
            summary = "Update a group",
            description = "Updates the specified group in Keycloak with the new details provided"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Group updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input provided"),
            @ApiResponse(responseCode = "404", description = "Group not found"),
            @ApiResponse(responseCode = "500", description = "Server error during group update")
    })
    @PutMapping("/groups/{groupName}")
    public ResponseEntity<String> updateGroup(@PathVariable String groupName, @RequestParam String newGroupName, @RequestBody GroupRepresentation groupRepresentation) {
        keyCloakClientService.updateGroup(groupName, newGroupName, groupRepresentation);
        return ResponseEntity.ok("OK");
    }

    @Operation(
            summary = "Delete a group",
            description = "Deletes the specified group from Keycloak"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Group deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Group not found"),
            @ApiResponse(responseCode = "500", description = "Server error during group deletion")
    })
    @DeleteMapping("/groups/{groupName}")
    public ResponseEntity<String> deleteGroup(@PathVariable String groupName) {
        keyCloakClientService.deleteGroup(groupName);
        return ResponseEntity.ok("OK");
    }

    @Operation(
            summary = "List all groups",
            description = "Retrieves a list of all groups available in Keycloak"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Groups retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Server error during groups retrieval")
    })
    @GetMapping("/groups")
    public ResponseEntity<List<GroupRepresentation>> listGroups() {
        List<GroupRepresentation> groups = keyCloakClientService.listGroups();
        return ResponseEntity.ok(groups);
    }

    @Operation(summary = "List groups for a specific user",
            description = "User Management"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of groups"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/groups/user/{userId}")
    public ResponseEntity<List<GroupRepresentation>> listGroupsForUser(@PathVariable String userId) {
        List<GroupRepresentation> groups = keyCloakClientService.listGroupsForUser(userId);
        return ResponseEntity.ok(groups);
    }

    @Operation(summary = "List users for a specific group",
            description = "Group Management"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of users"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Group not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/groups/{userId}")
    public ResponseEntity<List<UserRepresentation>> listUsersForGroup(@PathVariable String userId) {
        List<UserRepresentation> users = keyCloakClientService.listUsersInGroup(userId);
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Assign a group to a specific user",
            description = "User Management"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully assigned group to user"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "User or group not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/groups/user/{userId}")
    public ResponseEntity<String> assignGroupToUser(@RequestParam String groupName, @PathVariable String userId) {
        keyCloakClientService.assignGroupToUser(userId, groupName);
        return ResponseEntity.ok("OK");
    }


    @Operation(summary = "Revoke a group from a specific user",
            description = "User Management"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully revoked group from user"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "User or group not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/groups/revoke/{userId}")
    public ResponseEntity<String> revokeGroupFromUser(@PathVariable String userId, @RequestParam String groupName) {
        keyCloakClientService.revokeGroupFromUser(userId, groupName);
        return ResponseEntity.ok("OK");
    }

    @Operation(summary = "List groups for all users",
            description = "Group Management"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of user groups"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/groups/user")
    public ResponseEntity<List<UserRepresentation>> listUserGroups() {
        List<UserRepresentation> users = keyCloakClientService.listUserGroups();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "List groups for all groups",
            description = "Group Management"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of groups for groups"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/groups/group")
    public ResponseEntity<List<GroupRepresentation>> listGroupGroups() {
        List<GroupRepresentation> groups = keyCloakClientService.listGroupGroups();
        return ResponseEntity.ok(groups);
    }

    @Operation(summary = "List user group groups",
            description = "Group Management"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of user group groups"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/groups/group/user")
    public ResponseEntity<List<GroupRepresentation>> listUserGroupGroups() {
        List<GroupRepresentation> groups = keyCloakClientService.listUserGroupGroups();
        return ResponseEntity.ok(groups);
    }


    @Operation(summary = "Create a new user",
            description = "User Management"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully created user"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/users")
    public ResponseEntity<UserRepresentation> createUser(@RequestBody UserRepresentation user) {
        keyCloakClientService.createUser(user);
        return ResponseEntity.ok(user);
    }


    @Operation(summary = "Update an existing user",
            description = "User Management"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated user"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })

    @PutMapping("/users")
    public ResponseEntity<UserRepresentation> updateUser(@RequestBody UserRepresentation user) {
        keyCloakClientService.updateUser(user);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Delete an existing user",
            description = "User Management"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted user"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable String userId) {
        keyCloakClientService.deleteUser(userId);
        return ResponseEntity.ok("OK");
    }

    @Operation(summary = "Enable a user account",
            description = "User Management"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully enabled user account"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/users/status/{userId}/enable")
    public ResponseEntity<String> enableUser(@PathVariable String userId) {
        keyCloakClientService.enableUser(userId);
        return ResponseEntity.ok("OK");
    }

    @Operation(summary = "Disable a user account",
            description = "User Management"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully disabled user account"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/users/status/{userId}/disable")
    public ResponseEntity<String> disableUser(@PathVariable String userId) {
        keyCloakClientService.disableUser(userId);
        return ResponseEntity.ok("OK");
    }


    @Operation(summary = "Update a user's password",
            description = "User Management"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated user password"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/users/password/{userId}")
    public ResponseEntity<String> updateUserPassword(@PathVariable String userId, @RequestParam String newPassword, @RequestParam String oldPassword) {
        keyCloakClientService.updatePassword(userId, newPassword, oldPassword);
        return ResponseEntity.ok("OK");
    }


    @Operation(summary = "Log out a user",
            description = "User Management"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully logged out user"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/users/status/{userId}/logout")
    public ResponseEntity<String> logoutUser(@PathVariable String userId) {
        keyCloakClientService.logoutUser(userId);
        return ResponseEntity.ok("OK");
    }

    @Operation(summary = "List all sessions for a specific user",
            description = "User Management"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of user sessions"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/users/sessions/{userId}")
    public ResponseEntity<List<UserSessionRepresentation>> listUserSessions(@PathVariable String userId) {
        List<UserSessionRepresentation> sessions = keyCloakClientService.listUserSessions(userId);
        return ResponseEntity.ok(sessions);
    }

    @Operation(summary = "Get user information by user ID",
            description = "User Management"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user information"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/users/{userId}")
    public ResponseEntity<UserRepresentation> getUserInformation(@PathVariable String userId) {
        UserRepresentation user = keyCloakClientService.getUserInformation(userId);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "List all users",
            description = "User Management"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of users"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/users")
    public ResponseEntity<List<UserRepresentation>> listUsers() {
        List<UserRepresentation> users = keyCloakClientService.listUsers();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Create a new permission",
            description = "Permission Management"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully created permission"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/permissions")
    public ResponseEntity<String> createPermission(@RequestParam String permissionName, @RequestBody Map<String, Object> attributes) {
        keyCloakClientService.createPermission(permissionName, attributes);
        return ResponseEntity.ok("OK");
    }


    @Operation(summary = "Update an existing permission",
            description = "Permission Management"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated permission"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Permission not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/permissions")
    public ResponseEntity<String> updatePermission(@RequestParam String permissionName, @RequestBody Map<String, Object> attributes) {
        keyCloakClientService.updatePermission(permissionName, attributes);
        return ResponseEntity.ok("OK");
    }

    @Operation(summary = "Delete an existing permission",
            description = "Permission Management"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted permission"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Permission not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/permissions")
    public ResponseEntity<String> deletePermission(@RequestParam String permissionName) {
        keyCloakClientService.deletePermission(permissionName);
        return ResponseEntity.ok("OK");
    }

    @Operation(summary = "Add a custom attribute",
            description = "Custom Attribute Management"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully added custom attribute"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/custom/attribute")
    public ResponseEntity<String> addCustomAttribute(@RequestParam String attributeName, @RequestParam String key, @RequestParam String value) {
        keyCloakClientService.addCustomAttribute(attributeName, key, value);
        return ResponseEntity.ok("OK");
    }

    @Operation(summary = "Remove a custom attribute from a user",
            description = "Custom Attribute Management"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully removed custom attribute"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/custom/attribute/{userId}")
    public ResponseEntity<String> removeCustomAttribute(@PathVariable String userId, @RequestParam String key) {
        keyCloakClientService.removeCustomAttribute(userId, key);
        return ResponseEntity.ok("OK");
    }

    @Operation(summary = "List custom attributes for a user",
            description = "Custom Attribute Management"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved custom attributes"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/custom/attribute")
    public ResponseEntity<Map<String, String>> listCustomAttributes(@RequestBody UserRepresentation user) {
        Map<String, String> customAttributes = keyCloakClientService.listCustomAttributes(user);
        return ResponseEntity.ok(customAttributes);
    }

    @Operation(summary = "Revoke all users",
            description = "User Management"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully revoked all users"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/users/revoke")
    public ResponseEntity<String> revokeAllUsers() {
        keyCloakClientService.revokeAllUsers();
        return ResponseEntity.ok("OK");
    }

    @Operation(summary = "Revoke all groups",
            description = "Group Management"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully revoked all groups"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/groups/revoke")
    public ResponseEntity<String> revokeAllGroups() {
        keyCloakClientService.revokeAllGroups();
        return ResponseEntity.ok("OK");
    }


    @Operation(summary = "Revoke all user groups",
            description = "User Group Management"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully revoked all user groups"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/groups/user/revoke")
    public ResponseEntity<String> revokeAllUserGroups() {
        keyCloakClientService.revokeAllUserGroups();
        return ResponseEntity.ok("OK");
    }

    @Operation(summary = "Revoke all group groups",
            description = "Group Group Management"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully revoked all group groups"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/groups/group/revoke")
    public ResponseEntity<String> revokeAllGroupGroups() {
        keyCloakClientService.revokeAllGroupGroups();
        return ResponseEntity.ok("OK");
    }

    @Operation(summary = "Revoke all user group groups",
            description = "User Group Group Management"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully revoked all user group groups"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/groups/group/revoke/user")
    public ResponseEntity<String> revokeAllUserGroupGroups() {
        keyCloakClientService.revokeAllUserGroupGroups();
        return ResponseEntity.ok("OK");
    }

}
