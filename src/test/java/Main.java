

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.yilin.plugin.spring.support.SpringUtils;

import redis.clients.jedis.JedisCluster;

/**
 * 
 */
public class Main {
	public static void main(String[] args) throws Exception {
		@SuppressWarnings("resource")
		ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
		JedisCluster jedisCluster = (JedisCluster) SpringUtils.getBean("jedisCluster");
		System.out.println(jedisCluster);
		System.out.println(jedisCluster.getClusterNodes());
	}

}
