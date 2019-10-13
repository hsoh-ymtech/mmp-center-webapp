package net.mmp.center.webapp.service.impl;

import com.google.common.collect.Lists;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.mmp.center.webapp.model.RawData;
import net.mmp.center.webapp.repository.RawDataRepository;
import net.mmp.center.webapp.service.RawDataService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service(RawDataServiceImpl.BEAN_QUALIFIER)
public class RawDataServiceImpl implements RawDataService {
   public static final String BEAN_QUALIFIER = "net.mmp.center.webapp.service.impl.RawDataServiceImpl";
   private static final Logger logger = LogManager.getLogger(RawDataServiceImpl.class);

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

   private WebClient webClient = WebClient.builder().baseUrl("http://localhost:8080").build();
   @HystrixCommand(commandKey = "hello", fallbackMethod = "getAdsFallback",
           commandProperties = {@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3000"),
                   @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "50"),
                   @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "2"),
                   @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "6000")})
   public String getAds() {
      return webClient.get() //get방식으로 가져올꺼야
              .uri("/ads")//baseUrl이후의 uri는 /ads로 할꺼야
              .retrieve() //클라이언트 메세지를 보내고
              .bodyToMono(String.class) //body 타입은 String일꺼야
              .block(); //가져왔다면 리턴해줘
   }

   private String getAdsFallback(Throwable t) {
      logger.info(t.getMessage()); return "fallback";
   }
}
