package com.landao.guardian.core.interfaces;

@FunctionalInterface
public interface Ban {

    /**
     * 如果没有被ban返回null
     */
    Object info();

}
