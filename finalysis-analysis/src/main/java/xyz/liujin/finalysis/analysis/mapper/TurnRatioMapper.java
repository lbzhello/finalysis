package xyz.liujin.finalysis.analysis.mapper;

import xyz.liujin.finalysis.analysis.dto.TurnRatioQo;

import java.util.List;

public interface TurnRatioMapper {
    List<String> findCodes(TurnRatioQo qo);
}
