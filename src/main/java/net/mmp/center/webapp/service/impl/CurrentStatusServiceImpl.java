package net.mmp.center.webapp.service.impl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import net.mmp.center.webapp.domain.CurrentStatus;
import net.mmp.center.webapp.domain.ProtocolInfo;
import net.mmp.center.webapp.domain.ReflectorInfo;
import net.mmp.center.webapp.domain.TargetServerInfo;
import net.mmp.center.webapp.dto.CurrentStatusDTO;
import net.mmp.center.webapp.dto.CurrentStatusDTO.CurrentStatusResultDTO;
import net.mmp.center.webapp.dto.QualityHistoryDTO;
import net.mmp.center.webapp.exception.NotFoundException;
import net.mmp.center.webapp.repository.CurrentStatusRepository;
import net.mmp.center.webapp.repository.ProtocolInfoRepository;
import net.mmp.center.webapp.repository.ReflectorInfoRepository;
import net.mmp.center.webapp.service.CurrentStatusService;
import net.mmp.center.webapp.service.QualityHistoryService;
import net.mmp.center.webapp.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service(CurrentStatusServiceImpl.BEAN_QUALIFIER)
public class CurrentStatusServiceImpl implements CurrentStatusService {
	public static final String BEAN_QUALIFIER = "net.mmp.center.webapp.service.impl.CurrentStatusServiceImpl";

	private static final Logger logger = LogManager.getLogger(CurrentStatusServiceImpl.class);

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	SimpMessagingTemplate template;
	
	@Autowired
	private CurrentStatusRepository currentStatusRepository;

	@Autowired
	private ReflectorInfoRepository reflectorInfoRepository;

	@Autowired
	private ProtocolInfoRepository protocolInfoRepository;
	
	@Autowired
	@Qualifier(QualityHistoryServiceImpl.BEAN_QUALIFIER)
	private QualityHistoryService qualityHistoryService;

	public final static int RESULT_OK = 1;
	public final static int RESULT_FAIL = 0;

	@Value("${config.twamp.visualization.host}")
	private String eshost;

	@Value("${config.twamp.visualization.port}")
	private String esport;
	
	@Value("${config.cnc.server.ip}")
	private String cnchost;
	
	@Value("${config.cnc.server.port}")
	private String cncport;
	
	@Value("${config.twampsender.dir}")
	private String twampSenderDir;
	
	@Value("${config.firecracker.username}")
	private String vmUsername;
	
	@Value("${config.firecracker.password}")
	private String vmPassword;
	
	@Value("${config.firecracker.hostip}")
	private String vmHostip;
	
	@Value("${server.port}")
	private String port;
	
