package net.mmp.center.webapp.repository;

import net.mmp.center.webapp.domain.CurrentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrentStatusRepository extends JpaRepository<CurrentStatus, Integer>{

}
