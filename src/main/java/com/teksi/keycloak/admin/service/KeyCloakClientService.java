package com.teksi.keycloak.admin.service;

import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.idm.UserSessionRepresentation;

import java.util.List;
import java.util.Map;

public interface KeyCloakClientService {

    /*Realm Management*/
    void createRealm(String realmName);

    void updateRealm(String realmName, Map<String, Object> updates);

    void deleteRealm(String realmName);

    /*Role Management*/
    void createRole(String roleName);

    void removeRole(String roleName);

    void updateRole(String roleName, Map<String, Object> updates);

    RoleRepresentation getRole(String roleName);

    List<RoleRepresentation> listRoles();

    void addCompositeRole(String parentRole, List<String> childRoles);

    void removeCompositeRole(String parentRole, List<String> childRoles);

    void grantRoleToUser(String userId, String roleName);

    void revokeRoleFromUser(String userId, String roleName);

    void revokeAllRolesFromUser(String userId);

    void revokeAllRoles();

    /*Group Management*/
    void createGroup(String groupName);

    void updateGroup(String groupName, String newGroupName, GroupRepresentation updatedGroupRepresentation);

    void deleteGroup(String groupName);

    List<GroupRepresentation> listGroups();

    List<GroupRepresentation> listGroupsForUser(String userId);

    List<UserRepresentation> listUsersInGroup(String groupName);

    void assignGroupToUser(String groupName, String userId);

    void revokeGroupFromUser(String groupName, String userId);

    List<UserRepresentation> listUserGroups();

    List<GroupRepresentation> listGroupGroups();

    List<GroupRepresentation> listUserGroupGroups();

    /*User Management*/
    void createUser(UserRepresentation user);

    void updateUser(UserRepresentation user);

    void deleteUser(String userId);

    void enableUser(String userId);

    void disableUser(String userId);

    void updatePassword(String userId, String oldPassword, String newPassword);

    void logoutUser(String userId);

    List<UserSessionRepresentation> listUserSessions(String userId);

    UserRepresentation getUserInformation(String userId);

    List<UserRepresentation> listUsers();

    /*Permission Management*/
    void createPermission(String permissionName, Map<String, Object> attributes);

    void updatePermission(String permissionName, Map<String, Object> updates);

    void deletePermission(String permissionName);

    /*Custom Attributes Management*/
    void addCustomAttribute(String userId, String key, String value);

    void removeCustomAttribute(String userId, String key);

    Map<String, String> listCustomAttributes(UserRepresentation user);

    /*Mass Revocation*/
    void revokeAllUsers();

    void revokeAllGroups();

    void revokeAllUserGroups();

    void revokeAllGroupGroups();

    void revokeAllUserGroupGroups();
}
