package net.mmp.center.webapp.repository;

import java.util.List;

import net.mmp.center.webapp.domain.ReflectorInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ReflectorInfoRepository extends JpaRepository<ReflectorInfo, Integer>, JpaSpecificationExecutor<ReflectorInfo>{
	
	/**
	 * reflector IP 와 port 가 해당 입력값과 같은 row 검색
	 * @param ReflectorIp
	 * 									reflector IP Address
	 * @param Port
	 * 									port
	 * @return
	 * 									row List
	 */
	List<ReflectorInfo> findByReflectorIpAndPort(String ReflectorIp, int Port);

	List<ReflectorInfo> findByMeshId(String MeshId);
	
	List<ReflectorInfo> findByReflectorIp(String ReflectorIp);
	
	// ReflectorInfo findByReflectorIpAndPort(String reflectorIp, int port) throws net.mmp.center.webapp.exception.NullPointerException;
	
	List<ReflectorInfo> findByReflectorIpAndPortAndProtocolInfo_Type(String reflectorIp, int port, String type) throws net.mmp.center.webapp.exception.NullPointerException;
}