	public int QualityMeasureRegister(CurrentStatusDTO currentStatusDTO) {

		CurrentStatus currentStatusData = new CurrentStatus();
		List<ReflectorInfo> reflData = new ArrayList<>();
		
		currentStatusData.setSenderIp(currentStatusDTO.getSenderIp());
		currentStatusData.setSenderPort(currentStatusDTO.getSenderPort());
//		if (currentStatusDTO.getProtocol().equals("ICMP")) {
//			reflData = reflectorInfoRepository.findByReflectorIp(
//					currentStatusDTO.getReflectorIp());
//		} else {
//			reflData = reflectorInfoRepository.findByReflectorIpAndPort(
//					currentStatusDTO.getReflectorIp(), currentStatusDTO.getReflectorPort());
//		}
//		if (reflData.isEmpty()) {
//			throw new net.mmp.center.webapp.exception.NullPointerException("Reflector IP 또는 Port가 올바르지 않습니다.");
//		} else if (reflData.size() == 1) {
//			if (reflData.get(0).getProtocolInfo().getProtocolId() != 3 && !reflData.get(0).getProtocolInfo().getType().equals(currentStatusDTO.getProtocol())) {
//				throw new net.mmp.center.webapp.exception.NullPointerException("Protocol이 올바르지 않습니다.");
//			}
//		}
			reflData = reflectorInfoRepository
														.findByReflectorIpAndPort(
																currentStatusDTO.getReflectorIp(), currentStatusDTO.getReflectorPort());
		if (reflData.isEmpty()) {
			throw new net.mmp.center.webapp.exception.NullPointerException("Reflector IP 또는 Port가 올바르지 않습니다.");
		}
		currentStatusData.setReflectorInfo(reflData.get(0));
		currentStatusData.setRepeatCount(currentStatusDTO.getRepeatCount());
		currentStatusData.setSendCount(currentStatusDTO.getSendCount());
		LocalDateTime currTime = LocalDateTime.now();
		currentStatusData.setStartTime(currTime);
		currentStatusData.setTimeout(currentStatusDTO.getTimeout() * 1000);
		ProtocolInfo ptoTmp1 = protocolInfoRepository.findByType(currentStatusDTO.getProtocol());
		currentStatusData.setProtocolInfo(ptoTmp1);
		if (ptoTmp1.getProtocolId() == 1) {			
			currentStatusData.setMeasureMode(currentStatusDTO.getMeasureMode());
		} else {
			currentStatusData.setMeasureMode("");
		}
		CurrentStatus saveData = currentStatusRepository.save(currentStatusData);
		
		logger.info("품질 측정 Data 등록 완료 === CNC, 품질 이력 등록 시작...");
		
		int result;
		if (saveData != null) {
			
			
			
			/**
			 * 이전에 gRPC 이용하여 Process Run 한 부분입니다.
			 */
//			QualityMeasureProcessRun(saveData);
			
			/**
			 * 모든 상황에서 측정 시작시 즉시 품질 이력에 등록합니다.
			 */
			if (qualityHistoryRegister(saveData.getSessId(), saveData, LocalDateTime.now(), "start") == RESULT_OK) {
				result = RESULT_OK;
			} else {
				result = RESULT_FAIL;
			}
			
			/**
			 * 새로 들어온 CNC API Call 부분
			 */
			QualityMeasureByCNC(saveData, currentStatusDTO.getId(), currentStatusDTO.getPassword());
			
			logger.info("품질 측정 Data 등록 완료 === CNC API Call 종료");
			
			/**
			 * 무한 반복 측정시에만 즉시 품질 이력에 등록합니다.
			 */
//			if (currentStatusDTO.getRepeatCount() == -1) {
//				if (qualityHistoryRegister(saveData.getSessId(), saveData, "start") == RESULT_OK) {
//					result = RESULT_OK;
//				} else {
//					result = RESULT_FAIL;
//				}
//			}


			result = RESULT_OK;
		} else {
			result = RESULT_FAIL;
		}

		return result;
	}

	/**
	 * 측정 시작 - 프로세스 실행 부분 (Process 실행, gRPC)
	 */
//	public int QualityMeasureProcessRun(CurrentStatus saveData) {
//
////		List<String> command = Arrays.asList("./twampSender", "-H", saveData.getReflectorInfo().getReflectorIp(), "-s",
////				Integer.toString(saveData.getSessId()), "-m", Integer.toString(saveData.getRepeatCount()), "-c",
////				Integer.toString(saveData.getSendCount()), "-p",
////				Integer.toString(saveData.getReflectorInfo().getPort()), "-f", "sender.json");
////qualityHistoryRegister
////		Thread t = new Thread(new ProcessRun(command, directory));
////		t.start();
//
//		ManagedChannel channel = null;
//		try {
//			channel = ManagedChannelBuilder.forAddress(saveData.getSenderIp(), saveData.getSenderPort()).usePlaintext().build(); // twampd
//																												// 주소
//			MeasurementGrpc.MeasurementBlockingStub blockingStub = MeasurementGrpc.newBlockingStub(channel);
//			
//			MeasurementRequest.Builder requestBuilder = MeasurementRequest.newBuilder();
//			requestBuilder.setHost(saveData.getReflectorInfo().getReflectorIp());
//			requestBuilder.setSessionId(Integer.toString(saveData.getSessId()));
//			requestBuilder.setPort(saveData.getReflectorInfo().getPort());
//			requestBuilder.setMeasurement(saveData.getRepeatCount());
//			requestBuilder.setCount(saveData.getSendCount());
//			requestBuilder.setTest(saveData.getProtocolInfo().getProtocolId());// Protocol Mode
//			requestBuilder.setTimeout(saveData.getTimeout());
//			MeasurementRequest request = requestBuilder.build();
//			
//			MeasurementResponse response = null;
//			
//			try {
//				logger.info("품질측정 ... Process 시작");
//				response = blockingStub.doMeasure(request);
//			} catch (StatusRuntimeException e) {
//				if (currentStatusRepository.findOne(saveData.getSessId()) != null) {
//					currentStatusRepository.delete(saveData.getSessId());
//				}
//				throw new net.mmp.center.webapp.exception.StatusRuntimeException(
//						"Status Runtime Exception is = " + e.getMessage());
//			}
//			
//			if (Util.checkNullStr(response.getResult())) {
//				logger.info("pid: " + response.getPid() + ", result: " + response.getResult() + "\n");
//			} else {
//				logger.info("pid: " + response.getPid() + ", result: null\n");
//			}
//			if (response.getResult().indexOf("NOK") != -1) {
//				
//				currentStatusRepository.delete(saveData.getSessId());
//				throw new net.mmp.center.webapp.exception.NullPointerException("Protocol Error : Not Support");
//			}
//			
////			saveData.setMprotocol(protocolInfoRepository.findOne(response.getTest()).getType());
//			currentStatusRepository.save(saveData);
//		} finally {
//			if (channel != null) {
//				try {
//					channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
//				} catch (InterruptedException e) {
//					throw new net.mmp.center.webapp.exception.InterruptedException("InterruptedException = " + e);
//				}
//			}
//		}
//		return RESULT_OK;
//	}
	
