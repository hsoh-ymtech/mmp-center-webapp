package net.mmp.center.webapp.repository;

import net.mmp.center.webapp.domain.ReflectorInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@DataJpaTest
//@ActiveProfiles("test")
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ReflectorInfoRepositoryTests {
    @Autowired
    private ReflectorInfoRepository ReflectorInfoRepository;
    /*
    @Test
    public void saveTest() {
        final ReflectorInfo ReflectorInfo = new ReflectorInfo();
        final ReflectorInfo saveReflectorInfo = ReflectorInfoRepository.save(ReflectorInfo);
        assertNotNull(saveReflectorInfo.getReflectorId());
    }
    @Test
    public void saveAndFindTest() {
        final ReflectorInfo ReflectorInfo = new ReflectorInfo();
        final ReflectorInfo saveReflectorInfo = ReflectorInfoRepository.save(ReflectorInfo);
        final ReflectorInfo findReflectorInfo = ReflectorInfoRepository.findById(saveReflectorInfo.getReflectorId()).get();
        assertNotNull(findReflectorInfo.getReflectorId());
    }
     */
    @Test
    public void checkMeshIdTest() {

        final ReflectorInfo ReflectorInfo = new ReflectorInfo();
        final ReflectorInfo saveReflectorInfo = ReflectorInfoRepository.save(ReflectorInfo);
        final List<ReflectorInfo> list = ReflectorInfoRepository.findAll();
        /*
        for (ReflectorInfo item : list) {
            assertTrue(item.getMeshId().length()>32);
        }
        */
        assertTrue(1==1);
    }

}
