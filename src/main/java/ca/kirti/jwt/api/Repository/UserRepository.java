package ca.kirti.jwt.api.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ca.kirti.jwt.api.Entity.User;
@Repository
public interface UserRepository extends JpaRepository<User, Integer>{

	User findByUserName(String username);

}
