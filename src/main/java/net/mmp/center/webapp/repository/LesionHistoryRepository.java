package net.mmp.center.webapp.repository;

import net.mmp.center.webapp.domain.LesionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface LesionHistoryRepository extends JpaRepository<LesionHistory, Integer>, JpaSpecificationExecutor<LesionHistory>{

}
