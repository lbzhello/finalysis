package xyz.liujin.finalysis.common.constant;

import java.util.Arrays;

/**
 * 交易板块常亮
 */
public enum BoardEnum {
    SH_A(1, "沪A", "60"),
    SZ_A(2, "深A", "00"),
    CYB(3, "创业板", "300"),
    KCB(4, "科创板", "688"),
    SH_B(5, "沪B", "900"),
    SZ_B(6, "深B", "200"),
    UNKNOWN(0, "未知", ""),
    ;
    // 所属交易板块；如创业板
    private int board;
    // 板块名称
    private String boardName;
    // 股票代码前缀
    private String pre;

    BoardEnum(int board, String boardName, String pre) {
        this.board = board;
        this.boardName = boardName;
        this.pre = pre;
    }

    /**
     * 根据股票代码，判断所属交易板块
     * @return
     */
    public static final int getBoardByCode(String stockCode) {
        return Arrays.stream(values())
                .filter(board -> stockCode.startsWith(board.pre))
                .findFirst()
                .map(board -> board.board)
                .orElse(UNKNOWN.board);
    }
}
