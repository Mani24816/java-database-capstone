package com.project.back_end.repo;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

Admin findByUsername(String username);
}