	/**
	 * CNC 호출
	 * @param saveData
	 * @return
	 */
	public int QualityMeasureByCNC(CurrentStatus saveData, String id, String password) {
		List<TargetServerInfo> result = new ArrayList<TargetServerInfo>();
		
		String url = "http://" + cnchost + ":" + cncport + "/execute";
//		String url = "http://localhost:8091/execute";
		
		List<ReflectorInfo>reflData = reflectorInfoRepository.findByReflectorIp(saveData.getSenderIp());
		
		StringBuffer srcIp = new StringBuffer();
		
		/**
		 * 현재 Sender가 TEST용 VM or TEST용 소형장치 인지 검사하는 항목이다.
		 * 검사 기준은 Reflector 관리에서 주소를 test로 저장한 Reflector 인지 검사한다.
		 */
		if (reflData.get(0).getAddress().equals("test")) {			
			srcIp.append(vmHostip);
			srcIp.append(saveData.getSenderIp().split("\\.")[2]);
			srcIp.append(".");
			srcIp.append(saveData.getSenderIp().split("\\.")[3]);
		} else {
			srcIp.append(saveData.getSenderIp());
		}
		
		TargetServerInfo ts = new TargetServerInfo();
		StringBuffer command = new StringBuffer();

		if (saveData.getProtocolInfo().getProtocolId() == 1) {// Full TWAMP 일때
			
			command.append(twampSenderDir);
			
			if (saveData.getMeasureMode().equals("authorized") || saveData.getMeasureMode().equals("encryption")) {
				command.append("/run-client-auth.sh");
			} else {
				command.append("/run-client-noauth.sh");
			}
			
			command.append(" -S " + saveData.getSenderIp());
			command.append(" -H " + saveData.getReflectorInfo().getReflectorIp());
			command.append(" -c " + saveData.getSendCount());
			command.append(" -m " + saveData.getRepeatCount());
			command.append(" -s " + saveData.getSessId());
			command.append(" -e " + eshost + ":" + port);
			command.append(" -E " + "http://" + eshost + ":" + esport + "/twamp/measurement");
			
			/**
			 * 인증모드 or 암호화 모드 일 경우
			 */
			if (saveData.getMeasureMode().equals("authorized")) {
				command.append(" -M 2");
				command.append(" -U " + id);
				command.append(" -P " + password);
			} else if (saveData.getMeasureMode().equals("encryption")) {
				command.append(" -M 4");
				command.append(" -U " + id);
				command.append(" -P " + password);
			} else {// 무인증 모드 일 경우
				command.append(" -M 1");
			}
			
			
		} else if (saveData.getProtocolInfo().getProtocolId() == 2) { // Light TWAMP 일때
			command.append(twampSenderDir);
			command.append("/run-sender-manual.sh");
			command.append(" -H " + eshost);
			command.append(" -E " + esport);
			command.append(" -e " + port);
			command.append(" -S " + saveData.getSenderIp());
			command.append(" -R " + saveData.getReflectorInfo().getReflectorIp());
//			command.append(" -p " + saveData.getReflectorInfo().getPort());
			command.append(" -c " + saveData.getSendCount());
			command.append(" -m " + saveData.getRepeatCount());
			command.append(" -s " + saveData.getSessId());
			command.append(" -o " + saveData.getTimeout());
		}
		command.append(" &");

		
		ts.setCommand(command.toString());
		ts.setTargetIP(srcIp.toString());
		ts.setTargetPort(22);
		ts.setTimeout(5000);
		ts.setUserName(vmUsername);
		ts.setPassword(vmPassword);
		
		result.add(ts);
		
		Util.RequestAPIPOST(Util.convertJSONString(result), url);
		
		return 0;
	}
	

//	private int setMeasureProtocolType(String protocolType) {
//		int result = 0;
//		if (protocolType.equals("TWAMP")) {
//			result = 1;
//		} else if (protocolType.equals("ICMP")) {
//			result = 2;
//		} else if (protocolType.equals("TWAMP & ICMP")) {
//			result = 3;
//		}
//		return result;
//	}

