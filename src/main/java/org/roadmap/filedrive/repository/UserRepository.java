package org.roadmap.filedrive.repository;

import org.roadmap.filedrive.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User getByEmail(String email);

    User getByEmailAndPassword(String email, String password);
}
