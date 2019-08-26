package net.mmp.center.webapp.repository;

import net.mmp.center.webapp.domain.KibanaInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KibanaInfoRepository extends JpaRepository<KibanaInfo, Integer>{

}