	public PageImpl<CurrentStatusDTO> currentStatusList(Pageable pageable) {

		List<CurrentStatusDTO> resultData = new ArrayList<>();
		PageRequest pageRequest = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
		Page<CurrentStatus> list = currentStatusRepository.findAll(pageRequest);
		if (list.getContent().isEmpty()) {
			throw new NotFoundException("Not found pageable By current Status = " + pageRequest);
		}

		resultData = list.getContent().stream().map(data -> modelMapper.map(data, CurrentStatusDTO.class))
				.collect(Collectors.toList());

		for (int a = 0; a < list.getContent().size(); a++) {
			resultData.get(a).setProtocol(list.getContent().get(a).getProtocolInfo().getType());
		}
		
		PageImpl<CurrentStatusDTO> resultConvert = new PageImpl<>(resultData, pageRequest, list.getTotalElements());
		
		logger.info("Total Elements = " + list.getTotalElements());
		
		logger.info("품질 측정 조회 완료");
		return resultConvert;
	}

//	public int QualityMeasureStop(int sessId) {
//		/**
//		 * TODO ..... 측정 중지 -> 중지 완료 후 완료 시간 저장
//		 */
//		CurrentStatus findData = currentStatusRepository.findOne(sessId);
//		if (findData == null) {
//			throw new net.mmp.center.webapp.exception.NullPointerException("Null Pointer Exception = 측정 중지 부분");
//		}
//		if (findData.getPid() == 0) {
//			currentStatusRepository.delete(sessId);
//			throw new net.mmp.center.webapp.exception.NullPointerException("중지하려는 Process ID가 0 입니다.");
//		}
////		List<String> command = Arrays.asList("kill", "-9", Integer.toString(findData.getPid()));
////
////		Thread t = new Thread(new ProcessRun(command, directory));
////		t.start();
//
//		ManagedChannel channel = null;
//		StopResponse response = null;
//		try {
//			channel = ManagedChannelBuilder.forAddress(findData.getSenderIp(), findData.getSenderPort()).usePlaintext().build();
//			MeasurementGrpc.MeasurementBlockingStub blockingStub = MeasurementGrpc.newBlockingStub(channel);
//	
//			StopRequest.Builder requestBuilder = StopRequest.newBuilder();
//			requestBuilder.setPid(findData.getPid());
//			requestBuilder.setSessionId(Integer.toString(findData.getSessId()));
//			StopRequest request = requestBuilder.build();
//			
//			try {
//				logger.info("품질측정 ... Process 중지");
//				response = blockingStub.doStop(request);
//			} catch (StatusRuntimeException e) {
//				throw new net.mmp.center.webapp.exception.StatusRuntimeException(
//						"Status Runtime Exception is = " + e.getMessage());
//			}
//			
//			logger.info("Do Stop - pid: " + response.getPid());
//		} finally {
//			if(channel != null) {
//				try {
//					channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
//				} catch (InterruptedException e) {
//					throw new net.mmp.center.webapp.exception.InterruptedException("InterruptedException = " + e);
//				}
//			}
//		}
//
//		logger.info("품질 측정 중지 완료");
//		return (qualityHistoryRegister(sessId, findData, LocalDateTime.now(), "stop") == RESULT_OK ? RESULT_OK : RESULT_FAIL);
//	}
	
