# Guardian

> an easy but power security framework

# 特色

## 简约而不简单

> 配置简约

1. 导入starter

2. 配置必要的token密钥

3. 继承功能丰富的tokenService

   其他统统不需要配置！！！

> 配置灵活

​	支持在tokenService**代码级别**满足你对认证的一切幻想

## 自定义tokenBean

> 什么是tokenBean

​	我们会在token中保存用户的一些信息，其中必要的是用户的id，此外我们还可以保存比如用户姓名，用户头像等经常需要获取的信息。基于面向对象的思想，我们习惯把这些信息封装为一个对象。

> 特色功能

​	Guardian利用泛型反射技术来实现tokenBean和token的ORM映射。而且改良支持使用线程ThreadLocal来完成tokenBean和userId的随时获取，并且提供精准的泛型支持

> 注意

​	tokenBean不建议存放影响系统功能的字段，比如用户的角色列表等

## 天然支持多用户鉴权

​	打破市面上多用户鉴权配置负责，鉴权恐难的框架。Guardian独辟蹊径，使用了完全不同的架构思路，继承TokenSevice，让我来编写通用的业务代码，让用户来实现自己的特殊要求，无论多少种用户，只需要无脑继承就可！

# 基本功能

## 灵活配置

- [] 策略模式,自定义tokenBean转换规则
- [] 钩子函数AOP切入，为所以可能的地方提供AOP
- [] 日志功能,完善的鉴权日志流程

## tokenBean ORM

> 主键类型

​	常用的Integer,Long,String统统支持。一个@UserId系统就会帮你自动识别而判断

> 其他类型

- [ ] 枚举类型
- [ ] 日期类型(支持java8新api
- [ ] 集合类型
- [ ] 嵌套类型

> 灵活ORM

- [ ] 用注解避开反序列化
- [ ] 用transient关键字避开反序列化

## 解决跨域

​	默认自动配置，解决cors问题，另外提供yaml自定义

## 登陆验证

​	@RequiredLogin既可以标注在类上，又可以标注在方法上。依据就近原则，方法标注优先。

- 单种用户
   - 需要登陆
      - @RequireLogin
   - 不需要登陆
      - 不标注
- 多种用户(A,B,C....Z)
   - 需要登陆
      - 全部需要登陆
         - @RequireLogin
      - 只容许A,B使用
         - @RequireLogin(onlyFor={"A","B"})
      - 不容许B,C使用
         - @RequireLogin(forbidden={"B","C"})
   - 不需要登陆
      - 不标注