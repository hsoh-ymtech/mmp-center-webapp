package net.mmp.center.webapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan
@SpringBootApplication
public class CenterWebappApplication {

	public static void main(String[] args) {
		SpringApplication.run(CenterWebappApplication.class, args);
	}

}
