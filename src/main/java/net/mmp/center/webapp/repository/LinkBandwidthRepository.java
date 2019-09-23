package net.mmp.center.webapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.mmp.center.webapp.domain.LinkBandwidth;

@Repository
public interface LinkBandwidthRepository extends JpaRepository<LinkBandwidth, Integer>{
	List<LinkBandwidth> findBySrcIpAddressAndDestIpAddress(String srcIpAddress, String destIpAddress);
	List<LinkBandwidth> findTop1000BySrcIpAddressAndDestIpAddressOrderByMeasuredTimeDesc(String srcIpAddress, String destIpAddress);
}
