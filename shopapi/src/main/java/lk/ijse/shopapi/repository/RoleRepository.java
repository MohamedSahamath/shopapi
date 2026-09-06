package lk.ijse.shopapi.repository;

import lk.ijse.shopapi.entity.Role;
import lk.ijse.shopapi.util.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}