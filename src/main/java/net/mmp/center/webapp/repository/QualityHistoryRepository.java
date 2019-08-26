package net.mmp.center.webapp.repository;

import net.mmp.center.webapp.domain.QualityHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface QualityHistoryRepository extends JpaRepository<QualityHistory, Integer>, JpaSpecificationExecutor<QualityHistory>{

}
