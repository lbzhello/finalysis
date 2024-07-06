package xyz.liujin.finalysis.analysis.score;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * {@link xyz.liujin.finalysis.analysis.score.annotation.ScoreConfig} 注解带有的信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScoreConfigAnnotationMetaData {
    private String type;
    private int score;
}
