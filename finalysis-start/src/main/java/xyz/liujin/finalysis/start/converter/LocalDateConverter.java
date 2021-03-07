package xyz.liujin.finalysis.start.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import xyz.liujin.finalysis.base.util.DateUtils;

import java.time.LocalDate;
import java.util.Optional;

/**
 * yyyy-MM-dd -> LocalDate
 */
@Component
public class LocalDateConverter implements Converter<String, LocalDate> {
    @Override
    public LocalDate convert(String source) {
        return Optional.ofNullable(source).map(DateUtils::parseDate).orElse(null);
    }
}
