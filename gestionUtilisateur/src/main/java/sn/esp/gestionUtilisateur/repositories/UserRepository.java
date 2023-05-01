package sn.esp.gestionUtilisateur.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import sn.esp.gestionUtilisateur.entities.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

	User findUserByUsername(String username);

    User findUserByEmail(String email);

//    @Query("SELECT * FROM gestionutilisateur.user LIMIT 100;")
//    List<User> getUsers();

}
