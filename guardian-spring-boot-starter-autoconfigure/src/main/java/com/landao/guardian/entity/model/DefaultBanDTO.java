package com.landao.guardian.entity.model;

import com.landao.guardian.core.interfaces.BanDTO;

public class DefaultBanDTO implements BanDTO {

    private Boolean ban;

    private String throwMsg;

    @Override
    public boolean isBan() {
        return false;
    }

    @Override
    public String getThrowMsg() {
        return null;
    }

}
