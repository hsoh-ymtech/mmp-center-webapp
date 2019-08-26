package net.mmp.center.webapp.repository;

import net.mmp.center.webapp.domain.ReflectorProtocolRelationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ReflectorProtocolRelationshipRepository extends JpaRepository<ReflectorProtocolRelationship, Integer>, JpaSpecificationExecutor<ReflectorProtocolRelationship>{
	
//	/**
//	 * 해당 Reflector ID 와 같은 row 검색
//	 * @param reflectorId
//	 * 								Reflector ID
//	 * @return
//	 * 								Data List
//	 */
//	List<ReflectorProtocolRelationship> findByPk_ReflectorInfo_ReflectorId(@Param(value = "reflectorId") int reflectorId);
//	
//	/**
//	 * Reflector IP, Reflector Port, Protocol로 검색
//	 * @param reflectorIp
//	 * 								Reflector IP
//	 * @param port
//	 * 								Port
//	 * @param type
//	 * 								Protocol Type
//	 * @return
//	 * 								Data
//	 * @throws net.mmp.center.webapp.exception.NullPointerException
//	 */
//	ReflectorProtocolRelationship findByPk_ReflectorInfo_ReflectorIpAndPk_ReflectorInfo_PortAndPk_ProtocolInfo_Type(String reflectorIp, int port, String type) throws net.mmp.center.webapp.exception.NullPointerException;
//	
//	
//	/**
//	 * Reflector IP, Protocol로 검색
//	 * @param reflectorIp
//	 * 								Reflector IP
//	 * @param type
//	 * 								Type
//	 * @return
//	 * 								Data List
//	 * @throws net.mmp.center.webapp.exception.NullPointerException
//	 */
//	List<ReflectorProtocolRelationship> findByPk_ReflectorInfo_ReflectorIpAndPk_ProtocolInfo_Type(String reflectorIp, String type) throws net.mmp.center.webapp.exception.NullPointerException;
//	
//	/**
//	 * ProtocolID 와 ReflectorID가 같은 row 삭제
//	 * @param protocolId
//	 * 								Protocol ID
//	 * @param reflectorId
//	 * 								Reflector ID
//	 */
//	@Transactional
//	void deleteByPk_ProtocolInfo_ProtocolIdAndPk_ReflectorInfo_ReflectorId(int protocolId, int reflectorId);
//	
//	/**
//	 * ReflectorID가 같은 row 삭제
//	 * @param protocolId
//	 * 								Protocol ID
//	 */
//	@Transactional
//	void deleteByPk_ReflectorInfo_ReflectorId(int protocolId);
}
