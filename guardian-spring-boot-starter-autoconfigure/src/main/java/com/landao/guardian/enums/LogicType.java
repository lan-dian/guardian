package com.landao.guardian.enums;

public enum LogicType {

    /**
     * 满足其中一个
     */
    Or,
    /**
     * 全部满足
     */
    And,
    /**
     * 不是能是前面的任意一个
     */
    Not;
}
