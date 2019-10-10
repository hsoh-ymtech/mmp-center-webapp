package net.mmp.center.webapp.service;

import net.mmp.center.webapp.model.RawData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RawDataService {

    public Page<RawData> findByMeshId(String meshId, PageRequest pageRequest);

    public Page<RawData> findBySrcHostAndDstHost(String srcHost, String dstHost, PageRequest pageable);
}
