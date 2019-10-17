package net.mmp.center.webapp.service.impl;

import net.mmp.center.webapp.domain.BootstrapInfoLog;
import net.mmp.center.webapp.repository.BootstrapInfoLogRepository;
import net.mmp.center.webapp.service.BootstrapInfoLogService;
import org.elasticsearch.bootstrap.BootstrapInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(BootstrapInfoLogServiceImpl.BEAN_QUALIFIER)
public class BootstrapInfoLogServiceImpl implements BootstrapInfoLogService {
    public static final String BEAN_QUALIFIER = "net.mmp.center.webapp.service.impl.BootstrapInfoLogServiceImpl";

    @Autowired
    private BootstrapInfoLogRepository respository;

    public void save(BootstrapInfoLog data) {
        respository.save(data);
    }
}
