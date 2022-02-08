package com.landao.guardian.core.handler;

import com.landao.guardian.annotations.system.Handler;
import com.landao.guardian.core.GuardianContext;
import com.landao.guardian.core.interfaces.BanDTO;
import com.landao.guardian.exception.author.UnAuthorizationException;

@Handler
public class BanHandler {


    /**
     * 最简单的handler
     */
    public void checkBan(){
        BanDTO banDTO = GuardianContext.getTokenService().checkBan();
        if(banDTO.isBan()){
            throw new UnAuthorizationException(banDTO.getThrowMsg());
        }
    }



}
