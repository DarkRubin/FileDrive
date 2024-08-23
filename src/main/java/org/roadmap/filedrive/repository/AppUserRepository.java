package org.roadmap.filedrive.repository;

import org.roadmap.filedrive.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    AppUser findByEmail(String email);

    AppUser findByEmailAndPassword(String email, String password);
}
