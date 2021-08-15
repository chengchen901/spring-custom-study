package v3.aop.aspectj;

import com.study.beans.DefaultBeanFactory;
import org.aspectj.weaver.tools.PointcutExpression;
import org.aspectj.weaver.tools.PointcutParser;
import org.aspectj.weaver.tools.ShadowMatch;
import org.aspectj.weaver.tools.TypePatternMatcher;
import v2.di.ChinaUser;

import java.lang.reflect.Method;

public class AspectJTest {

    public static void main(String[] args) throws NoSuchMethodException {
        // 获取切点解析器
        PointcutParser pp = PointcutParser
                .getPointcutParserSupportingAllPrimitivesAndUsingContextClassloaderForResolution();
        // 切点解析器根据规则，解析出一个类型匹配器
        TypePatternMatcher tpm = pp.parseTypePattern("v2.di..*");
        // 根据表达式生成一个切点表达式
        PointcutExpression pe = pp.parsePointcutExpression(
                "execution(* com.study.beans.BeanFactory.get*(..))");

        // 匹配ChinaUser类的getName方法
        Class<?> cl = ChinaUser.class;
        Method aMethod = cl.getMethod("getName", null);
        // 匹配方法执行
        ShadowMatch sm = pe.matchesMethodExecution(aMethod);
        System.out.println("是否匹配到方法执行："+sm.alwaysMatches());		// 是否匹配到

        System.out.println(cl.getName()+"是否匹配表达式："+pe.couldMatchJoinPointsInType(cl));
        System.out.println(DefaultBeanFactory.class.getName()+"是否匹配表达式："+pe.couldMatchJoinPointsInType(DefaultBeanFactory.class));

        System.out.println("\r\n"+cl.getName()+"下的方法有：");
        for (Method m : cl.getMethods()) {
            System.out.println(m.getName());
        }
    }
}
