package com.landao.guardian.core.handler;

import com.landao.guardian.annotations.system.Handler;
import com.landao.guardian.core.GuardianContext;
import com.landao.guardian.core.interfaces.Ban;
import com.landao.guardian.exception.author.UnAuthorizationException;

@Handler
public class BanHandler {


    /**
     * 最简单的handler
     */
    public void checkBan(){
        Ban ban = GuardianContext.getTokenService().checkBan();
        if(ban.info()!=null){
            throw new UnAuthorizationException((String) ban.info());
        }
    }


}