	/**
	 * 측정 Action 조건에 따라서 품질 이력에 저장한다.(측정 시작, 측정중, 측정완료, 측정 실패)
	 * @param sessId
	 * 								세션 ID
	 * @param qualData
	 * 								품질 이력 Data
	 * @param type
	 * 								측정 시작인지, 중지인지 판단
	 * @return
	 * 							 	OK or FALSE
	 */
	public int qualityHistoryRegister(int sessId, CurrentStatus qualData, LocalDateTime endTime, String type) {
		
		QualityHistoryDTO qHdto = new QualityHistoryDTO();
		qHdto.setSessId(sessId);
		qHdto.setStartTime(qualData.getStartTime());
		qHdto.setCompleteTime(endTime);
		qHdto.setSendCount(qualData.getSendCount());
		qHdto.setRepeatCount(qualData.getRepeatCount());
		Optional<ReflectorInfo> ReflData = reflectorInfoRepository.findById(qualData.getReflectorInfo().getReflectorId());
		if (ReflData == null) {
			throw new NotFoundException("Not Found Reflector Id = " + qualData.getReflectorInfo().getReflectorId());
		}
		// String[] protocols = ReflData.getProtocolInfo().getType().split(" ");
		
		qHdto.setMeasureProtocol(qualData.getProtocolInfo().getType());
		qHdto.setMeasureMode(qualData.getMeasureMode());
		if (type.equals("stop")) {
			qHdto.setMeasureResult("-2/-2");
		} else if (type.equals("start")) {
			qHdto.setMeasureResult("-1/-1");
		} else if (type.equals("complete")) {
			qHdto.setMeasureResult("1/1");
		} else {
			qHdto.setMeasureResult("0/1");
		}
		
		return (qualityHistoryService.qualityHistoryRegister(qHdto) == RESULT_OK ? RESULT_OK : RESULT_FAIL);
	}
	
	
	/**
	 * 현재 측정중인 품질측정에 대하여 ES Query - Count 검사
	 * 추후에 작업 할 예정입니다.
	 * @param currData
	 */
//	private void judgeUnderMeasurement(CurrentStatus currData) {
//		
//		String url = "http://" + eshost + ":" + esport + "/execute";
//		
//		JudgeUnderMeasureThreadPool threadPool = new JudgeUnderMeasureThreadPool(1);
//		JudgeUnderMeasureThread thread = null;
//		thread = new JudgeUnderMeasureThread(url, reflData, dstIdx, eshost, esport, username, password, twampSenderDir, repeatCount, template);
//		threadPool.execute(thread);
//
//		while (true) {
//			if (threadPool.isThreadFinish()) {
////				int totalCount = threadPool.getTotalExecuteCount();
//				logger.info("FullMesh Finish!!!");
//				if (threadPool != null) {
//					threadPool.shutdown();
//				}
//				break;
//			}
//			try
//			{
//				Thread.sleep(5000);
//			}
//			catch (Exception e)
//			{
//				e.printStackTrace();
//			}
//		}
//		
//	}
	
	
	/**
	 * 측정 종료
	 */
	public int measurementComplete(CurrentStatusResultDTO currentStatusResultDTO) {
		logger.info("--- 품질 측정 완료 처리 시작	---");
		logger.info("--- SID : " + currentStatusResultDTO.getSessid() + "				---");
		logger.info("--- startTime : " + currentStatusResultDTO.getStartTime() + "	---");
		logger.info("--- endTime : " + currentStatusResultDTO.getEndTime() + "		---");
		logger.info("--- result : " + currentStatusResultDTO.getResult() + "			---");
		Optional<CurrentStatus> data = currentStatusRepository.findById(currentStatusResultDTO.getSessid());
		
		if (data == null) {
			throw new NotFoundException("Not found sessId Id = " + currentStatusResultDTO.getSessid());
		}
		
		long startTime = Long.parseLong(currentStatusResultDTO.getStartTime(), 10);
		long endTime = Long.parseLong(currentStatusResultDTO.getEndTime(), 10);
		LocalDateTime parseStartTime = 
												LocalDateTime.ofInstant(
														Instant.ofEpochSecond(startTime), TimeZone.getDefault().toZoneId()
												);
		LocalDateTime parseEndTime = 
												LocalDateTime.ofInstant(
														Instant.ofEpochSecond(endTime), TimeZone.getDefault().toZoneId()
												);
		logger.info("--- parse StartTime : " + parseStartTime.toString() + "	---");
		logger.info("--- parse EndTime : " + parseEndTime.toString() + "		---");
		data.get().setStartTime(parseStartTime);
		int result = 0;
		
		if (currentStatusResultDTO.getResult() == 0) {
			logger.info("품질 측정 결과 = 성공");
			result = qualityHistoryRegister(currentStatusResultDTO.getSessid(), data.get(), parseEndTime, "complete");
		} else {
			logger.info("품질 측정 결과 = 실패");
			result = qualityHistoryRegister(currentStatusResultDTO.getSessid(), data.get(), parseEndTime, "fail");
		}
		
		template.convertAndSend("/dashboard/measureEnd", true);

		logger.info("--- 품질 측정 완료 처리 종료	---");
		return result;
	}
}
