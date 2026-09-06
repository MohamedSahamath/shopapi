package lk.ijse.shopapi.repository;

import lk.ijse.shopapi.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}