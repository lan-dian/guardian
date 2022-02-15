package com.landao.guardian.core;

import com.landao.guardian.core.interfaces.Ban;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public interface AuthorService<T,R> {

    void setExtra();

    <U> U getExtra(Class<U> type);

    T getUser();

    Set<String> getRoles();

    Set<String> getPermissions();

    R getUserId();

    String getUserType();

    Ban checkBan();

    void logout();

    void kickOut(R userId);

    String parseToken(T userBean);

    String parseToken(T userBean, long time, TimeUnit timeUnit);
}
