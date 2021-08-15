package v4;

import com.study.context.AnnotationApplicationContext;
import org.junit.Test;

public class ApplicationContextTest {

    @Test
    public void annotationApplicationContextTest() throws Exception {
        final AnnotationApplicationContext aac = new AnnotationApplicationContext("v4");
        final User user = (User) aac.getBean("user");
        System.out.println(user.getName() + " " + user.getAge());

        aac.close();
    }
}
