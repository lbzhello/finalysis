package xyz.liujin.finalysis.spider.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.text.CharSequenceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import xyz.liujin.finalysis.spider.dto.KLineDto;
import xyz.liujin.finalysis.spider.entity.KLine;
import xyz.liujin.finalysis.spider.repository.KLineRepository;

@Service
@Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED, timeout = 3*60, rollbackFor = Exception.class)
public class KLineService {
    @Autowired
    private KLineRepository kLineRepository;

    public void add(KLineDto kLineDto) {
        KLine kLine = BeanUtil.toBean(kLineDto, KLine.class);
        kLineRepository.save(kLine);
    }

    public KLineDto getOne(Integer id) {
        return BeanUtil.toBean(kLineRepository.getOne(id), KLineDto.class);
    }
}
