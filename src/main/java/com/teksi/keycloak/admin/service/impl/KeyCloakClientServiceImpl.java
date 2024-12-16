package com.teksi.keycloak.admin.service.impl;

import com.teksi.keycloak.admin.service.KeyCloakClientService;
import jakarta.ws.rs.NotFoundException;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of the {@link KeyCloakClientService} interface that interacts with the Keycloak server
 * to manage authentication and authorization tasks.
 * This service communicates with the Keycloak server using client credentials to perform operations
 * such as obtaining tokens or interacting with the realm.
 *
 * <p>The class is configured using the following properties:
 * <ul>
 *     <li>{@code KEYCLOAK_SERVER_URL} - The URL of the Keycloak server.</li>
 *     <li>{@code KEYCLOAK_REALM_NAME} - The name of the realm in which the Keycloak client operates.</li>
 *     <li>{@code keycloak.client-id} - The client ID used for authentication with the Keycloak server.</li>
 *     <li>{@code keycloak.client-secret} - The client secret used for authentication with the Keycloak server.</li>
 * </ul>
 * </p>
 *
 * <p>This class utilizes the Keycloak Java adapter to build a Keycloak client using the provided configurations.</p>
 */

@Service
public class KeyCloakClientServiceImpl implements KeyCloakClientService {
    private final Keycloak keycloak;
    private final String realmName;

    /**
     * Constructs a new {@code KeyCloakClientServiceImpl} instance using the provided Keycloak server URL,
     * realm name, client ID, and client secret.
     * <p>This constructor initializes the Keycloak client with the specified credentials and server URL.</p>
     *
     * @param serverUrl    The URL of the Keycloak server.
     * @param realmName    The name of the realm to use in Keycloak.
     * @param clientId     The client ID for authenticating with Keycloak.
     * @param clientSecret The client secret for authenticating with Keycloak.
     */
    public KeyCloakClientServiceImpl(
            @Value("${keycloak.auth-server-url}")
            String serverUrl,
            @Value("${keycloak.realm}")
            String realmName,
            @Value("${keycloak.client-id}")
            String clientId,
            @Value("${keycloak.client-secret}")
            String clientSecret,
            @Value("${keycloak.username}")
            String adminUser,
            @Value("${keycloak.password}")
            String adminPassword

    ) {

        this.keycloak = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realmName)
                .grantType("password")
                .clientId(clientId)
                .clientSecret(clientSecret)
                .username(adminUser)
                .password(adminPassword)
                .scope("openid")
                .build();

