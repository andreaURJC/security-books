package es.codeurjc.books.repositories;

import es.codeurjc.books.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

}
