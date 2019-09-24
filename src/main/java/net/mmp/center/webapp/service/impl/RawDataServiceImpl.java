package net.mmp.center.webapp.service.impl;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.mmp.center.webapp.model.RawData;
import net.mmp.center.webapp.repository.RawDataRepository;
import net.mmp.center.webapp.service.RawDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service(RawDataServiceImpl.BEAN_QUALIFIER)
public class RawDataServiceImpl implements RawDataService {
   public static final String BEAN_QUALIFIER = "net.mmp.center.webapp.service.impl.RawDataServiceImpl";

   @Autowired
   private RawDataRepository repository;

   public Page<RawData> findByMeshId(String meshId, PageRequest pageRequest) {
      long count = repository.count();

      for(RawData data : repository.findAll(PageRequest.of(1, 1000))) {
               System.out.println(data);
      }
      //return Lists.newArrayList(repository.findByMeshId(meshId));
      return repository.findByMeshId(meshId,pageRequest);
   }

   public Page<RawData> findBySrcHostAndDstHost(String srcHost, String dstHost, PageRequest pageRequest) {
       //return Lists.newArrayList(repository.findBySrcHostAndDstHost(srcHost,dstHost));
      long count = repository.count();

      for(RawData data : repository.findAll(PageRequest.of(1, 1000))) {
         System.out.println(data);
      }
      return repository.findBySrcHostAndDstHost(srcHost,dstHost,pageRequest);
   }
}
