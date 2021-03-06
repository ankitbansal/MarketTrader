package com.sa.mt.options.repository;

import com.sa.mt.options.domain.DownloadStatus;
import com.sa.mt.options.downloader.DownloadType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.sa.mt.options.downloader.DownloadType.INSTRUMENT;
import static com.sa.mt.options.downloader.DownloadType.STOCK;
import static com.sa.mt.utils.DateUtils.getDate;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/test/resources/testContext.xml"})
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class})
public class DownloadStatusRepositoryTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private DownloadStatusRepository downloadStatusRepository;

    @Before
    public void setUp() {
        mongoTemplate.dropCollection(DownloadStatus.class);
    }

    @Test
    public void shouldRetrieveDownloadStatusForDownloadType() {
        DownloadStatus downloadStatusForInstrument = downloadStatus(INSTRUMENT, getDate("10-May-2011"));
        DownloadStatus otherDownloadStatus = downloadStatus(STOCK, getDate("15-Apr-2010"));
        List<DownloadStatus> downloadStatuses = Arrays.asList(downloadStatusForInstrument,
                otherDownloadStatus);

        downloadStatusRepository.save(downloadStatuses);
        DownloadStatus downloadStatus = downloadStatusRepository.find(INSTRUMENT);
        assertEquals(downloadStatusForInstrument, downloadStatus);
        assertFalse(otherDownloadStatus.equals(downloadStatus));
    }

    @Test
    public void shouldUpdateDownloadStatusDate() {
        DownloadStatus downloadStatus = downloadStatus(INSTRUMENT, getDate("10-May-2011"));
        downloadStatusRepository.save(Arrays.asList(downloadStatus));
        DownloadStatus newDownloadStatus = new DownloadStatus(INSTRUMENT, getDate("12-May-2011"));
        downloadStatusRepository.updateDownloadedDate(newDownloadStatus);

        assertEquals(newDownloadStatus, downloadStatusRepository.find(INSTRUMENT));
    }

    private DownloadStatus downloadStatus(DownloadType instrument, Date lastDownloadedDate) {
        return new DownloadStatus(instrument, lastDownloadedDate);
    }
}
