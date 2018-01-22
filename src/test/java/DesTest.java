import java.net.URLDecoder;

import com.yilin.netty.commons.utils.DesUtils;

public class DesTest {

	public static void main(String[] args) throws Exception {
//		System.out.println(DesUtils.encrypt("private static byte[] decrypt(byte[] data, byte[] key) throws Exception"));
//		System.out.println(DesUtils.encrypt("{\"version\":\"2.0.0\",\"userId\":\"1\",\"tokenId\":\"110\",\"softType\":\"ios_qhh_v2.0\",\"funCode\":\"100015\"}"));
		System.out.println(DesUtils.encrypt("{\"funCode\":\"110001\",\"pmType\":\"1\",\"softType\":\"android_weixiaojie_v2.0\",\"version\":\"2.0.0\"}"));
//		System.out.println(DesUtils.decrypt("vRNFWnipAlYXXmOWqfdJya8lPH/X3/+EBXIjZ+/zGStqlFYOOt7gZCWuiR7jl5z495cy+pYOnq/pk1ZWXAs+y/dmNWCROekUNX3Dvv3hoaU="));
//		System.out.println(DesUtils.decrypt(URLDecoder.decode("vRNFWnipAlYXXmOWqfdJya8lPH/X3/+EBXIjZ+/zGStqlFYOOt7gZCWuiR7jl5z495cy+pYOnq/pk1ZWXAs+y/dmNWCROekUNX3Dvv3hoaU=","UTF-8")));
//		System.out.println(DesUtils.decrypt(URLDecoder.decode("IvZM9W6McxU9VrOD5vocpf5lZD6vBmz+pcQNrn4dQ0SRvTpNwsMGHDdfZpU6wPcOp49t/tMLkyAzeCxOCrSWzkKA+RScUCRbg/8eujE7pv2kWs7mhoRLAQ==","UTF-8")));
//		System.out.println(DesUtils.decrypt(URLDecoder.decode("I/FIWvwbtvlRX5n9a955o01EVwAsBfXXmMeBDMwwJKoFw9hbOM4nATKKFRNf+GWH6yGb5f4w1Dn1gx4d1AsM+La4c2FOeGONXQ4eOEt2TdO+MfPqCAhyNg==","UTF-8")));
	}

}
