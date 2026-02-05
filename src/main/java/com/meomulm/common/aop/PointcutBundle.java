package com.meomulm.common.aop;

import org.aspectj.lang.annotation.Pointcut;


public class PointcutBundle {

    @Pointcut("execution(* com.meomulm..*Controller*.*(..))")
    public void controllerPointCut(){
    }


    @Pointcut("execution(* com.meomulm..*ServiceImpl*.*(..))")
    public void serviceImplPointCut(){

    }

}
