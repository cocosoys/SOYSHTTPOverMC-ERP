package com.github.cocosoys.mc.soyshttpovermcerp.entity.vo;

import lombok.Data;

/**
 * 权限组成员行：UUID + 玩家名。
 */
@Data
public class SoysGroupMemberVo {
    /** 玩家 UUID。 */
    private String uuid;
    /** 玩家名（可能为 null）。 */
    private String player;

    /** 静态工厂。 */
    public static SoysGroupMemberVo of(String uuid, String player) {
        SoysGroupMemberVo vo = new SoysGroupMemberVo();
        vo.setUuid(uuid);
        vo.setPlayer(player);
        return vo;
    }
}
