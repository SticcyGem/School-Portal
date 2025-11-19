package net.bscs22.schoolportal

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(
    properties = [
        "spring.datasource.url=jdbc:oracle:thin:@127.0.0.1:1521:xe",
        "spring.datasource.username=school",
        "spring.datasource.password=school",
        "spring.datasource.driver-class-name=oracle.jdbc.OracleDriver",
        "spring.session.jdbc.initialize-schema=never"
    ]
)
class SchoolPortalApplicationTests {

    @Test
    fun contextLoads() {
    }

}