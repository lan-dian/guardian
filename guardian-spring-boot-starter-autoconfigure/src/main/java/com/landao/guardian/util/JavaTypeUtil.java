package com.landao.guardian.util;



import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

public abstract class JavaTypeUtil {

    /**
     * 获取类的泛型类型
     * @param obj 类
     * @param index 第几个泛型的类型
     */
    public static Class<?> getGeneraType(Object obj,int index){
        ParameterizedType parameterizedType =
                (ParameterizedType) obj.getClass().getGenericSuperclass();
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        String typeName = actualTypeArguments[index].getTypeName();
        try {
            return Class.forName(typeName);
        } catch (ClassNotFoundException e) {
            //不可能发生
            e.printStackTrace();
        }
        return null;
    }

    public static Class<?> getFirstGeneraType(Object obj){
        return getGeneraType(obj,0);
    }

    public static Class<?> getSecondGeneraType(Object obj){
        return getGeneraType(obj,1);
    }

    public static boolean isInteger(Class<?> clazz){
        return Objects.equals(clazz,Integer.class) || Objects.equals(clazz,int.class);
    }

    public static boolean isString(Class<?> clazz){
        return Objects.equals(clazz,String.class);
    }

    public static boolean isLong(Class<?> clazz){
        return Objects.equals(clazz,Long.class) || Objects.equals(clazz,long.class);
    }

}
