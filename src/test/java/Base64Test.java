import java.io.UnsupportedEncodingException;

import com.mulhyac.netty.commons.utils.Base64;

public class Base64Test {

	public static void main(String[] args) throws UnsupportedEncodingException {
		System.out.println(Base64.encode("测试".getBytes("UTF-8")));
	}

}
