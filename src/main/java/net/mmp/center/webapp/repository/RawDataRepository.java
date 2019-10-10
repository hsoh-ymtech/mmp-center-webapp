package net.mmp.center.webapp.repository;

import net.mmp.center.webapp.model.RawData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface RawDataRepository extends ElasticsearchRepository<RawData, String> {

    @Query("{\"bool\": {\"must\":[{\"match\": {\"mesh_id\": \"?0\"}}]}}")
    Page<RawData> findByMeshId(String meshId, Pageable pageable);

    @Query("{\"bool\": {\"must\":[{\"match\": {\"src_host\": \"?0\"}}, {\"match\": {\"dst_host\": \"?1\"}}]}}")
    Page<RawData> findBySrcHostAndDstHost(String srcHost, String dstHost, Pageable pageable);
}