        this.realmName = realmName;
    }

    /**
     * Creates a new realm with the specified name.
     *
     * @param realmName The name of the realm to create.
     * @throws RuntimeException If the realm creation fails.
     */
    @Override
    public void createRealm(String realmName) {
        try {
            RealmRepresentation realmRepresentation = new RealmRepresentation();
            realmRepresentation.setRealm(realmName);
            realmRepresentation.setEnabled(true);
            keycloak.realms().create(realmRepresentation);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create realm: " + e.getMessage(), e);
        }
    }

    /**
     * Updates an existing realm with the specified name using the provided updates.
     * The updates map contains field names as keys and new values to be set on the realm representation.
     *
     * @param realmName The name of the realm to update.
     * @param updates   A map containing field names and their new values for updating the realm.
     * @throws RuntimeException If the realm update fails.
     */
    @Override
    public void updateRealm(String realmName, Map<String, Object> updates) {
        try {
            RealmResource realmResource = keycloak.realm(realmName);
            RealmRepresentation realmRepresentation = realmResource.toRepresentation();
            updates.forEach((key, value) -> {
                try {
                    Field field = RealmRepresentation.class.getDeclaredField(key);
                    field.setAccessible(true);
                    field.set(realmRepresentation, value);
                } catch (Exception ignored) {
                }
            });
            realmResource.update(realmRepresentation);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update realm: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes the realm with the specified name.
     *
     * @param realmName The name of the realm to delete.
     * @throws RuntimeException If the realm deletion fails.
     */
    @Override
    public void deleteRealm(String realmName) {
        try {
            keycloak.realm(realmName).remove();
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete realm: " + e.getMessage(), e);
        }
    }

    /**
     * Creates a new role within the current realm with the specified role name.
     *
     * @param roleName The name of the role to create.
     * @throws RuntimeException If the role creation fails.
     */
    @Override
    public void createRole(String roleName) {
        RealmResource realmResource = keycloak.realm(realmName);
        try {
            RoleRepresentation roleRepresentation = new RoleRepresentation();
            roleRepresentation.setName(roleName);
            roleRepresentation.setDescription("Role implemented via API");
            realmResource.roles().create(roleRepresentation);
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred while creating role.", e);
        }
    }

    /**
     * Removes the role with the specified role name from the current realm.
     *
     * @param roleName The name of the role to remove.
     * @throws RuntimeException If the role removal fails.
     */
    @Override
    public void removeRole(String roleName) {
        try {
            keycloak.realm(realmName).roles().get(roleName).remove();
        } catch (Exception e) {
            throw new RuntimeException("Failed to remove role: " + e.getMessage(), e);
        }
    }

    /**
     * Updates the specified role with the provided updates.
     * The updates map contains field names as keys and new values to be set on the role representation.
     *
     * @param roleName The name of the role to update.
     * @param updates  A map containing field names and their new values for updating the role.
     * @throws RuntimeException If the role update fails.
     */
    @Override
    public void updateRole(String roleName, Map<String, Object> updates) {
        try {
            RoleResource roleResource = keycloak.realm(realmName).roles().get(roleName);
            RoleRepresentation roleRepresentation = roleResource.toRepresentation();
            updates.forEach((key, value) -> {
                try {
                    Field field = RoleRepresentation.class.getDeclaredField(key);
                    field.setAccessible(true);
                    field.set(roleRepresentation, value);
                } catch (Exception ignored) {
                }
            });
            roleResource.update(roleRepresentation);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update role: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves the role representation for the specified role name.
     *
     * @param roleName The name of the role to retrieve.
     * @return The {@link RoleRepresentation} of the requested role.
     * @throws RuntimeException If the role retrieval fails.
     */
    @Override
    public RoleRepresentation getRole(String roleName) {
        try {
            return keycloak.realm(realmName).roles().get(roleName).toRepresentation();
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve role: " + e.getMessage(), e);
        }
    }

    /**
     * Lists all roles in the current realm.
     *
     * @return A list of {@link RoleRepresentation} objects representing all roles in the realm.
     * @throws RuntimeException If an error occurs while listing roles.
     */
    @Override
    public List<RoleRepresentation> listRoles() {
        RealmResource realmResource = keycloak.realm(realmName);
        try {
            return realmResource.roles().list();
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while listing roles.", e);
        }
    }

    /**
     * Adds composite roles to the specified parent role.
     * The child roles will be added as composite roles to the parent role.
     *
     * @param parentRole The name of the parent role.
     * @param childRoles A list of names of the child roles to add to the parent role as composites.
     * @throws RuntimeException If the operation to add composite roles fails.
     */
    @Override
    public void addCompositeRole(String parentRole, List<String> childRoles) {
        try {
            List<RoleRepresentation> childRoleRepresentations = childRoles.stream()
                    .map(role -> keycloak.realm(realmName).roles().get(role).toRepresentation())
                    .collect(Collectors.toList());
            keycloak.realm(realmName).roles().get(parentRole).addComposites(childRoleRepresentations);
        } catch (Exception e) {
            throw new RuntimeException("Failed to add composite role: " + e.getMessage(), e);
        }
    }

    /**
     * Removes composite roles from the specified parent role.
     * The child roles will be removed from the parent role's composite roles.
     *
     * @param parentRole The name of the parent role.
     * @param childRoles A list of names of the child roles to remove from the parent role's composites.
     * @throws RuntimeException If the operation to remove composite roles fails.
     */
    @Override
    public void removeCompositeRole(String parentRole, List<String> childRoles) {
        try {
            List<RoleRepresentation> childRoleRepresentations = childRoles.stream()
                    .map(role -> keycloak.realm(realmName).roles().get(role).toRepresentation())
                    .collect(Collectors.toList());
            keycloak.realm(realmName).roles().get(parentRole).deleteComposites(childRoleRepresentations);
        } catch (Exception e) {
            throw new RuntimeException("Failed to remove composite role: " + e.getMessage(), e);
        }
    }

    /**
     * Grants the specified role to the given user.
     *
     * @param userId   The user to whom the role will be granted.
     * @param roleName The name of the role to grant to the user.
     * @throws RuntimeException If the role granting operation fails.
     */
    @Override
    public void grantRoleToUser(String userId, String roleName) {
        RealmResource realmResource = keycloak.realm(realmName);
        try {
            UserResource userResource = realmResource.users().get(userId);
            RoleRepresentation roleRepresentation = realmResource.roles().get(roleName).toRepresentation();
            userResource.roles().realmLevel().add(Collections.singletonList(roleRepresentation));
        } catch (NotFoundException e) {
            throw new RuntimeException("User or role not found: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while granting the role.", e);
        }
    }

    /**
     * Revokes the specified role from the given user.
     *
     * @param userId   The user from whom the role will be revoked.
     * @param roleName The name of the role to revoke.
     * @throws RuntimeException If the user or role is not found or if the operation fails.
     */
    @Override
    public void revokeRoleFromUser(String userId, String roleName) {
        try {
            RealmResource realmResource = keycloak.realm(realmName);
            UserResource userResource = realmResource.users().get(userId);
            RoleRepresentation roleRepresentation = realmResource.roles().get(roleName).toRepresentation();

            if (roleRepresentation == null) {
                throw new RuntimeException("Role not found: " + roleName);
            }
            userResource.roles().realmLevel().remove(Collections.singletonList(roleRepresentation));
        } catch (NotFoundException e) {
            throw new RuntimeException("User or Role not found: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred while revoking role: " + e.getMessage(), e);
        }
    }

    @Override
    public void revokeAllRolesFromUser(String userId) {
        try {
            RealmResource realmResource = keycloak.realm(realmName);
            UserResource userResource = realmResource.users().get(userId);
            List<RoleRepresentation> userRoles = userResource.roles().realmLevel().listAll();
            if (userRoles.isEmpty()) {
                throw new RuntimeException("User does not have any roles to revoke.");
            }

            userResource.roles().realmLevel().remove(userRoles);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while revoking roles from the user: " + e.getMessage(), e);
        }
    }

    /**
     * Revokes all roles from the current realm.
     *
     * @throws RuntimeException If the operation to revoke roles fails.
     */
    @Override
    public void revokeAllRoles() {
        RealmResource realmResource = keycloak.realm(realmName);
        realmResource.roles().list().forEach(role -> realmResource.roles().get(role.getName()).remove());
    }

    /**
     * Creates a new group with the specified group name.
     *
     * @param groupName The name of the group to create.
     * @throws RuntimeException If the group creation fails.
     */
    @Override
    public void createGroup(String groupName) {
        GroupsResource groupsResource = keycloak.realm(realmName).groups();
        try {
            GroupRepresentation groupRepresentation = new GroupRepresentation();
            groupRepresentation.setName(groupName);
            groupsResource.add(groupRepresentation);
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred while creating group.", e);
        }
    }

    /**
     * Updates the specified group with a new name.
     *
     * @param groupName    The current name of the group to update.
     * @param newGroupName The new name for the group.
     * @throws RuntimeException If the group is not found or the update operation fails.
     */
    @Override
    public void updateGroup(String groupName, String newGroupName, GroupRepresentation updatedGroupRepresentation) {
        RealmResource realmResource = keycloak.realm(realmName);
        try {
            List<GroupRepresentation> groups = realmResource.groups().groups();

            GroupRepresentation groupRepresentation = groups.stream()
                    .filter(group -> group.getName().equals(groupName))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Group not found"));

            groupRepresentation.setName(newGroupName);
            if (updatedGroupRepresentation.getAttributes() != null) {
                groupRepresentation.setAttributes(updatedGroupRepresentation.getAttributes());
            }
            if (updatedGroupRepresentation.getRealmRoles() != null) {
                groupRepresentation.setRealmRoles(updatedGroupRepresentation.getRealmRoles());
            }
            if (updatedGroupRepresentation.getClientRoles() != null) {
                groupRepresentation.setClientRoles(updatedGroupRepresentation.getClientRoles());
            }
            if (updatedGroupRepresentation.getSubGroups() != null) {
                groupRepresentation.setSubGroups(updatedGroupRepresentation.getSubGroups());
            }

            realmResource.groups().group(groupRepresentation.getId()).update(groupRepresentation);
        } catch (NotFoundException e) {
            throw new RuntimeException("Group with name " + groupName + " not found.");
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred while updating the group.", e);
        }
    }

    /**
     * Deletes the group with the specified name.
     *
     * @param groupName The name of the group to delete.
     * @throws RuntimeException If the group is not found or the deletion operation fails.
     */
    @Override
    public void deleteGroup(String groupName) {
        RealmResource realm = keycloak.realm(realmName);
        try {
            GroupRepresentation group = realm.groups().groups().stream()
                    .filter(g -> g.getName().equals(groupName))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Group with name " + groupName + " not found."));
            if (group != null) {
                realm.groups().group(group.getId()).remove();
            }
        } catch (NotFoundException e) {
            throw new RuntimeException("Group with name " + groupName + " not found.");
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred while deleting the group.", e);
        }
    }

    /**
     * Lists all groups in the current realm.
     *
     * @return A list of {@link GroupRepresentation} objects representing all groups in the realm.
     * @throws RuntimeException If the operation to list groups fails.
     */
    @Override
    public List<GroupRepresentation> listGroups() {
        RealmResource realmResource = keycloak.realm(realmName);
        try {
            return realmResource.groups().groups();
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while listing groups.", e);
        }
    }

    /**
     * Lists all groups for a specific user.
     *
     * @param userId The user whose groups are to be listed.
     * @return A list of {@link GroupRepresentation} objects representing the groups the user is part of.
     * @throws RuntimeException If the operation to list user groups fails.
     */
    @Override
    public List<GroupRepresentation> listGroupsForUser(String userId) {
        try {
            return keycloak.realm(realmName).users().get(userId).groups();
        } catch (Exception e) {
            throw new RuntimeException("Failed to list groups for user: " + e.getMessage(), e);
        }
    }

    /**
     * Lists all users who are members of a specific group.
     *
     * @param groupName The name of the group whose members are to be listed.
     * @return A list of {@link UserRepresentation} objects representing users in the group.
     * @throws RuntimeException If the operation to list users in the group fails.
     */
    @Override
    public List<UserRepresentation> listUsersInGroup(String groupName) {
        try {
            GroupRepresentation groupRepresentation = keycloak.realm(realmName).groups().groups().stream()
                    .filter(g -> g.getName().equals(groupName))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Group not found"));
            return keycloak.realm(realmName).groups().group(groupRepresentation.getId()).members();
        } catch (Exception e) {
            throw new RuntimeException("Failed to list users in group: " + e.getMessage(), e);
        }
    }

    /**
     * Assigns a group to a user in the Keycloak realm.
     *
     * @param groupName the name of the group to be assigned to the user.
     * @param userId    the user to whom the group will be assigned.
     * @throws RuntimeException if an error occurs while assigning the group, including when the user or group cannot be found.
     */
    @Override
    public void assignGroupToUser(String groupName, String userId) {
        RealmResource realmResource = keycloak.realm(realmName);
        try {
            List<GroupRepresentation> groups = realmResource.groups().groups();
            GroupRepresentation group = groups.stream()
                    .filter(g -> g.getName().equals(groupName))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Group with name " + groupName + " not found."));

            UserResource userResource = realmResource.users().get(userId);
            userResource.joinGroup(group.getId());

        } catch (NotFoundException e) {
            String errorMessage = "Could not assign group to user. ";
            if (e.getMessage().contains("User")) {
                errorMessage += "User with ID " + userId + " not found.";
            } else if (e.getMessage().contains("Group")) {
                errorMessage += "Group with name " + groupName + " not found.";
            }
            throw new RuntimeException(errorMessage, e);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred while assigning user to the group.", e);
        }
    }

    /**
     * Revokes a group from a user in the Keycloak realm.
     *
     * @param userId    the user from whom the group will be revoked.
     * @param groupName the name of the group to be revoked from the user.
     * @throws RuntimeException if an error occurs while revoking the group, including when the user or group cannot be found.
     */
    @Override
    public void revokeGroupFromUser(String groupName, String userId) {
        RealmResource realmResource = keycloak.realm(realmName);
        try {
            List<GroupRepresentation> groups = realmResource.groups().groups();
            GroupRepresentation group = groups.stream()
                    .filter(g -> g.getName().equals(groupName))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Group with name " + groupName + " not found."));

            UserResource userResource = realmResource.users().get(userId);
            userResource.leaveGroup(group.getId());

        } catch (NotFoundException e) {
            String errorMessage = "Could not revoke group from user. ";
            if (e.getMessage().contains("User")) {
                errorMessage += "User with ID " + userId + " not found.";
            } else if (e.getMessage().contains("Group")) {
                errorMessage += "Group with name " + groupName + " not found.";
            }
            throw new RuntimeException(errorMessage, e);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred while revoking user from the group.", e);
        }
    }

    /**
     * Lists all users along with their assigned groups.
     *
     * @return a list of all users with their associated groups.
     * @throws RuntimeException if an error occurs while retrieving the list of users and their groups.
     */
    @Override
    public List<UserRepresentation> listUserGroups() {
        try {
            RealmResource realmResource = keycloak.realm(realmName);
            UsersResource usersResource = realmResource.users();

            List<UserRepresentation> allUsers = usersResource.list();

            for (UserRepresentation user : allUsers) {
                List<GroupRepresentation> groups = usersResource.get(user.getId()).groups();
                List<String> groupNames = groups.stream()
                        .map(GroupRepresentation::getName)
                        .toList();
                user.setAttributes(Collections.singletonMap("groups", groupNames));
            }

            return allUsers;
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving users with groups", e);
        }
    }

    /**
     * Lists all groups and their subgroups in the Keycloak realm.
     *
     * @return a list of all groups and their subgroups.
     * @throws RuntimeException if an error occurs while retrieving the group hierarchy.
     */
    @Override
    public List<GroupRepresentation> listGroupGroups() {
        try {
            RealmResource realmResource = keycloak.realm(realmName);
            GroupsResource groupsResource = realmResource.groups();
            List<GroupRepresentation> topGroups = groupsResource.groups();
            List<GroupRepresentation> allGroups = new ArrayList<>();
            for (GroupRepresentation group : topGroups) {
                allGroups.add(group);

                List<GroupRepresentation> subGroups = groupsResource.group(group.getId())
                        .toRepresentation()
                        .getSubGroups();
                if (subGroups != null) {
                    allGroups.addAll(subGroups);
                }
            }
            return allGroups;
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving group hierarchy", e);
        }
    }

    /**
     * Lists all groups that users are part of, including their subgroups.
     *
     * @return a list of all user groups and their subgroups.
     * @throws RuntimeException if an error occurs while retrieving the user group hierarchy.
     */
    @Override
    public List<GroupRepresentation> listUserGroupGroups() {
        try {
            RealmResource realmResource = keycloak.realm(realmName);
            UsersResource usersResource = realmResource.users();
            List<UserRepresentation> allUsers = usersResource.list();

            List<GroupRepresentation> allUserGroups = new ArrayList<>();
            for (UserRepresentation user : allUsers) {
                List<GroupRepresentation> userGroups = usersResource.get(user.getId()).groups();

                for (GroupRepresentation group : userGroups) {
                    allUserGroups.add(group); // Add the user's group

                    List<GroupRepresentation> subGroups = realmResource.groups()
                            .group(group.getId())
                            .toRepresentation()
                            .getSubGroups();
                    if (subGroups != null) {
                        allUserGroups.addAll(subGroups);
                    }
                }
            }

            return allUserGroups;
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving user group hierarchy", e);
        }
    }

    /**
     * Creates a new user in the Keycloak realm.
     *
     * @param user the user representation to be created.
     * @throws RuntimeException if an error occurs while creating the user.
     */
    @Override
    public void createUser(UserRepresentation user) {
        RealmResource realmResource = keycloak.realm(realmName);
        try {
            realmResource.users().create(user);
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred while creating user.", e);
        }
    }

    /**
     * Updates an existing user in the Keycloak realm.
     *
     * @param userId the user representation with updated data.
     * @throws RuntimeException if an error occurs while updating the user, including if the user cannot be found.
     */
    @Override
    public void updateUser(UserRepresentation user) {
        RealmResource realmResource = keycloak.realm(realmName);
        try {
            UserResource userResource = realmResource.users().get(user.getId());
            userResource.update(user);
        } catch (NotFoundException e) {
            throw new RuntimeException("User with ID " + user.getId() + " not found.");
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred while updating the user.", e);
        }
    }

    /**
     * Deletes a user from the Keycloak realm.
     *
     * @param userId the user representation to be deleted.
     * @throws RuntimeException if an error occurs while deleting the user, including if the user cannot be found.
     */
    @Override
    public void deleteUser(String userId) {
        RealmResource realmResource = keycloak.realm(realmName);
        try {
            UserResource userResource = realmResource.users().get(userId);
            userResource.remove();
        } catch (NotFoundException e) {
            throw new RuntimeException("User with ID " + userId + " not found.");
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred while deleting the user.", e);
        }
    }

    /**
     * Enables a user in the Keycloak realm.
     *
     * @param userId the user representation to be enabled.
     * @throws RuntimeException if an error occurs while enabling the user, including if the user cannot be found.
     */
    @Override
    public void enableUser(String userId) {
        RealmResource realmResource = keycloak.realm(realmName);
        try {
            UserResource userResource = realmResource.users().get(userId);
            UserRepresentation userRepresentation = userResource.toRepresentation();
            userRepresentation.setEnabled(true);
        } catch (NotFoundException e) {
            throw new RuntimeException("User with ID " + userId + " not found.");
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred while enabling the user.", e);
        }
    }

    /**
     * Disables a user in the Keycloak realm.
     *
     * @param userId the user representation to be disabled.
     * @throws RuntimeException if an error occurs while disabling the user, including if the user cannot be found.
     */
    @Override
    public void disableUser(String userId) {
        RealmResource realmResource = keycloak.realm(realmName);
        try {
            UserResource userResource = realmResource.users().get(userId);
            UserRepresentation userRepresentation = userResource.toRepresentation();
            userRepresentation.setEnabled(false);
        } catch (NotFoundException e) {
            throw new RuntimeException("User with ID " + userId + " not found.");
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred while disabling the user.", e);
        }
    }

    /**
     * Updates the password for a user in the Keycloak realm.
     *
     * @param userId      the user representation whose password will be updated.
     * @param oldPassword the current password of the user.
     * @param newPassword the new password for the user.
     * @throws RuntimeException if an error occurs while updating the password.
     */
    @Override
    public void updatePassword(String userId, String oldPassword, String newPassword) {
        RealmResource realmResource = keycloak.realm(realmName);
        try {
            CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
            credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
            credentialRepresentation.setValue(newPassword);
            UserResource userResource = realmResource.users().get(userId);
            userResource.resetPassword(credentialRepresentation);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Logs out a user from the Keycloak realm.
     *
     * @param userId the user representation to be logged out.
     * @throws RuntimeException if an error occurs while logging out the user.
     */
    @Override
    public void logoutUser(String userId) {
        try {
            keycloak.realm(realmName).users().get(userId).logout();
        } catch (Exception e) {
            throw new RuntimeException("Failed to logout user: " + e.getMessage(), e);
        }
    }

    /**
     * Lists all active sessions for a given user in the Keycloak realm.
     *
     * @param userId the user representation for whom sessions will be listed.
     * @return a list of user sessions.
     * @throws RuntimeException if an error occurs while retrieving the user sessions.
     */
    @Override
    public List<UserSessionRepresentation> listUserSessions(String userId) {
        try {
            return keycloak.realm(realmName).users().get(userId).getUserSessions();
        } catch (Exception e) {
            throw new RuntimeException("Failed to list user sessions: " + e.getMessage(), e);
        }
    }

    /**
     * Lists all users in the Keycloak realm.
     *
     * @param userId the user to which user information will be provided.
     * @return information for a given user
     */
    @Override
    public UserRepresentation getUserInformation(String userId) {
        RealmResource realm = keycloak.realm(realmName);
        return realm.users().get(userId).toRepresentation();
    }

    /**
     * Lists all users in the Keycloak realm.
     *
     * @return a list of all users in the realm.
     */
    @Override
    public List<UserRepresentation> listUsers() {
        RealmResource realm = keycloak.realm(realmName);
        return realm.users().list();
    }

    /**
     * Creates a permission in the Keycloak realm (not yet implemented).
     *
     * @param permissionName the name of the permission to be created.
     * @param attributes     additional attributes related to the permission.
     * @throws UnsupportedOperationException if called, since permission management is not yet implemented.
     */
    @Override
    public void createPermission(String permissionName, Map<String, Object> attributes) {
        throw new UnsupportedOperationException("Permission management requires Keycloak authorization setup and will be implemented later.");
    }

    /**
     * Updates a permission in the Keycloak realm (not yet implemented).
     *
     * @param permissionName the name of the permission to be updated.
     * @param updates        the updates to be applied to the permission.
     * @throws UnsupportedOperationException if called, since permission management is not yet implemented.
     */
    @Override
    public void updatePermission(String permissionName, Map<String, Object> updates) {
        throw new UnsupportedOperationException("Permission management requires Keycloak authorization setup and will be implemented later.");
    }

    /**
     * Deletes a permission in the Keycloak realm (not yet implemented).
     *
     * @param permissionName the name of the permission to be deleted.
     * @throws UnsupportedOperationException if called, since permission management is not yet implemented.
     */
    @Override
    public void deletePermission(String permissionName) {
        throw new UnsupportedOperationException("Permission management requires Keycloak authorization setup and will be implemented later.");
    }

    /**
     * Adds a custom attribute to a user in the Keycloak realm.
     *
     * @param userId the user to which the custom attribute will be added.
     * @param key    the key for the custom attribute.
     * @param value  the value of the custom attribute.
     * @throws RuntimeException if an error occurs while adding the custom attribute.
     */
    @Override
    public void addCustomAttribute(String userId, String key, String value) {

        try {
            RealmResource realmResource = keycloak.realm(realmName);
            UserResource userResource = realmResource.users().get(userId);
            UserRepresentation userRepresentation = userResource.toRepresentation();

            Map<String, List<String>> attributes = userRepresentation.getAttributes();
            if (attributes == null) {
                attributes = new HashMap<>();
            }
            attributes.put(key, Collections.singletonList(value));
            userRepresentation.setAttributes(attributes);
            keycloak.realm(realmName).users().get(userId).update(userRepresentation);
        } catch (Exception e) {
            throw new RuntimeException("Failed to add custom attribute: " + e.getMessage(), e);
        }
    }

    /**
     * Removes a custom attribute from a user in the Keycloak realm.
     *
     * @param userId the user from whom the custom attribute will be removed.
     * @param key    the key of the custom attribute to be removed.
     * @throws RuntimeException if an error occurs while removing the custom attribute.
     */
    @Override
    public void removeCustomAttribute(String userId, String key) {
        try {
            RealmResource realmResource = keycloak.realm(realmName);
            UserResource userResource = realmResource.users().get(userId);
            UserRepresentation userRepresentation = userResource.toRepresentation();

            Map<String, List<String>> attributes = userRepresentation.getAttributes();
            if (attributes != null && attributes.containsKey(key)) {
                attributes.remove(key);
            }
            userRepresentation.setAttributes(attributes);
            keycloak.realm(realmName).users().get(userId).update(userRepresentation);
        } catch (Exception e) {
            throw new RuntimeException("Failed to remove custom attribute: " + e.getMessage(), e);
        }
    }

    /**
     * Lists all custom attributes for a user in the Keycloak realm.
     *
     * @param user the user whose custom attributes will be listed.
     * @return a map of custom attribute keys and their corresponding values.
     * @throws RuntimeException if an error occurs while listing the custom attributes.
     */
    @Override
    public Map<String, String> listCustomAttributes(UserRepresentation user) {
        try {
            return user.getAttributes().entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().get(0)));
        } catch (Exception e) {
            throw new RuntimeException("Failed to list custom attributes: " + e.getMessage(), e);
        }
    }

    /**
     * Revokes (deletes) all users in the Keycloak realm.
     *
     * @throws RuntimeException if an error occurs while revoking (deleting) all users.
     */
    @Override
    public void revokeAllUsers() {
        RealmResource realmResource = keycloak.realm(realmName);
        try {
            realmResource.users().list().forEach(user -> {
                realmResource.users().get(user.getId()).remove();
            });
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred while revoking all users.", e);
        }
    }

    /**
     * Revokes (deletes) all groups in the Keycloak realm.
     *
     * @throws RuntimeException if an error occurs while revoking (deleting) all groups.
     */
    @Override
    public void revokeAllGroups() {
        RealmResource realmResource = keycloak.realm(realmName);
        try {
            realmResource.groups().groups().forEach(group -> {
                realmResource.groups().group(group.getId()).remove();
            });

        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred while revoking all groups.", e);
        }
    }

    /**
     * Revokes (removes) all groups assigned to users in the Keycloak realm.
     *
     * @throws RuntimeException if an error occurs while revoking (removing) all user groups.
     */
    @Override
    public void revokeAllUserGroups() {
        RealmResource realmResource = keycloak.realm(realmName);
        try {
            realmResource.users().list().forEach(user -> {
                realmResource.users().get(user.getId()).groups().forEach(group -> {
                    realmResource.users().get(user.getId()).leaveGroup(group.getId());
                });
            });
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred while revoking all user groups.", e);
        }
    }

    /**
     * Revokes (removes) all subgroup associations from groups in the Keycloak realm.
     *
     * @throws RuntimeException if an error occurs while revoking (removing) all subgroup associations.
     */
    @Override
    public void revokeAllGroupGroups() {
        RealmResource realmResource = keycloak.realm(realmName);
        try {
            realmResource.groups().groups().forEach(group -> {
                group.getSubGroups().forEach(subGroup -> {
                    realmResource.groups().group(subGroup.getId()).remove();
                });
            });
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred while revoking all group groups.", e);
        }
    }

    /**
     * Revokes (removes) all user subgroup associations in the Keycloak realm.
     *
     * @throws RuntimeException if an error occurs while revoking (removing) all user subgroup associations.
     */
    @Override
    public void revokeAllUserGroupGroups() {
        RealmResource realmResource = keycloak.realm(realmName);
        try {
            realmResource.users().list().forEach(user -> {
                realmResource.users().get(user.getId()).groups().forEach(group -> {
                    group.getSubGroups().forEach(subGroup -> {
                        realmResource.groups().group(subGroup.getId()).remove();
                    });
                });
            });
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred while revoking all user group groups.", e);
        }
    }
}
