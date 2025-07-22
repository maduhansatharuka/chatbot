/**
 * Copyrights 2024 Axiata Digital Labs Pvt Ltd.
 * All Rights Reserved.
 * <p>
 * These material are unpublished, proprietary, confidential source
 * code of Axiata Digital Labs Pvt Ltd (ADL) and constitute a TRADE
 * SECRET of ADL.
 * <p>
 * ADL retains all title to and intellectual property rights in these
 * materials.
 */
package com.adl.et.telco.selfcare.usermanagement.domain.boundry;

import com.adl.et.telco.selfcare.usermanagement.domain.entities.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * This is the repository interface for Permission entity.
 *
 * @author Hasini Hatharasinghe
 * @version 1.0
 * @since 2024/11/06
 */
@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    /**
     * Retrieves a list of unique menu IDs associated with the specified user ID
     *
     * @param userId the ID of the user for whom to retrieve menu IDs
     * @return a list of distinct menu IDs associated with the user
     */
    @Query("SELECT DISTINCT p.menuId " +
            "FROM Permission p " +
            "JOIN RoleToPermission rp ON rp.id.permissionId = p.referenceId " +
            "JOIN UserToRole ur ON ur.id.roleId = rp.id.roleId " +
            "WHERE ur.user.userId = :userId")
    List<Long> findMenuIdsByUserId(@Param("userId") Long userId);

    /**
     * Retrieves component IDs and their associated action IDs for the specified user ID.
     *
     * @param userId the ID of the user for whom to retrieve component and action IDs
     * @return a list of objects where each entry contains a component ID and a concatenated
     * string of associated action IDs for the specified user
     */
    @Query(value = "SELECT c.referenceId AS id, " +
            "GROUP_CONCAT(a.referenceId) AS actions " +
            "FROM sc_usermanagement.permission p " +
            "JOIN sc_usermanagement.role_to_permission rp ON rp.permissionId = p.referenceId " +
            "JOIN sc_usermanagement.user_to_role ur ON ur.roleId = rp.roleId " +
            "JOIN sc_usermanagement.component c ON p.componentId = c.referenceId " +
            "JOIN sc_usermanagement.action a ON a.componentId = c.referenceId " +
            "WHERE ur.userId = :userId " +
            "GROUP BY c.id", nativeQuery = true)
    List<Object[]> findComponentIdsAndActionsByUserId(@Param("userId") Long userId);

}