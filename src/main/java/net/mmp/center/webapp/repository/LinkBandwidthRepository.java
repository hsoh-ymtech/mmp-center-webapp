package net.mmp.center.webapp.repository;

import net.mmp.center.webapp.domain.KibanaInfo;
import net.mmp.center.webapp.domain.LinkBandwidth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LinkBandwidthRepository extends JpaRepository<LinkBandwidth, Integer>{

}
