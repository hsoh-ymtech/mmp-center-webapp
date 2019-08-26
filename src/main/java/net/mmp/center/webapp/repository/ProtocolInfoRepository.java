package net.mmp.center.webapp.repository;

import net.mmp.center.webapp.domain.ProtocolInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProtocolInfoRepository extends JpaRepository<ProtocolInfo, Integer>{
	
	/**
	 * Protocol_info 테이블에서 해당 type이 같은 row 검색
	 * @param Type
	 * 							protocol type
	 * @return
	 * 							row
	 */
	ProtocolInfo findByType(String Type);
}
