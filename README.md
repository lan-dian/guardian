# Guardian

> an easy but power security framework

# 使用

> 导包

```java
<groupId>com.landao</groupId>
<artifactId>guardian-spring-boot-starter</artifactId>
<version>1.0-SNAPSHOT</version>
```

​	目前没有上传到中央仓库，因为几乎每天更新好几个功能，可以git下来后自己安装，push上来的肯定都是可用的版本

> yaml

```yaml
guardian:
  token:
    private-key: 'toekn密钥'
```

> 认证服务

```java

/**
 * UserTokenDTO 可以自定义在token中保存的信息
 */
@GuardianService
public class UserAuthorService extends TokenService<UserTokenDTO,Integer> {

    /**
     * 可以注入其他组件，增强功能
     */
    @Resource
    StudentService studentService;

    @Resource
    StudentRoleService studentRoleService;

    @Override
    public Set<String> getRoles() {
        return studentRoleService.getStringRole(getUserId());
    }

    public String getName(){
        return getTokenBean().getName();
    }

    public Integer getBuildId(){
        Student student = studentService.lambdaQuery().eq(Student::getStudentId, getUserId()).one();
        return student.getBuildId();
    }

}
```

这个时候就可以使用了！！！

> 简单使用

```java
    @RequiredRole(roles = {RoleConst.student,RoleConst.teacher})
    @ApiOperation("查看我的预约")
    @GetMapping("/history")
    public CommonResult<PageDTO<HistoryAppointment>> getHistory(@RequestParam(defaultValue = "1") Integer page,
                                                                      @RequestParam(defaultValue = "7") Integer limit) {
        CommonResult<PageDTO<HistoryAppointment>> result = new CommonResult<>();

        if (page <= 0 || limit <= 0) {
            return result.err("分页参数异常");
        }

        Integer studentId = userAuthorService.getUserId();

        PageDTO<HistoryAppointment> pageDTO = seatService.pageHistoryAppointments(page, limit, studentId);

        return result.body(pageDTO);
    }
```

> 支持线程上下文

​	如果你不喜欢这种注入service的方式，你可以使用`GuardianContext.getUserId()`来直接获取所有的上下文信心，我保证所有操作不会超过一行代码！

​	当然，GuardianService注入模式本质是就是一个自带登陆信息的你的普通userService的增强版。

# 特色

## 简约而不简单

> 配置简约

1. 导入starter

2. 配置必要的token密钥

3. 继承功能丰富的tokenService

   其他统统不需要配置！！！

> 配置灵活

​	支持在tokenService**代码级别**满足你对认证的一切幻想

> 自定义切面顺序

```java
@Override
public void addInterceptors(InterceptorRegistry registry) {
    GuardianProperties.Interceptor interceptor = guardianProperties.getInterceptor();
    registry.addInterceptor(guardianInterceptor).addPathPatterns("/**").order(interceptor.getOrder());
}
```

​	**注意**:直接在yaml配置就可以，这里是想告诉你怎么使用这个order(每个配置项我都生成了自动提示的功能)

## 自定义tokenBean

> 什么是tokenBean

​	我们会在token中保存用户的一些信息，其中必要的是用户的id，此外我们还可以保存比如用户姓名，用户头像等经常需要获取的信息。基于面向对象的思想，我们习惯把这些信息封装为一个对象。

> 特色功能

​	Guardian利用泛型反射技术来实现tokenBean和token的ORM映射。而且改良支持使用线程ThreadLocal来完成tokenBean和userId的随时获取，并且提供精准的泛型支持

> 注意

​	tokenBean不建议存放影响系统功能的字段，比如用户的收货地址，假如用户登陆后很多业务都会根据这个字段来判断，但是你不能把它存到token里面，因为一旦收货地址变动，用户就可以利用这个破坏我们的业务逻辑。

> 自定义额外字段

​	但是，我们的权限框架给出了一种新的解法，我们可以自己额外封装一个对象，重写setExtra方法，利用GuardianContext上下文设置额外字段，并且这个操作会在鉴权结束后自动执行，所以我们可以在其他地方获取这些额外信息。而且具有缓存的功能，效率非常高。

​	请结合业务逻辑谨慎考虑使用此功能，因为有的时候，你的这些额外字段，可能一次都不会被获取，白白浪费的线程资源。当然，你也可以利用GuardianContext上下文只有操控这个额外字段，它没有登陆检查限制，相当于一个会伴随你整个请求线程的线程专属变量，如果你愿意，这也是一个不错的工具类。

## 天然支持多用户鉴权

​	打破市面上多用户鉴权配置负责，鉴权恐难的框架。Guardian独辟蹊径，使用了完全不同的架构思路，继承TokenSevice，让我来编写通用的业务代码，让用户来实现自己的特殊要求，无论多少种用户，只需要无脑继承就可！

## 智能提示

> yaml

​	所以的yaml配置，我们都会进行预先检测，常见的比如空字符串的检测，而且还会自动帮你削去字符串前后的空格，如果你配置错误，会收到非常友好的中文提示。

> 运行时检测

​	如果我们发现你在使用注解或其他功能的时候，会出现"程序员炒饭"现象，未能正确使用，我们也会通过简单的判断来查询出来，并告诉你正确的使用方法。

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

## 角色验证

- 已登陆,系统可以获取userType
  - 标注了任意与角色相关的注解
    - 按照:方法优先于类，复合优先于单注解的顺序，寻找和userType匹配的注解
      - 找到了
        - 依据逻辑判断类型开始匹配
          - or:符合其中一个就可以访问
          - and:全部符合才可以访问
          - not:禁止**只含有**指定角色的访问
          - 接口，自定义实现逻辑
      - 未找到
        - 禁止访问接口
  - 未标注任何与角色相关的注解
    - 可以访问接口
- 未登录,无法无法获取userType
  - 标注任意与角色相关的注解
    - 禁止访问接口
  - 未标注任何于角色相关的注解
    - 可以访问接口