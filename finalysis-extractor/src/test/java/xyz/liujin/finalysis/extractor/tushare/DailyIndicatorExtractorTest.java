package xyz.liujin.finalysis.extractor.tushare;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import xyz.liujin.finalysis.base.util.DebugUtils;

import java.time.LocalDate;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DailyIndicatorExtractor.class)
public class DailyIndicatorExtractorTest {
    @Autowired
    private DailyIndicatorExtractor dailyIndicatorExtractor;

    @Test
    public void extractDailyIndicator() {
        dailyIndicatorExtractor.extractDailyIndicator(LocalDate.of(2021, 4, 20),
                LocalDate.of(2021, 4, 30),
                List.of("000001"))
                .subscribe(it -> {
                    System.out.println(it);
                }, e -> System.out.println(e));
        DebugUtils.waitMillis(2000);
    }
}
