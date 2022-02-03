package com.landao.guardian.core;

import com.landao.guardian.annotations.system.Handler;
import com.landao.guardian.core.context.CurrentSubject;
import com.landao.guardian.core.interfaces.BanDTO;
import com.landao.guardian.exception.author.UnAuthorizationException;

@Handler
public class BanHandler {


    /**
     * 最简单的handler
     */
    public void checkBan(){
        BanDTO banDTO = CurrentSubject.getTokenService().checkBan();
        if(banDTO.isBan()){
            throw new UnAuthorizationException(banDTO.getThrowMsg());
        }
    }



}
