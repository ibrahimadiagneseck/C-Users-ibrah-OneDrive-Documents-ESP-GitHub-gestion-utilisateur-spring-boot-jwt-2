package sn.esp.gestionUtilisateur.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import sn.esp.gestionUtilisateur.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {

	User findUserByUsername(String username);

    User findUserByEmail(String email);
}
