package xyz.liujin.finalysis.analysis.mapper;

import xyz.liujin.finalysis.analysis.dto.IncreaseRatioQo;

import java.util.List;

public interface IncreaseRatioMapper {
    List<String> findCodes(IncreaseRatioQo qo);
}
