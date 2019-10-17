package net.mmp.center.webapp.repository;

import net.mmp.center.webapp.domain.BootstrapInfoLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BootstrapInfoLogRepository  extends JpaRepository<BootstrapInfoLog, Integer> {

}
