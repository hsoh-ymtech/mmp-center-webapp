package net.mmp.center.webapp;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.ip.udp.UnicastReceivingChannelAdapter;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

import net.mmp.center.webapp.domain.ProtocolInfo;
import net.mmp.center.webapp.domain.ReflectorInfo;
import net.mmp.center.webapp.repository.ProtocolInfoRepository;
import net.mmp.center.webapp.repository.ReflectorInfoRepository;
import net.mmp.center.webapp.thread.FullmeshThread;
import net.mmp.center.webapp.thread.FullmeshThreadPool;
import net.mmp.center.webapp.util.ProcessRun;
import net.mmp.center.webapp.util.Util;

@ServletComponentScan
@SpringBootApplication
@EnableZuulProxy
@EnableScheduling
@RefreshScope
@EnableHystrix
public class WebappApplication {

private static final Logger logger = LogManager.getLogger(WebappApplication.class);
	
	private static String directory = System.getProperty("user.dir") + File.separator + "src" + File.separator
			+ "twamp-visualization";

	@Value("${config.twamp.visualization.host}")
	private String eshost;

	@Value("${config.twamp.visualization.port}")
	private String esport;

	@Value("${config.cnc.server.ip}")
	private String cnchost;
	
	@Value("${config.cnc.server.port}")
	private String cncport;
	
	@Value("${config.firecracker.username}")
	private String vmUsername;
	
	@Value("${config.firecracker.password}")
	private String vmPassword;
	
	@Value("${config.firecracker.hostip}")
	private String vmHostip;
	
	@Value("${config.twampsender.dir}")
	private String twampSenderDir;
	
	@Autowired
	private ProtocolInfoRepository prtoocolInfoRepository;
	
	@Autowired
	private ReflectorInfoRepository reflectorInfoRepository;
	
	@Autowired
	SimpMessagingTemplate template;
	
	private int repeatCount = 0;
	
	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = SpringApplication.run(WebappApplication.class, args);
		
		String dir = Util.convertDirectory(directory);
		boolean isWindows = Util.isWindows();

		WebappApplication m = ctx.getBean(WebappApplication.class);
//		m.KibanaInit(dir, isWindows);
		
		m.setProtocolInfo();
	}

	
	/**
	 * 서버 실행시 해당 경로에 저장되어 있는 Visualization 그래프 정보들을 elasticsearch에 저장
	 * @param dir
	 * @param isWindows
	 */
	public void KibanaInit(String dir, boolean isWindows) {
		
		List<String> command = Arrays.asList("-H", eshost, "-P", esport);
		
		Thread t = new Thread(new ProcessRun(Util.convertFileNameFromBinByOS(command, Util.convertSHFileNameByOS()), dir));
		t.start();
	}
	
	public void setProtocolInfo() {
		logger.info("-------------------------------------------");
		logger.info("SET Protocol Infomation ...");
		ProtocolInfo protocolData = new ProtocolInfo();
		protocolData.setProtocolId(1);
		protocolData.setType("Full TWAMP");
		prtoocolInfoRepository.save(protocolData);
		protocolData.setProtocolId(2);
		protocolData.setType("Light TWAMP");
		prtoocolInfoRepository.save(protocolData);
		logger.info("SET Protocol Infomation Save Complete.");
		logger.info("-------------------------------------------");
	}
	
	/**
	 * Full Mesh Scheduling
	 * fixedRate = 10000
	 */
//	@Scheduled(fixedRate = 10000)
	public void fullmeshScheduling() {
		
		List<ReflectorInfo> reflData = reflectorInfoRepository.findAll();
		int dstIdx = repeatCount % (reflData.size() - 1);
		FullmeshThreadPool threadPool = new FullmeshThreadPool(1);
		FullmeshThread thread = null;
		thread = new FullmeshThread("http://" + cnchost + ":" + cncport + "/execute", reflData, dstIdx, eshost, esport, vmUsername, vmPassword, vmHostip, twampSenderDir, repeatCount, template);
		threadPool.execute(thread);

		while (true) {
			if (threadPool.isThreadFinish()) {
//				int totalCount = threadPool.getTotalExecuteCount();
				logger.info("FullMesh Finish!!!");
				if (threadPool != null) {
					threadPool.shutdown();
				}
				break;
			}
			try
			{
				Thread.sleep(1000);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		repeatCount++;
	}
	
	
	@Bean
	public MessageSource messageSource() {
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setBasename("messages/message");
		messageSource.setDefaultEncoding("UTF-8");
		return messageSource;
	}

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

	@Bean
	public IntegrationFlow processUniCastUdpMessage() {
		return IntegrationFlows
				.from(new UnicastReceivingChannelAdapter(80))
				.handle("UDPServiceImpl", "handleMessage")
				.get();
	}

}
