package xyz.liujin.finalysis.extractor.tushare;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import xyz.liujin.finalysis.base.util.DebugUtils;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DailyIndicatorExtractor.class)
public class DailyIndicatorExtractorTest {
    @Autowired
    private DailyIndicatorExtractor dailyIndicatorExtractor;

    @Test
    public void extractDailyIndicator() {
        dailyIndicatorExtractor.extractDailyIndicator()
                .subscribe(it -> {
                    System.out.println(it);
                });
        DebugUtils.waitMillis(2000);
    }
}
