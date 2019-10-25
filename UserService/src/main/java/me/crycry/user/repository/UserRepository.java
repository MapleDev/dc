package me.crycry.user.repository;

import me.crycry.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Long>{


    @Query("select u from User u where u.username=?1 and u.password=?2")
    User findFirstByUsernameAndPassword(String username, String password);

    User findFirstByUsername(String username);


    @Query("select u.username,u.email from User u where u.status=0")
    List<User> listUnPermitUser();
}
