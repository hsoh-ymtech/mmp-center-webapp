package net.mmp.center.webapp.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;

import net.mmp.center.webapp.WebApplication;
import net.mmp.center.webapp.domain.TargetServerInfo;

public class Util {
	
	/**
	 * String Null Check 
	 * @return true : str is not Null
	 */
	public static boolean checkNullStr(String str) {
		
		if(str != null && !"".equals(str) && !"null".equals(str)) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Object Null Check 
	 * @return true : obj is not Null
	 */
	public static boolean checkNullObj(Object obj) {
		
		if(obj != null && !"".equals(obj) && !"null".equals(obj)) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * 운영체제에 따라서 Directory를 변환 한다.(Window, Ubuntu 에서 확인 완료)
	 * @param directory
	 * @return
	 */
	public static String convertDirectory(String directory) {
		String dir = directory;
		String jarLocationUrl;
		try {
			jarLocationUrl = WebApplication.class.getProtectionDomain().getCodeSource().getLocation().getPath().toString();
			int index = jarLocationUrl.indexOf('!');
			if (0 < index) {
				String jarLocationPath = new URL(jarLocationUrl.substring(0, index)).getPath();
				String base = new File(jarLocationPath).getParent();
				dir = base + File.separator + "src" + File.separator + "twamp-visualization";
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return dir;
	}
	
	/**
	 * 현재 OS가 Windows 인지 검사한다.
	 * @return
	 */
	public static boolean isWindows() {
		boolean isWindows = System.getProperty("os.name")
				  .toLowerCase().startsWith("windows");
		if (isWindows) {
			return true;
		} else {
			return false;
		}
	}
	
	public static String convertSHFileNameByOS() {
		String fileName;

		if (!isWindows()) {
			fileName = "twampvisualizationgen.sh";
		} else {
			fileName = "twampvisualizationgen-windows.sh";
		}
		return fileName;
	}
	/**
	 * SH File 실행 Command Convert
	 * @param comm
	 * 								SH 파일 실행시 인자값 List
	 * @param filename
	 * 								SH 파일 이름
	 * @return
	 * 								Convert 결과
	 */
	public static List<String> convertFileNameFromBinByOS(List<String> comm, String filename) {
		List<String> command;
		if (isWindows()) {
			command = new ArrayList<String>(Arrays.asList("sh", filename));
		} else {
			command = new ArrayList<String>(Arrays.asList("./" + filename));
		}
		command.addAll(comm);
		return command;
	}
	
	public static String convertJSONString(List<TargetServerInfo> result) {
		Gson gson = new Gson();
		return gson.toJson(result);
	}
	
	public static StringBuffer RequestAPIPOST(String data, String url) {
		StringBuffer response = new StringBuffer();
		try {
			URL esurl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) esurl.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);
			
			OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
			osw.write(data);
			osw.flush();

			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String inputLine;
			while ((inputLine = br.readLine()) != null) {
				response.append(inputLine);
			}
			
			osw.close();
			br.close();
			conn.disconnect();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}
}
