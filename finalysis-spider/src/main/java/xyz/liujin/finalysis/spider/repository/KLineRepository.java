package xyz.liujin.finalysis.spider.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import xyz.liujin.finalysis.spider.entity.KLine;

@Repository
public interface KLineRepository extends JpaRepository<KLine, Integer> {
}
